package com.financetracker.view;

import com.financetracker.model.*;
import com.financetracker.service.AIService;
import com.financetracker.service.DeepSeekAPIService;
import com.financetracker.util.FontLoader;
import com.financetracker.util.LanguageUtil;
import com.financetracker.util.LanguageUtil.Language;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AI聊天面板 - 允许用户与AI助手聊天获取财务建议
 */
public class AIChatPanel extends JPanel {

    private User currentUser;
    private AIService aiService;
    private DeepSeekAPIService deepSeekService;
    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private JTextField inputField;
    private JButton sendButton;
    private JPanel quickOptionsPanel;
    private JCheckBox useDeepSeekCheckBox;
    private ExecutorService executorService;

    private List<String> chatHistory = new ArrayList<>();
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private Color userAvatarBgColor = new Color(66, 133, 244); // Google蓝色
    private Color aiAvatarBgColor = new Color(15, 157, 88); // Google绿色
    private Font avatarFont = new Font("Arial", Font.BOLD, 14);

    // 添加新的字段来支持流式输出
    private JPanel currentAIMessagePanel;
    private JTextArea currentAIMessageArea;
    private String currentStreamedMessage = "";

    public AIChatPanel(User user) {
        this.currentUser = user;
        this.aiService = new AIService();
        this.deepSeekService = new DeepSeekAPIService();
        this.executorService = Executors.newSingleThreadExecutor();
        setupUI();
        addInitialMessages();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 模拟窗口标题栏
        JPanel titleBarPanel = createTitleBarPanel();

        // 主内容面板
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);

        // 聊天区域 - 使用垂直BoxLayout来显示消息气泡
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(new Color(245, 245, 245));

        // 为聊天区域添加滚动功能
        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // 底部输入面板
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // 输入框和发送按钮
        inputField = new JTextField();
        inputField.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        // 底部工具面板，包含输入框和按钮
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setBackground(Color.WHITE);

        // 添加表情和上传按钮
        JPanel toolButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        toolButtonsPanel.setBackground(Color.WHITE);

        // 添加DeepSeek API切换选项
        useDeepSeekCheckBox = new JCheckBox("使用DeepSeek AI");
        useDeepSeekCheckBox.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_SMALL, FontLoader.STYLE_PLAIN));
        useDeepSeekCheckBox.setSelected(true);
        toolButtonsPanel.add(useDeepSeekCheckBox);

        JButton emojiButton = new JButton("😊");
        emojiButton.setBorderPainted(false);
        emojiButton.setContentAreaFilled(false);
        emojiButton.setFocusPainted(false);
        emojiButton.setFont(new Font("Arial", Font.PLAIN, 20));

        JButton uploadButton = new JButton("📎");
        uploadButton.setBorderPainted(false);
        uploadButton.setContentAreaFilled(false);
        uploadButton.setFocusPainted(false);
        uploadButton.setFont(new Font("Arial", Font.PLAIN, 20));

        toolButtonsPanel.add(emojiButton);
        toolButtonsPanel.add(uploadButton);

        // 发送按钮
        sendButton = new JButton("↑");
        sendButton.setFont(new Font("Arial", Font.BOLD, 18));
        sendButton.setForeground(Color.WHITE);
        sendButton.setBackground(new Color(0, 102, 102));
        sendButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        sendButton.setFocusPainted(false);

        // 组装底部面板
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(toolButtonsPanel, BorderLayout.WEST);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        // 快速选项按钮面板
        quickOptionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        quickOptionsPanel.setBackground(Color.WHITE);
        quickOptionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton suggestionsButton = createQuickOptionButton("😀 " + LanguageUtil.getText("chat.suggestion"));
        JButton holidayButton = createQuickOptionButton("❤️ " + LanguageUtil.getText("chat.holiday"));
        JButton forecastButton = createQuickOptionButton("📊 " + LanguageUtil.getText("chat.forecast"));

        quickOptionsPanel.add(suggestionsButton);
        quickOptionsPanel.add(holidayButton);
        quickOptionsPanel.add(forecastButton);

        // 将输入组件和快速选项添加到输入面板
        inputPanel.add(bottomPanel, BorderLayout.CENTER);
        inputPanel.add(quickOptionsPanel, BorderLayout.SOUTH);

        // 添加组件到内容面板
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(inputPanel, BorderLayout.SOUTH);

        // 添加标题栏和内容到主面板
        add(titleBarPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // 添加事件监听器
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        // 为快速选项按钮添加事件监听器
        suggestionsButton.addActionListener(e -> {
            inputField.setText("给我一些省钱的建议");
            sendMessage();
        });

        holidayButton.addActionListener(e -> {
            inputField.setText("如何为即将到来的假期制定预算？");
            sendMessage();
        });

        forecastButton.addActionListener(e -> {
            inputField.setText("分析我的支出并预测下个月的趋势");
            sendMessage();
        });
    }

    private JPanel createTitleBarPanel() {
        JPanel titleBarPanel = new JPanel(new BorderLayout());
        titleBarPanel.setPreferredSize(new Dimension(getWidth(), 40));
        titleBarPanel.setBackground(new Color(240, 240, 240));

        // 标题
        JLabel titleLabel = new JLabel(LanguageUtil.getText("main.title"), JLabel.CENTER);
        titleLabel.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));

        // 窗口按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        buttonPanel.setOpaque(false);

        // 创建红、黄、绿三个圆形按钮
        JPanel redButton = createCircleButton(new Color(255, 95, 87));
        JPanel yellowButton = createCircleButton(new Color(255, 189, 46));
        JPanel greenButton = createCircleButton(new Color(39, 201, 63));

        buttonPanel.add(redButton);
        buttonPanel.add(yellowButton);
        buttonPanel.add(greenButton);

        titleBarPanel.add(buttonPanel, BorderLayout.WEST);
        titleBarPanel.add(titleLabel, BorderLayout.CENTER);

        return titleBarPanel;
    }

    private JPanel createCircleButton(Color color) {
        JPanel button = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(color);
                g.fillOval(0, 0, 12, 12);
            }
        };
        button.setPreferredSize(new Dimension(15, 15));
        button.setOpaque(false);
        return button;
    }

    private JButton createQuickOptionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_SMALL, FontLoader.STYLE_PLAIN));
        button.setBackground(new Color(245, 245, 245));
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        button.setFocusPainted(false);
        return button;
    }

    private void addInitialMessages() {
        String welcomeMessage;
        if (LanguageUtil.CHINESE.equals(LanguageUtil.getCurrentLanguage())) {
            welcomeMessage = "欢迎使用AI助手！你可以向我询问有关财务管理、预算规划、投资建议等问题。";
        } else {
            welcomeMessage = "Welcome to AI Assistant! You can ask me questions about financial management, budget planning, investment advice, etc.";
        }
        appendMessage("AI助手", welcomeMessage, false);
    }

    private void sendMessage() {
        String userInput = inputField.getText().trim();
        if (userInput.isEmpty()) {
            return;
        }

        // 添加用户消息到聊天区域
        appendMessage("用户", userInput, true);
        inputField.setText("");

        // 显示"AI正在思考"的消息
        JPanel typingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        typingPanel.setOpaque(false);
        typingPanel.setName("typingIndicator");
        typingPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // 创建AI头像（使用与消息一致的样式）
        JPanel aiAvatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 填充圆形背景
                g2d.setColor(aiAvatarBgColor);
                g2d.fillOval(0, 0, getWidth(), getHeight());

                // 添加AI图标
                g2d.setColor(Color.WHITE);
                g2d.setFont(avatarFont);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth("AI");
                int textHeight = fm.getHeight();
                g2d.drawString("AI", (getWidth() - textWidth) / 2, (getHeight() + textHeight / 3) / 2);

                // 添加边框
                g2d.setColor(aiAvatarBgColor.darker());
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawOval(0, 0, getWidth() - 1, getHeight() - 1);

                g2d.dispose();
            }
        };
        aiAvatar.setPreferredSize(new Dimension(36, 36));
        aiAvatar.setMaximumSize(new Dimension(36, 36));
        aiAvatar.setOpaque(false);

        String thinkingText = LanguageUtil.CHINESE.equals(LanguageUtil.getCurrentLanguage())
                ? "AI助手正在思考..."
                : "AI Assistant is thinking...";
        JLabel typingLabel = new JLabel(thinkingText);
        typingLabel.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));

        typingPanel.add(aiAvatar);
        typingPanel.add(typingLabel);
        chatPanel.add(typingPanel);
        chatPanel.revalidate();
        chatPanel.repaint();
        scrollToBottom();

        // 获取用户的交易记录（如果有）
        List<Transaction> transactions = null;
        if (currentUser != null && currentUser.getAccounts() != null) {
            transactions = new ArrayList<>();
            for (Account account : currentUser.getAccounts()) {
                if (account.getTransactions() != null) {
                    transactions.addAll(account.getTransactions());
                }
            }
        }

        // 为了处理流式输出，我们先创建并显示一个空的AI消息面板
        // 移除"正在思考"指示器
        for (Component comp : chatPanel.getComponents()) {
            if (comp instanceof JPanel && "typingIndicator".equals(comp.getName())) {
                chatPanel.remove(comp);
                break;
            }
        }

        // 准备一个空消息和面板，后续用于流式更新
        currentStreamedMessage = "";
        prepareEmptyAIMessagePanel();

        final List<Transaction> finalTransactions = transactions;

        // 根据用户选择使用DeepSeek API或本地AI服务
        if (useDeepSeekCheckBox.isSelected()) {
            try {
                // 使用流式API
                deepSeekService.streamChat(
                        userInput,
                        finalTransactions,
                        // 部分响应回调 - 逐步更新UI
                        partialText -> {
                            SwingUtilities.invokeLater(() -> {
                                currentStreamedMessage += partialText;
                                updateStreamMessage(currentStreamedMessage);
                            });
                        },
                        // 完成回调
                        completeText -> {
                            SwingUtilities.invokeLater(() -> {
                                // 最终更新并完成消息
                                currentStreamedMessage = completeText;
                                updateStreamMessage(currentStreamedMessage);
                                currentAIMessagePanel = null;
                                currentAIMessageArea = null;
                            });
                        });
            } catch (Exception e) {
                e.printStackTrace();
                // 如果DeepSeek API调用失败，回退到本地AI服务
                String fallbackResponse = "DeepSeek API调用失败，使用本地AI：\n\n"
                        + aiService.getPersonalizedChatAdvice(userInput, finalTransactions);

                SwingUtilities.invokeLater(() -> {
                    currentStreamedMessage = fallbackResponse;
                    updateStreamMessage(currentStreamedMessage);
                    currentAIMessagePanel = null;
                    currentAIMessageArea = null;
                });
            }
        } else {
            // 使用本地AI服务（非流式）
            CompletableFuture.<String>supplyAsync(() -> {
                return aiService.getPersonalizedChatAdvice(userInput, finalTransactions);
            }, executorService).thenAccept(response -> {
                SwingUtilities.invokeLater(() -> {
                    // 完整更新消息
                    currentStreamedMessage = response;
                    updateStreamMessage(currentStreamedMessage);
                    currentAIMessagePanel = null;
                    currentAIMessageArea = null;
                });
            });
        }
    }

    /**
     * 准备一个空的AI消息面板，用于流式更新
     */
    private void prepareEmptyAIMessagePanel() {
        LocalDateTime now = LocalDateTime.now();
        String timeStr = now.format(TIME_FORMATTER);

        // 创建消息气泡面板
        JPanel bubblePanel = new JPanel(new BorderLayout(5, 5));
        bubblePanel.setOpaque(false);

        // AI消息 - 靠左，灰色背景
        bubblePanel.setLayout(new BorderLayout(5, 5));

        // AI头像（简单圆形）
        JPanel aiAvatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 填充圆形背景
                g2d.setColor(aiAvatarBgColor);
                g2d.fillOval(0, 0, getWidth(), getHeight());

                // 添加AI图标
                g2d.setColor(Color.WHITE);
                g2d.setFont(avatarFont);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth("AI");
                int textHeight = fm.getHeight();
                g2d.drawString("AI", (getWidth() - textWidth) / 2, (getHeight() + textHeight / 3) / 2);

                // 添加边框
                g2d.setColor(aiAvatarBgColor.darker());
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawOval(0, 0, getWidth() - 1, getHeight() - 1);

                g2d.dispose();
            }
        };
        aiAvatar.setPreferredSize(new Dimension(36, 36));
        aiAvatar.setMaximumSize(new Dimension(36, 36));
        aiAvatar.setOpaque(false);

        // 消息内容（开始为空）
        currentAIMessageArea = new JTextArea("");
        currentAIMessageArea.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));
        currentAIMessageArea.setLineWrap(true);
        currentAIMessageArea.setWrapStyleWord(true);
        currentAIMessageArea.setEditable(false);
        currentAIMessageArea.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        currentAIMessageArea.setBackground(new Color(230, 230, 230));

        // 消息气泡容器
        JPanel bubbleContainer = new JPanel();
        bubbleContainer.setLayout(new BoxLayout(bubbleContainer, BoxLayout.Y_AXIS));
        bubbleContainer.setOpaque(false);

        // 消息内容面板
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(new Color(230, 230, 230));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        messagePanel.add(currentAIMessageArea);

        // 时间标签
        JLabel timeLabel = new JLabel(timeStr, JLabel.LEFT);
        timeLabel.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_SMALL, FontLoader.STYLE_PLAIN));
        timeLabel.setForeground(Color.GRAY);

        bubbleContainer.add(messagePanel);
        bubbleContainer.add(timeLabel);

        // 组装AI消息面板
        JPanel aiMessagePanel = new JPanel(new BorderLayout(10, 0));
        aiMessagePanel.setOpaque(false);
        aiMessagePanel.add(aiAvatar, BorderLayout.WEST);
        aiMessagePanel.add(bubbleContainer, BorderLayout.CENTER);

        // 在左侧添加一些空间
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(aiMessagePanel, BorderLayout.CENTER);
        wrapperPanel.add(Box.createHorizontalStrut(80), BorderLayout.EAST);

        bubblePanel.add(wrapperPanel, BorderLayout.CENTER);

        // 添加边距
        JPanel paddingPanel = new JPanel(new BorderLayout());
        paddingPanel.setOpaque(false);
        paddingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        paddingPanel.add(bubblePanel);

        // 保存引用以便后续更新
        currentAIMessagePanel = paddingPanel;

        // 添加到聊天面板
        chatPanel.add(currentAIMessagePanel);
        chatPanel.revalidate();
        chatPanel.repaint();

        // 记录聊天历史
        chatHistory.add("AI助手: ");

        // 自动滚动到底部
        scrollToBottom();
    }

    /**
     * 更新流式消息内容
     * 
     * @param text 当前累积的消息文本
     */
    private void updateStreamMessage(String text) {
        if (currentAIMessageArea != null) {
            currentAIMessageArea.setText(text);

            // 更新聊天历史
            if (!chatHistory.isEmpty()) {
                chatHistory.set(chatHistory.size() - 1, "AI助手: " + text);
            }

            // 自动滚动到底部
            scrollToBottom();
        }
    }

    private void appendMessage(String sender, String message, boolean isUser) {
        LocalDateTime now = LocalDateTime.now();
        String timeStr = now.format(TIME_FORMATTER);

        // 创建消息气泡面板
        JPanel bubblePanel = new JPanel(new BorderLayout(5, 5));
        bubblePanel.setOpaque(false);

        // 消息内容
        JTextArea messageArea = new JTextArea(message);
        messageArea.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setEditable(false);
        messageArea.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        // 根据消息发送者设置不同的样式
        if (isUser) {
            // 用户消息 - 靠右，蓝色背景
            bubblePanel.setLayout(new BorderLayout(5, 5));

            // 用户头像（简单圆形）
            JPanel userAvatar = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // 填充圆形背景
                    g2d.setColor(userAvatarBgColor);
                    g2d.fillOval(0, 0, getWidth(), getHeight());

                    // 添加用户图标
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(avatarFont);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth("U");
                    int textHeight = fm.getHeight();
                    g2d.drawString("U", (getWidth() - textWidth) / 2, (getHeight() + textHeight / 3) / 2);

                    // 添加边框
                    g2d.setColor(userAvatarBgColor.darker());
                    g2d.setStroke(new BasicStroke(1.5f));
                    g2d.drawOval(0, 0, getWidth() - 1, getHeight() - 1);

                    g2d.dispose();
                }
            };
            userAvatar.setPreferredSize(new Dimension(36, 36));
            userAvatar.setMaximumSize(new Dimension(36, 36));
            userAvatar.setOpaque(false);

            // 消息气泡容器
            JPanel bubbleContainer = new JPanel();
            bubbleContainer.setLayout(new BoxLayout(bubbleContainer, BoxLayout.Y_AXIS));
            bubbleContainer.setOpaque(false);

            // 消息内容面板
            JPanel messagePanel = new JPanel(new BorderLayout());
            messagePanel.setBackground(new Color(0, 132, 255));
            messagePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
            messageArea.setBackground(new Color(0, 132, 255));
            messageArea.setForeground(Color.WHITE);
            messagePanel.add(messageArea);

            // 时间标签
            JLabel timeLabel = new JLabel(timeStr, JLabel.RIGHT);
            timeLabel.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_SMALL, FontLoader.STYLE_PLAIN));
            timeLabel.setForeground(Color.GRAY);

            bubbleContainer.add(messagePanel);
            bubbleContainer.add(timeLabel);

            // 组装用户消息面板
            JPanel userMessagePanel = new JPanel(new BorderLayout(10, 0));
            userMessagePanel.setOpaque(false);
            userMessagePanel.add(bubbleContainer, BorderLayout.CENTER);
            userMessagePanel.add(userAvatar, BorderLayout.EAST);

            // 在右侧添加一些空间
            JPanel wrapperPanel = new JPanel(new BorderLayout());
            wrapperPanel.setOpaque(false);
            wrapperPanel.add(userMessagePanel, BorderLayout.CENTER);
            wrapperPanel.add(Box.createHorizontalStrut(80), BorderLayout.WEST);

            bubblePanel.add(wrapperPanel, BorderLayout.CENTER);
        } else {
            // AI消息 - 靠左，灰色背景
            bubblePanel.setLayout(new BorderLayout(5, 5));

            // AI头像（简单圆形）
            JPanel aiAvatar = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // 填充圆形背景
                    g2d.setColor(aiAvatarBgColor);
                    g2d.fillOval(0, 0, getWidth(), getHeight());

                    // 添加AI图标
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(avatarFont);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth("AI");
                    int textHeight = fm.getHeight();
                    g2d.drawString("AI", (getWidth() - textWidth) / 2, (getHeight() + textHeight / 3) / 2);

                    // 添加边框
                    g2d.setColor(aiAvatarBgColor.darker());
                    g2d.setStroke(new BasicStroke(1.5f));
                    g2d.drawOval(0, 0, getWidth() - 1, getHeight() - 1);

                    g2d.dispose();
                }
            };
            aiAvatar.setPreferredSize(new Dimension(36, 36));
            aiAvatar.setMaximumSize(new Dimension(36, 36));
            aiAvatar.setOpaque(false);

            // 消息气泡容器
            JPanel bubbleContainer = new JPanel();
            bubbleContainer.setLayout(new BoxLayout(bubbleContainer, BoxLayout.Y_AXIS));
            bubbleContainer.setOpaque(false);

            // 消息内容面板
            JPanel messagePanel = new JPanel(new BorderLayout());
            messagePanel.setBackground(new Color(230, 230, 230));
            messagePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
            messageArea.setBackground(new Color(230, 230, 230));
            messagePanel.add(messageArea);

            // 时间标签
            JLabel timeLabel = new JLabel(timeStr, JLabel.LEFT);
            timeLabel.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_SMALL, FontLoader.STYLE_PLAIN));
            timeLabel.setForeground(Color.GRAY);

            bubbleContainer.add(messagePanel);
            bubbleContainer.add(timeLabel);

            // 组装AI消息面板
            JPanel aiMessagePanel = new JPanel(new BorderLayout(10, 0));
            aiMessagePanel.setOpaque(false);
            aiMessagePanel.add(aiAvatar, BorderLayout.WEST);
            aiMessagePanel.add(bubbleContainer, BorderLayout.CENTER);

            // 在左侧添加一些空间
            JPanel wrapperPanel = new JPanel(new BorderLayout());
            wrapperPanel.setOpaque(false);
            wrapperPanel.add(aiMessagePanel, BorderLayout.CENTER);
            wrapperPanel.add(Box.createHorizontalStrut(80), BorderLayout.EAST);

            bubblePanel.add(wrapperPanel, BorderLayout.CENTER);
        }

        // 添加边距
        JPanel paddingPanel = new JPanel(new BorderLayout());
        paddingPanel.setOpaque(false);
        paddingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        paddingPanel.add(bubblePanel);

        // 添加到聊天面板
        chatPanel.add(paddingPanel);
        chatPanel.revalidate();
        chatPanel.repaint();

        // 记录聊天历史
        chatHistory.add(sender + ": " + message);

        // 自动滚动到底部
        scrollToBottom();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    /**
     * 更新界面语言
     */
    public void updateLanguage() {
        // 更新快速选项按钮文本
        for (Component comp : quickOptionsPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                String text = button.getText();

                if (text.contains("Suggestions") || text.contains("建议")) {
                    button.setText("😀 " + LanguageUtil.getText("chat.suggestion"));
                } else if (text.contains("Holiday") || text.contains("假期")) {
                    button.setText("❤️ " + LanguageUtil.getText("chat.holiday"));
                } else if (text.contains("Future") || text.contains("未来") || text.contains("预测")) {
                    button.setText("📊 " + LanguageUtil.getText("chat.forecast"));
                }
            }
        }

        // 更新DeepSeek选择框文本
        Language currentLanguage = LanguageUtil.getCurrentLanguage();
        if (LanguageUtil.CHINESE.equals(currentLanguage)) {
            useDeepSeekCheckBox.setText("使用DeepSeek AI");
        } else {
            useDeepSeekCheckBox.setText("Use DeepSeek AI");
        }

        // 重绘整个面板
        revalidate();
        repaint();
    }
}
