def archiveFilename = "archive.tar.gz"
def scmVars = null

pipeline {
  agent none
  options {
    timestamps()
    ansiColor('xterm')
    disableResume()
    durabilityHint('PERFORMANCE_OPTIMIZED')
    skipDefaultCheckout()
  }
  stages {
    stage('Create archive') {
      agent {
        label 'linux && immutable'
      }
      environment {
       HOME="/var/lib/jenkins"
      }
      stages {
        stage('Checkout') {
          steps {
            checkout scm
          }
        }
        stage('Extract bootstrap cache') {
          steps {
            sh 'env | sort'

            sh '''#!/usr/bin/env bash
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
        }
        stage('Setup and archive') {
          stages {
            stage('setup.sh') {
              steps {
                sh 'env | sort'
                print scmVars
                sh 'bash src/dev/ci_setup/setup.sh'
              }
            }
            stage('Sibling ES') {
              steps {
                sh 'bash src/dev/ci_setup/checkout_sibling_es.sh'
              }
            }
            stage('Archive workspace') {
              steps {
                sh "tar -xzf ${archiveFilename} ."
              }
            }
            stage('Store workspace') {
              steps {
                step([
                  $class: 'ClassicUploadStep',
                  credentialsId: 'kibana-ci-gcs-plugin',
                  bucket: "gs://kibana-pipeline-testing/workspaces/latest",
                  pattern: archiveFilename,
                ])
              }
            }
          }
        }
      }
    }
  }
}