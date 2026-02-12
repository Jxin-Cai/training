# CMS System Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build a complete CMS system with Vue.js frontend and Spring Boot backend, following DDD hexagonal architecture with TDD approach.

**Architecture:** Hexagonal DDD (ports/adapters) with in-memory repositories, separate frontend/backend deployment, dark luxury theme.

**Tech Stack:** Vue 3 + Vite + TypeScript + Pinia, Spring Boot + Maven + Flexmark, TDD (Vitest + JUnit5 + Playwright).

---

## Context

### Original Request
Build a complete CMS system with Vue.js frontend and Spring Boot backend, including category management, markdown content management with publish/draft states, and deployment scripts.

### Interview Summary
**Key Discussions**:
- MVP Requirements: Frontend (content list, MD rendered detail), Backend (category CRUD, MD content CRUD), MD upload/edit with HTML rendering
- Technical Stack: Vue 3 + Vite + TypeScript, Spring Boot + Maven, in-memory storage, DDD layers
- User confirmed TDD approach, standard Maven/Vue structure, custom ports (to be specified)
- Dark luxury theme with black primary and gold accents

**Research Findings**:
- Current state: Greenfield project (only documentation exists)
- Project located at /Users/jxin/Agent/VB-Coding-Demo/training/4.omo-superpowers/java
- MVP.md (5 lines) and SPEC.md (159 lines) provide detailed requirements
- No existing Spring Boot or Vue.js code found

### Metis Review
**Identified Gaps** (addressed):
- DDD Implementation Depth: Use hexagonal ports/adapters, not simple layering
- Testing Strategy: Vitest + SpringBootTest + Playwright pyramid
- Markdown Processing: Flexmark with XSS protection, md-editor-v3 for frontend
- Architecture: Modular Vue structure, rich domain models (not anemic DTOs)
- Storage: In-memory Map repositories with JPA fallback via configuration

---

## Work Objectives

### Core Objective
Build a complete CMS system with category management, markdown content management, and separate frontend/backend deployment.

### Concrete Deliverables
- Backend: Spring Boot with hexagonal DDD architecture, REST APIs, in-memory repositories
- Frontend: Vue 3 with TypeScript, public/admin pages, dark luxury theme
- Deployment: Shell/cmd scripts for multiple deployment modes
- Tests: Comprehensive TDD coverage (unit, integration, E2E)

### Definition of Done
- [ ] All CRUD operations working for categories and content
- [ ] Markdown content renders to HTML with XSS protection
- [ ] Frontend displays content list and detail pages
- [ ] Admin interface for content and category management
- [ ] Deployment scripts work for all modes
- [ ] All tests pass (>90% domain, >80% integration, >60% E2E)

### Must Have
- Hexagonal DDD architecture (ports/adapters)
- TDD approach for all features
- In-memory storage with database fallback
- Dark luxury theme (black primary, gold accents)
- Separate frontend/backend deployment

### Must NOT Have (Guardrails)
- Anemic domain models (getters/setters only)
- Vuex or legacy Vue 2 patterns
- Missing XSS sanitization for markdown content
- Simple layered architecture (use hexagonal ports/adapters)
- Manual testing without automated verification

---

## Verification Strategy (MANDATORY)

> **UNIVERSAL RULE: ZERO HUMAN INTERVENTION**
>
> ALL tasks in this plan MUST be verifiable WITHOUT any human action.
> This is NOT conditional — it applies to EVERY task, regardless of test strategy.
>
> **FORBIDDEN** — acceptance criteria that require:
> - "User manually tests..." / "사용자가 직접 테스트..."
> - "User visually confirms..." / "사용자가 눈으로 확인..."
> - "User interacts with..." / "사용자가 직접 조작..."
> - "Ask user to verify..." / "사용자에게 확인 요청..."
> - ANY step where a human must perform an action
>
> **ALL verification is executed by the agent** using tools (Playwright, interactive_bash, curl, etc.). No exceptions.

### Test Decision
- **Infrastructure exists**: NO (greenfield project)
- **Automated tests**: YES (TDD)
- **Framework**: Backend (JUnit 5, Mockito, SpringBootTest), Frontend (Vitest, Vue Test Utils, Playwright)

### If TDD Enabled

Each TODO follows RED-GREEN-REFACTOR:

**Task Structure:**
1. **RED**: Write failing test first
   - Test file: `[path].test.ts` or `[path]Test.java`
   - Test command: `npm test [file]` or `mvn test -Dtest=[TestClass]`
   - Expected: FAIL (test exists, implementation doesn't)
2. **GREEN**: Implement minimum code to pass
   - Command: Same test command
   - Expected: PASS
3. **REFACTOR**: Clean up while keeping green
   - Command: Same test command
   - Expected: PASS (still)

**Test Setup Tasks (infrastructure doesn't exist):**
- Backend: JUnit 5 + SpringBootTest setup
- Frontend: Vitest + Vue Test Utils + Playwright setup

### Agent-Executed QA Scenarios (MANDATORY — ALL tasks)

> Whether TDD is enabled or not, EVERY task MUST include Agent-Executed QA Scenarios.
> - **With TDD**: QA scenarios complement unit tests at integration/E2E level
> - **Without TDD**: QA scenarios are the PRIMARY verification method
>
> These describe how the executing agent DIRECTLY verifies the deliverable
> by running it — opening browsers, executing commands, sending API requests.
> The agent performs what a human tester would do, but automated via tools.

**Verification Tool by Deliverable Type:**

| Type | Tool | How Agent Verifies |
|------|------|-------------------|
| **Frontend/UI** | Playwright (playwright skill) | Navigate, interact, assert DOM, screenshot |
| **TUI/CLI** | interactive_bash (tmux) | Run command, send keystrokes, validate output |
| **API/Backend** | Bash (curl) | Send requests, parse responses, assert fields |
| **Library/Module** | Bash (java/maven commands) | Build, run tests, verify functionality |
| **Config/Infra** | Bash (shell commands) | Apply config, run state checks, validate |

---

## Execution Strategy

### Parallel Execution Waves

> Maximize throughput by grouping independent tasks into parallel waves.
> Each wave completes before the next begins.

```
Wave 1 (Start Immediately):
├── Task 1: Backend Project Setup (no dependencies)
├── Task 2: Frontend Project Setup (no dependencies)
└── Task 3: Test Infrastructure Setup (no dependencies)

Wave 2 (After Wave 1):
├── Task 4: Backend Domain Layer (depends: 1, 3)
├── Task 5: Frontend Core Components (depends: 2, 3)
└── Task 6: Backend Application Layer (depends: 4)

Wave 3 (After Wave 2):
├── Task 7: Backend REST APIs (depends: 6)
├── Task 8: Frontend Admin Interface (depends: 5)
└── Task 9: Frontend Public Interface (depends: 5)

Wave 4 (After Wave 3):
├── Task 10: Integration Testing (depends: 7, 8, 9)
├── Task 11: Deployment Scripts (depends: 7, 8, 9)
└── Task 12: E2E Testing (depends: 10, 11)

Critical Path: Task 1 → Task 4 → Task 6 → Task 7 → Task 10 → Task 12
Parallel Speedup: ~60% faster than sequential
```

### Dependency Matrix

| Task | Depends On | Blocks | Can Parallelize With |
|------|------------|--------|---------------------|
| 1 | None | 4, 6, 7 | 2, 3 |
| 2 | None | 5, 8, 9 | 1, 3 |
| 3 | None | 4, 5, 10, 12 | 1, 2 |
| 4 | 1, 3 | 6 | 5, 8, 9 |
| 5 | 2, 3 | 8, 9 | 4, 6, 7 |
| 6 | 4 | 7 | 5, 8, 9 |
| 7 | 6 | 10, 11 | 8, 9 |
| 8 | 5 | 10, 11 | 7, 9 |
| 9 | 5 | 10, 11 | 7, 8 |
| 10 | 7, 8, 9 | 12 | 11 |
| 11 | 7, 8, 9 | None | 10, 12 |
| 12 | 10, 11 | None | None (final) |

---

## TODOs

> Implementation + Test = ONE Task. Never separate.
> EVERY task MUST have: Recommended Agent Profile + Parallelization info.

- [ ] 1. Backend Project Setup

  **What to do**:
  - Create Maven project structure with pom.xml
  - Set up Spring Boot with hexagonal DDD packages
  - Configure Flexmark for markdown processing
  - Set up in-memory storage configuration

  **Must NOT do**:
  - Create anemic domain models
  - Use simple layered architecture
  - Skip XSS protection configuration

  **Recommended Agent Profile**:
  > Select category + skills based on task domain. Justify each choice.
  - **Category**: `unspecified-low`
    - Reason: Standard project setup with well-defined patterns, no complex problem-solving required
  - **Skills**: [`git-master`]
    - `git-master`: For proper commit management and version control of the initial project structure
  - **Skills Evaluated but Omitted**:
    - `playwright`: Not needed for backend setup
    - `frontend-ui-ux`: Frontend-specific skill

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 2, 3)
  - **Blocks**: Tasks 4, 6, 7 (domain and application layers)
  - **Blocked By**: None (can start immediately)

  **References** (CRITICAL - Be Exhaustive):

  > The executor has NO context from your interview. References are their ONLY guide.
  > Each reference must answer: "What should I look at and WHY?"

  **Pattern References** (existing code to follow):
  - `doc/SPEC.md:114-129` - DDD hexagonal architecture specification (ports/adapters pattern)
  - `doc/SPEC.md:130-138` - DDD POJO definition guidelines (rich domain models)

  **API/Type References** (contracts to implement against):
  - Spring Boot DDD patterns: Hexagonal ports/adapters structure
  - Maven dependency management for Spring Boot + Flexmark

  **Test References** (testing patterns to follow):
  - JUnit 5 + SpringBootTest setup patterns
  - TDD RED-GREEN-REFACTOR workflow

  **Documentation References** (specs and requirements):
  - `doc/MVP.md:1-5` - Core CMS requirements (categories, content, MD processing)
  - `doc/SPEC.md:10-12` - Backend technology stack (Spring Boot, in-memory storage)

  **External References** (libraries and frameworks):
  - Spring Boot DDD examples: Hexagonal architecture implementation
  - Flexmark Java documentation: Markdown processing configuration

  **WHY Each Reference Matters** (explain the relevance):
  - `doc/SPEC.md:114-129`: Defines the exact hexagonal architecture pattern to follow, preventing simple layered implementation
  - `doc/MVP.md:1-5`: Contains core business requirements that must be reflected in the initial domain model
  - Spring Boot DDD patterns: Provide proven implementation patterns for hexagonal architecture

  **Acceptance Criteria**:

  > **AGENT-EXECUTABLE VERIFICATION ONLY** — No human action permitted.
  > Every criterion MUST be verifiable by running a command or using a tool.
  > REPLACE all placeholders with actual values from task context.

  **If TDD (tests enabled):**
  - [ ] Test file created: src/test/java/com/cms/CmsApplicationTests.java
  - [ ] Test covers: Spring Boot application starts successfully
  - [ ] mvn test -Dtest=CmsApplicationTests → PASS (1 test, 0 failures)

  **Agent-Executed QA Scenarios (MANDATORY — per-scenario, ultra-detailed):**

  \`\`\`
  Scenario: Spring Boot application starts successfully
    Tool: Bash (maven)
    Preconditions: Maven installed, JDK 11+
    Steps:
      1. cd /Users/jxin/Agent/VB-Coding-Demo/training/4.omo-superpowers/java/backend
      2. mvn clean compile
      3. mvn spring-boot:run
      4. Wait for: "Started CmsApplication" in stdout (timeout: 30s)
      5. Assert: Process exit code is 0 (successful start)
      6. Assert: stdout contains "Tomcat started on port(s)" (default 8080)
      7. curl -s http://localhost:8080/actuator/health → Assert status 200
      8. Stop application: Ctrl+C
    Expected Result: Spring Boot application starts and responds to health check
    Evidence: Application startup logs captured

  Scenario: Maven project structure is valid
    Tool: Bash (maven)
    Preconditions: Project files created
    Steps:
      1. cd /Users/jxin/Agent/VB-Coding-Demo/training/4.omo-superpowers/java/backend
      2. mvn validate
      3. Assert: Command exits with code 0
      4. Assert: No error messages in stderr
      5. ls -la src/main/java/com/cms/ → Assert domain/application/infrastructure/presentation directories exist
      6. cat pom.xml → Assert Spring Boot parent and dependencies present
    Expected Result: Valid Maven project structure with proper dependencies
    Evidence: Maven validation output and directory listing
  \`\`\`

  **Evidence to Capture:**
  - [ ] Application startup logs in .sisyphus/evidence/task-1-startup.log
  - [ ] Maven validation output in .sisyphus/evidence/task-1-validate.log
  - [ ] Project structure screenshot in .sisyphus/evidence/task-1-structure.png

  **Commit**: YES (groups with 2, 3)
  - Message: `feat: setup backend Maven project with Spring Boot and hexagonal DDD structure`
  - Files: `pom.xml`, `src/main/java/com/cms/CmsApplication.java`, domain/application/infrastructure/presentation packages
  - Pre-commit: `mvn test`

- [ ] 2. Frontend Project Setup

  **What to do**:
  - Create Vue 3 + Vite + TypeScript project
  - Set up Vue Router and Pinia stores
  - Configure md-editor-v3 for markdown editing
  - Set up dark luxury theme (black primary, gold accents)

  **Must NOT do**:
  - Use Vuex or legacy Vue 2 patterns
  - Skip TypeScript configuration
  - Use default light theme

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: Frontend setup requires UI/UX decisions, theme configuration, and component architecture
  - **Skills**: [`frontend-ui-ux`, `git-master`]
    - `frontend-ui-ux`: For dark luxury theme implementation and component design
    - `git-master`: For proper version control of frontend project structure
  - **Skills Evaluated but Omitted**:
    - `playwright`: Not needed for setup, will be used for testing later

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 1, 3)
  - **Blocks**: Tasks 5, 8, 9 (frontend components and interfaces)
  - **Blocked By**: None (can start immediately)

  **References**:
  - `doc/SPEC.md:3-7` - Frontend technology stack (Vue.js, npm build)
  - Vue 3 + Vite documentation: Project setup and configuration
  - md-editor-v3 documentation: Integration and dark theme setup
  - Pinia documentation: Store setup with TypeScript

  **Acceptance Criteria**:
  - [ ] Test file created: src/__tests__/setup.test.ts
  - [ ] Test covers: Vite dev server starts, Vue app mounts
  - [ ] npm test → PASS (1 test, 0 failures)

  **Agent-Executed QA Scenarios**:

  \`\`\`
  Scenario: Vite development server starts successfully
    Tool: Bash (npm)
    Preconditions: Node.js 16+, npm installed
    Steps:
      1. cd /Users/jxin/Agent/VB-Coding-Demo/training/4.omo-superpowers/java/frontend
      2. npm install
      3. npm run dev
      4. Wait for: "Local:" in stdout (timeout: 30s)
      5. Assert: stdout contains "http://localhost:3000" (or specified port)
      6. curl -s http://localhost:3000 → Assert contains Vue app HTML
      7. Stop server: Ctrl+C
    Expected Result: Vite server starts and serves Vue application
    Evidence: Server startup logs captured

  Scenario: Dark theme is applied correctly
    Tool: Playwright (playwright skill)
    Preconditions: Vite server running on localhost:3000
    Steps:
      1. Navigate to: http://localhost:3000
      2. Wait for: body element visible (timeout: 5s)
      3. Assert: getComputedStyle(body).backgroundColor equals "rgb(28, 25, 23)" (#1C1917)
      4. Assert: At least one element has color containing "rgb(202, 138, 4)" (#CA8A04 - gold accent)
      5. Screenshot: .sisyphus/evidence/task-2-dark-theme.png
    Expected Result: Dark luxury theme applied with black background and gold accents
    Evidence: Screenshot showing dark theme
  \`\`\`

  **Evidence to Capture:**
  - [ ] Vite startup logs in .sisyphus/evidence/task-2-startup.log
  - [ ] Dark theme screenshot in .sisyphus/evidence/task-2-dark-theme.png

  **Commit**: YES (groups with 1, 3)
  - Message: `feat: setup Vue 3 frontend with TypeScript, Pinia, and dark luxury theme`
  - Files: `package.json`, `vite.config.ts`, `src/main.ts`, theme files

- [ ] 3. Test Infrastructure Setup

  **What to do**:
  - Backend: Configure JUnit 5, Mockito, SpringBootTest
  - Frontend: Configure Vitest, Vue Test Utils, Playwright
  - Set up test databases and fixtures
  - Configure test coverage reporting

  **Must NOT do**:
  - Skip E2E testing setup
  - Use outdated testing frameworks
  - Skip coverage configuration

  **Recommended Agent Profile**:
  - **Category**: `unspecified-low`
    - Reason: Standard test infrastructure setup with well-known patterns
  - **Skills**: [`git-master`]
    - `git-master`: For proper commit management of test configuration
  - **Skills Evaluated but Omitted**:
    - `playwright`: Playwright setup is part of infrastructure, not testing execution

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 1, 2)
  - **Blocks**: All subsequent tasks (testing is foundational)
  - **Blocked By**: None (can start immediately)

  **References**:
  - JUnit 5 documentation: Spring Boot test configuration
  - Vitest documentation: Vue 3 testing setup
  - Playwright documentation: E2E testing configuration
  - Spring Boot Test documentation: @SpringBootTest setup

  **Acceptance Criteria**:
  - [ ] Backend test: src/test/java/com/cms/infrastructure/RepositoryTest.java
  - [ ] Frontend test: src/__tests__/components/HelloWorld.test.ts
  - [ ] E2E test: tests/e2e/basic.spec.ts
  - [ ] mvn test → PASS, npm test → PASS, npx playwright test → PASS

  **Agent-Executed QA Scenarios**:

  \`\`\`
  Scenario: Backend test infrastructure works
    Tool: Bash (maven)
    Preconditions: Backend project setup complete
    Steps:
      1. cd /Users/jxin/Agent/VB-Coding-Demo/training/4.omo-superpowers/java/backend
      2. mvn test
      3. Assert: Command exits with code 0
      4. Assert: stdout contains "BUILD SUCCESS"
      5. Assert: stdout contains "Tests run: X, Failures: 0"
    Expected Result: All backend tests pass
    Evidence: Maven test output captured

  Scenario: Frontend test infrastructure works
    Tool: Bash (npm)
    Preconditions: Frontend project setup complete
    Steps:
      1. cd /Users/jxin/Agent/VB-Coding-Demo/training/4.omo-superpowers/java/frontend
      2. npm test
      3. Assert: Command exits with code 0
      4. Assert: stdout contains "PASS" or "✓"
      5. npx playwright test --dry-run → Assert no syntax errors
    Expected Result: All frontend tests pass
    Evidence: npm test output captured
  \`\`\`

  **Evidence to Capture:**
  - [ ] Backend test results in .sisyphus/evidence/task-3-backend-tests.log
  - [ ] Frontend test results in .sisyphus/evidence/task-3-frontend-tests.log

  **Commit**: YES (groups with 1, 2)
  - Message: `test: setup comprehensive test infrastructure for backend and frontend`
  - Files: Test configuration files, sample tests

- [ ] 4. Backend Domain Layer

  **What to do**:
  - Create Category entity (rich domain model)
  - Create Content entity with markdown/html fields
  - Implement domain services for markdown processing
  - Create repository interfaces (ports)

  **Must NOT do**:
  - Create anemic DTOs (getters/setters only)
  - Skip domain business logic
  - Put infrastructure concerns in domain layer

  **Recommended Agent Profile**:
  - **Category**: `ultrabrain`
    - Reason: Complex domain modeling with rich entities, business logic, and DDD patterns
  - **Skills**: [`git-master`]
    - `git-master`: For proper commit management of domain layer
  - **Skills Evaluated but Omitted**:
    - `frontend-ui-ux`: Backend domain modeling only

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Sequential (depends on Task 1)
  - **Blocks**: Task 6 (application layer)
  - **Blocked By**: Task 1 (backend setup)

  **References**:
  - `doc/SPEC.md:130-138` - DDD POJO definition (rich domain models)
  - `doc/SPEC.md:114-129` - Hexagonal architecture (repository ports)
  - `doc/MVP.md:4` - Content requirements (categories, markdown, publish/draft)

  **Acceptance Criteria**:
  - [ ] Domain entity tests: CategoryTest.java, ContentTest.java
  - [ ] Domain service tests: MarkdownServiceTest.java
  - [ ] Repository interface tests: CategoryRepositoryTest.java
  - [ ] mvn test -Dtest=DomainLayerTest → PASS

  **Agent-Executed QA Scenarios**:

  \`\`\`
  Scenario: Category entity behaves correctly
    Tool: Bash (maven)
    Preconditions: Domain layer implemented
    Steps:
      1. cd /Users/jxin/Agent/VB-Coding-Demo/training/4.omo-superpowers/java/backend
      2. mvn test -Dtest=CategoryTest
      3. Assert: Command exits with code 0
      4. Assert: stdout contains "testCategoryCreation PASSED"
      5. Assert: stdout contains "testCategoryValidation PASSED"
    Expected Result: Category entity with business logic works correctly
    Evidence: Test output captured

  Scenario: Markdown processing service works
    Tool: Bash (maven)
    Preconditions: Flexmark dependency configured
    Steps:
      1. mvn test -Dtest=MarkdownServiceTest
      2. Assert: Command exits with code 0
      3. Assert: Test covers markdown to HTML conversion
      4. Assert: Test covers XSS protection
    Expected Result: Markdown service converts MD to HTML safely
    Evidence: Test output showing HTML conversion
  \`\`\`

  **Evidence to Capture:**
  - [ ] Domain layer test results in .sisyphus/evidence/task-4-domain-tests.log

  **Commit**: YES
  - Message: `feat: implement rich domain entities and repository interfaces following hexagonal DDD`
  - Files: Domain entities, repository interfaces, domain services

- [ ] 5. Frontend Core Components

  **What to do**:
  - Create layout components with dark theme
  - Set up Vue Router configuration
  - Create Pinia stores for categories and content
  - Implement basic UI components (buttons, forms, cards)

  **Must NOT do**:
  - Use Vuex instead of Pinia
  - Skip TypeScript typing
  - Create components without dark theme support

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: Frontend component creation with dark luxury theme and responsive design
  - **Skills**: [`frontend-ui-ux`, `git-master`]
    - `frontend-ui-ux`: For component design and dark theme implementation
    - `git-master`: For proper version control

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Sequential (depends on Task 2)
  - **Blocks**: Tasks 8, 9 (admin and public interfaces)
  - **Blocked By**: Task 2 (frontend setup)

  **References**:
  - Vue 3 documentation: Composition API with TypeScript
  - Pinia documentation: Store setup with TypeScript
  - Vue Router documentation: Route configuration
  - CSS documentation: Dark theme implementation

  **Acceptance Criteria**:
  - [ ] Component tests: LayoutTest.vue, ButtonTest.vue
  - [ ] Store tests: CategoryStoreTest.ts, ContentStoreTest.ts
  - [ ] Router tests: Router configuration tests
  - [ ] npm test → PASS

  **Agent-Executed QA Scenarios**:

  \`\`\`
  Scenario: Core components render with dark theme
    Tool: Playwright (playwright skill)
    Preconditions: Frontend dev server running
    Steps:
      1. Navigate to: http://localhost:3000
      2. Wait for: App component mounted (timeout: 5s)
      3. Assert: Layout component has dark background (#1C1917)
      4. Assert: Button components have gold accent (#CA8A04)
      5. Assert: All text is readable on dark background
      6. Screenshot: .sisyphus/evidence/task-5-components.png
    Expected Result: Core components render with dark luxury theme
    Evidence: Screenshot showing themed components

  Scenario: Pinia stores work correctly
    Tool: Bash (npm)
    Preconditions: Frontend project running
    Steps:
      1. npm test src/__tests__/stores/CategoryStoreTest.ts
      2. Assert: Test exits with code 0
      3. Assert: Store actions work correctly
      4. Assert: Store state management works
    Expected Result: Pinia stores function correctly
    Evidence: Test output captured
  \`\`\`

  **Evidence to Capture:**
  - [ ] Component screenshots in .sisyphus/evidence/task-5-components.png
  - [ ] Store test results in .sisyphus/evidence/task-5-stores.log

  **Commit**: YES
  - Message: `feat: create core frontend components with dark luxury theme and Pinia stores`
  - Files: Vue components, stores, router configuration

- [ ] 6. Backend Application Layer

  **What to do**:
  - Implement application services for category/content CRUD
  - Create use case services for markdown processing
  - Implement transaction management
  - Create DTOs for API responses

  **Must NOT do**:
  - Put business logic in application services
  - Skip transaction management
  - Create anemic service layer

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: Complex application services with transaction management and DTO mapping
  - **Skills**: [`git-master`]
    - `git-master`: For proper commit management
  - **Skills Evaluated but Omitted**:
    - Other skills not needed for application layer

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Sequential (depends on Task 4)
  - **Blocks**: Task 7 (REST APIs)
  - **Blocked By**: Task 4 (domain layer)

  **References**:
  - `doc/SPEC.md:118-120` - Application layer definition
  - Spring Boot documentation: Service layer patterns
  - DDD patterns: Application service implementation

  **Acceptance Criteria**:
  - [ ] Application service tests: CategoryServiceTest.java, ContentServiceTest.java
  - [ ] DTO tests: CategoryDtoTest.java, ContentDtoTest.java
  - [ ] Transaction tests: TransactionManagementTest.java
  - [ ] mvn test -Dtest=ApplicationLayerTest → PASS

  **Agent-Executed QA Scenarios**:

  \`\`\`
  Scenario: Application services manage CRUD operations
    Tool: Bash (maven)
    Preconditions: Application layer implemented
    Steps:
      1. mvn test -Dtest=CategoryServiceTest
      2. Assert: Command exits with code 0
      3. Assert: Create operation works
      4. Assert: Read operation works
      5. Assert: Update operation works
      6. Assert: Delete operation works
    Expected Result: Application services handle CRUD correctly
    Evidence: Test output captured

  Scenario: Transaction management works
    Tool: Bash (maven)
    Preconditions: Transaction configuration implemented
    Steps:
      1. mvn test -Dtest=TransactionManagementTest
      2. Assert: Command exits with code 0
      3. Assert: Transactions roll back on errors
      4. Assert: Transactions commit on success
    Expected Result: Transaction management works correctly
    Evidence: Test output showing transaction behavior
  \`\`\`

  **Evidence to Capture:**
  - [ ] Application service test results in .sisyphus/evidence/task-6-app-services.log

  **Commit**: YES
  - Message: `feat: implement application services with transaction management and DTOs`
  - Files: Application services, DTOs, transaction configuration

- [ ] 7. Backend REST APIs

  **What to do**:
  - Create REST controllers for categories and content
  - Implement proper HTTP status codes and error handling
  - Add CORS configuration for frontend communication
  - Create API documentation

  **Must NOT do**:
  - Skip error handling
  - Return raw entities without DTOs
  - Skip CORS configuration

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: REST API implementation with proper error handling, CORS, and documentation
  - **Skills**: [`git-master`]
    - `git-master`: For proper commit management
  - **Skills Evaluated but Omitted**:
    - Other skills not needed for REST API implementation

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Sequential (depends on Task 6)
  - **Blocks**: Tasks 10, 11 (integration and deployment)
  - **Blocked By**: Task 6 (application layer)

  **References**:
  - Spring Boot documentation: REST controller implementation
  - CORS documentation: Cross-origin configuration
  - HTTP documentation: Proper status codes

  **Acceptance Criteria**:
  - [ ] Controller tests: CategoryControllerTest.java, ContentControllerTest.java
  - [ ] Integration tests: API integration tests
  - [ ] CORS tests: Cross-origin request tests
  - [ ] mvn test -Dtest=RestApiTest → PASS

  **Agent-Executed QA Scenarios**:

  \`\`\`
  Scenario: Category CRUD API endpoints work
    Tool: Bash (curl)
    Preconditions: Backend application running
    Steps:
      1. Start backend: mvn spring-boot:run
      2. Wait for: "Started CmsApplication" (timeout: 30s)
      3. CREATE: curl -s -X POST http://localhost:8080/api/categories -H "Content-Type: application/json" -d '{"name":"Test Category"}'
      4. Assert: HTTP status 201
      5. Assert: Response contains category ID
      6. READ: curl -s http://localhost:8080/api/categories/1
      7. Assert: HTTP status 200
      8. Assert: Response contains "Test Category"
      9. UPDATE: curl -s -X PUT http://localhost:8080/api/categories/1 -H "Content-Type: application/json" -d '{"name":"Updated Category"}'
      10. Assert: HTTP status 200
      11. DELETE: curl -s -X DELETE http://localhost:8080/api/categories/1
      12. Assert: HTTP status 204
      13. Stop backend: Ctrl+C
    Expected Result: All CRUD operations work via REST API
    Evidence: API responses captured

  Scenario: CORS configuration works
    Tool: Bash (curl)
    Preconditions: Backend running with CORS configured
    Steps:
      1. curl -s -H "Origin: http://localhost:3000" -H "Access-Control-Request-Method: GET" http://localhost:8080/api/categories
      2. Assert: Response contains "Access-Control-Allow-Origin: http://localhost:3000"
      3. Assert: Response contains "Access-Control-Allow-Methods: GET"
    Expected Result: CORS allows frontend requests
    Evidence: CORS headers captured
  \`\`\`

  **Evidence to Capture:**
  - [ ] API test results in .sisyphus/evidence/task-7-api-tests.log
  - [ ] CORS headers in .sisyphus/evidence/task-7-cors.log

  **Commit**: YES
  - Message: `feat: implement REST APIs with proper error handling and CORS configuration`
  - Files: REST controllers, error handling, CORS configuration

- [ ] 8. Frontend Admin Interface

  **What to do**:
  - Create category management interface (CRUD)
  - Create content management interface with MD editor
  - Implement publish/draft functionality
  - Add form validation and error handling

  **Must NOT do**:
  - Skip form validation
  - Use basic textarea instead of MD editor
  - Skip error handling

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: Complex admin interface with forms, validation, MD editor integration
  - **Skills**: [`frontend-ui-ux`, `git-master`]
    - `frontend-ui-ux`: For admin interface design and UX
    - `git-master`: For proper version control

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Sequential (depends on Task 5)
  - **Blocks**: Tasks 10, 11 (integration and deployment)
  - **Blocked By**: Task 5 (core components)

  **References**:
  - md-editor-v3 documentation: Integration and configuration
  - Vue 3 documentation: Form handling and validation
  - Pinia documentation: API integration

  **Acceptance Criteria**:
  - [ ] Component tests: CategoryManagementTest.vue, ContentManagementTest.vue
  - [ ] Integration tests: Admin interface integration tests
  - [ ] E2E tests: Admin workflow tests
  - [ ] npm test → PASS

  **Agent-Executed QA Scenarios**:

  \`\`\`
  Scenario: Category management interface works
    Tool: Playwright (playwright skill)
    Preconditions: Frontend and backend running
    Steps:
      1. Navigate to: http://localhost:3000/admin/categories
      2. Wait for: Category management page loaded (timeout: 5s)
      3. Click: button[data-testid="add-category"]
      4. Fill: input[name="categoryName"] → "Test Category"
      5. Click: button[data-testid="save-category"]
      6. Wait for: Success message visible (timeout: 3s)
      7. Assert: Category list contains "Test Category"
      8. Screenshot: .sisyphus/evidence/task-8-category-mgmt.png
    Expected Result: Category CRUD operations work via admin interface
    Evidence: Screenshot showing category management

  Scenario: Content management with MD editor works
    Tool: Playwright (playwright skill)
    Preconditions: Admin interface loaded
    Steps:
      1. Navigate to: http://localhost:3000/admin/content
      2. Click: button[data-testid="add-content"]
      3. Fill: input[name="contentTitle"] → "Test Article"
      4. Select: select[name="categoryId"] → "Test Category"
      5. Fill: .md-editor → "# Test Article\n\nThis is **bold** text."
      6. Click: button[data-testid="save-draft"]
      7. Wait for: Success message visible (timeout: 3s)
      8. Assert: Content list contains "Test Article"
      9. Assert: Status shows "Draft"
      10. Screenshot: .sisyphus/evidence/task-8-content-mgmt.png
    Expected Result: Content creation with MD editor works
    Evidence: Screenshot showing content management
  \`\`\`

  **Evidence to Capture:**
  - [ ] Category management screenshot in .sisyphus/evidence/task-8-category-mgmt.png
  - [ ] Content management screenshot in .sisyphus/evidence/task-8-content-mgmt.png

  **Commit**: YES
  - Message: `feat: implement admin interface with category/content management and MD editor`
  - Files: Admin Vue components, forms, validation

- [ ] 9. Frontend Public Interface

  **What to do**:
  - Create content list page (sorted by publish time)
  - Create content detail page (rendered HTML)
  - Implement category filtering
  - Add responsive design for PC focus

  **Must NOT do**:
  - Skip responsive design
  - Show raw markdown instead of rendered HTML
  - Skip content sorting

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
    - Reason: Public interface design with content rendering and responsive layout
  - **Skills**: [`frontend-ui-ux`, `git-master`]
    - `frontend-ui-ux`: For public interface design and content rendering
    - `git-master`: For proper version control

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Sequential (depends on Task 5)
  - **Blocks**: Tasks 10, 11 (integration and deployment)
  - **Blocked By**: Task 5 (core components)

  **References**:
  - Vue 3 documentation: Component composition and responsive design
  - CSS documentation: PC-focused responsive design
  - HTML documentation: Safe content rendering

  **Acceptance Criteria**:
  - [ ] Component tests: ContentListTest.vue, ContentDetailTest.vue
  - [ ] Integration tests: Public interface integration tests
  - [ ] E2E tests: Public user workflow tests
  - [ ] npm test → PASS

  **Agent-Executed QA Scenarios**:

  \`\`\`
  Scenario: Content list displays sorted by publish time
    Tool: Playwright (playwright skill)
    Preconditions: Frontend and backend running, sample content exists
    Steps:
      1. Navigate to: http://localhost:3000/
      2. Wait for: Content list loaded (timeout: 5s)
      3. Assert: Content items are displayed
      4. Assert: Items are sorted by publish date (newest first)
      5. Assert: Each item shows title, excerpt, publish date
      6. Screenshot: .sisyphus/evidence/task-9-content-list.png
    Expected Result: Content list displays correctly sorted by publish time
    Evidence: Screenshot showing content list

  Scenario: Content detail renders HTML from markdown
    Tool: Playwright (playwright skill)
    Preconditions: Content with markdown exists
    Steps:
      1. Navigate to: http://localhost:3000/content/1
      2. Wait for: Content detail loaded (timeout: 5s)
      3. Assert: Title is displayed
      4. Assert: HTML content is rendered (not raw markdown)
      5. Assert: Headings are properly formatted (<h1>, <h2>, etc.)
      6. Assert: Bold text is rendered with <strong> tags
      7. Assert: No XSS vulnerabilities (scripts not executed)
      8. Screenshot: .sisyphus/evidence/task-9-content-detail.png
    Expected Result: Content detail renders safe HTML from markdown
    Evidence: Screenshot showing rendered content
  \`\`\`

  **Evidence to Capture:**
  - [ ] Content list screenshot in .sisyphus/evidence/task-9-content-list.png
  - [ ] Content detail screenshot in .sisyphus/evidence/task-9-content-detail.png

  **Commit**: YES
  - Message: `feat: implement public interface with content list and detail pages`
  - Files: Public Vue components, content rendering, responsive design

- [ ] 10. Integration Testing

  **What to do**:
  - Create comprehensive integration tests
  - Test frontend-backend communication
  - Test markdown processing end-to-end
  - Verify error handling across layers

  **Must NOT do**:
  - Skip end-to-end testing
  - Skip error scenario testing
  - Skip performance testing

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: Complex integration testing across multiple layers and systems
  - **Skills**: [`git-master`]
    - `git-master`: For proper version control
  - **Skills Evaluated but Omitted**:
    - Other skills not needed for integration testing

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Sequential (depends on Tasks 7, 8, 9)
  - **Blocks**: Task 12 (E2E testing)
  - **Blocked By**: Tasks 7, 8, 9 (APIs and interfaces)

  **References**:
  - Spring Boot documentation: Integration testing
  - Playwright documentation: E2E testing patterns
  - Testing documentation: Integration test best practices

  **Acceptance Criteria**:
  - [ ] Integration tests: Full workflow tests
  - [ ] Performance tests: Markdown rendering performance
  - [ ] Error tests: Error scenario coverage
  - [ ] mvn test + npm test + npx playwright test → ALL PASS

  **Agent-Executed QA Scenarios**:

  \`\`\`
  Scenario: Full content creation workflow works
    Tool: Playwright (playwright skill)
    Preconditions: Both frontend and backend running
    Steps:
      1. Navigate to: http://localhost:3000/admin/content
      2. Create new content with markdown
      3. Publish content
      4. Navigate to: http://localhost:3000/
      5. Verify content appears in list
      6. Click on content to view detail
      7. Verify HTML is rendered correctly
      8. Screenshot: .sisyphus/evidence/task-10-workflow.png
    Expected Result: Complete workflow from creation to display works
    Evidence: Screenshots and logs captured

  Scenario: Error handling works across layers
    Tool: Playwright (playwright skill)
    Preconditions: Applications running
    Steps:
      1. Test invalid markdown input
      2. Test network errors
      3. Test validation errors
      4. Verify proper error messages displayed
      5. Verify graceful degradation
    Expected Result: All error scenarios handled gracefully
    Evidence: Error handling logs and screenshots
  \`\`\`

  **Evidence to Capture:**
  - [ ] Full workflow screenshots in .sisyphus/evidence/task-10-workflow.png
  - [ ] Integration test results in .sisyphus/evidence/task-10-integration.log

  **Commit**: YES
  - Message: `test: implement comprehensive integration tests for full system workflow`
  - Files: Integration test suites, performance tests

- [ ] 11. Deployment Scripts

  **What to do**:
  - Create deploy.sh and deploy.cmd (Linux/Mac/Windows)
  - Create shutdown.sh and shutdown.cmd
  - Support multiple deployment modes (both, frontend-only, backend-only)
  - Add process management and logging

  **Must NOT do**:
  - Skip Windows support
  - Skip graceful shutdown
  - Skip error handling in scripts

  **Recommended Agent Profile**:
  - **Category**: `unspecified-low`
    - Reason: Standard shell script creation with well-known patterns
  - **Skills**: [`git-master`]
    - `git-master`: For proper version control
  - **Skills Evaluated but Omitted**:
    - Other skills not needed for script creation

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Sequential (depends on Tasks 7, 8, 9)
  - **Blocks**: Task 12 (final E2E testing)
  - **Blocked By**: Tasks 7, 8, 9 (APIs and interfaces)

  **References**:
  - `doc/SPEC.md:22-93` - Deployment script requirements
  - Shell scripting documentation: Cross-platform script patterns
  - Process management documentation: Graceful shutdown patterns

  **Acceptance Criteria**:
  - [ ] Script tests: All deployment modes work
  - [ ] Process tests: Services start/stop correctly
  - [ ] Cross-platform tests: Linux/Mac/Windows support
  - [ ] All scripts execute without errors

  **Agent-Executed QA Scenarios**:

  \`\`\`
  Scenario: Deploy both frontend and backend (Linux/Mac)
    Tool: Bash (shell)
    Preconditions: Project built and ready
    Steps:
      1. cd /Users/jxin/Agent/VB-Coding-Demo/training/4.omo-superpowers/java
      2. chmod +x deploy.sh
      3. ./deploy.sh
      4. Wait for: "Frontend started" message (timeout: 30s)
      5. Wait for: "Backend started" message (timeout: 30s)
      6. curl -s http://localhost:3000 → Assert frontend responds
      7. curl -s http://localhost:8080/api/health → Assert backend responds
      8. ./shutdown.sh
      9. Wait for: Services stopped (timeout: 10s)
    Expected Result: Both services deploy and stop correctly
    Evidence: Deployment logs captured

  Scenario: Deploy frontend only (Windows)
    Tool: Bash (cmd)
    Preconditions: Windows environment or simulation
    Steps:
      1. deploy.cmd frontend
      2. Wait for: Frontend service started
      3. Verify frontend responds on specified port
      4. shutdown.cmd frontend
      5. Verify frontend stops gracefully
    Expected Result: Frontend-only deployment works on Windows
    Evidence: Windows deployment logs
  \`\`\`

  **Evidence to Capture:**
  - [ ] Deployment logs in .sisyphus/evidence/task-11-deploy.log
  - [ ] Shutdown logs in .sisyphus/evidence/task-11-shutdown.log

  **Commit**: YES
  - Message: `feat: implement cross-platform deployment scripts with multiple deployment modes`
  - Files: deploy.sh, deploy.cmd, shutdown.sh, shutdown.cmd

- [ ] 12. E2E Testing and Final Verification

  **What to do**:
  - Create comprehensive E2E test suite
  - Test all user workflows
  - Verify deployment scripts work
  - Generate test coverage reports

  **Must NOT do**:
  - Skip any user workflow testing
  - Skip deployment testing
  - Skip coverage reporting

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: Final comprehensive testing covering all aspects of the system
  - **Skills**: [`git-master`]
    - `git-master`: For proper version control
  - **Skills Evaluated but Omitted**:
    - Other skills not needed for final testing

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Sequential (final task)
  - **Blocks**: None (final task)
  - **Blocked By**: Task 10 (integration testing), Task 11 (deployment scripts)

  **References**:
  - Playwright documentation: Comprehensive E2E testing
  - Coverage documentation: Test coverage reporting
  - QA documentation: Final verification procedures

  **Acceptance Criteria**:
  - [ ] E2E tests: All user workflows covered
  - [ ] Coverage reports: >90% domain, >80% integration, >60% E2E
  - [ ] Deployment tests: All deployment modes verified
  - [ ] Final verification: System ready for production

  **Agent-Executed QA Scenarios**:

  \`\`\`
  Scenario: Complete user workflow from admin to public
    Tool: Playwright (playwright skill)
    Preconditions: Fresh deployment via scripts
    Steps:
      1. Deploy both services using deploy.sh
      2. Admin creates category via admin interface
      3. Admin creates content with markdown via admin interface
      4. Admin publishes content
      5. Public user views content list
      6. Public user views content detail
      7. Verify all functionality works
      8. Shutdown services using shutdown.sh
      9. Generate coverage reports
      10. Screenshot: .sisyphus/evidence/task-12-final-verification.png
    Expected Result: Complete system works end-to-end
    Evidence: Final verification screenshots and coverage reports

  Scenario: All deployment modes work correctly
    Tool: Bash (shell)
    Preconditions: All scripts created
    Steps:
      1. Test deploy.sh (both services)
      2. Test deploy.sh frontend
      3. Test deploy.sh backend
      4. Test deploy.cmd (both services)
      5. Test deploy.cmd frontend
      6. Test deploy.cmd backend
      7. Test all corresponding shutdown scripts
      8. Verify all services start/stop correctly
    Expected Result: All deployment modes work on all platforms
    Evidence: Deployment test logs
  \`\`\`

  **Evidence to Capture:**
  - [ ] Final verification screenshots in .sisyphus/evidence/task-12-final-verification.png
  - [ ] Coverage reports in .sisyphus/evidence/task-12-coverage/
  - [ ] Deployment test logs in .sisyphus/evidence/task-12-deployment-tests.log

  **Commit**: YES
  - Message: `test: implement comprehensive E2E testing and final system verification`
  - Files: E2E test suites, coverage reports, final documentation

---

## Commit Strategy

| After Task | Message | Files | Verification |
|------------|---------|-------|--------------|
| 1-3 | `feat: setup project infrastructure with Maven, Vue 3, and test frameworks` | pom.xml, package.json, test configs | mvn test, npm test |
| 4-6 | `feat: implement backend domain and application layers with hexagonal DDD` | Domain entities, services, DTOs | mvn test -Dtest=BackendTest |
| 7-9 | `feat: implement REST APIs and frontend interfaces with dark theme` | Controllers, Vue components | mvn test, npm test, playwright test |
| 10-12 | `test: comprehensive integration, E2E testing, and deployment scripts` | Integration tests, scripts, coverage | All tests pass, deployment verified |

---

## Success Criteria

### Verification Commands
```bash
# Backend
cd backend && mvn clean test  # Expected: All tests pass

# Frontend  
cd frontend && npm test       # Expected: All tests pass

# E2E
npx playwright test          # Expected: All E2E tests pass

# Deployment
./deploy.sh && ./shutdown.sh  # Expected: Services start/stop correctly

# Coverage
mvn jacoco:report             # Expected: >90% domain coverage
npm run test:coverage         # Expected: >80% component coverage
```

### Final Checklist
- [ ] All CRUD operations working for categories and content
- [ ] Markdown content renders to HTML with XSS protection
- [ ] Frontend displays content list and detail pages with dark theme
- [ ] Admin interface for content and category management works
- [ ] Deployment scripts work for all modes and platforms
- [ ] All tests pass (>90% domain, >80% integration, >60% E2E)
- [ ] System follows hexagonal DDD architecture
- [ ] TDD approach used for all features
- [ ] Dark luxury theme applied consistently
- [ ] Cross-platform deployment supported