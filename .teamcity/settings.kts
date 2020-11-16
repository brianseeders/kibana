import jetbrains.buildServer.configs.kotlin.v2019_2.*

version = "2020.1"

project(Project {
  params {
    param("teamcity.ui.settings.readOnly", "true")
  }

  features {
    feature {
      type = "CloudImage"
      param("network", "default")
      param("subnet", "default")
      param("growingId", "true")
      param("agent_pool_id", "-2")
      param("preemptible", "false")
      param("source-id", "kibana-c2-16-")
      param("machineType", "c2-standard-16")
      param("sourceProject", "elastic-kibana-184716")
      param("sourceImageFamily", "kibana-ci-elastic-dev")
  //    param("sourceImageFamily", "elastic-kibana-ci-ubuntu-1804-lts-nocache")
      // param("sourceProject", "elastic-images-prod")
      // param("sourceImageFamily", "elastic-kibana-ci-ubuntu-1804-lts")
      param("zone", "us-central1-a")
      param("profileId", "google-4")
      param("diskType", "pd-ssd")
      param("machineCustom", "false")
      param("maxInstances", "200")
      param("imageType", "ImageFamily")
      param("diskSizeGb", "75") // TODO
    }
  }
})
