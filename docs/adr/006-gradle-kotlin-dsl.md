# ADR-006: Gradle Kotlin DSL을 표준 빌드 도구로 사용

## 상태

수락됨

## 맥락

Spring Boot 프로젝트를 시작하기 전에 빌드 도구를 하나로 정해야 한다. 테스트 명령, 의존성 관리, TDD Guard 연동, pre-commit 검증 경로가 한 방향으로 맞아야 한다.

## 결정

Gradle Wrapper와 `build.gradle.kts`를 표준 빌드 구성으로 사용한다.

## 검토한 대안

- Gradle Kotlin DSL
- Gradle Groovy DSL
- Maven

## 결과

### 장점

- 테스트 명령을 `./gradlew test`로 통일할 수 있다.
- Kotlin DSL로 빌드 설정을 타입 친화적으로 관리할 수 있다.
- Gradle Wrapper를 사용하면 로컬 환경 차이를 줄일 수 있다.

### 단점

- Maven 예제나 명령어를 그대로 사용할 수 없다.
- Gradle Wrapper 파일을 생성하고 관리해야 한다.

## 구현 시 유의사항

- TDD Guard JUnit 5 reporter는 `build.gradle.kts`에 설정한다.
- Maven 명령은 이 프로젝트의 기본 경로로 사용하지 않는다.
- CI나 pre-commit 검증도 `./gradlew test`를 기준으로 맞춘다.

## 후속 작업

- Spring Boot와 Java 버전을 확정한 뒤 Gradle 파일을 생성한다.
- Gradle Wrapper 생성 방식을 정한다.
