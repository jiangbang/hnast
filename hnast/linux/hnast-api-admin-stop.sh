#!/bin/bash
#vi set ff=unix
#不要上传此文件 ， 在linux下新建文件，并复制内容
###########################################################################
# @author: maowei 
# @desc: stop hnast-api-admin
# @time: 2020-10-19
###########################################################################
SERVER_NAME=hnast-api-admin
DEPLOY_DIR=/home/www/hnast/api
PIDS=`ps -ef | grep java | grep "${DEPLOY_DIR}/${SERVER_NAME}" |awk '{print $2}'`
if [ -z "$PIDS" ]; then
    echo "ERROR: The $SERVER_NAME does not started!"
    exit 1 
fi

echo -e "Stopping the $SERVER_NAME ...\c"
for PID in $PIDS ; do
    kill $PID > /dev/null 2>&1
done

COUNT=0
while [ $COUNT -lt 1 ]; do    
    echo -e ".\c"
    sleep 1
    COUNT=1
    for PID in $PIDS ; do
        PID_EXIST=`ps -f -p $PID | grep java`
        if [ -n "$PID_EXIST" ]; then
            COUNT=0
            break
        fi
    done
done

echo "OK!"
echo "PID: $PIDS"
