FROM openjdk:8u181-jdk-alpine

EXPOSE 1099

WORKDIR /src

RUN mkdir build
COPY ./libs ./libs
COPY ./src ./src

RUN javac -d ./build -classpath libs/tomcat-embed-el-8.5.3.jar:libs/tomcat-catalina-8.5.3.jar ./src/RmiServer.java 

CMD ["java", "-classpath", "build:libs/tomcat-catalina-8.5.3.jar:libs/tomcat-embed-el-8.5.3.jar", "RmiServer"]
