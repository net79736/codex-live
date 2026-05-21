# Step 6: static-ui

## 읽어야 할 파일

- `/AGENTS.md`
- `/docs/PRD.md`
- `/docs/UI_GUIDE.md`
- `/docs/ARCHITECTURE.md`
- `/docs/ADR.md`
- `/phases/0-mvp/step5-output.json`

## 작업

MVP 정적 UI를 만든다.

포함할 것:

- `src/main/resources/static/index.html`
- `src/main/resources/static/styles.css`
- `src/main/resources/static/app.js`
- Empty, Loading, Success, Error 상태
- `POST /api/channel-analyses` 호출

## Acceptance Criteria

```bash
./gradlew test
```

## 검증 절차

1. 기존 Java 테스트가 모두 통과하는지 확인한다.
2. UI가 Controller API 계약과 맞는지 확인한다.
3. 모바일 1컬럼, 데스크톱 2컬럼 기준을 확인한다.

## 금지사항

- React/Next.js를 추가하지 마라. 이유: MVP에서는 Spring Boot 정적 리소스로 충분하다.
- UI에 비즈니스 로직을 넣지 마라. 이유: 분석 흐름은 서버 use case가 담당한다.
