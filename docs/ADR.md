# 아키텍처 결정 기록 (ADR)

## 철학

MVP는 작동하는 분석 흐름을 빠르게 검증하되, 외부 API와 비즈니스 로직을 분리해 테스트 가능성을 유지한다.

---

### ADR-001: Spring Boot 단일 애플리케이션과 정적 MVP UI

**결정**: 백엔드 API와 MVP 웹 UI를 하나의 Spring Boot 애플리케이션에서 제공한다. UI는 `src/main/resources/static`의 HTML/CSS/JavaScript 정적 리소스로 둔다.

**이유**: 초기에는 배포 단위·인증·CORS·별도 프론트 빌드 파이프라인 부담을 줄이는 것이 중요하다. URL 입력, 로딩, 결과, 오류 표시 수준의 UI는 정적 페이지와 JSON API 호출로 충분하다.

**검토한 대안**: Spring Boot 정적 리소스, 서버 사이드 템플릿(Thymeleaf 등), React/Next.js 별도 프론트엔드.

**구현 시 유의사항**: UI는 비즈니스 로직을 페이지에 넣지 않고 JSON API를 호출한다. 최소 UI 상태는 Empty, Loading, Success, Error를 따른다.

**트레이드오프**: UI가 복잡해지면 별도 SPA/Next.js로 분리하는 작업이 필요할 수 있다.

---

### ADR-002: YouTube/OpenAI 호출은 포트로 분리

**결정**: application 유스케이스는 `YoutubeChannelClient`, `ChannelInsightClient` 인터페이스에만 의존한다.

**이유**: 외부 API, 네트워크, API 키는 테스트에서 부작용이 크므로 대체 가능해야 한다.

**트레이드오프**: 작은 MVP치고 클래스 수가 늘어난다.

---

### ADR-003: MVP에서는 데이터베이스와 Repository를 사용하지 않음

**결정**: 분석 이력 저장, 사용자 계정, DB, Repository 계층은 MVP에서 제외한다. 각 분석은 요청 단위(request-scoped) 연산으로 처리한다.

**이유**: 핵심 가설은 “채널 URL 하나로 유용한 인사이트가 나오는가”이며, 저장·이력·워크스페이스는 그 가설 검증 이후 문제다.

**검토한 대안**: DB 없음, 분석 이력용 임베디드 DB(H2 등), 처음부터 PostgreSQL.

**구현 시 유의사항**: 나중에 저장이 필요하면 domain port를 먼저 정의하고 infrastructure adapter로 구현한다.

**트레이드오프**: 새로고침 후 이전 분석 결과를 서버에서 다시 볼 수 없다. 사용자가 응답을 로컬에 보관하지 않으면 결과는 요청 후 사라진다.

---

### ADR-004: 테스트 설명을 필수화

**결정**: 새 테스트 또는 수정된 테스트는 `@DisplayName` 또는 명확한 테스트 메서드명으로 검증 목적을 설명한다.

**이유**: TDD 흐름에서 테스트가 설계 문서 역할을 하려면 무엇을 검증하는지 즉시 읽혀야 한다.

**트레이드오프**: 테스트 작성 시 약간의 문서화 비용이 추가된다.

---

### ADR-005: DDD/헥사고널에 가까운 레이어링을 사용

**결정**: 패키지는 `interfaces`, `application`, `domain`, `infrastructure`, `config`로 나누고, Controller는 application 유스케이스만 호출한다.

**이유**: DDD를 학습하면서도 MVP 구현 속도를 유지하려면, 비즈니스 규칙과 외부 API 구현을 분리하는 의존 방향이 필요하다.

**트레이드오프**: 초반에는 파일과 패키지가 늘어나지만, 테스트와 리팩터링 경계가 명확해진다.

---

### ADR-006: 빌드 도구로 Gradle Kotlin DSL 사용

**결정**: Gradle Wrapper와 `build.gradle.kts`를 표준 빌드 구성으로 사용한다.

**이유**: 표준 테스트 명령(`./gradlew test`)과 의존성 관리 경로를 하나로 맞추고, CI·TDD Guard·pre-commit hook과 동일한 경로를 쓰기 위함이다.

**검토한 대안**: Gradle Kotlin DSL, Gradle Groovy DSL, Maven.

**구현 시 유의사항**: TDD Guard JUnit 5 reporter는 `build.gradle.kts`에 설정한다. Maven 명령은 이 프로젝트의 기본 경로로 사용하지 않는다.

**트레이드오프**: Maven에 익숙한 경우 Gradle DSL 학습 비용이 있다.

---

### ADR-007: OpenAI Responses API와 Structured Outputs 사용

**결정**: 채널 인사이트 생성에는 OpenAI Responses API와 Structured Outputs를 사용한다.

**이유**: 자유 형식 텍스트는 검증이 어렵고 UI/API 계약을 깨기 쉽다. `summary`, `strengths`, `opportunities`, `nextActions` 필드를 스키마로 고정하면 파싱 실패를 줄이고 Controller·UI DTO와 맞추기 쉽다.

**검토한 대안**: Responses API + Structured Outputs, Responses API + 프롬프트만 JSON 지시, Chat Completions API, 다른 LLM 제공자.

**구현 시 유의사항**: application 계층은 OpenAI SDK가 아니라 `ChannelInsightClient` 포트에만 의존한다. infrastructure 어댑터가 OpenAI 응답을 domain/application 모델로 변환한다.

**트레이드오프**: 스키마·모델 변경 시 문서와 코드를 함께 갱신해야 한다.

**후속 작업**: 초기 모델과 인사이트 응답 JSON 스키마를 구현 전에 확정한다.

---

### ADR-008: OpenAI Java SDK를 직접 사용

**결정**: `ChannelInsightClient` 포트 뒤에서 OpenAI Java SDK를 직접 사용한다. Spring AI나 Raw HTTP 클라이언트는 MVP 기본 경로로 사용하지 않는다.

**이유**: ADR-007(Responses API·Structured Outputs)과의 맞춤이 명확하고, SDK 타입을 infrastructure에만 격리할 수 있다.

**검토한 대안**: OpenAI Java SDK 직접 사용, Spring AI, Raw HTTP 클라이언트.

**구현 시 유의사항**: SDK 타입은 infrastructure 어댑터 안에만 둔다. 테스트는 OpenAI를 실제 호출하지 않고 fake `ChannelInsightClient`를 사용한다. API 키·모델은 환경 변수 또는 `application.properties`로 주입한다.

**트레이드오프**: Spring AI의 공통 추상화·자동 설정 이점은 없고, SDK 업그레이드 대응은 infrastructure가 직접 맡는다.

---

## 새 ADR 작성 템플릿

아래 형식으로 ADR-009부터 이어서 추가한다. 기존 ADR과 번호가 겹치지 않게 한다.

```markdown
### ADR-00N: {결정 제목}

**결정**: {무엇을 선택하는가}

**이유**: {왜 이 선택인가}

**검토한 대안**: {대안 A}, {대안 B}

**구현 시 유의사항**: {코드·빌드·테스트에 반영할 내용}

**트레이드오프**: {감수하는 비용}

**후속 작업**: (선택) {구현 전 확인 사항}
```
