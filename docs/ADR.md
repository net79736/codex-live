# 아키텍처 결정 기록 (ADR)

## 문서 구조

| 문서 | 역할 |
|------|------|
| **`docs/ADR.md`** (이 파일) | 결정 **인덱스** — 한 줄 요약, 상세 문서 링크 |
| **`docs/adr/NNN-slug.md`** | 결정 **상세** — 맥락, 대안, 장단점, 구현 주의사항 |

**번호 `NNN`은 인덱스와 상세 파일에서 반드시 같아야 한다.**  
새 ADR은 `docs/adr/000-template.md`를 복사해 `docs/adr/NNN-slug.md`를 만든 뒤, 이 파일 목록에 한 줄 요약과 링크를 추가한다.

## 철학

MVP는 작동하는 분석 흐름을 빠르게 검증하되, 외부 API와 비즈니스 로직을 분리해 테스트 가능성을 유지한다.

---

## ADR 목록

| 번호 | 제목 | 결정 요약 | 상세 |
|------|------|-----------|------|
| [001](adr/001-spring-boot-static-mvp-ui.md) | Spring Boot 단일 앱 + 정적 MVP UI | API·UI를 한 Spring Boot 앱, UI는 `static/` | [상세](adr/001-spring-boot-static-mvp-ui.md) |
| [002](adr/002-ports-for-external-apis.md) | 외부 API는 포트로 분리 | `YoutubeChannelClient`, `ChannelInsightClient` | [상세](adr/002-ports-for-external-apis.md) |
| [003](adr/003-no-database-for-mvp.md) | MVP DB/Repository 없음 | 요청 단위 분석, 이력 저장 제외 | [상세](adr/003-no-database-for-mvp.md) |
| [004](adr/004-test-display-names-required.md) | 테스트 설명 필수 | `@DisplayName` 또는 명확한 메서드명 | [상세](adr/004-test-display-names-required.md) |
| [005](adr/005-ddd-hexagonal-layering.md) | DDD/헥사고널 레이어링 | `interfaces` → `application` → `domain` ← `infrastructure` | [상세](adr/005-ddd-hexagonal-layering.md) |
| [006](adr/006-gradle-kotlin-dsl.md) | Gradle Kotlin DSL | `./gradlew test`, `build.gradle.kts` | [상세](adr/006-gradle-kotlin-dsl.md) |
| [007](adr/007-openai-responses-structured-outputs.md) | Responses + Structured Outputs | 인사이트 필드 스키마 고정 | [상세](adr/007-openai-responses-structured-outputs.md) |
| [008](adr/008-openai-java-sdk-directly.md) | OpenAI Java SDK 직접 사용 | Spring AI·Raw HTTP는 MVP 기본 경로 아님 | [상세](adr/008-openai-java-sdk-directly.md) |
| [011](adr/011-ollama-local-llm-profile.md) | Ollama 로컬 LLM 프로필 | 프로필 `ollama`, 포트 어댑터 추가 | [상세](adr/011-ollama-local-llm-profile.md) |

> **009, 010** 번호는 비어 있다. 다음 신규 ADR은 **ADR-009**부터 부여하거나, Ollama 이후 연속 번호로 **ADR-012**를 쓸 수 있다. 팀 내에서 하나만 정해 일관되게 쓴다.

---

## 인덱스 요약 (빠른 참조)

### ADR-001: Spring Boot 단일 애플리케이션과 정적 MVP UI

백엔드 API와 MVP 웹 UI를 하나의 Spring Boot 앱에서 제공한다. UI는 `src/main/resources/static` 정적 리소스. → [상세](adr/001-spring-boot-static-mvp-ui.md)

### ADR-002: YouTube/OpenAI 호출은 포트로 분리

application은 `YoutubeChannelClient`, `ChannelInsightClient`에만 의존한다. → [상세](adr/002-ports-for-external-apis.md)

### ADR-003: MVP에서는 데이터베이스와 Repository를 사용하지 않음

분석 이력·DB·Repository는 MVP 제외. 요청 단위 연산. → [상세](adr/003-no-database-for-mvp.md)

### ADR-004: 테스트 설명을 필수화

`@DisplayName` 또는 명확한 테스트 메서드명. Given/When/Then. → [상세](adr/004-test-display-names-required.md)

### ADR-005: DDD/헥사고널에 가까운 레이어링

`interfaces`, `application`, `domain`, `infrastructure`, `config`. Controller는 application만 호출. → [상세](adr/005-ddd-hexagonal-layering.md)

### ADR-006: 빌드 도구로 Gradle Kotlin DSL 사용

Gradle Wrapper, `build.gradle.kts`, `./gradlew test`. → [상세](adr/006-gradle-kotlin-dsl.md)

### ADR-007: OpenAI Responses API와 Structured Outputs

`summary`, `strengths`, `opportunities`, `nextActions` 스키마 고정. → [상세](adr/007-openai-responses-structured-outputs.md)

### ADR-008: OpenAI Java SDK를 직접 사용

`ChannelInsightClient` 뒤 OpenAI Java SDK. ADR-007과 함께 적용. → [상세](adr/008-openai-java-sdk-directly.md)

### ADR-011: Ollama 로컬 LLM 프로필

프로필 `ollama`, `OllamaChannelInsightClient`, OpenAI 빈 `!dev & !ollama`. → [상세](adr/011-ollama-local-llm-profile.md)

---

## 새 ADR 추가 절차

1. 사용하지 않는 번호를 정한다(현재 비어 있음: **009**, **010** 또는 **012+**).
2. `docs/adr/000-template.md`를 복사해 `docs/adr/NNN-kebab-case-slug.md`를 작성한다.
3. 이 파일 **목록 표**와 **인덱스 요약**에 한 줄 요약과 링크를 추가한다.
4. `docs/ARCHITECTURE.md`, `AGENTS.md`, 관련 `phases/` step에 필요 시 링크를 반영한다.

상세 본문 형식은 `docs/adr/000-template.md`를 따른다. 인덱스에는 **중복으로 긴 본문을 붙이지 않는다.**
