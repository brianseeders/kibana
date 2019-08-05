#!/usr/bin/env bash

set -e

dir="$(pwd)"
cacheDir="${CACHE_DIR:-"$HOME/.kibana"}"

RED='\033[0;31m'
C_RESET='\033[0m' # Reset color

###
### Since the Jenkins logging output collector doesn't look like a TTY
### Node/Chalk and other color libs disable their color output. But Jenkins
### can handle color fine, so this forces https://github.com/chalk/supports-color
### to enable color support in Chalk and other related modules.
###
export FORCE_COLOR=1

export KIBANA_DIR="$dir"
export XPACK_DIR="$KIBANA_DIR/x-pack"

parentDir="$(cd "$KIBANA_DIR/.."; pwd)"
export PARENT_DIR="$parentDir"

kbnBranch="$(jq -r .branch "$KIBANA_DIR/package.json")"
export KIBANA_PKG_BRANCH="$kbnBranch"

echo " -- KIBANA_DIR='$KIBANA_DIR'"
echo " -- XPACK_DIR='$XPACK_DIR'"
echo " -- PARENT_DIR='$PARENT_DIR'"
echo " -- KIBANA_PKG_BRANCH='$KIBANA_PKG_BRANCH'"
echo " -- TEST_ES_SNAPSHOT_VERSION='$TEST_ES_SNAPSHOT_VERSION'"

function verifyNoGitModifications {
  return
  GIT_CHANGES="$(git ls-files --modified)"
  if [ "$GIT_CHANGES" ]; then
    echo -e "\n${RED}ERROR: '$1' caused changes to the following files:${C_RESET}\n"
    echo -e "$GIT_CHANGES\n"
    exit 1
  fi
}

###
### setup yarn offline cache
###
# yarn config set yarn-offline-mirror "$cacheDir/yarn-offline-cache"

###
### install dependencies
###
# echo " -- installing node.js dependencies"
# yarn kbn bootstrap --prefer-offline
# verifyNoGitModifications 'yarn kbn bootstrap'

###
### rebuild kbn-pm distributable to ensure it's not out of date
###
# echo " -- building kbn-pm distributable"
# yarn kbn run build -i @kbn/pm
# verifyNoGitModifications 'yarn kbn run build -i @kbn/pm'

###
### rebuild renovate config to ensure it's not out of date
###
# echo " -- building renovate config"
# node scripts/build_renovate_config
# verifyNoGitModifications 'scripts/build_renovate_config'