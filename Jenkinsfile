node {
  withDocker {
    def GIT_COMMIT = ''
    def SCM_VARS = ''

    stage('Git Checkout') {
      // git branch: 'jenkins-pipeline', credentialsId: 'f6c7695a-671e-4f4f-a331-acdce44ff9ba', url: 'git@github.com:brianseeders/kibana.git'
      SCM_VARS = checkout scm
      // GIT_COMMIT = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
      GIT_COMMIT = SCM_VARS.GIT_COMMIT
    }

    def baseImage = "kibana-base:${GIT_COMMIT}"

    stage('Build base image') {
      sh "DOCKER_BUILDKIT=1 docker build -t '${baseImage}' ."
    }

    stage('Linting') {
      sh "docker run --rm -it ${baseImage} lint:sass"
    }
  }
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
  - name: docker
    image: docker:stable
    command: ['cat']
    tty: true
    env: 
    - name: DOCKER_HOST
      value: tcp://localhost:2376
    - name: DOCKER_TLS_VERIFY
      value: "1"
    - name: DOCKER_CERT_PATH
      value: "/certs/client"
    volumeMounts: 
      - name: certs
        mountPath: /certs
  - name: dind
    image: docker:stable-dind
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
"""
  ) {
    node('docker') {
      container('docker') {
        closure()
      }
    }
  }
}