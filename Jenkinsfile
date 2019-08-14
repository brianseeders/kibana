// TODO is there a way to make this actually work with the Jenkins sandbox?
@NonCPS
def getJobs() {
  def jobs = [:]
  (1..12).each { jobs["oss-ciGroup${it}"] = ossCiGroupRunner(it) }
  (1..10).each { jobs["xpack-ciGroup${it}"] = xpackCiGroupRunner(it) }

  return jobs
}

def workspaceArchiveFilename = 'workspace.archive.tar.gz'
def homeArchiveFilename = 'home.archive.tar.gz'

// TODO - temporary stage wrapper
def cStage(name, closure) {
  print "Stage: ${name}"
  closure()
}

timestamps {
  ansiColor('xterm') {
    // parallel([
    //   'oss-ciGroup1': ossCiGroupRunner(1),
    //   // 'oss-ciGroup2': ossCiGroupRunner(2),
    //   // 'oss-ciGroup3': ossCiGroupRunner(3),
    //   // 'oss-ciGroup4': ossCiGroupRunner(4),
    //   // 'oss-ciGroup5': ossCiGroupRunner(5),
    //   // 'oss-ciGroup6': ossCiGroupRunner(6),
    //   // 'oss-ciGroup7': ossCiGroupRunner(7),
    //   // 'oss-ciGroup8': ossCiGroupRunner(8),
    //   // 'oss-ciGroup9': ossCiGroupRunner(9),
    //   // 'oss-ciGroup10': ossCiGroupRunner(10),
    //   // 'oss-ciGroup11': ossCiGroupRunner(11),
    //   // 'oss-ciGroup12': ossCiGroupRunner(12),
    //   // 'xpack-ciGroup1': xpackCiGroupRunner(1),
    //   'xpack-ciGroup2': xpackCiGroupRunner(2),
    //   // 'xpack-ciGroup3': xpackCiGroupRunner(3),
    //   // 'xpack-ciGroup4': xpackCiGroupRunner(4),
    //   // 'xpack-ciGroup5': xpackCiGroupRunner(5),
    //   // 'xpack-ciGroup6': xpackCiGroupRunner(6),
    //   // 'xpack-ciGroup7': xpackCiGroupRunner(7),
    //   // 'xpack-ciGroup8': xpackCiGroupRunner(8),
    //   // 'xpack-ciGroup9': xpackCiGroupRunner(9),
    //   // 'xpack-ciGroup10': xpackCiGroupRunner(10),
    // ])
    withOssWorker {
      run = {
        cStage('Archive workspace') {
          bash "touch ${workspaceArchiveFilename} && tar -czf ${workspaceArchiveFilename} --exclude=${workspaceArchiveFilename} . /var/lib/jenkins/.kibana/node"
        }
        cStage('Store workspace') {
          step([
            $class: 'ClassicUploadStep',
            credentialsId: 'kibana-ci-gcs-plugin',
            bucket: "gs://kibana-pipeline-testing/workspaces/latest",
            pattern: workspaceArchiveFilename,
          ])
        }
      }
    }

    cStage('Worker node') {
      node('linux && immutable') {
        skipDefaultCheckout()

        env.HOME = env.JENKINS_HOME 

        withEnv 
      }
    }
  }
}

def withOssWorker(body) {
  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()

  withBootstrappedWorker {
    if (body.beforeBuild) {
      body.beforeBuild()
    }

    cStage('Build OSS Kibana') {
      withEnv 'node scripts/build --debug --oss'
    }

    body.run()
  }
}

def withXpackWorker(closure) {
  withBootstrappedWorker {
    cStage('Build Default Kibana') {
      withEnv 'node scripts/build --debug --no-oss'
    }

    closure()
  }
}

def withBootstrappedWorker(closure) {
  node('linux && immutable') {
    skipDefaultCheckout()

    env.HOME = env.JENKINS_HOME

    cStage('Checkout') {
      def scmVars = checkout scm
      env.GIT_BRANCH = scmVars.GIT_BRANCH
    }

    dir('./kibana') {
      cStage('Extract bootstrap cache') {
        bash 'env | sort'

        bash '''#!/usr/bin/env bash
  # targetBranch="${PR_TARGET_BRANCH:-${GIT_BRANCH#*/}}"
  targetBranch="master"
  bootstrapCache="$HOME/.kibana/bootstrap_cache/$targetBranch.tar"

  ###
  ### Extract the bootstrap cache that we create in the packer_cache.sh script
  ###
  if [ -f "$bootstrapCache" ]; then
  echo "extracting bootstrap_cache from $bootstrapCache";
  tar -xf "$bootstrapCache";
  else
  echo ""
  echo ""
  echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";
  echo "            bootstrap_cache missing";
  echo "            looked for '$bootstrapCache'";
  echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";
  echo ""
  echo ""
  fi'''
      }
      
      cStage('setup.sh') {
        bash 'source src/dev/ci_setup/setup.sh'
      }

      cStage('Sibling ES') {
        bash 'rm -rf ../elasticsearch'
        bash 'source src/dev/ci_setup/setup_docker.sh; source src/dev/ci_setup/checkout_sibling_es.sh'
      }

      closure()
    }
  }
}

def bash(script) {
  sh "#!/bin/bash -x\n${script}"
}

def withEnv(script){
  bash '''set -e

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

###
### check that we seem to be in a kibana project
###
if [ -f "$dir/package.json" ] && [ -f "$dir/.node-version" ]; then
  echo "Setting up node.js and yarn in $dir"
else
  echo "${RED}src/dev/ci_setup/setup.sh must be run within a kibana repo${C_RESET}"
  exit 1
fi


export KIBANA_DIR="$dir"
export XPACK_DIR="$KIBANA_DIR/x-pack"

parentDir="$(cd "$KIBANA_DIR/.."; pwd)"
export PARENT_DIR="$parentDir"

kbnBranch="$(jq -r .branch "$KIBANA_DIR/package.json")"
export KIBANA_PKG_BRANCH="$kbnBranch"

UNAME=$(uname)
OS="linux"
if [[ "$UNAME" = *"MINGW64_NT"* ]]; then
  OS="win"
fi

nodeVersion="$(cat "$dir/.node-version")"
nodeDir="$cacheDir/node/$nodeVersion"

if [[ "$OS" == "win" ]]; then
  nodeBin="$HOME/node"
else
  nodeBin="$nodeDir/bin"
fi

export PATH="$nodeBin:$PATH"

yarnGlobalDir="$(yarn global bin || echo '')"
export PATH="$PATH:$yarnGlobalDir"

# use a proxy to fetch chromedriver/geckodriver asset
export GECKODRIVER_CDNURL="https://us-central1-elastic-kibana-184716.cloudfunctions.net/kibana-ci-proxy-cache"
export CHROMEDRIVER_CDNURL="https://us-central1-elastic-kibana-184716.cloudfunctions.net/kibana-ci-proxy-cache"

export CHECKS_REPORTER_ACTIVE=false

function checks-reporter-with-killswitch() {
  if [ "$CHECKS_REPORTER_ACTIVE" == "true" ] ; then
    yarn run github-checks-reporter "$@"
  else
    arguments=("$@");
    "${arguments[@]:1}";
  fi
}

''' + script
}

def reportFailedTests() {
  withEnv 'node "$KIBANA_DIR/src/dev/failed_tests/cli"'
}

def ossCiGroupRunner(ciGroupNumber, additionalScript='') {
  return {
    stage("oss-ciGroup${ciGroupNumber}") {
      // TODO need to move functionalTests:ensureAllTestsInCiGroup to before the build

      withOssWorker {
        beforeBuild = {
          withEnv "yarn run grunt functionalTests:ensureAllTestsInCiGroup"
        }

        run = {
          try {
            withEnv """
              set -e
              export CI_GROUP=${ciGroupNumber}
              export TEST_BROWSER_HEADLESS=1

              

              checks-reporter-with-killswitch "Functional tests / Group ${ciGroupNumber}" yarn run grunt "run:functionalTests_ciGroup${ciGroupNumber}"

              ${additionalScript}
            """
          }
          catch(exception) {
            reportFailedTests()
            throw exception
          }
        }
      }
    }
  }
}

def xpackCiGroupRunner(ciGroupNumber, additionalScript='') {
  return {
    stage("xpack-ciGroup${ciGroupNumber}") {
      // TODO need to move 'Ensuring all functional tests are in a ciGroup' to before the build

      withXpackWorker {
        try {
          withEnv """
            set -e
            export CI_GROUP=${ciGroupNumber}
            export TEST_BROWSER_HEADLESS=1

            cd "\$XPACK_DIR"

            echo " -> Ensuring all functional tests are in a ciGroup"
            cd "\$XPACK_DIR"
            node scripts/functional_tests --assert-none-excluded \
              --include-tag ciGroup1 \
              --include-tag ciGroup2 \
              --include-tag ciGroup3 \
              --include-tag ciGroup4 \
              --include-tag ciGroup5 \
              --include-tag ciGroup6 \
              --include-tag ciGroup7 \
              --include-tag ciGroup8 \
              --include-tag ciGroup9 \
              --include-tag ciGroup10

            echo " -> building and extracting default Kibana distributable for use in functional tests"
            cd "\$KIBANA_DIR"
            node scripts/build --debug --no-oss
            linuxBuild="\$(find "\$KIBANA_DIR/target" -name 'kibana-*-linux-x86_64.tar.gz')"
            installDir="\$PARENT_DIR/install/kibana"
            mkdir -p "\$installDir"
            tar -xzf "\$linuxBuild" -C "\$installDir" --strip=1

            echo " -> Running functional and api tests"
            cd "\$XPACK_DIR"

            checks-reporter-with-killswitch "X-Pack Chrome Functional tests / Group ${ciGroupNumber}" \
              node scripts/functional_tests \
                --debug --bail \
                --kibana-install-dir "\$installDir" \
                --include-tag "ciGroup${ciGroupNumber}"

            echo ""
            echo ""

            ${additionalScript}
          """
        }
        catch(exception) {
          reportFailedTests()
          throw exception
        }
      }
    }
  }
}