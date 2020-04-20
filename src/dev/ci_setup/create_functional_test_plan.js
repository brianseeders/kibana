/*
 * Licensed to Elasticsearch B.V. under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch B.V. licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import fs from 'fs';
import path from 'path';

import { getTests } from '@kbn/test';
import { REPO_ROOT } from '@kbn/dev-utils';
import { CI as ALL_OSS } from '../../../test/all_configs';

// TODO
// eslint-disable-next-line @kbn/eslint/no-restricted-paths
import { ALL as ALL_XPACK } from '../../../x-pack/test/all_configs';

const FUNCTIONAL_METRICS_PATH =
  process.env.FUNCTIONAL_METRICS_PATH || 'target/functional_test_suite_metrics.json';

let historical = {};

try {
  const historicalJson = fs.readFileSync(FUNCTIONAL_METRICS_PATH);
  historical = JSON.parse(historicalJson);
} catch (ex) {
  console.log('Error reading file for functional test metrics:');
  console.log(ex);
}

const getTestsTransformed = async (config, overrides = {}) => {
  const suites = await getTests(config, overrides);
  return suites.map(suite => ({
    file: path.relative(REPO_ROOT, suite.file),
    config: path.relative(REPO_ROOT, config),
    duration: suite.file.match('api_integration') ? 30 : 180,
  }));
};

(async () => {
  const configGroups = {
    oss: ALL_OSS,
    xpack: ALL_XPACK,
  };

  // Creating list of all test suites

  const allSuites = {};
  for (const key in configGroups) {
    if (configGroups.hasOwnProperty(key)) {
      allSuites[key] = [];

      for (const file of configGroups[key]) {
        // const suites = await getTestsTransformed(file, { suiteTags: { include: ['testing'] } });
        const suites = await getTestsTransformed(file);
        suites.forEach(suite => allSuites[key].push(suite));
      }
    }
  }

  // const ff = require.resolve('../../../test/functional/config.firefox.js');
  // allSuites.ossFirefox = await getTestsTransformed(ff, {
  //   suiteTags: {
  //     include: ['smoke'],
  //   },
  // });

  // const xpackFF = require.resolve('../../../x-pack/test/functional/config.firefox.js');
  // allSuites.xpackFirefox = await getTestsTransformed(xpackFF, {
  //   suiteTags: {
  //     include: ['smoke'],
  //   },
  // });

  // Add historical data

  const queues = {};

  const historicalByTag = {};

  for (const group of Object.keys(historical)) {
    historicalByTag[group] = {};
    for (const suite of Object.values(historical[group])) {
      historicalByTag[group][suite.config] = historicalByTag[group][suite.config] || {};
      historicalByTag[group][suite.config][suite.file] = suite.duration;
    }
  }

  Object.keys(allSuites).forEach(group => {
    const suites = allSuites[group];
    for (const suite of suites) {
      if (
        historicalByTag[group] &&
        historicalByTag[group][suite.config] &&
        suite.file in historicalByTag[group][suite.config]
      ) {
        suite.duration = historicalByTag[group][suite.config][suite.file];
      }
    }

    // Create plan

    console.log('Creating plan...');

    suites.sort((a, b) => b.duration - a.duration);

    const suitesByConfig = {};
    suites.forEach(suite => {
      suite.config = suite.config.replace(/^x-pack\//, ''); // TODO
      suite.file = suite.file.replace(/^x-pack\//, ''); // TODO

      suitesByConfig[suite.config] = suitesByConfig[suite.config] || [];
      suitesByConfig[suite.config].push(suite);
    });

    const finalQueue = [];

    const TARGET_DURATION_SECONDS = 60 * 9;

    const nextGroup = () => {
      const group = [];

      // const next = suites.shift();
      const next = suites[0];
      const byConfig = suitesByConfig[next.config];
      let totalDuration = 0;
      while (totalDuration < TARGET_DURATION_SECONDS && byConfig.length > 0) {
        const suite =
          totalDuration === 0 || byConfig[0].duration + totalDuration <= TARGET_DURATION_SECONDS
            ? byConfig.shift()
            : byConfig.pop();

        totalDuration += suite.duration;
        const index = suites.indexOf(suite);
        if (index >= 0) {
          suites.splice(index, 1);
        }

        group.push(suite);
      }

      return {
        config: next.config,
        files: group.map(f => ({ file: f.file, duration: f.duration })),
        estimatedDuration: totalDuration,
      };
    };

    while (suites.length) {
      finalQueue.push(nextGroup());
    }

    finalQueue.sort((a, b) => b.estimatedDuration - a.estimatedDuration);

    queues[group] = finalQueue;
  });

  console.log('Checking plan...');

  let total = 0;
  total += 35 * 60; // time til xpack starts
  total += 180; // time for one plugin build step per process
  total += 120; // cleanup time after tests

  const processes = [];
  for (let i = 0; i < 24; i++) {
    processes.push({ name: `process-${i + 1}`, totalDuration: 0, suites: [] });
  }
  let highestDuration = 0;

  const order = [];

  const xpack = [...queues.xpack];
  const iterate = () => {
    const process = processes[0];
    const suite = xpack.shift();
    process.suites.push(suite);
    const estimate = suite.estimatedDuration + 180; // extra time is startup overhead, etc
    process.totalDuration += estimate;
    highestDuration = Math.max(highestDuration, process.totalDuration);

    processes.sort((a, b) => a.totalDuration - b.totalDuration);

    order.push({ name: process.name, suite });
  };

  while (xpack.length) {
    iterate();
  }

  console.log(processes);
  console.log(order);

  console.log('XPack suites', queues.xpack.length);
  for (const process of processes) {
    console.log(process.name, Math.floor(process.totalDuration / 60));
  }
  console.log('Estimated CI time:', Math.floor((highestDuration + total) / 60));

  const outputFile = process.env.TEST_PLAN_FILE || 'target/test-suites-ci-plan.json';
  fs.writeFileSync(outputFile, JSON.stringify(queues, null, 2)); // TODO
})();
