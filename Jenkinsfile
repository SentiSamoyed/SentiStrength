pipeline {
    agent any

    environment {
        DB_ADDRESS = credentials('DB_ADDRESS')
        DB_USER = credentials('DB_USER')
        DB_PASSWORD = credentials('DB_PASSWORD')
        TRACKER_URL = credentials('TRACKER_URL')
    }

    stages {
        stage('Build') {
            when {
                anyOf {
                    branch 'master'
                    branch 'development'
                    branch 'test'
                }
            }

            steps {
                echo 'Running gradle build...'
                withGradle {
                    sh './gradlew build'
                    sh './gradlew bootJar'
                }
                archiveArtifacts artifacts: '**/build/libs/*.jar'
            }
        }

        stage('Release') {
            when {
                branch 'master'
            }

            steps {
                echo 'Now trying to release the jar...'
                withGradle {
                    sh './gradlew release'
                }
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
                withGradle {
                    sh './gradlew test'
                }
            }
        }
        stage('Deploy') {
            when {
                anyOf {
                    branch 'master'
                    branch 'test'
                }
            }
            steps {
                echo 'Deploying....'
                sh 'sudo bash ./docker-build.sh'
                sh 'sudo DB_PASSWORD=$DB_PASSWORD DB_ADDRESS=$DB_ADDRESS DB_USER=$DB_USER TRACKER_URL=$TRACKER_URL bash ./deploy.sh'
            }
        }
    }
}