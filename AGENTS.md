# 프로젝트: YouTube Channel Insights

## 기술 스택
- Java 17
- Spring Boot
- Spring MVC
- JUnit 5 기반 테스트
- YouTube Data API v3
- OpenAI Responses API

## 제품 목표
- YouTube 채널 URL을 입력하면 공개 채널 데이터를 수집하고 OpenAI로 분석해 실행 가능한 채널 인사이트를 제공한다.
- MVP는 채널 URL 입력, 채널 기본 정보 수집, AI 요약/강점/개선 기회/다음 액션 생성, 웹 화면과 JSON API 제공에 집중한다.
- 사용자 계정, OAuth, 비공개 Analytics, 분석 이력 저장, 결제, 경쟁 채널 자동 비교는 MVP 범위에서 제외한다.

## 아키텍처 규칙
- CRITICAL: Controller, Service, Repository 책임을 섞지 말 것.
- CRITICAL: `interfaces` 계층은 HTTP 요청/응답, validation, status code 처리만 담당한다.
- CRITICAL: 비즈니스 로직과 채널 분석 흐름은 Controller가 아니라 `application` 계층의 유스케이스가 조립한다.
- CRITICAL: 핵심 비즈니스 규칙은 `domain` 계층에 둔다. `domain`은 Spring MVC, 외부 API SDK, DB 구현에 의존하지 않는다.
- CRITICAL: 외부 API, DB, 시간, 랜덤 등 부작용이 있는 의존성은 테스트 가능하게 분리한다.
- CRITICAL: YouTube/OpenAI 호출은 application/domain 내부에 직접 구현하지 않고 포트/어댑터로 분리한다.
- `application`은 유스케이스 조립, 트랜잭션 경계, 포트 호출 순서를 담당한다.
- `infrastructure`는 YouTube/OpenAI 클라이언트, HTTP client, DB 같은 기술 구현을 담당한다.
- MVP에서는 Repository를 만들지 않는다. 저장 기능이 생기면 먼저 domain port를 만들고 infrastructure adapter로 구현한다.

## 권장 패키지 구조
```text
src/main/java/com/codexlive/youtube/
├── interfaces/        # REST Controller, request/response, exception handler
├── application/       # 유스케이스, facade/service, input/output DTO
├── domain/            # 모델, 값 객체, 도메인 서비스, 포트 인터페이스
├── infrastructure/    # YouTube/OpenAI 외부 API 어댑터, DB/HTTP 구현
└── config/            # Spring bean, HTTP client 설정

src/main/resources/
├── application.properties
└── static/            # MVP 웹 UI

src/test/java/com/codexlive/youtube/
├── application/       # 유스케이스 단위 테스트
├── domain/            # URL 파서, 값 객체, 도메인 규칙 테스트
└── interfaces/        # Controller 테스트
```

## 외부 의존성 규칙
- `YoutubeChannelClient`: YouTube Data API 호출 포트.
- `ChannelInsightClient`: OpenAI 분석 호출 포트.
- 테스트에서는 외부 API를 실제로 호출하지 않고 fake/mock으로 대체한다.
- API 키는 환경 변수나 설정으로 주입한다.
- `YOUTUBE_API_KEY`, `OPENAI_API_KEY`, `OPENAI_MODEL` 설정을 사용한다.

## 개발 프로세스
- CRITICAL: 새 기능 구현 시 반드시 실패하는 테스트를 먼저 작성하고, 테스트가 통과하는 최소 구현을 작성할 것 (Red-Green-Refactor).
- 실패 테스트를 만든 뒤 가능하면 해당 테스트가 실제로 실패하는 것을 확인한다.
- 새 테스트 또는 수정된 테스트는 `@DisplayName` 또는 명확한 테스트 메서드명으로 검증 목적을 설명한다.
- 성공 케이스뿐 아니라 잘못된 URL, API 키 누락, 외부 API 실패 같은 실패 케이스 테스트를 먼저 설계한다.
- 테스트에서는 Given / When / Then 흐름이 드러나게 작성한다.
- 구현 전 `docs/PRD.md`, `docs/ARCHITECTURE.md`, `docs/ADR.md`를 확인한다.
- 커밋 메시지는 conventional commits 형식을 따를 것 (feat:, fix:, docs:, refactor:)

## 오류 처리 기준
- 잘못된 URL: `400 Bad Request`
- 채널 없음: `400 Bad Request`
- API 키 누락: `503 Service Unavailable`
- 외부 API 실패: `502 Bad Gateway`

## 명령어
- `./gradlew test`   # Gradle wrapper 테스트
- `mvn test`         # Maven 테스트
- 실제 Gradle/Maven 파일이 생기면 JUnit 5 test result reporter 설정을 추가해 TDD Guard가 테스트 결과를 읽을 수 있게 한다.
