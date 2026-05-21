# ADR-011: Ollama 로컬 LLM 프로필

## 상태

수락됨

## 맥락

개발자는 OpenAI API 비용 없이 실제 LLM 분석 품질을 로컬에서 검증하고 싶다. M4 Max 32GB 환경에서는 Ollama로 충분한 크기의 오픈 모델을 실행할 수 있다.

## 결정

- `ChannelInsightClient` 포트 뒤에 `OllamaChannelInsightClient` infrastructure 어댑터를 추가한다.
- Spring 프로필 `ollama`가 활성화되면 Ollama `POST /api/chat` + `format: json`으로 인사이트를 생성한다.
- 기본(프로필 없음) 경로는 OpenAI Responses API를 유지한다. `dev` 프로필은 Stub 인사이트를 유지한다.
- OpenAI 빈은 `@Profile("!dev & !ollama")`로 Ollama 프로필과 동시에 로드되지 않게 한다.

## 검토한 대안

- OpenAI 호환 `/v1/chat/completions`만 사용
- Spring AI Ollama starter
- 런타임에 OpenAI 실패 시 Ollama 폴백

## 결과

### 장점

- API 키·과금 없이 로컬에서 실제 분석 흐름을 검증할 수 있다.
- 포트/어댑터 구조를 바꾸지 않고 provider만 교체한다.

### 단점

- Structured Outputs만큼 JSON 형식이 안정적이지 않을 수 있어 파싱 실패 시 502가 날 수 있다.
- 모델·Ollama 데몬을 개발 환경마다 설치·관리해야 한다.

## 구현 시 유의사항

- 기본 모델: `qwen2.5:14b` (`ollama.model`, `OLLAMA_MODEL`)
- 설정: `ollama.base-url` / `OLLAMA_BASE_URL` (기본 `http://localhost:11434`)
- 단위 테스트는 fake `OllamaChatGateway`만 사용하고 Ollama를 실제 호출하지 않는다.
- 프롬프트 본문은 `ChannelInsightPrompt`에서 OpenAI·Ollama가 공유한다.

## 후속 작업

- 로컬 품질이 충분하면 배포 환경용 Ollama 호스트 분리 검토
- JSON 스키마 검증 강화(재시도 등)
