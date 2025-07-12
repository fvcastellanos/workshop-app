node {

    repositoryUrl = 'https://github.com/fvcastellanos/workshop-app.git'
    mavenImageName = 'maven:3.9-eclipse-temurin-21'
    postgresImageName = 'postgres:17'

    environment {
        DB_CREDENTIALS = credentials('workshop-db-credentials')
        DB_NAME = credentials('workshop-db')
        DB_SCHEMA = credentials('workshop-schema')
    }

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

    stage('Clean up') {

        sh "docker compose -f ./docker/services.yaml down --remove-orphans"
        sh "docker volume prune -f"
        sh "docker network prune -f"
        sh "docker rm -f postgres || true"
    }
}
