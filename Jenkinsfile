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

        // docker.image(postgresImageName).withRun(
        //      '-e POSTGRES_USER=$DB_CREDENTIALS_USR ' +
        //      '-e POSTGRES_PASSWORD=${DB_CREDENTIALS_PSW} ' +
        //      '-e POSTGRES_DB=${DB_NAME} ' +
        //      '-e PGPASSWORD=${DB_CREDENTIALS_PSW}'
        // ) { container ->

        //     docker.image(postgresImageName).inside("--link ${container.id}:postgres") {
        //         sh 'while ! pg_isready -h postgres -d ${DB_NAME}; do sleep 2; done'
        //         sh 'psql -h postgres -U ${DB_CREDENTIALS_USR} -d ${DB_NAME} -c "CREATE SCHEMA IF NOT EXISTS ${DB_SCHEMA};"'
        //     }
        // }
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


    }

}
