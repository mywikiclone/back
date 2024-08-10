#!/usr/bin/env bash

# 프로젝트 경로
PROJECT_ROOT="/home/ec2-user/back"
JAR_FILE="$PROJECT_ROOT/ssagemeogja.jar"

# 각종 로그 경로
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +'%Y-%m-%d %H:%M:%S')

CURRENT_PID=$(pgrep -f $JAR_FILE)

if [ -z $CURRENT_PID ]; then
    echo "[$TIME_NOW] > 실행중인 애플리케이션이 없습니다." >> $DEPLOY_LOG
else
    echo "[$TIME_NOW] > 실행중인 애플리케이션을 종료합니다. PID = $CURRENT_PID" >> $DEPLOY_LOG
    kill -15 $CURRENT_PID
fi