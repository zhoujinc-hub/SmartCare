# SmartCare 新成员接入指南

## 1. 你需要先了解什么

SmartCare 是一个多模块协作项目，主要目录：

- `frontend/` 前端代码
- `backend/` 后端代码
- `ai-service/` AI 服务代码
- `database/` 数据库脚本与结构
- `docs/` 项目文档

## 2. 首次接入

1. 克隆仓库并进入项目目录
2. 安装 Node.js（建议 LTS）
3. 在仓库根目录执行：

```bash
npm install
npm run prepare
```

说明：
- `prepare` 会启用 Husky Git hooks
- 提交时会自动执行 commitlint 校验

## 3. 分支规范

- 不直接在 `main` 开发
- 功能分支：`feat/<module>-<desc>`
- 修复分支：`fix/<module>-<desc>`
- 文档分支：`docs/<module>-<desc>`

示例：

```text
feat/backend-patient-api
fix/frontend-timezone-bug
docs/repo-onboarding
```

## 4. 提交规范

格式：

```text
<type>(<scope>): <subject>
```

常用类型：
- `feat` 新功能
- `fix` 修复问题
- `refactor` 重构
- `docs` 文档更新
- `test` 测试变更
- `build` 构建或依赖变更
- `ci` CI 变更
- `chore` 杂项

示例：

```text
feat(backend): 新增患者随访接口
fix(frontend): 修复提醒时区显示错误
docs(repo): 新增新成员接入指南
```

完整规范见：`docs/git_commit_convention.md`

## 5. 日常开发流程

1. 拉最新主干：

```bash
git checkout main
git pull
```

2. 创建分支：

```bash
git checkout -b feat/<module>-<desc>
```

3. 开发与本地自测
4. 提交改动：

```bash
git add <files>
git commit -m "feat(scope): 描述"
```

5. 推送分支：

```bash
git push -u origin <branch>
```

6. 发起 PR 到 `main`

## 6. PR 要求

- 使用模板：`.github/pull_request_template.md`
- 说明变更内容、影响范围、自测记录、风险与回滚方案
- PR 会自动触发 CI：`.github/workflows/pr-check.yml`

## 7. 常用 Git 操作

并入上一条提交（不新增 commit）：

```bash
git add <files>
git commit --amend --no-edit
```

仅修改上一条提交信息：

```bash
git commit --amend -m "docs(repo): 新描述"
```

## 8. 基本协作要求

- 一个 commit 只做一件事
- 不提交密钥、token、密码、隐私数据
- 不混入与当前任务无关的改动
- 接口/数据库变更必须同步更新 docs

## 9. 相关文件

- `agent.md`
- `docs/git_commit_convention.md`
- `commitlint.config.cjs`
- `.husky/commit-msg`
- `.github/pull_request_template.md`
- `.github/workflows/pr-check.yml`
