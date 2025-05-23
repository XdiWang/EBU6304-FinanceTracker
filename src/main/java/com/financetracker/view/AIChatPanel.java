package com.financetracker.view;

import com.financetracker.model.*;
import com.financetracker.service.AIService;
import com.financetracker.service.DeepSeekAPIService;
import com.financetracker.service.TransactionService;
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
import java.util.stream.Collectors;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.BasicStroke;
import java.awt.event.ItemEvent;

/**
 * AI聊天面板 - 允许用户与AI助手聊天获取财务建议
 */
public class AIChatPanel extends JPanel {

    private User currentUser;
    private AIService aiService;
    private DeepSeekAPIService deepSeekService;
    private TransactionService transactionService;
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

    public AIChatPanel(User user, TransactionService transactionService) {
        this.currentUser = user;
        this.aiService = new AIService();
        this.deepSeekService = new DeepSeekAPIService();
        this.transactionService = transactionService;
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
                System.out.println("用户头像加载成功");
            } else {
                System.out.println("无法找到用户头像图片资源");
            }

            // 加载AI头像
            InputStream aiStream = getClass().getResourceAsStream("/resources/images/chat.png");
            if (aiStream != null) {
                aiAvatarImage = ImageIO.read(aiStream);
                System.out.println("AI头像加载成功");
            } else {
                System.out.println("无法找到AI头像图片资源");
                // 尝试替代路径
                aiStream = getClass().getResourceAsStream("/images/chat.png");
                if (aiStream != null) {
                    aiAvatarImage = ImageIO.read(aiStream);
                    System.out.println("通过替代路径加载AI头像成功");
                }
            }

            // 加载发送按钮图片
            InputStream sendStream = getClass().getResourceAsStream("/resources/images/send.png");
            if (sendStream != null) {
                sendButtonImage = ImageIO.read(sendStream);
            } else {
                System.out.println("无法找到发送按钮图片资源");
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
        // 增强聊天区域与输入区域的分隔
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)), // 底部添加细线
                BorderFactory.createEmptyBorder(0, 0, 5, 0) // 底部内边距
        ));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        // 禁用水平滚动条
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // 底部输入面板
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // 使用GridBagLayout创建输入控件面板
        JPanel controlsPanel = new JPanel(new GridBagLayout());
        controlsPanel.setBackground(Color.WHITE);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        GridBagConstraints gbc = new GridBagConstraints();

        // DeepSeek AI 选项设置
        useDeepSeekCheckBox = new JCheckBox("使用DeepSeek AI");
        useDeepSeekCheckBox.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_SMALL, FontLoader.STYLE_BOLD));
        useDeepSeekCheckBox.setSelected(true);
        useDeepSeekCheckBox.setForeground(new Color(30, 30, 30));
        useDeepSeekCheckBox.setBackground(Color.WHITE);
        useDeepSeekCheckBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)));

        // 添加选中状态变化监听器，提供更明显的视觉反馈
        useDeepSeekCheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                useDeepSeekCheckBox.setBackground(new Color(240, 248, 255)); // 轻微蓝色背景表示激活
                useDeepSeekCheckBox.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(100, 181, 246), 1),
                        BorderFactory.createEmptyBorder(2, 4, 2, 4)));
            } else {
                useDeepSeekCheckBox.setBackground(Color.WHITE);
                useDeepSeekCheckBox.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                        BorderFactory.createEmptyBorder(2, 4, 2, 4)));
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        controlsPanel.add(useDeepSeekCheckBox, gbc);

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

        // 添加输入框到GridBag布局
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        controlsPanel.add(inputFieldPanel, gbc);

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

        // 添加发送按钮到GridBag布局
        gbc.gridx = 2;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        controlsPanel.add(sendButtonPanel, gbc);

        // 快速选项按钮面板 - 改进样式
        quickOptionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        quickOptionsPanel.setBackground(new Color(248, 249, 250)); // 浅色背景增强区分度
        quickOptionsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)), // 顶部细线
                BorderFactory.createEmptyBorder(15, 20, 15, 20) // 内边距
        ));

        JButton suggestionsButton = createQuickOptionButton("建议");
        JButton holidayButton = createQuickOptionButton("假期规划");
        JButton forecastButton = createQuickOptionButton("支出预测");

        quickOptionsPanel.add(suggestionsButton);
        quickOptionsPanel.add(holidayButton);
        quickOptionsPanel.add(forecastButton);

        // 将输入组件添加到输入面板
        inputPanel.add(controlsPanel, BorderLayout.CENTER);
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
        titleBarPanel.setBackground(Color.WHITE);

        // 标题 - 改为居中并加粗
        JLabel titleLabel = new JLabel("AI-Empowered Personal Finance Tracker", JLabel.CENTER); // 确保居中
        titleLabel.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_BOLD));
        titleLabel.setForeground(Color.BLACK);
        // titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0)); //
        // 如果居中，则不需要特定边距

        titleBarPanel.add(titleLabel, BorderLayout.CENTER); // 使用CENTER实现居中

        return titleBarPanel;
    }

    private JButton createQuickOptionButton(String text) {
        // 创建带有圆角背景的按钮
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 更鲜明的背景色
                g2d.setColor(new Color(240, 242, 245));

                // 绘制圆角矩形
                int arc = 20;
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

                // 添加细微边框增强层次感
                g2d.setColor(new Color(220, 225, 235));
                g2d.setStroke(new BasicStroke(1.0f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        // 改进按钮样式
        button.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_SMALL, FontLoader.STYLE_PLAIN));
        button.setForeground(new Color(60, 64, 67)); // 更深的文字颜色提高可读性
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // 添加鼠标悬停效果
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(new Color(25, 103, 210)); // 悬停时文字变蓝
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(new Color(60, 64, 67)); // 恢复原色
            }
        });

        // 设置按钮文本
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
        // 不添加初始消息，让对话从用户开始
        // String welcomeMessage;
        // if (LanguageUtil.CHINESE.equals(LanguageUtil.getCurrentLanguage())) {
        // welcomeMessage = "欢迎使用AI助手！你可以向我询问有关财务管理、预算规划、投资建议等问题。";
        // } else {
        // welcomeMessage = "Welcome to AI Assistant! You can ask me questions about
        // financial management, budget planning, investment advice, etc.";
        // }
        // appendMessage("AI助手", welcomeMessage, false);
    }

    private String summarizeTransactionsForAI() {
        if (transactionService == null) {
            return ""; // Or some default message indicating no transaction data
        }
        List<Transaction> transactions = transactionService.getTransactions();
        if (transactions.isEmpty()) {
            return LanguageUtil.getText("aiChat.noTransactions");
        }

        // Basic summary: total income, total expenses, and last few transactions
        double totalIncome = transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();
        double totalExpenses = transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
                .mapToDouble(t -> Math.abs(t.getAmount())) // Expenses are negative
                .sum();

        String summary = String.format(LanguageUtil.getText("aiChat.transactionSummaryFormat"),
                totalIncome, totalExpenses, transactions.size());

        // Add last 3-5 transactions as examples
        int limit = Math.min(transactions.size(), 3);
        if (limit > 0) {
            summary += "\n" + LanguageUtil.getText("aiChat.recentTransactionsHeader");
            for (int i = 0; i < limit; i++) {
                Transaction tx = transactions.get(transactions.size() - 1 - i); // Get latest
                summary += String.format("\n- %s: %s %.2f (%s)",
                        tx.getDate().format(DateTimeFormatter.ISO_DATE),
                        tx.getDescription(),
                        tx.getAmount(), // Keep sign for AI to understand income/expense
                        tx.getCategory().getName());
            }
        }
        return summary;
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (message.isEmpty()) {
            return;
        }

        appendMessage(currentUser.getUsername(), message, true);
        inputField.setText("");

        // Prepare context for AI
        String transactionContext = summarizeTransactionsForAI();
        String fullPrompt = message;
        if (!transactionContext.isEmpty()) {
            fullPrompt = LanguageUtil.getText("aiChat.promptPrefixWithContext") + "\n" +
                    transactionContext + "\n\n" +
                    LanguageUtil.getText("aiChat.userQueryHeader") + "\n" + message;
        }

        // Prepare for AI response (streaming or direct)
        prepareEmptyAIMessagePanel();

        final String finalPrompt = fullPrompt; // For use in lambda

        executorService.submit(() -> {
            try {
                if (useDeepSeekCheckBox.isSelected()) {
                    // For DeepSeek, we'll use the streaming approach
                    deepSeekService.streamChat(finalPrompt, transactionService.getTransactions(),
                            this::updateStreamMessage, (String completeResponse) -> {
                                // Stream finished, finalize message or do cleanup
                                currentStreamedMessage = ""; // Reset for next message
                                scrollToBottom();
                            });
                } else {
                    // For basic AIService (non-streaming)
                    String aiResponse = aiService.getPersonalizedChatAdvice(finalPrompt,
                            transactionService.getTransactions());
                    SwingUtilities.invokeLater(() -> {
                        // If we were streaming, we'd append to currentAIMessageArea.
                        // Since it's a full response, we can replace the "typing..."
                        if (currentAIMessageArea != null) {
                            currentAIMessageArea.setText(aiResponse);
                            // Adjust panel size after setting text
                            chatPanel.revalidate();
                            chatPanel.repaint();
                        } else { // Fallback if streaming panel wasn't perfectly set up
                            appendMessage("AI", aiResponse, false);
                        }
                        scrollToBottom();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    String errorMsg = LanguageUtil.getText("aiChat.error.apiError") + ": " + e.getMessage();
                    if (currentAIMessageArea != null) {
                        currentAIMessageArea.setText(errorMsg);
                        currentAIMessageArea.setForeground(Color.RED);
                    } else {
                        appendMessage("AI", errorMsg, false);
                    }
                    scrollToBottom();
                });
            }
        });
    }

    private void prepareEmptyAIMessagePanel() {
        // Create a new panel for the AI's message (bubble + avatar)
        // This panel will contain the JTextArea that gets updated by the stream

        // Re-use bubble creation logic for consistency
        currentAIMessageArea = new JTextArea(LanguageUtil.getText("aiChat.typing")); // Initial "Typing..."
        currentAIMessageArea.setEditable(false);
        currentAIMessageArea.setLineWrap(true);
        currentAIMessageArea.setWrapStyleWord(true);
        currentAIMessageArea.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));
        currentAIMessageArea.setOpaque(false);
        currentAIMessageArea.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Create the bubble background for the AI's streaming message
        JPanel textBubble = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(240, 240, 240)); // Light gray bubble for AI
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        textBubble.setOpaque(false);
        textBubble.add(currentAIMessageArea, BorderLayout.CENTER);

        // Apply similar width constraints as other messages
        int maxWidth = 300;
        if (scrollPane != null && scrollPane.getViewport() != null) {
            int viewportWidth = scrollPane.getViewport().getWidth();
            if (viewportWidth > 0) {
                maxWidth = (int) (viewportWidth * 0.70); // AI can be a bit wider
                if (maxWidth < 200)
                    maxWidth = 200;
            }
        }
        textBubble.setMaximumSize(new Dimension(maxWidth, Integer.MAX_VALUE));
        // textBubble.setPreferredSize(new Dimension(maxWidth, 60)); // Avoid fixed
        // preferred height initially for typing

        JPanel aiAvatar = createAIAvatarPanel();

        // This is the main panel for one AI message row (avatar + bubble)
        JPanel aiMessageRowPanel = new JPanel(new GridBagLayout());
        aiMessageRowPanel.setOpaque(false);
        aiMessageRowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.anchor = GridBagConstraints.PAGE_START;

        // AI Avatar on the left
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        aiMessageRowPanel.add(aiAvatar, gbc);

        // AI Bubble on the right
        gbc.gridx = 1;
        gbc.weightx = 1.0; // Bubble takes remaining space
        gbc.fill = GridBagConstraints.HORIZONTAL;
        aiMessageRowPanel.add(textBubble, gbc);

        // Add a little vertical space before this new message panel
        chatPanel.add(Box.createVerticalStrut(10));
        chatPanel.add(aiMessageRowPanel); // Add the structured row panel
        currentAIMessagePanel = aiMessageRowPanel; // Store reference to the whole row panel

        chatPanel.revalidate();
        chatPanel.repaint();
        scrollToBottom();
    }

    private synchronized void updateStreamMessage(String textChunk) {
        SwingUtilities.invokeLater(() -> {
            if (currentAIMessageArea != null) {
                if (currentStreamedMessage.isEmpty() && textChunk.equals(LanguageUtil.getText("aiChat.typing"))) {
                    // If the first chunk is still "Typing...", don't append, wait for real content
                    return;
                }
                if (currentStreamedMessage.equals(LanguageUtil.getText("aiChat.typing"))
                        || currentAIMessageArea.getText().equals(LanguageUtil.getText("aiChat.typing"))) {
                    currentStreamedMessage = ""; // Clear "Typing..."
                    currentAIMessageArea.setText("");
                }
                currentStreamedMessage += textChunk;
                currentAIMessageArea.append(textChunk); // Append the new chunk

                // Dynamically adjust the size of the JTextArea and its container
                // This is a bit tricky with BoxLayout, might need to revalidate the parent
                currentAIMessageArea.getParent().revalidate(); // Revalidate textBubble
                currentAIMessagePanel.revalidate(); // Revalidate the whole message panel (avatar + bubble)
                chatPanel.revalidate();
                chatPanel.repaint();
                scrollToBottom(); // Keep scrolling as new text arrives
            }
        });
    }

    private void appendMessage(String sender, String message, boolean isUser) {
        LocalDateTime now = LocalDateTime.now();
        String timeStr = now.format(TIME_FORMATTER);

        JPanel messageRow = new JPanel(new GridBagLayout());
        messageRow.setOpaque(false);
        messageRow.setAlignmentX(Component.LEFT_ALIGNMENT); // Needed for BoxLayout.Y_AXIS parent (chatPanel)

        JPanel avatarPanel = isUser ? createUserAvatarPanel() : createAIAvatarPanel();
        // avatarPanel.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE)); // Set
        // in create methods
        // avatarPanel.setOpaque(false); // Set in create methods

        JPanel bubbleWithTimePanel = createBubbleWithTimePanel(message, timeStr, isUser);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.PAGE_START; // All items align to the top of their cell
        gbc.weighty = 0; // Do not stretch vertically; prefer compact height

        if (isUser) {
            // User messages: [Glue] [Bubble] [Avatar]
            // Glue pushes Bubble and Avatar to the right.
            gbc.gridx = 0;
            gbc.weightx = 1.0; // Glue takes up all extra horizontal space
            gbc.fill = GridBagConstraints.HORIZONTAL;
            messageRow.add(Box.createHorizontalStrut(0), gbc); // Strut acts as glue here

            gbc.gridx = 1;
            gbc.weightx = 0; // Bubble takes its preferred width, constrained by setMaximumSize
            gbc.fill = GridBagConstraints.NONE; // Do not fill horizontally beyond preferred size
            gbc.anchor = GridBagConstraints.PAGE_END; // Anchor bubble to the right of its cell (before avatar)
            messageRow.add(bubbleWithTimePanel, gbc);

            gbc.gridx = 2;
            gbc.weightx = 0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.PAGE_END; // Anchor avatar to the far right
            messageRow.add(avatarPanel, gbc);
        } else {
            // AI messages: [Avatar] [Bubble] [Glue]
            // Glue pushes Avatar and Bubble to the left.
            gbc.gridx = 0;
            gbc.weightx = 0; // Avatar is fixed size
            gbc.fill = GridBagConstraints.NONE;
            messageRow.add(avatarPanel, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0; // Bubble takes its preferred width, constrained by setMaximumSize
                             // If we want bubble to expand with weightx=1, fill=HORIZONTAL might be needed
            gbc.fill = GridBagConstraints.HORIZONTAL; // Let AI bubble take available space if message is long
            gbc.anchor = GridBagConstraints.PAGE_START; // Ensure bubble content aligns left within its cell
            messageRow.add(bubbleWithTimePanel, gbc);

            gbc.gridx = 2;
            gbc.weightx = 1.0; // Glue takes up all extra horizontal space
            gbc.fill = GridBagConstraints.HORIZONTAL;
            messageRow.add(Box.createHorizontalStrut(0), gbc); // Strut acts as glue
        }

        chatPanel.add(messageRow);
        chatPanel.add(Box.createVerticalStrut(5));
        chatPanel.revalidate();
        chatPanel.repaint();

        chatHistory.add(sender + ": " + message);
        scrollToBottom();
    }

    private JPanel createBubbleWithTimePanel(String message, String timeStr, boolean isUser) {
        JPanel bubblePanel = new JPanel(new BorderLayout(0, 3)); // Small gap for time
        bubblePanel.setOpaque(false);

        JTextArea messageArea = new JTextArea(message);
        messageArea.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setEditable(false);
        messageArea.setOpaque(false);
        messageArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 5, 12)); // Adjusted padding

        JLabel timeLabel = new JLabel(timeStr);
        timeLabel.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_SMALL, FontLoader.STYLE_PLAIN));
        timeLabel.setForeground(new Color(150, 150, 150)); // Slightly lighter gray for time

        JPanel bubbleBackground = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isUser) {
                    g2d.setColor(new Color(66, 133, 244, 220));
                } else {
                    g2d.setColor(new Color(240, 240, 240));
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        bubbleBackground.setOpaque(false);
        bubbleBackground.add(messageArea, BorderLayout.CENTER);

        bubblePanel.add(bubbleBackground, BorderLayout.CENTER);

        // Panel for time, to control its alignment within the bubble
        JPanel timePanel = new JPanel(new FlowLayout(isUser ? FlowLayout.RIGHT : FlowLayout.LEFT, 12, 0));
        timePanel.setOpaque(false);
        timePanel.add(timeLabel);
        bubblePanel.add(timePanel, BorderLayout.SOUTH);

        if (isUser) {
            messageArea.setForeground(Color.WHITE);
        } else {
            messageArea.setForeground(Color.BLACK);
        }

        // Width calculation for the bubblePanel
        int desiredMaxWidth;
        float widthPercentage = isUser ? 0.60f : 0.70f; // User bubbles slightly narrower

        if (scrollPane != null && scrollPane.getViewport() != null && scrollPane.getViewport().getWidth() > 0) {
            desiredMaxWidth = (int) (scrollPane.getViewport().getWidth() * widthPercentage);
        } else if (getParent() != null && getParent().getWidth() > 0) { // Fallback to parent width
            desiredMaxWidth = (int) (getParent().getWidth() * widthPercentage);
        } else {
            desiredMaxWidth = 350; // Absolute fallback
        }

        if (desiredMaxWidth < 150)
            desiredMaxWidth = 150; // Min width

        // Important: JTextArea needs a preferred size that allows wrapping.
        // We give it a constrained width (maxWidth) and let height be preferred.
        messageArea.setSize(new Dimension(desiredMaxWidth, Integer.MAX_VALUE)); // Hint for preferred size calculation
                                                                                // with line wrapping.

        // The bubblePanel's size should be primarily dictated by the messageArea's
        // wrapped content.
        // Set MaximumSize to control its upper bound when chatPanel (BoxLayout) tries
        // to give it space.
        bubblePanel.setMaximumSize(new Dimension(desiredMaxWidth, Integer.MAX_VALUE));

        // To ensure the bubble doesn't shrink too much if message is short,
        // one could set a minimum size, but typically preferred size of JTextArea
        // should handle this.
        // bubblePanel.setMinimumSize(new Dimension(50,
        // bubblePanel.getPreferredSize().height));

        // For BoxLayout X_AXIS in parent (messageRow), we need to set alignment
        // However, bubblePanel is added to messageRow using BoxLayout's default add
        // (which respects preferred size)
        // The messageRow itself uses createHorizontalGlue to manage alignment.
        return bubblePanel;
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
