package com.financetracker;

//import com.financetracker.model.User;
import com.financetracker.util.FontLoader;
import com.financetracker.util.FontTester;
import com.financetracker.util.FontUtils;
import com.financetracker.util.LanguageUtil;
import com.financetracker.view.LoginFrame;
//import com.financetracker.view.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AI-Empowered Personal Finance Tracker
 * 主应用程序类 - 应用程序的入口点
 */
public class FinanceTrackerApp {
    private static final Logger LOGGER = Logger.getLogger(FinanceTrackerApp.class.getName());

    public static void main(String[] args) {
        // 设置字符编码相关的系统属性
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");

        // 设置中文本地化环境，确保UI元素能正确显示中文
        try {
            Locale.setDefault(Locale.CHINESE);
            JComponent.setDefaultLocale(Locale.CHINESE);
        } catch (Exception e) {
            LOGGER.warning("设置中文本地化环境失败: " + e.getMessage());
        }

        // 添加特殊属性以确保菜单栏中文字符正确显示
        System.setProperty("swing.aatext", "true");
        System.setProperty("awt.useSystemAAFontSettings", "on");

        // 设置全局渲染提示，改善中文字符渲染
        System.setProperty("javax.swing.JComponent.DEBUG", "false");
        UIManager.put("ToolTip.useSystemFontSettings", Boolean.FALSE);
        UIManager.put("swing.useSystemFontSettings", Boolean.FALSE);

        // 设置窗口标题栏字体（尝试解决标题栏中文显示问题）
        System.setProperty("windows.shell.font.languages", "zh-CN,zh-TW,zh-HK,zh");
        System.setProperty("awt.useSystemAAFontSettings", "on");

        // 设置JFrame窗口标题渲染属性
        UIManager.put("InternalFrame.titleFont",
                new javax.swing.plaf.FontUIResource(new Font("Microsoft YaHei", Font.PLAIN, 12)));
        UIManager.put("InternalFrame.titlePaneBackground", new Color(240, 240, 240));
        UIManager.put("RootPane.titleFont",
                new javax.swing.plaf.FontUIResource(new Font("Microsoft YaHei", Font.PLAIN, 12)));

        try {
            // 尝试修改已加载的系统编码属性（需要Java 9+）
            java.lang.reflect.Field charset = java.nio.charset.Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
        } catch (Exception e) {
            LOGGER.info("无法重置默认字符集：" + e.getMessage());
        }

        // 检查命令行参数
        final boolean testFont = hasTestFontArg(args);

        // 配置日志级别
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.INFO);

        LOGGER.info("财务跟踪应用程序启动中...");

        // 诊断系统字体支持情况
        boolean hasChineseFonts = FontUtils.hasChineseFonts();
        LOGGER.info("系统中文字体支持: " + (hasChineseFonts ? "已支持" : "不支持"));

        // 如果系统不支持中文字体，尝试从资源中提取并注册字体
        if (!hasChineseFonts) {
            LOGGER.info("系统不支持中文字体，尝试注册内嵌字体");
            try {
                // 提取内嵌字体到临时目录
                File fontFile = FontUtils.extractFontToTemp("/fonts/simhei.ttf");
                if (fontFile != null && fontFile.exists()) {
                    LOGGER.info("成功提取字体文件: " + fontFile.getAbsolutePath());

                    // 验证字体文件是否有效
                    if (FontUtils.isValidFontFile(fontFile)) {
                        // 注册字体到系统
                        Font font = FontUtils.registerExternalFont(fontFile);
                        if (font != null) {
                            LOGGER.info("已成功注册外部字体");
                            FontUtils.printFontInfo(font);
                        }
                    } else {
                        LOGGER.severe("字体文件无效: " + fontFile.getAbsolutePath());
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "注册外部字体时出错: " + e.getMessage(), e);
            }
        }

        // 输出系统可用字体信息，帮助诊断字体问题
        try {
            LOGGER.info("系统字体信息:");
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Font[] fonts = ge.getAllFonts();
            int chineseFontCount = 0;
            for (Font font : fonts) {
                if (font.canDisplayUpTo("你好") == -1) {
                    LOGGER.info("找到支持中文的系统字体: " + font.getFontName());
                    chineseFontCount++;
                }
            }
            LOGGER.info("系统中共有 " + chineseFontCount + " 个支持中文的字体");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "获取系统字体信息时出错", e);
        }

        // 设置现代化UI外观
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());

            // 初始化字体
            FontLoader.initializeFont();

            // 强制刷新所有UI组件的字体
            FontLoader.refreshAllUIComponentFonts();

            // 特别处理菜单栏字体，解决中文显示为方块的问题
            Font menuFont = FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN);
            FontUtils.applyMenuFontFix(menuFont);

            LOGGER.info("成功初始化UI和字体");
        } catch (Exception ex) {
            LOGGER.severe("初始化UI外观失败: " + ex.getMessage());
        }

        // 在EDT中运行应用程序
        SwingUtilities.invokeLater(() -> {
            // 确保语言设置已正确加载
            LOGGER.info("当前语言: " + LanguageUtil.getCurrentLanguage().getCode() +
                    " (" + (LanguageUtil.CHINESE.equals(LanguageUtil.getCurrentLanguage()) ? "中文" : "English") + ")");
            LOGGER.info("中文字体支持: " + (FontLoader.supportsChineseDisplay() ? "已启用" : "未启用"));

            // 特别修复菜单栏字体问题
            FontLoader.fixMenuBarFont();

            // 如果指定了测试字体参数，显示字体测试窗口
            if (testFont) {
                LOGGER.info("显示字体测试窗口");
                FontTester.showFontTest();
            } else {
                // 启动应用程序的登录窗口
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }

    /**
     * 检查命令行参数中是否包含测试字体的标记
     *
     * @param args 命令行参数
     * @return 如果包含测试字体的标记返回true，否则返回false
     */
    private static boolean hasTestFontArg(String[] args) {
        for (String arg : args) {
            if ("-testfont".equals(arg) || "--test-font".equals(arg)) {
                return true;
            }
        }
        return false;
    }
}