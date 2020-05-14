#!/usr/bin/env bash

source test/scripts/jenkins_test_setup.sh

if [[ -z "$CODE_COVERAGE" ]]; then

  destDir="build/kibana-build-oss"
  if [[ ! "$TASK_QUEUE_PROCESS_ID" ]]; then
    destDir="${destDir}-${CI_PARALLEL_PROCESS_NUMBER}"
  fi

  if [[ ! -d $destDir ]]; then
    mkdir -p $destDir
    cp -pR "$WORKSPACE/kibana-build-oss/." $destDir/

    if [[ "$TASK_QUEUE_PROCESS_ID" ]]; then
      echo " -> building kibana platform plugins"
      node scripts/build_kibana_platform_plugins \
        --oss \
        --scan-dir "$KIBANA_DIR/test/plugin_functional/plugins" \
        --workers 2 \
        --verbose;
    fi
  fi

  export KIBANA_INSTALL_DIR="$destDir"
fi
