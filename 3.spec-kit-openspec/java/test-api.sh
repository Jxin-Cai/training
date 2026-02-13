#!/bin/bash

echo "=== 测试后端API ==="

# 测试后端是否启动
echo ""
echo "1. 测试后端健康检查..."
curl -s http://localhost:8080/api/categories || echo "❌ 后端未启动或端口不对"

echo ""
echo "2. 测试分类API..."
curl -s http://localhost:8080/api/categories | head -100

echo ""
echo "3. 测试分类树API..."
curl -s http://localhost:8080/api/categories/tree | head -100

echo ""
echo "4. 测试内容API..."
curl -s http://localhost:8080/api/content | head -100

echo ""
echo "=== 测试前端代理 ==="

echo "5. 测试前端代理 (需要前端启动)..."
curl -s http://localhost:5173/api/categories | head -100

echo ""
echo "如果上面都有JSON返回，说明前后端连通成功！"
