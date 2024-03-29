# DPANG FAQ SERVER

## 🌐 프로젝트 개요

이 프로젝트는 FAQ 서비스를 지원하는 마이크로서비스로서, 사용자가 자주 묻는 질문에 대한 답변을 제공하고 관리하는 기능을 제공합니다.

이를 통해 사용자에게 신속하고 정확한 정보를 제공함으로써, 사용자 경험을 향상시키는데 중점을 두고 있습니다.

## 🔀 프로젝트 아키텍처

```mermaid
sequenceDiagram
    participant Client as Client
    participant Gateway as Spring Cloud Gateway
    participant FAQService as FAQ Service
    participant MySQL as MySQL
    
    Client ->> Gateway: 요청 전송
    Gateway ->> FAQService: 요청 전달 <br> (X-DPANG-CLIENT-ID, X-DPANG-CLIENT-ROLE 헤더 추가)
    FAQService ->> FAQService: 해당 요청 권한 식별

    opt 요청에 대한 권한이 있는 경우
        FAQService ->> MySQL: 데이터 요청
        MySQL -->> FAQService: 데이터 응답
        FAQService ->> FAQService: 응답 처리
        FAQService -->> Gateway: 응답 전송
        Gateway -->> Client: 최종 응답 전달
    end

    opt 요청에 대한 권한이 없는 경우
        FAQService -->> Gateway: 사용자 권한 없음 응답
        Gateway -->> Client: 사용자 권한 없음 응답
    end

    opt 인증 실패한 경우
        Gateway -->> Client: 인증 실패 응답
    end

```

## 🗃️ 데이터베이스 구조

FAQ 서버에서 사용하는 데이터베이스(MySQL)는 다음과 같은 테이블 구조를 가지고 있습니다

```mermaid
erDiagram
    faq {
        bigint faq_id PK "FAQ ID"
        bigint author_id "작성자 ID"
        varchar(255) question "질문"
        enum category "카테고리"
        varchar(255) answer "답변"
        datetime(6) created_at "생성 일자"
        datetime(6) updated_at "수정 일자"
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