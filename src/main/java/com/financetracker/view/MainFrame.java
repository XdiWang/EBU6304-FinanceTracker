// MainFrame.java
package com.financetracker.view;

import com.financetracker.model.User;
import com.financetracker.util.LanguageUtil;
import com.financetracker.util.FontLoader;
import com.financetracker.service.TransactionService;
import com.financetracker.model.Account;
import com.financetracker.model.Category;
import com.financetracker.model.Transaction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.ButtonGroup;
import java.awt.RenderingHints;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import java.io.InputStream;
// import java.io.FileNotFoundException; // Not strictly needed if using getResourceAsStream properly

public class MainFrame extends JFrame {

    private User currentUser;
    private TransactionService transactionService;
    private JPanel contentPanel;
    private DashboardPanel dashboardPanel;
    private OverviewPanel overviewPanel;
    private AccountPanel accountPanel; // 确保 accountPanel 被正确初始化
    private AIChatPanel aiChatPanel;

    private JMenuItem importMenuItem;
    private JMenuItem exportMenuItem;
    private JMenuItem logoutMenuItem;
    private JMenuItem exitMenuItem;
    private JRadioButtonMenuItem chineseMenuItem;
    private JRadioButtonMenuItem englishMenuItem;

    private JMenuBar menuBar;
    private JToolBar toolBar;
    private JMenu fileMenu;
    private JMenu viewMenu;
    private JMenu languageMenu;
    private JMenu helpMenu;

    private JToggleButton dashboardButton;
    private JToggleButton overviewButton;
    private JToggleButton accountButton;
    private JToggleButton aiChatButton;

    private JLabel titleLabel;

    private static final Logger LOGGER = Logger.getLogger(MainFrame.class.getName());
    private static final DateTimeFormatter CSV_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter PDF_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public MainFrame(User user) {
        this.currentUser = user;
        this.transactionService = new TransactionService();

        if (user.getPreferredLanguage() != null) {
            LanguageUtil.setCurrentLanguage(user.getPreferredLanguage());
        } else {
            LanguageUtil.setCurrentLanguage(LanguageUtil.CHINESE);
        }
        System.out.println("当前语言: " + (LanguageUtil.getCurrentLanguage().equals(LanguageUtil.CHINESE) ? "中文" : "英文"));

        initComponents(); // 初始化 accountPanel 等
        setupUI();
        updateLanguage();

        System.out.println("文件菜单文本: " + fileMenu.getText());
        System.out.println("视图菜单文本: " + viewMenu.getText());
        System.out.println("语言菜单文本: " + languageMenu.getText());

        if (fileMenu.getText().equals("menu.file") || viewMenu.getText().equals("menu.view")) {
            System.out.println("检测到菜单未能正确翻译，强制重新加载...");
            if (LanguageUtil.getCurrentLanguage().equals(LanguageUtil.CHINESE)) {
                fileMenu.setText("文件");
                viewMenu.setText("视图");
                languageMenu.setText("语言");
                helpMenu.setText("帮助");
            } else {
                fileMenu.setText("File");
                viewMenu.setText("View");
                languageMenu.setText("Language");
                helpMenu.setText("Help");
            }
            SwingUtilities.updateComponentTreeUI(this);
        }
    }

    private void initComponents() {
        dashboardPanel = new DashboardPanel(currentUser, transactionService);
        overviewPanel = new OverviewPanel(currentUser, transactionService);
        accountPanel = new AccountPanel(currentUser, transactionService); // 确保在这里初始化
        aiChatPanel = new AIChatPanel(currentUser, transactionService);
    }

    private void setupUI() {
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setBackground(Color.WHITE);

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(Color.WHITE);
        contentWrapper.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JPanel titleBarPanel = createTitleBarPanel();
        menuBar = createMenuBar();
        toolBar = createToolBar();

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titleBarPanel, BorderLayout.NORTH);
        topPanel.add(menuBar, BorderLayout.CENTER);
        topPanel.add(toolBar, BorderLayout.SOUTH);

        contentWrapper.add(topPanel, BorderLayout.NORTH);

        contentPanel = new JPanel(new CardLayout());
        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(overviewPanel, "overview");
        contentPanel.add(accountPanel, "account");
        contentPanel.add(aiChatPanel, "aichat");

        contentWrapper.add(contentPanel, BorderLayout.CENTER);
        setContentPane(contentWrapper);

        FontLoader.applyFontToComponentTree(contentWrapper, FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN);
        showPanel("dashboard");
        enableFrameDragging();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        Font menuFont = FontLoader.getFont(14, Font.PLAIN);
        boolean isEnglish = LanguageUtil.getCurrentLanguage().equals(LanguageUtil.ENGLISH);

        String fileText = LanguageUtil.getText("menu.file");
        if (isEnglish) fileText = fileText.replace(".", " ");
        fileMenu = new JMenu(fileText);
        fileMenu.setFont(menuFont);
        fileMenu.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        importMenuItem = new JMenuItem(LanguageUtil.getText("file.import"));
        importMenuItem.setFont(menuFont);
        importMenuItem.addActionListener(e -> importData());
        exportMenuItem = new JMenuItem(LanguageUtil.getText("file.export"));
        exportMenuItem.setFont(menuFont);
        exportMenuItem.addActionListener(e -> exportData());
        logoutMenuItem = new JMenuItem(LanguageUtil.getText("menu.logout"));
        logoutMenuItem.setFont(menuFont);
        logoutMenuItem.addActionListener(e -> logout());
        exitMenuItem = new JMenuItem(LanguageUtil.getText("menu.exit"));
        exitMenuItem.setFont(menuFont);
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(importMenuItem);
        fileMenu.add(exportMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(logoutMenuItem);
        fileMenu.add(exitMenuItem);

        String viewText = LanguageUtil.getText("menu.view");
        if (isEnglish) viewText = viewText.replace(".", " ");
        viewMenu = new JMenu(viewText);
        viewMenu.setFont(menuFont);
        viewMenu.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        JMenuItem dashboardMenuItem = new JMenuItem(LanguageUtil.getText("main.dashboard"));
        dashboardMenuItem.setFont(menuFont);
        dashboardMenuItem.addActionListener(e -> showPanel("dashboard"));
        JMenuItem overviewMenuItem = new JMenuItem(LanguageUtil.getText("main.overview"));
        overviewMenuItem.setFont(menuFont);
        overviewMenuItem.addActionListener(e -> showPanel("overview"));
        JMenuItem accountMenuItem = new JMenuItem(LanguageUtil.getText("main.account"));
        accountMenuItem.setFont(menuFont);
        accountMenuItem.addActionListener(e -> showPanel("account"));
        JMenuItem aiChatMenuItem = new JMenuItem(LanguageUtil.getText("main.ai_chat"));
        aiChatMenuItem.setFont(menuFont);
        aiChatMenuItem.addActionListener(e -> showPanel("aichat"));
        viewMenu.add(dashboardMenuItem);
        viewMenu.add(overviewMenuItem);
        viewMenu.add(accountMenuItem);
        viewMenu.add(aiChatMenuItem);

        String languageText = LanguageUtil.getText("menu.language");
        if (isEnglish) languageText = languageText.replace(".", " ");
        languageMenu = new JMenu(languageText);
        languageMenu.setFont(menuFont);
        languageMenu.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        ButtonGroup languageGroup = new ButtonGroup();
        englishMenuItem = new JRadioButtonMenuItem("English");
        englishMenuItem.setFont(menuFont);
        if (LanguageUtil.getCurrentLanguage().equals(LanguageUtil.ENGLISH)) englishMenuItem.setSelected(true);
        englishMenuItem.addActionListener(e -> {
            LanguageUtil.setCurrentLanguage(LanguageUtil.ENGLISH);
            updateLanguage();
        });
        chineseMenuItem = new JRadioButtonMenuItem("中文");
        chineseMenuItem.setFont(menuFont);
        if (LanguageUtil.getCurrentLanguage().equals(LanguageUtil.CHINESE)) chineseMenuItem.setSelected(true);
        chineseMenuItem.addActionListener(e -> {
            LanguageUtil.setCurrentLanguage(LanguageUtil.CHINESE);
            updateLanguage();
        });
        languageGroup.add(englishMenuItem);
        languageGroup.add(chineseMenuItem);
        languageMenu.add(englishMenuItem);
        languageMenu.add(chineseMenuItem);

        String helpText = LanguageUtil.getText("menu.help");
        if (isEnglish) helpText = helpText.replace(".", " ");
        helpMenu = new JMenu(helpText);
        helpMenu.setFont(menuFont);
        helpMenu.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        JMenuItem aboutMenuItem = new JMenuItem(LanguageUtil.getText("menu.about"));
        aboutMenuItem.setFont(menuFont);
        aboutMenuItem.addActionListener(e -> showAboutDialog());
        JMenuItem helpContentsMenuItem = new JMenuItem(LanguageUtil.getText("menu.help_item")); // Corrected key
        helpContentsMenuItem.setFont(menuFont);
        helpContentsMenuItem.addActionListener(e -> showHelpDialog());
        helpMenu.add(aboutMenuItem);
        helpMenu.add(helpContentsMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(languageMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);
        return menuBar;
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(Color.WHITE);
        toolBar.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        Font buttonFont = FontLoader.getFont(14, Font.PLAIN);
        dashboardButton = new JToggleButton(LanguageUtil.getText("main.dashboard"));
        overviewButton = new JToggleButton(LanguageUtil.getText("main.overview"));
        accountButton = new JToggleButton(LanguageUtil.getText("main.account"));
        aiChatButton = new JToggleButton(LanguageUtil.getText("main.ai_chat"));
        ButtonGroup navButtonGroup = new ButtonGroup();
        navButtonGroup.add(dashboardButton);
        navButtonGroup.add(overviewButton);
        navButtonGroup.add(accountButton);
        navButtonGroup.add(aiChatButton);
        Color accentColor = new Color(0, 122, 255);
        JToggleButton[] buttons = {dashboardButton, overviewButton, accountButton, aiChatButton};
        for (JToggleButton button : buttons) {
            button.setFont(buttonFont);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setBackground(Color.WHITE);
            button.setForeground(new Color(60, 60, 60));
            button.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!button.isSelected()) button.setForeground(accentColor);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    if (!button.isSelected()) button.setForeground(new Color(60, 60, 60));
                }
            });
            button.setUI(new BasicToggleButtonUI() {
                @Override
                public void paint(Graphics g, JComponent c) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    AbstractButton button = (AbstractButton) c;
                    ButtonModel model = button.getModel();
                    FontMetrics fm = g2d.getFontMetrics();
                    if (model.isSelected()) g2d.setColor(accentColor);
                    else if (model.isRollover()) g2d.setColor(accentColor);
                    else g2d.setColor(new Color(60, 60, 60));
                    String text = button.getText();
                    int textX = (c.getWidth() - fm.stringWidth(text)) / 2;
                    int textY = (c.getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2d.drawString(text, textX, textY);
                    if (model.isSelected()) {
                        int lineHeight = 2;
                        int lineWidth = fm.stringWidth(text);
                        int lineX = textX;
                        int lineY = c.getHeight() - lineHeight - 2;
                        g2d.setColor(accentColor);
                        g2d.fillRect(lineX, lineY, lineWidth, lineHeight);
                    }
                    g2d.dispose();
                }
            });
        }
        toolBar.addSeparator(new Dimension(10, 0));
        toolBar.add(dashboardButton);
        toolBar.addSeparator(new Dimension(5, 0));
        toolBar.add(overviewButton);
        toolBar.addSeparator(new Dimension(5, 0));
        toolBar.add(accountButton);
        toolBar.addSeparator(new Dimension(5, 0));
        toolBar.add(aiChatButton);
        toolBar.addSeparator(new Dimension(10, 0));
        dashboardButton.addActionListener(e -> showPanel("dashboard"));
        overviewButton.addActionListener(e -> showPanel("overview"));
        accountButton.addActionListener(e -> showPanel("account"));
        aiChatButton.addActionListener(e -> showPanel("aichat"));
        return toolBar;
    }

    public void showPanel(String panelName) {
        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        cardLayout.show(contentPanel, panelName);
        dashboardButton.setSelected(false);
        overviewButton.setSelected(false);
        accountButton.setSelected(false);
        aiChatButton.setSelected(false);
        String panelTitleKey = "";
        switch (panelName) {
            case "dashboard":
                dashboardButton.setSelected(true);
                panelTitleKey = "main.dashboard";
                break;
            case "overview":
                overviewButton.setSelected(true);
                panelTitleKey = "main.overview";
                break;
            case "account":
                accountButton.setSelected(true);
                panelTitleKey = "main.account";
                break;
            case "aichat":
                aiChatButton.setSelected(true);
                panelTitleKey = "main.ai_chat";
                break;
        }
        String currentPanelTitle = LanguageUtil.getText(panelTitleKey);
        setTitle(currentPanelTitle + " - " + LanguageUtil.getText("main.title"));
        if (titleLabel != null) {
            titleLabel.setText(currentPanelTitle);
            titleLabel.setFont(FontLoader.getFont(16, Font.BOLD)); // Consistent bold title
        }
        // Update viewMenu text based on current language
        String viewMenuText = LanguageUtil.getText("menu.view");
        if (LanguageUtil.getCurrentLanguage().equals(LanguageUtil.ENGLISH)) {
            viewMenu.setText(viewMenuText.replace(".", " "));
        } else {
            viewMenu.setText(viewMenuText);
        }

        // If the specific panel has an onActivate method, call it
        // This is useful for panels that need to refresh data when they become visible
        try {
            Object panelInstance = null;
            if ("dashboard".equals(panelName) && dashboardPanel != null) panelInstance = dashboardPanel;
            else if ("overview".equals(panelName) && overviewPanel != null) panelInstance = overviewPanel;
            else if ("account".equals(panelName) && accountPanel != null) panelInstance = accountPanel;
            else if ("aichat".equals(panelName) && aiChatPanel != null) panelInstance = aiChatPanel;

            if (panelInstance != null) {
                // Example: dashboardPanel.onActivate(); - if such method exists
                // For now, we assume refresh is handled by property change listeners or initial load
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error calling onActivate for panel " + panelName, e);
        }
    }

    private void updateLanguage() {
        setTitle(LanguageUtil.getText("main.title"));
        if (titleLabel != null) {
            titleLabel.setText(LanguageUtil.getText("main.title")); // Default title for title bar
            titleLabel.setFont(FontLoader.getFont(14, Font.PLAIN));
        }
        boolean isEnglish = LanguageUtil.getCurrentLanguage().equals(LanguageUtil.ENGLISH);
        String fileMenuText = LanguageUtil.getText("menu.file");
        String viewMenuText = LanguageUtil.getText("menu.view");
        String languageMenuText = LanguageUtil.getText("menu.language");
        String helpMenuText = LanguageUtil.getText("menu.help");
        if (isEnglish) {
            fileMenu.setText(fileMenuText.replace(".", " "));
            viewMenu.setText(viewMenuText.replace(".", " "));
            languageMenu.setText(languageMenuText.replace(".", " "));
            helpMenu.setText(helpMenuText.replace(".", " "));
        } else {
            fileMenu.setText(fileMenuText);
            viewMenu.setText(viewMenuText);
            languageMenu.setText(languageMenuText);
            helpMenu.setText(helpMenuText);
        }
        menuBar.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        fileMenu.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        viewMenu.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        languageMenu.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        helpMenu.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        importMenuItem.setText(LanguageUtil.getText("file.import"));
        exportMenuItem.setText(LanguageUtil.getText("file.export"));
        logoutMenuItem.setText(LanguageUtil.getText("menu.logout"));
        exitMenuItem.setText(LanguageUtil.getText("menu.exit"));
        chineseMenuItem.setText("中文");
        englishMenuItem.setText("English");
        dashboardButton.setText(LanguageUtil.getText("main.dashboard"));
        overviewButton.setText(LanguageUtil.getText("main.overview"));
        accountButton.setText(LanguageUtil.getText("main.account"));
        aiChatButton.setText(LanguageUtil.getText("main.ai_chat"));

        // Update help menu items
        if (helpMenu.getItemCount() > 0 && helpMenu.getItem(0) != null) { // Assuming "About" is first
            helpMenu.getItem(0).setText(LanguageUtil.getText("menu.about"));
        }
        if (helpMenu.getItemCount() > 1 && helpMenu.getItem(1) != null) { // Assuming "Help Contents" is second
            helpMenu.getItem(1).setText(LanguageUtil.getText("menu.help_item"));
        }


        // Update panel specific languages
        if (dashboardPanel != null) dashboardPanel.propertyChange(new PropertyChangeEvent(this, "userSettingsChanged", null, null)); // Trigger refresh
        if (overviewPanel != null) overviewPanel.propertyChange(new PropertyChangeEvent(this, "userSettingsChanged", null, null));
        if (accountPanel != null) accountPanel.propertyChange(new PropertyChangeEvent(this, "userSettingsChanged", null, null));
        if (aiChatPanel != null) aiChatPanel.updateLanguage(); // AIChatPanel has a direct updateLanguage method

        // Determine current panel and refresh its title
        String currentPanelName = "";
        Component[] components = contentPanel.getComponents();
        for (Component component : components) {
            if (component.isVisible()) {
                if (component == dashboardPanel) currentPanelName = "dashboard";
                else if (component == overviewPanel) currentPanelName = "overview";
                else if (component == accountPanel) currentPanelName = "account";
                else if (component == aiChatPanel) currentPanelName = "aichat";
                break;
            }
        }
        if (!currentPanelName.isEmpty()) {
            showPanel(currentPanelName); // This will also update the titleLabel
        }
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void importData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(LanguageUtil.getText("file.import"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(java.io.File f) {
                return f.getName().toLowerCase().endsWith(".csv") || f.isDirectory();
            }
            public String getDescription() {
                return LanguageUtil.getText("file.csv_files") + " (*.csv)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();

            Account[] availableAccounts;
            if (accountPanel != null && accountPanel.getAccountComboBoxModel() != null && accountPanel.getAccountComboBoxModel().getSize() > 0) {
                int size = accountPanel.getAccountComboBoxModel().getSize();
                availableAccounts = new Account[size];
                for (int i = 0; i < size; i++) {
                    availableAccounts[i] = accountPanel.getAccountComboBoxModel().getElementAt(i);
                }
                if (size == 0) { // Handle case where model is empty but not null
                    LOGGER.warning("AccountPanel ComboBox is empty. Using default list for CSV import.");
                    availableAccounts = new Account[]{
                            new Account("银行卡 (导入)", Account.AccountType.BANK),
                            new Account("支付宝 (导入)", Account.AccountType.ALIPAY),
                            new Account("微信 (导入)", Account.AccountType.WECHAT_PAY)
                    };
                }
            } else {
                LOGGER.warning("AccountPanel not ready or no accounts available for CSV import selection. Using default list.");
                availableAccounts = new Account[]{
                        new Account("银行卡 (导入)", Account.AccountType.BANK),
                        new Account("支付宝 (导入)", Account.AccountType.ALIPAY),
                        new Account("微信 (导入)", Account.AccountType.WECHAT_PAY)
                };
            }

            Account selectedAccountForImport = (Account) JOptionPane.showInputDialog(
                    this,
                    "选择要将导入的交易关联到的账户:",
                    "分配CSV导入账户",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    availableAccounts,
                    availableAccounts.length > 0 ? availableAccounts[0] : null);

            if (selectedAccountForImport == null) {
                JOptionPane.showMessageDialog(this, "CSV导入已取消：未选择账户。", "导入已取消", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Optional: Clear existing transactions from the selected account before import
            // selectedAccountForImport.clearTransactions();

            List<Transaction> importedTransactions = new ArrayList<>();
            List<String> errorMessages = new ArrayList<>();
            Map<String, Category> categoryCache = new HashMap<>();

            try (BufferedReader br = new BufferedReader(new FileReader(selectedFile, StandardCharsets.UTF_8))) {
                String line;
                boolean isFirstLine = true;
                int lineNumber = 0;

                while ((line = br.readLine()) != null) {
                    lineNumber++;
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }
                    if (line.trim().isEmpty()) continue;

                    String[] values = line.split(",");
                    if (values.length < 5) {
                        errorMessages.add("第 " + lineNumber + " 行: 列数不足 (期望 5, 实际 " + values.length + "): \"" + line + "\"");
                        continue;
                    }

                    try {
                        String dateStr = values[0].trim();
                        String typeStr = values[1].trim();
                        String descriptionStr = values[2].trim();
                        String amountStr = values[3].trim().replace("¥", "").replace("￥", "").trim();
                        String categoryStr = values[4].trim();

                        LocalDate date = LocalDate.parse(dateStr, CSV_DATE_FORMATTER);
                        double amount = Double.parseDouble(amountStr);
                        double signedAmount;
                        Transaction.TransactionType type;

                        if (typeStr.equalsIgnoreCase("Income") || typeStr.equalsIgnoreCase("收入")) {
                            type = Transaction.TransactionType.INCOME;
                            signedAmount = Math.abs(amount);
                        } else if (typeStr.equalsIgnoreCase("Expense") || typeStr.equalsIgnoreCase("支出")) {
                            type = Transaction.TransactionType.EXPENSE;
                            signedAmount = -Math.abs(amount);
                        } else {
                            errorMessages.add("第 " + lineNumber + " 行: 无效的交易类型 '" + typeStr + "'");
                            continue;
                        }

                        Category category = categoryCache.computeIfAbsent(categoryStr, name -> {
                            if (name.equalsIgnoreCase(Category.FOOD.getName())) return Category.FOOD;
                            if (name.equalsIgnoreCase(Category.TRANSPORT.getName())) return Category.TRANSPORT;
                            if (name.equalsIgnoreCase(Category.SHOPPING.getName())) return Category.SHOPPING;
                            if (name.equalsIgnoreCase(Category.ENTERTAINMENT.getName())) return Category.ENTERTAINMENT;
                            if (name.equalsIgnoreCase(Category.UTILITIES.getName())) return Category.UTILITIES;
                            if (name.equalsIgnoreCase(Category.RENT.getName())) return Category.RENT;
                            if (name.equalsIgnoreCase(Category.EDUCATION.getName())) return Category.EDUCATION;
                            if (name.equalsIgnoreCase(Category.HEALTH.getName())) return Category.HEALTH;
                            if (name.equalsIgnoreCase(Category.SALARY.getName())) return Category.SALARY;
                            if (name.equalsIgnoreCase(Category.INVESTMENT.getName())) return Category.INVESTMENT;
                            if (name.equalsIgnoreCase(Category.GIFT.getName())) return Category.GIFT;
                            if (name.equalsIgnoreCase(Category.REFUND.getName())) return Category.REFUND;
                            // Add other predefined categories from Category.java
                            if (name.equalsIgnoreCase(Category.MEDICAL.getName())) return Category.MEDICAL;
                            if (name.equalsIgnoreCase(Category.FITNESS.getName())) return Category.FITNESS;
                            if (name.equalsIgnoreCase(Category.TRAVEL.getName())) return Category.TRAVEL;
                            if (name.equalsIgnoreCase(Category.TRAINING.getName())) return Category.TRAINING;
                            if (name.equalsIgnoreCase(Category.OTHER.getName())) return Category.OTHER;
                            return new Category(name);
                        });

                        String transactionId = "CSV-TX-" + System.currentTimeMillis() + "-" + importedTransactions.size();
                        Transaction transaction = new Transaction(transactionId, date, signedAmount, descriptionStr, category, type, selectedAccountForImport);
                        importedTransactions.add(transaction);

                    } catch (DateTimeParseException e) {
                        errorMessages.add("第 " + lineNumber + " 行: 日期格式无效 '" + values[0] + "'. 期望 yyyy-MM-dd. 错误: " + e.getMessage());
                    } catch (NumberFormatException e) {
                        errorMessages.add("第 " + lineNumber + " 行: 金额格式无效 '" + values[3] + "'. 错误: " + e.getMessage());
                    } catch (Exception e) {
                        errorMessages.add("第 " + lineNumber + " 行: 处理错误 '" + line + "'. 错误: " + e.getMessage());
                        LOGGER.log(Level.WARNING, "处理CSV行 " + lineNumber + " 时出错", e);
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "读取CSV文件时出错: " + e.getMessage(), "导入错误", JOptionPane.ERROR_MESSAGE);
                LOGGER.log(Level.SEVERE, "读取CSV文件时出错", e);
                return;
            }

            if (!importedTransactions.isEmpty()) {
                for (Transaction tx : importedTransactions) {
                    transactionService.addTransaction(tx);
                    selectedAccountForImport.addTransaction(tx); // This will trigger balance calculation in Account
                }
                // selectedAccountForImport.calculateBalance(); // Not needed if Account.addTransaction does it
            }

            StringBuilder summaryMessage = new StringBuilder();
            summaryMessage.append(LanguageUtil.getText("file.imported")).append(" ")
                    .append(importedTransactions.size()).append(" transactions from ")
                    .append(selectedFile.getName()).append(".\n");
            if (!errorMessages.isEmpty()) {
                summaryMessage.append("\nEncountered ").append(errorMessages.size()).append(" errors:\n");
                for (int i = 0; i < Math.min(errorMessages.size(), 10); i++) {
                    summaryMessage.append("- ").append(errorMessages.get(i)).append("\n");
                }
                if (errorMessages.size() > 10) {
                    summaryMessage.append("...and ").append(errorMessages.size() - 10).append(" more errors (see logs).\n");
                }
            }
            JOptionPane.showMessageDialog(this,
                    summaryMessage.toString(),
                    errorMessages.isEmpty() ? "Import Successful" : (importedTransactions.isEmpty() ? "Import Failed" : "Import Partially Successful"),
                    errorMessages.isEmpty() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);

            showPanel("account"); // Switch to account panel to see results
        }
    }


    private void exportData() {
        // ... (exportData method remains the same as your last provided version) ...
        // Ensure LOGGER, CSV_DATE_FORMATTER, PDF_DATE_FORMATTER are defined as static final in MainFrame
        LOGGER.info("exportData 方法开始执行。");
        List<Transaction> transactions = transactionService.getTransactions();
        if (transactions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有可导出的交易数据。", "导出数据", JOptionPane.INFORMATION_MESSAGE);
            LOGGER.info("没有交易数据可导出。");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(LanguageUtil.getText("file.export"));
        String[] exportOptions = { LanguageUtil.getText("file.csv_format"), LanguageUtil.getText("file.pdf_format") };
        String exportType = (String) JOptionPane.showInputDialog(this, LanguageUtil.getText("file.select_format"),
                LanguageUtil.getText("file.export_data"), JOptionPane.QUESTION_MESSAGE, null, exportOptions, exportOptions[0]);
        if (exportType != null) {
            LOGGER.info("用户选择的导出类型: " + exportType);
            String fileExtension = exportType.equals(LanguageUtil.getText("file.csv_format")) ? ".csv" : ".pdf";
            String fileDescription = exportType.equals(LanguageUtil.getText("file.csv_format")) ? LanguageUtil.getText("file.csv_files") : LanguageUtil.getText("file.pdf_files");
            fileChooser.setFileFilter(new FileNameExtensionFilter(fileDescription, fileExtension.substring(1)));
            String suggestedFileName = "transactions_export_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + fileExtension;
            fileChooser.setSelectedFile(new File(suggestedFileName));
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (!selectedFile.getName().toLowerCase().endsWith(fileExtension)) {
                    selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + fileExtension);
                }
                LOGGER.info("准备导出到文件: " + selectedFile.getAbsolutePath());
                try {
                    if (exportType.equals(LanguageUtil.getText("file.csv_format"))) {
                        LOGGER.info("调用 exportToCsv...");
                        exportToCsv(transactions, selectedFile);
                        LOGGER.info("exportToCsv 调用完成。");
                    } else {
                        LOGGER.info("调用 exportToPdf...");
                        exportToPdf(transactions, selectedFile);
                        LOGGER.info("exportToPdf 调用完成。");
                    }
                    JOptionPane.showMessageDialog(this, LanguageUtil.getText("file.exported") + " " + selectedFile.getName(),
                            LanguageUtil.getText("file.export_data"), JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "导出数据时发生IO错误: " + selectedFile.getAbsolutePath(), e);
                    JOptionPane.showMessageDialog(this, "导出数据失败 (IO错误): " + e.getMessage(), "导出错误", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "导出数据时发生未知错误: " + selectedFile.getAbsolutePath(), e);
                    JOptionPane.showMessageDialog(this, "导出数据失败 (未知错误): " + e.toString() + "\n详情请查看日志。", "导出错误", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            } else {
                LOGGER.info("用户取消了文件选择。");
            }
        } else {
            LOGGER.info("用户取消了导出类型选择。");
        }
        LOGGER.info("exportData 方法执行完毕。");
    }

    private void exportToCsv(List<Transaction> transactions, File file) throws IOException {
        // ... (exportToCsv method remains the same) ...
        try (FileWriter out = new FileWriter(file, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(out, CSVFormat.DEFAULT
                     .withHeader("Date", "Type", "Description", "Amount", "Category", "Account"))) {
            for (Transaction tx : transactions) {
                String accountName = (tx.getAccount() != null) ? tx.getAccount().getName() : "N/A";
                String categoryName = (tx.getCategory() != null) ? tx.getCategory().getName() : "N/A";
                csvPrinter.printRecord(
                        tx.getDate().format(CSV_DATE_FORMATTER),
                        tx.getType().getDisplayName(),
                        tx.getDescription(),
                        String.format("%.2f", tx.getAmount()),
                        categoryName,
                        accountName
                );
            }
            csvPrinter.flush();
        }
    }

    private void exportToPdf(List<Transaction> transactions, File file) throws IOException {
        // ... (exportToPdf method remains the same, ensure fontResourcePath is correct) ...
        LOGGER.info("exportToPdf 开始，目标文件: " + file.getAbsolutePath());
        PDFont titleFont, headerFont, bodyFont;
        String fontResourcePath = "/resources/fonts/simhei.ttf"; // Ensure this path is correct
        try (PDDocument document = new PDDocument()) {
            LOGGER.info("PDDocument已创建。");
            PDType0Font loadedChineseFont = null;
            try (InputStream fontStream = MainFrame.class.getResourceAsStream(fontResourcePath)) {
                if (fontStream != null) {
                    loadedChineseFont = PDType0Font.load(document, fontStream);
                    LOGGER.info("中文字体 '" + fontResourcePath + "' 已成功加载到PDDocument。");
                } else {
                    LOGGER.warning("中文字体资源未找到: " + fontResourcePath + ". PDF中的中文可能无法正确显示。将使用标准字体。");
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "加载中文字体 '" + fontResourcePath + "' 时发生IO错误。将使用标准字体。", e);
            }
            if (loadedChineseFont != null) {
                titleFont = loadedChineseFont;
                headerFont = loadedChineseFont;
                bodyFont = loadedChineseFont;
                LOGGER.info("PDF将使用加载的中文字体。");
            } else {
                titleFont = PDType1Font.HELVETICA_BOLD;
                headerFont = PDType1Font.HELVETICA_BOLD;
                bodyFont = PDType1Font.HELVETICA;
                LOGGER.warning("由于中文字体加载失败，PDF将使用标准西文字体，中文内容可能无法显示。");
            }
            PDPage page = new PDPage();
            document.addPage(page);
            LOGGER.info("PDPage已创建并添加到文档。");
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                LOGGER.info("PDPageContentStream已创建。");
                float margin = 50;
                float yStart = page.getMediaBox().getHeight() - margin;
                float yPosition = yStart;
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
                float titleFontSize = 16f;
                float headerFontSize = 10f;
                float cellFontSize = 9f;
                float leading = 1.5f * cellFontSize;
                LOGGER.info("开始写入PDF标题。");
                contentStream.beginText();
                contentStream.setFont(titleFont, titleFontSize);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("交易报告 (Transaction Report)");
                contentStream.endText();
                yPosition -= (titleFontSize + 10);
                LOGGER.info("PDF标题写入完毕。");
                LOGGER.info("开始写入PDF表头。");
                contentStream.beginText();
                contentStream.setFont(headerFont, headerFontSize);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("日期(Date)");
                contentStream.newLineAtOffset(80, 0);
                contentStream.showText("类型(Type)");
                contentStream.newLineAtOffset(60, 0);
                contentStream.showText("描述(Description)");
                contentStream.newLineAtOffset(180, 0);
                contentStream.showText("金额(Amount)");
                contentStream.newLineAtOffset(70, 0);
                contentStream.showText("类别(Category)");
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText("账户(Account)");
                contentStream.endText();
                yPosition -= 5;
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(margin + tableWidth, yPosition);
                contentStream.stroke();
                yPosition -= leading;
                LOGGER.info("PDF表头写入完毕。");
                int lineCount = 0;
                for (Transaction tx : transactions) {
                    lineCount++;
                    LOGGER.fine("准备写入PDF行 " + lineCount + ": " + tx.getDescription());
                    if (yPosition < margin + leading) {
                        LOGGER.warning("PDF分页未完全实现，仅写入部分数据。后续交易未写入。");
                        break;
                    }
                    String accountName = (tx.getAccount() != null) ? tx.getAccount().getName() : "N/A";
                    String amountStr = String.format("%.2f", tx.getAmount());
                    String description = tx.getDescription();
                    if (description.length() > 30) description = description.substring(0, 27) + "...";
                    String categoryName = tx.getCategory() != null ? tx.getCategory().getName() : "N/A";
                    contentStream.beginText();
                    contentStream.setFont(bodyFont, cellFontSize);
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText(tx.getDate().format(PDF_DATE_FORMATTER));
                    contentStream.newLineAtOffset(80, 0);
                    contentStream.showText(tx.getType().getDisplayName());
                    contentStream.newLineAtOffset(60, 0);
                    contentStream.showText(description);
                    contentStream.newLineAtOffset(180, 0);
                    contentStream.showText(amountStr);
                    contentStream.newLineAtOffset(70, 0);
                    contentStream.showText(categoryName);
                    contentStream.newLineAtOffset(100, 0);
                    contentStream.showText(accountName);
                    contentStream.endText();
                    yPosition -= leading;
                }
                LOGGER.info("所有交易数据已写入PDF内容流（或已达到单页限制）。");
            }
            LOGGER.info("准备保存PDDocument到文件: " + file.getName());
            document.save(file);
            LOGGER.info("PDDocument已成功保存。");
        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, "在处理PDDocument或PDPageContentStream时发生IO错误", ioe);
            throw ioe;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "在exportToPdf中发生未知错误", e);
            throw new IOException("PDF导出时发生内部错误: " + e.getMessage(), e);
        }
        LOGGER.info("exportToPdf 执行完毕。");
    }


    private void logout() {
        int result = JOptionPane.showConfirmDialog(this, LanguageUtil.getText("logout.message"),
                LanguageUtil.getText("logout.confirm"), JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            dispose();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this, LanguageUtil.getText("about.message"),
                LanguageUtil.getText("about.title"), JOptionPane.INFORMATION_MESSAGE);
    }

    private void showHelpDialog() {
        JOptionPane.showMessageDialog(this, LanguageUtil.getText("help.message"),
                LanguageUtil.getText("help.title"), JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createTitleBarPanel() {
        JPanel titleBarPanel = new JPanel(new BorderLayout());
        titleBarPanel.setPreferredSize(new Dimension(getWidth(), 40));
        titleBarPanel.setBackground(Color.WHITE);
        titleLabel = new JLabel(LanguageUtil.getText("main.title"), JLabel.CENTER);
        titleLabel.setFont(FontLoader.getFont(14, Font.BOLD));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        buttonPanel.setOpaque(false);
        JPanel redButton = createCircleButton(new Color(255, 95, 87));
        JPanel yellowButton = createCircleButton(new Color(255, 189, 46));
        JPanel greenButton = createCircleButton(new Color(39, 201, 63));
        buttonPanel.add(redButton);
        buttonPanel.add(yellowButton);
        buttonPanel.add(greenButton);
        MouseAdapter closeAdapter = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { dispose(); }
            @Override public void mouseEntered(MouseEvent e) { setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
            @Override public void mouseExited(MouseEvent e) { setCursor(Cursor.getDefaultCursor()); }
        };
        redButton.addMouseListener(closeAdapter);
        MouseAdapter minimizeAdapter = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { setState(Frame.ICONIFIED); }
            @Override public void mouseEntered(MouseEvent e) { setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
            @Override public void mouseExited(MouseEvent e) { setCursor(Cursor.getDefaultCursor()); }
        };
        yellowButton.addMouseListener(minimizeAdapter);
        MouseAdapter maximizeAdapter = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if ((getExtendedState() & Frame.MAXIMIZED_BOTH) == 0) setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);
                else setExtendedState(getExtendedState() & ~Frame.MAXIMIZED_BOTH);
            }
            @Override public void mouseEntered(MouseEvent e) { setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
            @Override public void mouseExited(MouseEvent e) { setCursor(Cursor.getDefaultCursor()); }
        };
        greenButton.addMouseListener(maximizeAdapter);
        titleBarPanel.add(buttonPanel, BorderLayout.WEST);
        titleBarPanel.add(titleLabel, BorderLayout.CENTER);
        return titleBarPanel;
    }

    private JPanel createCircleButton(Color color) {
        JPanel button = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 60));
                g2d.fillOval(-1, -1, 14, 14);
                g2d.setColor(color);
                g2d.fillOval(0, 0, 12, 12);
                if (color.equals(new Color(39, 201, 63))) {
                    g2d.setColor(new Color(30, 170, 50));
                    g2d.drawArc(1, 1, 10, 10, 45, 180);
                    g2d.setColor(new Color(100, 255, 120, 120));
                    g2d.drawArc(2, 2, 8, 8, 225, 180);
                }
                g2d.dispose();
            }
        };
        button.setPreferredSize(new Dimension(15, 15));
        button.setOpaque(false);
        return button;
    }

    private void enableFrameDragging() {
        MouseAdapter dragAdapter = new MouseAdapter() {
            private int dragStartX, dragStartY;
            @Override public void mousePressed(MouseEvent e) {
                dragStartX = e.getX();
                dragStartY = e.getY();
            }
            @Override public void mouseDragged(MouseEvent e) {
                setLocation(getLocation().x + e.getX() - dragStartX, getLocation().y + e.getY() - dragStartY);
            }
        };
        // Apply dragging to the title label or the entire title bar panel
        if (titleLabel != null) {
            titleLabel.addMouseListener(dragAdapter);
            titleLabel.addMouseMotionListener(dragAdapter);
        } else if (menuBar != null && menuBar.getParent() instanceof JPanel) { // Fallback to titleBarPanel if titleLabel is complex
            menuBar.getParent().addMouseListener(dragAdapter);
            menuBar.getParent().addMouseMotionListener(dragAdapter);
        }
    }
}
