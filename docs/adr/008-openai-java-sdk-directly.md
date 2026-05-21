# ADR-008: OpenAI Java SDK를 직접 사용

## 상태

수락됨

## 맥락

OpenAI 호출을 구현할 때 공식 Java SDK를 직접 사용할지, Spring AI 같은 상위 추상화를 사용할지, Raw HTTP 클라이언트를 사용할지 결정해야 한다. ADR-007(Responses API·Structured Outputs)과 맞는 구현 경로가 필요하다.

## 결정

- `ChannelInsightClient` 포트 뒤에서 **OpenAI Java SDK를 직접** 사용한다.
- Spring AI, Raw HTTP 클라이언트는 MVP **기본 경로**로 사용하지 않는다.

## 검토한 대안

- OpenAI Java SDK 직접 사용
- Spring AI
- Raw HTTP 클라이언트

## 결과

### 장점

- ADR-007(Responses API·Structured Outputs)과의 맞춤이 명확하다.
- SDK 타입을 infrastructure 어댑터 안에 격리하기 쉽다.
- MVP에서 불필요한 추상화 계층을 줄일 수 있다.

### 단점

- Spring AI의 공통 추상화·자동 설정 이점은 없다.
- SDK 버전 변경 대응을 infrastructure가 직접 맡는다.

## 구현 시 유의사항

- SDK 타입은 infrastructure 계층 밖으로 노출하지 않는다.
- application/domain은 프로젝트 소유 모델만 사용한다.
- 테스트는 fake `ChannelInsightClient`를 사용하고 OpenAI를 실제 호출하지 않는다.
- API 키·모델은 환경 변수 또는 `application.properties`로 주입한다.

## 후속 작업

- SDK 버전 업그레이드 시 infrastructure 테스트로 회귀 확인
