# ADR-002: YouTube/OpenAI 호출은 포트로 분리

## 상태

수락됨

## 맥락

YouTube Data API와 LLM(OpenAI·Ollama) 호출은 네트워크, API 키, 응답 형식 변경 등 **테스트하기 어려운 부작용**이 크다. application 유스케이스가 SDK나 HTTP 클라이언트에 직접 묶이면 TDD와 provider 교체가 어려워진다.

## 결정

- application 유스케이스는 외부 API에 직접 의존하지 않는다.
- YouTube 수집은 `YoutubeChannelClient` 포트에만 의존한다.
- 채널 인사이트 생성은 `ChannelInsightClient` 포트에만 의존한다.
- 실제 호출·변환은 `infrastructure` 어댑터가 담당한다.

## 검토한 대안

- Controller/Service에서 SDK 직접 호출
- 단일 `ExternalApiClient`로 YouTube·LLM 통합
- Spring `@Service` 하나에 모든 외부 연동

## 결과

### 장점

- 단위·통합 테스트에서 fake/mock으로 대체하기 쉽다.
- OpenAI ↔ Ollama ↔ Stub 등 provider 교체 시 application 코드 변경을 최소화한다.
- AGENTS.md의 헥사고널 의존 방향과 일치한다.

### 단점

- MVP 규모에 비해 인터페이스·어댑터 클래스 수가 늘어난다.
- 새 외부 연동마다 port + adapter 쌍을 추가해야 한다.

## 구현 시 유의사항

- domain/application 패키지에는 YouTube·OpenAI·Ollama SDK 타입을 import하지 않는다.
- 테스트 기본 경로는 fake `YoutubeChannelClient`, fake `ChannelInsightClient`이다.
- API 키·베이스 URL은 설정(`application.properties`, 환경 변수)으로 주입한다.

## 후속 작업

- 새 provider 추가 시 ADR과 `docs/ADR.md` 인덱스에 결정을 등록한다.
