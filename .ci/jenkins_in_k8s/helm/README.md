# Jenkins-in-k8s Helm

## Install

Note: `namespace` and `name` should be unique if you are installing in a shared Kubernetes cluster.

`helm install --namespace jenkins --name jenkins -f values.yml stable/jenkins`

## Update

`helm upgrade jenkins -f values.yml stable/jenkins`

## Delete

`helm del --purge jenkins`