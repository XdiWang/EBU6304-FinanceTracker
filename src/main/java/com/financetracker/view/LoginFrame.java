
package com.financetracker.view;

import com.financetracker.controller.UserController;
import com.financetracker.util.LanguageUtil;
import com.financetracker.util.FontLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 登录窗口 - 允许用户登录或注册
 */
public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private UserController userController;
    private JLabel titleLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;

    public LoginFrame() {
        userController = new UserController();
        setupUI();
    }

    private void setupUI() {
        setTitle(LanguageUtil.getText("login.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // 主面板 - 使用单一面板代替分割面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255)); // 淡蓝色背景，与原型匹配

        // 创建标题栏
        JPanel titleBarPanel = createTitleBarPanel();

        // 登录表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 248, 255)); // 淡蓝色背景，与原型匹配

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        // 用户名输入区域
        usernameLabel = new JLabel(LanguageUtil.getText("login.username"));
        usernameLabel.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_BOLD));
        usernameField = new JTextField(15);
        usernameField.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));
        usernameField.setPreferredSize(new Dimension(350, 40));
        usernameField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // 密码输入区域
        passwordLabel = new JLabel(LanguageUtil.getText("login.password"));
        passwordLabel.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_BOLD));
        passwordField = new JPasswordField(15);
        passwordField.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));
        passwordField.setPreferredSize(new Dimension(350, 40));
        passwordField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        passwordField.setEchoChar('\u25CF'); // Set the echo character to a bullet for better visibility

        // 按钮
        loginButton = new JButton(LanguageUtil.getText("login.login"));
        registerButton = new JButton(LanguageUtil.getText("login.register"));

        // 设置按钮样式
        loginButton.setPreferredSize(new Dimension(150, 45));
        loginButton.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_BOLD));
        loginButton.setBackground(Color.BLACK);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);

        registerButton.setPreferredSize(new Dimension(150, 45));
        registerButton.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_BOLD));
        registerButton.setBackground(Color.BLACK);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);

        // 使用GridBagLayout添加组件
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(passwordField, gbc);

        // 创建一个面板来放置按钮，并使用FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.add(registerButton);
        buttonPanel.add(loginButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        // 将表单添加到主面板
        mainPanel.add(titleBarPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // 设置主面板
        setContentPane(mainPanel);

        // 设置登录按钮为默认按钮
        getRootPane().setDefaultButton(loginButton);

        // 添加按钮监听器
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegisterDialog();
            }
        });

        // 添加语言切换菜单
        JMenuBar menuBar = new JMenuBar();
        JMenu languageMenu = new JMenu(LanguageUtil.getText("main.language"));
        ButtonGroup languageGroup = new ButtonGroup();
        JRadioButtonMenuItem chineseMenuItem = new JRadioButtonMenuItem(LanguageUtil.getText("main.chinese"));
        JRadioButtonMenuItem englishMenuItem = new JRadioButtonMenuItem(LanguageUtil.getText("main.english"));

        // 设置菜单项字体以确保中文显示正常
        Font menuFont = FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN);
        languageMenu.setFont(menuFont);
        chineseMenuItem.setFont(menuFont);
        englishMenuItem.setFont(menuFont);

        // 根据当前语言设置选中项
        if (LanguageUtil.getCurrentLanguage().equals(LanguageUtil.CHINESE)) {
            chineseMenuItem.setSelected(true);
        } else {
            englishMenuItem.setSelected(true);
        }

        languageGroup.add(chineseMenuItem);
        languageGroup.add(englishMenuItem);
        languageMenu.add(chineseMenuItem);
        languageMenu.add(englishMenuItem);
        menuBar.add(languageMenu);

        // 添加语言切换事件
        chineseMenuItem.addActionListener(e -> {
            LanguageUtil.setCurrentLanguage(LanguageUtil.CHINESE);
            updateTexts();
            // 刷新字体以确保中文显示正常
            FontLoader.refreshAllUIComponentFonts();
        });

        englishMenuItem.addActionListener(e -> {
            LanguageUtil.setCurrentLanguage(LanguageUtil.ENGLISH);
            updateTexts();
            // 刷新字体以确保UI显示正常
            FontLoader.refreshAllUIComponentFonts();
        });

        // 添加语言菜单到标题栏
        titleBarPanel.add(menuBar, BorderLayout.EAST);
    }

    private JPanel createTitleBarPanel() {
        JPanel titleBarPanel = new JPanel(new BorderLayout());
        titleBarPanel.setPreferredSize(new Dimension(getWidth(), 40));
        titleBarPanel.setBackground(new Color(240, 240, 240));

        // 标题
        titleLabel = new JLabel(LanguageUtil.getText("login.title"), JLabel.CENTER);
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

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    LanguageUtil.getText("login.error.empty"),
                    LanguageUtil.getText("login.error"),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 添加调试输出
        System.out.println("尝试登录: 用户名 = " + username + ", 密码 = " + password);

        boolean success = userController.validateLogin(username, password);
        System.out.println("登录结果: " + (success ? "成功" : "失败"));

        if (success) {
            // 显示二次验证对话框
            boolean twoFactorVerified = showTwoFactorAuthDialog();

            if (twoFactorVerified) {
                // 打开主窗口
                MainFrame mainFrame = new MainFrame(userController.getCurrentUser());
                mainFrame.setVisible(true);
                dispose(); // 关闭登录窗口
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    LanguageUtil.getText("login.error.invalid"),
                    LanguageUtil.getText("login.error"),
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    // 显示两因素认证对话框，与原型图匹配
    private boolean showTwoFactorAuthDialog() {
        JDialog twoFactorDialog = new JDialog(this, LanguageUtil.getText("2fa.title"), true);
        twoFactorDialog.setSize(550, 350);
        twoFactorDialog.setLocationRelativeTo(this);
        twoFactorDialog.setLayout(new BorderLayout());
        twoFactorDialog.getContentPane().setBackground(new Color(240, 248, 255));

        // 创建标题栏
        JPanel titleBarPanel = createTitleBarPanel();
        twoFactorDialog.add(titleBarPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(240, 248, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 电子邮件/电话号码输入
        JLabel contactLabel = new JLabel(LanguageUtil.getText("2fa.contact"));
        contactLabel.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_BOLD));

        JTextField contactField = new JTextField(20);
        contactField.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));
        contactField.setPreferredSize(new Dimension(300, 40));

        // 获取验证码按钮
        JButton getCodeButton = new JButton(LanguageUtil.getText("2fa.get_code"));
        getCodeButton.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_BOLD));
        getCodeButton.setBackground(Color.BLACK);
        getCodeButton.setForeground(Color.WHITE);
        getCodeButton.setFocusPainted(false);
        getCodeButton.setBorderPainted(false);

        // 验证码输入
        JLabel codeLabel = new JLabel(LanguageUtil.getText("2fa.enter_code"));
        codeLabel.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_BOLD));

        JTextField codeField = new JTextField(10);
        codeField.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));
        codeField.setPreferredSize(new Dimension(300, 40));

        // 登录和重新发送按钮
        JButton loginButton = new JButton(LanguageUtil.getText("2fa.login"));
        loginButton.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_BOLD));
        loginButton.setBackground(Color.BLACK);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setPreferredSize(new Dimension(120, 45));

        JButton resendButton = new JButton(LanguageUtil.getText("2fa.resend"));
        resendButton.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_BOLD));
        resendButton.setBackground(Color.BLACK);
        resendButton.setForeground(Color.WHITE);
        resendButton.setFocusPainted(false);
        resendButton.setBorderPainted(false);
        resendButton.setPreferredSize(new Dimension(120, 45));

        // 添加联系方式和获取验证码按钮
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(contactLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.7;
        contentPanel.add(contactField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        contentPanel.add(getCodeButton, gbc);

        // 添加验证码输入
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(codeLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        contentPanel.add(codeField, gbc);

        // 添加按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.add(loginButton);
        buttonPanel.add(resendButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(buttonPanel, gbc);

        twoFactorDialog.add(contentPanel, BorderLayout.CENTER);

        // 生成验证码
        final String[] verificationCode = { generateVerificationCode() };
        System.out.println("生成验证码: " + verificationCode[0]);

        // 添加按钮事件处理
        getCodeButton.addActionListener(e -> {
            String contact = contactField.getText().trim();
            if (contact.isEmpty()) {
                JOptionPane.showMessageDialog(twoFactorDialog,
                        LanguageUtil.getText("2fa.error.contact"),
                        LanguageUtil.getText("2fa.error"),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            verificationCode[0] = generateVerificationCode();
            System.out.println("生成新验证码: " + verificationCode[0]);
            JOptionPane.showMessageDialog(twoFactorDialog,
                    LanguageUtil.getText("2fa.send.message") + " " + contact + "\n" + verificationCode[0],
                    LanguageUtil.getText("2fa.send.success"),
                    JOptionPane.INFORMATION_MESSAGE);
        });

        resendButton.addActionListener(e -> {
            String contact = contactField.getText().trim();
            if (contact.isEmpty()) {
                JOptionPane.showMessageDialog(twoFactorDialog,
                        LanguageUtil.getText("2fa.error.contact"),
                        LanguageUtil.getText("2fa.error"),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            verificationCode[0] = generateVerificationCode();
            System.out.println("重新发送验证码: " + verificationCode[0]);
            JOptionPane.showMessageDialog(twoFactorDialog,
                    LanguageUtil.getText("2fa.send.message") + " " + contact + "\n" + verificationCode[0],
                    LanguageUtil.getText("2fa.send.success"),
                    JOptionPane.INFORMATION_MESSAGE);
        });

        final boolean[] verified = { false };

        loginButton.addActionListener(e -> {
            String code = codeField.getText().trim();
            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(twoFactorDialog,
                        LanguageUtil.getText("2fa.error.code"),
                        LanguageUtil.getText("2fa.error"),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (code.equals(verificationCode[0])) {
                verified[0] = true;
                twoFactorDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(twoFactorDialog,
                        LanguageUtil.getText("2fa.error.invalid"),
                        LanguageUtil.getText("2fa.error"),
                        JOptionPane.ERROR_MESSAGE);
                codeField.setText("");
            }
        });

        twoFactorDialog.setVisible(true);
        return verified[0];
    }

    private String generateVerificationCode() {
        // 生成6位随机数字验证码
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }

    private void openRegisterDialog() {
        JDialog registerDialog = new JDialog(this, LanguageUtil.getText("register.title"), true);
        registerDialog.setSize(450, 550);
        registerDialog.setLocationRelativeTo(this);
        registerDialog.setResizable(false);

        // 确保对话框使用正确的字体
        registerDialog.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;

        // 用户名字段
        JLabel usernameLabel = new JLabel(LanguageUtil.getText("register.username"));
        usernameLabel.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_BOLD));
        JTextField usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(400, 35));
        usernameField.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));

        // 密码字段
        JLabel passwordLabel = new JLabel(LanguageUtil.getText("register.password"));
        passwordLabel.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_BOLD));
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(400, 35));
        passwordField.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));
        passwordField.setEchoChar('\u25CF'); // Use bullet character for password

        // 确认密码
        JLabel confirmPasswordLabel = new JLabel(LanguageUtil.getText("register.confirm"));
        confirmPasswordLabel.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_BOLD));

        JPasswordField confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));
        confirmPasswordField.setPreferredSize(new Dimension(400, 35));
        confirmPasswordField.setEchoChar('\u25CF'); // Use bullet character for confirm password

        // 电子邮件
        JLabel emailLabel = new JLabel(LanguageUtil.getText("register.email"));
        emailLabel.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_BOLD));

        JTextField emailField = new JTextField(20);
        emailField.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));
        emailField.setPreferredSize(new Dimension(400, 35));

        // 添加手机号
        JLabel phoneLabel = new JLabel(LanguageUtil.getText("register.phone"));
        phoneLabel.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_BOLD));

        JTextField phoneField = new JTextField(20);
        phoneField.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));
        phoneField.setPreferredSize(new Dimension(400, 35));

        // 注册和取消按钮
        JButton registerButton = new JButton(LanguageUtil.getText("register.register"));
        registerButton.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_BOLD));
        registerButton.setBackground(Color.BLACK);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(180, 45));

        JButton cancelButton = new JButton(LanguageUtil.getText("register.cancel"));
        cancelButton.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_BOLD));
        cancelButton.setBackground(Color.BLACK);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setPreferredSize(new Dimension(180, 45));

        // 添加组件到面板
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(usernameLabel, gbc);

        gbc.gridy = 1;
        contentPanel.add(usernameField, gbc);

        gbc.gridy = 2;
        contentPanel.add(passwordLabel, gbc);

        gbc.gridy = 3;
        contentPanel.add(passwordField, gbc);

        gbc.gridy = 4;
        contentPanel.add(confirmPasswordLabel, gbc);

        gbc.gridy = 5;
        contentPanel.add(confirmPasswordField, gbc);

        gbc.gridy = 6;
        contentPanel.add(emailLabel, gbc);

        gbc.gridy = 7;
        contentPanel.add(emailField, gbc);

        gbc.gridy = 8;
        contentPanel.add(phoneLabel, gbc);

        gbc.gridy = 9;
        contentPanel.add(phoneField, gbc);

        // 语言切换按钮，允许在对话框中切换语言
        JPanel languagePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        languagePanel.setBackground(new Color(240, 248, 255));
        languagePanel.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));

        JButton langChineseButton = new JButton("中文");
        langChineseButton.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));
        langChineseButton.setPreferredSize(new Dimension(80, 30));
        langChineseButton.addActionListener(e -> {
            LanguageUtil.setCurrentLanguage(LanguageUtil.CHINESE);
            updateRegisterDialogTexts(registerDialog, usernameLabel, passwordLabel, confirmPasswordLabel,
                    emailLabel, phoneLabel, registerButton, cancelButton);
            titleLabel.setText(LanguageUtil.getText("register.title"));
            registerDialog.setTitle(LanguageUtil.getText("register.title"));
        });

        JButton langEnglishButton = new JButton("English");
        langEnglishButton.setFont(FontLoader.getFont(FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN));
        langEnglishButton.setPreferredSize(new Dimension(80, 30));
        langEnglishButton.addActionListener(e -> {
            LanguageUtil.setCurrentLanguage(LanguageUtil.ENGLISH);
            updateRegisterDialogTexts(registerDialog, usernameLabel, passwordLabel, confirmPasswordLabel,
                    emailLabel, phoneLabel, registerButton, cancelButton);
            titleLabel.setText(LanguageUtil.getText("register.title"));
            registerDialog.setTitle(LanguageUtil.getText("register.title"));
        });

        languagePanel.add(langChineseButton);
        languagePanel.add(langEnglishButton);

        gbc.gridy = 10;
        contentPanel.add(languagePanel, gbc);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = 11;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(buttonPanel, gbc);

        registerDialog.add(contentPanel, BorderLayout.CENTER);

        // 应用字体到整个对话框组件树
        FontLoader.applyFontToComponentTree(registerDialog, FontLoader.FONT_SIZE_MEDIUM, FontLoader.STYLE_PLAIN);

        // 添加按钮事件
        cancelButton.addActionListener(e -> registerDialog.dispose());

        registerButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            // 验证所有字段
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()
                    || phone.isEmpty()) {
                JOptionPane.showMessageDialog(registerDialog,
                        LanguageUtil.getText("register.error.empty"),
                        LanguageUtil.getText("register.error"),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 密码匹配验证
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(registerDialog,
                        LanguageUtil.getText("register.error.password"),
                        LanguageUtil.getText("register.error"),
                        JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
                confirmPasswordField.setText("");
                return;
            }

            // 邮箱格式验证
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            if (!email.matches(emailRegex)) {
                JOptionPane.showMessageDialog(registerDialog,
                        LanguageUtil.getText("register.error.email"),
                        LanguageUtil.getText("register.error"),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 手机号格式验证 (中国手机号)
            String phoneRegex = "^1[3-9]\\d{9}$";
            if (!phone.matches(phoneRegex)) {
                JOptionPane.showMessageDialog(registerDialog,
                        LanguageUtil.getText("register.error.phone"),
                        LanguageUtil.getText("register.error"),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 注册用户 (这里需要修改UserController以支持手机号)
            boolean success = userController.registerUser(username, password, email, phone);

            if (success) {
                JOptionPane.showMessageDialog(registerDialog,
                        LanguageUtil.getText("register.success.message"),
                        LanguageUtil.getText("register.success"),
                        JOptionPane.INFORMATION_MESSAGE);
                registerDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(registerDialog,
                        LanguageUtil.getText("register.error.exists"),
                        LanguageUtil.getText("register.error"),
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        registerDialog.setVisible(true);
    }

    /**
     * 更新注册对话框的文本标签
     */
    private void updateRegisterDialogTexts(JDialog dialog, JLabel usernameLabel, JLabel passwordLabel,
            JLabel confirmPasswordLabel, JLabel emailLabel, JLabel phoneLabel,
            JButton registerButton, JButton cancelButton) {

        // 更新标签文本
        usernameLabel.setText(LanguageUtil.getText("register.username"));
        passwordLabel.setText(LanguageUtil.getText("register.password"));
        confirmPasswordLabel.setText(LanguageUtil.getText("register.confirm"));
        emailLabel.setText(LanguageUtil.getText("register.email"));
        phoneLabel.setText(LanguageUtil.getText("register.phone"));

        // 更新按钮文本
        registerButton.setText(LanguageUtil.getText("register.register"));
        cancelButton.setText(LanguageUtil.getText("register.cancel"));

        // 刷新对话框UI
        SwingUtilities.updateComponentTreeUI(dialog);
    }

    /**
     * 更新界面语言
     */
    private void updateTexts() {
        // 更新窗口标题
        setTitle(LanguageUtil.getText("login.title"));
        titleLabel.setText(LanguageUtil.getText("login.title"));

        // 更新标签
        usernameLabel.setText(LanguageUtil.getText("login.username"));
        passwordLabel.setText(LanguageUtil.getText("login.password"));

        // 更新按钮
        loginButton.setText(LanguageUtil.getText("login.login"));
        registerButton.setText(LanguageUtil.getText("login.register"));

        // 重绘整个窗口以应用更改
        SwingUtilities.updateComponentTreeUI(this);
    }
}

