// state = error|failure|pending|success
def post(state) {
  return
  withGithubCredentials {
    def path = "repos/elastic/kibana/statuses/${GIT_COMMIT}" // TODO
    return githubApi.post(path, [
      state: state,
      context: "kibana-ci",
      description: "Kibana CI",
      target_url: env.BUILD_URL,
    ])
  }
}

def post() {
  def status = buildUtils.getBuildStatus()
  def state = (status == 'SUCCESS' || status == 'UNSTABLE') ? 'success' : 'failure'
  post(state)
}

return this
