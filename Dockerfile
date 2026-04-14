# 构建阶段
FROM maven:3.8.6-openjdk-8 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 运行阶段
FROM openjdk:8-jdk-alpine
WORKDIR /app

# 安装运行时依赖
RUN apk add --no-cache curl

# 从构建阶段复制 JAR 文件
COPY --from=builder /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s \
    CMD curl -f http://localhost:8080/ || exit 1

# 启动命令
ENTRYPOINT ["java","-jar","/app.jar"]
