pipeline {
    agent {
   any {}
    }
    environment {
    // Get the maven tool
        // ** NOTE: This 'M3' maven tool must be configured in the global configuration
        def mvnHome = tool 'M3'
        //def dockerHome = tool 'Docker'
   }
   stages {
   stage('First Stage') {
            steps {
                echo 'Hello, world!' 
            }
        }
    
    
        stage('test') {
      steps {
        echo 'testing'
        sh "${mvnHome}/bin/mvn -B test"
      }
      }
      }
      }