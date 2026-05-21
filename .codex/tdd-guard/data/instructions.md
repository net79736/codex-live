# TDD Guard Instructions for Java Spring

Apply classic TDD for Java Spring work:

- Write or update a failing JUnit 5 test before changing production code.
- Every new or updated test must include a clear test description that explains what behavior is being verified.
- Prefer JUnit 5 `@DisplayName` for the description. If `@DisplayName` is not used, the test method name must still clearly describe the behavior under test.
- Prefer focused unit or slice tests for services, controllers, repositories, and validators.
- Use Spring Boot integration tests only when wiring, persistence, transactions, security, or request/response behavior is the subject under test.
- Implement the smallest production change needed to make the current failing test pass.
- Do not add behavior that is not covered by a failing or newly updated test.
- After tests pass, refactor only while keeping tests green.
- For bug fixes, add a regression test that fails before the fix.

Expected Java/Spring test commands are usually one of:

```bash
./gradlew test
mvn test
```

If this project uses JUnit 5, configure the TDD Guard JUnit5 reporter in the build file and set `TDD_GUARD_PROJECT_ROOT` to the repository root.
