#!/bin/bash
CONTAINER="smartcare-db"
DB_NAME: ${DB_NAME}
DB_USER: ${DB_USER}
DB_PASSWORD: ${DB_PASSWORD}

echo "🔄 执行数据库更新..."

for file in ./migrations/*.sql; do
    if [ -f "$file" ]; then
        filename=$(basename "$file")
        echo "📄 执行: $filename"

        docker cp "$file" $CONTAINER:/tmp/
        docker exec -i $CONTAINER mysql -u$DB_USER -p$DB_PASS $DB_NAME < "$file"

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

echo "🎉 完成！"
