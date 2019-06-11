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

import chrome from 'ui/chrome';
import { SavedQuery } from '../../../search';

export const saveQuery = (savedQuery: SavedQuery) => {
  const savedObjectsClient = chrome.getSavedObjectsClient();

  const query = {
    query:
      typeof savedQuery.query.query === 'string'
        ? savedQuery.query.query
        : JSON.stringify(savedQuery.query.query),
    language: savedQuery.query.language,
  };

  const queryObject = {
    title: savedQuery.title,
    description: savedQuery.description,
    query,
    filters: '',
    timefilter: '',
  };

  if (savedQuery.filters) {
    queryObject.filters = JSON.stringify(savedQuery.filters);
  }

  if (savedQuery.timefilter) {
    queryObject.timefilter = JSON.stringify(savedQuery.timefilter);
  }

  savedObjectsClient.create('query', queryObject);
};
