# 个人博客网站 Personal Blog Website

一个功能完整的个人博客系统，使用 Spring Boot + Thymeleaf + H2 数据库构建，采用现代 Glassmorphism 设计风格。

## 功能特性

### 核心功能
- ✅ **用户认证** - Spring Security 实现的注册/登录系统
- ✅ **随笔管理** - 创建、编辑、删除、置顶、收藏
- ✅ **标签系统** - 自定义标签分类
- ✅ **回收站** - 软删除与恢复功能
- ✅ **搜索功能** - 按标题和内容搜索
- ✅ **Markdown 编辑器** - 支持 Markdown 语法写作
- ✅ **AI 聊天** - 支持本地AI、DeepSeek、OpenAI 多提供商

### 界面设计
- 🎨 **Glassmorphism 风格** - 毛玻璃视觉效果
- 🌈 **主题定制** - 自定义背景颜色和图片
- 📱 **响应式布局** - 适配各种屏幕尺寸

### 开发者特性
- 📚 **RESTful API** - 完整的 REST API 接口
- 📖 **Swagger 文档** - API 在线文档
- 🧪 **单元测试** - Service 层完整测试覆盖
- 🐳 **Docker 支持** - 一键容器化部署

## 技术栈

### 后端
- **Spring Boot 2.7.18** - 应用框架
- **Spring Security** - 安全认证
- **Spring Data JPA** - 数据持久化
- **H2 Database** - 嵌入式数据库
- **Thymeleaf** - 模板引擎

### 前端
- **HTML5/CSS3** - 页面结构与样式
- **JavaScript** - 交互逻辑
- **Editormd** - Markdown 编辑器

### 工具
- **Maven** - 项目构建
- **Docker** - 容器化部署
- **JUnit + Mockito** - 单元测试
- **SpringDoc OpenAPI** - API 文档

## 项目结构

```
personal-website/
├── src/
│   ├── main/
│   │   ├── java/com/zhongbei/
│   │   │   ├── controller/     # 控制器层
│   │   │   ├── service/        # 服务层
│   │   │   ├── repository/     # 数据访问层
│   │   │   ├── model/          # 实体类
│   │   │   └── config/         # 配置类
│   │   └── resources/
│   │       ├── templates/      # Thymeleaf 模板
│   │       └── application.properties
│   └── test/                   # 测试代码
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## 快速开始

### 环境要求
- JDK 8+
- Maven 3.6+
- Docker (可选)

### 本地运行

1. **克隆项目**
```bash
git clone <repository-url>
cd personal-website
```

2. **构建项目**
```bash
mvn clean package -DskipTests
```

3. **运行应用**
```bash
java -jar target/personal-website-1.0-SNAPSHOT.jar
```

4. **访问应用**
- 主页: http://localhost:8080
- 登录页: http://localhost:8080/login
- API文档: http://localhost:8080/swagger-ui.html

### Docker 部署

1. **使用 Docker Compose (推荐)**
```bash
docker-compose up -d
```

2. **手动构建**
```bash
docker build -t personal-website .
docker run -d -p 8080:8080 --name personal-website personal-website
```

## API 文档

启动应用后访问 Swagger UI: http://localhost:8080/swagger-ui.html

### 主要接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/diaries | 获取随笔列表 |
| GET | /api/diaries/{id} | 获取随笔详情 |
| POST | /api/diaries | 创建随笔 |
| PUT | /api/diaries/{id} | 更新随笔 |
| DELETE | /api/diaries/{id} | 删除随笔 |
| GET | /api/diaries/search | 搜索随笔 |
| GET | /api/diaries/trash | 获取回收站 |
| POST | /api/diaries/{id}/restore | 恢复随笔 |
| POST | /api/diaries/{id}/pin | 置顶随笔 |
| GET | /api/tags | 获取标签列表 |
| POST | /api/tags | 创建标签 |

## 配置说明

### AI 服务配置

编辑 `src/main/resources/application.properties`:

```properties
# AI 服务提供商: local, openai, deepseek
ai.provider=local

# OpenAI API Key
# openai.api.key=your-openai-key

# DeepSeek API Key
# deepseek.api.key=your-deepseek-key
```

### 数据库配置

```properties
spring.datasource.url=jdbc:h2:file:./personal_website
spring.datasource.username=sa
spring.datasource.password=
```

## 页面路由

| 路径 | 页面 |
|------|------|
| / | 首页 |
| /login | 登录 |
| /register | 注册 |
| /diaries | 随笔列表 |
| /diaries/new | 写随笔 |
| /diaries/{id} | 查看随笔 |
| /diaries/{id}/edit | 编辑随笔 |
| /diaries/trash | 回收站 |
| /tags | 标签管理 |
| /favorites | 我的收藏 |
| /chat | AI 聊天 |
| /settings | 设置 |

## 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=DiaryServiceTest

# 生成测试报告
mvn test surefire-report:report
```

## 面试亮点说明

### 为什么选择这个项目？

1. **完整的技术栈** - 涵盖了 Java Web 开发的核心技术
2. **实际的功能实现** - 不是 Hello World，有完整的功能
3. **现代化的设计** - Glassmorphism 风格，紧跟设计趋势
4. **良好的代码结构** - 分层设计，职责清晰

### 可以突出的技术点

1. **RESTful API 设计** - 遵循 REST 规范
2. **单元测试** - 测试驱动开发的意识
3. **Docker 容器化** - DevOps 能力
4. **Swagger 文档** - API 文档自动化
5. **软删除设计** - 数据库设计技巧
6. **Spring Security** - 安全认证机制

## 截图预览

> 主页
> 随笔列表
> Markdown 编辑器
> AI 聊天

## License

MIT License
