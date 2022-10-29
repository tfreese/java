# docker build -t jconkylatest .
# docker build -t jconky:latest -f service/Dockerfile .
# docker run [-d] --name jconky --memory 64m --publish 7999:7999 --network host jconky:latest
# docker start/stop jconky;

# --network host: für Internetzugang

#FROM localhost:5083/openjdk:17
FROM openjdk:latest

MAINTAINER Thomas Freese <commercial@freese-home.de>
 
ARG JAR_FILE=app/*-spring.jar
ENV VM_PARAMS="-Xms12m -Xmx64m"

COPY ${JAR_FILE} /app/app.jar

RUN echo "Europe/Berlin" > /etc/timezone

# GUI-Bibiotheken für JavaFX installieren -> Kennt den Befehl 'apt-get' nicht
#RUN apt-get update && apt-get install --no-install-recommends -y xorg libgl1-mesa-glx && rm -rf /var/lib/apt/lists/*
##RUN apt-get update && apt-get install --no-install-recommends -y xvfb openjfx && rm -rf /var/lib/apt/lists/*
###RUN apt-get update && apt-get install libgtk-3-0 libglu1-mesa -y && apt-get update

WORKDIR /
VOLUME /tmp

ENTRYPOINT exec java \
 $VM_PARAMS \
 -XX:TieredStopAtLevel=1 \
 -Djava.security.egd=file:/dev/./urandom \
 -jar \
 /app/app.jar \
 $0 $@
