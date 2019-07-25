pipeline {
    agent any
    options {
        skipDefaultCheckout true
        disableResume()
        durabilityHint('PERFORMANCE_OPTIMIZED')
    }
    stages {
        stage('Init') {
            steps {
                script {
                    parallel([
                        A: {
                          node {
                            parallel([
                                A1: {
                                    sh 'echo A1'
                                },
                                A2: {
                                    sh 'echo A2'
                                }
                            ])
                          }
                        },
                        B: {
                          node {
                            sh 'echo B1'
                          }
                        },
                        C: {
                          node {
                            sh 'echo C1'
                            sh 'echo C2'
                          }
                        },
                    ])
                }
            }
        }
    }
}