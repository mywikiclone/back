#!/usr/bin/env bash

# 프로젝트 경로
PROJECT_ROOT="/home/ec2-user/back"
JAR_FILE="$PROJECT_ROOT/ssagemeogja.jar"

# 각종 로그 경로
APP_LOG="$PROJECT_ROOT/application.log"
ERROR_LOG="$PROJECT_ROOT/error.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +'%Y-%m-%d %H:%M:%S')

echo "[$TIME_NOW] > $JAR_FILE 파일 복사" >> $DEPLOY_LOG
cp $PROJECT_ROOT/build/libs/*.jar $JAR_FILE

# jar 파일 실행
echo "[$TIME_NOW] > $JAR_FILE 파일 실행" >> $DEPLOY_LOG
nohup java -jar -Dspring.profiles.active=aws $JAR_FILE > $APP_LOG 2> $ERROR_LOG &

# 결과 출력
CURRENT_PID=$(pgrep -f $JAR_FILE)
echo "[$TIME_NOW] > 실행 완료. PID = $CURRENT_PID" >> $DEPLOY_LOG