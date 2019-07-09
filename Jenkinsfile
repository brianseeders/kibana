#!/usr/bin/env groovy

pipeline {
  agent none
  environment {
    // Global vars
    CI = true
    BASE_DIR = "."
    CI_DIR = "./.ci"
    GROOVY_SRC = "${CI_DIR}/src/groovy"
    HOME = "${JENKINS_HOME}"  // /var/lib/jenkins
    MAIN_CACHE_DIR = "${JENKINS_HOME}/.kibana" // /var/lib/jenkins/.kibana
    BOOTSTRAP_CACHE_DIR = "${MAIN_CACHE_DIR}/bootstrap_cache" // /var/lib/jenkins/.kibana/bootstrap_cache
    WORKSPACE_DIR = "${JENKINS_HOME}/workspace" // /var/lib/jenkins/workspace
    WORKSPACE_CACHE_DIR = "${MAIN_CACHE_DIR}/workspace_cache" // /var/lib/jenkins/.kibana/workspace_cache
//    WORKSPACE_CACHE_NAME = "JOB_NAME-${JOB_NAME}-BUILD_ID-${BUILD_ID}.tgz"
    // /var/lib/jenkins/.kibana/workspace_cache/JOB_NAME-SOMEBRANCHNAME-BUILD_ID-SOMEBUILDNUMBER.tgz
//    FULL_WORKSPACE_CACHE_PATH = "${WORKSPACE_CACHE_DIR}/${WORKSPACE_CACHE_NAME}"
    TEMP_PIPELINE_SETUP_DIR = "src/dev/temp_pipeline_setup"
    // PR_SOURCE_BRANCH = "${ghprbSourceBranch}"
    // PR_TARGET_BRANCH = "${ghprbTargetBranch}"
    // PR_AUTHOR = "${ghprbPullAuthorLogin}"
    CREDENTIALS_ID ='kibana-ci-gcs-plugin'
    KIBANA_DIR = "${WORKSPACE}"
    // BUCKET = "gs://kibana-ci-artifacts/jobs/${JOB_NAME}/${BUILD_NUMBER}"
    // PATTERN = "${FULL_WORKSPACE_CACHE_PATH}"
  }
  stages {
    stage('Install All-The-Things') {
      agent { label 'linux || immutable' }
      steps {
        mkdirp(env.WORKSPACE_CACHE_DIR)
        // dir("${env.BASE_DIR}"){
          // sh "${CI_DIR}/run_pipeline.sh"
        script {
          def d = load("${env.GROOVY_SRC}/dump.groovy")
          // def t = load("${env.GROOVY_SRC}/tar.groovy")
          d.dumpEnv()
          // d.dumpSize(env.KIBANA_DIR)
          // println "### d.branchName: ${d.branchName()}"
          // t.tarAll(env)
          // d.dumpSizes([
          //   "${env.WORKSPACE}",
          //   "${env.WORKSPACE_DIR}/elasticsearch",
          //   "${t.workspaceCacheName(env)}"
          // ])
        }
          // step([$class: 'ClassicUploadStep',
          //   credentialsId: env.CREDENTIALS_ID, bucket: ?, pattern: ?])
        // }
      }
    }
    stage('kibana-intake') {
      agent { label 'linux || immutable' }
      // options { skipDefaultCheckout() }
      steps {
        script {
          mkdirp(env.WORKSPACE_CACHE_DIR)
        }
        // step([$class: 'DownloadStep', credentialsId: env.CREDENTIALS_ID,
        //   bucketUri: ?
        //   bucketUri: "gs://kibana-ci-artifacts/jobs/${JOB_NAME}/${BUILD_ID}/var/lib/jenkins/.kibana/workspace_cache/JOB_NAME-kibana-automation-pipeline-BUILD_ID-${BUILD_ID}.tgz", localDirectory: "${WORKSPACE_CACHE_DIR}"])
        // script { unTar(env) }
//        dir("${WORKSPACE}"){
//          sh './test/scripts/jenkins_unit.sh'
//        }
      }
    }
    stage('Component Integration Tests') {
      agent { label 'linux || immutable' }
      options { skipDefaultCheckout() }
      steps {
        sh 'echo "Not implemented yet"'
      }
    }
    stage('Functional Tests') {
      agent { label 'linux || immutable' }
      options { skipDefaultCheckout() }
      steps {
        sh 'echo "Not implemented yet"'
      }
    }
    stage('Finish') {
      agent { label 'linux || immutable' }
      options { skipDefaultCheckout() }
      steps {
        sh 'echo "Not implemented yet"'
      }
    }
  }
}
def clearDir(String x){
  dir(x){
    sh 'rm -rf ./*'
  }
}
def mkdirp(x){ sh "mkdir -p ${x}" }
