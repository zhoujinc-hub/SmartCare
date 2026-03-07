# API 设计

## POST /api/events
从 AI 服务接收事件。

请求：
{
  "elder_id": 1,
  "event_type": "fall",
  "timestamp": "2026-03-07T10:20:00"
}

响应：
{
  "status": "ok"
}

## GET /api/alerts
返回警报记录。

响应：
[
 {
   "id": 1,
   "type": "fall",
   "time": "2026-03-07 10:20"
 }
]

## GET /api/elders
返回老人列表。