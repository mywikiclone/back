FROM openjdk:21
ARG JAR_FILE=build/libs/*.jar



#RUN apt-get update && apt-get install -y redis-server



COPY ${JAR_FILE} app.jar


CMD java -jar -Dspring.profiles.active=aws /app.jar


ENV TZ=Asia/Seoul


#ec2인스턴스에 깔린 redis과 컨테이너 연결/컨테이너 내부에서 일어난 일들ㅇ을 ec2인스턴스에 로그 남기는 방법 알아보기
#이거 실행시에 reqeust response에서 host가 어떻게 나오는지 보기.
#yml파일 docker파일은 대강 만들었으니 우선은 deploy파일을 ec2인스턴스에 넣어서 실행해보기.