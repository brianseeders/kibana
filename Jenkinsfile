#!/bin/groovy
// TODO is there a way to make this actually work with the Jenkins sandbox?

def getJobs() {
  def jobs = [:]
  (1..12).each { jobs["oss-ciGroup${it}"] = ossCiGroupRunner(it) }
  (1..10).each { jobs["xpack-ciGroup${it}"] = xpackCiGroupRunner(it) }

  return jobs
}

def failedTests = []

def workspaceArchiveFilename = 'workspace.archive.tar.gz'
def ossWorkspaceArchiveFilename = 'workspace-oss.archive.tar.gz'
def defaultWorkspaceArchiveFilename = 'workspace-default.archive.tar.gz'

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
  // "functional.apps.visualize.index",
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
    // "api_integration.apis.apm.feature_controls",
    // "api_integration.apis.beats.assign_tags_to_beats",
    // "api_integration.apis.beats.create_enrollment_tokens",
    // "api_integration.apis.beats.enroll_beat",
    // "api_integration.apis.beats.get_beat",
    // "api_integration.apis.beats.list_beats",
    // "api_integration.apis.beats.remove_tags_from_beats",
    // "api_integration.apis.beats.set_tag",
    // "api_integration.apis.beats.update_beat",
    // "api_integration.apis.code.feature_controls",
    // "api_integration.apis.code.repo_status",
    // "api_integration.apis.console.feature_controls",
    // "api_integration.apis.es.has_privileges",
    // "api_integration.apis.es.post_privileges",
    // "api_integration.apis.infra.feature_controls",
    // "api_integration.apis.infra.ip_to_hostname",
    // "api_integration.apis.infra.log_analysis",
    // "api_integration.apis.infra.log_entries",
    // "api_integration.apis.infra.log_entry_highlights",
    // "api_integration.apis.infra.log_item",
    // "api_integration.apis.infra.log_summary",
    // "api_integration.apis.infra.logs_without_millis",
    // "api_integration.apis.infra.metadata",
    // "api_integration.apis.infra.metrics_explorer",
    // "api_integration.apis.infra.sources",
    // "api_integration.apis.infra.waffle",
    // "api_integration.apis.kibana.kql_telemetry.kql_telemetry",
    // "api_integration.apis.kibana.stats.stats",
    // "api_integration.apis.logstash.cluster.load",
    // "api_integration.apis.logstash.pipeline.delete",
    // "api_integration.apis.logstash.pipeline.load",
    // "api_integration.apis.logstash.pipeline.save",
    // "api_integration.apis.logstash.pipelines.delete",
    // "api_integration.apis.logstash.pipelines.list",
    // "api_integration.apis.management.advanced_settings.feature_controls",
    // "api_integration.apis.management.cross_cluster_replication.auto_follow_pattern",
    // "api_integration.apis.management.cross_cluster_replication.follower_indices",
    // "api_integration.apis.management.index_lifecycle_management.indices",
    // "api_integration.apis.management.index_lifecycle_management.nodes",
    // "api_integration.apis.management.index_lifecycle_management.policies",
    // "api_integration.apis.management.index_lifecycle_management.templates",
    // "api_integration.apis.management.index_management.indices",
    // "api_integration.apis.management.index_management.mapping",
    // "api_integration.apis.management.index_management.settings",
    // "api_integration.apis.management.index_management.stats",
    // "api_integration.apis.management.index_management.templates",
    // "api_integration.apis.management.remote_clusters.remote_clusters",
    // "api_integration.apis.management.rollup.index_patterns_extensions",
    // "api_integration.apis.management.rollup.rollup_search",
    // "api_integration.apis.maps.migrations",
    // "api_integration.apis.monitoring.apm.instances",
    // "api_integration.apis.monitoring.apm.overview",
    // "api_integration.apis.monitoring.beats.detail",
    // "api_integration.apis.monitoring.beats.list",
    // "api_integration.apis.monitoring.beats.overview",
    // "api_integration.apis.monitoring.cluster.list",
    // "api_integration.apis.monitoring.cluster.overview",
    // "api_integration.apis.monitoring.common.mappings_exist",
    // "api_integration.apis.monitoring.elasticsearch.ccr_shard",
    // "api_integration.apis.monitoring.elasticsearch.index_detail",
    // "api_integration.apis.monitoring.elasticsearch.indices",
    // "api_integration.apis.monitoring.elasticsearch.node_detail_advanced",
    // "api_integration.apis.monitoring.elasticsearch.nodes",
    // "api_integration.apis.monitoring.elasticsearch.overview",
    // "api_integration.apis.monitoring.elasticsearch_settings.check_cluster",
    // "api_integration.apis.monitoring.elasticsearch_settings.check_nodes",
    // "api_integration.apis.monitoring.elasticsearch_settings.set_collection_enabled",
    // "api_integration.apis.monitoring.elasticsearch_settings.set_collection_interval",
    // "api_integration.apis.monitoring.kibana.instance",
    // "api_integration.apis.monitoring.kibana.listing",
    // "api_integration.apis.monitoring.kibana.overview",
    // "api_integration.apis.monitoring.logs.cluster",
    // "api_integration.apis.monitoring.logs.index_detail",
    // "api_integration.apis.monitoring.logs.node_detail",
    // "api_integration.apis.monitoring.logstash.node_detail",
    // "api_integration.apis.monitoring.logstash.nodes",
    // "api_integration.apis.monitoring.logstash.overview",
    // "api_integration.apis.monitoring.setup.collection.detect_apm",
    // "api_integration.apis.monitoring.setup.collection.detect_beats_management",
    // "api_integration.apis.monitoring.setup.collection.detect_logstash_management",
    // "api_integration.apis.monitoring.setup.collection.es_and_kibana_exclusive_mb",
    // "api_integration.apis.monitoring.setup.collection.es_and_kibana_mb",
    // "api_integration.apis.monitoring.setup.collection.kibana_exclusive_mb",
    // "api_integration.apis.monitoring.setup.collection.kibana_mb",
    // "api_integration.apis.monitoring.standalone_cluster.clusters",
    // "api_integration.apis.security.basic_login",
    // "api_integration.apis.security.builtin_es_privileges",
    // "api_integration.apis.security.index_fields",
    // "api_integration.apis.security.privileges",
    // "api_integration.apis.security.roles",
    // "api_integration.apis.short_urls.feature_controls",
    // "api_integration.apis.siem.authentications",
    // "api_integration.apis.siem.domains",
    // "api_integration.apis.siem.events",
    // "api_integration.apis.siem.feature_controls",
    // "api_integration.apis.siem.hosts",
    // "api_integration.apis.siem.kpi_hosts",
    // "api_integration.apis.siem.kpi_network",
    // "api_integration.apis.siem.network_dns",
    // "api_integration.apis.siem.network_top_n_flow",
    // "api_integration.apis.siem.overview_host",
    // "api_integration.apis.siem.overview_network",
    // "api_integration.apis.siem.saved_objects.notes",
    // "api_integration.apis.siem.saved_objects.pinned_events",
    // "api_integration.apis.siem.saved_objects.timeline",
    // "api_integration.apis.siem.sources",
    // "api_integration.apis.siem.timeline_details",
    // "api_integration.apis.siem.tls",
    // "api_integration.apis.siem.uncommon_processes",
    // "api_integration.apis.siem.users",
    // "api_integration.apis.spaces.saved_objects",
    // "api_integration.apis.telemetry.telemetry_local",
    // "api_integration.apis.uptime.feature_controls",
    // "api_integration.apis.uptime.get_all_pings",
    // "api_integration.apis.uptime.graphql.doc_count",
    // "api_integration.apis.uptime.graphql.error_list",
    // "api_integration.apis.uptime.graphql.filter_bar",
    // "api_integration.apis.uptime.graphql.monitor_charts",
    // "api_integration.apis.uptime.graphql.monitor_list",
    // "api_integration.apis.uptime.graphql.monitor_page_title",
    // "api_integration.apis.uptime.graphql.monitor_states",
    // "api_integration.apis.uptime.graphql.monitor_status_bar",
    // "api_integration.apis.uptime.graphql.ping_list",
    // "api_integration.apis.uptime.graphql.snapshot_histogram",
    // "api_integration.apis.uptime.location",
    // "api_integration.apis.xpack_main.features.features",
    // "api_integration.apis.xpack_main.settings.settings",
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
    "spaces_api_integration.security_and_spaces.apis.update"
  ],
  "test/spaces_api_integration/security_and_spaces/config_basic.ts": [
    "spaces_api_integration.security_and_spaces.apis.create",
    "spaces_api_integration.security_and_spaces.apis.delete",
    "spaces_api_integration.security_and_spaces.apis.get_all",
    "spaces_api_integration.security_and_spaces.apis.select",
    "spaces_api_integration.security_and_spaces.apis.update"
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

// TODO - temporary stage wrapper
def cStage(name, closure) {
  print "Stage: ${name}"
  closure()
}

def ossKibanaBuildComplete = false
def ossKibanaBuildUploaded = false

def defaultKibanaBuildComplete = false
def defaultKibanaBuildUploaded = false

def waitTil(conditionClosure) {
  for(def i = 0; i < 50000 && !conditionClosure(); i++) {
    sleep 10
  }
}

def ossCiGroupRunner = { additionalScript='' ->
  return {
    cStage("oss-testRunner") {
      sleep 360

      waitTil { ossKibanaBuildComplete }
      // TODO need to move functionalTests:ensureAllTestsInCiGroup to before the build
      node('linux && immutable && tests') {
        skipDefaultCheckout()

        env.HOME = env.JENKINS_HOME

        sleep 150

        waitTil { ossKibanaBuildUploaded }

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

          nohup yarn test:ui:server --kibana-install-dir "\$installDir" > test-server-output.log &
        """

        sleep 60 // TODO

        while(!ossTestSuites.isEmpty()) {
          def testSuite = ossTestSuites.pop()

          try {
            cStage("oss-ciGroup-${testSuite}") {
              withTestReporter {
                withEnv """
                  set -e
                  export CI_GROUP=${testSuite}
                  export TEST_BROWSER_HEADLESS=1

                  cd "\$KIBANA_DIR"

                  checks-reporter-with-killswitch "Functional tests / Group ${testSuite}" \
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
          } catch (ex) {
            print "Error during oss test suite: ${testSuite}"
            print ex.toString()
          }
        }
      }
    }
  }
}

def xpackCiGroupRunner = { primaryConfigPaths, additionalScript='' ->
  return {
    def configPaths = primaryConfigPaths + (xpackSuites.keySet() as String[]);
    def lastConfigPath = null

    cStage("xpack-testRunner") {
      // TODO need to move 'Ensuring all functional tests are in a ciGroup' to before the build

      sleep 840

      waitTil { defaultKibanaBuildComplete }

      node('linux && immutable && tests') {
        skipDefaultCheckout()

        env.HOME = env.JENKINS_HOME

        sleep 150

        waitTil { defaultKibanaBuildUploaded }

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

        configPaths.each { configPath ->
          while(!xpackSuites[configPath].isEmpty()) {
            def testSuite = xpackSuites[configPath].pop()

            if (configPath != lastConfigPath) {
              lastConfigPath = configPath
              
              withEnv """
                cd "\$XPACK_DIR"

                rm -f test-server-output.log
                installDir="\$PARENT_DIR/install/kibana"
                if [ -f runner.pid ]; then kill \$(cat runner.pid); sleep 5; fi
                nohup node scripts/functional_tests_server --config '${configPath}' --kibana-install-dir "\$installDir" > test-server-output.log & echo \$! > runner.pid
              """

              sleep 10 // TODO

              sh 'cat x-pack/test-server-output.log'

              // TODO
              waitTil {
                // TODO also check for process still running
                return sh (
                  script: 'grep "Elasticsearch and Kibana are ready" x-pack/test-server-output.log',
                  returnStatus: true
                ) == 0
              }
            }

            try {
              cStage("xpack-ciGroup-${testSuite}") {
                withTestReporter {
                  withEnv """
                    set -e
                    export CI_GROUP=${testSuite}
                    export TEST_BROWSER_HEADLESS=1

                    echo " -> Running functional and api tests"
                    cd "\$XPACK_DIR"

                    checks-reporter-with-killswitch "X-Pack Chrome Functional tests / Group ${testSuite}" \
                      node ../scripts/functional_test_runner \
                        --debug --bail \
                        --config '${configPath}' \
                        --include-tag '${testSuite}'

                    echo ""
                    echo ""

                    ${additionalScript ?: ''}
                  """
                }
              }
            } catch (ex) {
              print "Error during xpack test suite: ${testSuite}"
              print ex.toString()

              failedTests << [config: configPath, suite: testSuite, error: ex]
            }
          }
        }
      }
    }
  }
}

def buildOssKibana = {
  withBootstrappedWorker {
    stage('Build OSS Kibana') {
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
  withBootstrappedWorker {
    stage('Build Default Kibana') {
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
        'Build OSS Kibana': { buildOssKibana() },
        'oss-testRunner1': ossCiGroupRunner(),
        'oss-testRunner2': ossCiGroupRunner(),
        'oss-testRunner3': ossCiGroupRunner(),
        'oss-testRunner4': ossCiGroupRunner(),
        'oss-testRunner5': ossCiGroupRunner(),
        'oss-testRunner6': ossCiGroupRunner(),
        'oss-testRunner7': ossCiGroupRunner(),
        'oss-testRunner8': ossCiGroupRunner(),
        'oss-testRunner9': ossCiGroupRunner(),
        'oss-testRunner10': ossCiGroupRunner(),
        'oss-testRunner11': ossCiGroupRunner(),
        'oss-testRunner12': ossCiGroupRunner(),
        'oss-testRunner13': ossCiGroupRunner(),
        'oss-testRunner14': ossCiGroupRunner(),
        'oss-testRunner15': ossCiGroupRunner(),
        'oss-testRunner16': ossCiGroupRunner(),
        'oss-testRunner17': ossCiGroupRunner(),
        'oss-testRunner18': ossCiGroupRunner(),
        'oss-testRunner19': ossCiGroupRunner(),
        'oss-testRunner20': ossCiGroupRunner(),
        'Build Default Kibana': { buildDefaultKibana() },
        // 'xpack-testRunner1': xpackCiGroupRunner(["test/reporting/configs/chromium_api.js"]),
        // 'xpack-testRunner2': xpackCiGroupRunner(["test/reporting/configs/chromium_functional.js"]),
        // 'xpack-testRunner3': xpackCiGroupRunner(["test/reporting/configs/generate_api.js"]), //-
        // 'xpack-testRunner4': xpackCiGroupRunner(["test/functional/config.js"]), // //
        // 'xpack-testRunner5': xpackCiGroupRunner(["test/api_integration/config_security_basic.js"]), //-
        // 'xpack-testRunner6': xpackCiGroupRunner(["test/api_integration/config.js"]), //
        // 'xpack-testRunner6-2': xpackCiGroupRunner(["test/api_integration/config.js"]), // 
        // 'xpack-testRunner6-3': xpackCiGroupRunner(["test/api_integration/config.js"]), //
        // 'xpack-testRunner7': xpackCiGroupRunner(["test/alerting_api_integration/config_security_enabled.js"]), //
        // 'xpack-testRunner8': xpackCiGroupRunner(["test/plugin_api_integration/config.js"]), //-
        // 'xpack-testRunner9': xpackCiGroupRunner(["test/kerberos_api_integration/config.ts"]), //-
        // 'xpack-testRunner10': xpackCiGroupRunner(["test/kerberos_api_integration/anonymous_access.config.ts"]),
        // 'xpack-testRunner11': xpackCiGroupRunner(["test/saml_api_integration/config.js"]), //-
        // 'xpack-testRunner12': xpackCiGroupRunner(["test/token_api_integration/config.js"]), //-
        // 'xpack-testRunner13': xpackCiGroupRunner(["test/oidc_api_integration/config.ts"]), //-
        // 'xpack-testRunner14': xpackCiGroupRunner(["test/oidc_api_integration/implicit_flow.config.ts"]), //-
        // 'xpack-testRunner16': xpackCiGroupRunner(["test/spaces_api_integration/spaces_only/config.ts"]), //-
        // 'xpack-testRunner17': xpackCiGroupRunner(["test/spaces_api_integration/security_and_spaces/config_trial.ts"]), //
        // 'xpack-testRunner18': xpackCiGroupRunner(["test/spaces_api_integration/security_and_spaces/config_basic.ts"]),
        // 'xpack-testRunner19': xpackCiGroupRunner(["test/saved_object_api_integration/security_and_spaces/config_trial.ts"]), //
        // 'xpack-testRunner19-2': xpackCiGroupRunner(["test/saved_object_api_integration/security_and_spaces/config_trial.ts"]), //
        // 'xpack-testRunner20': xpackCiGroupRunner(["test/saved_object_api_integration/security_and_spaces/config_basic.ts"]),// 
        // 'xpack-testRunner21': xpackCiGroupRunner(["test/saved_object_api_integration/security_only/config_trial.ts"]),
        // 'xpack-testRunner22': xpackCiGroupRunner(["test/saved_object_api_integration/security_only/config_basic.ts"]), //
        // 'xpack-testRunner23': xpackCiGroupRunner(["test/saved_object_api_integration/spaces_only/config.ts"]),
        // 'xpack-testRunner24': xpackCiGroupRunner(["test/ui_capabilities/security_and_spaces/config.ts"]),
        // 'xpack-testRunner25': xpackCiGroupRunner(["test/ui_capabilities/security_only/config.ts"]), //-
        // 'xpack-testRunner26': xpackCiGroupRunner(["test/ui_capabilities/spaces_only/config.ts"]),
        // 'xpack-testRunner27': xpackCiGroupRunner(["test/upgrade_assistant_integration/config.js"]), //-
        // 'xpack-testRunner28': xpackCiGroupRunner(["test/functional/config.js"]),
        // 'xpack-testRunner29': xpackCiGroupRunner(["test/functional/config.js"]),
        // 'xpack-testRunner30': xpackCiGroupRunner(["test/functional/config.js"]),
        'xpack-testRunner1': xpackCiGroupRunner(["test/reporting/configs/chromium_api.js"]),
        'xpack-testRunner2': xpackCiGroupRunner(["test/reporting/configs/chromium_functional.js"]),
        'xpack-testRunner3': xpackCiGroupRunner(["test/reporting/configs/generate_api.js"]), //-
        'xpack-testRunner4': xpackCiGroupRunner(["test/functional/config.js"]),
        'xpack-testRunner4-2': xpackCiGroupRunner(["test/functional/config.js"]),
        'xpack-testRunner4-3': xpackCiGroupRunner(["test/functional/config.js"]),
        'xpack-testRunner4-4': xpackCiGroupRunner(["test/functional/config.js"]),
        'xpack-testRunner4-5': xpackCiGroupRunner(["test/functional/config.js"]),
        'xpack-testRunner4-6': xpackCiGroupRunner(["test/functional/config.js"]),
        'xpack-testRunner4-7': xpackCiGroupRunner(["test/functional/config.js"]),
        'xpack-testRunner4-8': xpackCiGroupRunner(["test/functional/config.js"]),
        'xpack-testRunner4-9': xpackCiGroupRunner(["test/functional/config.js"]),
        'xpack-testRunner4-10': xpackCiGroupRunner(["test/functional/config.js"]),
        'xpack-testRunner4-11': xpackCiGroupRunner(["test/functional/config.js"]),
        'xpack-testRunner4-12': xpackCiGroupRunner(["test/functional/config.js"]),
        'xpack-testRunner4-13': xpackCiGroupRunner(["test/functional/config.js"]),
        'xpack-testRunner4-14': xpackCiGroupRunner(["test/functional/config.js"]),
        'xpack-testRunner4-15': xpackCiGroupRunner(["test/functional/config.js"]),
        'xpack-testRunner5': xpackCiGroupRunner([
          "test/api_integration/config_security_basic.js",
          "test/plugin_api_integration/config.js",
          "test/kerberos_api_integration/config.ts",
          "test/saml_api_integration/config.js",
          "test/token_api_integration/config.js",
        ]),
        'xpack-testRunner5-2': xpackCiGroupRunner([
          "test/oidc_api_integration/config.ts",
          "test/oidc_api_integration/implicit_flow.config.ts",
          "test/spaces_api_integration/spaces_only/config.ts",
          "test/ui_capabilities/security_only/config.ts",
          "test/upgrade_assistant_integration/config.js",
        ]), //-
        'xpack-testRunner6': xpackCiGroupRunner(["test/api_integration/config.js"]), //
        'xpack-testRunner6-2': xpackCiGroupRunner(["test/api_integration/config.js"]), // 
        'xpack-testRunner6-3': xpackCiGroupRunner(["test/api_integration/config.js"]), //
        'xpack-testRunner7': xpackCiGroupRunner(["test/alerting_api_integration/config_security_enabled.js"]), //
        // 'xpack-testRunner8': xpackCiGroupRunner([]), //-
        // 'xpack-testRunner9': xpackCiGroupRunner(["test/kerberos_api_integration/config.ts"]), //-
        'xpack-testRunner10': xpackCiGroupRunner(["test/kerberos_api_integration/anonymous_access.config.ts"]),
        // 'xpack-testRunner11': xpackCiGroupRunner([]), //-
        // 'xpack-testRunner12': xpackCiGroupRunner([]), //-
        // 'xpack-testRunner13': xpackCiGroupRunner([]), //-
        // 'xpack-testRunner14': xpackCiGroupRunner([]), //-
        // 'xpack-testRunner16': xpackCiGroupRunner([]), //-
        'xpack-testRunner17': xpackCiGroupRunner(["test/spaces_api_integration/security_and_spaces/config_trial.ts"]), //
        'xpack-testRunner18': xpackCiGroupRunner(["test/spaces_api_integration/security_and_spaces/config_basic.ts"]),
        'xpack-testRunner19': xpackCiGroupRunner(["test/saved_object_api_integration/security_and_spaces/config_trial.ts"]), //
        'xpack-testRunner19-2': xpackCiGroupRunner(["test/saved_object_api_integration/security_and_spaces/config_trial.ts"]), //
        'xpack-testRunner20': xpackCiGroupRunner(["test/saved_object_api_integration/security_and_spaces/config_basic.ts"]),// 
        'xpack-testRunner21': xpackCiGroupRunner(["test/saved_object_api_integration/security_only/config_trial.ts"]),
        'xpack-testRunner22': xpackCiGroupRunner(["test/saved_object_api_integration/security_only/config_basic.ts"]), //
        'xpack-testRunner23': xpackCiGroupRunner(["test/saved_object_api_integration/spaces_only/config.ts"]),
        'xpack-testRunner24': xpackCiGroupRunner(["test/ui_capabilities/security_and_spaces/config.ts"]),
        // 'xpack-testRunner25': xpackCiGroupRunner([]), //-
        'xpack-testRunner26': xpackCiGroupRunner(["test/ui_capabilities/spaces_only/config.ts"]),
        // 'xpack-testRunner27': xpackCiGroupRunner([]), //-
        // 'xpack-testRunner28': xpackCiGroupRunner(["test/functional/config.js"]),
        // 'xpack-testRunner29': xpackCiGroupRunner(["test/functional/config.js"]),
        // 'xpack-testRunner30': xpackCiGroupRunner(["test/functional/config.js"]),
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
      print failedTests
    }
  }
}

def withBootstrappedWorker(closure) {
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

