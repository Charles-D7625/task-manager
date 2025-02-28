# Используем официальный образ OpenJDK для сборки приложения
FROM maven:3.8.4-openjdk-17-slim AS builder

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем pom.xml и загружаем зависимости
COPY ./pom.xml .
COPY ./.mvn ./.mvn
COPY ./mvnw .
COPY ./src ./src

# Устанавливаем dos2unix, используя apt-get
RUN apt-get update && apt-get install -y dos2unix

# Преобразуем файл mvnw в Unix формат
RUN dos2unix ./mvnw

# Делаем mvnw исполнимым и собираем проект
RUN chmod +x ./mvnw && ./mvnw package -DskipTests

# Извлекаем слои приложения с помощью layertools
RUN java -Djarmode=layertools -jar ./target/*SNAPSHOT.jar extract

# Описание второго этапа, где мы создаем минимальный образ для запуска приложения
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем файл jar из предыдущего шага
COPY --from=builder /app/target/*.jar app.jar

# Указываем команду, которая будет выполняться при старте контейнера
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Открываем порт, на котором будет работать приложение
EXPOSE 8080
