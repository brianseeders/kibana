#!/usr/bin/env bash

source src/dev/ci_setup/setup_env.sh
echo " -> building kibana platform plugins"
node scripts/build_kibana_platform_plugins \
  --scan-dir "$XPACK_DIR/test/plugin_functional/plugins" \
  --scan-dir "$XPACK_DIR/test/functional_with_es_ssl/fixtures/plugins" \
  --scan-dir "$XPACK_DIR/test/alerting_api_integration/plugins" \
  --scan-dir "$XPACK_DIR/test/plugin_api_integration/plugins" \
  --scan-dir "$XPACK_DIR/test/plugin_api_perf/plugins" \
  --workers 20 \
  --verbose
