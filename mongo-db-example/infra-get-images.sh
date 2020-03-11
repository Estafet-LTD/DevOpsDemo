# get the images from Red Hat registry and tag them for the Thales Infrastructure registry

docker pull registry.redhat.io/rhscl/mongodb-34-rhel7
docker tag registry.redhat.io/rhscl/mongodb-34-rhel7  repo.thales.com:5000/rhscl/mongodb-34-rhel7
docker push repo.thales.com:5000/rhscl/mongodb-34-rhel7