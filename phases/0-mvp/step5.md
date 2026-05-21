# Step 5: openai-adapter

## 읽어야 할 파일

- `/AGENTS.md`
- `/docs/ARCHITECTURE.md`
- `/docs/ADR.md`
- `/docs/adr/007-openai-responses-structured-outputs.md`
- `/docs/adr/008-openai-java-sdk-directly.md`
- `/phases/0-mvp/step4-output.json`

## 작업

`ChannelInsightClient` infrastructure adapter를 구현한다.

포함할 것:

- `OpenAiChannelInsightClient`
- OpenAI API 설정 properties
- Structured Outputs 응답 스키마
- API 키 누락 처리
- OpenAI 실패 처리

## Acceptance Criteria

```bash
./gradlew test
```

## 검증 절차

1. OpenAI client를 fake/stub으로 대체하는 adapter 테스트를 먼저 작성한다.
2. 실제 OpenAI API를 단위 테스트에서 호출하지 않는다.
3. SDK 타입이 infrastructure 밖으로 노출되지 않는지 확인한다.

## 금지사항

- 프롬프트만으로 JSON 형식을 기대하지 마라. 이유: 결과 구조가 깨질 수 있다.
- application/domain 계층에 OpenAI SDK 타입을 노출하지 마라. 이유: 계층 책임 위반이다.
