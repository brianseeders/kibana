#!/bin/groovy

library 'kibana-pipeline-library'
kibanaLibrary.load()

kibanaPipeline(timeoutMinutes: 155, checkPrChanges: true) {
  ciStats.trackBuild {
    githubPr.withDefaultPrComments {
      catchError {
        retryable.enable()
        kibanaPipeline.allCiTasks()
      }

      if (params.NOTIFY_ON_FAILURE) {
        slackNotifications.onFailure()
        kibanaPipeline.sendMail()
      }
    }
  }
}
