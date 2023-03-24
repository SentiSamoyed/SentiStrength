pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Building the jar...'
                sh 'gradle shadowJar'
                archiveArtifacts artifacts: '**/build/libs/*.jar'
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