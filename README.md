# 个人健康管理系统

一个面向个人健康数据记录、趋势分析与健康评估的全栈 Web 应用。系统支持健康指标录入、BMI 自动计算、血压/血糖/心率分级评估、ECharts 趋势可视化，以及基于 DeepSeek 的 AI 健康助手。

## 技术栈

| 层级 | 技术 |
| --- | --- |
| 后端 | Java 17、Spring Boot 2.7、MyBatis Plus 3.5、MySQL 8、JWT、Maven |
| 前端 | Vue 3、Vite 4、Element Plus、Pinia、Vue Router、Axios、ECharts |
| 工程化 | 统一响应与异常处理、Bean Validation、单元测试、环境变量外置 |

## 核心功能

- 用户注册、登录、个人信息维护、修改密码
- 基于 JWT 的无状态认证与接口权限控制
- 健康记录新增、编辑、逻辑删除、分页查询与日期筛选
- 身高体重录入后自动计算 BMI
- BMI、血压、血糖、心率单项评估与综合健康评分
- 7/14/30/90 天指标趋势图，支持正常区间参考线
- DeepSeek AI 健康问答与个性化健康建议
- AI 会话历史支持内存或 Redis 持久化，可配置过期时间
- AI 多会话管理：新建、选择、重命名、删除会话并保留消息
- 管理员角色与用户管理后台，支持查看多用户及健康记录
- 管理员操作日志，记录关键操作、请求路径、IP 与执行结果
- 系统公告管理，支持草稿、发布、编辑与删除

## 项目截图

| 页面 | 预览 |
| --- | --- |
| 健康概览 | ![健康概览](https://github.com/user-attachments/assets/20477ee6-caea-4e62-b9f9-1804ce9c6af1) |
| 健康记录列表 | ![健康记录](https://github.com/user-attachments/assets/00e8580d-e5d5-4242-b801-8f5c69ef570c) |
| 数据可视化 | ![数据可视化](https://github.com/user-attachments/assets/a009b88f-526d-49b2-8159-4ba2f6a4af9b) |
| 数据录入 | ![数据录入](https://github.com/user-attachments/assets/b02e9a03-b421-4e80-9305-8c5e73cc5054) |
| 个人中心 | ![个人中心](https://github.com/user-attachments/assets/c20f256b-42d4-4963-bc36-3154de2e9fea) |

## 项目结构

```text
health-management-system/
├── backend/                         # Spring Boot 后端
│   ├── src/main/java/com/health/
│   │   ├── common/                  # 业务异常与统一错误码
│   │   ├── config/                  # 全局异常、JWT、MyBatis Plus、CORS 配置
│   │   ├── controller/              # REST API 控制层
│   │   ├── dto/                     # 请求参数对象与校验
│   │   ├── entity/                  # 数据库实体
│   │   ├── mapper/                  # MyBatis Plus Mapper
│   │   ├── service/                 # 业务接口与实现
│   │   └── utils/                   # JWT、密码、用户上下文、健康评估工具
│   └── src/test/java/               # 单元测试
├── frontend/                        # Vue 3 前端
│   ├── src/api/                     # API 封装
│   ├── src/router/                  # 路由与登录守卫
│   ├── src/store/                   # Pinia 状态管理
│   ├── src/utils/                   # Axios 封装
│   └── src/views/                   # 页面组件
└── sql/init.sql                     # 数据库初始化脚本
```

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 16+（推荐 18+）
- MySQL 8.0+

### 2. 初始化数据库

执行 `sql/init.sql`：

```bash
mysql -u root -p < sql/init.sql
```

脚本会创建 `health_db` 数据库、`sys_user` 与 `health_record` 表，并写入测试账号：

- 用户名：`test`
- 密码：`123456`

### 3. 安全配置后端环境变量

项目通过环境变量读取敏感配置，真实密码和密钥不应提交到代码仓库。

Windows 推荐使用本地启动脚本：

```powershell
cd backend
Copy-Item .env.local.example .env.local
```

编辑 `backend/.env.local`，将 `DB_PASSWORD` 改为本机 MySQL 密码，然后运行：

```powershell
.\run-local.ps1
```

`backend/.env.local` 已被 Git 忽略，不会出现在提交或推送内容中。

Linux/macOS 可手动导出环境变量：

```bash
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password
export JWT_SECRET=replace-with-a-random-secret-of-at-least-32-characters
export DEEPSEEK_API_KEY=your_deepseek_api_key
cd backend
mvn spring-boot:run
```

后端服务默认地址为 `http://localhost:8080/api`。

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认地址为 `http://localhost:5173`，开发环境通过 Vite 将 `/api` 请求代理到后端。

## 接口概览

| 方法 | 路径 | 说明 | 认证 |
| --- | --- | --- | --- |
| POST | `/api/user/register` | 用户注册 | 否 |
| POST | `/api/user/login` | 用户登录 | 否 |
| GET | `/api/user/info` | 获取当前用户信息 | 是 |
| PUT | `/api/user/info` | 更新用户信息 | 是 |
| PUT | `/api/user/password` | 修改密码 | 是 |
| POST | `/api/health/record` | 新增健康记录 | 是 |
| PUT | `/api/health/record` | 更新健康记录 | 是 |
| DELETE | `/api/health/record/{id}` | 删除健康记录 | 是 |
| GET | `/api/health/record/list` | 分页查询记录 | 是 |
| GET | `/api/health/record/assessment` | 获取健康评估 | 是 |
| GET | `/api/health/record/trend` | 获取趋势数据 | 是 |
| POST | `/api/ai/chat` | AI 健康问答 | 是 |

## 工程实践

- 使用 `@RestControllerAdvice` 统一处理业务异常、参数校验异常与系统异常
- 使用 Bean Validation 对接口入参做声明式校验
- JWT 认证与拦截器鉴权，敏感接口不依赖 Session
- 密码采用 PBKDF2WithHmacSHA256 加随机盐存储，兼容历史 MD5 数据并支持登录后自动升级
- 使用 MyBatis Plus 逻辑删除、自动填充与分页插件
- 前端使用 Axios 拦截器统一携带 Token 并处理 401 跳转
- 通过环境变量外置数据库、JWT、DeepSeek 等敏感配置

## 测试

后端单元测试位于 `backend/src/test/java`，可执行：

```bash
cd backend
mvn test
```

## 简历项目描述建议

如需整理到简历，可参考 [docs/RESUME.md](docs/RESUME.md)。

## License

[MIT](LICENSE)
