module.exports = {
  extends: ["@commitlint/config-conventional"],
  rules: {
    "type-enum": [
      2,
      "always",
      [
        "feat",
        "fix",
        "refactor",
        "perf",
        "docs",
        "test",
        "build",
        "ci",
        "chore",
        "revert"
      ]
    ],
    "scope-case": [2, "always", "kebab-case"],
    "subject-empty": [2, "never"],
    "subject-full-stop": [2, "never", "."]
  }
};
