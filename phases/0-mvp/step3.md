# Step 3: api-controller-errors

## 읽어야 할 파일

- `/AGENTS.md`
- `/docs/PRD.md`
- `/docs/ARCHITECTURE.md`
- `/docs/ADR.md`
- `/phases/0-mvp/step2-output.json`

## 작업

JSON API Controller와 오류 매핑을 만든다.

포함할 것:

- `POST /api/channel-analyses`
- request DTO
- response DTO
- `GlobalExceptionHandler`
- 잘못된 URL, 채널 없음, API 키 누락, 외부 API 실패 매핑

## Acceptance Criteria

```bash
./gradlew test
```

## 검증 절차

1. Controller slice 테스트를 먼저 작성한다.
2. Controller는 application use case만 호출하는지 확인한다.
3. status code와 오류 메시지를 검증한다.

## 금지사항

- Controller에서 YouTube/OpenAI client를 직접 호출하지 마라. 이유: 계층 책임 위반이다.
- Controller에 비즈니스 판단을 넣지 마라. 이유: use case/domain에서 처리해야 한다.
