#!/bin/groovy
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
    parallel([
      'oss-kibana': {
        buildOssKibana()

        parallel([
          'oss-ciGroup1': ossCiGroupRunner(1),
          'oss-ciGroup2': ossCiGroupRunner(2),
          'oss-ciGroup3': ossCiGroupRunner(3),
          'oss-ciGroup4': ossCiGroupRunner(4),
          'oss-ciGroup5': ossCiGroupRunner(5),
          'oss-ciGroup6': ossCiGroupRunner(6),
          'oss-ciGroup7': ossCiGroupRunner(7),
          'oss-ciGroup8': ossCiGroupRunner(8),
          'oss-ciGroup9': ossCiGroupRunner(9),
          'oss-ciGroup10': ossCiGroupRunner(10),
          'oss-ciGroup11': ossCiGroupRunner(11),
          'oss-ciGroup12': ossCiGroupRunner(12),
        ])
      },
      'default-kibana': {
        buildDefaultKibana()

        parallel([
          'xpack-ciGroup1': xpackCiGroupRunner(1),
          'xpack-ciGroup2': xpackCiGroupRunner(2),
          'xpack-ciGroup3': xpackCiGroupRunner(3),
          'xpack-ciGroup4': xpackCiGroupRunner(4),
          'xpack-ciGroup5': xpackCiGroupRunner(5),
          'xpack-ciGroup6': xpackCiGroupRunner(6),
          'xpack-ciGroup7': xpackCiGroupRunner(7),
          'xpack-ciGroup8': xpackCiGroupRunner(8),
          'xpack-ciGroup9': xpackCiGroupRunner(9),
          'xpack-ciGroup10': xpackCiGroupRunner(10),
        ])
      },
      'oss-intake': {
        withBootstrappedWorker {
          stage('OSS Intake') {
            withTestReporter {
              withEnv 'export TEST_BROWSER_HEADLESS=1; "$(FORCE_COLOR=0 yarn bin)/grunt" jenkins:unit --dev'
            }
          }
        }
      },
      'default-intake': {
        withBootstrappedWorker {
          cStage('Default Intake') {
            withTestReporter {
              withEnv '''
                export TEST_BROWSER_HEADLESS=1

                echo " -> Running mocha tests"
                cd "$XPACK_DIR"
                checks-reporter-with-killswitch "X-Pack Mocha" yarn test
                echo ""
                echo ""

                echo " -> Running jest tests"
                cd "$XPACK_DIR"
                checks-reporter-with-killswitch "X-Pack Jest" node scripts/jest --ci --verbose
                echo ""
                echo ""

                echo " -> Running SIEM cyclic dependency test"
                cd "$XPACK_DIR"
                checks-reporter-with-killswitch "X-Pack SIEM cyclic dependency test" node legacy/plugins/siem/scripts/check_circular_deps
                echo ""
                echo ""

                echo " -> Running jest contracts tests"
                cd "$XPACK_DIR"
                SLAPSHOT_ONLINE=true CONTRACT_ONLINE=true node scripts/jest_contract.js --ci --verbose
                echo ""
                echo ""
              '''
            }
          }
        }
      },
    ])
  }
}

def buildOssKibana() {
  withBootstrappedWorker {
    stage('Build OSS Kibana') {
      withEnv 'node scripts/build --debug --oss'
    }

    def workspaceArchiveFilename = 'workspace-oss.archive.tar.gz'

    stage('Archive workspace') {
      bash "touch ${workspaceArchiveFilename} && tar -czf ${workspaceArchiveFilename} --exclude=${workspaceArchiveFilename} . /var/lib/jenkins/.kibana/node"
    }

    stage('Upload workspace') {
      step([
        $class: 'ClassicUploadStep',
        credentialsId: 'kibana-ci-gcs-plugin',
        bucket: "gs://kibana-pipeline-testing/workspaces/latest",
        pattern: workspaceArchiveFilename,
      ])
    }
  }
}

def buildDefaultKibana() {
  withBootstrappedWorker {
    cStage('Build Default Kibana') {
      withEnv 'node scripts/build --debug --no-oss'
    }

    def workspaceArchiveFilename = 'workspace-default.archive.tar.gz'

    stage('Archive workspace') {
      bash "touch ${workspaceArchiveFilename} && tar -czf ${workspaceArchiveFilename} --exclude=${workspaceArchiveFilename} . /var/lib/jenkins/.kibana/node"
    }

    stage('Upload workspace') {
      step([
        $class: 'ClassicUploadStep',
        credentialsId: 'kibana-ci-gcs-plugin',
        bucket: "gs://kibana-pipeline-testing/workspaces/latest",
        pattern: workspaceArchiveFilename,
      ])
    }
  }
}

def withBootstrappedWorker(closure) {
  node('linux && immutable') {
    def workspaceArchiveFilename = 'workspace.archive.tar.gz'

    skipDefaultCheckout()

    env.HOME = env.JENKINS_HOME

    parallel([
      checkout: {
        cStage('Checkout') {
          def scmVars = checkout scm
          env.GIT_BRANCH = scmVars.GIT_BRANCH
        }
      },
      cache: {
        cStage('Bootstrap cache') {
          bash '''
            targetBranch="master"
            bootstrapCache="$HOME/.kibana/bootstrap_cache/$targetBranch.tar"

            ###
            ### Extract the bootstrap cache that we create in the packer_cache.sh script
            ###
            if [ -f "$bootstrapCache" ]; then
              mkdir -p ./bootstrap-cache
              echo "extracting bootstrap_cache from $bootstrapCache";
              tar -xf "$bootstrapCache" -C ./bootstrap-cache;
            fi
          '''
        }
        // cStage('Download archive') {
        //   step([
        //     $class: 'DownloadStep',
        //     credentialsId: 'kibana-ci-gcs-plugin',
        //     bucketUri: "gs://kibana-pipeline-testing/workspaces/latest/${workspaceArchiveFilename}",
        //     localDirectory: env.WORKSPACE
        //   ])
        // }
      }
    ])

    dir('./kibana') {
      // cStage('Extract archive') {
      //   bash "tar -xzf ../workspaces/latest/${workspaceArchiveFilename}"
      //   bash 'rm -rf /var/lib/jenkins/.kibana/node && mv var/lib/jenkins/.kibana/node /var/lib/jenkins/.kibana/'
      //   bash 'git reset --hard'
      // }

      cStage('Move bootstrap-cache') {
        bash 'cp -aTl ../bootstrap-cache/ ./'
      }
      
      stage('setup.sh') {
        bash 'source src/dev/ci_setup/setup.sh'
      }

      stage('Sibling ES') {
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

# TODO
export NODE_OPTIONS="--max_old_space_size=2048"

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

def withTestReporter(closure) {
  try {
    closure()
  } catch(ex) {
    reportFailedTests()
    throw ex
  }
}

def reportFailedTests() {
  withEnv 'node "$KIBANA_DIR/src/dev/failed_tests/cli"'
}

def ossCiGroupRunner(ciGroupNumber, additionalScript='') {
  return {
    stage("oss-ciGroup${ciGroupNumber}") {
      // TODO need to move functionalTests:ensureAllTestsInCiGroup to before the build
      node('linux && immutable') {
        skipDefaultCheckout()

        env.HOME = env.JENKINS_HOME 

        def workspaceArchiveFilename = 'workspace-oss.archive.tar.gz'

        cStage('Download archive') {
          step([
            $class: 'DownloadStep',
            credentialsId: 'kibana-ci-gcs-plugin',
            bucketUri: "gs://kibana-pipeline-testing/workspaces/latest/${workspaceArchiveFilename}",
            localDirectory: env.WORKSPACE
          ])
        }

        cStage('Extract archive') {
          bash "tar -xzf workspaces/latest/${workspaceArchiveFilename}"
          bash 'rm -rf /var/lib/jenkins/.kibana/node && mv var/lib/jenkins/.kibana/node /var/lib/jenkins/.kibana/'
        }

        withTestReporter {
          withEnv """
            set -e
            export CI_GROUP=${ciGroupNumber}
            export TEST_BROWSER_HEADLESS=1

            checks-reporter-with-killswitch "Functional tests / Group ${ciGroupNumber}" yarn run grunt "run:functionalTests_ciGroup${ciGroupNumber}"

            ${additionalScript}
          """
        }
      }
    }
  }
}

def xpackCiGroupRunner(ciGroupNumber, additionalScript='') {
  return {
    stage("xpack-ciGroup${ciGroupNumber}") {
      // TODO need to move 'Ensuring all functional tests are in a ciGroup' to before the build

      node('linux && immutable') {
        skipDefaultCheckout()

        env.HOME = env.JENKINS_HOME 

        def workspaceArchiveFilename = 'workspace-default.archive.tar.gz'

        cStage('Download archive') {
          step([
            $class: 'DownloadStep',
            credentialsId: 'kibana-ci-gcs-plugin',
            bucketUri: "gs://kibana-pipeline-testing/workspaces/latest/${workspaceArchiveFilename}",
            localDirectory: env.WORKSPACE
          ])
        }

        cStage('Extract archive') {
          bash "tar -xzf workspaces/latest/${workspaceArchiveFilename}"
          bash 'rm -rf /var/lib/jenkins/.kibana/node && mv var/lib/jenkins/.kibana/node /var/lib/jenkins/.kibana/'
        }

        withTestReporter {
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

            echo " -> extracting default Kibana distributable for use in functional tests"
            cd "\$KIBANA_DIR"
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
      }
    }
  }
}