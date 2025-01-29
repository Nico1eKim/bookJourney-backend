FROM amazoncorretto:17
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
COPY ./build/libs/*.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
