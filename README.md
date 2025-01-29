# URL Shortener Subscription Service

![Java Version](https://img.shields.io/badge/Java-21-blue)
![version](https://img.shields.io/badge/version-1.6.1-blue)

## Introduction

The **URL Shortener Subscription Service** is responsible for managing user subscriptions, handling premium features,
and processing billing. This microservice consumes payment event from `payment-service` and provides event-driven
notifications using **Kafka**. It is built using **Spring Boot**, **JPA**, and **MySQL**. The service is fully
containerized using **Docker** for easy deployment.

## Prerequisites

Ensure the following dependencies are installed before running the project:

- **Java 21+** (JDK)
- **Maven**
- **Docker**
- **Kafka**
- **MySQL**

## Installation

### Clone the Repository

```bash
git clone https://github.com/your-repo/urlshortener-subs-service
cd urlshortener-subs-service
```

### To compile and generate the JAR file, run:

```bash
./mvnw clean package -DskipTests
```

## Configuration

The **URL Shortener Subscription Service** uses different configuration files for managing environment-specific
settings.

### application.yml

This is the main configuration file containing default settings for the application.

```yml
spring:
  application:
    name: urlshortener-subscription-service
  profiles:
    active: dev

server:
  port: 9099
```

#### Eureka Configuration

The service is registered with **Eureka Service Discovery** for dynamic service lookup.

```yml
eureka:
  client:
    enabled: true
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    status-page-url-path: /admin/management/info
    health-check-url-path: /admin/management/health
```

- **eureka.client.enabled**: Enables Eureka client registration.
- **eureka.client.service-url.defaultZone**: Points to the Eureka server for service registration.
- **eureka.instance.status-page-url-path**: Specifies the status page path.
- **eureka.instance.health-check-url-path**: Configures the health check endpoint.

#### Management & Monitoring

Spring Boot **Actuator** is used to expose management and health check endpoints.

```yml
management:
  info:
    env:
      enabled: true
  endpoints:
    web:
      exposure:
        include: health,prometheus,info
      base-path: /admin/management
  endpoint:
    health:
      show-details: always
    info:
      access: read_only
    metrics:
      access: read_only
```

- **management.info.env.enabled**: Allows environment details to be included in the `/info` endpoint.
- **management.endpoints.web.exposure.include**: Exposes `health`, `prometheus`, and `info` endpoints.
- **management.endpoints.web.base-path**: Sets the base path for management endpoints to `/admin/management`.
- **management.endpoint.health.show-details**: Always shows full health details.
- **management.endpoint.info.access**: Sets `info` access to `read_only`.
- **management.endpoint.metrics.access**: Restricts `metrics` access to `read_only`.

#### UI Configuration

Configures the dashboard URL.

```yml
ui:
  dashboard:
    url: http://localhost:3000/dashboard
```

- **ui.dashboard.url**: Defines the dashboard URL, set to `http://localhost:3000/dashboard`.

#### Subscription Service Settings

Defines default settings for subscriptions and notification-related configurations.

```yml
subscription:
  notification:
    default-logo-url: https://res.cloudinary.com/dmdbqq7fp/bysb90sd8dsjst6ieeno.png
  packs:
    initializer:
      push-default-packs-to-database: true
```

- **subscription.notification.default-logo-url**: Sets the default logo URL for notifications.
- **subscription.packs.initializer.push-default-packs-to-database**: If `true`, initializes default subscription packs
  in the database at startup.

---

### application-prod.yml

This is the main configuration file containing production settings for the application.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/urlshortener?serverTimezone=UTC&createDatabaseIfNotExist=true
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      pool-name: SubscriptionServiceConnectionPool
      maximum-pool-size: 20
      minimum-idle: 5
  jpa:
    hibernate:
      ddl-auto: update
    generate-ddl: true
    open-in-view: false
    show-sql: false
  kafka:
    bootstrap-servers: localhost:9092
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      username:
      password:
      database: 7
```

#### Kafka Configuration

This section defines the Kafka topics for different event types used by the service.

```yaml
kafka:
  payment:
    success:
      topic:
        name: urlshortener.payment.events
  user:
    registration:
      success:
        topic:
          name: user.registration.completed
  notification:
    email:
      topic:
        name: urlshortener.notifications.email
```

- **kafka.payment.success.topic.name**: The Kafka topic name for payment success events.
    - Default: urlshortener.payment.events
- **kafka.user.registration.success.topic.name**: The Kafka topic name for user registration success events.
    - Default: user.registration.completed
- **kafka.notification.email.topic.name**: The Kafka topic name for email notification events.
    - Default: urlshortener.notifications.email

## Docker Setup

The application is Dockerized for simplified deployment. The `Dockerfile` is already configured to build and run the
Spring Boot application.

The `Dockerfile` defines the build and runtime configuration for the container.

### Building the Docker Image

To build the Docker image, run the following command:

```bash
docker build -t akgarg0472/urlshortener-subscription-service:tag .
```

### Run the Docker Container

You can run the application with custom environment variables using the docker run command. For example:

```bash
docker run -p 9090:9090 \
           -e SPRING_PROFILES_ACTIVE=prod \
           akgarg0472/urlshortener-subscription-service:tag
```

This will start the container with the necessary environment variables.