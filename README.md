# DPANG FAQ SERVER

## 🌐 프로젝트 개요

이 프로젝트는 FAQ(자주 묻는 질문) 서비스를 지원하는 마이크로서비스로 서비스의 주 목적은 사용자들이 가진 질문에 대한 적절하고 효과적인 답변을 제공하고 관리하는 것입니다.

이를 통해 사용자들의 문의를
신속하게 해결하며, 고객 만족도를 향상시키는데 중점을 두고 있습니다.

## 🛠️ 프로젝트 개발 환경

### OS 환경

> macOS Sonoma  
> Windows 10

### 개발 도구

> IDE: Intellij IDEA  
> Java 17

### 빌드 도구

> Gradle

### 주요 플러그인 버전

> 'org.springframework.boot': '3.2.1'  
> 'org.jetbrains.kotlin.jvm': '1.9.21'  
> 'org.jetbrains.kotlin.plugin.spring': '1.9.21'

## 🔀 프로젝트 아키텍처

아래의 Sequence Diagram은 본 프로젝트의 주요 컴포넌트인 Spring Cloud Gateway, Redis, 그리고 FAQ 서비스 간의 상호작용을 보여줍니다.

```mermaid
sequenceDiagram
    participant Client as Client
    participant Gateway as Spring Cloud Gateway
    participant FAQService as FAQ Service
    participant MySQL as MySQL
    Client ->> Gateway: 요청 전송 (JWT 토큰 포함)
    Gateway ->> Gateway: 요청 인증 및 인가

    alt 인증 성공
        Gateway ->> FAQService: 요청 전달 (X-DPANG-CLIENT-ID 헤더 추가)
        FAQService ->> FAQService: 해당 요청 권한 식별

        alt 요청이 역할에 적합
            FAQService ->> MySQL: 데이터 요청
            MySQL -->> FAQService: 데이터 응답
            FAQService ->> FAQService: 응답 처리
            FAQService -->> Gateway: 응답 전송
            Gateway -->> Client: 최종 응답 전달

        else 요청이 역할에 부적합
            FAQService -->> Gateway: 사용자 권한 없음 응답
            Gateway -->> Client: 사용자 권한 없음 응답
        end

    else 인증 실패
        Gateway -->> Client: 인증 실패 응답
    end

```

위의 시퀀스 다이어그램을 통해 볼 수 있듯이, 클라이언트의 요청은 먼저 Spring Cloud Gateway를 통과합니다. 이 단계에서 사용자의 인증 정보가 검증되며, 인증이 정상적으로 이루어진 경우에만 서비스
요청이 진행됩니다.

인증이 성공적으로 완료되면, 'X-DPANG-CLIENT-ID'라는 이름의 커스텀 헤더에 사용자 ID가 첨부됩니다. 이 헤더는 FAQ 서비스로의 요청과 함께 전송되며, FAQ 서비스는 이를 통해 요청한 사용자를
정확히 식별할 수
있습니다. 이렇게 식별된 사용자의 요청은 적절한 처리 과정을 거친 후 결과값이 반환됩니다.

## 🗃️ 데이터베이스 구조

FAQ 서버에서 사용하는 데이터베이스(MySQL)는 다음과 같은 테이블 구조를 가지고 있습니다

```mermaid
erDiagram
    FAQ {
        datetime(6) created_at
        bigint faq_id PK
        datetime(6) updated_at
        binary(16) author_id
        varchar(255) answer
        varchar(255) question
        enum category
    }

```

## 📑 API 명세

FAQ 서비스는 다음과 같은 API를 제공합니다:

1. FAQ 생성: 새로운 FAQ 정보를 시스템에 추가합니다. (관리자, 슈퍼 관리자 권한)

2. FAQ 조회: 시스템에 등록된 FAQ 정보를 조회합니다. (사용자, 관리자, 슈퍼 관리자 권한)
    - 카테고리별 조회: 특정 카테고리에 속한 FAQ를 조회합니다.
    - 식별자별 조회: 고유 식별자를 통해 특정 FAQ를 조회합니다.

3. FAQ 수정: 기존에 등록된 FAQ 정보를 업데이트합니다. (관리자, 슈퍼 관리자 권한)

4. FAQ 삭제: 특정 FAQ 정보를 시스템에서 제거합니다. (관리자, 슈퍼 관리자 권한)

## 🔬 테스트

프로젝트에서는 다음과 같이 단위 테스트, 통합 테스트 두 가지 방법으로 테스트를 진행하였습니다.

### 단위 테스트

- kotest의 Behavior Driven Development(BDD) 스타일을 적용, 총 17개의 단위 테스트를 수행하였습니다.
- 이 과정에서, 각 메서드가 의도된 기능을 정밀하게 수행하는지, 각각의 결과가 기대한 바와 일치하는지 검증하였습니다.

### 통합 테스트

- 서비스의 여러 컴포넌트가 함께 잘 작동하는지 검증하기 위해 진행하였습니다.
    - 서비스 테스트: 데이터가 정확하게 DB에 저장되는지를 확인하기 위해 Junit을 사용하여 총 4개의 테스트를 진행하였습니다.
    - 컨트롤러 테스트: 사용자의 요청에 따른 응답을 검증하기 위해 Junit을 사용하여 총 44개의 테스트를 진행하였습니다. 이 테스트들은 사용자 권한 확인, 특정 상황에서 올바른 상태 코드를 반환하는지, 예외
      처리가 잘 이루어지는지 등을 검증하였습니다

자세한 내용과 테스트 코드, 그리고 실행 결과는 [여기](./src/test/kotlin/kea/dpang/faq)에서 확인하실 수 있습니다.

## ✅ 프로젝트 실행

해당 프로젝트를 추가로 개발 혹은 실행시켜보고 싶으신 경우 아래의 절차에 따라 진행해주세요

#### 1. `secret.yml` 생성

```commandline
cd ./src/main/resources
touch secret.yml
```

#### 2. `secret.yml` 작성

```text
spring:
  datasource:
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://{YOUR_DB_HOST}:{YOUR_DB_PORT}/{YOUR_DB_NAME}
      username: {YOUR_DB_USERNAME}
      password: {YOUR_DB_PASSWORD}

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