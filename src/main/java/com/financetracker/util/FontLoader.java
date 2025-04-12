package com.financetracker.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Component;
import java.awt.Container;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.JComponent;
import javax.swing.AbstractButton;
import javax.swing.text.JTextComponent;
import javax.swing.JFrame;
import javax.swing.JDialog;

/**
 * 字体加载工具类，用于确保应用程序可以正确显示中文字符
 */
public class FontLoader {
    private static final Logger LOGGER = Logger.getLogger(FontLoader.class.getName());

    // 字体大小常量
    public static final int FONT_SIZE_SMALL = 12;
    public static final int FONT_SIZE_MEDIUM = 14;
    public static final int FONT_SIZE_LARGE = 16;
    public static final int FONT_SIZE_XLARGE = 18;
    public static final int FONT_SIZE_XXLARGE = 22;

    // 字体样式常量
    public static final int STYLE_PLAIN = Font.PLAIN;
    public static final int STYLE_BOLD = Font.BOLD;
    public static final int STYLE_ITALIC = Font.ITALIC;

    // 默认字体
    private static Font defaultFont;
    private static boolean initialized = false;

    // 内置字体路径
    private static final String EMBEDDED_FONT_PATH = "/fonts/simhei.ttf";

    /**
     * 初始化字体
     */
    public static void initializeFont() {
        if (initialized) {
            return;
        }

        LOGGER.info("开始初始化字体...");

        // 先尝试加载内置的simhei.ttf字体
        try {
            LOGGER.info("尝试加载内置字体: " + EMBEDDED_FONT_PATH);

            // 从资源中加载字体文件
            InputStream is = FontLoader.class.getResourceAsStream(EMBEDDED_FONT_PATH);
            if (is == null) {
                throw new IOException("无法找到字体资源: " + EMBEDDED_FONT_PATH);
            }

            try {
                // 创建字体实例
                Font baseFont = Font.createFont(Font.TRUETYPE_FONT, is);
                LOGGER.info("成功创建基础字体: " + baseFont.getFontName());

                // 注册字体到系统中
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(baseFont);
                LOGGER.info("已注册字体到系统: " + baseFont.getFontName());

                // 创建实际使用的派生字体
                defaultFont = baseFont.deriveFont(Font.PLAIN, FONT_SIZE_MEDIUM);
                LOGGER.info("创建了派生字体: " + defaultFont.getFontName() + ", 字体家族: " + defaultFont.getFamily());

                // 测试字体是否可以显示中文
                boolean canDisplay = defaultFont.canDisplayUpTo("你好，世界！") == -1;
                LOGGER.info("内置字体能否显示中文: " + (canDisplay ? "是" : "否"));

                // 立即全局应用到所有UI组件，确保菜单栏等组件也使用此字体
                applyFontToAllUIComponents(defaultFont);

                // 重新应用Look and Feel以确保字体更改生效
                try {
                    UIManager.setLookAndFeel(UIManager.getLookAndFeel());
                } catch (Exception e) {
                    LOGGER.warning("重新应用Look and Feel时出错: " + e.getMessage());
                }

                initialized = true;
                return;
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    LOGGER.warning("关闭字体资源流时出错: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "无法加载内置黑体字体，详细错误: " + e.getMessage(), e);
        }

        // 如果黑体字体加载失败，尝试加载任何中文字体
        try {
            LOGGER.info("尝试加载任何系统中文字体");

            String[] fontNames = { "Microsoft YaHei", "微软雅黑", "SimHei", "黑体", "SimSun", "宋体", "NSimSun", "新宋体",
                    "WenQuanYi Micro Hei", "文泉驿微米黑", "Arial Unicode MS", "FangSong", "仿宋", "KaiTi", "楷体" };

            for (String fontName : fontNames) {
                try {
                    Font font = new Font(fontName, Font.PLAIN, FONT_SIZE_MEDIUM);
                    if (font.canDisplayUpTo("你好，世界！") == -1) {
                        defaultFont = font;
                        LOGGER.info("找到可用的系统中文字体: " + fontName);
                        applyFontToAllUIComponents(defaultFont);
                        initialized = true;
                        return;
                    }
                } catch (Exception ex) {
                    LOGGER.warning("检查系统字体 " + fontName + " 时出错");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "检查系统中文字体时出错: " + e.getMessage(), e);
        }

        // 如果仍然找不到合适的字体，使用Java默认无衬线字体
        defaultFont = new Font(Font.SANS_SERIF, Font.PLAIN, FONT_SIZE_MEDIUM);
        LOGGER.warning("未找到合适的中文字体，使用系统默认字体: " + defaultFont.getFontName());
        applyFontToAllUIComponents(defaultFont);
        initialized = true;
    }

    /**
     * 获取字体
     *
     * @param size  字体大小
     * @param style 字体样式
     * @return 指定大小和样式的字体
     */
    public static Font getFont(int size, int style) {
        if (!initialized) {
            initializeFont();
        }
        return defaultFont.deriveFont(style, size);
    }

    /**
     * 应用字体到所有UI组件，确保菜单栏等组件也能正确显示中文
     *
     * @param font 要应用的字体
     */
    private static void applyFontToAllUIComponents(Font font) {
        LOGGER.info("开始全局应用字体到所有UI组件: " + font.getFontName());

        // 设置字体抗锯齿属性
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // 创建统一的字体资源
        FontUIResource fontUIResource = new FontUIResource(font);

        // 获取当前UI默认值
        UIDefaults defaults = UIManager.getLookAndFeelDefaults();

        // 遍历所有UI默认值，替换所有字体
        for (Object key : defaults.keySet()) {
            if (key != null && defaults.get(key) instanceof FontUIResource) {
                UIManager.put(key, fontUIResource);
                LOGGER.fine("已更新UI组件 " + key + " 的字体");
            }
        }

        // 明确设置菜单相关组件的字体，确保菜单栏能正确显示中文
        UIManager.put("MenuBar.font", fontUIResource);
        UIManager.put("Menu.font", fontUIResource);
        UIManager.put("MenuItem.font", fontUIResource);
        UIManager.put("PopupMenu.font", fontUIResource);
        UIManager.put("RadioButtonMenuItem.font", fontUIResource);
        UIManager.put("CheckBoxMenuItem.font", fontUIResource);

        // 额外设置菜单的默认字体族
        UIManager.put("MenuItem.acceleratorFont", fontUIResource);
        UIManager.put("Menu.acceleratorFont", fontUIResource);
        UIManager.put("MenuBar.titleFont", fontUIResource);

        // 禁用菜单栏的特殊字体处理
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        UIManager.put("swing.useSystemFontSettings", Boolean.FALSE);
        UIManager.put("Menu.crossMenuMnemonic", Boolean.FALSE);

        // 为菜单栏特别设置，确保中文字符正确显示
        UIManager.put("MenuBar.useSystemFont", Boolean.FALSE);
        UIManager.put("Menu.useSystemFont", Boolean.FALSE);
        UIManager.put("MenuItem.useSystemFont", Boolean.FALSE);
        UIManager.put("PopupMenu.useSystemFont", Boolean.FALSE);
        UIManager.put("RadioButtonMenuItem.useSystemFont", Boolean.FALSE);
        UIManager.put("CheckBoxMenuItem.useSystemFont", Boolean.FALSE);

        // 其他常用组件字体设置
        UIManager.put("Button.font", fontUIResource);
        UIManager.put("ToggleButton.font", fontUIResource);
        UIManager.put("RadioButton.font", fontUIResource);
        UIManager.put("CheckBox.font", fontUIResource);
        UIManager.put("ColorChooser.font", fontUIResource);
        UIManager.put("ComboBox.font", fontUIResource);
        UIManager.put("Label.font", fontUIResource);
        UIManager.put("List.font", fontUIResource);
        UIManager.put("OptionPane.font", fontUIResource);
        UIManager.put("Panel.font", fontUIResource);
        UIManager.put("ProgressBar.font", fontUIResource);
        UIManager.put("ScrollPane.font", fontUIResource);
        UIManager.put("Viewport.font", fontUIResource);
        UIManager.put("TabbedPane.font", fontUIResource);
        UIManager.put("Table.font", fontUIResource);
        UIManager.put("TableHeader.font", fontUIResource);
        UIManager.put("TextField.font", fontUIResource);
        UIManager.put("PasswordField.font", fontUIResource);
        UIManager.put("TextArea.font", fontUIResource);
        UIManager.put("TextPane.font", fontUIResource);
        UIManager.put("EditorPane.font", fontUIResource);
        UIManager.put("TitledBorder.font", fontUIResource);
        UIManager.put("ToolBar.font", fontUIResource);
        UIManager.put("ToolTip.font", fontUIResource);
        UIManager.put("Tree.font", fontUIResource);

        // 修复对话框字体
        UIManager.put("OptionPane.messageFont", fontUIResource);
        UIManager.put("OptionPane.buttonFont", fontUIResource);
        UIManager.put("OptionPane.titleFont", fontUIResource);

        LOGGER.info("已完成全局字体应用");
    }

    /**
     * 判断当前环境是否支持中文显示
     *
     * @return 如果支持中文返回true，否则返回false
     */
    public static boolean supportsChineseDisplay() {
        if (!initialized) {
            initializeFont();
        }
        return defaultFont.canDisplayUpTo("你好，世界！") == -1;
    }

    /**
     * 强制刷新所有UI组件的字体
     * 可以在应用程序运行过程中调用，以确保所有组件都使用更新后的字体
     */
    public static void refreshAllUIComponentFonts() {
        if (!initialized) {
            initializeFont();
            return;
        }

        // 重新应用字体到所有UI组件
        applyFontToAllUIComponents(defaultFont);

        // 通知所有组件更新UI
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getLookAndFeel());
                LOGGER.info("已刷新所有UI组件的外观");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "刷新UI组件时出错: " + e.getMessage(), e);
            }
        });
    }

    /**
     * 为菜单栏特别设置字体，修复菜单栏中文显示方块问题
     * 应在创建菜单栏之前调用此方法
     */
    public static void fixMenuBarFont() {
        if (!initialized) {
            initializeFont();
        }

        // 创建新的字体实例专门用于菜单栏
        Font menuFont = defaultFont.deriveFont(Font.PLAIN, FONT_SIZE_MEDIUM);
        FontUIResource menuFontResource = new FontUIResource(menuFont);

        // 设置菜单专用字体
        UIManager.put("Menu.font", menuFontResource);
        UIManager.put("MenuBar.font", menuFontResource);
        UIManager.put("MenuItem.font", menuFontResource);
        UIManager.put("PopupMenu.font", menuFontResource);
        UIManager.put("RadioButtonMenuItem.font", menuFontResource);
        UIManager.put("CheckBoxMenuItem.font", menuFontResource);

        LOGGER.info("已应用特殊字体修复到菜单栏组件");
    }

    /**
     * 递归应用字体到组件及其所有子组件
     * 用于确保整个UI树中的所有组件都使用正确的字体
     *
     * @param component 要应用字体的组件
     * @param size      字体大小
     * @param style     字体样式
     */
    public static void applyFontToComponentTree(Component component, int size, int style) {
        if (!initialized) {
            initializeFont();
        }

        Font font = getFont(size, style);

        // 应用字体到当前组件
        if (component instanceof JComponent) {
            component.setFont(font);

            // 特殊处理按钮组件
            if (component instanceof AbstractButton) {
                ((AbstractButton) component).setFocusPainted(false);
            }

            // 特殊处理文本组件
            if (component instanceof JTextComponent) {
                ((JTextComponent) component).putClientProperty("caretAspectRatio", 0.1);
                ((JTextComponent) component).putClientProperty("caretWidth", 2);
            }

            // 特殊处理JFrame和JDialog，确保标题栏字体正确
            if (component instanceof JFrame || component instanceof JDialog) {
                try {
                    // 设置窗口标题字体
                    UIManager.put("InternalFrame.titleFont", new FontUIResource(font));
                    UIManager.put("RootPane.titleFont", new FontUIResource(font));
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "无法设置窗口标题字体: " + e.getMessage());
                }
            }
        }

        // 如果是容器，递归应用到所有子组件
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                applyFontToComponentTree(child, size, style);
            }
        }
    }
}