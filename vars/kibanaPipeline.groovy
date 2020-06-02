def withPostBuildReporting(Closure closure) {
  try {
    closure()
  } finally {
    catchErrors {
      runErrorReporter()
    }

    catchErrors {
      runbld.junit()
    }

    catchErrors {
      publishJunit()
    }
  }
}

def withFunctionalTestEnv(List additionalEnvs = [], Closure closure) {
  // This can go away once everything that uses the deprecated workers.parallelProcesses() is moved to task queue
  def parallelId = env.TASK_QUEUE_PROCESS_ID ?: env.CI_PARALLEL_PROCESS_NUMBER

  def kibanaPort = "61${parallelId}1"
  def esPort = "61${parallelId}2"
  def esTransportPort = "61${parallelId}3"

  withEnv([
    "CI_GROUP=${parallelId}",
    "REMOVE_KIBANA_INSTALL_DIR=1",
    "CI_PARALLEL_PROCESS_NUMBER=${parallelId}",
    "TEST_KIBANA_HOST=localhost",
    "TEST_KIBANA_PORT=${kibanaPort}",
    "TEST_KIBANA_URL=http://elastic:changeme@localhost:${kibanaPort}",
    "TEST_ES_URL=http://elastic:changeme@localhost:${esPort}",
    "TEST_ES_TRANSPORT_PORT=${esTransportPort}",
    "KBN_NP_PLUGINS_BUILT=true",
  ] + additionalEnvs) {
    closure()
  }
}

def functionalTestProcess(String name, Closure closure) {
  return {
    withFunctionalTestEnv(["JOB=${name}"], closure)
  }
}

def functionalTestProcess(String name, String script) {
  return functionalTestProcess(name) {
    retryable(name) {
      bash(script, "Execute ${name}")
    }
  }
}

def ossCiGroupProcess(ciGroup) {
  return functionalTestProcess("ciGroup" + ciGroup) {
    withEnv([
      "CI_GROUP=${ciGroup}",
      "JOB=kibana-ciGroup${ciGroup}",
    ]) {
      retryable("kibana-ciGroup${ciGroup}") {
        bash("./test/scripts/jenkins_ci_group.sh", "Execute kibana-ciGroup${ciGroup}")
      }
    }
  }
}

def xpackCiGroupProcess(ciGroup) {
  return functionalTestProcess("xpack-ciGroup" + ciGroup) {
    withEnv([
      "CI_GROUP=${ciGroup}",
      "JOB=xpack-kibana-ciGroup${ciGroup}",
    ]) {
      retryable("xpack-kibana-ciGroup${ciGroup}") {
        bash("./test/scripts/jenkins_xpack_ci_group.sh", "Execute xpack-kibana-ciGroup${ciGroup}")
      }
    }
  }
}

def uploadGcsArtifact(uploadPrefix, pattern) {
  googleStorageUpload(
    credentialsId: 'kibana-ci-gcs-plugin',
    bucket: "gs://${uploadPrefix}",
    pattern: pattern,
    sharedPublicly: true,
    showInline: true,
  )
}

def downloadCoverageArtifacts() {
  def storageLocation = "gs://kibana-pipeline-testing/jobs/${env.JOB_NAME}/${BUILD_NUMBER}/coverage/"
  def targetLocation = "/tmp/downloaded_coverage"

  sh "mkdir -p '${targetLocation}' && gsutil -m cp -r '${storageLocation}' '${targetLocation}'"
}

def uploadCoverageArtifacts(prefix, pattern) {
  def uploadPrefix = "kibana-pipeline-testing/jobs/${env.JOB_NAME}/${BUILD_NUMBER}/coverage/${prefix}"
  uploadGcsArtifact(uploadPrefix, pattern)
}

def withGcsArtifactUpload(workerName, closure) {
  def uploadPrefix = "kibana-pipeline-testing/jobs/${env.JOB_NAME}/${BUILD_NUMBER}/${workerName}"
  def ARTIFACT_PATTERNS = [
    'target/kibana-*',
    'target/test-metrics/*',
    'target/kibana-siem/**/*.png',
    'target/junit/**/*',
    'target/test-suites-ci-plan.json',
    'test/**/screenshots/session/*.png',
    'test/**/screenshots/failure/*.png',
    'test/**/screenshots/diff/*.png',
    'test/functional/failure_debug/html/*.html',
    'x-pack/test/**/screenshots/session/*.png',
    'x-pack/test/**/screenshots/failure/*.png',
    'x-pack/test/**/screenshots/diff/*.png',
    'x-pack/test/functional/failure_debug/html/*.html',
    'x-pack/test/functional/apps/reporting/reports/session/*.pdf',
  ]

  withEnv([
    "GCS_UPLOAD_PREFIX=${uploadPrefix}"
  ], {
    try {
      closure()
    } finally {
      catchErrors {
        ARTIFACT_PATTERNS.each { pattern ->
          uploadGcsArtifact(uploadPrefix, pattern)
        }

        dir(env.WORKSPACE) {
          ARTIFACT_PATTERNS.each { pattern ->
            uploadGcsArtifact(uploadPrefix, "parallel/*/kibana/${pattern}")
          }
        }
      }
    }
  })

  if (env.CODE_COVERAGE) {
    sh 'tar -czf kibana-coverage.tar.gz target/kibana-coverage/**/*'
    uploadGcsArtifact("kibana-pipeline-testing/jobs/${env.JOB_NAME}/${BUILD_NUMBER}/coverage/${workerName}", 'kibana-coverage.tar.gz')
  }
}

def publishJunit() {
  junit(testResults: 'target/junit/**/*.xml', allowEmptyResults: true, keepLongStdio: true)

  // junit() is weird about paths for security reasons, so we need to actually change to an upper directory first
  dir(env.WORKSPACE) {
    junit(testResults: 'parallel/*/kibana/target/junit/**/*.xml', allowEmptyResults: true, keepLongStdio: true)
  }
}

def sendMail() {
  // If the build doesn't have a result set by this point, there haven't been any errors and it can be marked as a success
  // The e-mail plugin for the infra e-mail depends upon this being set
  currentBuild.result = currentBuild.result ?: 'SUCCESS'

  def buildStatus = buildUtils.getBuildStatus()
  if (buildStatus != 'SUCCESS' && buildStatus != 'ABORTED') {
    node('flyweight') {
      sendInfraMail()
      sendKibanaMail()
    }
  }
}

def sendInfraMail() {
  catchErrors {
    step([
      $class: 'Mailer',
      notifyEveryUnstableBuild: true,
      recipients: 'infra-root+build@elastic.co',
      sendToIndividuals: false
    ])
  }
}

def sendKibanaMail() {
  catchErrors {
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

def bash(script, label) {
  sh(
    script: "#!/bin/bash\n${script}",
    label: label
  )
}

def doSetup() {
  retryWithDelay(2, 15) {
    try {
      bash("./test/scripts/jenkins_setup.sh", "Setup Build Environment and Dependencies")
    } catch (ex) {
      try {
        // Setup expects this directory to be missing, so we need to remove it before we do a retry
        bash("rm -rf ../elasticsearch", "Remove elasticsearch sibling directory, if it exists")
      } finally {
        throw ex
      }
    }
  }
}

def buildOss() {
  withEnv(['KBN_OPTIMIZER_MAX_WORKERS=24']) {
    bash("./test/scripts/jenkins_build_kibana.sh", "Build OSS/Default Kibana")
  }
}

def buildXpack() {
  withEnv(['KBN_OPTIMIZER_MAX_WORKERS=24']) {
    bash("./test/scripts/jenkins_xpack_build_kibana.sh", "Build X-Pack Kibana")
  }
}

def runErrorReporter() {
  def status = buildUtils.getBuildStatus()
  def dryRun = status != "ABORTED" ? "" : "--no-github-update"

  bash(
    """
      source src/dev/ci_setup/setup_env.sh
      node scripts/report_failed_tests ${dryRun}
    """,
    "Report failed tests, if necessary"
  )
}

def call(Map params = [:], Closure closure) {
  def config = [timeoutMinutes: 135, checkPrChanges: false] + params

  stage("Kibana Pipeline") {
    timeout(time: config.timeoutMinutes, unit: 'MINUTES') {
      timestamps {
        ansiColor('xterm') {
          if (config.checkPrChanges && githubPr.isPr()) {
            print "Checking PR for changes to determine if CI needs to be run..."

            if (prChanges.areChangesSkippable()) {
              print "No changes requiring CI found in PR, skipping."
              return
            }
          }
          closure()
        }
      }
    }
  }
}

def withFunctionalTaskQueue(Map options = [:], Closure closure) {
  def setupClosure = {
    bash("${env.WORKSPACE}/kibana/test/scripts/jenkins_setup_parallel_workspace.sh", "Set up duplicate workspace for parallel process")
  }

  def config = [parallel: 24, setup: setupClosure] + options

  withTaskQueue(config) {
    closure.call()
  }
}

def intakeTask(description, script) {
  return {
    withFunctionalTestEnv {
      bash("source test/scripts/jenkins_test_setup.sh; ${script}", description)
    }
  }
}

def intakeTaskDocker(description, script) {
  return {
    docker
      .image('kibana-ci')
      .inside(
        " -v '${env.JENKINS_HOME}:${env.JENKINS_HOME}' -v '/dev/shm/workspace:/dev/shm/workspace' --shm-size 2GB --cpus 4",
        intakeTask(description, script)
      )
  }
}

def buildDocker() {
  sh(script: 'docker build -t kibana-ci -f ./.ci/Dockerfile .', label: 'Build CI Docker image')
}

def buildOssPlugins() {
  bash('./test/scripts/jenkins_build_plugins.sh', 'Build OSS Plugins')
}

def buildXpackPlugins() {
  bash('./test/scripts/jenkins_xpack_build_plugins.sh', 'Build X-Pack Plugins')
}

def intakeTasks() {
  intakeOtherTasks()
  intakeTestTasks()
}

def intakeOtherTasks() {
  tasks([
    intakeTask('run:eslint', 'yarn run grunt run:eslint'),
    intakeTask('run:sasslint', 'yarn run grunt run:sasslint'),
    intakeTask('run:checkTsProjects', 'yarn run grunt run:checkTsProjects'),
    intakeTask('run:checkDocApiChanges', 'yarn run grunt run:checkDocApiChanges'),
    intakeTask('run:typeCheck', 'yarn run grunt run:typeCheck'),
    intakeTask('run:i18nCheck', 'yarn run grunt run:i18nCheck'),
    intakeTask('run:checkFileCasing', 'yarn run grunt run:checkFileCasing'),
    intakeTask('run:checkLockfileSymlinks', 'yarn run grunt run:checkLockfileSymlinks'),
    intakeTask('run:licenses', 'yarn run grunt run:licenses'),
    intakeTask('run:verifyDependencyVersions', 'yarn run grunt run:verifyDependencyVersions'),
    intakeTask('run:verifyNotice', 'yarn run grunt run:verifyNotice'),
    intakeTask('run:test_projects', 'yarn run grunt run:test_projects'),
    intakeTask('run:test_hardening', 'yarn run grunt run:test_hardening'),
  ])
}

def intakeTestTasks() {
  tasks([
    // These 4 tasks require isolation because of hard-coded, conflicting ports and such, so let's use Docker here
    intakeTaskDocker('run:test_jest_integration', 'yarn run grunt run:test_jest_integration'),
    intakeTaskDocker('run:mocha', 'yarn run grunt run:mocha'),
    intakeTaskDocker('run:test_karma_ci', 'yarn run grunt run:test_karma_ci'),
    intakeTaskDocker('X-Pack Karma', 'cd x-pack; checks-reporter-with-killswitch "X-Pack Karma Tests" yarn test:karma'),

    intakeTask('run:test_jest', 'yarn run grunt run:test_jest'),
    intakeTask('run:apiIntegrationTests', 'yarn run grunt run:apiIntegrationTests'),
    intakeTask('X-Pack SIEM cyclic dependency', 'cd x-pack; checks-reporter-with-killswitch "X-Pack SIEM cyclic dependency test" node plugins/siem/scripts/check_circular_deps'),
    intakeTask('X-Pack Jest', 'cd x-pack; checks-reporter-with-killswitch "X-Pack Jest" node --max-old-space-size=6144 scripts/jest --ci --verbose --maxWorkers=10'),
  ])
}

def ossTasks() {
  task {
    buildOss()

    def ciGroups = 1..12
    tasks(ciGroups.collect { ossCiGroupProcess(it) })

    tasks([
      functionalTestProcess('oss-firefox', './test/scripts/jenkins_firefox_smoke.sh'),
      functionalTestProcess('oss-accessibility', './test/scripts/jenkins_accessibility.sh'),
      functionalTestProcess('oss-pluginFunctional', './test/scripts/jenkins_plugin_functional.sh')
      // functionalTestProcess('oss-visualRegression', './test/scripts/jenkins_visual_regression.sh'),
    ])

    // Does this stuff require running out of the same workspace that the build happened in?
    functionalTestProcess('oss-pluginFunctional', './test/scripts/jenkins_plugin_functional.sh')()
  }
}

def xpackTests() {
  task {
    buildXpack()

    def ciGroups = 1..14
    tasks(ciGroups.collect { xpackCiGroupProcess(it) })

    tasks([
      functionalTestProcess('xpack-firefox', './test/scripts/jenkins_xpack_firefox_smoke.sh'),
      functionalTestProcess('xpack-accessibility', './test/scripts/jenkins_xpack_accessibility.sh'),
      // functionalTestProcess('xpack-visualRegression', './test/scripts/jenkins_xpack_visual_regression.sh'),
    ])

    whenChanged(['x-pack/plugins/siem/', 'x-pack/legacy/plugins/siem/', 'x-pack/test/siem_cypress/']) {
      task(functionalTestProcess('xpack-siemCypress', './test/scripts/jenkins_siem_cypress.sh'))
    }
  }
}

def withTasks(Map params = [worker: [:]], Closure closure) {
  catchErrors {
    def config = [name: 'ci-worker', size: 'xxl', ramDisk: true] + (params.worker ?: [:])

    workers.ci(config) {
      withFunctionalTaskQueue(parallel: 24) {
        parallel([
          docker: { buildDocker() },

          // There are integration tests etc that require the plugins to be built first, so let's go ahead and build them before set up the parallel workspaces
          ossPlugins: { buildOssPlugins() },
          xpackPlugins: { buildXpackPlugins() },
        ])

        catchErrors {
          closure()
        }
      }
    }
  }
}

def allCiTasks() {
  withTasks {
    intakeTasks()
    ossTasks()
    xpackTests()
  }
}

return this
