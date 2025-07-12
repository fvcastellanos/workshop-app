node {

    repositoryUrl = 'https://github.com/fvcastellanos/workshop-app.git'
    mavenImageName = 'maven:3.9-eclipse-temurin-21'
    postgresImageName = 'postgres:17'

    environment {
        env.DB_CREDENTIALS = credentials('workshop-db-credentials')
        env.DB_NAME = credentials('workshop-db')
        env.DB_SCHEMA = credentials('workshop-schema')
    }

    stages {
        stage('Prepare Data Services') {

            sh "docker compose -f ./docker/services.yaml up -d"
            sh "while ! docker exec postgres pg_isready -U ${DB_CREDENTIALS_USR} -d ${DB_NAME}; do sleep 2; done"
            sh "docker exec -i postgres psql -U ${DB_CREDENTIALS_USR} -d ${DB_NAME} -c 'CREATE SCHEMA IF NOT EXISTS ${DB_SCHEMA};'"
        }

        stage('Checkout') {

            checkout scm: scmGit(
                branches: [[name: '$BRANCH_NAME']], 
                extensions: [], 
                userRemoteConfigs: [[
                    credentialsId: 'git-credentials', 
                    url: repositoryUrl
                ]]
            )
        }

        stage('Build') {

            docker.image(mavenImageName)
                .inside {
                    sh 'mvn -B clean package -DskipTests'
                }
        }

    }

    post {
        always {
            echo 'Cleaning up...'
            sh "docker compose -f ./docker/services.yaml down --remove-orphans"
            sh "docker volume prune -f"
            sh "docker network prune -f"

        }
    }

}
