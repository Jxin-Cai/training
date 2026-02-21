# Test Design for Architecture: java CMS

**Purpose:** 架构关注点、可测试性缺口和NFR需求，供架构/开发团队审查。作为QA和工程团队在测试开发开始前必须解决的问题的契约。

**Date:** 2026-02-17
**Author:** Jxin
**Status:** Architecture Review Pending
**Project:** java
**PRD Reference:** `_bmad-output/planning-artifacts/prd.md`
**ADR Reference:** `_bmad-output/planning-artifacts/architecture.md`

---

## Executive Summary

**Scope:** 轻量级CMS内容管理平台，支持5-6人小团队的内部知识沉淀和分享。包含后台管理（文章/分类/用户管理）和前台展示（内容阅读）。

**Business Context** (from PRD):

- **Impact:** 解决团队内部信息分散、知识难以沉淀的问题
- **Problem:** 现有工具（Confluence太复杂、Notion缺乏发布概念、Git Markdown门槛高）
- **GA Launch:** MVP验证阶段

**Architecture** (from ADR):

- **Key Decision 1:** DDD分层架构（Presentation/Application/Domain/Infrastructure）
- **Key Decision 2:** Spring Boot + Vue.js 前后端分离
- **Key Decision 3:** 内存存储（非持久化数据库）

**Expected Scale:**

- 5-6人同时在线使用
- 每周4+篇新内容
- 99.9%可用性

**Risk Summary:**

- **Total risks**: 7
- **High-priority (≥6)**: 3 risks requiring immediate mitigation
- **Test effort**: ~30 tests (~2-3 weeks for 1 QA)

---

## Quick Guide

### 🚨 BLOCKERS - Team Must Decide (Can't Proceed Without)

**Sprint 0 Critical Path** - These MUST be completed before QA can write integration tests:

1. **BLK-01: 测试数据重置API** - 需要提供API端点来重置内存存储（推荐负责人：Backend Team）
2. **BLK-02: 测试环境配置** - 需要独立测试环境配置文件，支持测试模式（推荐负责人：DevOps）
3. **BLK-03: CSRF Token API** - 需要API获取CSRF Token用于API测试（推荐负责人：Backend Team）

**What we need from team:** Complete these 3 items in Sprint 0 or test development is blocked.

---

### ⚠️ HIGH PRIORITY - Team Should Validate (We Provide Recommendation, You Approve)

1. **SEC-01: XSS防护策略** - 建议使用OWASP推荐的Markdown sanitizer（Sprint 1）
2. **DATA-01: 自动保存机制** - 建议每30秒自动保存 + 离开页面前提醒（Sprint 1）
3. **SEC-02: CSRF防护实现** - 建议使用Spring Security CSRF Token（Sprint 1）

**What we need from team:** Review recommendations and approve (or suggest changes).

---

### 📋 INFO ONLY - Solutions Provided (Review, No Decisions Needed)

1. **Test strategy**: 30% E2E + 47% API + 23% Unit（前后端分离架构，API优先测试）
2. **Tooling**: Playwright (E2E/API), JUnit + Mockito (Unit)
3. **Tiered CI/CD**: PR (<10min) → Nightly (<30min) → Weekly (Performance)
4. **Coverage**: ~30 test scenarios prioritized P0-P3 with risk-based classification
5. **Quality gates**: P0=100%, P1≥95%, Coverage≥80%

**What we need from team:** Just review and acknowledge (we already have the solution).

---

## For Architects and Devs - Open Topics 👷

### Risk Assessment

**Total risks identified**: 7 (3 high-priority score ≥6, 3 medium, 1 low)

#### High-Priority Risks (Score ≥6) - IMMEDIATE ATTENTION

| Risk ID | Category | Description | Probability | Impact | Score | Mitigation | Owner | Timeline |
|---------|----------|-------------|-------------|--------|-------|------------|-------|----------|
| **SEC-01** | **SEC** | XSS攻击绕过Markdown渲染 | 2 | 3 | **6** | 输入验证 + 输出编码 + 安全测试 | Backend | Sprint 1 |
| **SEC-02** | **SEC** | CSRF Token失效或绕过 | 2 | 3 | **6** | Spring Security CSRF + Token测试 | Backend | Sprint 1 |
| **DATA-01** | **DATA** | 内存数据丢失（重启/崩溃） | 3 | 2 | **6** | 自动保存 + 用户提醒 | Backend | Sprint 1 |

#### Medium-Priority Risks (Score 3-5)

| Risk ID | Category | Description | Probability | Impact | Score | Mitigation | Owner |
|---------|----------|-------------|-------------|--------|-------|------------|-------|
| PERF-01 | PERF | 并发写入冲突 | 2 | 2 | 4 | 并发测试 + 锁机制 | Backend |
| PERF-02 | PERF | Markdown大文档渲染性能 | 2 | 2 | 4 | 性能测试 + 限制文档大小 | Frontend |
| OPS-01 | OPS | Session过期处理不当 | 2 | 2 | 4 | Session超时测试 | Backend |

#### Low-Priority Risks (Score 1-2)

| Risk ID | Category | Description | Probability | Impact | Score | Action |
|---------|----------|-------------|-------------|--------|-------|--------|
| SEC-03 | SEC | 密码泄露 | 1 | 3 | 3 | Monitor |

#### Risk Category Legend

- **TECH**: Technical/Architecture (flaws, integration, scalability)
- **SEC**: Security (access controls, auth, data exposure)
- **PERF**: Performance (SLA violations, degradation, resource limits)
- **DATA**: Data Integrity (loss, corruption, inconsistency)
- **BUS**: Business Impact (UX harm, logic errors, revenue)
- **OPS**: Operations (deployment, config, monitoring)

---

### Testability Concerns and Architectural Gaps

**🚨 ACTIONABLE CONCERNS - Architecture Team Must Address**

#### 1. Blockers to Fast Feedback (WHAT WE NEED FROM ARCHITECTURE)

| Concern | Impact | What Architecture Must Provide | Owner | Timeline |
|---------|--------|--------------------------------|-------|----------|
| **内存存储无重置API** | 测试无法并行，数据污染 | 提供POST /api/test/reset端点清空内存 | Backend | Sprint 0 |
| **无测试配置隔离** | 测试影响开发环境 | application-test.yml独立配置 | DevOps | Sprint 0 |
| **CSRF Token获取困难** | API测试无法执行 | 提供GET /api/csrf-token端点 | Backend | Sprint 0 |

#### 2. Architectural Improvements Needed (WHAT SHOULD BE CHANGED)

1. **Session测试隔离**
   - **Current problem**: Session存储在内存，测试间无法隔离
   - **Required change**: 提供测试模式下的Mock Session或Session重置机制
   - **Impact if not fixed**: E2E测试无法并行执行
   - **Owner**: Backend
   - **Timeline**: Sprint 1

2. **Markdown安全渲染验证点**
   - **Current problem**: 需要验证XSS防护是否完整
   - **Required change**: 提供安全测试用的XSS payload注入点
   - **Impact if not fixed**: 安全测试覆盖不完整
   - **Owner**: Backend
   - **Timeline**: Sprint 1

---

### Testability Assessment Summary

**📊 CURRENT STATE - FYI**

#### What Works Well

- ✅ 前后端分离架构支持独立测试
- ✅ RESTful API设计统一，易于断言
- ✅ 内存存储易于重置和种子数据注入
- ✅ 统一API响应格式 {code, message, data}

#### Accepted Trade-offs (No Action Required)

For java MVP Phase 1, the following trade-offs are acceptable:

- **内存存储非持久化** - MVP验证阶段，数据丢失可接受
- **暂无移动端适配** - 桌面端优先，符合PRD范围

---

### Risk Mitigation Plans (High-Priority Risks ≥6)

**Purpose**: Detailed mitigation strategies for all 3 high-priority risks (score ≥6).

#### SEC-01: XSS攻击绕过 (Score: 6) - CRITICAL

**Mitigation Strategy:**

1. 使用OWASP推荐的Markdown sanitizer（如CommonMark + sanitizer）
2. 输入验证：限制允许的HTML标签
3. 输出编码：确保渲染时转义危险字符

**Owner:** Backend Team
**Timeline:** Sprint 1
**Status:** Planned
**Verification:** 安全测试覆盖常见XSS payload

---

#### SEC-02: CSRF防护失效 (Score: 6) - CRITICAL

**Mitigation Strategy:**

1. 使用Spring Security内置CSRF防护
2. 所有状态变更请求验证CSRF Token
3. 前端存储Token并在请求头中传递

**Owner:** Backend Team
**Timeline:** Sprint 1
**Status:** Planned
**Verification:** API测试验证无Token请求被拒绝

---

#### DATA-01: 内存数据丢失 (Score: 6) - HIGH

**Mitigation Strategy:**

1. 编辑时每30秒自动保存
2. 离开页面前提醒未保存内容
3. 显示"已自动保存"状态提示

**Owner:** Frontend + Backend Team
**Timeline:** Sprint 1
**Status:** Planned
**Verification:** E2E测试验证自动保存功能

---

### Assumptions and Dependencies

#### Assumptions

1. 后端API按照PRD定义的RESTful规范实现
2. 前端Vue.js使用Ant Design Vue组件库
3. MVP阶段不需要性能基准测试
4. 5-6人小团队使用，不需要高并发优化

#### Dependencies

1. Spring Boot后端项目初始化完成 - Sprint 0
2. Vue.js前端项目初始化完成 - Sprint 0
3. 测试环境部署就绪 - Sprint 0

#### Risks to Plan

- **Risk**: 测试环境不稳定
  - **Impact**: 阻塞E2E测试执行
  - **Contingency**: 优先使用本地Docker环境测试

---

**End of Architecture Document**

**Next Steps for Architecture Team:**

1. Review Quick Guide (🚨/⚠️/📋) and prioritize blockers
2. Assign owners and timelines for high-priority risks (≥6)
3. Validate assumptions and dependencies
4. Provide feedback to QA on testability gaps

**Next Steps for QA Team:**

1. Wait for Sprint 0 blockers to be resolved
2. Refer to companion QA doc (test-design-qa.md) for test scenarios
3. Begin test infrastructure setup (factories, fixtures, environments)
