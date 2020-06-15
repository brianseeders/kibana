/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */

import { DefaultVersionNumber } from './default_version_number';
import { pipe } from 'fp-ts/lib/pipeable';
import { left } from 'fp-ts/lib/Either';
import { foldLeftRight, getPaths } from '../../../test_utils';

describe('default_version_number', () => {
  test('it should validate a version number', () => {
    const payload = 5;
    const decoded = DefaultVersionNumber.decode(payload);
    const message = pipe(decoded, foldLeftRight);

    expect(getPaths(left(message.errors))).toEqual([]);
    expect(message.schema).toEqual(payload);
  });

  test('it should not validate a 0', () => {
    const payload = 0;
    const decoded = DefaultVersionNumber.decode(payload);
    const message = pipe(decoded, foldLeftRight);

    expect(getPaths(left(message.errors))).toEqual(['Invalid value "0" supplied to ""']);
    expect(message.schema).toEqual({});
  });

  test('it should not validate a -1', () => {
    const payload = -1;
    const decoded = DefaultVersionNumber.decode(payload);
    const message = pipe(decoded, foldLeftRight);

    expect(getPaths(left(message.errors))).toEqual(['Invalid value "-1" supplied to ""']);
    expect(message.schema).toEqual({});
  });

  test('it should not validate a string', () => {
    const payload = '5';
    const decoded = DefaultVersionNumber.decode(payload);
    const message = pipe(decoded, foldLeftRight);

    expect(getPaths(left(message.errors))).toEqual(['Invalid value "5" supplied to ""']);
    expect(message.schema).toEqual({});
  });

  test('it should return a default of 1', () => {
    const payload = null;
    const decoded = DefaultVersionNumber.decode(payload);
    const message = pipe(decoded, foldLeftRight);

    expect(getPaths(left(message.errors))).toEqual([]);
    expect(message.schema).toEqual(1);
  });
});
