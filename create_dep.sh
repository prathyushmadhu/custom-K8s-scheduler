#!/bin/bash


kubectl apply -f admission-controller/scheduler-deployment.yaml 
kubectl apply -f admission-controller/webhook-configuration.yaml


