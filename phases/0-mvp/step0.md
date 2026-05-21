# Step 0: project-scaffold

## 읽어야 할 파일

- `/AGENTS.md`
- `/docs/PRD.md`
- `/docs/ARCHITECTURE.md`
- `/docs/ADR.md`
- `/docs/adr/006-gradle-kotlin-dsl.md`

## 작업

Gradle Kotlin DSL 기반 Spring Boot 프로젝트 골격을 만든다.

포함할 것:

- `settings.gradle.kts`
- `build.gradle.kts`
- `src/main/java/com/codexlive/youtube/YoutubeChannelInsightsApplication.java`
- `src/test/java/com/codexlive/youtube/YoutubeChannelInsightsApplicationTest.java`
- `src/main/resources/application.properties`

테스트는 먼저 작성한다. 최소 smoke test는 Spring context가 뜨는지 검증한다.

## Acceptance Criteria

```bash
./gradlew test
```

## 검증 절차

1. `./gradlew test`를 실행한다.
2. Gradle wrapper가 아직 없어서 실행할 수 없으면 step 상태를 `blocked`로 기록하고, `blocked_reason`에 wrapper 생성 필요성을 쓴다.
3. 아키텍처 체크리스트를 확인한다:
   - `ARCHITECTURE.md`의 기본 패키지 구조를 따른다.
   - production code는 최소 애플리케이션 진입점만 둔다.
   - 새 테스트에는 `@DisplayName`을 사용한다.

## 금지사항

- YouTube/OpenAI adapter를 이 step에서 만들지 마라. 이유: 외부 API 연동은 별도 step에서 테스트와 함께 진행한다.
- Controller/use case/domain parser를 이 step에서 만들지 마라. 이유: 각 레이어별 TDD step을 분리한다.
- Maven 파일을 만들지 마라. 이유: Gradle Kotlin DSL을 표준으로 결정한다.
