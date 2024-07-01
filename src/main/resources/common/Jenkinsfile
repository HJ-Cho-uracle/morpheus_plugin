pipeline {
  agent none

  parameters {
    choice(name: 'BUILD_TYPE', choices: ['ALL', 'ANDROID', 'IOS'], description: 'Build project type')
    string(name: 'CID_PREFIX', defaultValue: 'morpheusone-guide-demo', description: 'Credentials ID Prefix')
  }

  environment {
      CERTIFICATE_CID = "${CID_PREFIX}-ios-certificate"
      PROFILE_CID = "${CID_PREFIX}-ios-profile"
      ACCOUNT = credentials("${CID_PREFIX}-ios-account")
      ACCOUNT_JSON = readJSON(text: ACCOUNT)
      ACCOUNT_CERTTPW = "${ACCOUNT_JSON.certificatePassword}"
      ACCOUNT_TEAMID = "${ACCOUNT_JSON.teamId}"

      ARTIFACT_PATH = '**/*.apk, **/*.aab, **/*.ipa'
      NPM_BIN_PATH = '~/.nvm/versions/node/v18.18.2/bin'
      IOS_PJT_PATH = 'ios/project'
  }

  stages {
    stage('Build') {
      parallel {
        stage('Android') {
          when {
            expression { params.BUILD_TYPE == 'ALL' || params.BUILD_TYPE == 'ANDROID' }
          }
          agent { node { label 'global-mcdp-client-build' } }
          steps {
            script {
              container('node') {
                sh """
                #!/bin/bash
                npm install
                if [ -d web ]; then
                    cd web && npm install && npm run build
                fi
                """
              }
              container('openjdk11') {
                sh "/bin/sh ./gradlew clean assembleDebug"
              }
              try {
                stash includes: '**/*.apk', name: 'apk'
              } catch (err) {}
              try {
                stash includes: '**/*.aab', name: 'aab'
              } catch (err) {}
            }
          }
        }
    // ------------------------------------------------------------------------
        stage('iOS') {
          when {
            expression { params.BUILD_TYPE == 'ALL' || params.BUILD_TYPE == 'IOS' }
          }
          agent { node { label 'macmini' } }
          steps {
            withCredentials([file(credentialsId: env.CERTIFICATE_CID, variable: 'ios_cert'),
                            file(credentialsId: env.PROFILE_CID, variable: 'ios_profile')]) {
              script {
                sh """
                #!/bin/bash +x
                if [ ! -d "$IOS_PJT_PATH" ]; then
                  echo "iOS SOURCE CODE is not exist."
                  exit 0
                fi

                cp \$ios_cert $IOS_PJT_PATH/development.p12
                cp \$ios_profile $IOS_PJT_PATH/development.mobileprovision

                cat > $IOS_PJT_PATH/.env <<EOF
CERT_PATH=development.p12
CERT_AUTH=$ACCOUNT_CERTTPW
PROVISION=development.mobileprovision
TEAM_ID=$ACCOUNT_TEAMID
EOF
                """
              }
            }
            script {
              sh """
              #!/bin/bash
              export LANG=en_US.UTF-8
              export LANGUAGE=en_US.UTF-8
              export LC_ALL=en_US.UTF-8
              export PATH=/usr/local/bin:$NPM_BIN_PATH:$PATH
              npm install && cd $IOS_PJT_PATH && pod install
              """
              sh """
              #!/bin/bash +x
              cd $IOS_PJT_PATH
              cat > Gemfile <<EOF
source "https://rubygems.org"

gem "fastlane"
EOF

              mkdir fastlane
              cat > fastlane/Fastfile <<EOF
default_platform(:ios)

platform :ios do
    \\\$certificate_path = ENV['CERT_PATH']
    \\\$certificate_password = ENV['CERT_AUTH']
    \\\$provision = ENV['PROVISION']
    \\\$team_id = ENV['TEAM_ID']
    \\\$keychain_name = SecureRandom.uuid
    \\\$keychain_password = SecureRandom.hex(10)

    after_all do |lane, options|
      remove_keychain
    end

    error do |lane, exception, options|
      remove_keychain
    end

    desc "Remove Keychain from CI"
    private_lane :remove_keychain do |options|
        if File.exist?(File.expand_path("~/Library/Keychains/#{\\\$keychain_name}-db"))
            UI.important "Deleting keychain #{\\\$keychain_name}"
            delete_keychain(name: \\\$keychain_name)
        elsif
            UI.important "No keychain file found to delete"
        end
    end

    desc "Setup Keychain for match on CI"
    private_lane :setup_keychain do |options|
        create_keychain(
            name: \\\$keychain_name,
            password: \\\$keychain_password,
            default_keychain: false,
            unlock: false,
            timeout: 0,
            lock_when_sleeps: true
        )
    end

    desc "Install Certificates and Provisioning Profiles"
    lane :match_sync do |options|
        UI.message "Installing Apple Certificates and Provisioning Profiles for CI"
        setup_keychain
        import_certificate(
            certificate_path: \\\$certificate_path,
            certificate_password: \\\$certificate_password,
            keychain_name: \\\$keychain_name,
            keychain_password: \\\$keychain_password
        )
        sh("security", "unlock-keychain" ,"-p", \\\$keychain_password, \\\$keychain_name+"-db")
    end

    desc "Build iOS App"
        lane :build do |options|
            match_sync
            disable_automatic_code_signing(
                path: "project.xcodeproj",
                team_id: \\\$team_id
            )
            update_project_provisioning(
                xcodeproj: "project.xcodeproj",
                profile: \\\$provision # optional if you use sigh
            )
            gym(
                workspace: "project.xcworkspace",
                configuration: "Debug",
                scheme: "project",
                xcargs: "-allowProvisioningUpdates",
                export_method: "development"
            )
    end
end
EOF
              """
              sh """
              #!/bin/bash
              export LANG=en_US.UTF-8
              export LANGUAGE=en_US.UTF-8
              export LC_ALL=en_US.UTF-8
              export PATH=/usr/local/bin:$PATH
              cd $IOS_PJT_PATH && fastlane ios build
              """
              stash includes: '**/*.ipa', name: 'ipa'
            }
          }
        }
      }
    }
    // ------------------------------------------------------------------------
    stage('Archive') {
      when {
        expression { params.BUILD_TYPE != null && params.BUILD_TYPE != '' }
      }
      agent { node { label 'global-mcdp-client-build' } }
      steps {
        script {
          try {
            unstash 'apk'
          } catch (err) {}
          try {
            unstash 'aab'
          } catch (err) {}
          try {
            unstash 'ipa'
          } catch (err) {}
          archiveArtifacts(artifacts: ARTIFACT_PATH, allowEmptyArchive: false)
        }
      }
    }
  }

  options {
    preserveStashes(buildCount: 1)
    disableConcurrentBuilds()
    timeout(time: 1, unit: 'HOURS')
  }
}