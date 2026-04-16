#!/bin/bash

# 获取脚本所在目录的上一级（项目根目录）
PROJECT_ROOT="$(dirname "$(cd "$(dirname "$0")" && pwd)")"
ENV_FILE="$PROJECT_ROOT/.env"

# 加载上级目录的 .env 文件
if [ -f "$ENV_FILE" ]; then
    echo "📋 加载配置: $ENV_FILE"
    # 读取并导出变量（跳过注释和空行）
    export $(grep -v '^#' "$ENV_FILE" | grep -v '^$' | xargs)
else
    echo "⚠️ 未找到 .env 文件: $ENV_FILE"
    echo "   使用默认配置"
fi

CONTAINER="smartcare-db"
DB_NAME=${DB_NAME:-smartcare_db}
DB_USER=${DB_USER:-root}
DB_PASSWORD=${DB_PASSWORD:-root}

echo "🔄 执行数据库更新..."
echo "   数据库: $DB_NAME"
echo "   用户: $DB_USER"

# 检查是否有 SQL 文件
if ! ls ./migrations/*.sql 1> /dev/null 2>&1; then
    echo "⚠️  migrations/ 目录下没有 .sql 文件"
    exit 0
fi

for file in ./migrations/*.sql; do
    if [ -f "$file" ]; then
        filename=$(basename "$file")
        echo "📄 执行: $filename"

        docker cp "$file" $CONTAINER:/tmp/
        docker exec -i $CONTAINER mysql -u"$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" < "$file" -e "SET NAMES utf8mb4;"

        if [ $? -eq 0 ]; then
            echo "✅ $filename 成功"
            mkdir -p ./migrations/applied
            mv "$file" ./migrations/applied/
        else
            echo "❌ $filename 失败"
            exit 1
        fi
    fi
done

echo "🎉 所有更新完成！"
