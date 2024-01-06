#!bin/bash

# 버전 설정
VERSION='0.0.1'

#gradle로 빌드 수행
./gradlew clean build -x test

ROOT_PATH=`pwd`
echo $ROOT_PATH

echo 'Api-application Docker Image Bulid'
docker build -t api:$VERSION -f $ROOT_PATH/docker/api-application/Dockerfile .
echo 'Api-application Docker Image build... Done!!'

echo 'Consumer application Docker Image Bulid'
docker build -t consumer:$VERSION -f $ROOT_PATH/docker/consumer-application/Dockerfile .
echo 'Consumer Image build... Done!!'

echo 'React Docker Image Bulid'
docker build -t react-front:$VERSION -f $ROOT_PATH/docker/react/Dockerfile .
echo 'React Image build... Done!!'

cd $ROOT_PATH/docker/compose && docker-compose up -d
echo 'End.. Project Start Process'