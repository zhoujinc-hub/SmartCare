FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# 1. 先只复制 pom.xml，预下载依赖（这一层会被缓存）
COPY pom.xml .
RUN mvn dependency:go-offline -q

# 2. 再复制源代码并打包（代码修改只影响这一步）
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
