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
import java.awt.geom.Ellipse2D;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

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

    // 添加头像图片
    private BufferedImage userAvatarImage;
    private BufferedImage aiAvatarImage;
    private BufferedImage sendButtonImage;
    private final int AVATAR_SIZE = 36; // 固定头像大小

    // 添加新的字段来支持流式输出
    private JPanel currentAIMessagePanel;
    private JTextArea currentAIMessageArea;
    private String currentStreamedMessage = "";

    public AIChatPanel(User user) {
        this.currentUser = user;
        this.aiService = new AIService();
        this.deepSeekService = new DeepSeekAPIService();
        this.executorService = Executors.newCachedThreadPool();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 加载头像图片
        loadAvatarImages();

        // 设置UI
        setupUI();

        // 确保DeepSeek复选框可见
        if (useDeepSeekCheckBox != null) {
            useDeepSeekCheckBox.setVisible(true);
            System.out.println("[Constructor] DeepSeek checkbox initialized: " + (useDeepSeekCheckBox != null));
        }

        // 添加初始消息
        addInitialMessages();
    }

    /**
     * 加载头像图片资源
     */
    private void loadAvatarImages() {
        try {
            // 加载用户头像
            InputStream userStream = getClass().getResourceAsStream("/resources/images/user.png");
            if (userStream != null) {
                userAvatarImage = ImageIO.read(userStream);
            }

            // 加载AI头像
            InputStream aiStream = getClass().getResourceAsStream("/resources/images/chat.png");
            if (aiStream != null) {
                aiAvatarImage = ImageIO.read(aiStream);
            }

            // 加载发送按钮图片
            InputStream sendStream = getClass().getResourceAsStream("/resources/images/send.png");
            if (sendStream != null) {
                sendButtonImage = ImageIO.read(sendStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        chatPanel.setBackground(Color.WHITE);

        // 为聊天区域添加滚动功能
        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // 底部输入面板
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // 自定义圆角输入框
        JPanel inputFieldPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(245, 245, 245));

                // 绘制圆角矩形
                int arc = 20; // 圆角大小
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        inputFieldPanel.setOpaque(false);

        // 输入框
        inputField = new JTextField();
        inputField.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));
        inputField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        inputField.setOpaque(false);

        inputFieldPanel.add(inputField, BorderLayout.CENTER);
        inputFieldPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // 底部工具面板，包含输入框和按钮
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setBackground(Color.WHITE);

        // 添加表情和上传按钮
        JPanel toolButtonsPanel = new JPanel(new BorderLayout());
        toolButtonsPanel.setBackground(Color.WHITE);
        toolButtonsPanel.setPreferredSize(new Dimension(250, 40)); // 确保面板有足够空间

        // 创建DeepSeek选择框的专用面板
        JPanel deepSeekPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deepSeekPanel.setBackground(Color.WHITE);
        deepSeekPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        deepSeekPanel.setPreferredSize(new Dimension(150, 30));

        // 添加DeepSeek API切换选项 - 使用标准JCheckBox
        useDeepSeekCheckBox = new JCheckBox("使用DeepSeek AI");
        useDeepSeekCheckBox.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_SMALL, FontLoader.STYLE_BOLD));
        useDeepSeekCheckBox.setSelected(true);
        useDeepSeekCheckBox.setForeground(new Color(30, 30, 30));
        useDeepSeekCheckBox.setBackground(Color.WHITE);
        // 增加边框和边距使其更加明显
        useDeepSeekCheckBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)));
        deepSeekPanel.add(useDeepSeekCheckBox);

        // 添加DeepSeek面板到工具按钮面板
        toolButtonsPanel.add(deepSeekPanel, BorderLayout.WEST);

        // 表情符号和附件按钮放在东侧
        JPanel extraButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        extraButtonsPanel.setBackground(Color.WHITE);

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

        extraButtonsPanel.add(emojiButton);
        extraButtonsPanel.add(uploadButton);

        toolButtonsPanel.add(extraButtonsPanel, BorderLayout.EAST);

        // 发送按钮面板 - 圆角背景
        JPanel sendButtonPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(240, 240, 240));

                // 绘制圆角矩形
                int arc = 20; // 圆角大小
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        sendButtonPanel.setOpaque(false);
        sendButtonPanel.setPreferredSize(new Dimension(40, 40));

        // 发送按钮 - 使用图片
        sendButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (sendButtonImage != null) {
                    int imgSize = Math.min(getWidth(), getHeight()) - 12;
                    g.drawImage(sendButtonImage,
                            (getWidth() - imgSize) / 2,
                            (getHeight() - imgSize) / 2,
                            imgSize, imgSize, this);
                }
            }
        };
        sendButton.setOpaque(false);
        sendButton.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        sendButton.setFocusPainted(false);
        sendButton.setContentAreaFilled(false);

        sendButtonPanel.add(sendButton, BorderLayout.CENTER);

        // 组装底部面板
        bottomPanel.add(toolButtonsPanel, BorderLayout.WEST);
        bottomPanel.add(inputFieldPanel, BorderLayout.CENTER);
        bottomPanel.add(sendButtonPanel, BorderLayout.EAST);

        // 快速选项按钮面板
        quickOptionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        quickOptionsPanel.setBackground(Color.WHITE);
        quickOptionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton suggestionsButton = createQuickOptionButton("建议");
        JButton holidayButton = createQuickOptionButton("假期规划");
        JButton forecastButton = createQuickOptionButton("支出预测");

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
        titleBarPanel.setBackground(Color.WHITE); // 从灰色改为白色

        // 标题 - 加粗显示
        JLabel titleLabel = new JLabel(LanguageUtil.getText("main.title"), JLabel.CENTER);
        titleLabel.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_BOLD)); // 设置为粗体
        titleLabel.setForeground(Color.BLACK);

        titleBarPanel.add(titleLabel, BorderLayout.CENTER);

        return titleBarPanel;
    }

    private JButton createQuickOptionButton(String text) {
        // 创建带有圆角背景的按钮
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(245, 245, 245));

                // 绘制圆角矩形
                int arc = 20; // 圆角大小
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_SMALL, FontLoader.STYLE_PLAIN));
        button.setForeground(Color.BLACK);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // 去掉表情符号前缀，直接使用简单文本
        String plainText = text;
        if (text.contains("suggestion") || text.contains("建议")) {
            plainText = LanguageUtil.CHINESE.equals(LanguageUtil.getCurrentLanguage()) ? "建议" : "Suggestions";
        } else if (text.contains("holiday") || text.contains("假期")) {
            plainText = LanguageUtil.CHINESE.equals(LanguageUtil.getCurrentLanguage()) ? "假期规划" : "Holiday planning";
        } else if (text.contains("forecast") || text.contains("预测")) {
            plainText = LanguageUtil.CHINESE.equals(LanguageUtil.getCurrentLanguage()) ? "支出预测" : "Future spending";
        }
        button.setText(plainText);

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

        // 创建AI头像
        JPanel aiAvatar = createAIAvatarPanel();
        aiAvatar.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        aiAvatar.setMaximumSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
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

        // AI头像（使用图片）
        JPanel aiAvatar = createAIAvatarPanel();
        aiAvatar.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        aiAvatar.setMaximumSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        aiAvatar.setOpaque(false);

        // 消息内容（开始为空）
        currentAIMessageArea = new JTextArea("");
        currentAIMessageArea.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));
        currentAIMessageArea.setLineWrap(true);
        currentAIMessageArea.setWrapStyleWord(true);
        currentAIMessageArea.setEditable(false);
        currentAIMessageArea.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        currentAIMessageArea.setOpaque(false);

        // 消息气泡容器
        JPanel bubbleContainer = new JPanel();
        bubbleContainer.setLayout(new BoxLayout(bubbleContainer, BoxLayout.Y_AXIS));
        bubbleContainer.setOpaque(false);

        // 时间标签 - 移到顶部
        JLabel timeLabel = new JLabel(timeStr, JLabel.LEFT);
        timeLabel.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_SMALL, FontLoader.STYLE_PLAIN));
        timeLabel.setForeground(Color.GRAY);

        // 消息内容面板 - 圆角灰色气泡
        JPanel messagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(230, 230, 230));

                // 绘制圆角矩形
                int arc = 20; // 圆角大小
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        messagePanel.setOpaque(false);
        messagePanel.setLayout(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        messagePanel.add(currentAIMessageArea);

        // 先添加时间，再添加消息
        bubbleContainer.add(timeLabel);
        bubbleContainer.add(Box.createVerticalStrut(5)); // 添加间距
        bubbleContainer.add(messagePanel);

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

        // 根据消息发送者设置不同的样式
        if (isUser) {
            // 用户消息 - 靠右，蓝色背景
            bubblePanel.setLayout(new BorderLayout(5, 5));

            // 用户头像（使用图片）
            JPanel userAvatar = createUserAvatarPanel();
            userAvatar.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
            userAvatar.setMaximumSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
            userAvatar.setOpaque(false);

            // 消息气泡容器
            JPanel bubbleContainer = new JPanel();
            bubbleContainer.setLayout(new BoxLayout(bubbleContainer, BoxLayout.Y_AXIS));
            bubbleContainer.setOpaque(false);

            // 时间标签 - 移到顶部
            JLabel timeLabel = new JLabel(timeStr, JLabel.RIGHT);
            timeLabel.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_SMALL, FontLoader.STYLE_PLAIN));
            timeLabel.setForeground(Color.GRAY);

            // 消息内容面板 - 圆角蓝色气泡 - 进一步淡化颜色
            JPanel messagePanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(66, 133, 244, 150)); // 降低透明度到150

                    // 绘制圆角矩形
                    int arc = 20; // 圆角大小
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };
            messagePanel.setOpaque(false);
            messagePanel.setLayout(new BorderLayout());
            messagePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

            messageArea.setOpaque(false);
            messageArea.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
            messageArea.setForeground(Color.BLACK);
            messagePanel.add(messageArea);

            // 先添加时间，再添加消息
            bubbleContainer.add(timeLabel);
            bubbleContainer.add(Box.createVerticalStrut(5)); // 添加间距
            bubbleContainer.add(messagePanel);

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

            // AI头像（使用图片）
            JPanel aiAvatar = createAIAvatarPanel();
            aiAvatar.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
            aiAvatar.setMaximumSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
            aiAvatar.setOpaque(false);

            // 消息气泡容器
            JPanel bubbleContainer = new JPanel();
            bubbleContainer.setLayout(new BoxLayout(bubbleContainer, BoxLayout.Y_AXIS));
            bubbleContainer.setOpaque(false);

            // 时间标签 - 移到顶部
            JLabel timeLabel = new JLabel(timeStr, JLabel.LEFT);
            timeLabel.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_SMALL, FontLoader.STYLE_PLAIN));
            timeLabel.setForeground(Color.GRAY);

            // 消息内容面板 - 圆角灰色气泡
            JPanel messagePanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(230, 230, 230));

                    // 绘制圆角矩形
                    int arc = 20; // 圆角大小
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };
            messagePanel.setOpaque(false);
            messagePanel.setLayout(new BorderLayout());
            messagePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

            messageArea.setOpaque(false);
            messageArea.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
            messageArea.setForeground(Color.BLACK);
            messagePanel.add(messageArea);

            // 先添加时间，再添加消息
            bubbleContainer.add(timeLabel);
            bubbleContainer.add(Box.createVerticalStrut(5)); // 添加间距
            bubbleContainer.add(messagePanel);

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
                    button.setText(
                            LanguageUtil.CHINESE.equals(LanguageUtil.getCurrentLanguage()) ? "建议" : "Suggestions");
                } else if (text.contains("Holiday") || text.contains("假期")) {
                    button.setText(LanguageUtil.CHINESE.equals(LanguageUtil.getCurrentLanguage()) ? "假期规划"
                            : "Holiday planning");
                } else if (text.contains("Future") || text.contains("支出") || text.contains("预测")) {
                    button.setText(LanguageUtil.CHINESE.equals(LanguageUtil.getCurrentLanguage()) ? "支出预测"
                            : "Future spending");
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

        // 确保DeepSeek复选框可见
        useDeepSeekCheckBox.setVisible(true);
        useDeepSeekCheckBox.revalidate();

        // 打印调试信息，确认复选框状态
        System.out.println("DeepSeek checkbox visible: " + useDeepSeekCheckBox.isVisible());
        System.out.println("DeepSeek checkbox text: " + useDeepSeekCheckBox.getText());
        System.out.println("DeepSeek checkbox enabled: " + useDeepSeekCheckBox.isEnabled());

        // 重绘整个面板
        revalidate();
        repaint();
    }

    /**
     * 创建AI头像面板
     */
    private JPanel createAIAvatarPanel() {
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 绘制AI头像图片
                if (aiAvatarImage != null) {
                    int diameter = Math.min(getWidth(), getHeight());
                    // 创建圆形裁剪区域
                    g2d.setClip(new Ellipse2D.Float(0, 0, diameter, diameter));
                    // 绘制图片，缩放到合适大小
                    g2d.drawImage(aiAvatarImage, 0, 0, diameter, diameter, null);
                } else {
                    // 如果图片加载失败，显示文字
                    g2d.setColor(new Color(200, 200, 200));
                    g2d.fillOval(0, 0, getWidth(), getHeight());
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(avatarFont);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth("AI");
                    int textHeight = fm.getHeight();
                    g2d.drawString("AI", (getWidth() - textWidth) / 2, (getHeight() + textHeight / 3) / 2);
                }

                g2d.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        avatarPanel.setOpaque(false);
        return avatarPanel;
    }

    /**
     * 创建用户头像面板
     */
    private JPanel createUserAvatarPanel() {
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 绘制用户头像图片
                if (userAvatarImage != null) {
                    int diameter = Math.min(getWidth(), getHeight());
                    // 创建圆形裁剪区域
                    g2d.setClip(new Ellipse2D.Float(0, 0, diameter, diameter));
                    // 绘制图片，缩放到合适大小
                    g2d.drawImage(userAvatarImage, 0, 0, diameter, diameter, null);
                } else {
                    // 如果图片加载失败，显示文字
                    g2d.setColor(new Color(200, 200, 200));
                    g2d.fillOval(0, 0, getWidth(), getHeight());
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(avatarFont);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth("U");
                    int textHeight = fm.getHeight();
                    g2d.drawString("U", (getWidth() - textWidth) / 2, (getHeight() + textHeight / 3) / 2);
                }

                g2d.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        avatarPanel.setOpaque(false);
        return avatarPanel;
    }
}
