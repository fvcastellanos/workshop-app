node {

    imageName = 'maven:3.9-eclipse-temurin-21'

    stage 'Checkout' {

        checkout scm: scmGit(
            branches: [[name: '$BRANCH_NAME']], 
            extensions: [], 
            userRemoteConfigs: [[
                credentialsId: 'git-credentials', 
                url: 'https://github.com/fvcastellanos/workshop-app.git'
            ]]
        )
    }

    stage 'Build' {

        docker.image(imageName)
            .inside {
                sh 'mvn -B clean package -DskipTests'
            }
    }

    stage('Clean up') {


    }

}
