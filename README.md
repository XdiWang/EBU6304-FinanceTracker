# AI-Powered Personal Finance Tracker

An intelligent personal finance management application developed with Java Swing, integrating AI technology to provide spending analysis and financial advice.

## Features

### 1. Multi-language Support (Partially Implemented)
- Chinese and English interface support
- Language switching in login, registration, and main interface
- User language preference settings
- Internationalization support for all UI text (ongoing improvement)

### 2. User Authentication and Security
- User registration with username, password, email, and phone number validation
- Email format validation
- Phone number format validation (Chinese mobile numbers)
- Secure password storage
- Two-factor authentication (2FA)
- Secure logout functionality

### 3. Account Management
- Multiple account types: bank accounts, Alipay, WeChat Pay, cash, and others
- Account creation, editing, and deletion
- Automatic balance calculation and updates
- Detailed account information and transaction history
- Currency symbol display (¥)

### 4. Transaction Records
- Income and expense tracking
- Custom transaction categories
- Transaction creation, editing, and deletion
- Transaction description and amount recording
- Unique transaction ID generation
- Transaction date management

### 5. Data Visualization and Analysis (Style Improvements Ongoing)
- Dashboard overview with financial summary
- Income and expense ratio analysis
- Category-based expense analysis
- Financial trend charts
- Monthly income-expense comparison
- Expense category pie charts
- Transaction history timeline
- Monthly data selection in overview panel
- Decoupled dashboard and overview panel

### 6. AI Assistant Features (Core Implementation Complete)
- AI service selection (e.g., DeepSeek)
- Intelligent dialogue and financial advice
- Chat interface with:
  * Clear title bar
  * Distinct message bubbles
  * User and AI avatars
  * Timestamps
  * Dynamic message width
  * Auto-scrolling chat history
  * Input box with enter-to-send
- DeepSeek AI API integration
- Streaming output with typing effect
- Transaction data context for personalized advice
- Quick response options
- In-memory chat history
- Error handling for API calls

### 7. Data Import/Export
- CSV format import/export
- Data backup and recovery

### 8. Budget Management
- Monthly and daily budget limits
- Budget usage visualization
- Budget overspending warnings
- Category-based budget limits

### 9. Automatic Categorization
- Description-based automatic categorization
- Smart transaction type recognition
- Anomaly detection
- Seasonal spending pattern recognition

### 10. User Interface and Experience (Continuous Optimization)
- Modern UI with FlatLaf
- Responsive layout
- Adaptive font sizes
- Chinese character support
- Tooltips and help text
- Dynamic theme switching
- Customizable interface elements
- Optimized UI spacing and alignment

### 11. Reporting Features
- Monthly financial reports
- Annual consumption summary
- Custom period reports
- Category analysis reports
- Savings rate calculation

### 12. System Features
- Multi-platform support (Windows, macOS, Linux)
- Automatic Chinese font loading
- Advanced logging
- Command-line parameter support
- Automatic font detection and replacement
- UTF-8 encoding support

## Technology Stack

- Java Swing: Building the user interface
- JFreeChart: Data visualization
- Apache Commons CSV: Processing CSV files
- Jackson: Processing JSON data
- DeepLearning4j: AI functionality support
- JUnit: Unit testing framework

## System Architecture

Designed using MVC (Model-View-Controller) architecture:

- **Model Layer**: Contains core data models for users, accounts, transactions, and categories
- **View Layer**: Includes login interface, main window, dashboard, account management, and AI chat interface
- **Controller Layer**: Handles user authentication and business logic

## System Requirements

- Java 11 or higher
- Recommended screen resolution: 1280x720 or higher

## Installation and Running

1. Ensure Java 11+ is installed
2. Clone the repository: `git clone `
3. Enter the project directory: `cd finance-tracker`
4. Build the project with Maven: `mvn package`
5. Run the application: `java -jar target/finance-tracker-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Project Structure

```
src/main/java/com/financetracker/
├── controller/    # Controller classes
├── model/         # Data model classes
├── service/       # Service classes
├── util/          # Utility classes
├── view/          # View classes
└── FinanceTrackerApp.java  # Program entry
```

## Known Limitations

- User data and chat history are currently stored in memory only
- Some UI elements require further internationalization
- Chart styles need optimization for better readability
- Data persistence implementation pending

## License

This project is licensed under the MIT License.

---

# AI赋能个人财务追踪器

一个智能化的个人财务管理应用，基于Java Swing开发，结合AI技术提供消费分析和财务建议。

## 功能特点

### 1. 多语言支持（部分实现）
- 支持中文和英文界面
- 可在登录界面、注册界面和主界面切换语言
- 用户可以设置并保存语言偏好
- 所有UI文本均支持国际化（各面板逐步完善中）

### 2. 用户认证与安全
- 用户注册功能，支持用户名、密码、邮箱和手机号码验证
- 邮箱格式验证
- 手机号格式验证（中国手机号）
- 用户登录功能，支持密码加密存储
- 双因素身份验证（2FA）
- 安全退出功能

### 3. 账户管理
- 支持多种账户类型：银行账户、支付宝、微信支付、现金和其他
- 账户创建、编辑和删除功能
- 账户余额自动计算和更新
- 显示账户详细信息和交易历史
- 交易金额显示支持货币符号（¥）

### 4. 交易记录
- 支持收入和支出两种交易类型
- 交易分类功能，支持自定义分类
- 交易记录的创建、编辑和删除
- 交易描述和金额记录
- 自动生成唯一交易ID
- 交易日期记录和管理

### 5. 数据可视化与分析（样式持续改进中）
- 仪表盘概览，展示财务状况摘要
- 收入和支出的比例分析
- 按类别的支出分析
- 财务趋势图表
- 月度收支对比
- 支出类别饼图
- 交易历史时间线
- 概览面板支持按月选择数据
- 仪表盘与概览面板解耦

### 6. AI助手功能（核心交互已实现）
- 基于用户选择的AI服务（如DeepSeek）
- 智能对话和财务建议
- 聊天界面功能：
  * 清晰的标题栏
  * 用户和AI消息气泡区分
  * 用户和AI头像显示
  * 时间戳显示
  * 动态消息宽度
  * 自动滚动聊天记录
  * 输入框支持回车发送
- DeepSeek AI API集成
- 流式输出和打字效果
- 交易数据上下文支持
- 快速响应选项
- 内存中聊天历史
- API调用错误处理

### 7. 数据导入导出
- 支持CSV格式导入导出
- 数据备份和恢复功能

### 8. 预算管理
- 设置月度和日常预算限额
- 预算使用进度可视化
- 超出预算警告
- 按类别设置预算限额

### 9. 自动分类功能
- 基于交易描述自动分类
- 智能识别常见交易类型
- 异常支出检测
- 季节性消费模式识别

### 10. 用户界面与体验（持续优化）
- 现代化UI设计，采用FlatLaf
- 响应式界面布局
- 自适应字体大小
- 中文字符显示支持
- 标签和帮助文本
- 动态切换界面主题
- 可自定义界面元素
- 优化的UI间距和对齐

### 11. 报表功能
- 月度财务报表生成
- 年度消费总结
- 自定义时间段报表
- 类别分析报表
- 节省率计算

### 12. 系统功能
- 多平台支持（Windows, macOS, Linux）
- 中文字体自动加载
- 高级日志记录功能
- 命令行参数支持
- 自动字体检测和替换
- UTF-8编码支持

## 技术栈

- Java Swing：构建用户界面
- JFreeChart：数据可视化
- Apache Commons CSV：处理CSV文件
- Jackson：处理JSON数据
- DeepLearning4j：AI功能支持
- JUnit：单元测试框架

## 系统架构

采用MVC（Model-View-Controller）架构设计：

- **模型层（Model）**：包含用户、账户、交易和分类等核心数据模型
- **视图层（View）**：包含登录界面、主窗口、仪表盘、账户管理和AI聊天界面等
- **控制器层（Controller）**：处理用户认证和业务逻辑

## 系统要求

- Java 11或更高版本
- 推荐屏幕分辨率：1280x720或更高

## 安装和运行

1. 确保已安装Java 11+
2. 克隆代码库：`git clone`
3. 进入项目目录：`cd finance-tracker`
4. 使用Maven构建项目：`mvn package`
5. 运行应用：`java -jar target/finance-tracker-1.0-SNAPSHOT-jar-with-dependencies.jar`

## 项目结构

```
src/main/java/com/financetracker/
├── controller/    # 控制器类
├── model/         # 数据模型类
├── service/       # 服务类
├── util/          # 工具类
├── view/          # 视图类
└── FinanceTrackerApp.java  # 程序入口
```

## 已知限制

- 用户数据和聊天记录目前仅保存在内存中
- 部分UI元素需要进一步完善国际化
- 图表样式需要优化以提高可读性
- 数据持久化实现待完成

## 许可证

本项目采用MIT许可证。

