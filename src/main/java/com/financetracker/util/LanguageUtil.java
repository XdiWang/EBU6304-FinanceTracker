
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
            Map<String, String> chineseTexts = new HashMap<>();
            Map<String, String> englishTexts = new HashMap<>();

            // 直接加载菜单标题的中英文映射
            chineseTexts.put("menu.file", "文件");
            chineseTexts.put("menu.view", "视图");
            chineseTexts.put("menu.language", "语言");
            chineseTexts.put("menu.help", "帮助");
            chineseTexts.put("menu.about", "关于");
            chineseTexts.put("menu.help_item", "帮助内容");
            chineseTexts.put("menu.logout", "退出登录");
            chineseTexts.put("menu.exit", "退出");

            // 文件菜单下拉项
            chineseTexts.put("file.import", "导入CSV");
            chineseTexts.put("file.export", "导出数据");

            // 主要菜单项
            chineseTexts.put("main.dashboard", "仪表盘");
            chineseTexts.put("main.overview", "概览");
            chineseTexts.put("main.account", "账户");
            chineseTexts.put("main.ai_chat", "AI聊天");
            chineseTexts.put("main.chinese", "中文");
            chineseTexts.put("main.english", "英文");

            // 注册相关
            chineseTexts.put("register.phone", "手机号码:");
            chineseTexts.put("register.error.phone", "手机号格式无效");
            chineseTexts.put("register.error.email", "邮箱格式无效");

            englishTexts.put("menu.file", "File");
            englishTexts.put("menu.view", "View");
            englishTexts.put("menu.language", "Language");
            englishTexts.put("menu.help", "Help");
            englishTexts.put("menu.about", "About");
            englishTexts.put("menu.help_item", "Help Contents");
            englishTexts.put("menu.logout", "Logout");
            englishTexts.put("menu.exit", "Exit");

            // 文件菜单下拉项（英文）
            englishTexts.put("file.import", "Import CSV");
            englishTexts.put("file.export", "Export Data");

            // 主要菜单项（英文）
            englishTexts.put("main.dashboard", "Dashboard");
            englishTexts.put("main.overview", "Overview");
            englishTexts.put("main.account", "Account");
            englishTexts.put("main.ai_chat", "AI Chat");
            englishTexts.put("main.chinese", "Chinese");
            englishTexts.put("main.english", "English");

            // 注册相关（英文）
            englishTexts.put("register.phone", "Phone Number:");
            englishTexts.put("register.error.phone", "Invalid phone number format");
            englishTexts.put("register.error.email", "Invalid email format");

            // 加载中文资源
            Locale chineseLocale = Locale.CHINESE;
            try {
                ResourceBundle chineseBundle = ResourceBundle.getBundle("resources.i18n.messages", chineseLocale);
                resourceBundles.put(CHINESE.getCode(), chineseBundle);
            } catch (MissingResourceException e) {
                System.err.println("警告: 无法加载中文资源文件，使用内存资源替代: " + e.getMessage());
                createInMemoryResources(true); // 加载完整的中文资源
            }

            // 对于常见菜单项，使用内存映射确保总能找到翻译
            InMemoryResourceBundle chineseMenuBundle = new InMemoryResourceBundle(chineseTexts);
            resourceBundles.put(CHINESE.getCode() + "_menu", chineseMenuBundle);

            // 加载英文资源
            Locale englishLocale = Locale.ENGLISH;
            try {
                ResourceBundle englishBundle = ResourceBundle.getBundle("resources.i18n.messages", englishLocale);
                resourceBundles.put(ENGLISH.getCode(), englishBundle);
            } catch (MissingResourceException e) {
                System.err.println("警告: 无法加载英文资源文件，使用内存资源替代: " + e.getMessage());
                createInMemoryResources(false); // 加载完整的英文资源
            }

            // 对于常见菜单项，使用内存映射确保总能找到翻译
            InMemoryResourceBundle englishMenuBundle = new InMemoryResourceBundle(englishTexts);
            resourceBundles.put(ENGLISH.getCode() + "_menu", englishMenuBundle);

        } catch (Exception e) {
            System.err.println("警告: 资源加载过程中发生错误: " + e.getMessage());
            // 如果资源文件加载失败，创建内存中的基本资源映射
            createInMemoryResources(true);
            createInMemoryResources(false);
        }
    }

    /**
     * 如果资源文件不可用，创建内存中的基本资源
     */
    private static void createInMemoryResources(boolean isChinese) {
        Map<String, String> texts = new HashMap<>();

        if (isChinese) {
            // 中文翻译
            // 登录和注册界面
            texts.put("login.title", "AI驱动个人财务追踪器");
            texts.put("login.username", "用户名:");
            texts.put("login.password", "密码:");
            texts.put("login.login", "登录");
            texts.put("login.register", "注册");
            texts.put("login.error", "登录错误");
            texts.put("login.error.empty", "用户名和密码不能为空");
            texts.put("login.error.invalid", "用户名或密码错误");

            texts.put("register.title", "注册新账户");
            texts.put("register.username", "用户名:");
            texts.put("register.password", "密码:");
            texts.put("register.confirm", "确认密码:");
            texts.put("register.email", "电子邮箱:");
            texts.put("register.phone", "手机号码:");
            texts.put("register.register", "注册");
            texts.put("register.cancel", "取消");
            texts.put("register.error", "注册错误");
            texts.put("register.error.empty", "所有字段必须填写");
            texts.put("register.error.password", "密码不匹配");
            texts.put("register.error.exists", "用户名已存在");
            texts.put("register.error.email", "邮箱格式无效");
            texts.put("register.error.phone", "手机号格式无效");
            texts.put("register.success", "注册成功");
            texts.put("register.success.message", "注册成功！请登录。");

            texts.put("2fa.title", "两因素认证");
            texts.put("2fa.contact", "电子邮箱/电话号码:");
            texts.put("2fa.get_code", "获取验证码");
            texts.put("2fa.enter_code", "输入验证码:");
            texts.put("2fa.login", "登录");
            texts.put("2fa.resend", "重新发送");
            texts.put("2fa.error", "错误");
            texts.put("2fa.error.contact", "请输入电子邮箱或电话号码");
            texts.put("2fa.error.code", "请输入验证码");
            texts.put("2fa.error.invalid", "验证码错误");
            texts.put("2fa.send.success", "验证码发送成功");
            texts.put("2fa.send.message", "验证码已发送至");

            texts.put("main.title", "AI驱动个人财务追踪器");

            // 菜单项文本
            texts.put("menu.file", "文件");
            texts.put("menu.view", "视图");
            texts.put("menu.language", "语言");
            texts.put("menu.help", "帮助");
            texts.put("menu.about", "关于");
            texts.put("menu.help_item", "帮助内容");
            texts.put("menu.logout", "退出登录");
            texts.put("menu.exit", "退出");

            texts.put("main.file", "文件");
            texts.put("main.import", "导入CSV...");
            texts.put("main.export", "导出数据...");
            texts.put("main.logout", "退出登录");
            texts.put("main.exit", "退出");

            texts.put("main.dashboard", "仪表盘");
            texts.put("main.overview", "概览");
            texts.put("main.account", "账户");
            texts.put("main.ai_chat", "AI聊天");

            texts.put("main.language", "语言");
            texts.put("main.chinese", "中文");
            texts.put("main.english", "英文");

            texts.put("main.help", "帮助");
            texts.put("main.help_contents", "帮助内容");
            texts.put("main.about", "关于");

            // 仪表盘
            texts.put("dashboard.welcome", "你好, ");
            texts.put("dashboard.income", "收入");
            texts.put("dashboard.expenses", "支出");
            texts.put("dashboard.recent", "近期交易");
            texts.put("dashboard.limits", "预算限额");
            texts.put("dashboard.daily", "日限额");
            texts.put("dashboard.monthly", "月限额");

            // 概览
            texts.put("overview.classify", "分类");
            texts.put("overview.export", "导出");
            texts.put("overview.add", "添加分类");
            texts.put("overview.edit", "编辑");
            texts.put("overview.delete", "删除");
            texts.put("overview.confirm_delete", "确认删除");
            texts.put("overview.name", "名称");
            texts.put("overview.amount", "金额");
            texts.put("overview.color", "颜色");

            // AI聊天
            texts.put("chat.greeting", "你好！我是你的AI财务助手。我可以帮助你分析支出、提供预算建议，或回答财务相关问题。我能为你做什么？");
            texts.put("chat.suggestion", "建议");
            texts.put("chat.holiday", "假期规划");
            texts.put("chat.forecast", "未来支出预测");

            // 文件操作
            texts.put("file.import", "导入CSV");
            texts.put("file.export", "导出数据");
            texts.put("file.select_format", "选择导出格式:");
            texts.put("file.csv_format", "CSV格式");
            texts.put("file.pdf_format", "PDF格式");
            texts.put("file.imported", "已导入文件:");
            texts.put("file.exported", "已导出文件:");
            texts.put("file.import_data", "导入数据");
            texts.put("file.export_data", "导出数据");
            texts.put("file.csv_files", "CSV文件 (*.csv)");
            texts.put("file.pdf_files", "PDF文件 (*.pdf)");

            // 确认对话框
            texts.put("dialog.confirm", "确认");
            texts.put("dialog.cancel", "取消");
            texts.put("dialog.yes", "是");
            texts.put("dialog.no", "否");
            texts.put("dialog.ok", "确定");

            // 登出确认
            texts.put("logout.confirm", "确认退出登录");
            texts.put("logout.message", "确定要退出登录吗？");

            // 关于和帮助
            texts.put("about.title", "关于");
            texts.put("about.message", "AI驱动个人财务追踪器\n版本 1.0\n\n一个用于追踪个人财务的综合应用，提供AI辅助功能。");
            texts.put("help.title", "帮助");
            texts.put("help.message",
                    "该应用帮助您跟踪财务状况。\n\n- 使用仪表盘查看收入和支出概览\n- 使用概览分析您的支出类别\n- 使用账户管理您的财务账户\n- 使用AI聊天获取个性化财务建议");
        } else {
            // 英文翻译
            texts.put("login.title", "AI-Empowered Personal Finance Tracker");
            texts.put("login.username", "Username:");
            texts.put("login.password", "Password:");
            texts.put("login.login", "Login");
            texts.put("login.register", "Register");
            texts.put("login.error", "Login Error");
            texts.put("login.error.empty", "Username and password cannot be empty");
            texts.put("login.error.invalid", "Invalid username or password");

            texts.put("register.title", "Register New Account");
            texts.put("register.username", "Username:");
            texts.put("register.password", "Password:");
            texts.put("register.confirm", "Confirm Password:");
            texts.put("register.email", "Email:");
            texts.put("register.phone", "Phone Number:");
            texts.put("register.register", "Register");
            texts.put("register.cancel", "Cancel");
            texts.put("register.error", "Registration Error");
            texts.put("register.error.empty", "All fields must be filled");
            texts.put("register.error.password", "Passwords do not match");
            texts.put("register.error.exists", "Username already exists");
            texts.put("register.error.email", "Invalid email format");
            texts.put("register.error.phone", "Invalid phone number format");
            texts.put("register.success", "Registration Successful");
            texts.put("register.success.message", "Registration successful! Please login.");

            texts.put("2fa.title", "Two-Factor Authentication");
            texts.put("2fa.contact", "Email / Telephone number:");
            texts.put("2fa.get_code", "Get verification code");
            texts.put("2fa.enter_code", "Enter verification code:");
            texts.put("2fa.login", "Login");
            texts.put("2fa.resend", "Resend");
            texts.put("2fa.error", "Error");
            texts.put("2fa.error.contact", "Please enter email or telephone number");
            texts.put("2fa.error.code", "Please enter verification code");
            texts.put("2fa.error.invalid", "Invalid verification code");
            texts.put("2fa.send.success", "Verification Code Sent");
            texts.put("2fa.send.message", "Verification code sent to");

            texts.put("main.title", "AI-Empowered Personal Finance Tracker");

            // 菜单项文本(英文)
            texts.put("menu.file", "File");
            texts.put("menu.view", "View");
            texts.put("menu.language", "Language");
            texts.put("menu.help", "Help");
            texts.put("menu.about", "About");
            texts.put("menu.help_item", "Help Contents");
            texts.put("menu.logout", "Logout");
            texts.put("menu.exit", "Exit");

            texts.put("main.file", "File");
            texts.put("main.import", "Import CSV...");
            texts.put("main.export", "Export Data...");
            texts.put("main.logout", "Logout");
            texts.put("main.exit", "Exit");

            texts.put("main.dashboard", "Dashboard");
            texts.put("main.overview", "Overview");
            texts.put("main.account", "Account");
            texts.put("main.ai_chat", "AI Chat");

            texts.put("main.language", "Language");
            texts.put("main.chinese", "Chinese");
            texts.put("main.english", "English");

            texts.put("main.help", "Help");
            texts.put("main.help_contents", "Help Contents");
            texts.put("main.about", "About");

            texts.put("dashboard.welcome", "Hi, ");
            texts.put("dashboard.income", "Income");
            texts.put("dashboard.expenses", "Expenses");
            texts.put("dashboard.recent", "Recent Transactions");
            texts.put("dashboard.limits", "Budget Limits");
            texts.put("dashboard.daily", "Daily Limits");
            texts.put("dashboard.monthly", "Monthly Limits");

            // 其余英文翻译...
            // ...
        }

        // 包装到自定义资源包中
        if (isChinese) {
            resourceBundles.put(CHINESE.getCode(), new InMemoryResourceBundle(texts));
        } else {
            resourceBundles.put(ENGLISH.getCode(), new InMemoryResourceBundle(texts));
        }
    }

    /**
     * 获取当前语言的文本
     *
     * @param key 文本键
     * @return 本地化文本
     */
    public static String getText(String key) {
        try {
            // 对于菜单项，优先从菜单专用资源包中查找
            if (key.startsWith("menu.") || key.startsWith("file.") || key.startsWith("main.")) {
                ResourceBundle menuBundle = resourceBundles.get(currentLanguage.getCode() + "_menu");
                if (menuBundle != null && menuBundle.containsKey(key)) {
                    return menuBundle.getString(key);
                }
            }

            // 从常规资源包中查找
            ResourceBundle bundle = resourceBundles.get(currentLanguage.getCode());
            if (bundle != null && bundle.containsKey(key)) {
                return bundle.getString(key);
            }
        } catch (Exception e) {
            System.err.println("无法获取文本键: " + key + ", 错误: " + e.getMessage());
        }

        // 常见菜单项硬编码翻译
        if (key.equals("menu.file")) {
            return currentLanguage.equals(CHINESE) ? "文件" : "File";
        } else if (key.equals("menu.view")) {
            return currentLanguage.equals(CHINESE) ? "视图" : "View";
        } else if (key.equals("menu.language")) {
            return currentLanguage.equals(CHINESE) ? "语言" : "Language";
        } else if (key.equals("menu.help")) {
            return currentLanguage.equals(CHINESE) ? "帮助" : "Help";
        } else if (key.equals("register.phone")) {
            return currentLanguage.equals(CHINESE) ? "手机号码:" : "Phone Number:";
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
