#! /bin/bash
#
# Thomas Freese
#

clear;

HOST="http://localhost:30090/cache"
readonly CURL_CMD="curl --connect-timeout 3 --max-time 6 --silent -i $HOST"

$CURL_CMD -X GET
echo
echo

echo "Get for not existing Key."
RESPONSE=$($CURL_CMD/123 -X GET -H "Accept: application/vnd.gradle.build-cache-artifact.v2")
echo "$RESPONSE"
echo

EXIST_CODE=$(echo "$RESPONSE" | awk '/^HTTP/{print $2}')
#echo "$RESPONSE" | grep "HTTP/" | awk '{print $2}'

if [ $EXIST_CODE -eq "404" ]; then
    echo "Put and retrieve."
    $CURL_CMD/123 -X PUT -H "Content-Type: application/vnd.gradle.build-cache-artifact.v2" -d "[1,2,3]"
    echo
    $CURL_CMD/123 -X GET -H "Accept: application/vnd.gradle.build-cache-artifact.v2"
    echo
    echo
fi
