#!/bin/bash
echo "🗑️  删除旧数据..."
docker compose down -v

echo "🚀 启动新数据库..."
docker compose up -d

echo "⏳ 等待 MySQL 就绪..."
sleep 10

echo "✅ 完成！"
docker logs smartcare-db --tail 20
