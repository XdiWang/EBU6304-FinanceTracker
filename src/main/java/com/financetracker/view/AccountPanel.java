package com.financetracker.view;

import com.financetracker.model.Account;
import com.financetracker.model.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 账户管理面板 - 允许用户管理多个账户和添加交易
 */
public class AccountPanel extends JPanel {

    @SuppressWarnings("unused")
    private User currentUser;
    private JComboBox<Account> accountComboBox;
    private JLabel balanceLabel;
    private JTable transactionsTable;
    private DefaultTableModel tableModel;

    public AccountPanel(User user) {
        this.currentUser = user;
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 顶部账户选择面板
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        // 账户选择下拉框
        JPanel accountSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel accountLabel = new JLabel("选择账户:");

        // 创建假账户
        Account bankAccount = new Account("中国银行储蓄卡", Account.AccountType.BANK);
        Account alipayAccount = new Account("支付宝", Account.AccountType.ALIPAY);
        Account weChatAccount = new Account("微信支付", Account.AccountType.WECHAT_PAY);

        accountComboBox = new JComboBox<>();
        accountComboBox.addItem(bankAccount);
        accountComboBox.addItem(alipayAccount);
        accountComboBox.addItem(weChatAccount);

        accountSelectionPanel.add(accountLabel);
        accountSelectionPanel.add(accountComboBox);

        // 账户余额面板
        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel balanceTitleLabel = new JLabel("账户余额:");
        balanceLabel = new JLabel("¥ 12,345.67");
        balanceLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));

        balancePanel.add(balanceTitleLabel);
        balancePanel.add(balanceLabel);

        topPanel.add(accountSelectionPanel, BorderLayout.WEST);
        topPanel.add(balancePanel, BorderLayout.EAST);

        // 中心交易记录表格
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "交易记录", TitledBorder.LEFT, TitledBorder.TOP));

        String[] columnNames = { "日期", "描述", "类别", "金额", "类型" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 使表格不可编辑
            }
        };

        // 添加一些模拟交易数据
        addMockTransactions();

        transactionsTable = new JTable(tableModel);
        transactionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionsTable.setRowHeight(25);

        JScrollPane tableScrollPane = new JScrollPane(transactionsTable);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        // 底部按钮面板
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // 添加新交易按钮面板
        JPanel addTransactionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addIncomeButton = new JButton("添加收入");
        JButton addExpenseButton = new JButton("添加支出");

        addTransactionPanel.add(addIncomeButton);
        addTransactionPanel.add(addExpenseButton);

        // 管理账户按钮面板
        JPanel manageAccountsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addAccountButton = new JButton("添加账户");
        JButton editAccountButton = new JButton("编辑账户");
        JButton deleteAccountButton = new JButton("删除账户");

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

    private void addMockTransactions() {
        // 添加一些模拟交易数据
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Object[][] data = {
                { LocalDate.now().format(formatter), "工资", "收入", "+¥ 10,000.00", "收入" },
                { LocalDate.now().minusDays(1).format(formatter), "超市购物", "食品", "-¥ 320.50", "支出" },
                { LocalDate.now().minusDays(2).format(formatter), "电费", "水电煤", "-¥ 150.00", "支出" },
                { LocalDate.now().minusDays(3).format(formatter), "餐厅", "餐饮", "-¥ 89.00", "支出" },
                { LocalDate.now().minusDays(4).format(formatter), "网购", "购物", "-¥ 299.00", "支出" },
                { LocalDate.now().minusDays(5).format(formatter), "电影票", "娱乐", "-¥ 80.00", "支出" },
                { LocalDate.now().minusDays(6).format(formatter), "兼职收入", "收入", "+¥ 500.00", "收入" },
                { LocalDate.now().minusDays(7).format(formatter), "公交卡充值", "交通", "-¥ 100.00", "支出" }
        };

        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }

    private void updateAccountDetails() {
        Account selectedAccount = (Account) accountComboBox.getSelectedItem();
        if (selectedAccount != null) {
            // 实际应用中，应根据选定账户的数据更新余额和交易列表
            String accountType = selectedAccount.getType().getDisplayName();

            // 模拟不同账户的余额
            if (accountType.equals("银行账户")) {
                balanceLabel.setText("¥ 12,345.67");
            } else if (accountType.equals("支付宝")) {
                balanceLabel.setText("¥ 5,678.90");
            } else if (accountType.equals("微信支付")) {
                balanceLabel.setText("¥ 1,234.56");
            }

            // 清空并重新填充交易表格
            tableModel.setRowCount(0);
            addMockTransactions();
        }
    }

    private void showAddTransactionDialog(boolean isIncome) {
        JDialog addTransactionDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isIncome ? "添加收入" : "添加支出", true);
        addTransactionDialog.setSize(400, 300);
        addTransactionDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel dateLabel = new JLabel("日期:");
        JTextField dateField = new JTextField(10);
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        JLabel amountLabel = new JLabel("金额:");
        JTextField amountField = new JTextField(10);

        JLabel descriptionLabel = new JLabel("描述:");
        JTextField descriptionField = new JTextField(20);

        JLabel categoryLabel = new JLabel("类别:");
        JComboBox<String> categoryComboBox = new JComboBox<>();

        // 根据是收入还是支出，添加不同的类别选项
        if (isIncome) {
            categoryComboBox.addItem("工资");
            categoryComboBox.addItem("投资收入");
            categoryComboBox.addItem("礼金");
            categoryComboBox.addItem("退款");
            categoryComboBox.addItem("其他收入");
        } else {
            categoryComboBox.addItem("食品");
            categoryComboBox.addItem("交通");
            categoryComboBox.addItem("购物");
            categoryComboBox.addItem("娱乐");
            categoryComboBox.addItem("水电煤");
            categoryComboBox.addItem("房租");
            categoryComboBox.addItem("教育");
            categoryComboBox.addItem("医疗健康");
        }

        panel.add(dateLabel);
        panel.add(dateField);
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(descriptionLabel);
        panel.add(descriptionField);
        panel.add(categoryLabel);
        panel.add(categoryComboBox);

        JButton cancelButton = new JButton("取消");
        JButton confirmButton = new JButton("确认");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        panel.add(new JLabel("")); // 占位
        panel.add(buttonPanel);

        addTransactionDialog.add(panel);

        // 添加按钮监听器
        cancelButton.addActionListener(e -> addTransactionDialog.dispose());

        confirmButton.addActionListener(e -> {
            try {
                String dateStr = dateField.getText();
                String amountStr = amountField.getText();
                String description = descriptionField.getText();
                String category = (String) categoryComboBox.getSelectedItem();

                // 基本验证
                if (dateStr.isEmpty() || amountStr.isEmpty() || description.isEmpty()) {
                    JOptionPane.showMessageDialog(addTransactionDialog,
                            "所有字段都必须填写",
                            "错误",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 尝试解析金额
                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                    if (amount <= 0) {
                        JOptionPane.showMessageDialog(addTransactionDialog,
                                "金额必须大于零",
                                "错误",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addTransactionDialog,
                            "无效的金额格式，请输入数字",
                            "错误",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 在实际应用中，这里应该添加交易到数据库或内存中的数据结构
                // 现在只是显示一个成功消息
                JOptionPane.showMessageDialog(addTransactionDialog,
                        "交易已成功添加",
                        "成功",
                        JOptionPane.INFORMATION_MESSAGE);

                addTransactionDialog.dispose();

                // 更新交易列表（简单地添加到表格）
                String formattedAmount = String.format("%s¥ %.2f", isIncome ? "+" : "-", amount);
                String type = isIncome ? "收入" : "支出";

                tableModel.insertRow(0, new Object[] { dateStr, description, category, formattedAmount, type });
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addTransactionDialog,
                        "添加交易时出错: " + ex.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        addTransactionDialog.setVisible(true);
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
        panel.add(new JLabel("")); // 占位
        panel.add(buttonPanel);

        addAccountDialog.add(panel);

        // 添加按钮监听器
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

            // 在实际应用中，这里应创建并添加新账户
            JOptionPane.showMessageDialog(addAccountDialog,
                    "添加了新账户: " + accountName + " (" + accountType + ")",
                    "成功",
                    JOptionPane.INFORMATION_MESSAGE);

            addAccountDialog.dispose();

            // 模拟添加账户到下拉框
            Account.AccountType type = Account.AccountType.BANK; // 默认
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

        // 设置当前账户类型
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
        panel.add(new JLabel("")); // 占位
        panel.add(buttonPanel);

        editAccountDialog.add(panel);

        // 添加按钮监听器
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

            // 在实际应用中，这里应更新账户信息
            JOptionPane.showMessageDialog(editAccountDialog,
                    "账户已更新: " + accountName + " (" + accountType + ")",
                    "成功",
                    JOptionPane.INFORMATION_MESSAGE);

            editAccountDialog.dispose();

            // 模拟更新账户
            Account.AccountType type = Account.AccountType.BANK; // 默认
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

            // 刷新下拉框
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
                "确定要删除账户 \"" + selectedAccount + "\" 吗？\n该操作不可撤销。",
                "确认删除",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // 在实际应用中，这里应删除账户数据

            // 从下拉框中移除该账户
            accountComboBox.removeItem(selectedAccount);

            JOptionPane.showMessageDialog(this,
                    "账户已成功删除",
                    "成功",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}