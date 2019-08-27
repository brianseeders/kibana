#!/bin/groovy

def failedTests = []
def flakyTests = []

def workspaceArchiveFilename = 'workspace.archive.tar.gz'
def ossWorkspaceArchiveFilename = 'workspace-oss.archive.tar.gz'
def defaultWorkspaceArchiveFilename = 'workspace-default.archive.tar.gz'

def RUN_OSS = true
def RUN_DEFAULT = true

def ossKibanaBuildComplete = false
def ossKibanaBuildUploaded = false

def defaultKibanaBuildComplete = false
def defaultKibanaBuildUploaded = false

def waitTil(conditionClosure) {
  for(def i = 0; i < 50000 && !conditionClosure(); i++) {
    sleep 10
  }
}

properties([durabilityHint('PERFORMANCE_OPTIMIZED')])

def ossTestSuites = [
  "api_integration.apis.elasticsearch",
  "api_integration.apis.general.cookies",
  "api_integration.apis.general.csp",
  "api_integration.apis.home.sample_data",
  "api_integration.apis.index_patterns.es_errors.errors",
  "api_integration.apis.index_patterns.fields_for_time_pattern_route.errors",
  "api_integration.apis.index_patterns.fields_for_time_pattern_route.pattern",
  "api_integration.apis.index_patterns.fields_for_time_pattern_route.query_params",
  "api_integration.apis.index_patterns.fields_for_wildcard_route.conflicts",
  "api_integration.apis.index_patterns.fields_for_wildcard_route.params",
  "api_integration.apis.index_patterns.fields_for_wildcard_route.response",
  "api_integration.apis.kql_telemetry.kql_telemetry",
  "api_integration.apis.management.saved_objects.find",
  "api_integration.apis.management.saved_objects.relationships",
  "api_integration.apis.saved_objects.bulk_create",
  "api_integration.apis.saved_objects.bulk_get",
  "api_integration.apis.saved_objects.create",
  "api_integration.apis.saved_objects.delete",
  "api_integration.apis.saved_objects.export",
  "api_integration.apis.saved_objects.find",
  "api_integration.apis.saved_objects.get",
  "api_integration.apis.saved_objects.import",
  "api_integration.apis.saved_objects.migrations",
  "api_integration.apis.saved_objects.resolve_import_errors",
  "api_integration.apis.saved_objects.update",
  "api_integration.apis.scripts.languages",
  "api_integration.apis.search.count",
  "api_integration.apis.shorten",
  "api_integration.apis.stats.stats",
  "api_integration.apis.status.status",
  "api_integration.apis.suggestions.suggestions",
  "api_integration.apis.ui_metric.ui_metric",
  "functional.apps.console._console",
  "functional.apps.context._date_nanos",
  "functional.apps.context._discover_navigation",
  "functional.apps.context._filters",
  "functional.apps.context._size",
  "functional.apps.dashboard.bwc_shared_urls",
  "functional.apps.dashboard.create_and_add_embeddables",
  "functional.apps.dashboard.dashboard_clone",
  "functional.apps.dashboard.dashboard_filter_bar",
  "functional.apps.dashboard.dashboard_filtering",
  "functional.apps.dashboard.dashboard_grid",
  "functional.apps.dashboard.dashboard_listing",
  "functional.apps.dashboard.dashboard_options",
  "functional.apps.dashboard.dashboard_query_bar",
  "functional.apps.dashboard.dashboard_save",
  "functional.apps.dashboard.dashboard_snapshots",
  "functional.apps.dashboard.dashboard_state",
  "functional.apps.dashboard.dashboard_time_picker",
  "functional.apps.dashboard.data_shared_attributes",
  "functional.apps.dashboard.embed_mode",
  "functional.apps.dashboard.embeddable_rendering",
  "functional.apps.dashboard.empty_dashboard",
  "functional.apps.dashboard.full_screen_mode",
  "functional.apps.dashboard.panel_controls",
  "functional.apps.dashboard.panel_expand_toggle",
  "functional.apps.dashboard.time_zones",
  "functional.apps.dashboard.view_edit",
  "functional.apps.discover._date_nanos",
  "functional.apps.discover._discover",
  "functional.apps.discover._doc_navigation",
  "functional.apps.discover._errors",
  "functional.apps.discover._field_data",
  "functional.apps.discover._inspector",
  "functional.apps.discover._large_string",
  "functional.apps.discover._shared_links",
  "functional.apps.discover._sidebar",
  "functional.apps.discover._source_filters",
  "functional.apps.getting_started._shakespeare",
  "functional.apps.home._add_data",
  "functional.apps.home._home",
  "functional.apps.home._navigation",
  "functional.apps.home._sample_data",
  "functional.apps.management._create_index_pattern_wizard",
  "functional.apps.management._handle_alias",
  "functional.apps.management._handle_version_conflict",
  "functional.apps.management._import_objects",
  "functional.apps.management._index_pattern_create_delete",
  "functional.apps.management._index_pattern_filter",
  "functional.apps.management._index_pattern_popularity",
  "functional.apps.management._index_pattern_results_sort",
  "functional.apps.management._kibana_settings",
  "functional.apps.management._scripted_fields_filter",
  "functional.apps.management._scripted_fields_preview",
  "functional.apps.management._test_huge_fields",
  "functional.apps.status_page",
  "functional.apps.timelion._expression_typeahead",
  "functional.apps.visualize._area_chart",
  "functional.apps.visualize._chart_types",
  "functional.apps.visualize._data_table_nontimeindex",
  "functional.apps.visualize._embedding_chart",
  "functional.apps.visualize._experimental_vis",
  "functional.apps.visualize._gauge_chart",
  "functional.apps.visualize._heatmap_chart",
  "functional.apps.visualize._histogram_request_start",
  "functional.apps.visualize._inspector",
  "functional.apps.visualize._lab_mode",
  "functional.apps.visualize._line_chart",
  "functional.apps.visualize._linked_saved_searches",
  "functional.apps.visualize._markdown_vis",
  "functional.apps.visualize._metric_chart",
  "functional.apps.visualize._pie_chart",
  "functional.apps.visualize._point_series_options",
  "functional.apps.visualize._region_map",
  "functional.apps.visualize._shared_item",
  "functional.apps.visualize._tag_cloud",
  "functional.apps.visualize._tile_map",
  "functional.apps.visualize._tsvb_chart",
  "functional.apps.visualize._tsvb_markdown",
  "functional.apps.visualize._tsvb_table",
  "functional.apps.visualize._tsvb_time_series",
  "functional.apps.visualize._vega_chart",
  "functional.apps.visualize._vertical_bar_chart_nontimeindex",
  "functional.apps.visualize._visualize_listing",
  "functional.apps.visualize.input_control_vis.chained_controls",
  "functional.apps.visualize.input_control_vis.dynamic_options",
  "functional.apps.visualize.input_control_vis.input_control_options",
  "interpreter_functional.run_pipeline.basic",
  "interpreter_functional.run_pipeline.metric",
  "interpreter_functional.run_pipeline.tag_cloud",
  "plugin_functional.app_plugins.app_navigation",
  "plugin_functional.core_plugins.ui_plugins",
  "plugin_functional.custom_visualizations.self_changing_vis",
  "plugin_functional.embedding_visualizations.embed_by_id",
  "plugin_functional.panel_actions.panel_actions",
]

def xpackSuites = [
  "test/reporting/configs/chromium_api.js": [
    "reporting.api.bwc_existing_indexes",
    "reporting.api.bwc_generation_urls",
    "reporting.api.chromium_tests",
    "reporting.api.usage"
  ],
  "test/reporting/configs/chromium_functional.js": [
    "reporting.functional.reporting"
  ],
  "test/reporting/configs/generate_api.js": [
    "reporting.api.generate.csv_saved_search"
  ],
  "test/functional/config.js": [
    "functional.apps.advanced_settings.feature_controls.advanced_settings_security",
    "functional.apps.advanced_settings.feature_controls.advanced_settings_spaces",
    "functional.apps.apm.feature_controls.apm_security",
    "functional.apps.apm.feature_controls.apm_spaces",
    "functional.apps.canvas.feature_controls.canvas_security",
    "functional.apps.canvas.feature_controls.canvas_spaces",
    "functional.apps.canvas.smoke_test",
    "functional.apps.code.code_intelligence",
    "functional.apps.code.explore_repository",
    "functional.apps.code.file_tree",
    "functional.apps.code.history",
    "functional.apps.code.manage_repositories",
    "functional.apps.code.search",
    "functional.apps.code.with_security",
    "functional.apps.cross_cluster_replication.home_page",
    "functional.apps.dashboard.feature_controls.dashboard_security",
    "functional.apps.dashboard.feature_controls.dashboard_spaces",
    "functional.apps.dashboard_mode.dashboard_view_mode",
    "functional.apps.dev_tools.feature_controls.dev_tools_security",
    "functional.apps.dev_tools.feature_controls.dev_tools_spaces",
    "functional.apps.dev_tools.searchprofiler_editor",
    "functional.apps.discover.feature_controls.discover_security",
    "functional.apps.discover.feature_controls.discover_spaces",
    "functional.apps.graph.feature_controls.graph_security",
    "functional.apps.graph.feature_controls.graph_spaces",
    "functional.apps.graph.graph",
    "functional.apps.grok_debugger.grok_debugger",
    "functional.apps.index_lifecycle_management.home_page",
    "functional.apps.index_management.home_page",
    "functional.apps.index_patterns.feature_controls.index_patterns_security",
    "functional.apps.index_patterns.feature_controls.index_patterns_spaces",
    "functional.apps.infra.feature_controls.infrastructure_security",
    "functional.apps.infra.feature_controls.infrastructure_spaces",
    "functional.apps.infra.feature_controls.logs_security",
    "functional.apps.infra.feature_controls.logs_spaces",
    "functional.apps.infra.home_page",
    "functional.apps.infra.logs_source_configuration",
    "functional.apps.infra.metrics_source_configuration",
    "functional.apps.license_management.home_page",
    "functional.apps.logstash.pipeline_create",
    "functional.apps.logstash.pipeline_list",
    "functional.apps.machine_learning.feature_controls.ml_security",
    "functional.apps.machine_learning.feature_controls.ml_spaces",
    "functional.apps.maps.add_layer_panel",
    "functional.apps.maps.documents_source.search_hits",
    "functional.apps.maps.documents_source.top_hits",
    "functional.apps.maps.embeddable.dashboard",
    "functional.apps.maps.embeddable.embeddable_state",
    "functional.apps.maps.embeddable.tooltip_filter_actions",
    "functional.apps.maps.feature_controls.maps_security",
    "functional.apps.maps.feature_controls.maps_spaces",
    "functional.apps.maps.full_screen_mode",
    "functional.apps.maps.import_geojson.add_layer_import_panel",
    "functional.apps.maps.import_geojson.file_indexing_panel",
    "functional.apps.maps.joins",
    "functional.apps.maps.layer_errors",
    "functional.apps.maps.sample_data",
    "functional.apps.maps.saved_object_management",
    "functional.apps.monitoring.beats.beat_detail",
    "functional.apps.monitoring.beats.cluster",
    "functional.apps.monitoring.beats.listing",
    "functional.apps.monitoring.beats.overview",
    "functional.apps.monitoring.cluster.alerts",
    "functional.apps.monitoring.cluster.list",
    "functional.apps.monitoring.cluster.overview",
    "functional.apps.monitoring.elasticsearch.index_detail",
    "functional.apps.monitoring.elasticsearch.indices",
    "functional.apps.monitoring.elasticsearch.node_detail",
    "functional.apps.monitoring.elasticsearch.nodes",
    "functional.apps.monitoring.elasticsearch.overview",
    "functional.apps.monitoring.elasticsearch.shards",
    "functional.apps.monitoring.enable_monitoring",
    "functional.apps.monitoring.feature_controls.monitoring_security",
    "functional.apps.monitoring.feature_controls.monitoring_spaces",
    "functional.apps.monitoring.kibana.instances",
    "functional.apps.monitoring.kibana.overview",
    "functional.apps.monitoring.logstash.pipelines",
    "functional.apps.remote_clusters.home_page",
    "functional.apps.rollup_job.rollup_jobs",
    "functional.apps.saved_objects_management.feature_controls.saved_objects_management_security",
    "functional.apps.security.doc_level_security_roles",
    "functional.apps.security.field_level_security",
    "functional.apps.security.management",
    "functional.apps.security.rbac_phase1",
    "functional.apps.security.secure_roles_perm",
    "functional.apps.security.security",
    "functional.apps.security.user_email",
    "functional.apps.security.users",
    "functional.apps.snapshot_restore.home_page",
    "functional.apps.spaces.feature_controls.spaces_security",
    "functional.apps.spaces.spaces_selection",
    "functional.apps.status_page.status_page",
    "functional.apps.timelion.feature_controls.timelion_security",
    "functional.apps.timelion.feature_controls.timelion_spaces",
    "functional.apps.upgrade_assistant.upgrade_assistant",
    "functional.apps.uptime.feature_controls.uptime_security",
    "functional.apps.uptime.feature_controls.uptime_spaces",
    "functional.apps.uptime.monitor",
    "functional.apps.uptime.overview",
    "functional.apps.visualize.feature_controls.visualize_security",
    "functional.apps.visualize.feature_controls.visualize_spaces",
    "functional.apps.watcher.watcher_test",
    "functional.apps.maps.es_geo_grid_source", // slow
  ],
  "test/api_integration/config_security_basic.js": [
    "api_integration.apis.security.basic_login",
    "api_integration.apis.security.builtin_es_privileges",
    "api_integration.apis.security.index_fields",
    "api_integration.apis.security.privileges",
    "api_integration.apis.security.roles"
  ],
  "test/api_integration/config.js": [
    "api_integration.apis.apm",
    "api_integration.apis.beats",
    "api_integration.apis.code",
    "api_integration.apis.code",
    "api_integration.apis.console",
    "api_integration.apis.es",
    "api_integration.apis.infra",
    "api_integration.apis.kibana",
    "api_integration.apis.logstash",
    "api_integration.apis.management",
    "api_integration.apis.maps",
    "api_integration.apis.monitoring",
    "api_integration.apis.security",
    "api_integration.apis.short_urls",
    "api_integration.apis.siem",
    "api_integration.apis.spaces",
    "api_integration.apis.telemetry",
    "api_integration.apis.uptime",
    "api_integration.apis.xpack_main",
  ],
  "test/alerting_api_integration/config_security_enabled.js": [
    "alerting_api_integration.apis.actions.builtin_action_types.email",
    "alerting_api_integration.apis.actions.builtin_action_types.es_index",
    "alerting_api_integration.apis.actions.builtin_action_types.server_log",
    "alerting_api_integration.apis.actions.builtin_action_types.slack",
    "alerting_api_integration.apis.actions.create",
    "alerting_api_integration.apis.actions.delete",
    "alerting_api_integration.apis.actions.execute",
    "alerting_api_integration.apis.actions.find",
    "alerting_api_integration.apis.actions.get",
    "alerting_api_integration.apis.actions.list_action_types",
    "alerting_api_integration.apis.actions.update",
    "alerting_api_integration.apis.alerting.alerts",
    "alerting_api_integration.apis.alerting.create",
    "alerting_api_integration.apis.alerting.delete",
    "alerting_api_integration.apis.alerting.disable",
    "alerting_api_integration.apis.alerting.enable",
    "alerting_api_integration.apis.alerting.find",
    "alerting_api_integration.apis.alerting.get",
    "alerting_api_integration.apis.alerting.list_alert_types",
    "alerting_api_integration.apis.alerting.update"
  ],
  "test/plugin_api_integration/config.js": [
    "plugin_api_integration.encrypted_saved_objects.encrypted_saved_objects_api",
    "plugin_api_integration.task_manager.task_manager_integration"
  ],
  "test/kerberos_api_integration/config.ts": [
    "kerberos_api_integration.apis.security.kerberos_login"
  ],
  "test/kerberos_api_integration/anonymous_access.config.ts": [
    "kerberos_api_integration.apis.security.kerberos_login"
  ],
  "test/saml_api_integration/config.js": [
    "saml_api_integration.apis.security.saml_login"
  ],
  "test/token_api_integration/config.js": [
    "token_api_integration.auth.header",
    "token_api_integration.auth.login",
    "token_api_integration.auth.logout",
    "token_api_integration.auth.session"
  ],
  "test/oidc_api_integration/config.ts": [
    "oidc_api_integration.apis.authorization_code_flow.oidc_auth"
  ],
  "test/oidc_api_integration/implicit_flow.config.ts": [
    "oidc_api_integration.apis.implicit_flow.oidc_auth"
  ],
  "test/spaces_api_integration/spaces_only/config.ts": [
    "spaces_api_integration.spaces_only.apis.create",
    "spaces_api_integration.spaces_only.apis.delete",
    "spaces_api_integration.spaces_only.apis.get_all",
    "spaces_api_integration.spaces_only.apis.select",
    "spaces_api_integration.spaces_only.apis.update"
  ],
  "test/spaces_api_integration/security_and_spaces/config_trial.ts": [
    "spaces_api_integration.security_and_spaces.apis.create",
    "spaces_api_integration.security_and_spaces.apis.delete",
    "spaces_api_integration.security_and_spaces.apis.get_all",
    "spaces_api_integration.security_and_spaces.apis.select",
    "spaces_api_integration.security_and_spaces.apis.update",
  ],
  "test/spaces_api_integration/security_and_spaces/config_basic.ts": [
    "spaces_api_integration.security_and_spaces.apis.create",
    "spaces_api_integration.security_and_spaces.apis.delete",
    "spaces_api_integration.security_and_spaces.apis.get_all",
    "spaces_api_integration.security_and_spaces.apis.select",
    "spaces_api_integration.security_and_spaces.apis.update",
  ],
  "test/saved_object_api_integration/security_and_spaces/config_trial.ts": [
    "saved_object_api_integration.security_and_spaces.apis.bulk_create",
    "saved_object_api_integration.security_and_spaces.apis.bulk_get",
    "saved_object_api_integration.security_and_spaces.apis.create",
    "saved_object_api_integration.security_and_spaces.apis.delete",
    "saved_object_api_integration.security_and_spaces.apis.export",
    "saved_object_api_integration.security_and_spaces.apis.find",
    "saved_object_api_integration.security_and_spaces.apis.get",
    "saved_object_api_integration.security_and_spaces.apis.import",
    "saved_object_api_integration.security_and_spaces.apis.resolve_import_errors",
    "saved_object_api_integration.security_and_spaces.apis.update"
  ],
  "test/saved_object_api_integration/security_and_spaces/config_basic.ts": [
    "saved_object_api_integration.security_and_spaces.apis.bulk_create",
    "saved_object_api_integration.security_and_spaces.apis.bulk_get",
    "saved_object_api_integration.security_and_spaces.apis.create",
    "saved_object_api_integration.security_and_spaces.apis.delete",
    "saved_object_api_integration.security_and_spaces.apis.export",
    "saved_object_api_integration.security_and_spaces.apis.find",
    "saved_object_api_integration.security_and_spaces.apis.get",
    "saved_object_api_integration.security_and_spaces.apis.import",
    "saved_object_api_integration.security_and_spaces.apis.resolve_import_errors",
    "saved_object_api_integration.security_and_spaces.apis.update"
  ],
  "test/saved_object_api_integration/security_only/config_trial.ts": [
    "saved_object_api_integration.security_only.apis.bulk_create",
    "saved_object_api_integration.security_only.apis.bulk_get",
    "saved_object_api_integration.security_only.apis.create",
    "saved_object_api_integration.security_only.apis.delete",
    "saved_object_api_integration.security_only.apis.export",
    "saved_object_api_integration.security_only.apis.find",
    "saved_object_api_integration.security_only.apis.get",
    "saved_object_api_integration.security_only.apis.import",
    "saved_object_api_integration.security_only.apis.resolve_import_errors",
    "saved_object_api_integration.security_only.apis.update"
  ],
  "test/saved_object_api_integration/security_only/config_basic.ts": [
    "saved_object_api_integration.security_only.apis.bulk_create",
    "saved_object_api_integration.security_only.apis.bulk_get",
    "saved_object_api_integration.security_only.apis.create",
    "saved_object_api_integration.security_only.apis.delete",
    "saved_object_api_integration.security_only.apis.export",
    "saved_object_api_integration.security_only.apis.find",
    "saved_object_api_integration.security_only.apis.get",
    "saved_object_api_integration.security_only.apis.import",
    "saved_object_api_integration.security_only.apis.resolve_import_errors",
    "saved_object_api_integration.security_only.apis.update"
  ],
  "test/saved_object_api_integration/spaces_only/config.ts": [
    "saved_object_api_integration.spaces_only.apis.bulk_create",
    "saved_object_api_integration.spaces_only.apis.bulk_get",
    "saved_object_api_integration.spaces_only.apis.create",
    "saved_object_api_integration.spaces_only.apis.delete",
    "saved_object_api_integration.spaces_only.apis.export",
    "saved_object_api_integration.spaces_only.apis.find",
    "saved_object_api_integration.spaces_only.apis.get",
    "saved_object_api_integration.spaces_only.apis.import",
    "saved_object_api_integration.spaces_only.apis.resolve_import_errors",
    "saved_object_api_integration.spaces_only.apis.update"
  ],
  "test/ui_capabilities/security_and_spaces/config.ts": [
    "ui_capabilities.security_and_spaces.tests.catalogue",
    "ui_capabilities.security_and_spaces.tests.foo",
    "ui_capabilities.security_and_spaces.tests.nav_links"
  ],
  "test/ui_capabilities/security_only/config.ts": [
    "ui_capabilities.security_only.tests.catalogue",
    "ui_capabilities.security_only.tests.foo",
    "ui_capabilities.security_only.tests.nav_links"
  ],
  "test/ui_capabilities/spaces_only/config.ts": [
    "ui_capabilities.spaces_only.tests.catalogue",
    "ui_capabilities.spaces_only.tests.foo",
    "ui_capabilities.spaces_only.tests.nav_links"
  ],
  "test/upgrade_assistant_integration/config.js": [
    "upgrade_assistant_integration.upgrade_assistant.reindexing"
  ]
]

// class CStage {
//   String name
//   String fullName

//   List<CStage> stages = []
//   Date start
//   Date end
//   WorkflowScript ctx

//   CStage(name, prevName, ctx) {
//     this.name = name
//     this.fullName = "${prevName}[${name}]"
//     this.ctx = ctx
//     this.start = new Date()
//   }

//   def addStage(CStage stage) {
//     this.stages << stage
//   }

//   def setEnd() {
//     this.end = new Date();
//   }
// }

// class StageContext {
//   CStage parentStage
//   Closure cStage

//   def cStage(name, Closure closure) {
//     this.parentStage.ctx.print "Stage start: ${name}"
//     def stage = new CStage(name, this.parentStage.fullName, this.parentStage.ctx)
//     this.parentStage.addStage(stage)
//     closure.resolveStrategy = Closure.DELEGATE_FIRST
//     closure.delegate = new StageContext(stage)
//     try {
//       closure()
//     } finally {
//       stage.setEnd()
//       def duration = groovy.time.TimeCategory.minus(stage.end, stage.start)
//       this.parentStage.ctx.print "Stage end: ${stage.fullName} (${duration})"
//     }
//   }

//   StageContext(parentStage) {
//     this.parentStage = parentStage
//   }
// }

// def rootStage(ctx, closure) {
//   def stage = new CStage('root', '', ctx)
//   closure.resolveStrategy = Closure.DELEGATE_FIRST
//   closure.delegate = new StageContext(stage)
//   closure()
//   stage.setEnd()

//   return stage
// }

// def printStages(stage, prev = '') {
//   def stageString = stage.fullName
//   def duration = groovy.time.TimeCategory.minus(stage.end, stage.start)
//   print "${stageString} (${duration})"
//   stage.stages.each { printStages(it, stageString) }
// }

def cStage(stage, closure) {
  print "Begin Stage: ${stage}"
  closure()
  print "End Stage: ${stage}"
}

def rootStage(_, closure) {
  cStage('Root', closure)
}

// TODO wrap rootStage in a try{} and print info
def root = rootStage(this) {
  def ossCiGroupRunner = { additionalScript='' ->
    return {
      if (!RUN_OSS) { return }

      cStage("oss-testRunner") {

        if (!ossKibanaBuildComplete) {
          sleep 360
          waitTil { ossKibanaBuildComplete }
        }

        // TODO need to move functionalTests:ensureAllTestsInCiGroup to before the build
        node('testrunner-large') {
          skipDefaultCheckout()

          env.HOME = env.JENKINS_HOME

          if (!ossKibanaBuildUploaded) {
            sleep 150
            waitTil { ossKibanaBuildUploaded }
          }

          cStage('Download archive') {
            step([
              $class: 'DownloadStep',
              credentialsId: 'kibana-ci-gcs-plugin',
              bucketUri: "gs://kibana-pipeline-testing/workspaces/latest/${ossWorkspaceArchiveFilename}",
              localDirectory: env.WORKSPACE
            ])
          }

          cStage('Extract archive') {
            bash "tar -xzf workspaces/latest/${ossWorkspaceArchiveFilename}"
            bash 'rm -rf /var/lib/jenkins/.kibana/node && mv var/lib/jenkins/.kibana/node /var/lib/jenkins/.kibana/'
          }

          withEnv """
            set -e
            export TEST_BROWSER_HEADLESS=1

            echo " -> extracting default Kibana distributable for use in functional tests"
            cd "\$KIBANA_DIR"
            linuxBuild="\$(find "\$KIBANA_DIR/target" -name 'kibana-*-linux-x86_64.tar.gz')"
            installDir="\$PARENT_DIR/install/kibana"
            mkdir -p "\$installDir"
            tar -xzf "\$linuxBuild" -C "\$installDir" --strip=1
          """

          def nextWorker = 1

          def startTesting = {
            def workerNumber = nextWorker
            def lastConfigPath = null

            nextWorker++

            cStage('Copy Kibana') {
              withEnv """
                installDir="\$PARENT_DIR/install/kibana"
                destDir=\${installDir}-${workerNumber}
                cp -R "\$installDir" "\$destDir"
              """
            }

            def kibanaPort = "61${workerNumber}1"
            def esPort = "61${workerNumber}2"

            def portString = "TEST_KIBANA_URL=http://elastic:changeme@localhost:${kibanaPort} TEST_ES_URL=http://elastic:changeme@localhost:${esPort}"
            
            // nohup yarn test:ui:server --kibana-install-dir "\$installDir" > test-server-output.log &

            cStage('Launch Kibana/ES') {
              withEnv """
                cd "\$KIBANA_DIR"

                rm -f test-server-output-${workerNumber}.log
                installDir="\$PARENT_DIR/install/kibana-${workerNumber}"
                if [ -f runner-${workerNumber}.pid ]; then kill \$(cat runner-${workerNumber}.pid); sleep 5; fi
                ${portString} nohup yarn test:ui:server --kibana-install-dir "\$installDir" > test-server-output-${workerNumber}.log & echo \$! > runner-${workerNumber}.pid
              """
            }
            
            sleep 10 // TODO
            waitTil {
              // TODO also check for process still running
              return sh (
                script: "grep 'Elasticsearch and Kibana are ready' test-server-output-${workerNumber}.log",
                returnStatus: true
              ) == 0
            }

            sh "cat test-server-output-${workerNumber}.log"

            while(!ossTestSuites.isEmpty()) {
              // TODO possible to pop an empty list
              def testSuite = ossTestSuites.pop()
              
              def runTests = {
                cStage("oss-ciGroup-${testSuite}") {
                  withTestReporter {
                    withEnv """
                      set -e
                      export CI_GROUP=${testSuite}
                      export TEST_BROWSER_HEADLESS=1

                      cd "\$KIBANA_DIR"

                      ${portString} checks-reporter-with-killswitch "Functional tests / Group ${testSuite}" \
                        node scripts/functional_test_runner \
                          --include-tag '${testSuite}' \
                          # --config test/functional/config.js \
                          # --config test/functional/config.firefox.js \
                          --bail --debug \
                          --kibana-install-dir "\$installDir"

                      ${additionalScript ?: ''}
                    """
                  }
                }
              }

              try {
                runTests()
              } catch (ex) {
                print "Error during oss test suite: ${testSuite}"
                print ex.toString()

                try {
                  runTests()
                  unstable "OSS test succeeded on second attempt, considering flaky: ${testSuite}"
                  flakyTests << [type: "oss", suite: testSuite]
                } catch(innerException) {
                  print "Error during oss test suite: ${testSuite}"
                  print innerException.toString()
                  failedTests << [type: "oss", suite: testSuite]
                }
              }
            }
          }

          parallel([
            worker1: { startTesting() },
            worker2: { startTesting() },
            worker3: { startTesting() },
            worker4: { startTesting() },
            worker5: { startTesting() },
            worker6: { startTesting() },
            worker7: { startTesting() },
            worker8: { startTesting() },
          ])
          
        }
      }
    }
  }

  def xpackCiGroupRunner = { primaryConfigPaths, additionalScript='' ->
    return {
      if (!RUN_DEFAULT) { return }

      def configPaths = primaryConfigPaths + (xpackSuites.keySet().findAll { it != "test/functional/config.js" })

      cStage("xpack-testRunner ${primaryConfigPaths}") {
        // TODO need to move 'Ensuring all functional tests are in a ciGroup' to before the build

        if (!defaultKibanaBuildComplete) {
          sleep 840
          waitTil { defaultKibanaBuildComplete }
        }

        node('testrunner-large') {
          skipDefaultCheckout()

          env.HOME = env.JENKINS_HOME

          if (!defaultKibanaBuildUploaded) {
            sleep 150
            waitTil { defaultKibanaBuildUploaded }
          }

          cStage('Download archive') {
            step([
              $class: 'DownloadStep',
              credentialsId: 'kibana-ci-gcs-plugin',
              bucketUri: "gs://kibana-pipeline-testing/workspaces/latest/${defaultWorkspaceArchiveFilename}",
              localDirectory: env.WORKSPACE
            ])
          }

          cStage('Extract archive') {
            bash "tar -xzf workspaces/latest/${defaultWorkspaceArchiveFilename}"
            bash 'rm -rf /var/lib/jenkins/.kibana/node && mv var/lib/jenkins/.kibana/node /var/lib/jenkins/.kibana/'
          }

          cStage('Extract Kibana') {
            withEnv """
              set -e
              export TEST_BROWSER_HEADLESS=1

              # TODO test in ciGroup verification replacement?

              echo " -> extracting default Kibana distributable for use in functional tests"
              cd "\$KIBANA_DIR"
              linuxBuild="\$(find "\$KIBANA_DIR/target" -name 'kibana-*-linux-x86_64.tar.gz')"
              installDir="\$PARENT_DIR/install/kibana"
              mkdir -p "\$installDir"
              tar -xzf "\$linuxBuild" -C "\$installDir" --strip=1
            """
          }

          def nextWorker = 1

          def startTesting = {
            def workerNumber = nextWorker
            def lastConfigPath = null
            
            nextWorker++

            cStage('Copy Kibana') {
              withEnv """
                installDir="\$PARENT_DIR/install/kibana"
                destDir=\${installDir}-${workerNumber}
                cp -R "\$installDir" "\$destDir"
              """
            }

            def kibanaPort = "61${workerNumber}1"
            def esPort = "61${workerNumber}2"

            def portString = "TEST_KIBANA_URL=http://elastic:changeme@localhost:${kibanaPort} TEST_ES_URL=http://elastic:changeme@localhost:${esPort}"

            configPaths.each { configPath ->
              while(!xpackSuites[configPath].isEmpty()) {
                def testSuites = [xpackSuites[configPath].pop()].flatten()

                if (configPath != lastConfigPath) {
                  lastConfigPath = configPath

                  cStage('Launch Kibana/ES') {
                    withEnv """
                      cd "\$XPACK_DIR"

                      rm -f test-server-output-${workerNumber}.log
                      installDir="\$PARENT_DIR/install/kibana-${workerNumber}"
                      if [ -f runner-${workerNumber}.pid ]; then kill \$(cat runner-${workerNumber}.pid); sleep 5; fi
                      ${portString} nohup node scripts/functional_tests_server --config '${configPath}' --kibana-install-dir "\$installDir" > test-server-output-${workerNumber}.log & echo \$! > runner-${workerNumber}.pid
                    """

                    sleep 10 // TODO

                    sh "cat x-pack/test-server-output-${workerNumber}.log"

                    // TODO
                    waitTil {
                      // TODO also check for process still running
                      return sh (
                        script: "grep 'Elasticsearch and Kibana are ready' x-pack/test-server-output-${workerNumber}.log",
                        returnStatus: true
                      ) == 0
                    }

                    sh "cat x-pack/test-server-output-${workerNumber}.log"
                  }
                }

                def runTests = {
                  cStage("${workerNumber}: " + testSuites.join(", ")) {
                    withTestReporter {
                      def tagArgs = testSuites.collect { "--include-tag '${it}'" }.join(" ")

                      withEnv """
                        set -e
                        export TEST_BROWSER_HEADLESS=1

                        echo " -> Running functional and api tests"
                        cd "\$XPACK_DIR"

                        ${portString} checks-reporter-with-killswitch "X-Pack Chrome Functional tests / Group ${testSuites.join(", ")}" \
                          node ../scripts/functional_test_runner \
                            --debug --bail \
                            --config '${configPath}' \
                            ${tagArgs}

                        echo ""
                        echo ""

                        ${additionalScript ?: ''}
                      """
                    }
                  }
                }

                try {
                  runTests()
                } catch (ex) {
                  print "Error during xpack test suite: ${testSuite}"
                  print ex.toString()

                  try {
                    runTests()
                    unstable "xpack test succeeded on second attempt, considering flaky: ${testSuite}"
                    flakyTests << [type: "xpack", suite: testSuite]
                  } catch(innerException) {
                    print "Error during xpack test suite: ${testSuite}"
                    print innerException.toString()
                    failedTests << [type: "xpack", suite: testSuite]
                  }
                }
              }
            }
          }

          parallel([
            worker1: { startTesting() },
            worker2: { startTesting() },
            worker3: { startTesting() },
            worker4: { startTesting() },
            worker5: { startTesting() },
            worker6: { startTesting() },
            worker7: { startTesting() },
            worker8: { startTesting() },
          ])
        }
      }
    }
  }

  def withBootstrappedWorker = { closure ->
    node('linux && immutable && builds') {
      skipDefaultCheckout()

      env.HOME = env.JENKINS_HOME

      parallel([
        checkout: {
          cStage('Checkout') {
            def scmVars = checkout scm
            env.GIT_BRANCH = scmVars.GIT_BRANCH
          }
        },
        cache: {
          cStage('Bootstrap cache') {
            bash '''
              targetBranch="master"
              bootstrapCache="$HOME/.kibana/bootstrap_cache/$targetBranch.tar"

              ###
              ### Extract the bootstrap cache that we create in the packer_cache.sh script
              ###
              if [ -f "$bootstrapCache" ]; then
                mkdir -p ./bootstrap-cache
                echo "extracting bootstrap_cache from $bootstrapCache";
                tar -xf "$bootstrapCache" -C ./bootstrap-cache;
              fi
            '''
          }
          // cStage('Download archive') {
          //   step([
          //     $class: 'DownloadStep',
          //     credentialsId: 'kibana-ci-gcs-plugin',
          //     bucketUri: "gs://kibana-pipeline-testing/workspaces/latest/${workspaceArchiveFilename}",
          //     localDirectory: env.WORKSPACE
          //   ])
          // }
        }
      ])

      dir('./kibana') {
        // cStage('Extract archive') {
        //   bash "tar -xzf ../workspaces/latest/${workspaceArchiveFilename}"
        //   bash 'rm -rf /var/lib/jenkins/.kibana/node && mv var/lib/jenkins/.kibana/node /var/lib/jenkins/.kibana/'
        //   bash 'git reset --hard' # TODO account for .git itself
        // }

        cStage('Move bootstrap-cache') {
          bash 'cp -aTl ../bootstrap-cache/ ./'
        }
        
        cStage('setup.sh') {
          bash 'source src/dev/ci_setup/setup.sh'
        }

        cStage('Sibling ES') {
          bash 'rm -rf ../elasticsearch'
          bash 'source src/dev/ci_setup/setup_docker.sh; source src/dev/ci_setup/checkout_sibling_es.sh'
        }

        closure()
      }
    }
  }

  def buildOssKibana = {
    if (!RUN_OSS || ossKibanaBuildComplete) { return }

    withBootstrappedWorker {
      cStage('Build OSS Kibana') {
        cStage('Build OSS Kibana') {
          withEnv 'node scripts/build --debug --oss'
        }

        ossKibanaBuildComplete = true

        cStage('Archive workspace') {
          bash "touch ${ossWorkspaceArchiveFilename} && tar -czf ${ossWorkspaceArchiveFilename} --exclude=${ossWorkspaceArchiveFilename} . /var/lib/jenkins/.kibana/node"
        }

        cStage('Upload workspace') {
          step([
            $class: 'ClassicUploadStep',
            credentialsId: 'kibana-ci-gcs-plugin',
            bucket: "gs://kibana-pipeline-testing/workspaces/latest",
            pattern: ossWorkspaceArchiveFilename,
          ])
        }

        ossKibanaBuildUploaded = true
      }
    }
  }

  def buildDefaultKibana = {
    if (!RUN_DEFAULT || defaultKibanaBuildComplete) { return }

    withBootstrappedWorker {
      cStage('Build Default Kibana') {
        cStage('Build Default Kibana') {
          withEnv 'node scripts/build --debug --no-oss'
        }

        defaultKibanaBuildComplete = true

        cStage('Archive workspace') {
          bash "touch ${defaultWorkspaceArchiveFilename} && tar -czf ${defaultWorkspaceArchiveFilename} --exclude=${defaultWorkspaceArchiveFilename} . /var/lib/jenkins/.kibana/node"
        }

        cStage('Upload workspace') {
          step([
            $class: 'ClassicUploadStep',
            credentialsId: 'kibana-ci-gcs-plugin',
            bucket: "gs://kibana-pipeline-testing/workspaces/latest",
            pattern: defaultWorkspaceArchiveFilename,
          ])
        }

        defaultKibanaBuildUploaded = true
      }
    }
  }

  timestamps {
    ansiColor('xterm') {
      try {
        parallel([
          'Status': {
            // Update status of job, etc in a loop?
          },
          'Build OSS Kibana': { buildOssKibana() },
          'oss-testRunner1': ossCiGroupRunner(),
          'oss-testRunner2': ossCiGroupRunner(),
          'oss-testRunner3': ossCiGroupRunner(),
          'Build Default Kibana': { buildDefaultKibana() },
          'xpack-testRunner1': xpackCiGroupRunner(["test/reporting/configs/chromium_api.js", "test/reporting/configs/chromium_functional.js"]),
          'xpack-testRunner2-1': xpackCiGroupRunner(["test/functional/config.js"]),
          'xpack-testRunner2-2': xpackCiGroupRunner(["test/functional/config.js"]),
          'xpack-testRunner2-3': xpackCiGroupRunner(["test/functional/config.js"]),
          'xpack-testRunner3': xpackCiGroupRunner([
            "test/api_integration/config_security_basic.js",
            "test/plugin_api_integration/config.js",
            "test/kerberos_api_integration/config.ts",
            "test/saml_api_integration/config.js",
            "test/token_api_integration/config.js",
            "test/oidc_api_integration/config.ts",
            "test/oidc_api_integration/implicit_flow.config.ts",
            "test/spaces_api_integration/spaces_only/config.ts",
            "test/ui_capabilities/security_only/config.ts",
            "test/upgrade_assistant_integration/config.js",
          ]),
          'xpack-testRunner4': xpackCiGroupRunner([
            "test/api_integration/config.js",
            "test/alerting_api_integration/config_security_enabled.js",
            "test/spaces_api_integration/security_and_spaces/config_trial.ts",
            "test/saved_object_api_integration/security_and_spaces/config_trial.ts",
            "test/saved_object_api_integration/security_and_spaces/config_trial.ts",
            "test/saved_object_api_integration/security_and_spaces/config_basic.ts",
            "test/saved_object_api_integration/security_only/config_basic.ts",
          ]),
          // 'oss-intake': {
          //   withBootstrappedWorker {
          //     stage('OSS Intake') {
          //       withTestReporter {
          //         withEnv 'export TEST_BROWSER_HEADLESS=1; "$(FORCE_COLOR=0 yarn bin)/grunt" jenkins:unit --dev'
          //       }
          //     }
          //   }
          // },
          // 'default-intake': {
          //   withBootstrappedWorker {
          //     stage('Default Intake') {
          //       withTestReporter {
          //         withEnv '''
          //           export TEST_BROWSER_HEADLESS=1

          //           echo " -> Running mocha tests"
          //           cd "$XPACK_DIR"
          //           checks-reporter-with-killswitch "X-Pack Mocha" yarn test
          //           echo ""
          //           echo ""

          //           echo " -> Running jest tests"
          //           cd "$XPACK_DIR"
          //           checks-reporter-with-killswitch "X-Pack Jest" node scripts/jest --ci --verbose
          //           echo ""
          //           echo ""

          //           echo " -> Running SIEM cyclic dependency test"
          //           cd "$XPACK_DIR"
          //           checks-reporter-with-killswitch "X-Pack SIEM cyclic dependency test" node legacy/plugins/siem/scripts/check_circular_deps
          //           echo ""
          //           echo ""

          //           echo " -> Running jest contracts tests"
          //           cd "$XPACK_DIR"
          //           SLAPSHOT_ONLINE=true CONTRACT_ONLINE=true node scripts/jest_contract.js --ci --verbose
          //           echo ""
          //           echo ""
          //         '''
          //       }
          //     }
          //   }
          // },
        ])
      } finally {
        print "Failed Tests:"
        print failedTests

        print "Flaky Tests:"
        print flakyTests
      }
    }
  }
}

printStages(root)

def bash(script) {
  sh "#!/bin/bash -x\n${script}"
}

def withEnv(script){
  bash '''set -e

dir="$(pwd)"
cacheDir="${CACHE_DIR:-"$HOME/.kibana"}"

RED='\033[0;31m'
C_RESET='\033[0m' # Reset color

###
### Since the Jenkins logging output collector doesn't look like a TTY
### Node/Chalk and other color libs disable their color output. But Jenkins
### can handle color fine, so this forces https://github.com/chalk/supports-color
### to enable color support in Chalk and other related modules.
###
export FORCE_COLOR=1

# TODO
export NODE_OPTIONS="--max_old_space_size=2048"

###
### check that we seem to be in a kibana project
###
if [ -f "$dir/package.json" ] && [ -f "$dir/.node-version" ]; then
  echo "Setting up node.js and yarn in $dir"
else
  echo "${RED}src/dev/ci_setup/setup.sh must be run within a kibana repo${C_RESET}"
  exit 1
fi


export KIBANA_DIR="$dir"
export XPACK_DIR="$KIBANA_DIR/x-pack"

parentDir="$(cd "$KIBANA_DIR/.."; pwd)"
export PARENT_DIR="$parentDir"

kbnBranch="$(jq -r .branch "$KIBANA_DIR/package.json")"
export KIBANA_PKG_BRANCH="$kbnBranch"

UNAME=$(uname)
OS="linux"
if [[ "$UNAME" = *"MINGW64_NT"* ]]; then
  OS="win"
fi

nodeVersion="$(cat "$dir/.node-version")"
nodeDir="$cacheDir/node/$nodeVersion"

if [[ "$OS" == "win" ]]; then
  nodeBin="$HOME/node"
else
  nodeBin="$nodeDir/bin"
fi

export PATH="$nodeBin:$PATH"

yarnGlobalDir="$(yarn global bin || echo '')"
export PATH="$PATH:$yarnGlobalDir"

# use a proxy to fetch chromedriver/geckodriver asset
export GECKODRIVER_CDNURL="https://us-central1-elastic-kibana-184716.cloudfunctions.net/kibana-ci-proxy-cache"
export CHROMEDRIVER_CDNURL="https://us-central1-elastic-kibana-184716.cloudfunctions.net/kibana-ci-proxy-cache"

export CHECKS_REPORTER_ACTIVE=false

function checks-reporter-with-killswitch() {
  if [ "$CHECKS_REPORTER_ACTIVE" == "true" ] ; then
    yarn run github-checks-reporter "$@"
  else
    arguments=("$@");
    "${arguments[@]:1}";
  fi
}

''' + script
}

def withTestReporter(closure) {
  try {
    closure()
  } catch(ex) {
    reportFailedTests()
    throw ex
  }
}

def reportFailedTests() {
  withEnv 'node "$KIBANA_DIR/src/dev/failed_tests/cli"'
}

