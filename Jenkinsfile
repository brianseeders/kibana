#!/bin/groovy
node {
  def REMOTE_TAG_BASE = 'gcr.io/elastic-kibana-184716/brianseeders/kibana'
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
    builds: {
      parallel([
        oss: {
          stage('builds > oss') {
            withDocker {
              def data = pullCacheAndSource(CACHE_IMAGES_TO_TRY)
              def tag = REMOTE_TAG_BASE+'-oss'

              stage('builds > oss > build') {
                sh "DOCKER_BUILDKIT=1 docker build -f Dockerfile-oss --cache-from='${CACHE_REMOTE_TAG}' --target final --build-arg 'CACHE_IMAGE=${CACHE_REMOTE_TAG}' -t '${tag}' ."
              }

              stage('builds > oss > push') {
                sh "docker push '${tag}'"
              }

              stage('builds > oss > tests') {
                withBuild(tag) {
                  stage('builds > oss > tests > ciGroup3') {
                    sh 'export CI_GROUP=3; export TEST_BROWSER_HEADLESS=1 && cd /app && yarn run grunt "run:functionalTests_ciGroup3"'
                  }
                }
              }
            }
          }
        },
        cache: {
          stage('builds > cache') {
            withDocker {
              stage('builds > cache > scm') {
                checkout scm
              }

              stage('builds > cache > build') {
                sh "docker build -f Dockerfile-cache -t '${CACHE_REMOTE_TAG}' ."
              }

              stage('builds > cache > push') {
                sh "docker push '${CACHE_REMOTE_TAG}'"
              }
            }
          }
        },
      ])
    },
    intake: {
      stage('intake') {
        withCache(CACHE_REMOTE_TAG) {
          stage('intake > run') {
            sh "./test/scripts/jenkins_unit.sh"
          }
        }
      }
    },
    sasslint: {
      stage('sasslint') {
        withCache(CACHE_REMOTE_TAG) {
          stage('sasslint > run') {
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
    resources:
      requests:
        cpu: 4
        memory: 8Gi
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
    resources:
      requests:
        cpu: 1
        memory: 4Gi
"""
  ) {
    node(POD_LABEL) {
      container('kibana') {
        closure()
      }
    }
  }
}

def withBuild(tag, closure) {
  withKibanaImage(tag) {
    sh 'cd /app && bsdtar -xzf /home/kibana/node_modules.tar.gz'
    sh 'cd /app && mkdir -p build/oss && bsdtar -xzf target/*oss*.tar.gz -C build/oss'
    sh 'cd /app && bash src/dev/ci_setup/setup_docker.sh'

    closure()
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
        try {
          sh '[ -d "/app/.git" ] && cp -R /app/.git .'
        } catch (ex) {
          print "No git cache present in image..."
        }
        checkout scm
      }
    ])

    sh 'yarn kbn bootstrap --frozen-lockfile --prefer-offline'

    closure()
  }
}

def pullCacheAndSource(cacheImagesToTry) {
  def SCM_VARS
  def GIT_COMMIT

  parallel([
    image_pull: {
      stage('Pull cache image') {
        for(image in cacheImagesToTry) {
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

  return [
    SCM_VARS: SCM_VARS,
    GIT_COMMIT: GIT_COMMIT,
  ]
}