# log into the internal registry and push

oc login -u developer -p developer

TOKEN=$(oc whoami -t)

docker login -u developer -p $TOKEN docker-registry.default.svc.cluster.local:5000


docker pull repo.thales.com:5000/rhscl/mongodb-34-rhel7

docker tag repo.thales.com:5000/rhscl/mongodb-34-rhel7 docker-registry.default.svc.cluster.local:5000/openshift/mongodb-34-rhel7:3.4

docker push docker-registry.default.svc.cluster.local:5000/openshift/mongodb-34-rhel7:3.4

oc login -u system:admin