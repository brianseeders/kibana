FROM node:10.15.2 AS base

# dumb-init
RUN curl -Lo /usr/local/bin/dumb-init https://github.com/Yelp/dumb-init/releases/download/v1.2.2/dumb-init_1.2.2_amd64 \
  && chmod +x /usr/local/bin/dumb-init

# Chrome
RUN curl -sSL https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
  && sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list' \
  && apt-get update \
  && apt-get install -y google-chrome-stable fonts-ipafont-gothic fonts-wqy-zenhei fonts-thai-tlwg fonts-kacst ttf-freefont \
  --no-install-recommends \
  && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
# rm is so that Kaniko wont try to add tmp files to the image

WORKDIR /app

RUN groupadd -r kibana && useradd -r -g kibana kibana && mkdir /home/kibana && chown kibana:kibana /home/kibana

FROM base AS builder

RUN chown kibana /app
USER kibana

# .gitignored but committed in git
RUN mkdir /app/data && mkdir /app/plugins

COPY --chown=kibana:kibana preinstall_check.js /app/
COPY --chown=kibana:kibana scripts/kbn.js /app/scripts/kbn.js
COPY --chown=kibana:kibana src/setup_node_env /app/src/setup_node_env
COPY --chown=kibana:kibana package.json yarn.lock .yarnrc tsconfig.browser.json tsconfig.json tsconfig.types.json /app/
COPY --chown=kibana:kibana packages/elastic-datemath/package.json /app/packages/elastic-datemath/package.json
COPY --chown=kibana:kibana packages/eslint-config-kibana/package.json /app/packages/eslint-config-kibana/package.json
COPY --chown=kibana:kibana packages/kbn-babel-code-parser/package.json /app/packages/kbn-babel-code-parser/package.json
COPY --chown=kibana:kibana packages/kbn-babel-preset/package.json /app/packages/kbn-babel-preset/package.json
COPY --chown=kibana:kibana packages/kbn-config-schema/package.json /app/packages/kbn-config-schema/package.json
COPY --chown=kibana:kibana packages/kbn-dev-utils/package.json /app/packages/kbn-dev-utils/package.json
COPY --chown=kibana:kibana packages/kbn-elastic-idx/package.json /app/packages/kbn-elastic-idx/package.json
COPY --chown=kibana:kibana packages/kbn-es-query/package.json /app/packages/kbn-es-query/package.json
COPY --chown=kibana:kibana packages/kbn-es/package.json /app/packages/kbn-es/package.json
COPY --chown=kibana:kibana packages/kbn-eslint-import-resolver-kibana/package.json /app/packages/kbn-eslint-import-resolver-kibana/package.json
COPY --chown=kibana:kibana packages/kbn-eslint-plugin-eslint/package.json /app/packages/kbn-eslint-plugin-eslint/package.json
COPY --chown=kibana:kibana packages/kbn-expect/package.json /app/packages/kbn-expect/package.json
COPY --chown=kibana:kibana packages/kbn-i18n/package.json /app/packages/kbn-i18n/package.json
COPY --chown=kibana:kibana packages/kbn-interpreter/package.json /app/packages/kbn-interpreter/package.json
COPY --chown=kibana:kibana packages/kbn-maki/package.json /app/packages/kbn-maki/package.json
COPY --chown=kibana:kibana packages/kbn-plugin-generator/package.json /app/packages/kbn-plugin-generator/package.json
COPY --chown=kibana:kibana packages/kbn-plugin-helpers/package.json /app/packages/kbn-plugin-helpers/package.json
COPY --chown=kibana:kibana packages/kbn-pm/package.json /app/packages/kbn-pm/package.json
COPY --chown=kibana:kibana packages/kbn-spec-to-console/package.json /app/packages/kbn-spec-to-console/package.json
COPY --chown=kibana:kibana packages/kbn-test-subj-selector/package.json /app/packages/kbn-test-subj-selector/package.json
COPY --chown=kibana:kibana packages/kbn-test/package.json /app/packages/kbn-test/package.json
COPY --chown=kibana:kibana packages/kbn-ui-framework/package.json /app/packages/kbn-ui-framework/package.json
COPY --chown=kibana:kibana x-pack/package.json /app/x-pack/package.json
COPY --chown=kibana:kibana x-pack/legacy/plugins/infra/package.json /app/x-pack/legacy/plugins/infra/package.json
COPY --chown=kibana:kibana x-pack/legacy/plugins/siem/package.json /app/x-pack/legacy/plugins/siem/package.json
COPY --chown=kibana:kibana x-pack/legacy/plugins/siem/yarn.lock /app/x-pack/legacy/plugins/siem/yarn.lock
COPY --chown=kibana:kibana test/plugin_functional/plugins/core_plugin_a/package.json /app/test/plugin_functional/plugins/core_plugin_a/package.json
COPY --chown=kibana:kibana test/plugin_functional/plugins/core_plugin_b/package.json /app/test/plugin_functional/plugins/core_plugin_b/package.json
COPY --chown=kibana:kibana test/plugin_functional/plugins/kbn_tp_custom_visualizations/package.json /app/test/plugin_functional/plugins/kbn_tp_custom_visualizations/package.json
COPY --chown=kibana:kibana test/plugin_functional/plugins/kbn_tp_embeddable_explorer/package.json /app/test/plugin_functional/plugins/kbn_tp_embeddable_explorer/package.json
COPY --chown=kibana:kibana test/plugin_functional/plugins/kbn_tp_sample_app_plugin/package.json /app/test/plugin_functional/plugins/kbn_tp_sample_app_plugin/package.json
COPY --chown=kibana:kibana test/plugin_functional/plugins/kbn_tp_sample_panel_action/package.json /app/test/plugin_functional/plugins/kbn_tp_sample_panel_action/package.json
COPY --chown=kibana:kibana test/plugin_functional/plugins/kbn_tp_visualize_embedding/package.json /app/test/plugin_functional/plugins/kbn_tp_visualize_embedding/package.json
COPY --chown=kibana:kibana test/interpreter_functional/plugins/kbn_tp_run_pipeline/package.json /app/test/interpreter_functional/plugins/kbn_tp_run_pipeline/package.json

RUN yarn install --frozen-lockfile

COPY --chown=kibana:kibana . /app

RUN yarn kbn bootstrap --frozen-lockfile
RUN rm -rf node_modules/\@elastic/nodegit/.vscode

FROM base AS final

USER kibana

COPY --from=builder /app /app

ENTRYPOINT ["/usr/local/bin/dumb-init", "--", "yarn"]
CMD ["start"]
# CMD ["/bin/bash"]