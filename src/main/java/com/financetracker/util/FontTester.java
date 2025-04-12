package com.financetracker.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

/**
 * 字体测试工具 - 用于测试中文字体显示
 */
public class FontTester extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(FontTester.class.getName());

    public FontTester() {
        setTitle("中文字体测试 - Chinese Font Test");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 创建字体信息面板
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("系统字体信息"));

        // 显示默认字体
        infoPanel.add(new JLabel("默认字体: " +
                new JLabel().getFont().getFontName() +
                " (中文显示测试: 你好，世界！)"));

        // 显示FontLoader加载的字体
        FontLoader.initializeFont();
        Font chineseFont = FontLoader.getFont(16, Font.PLAIN);
        infoPanel.add(new JLabel("FontLoader字体: " +
                chineseFont.getFontName() +
                " (中文显示测试: 你好，世界！)",
                JLabel.LEFT));

        // 显示中文支持状态
        boolean supportsChineseDisplay = FontLoader.supportsChineseDisplay();
        JLabel statusLabel = new JLabel("中文字体支持: " +
                (supportsChineseDisplay ? "已启用" : "未启用"), JLabel.LEFT);
        statusLabel.setForeground(supportsChineseDisplay ? Color.GREEN.darker() : Color.RED);
        infoPanel.add(statusLabel);

        // 添加一个使用FontLoader字体的示例文本
        JTextArea sampleText = new JTextArea(
                "这是一段中文示例文本，用于测试字体是否正确显示。\n" +
                        "This is a sample Chinese text for testing font display.\n\n" +
                        "数字和符号: 1234567890 !@#$%^&*()\n" +
                        "中文标点符号: 。，、；：、【】《》？！\n\n" +
                        "财务术语测试:\n" +
                        "收入、支出、预算、投资、储蓄、股票、基金、利息、分红、债券");
        sampleText.setFont(chineseFont);
        sampleText.setEditable(false);
        sampleText.setLineWrap(true);
        sampleText.setWrapStyleWord(true);
        sampleText.setBackground(new Color(245, 245, 245));
        sampleText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建UI组件样例
        JPanel componentPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        componentPanel.setBorder(BorderFactory.createTitledBorder("UI组件中文显示测试"));

        JButton button = new JButton("测试按钮 Test Button");
        button.setFont(chineseFont);
        componentPanel.add(button);

        JTextField textField = new JTextField("中文输入框测试 TextField Test");
        textField.setFont(chineseFont);
        componentPanel.add(textField);

        JComboBox<String> comboBox = new JComboBox<>(
                new String[] { "选项一", "选项二", "选项三" });
        comboBox.setFont(chineseFont);
        componentPanel.add(comboBox);

        // 组装界面
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(infoPanel, BorderLayout.NORTH);
        topPanel.add(new JScrollPane(sampleText), BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(componentPanel, BorderLayout.SOUTH);

        // 添加关闭按钮
        JButton closeButton = new JButton("关闭测试窗口 Close");
        closeButton.setFont(chineseFont);
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // 记录关闭事件
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LOGGER.info("字体测试窗口已关闭");
            }
        });
    }

    /**
     * 显示字体测试窗口
     */
    public static void showFontTest() {
        SwingUtilities.invokeLater(() -> {
            FontTester tester = new FontTester();
            tester.setVisible(true);
            LOGGER.info("字体测试窗口已显示");
        });
    }

    /**
     * 主方法，用于直接运行字体测试
     */
    public static void main(String[] args) {
        // 设置日志级别
        Logger.getLogger("").getHandlers()[0].setLevel(java.util.logging.Level.INFO);

        LOGGER.info("启动字体测试...");
        showFontTest();
    }
}