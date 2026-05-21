# ADR-007: OpenAI Responses API와 Structured Outputs 사용

## 상태

수락됨

## 맥락

채널 분석 결과는 웹 화면과 JSON API에서 안정적으로 렌더링되어야 한다. 자유 형식 텍스트만 받으면 파싱 실패, 누락 필드, UI 계약 불일치가 발생하기 쉽다.

## 결정

채널 인사이트 생성에는 OpenAI Responses API와 Structured Outputs를 사용한다.

## 검토한 대안

- Responses API + Structured Outputs
- Responses API + 프롬프트만으로 JSON 형식 요청
- Chat Completions API
- 다른 LLM 제공자

## 결과

### 장점

- `summary`, `strengths`, `opportunities`, `nextActions` 같은 결과 구조를 고정할 수 있다.
- Controller와 UI가 기대하는 응답 형식을 안정적으로 유지할 수 있다.
- 테스트에서 fake client로 같은 구조의 결과를 쉽게 대체할 수 있다.

### 단점

- 스키마를 먼저 설계해야 한다.
- 스키마 변경 시 문서, DTO, 테스트를 함께 수정해야 한다.

## 구현 시 유의사항

- application 계층은 OpenAI SDK가 아니라 `ChannelInsightClient` 포트에만 의존한다.
- infrastructure 어댑터가 OpenAI 응답을 프로젝트 모델로 변환한다.
- OpenAI를 실제 호출하는 테스트는 기본 단위 테스트에 넣지 않는다.

## 후속 작업

- 초기 모델을 확정한다.
- 인사이트 응답 JSON 스키마를 확정한다.
