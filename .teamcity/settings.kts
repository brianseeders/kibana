import builds.oss.*
import builds.default.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import templates.DefaultTemplate

version = "2020.1"

project {
  params {
    param("teamcity.ui.settings.readOnly", "true")
  }

  vcsRoot(DefaultRoot)
  template(DefaultTemplate)

  defaultTemplate = DefaultTemplate

//        triggers {
//            vcs {
//                perCheckinTriggering = true
//            }
//        }
//    }


  features {
    feature {
      id = "KIBANA_BRIANSEEDERS"
      type = "CloudImage"
      param("subnet", "default")
      param("growingId", "true")
      param("agent_pool_id", "-2")
      param("source-id", "kibana-brianseeders-")
      param("network", "default")
      param("preemptible", "false")
      param("zone", "us-central1-a")
      param("profileId", "kibana-brianseeders")
      param("diskType", "pd-ssd")
      param("sourceImage", "teamcity-agent-1596749170")
//      param("sourceImageFamily", "kibana-teamcity-dev-agents")
      param("machineCustom", "false")
      param("maxInstances", "3")
      param("imageType", "Image") // TODO
      param("machineType", "n2-standard-16")
    }

    feature {
      id = "kibana-brianseeders"
      type = "CloudProfile"
      param("profileServerUrl", "")
      param("system.cloud.profile_id", "kibana-brianseeders")
      param("total-work-time", "")
      param("credentialsType", "key")
      param("description", "")
      param("cloud-code", "google")
      param("terminate-after-build", "true")
      param("enabled", "true")
      param("agentPushPreset", "")
      param("profileId", "kibana-brianseeders")
      param("name", "kibana-brianseeders")
      param("next-hour", "")
      param("terminate-idle-time", "15")
      param("secure:accessKey", "credentialsJSON:8d4330f9-319c-4600-aa2a-f41f8e78657d")
    }
  }

  subProject {
    id("OSS")
    name = "OSS Distro"

    buildType(OssBuild)

    subProject {
      id("OSS_Functional")
      name = "Functional"

      val ciGroups = (1..12).map { OssCiGroup(it) }

      buildType {
        id("CIGroups_Composite")
        name = "CI Groups"
        type = BuildTypeSettings.Type.COMPOSITE

        dependencies {
          for (ciGroup in ciGroups) {
            snapshot(ciGroup) {
              reuseBuilds = ReuseBuilds.SUCCESSFUL
              onDependencyCancel = FailureAction.CANCEL
              onDependencyFailure = FailureAction.CANCEL
              synchronizeRevisions = true
            }
          }
        }
      }

      buildType(OssVisualRegression)

      subProject {
        id("CIGroups")
        name = "CI Groups"

        for (ciGroup in ciGroups) buildType(ciGroup)
      }
    }
  }

  subProject {
    id("Default")
    name = "Default Distro"

    buildType(DefaultBuild)

    subProject {
      id("Default_Functional")
      name = "Functional"

      val ciGroups = (1..10).map { DefaultCiGroup(it) }

      buildType {
        id("Default_CIGroups_Composite")
        name = "CI Groups"
        type = BuildTypeSettings.Type.COMPOSITE

        dependencies {
          for (ciGroup in ciGroups) {
            snapshot(ciGroup) {
              reuseBuilds = ReuseBuilds.SUCCESSFUL
              onDependencyCancel = FailureAction.CANCEL
              onDependencyFailure = FailureAction.CANCEL
              synchronizeRevisions = true
            }
          }
        }
      }

      buildType(DefaultVisualRegression)

      subProject {
        id("Default_CIGroups")
        name = "CI Groups"

        for (ciGroup in ciGroups) buildType(ciGroup)
      }
    }
  }
}
