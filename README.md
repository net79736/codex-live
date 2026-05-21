# Codex Live

이 저장소는 Java Spring 프로젝트를 Codex로 작업하기 좋게 만들기 위한 기본 세팅이다.

Codex를 처음 쓰는 사람 기준으로 말하면, 이 repo에는 크게 네 종류의 파일이 있다.

1. Codex가 프로젝트 규칙을 읽는 파일
2. Codex가 자동으로 실행하는 hook 설정
3. 반복 작업을 Codex skill로 정리한 파일
4. 사람이 직접 읽고 수정하는 기획/아키텍처 문서

아래 내용을 위에서부터 천천히 읽으면 된다.

## 제일 먼저 알아야 할 것

Codex는 그냥 채팅창이 아니라, 이 프로젝트 안의 파일을 읽고 수정하고 명령어도 실행할 수 있는 코딩 에이전트다.

그래서 Codex에게 일을 시킬 때 중요한 것은 두 가지다.

- 프로젝트 규칙을 어디에 적어두는가
- Codex가 위험한 행동을 하지 않도록 어떤 자동 장치를 걸어두는가

이 repo에서는 그 역할을 아래 파일들이 한다.

```text
AGENTS.md
.codex/config.toml
.codex/hooks.json
.codex/hooks/
.codex/skills/
.codex/tdd-guard/data/instructions.md
.githooks/pre-commit
docs/
scripts/
```

## AGENTS.md

`AGENTS.md`는 Codex가 기본으로 읽는 프로젝트 설명서다.

쉽게 말하면 Codex에게 주는 "우리 프로젝트에서 지켜야 할 규칙"이다. 사람이 매번 설명하지 않아도 Codex가 이 파일을 보고 기본 방향을 잡는다.

현재 이 파일에는 Java Spring 프로젝트 기준으로 이런 내용이 들어 있다.

- Java / Spring Framework / Spring Boot 프로젝트다.
- 테스트는 JUnit 5 기준이다.
- Controller, Service, Repository 책임을 섞지 않는다.
- 비즈니스 로직은 Controller가 아니라 Service에 둔다.
- 새 기능은 TDD로 만든다.
- 커밋 메시지는 `feat:`, `fix:`, `docs:` 같은 conventional commits 형식을 따른다.

Codex가 이상한 방향으로 코드를 짜면 보통 이 파일의 규칙이 부족하거나 애매한 경우가 많다. 프로젝트 규칙을 바꾸고 싶으면 여기부터 수정하면 된다.

## .codex/config.toml

`.codex/config.toml`은 Codex 설정 파일이다.

현재는 hook 기능을 켜는 역할만 한다.

```toml
[features]
hooks = true
```

실제 hook 목록은 `.codex/hooks.json`에 따로 있다. 이렇게 분리한 이유는 hook 목록을 한눈에 보기 쉽기 때문이다.

## .codex/hooks.json

`.codex/hooks.json`은 Codex가 특정 순간에 자동으로 실행할 명령을 적어두는 파일이다.

hook은 "어떤 일이 일어나면 자동으로 이걸 실행해라"라는 뜻이다.

현재 들어 있는 hook은 네 종류다.

### SessionStart

Codex 세션이 시작되거나 다시 열릴 때 실행된다.

현재 실행되는 것:

- `.codex/hooks/session_start.py`
- `.codex/hooks/tdd_guard.py`

역할:

- Codex에게 이 repo가 어떤 구조인지 짧게 알려준다.
- TDD Guard를 시작 상태에 맞게 준비한다.

### PreToolUse

Codex가 도구를 쓰기 직전에 실행된다.

현재 두 가지가 걸려 있다.

첫 번째는 Bash 명령어 보호다.

```text
.codex/hooks/pre_tool_use_policy.py
```

이 스크립트는 위험한 명령을 막는다.

예:

- `rm -rf`
- `git reset --hard`
- `git push --force`
- `DROP TABLE`

두 번째는 파일 수정 전에 TDD Guard를 실행하는 것이다.

```text
.codex/hooks/tdd_guard.py
```

Codex가 `Write`, `Edit`, `MultiEdit`, `TodoWrite` 같은 파일 수정 도구를 쓰기 전에 TDD Guard가 개입한다.

### UserPromptSubmit

사용자가 프롬프트를 보낼 때 실행된다.

TDD Guard는 `tdd-guard on`, `tdd-guard off` 같은 명령을 처리할 수 있다. 이 hook이 있어야 대화 중에 TDD Guard를 켜고 끄는 흐름이 동작한다.

### Stop

Codex가 답변을 끝내려고 할 때 실행된다.

현재 실행되는 것:

```text
.codex/hooks/stop_validate.py
```

역할:

- Java Spring 프로젝트면 테스트 명령을 찾아 실행한다.
- Gradle이면 `./gradlew test` 또는 `gradle test`
- Maven이면 `./mvnw test` 또는 `mvn test`
- 테스트가 실패하면 Codex가 그냥 끝내지 못하게 막는다.

아직 이 repo에는 `build.gradle`, `build.gradle.kts`, `pom.xml`이 없으면 이 hook은 아무 것도 하지 않고 넘어간다.

## .codex/hooks/

`.codex/hooks/` 폴더에는 `hooks.json`에서 호출하는 실제 스크립트가 들어 있다.

현재 파일은 이렇다.

```text
.codex/hooks/session_start.py
.codex/hooks/pre_tool_use_policy.py
.codex/hooks/tdd_guard.py
.codex/hooks/stop_validate.py
```

각 파일 역할:

- `session_start.py`: Codex 세션 시작 시 프로젝트 context를 추가한다.
- `pre_tool_use_policy.py`: 위험한 Bash 명령어를 차단한다.
- `tdd_guard.py`: `npx -y tdd-guard@latest`를 실행하는 wrapper다.
- `stop_validate.py`: 작업 종료 전에 Gradle/Maven/npm 테스트 명령을 감지해서 실행한다.

보통은 이 파일들을 자주 수정하지 않아도 된다. hook 동작을 바꾸고 싶을 때만 보면 된다.

## TDD Guard

TDD Guard는 Codex가 테스트 없이 구현부터 해버리는 것을 막기 위한 도구다.

현재 설정은 Java Spring 프로젝트 기준으로 맞춰져 있다.

설정 파일:

```text
.codex/tdd-guard/data/instructions.md
```

이 repo에서는 TDD Guard 설정을 Codex 기준 경로인 `.codex/tdd-guard/data/`에서 관리한다.
`.codex/hooks/tdd_guard.py`가 `tdd-guard` 실행 전에 필요한 호환 데이터를 `.codex/tdd-guard/compat/` 아래에 준비하므로, repo 루트의 `.claude`는 사용하지 않는다.

이 파일에는 이런 규칙이 들어 있다.

- production code를 바꾸기 전에 JUnit 5 테스트를 먼저 작성한다.
- 버그 수정이면 재현 테스트를 먼저 만든다.
- 테스트가 요구하는 만큼만 구현한다.
- 테스트가 통과한 뒤에만 refactor한다.

중요한 점:

Codex hook만으로 TDD Guard가 완벽해지는 것은 아니다. Java Spring 프로젝트에서는 JUnit5 reporter도 build file에 추가해야 테스트 결과가 TDD Guard에 전달된다.

Gradle Kotlin 예:

```kotlin
testImplementation("io.github.nizos:tdd-guard-junit5:0.1.0")
```

Maven 예:

```xml
<dependency>
  <groupId>io.github.nizos</groupId>
  <artifactId>tdd-guard-junit5</artifactId>
  <version>0.1.0</version>
  <scope>test</scope>
</dependency>
```

나중에 실제 Spring 프로젝트의 `build.gradle.kts` 또는 `pom.xml`이 생기면 이 설정을 붙여야 한다.

## .codex/skills/

`.codex/skills/`는 Codex에게 특정 작업 방식을 알려주는 폴더다.

hook이 "자동 실행 장치"라면, skill은 "작업 매뉴얼"에 가깝다.

현재 skill은 두 개다.

```text
.codex/skills/harness/SKILL.md
.codex/skills/harness-review/SKILL.md
```

### harness

`harness` skill은 큰 작업을 phase와 step으로 쪼개서 진행하는 방법을 설명한다.

예를 들어 "회원가입 기능 만들어줘" 같은 큰 작업이 있으면 바로 코드를 짜는 대신:

1. 문서를 읽고
2. 작업을 step으로 나누고
3. `phases/` 아래에 step 파일을 만들고
4. `scripts/execute.py`로 순서대로 실행하는 방식이다.

### harness-review

`harness-review` skill은 변경사항을 리뷰할 때 쓰는 체크리스트다.

확인하는 것:

- 아키텍처 문서를 지켰는가
- ADR의 기술 선택을 벗어나지 않았는가
- 테스트가 있는가
- `AGENTS.md`의 CRITICAL 규칙을 어기지 않았는가
- 빌드/테스트가 통과하는가

## docs/

`docs/`는 사람이 프로젝트 의도를 적는 곳이다.

현재 파일:

```text
docs/PRD.md
docs/ARCHITECTURE.md
docs/UI_GUIDE.md
docs/ADR.md
```

읽는 순서:

1. `docs/PRD.md`
   - 제품 요구사항 정의서다.
   - 문제 정의, 사용자, 유저 스토리, 유저 플로우, 화면 상태, MVP 요구사항, 성공 기준을 적는다.

2. `docs/ARCHITECTURE.md`
   - 프로젝트 구조와 계층 규칙을 적는 문서다.
   - Java Spring이면 controller/service/repository/domain 같은 구조를 여기에 정리하면 된다.

3. `docs/ADR.md`
   - 기술 선택의 이유를 기록하는 문서다.
   - 예: 왜 Spring Boot를 쓰는지, DB는 왜 PostgreSQL인지, 테스트 전략은 무엇인지.

4. `docs/UI_GUIDE.md`
   - UI가 있는 프로젝트라면 화면 디자인 방향을 적는다.
   - API 서버만 있는 백엔드 프로젝트라면 비워두거나 최소화해도 된다.

Codex가 `docs/`를 읽는 시점:

1. 사용자가 직접 요청할 때
   - 예: "docs 먼저 읽고 구현해줘", "PRD 기준으로 리뷰해줘".

2. 작업 성격상 문서가 필요하다고 판단될 때
   - 큰 기능 구현, 설계 변경, UI 작업, 아키텍처 판단, 리뷰처럼 제품 의도나 구조 기준이 필요한 경우 Codex가 먼저 관련 문서를 확인한다.

3. Harness 실행 도구를 사용할 때
   - `scripts/execute.py`는 step 실행 전에 `AGENTS.md`와 `docs/*.md`를 읽어 Codex 프롬프트에 함께 넣는다.

4. Harness skill이나 review skill을 사용할 때
   - `.codex/skills/harness/SKILL.md`는 작업 계획을 만들 때 `docs/`를 읽도록 안내한다.
   - `.codex/skills/harness-review/SKILL.md`는 리뷰할 때 `docs/ARCHITECTURE.md`, `docs/ADR.md` 등을 기준으로 확인한다.

중요한 점:

- 일반 대화마다 Codex가 `docs/` 전체를 자동으로 항상 읽는 것은 아니다.
- 항상 지켜야 하는 짧고 강한 규칙은 `AGENTS.md`에 둔다.
- 제품 의도, 유저 플로우, 아키텍처 배경처럼 길고 상세한 기준은 `docs/`에 둔다.
- 그래서 큰 작업을 시킬 때는 "docs 읽고"라고 말하면 가장 확실하다.

## scripts/

`scripts/`는 Harness 실행 도구가 들어 있는 곳이다.

```text
scripts/execute.py
scripts/test_execute.py
```

### scripts/execute.py

`phases/{task-name}` 아래에 있는 step 파일을 순서대로 실행하는 스크립트다.

사용 예:

```bash
python3 scripts/execute.py 0-mvp
```

자동으로 하는 일:

- `feat-{task-name}` 브랜치를 만든다.
- `AGENTS.md`와 `docs/*.md` 내용을 Codex 프롬프트에 넣는다.
- 완료된 step summary를 다음 step에 전달한다.
- 실패하면 최대 3회 재시도한다.
- step마다 커밋을 만든다.

### scripts/test_execute.py

`execute.py`가 의도대로 동작하는지 확인하는 테스트다.

현재 환경에 `pytest`가 설치되어 있으면 아래처럼 실행한다.

```bash
python3 -m pytest scripts/test_execute.py
```

## .githooks/pre-commit

Git 커밋 전에 자동으로 실행되는 스크립트다.

현재 repo는 `core.hooksPath`가 `.githooks`로 설정되어 있어서 이 파일이 실제 pre-commit hook으로 동작한다.

하는 일:

- Gradle 프로젝트면 `check`, `build`, `test`를 실행한다.
- Maven 프로젝트면 `verify`, `package`, `test`를 실행한다.
- `PRECOMMIT_LINT_CMD`, `PRECOMMIT_BUILD_CMD`, `PRECOMMIT_TEST_CMD` 환경변수가 있으면 그 명령을 대신 쓴다.

예:

```bash
PRECOMMIT_TEST_CMD="./gradlew test --tests MyServiceTest" git commit -m "feat: add service"
```

아직 Gradle/Maven 파일이 없으면 검사를 skip한다.

## 처음 작업할 때 추천 흐름

1. `AGENTS.md`를 프로젝트에 맞게 수정한다.
2. `docs/PRD.md`에 만들 기능을 적는다.
3. `docs/ARCHITECTURE.md`에 Spring 계층 구조를 적는다.
4. `docs/ADR.md`에 기술 선택 이유를 적는다.
5. Codex에게 "docs 읽고 phase/step 계획 만들어줘"라고 시킨다.
6. 계획이 괜찮으면 phase 파일을 만들고 `scripts/execute.py`로 실행한다.
7. 커밋 전 `git status`와 테스트 결과를 확인한다.

## Codex에게 말할 때 예시

처음 설계:

```text
docs를 먼저 읽고, 회원가입 기능을 harness 방식으로 phase/step 계획으로 나눠줘.
아직 코드는 수정하지 말고 계획만 보여줘.
```

구현:

```text
방금 만든 phase 계획대로 step 파일들을 생성해줘.
Java Spring, JUnit 5, TDD 기준으로 작성해줘.
```

리뷰:

```text
harness-review 기준으로 지금 변경사항을 리뷰해줘.
커밋 전에 위험한 부분만 먼저 알려줘.
```

커밋 전 확인:

```bash
git status --short
python3 -m py_compile scripts/execute.py scripts/test_execute.py
```

Spring 프로젝트가 들어온 뒤에는:

```bash
./gradlew test
# 또는
mvn test
```

## 주의할 점

- `AGENTS.md`는 Codex가 보는 기본 규칙 파일이다. 이름을 `AGENT.md`로 바꾸지 말 것.
- `.codex/hooks.json`은 hook 목록이다. 어떤 자동 실행이 걸려 있는지 보고 싶으면 이 파일을 보면 된다.
- `.codex/config.toml`에는 hook 기능 활성화만 둔다.
- TDD Guard 설정은 `.codex/tdd-guard/data/`에서 관리한다.
- TDD Guard 호환 데이터가 필요하면 `.codex/tdd-guard/compat/` 아래에 생성한다.
- Java Spring 프로젝트 파일이 생기면 TDD Guard JUnit5 reporter dependency를 추가해야 한다.
- 커밋 전에 `git status`를 보고 untracked 파일이 빠지지 않았는지 확인한다.
