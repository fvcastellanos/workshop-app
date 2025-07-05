node {

    stage 'Checkout' {

        checkout scm
    }

    stage 'Build' {

        docker.image('maven:3.9-eclipse-temurin-21')
            .inside {
                sh 'mvn mvn -b clean package -DskipTests'
            }
    }

}
