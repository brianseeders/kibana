#!/bin/groovy

properties([durabilityHint('PERFORMANCE_OPTIMIZED')])

timeout(time: 180, unit: 'MINUTES') {
  timestamps {
    ansiColor('xterm') {
      parallel([
        'kibana-intake': jobRunner('kibana-intake'),
        'x-pack-intake': jobRunner('x-pack-intake'),
        'kibana-firefoxSmoke': jobRunner('kibana-firefoxSmoke'),
        'kibana-ciGroup1': jobRunner('kibana-ciGroup1'),
        'kibana-ciGroup2': jobRunner('kibana-ciGroup2'),
        'kibana-ciGroup3': jobRunner('kibana-ciGroup3'),
        'kibana-ciGroup4': jobRunner('kibana-ciGroup4'),
        'kibana-ciGroup5': jobRunner('kibana-ciGroup5'),
        'kibana-ciGroup6': jobRunner('kibana-ciGroup6'),
        'kibana-ciGroup7': jobRunner('kibana-ciGroup7'),
        'kibana-ciGroup8': jobRunner('kibana-ciGroup8'),
        'kibana-ciGroup9': jobRunner('kibana-ciGroup9'),
        'kibana-ciGroup10': jobRunner('kibana-ciGroup10'),
        'kibana-ciGroup11': jobRunner('kibana-ciGroup11'),
        'kibana-ciGroup12': jobRunner('kibana-ciGroup12'),
        'kibana-visualRegression': jobRunner('kibana-visualRegression'),

        // make sure all x-pack-ciGroups are listed in test/scripts/jenkins_xpack_ci_group.sh
        'x-pack-firefoxSmoke': jobRunner('x-pack-firefoxSmoke'),
        'x-pack-ciGroup1': jobRunner('x-pack-ciGroup1'),
        'x-pack-ciGroup2': jobRunner('x-pack-ciGroup2'),
        'x-pack-ciGroup3': jobRunner('x-pack-ciGroup3'),
        'x-pack-ciGroup4': jobRunner('x-pack-ciGroup4'),
        'x-pack-ciGroup5': jobRunner('x-pack-ciGroup5'),
        'x-pack-ciGroup6': jobRunner('x-pack-ciGroup6'),
        'x-pack-ciGroup7': jobRunner('x-pack-ciGroup7'),
        'x-pack-ciGroup8': jobRunner('x-pack-ciGroup8'),
        'x-pack-ciGroup9': jobRunner('x-pack-ciGroup9'),
        'x-pack-ciGroup10': jobRunner('x-pack-ciGroup10'),
        'x-pack-visualRegression': jobRunner('x-pack-visualRegression'),
      ])
    }
  }
}

def withWorker(name, closure) {
  stage("Worker: ${name}") {
    node('linux && immutable') {
      closure()
    }
  }
}

def jobRunner(name) {
  return {
    withEnv([
      "JOB=${name}",
      "CI=true",
      "HOME=${env.JENKINS_HOME}",
    ]) {
      withWorker(name) {
        stage('Checkout') {
          checkout scm
        }

        dir('kibana') {
          stage('Run CI') {
            runbld('.ci/run.sh')
          }
        }
      }
    }
  }
}

def runbld(script) {
  sh '#!/usr/local/bin/runbld\n' + script
}

def bash(script) {
  sh "#!/bin/bash -x\n${script}"
}

