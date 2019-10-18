#!/bin/groovy

library 'kibana-pipeline-library'

isBeforeStart = false
agentLabel = 'tests-xl'

stage("Kibana Pipeline") { // This stage is just here to help the BlueOcean UI a little bit
  timeout(time: 180, unit: 'MINUTES') {
    timestamps {
      ansiColor('xterm') {
        catchError {
          parallel([
            // 'kibana-intake-agent': legacyJobRunner('kibana-intake'),
            // 'x-pack-intake-agent': legacyJobRunner('x-pack-intake'),
            'kibana-oss-agent': withWorkers('kibana-oss-tests', { buildOss() }, [
              'oss-ciGroup1': getOssCiGroupWorker(1),
              'oss-ciGroup2': getOssCiGroupWorker(2),
              'oss-ciGroup3': getOssCiGroupWorker(3),
              'oss-ciGroup4': getOssCiGroupWorker(4),
              'oss-ciGroup5': getOssCiGroupWorker(5),
              'oss-ciGroup6': getOssCiGroupWorker(6),
              'oss-ciGroup7': getOssCiGroupWorker(7),
              'oss-ciGroup8': getOssCiGroupWorker(8),
              'oss-ciGroup9': getOssCiGroupWorker(9),
              'oss-ciGroup10': getOssCiGroupWorker(10),
              'oss-ciGroup11': getOssCiGroupWorker(11),
              'oss-ciGroup12': getOssCiGroupWorker(12),
              'oss-firefoxSmoke': getPostBuildWorker('firefoxSmoke', { runbld './test/scripts/jenkins_firefox_smoke.sh' }),
              // 'oss-visualRegression': getPostBuildWorker('visualRegression', { runbld './test/scripts/jenkins_visual_regression.sh' }),
            ]),
            'kibana-oss-agent-2': withWorkers('kibana-oss-tests-2', { buildOss() }, [
              'oss-ciGroup1': getOssCiGroupWorker(1),
              'oss-ciGroup2': getOssCiGroupWorker(2),
              'oss-ciGroup3': getOssCiGroupWorker(3),
              'oss-ciGroup4': getOssCiGroupWorker(4),
              'oss-ciGroup5': getOssCiGroupWorker(5),
              'oss-ciGroup6': getOssCiGroupWorker(6),
              'oss-ciGroup7': getOssCiGroupWorker(7),
              'oss-ciGroup8': getOssCiGroupWorker(8),
              'oss-ciGroup9': getOssCiGroupWorker(9),
              'oss-ciGroup10': getOssCiGroupWorker(10),
              'oss-ciGroup11': getOssCiGroupWorker(11),
              'oss-ciGroup12': getOssCiGroupWorker(12),
              'oss-firefoxSmoke': getPostBuildWorker('firefoxSmoke', { runbld './test/scripts/jenkins_firefox_smoke.sh' }),
              // 'oss-visualRegression': getPostBuildWorker('visualRegression', { runbld './test/scripts/jenkins_visual_regression.sh' }),
            ]),
            'kibana-oss-agent-3': withWorkers('kibana-oss-tests-3', { buildOss() }, [
              'oss-ciGroup1': getOssCiGroupWorker(1),
              'oss-ciGroup2': getOssCiGroupWorker(2),
              'oss-ciGroup3': getOssCiGroupWorker(3),
              'oss-ciGroup4': getOssCiGroupWorker(4),
              'oss-ciGroup5': getOssCiGroupWorker(5),
              'oss-ciGroup6': getOssCiGroupWorker(6),
              'oss-ciGroup7': getOssCiGroupWorker(7),
              'oss-ciGroup8': getOssCiGroupWorker(8),
              'oss-ciGroup9': getOssCiGroupWorker(9),
              'oss-ciGroup10': getOssCiGroupWorker(10),
              'oss-ciGroup11': getOssCiGroupWorker(11),
              'oss-ciGroup12': getOssCiGroupWorker(12),
              'oss-firefoxSmoke': getPostBuildWorker('firefoxSmoke', { runbld './test/scripts/jenkins_firefox_smoke.sh' }),
              // 'oss-visualRegression': getPostBuildWorker('visualRegression', { runbld './test/scripts/jenkins_visual_regression.sh' }),
            ]),
            'kibana-oss-agent-4': withWorkers('kibana-oss-tests-4', { buildOss() }, [
              'oss-ciGroup1': getOssCiGroupWorker(1),
              'oss-ciGroup2': getOssCiGroupWorker(2),
              'oss-ciGroup3': getOssCiGroupWorker(3),
              'oss-ciGroup4': getOssCiGroupWorker(4),
              'oss-ciGroup5': getOssCiGroupWorker(5),
              'oss-ciGroup6': getOssCiGroupWorker(6),
              'oss-ciGroup7': getOssCiGroupWorker(7),
              'oss-ciGroup8': getOssCiGroupWorker(8),
              'oss-ciGroup9': getOssCiGroupWorker(9),
              'oss-ciGroup10': getOssCiGroupWorker(10),
              'oss-ciGroup11': getOssCiGroupWorker(11),
              'oss-ciGroup12': getOssCiGroupWorker(12),
              'oss-firefoxSmoke': getPostBuildWorker('firefoxSmoke', { runbld './test/scripts/jenkins_firefox_smoke.sh' }),
              // 'oss-visualRegression': getPostBuildWorker('visualRegression', { runbld './test/scripts/jenkins_visual_regression.sh' }),
            ]),
            'kibana-oss-agent-5': withWorkers('kibana-oss-tests-5', { buildOss() }, [
              'oss-ciGroup1': getOssCiGroupWorker(1),
              'oss-ciGroup2': getOssCiGroupWorker(2),
              'oss-ciGroup3': getOssCiGroupWorker(3),
              'oss-ciGroup4': getOssCiGroupWorker(4),
              'oss-ciGroup5': getOssCiGroupWorker(5),
              'oss-ciGroup6': getOssCiGroupWorker(6),
              'oss-ciGroup7': getOssCiGroupWorker(7),
              'oss-ciGroup8': getOssCiGroupWorker(8),
              'oss-ciGroup9': getOssCiGroupWorker(9),
              'oss-ciGroup10': getOssCiGroupWorker(10),
              'oss-ciGroup11': getOssCiGroupWorker(11),
              'oss-ciGroup12': getOssCiGroupWorker(12),
              'oss-firefoxSmoke': getPostBuildWorker('firefoxSmoke', { runbld './test/scripts/jenkins_firefox_smoke.sh' }),
              // 'oss-visualRegression': getPostBuildWorker('visualRegression', { runbld './test/scripts/jenkins_visual_regression.sh' }),
            ]),
            // 'kibana-xpack-agent': withWorkers('kibana-xpack-tests', { buildXpack() }, [
            //   'xpack-ciGroup1': getXpackCiGroupWorker(1),
            //   'xpack-ciGroup2': getXpackCiGroupWorker(2),
            //   'xpack-ciGroup3': getXpackCiGroupWorker(3),
            //   'xpack-ciGroup4': getXpackCiGroupWorker(4),
            //   'xpack-ciGroup5': getXpackCiGroupWorker(5),
            //   'xpack-ciGroup6': getXpackCiGroupWorker(6),
            //   'xpack-ciGroup7': getXpackCiGroupWorker(7),
            //   'xpack-ciGroup8': getXpackCiGroupWorker(8),
            //   'xpack-ciGroup9': getXpackCiGroupWorker(9),
            //   'xpack-ciGroup10': getXpackCiGroupWorker(10),
            //   'xpack-firefoxSmoke': getPostBuildWorker('xpack-firefoxSmoke', { runbld './test/scripts/jenkins_xpack_firefox_smoke.sh' }),
            //   // 'xpack-visualRegression': getPostBuildWorker('xpack-visualRegression', { runbld './test/scripts/jenkins_xpack_visual_regression.sh' }),
            // ]),
          ])
        }
      }
    }
  }
}

def withWorkers(name, preWorkerClosure = {}, workerClosures = [:]) {
  return {
    jobRunner(agentLabel) {
      try {
        isBeforeStart = false
        doSetup()
        preWorkerClosure()

        def nextWorker = 1
        def worker = { workerClosure ->
          def workerNumber = nextWorker
          nextWorker++

          return {
            workerClosure(workerNumber)
          }
        }

        def workers = [:]
        workerClosures.each { workerName, workerClosure ->
          workers[workerName] = worker(workerClosure)
        }

        parallel(workers)
      } finally {
        catchError {
          // uploadAllGcsArtifacts(name)
        }

        catchError {
          runbldJunit()
        }

        catchError {
          publishJunit()
        }

        catchError {
          // runErrorReporter()
        }
      }
    }
  }
}

def getPostBuildWorker(name, closure) {
  return { workerNumber ->
    def kibanaPort = "61${workerNumber}1"
    def esPort = "61${workerNumber}2"
    def esTransportPort = "61${workerNumber}3"

    withEnv([
      "CI_WORKER_NUMBER=${workerNumber}",
      "TEST_KIBANA_HOST=localhost",
      "TEST_KIBANA_PORT=${kibanaPort}",
      "TEST_KIBANA_URL=http://elastic:changeme@localhost:${kibanaPort}",
      "TEST_ES_URL=http://elastic:changeme@localhost:${esPort}",
      "TEST_ES_TRANSPORT_PORT=${esTransportPort}",
      "IS_PIPELINE_JOB=1",
    ]) {
      closure()
    }
  }
}

def getOssCiGroupWorker(ciGroup) {
  return getPostBuildWorker("ciGroup" + ciGroup, {
    withEnv([
      "CI_GROUP=${ciGroup}",
      "JOB=kibana-ciGroup${ciGroup}",
    ]) {
      //withDockerImage('--cpu-shares 68') {
      withDockerImage {
        runbld "./test/scripts/jenkins_ci_group.sh"
      }
    }
  })
}

def getXpackCiGroupWorker(ciGroup) {
  return getPostBuildWorker("xpack-ciGroup" + ciGroup, {
    withEnv([
      "CI_GROUP=${ciGroup}",
      "JOB=xpack-kibana-ciGroup${ciGroup}",
    ]) {
      //withDockerImage('--cpu-shares 68') {
      withDockerImage {
        runbld "./test/scripts/jenkins_xpack_ci_group.sh"
      }
    }
  })
}

def legacyJobRunner(name) {
  return {
    parallel([
      "${name}": {
        withEnv([
          "JOB=${name}",
        ]) {
          jobRunner('linux && immutable') {
            try {
              runbld('.ci/run.sh', true)
            } finally {
              catchError {
                uploadAllGcsArtifacts(name)
              }
              catchError {
                publishJunit()
              }
              catchError {
                runErrorReporter()
              }
            }
          }
        }
      }
    ])
  }
}

def jobRunner(label, closure) {
  node(label) {

    try {
      if (!isBeforeStart) {
        sh 'mkdir -p /dev/shm/workspace'
        sh "sh -c 'rm -rf ${WORKSPACE}; ln -s /dev/shm/workspace ${WORKSPACE}'"
      }

      // sh 'ls -alh /dev/shm/workspace'
      // sh 'chmod 777 /dev/shm/workspace'
    } finally {}

    def scmVars = checkout scm

    withEnv([
      "CI=true",
      "HOME=${env.JENKINS_HOME}",
      "PR_SOURCE_BRANCH=${env.ghprbSourceBranch ?: ''}",
      "PR_TARGET_BRANCH=${env.ghprbTargetBranch ?: ''}",
      "PR_AUTHOR=${env.ghprbPullAuthorLogin ?: ''}",
      "TEST_BROWSER_HEADLESS=1",
      "GIT_BRANCH=${scmVars.GIT_BRANCH}",
    ]) {
      withCredentials([
        string(credentialsId: 'vault-addr', variable: 'VAULT_ADDR'),
        string(credentialsId: 'vault-role-id', variable: 'VAULT_ROLE_ID'),
        string(credentialsId: 'vault-secret-id', variable: 'VAULT_SECRET_ID'),
      ]) {
        // scm is configured to check out to the ./kibana directory
        dir('kibana') {
          dockerBuild()
          closure()
        }
      }
    }
  }
}

// TODO what should happen if GCS, Junit, or email publishing fails? Unstable build? Failed build?

def uploadGcsArtifact(workerName, pattern) {
  def storageLocation = "gs://kibana-ci-artifacts/jobs/${env.JOB_NAME}/${BUILD_NUMBER}/${workerName}" // TODO
  // def storageLocation = "gs://kibana-pipeline-testing/jobs/pipeline-test/${BUILD_NUMBER}/${workerName}"

  googleStorageUpload(
    credentialsId: 'kibana-ci-gcs-plugin',
    bucket: storageLocation,
    pattern: pattern,
    sharedPublicly: true,
    showInline: true,
  )
}

def uploadAllGcsArtifacts(workerName) {
  def ARTIFACT_PATTERNS = [
    'target/kibana-*',
    'target/junit/**/*',
    'test/**/screenshots/**/*.png',
    'test/functional/failure_debug/html/*.html',
    'x-pack/test/**/screenshots/**/*.png',
    'x-pack/test/functional/failure_debug/html/*.html',
    'x-pack/test/functional/apps/reporting/reports/session/*.pdf',
  ]

  ARTIFACT_PATTERNS.each { pattern ->
    uploadGcsArtifact(workerName, pattern)
  }
}

def publishJunit() {
  junit(testResults: 'target/junit/**/*.xml', allowEmptyResults: true, keepLongStdio: true)
}

def sendMail() {
  sendInfraMail()
  sendKibanaMail()
}

def sendInfraMail() {
  catchError {
    step([
      $class: 'Mailer',
      notifyEveryUnstableBuild: true,
      recipients: 'infra-root+build@elastic.co',
      sendToIndividuals: false
    ])
  }
}

def sendKibanaMail() {
  catchError {
    def buildStatus = buildUtils.getBuildStatus()

    if(params.NOTIFY_ON_FAILURE && buildStatus != 'SUCCESS' && buildStatus != 'ABORTED') {
      emailext(
        to: 'build-kibana@elastic.co',
        subject: "${env.JOB_NAME} - Build # ${env.BUILD_NUMBER} - ${buildStatus}",
        body: '${SCRIPT,template="groovy-html.template"}',
        mimeType: 'text/html',
      )
    }
  }
}

def dockerBuild() {
  if (isBeforeStart) { return }

  return docker.build('kibana-ci', '-f .ci/Dockerfile .')
}

def withDockerImage(args, closure) {
  // if (isBeforeStart) { return }

  // todo mount /home/kibana/.kibana/ and /home/kibana/.es?
  args += " -v '${env.JENKINS_HOME}:${env.JENKINS_HOME}' -v '/dev/shm/workspace:/dev/shm/workspace' --shm-size 2GB"
  docker.image('kibana-ci').inside(args) {

      // sh "ls -alh ${env.WORKSPACE}"
      // sh 'ls -alh /dev/shm'
      // sh 'ls -alh /dev/shm/workspace || true'
      // sh "ln -s '${env.WORKSPACE}' /dev/shm/workspace"
      closure()

  }
}

def withDockerImage(closure) {
  withDockerImage('', closure)
}

def runbld(script, enableJunitProcessing = false) {
  if (isBeforeStart) { return }

  return bash(script)

  def extraConfig = enableJunitProcessing ? "" : "--config ${env.WORKSPACE}/kibana/.ci/runbld_no_junit.yml"

  sh "/usr/local/bin/runbld -d '${pwd()}' ${extraConfig} ${script}"
}

def runbldJunit() {
  sh "/usr/local/bin/runbld -d '${pwd()}' ${env.WORKSPACE}/kibana/test/scripts/jenkins_runbld_junit.sh"
}

def bash(script) {
  if (isBeforeStart) { return }

  sh "#!/bin/bash\n${script}"
}

def doSetup() {
  if (isBeforeStart) { return }

  withDockerImage {
    runbld "./test/scripts/jenkins_setup.sh"
  }
}

def buildOss() {
  if (isBeforeStart) { return }

  withDockerImage {
    runbld "./test/scripts/jenkins_build_kibana.sh"
  }
}

def buildXpack() {
  if (isBeforeStart) { return }

  withDockerImage {
    runbld "./test/scripts/jenkins_xpack_build_kibana.sh"
  }
}

def runErrorReporter() {
  bash """
    source src/dev/ci_setup/setup_env.sh
    node src/dev/failed_tests/cli
  """
}

def withDebug(closure) {
  try {
    closure()
  } finally {
    while(true) {
      def script = input message: 'Script?', parameters: [text(defaultValue: '', description: '', name: 'script')]
      try {
        sh script
      } catch (ex) {
        print ex
      }
    }
  }
}

def withStart(closure) {
  isBeforeStart = false
  withDebug {
    closure()
  }
}
