def branchName = {
  sh (
    script: 'git rev-parse --abbrev-ref HEAD',
    returnStdout: true
  ).trim()
}

def tarName = { "${it.JOB_NAME}-${branchName()}-${it.BUILD_ID}.tgz" }

def workspaceCacheName = { sprintf('%s/%s', it.WORKSPACE_CACHE_DIR, tarName(it) }

def unTar = {
  clearDir(it.WORKSPACE_DIR)
  dir(it.WORKSPACE_CACHE_DIR){
    sh 'pwd'
    sh 'ls -la'
    sh "tar xfz ${tarName(it)} --strip-components=4 -C ${it.WORKSPACE_DIR}"
  }
}

def tarGlobs = { "${it.WORKSPACE_DIR}/elasticsearch/* ${it.WORKSPACE_DIR}/${it.JOB_NAME}/*" }

def tarAll = {
  dir(it.WORKSPACE_CACHE_DIR){
    sh "tar -czf ${it.WORKSPACE_CACHE_NAME} ${tarGlobs(it)}"
  }
}

def bucket {

}
return this
