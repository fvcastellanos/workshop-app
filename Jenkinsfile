pipeline {
    agent any

    environment {

        REPOSITORY_URL = 'https://github.com/fvcastellanos/workshop-app.git'
        MAVEN_IMAGE_NAME = 'maven:3.9-eclipse-temurin-21'
        POSTGRES_IMAGE_NAME = 'postgres:17'

        DB_CREDENTIALS = credentials('workshop-db-credentials')
        DB_NAME = credentials('workshop-db')
        DB_SCHEMA = credentials('workshop-schema')
    }

    stages {
        stage('Prepare Data Services') {

            steps {
                sh "docker compose -f ./docker/services.yaml up -d"
                sh "while ! docker exec postgres pg_isready -U ${DB_CREDENTIALS_USR} -d ${DB_NAME}; do sleep 2; done"
                sh "docker exec -i postgres psql -U ${DB_CREDENTIALS_USR} -d ${DB_NAME} -c 'CREATE SCHEMA IF NOT EXISTS ${DB_SCHEMA};'"
            }
        }

        stage('Checkout') {

            steps {
                checkout scm: scmGit(
                    branches: [[name: '$BRANCH_NAME']], 
                    extensions: [], 
                    userRemoteConfigs: [[
                        credentialsId: 'git-credentials', 
                        url: ${REPOSITORY_URL}
                    ]]
                )
            }
        }

        stage('Build') {

            steps {
                docker.image(${MAVEN_IMAGE_NAME})
                    .inside {
                        sh 'mvn -B clean package -DskipTests'
                    }
            }

        }

        stage('Clean up') {

            steps {
                sh "docker compose -f ./docker/services.yaml down --remove-orphans"
                sh "docker volume prune -f"
                sh "docker network prune -f"
                sh "docker rm -f postgres || true"                
            }

        }

    }
}
