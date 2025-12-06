#!/bin/bash

sleep 5

STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:3000/actuator/health)

if [ "$STATUS" == "200" ]; then
  echo "Health OK"
  exit 0
else
  echo "Health check failed"
  exit 1
fi
