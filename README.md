# FlawlessRead

## Description
FlawlessRead is a Java-based application that utilizes an HTML and CSS frontend. This repository contains the source code, infrastructure configurations, and the project's documentation, including the final report (Relatório Final PGI.pdf).

## Technologies
* Backend: Java
* Build and Dependency Management: Maven
* Frontend: HTML and CSS
* Infrastructure: Docker and Docker Compose

## Project Structure
* src/: Contains the source code of the application.
* pom.xml, .mvn/, mvnw, mvnw.cmd: Maven configuration files and wrapper for building the project.
* Dockerfile and compose.yml: Files used for containerization and service orchestration.
* Relatório Final PGI.pdf: The project's final report and documentation.

## How to Run the Project
You can run this application locally using either Docker or Maven.

### Option 1: Using Docker (Recommended)
Ensure that Docker and Docker Compose are installed on your system.

1. Clone the repository:
   git clone https://github.com/franciscamateusPt05/FlawlessRead.git
   cd FlawlessRead

2. Build and start the containers:
   docker-compose up --build

### Option 2: Using Maven
Ensure that a Java Development Kit (JDK) is installed on your system.

1. Clone the repository:
   git clone https://github.com/franciscamateusPt05/FlawlessRead.git
   cd FlawlessRead

2. Build and run the application:
   - On Windows:
     mvnw.cmd clean install
     mvnw.cmd spring-boot:run
   - On Linux/macOS:
     ./mvnw clean install
     ./mvnw spring-boot:run

Note: The execution command assumes the project is built with Spring Boot. Adjust the run command if a different framework is being used.

## Author
* franciscamateusPt05 (https://github.com/franciscamateusPt05)
