def call(List<Closure> closures) {
  withTaskQueue.addTasks(closures)
}

def intake(Map params = [:]) {
  def config = params ?: [test: true, check: true, lint: true]

  if (config.check) {
    intakeCheck()
  }

  if (config.lint) {
    intakeLint()
  }

  if (config.test) {
    intakeTest()
  }
}

def intakeCheck() {
  tasks([
    kibanaPipeline.intakeTask('run:checkTsProjects', 'yarn run grunt run:checkTsProjects'),
    kibanaPipeline.intakeTask('run:checkDocApiChanges', 'yarn run grunt run:checkDocApiChanges'),
    kibanaPipeline.intakeTask('run:typeCheck', 'yarn run grunt run:typeCheck'),
    kibanaPipeline.intakeTask('run:i18nCheck', 'yarn run grunt run:i18nCheck'),
    kibanaPipeline.intakeTask('run:checkFileCasing', 'yarn run grunt run:checkFileCasing'),
    kibanaPipeline.intakeTask('run:checkLockfileSymlinks', 'yarn run grunt run:checkLockfileSymlinks'),
    kibanaPipeline.intakeTask('run:licenses', 'yarn run grunt run:licenses'),
    kibanaPipeline.intakeTask('run:verifyDependencyVersions', 'yarn run grunt run:verifyDependencyVersions'),
    kibanaPipeline.intakeTask('run:verifyNotice', 'yarn run grunt run:verifyNotice'),
    kibanaPipeline.intakeTask('run:test_projects', 'yarn run grunt run:test_projects'),
    kibanaPipeline.intakeTask('run:test_hardening', 'yarn run grunt run:test_hardening'),
  ])
}

def intakeLint() {
  tasks([
    kibanaPipeline.intakeTask('run:eslint', 'yarn run grunt run:eslint'),
    kibanaPipeline.intakeTask('run:sasslint', 'yarn run grunt run:sasslint'),
  ])
}

def intakeTest() {
  tasks([
    // These 4 tasks require isolation because of hard-coded, conflicting ports and such, so let's use Docker here
    kibanaPipeline.intakeTaskDocker('run:test_jest_integration', 'yarn run grunt run:test_jest_integration'),
    kibanaPipeline.intakeTaskDocker('run:mocha', 'yarn run grunt run:mocha'),
    kibanaPipeline.intakeTaskDocker('run:test_karma_ci', 'yarn run grunt run:test_karma_ci'),
    kibanaPipeline.intakeTaskDocker('X-Pack Karma', 'cd x-pack; checks-reporter-with-killswitch "X-Pack Karma Tests" yarn test:karma'),

    kibanaPipeline.intakeTask('run:test_jest', 'yarn run grunt run:test_jest'),
    kibanaPipeline.intakeTask('run:apiIntegrationTests', 'yarn run grunt run:apiIntegrationTests'),
    kibanaPipeline.intakeTask('X-Pack SIEM cyclic dependency', 'cd x-pack; checks-reporter-with-killswitch "X-Pack SIEM cyclic dependency test" node plugins/security_solution/scripts/check_circular_deps'),
    kibanaPipeline.intakeTask('X-Pack List cyclic dependency', 'cd x-pack; checks-reporter-with-killswitch "X-Pack List cyclic dependency test" node plugins/lists/scripts/check_circular_deps'),
    kibanaPipeline.intakeTask('X-Pack Jest', 'cd x-pack; checks-reporter-with-killswitch "X-Pack Jest" node --max-old-space-size=6144 scripts/jest --ci --verbose --maxWorkers=10'),
  ])
}

def functionalOss(Map params = [:]) {
  def config = params ?: [ciGroups: true, firefox: true, accessibility: true, pluginFunctional: true, visualRegression: false]

  task {
    kibanaPipeline.buildOss()

    if (config.ciGroups) {
      def ciGroups = 1..12
      tasks(ciGroups.collect { kibanaPipeline.ossCiGroupProcess(it) })
    }

    if (config.firefox) {
      task(kibanaPipeline.functionalTestProcess('oss-firefox', './test/scripts/jenkins_firefox_smoke.sh'))
    }

    if (config.accessibility) {
      task(kibanaPipeline.functionalTestProcess('oss-accessibility', './test/scripts/jenkins_accessibility.sh'))
    }

    if (config.pluginFunctional) {
      task(kibanaPipeline.functionalTestProcess('oss-pluginFunctional', './test/scripts/jenkins_plugin_functional.sh'))
    }

    if (config.visualRegression) {
      task(kibanaPipeline.functionalTestProcess('oss-visualRegression', './test/scripts/jenkins_visual_regression.sh'))
    }
  }
}

def functionalXpack(Map params = [:]) {
  def config = params ?: [ciGroups: true, firefox: true, accessibility: true, pluginFunctional: true, visualRegression: false]

  task {
    kibanaPipeline.buildXpack()

    if (config.ciGroups) {
      def ciGroups = 1..14
      tasks(ciGroups.collect { kibanaPipeline.xpackCiGroupProcess(it) })
    }

    if (config.firefox) {
      task(kibanaPipeline.functionalTestProcess('xpack-firefox', './test/scripts/jenkins_xpack_firefox_smoke.sh'))
    }

    if (config.accessibility) {
      task(kibanaPipeline.functionalTestProcess('xpack-accessibility', './test/scripts/jenkins_xpack_accessibility.sh'))
    }

    if (config.visualRegression) {
      task(kibanaPipeline.functionalTestProcess('xpack-visualRegression', './test/scripts/jenkins_xpack_visual_regression.sh'))
    }

    whenChanged(['x-pack/plugins/security_solution/', 'x-pack/test/security_solution_cypress/']) {
      task(kibanaPipeline.functionalTestProcess('xpack-securitySolutionCypress', './test/scripts/jenkins_security_solution_cypress.sh'))
    }
  }
}

return this
