#!/bin/groovy
// TODO is there a way to make this actually work with the Jenkins sandbox?

def getJobs() {
  def jobs = [:]
  (1..12).each { jobs["oss-ciGroup${it}"] = ossCiGroupRunner(it) }
  (1..10).each { jobs["xpack-ciGroup${it}"] = xpackCiGroupRunner(it) }

  return jobs
}

def workspaceArchiveFilename = 'workspace.archive.tar.gz'
def ossWorkspaceArchiveFilename = 'workspace-oss.archive.tar.gz'
def defaultWorkspaceArchiveFilename = 'workspace-default.archive.tar.gz'

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

def ossCiGroupRunner = { testSuite, additionalScript='' ->
  return {
    cStage("oss-ciGroup-${testSuite}") {
      waitTil { ossKibanaBuildComplete }
      // TODO need to move functionalTests:ensureAllTestsInCiGroup to before the build
      node('linux && immutable && tests') {
        skipDefaultCheckout()

        env.HOME = env.JENKINS_HOME

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

        stage("oss-ciGroup-${testSuite}") {
          withTestReporter {
            withEnv """
              set -e
              export CI_GROUP=${testSuite}
              export TEST_BROWSER_HEADLESS=1

              echo " -> extracting default Kibana distributable for use in functional tests"
              cd "\$KIBANA_DIR"
              linuxBuild="\$(find "\$KIBANA_DIR/target" -name 'kibana-*-linux-x86_64.tar.gz')"
              installDir="\$PARENT_DIR/install/kibana"
              mkdir -p "\$installDir"
              tar -xzf "\$linuxBuild" -C "\$installDir" --strip=1

              checks-reporter-with-killswitch "Functional tests / Group ${testSuite}" \
                node scripts/functional_tests \
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
    }
  }
}

def xpackCiGroupRunner = { testSuite, additionalScript='' ->
  return {
    cStage("xpack-ciGroup-${testSuite}") {
      // TODO need to move 'Ensuring all functional tests are in a ciGroup' to before the build

      waitTil { defaultKibanaBuildComplete }

      node('linux && immutable && tests') {
        skipDefaultCheckout()

        env.HOME = env.JENKINS_HOME 

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

        stage("xpack-ciGroup-${testSuite}") {
          withTestReporter {
            withEnv """
              set -e
              export CI_GROUP=${testSuite}
              export TEST_BROWSER_HEADLESS=1

              cd "\$XPACK_DIR"

              echo " -> Ensuring all functional tests are in a ciGroup"
              cd "\$XPACK_DIR"
              node scripts/functional_tests --assert-none-excluded \
                --include-tag ciGroup1 \
                --include-tag ciGroup2 \
                --include-tag ciGroup3 \
                --include-tag ciGroup4 \
                --include-tag ciGroup5 \
                --include-tag ciGroup6 \
                --include-tag ciGroup7 \
                --include-tag ciGroup8 \
                --include-tag ciGroup9 \
                --include-tag ciGroup10

              echo " -> extracting default Kibana distributable for use in functional tests"
              cd "\$KIBANA_DIR"
              linuxBuild="\$(find "\$KIBANA_DIR/target" -name 'kibana-*-linux-x86_64.tar.gz')"
              installDir="\$PARENT_DIR/install/kibana"
              mkdir -p "\$installDir"
              tar -xzf "\$linuxBuild" -C "\$installDir" --strip=1

              echo " -> Running functional and api tests"
              cd "\$XPACK_DIR"

              checks-reporter-with-killswitch "X-Pack Chrome Functional tests / Group ${testSuite}" \
                node scripts/functional_tests \
                  --debug --bail \
                  --kibana-install-dir "\$installDir" \
                  --include-tag '${testSuite}'

              echo ""
              echo ""

              ${additionalScript ?: ''}
            """
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
    parallel([
      'Build OSS Kibana': { buildOssKibana() },
      'oss-ciGroup-functional.apps.console': ossCiGroupRunner('functional.apps.console'),
      'oss-ciGroup-functional.apps.getting_started': ossCiGroupRunner('functional.apps.getting_started'),
      'oss-ciGroup-functional.apps.context': ossCiGroupRunner('functional.apps.context'),
      // 'oss-ciGroup-functional.apps.dashboard': ossCiGroupRunner('functional.apps.dashboard'),
      'oss-ciGroup-functional.apps.dashboard.empty_dashboard': ossCiGroupRunner('functional.apps.dashboard.empty_dashboard'),
      'oss-ciGroup-functional.apps.dashboard.embeddable_rendering': ossCiGroupRunner('functional.apps.dashboard.embeddable_rendering'),
      'oss-ciGroup-functional.apps.dashboard.create_and_add_embeddables': ossCiGroupRunner('functional.apps.dashboard.create_and_add_embeddables'),
      'oss-ciGroup-functional.apps.dashboard.time_zones': ossCiGroupRunner('functional.apps.dashboard.time_zones'),
      'oss-ciGroup-functional.apps.dashboard.dashboard_options': ossCiGroupRunner('functional.apps.dashboard.dashboard_options'),
      'oss-ciGroup-functional.apps.dashboard.data_shared_attributes': ossCiGroupRunner('functional.apps.dashboard.data_shared_attributes'),
      'oss-ciGroup-functional.apps.dashboard.embed_mode': ossCiGroupRunner('functional.apps.dashboard.embed_mode'),
      'oss-ciGroup-functional.apps.dashboard.dashboard_query_bar': ossCiGroupRunner('functional.apps.dashboard.dashboard_query_bar'),
      'oss-ciGroup-functional.apps.dashboard.full_screen_mode': ossCiGroupRunner('functional.apps.dashboard.full_screen_mode'),
      'oss-ciGroup-functional.apps.dashboard.dashboard_filter_bar': ossCiGroupRunner('functional.apps.dashboard.dashboard_filter_bar'),
      'oss-ciGroup-functional.apps.dashboard.dashboard_filtering': ossCiGroupRunner('functional.apps.dashboard.dashboard_filtering'),
      'oss-ciGroup-functional.apps.dashboard.panel_expand_toggle': ossCiGroupRunner('functional.apps.dashboard.panel_expand_toggle'),
      'oss-ciGroup-functional.apps.dashboard.dashboard_grid': ossCiGroupRunner('functional.apps.dashboard.dashboard_grid'),
      'oss-ciGroup-functional.apps.dashboard.dashboard_snapshots': ossCiGroupRunner('functional.apps.dashboard.dashboard_snapshots'),
      'oss-ciGroup-functional.apps.dashboard.view_edit': ossCiGroupRunner('functional.apps.dashboard.view_edit'),
      'oss-ciGroup-functional.apps.dashboard.dashboard_time_picker': ossCiGroupRunner('functional.apps.dashboard.dashboard_time_picker'),
      'oss-ciGroup-functional.apps.dashboard.bwc_shared_urls': ossCiGroupRunner('functional.apps.dashboard.bwc_shared_urls'),
      'oss-ciGroup-functional.apps.dashboard.panel_controls': ossCiGroupRunner('functional.apps.dashboard.panel_controls'),
      'oss-ciGroup-functional.apps.dashboard.dashboard_state': ossCiGroupRunner('functional.apps.dashboard.dashboard_state'),
      'oss-ciGroup-functional.apps.dashboard.dashboard_save': ossCiGroupRunner('functional.apps.dashboard.dashboard_save'),
      'oss-ciGroup-functional.apps.dashboard.dashboard_time': ossCiGroupRunner('functional.apps.dashboard.dashboard_time'),
      'oss-ciGroup-functional.apps.dashboard.dashboard_listing': ossCiGroupRunner('functional.apps.dashboard.dashboard_listing'),
      'oss-ciGroup-functional.apps.dashboard.dashboard_clone': ossCiGroupRunner('functional.apps.dashboard.dashboard_clone'),
      'oss-ciGroup-functional.apps.discover': ossCiGroupRunner('functional.apps.discover'),
      'oss-ciGroup-functional.apps.home': ossCiGroupRunner('functional.apps.home'),
      // 'oss-ciGroup-functional.apps.management': ossCiGroupRunner('functional.apps.management'),
      'oss-ciGroup-functional.apps.management._create_index_pattern_wizard': ossCiGroupRunner('functional.apps.management._create_index_pattern_wizard'),
      'oss-ciGroup-functional.apps.management._index_pattern_create_delete': ossCiGroupRunner('functional.apps.management._index_pattern_create_delete'),
      'oss-ciGroup-functional.apps.management._index_pattern_results_sort': ossCiGroupRunner('functional.apps.management._index_pattern_results_sort'),
      'oss-ciGroup-functional.apps.management._index_pattern_popularity': ossCiGroupRunner('functional.apps.management._index_pattern_popularity'),
      'oss-ciGroup-functional.apps.management._kibana_settings': ossCiGroupRunner('functional.apps.management._kibana_settings'),
      'oss-ciGroup-functional.apps.management._scripted_fields': ossCiGroupRunner('functional.apps.management._scripted_fields'),
      'oss-ciGroup-functional.apps.management._scripted_fields_preview': ossCiGroupRunner('functional.apps.management._scripted_fields_preview'),
      'oss-ciGroup-functional.apps.management._index_pattern_filter': ossCiGroupRunner('functional.apps.management._index_pattern_filter'),
      'oss-ciGroup-functional.apps.management._scripted_fields_filter': ossCiGroupRunner('functional.apps.management._scripted_fields_filter'),
      'oss-ciGroup-functional.apps.management._import_objects': ossCiGroupRunner('functional.apps.management._import_objects'),
      'oss-ciGroup-functional.apps.management._test_huge_fields': ossCiGroupRunner('functional.apps.management._test_huge_fields'),
      'oss-ciGroup-functional.apps.management._handle_alias': ossCiGroupRunner('functional.apps.management._handle_alias'),
      'oss-ciGroup-functional.apps.management._handle_version_conflict': ossCiGroupRunner('functional.apps.management._handle_version_conflict'),
      'oss-ciGroup-functional.apps.status_page': ossCiGroupRunner('functional.apps.status_page'),
      'oss-ciGroup-functional.apps.timelion': ossCiGroupRunner('functional.apps.timelion'),
      'oss-ciGroup-api_integration.apis': ossCiGroupRunner('api_integration.apis'),
      // 'oss-ciGroup-api_integration.apis.elasticsearch': ossCiGroupRunner('api_integration.apis.elasticsearch'),
      // 'oss-ciGroup-api_integration.apis.general': ossCiGroupRunner('api_integration.apis.general'),
      // 'oss-ciGroup-api_integration.apis.home': ossCiGroupRunner('api_integration.apis.home'),
      // 'oss-ciGroup-api_integration.apis.index_patterns': ossCiGroupRunner('api_integration.apis.index_patterns'),
      // 'oss-ciGroup-api_integration.apis.kql_telemetry': ossCiGroupRunner('api_integration.apis.kql_telemetry'),
      // 'oss-ciGroup-api_integration.apis.management': ossCiGroupRunner('api_integration.apis.management'),
      // 'oss-ciGroup-api_integration.apis.saved_objects': ossCiGroupRunner('api_integration.apis.saved_objects'),
      // 'oss-ciGroup-api_integration.apis.scripts': ossCiGroupRunner('api_integration.apis.scripts'),
      // 'oss-ciGroup-api_integration.apis.search': ossCiGroupRunner('api_integration.apis.search'),
      // 'oss-ciGroup-api_integration.apis.shorten': ossCiGroupRunner('api_integration.apis.shorten'),
      // 'oss-ciGroup-api_integration.apis.suggestions': ossCiGroupRunner('api_integration.apis.suggestions'),
      // 'oss-ciGroup-api_integration.apis.status': ossCiGroupRunner('api_integration.apis.status'),
      // 'oss-ciGroup-api_integration.apis.stats': ossCiGroupRunner('api_integration.apis.stats'),
      // 'oss-ciGroup-api_integration.apis.ui_metric': ossCiGroupRunner('api_integration.apis.ui_metric'),
      'oss-ciGroup-plugin_functional.app_plugins.app_navigation': ossCiGroupRunner('plugin_functional.app_plugins.app_navigation'),
      'oss-ciGroup-plugin_functional.custom_visualizations.self_changing_vis': ossCiGroupRunner('plugin_functional.custom_visualizations.self_changing_vis'),
      'oss-ciGroup-plugin_functional.embedding_visualizations.embed_by_id': ossCiGroupRunner('plugin_functional.embedding_visualizations.embed_by_id'),
      'oss-ciGroup-plugin_functional.panel_actions.panel_actions': ossCiGroupRunner('plugin_functional.panel_actions.panel_actions'),
      'oss-ciGroup-plugin_functional.core_plugins.ui_plugins': ossCiGroupRunner('plugin_functional.core_plugins.ui_plugins'),
      'oss-ciGroup-interpreter_functional.run_pipeline.basic': ossCiGroupRunner('interpreter_functional.run_pipeline.basic'),
      'oss-ciGroup-interpreter_functional.run_pipeline.tag_cloud': ossCiGroupRunner('interpreter_functional.run_pipeline.tag_cloud'),
      'oss-ciGroup-interpreter_functional.run_pipeline.metric': ossCiGroupRunner('interpreter_functional.run_pipeline.metric'),
      // 'oss-ciGroup1': ossCiGroupRunner(1),
      // 'oss-ciGroup2': ossCiGroupRunner(2),
      // 'oss-ciGroup3': ossCiGroupRunner(3),
      // 'oss-ciGroup4': ossCiGroupRunner(4),
      // 'oss-ciGroup5': ossCiGroupRunner(5),
      // 'oss-ciGroup6': ossCiGroupRunner(6),
      // 'oss-ciGroup7': ossCiGroupRunner(7),
      // 'oss-ciGroup8': ossCiGroupRunner(8),
      // 'oss-ciGroup9': ossCiGroupRunner(9),
      // 'oss-ciGroup10': ossCiGroupRunner(10),
      // 'oss-ciGroup11': ossCiGroupRunner(11),
      // 'oss-ciGroup12': ossCiGroupRunner(12),
      'Build Default Kibana': { buildDefaultKibana() },
      'xpack-ciGroup-reporting.api.chromium_tests': xpackCiGroupRunner('reporting.api.chromium_tests'),
      'xpack-ciGroup-reporting.api.bwc_existing_indexes': xpackCiGroupRunner('reporting.api.bwc_existing_indexes'),
      'xpack-ciGroup-reporting.api.bwc_generation_urls': xpackCiGroupRunner('reporting.api.bwc_generation_urls'),
      'xpack-ciGroup-reporting.api.usage': xpackCiGroupRunner('reporting.api.usage'),
      'xpack-ciGroup-reporting.functional.reporting': xpackCiGroupRunner('reporting.functional.reporting'),
      'xpack-ciGroup-reporting.api.generate': xpackCiGroupRunner('reporting.api.generate'),
      'xpack-ciGroup-functional.apps.advanced_settings': xpackCiGroupRunner('functional.apps.advanced_settings'),
      'xpack-ciGroup-functional.apps.canvas': xpackCiGroupRunner('functional.apps.canvas'),
      'xpack-ciGroup-functional.apps.graph': xpackCiGroupRunner('functional.apps.graph'),
      'xpack-ciGroup-functional.apps.monitoring': xpackCiGroupRunner('functional.apps.monitoring'),
      'xpack-ciGroup-functional.apps.watcher': xpackCiGroupRunner('functional.apps.watcher'),
      'xpack-ciGroup-functional.apps.dashboard': xpackCiGroupRunner('functional.apps.dashboard'),
      'xpack-ciGroup-functional.apps.dashboard_mode': xpackCiGroupRunner('functional.apps.dashboard_mode'),
      'xpack-ciGroup-functional.apps.discover': xpackCiGroupRunner('functional.apps.discover'),
      'xpack-ciGroup-functional.apps.security': xpackCiGroupRunner('functional.apps.security'),
      'xpack-ciGroup-functional.apps.spaces': xpackCiGroupRunner('functional.apps.spaces'),
      'xpack-ciGroup-functional.apps.logstash': xpackCiGroupRunner('functional.apps.logstash'),
      'xpack-ciGroup-functional.apps.grok_debugger': xpackCiGroupRunner('functional.apps.grok_debugger'),
      'xpack-ciGroup-functional.apps.infra': xpackCiGroupRunner('functional.apps.infra'),
      'xpack-ciGroup-functional.apps.machine_learning': xpackCiGroupRunner('functional.apps.machine_learning'),
      'xpack-ciGroup-functional.apps.rollup_job': xpackCiGroupRunner('functional.apps.rollup_job'),
      // 'xpack-ciGroup-functional.apps.maps': xpackCiGroupRunner('functional.apps.maps'),
      'xpack-ciGroup-functional.apps.maps.documents_source': xpackCiGroupRunner('functional.apps.maps.documents_source'),
      'xpack-ciGroup-functional.apps.maps.saved_object_management': xpackCiGroupRunner('functional.apps.maps.saved_object_management'),
      'xpack-ciGroup-functional.apps.maps.sample_data': xpackCiGroupRunner('functional.apps.maps.sample_data'),
      'xpack-ciGroup-functional.apps.maps.full_screen_mode': xpackCiGroupRunner('functional.apps.maps.full_screen_mode'),
      'xpack-ciGroup-functional.apps.maps.es_geo_grid_source': xpackCiGroupRunner('functional.apps.maps.es_geo_grid_source'),
      'xpack-ciGroup-functional.apps.maps.joins': xpackCiGroupRunner('functional.apps.maps.joins'),
      'xpack-ciGroup-functional.apps.maps.add_layer_panel': xpackCiGroupRunner('functional.apps.maps.add_layer_panel'),
      'xpack-ciGroup-functional.apps.maps.import_geojson': xpackCiGroupRunner('functional.apps.maps.import_geojson'),
      'xpack-ciGroup-functional.apps.maps.layer_errors': xpackCiGroupRunner('functional.apps.maps.layer_errors'),
      'xpack-ciGroup-functional.apps.maps.embeddable': xpackCiGroupRunner('functional.apps.maps.embeddable'),
      'xpack-ciGroup-functional.apps.status_page': xpackCiGroupRunner('functional.apps.status_page'),
      'xpack-ciGroup-functional.apps.timelion': xpackCiGroupRunner('functional.apps.timelion'),
      'xpack-ciGroup-functional.apps.upgrade_assistant': xpackCiGroupRunner('functional.apps.upgrade_assistant'),
      'xpack-ciGroup-functional.apps.code': xpackCiGroupRunner('functional.apps.code'),
      'xpack-ciGroup-functional.apps.visualize': xpackCiGroupRunner('functional.apps.visualize'),
      'xpack-ciGroup-functional.apps.uptime': xpackCiGroupRunner('functional.apps.uptime'),
      'xpack-ciGroup-functional.apps.saved_objects_management': xpackCiGroupRunner('functional.apps.saved_objects_management'),
      'xpack-ciGroup-functional.apps.dev_tools': xpackCiGroupRunner('functional.apps.dev_tools'),
      'xpack-ciGroup-functional.apps.apm': xpackCiGroupRunner('functional.apps.apm'),
      'xpack-ciGroup-functional.apps.index_patterns': xpackCiGroupRunner('functional.apps.index_patterns'),
      'xpack-ciGroup-functional.apps.index_management': xpackCiGroupRunner('functional.apps.index_management'),
      'xpack-ciGroup-functional.apps.index_lifecycle_management': xpackCiGroupRunner('functional.apps.index_lifecycle_management'),
      'xpack-ciGroup-functional.apps.snapshot_restore': xpackCiGroupRunner('functional.apps.snapshot_restore'),
      'xpack-ciGroup-functional.apps.cross_cluster_replication': xpackCiGroupRunner('functional.apps.cross_cluster_replication'),
      'xpack-ciGroup-functional.apps.remote_clusters': xpackCiGroupRunner('functional.apps.remote_clusters'),
      'xpack-ciGroup-functional.apps.license_management': xpackCiGroupRunner('functional.apps.license_management'),
      'xpack-ciGroup-api_integration.apis.security': xpackCiGroupRunner('api_integration.apis.security'),
      'xpack-ciGroup-api_integration.apis.es': xpackCiGroupRunner('api_integration.apis.es'),
      'xpack-ciGroup-api_integration.apis.monitoring': xpackCiGroupRunner('api_integration.apis.monitoring'),
      'xpack-ciGroup-api_integration.apis.xpack_main': xpackCiGroupRunner('api_integration.apis.xpack_main'),
      'xpack-ciGroup-api_integration.apis.telemetry': xpackCiGroupRunner('api_integration.apis.telemetry'),
      'xpack-ciGroup-api_integration.apis.logstash': xpackCiGroupRunner('api_integration.apis.logstash'),
      'xpack-ciGroup-api_integration.apis.kibana': xpackCiGroupRunner('api_integration.apis.kibana'),
      'xpack-ciGroup-api_integration.apis.infra': xpackCiGroupRunner('api_integration.apis.infra'),
      'xpack-ciGroup-api_integration.apis.beats': xpackCiGroupRunner('api_integration.apis.beats'),
      'xpack-ciGroup-api_integration.apis.management': xpackCiGroupRunner('api_integration.apis.management'),
      'xpack-ciGroup-api_integration.apis.uptime': xpackCiGroupRunner('api_integration.apis.uptime'),
      'xpack-ciGroup-api_integration.apis.maps': xpackCiGroupRunner('api_integration.apis.maps'),
      'xpack-ciGroup-api_integration.apis.siem': xpackCiGroupRunner('api_integration.apis.siem'),
      'xpack-ciGroup-plugin_api_integration.task_manager.task_manager_integration': xpackCiGroupRunner('plugin_api_integration.task_manager.task_manager_integration'),
      'xpack-ciGroup-plugin_api_integration.encrypted_saved_objects': xpackCiGroupRunner('plugin_api_integration.encrypted_saved_objects'),
      'xpack-ciGroup-kerberos_api_integration.apis.index': xpackCiGroupRunner('kerberos_api_integration.apis.index'),
      'xpack-ciGroup-saml_api_integration.apis.security': xpackCiGroupRunner('saml_api_integration.apis.security'),
      'xpack-ciGroup-token_api_integration': xpackCiGroupRunner('token_api_integration'),
      'xpack-ciGroup-oidc_api_integration.apis.authorization_code_flow': xpackCiGroupRunner('oidc_api_integration.apis.authorization_code_flow'),
      'xpack-ciGroup-upgrade_assistant_integration.upgrade_assistant.reindexing': xpackCiGroupRunner('upgrade_assistant_integration.upgrade_assistant.reindexing'),
      // 'xpack-ciGroup2': xpackCiGroupRunner(2),
      // 'xpack-ciGroup3': xpackCiGroupRunner(3),
      // 'xpack-ciGroup4': xpackCiGroupRunner(4),
      // 'xpack-ciGroup5': xpackCiGroupRunner(5),
      // 'xpack-ciGroup6': xpackCiGroupRunner(6),
      // 'xpack-ciGroup7': xpackCiGroupRunner(7),
      // 'xpack-ciGroup8': xpackCiGroupRunner(8),
      // 'xpack-ciGroup9': xpackCiGroupRunner(9),
      // 'xpack-ciGroup10': xpackCiGroupRunner(10),
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

