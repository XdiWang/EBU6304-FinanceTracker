package com.financetracker.view;

import com.financetracker.model.User;
import com.financetracker.util.LanguageUtil;
import com.financetracker.util.FontLoader;

import javax.swing.*;
//import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.ButtonGroup;

/**
 * 应用程序的主窗口
 */
public class MainFrame extends JFrame {

    private User currentUser;
    private JPanel contentPanel;
    private DashboardPanel dashboardPanel;
    private OverviewPanel overviewPanel;
    private AccountPanel accountPanel;
    private AIChatPanel aiChatPanel;

    // 菜单项
    private JMenuItem importMenuItem;
    private JMenuItem exportMenuItem;
    private JMenuItem logoutMenuItem;
    private JMenuItem exitMenuItem;
    private JRadioButtonMenuItem chineseMenuItem;
    private JRadioButtonMenuItem englishMenuItem;

    // 顶部菜单
    private JMenuBar menuBar;
    private JToolBar toolBar;
    private JMenu fileMenu;
    private JMenu viewMenu;
    private JMenu languageMenu;
    private JMenu helpMenu;

    // 工具栏按钮
    private JToggleButton dashboardButton;
    private JToggleButton overviewButton;
    private JToggleButton accountButton;
    private JToggleButton aiChatButton;

    // 添加自定义标题栏
    private JLabel titleLabel;

    private static final Logger LOGGER = Logger.getLogger(MainFrame.class.getName());

    public MainFrame(User user) {
        this.currentUser = user;
        // 设置用户的语言偏好
        LanguageUtil.setCurrentLanguage(user.getPreferredLanguage());
        initComponents();
        setupUI();
        updateLanguage();
    }

    private void initComponents() {
        // 初始化面板
        dashboardPanel = new DashboardPanel(currentUser);
        overviewPanel = new OverviewPanel(currentUser);
        accountPanel = new AccountPanel(currentUser);
        aiChatPanel = new AIChatPanel(currentUser);
    }

    private void setupUI() {
        // 使用自定义窗口装饰，不使用系统标题栏
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setBackground(Color.WHITE);

        // 创建主内容面板
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(Color.WHITE);

        // 自定义窗口边框
        contentWrapper.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // 添加自定义标题栏
        JPanel titleBarPanel = createTitleBarPanel();

        // 创建顶部菜单栏
        menuBar = createMenuBar();

        // 创建顶部工具栏按钮
        toolBar = createToolBar();

        // 添加菜单栏和工具栏到顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titleBarPanel, BorderLayout.NORTH);
        topPanel.add(menuBar, BorderLayout.CENTER);
        topPanel.add(toolBar, BorderLayout.SOUTH);

        contentWrapper.add(topPanel, BorderLayout.NORTH);

        // 创建主内容面板（使用CardLayout切换不同面板）
        contentPanel = new JPanel(new CardLayout());
        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(overviewPanel, "overview");
        contentPanel.add(accountPanel, "account");
        contentPanel.add(aiChatPanel, "aichat");

        contentWrapper.add(contentPanel, BorderLayout.CENTER);
        setContentPane(contentWrapper);

        // 应用字体到所有组件
        FontLoader.applyFontToComponentTree(contentWrapper, FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN);

        // 默认显示仪表盘
        showPanel("dashboard");

        // 添加窗口拖动功能
        enableFrameDragging();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);

        // 获取推荐的中文字体
        Font menuFont = FontLoader.getFont(14, Font.PLAIN);

        // 文件菜单
        fileMenu = new JMenu(LanguageUtil.getText("menu.file"));
        fileMenu.setFont(menuFont);

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

        // 视图菜单
        viewMenu = new JMenu(LanguageUtil.getText("menu.view"));
        viewMenu.setFont(menuFont);

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

        // 语言菜单
        languageMenu = new JMenu(LanguageUtil.getText("menu.language"));
        languageMenu.setFont(menuFont);

        ButtonGroup languageGroup = new ButtonGroup();

        englishMenuItem = new JRadioButtonMenuItem("English");
        englishMenuItem.setFont(menuFont);
        if (LanguageUtil.getCurrentLanguage().equals(LanguageUtil.ENGLISH)) {
            englishMenuItem.setSelected(true);
        }
        englishMenuItem.addActionListener(e -> {
            LanguageUtil.setCurrentLanguage(LanguageUtil.ENGLISH);
            updateLanguage();
        });

        chineseMenuItem = new JRadioButtonMenuItem("中文");
        chineseMenuItem.setFont(menuFont);
        if (LanguageUtil.getCurrentLanguage().equals(LanguageUtil.CHINESE)) {
            chineseMenuItem.setSelected(true);
        }
        chineseMenuItem.addActionListener(e -> {
            LanguageUtil.setCurrentLanguage(LanguageUtil.CHINESE);
            updateLanguage();
        });

        languageGroup.add(englishMenuItem);
        languageGroup.add(chineseMenuItem);
        languageMenu.add(englishMenuItem);
        languageMenu.add(chineseMenuItem);

        // 帮助菜单
        helpMenu = new JMenu(LanguageUtil.getText("menu.help"));
        helpMenu.setFont(menuFont);

        JMenuItem aboutMenuItem = new JMenuItem(LanguageUtil.getText("menu.about"));
        aboutMenuItem.setFont(menuFont);
        aboutMenuItem.addActionListener(e -> showAboutDialog());

        JMenuItem helpMenuItem = new JMenuItem(LanguageUtil.getText("menu.help_item"));
        helpMenuItem.setFont(menuFont);
        helpMenuItem.addActionListener(e -> showHelpDialog());

        helpMenu.add(aboutMenuItem);
        helpMenu.add(helpMenuItem);

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
        toolBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // 获取推荐的中文字体
        Font buttonFont = FontLoader.getFont(14, Font.PLAIN);

        // 使用JToggleButton代替JButton，以便显示选中状态
        dashboardButton = new JToggleButton(LanguageUtil.getText("main.dashboard"));
        overviewButton = new JToggleButton(LanguageUtil.getText("main.overview"));
        accountButton = new JToggleButton(LanguageUtil.getText("main.account"));
        aiChatButton = new JToggleButton(LanguageUtil.getText("main.ai_chat"));

        // 添加按钮到ButtonGroup，确保只有一个按钮被选中
        ButtonGroup navButtonGroup = new ButtonGroup();
        navButtonGroup.add(dashboardButton);
        navButtonGroup.add(overviewButton);
        navButtonGroup.add(accountButton);
        navButtonGroup.add(aiChatButton);

        // 设置按钮样式
        dashboardButton.setFont(buttonFont);
        overviewButton.setFont(buttonFont);
        accountButton.setFont(buttonFont);
        aiChatButton.setFont(buttonFont);

        dashboardButton.setFocusPainted(false);
        overviewButton.setFocusPainted(false);
        accountButton.setFocusPainted(false);
        aiChatButton.setFocusPainted(false);

        dashboardButton.setBackground(Color.WHITE);
        overviewButton.setBackground(Color.WHITE);
        accountButton.setBackground(Color.WHITE);
        aiChatButton.setBackground(Color.WHITE);

        // 设置选中时的背景色
        UIManager.put("ToggleButton.select", new Color(230, 240, 250));

        // 添加按钮到工具栏
        toolBar.add(dashboardButton);
        toolBar.add(overviewButton);
        toolBar.add(accountButton);
        toolBar.add(aiChatButton);

        // 添加按钮事件监听器
        dashboardButton.addActionListener(e -> showPanel("dashboard"));
        overviewButton.addActionListener(e -> showPanel("overview"));
        accountButton.addActionListener(e -> showPanel("account"));
        aiChatButton.addActionListener(e -> showPanel("aichat"));

        // 默认选中仪表盘按钮
        dashboardButton.setSelected(true);

        return toolBar;
    }

    /**
     * 显示指定面板
     */
    public void showPanel(String panelName) {
        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        cardLayout.show(contentPanel, panelName);

        // 重置所有按钮选中状态
        dashboardButton.setSelected(false);
        overviewButton.setSelected(false);
        accountButton.setSelected(false);
        aiChatButton.setSelected(false);

        // 根据当前面板设置相应按钮的选中状态
        switch (panelName) {
            case "dashboard":
                dashboardButton.setSelected(true);
                setTitle(LanguageUtil.getText("main.dashboard") + " - " + LanguageUtil.getText("main.title"));
                if (titleLabel != null) {
                    titleLabel.setText(LanguageUtil.getText("main.dashboard"));
                    titleLabel.setFont(FontLoader.getFont(16, Font.BOLD));
                }
                viewMenu.setText(
                        LanguageUtil.getText("menu.view") + " (" + LanguageUtil.getText("main.dashboard") + ")");
                if (dashboardPanel != null) {
                    try {
                        dashboardPanel.getClass().getMethod("onActivate").invoke(dashboardPanel);
                    } catch (Exception e) {
                        // 忽略如果方法不存在
                    }
                }
                break;
            case "overview":
                overviewButton.setSelected(true);
                setTitle(LanguageUtil.getText("main.overview") + " - " + LanguageUtil.getText("main.title"));
                if (titleLabel != null) {
                    titleLabel.setText(LanguageUtil.getText("main.overview"));
                    titleLabel.setFont(FontLoader.getFont(16, Font.BOLD));
                }
                viewMenu.setText(
                        LanguageUtil.getText("menu.view") + " (" + LanguageUtil.getText("main.overview") + ")");
                if (overviewPanel != null) {
                    try {
                        overviewPanel.getClass().getMethod("onActivate").invoke(overviewPanel);
                    } catch (Exception e) {
                        // 忽略如果方法不存在
                    }
                }
                break;
            case "account":
                accountButton.setSelected(true);
                setTitle(LanguageUtil.getText("main.account") + " - " + LanguageUtil.getText("main.title"));
                if (titleLabel != null) {
                    titleLabel.setText(LanguageUtil.getText("main.account"));
                    titleLabel.setFont(FontLoader.getFont(16, Font.BOLD));
                }
                viewMenu.setText(LanguageUtil.getText("menu.view") + " (" + LanguageUtil.getText("main.account") + ")");
                if (accountPanel != null) {
                    try {
                        accountPanel.getClass().getMethod("onActivate").invoke(accountPanel);
                    } catch (Exception e) {
                        // 忽略如果方法不存在
                    }
                }
                break;
            case "aichat":
                aiChatButton.setSelected(true);
                setTitle(LanguageUtil.getText("main.ai_chat") + " - " + LanguageUtil.getText("main.title"));
                if (titleLabel != null) {
                    titleLabel.setText(LanguageUtil.getText("main.ai_chat"));
                    titleLabel.setFont(FontLoader.getFont(16, Font.BOLD));
                }
                viewMenu.setText(LanguageUtil.getText("menu.view") + " (" + LanguageUtil.getText("main.ai_chat") + ")");
                if (aiChatPanel != null) {
                    try {
                        aiChatPanel.getClass().getMethod("onActivate").invoke(aiChatPanel);
                    } catch (Exception e) {
                        // 忽略如果方法不存在
                    }
                }
                break;
        }
    }

    /**
     * 更新界面语言
     */
    private void updateLanguage() {
        // 更新窗口标题
        setTitle(LanguageUtil.getText("main.title"));

        // 更新自定义标题栏标题
        if (titleLabel != null) {
            titleLabel.setText(LanguageUtil.getText("main.title"));
            titleLabel.setFont(FontLoader.getFont(14, Font.PLAIN));
        }

        // 更新菜单文本
        fileMenu.setText(LanguageUtil.getText("menu.file"));
        viewMenu.setText(LanguageUtil.getText("menu.view"));
        languageMenu.setText(LanguageUtil.getText("menu.language"));
        helpMenu.setText(LanguageUtil.getText("menu.help"));

        // 更新菜单项文本
        importMenuItem.setText(LanguageUtil.getText("file.import"));
        exportMenuItem.setText(LanguageUtil.getText("file.export"));
        logoutMenuItem.setText(LanguageUtil.getText("menu.logout"));
        exitMenuItem.setText(LanguageUtil.getText("menu.exit"));

        // 更新语言菜单项
        chineseMenuItem.setText(LanguageUtil.getText("main.chinese"));
        englishMenuItem.setText(LanguageUtil.getText("main.english"));

        // 更新工具栏按钮文本
        dashboardButton.setText(LanguageUtil.getText("main.dashboard"));
        overviewButton.setText(LanguageUtil.getText("main.overview"));
        accountButton.setText(LanguageUtil.getText("main.account"));
        aiChatButton.setText(LanguageUtil.getText("main.ai_chat"));

        // 更新各个面板的语言
        if (dashboardPanel != null) {
            // 如果DashboardPanel有自己的updateLanguage方法，调用它
            try {
                java.lang.reflect.Method updateMethod = dashboardPanel.getClass().getMethod("updateLanguage");
                updateMethod.invoke(dashboardPanel);
            } catch (Exception e) {
                // 如果没有此方法，忽略错误
            }
        }

        if (overviewPanel != null) {
            try {
                java.lang.reflect.Method updateMethod = overviewPanel.getClass().getMethod("updateLanguage");
                updateMethod.invoke(overviewPanel);
            } catch (Exception e) {
                // 忽略
            }
        }

        if (accountPanel != null) {
            try {
                java.lang.reflect.Method updateMethod = accountPanel.getClass().getMethod("updateLanguage");
                updateMethod.invoke(accountPanel);
            } catch (Exception e) {
                // 忽略
            }
        }

        if (aiChatPanel != null) {
            try {
                java.lang.reflect.Method updateMethod = aiChatPanel.getClass().getMethod("updateLanguage");
                updateMethod.invoke(aiChatPanel);
            } catch (Exception e) {
                // 忽略
            }
        }

        // 更新当前窗口标题
        String currentPanel = "";
        // 通过判断可见面板来确定当前面板
        if (aiChatPanel.isVisible()) {
            currentPanel = "aichat";
        } else if (dashboardPanel.isVisible()) {
            currentPanel = "dashboard";
        } else if (overviewPanel.isVisible()) {
            currentPanel = "overview";
        } else if (accountPanel.isVisible()) {
            currentPanel = "account";
        }

        if (!currentPanel.isEmpty()) {
            showPanel(currentPanel);
        }

        // 重绘整个窗口以应用更改
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
                return LanguageUtil.getText("file.csv_files");
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            // 这里添加导入CSV文件的逻辑
            JOptionPane.showMessageDialog(this,
                    LanguageUtil.getText("file.imported") + " " + selectedFile.getName(),
                    LanguageUtil.getText("file.import_data"),
                    JOptionPane.INFORMATION_MESSAGE);

            // 导入后切换到对应面板，实现面板互通
            showPanel("overview");
        }
    }

    private void exportData() {
        // 创建文件选择器
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(LanguageUtil.getText("file.export"));

        // 提供CSV和PDF选项
        String[] exportOptions = {
                LanguageUtil.getText("file.csv_format"),
                LanguageUtil.getText("file.pdf_format")
        };
        String exportType = (String) JOptionPane.showInputDialog(
                this,
                LanguageUtil.getText("file.select_format"),
                LanguageUtil.getText("file.export_data"),
                JOptionPane.QUESTION_MESSAGE,
                null,
                exportOptions,
                exportOptions[0]);

        if (exportType != null) {
            // 设置文件过滤器
            if (exportType.equals(LanguageUtil.getText("file.csv_format"))) {
                fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                    public boolean accept(java.io.File f) {
                        return f.getName().toLowerCase().endsWith(".csv") || f.isDirectory();
                    }

                    public String getDescription() {
                        return LanguageUtil.getText("file.csv_files");
                    }
                });
            } else {
                fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                    public boolean accept(java.io.File f) {
                        return f.getName().toLowerCase().endsWith(".pdf") || f.isDirectory();
                    }

                    public String getDescription() {
                        return LanguageUtil.getText("file.pdf_files");
                    }
                });
            }

            int result = fileChooser.showSaveDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File selectedFile = fileChooser.getSelectedFile();
                // 处理导出文件的逻辑
                JOptionPane.showMessageDialog(this,
                        LanguageUtil.getText("file.exported") + " " + selectedFile.getName() + " " + exportType,
                        LanguageUtil.getText("file.export_data"),
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void logout() {
        int result = JOptionPane.showConfirmDialog(this,
                LanguageUtil.getText("logout.message"),
                LanguageUtil.getText("logout.confirm"),
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            dispose();
            // 创建并显示登录窗口
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                LanguageUtil.getText("about.message"),
                LanguageUtil.getText("about.title"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showHelpDialog() {
        JOptionPane.showMessageDialog(this,
                LanguageUtil.getText("help.message"),
                LanguageUtil.getText("help.title"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    // 添加自定义标题栏
    private JPanel createTitleBarPanel() {
        JPanel titleBarPanel = new JPanel(new BorderLayout());
        titleBarPanel.setPreferredSize(new Dimension(getWidth(), 40));
        titleBarPanel.setBackground(new Color(240, 240, 240));

        // 标题
        titleLabel = new JLabel(LanguageUtil.getText("main.title"), JLabel.CENTER);
        titleLabel.setFont(FontLoader.getFont(14, Font.PLAIN));

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

        // 添加按钮事件
        redButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose(); // 关闭窗口
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());
            }
        });

        yellowButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setState(Frame.ICONIFIED); // 最小化窗口
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());
            }
        });

        greenButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 全屏/还原窗口
                if ((getExtendedState() & Frame.MAXIMIZED_BOTH) == 0) {
                    setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);
                } else {
                    setExtendedState(getExtendedState() & ~Frame.MAXIMIZED_BOTH);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());
            }
        });

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

    /**
     * 启用窗口拖动功能，因为我们使用了无装饰窗口
     */
    private void enableFrameDragging() {
        MouseAdapter dragAdapter = new MouseAdapter() {
            private int dragStartX;
            private int dragStartY;

            @Override
            public void mousePressed(MouseEvent e) {
                dragStartX = e.getX();
                dragStartY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int currentX = getLocation().x;
                int currentY = getLocation().y;
                setLocation(currentX + e.getX() - dragStartX, currentY + e.getY() - dragStartY);
            }
        };

        // 在标题栏上添加拖动事件
        titleLabel.addMouseListener(dragAdapter);
        titleLabel.addMouseMotionListener(dragAdapter);
    }
}