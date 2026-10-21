FROM eclipse-temurin:21-jdk
WORKDIR /app
Copy BankQueueManagement.java
RUN javac BankQueueManagement.java
CMD ["java","BankQueueManagement"]
