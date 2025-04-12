package com.financetracker.util;

//import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.Objects;
//import java.util.Set;

/**
 * 语言工具类 - 管理应用的多语言支持
 */
public class LanguageUtil {

    /**
     * 语言类，表示一种语言
     */
    public static class Language {
        private final String code;

        public Language(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            Language language = (Language) obj;
            return Objects.equals(code, language.code);
        }

        @Override
        public int hashCode() {
            return Objects.hash(code);
        }
    }

    // 支持的语言
    public static final Language ENGLISH = new Language("en");
    public static final Language CHINESE = new Language("zh");

    // 当前语言，默认为英语
    private static Language currentLanguage = ENGLISH;

    // 语言资源映射
    private static final Map<String, ResourceBundle> resourceBundles = new HashMap<>();

    // 静态初始化块加载语言资源
    static {
        try {
            // 加载中文资源
            Locale chineseLocale = Locale.CHINESE;
            resourceBundles.put(CHINESE.getCode(), ResourceBundle.getBundle("i18n.messages", chineseLocale));

            // 加载英文资源
            Locale englishLocale = Locale.ENGLISH;
            resourceBundles.put(ENGLISH.getCode(), ResourceBundle.getBundle("i18n.messages", englishLocale));
        } catch (MissingResourceException e) {
            System.err.println("警告: 无法加载语言资源文件: " + e.getMessage());

            // 如果资源文件加载失败，创建内存中的基本资源映射
            createInMemoryResources();
        }
    }

    /**
     * 如果资源文件不可用，创建内存中的基本资源
     */
    private static void createInMemoryResources() {
        Map<String, String> chineseTexts = new HashMap<>();
        Map<String, String> englishTexts = new HashMap<>();

        // 登录和注册界面
        chineseTexts.put("login.title", "AI驱动个人财务追踪器");
        chineseTexts.put("login.username", "用户名:");
        chineseTexts.put("login.password", "密码:");
        chineseTexts.put("login.login", "登录");
        chineseTexts.put("login.register", "注册");
        chineseTexts.put("login.error", "登录错误");
        chineseTexts.put("login.error.empty", "用户名和密码不能为空");
        chineseTexts.put("login.error.invalid", "用户名或密码错误");

        chineseTexts.put("register.title", "注册新账户");
        chineseTexts.put("register.username", "用户名:");
        chineseTexts.put("register.password", "密码:");
        chineseTexts.put("register.confirm", "确认密码:");
        chineseTexts.put("register.email", "电子邮箱:");
        chineseTexts.put("register.phone", "手机号码:");
        chineseTexts.put("register.register", "注册");
        chineseTexts.put("register.cancel", "取消");
        chineseTexts.put("register.error", "注册错误");
        chineseTexts.put("register.error.empty", "所有字段必须填写");
        chineseTexts.put("register.error.password", "密码不匹配");
        chineseTexts.put("register.error.exists", "用户名已存在");
        chineseTexts.put("register.error.email", "邮箱格式无效");
        chineseTexts.put("register.error.phone", "手机号格式无效");
        chineseTexts.put("register.success", "注册成功");
        chineseTexts.put("register.success.message", "注册成功！请登录。");

        // 验证界面
        chineseTexts.put("2fa.title", "两因素认证");
        chineseTexts.put("2fa.contact", "电子邮箱/电话号码:");
        chineseTexts.put("2fa.get_code", "获取验证码");
        chineseTexts.put("2fa.enter_code", "输入验证码:");
        chineseTexts.put("2fa.login", "登录");
        chineseTexts.put("2fa.resend", "重新发送");
        chineseTexts.put("2fa.error", "错误");
        chineseTexts.put("2fa.error.contact", "请输入电子邮箱或电话号码");
        chineseTexts.put("2fa.error.code", "请输入验证码");
        chineseTexts.put("2fa.error.invalid", "验证码错误");
        chineseTexts.put("2fa.send.success", "验证码发送成功");
        chineseTexts.put("2fa.send.message", "验证码已发送至");

        // 主界面
        chineseTexts.put("main.title", "AI驱动个人财务追踪器");

        // 菜单项文本
        chineseTexts.put("menu.file", "文件");
        chineseTexts.put("menu.view", "视图");
        chineseTexts.put("menu.language", "语言");
        chineseTexts.put("menu.help", "帮助");
        chineseTexts.put("menu.about", "关于");
        chineseTexts.put("menu.help_item", "帮助内容");
        chineseTexts.put("menu.logout", "退出登录");
        chineseTexts.put("menu.exit", "退出");

        chineseTexts.put("main.file", "文件");
        chineseTexts.put("main.import", "导入CSV...");
        chineseTexts.put("main.export", "导出数据...");
        chineseTexts.put("main.logout", "退出登录");
        chineseTexts.put("main.exit", "退出");

        chineseTexts.put("main.dashboard", "仪表盘");
        chineseTexts.put("main.overview", "概览");
        chineseTexts.put("main.account", "账户");
        chineseTexts.put("main.ai_chat", "AI聊天");

        chineseTexts.put("main.language", "语言");
        chineseTexts.put("main.chinese", "中文");
        chineseTexts.put("main.english", "英文");

        chineseTexts.put("main.help", "帮助");
        chineseTexts.put("main.help_contents", "帮助内容");
        chineseTexts.put("main.about", "关于");

        // 仪表盘
        chineseTexts.put("dashboard.welcome", "你好, ");
        chineseTexts.put("dashboard.income", "收入");
        chineseTexts.put("dashboard.expenses", "支出");
        chineseTexts.put("dashboard.recent", "近期交易");
        chineseTexts.put("dashboard.limits", "预算限额");
        chineseTexts.put("dashboard.daily", "日限额");
        chineseTexts.put("dashboard.monthly", "月限额");

        // 概览
        chineseTexts.put("overview.classify", "分类");
        chineseTexts.put("overview.export", "导出");
        chineseTexts.put("overview.add", "添加分类");
        chineseTexts.put("overview.edit", "编辑");
        chineseTexts.put("overview.delete", "删除");
        chineseTexts.put("overview.confirm_delete", "确认删除");
        chineseTexts.put("overview.name", "名称");
        chineseTexts.put("overview.amount", "金额");
        chineseTexts.put("overview.color", "颜色");

        // AI聊天
        chineseTexts.put("chat.greeting", "你好！我是你的AI财务助手。我可以帮助你分析支出、提供预算建议，或回答财务相关问题。我能为你做什么？");
        chineseTexts.put("chat.suggestion", "建议");
        chineseTexts.put("chat.holiday", "假期规划");
        chineseTexts.put("chat.forecast", "未来支出预测");

        // 文件操作
        chineseTexts.put("file.import", "导入CSV");
        chineseTexts.put("file.export", "导出数据");
        chineseTexts.put("file.select_format", "选择导出格式:");
        chineseTexts.put("file.csv_format", "CSV格式");
        chineseTexts.put("file.pdf_format", "PDF格式");
        chineseTexts.put("file.imported", "已导入文件:");
        chineseTexts.put("file.exported", "已导出文件:");
        chineseTexts.put("file.import_data", "导入数据");
        chineseTexts.put("file.export_data", "导出数据");
        chineseTexts.put("file.csv_files", "CSV文件 (*.csv)");
        chineseTexts.put("file.pdf_files", "PDF文件 (*.pdf)");

        // 确认对话框
        chineseTexts.put("dialog.confirm", "确认");
        chineseTexts.put("dialog.cancel", "取消");
        chineseTexts.put("dialog.yes", "是");
        chineseTexts.put("dialog.no", "否");
        chineseTexts.put("dialog.ok", "确定");

        // 登出确认
        chineseTexts.put("logout.confirm", "确认退出登录");
        chineseTexts.put("logout.message", "确定要退出登录吗？");

        // 关于和帮助
        chineseTexts.put("about.title", "关于");
        chineseTexts.put("about.message", "AI驱动个人财务追踪器\n版本 1.0\n\n一个用于追踪个人财务的综合应用，提供AI辅助功能。");
        chineseTexts.put("help.title", "帮助");
        chineseTexts.put("help.message",
                "该应用帮助您跟踪财务状况。\n\n- 使用仪表盘查看收入和支出概览\n- 使用概览分析您的支出类别\n- 使用账户管理您的财务账户\n- 使用AI聊天获取个性化财务建议");

        // 英文翻译
        englishTexts.put("login.title", "AI-Empowered Personal Finance Tracker");
        englishTexts.put("login.username", "Username:");
        englishTexts.put("login.password", "Password:");
        englishTexts.put("login.login", "Login");
        englishTexts.put("login.register", "Register");
        englishTexts.put("login.error", "Login Error");
        englishTexts.put("login.error.empty", "Username and password cannot be empty");
        englishTexts.put("login.error.invalid", "Invalid username or password");

        englishTexts.put("register.title", "Register New Account");
        englishTexts.put("register.username", "Username:");
        englishTexts.put("register.password", "Password:");
        englishTexts.put("register.confirm", "Confirm Password:");
        englishTexts.put("register.email", "Email:");
        englishTexts.put("register.phone", "Phone Number:");
        englishTexts.put("register.register", "Register");
        englishTexts.put("register.cancel", "Cancel");
        englishTexts.put("register.error", "Registration Error");
        englishTexts.put("register.error.empty", "All fields must be filled");
        englishTexts.put("register.error.password", "Passwords do not match");
        englishTexts.put("register.error.exists", "Username already exists");
        englishTexts.put("register.error.email", "Invalid email format");
        englishTexts.put("register.error.phone", "Invalid phone number format");
        englishTexts.put("register.success", "Registration Successful");
        englishTexts.put("register.success.message", "Registration successful! Please login.");

        englishTexts.put("2fa.title", "Two-Factor Authentication");
        englishTexts.put("2fa.contact", "Email / Telephone number:");
        englishTexts.put("2fa.get_code", "Get verification code");
        englishTexts.put("2fa.enter_code", "Enter verification code:");
        englishTexts.put("2fa.login", "Login");
        englishTexts.put("2fa.resend", "Resend");
        englishTexts.put("2fa.error", "Error");
        englishTexts.put("2fa.error.contact", "Please enter email or telephone number");
        englishTexts.put("2fa.error.code", "Please enter verification code");
        englishTexts.put("2fa.error.invalid", "Invalid verification code");
        englishTexts.put("2fa.send.success", "Verification Code Sent");
        englishTexts.put("2fa.send.message", "Verification code sent to");

        englishTexts.put("main.title", "AI-Empowered Personal Finance Tracker");

        // 菜单项文本(英文)
        englishTexts.put("menu.file", "File");
        englishTexts.put("menu.view", "View");
        englishTexts.put("menu.language", "Language");
        englishTexts.put("menu.help", "Help");
        englishTexts.put("menu.about", "About");
        englishTexts.put("menu.help_item", "Help Contents");
        englishTexts.put("menu.logout", "Logout");
        englishTexts.put("menu.exit", "Exit");

        englishTexts.put("main.file", "File");
        englishTexts.put("main.import", "Import CSV...");
        englishTexts.put("main.export", "Export Data...");
        englishTexts.put("main.logout", "Logout");
        englishTexts.put("main.exit", "Exit");

        englishTexts.put("main.dashboard", "Dashboard");
        englishTexts.put("main.overview", "Overview");
        englishTexts.put("main.account", "Account");
        englishTexts.put("main.ai_chat", "AI Chat");

        englishTexts.put("main.language", "Language");
        englishTexts.put("main.chinese", "Chinese");
        englishTexts.put("main.english", "English");

        englishTexts.put("main.help", "Help");
        englishTexts.put("main.help_contents", "Help Contents");
        englishTexts.put("main.about", "About");

        englishTexts.put("dashboard.welcome", "Hi, ");
        englishTexts.put("dashboard.income", "Income");
        englishTexts.put("dashboard.expenses", "Expenses");
        englishTexts.put("dashboard.recent", "Recent Transactions");
        englishTexts.put("dashboard.limits", "Budget Limits");
        englishTexts.put("dashboard.daily", "Daily Limits");
        englishTexts.put("dashboard.monthly", "Monthly Limits");

        englishTexts.put("overview.classify", "Classify");
        englishTexts.put("overview.export", "Export");
        englishTexts.put("overview.add", "Add Classification");
        englishTexts.put("overview.edit", "Edit");
        englishTexts.put("overview.delete", "Delete");
        englishTexts.put("overview.confirm_delete", "Confirm Deletion");
        englishTexts.put("overview.name", "Name");
        englishTexts.put("overview.amount", "Amount");
        englishTexts.put("overview.color", "Color");

        englishTexts.put("chat.greeting",
                "Hello! I'm your AI financial assistant. I can help you analyze expenses, provide budget advice, or answer finance-related questions. How can I assist you today?");
        englishTexts.put("chat.suggestion", "Suggestions");
        englishTexts.put("chat.holiday", "Holiday planning");
        englishTexts.put("chat.forecast", "Future spending projections");

        // 文件操作
        englishTexts.put("file.import", "Import CSV");
        englishTexts.put("file.export", "Export Data");
        englishTexts.put("file.select_format", "Select export format:");
        englishTexts.put("file.csv_format", "CSV Format");
        englishTexts.put("file.pdf_format", "PDF Format");
        englishTexts.put("file.imported", "Imported file:");
        englishTexts.put("file.exported", "Exported file:");
        englishTexts.put("file.import_data", "Import Data");
        englishTexts.put("file.export_data", "Export Data");
        englishTexts.put("file.csv_files", "CSV Files (*.csv)");
        englishTexts.put("file.pdf_files", "PDF Files (*.pdf)");

        // 确认对话框
        englishTexts.put("dialog.confirm", "Confirm");
        englishTexts.put("dialog.cancel", "Cancel");
        englishTexts.put("dialog.yes", "Yes");
        englishTexts.put("dialog.no", "No");
        englishTexts.put("dialog.ok", "OK");

        // 登出确认
        englishTexts.put("logout.confirm", "Confirm Logout");
        englishTexts.put("logout.message", "Are you sure you want to logout?");

        // 关于和帮助
        englishTexts.put("about.title", "About");
        englishTexts.put("about.message",
                "AI-Empowered Personal Finance Tracker\nVersion 1.0\n\nA comprehensive application for tracking personal finances with AI assistance.");
        englishTexts.put("help.title", "Help");
        englishTexts.put("help.message",
                "This application helps you track your finances.\n\n- Use Dashboard to view your income and expenses overview\n- Use Overview to analyze your spending categories\n- Use Account to manage your financial accounts\n- Use AI Chat to get personalized financial advice");

        // 包装到自定义资源包中
        resourceBundles.put(CHINESE.getCode(), new InMemoryResourceBundle(chineseTexts));
        resourceBundles.put(ENGLISH.getCode(), new InMemoryResourceBundle(englishTexts));
    }

    /**
     * 获取当前语言的文本
     *
     * @param key 文本键
     * @return 本地化文本
     */
    public static String getText(String key) {
        try {
            ResourceBundle bundle = resourceBundles.get(currentLanguage.getCode());
            if (bundle != null && bundle.containsKey(key)) {
                return bundle.getString(key);
            }
        } catch (Exception e) {
            System.err.println("无法获取文本键: " + key + ", 错误: " + e.getMessage());
        }

        // 如果找不到，返回键名
        return key;
    }

    /**
     * 设置当前语言
     *
     * @param language 语言
     */
    public static void setCurrentLanguage(Language language) {
        if (language != null && (CHINESE.equals(language) || ENGLISH.equals(language))) {
            currentLanguage = language;
        }
    }

    /**
     * 获取当前语言
     *
     * @return 当前语言
     */
    public static Language getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * 内存资源包实现
     */
    private static class InMemoryResourceBundle extends ResourceBundle {
        private final Map<String, String> resources;

        public InMemoryResourceBundle(Map<String, String> resources) {
            this.resources = resources;
        }

        @Override
        protected Object handleGetObject(String key) {
            return resources.get(key);
        }

        @Override
        public boolean containsKey(String key) {
            return resources.containsKey(key);
        }

        @Override
        public java.util.Enumeration<String> getKeys() {
            return java.util.Collections.enumeration(resources.keySet());
        }
    }
}