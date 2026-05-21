# ADR-005: DDD/헥사고널에 가까운 레이어링을 사용

## 상태

수락됨

## 맥락

MVP를 빠르게 만들면서도 DDD 학습 목표와 테스트 가능한 경계가 필요하다. Controller에 비즈니스·외부 API가 섞이면 리팩터링과 단위 테스트가 어려워진다.

## 결정

- 패키지는 `interfaces`, `application`, `domain`, `infrastructure`, `config`로 나눈다.
- 의존 방향: `interfaces → application → domain ← infrastructure`
- Controller(`interfaces`)는 **application 유스케이스만** 호출한다.
- `domain`은 Spring MVC, 외부 API SDK, DB 구현에 의존하지 않는다.

## 검토한 대안

- 전통 3계층(Controller / Service / Repository)만 사용
- 모놀리식 Service에 모든 로직
- 모듈 분리 없이 패키지 평면 구조

## 결과

### 장점

- 비즈니스 규칙과 인프라 구현을 분리해 테스트·교체가 쉽다.
- AGENTS.md CRITICAL 규칙과 `docs/ARCHITECTURE.md`와 일치한다.
- provider 추가(OpenAI, Ollama) 시 경계가 명확하다.

### 단점

- 초반 파일·패키지 수가 늘어난다.
- 작은 변경에도 “어느 계층인가” 판단 비용이 있다.

## 구현 시 유의사항

- Controller가 domain service나 infrastructure adapter를 직접 호출하지 않는다.
- application은 orchestration; 검증 가능한 규칙은 domain으로 내린다.
- MVP에서는 Repository를 만들지 않는다(ADR-003).

## 후속 작업

- 저장 기능 도입 시 domain port 정의 후 adapter 구현(ADR-003)
