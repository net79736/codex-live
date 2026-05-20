# 프로젝트: Java Spring 애플리케이션

## 기술 스택
- Java
- Spring Framework / Spring Boot
- JUnit 5 기반 테스트

## 아키텍처 규칙
- CRITICAL: Controller, Service, Repository 책임을 섞지 말 것.
- CRITICAL: 비즈니스 로직은 Controller가 아니라 Service 계층에 둔다.
- CRITICAL: 외부 API, DB, 시간, 랜덤 등 부작용이 있는 의존성은 테스트 가능하게 분리한다.

## 개발 프로세스
- CRITICAL: 새 기능 구현 시 반드시 테스트를 먼저 작성하고, 테스트가 통과하는 구현을 작성할 것 (TDD)
- 커밋 메시지는 conventional commits 형식을 따를 것 (feat:, fix:, docs:, refactor:)

## 명령어
./gradlew test   # Gradle wrapper 테스트
mvn test         # Maven 테스트
