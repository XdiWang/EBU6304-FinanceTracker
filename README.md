# AI赋能个人财务追踪器

一个智能化的个人财务管理应用，基于Java Swing开发，结合AI技术提供消费分析和财务建议。

## 功能特点

- **多账户管理**：管理多个账户，如银行账户、支付宝、微信支付等
- **智能交易分类**：AI自动对交易进行分类
- **支出分析**：可视化展示消费类别占比和消费趋势
- **预算规划**：设置每日和每月预算上限
- **AI助手**：提供个性化财务建议和消费分析
- **数据导入导出**：支持CSV和PDF格式

## 技术栈

- Java Swing：构建用户界面
- JFreeChart：数据可视化
- Apache Commons CSV：处理CSV文件
- Jackson：处理JSON数据
- DeepLearning4j：AI功能支持

## 系统架构

采用MVC（Model-View-Controller）架构设计：

- **模型层（Model）**：包含用户、账户、交易和分类等核心数据模型
- **视图层（View）**：包含登录界面、主窗口、仪表盘、账户管理和AI聊天界面等
- **控制器层（Controller）**：处理用户认证和业务逻辑

## 用户界面

- **登录/注册界面**：用户身份验证
- **仪表盘**：显示月度收支概览和趋势图
- **支出概览**：使用饼图展示各类支出占比
- **账户管理**：管理多个账户和交易记录
- **AI聊天**：与AI助手聊天获取财务建议

## 系统要求

- Java 11或更高版本
- 推荐屏幕分辨率：1280x720或更高

## 安装和运行

1. 确保已安装Java 11+
2. 克隆代码库：`git clone '
3. 进入项目目录：`cd finance-tracker`
4. 使用Maven构建项目：`mvn package`
5. 运行应用：`java -jar target/finance-tracker-1.0-SNAPSHOT-jar-with-dependencies.jar`

## 开发者指南

### 项目结构

```
src/main/java/com/financetracker/
├── controller/    # 控制器类
├── model/         # 数据模型类
├── service/       # 服务类
├── util/          # 工具类
├── view/          # 视图类
└── FinanceTrackerApp.java  # 程序入口
```

### 构建说明

项目使用Maven管理依赖，可通过以下命令构建：

```
mvn clean package
```

## 未来计划功能

- 云同步：支持多设备数据同步
- 消费预测：使用机器学习预测未来消费趋势
- 定时提醒：预算超支和账单提醒
- 多语言支持：更多语言选项

## 许可证

本项目采用MIT许可证。

