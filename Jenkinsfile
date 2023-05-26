pipeline {
    agent any

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
            environment {
                NJU_PASSWORD = credentials('NJU_PASSWORD')
            }
            when {
                anyOf {
                    branch 'master'
                    branch 'test'
                }
            }
            steps {
                echo 'Deploying....'
                sh 'sudo bash ./docker-build.sh'
                sh 'sudo NJU_PASSWORD=$NJU_PASSWORD bash ./deploy.sh'
            }
        }
    }
}