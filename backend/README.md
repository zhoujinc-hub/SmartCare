



## 项目结构

com.smartcare
│
├─ SmartcareApplication.java   ⭐ 启动类（必须在最外层）
│
├─ config/                     ⭐ 所有配置（统一）
│   ├─ CorsConfig.java
│   ├─ SecurityConfig.java
│   └─ MybatisPlusConfig.java
│
├─ controller/                 ⭐ 接口层
│   ├─ AuthController.java
│   ├─ UserController.java
│   ├─ HealthController.java
│   └─ AIController.java
│
├─ service/                    ⭐ 业务层
│   ├─ AuthService.java
│   ├─ UserService.java
│   ├─ HealthService.java
│   ├─ AIService.java
│   │
│   └─ impl/
│       ├─ AuthServiceImpl.java
│       ├─ UserServiceImpl.java
│       ├─ HealthServiceImpl.java
│       └─ AIServiceImpl.java
│
├─ mapper/                     ⭐ MyBatis接口
│   ├─ UserMapper.java
│   ├─ HealthMapper.java
│   └─ AIChatMapper.java
│
├─ entity/                     ⭐ 数据库实体（只保留一个地方！）
│   ├─ BaseEntity.java
│   ├─ User.java
│   ├─ HealthRecord.java
│   └─ AIChat.java
│
├─ dto/                        ⭐ 接口数据
│   ├─ request/
│   └─ response/
│
├─ exception/                  ⭐ 全局异常
│   ├─ GlobalExceptionHandler.java
│   └─ BusinessException.java
│
├─ util/                       ⭐ 工具类
│   ├─ JwtUtil.java
│   ├─ DateUtil.java
│   └─ ResponseUtil.java
│
└─ common/                     ⭐ 通用返回
├─ Result.java
└─ ResultCode.java
