# Step 2: application-usecase-ports

## 읽어야 할 파일

- `/AGENTS.md`
- `/docs/PRD.md`
- `/docs/ARCHITECTURE.md`
- `/docs/ADR.md`
- `/phases/0-mvp/step1-output.json`

## 작업

채널 분석 유스케이스와 외부 의존성 포트를 정의한다.

포함할 것:

- `AnalyzeChannelUseCase`
- `AnalyzeChannelCommand`
- `ChannelAnalysisResult`
- `YoutubeChannelClient` port
- `ChannelInsightClient` port
- `ChannelSnapshot`
- `ChannelInsight`

## Acceptance Criteria

```bash
./gradlew test
```

## 검증 절차

1. fake `YoutubeChannelClient`, fake `ChannelInsightClient`를 사용하는 application 테스트를 먼저 작성한다.
2. URL 파싱 -> YouTube 조회 -> OpenAI 인사이트 생성 순서를 검증한다.
3. 외부 API를 실제 호출하지 않는다.

## 금지사항

- infrastructure adapter를 만들지 마라. 이유: 포트와 유스케이스를 먼저 고정한다.
- Controller를 만들지 마라. 이유: HTTP 계층은 다음 step에서 분리한다.
