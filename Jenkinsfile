#!/bin/groovy
node {
  def REMOTE_TAG = 'gcr.io/elastic-kibana-184716/brianseeders/kibana-dind:latest'
  def GIT_COMMIT = ''
  def SCM_VARS = ''

  sh 'env | sort'

  def CACHE_REMOTE_TAG = "gcr.io/elastic-kibana-184716/brianseeders/kibana-dind:cache"
  def CACHE_IMAGES_TO_TRY = [
    CACHE_REMOTE_TAG,
  ]

  def BASE_IMAGE_FOR_CACHE = 'base'
  def CACHE_IMAGE = ''

  parallel([
    withBuild: {
      stage('Launch k8s docker agent') {
        withDocker {
          parallel([
            image_pull: {
              stage('Pull cache image') {
                for(image in CACHE_IMAGES_TO_TRY) {
                  try {
                    // TODO add multiple cache image attempts for branch, tag, master
                    sh "docker pull '${image}'"
                    // CACHE_IMAGE = image
                    // BASE_IMAGE_FOR_CACHE = image
                  } catch(ex) {}
                }
              }
            },
            checkout: {
              SCM_VARS = checkout scm
              GIT_COMMIT = SCM_VARS.GIT_COMMIT
              print SCM_VARS
            }
          ])

          def baseImage = "kibana-base:${GIT_COMMIT}"

          stage('Build base image') {
            sh "docker build --cache-from='${CACHE_REMOTE_TAG}' --target final --build-arg 'CACHE_IMAGE=${CACHE_REMOTE_TAG}' -t '${REMOTE_TAG}' ."
          }

          stage('Push base image') {
            sh "docker push '${REMOTE_TAG}'"
          }
        }
      }
      parallel([
        buildOss: {
          withBase(REMOTE_TAG) {
            stage('Build OSS') {
              sh 'cd /app && node scripts/build --debug --oss'
            }
          }
        },
        buildDefault: {
          withBase(REMOTE_TAG) {
            stage('Build Default') {
              sh 'cd /app && node scripts/build --debug --no-oss'
            }
          }
        }
      ])
    },
    intake: {
      stage('Launch k8s kibana-cache agent') {
        withCache(CACHE_REMOTE_TAG) {
          stage('Intake') {
            sh "./test/scripts/jenkins_unit.sh"
          }
        }
      }
    },
    sasslint: {
      stage('Launch k8s kibana-cache agent') {
        withCache(CACHE_REMOTE_TAG) {
          stage('Linting') {
            sh "yarn lint:sass"
          }
        }
      }      
    }
  ])
}

def withDocker(closure) {
  podTemplate(inheritFrom: '', label: 'docker', yaml: """
---
apiVersion: v1
kind: Pod
metadata:
  name: dind
spec:
  containers:
  # - name: docker
  #   image: docker:stable
  #   command: ['cat']
  #   tty: true
  #   env: 
  #   - name: DOCKER_HOST
  #     value: tcp://localhost:2376
  #   - name: DOCKER_TLS_VERIFY
  #     value: "1"
  #   - name: DOCKER_CERT_PATH
  #     value: "/certs/client"
  #   volumeMounts: 
  #     - name: certs
  #       mountPath: /certs
  - name: docker
    image: gcr.io/elastic-kibana-184716/brianseeders/builder:latest
    command: ['cat']
    tty: true
    env: 
    - name: DOCKER_HOST
      value: tcp://localhost:2376
    - name: DOCKER_BUILDKIT
      value: ""
    - name: DOCKER_TLS_VERIFY
      value: "1"
    - name: DOCKER_CERT_PATH
      value: "/certs/client"
    - name: GOOGLE_APPLICATION_CREDENTIALS
      value: /secret/kaniko-secret.json
    volumeMounts: 
      - name: certs
        mountPath: /certs
      - name: kaniko-secret
        mountPath: /secret
  - name: dind
    image: docker:stable-dind
    args: ["--storage-driver", "overlay2"]
    tty: true
    securityContext:
      privileged: true
    env:
    - name: DOCKER_TLS_VERIFY
      value: "1"
    - name: DOCKER_CERT_PATH
      value: "/certs/client"
    volumeMounts: 
      - name: docker-storage 
        mountPath: /var/lib/docker
      - name: certs
        mountPath: /certs
  volumes:
  - name: docker-storage
    emptyDir: {}
  - name: certs
    emptyDir: {}
  - name: kaniko-secret
    secret:
      secretName: kaniko-secret
"""
  ) {
    node('docker') {
      container('docker') {
        sh 'gcloud auth activate-service-account --key-file=/secret/kaniko-secret.json'

        closure()
      }
    }
  }
}

def withKibanaImage(baseImage, closure) {
  def POD_LABEL = "kibana-base-${UUID.randomUUID().toString()}"

  podTemplate(inheritFrom: '', label: POD_LABEL, yaml: """
---
apiVersion: v1
kind: Pod
metadata:
  name: ${POD_LABEL}
spec:
  containers:
  - name: jnlp
    image: jenkins/jnlp-slave:alpine
    tty: true
    securityContext:
      runAsUser: 999
      allowPrivilegeEscalation: false
  - name: kibana
    image: ${baseImage}
    imagePullPolicy: Always
    command: ['cat']
    tty: true
    securityContext:
      runAsUser: 999
"""
  ) {
    node(POD_LABEL) {
      container('kibana') {
        closure()
      }
    }
  }
}

def withBase(tag, closure) {
  withKibanaImage(tag) {
    sh 'cd /app && bsdtar -xzf node_modules.tar.gz'

    closure()
  }  
}

def withCache(tag, closure) {
  withKibanaImage(tag) {
    parallel([
      cache: {
        sh 'bsdtar -xzf /home/kibana/yarn.tar.gz -C /'
        sh 'bsdtar -xzf /home/kibana/node_modules.tar.gz'
      },
      git: {
        checkout scm
      }
    ])

    sh 'yarn kbn bootstrap --frozen-lockfile --prefer-offline'

    closure()
  }
}