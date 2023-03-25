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