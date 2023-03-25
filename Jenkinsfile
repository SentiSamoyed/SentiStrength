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
                echo 'Building the jar...'
                withGradle {
                    sh './gradlew shadowJar'
                }
                archiveArtifacts artifacts: '**/build/libs/*.jar'
            }
        }

        stage('Release') {
            when {
                branch 'master'
            }

            steps {
                withGradle {
                    sh './gradlew release'
                }
            }
        }
//        stage('Test') {
//            steps {
//                echo 'Testing..'
//            }
//        }
//        stage('Deploy') {
//            steps {
//                echo 'Deploying....'
//            }
//        }
    }
}