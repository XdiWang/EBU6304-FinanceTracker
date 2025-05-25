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
        try {
            // 设置Swing风格为系统原生风格
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // 先初始化字体
            FontLoader.initializeFont();
            boolean supportsChineseDisplay = FontLoader.supportsChineseDisplay();
            System.out.println("系统是否支持中文显示: " + supportsChineseDisplay);

            // 确保菜单栏使用正确字体
            FontLoader.fixMenuBarFont();

            // 启动登录界面
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "启动时出错: " + e.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
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

