/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */

import { createUsersAndRoles } from '../../common/lib/create_users_and_roles';
import { TestInvoker } from '../../common/lib/types';

// eslint-disable-next-line import/no-default-export
export default function ({ loadTestFile, getService }: TestInvoker) {
  const es = getService('legacyEs');
  const supertest = getService('supertest');

  describe('spaces api with security', function () {
    before(async () => {
      await createUsersAndRoles(es, supertest);
    });

    describe('', () => {
      this.tags('ciGroup8');

      loadTestFile(require.resolve('./copy_to_space'));
      loadTestFile(require.resolve('./resolve_copy_to_space_conflicts'));
    });

    describe('', () => {
      this.tags('ciGroup20');

      loadTestFile(require.resolve('./create'));
      loadTestFile(require.resolve('./delete'));
    });

    describe('', () => {
      this.tags('ciGroup24');

      loadTestFile(require.resolve('./get_all'));
      loadTestFile(require.resolve('./get'));
    });

    describe('', () => {
      this.tags('ciGroup22');

      loadTestFile(require.resolve('./share_add'));
      loadTestFile(require.resolve('./share_remove'));
    });

    describe('', () => {
      this.tags('ciGroup14');

      loadTestFile(require.resolve('./update'));
    });
  });
}
