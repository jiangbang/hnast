#!/bin/bash
#vi set ff=unix
###########################################################################
# @author: maowei 
# @desc: start hnast-api-admin
# @time: 2020-10-19
###########################################################################

SERVER_NAME=hnast-api-admin
DEPLOY_DIR=/home/www/hnast/api
STDOUT_FILE=${DEPLOY_DIR}/log/${SERVER_NAME}.log
JAR_FILE=${DEPLOY_DIR}/${SERVER_NAME}.jar

PIDS=`ps -f | grep java | grep "${DEPLOY_DIR}/${SERVER_NAME}" |awk '{print $2}'`
if [ -n "$PIDS" ]; then
    echo "ERROR: The ${SERVER_NAME} already started!"
    echo "PID: $PIDS"
    exit 1 
fi
JAVA_OPTS="-server -Xmx2048m -Xms2048m -XX:-OmitStackTraceInFastThrow -XX:+PrintCommandLineFlags -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Djava.util.Arrays.useLegacyMergeSort=true -Dfile.encoding=UTF-8"
echo -e "Starting the ${SERVER_NAME} ...\c"
#nohup java $JAVA_OPTS -jar ${JAR_FILE} > $STDOUT_FILE 2>&1 &
chmod +x ${JAR_FILE}
nohup java -jar ${JAR_FILE} > /dev/null 2>&1 &
COUNT=0
while [ $COUNT -lt 1 ]; do    
    echo -e ".\c"
    sleep 1 
    COUNT=`ps -f | grep java | grep "${DEPLOY_DIR}/${SERVER_NAME}" | awk '{print $2}' | wc -l`
    if [ $COUNT -gt 0 ]; then
        break
    fi
done

echo "OK!"
PIDS=`ps -f | grep java | grep "${DEPLOY_DIR}/${SERVER_NAME}" | awk '{print $2}'`
echo "PID: $PIDS"
echo "STDOUT: $STDOUT_FILE"

