# DPANG FAQ SERVER

## 🌐 프로젝트 개요

이 프로젝트는 FAQ(자주 묻는 질문) 서비스를 지원하는 마이크로서비스로 서비스의 주 목적은 사용자들이 가진 질문에 대한 적절하고 효과적인 답변을 제공하고 관리하는 것입니다.

이를 통해 사용자들의 문의를 신속하게 해결하며, 고객 만족도를 향상시키는데 중점을 두고 있습니다.

## 🗃️ 데이터베이스 구조

FAQ 서버에서 사용하는 데이터베이스(MySQL)는 다음과 같은 테이블 구조를 가지고 있습니다

```mermaid
erDiagram
    faq {
        datetime(6) created_at
        bigint faq_id PK
        datetime(6) updated_at
        bigint author_id
        varchar(255) answer
        varchar(255) question
        enum category
    }

```

## ✅ 프로젝트 실행

해당 프로젝트를 추가로 개발 혹은 실행시켜보고 싶으신 경우 아래의 절차에 따라 진행해주세요

#### 1. `secret.yml` 생성

```commandline
cd ./src/main/resources
touch secret.yml
```

#### 2. `secret.yml` 작성

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://{YOUR_DB_HOST}:{YOUR_DB_PORT}/{YOUR_DB_NAME}
    username: { YOUR_DB_USERNAME }
    password: { YOUR_DB_PASSWORD }

  application:
    name: faq-server

eureka:
  instance:
    prefer-ip-address: true

  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://{YOUR_EUREKA_SERVER_IP}:{YOUR_EUREKA_SERVER_PORT}/eureka/
```

#### 3. 프로젝트 실행

```commandline
./gradlew bootrun
```

**참고) 프로젝트가 실행 중인 환경에서 아래 URL을 통해 API 명세서를 확인할 수 있습니다**

```commandline
http://localhost:8080/swagger-ui/index.html
```