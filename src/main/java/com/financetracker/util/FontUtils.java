package com.financetracker.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 字体工具类 - 用于诊断和修复字体问题
 */
public class FontUtils {

    private static final Logger LOGGER = Logger.getLogger(FontUtils.class.getName());

    /**
     * 检查系统中是否有中文字体
     *
     * @return 如果有中文字体返回true，否则返回false
     */
    public static boolean hasChineseFonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] fonts = ge.getAllFonts();
        for (Font font : fonts) {
            if (font.canDisplayUpTo("你好，世界！") == -1) {
                LOGGER.info("发现支持中文的系统字体: " + font.getFontName());
                return true;
            }
        }
        return false;
    }

    /**
     * 提取内嵌字体文件到临时目录
     *
     * @param fontPath 内嵌字体路径
     * @return 提取后的字体文件
     */
    public static File extractFontToTemp(String fontPath) {
        String tempDir = System.getProperty("java.io.tmpdir");
        String fontName = fontPath.substring(fontPath.lastIndexOf('/') + 1);
        File outputFile = new File(tempDir, fontName);

        try (InputStream is = FontUtils.class.getResourceAsStream(fontPath);
             OutputStream os = new FileOutputStream(outputFile)) {

            if (is == null) {
                LOGGER.severe("无法加载字体资源: " + fontPath);
                return null;
            }

            LOGGER.info("正在提取字体到临时文件: " + outputFile.getAbsolutePath());

            byte[] buffer = new byte[4096];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            LOGGER.info("字体提取成功，大小: " + outputFile.length() + " 字节");
            return outputFile;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "提取字体文件时出错: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 检测字体文件是否有效
     *
     * @param fontFile 字体文件
     * @return 如果有效返回true，否则返回false
     */
    public static boolean isValidFontFile(File fontFile) {
        if (fontFile == null || !fontFile.exists()) {
            return false;
        }

        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            LOGGER.info("成功加载字体: " + font.getFontName());
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "验证字体文件时出错: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 打印字体信息
     *
     * @param font 字体
     */
    public static void printFontInfo(Font font) {
        if (font == null) {
            LOGGER.info("字体为空");
            return;
        }

        LOGGER.info("字体名称: " + font.getFontName());
        LOGGER.info("字体家族: " + font.getFamily());
        LOGGER.info("字体样式: " + (font.isPlain() ? "普通" : (font.isBold() ? "粗体" : "斜体")));
        LOGGER.info("字体大小: " + font.getSize());
        LOGGER.info("支持中文: " + (font.canDisplayUpTo("你好，世界！") == -1 ? "是" : "否"));
    }

    /**
     * 加载并注册系统外部字体
     *
     * @param fontFile 字体文件
     * @return 加载的字体
     */
    public static Font registerExternalFont(File fontFile) {
        if (fontFile == null || !fontFile.exists()) {
            LOGGER.severe("字体文件不存在");
            return null;
        }

        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
            LOGGER.info("成功注册外部字体: " + font.getFontName());
            return font;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "注册外部字体时出错: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 特别处理菜单栏字体问题，应用于UI创建前
     *
     * @param menuFont 要应用于菜单的字体
     */
    public static void applyMenuFontFix(Font menuFont) {
        if (menuFont == null) {
            LOGGER.warning("传入的菜单字体为空，将尝试使用系统字体");
            // 尝试寻找可用的中文字体
            String[] fontNames = { "Microsoft YaHei", "微软雅黑", "SimHei", "黑体", "SimSun", "宋体",
                    "NSimSun", "新宋体", "WenQuanYi Micro Hei", "文泉驿微米黑" };

            for (String fontName : fontNames) {
                try {
                    Font font = new Font(fontName, Font.PLAIN, 14);
                    if (font.canDisplayUpTo("你好，世界！") == -1) {
                        menuFont = font;
                        LOGGER.info("发现可用于菜单的中文字体: " + fontName);
                        break;
                    }
                } catch (Exception ex) {
                    // 继续尝试下一个字体
                }
            }

            // 如果仍然没有找到合适的字体，使用默认无衬线字体
            if (menuFont == null) {
                menuFont = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
            }
        }

        LOGGER.info("应用特殊菜单字体: " + menuFont.getFontName());

        // 设置明确的字体以覆盖系统字体
        javax.swing.plaf.FontUIResource fontResource = new javax.swing.plaf.FontUIResource(menuFont);
        javax.swing.UIManager.put("MenuBar.font", fontResource);
        javax.swing.UIManager.put("Menu.font", fontResource);
        javax.swing.UIManager.put("MenuItem.font", fontResource);
        javax.swing.UIManager.put("PopupMenu.font", fontResource);
        javax.swing.UIManager.put("RadioButtonMenuItem.font", fontResource);
        javax.swing.UIManager.put("CheckBoxMenuItem.font", fontResource);

        // 禁用系统字体
        javax.swing.UIManager.put("MenuBar.useSystemFont", Boolean.FALSE);
        javax.swing.UIManager.put("Menu.useSystemFont", Boolean.FALSE);
        javax.swing.UIManager.put("MenuItem.useSystemFont", Boolean.FALSE);

        // 应用其他相关设置
        javax.swing.UIManager.put("swing.useSystemFontSettings", Boolean.FALSE);
    }
}