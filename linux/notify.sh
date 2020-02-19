#!/bin/bash

while getopts d:t:c: option
do
case "${option}"
in
d) DEST=${OPTARG};;
t) TITLE=${OPTARG};;
c) TEXT=${OPTARG};;
esac
done

echo $DEST
echo $TITLE
echo $TEXT

if [ -z "$DEST" ]
then
      echo "use option -d to set the destination id"
      
fi

curl -g -s -G \
    'https://us-central1-notify-15448.cloudfunctions.net/sendNotification' \
    --data-urlencode "to=$DEST" \
    --data-urlencode "text=$TEXT" \
    --data-urlencode "title=$TITLE" 

echo ""
