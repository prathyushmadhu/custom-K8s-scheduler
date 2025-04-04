#!/bin/bash

# Names of the resources
DEPLOYMENT_NAME="custom-k8s-scheduler"
SERVICE_NAME="custom-k8s-scheduler"
WEBHOOK_NAME="custom-scheduler-webhook"

kubectl delete deployment $DEPLOYMENT_NAME --ignore-not-found=true

kubectl delete service $SERVICE_NAME --ignore-not-found=true

kubectl delete mutatingwebhookconfiguration $WEBHOOK_NAME --ignore-not-found=true

