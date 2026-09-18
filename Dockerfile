# Build stage: compile the app with Maven, so no JDK or Maven is needed at runtime.
FROM maven:3.9.16-eclipse-temurin-17 AS build
WORKDIR /build

# Dependencies are resolved before the sources are copied, so a code change does not
# re-download the whole dependency tree on the next build.
COPY app/pom.xml .
RUN mvn -B -q dependency:go-offline

COPY app/src ./src
RUN mvn -B -q -DskipTests package && mv target/*.jar target/app.jar

# Runtime stage: a JRE only, which keeps the image small and reduces the attack surface.
FROM eclipse-temurin:17.0.20_8-jre-alpine
WORKDIR /opt/app

RUN addgroup -S smartclinic && adduser -S smartclinic -G smartclinic
COPY --from=build --chown=smartclinic:smartclinic /build/target/app.jar app.jar

# Never run as root inside a container.
USER smartclinic
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s \
    CMD wget -q -O /dev/null http://localhost:8080/ || exit 1

ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]
