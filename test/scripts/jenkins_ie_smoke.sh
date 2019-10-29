#!/usr/bin/env bash

set -e

if [[ -z "$IS_PIPELINE_JOB" ]] ; then
  trap 'node "$KIBANA_DIR/src/dev/failed_tests/cli"' EXIT
else
  source src/dev/ci_setup/setup_env.sh
fi

# if [[ -z "$IS_PIPELINE_JOB" ]] ; then
#   node scripts/build --debug --oss;
#   windowsBuild="$(find "$KIBANA_DIR/target" -name 'kibana-oss-*-windows-x86_64.zip')"
#   installDir="$PARENT_DIR/install"
#   mkdir -p "$installDir"
#   unzip -q "$windowsBuild" -d "$installDir"
#   installDir="$(find "$installDir" -maxdepth 1 -name 'kibana-*-SNAPSHOT-windows-x86_64')"
# else
#   installDir="$(realpath $PARENT_DIR/kibana/build/oss/kibana-*-SNAPSHOT-linux-x86_64)"
#   destDir=${installDir}-${CI_WORKER_NUMBER}
#   cp -R "$installDir" "$destDir"

#   export KIBANA_INSTALL_DIR="$destDir"
# fi

# --kibana-install-dir "$installDir" \

checks-reporter-with-killswitch "Internet Explorer smoke test" \
  node scripts/functional_tests \
    --bail --debug \
    --include-tag "ciGroup1" \
    --config test/functional/config.ie.js;
