# SmartCare 项目协作指南（新成员必读）

## 1. 项目简介

SmartCare 是一个多模块协作项目，主要目录如下：

- `frontend/` 前端代码
- `backend/` 后端代码
- `ai-service/` AI 服务代码
- `database/` 数据库脚本与结构
- `docs/` 项目文档

## 2. 首次加入要做什么

1. 克隆仓库并进入目录
2. 安装 Node.js（建议 LTS 版本）
3. 在项目根目录执行：

```bash
npm install
npm run prepare
```

说明：

- `prepare` 会启用 Husky Git Hooks。
- 提交时会自动执行 `commitlint` 检查提交信息格式。

## 3. 分支规范

- 不直接在 `main` 开发。
- 功能分支：`feat/<module>-<desc>`
- 修复分支：`fix/<module>-<desc>`
- 文档分支：`docs/<module>-<desc>`

示例：

```text
feat/backend-patient-api
fix/frontend-timezone-bug
docs/repo-onboarding
```

## 4. 提交规范（必须）

提交信息格式：

```text
<type>(<scope>): <subject>
```

常用 `type`：

- `feat` 新功能
- `fix` 修复问题
- `refactor` 重构
- `docs` 文档更新
- `test` 测试变更
- `build` 构建/依赖变更
- `ci` CI 配置变更
- `chore` 其他杂项

示例：

```text
feat(backend): 新增患者随访接口
fix(frontend): 修复提醒时区显示错误
docs(repo): 更新新成员接入指南
```

参考文档：

- `docs/git_commit_convention.md`

## 5. 日常开发流程

1. 拉取最新主干：

```bash
git checkout main
git pull
```

2. 创建任务分支：

```bash
git checkout -b feat/<module>-<desc>
```

3. 开发并自测（至少覆盖你改动的路径）
4. 提交：

```bash
git add <files>
git commit -m "feat(scope): 描述"
```

5. 推送分支：

```bash
git push -u origin <branch>
```

6. 发起 PR 到 `main`（按模板填写）

## 6. PR 规则

- PR 使用模板：`.github/pull_request_template.md`
- 必填内容：变更内容、影响范围、自测记录、风险与回滚方案
- PR 会自动触发 CI：`.github/workflows/pr-check.yml`
- CI 中会校验提交信息是否符合规范

## 7. 常见操作

修改刚提交的最后一次 commit（不新增 commit）：

```bash
git add <files>
git commit --amend --no-edit
```

仅修改最后一次 commit 的提交信息：

```bash
git commit --amend -m "docs(repo): 新描述"
```

## 8. 基本要求

- 一个 commit 只做一件事（原子提交）
- 不提交密钥、token、密码、隐私数据
- 不混入与当前任务无关的改动
- 变更接口/数据库时同步更新 `docs/`

## 9. 相关文件

- `agent.md`：项目协作工作说明
- `docs/git_commit_convention.md`：提交规范完整说明
- `commitlint.config.cjs`：提交规范校验规则
- `.husky/commit-msg`：本地提交钩子
- `.github/pull_request_template.md`：PR 模板
- `.github/workflows/pr-check.yml`：PR 校验工作流

