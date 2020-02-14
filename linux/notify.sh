#!/bin/bash

curl -g -s -G \
    'https://us-central1-notify-15448.cloudfunctions.net/sendNotification' \
    --data-urlencode "to=nad9P73" \
    --data-urlencode "text=ciao" \
    --data-urlencode "title=caio" 
