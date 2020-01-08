pipeline {
    agent {
   any {}
    }
    environment {
    // Get the maven tool
        // ** NOTE: This 'M3' maven tool must be configured in the global configuration
        def mvnHome = tool 'M3'
        def dockerHome = tool 'Docker'
   }
    
    stages {
        stage('test') {
      steps {
        echo 'testing'
    // sh "${mvnHome}/bin/mvn -B test -f example/pom.xml"
    // sh "${mvnHome}/bin/mvn -version"
    // sh "${dockerHome}/bin/docker -v"
      }
  }
       stage('SonarQube analysis') {
     steps{
     withSonarQubeEnv('sonarqube') {
                   sh "${mvnHome}/bin/mvn -f example/pom.xml  -DskipTests sonar:sonar "
                    }
    }
  }

        stage('Stage 2') {
            steps {
                echo 'Goodbye, world!' 
            }
        }
    }
}