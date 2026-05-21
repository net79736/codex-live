# Step 4: youtube-adapter

## 읽어야 할 파일

- `/AGENTS.md`
- `/docs/ARCHITECTURE.md`
- `/docs/ADR.md`
- `/phases/0-mvp/step3-output.json`

## 작업

`YoutubeChannelClient` infrastructure adapter를 구현한다.

포함할 것:

- `YoutubeDataApiChannelClient`
- YouTube API 설정 properties
- API 키 누락 처리
- YouTube 응답을 application/domain 모델로 변환

## Acceptance Criteria

```bash
./gradlew test
```

## 검증 절차

1. HTTP client를 fake/stub으로 대체하는 adapter 테스트를 먼저 작성한다.
2. 실제 YouTube API를 단위 테스트에서 호출하지 않는다.
3. API 키 누락과 채널 없음을 구분한다.

## 금지사항

- application/domain 계층에 YouTube DTO를 노출하지 마라. 이유: 외부 API 형식에 오염된다.
- 실제 API 키가 필요한 테스트를 기본 테스트에 넣지 마라. 이유: 재현성과 보안 문제가 생긴다.
