# Git 提交规范（SmartCare）

## 1. 适用范围

本规范适用于 SmartCare 仓库所有提交。
目标是让提交信息可读、可追踪、可自动校验。

## 2. 提交信息格式（Conventional Commits）

```text
<type>(<scope>): <subject>

[optional body]

[optional footer]
```

要求：
- `type` 必填，英文小写。
- `scope` 选填，建议填写模块名。
- `subject` 必填，简洁明确，建议不超过 50 字符，末尾不加句号。
- 破坏性变更需在 footer 写：`BREAKING CHANGE: <description>`。

## 3. type 约定

- `feat` 新功能
- `fix` 缺陷修复
- `refactor` 重构（不新增功能、不修复 bug）
- `perf` 性能优化
- `docs` 文档变更
- `test` 测试变更
- `build` 构建或依赖变更
- `ci` CI/CD 变更
- `chore` 杂项任务
- `revert` 回滚提交

## 4. scope 建议

常用 scope：
- `backend`
- `frontend`
- `ai-service`
- `database`
- `api`
- `auth`
- `patient`
- `doctor`
- `docs`
- `repo`

规则：
- 一次提交跨多个模块时，选主要影响模块。
- 不建议在一个提交里写多个 scope。

## 5. 示例

```text
feat(backend): add patient follow-up api
fix(frontend): correct timezone rendering
docs(repo): update onboarding guide
ci(repo): add pr commitlint workflow
revert: revert "feat(database): add visit_summary table"
```

破坏性变更示例：

```text
feat(backend)!: migrate backend from express to spring boot

BREAKING CHANGE: backend runtime changed from Node.js/Express to Java/Spring Boot
```

## 6. 提交粒度要求

- 一个提交只做一件事（原子提交）。
- 功能改动和纯格式化改动尽量分开。
- 不混入与当前任务无关的改动。
- 提交前应完成最小自测（至少覆盖本次改动路径）。

## 7. 禁止项

- 禁止无意义提交信息：`update`、`fix bug`、`临时提交` 等。
- 禁止提交密钥、密码、token、隐私数据。
- 禁止把调试代码和无关文件一并提交。

## 8. 分支与 PR 流程（当前仓库策略）

分支角色：
- `main`：稳定主分支，受保护，禁止直推。
- `development`：日常集成分支，受保护，禁止直推。
- 功能分支：从 `development` 拉出，例如 `feat/...`、`fix/...`、`docs/...`。

标准流程：

```bash
git checkout development
git pull origin development
git checkout -b docs/ai-service-update

# 开发并提交
git add .
git commit -m "docs(ai-service): update ai module docs"

git push -u origin docs/ai-service-update
# 在 GitHub 发 PR: base=development, compare=docs/ai-service-update
```

合并后清理：

```bash
git checkout development
git pull origin development
git branch -d docs/ai-service-update
git push origin --delete docs/ai-service-update
```

## 9. 自动校验（commitlint + husky）

仓库已配置：
- `package.json`
- `commitlint.config.cjs`
- `.husky/commit-msg`

首次启用：

```bash
npm install
npm run prepare
```

校验示例：

```bash
git commit -m "update"
```

预期：提交被拦截并提示格式不符合规范。

## 10. 提交前检查清单

- 提交信息符合 `<type>(<scope>): <subject>`。
- 改动是原子性的且与任务目标一致。
- 关键自测已通过。
- 无敏感信息、无无关改动。
- PR 的 base 分支选择正确（通常为 `development`）。
