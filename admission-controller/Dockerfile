FROM maven:3-eclipse-temurin-21 AS build
RUN mkdir -p /usr/src/project
WORKDIR /usr/src/project
COPY . .
RUN mvn clean package -DskipTests

WORKDIR /usr/src/project/target
RUN jar xf K8s-Scheduler-0.0.1-SNAPSHOT.jar

RUN jdeps --ignore-missing-deps -q  \
    --recursive  \
    --multi-release 21  \
    --print-module-deps  \
    --class-path 'BOOT-INF/lib/*'  \
    K8s-Scheduler-0.0.1-SNAPSHOT.jar > /usr/src/project/deps.info

RUN jlink \
    --add-modules $(cat /usr/src/project/deps.info) \
    --strip-debug \
    --compress 2 \
    --no-header-files \
    --no-man-pages \
    --output /myjre

FROM debian:bookworm-slim
RUN groupadd -g 1001 scheduler && useradd -u 1001 -g scheduler -m -d /home/scheduler scheduler

ENV JAVA_HOME /usr/java/jdk21
ENV PATH $JAVA_HOME/bin:$PATH

COPY --from=build /myjre $JAVA_HOME

RUN mkdir /project
WORKDIR /project

COPY --from=build /usr/src/project/target/K8s-Scheduler-0.0.1-SNAPSHOT.jar .

RUN chown -R scheduler:scheduler /project

USER 1001

# admission controller webhook exposion
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "K8s-Scheduler-0.0.1-SNAPSHOT.jar"]
