# 아키텍처

## 기술 스택
- Java 17
- Spring Boot
- Spring MVC
- JUnit 5
- YouTube Data API v3
- OpenAI Responses API (기본 프로필)
- Ollama (로컬 `ollama` 프로필)

## 계층 규칙
- `interfaces`는 HTTP 요청/응답, validation, status code, exception mapping만 담당한다.
- `application`은 유스케이스 조립, 트랜잭션 경계, 도메인 서비스와 포트 호출 순서를 담당한다.
- `domain`은 모델, 값 객체, 도메인 규칙, 포트 인터페이스를 담당한다.
- `infrastructure`는 YouTube/OpenAI, HTTP client, DB 같은 기술 구현을 담당한다.
- 외부 API 호출은 application/domain 내부에 직접 넣지 않고 포트/어댑터로 분리한다.
- Repository는 MVP에서 사용하지 않는다. 저장 기능이 생기면 domain port를 먼저 만들고 infrastructure adapter로 구현한다.

## 디렉토리 구조
```text
src/main/java/com/codexlive/youtube/
├── interfaces/        # REST Controller, request/response, exception handler
├── application/       # 유스케이스, facade/service, input/output DTO
├── domain/            # 모델, 값 객체, 도메인 서비스, 포트 인터페이스
├── infrastructure/    # YouTube/OpenAI 외부 API 어댑터, DB/HTTP 구현
└── config/            # Spring bean, HTTP client 설정

src/main/resources/
├── application.properties
└── static/            # MVP 웹 UI

src/test/java/com/codexlive/youtube/
├── application/       # 유스케이스 단위 테스트
├── domain/            # URL 파서, 값 객체, 도메인 규칙 테스트
└── interfaces/        # Controller 테스트
```

## 데이터 흐름
```text
사용자 입력
→ Web UI 또는 POST /api/channel-analyses
→ ChannelAnalysisController
→ ChannelAnalysisUseCase 또는 ChannelAnalysisFacade
→ ChannelUrlParser
→ YoutubeChannelClient
→ ChannelInsightClient
→ ChannelAnalysisResult
→ JSON 응답 / 화면 렌더링
```

## 외부 의존성
- `YoutubeChannelClient`: YouTube Data API 호출 포트
- `ChannelInsightClient`: 채널 인사이트 분석 포트 (OpenAI 기본, Ollama `ollama` 프로필)
- 테스트에서는 두 포트를 fake/mock으로 대체한다.

## DDD 설계 기준
- 작은 MVP라도 `interfaces -> application -> domain -> infrastructure` 의존 방향을 지킨다.
- Controller는 application 유스케이스만 호출하고 domain service나 infrastructure adapter를 직접 호출하지 않는다.
- application 유스케이스는 orchestration을 담당하고, 검증 가능한 비즈니스 규칙은 domain 객체나 domain service로 내린다.
- domain은 프레임워크 annotation과 외부 API DTO에 오염되지 않게 유지한다.
- infrastructure adapter는 외부 응답을 domain/application에서 쓰는 모델로 변환한 뒤 반환한다.

## 설정
- `YOUTUBE_API_KEY`: YouTube Data API 키
- `OPENAI_API_KEY`: OpenAI API 키 (기본 프로필)
- `OPENAI_MODEL`: OpenAI 분석 모델
- `OLLAMA_BASE_URL`: Ollama API 주소 (기본 `http://localhost:11434`, `ollama` 프로필)
- `OLLAMA_MODEL`: Ollama 분석 모델 (기본 `qwen2.5:14b`, `ollama` 프로필)

### Spring 프로필
- `dev`: Stub 인사이트 (OpenAI·Ollama 불필요)
- `ollama`: 로컬 Ollama 인사이트 (OpenAI 키 불필요)
- 기본: OpenAI 인사이트

## 오류 처리
- 잘못된 URL: `400 Bad Request`
- 채널 없음: `400 Bad Request`
- API 키 누락: `503 Service Unavailable`
- 외부 API 실패: `502 Bad Gateway`
