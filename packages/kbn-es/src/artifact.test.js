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

import { ToolingLog } from '@kbn/dev-utils';
jest.mock('node-fetch');
import fetch from 'node-fetch';
const { Response } = jest.requireActual('node-fetch');

import { Artifact } from './artifact';

const log = new ToolingLog();
let MOCKS;

const PLATFORM = process.platform === 'win32' ? 'windows' : process.platform;
const MOCK_VERSION = 'test-version';
const MOCK_URL = 'http://127.0.0.1:12345';
const MOCK_FILENAME = 'test-filename';

const createArchive = (params = {}) => {
  const license = params.license || 'default';

  return {
    license: 'default',
    version: MOCK_VERSION,
    url: MOCK_URL + `/${license}`,
    platform: PLATFORM,
    filename: MOCK_FILENAME + `.${license}`,
    ...params,
  };
};

const mockFetch = mock =>
  fetch.mockReturnValue(Promise.resolve(new Response(JSON.stringify(mock))));

beforeEach(() => {
  jest.resetAllMocks();

  MOCKS = {
    valid: {
      archives: [createArchive({ license: 'oss' }), createArchive({ license: 'default' })],
    },
  };
});

const artifactTest = (requestedLicense, expectedLicense) => {
  return async () => {
    const artifact = await Artifact.getSnapshot(requestedLicense, MOCK_VERSION, log);
    expect(fetch).toHaveBeenCalledTimes(1);
    expect(artifact.getUrl()).toEqual(MOCK_URL + `/${expectedLicense}`);
    expect(artifact.getChecksumUrl()).toEqual(MOCK_URL + `/${expectedLicense}.sha512`);
    expect(artifact.getChecksumType()).toEqual('sha512');
    expect(artifact.getFilename()).toEqual(MOCK_FILENAME + `.${expectedLicense}`);
  };
};

describe('Artifact', () => {
  describe('getSnapshot()', () => {
    describe('with default snapshot', () => {
      beforeEach(() => {
        mockFetch(MOCKS.valid);
      });

      it('should return artifact metadata for an oss artifact', artifactTest('oss', 'oss'));

      it(
        'should return artifact metadata for a default artifact',
        artifactTest('default', 'default')
      );

      it(
        'should default to default license with anything other than "oss"',
        artifactTest('INVALID_LICENSE', 'default')
      );

      it('should throw when an artifact cannot be found in the manifest for the specified parameters', async () => {
        await expect(Artifact.getSnapshot('default', 'INVALID_VERSION', log)).rejects.toThrow(
          "couldn't find an artifact"
        );
      });
    });
  });
});
