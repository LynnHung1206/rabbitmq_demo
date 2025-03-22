FROM azul/zulu-openjdk-debian:21

WORKDIR /app

# 從工作目錄相對路徑複製 JAR 文件
# 如果使用 context: . 和 file: ./docker/Dockerfile，那麼以下路徑應該是：
COPY target/*.jar rabbitmq-demo.jar
# 如果只將 docker 目錄作為 context，則需要使用：
# COPY target/*.jar app.jar

# 設置環境變數

ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/test \
    SPRING_DATASOURCE_USERNAME=postgres \
    SPRING_DATASOURCE_PASSWORD=postgres \
    SPRING_RABBITMQ_HOST=rabbitmq \
    SPRING_RABBITMQ_PORT=5672 \
    SPRING_RABBITMQ_USERNAME=root \
    SPRING_RABBITMQ_PASSWORD=root123 \
    TZ="Asia/Taipei"

# Set Java options separately
ENV JAVA_OPTS="-Xms256M -Xmx4096M"


# 暴露應用程序端口
EXPOSE 8083
# Add pg_isready to test connection
RUN apt-get update && apt-get install -y postgresql-client
# 啟動應用程序
ENTRYPOINT until pg_isready -h postgres -p 5432 -U postgres; do sleep 1; done && \
          java $JAVA_OPTS -jar rabbitmq-demo.jar