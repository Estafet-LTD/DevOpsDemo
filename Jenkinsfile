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
        sh "${mvnHome}/bin/mvn -B test"
      }
      }
  
       stage('SonarQube analysis') {
     steps{
     withSonarQubeEnv('sonarqube') {
                   sh "${mvnHome}/bin/mvn -DskipTests clean install sonar:sonar"
                    }
    }
  }
  stage('Build App') {
     steps {
      sh "${mvnHome}/bin/mvn -DskipTests clean install"
      }
   }
    
//     steps {
//        script {
//          openshift.withCluster() {
//           openshift.withProject('example-project') {
//           openshift.newApp('wildfly~http://192.168.118.130:3000/root/example')
//          }
//         }
//        }
//      }
//    }

 stage('Create Image Builder') {
 when {
        expression {
          openshift.withCluster() {
          openshift.withProject('example-project') {
            return !openshift.selector("bc", "example").exists();
          }
        }
        }
      }
       steps {
        script {
          openshift.withCluster() {
          openshift.withProject('example-project') {
           openshift.newBuild("--name=example", "--image-stream=wildfly", "--binary")
         }
          }
        }
      }
   }
   
  stage('Build Image') {
   steps {
     script {
        openshift.withCluster() {
        openshift.withProject('example-project') {
           openshift.selector("bc", "example").startBuild("--from-file=target/example-0.0.1-SNAPSHOT.jar", "--wait")
        }
         }
       }
    }
  }

        stage('Final Stage') {
            steps {
                echo 'Goodbye, world!' 
            }
        }
    }
}