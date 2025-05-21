package com.financetracker.view;

import com.financetracker.model.Account;
import com.financetracker.model.User;
import com.financetracker.model.Transaction;
import com.financetracker.service.TransactionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 账户管理面板 - 允许用户管理多个账户和添加交易
 */
public class AccountPanel extends JPanel implements PropertyChangeListener {

    @SuppressWarnings("unused")
    private User currentUser;
    private TransactionService transactionService;
    private JComboBox<Account> accountComboBox;
    private JLabel balanceLabel;
    private JTable transactionsTable;
    private DefaultTableModel tableModel;
    private Color accentColor = new Color(0, 122, 255); // Apple blue accent color
    private Color backgroundColor = new Color(248, 248, 248); // Light gray background
    private Color textColor = new Color(50, 50, 50); // Dark gray text

    public AccountPanel(User user, TransactionService transactionService) {
        this.currentUser = user;
        this.transactionService = transactionService;
        this.transactionService.addPropertyChangeListener(this);
        setupUI();
        refreshTransactionTable();
    }

    private void setupUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(backgroundColor);

        // 顶部账户选择面板
        JPanel topPanel = new JPanel(new BorderLayout(15, 15));
        topPanel.setBackground(backgroundColor);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // 账户选择下拉框
        JPanel accountSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        accountSelectionPanel.setBackground(backgroundColor);
        JLabel accountLabel = new JLabel("选择账户:");
        accountLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        accountLabel.setForeground(textColor);

        // 创建假账户
        Account bankAccount = new Account("中国银行储蓄卡 (银行账户)", Account.AccountType.BANK);
        Account alipayAccount = new Account("支付宝", Account.AccountType.ALIPAY);
        Account weChatAccount = new Account("微信支付", Account.AccountType.WECHAT_PAY);

        accountComboBox = new JComboBox<>();
        accountComboBox.addItem(bankAccount);
        accountComboBox.addItem(alipayAccount);
        accountComboBox.addItem(weChatAccount);
        accountComboBox.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        accountComboBox.setBackground(Color.WHITE);
        accountComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        // Make the dropdown look more modern
        accountComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(new EmptyBorder(6, 10, 6, 10));
                if (isSelected) {
                    setBackground(accentColor);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(textColor);
                }
                return this;
            }
        });

        accountSelectionPanel.add(accountLabel);
        accountSelectionPanel.add(accountComboBox);

        // 账户余额面板
        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        balancePanel.setBackground(backgroundColor);
        JLabel balanceTitleLabel = new JLabel("账户余额:");
        balanceTitleLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        balanceTitleLabel.setForeground(textColor);

        balanceLabel = new JLabel("￥12,345.67");
        balanceLabel.setFont(new Font("SF Pro Display", Font.BOLD, 18));
        balanceLabel.setForeground(accentColor);

        balancePanel.add(balanceTitleLabel);
        balancePanel.add(balanceLabel);

        topPanel.add(accountSelectionPanel, BorderLayout.WEST);
        topPanel.add(balancePanel, BorderLayout.EAST);

        // 中心交易记录表格
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        JLabel transactionHeaderLabel = new JLabel("交易记录");
        transactionHeaderLabel.setFont(new Font("SF Pro Display", Font.BOLD, 16));
        transactionHeaderLabel.setForeground(textColor);
        transactionHeaderLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        centerPanel.add(transactionHeaderLabel, BorderLayout.NORTH);

        String[] columnNames = { "日期", "描述", "类别", "金额", "类型" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 使表格不可编辑
            }
        };

        transactionsTable = new JTable(tableModel);
        transactionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionsTable.setRowHeight(40);
        transactionsTable.setShowGrid(false);
        transactionsTable.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        transactionsTable.setBackground(Color.WHITE);
        transactionsTable.setSelectionBackground(new Color(240, 245, 255));
        transactionsTable.setSelectionForeground(textColor);
        transactionsTable.setIntercellSpacing(new Dimension(0, 0));

        // Style the table header
        JTableHeader header = transactionsTable.getTableHeader();
        header.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        header.setBackground(new Color(245, 245, 247));
        header.setForeground(new Color(100, 100, 100));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        // Apple-style row renderer with subtle separators
        transactionsTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);

                // Set padding
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                        BorderFactory.createEmptyBorder(0, 12, 0, 12)));

                // Style based on column type
                if (column == 3) { // Amount column
                    label.setHorizontalAlignment(SwingConstants.RIGHT);
                    String text = value.toString();
                    if (text.startsWith("+")) {
                        label.setForeground(new Color(76, 217, 100)); // Green for income
                    } else if (text.startsWith("-")) {
                        label.setForeground(new Color(255, 59, 48)); // Red for expense
                    }
                } else {
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                    label.setForeground(isSelected ? textColor : new Color(60, 60, 60));
                }

                if (isSelected) {
                    label.setBackground(new Color(0, 122, 255, 20));
                    label.setForeground(column == 3 ? label.getForeground() : textColor);
                } else {
                    label.setBackground(Color.WHITE);
                }

                return label;
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(transactionsTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableScrollPane.getViewport().setBackground(Color.WHITE);

        // Customize the scrollbar for Apple look
        JScrollBar verticalScrollBar = tableScrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16);

        // Simpler scrollbar customization to avoid errors
        verticalScrollBar.putClientProperty("JScrollBar.showButtons", false);
        verticalScrollBar.putClientProperty("Scrollbar.thumbArc", 8);
        verticalScrollBar.putClientProperty("Scrollbar.minimumThumbSize", new Dimension(8, 40));
        verticalScrollBar.setPreferredSize(new Dimension(8, 0));

        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        // 底部按钮面板
        JPanel bottomPanel = new JPanel(new BorderLayout(15, 0));
        bottomPanel.setBackground(backgroundColor);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        // 添加新交易按钮面板
        JPanel addTransactionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        addTransactionPanel.setBackground(backgroundColor);

        JButton addIncomeButton = createStyledButton("添加收入", new Color(76, 217, 100)); // Green for income
        JButton addExpenseButton = createStyledButton("添加支出", new Color(255, 59, 48)); // Red for expense

        addTransactionPanel.add(addIncomeButton);
        addTransactionPanel.add(addExpenseButton);

        // 管理账户按钮面板
        JPanel manageAccountsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        manageAccountsPanel.setBackground(backgroundColor);

        JButton addAccountButton = createStyledButton("添加账户", accentColor);
        JButton editAccountButton = createStyledButton("编辑账户", accentColor);
        JButton deleteAccountButton = createStyledButton("删除账户", new Color(142, 142, 147)); // Gray for delete

        manageAccountsPanel.add(addAccountButton);
        manageAccountsPanel.add(editAccountButton);
        manageAccountsPanel.add(deleteAccountButton);

        bottomPanel.add(addTransactionPanel, BorderLayout.WEST);
        bottomPanel.add(manageAccountsPanel, BorderLayout.EAST);

        // 添加所有面板到主面板
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // 添加事件监听器
        accountComboBox.addActionListener(e -> updateAccountDetails());
        addIncomeButton.addActionListener(e -> showAddTransactionDialog(true));
        addExpenseButton.addActionListener(e -> showAddTransactionDialog(false));
        addAccountButton.addActionListener(e -> showAddAccountDialog());
        editAccountButton.addActionListener(e -> showEditAccountDialog());
        deleteAccountButton.addActionListener(e -> deleteAccount());
    }

    private JButton createStyledButton(String text, Color buttonColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(buttonColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(buttonColor.brighter());
                } else {
                    g2.setColor(buttonColor);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        button.setFont(new Font("SF Pro Display", Font.PLAIN, 13));
        button.setForeground(Color.WHITE);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        return button;
    }

    private void refreshTransactionTable() {
        if (tableModel == null || transactionService == null) {
            return;
        }
        tableModel.setRowCount(0);
        List<Transaction> transactions = transactionService.getTransactions();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Transaction tx : transactions) {
            String typeStr = tx.getType() == Transaction.TransactionType.INCOME ? "收入" : "支出";
            String amountStr = String.format("%s￥%.2f", tx.getType() == Transaction.TransactionType.INCOME ? "+" : "-",
                    Math.abs(tx.getAmount()));

            String categoryName = tx.getCategory() != null ? tx.getCategory().getName() : "未分类";

            tableModel.addRow(new Object[] {
                    tx.getDate().format(dateFormatter),
                    tx.getDescription(),
                    categoryName,
                    amountStr,
                    typeStr
            });
        }
    }

    private void updateAccountDetails() {
        Account selectedAccount = (Account) accountComboBox.getSelectedItem();
        if (selectedAccount != null) {
            // 实际应用中，应根据选定账户的数据更新余额和交易列表
            String accountType = selectedAccount.getType().getDisplayName();

            // 模拟不同账户的余额
            if (accountType.equals("银行账户")) {
                balanceLabel.setText("￥12,345.67");
            } else if (accountType.equals("支付宝")) {
                balanceLabel.setText("￥5,678.90");
            } else if (accountType.equals("微信支付")) {
                balanceLabel.setText("￥1,234.56");
            }

            // 清空并重新填充交易表格
            tableModel.setRowCount(0);
            refreshTransactionTable();
        }
    }

    private void showAddTransactionDialog(boolean isIncome) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), isIncome ? "添加收入" : "添加支出", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(new Color(245, 245, 247));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel dateLabel = new JLabel("日期:");
        formPanel.add(dateLabel, gbc);
        gbc.gridx = 1;
        JTextField dateField = new JTextField(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        dateField.setToolTipText("YYYY-MM-DD");
        formPanel.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel descriptionLabel = new JLabel("描述:");
        formPanel.add(descriptionLabel, gbc);
        gbc.gridx = 1;
        JTextField descriptionField = new JTextField(20);
        formPanel.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel categoryLabel = new JLabel("类别:");
        formPanel.add(categoryLabel, gbc);
        gbc.gridx = 1;
        JTextField categoryField = new JTextField("餐饮");
        formPanel.add(categoryField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel amountLabel = new JLabel("金额 (￥):");
        formPanel.add(amountLabel, gbc);
        gbc.gridx = 1;
        JTextField amountField = new JTextField(10);
        formPanel.add(amountField, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        buttonPanel.setBackground(new Color(245, 245, 247));
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            try {
                LocalDate date = LocalDate.parse(dateField.getText(), DateTimeFormatter.ISO_DATE);
                String description = descriptionField.getText();
                String categoryName = categoryField.getText();
                double amount = Double.parseDouble(amountField.getText());

                if (description.isEmpty() || amount <= 0) {
                    JOptionPane.showMessageDialog(dialog, "描述和金额不能为空，金额必须大于0。", "输入错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                com.financetracker.model.Category category = new com.financetracker.model.Category(categoryName);

                String transactionId = "TX" + System.currentTimeMillis();

                Transaction newTransaction = new Transaction(
                        transactionId,
                        date,
                        isIncome ? amount : -amount,
                        description,
                        category,
                        isIncome ? Transaction.TransactionType.INCOME : Transaction.TransactionType.EXPENSE,
                        (Account) accountComboBox.getSelectedItem());

                transactionService.addTransaction(newTransaction);

                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "金额必须是有效的数字。", "输入错误", JOptionPane.ERROR_MESSAGE);
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "日期格式无效。请使用 YYYY-MM-DD。", "输入错误", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "保存交易时出错: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void showAddAccountDialog() {
        JDialog addAccountDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "添加账户", true);
        addAccountDialog.setSize(300, 200);
        addAccountDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("账户名称:");
        JTextField nameField = new JTextField(15);

        JLabel typeLabel = new JLabel("账户类型:");
        JComboBox<String> typeComboBox = new JComboBox<>();
        typeComboBox.addItem("银行账户");
        typeComboBox.addItem("支付宝");
        typeComboBox.addItem("微信支付");
        typeComboBox.addItem("现金");
        typeComboBox.addItem("其他");

        JButton cancelButton = new JButton("取消");
        JButton addButton = new JButton("添加");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(addButton);

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(typeLabel);
        panel.add(typeComboBox);
        panel.add(new JLabel(""));
        panel.add(buttonPanel);

        addAccountDialog.add(panel);

        cancelButton.addActionListener(e -> addAccountDialog.dispose());

        addButton.addActionListener(e -> {
            String accountName = nameField.getText().trim();
            String accountType = (String) typeComboBox.getSelectedItem();

            if (accountName.isEmpty()) {
                JOptionPane.showMessageDialog(addAccountDialog,
                        "请输入账户名称",
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(addAccountDialog,
                    "添加了新账户: " + accountName + " (" + accountType + ")",
                    "成功",
                    JOptionPane.INFORMATION_MESSAGE);

            addAccountDialog.dispose();

            Account.AccountType type = Account.AccountType.BANK;
            if (accountType.equals("支付宝")) {
                type = Account.AccountType.ALIPAY;
            } else if (accountType.equals("微信支付")) {
                type = Account.AccountType.WECHAT_PAY;
            } else if (accountType.equals("现金")) {
                type = Account.AccountType.CASH;
            } else if (accountType.equals("其他")) {
                type = Account.AccountType.OTHER;
            }

            Account newAccount = new Account(accountName, type);
            accountComboBox.addItem(newAccount);
            accountComboBox.setSelectedItem(newAccount);
        });

        addAccountDialog.setVisible(true);
    }

    private void showEditAccountDialog() {
        Account selectedAccount = (Account) accountComboBox.getSelectedItem();
        if (selectedAccount == null) {
            JOptionPane.showMessageDialog(this,
                    "请先选择一个账户",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog editAccountDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "编辑账户", true);
        editAccountDialog.setSize(300, 200);
        editAccountDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("账户名称:");
        JTextField nameField = new JTextField(selectedAccount.getName(), 15);

        JLabel typeLabel = new JLabel("账户类型:");
        JComboBox<String> typeComboBox = new JComboBox<>();
        typeComboBox.addItem("银行账户");
        typeComboBox.addItem("支付宝");
        typeComboBox.addItem("微信支付");
        typeComboBox.addItem("现金");
        typeComboBox.addItem("其他");

        typeComboBox.setSelectedItem(selectedAccount.getType().getDisplayName());

        JButton cancelButton = new JButton("取消");
        JButton saveButton = new JButton("保存");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(typeLabel);
        panel.add(typeComboBox);
        panel.add(new JLabel(""));
        panel.add(buttonPanel);

        editAccountDialog.add(panel);

        cancelButton.addActionListener(e -> editAccountDialog.dispose());

        saveButton.addActionListener(e -> {
            String accountName = nameField.getText().trim();
            String accountType = (String) typeComboBox.getSelectedItem();

            if (accountName.isEmpty()) {
                JOptionPane.showMessageDialog(editAccountDialog,
                        "请输入账户名称",
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(editAccountDialog,
                    "账户已更新: " + accountName + " (" + accountType + ")",
                    "成功",
                    JOptionPane.INFORMATION_MESSAGE);

            editAccountDialog.dispose();

            Account.AccountType type = Account.AccountType.BANK;
            if (accountType.equals("支付宝")) {
                type = Account.AccountType.ALIPAY;
            } else if (accountType.equals("微信支付")) {
                type = Account.AccountType.WECHAT_PAY;
            } else if (accountType.equals("现金")) {
                type = Account.AccountType.CASH;
            } else if (accountType.equals("其他")) {
                type = Account.AccountType.OTHER;
            }

            selectedAccount.setName(accountName);
            selectedAccount.setType(type);

            accountComboBox.repaint();
        });

        editAccountDialog.setVisible(true);
    }

    private void deleteAccount() {
        Account selectedAccount = (Account) accountComboBox.getSelectedItem();
        if (selectedAccount == null) {
            JOptionPane.showMessageDialog(this,
                    "请先选择一个账户",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除账户 '" + selectedAccount.getName() + "' 吗？\n此操作也会删除与此账户关联的所有交易。",
                "确认删除",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: Implement account deletion logic with AccountService
            // transactionService.deleteAccount(selectedAccount); // This was incorrect
            // For now, just remove from ComboBox
            accountComboBox.removeItem(selectedAccount);

            JOptionPane.showMessageDialog(this,
                    "账户已成功删除 (模拟)", // Indicate it's a simulation for now
                    "成功",
                    JOptionPane.INFORMATION_MESSAGE);

            // Refresh or update UI as needed, e.g., if transactions for this account were
            // shown
            // refreshTransactionTable(); // Might be needed if transactions are filtered by
            // account
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("transactions".equals(evt.getPropertyName())) {
            SwingUtilities.invokeLater(this::refreshTransactionTable);
        }
    }
}
