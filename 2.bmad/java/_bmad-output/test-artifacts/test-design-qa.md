# Test Design for QA: java CMS

**Purpose:** QA团队测试执行指南。定义测试什么、如何测试，以及QA需要从其他团队获得什么。

**Date:** 2026-02-17
**Author:** Jxin
**Status:** Draft
**Project:** java

**Related:** See Architecture doc (test-design-architecture.md) for testability concerns and architectural blockers.

---

## Executive Summary

**Scope:** 轻量级CMS内容管理平台MVP测试，包含6个Epic的核心功能验证。

**Risk Summary:**

- Total Risks: 7 (3 high-priority score ≥6, 3 medium, 1 low)
- Critical Categories: Security (SEC), Data Integrity (DATA)

**Coverage Summary:**

- P0 tests: ~10 (critical paths, security)
- P1 tests: ~14 (important features, integration)
- P2 tests: ~6 (edge cases, regression)
- P3 tests: ~2 (exploratory, benchmarks)
- **Total**: ~32 tests (~2-3 weeks with 1 QA)

---

## Not in Scope

| Item | Reasoning | Mitigation |
|------|-----------|------------|
| **移动端适配测试** | MVP范围不含移动端 | 桌面端优先，后续版本覆盖 |
| **性能基准测试** | MVP阶段无SLA要求 | 手动验证加载时间<2秒 |
| **并发压力测试** | 5-6人小团队，无高并发需求 | 简单并发测试即可 |

---

## Dependencies & Test Blockers

**CRITICAL:** QA cannot proceed without these items from other teams.

### Backend/Architecture Dependencies (Sprint 0)

1. **BLK-01: 测试数据重置API** - Backend - Sprint 0
   - 需要POST /api/test/reset端点
   - 无此端点无法并行测试，数据污染

2. **BLK-02: 测试环境配置** - DevOps - Sprint 0
   - 需要application-test.yml独立配置
   - 无此配置测试影响开发环境

3. **BLK-03: CSRF Token API** - Backend - Sprint 0
   - 需要GET /api/csrf-token端点
   - 无此端点API测试无法执行

### QA Infrastructure Setup (Sprint 0)

1. **Test Data Factories** - QA
   - User factory with faker-based randomization
   - Article factory with test content
   - Category factory

2. **Test Environments** - QA
   - Local: Docker Compose (Backend + Frontend)
   - CI/CD: GitHub Actions or Jenkins

---

## Risk Assessment

**Note:** Full risk details in Architecture doc. This section summarizes risks relevant to QA test planning.

### High-Priority Risks (Score ≥6)

| Risk ID | Category | Description | Score | QA Test Coverage |
|---------|----------|-------------|-------|------------------|
| **SEC-01** | SEC | XSS攻击绕过 | **6** | P0-008, P0-009 安全测试 |
| **SEC-02** | SEC | CSRF防护失效 | **6** | P0-010 CSRF测试 |
| **DATA-01** | DATA | 内存数据丢失 | **6** | P0-006 自动保存测试 |

### Medium/Low-Priority Risks

| Risk ID | Category | Description | Score | QA Test Coverage |
|---------|----------|-------------|-------|------------------|
| PERF-01 | PERF | 并发写入冲突 | 4 | P2-001 并发测试 |
| PERF-02 | PERF | Markdown渲染性能 | 4 | P2-002 大文档测试 |
| OPS-01 | OPS | Session过期 | 4 | P1-003 Session测试 |

---

## Entry Criteria

**QA testing cannot begin until ALL of the following are met:**

- [ ] All requirements and assumptions agreed upon by QA, Dev, PM
- [ ] Test environments provisioned and accessible
- [ ] Test data factories ready or seed data available
- [ ] Sprint 0 blockers resolved (see Dependencies section)
- [ ] Feature deployed to test environment
- [ ] Backend API endpoints available for testing

## Exit Criteria

**Testing phase is complete when ALL of the following are met:**

- [ ] All P0 tests passing
- [ ] All P1 tests passing (or failures triaged and accepted)
- [ ] No open high-priority / high-severity bugs
- [ ] Test coverage agreed as sufficient by QA Lead and Dev Lead
- [ ] Security risks (SEC-01, SEC-02) mitigated and verified

---

## Test Coverage Plan

### P0 (Critical)

**Criteria:** Blocks core functionality + High risk (≥6) + No workaround + Affects majority of users

| Test ID | Requirement | Test Level | Risk Link | Notes |
|---------|-------------|------------|-----------|-------|
| **P0-001** | 用户登录成功 | API | - | 正确凭证 |
| **P0-002** | 用户登录失败 | API | - | 错误密码 |
| **P0-003** | 作者访问后台 | API | - | 角色权限 |
| **P0-004** | 读者拒绝后台 | API | - | 角色权限 |
| **P0-005** | 创建文章 | API | - | 核心功能 |
| **P0-006** | 自动保存 | E2E | DATA-01 | 30秒触发 |
| **P0-007** | 发布文章 | API | - | 核心功能 |
| **P0-008** | XSS防护 | API | SEC-01 | 恶意脚本 |
| **P0-009** | XSS渲染安全 | E2E | SEC-01 | Markdown渲染 |
| **P0-010** | CSRF防护 | API | SEC-02 | Token验证 |
| **P0-011** | 首页加载性能 | E2E | - | <2秒 |

**Total P0:** ~11 tests

---

### P1 (High)

**Criteria:** Important features + Medium risk (3-4) + Common workflows + Workaround exists but difficult

| Test ID | Requirement | Test Level | Risk Link | Notes |
|---------|-------------|------------|-----------|-------|
| **P1-001** | 登出功能 | API | - | Session清除 |
| **P1-002** | 未登录访问前台 | E2E | - | 公开阅读 |
| **P1-003** | Session超时 | API | OPS-01 | 过期处理 |
| **P1-004** | 创建分类 | API | - | CRUD |
| **P1-005** | 编辑分类 | API | - | CRUD |
| **P1-006** | 删除空分类 | API | - | 约束验证 |
| **P1-007** | 删除有文章分类（拒绝） | API | - | 约束验证 |
| **P1-008** | Markdown实时预览 | E2E | - | 编辑器 |
| **P1-009** | 草稿转发布 | API | - | 状态切换 |
| **P1-010** | 发布转草稿 | API | - | 状态切换 |
| **P1-011** | 编辑文章 | API | - | CRUD |
| **P1-012** | 删除文章 | API | - | CRUD |
| **P1-013** | 分类筛选 | E2E | - | 前台功能 |
| **P1-014** | 搜索文章 | API | - | 前台功能 |

**Total P1:** ~14 tests

---

### P2 (Medium)

**Criteria:** Secondary features + Low risk (1-2) + Edge cases + Regression prevention

| Test ID | Requirement | Test Level | Risk Link | Notes |
|---------|-------------|------------|-----------|-------|
| **P2-001** | 并发编辑 | API | PERF-01 | 冲突处理 |
| **P2-002** | 大文档渲染 | E2E | PERF-02 | 性能边界 |
| **P2-003** | 分类列表显示 | E2E | - | 后台功能 |
| **P2-004** | 用户列表显示 | E2E | - | 后台功能 |
| **P2-005** | 创建用户 | API | - | 管理功能 |
| **P2-006** | 分配角色 | API | - | 管理功能 |

**Total P2:** ~6 tests

---

### P3 (Low)

**Criteria:** Nice-to-have + Exploratory + Performance benchmarks + Documentation validation

| Test ID | Requirement | Test Level | Notes |
|---------|-------------|------------|-------|
| **P3-001** | 部署脚本验证 | E2E | deploy.sh/shutdown.sh |
| **P3-002** | 浏览器兼容性 | E2E | Chrome/Firefox/Safari |

**Total P3:** ~2 tests

---

## Execution Strategy

**Philosophy:** PR优先运行所有测试，除非有显著基础设施开销。

### Every PR: All Tests (~10-15 min)

**All functional tests** (from any priority level):

- All E2E, API tests using Playwright
- Parallelized across shards
- Total: ~32 tests

**Why run in PRs:** Fast feedback, catch issues early

### Nightly: Full Regression (~30 min)

- Full test suite with detailed reporting
- Performance baseline check
- Security scan

### Weekly: Long-Running (~1 hour)

- Comprehensive regression
- Documentation validation
- Manual exploratory testing

---

## QA Effort Estimate

**QA test development effort only**:

| Priority | Count | Effort Range | Notes |
|----------|-------|--------------|-------|
| P0 | ~11 | ~1-1.5 weeks | Security + Core flows |
| P1 | ~14 | ~1-1.5 weeks | Standard CRUD + Integration |
| P2 | ~6 | ~2-3 days | Edge cases |
| P3 | ~2 | ~1 day | Exploratory |
| **Total** | ~33 | **~2-3 weeks** | **1 QA engineer** |

**Assumptions:**

- Includes test design, implementation, debugging, CI integration
- Excludes ongoing maintenance (~10% effort)
- Assumes test infrastructure (factories, fixtures) ready

---

## Sprint Planning Handoff

| Work Item | Owner | Target Sprint | Dependencies/Notes |
|-----------|-------|---------------|-------------------|
| 测试框架搭建 | QA | Sprint 0 | 需要BLK-01~03解决 |
| P0测试实现 | QA | Sprint 1 | 需要后端API就绪 |
| P1测试实现 | QA | Sprint 1-2 | 需要P0通过 |
| P2/P3测试实现 | QA | Sprint 2 | 低优先级 |

---

## Appendix A: Test Tagging

**Playwright Tags for Selective Execution:**

```javascript
// P0 critical test - XSS Security
test('@P0 @API @Security XSS attack should be blocked', async ({ request }) => {
  const response = await request.post('/api/articles', {
    data: {
      title: 'Test',
      content: '<script>alert("xss")</script>'
    }
  });
  
  expect(response.status()).toBe(200);
  // Verify script is sanitized
});

// P1 test - CRUD
test('@P1 @API Create category successfully', async ({ request }) => {
  const response = await request.post('/api/categories', {
    data: { name: 'Test Category', description: 'Test' }
  });
  
  expect(response.status()).toBe(201);
});
```

**Run specific tags:**

```bash
# Run only P0 tests
npx playwright test --grep @P0

# Run P0 + P1 tests
npx playwright test --grep "@P0|@P1"

# Run only security tests
npx playwright test --grep @Security
```

---

## Appendix B: Knowledge Base References

- **Risk Governance**: Risk scoring methodology
- **Test Priorities Matrix**: P0-P3 criteria
- **Test Levels Framework**: E2E vs API vs Unit selection
- **Test Quality**: Definition of Done (no hard waits, <300 lines, <1.5 min)

---

**Generated by:** BMad TEA Agent
**Workflow:** `_bmad/tea/testarch/test-design`
**Version:** 4.0 (BMad v6)
