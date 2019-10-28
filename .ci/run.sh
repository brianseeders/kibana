#!/usr/bin/env bash

set -e

# move to Kibana root
cd "$(dirname "$0")/.."

source src/dev/ci_setup/load_env_keys.sh
source src/dev/ci_setup/extract_bootstrap_cache.sh
source src/dev/ci_setup/setup.sh
source src/dev/ci_setup/checkout_sibling_es.sh

case "$JOB" in
kibana-intake | windows-kibana-intake)
  ./test/scripts/jenkins_unit.sh
  ;;
kibana-ciGroup* | windows-kibana-ciGroup*)
  export CI_GROUP="${JOB##kibana-ciGroup}"
  ./test/scripts/jenkins_ci_group.sh
  ;;
kibana-visualRegression* | windows-kibana-visualRegression*)
  ./test/scripts/jenkins_visual_regression.sh
  ;;
kibana-firefoxSmoke* | windows-kibana-firefoxSmoke*)
  ./test/scripts/jenkins_firefox_smoke.sh
  ;;
x-pack-intake | windows-x-pack-intake)
  ./test/scripts/jenkins_xpack.sh
  ;;
x-pack-ciGroup* | windows-x-pack-ciGroup*)
  export CI_GROUP="${JOB##x-pack-ciGroup}"
  ./test/scripts/jenkins_xpack_ci_group.sh
  ;;
x-pack-visualRegression* | windows-x-pack-visualRegression*)
  ./test/scripts/jenkins_xpack_visual_regression.sh
  ;;
x-pack-firefoxSmoke* | windows-x-pack-firefoxSmoke*)
  ./test/scripts/jenkins_xpack_firefox_smoke.sh
  ;;
*)
  echo "JOB '$JOB' is not implemented."
  exit 1
  ;;
esac
