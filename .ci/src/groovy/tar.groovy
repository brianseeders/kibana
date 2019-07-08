def branchName = {
  sh (
    script: 'git rev-parse --abbrev-ref HEAD',
    returnStdout: true
  ).trim()
}

def tarName = { envObj ->
  "${envObj.JOB_NAME}-${branchName()}-${envObj.BUILD_ID}.tgz"
}

def unTar(envObj){
  clearDir(envObj.WORKSPACE_DIR)
  dir(envObj.WORKSPACE_CACHE_DIR){
    sh 'pwd'
    sh 'ls -la'
    sh "tar xfz ${tarName(envObj)} --strip-components=4 -C ${envObj.WORKSPACE_DIR}"
  }
}

def tarGlobs = { envObj ->
  "${envObj.WORKSPACE_DIR}/elasticsearch/* ${envObj.WORKSPACE_DIR}/${envObj.JOB_NAME}/*"
}

def tarAll = { envObj ->
  dir(envObj.WORKSPACE_CACHE_DIR){
    sh "tar -czf ${envObj.WORKSPACE_CACHE_NAME} ${tarGlobs(envObj)}"
  }
}

def bucket {

}
return this
