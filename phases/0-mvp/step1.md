# Step 1: domain-url-parser

## 읽어야 할 파일

- `/AGENTS.md`
- `/docs/PRD.md`
- `/docs/ARCHITECTURE.md`
- `/docs/ADR.md`
- `/phases/0-mvp/step0-output.json`

## 작업

YouTube 채널 URL을 domain 계층에서 파싱한다.

포함할 것:

- `ChannelUrl`
- `ChannelIdentifier`
- `ChannelUrlParser`
- 잘못된 URL을 표현하는 domain exception 또는 result

지원 URL:

- `https://www.youtube.com/channel/{channelId}`
- `https://www.youtube.com/@{handle}`
- `https://www.youtube.com/user/{username}`

## Acceptance Criteria

```bash
./gradlew test
```

## 검증 절차

1. 실패하는 URL parser 테스트를 먼저 작성한다.
2. 최소 구현으로 테스트를 통과시킨다.
3. domain 계층이 Spring MVC나 외부 API SDK에 의존하지 않는지 확인한다.

## 금지사항

- Controller에서 URL 파싱하지 마라. 이유: 비즈니스 규칙은 domain/application 쪽에 둔다.
- YouTube API를 호출하지 마라. 이유: 이 step은 순수 URL 규칙만 검증한다.
