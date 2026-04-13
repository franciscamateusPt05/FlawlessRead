# FlawlessRead

A Java-based application featuring an HTML and CSS frontend, containerized with Docker for seamless deployment and development.

---

## Table of Contents
- [Description](#description)
- [Technologies](#technologies)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Running the Application](#running-the-application)
  - [Accessing the Application](#accessing-the-application)
- [Documentation](#documentation)
- [Author](#author)

---

## Description
FlawlessRead is a full-stack project developed with a Java backend and a frontend built using HTML and CSS. The application is designed to be easily portable across different environments through Docker containerization, ensuring consistency between development and production setups.

## Technologies
- **Backend:** Java
- **Build Tool:** Maven
- **Frontend:** HTML, CSS
- **Infrastructure:** Docker, Docker Compose

## Project Structure
```text
FlawlessRead/
├── .mvn/                   # Maven wrapper configuration
├── src/                    # Application source code
├── Dockerfile              # Docker image configuration
├── compose.yml             # Docker Compose orchestration
├── mvnw / mvnw.cmd         # Maven wrapper executables
├── pom.xml                 # Maven dependencies and build configuration
└── Relatório Final PGI.pdf # Project documentation and final report
```

## Getting Started

Follow these instructions to get a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

Before proceeding, ensure you have the following installed on your system:

- **Docker**
- **Docker Compose**

### Installation

Clone the repository to your local machine:

```bash
git clone [https://github.com/franciscamateusPt05/FlawlessRead.git](https://github.com/franciscamateusPt05/FlawlessRead.git)
```

Navigate into the project directory:

```bash
cd FlawlessRead
```

### Running the Application

This project is configured to handle all dependencies automatically within a containerized environment. 

Build and start the services using Docker Compose:

```bash
docker-compose up --build
```

The application will start automatically once the image is built and the container is ready.

### Accessing the Application

Once the application has started successfully, open your web browser and navigate to:

```text
http://localhost:8080
```

*Note: If you have configured a different port in your application properties or Dockerfile, please use that port instead.*

## Documentation

For a comprehensive overview of the project's architecture, development lifecycle, and core objectives, please refer to the **Relatório Final PGI.pdf** file located in the root directory.

## Author

**franciscamateusPt05**
- GitHub: [https://github.com/franciscamateusPt05](https://github.com/franciscamateusPt05)
