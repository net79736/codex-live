# ADR-010: OpenAI Java SDK를 직접 사용

## 상태

제안됨

## 맥락

OpenAI 호출을 구현할 때 공식 Java SDK를 직접 사용할지, Spring AI 같은 상위 추상화를 사용할지, Raw HTTP 클라이언트를 사용할지 결정해야 한다.

## 결정

`ChannelInsightClient` 포트 뒤에서 OpenAI Java SDK를 직접 사용한다.

## 검토한 대안

- OpenAI Java SDK 직접 사용
- Spring AI
- Raw HTTP 클라이언트

## 결과

### 장점

- OpenAI API 기능을 직접 제어할 수 있다.
- SDK 타입을 infrastructure 어댑터 안에 격리하기 쉽다.
- MVP에서 불필요한 추상화 계층을 줄일 수 있다.

### 단점

- Spring AI의 자동 설정과 공통 추상화 이점은 사용하지 못한다.
- SDK 버전 변경 대응을 infrastructure에서 직접 관리해야 한다.

## 구현 시 유의사항

- SDK 타입은 infrastructure 계층 밖으로 노출하지 않는다.
- application/domain 계층은 프로젝트 소유 모델만 사용한다.
- 테스트에서는 fake `ChannelInsightClient`를 사용하고 OpenAI를 실제 호출하지 않는다.

## 후속 작업

- 구현 시점에 SDK 버전을 확인한다.
- API 키와 모델 설정 주입 방식을 확정한다.
