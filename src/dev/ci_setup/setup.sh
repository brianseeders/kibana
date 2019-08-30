#!/usr/bin/env bash

set -e

source src/dev/ci_setup/setup_env.sh true

echo " -- KIBANA_DIR='$KIBANA_DIR'"
echo " -- XPACK_DIR='$XPACK_DIR'"
echo " -- PARENT_DIR='$PARENT_DIR'"

# uncomment to pin to a snapshot from: https://artifacts-api.elastic.co/v1/branches/{branch}/builds
# export TEST_ES_SNAPSHOT_VERSION=8.0.0-5480a616
echo " -- TEST_ES_SNAPSHOT_VERSION='$TEST_ES_SNAPSHOT_VERSION'"

###
### install dependencies
###
echo " -- installing node.js dependencies"
yarn kbn bootstrap --prefer-offline

###
### verify no git modifications
###
GIT_CHANGES="$(git ls-files --modified)"
if [ "$GIT_CHANGES" ]; then
  echo -e "\n${RED}ERROR: 'yarn kbn bootstrap' caused changes to the following files:${C_RESET}\n"
  echo -e "$GIT_CHANGES\n"
  exit 1
fi

  if [ "$changes" != "" ] && [ "$diff" == "" ]; then
    echo ""
    echo -e "${RED}WARNING: hard reseting repo to discard un-diffable changes:\n"
    echo ""
    echo "$changes"
    echo ""
    git reset --hard;
  fi
}

###
### install dependencies
###
echo " -- installing node.js dependencies"
verifyRunWithoutChanges yarn kbn bootstrap --prefer-offline

# skip kbn-pm build check on windows, installed packages are slightly different so module ids are slightly different
if [ "$OS" != "win" ]; then
  ###
  ### rebuild kbn-pm distributable to ensure it's not out of date
  ###
  echo " -- building kbn-pm distributable"
  verifyRunWithoutChanges yarn kbn run build -i @kbn/pm
fi

###
### rebuild kbn-pm distributable to ensure it's not out of date
###
echo " -- building renovate config"
node scripts/build_renovate_config

###
### verify no git modifications
###
GIT_CHANGES="$(git ls-files --modified)"
if [ "$GIT_CHANGES" ]; then
  echo -e "\n${RED}ERROR: 'node scripts/build_renovate_config' caused changes to the following files:${C_RESET}\n"
  echo -e "$GIT_CHANGES\n"
  exit 1
fi
