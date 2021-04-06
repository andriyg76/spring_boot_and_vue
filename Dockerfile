ARG VERSION=SNAPSHOT
ARG http_proxy=""
ARG https_proxy=""
ARG no_proxy=""

FROM node:lts-alpine
WORKDIR /vue/

ADD . .

RUN \
    cd /vue/backend/ && \
    yarn config set strict-ssl false && \
    yarn install && \
    yarn build && \
    true

FROM maven:3-openjdk-11
WORKDIR /vue/
COPY --from=0 /vue/ .

RUN \
    mvn versions:set -DnewVersion=${VERSION} && \
    mvn package -Pnotests && \
    true

FROM openjdk:11-jre-slim
WORKDIR /vue/

RUN \
    groupadd  vue -g 1006 && \
    adduser --ingroup vue --uid 1006 vue && \
    true
USER oranta
COPY --from=1 /vue/target/*.war /vue/spring_boot_and_vue.war
ENV JAVA_OPTS ""
CMD java $JAVA_OPTS -jar /vue/spring_boot_and_vue.war