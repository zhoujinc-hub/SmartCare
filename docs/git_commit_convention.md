# Git 提交规范（SmartCare）

## 1. 提交消息格式

统一使用 Conventional Commits：

```text
<type>(<scope>): <subject>

[optional body]

[optional footer]
```

要求：

- `type` 必填，小写英文。
- `scope` 选填，建议填写模块名（如 `backend`、`frontend`、`ai-service`、`database`、`docs`）。
- `subject` 必填，使用简短祈使句，建议不超过 50 字符，末尾不加句号。
- 如有破坏性变更，在 footer 增加：`BREAKING CHANGE: <description>`。

## 2. type 取值

- `feat`: 新功能
- `fix`: 缺陷修复
- `refactor`: 重构（不新增功能、不修复 bug）
- `perf`: 性能优化
- `docs`: 文档变更
- `test`: 测试新增或修改
- `build`: 构建系统或依赖变更
- `ci`: CI/CD 配置变更
- `chore`: 其他杂项（不影响业务逻辑）
- `revert`: 回滚提交

## 3. scope 约定

建议使用以下 scope：

- `backend`
- `frontend`
- `ai-service`
- `database`
- `api`
- `auth`
- `patient`
- `doctor`
- `docs`

如果一次提交跨多个模块，优先选择“主要影响模块”；不要堆多个 scope。

## 4. 提交示例

```text
feat(backend): add patient follow-up record API
fix(frontend): correct medication reminder timezone handling
refactor(ai-service): simplify symptom feature extraction pipeline
docs(api): update authentication error code table
build: bump fastapi and pydantic versions
revert: revert "feat(database): add visit_summary table"
```

含破坏性变更：

```text
feat(api): rename /v1/patients endpoint to /v2/patients

BREAKING CHANGE: old /v1/patients endpoint has been removed
```

## 5. 原子性与粒度

- 一个提交只做一件事（单一目的）。
- 代码改动与格式化改动尽量分开提交。
- 不要把重命名、重构、功能新增、修复 bug 混在同一提交。
- 提交前确保本地可编译、可运行、核心测试通过。

## 6. 禁止项

- 禁止：`update`、`fix bug`、`修改一下`、`临时提交` 这类无意义消息。
- 禁止将敏感信息（密钥、密码、token、隐私数据）提交到仓库。
- 禁止把与本次任务无关的改动一起提交。

## 7. 分支与提交建议

- 功能分支命名建议：`feat/<module>-<short-desc>`。
- 修复分支命名建议：`fix/<module>-<short-desc>`。
- 提交频率建议：完成一个可回滚的最小单元就提交一次。

## 8. 快速检查清单（提交前）

- 提交信息符合 `<type>(<scope>): <subject>`。
- 改动是原子性的，且和任务目标一致。
- 关键测试已通过（至少覆盖改动路径）。
- 无调试代码、无无关文件、无敏感信息。

## 9. 自动校验（commitlint + husky）

仓库已提供以下配置文件：

- `package.json`
- `commitlint.config.cjs`
- `.husky/commit-msg`

首次启用请在仓库根目录执行：

```bash
npm install
npm run prepare
```

验证方式（故意提交一个错误消息）：

```bash
git commit -m "update"
```

预期：提交被拒绝，并提示提交信息不符合规范。
