# ADR-001: Spring Boot 단일 애플리케이션과 정적 MVP UI

## 상태

수락됨

## 맥락

MVP는 채널 URL 입력 → 분석 → 결과 표시까지 한 흐름을 빠르게 검증해야 한다. 별도 프론트엔드 앱, 인증, CORS, 별도 배포 파이프라인은 초기에 부담이다. UI는 URL 입력, 로딩, 결과, 오류 표시 수준이면 충분하다.

## 결정

- 백엔드 API와 MVP 웹 UI를 **하나의 Spring Boot 애플리케이션**에서 제공한다.
- MVP 웹 UI는 `src/main/resources/static` 아래 HTML/CSS/JavaScript **정적 리소스**로 둔다.

## 검토한 대안

- Spring Boot 정적 HTML/CSS/JavaScript
- 서버 사이드 템플릿(Thymeleaf 등)
- React 또는 Next.js 별도 프론트엔드

## 결과

### 장점

- 배포 단위가 하나라 로컬 실행·초기 배포가 단순하다.
- CORS, 별도 프론트 빌드, 프론트 전용 CI 부담을 줄일 수 있다.
- JSON API와 UI를 같은 버전으로 함께 배포할 수 있다.

### 단점

- UI가 커지면 정적 페이지만으로는 구조가 답답해질 수 있다.
- 복잡한 클라이언트 상태·라우팅이 필요해지면 SPA/Next.js 분리를 검토해야 한다.

## 구현 시 유의사항

- UI는 비즈니스 로직을 페이지에 넣지 않고 JSON API만 호출한다.
- 최소 화면 상태는 Empty, Loading, Success, Error를 지원한다.
- 정적 파일 경로: `src/main/resources/static`

## 후속 작업

- MVP 화면 문구·오류 메시지 확정
- 결과 복사·보내기는 MVP 범위에 포함할지 제품에서 결정
