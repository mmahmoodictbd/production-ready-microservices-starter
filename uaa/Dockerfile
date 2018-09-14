FROM anapsix/alpine-java

MAINTAINER Mossaddeque Mahmood

VOLUME /tmp
ADD target/uaa.jar app.jar
ENV JAVA_OPTS="-Dspring.profiles.active=prod,mysql,docker"
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
