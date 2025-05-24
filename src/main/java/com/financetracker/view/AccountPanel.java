// AccountPanel.java
package com.financetracker.view;

import com.financetracker.model.Account;
import com.financetracker.model.User;
import com.financetracker.model.Transaction;
import com.financetracker.model.Category;
import com.financetracker.service.TransactionService;
import com.financetracker.util.FontLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class AccountPanel extends JPanel implements PropertyChangeListener {

    private User currentUser;
    private TransactionService transactionService;
    private JComboBox<Account> accountComboBox;
    private DefaultComboBoxModel<Account> accountComboBoxModel;
    private JLabel balanceLabel;
    private JTable transactionsTable;
    private DefaultTableModel tableModel;
    private JButton deleteTransactionButton; // 新增删除按钮

    // 用于在刷新表格时临时存储当前账户的交易列表，以便按行号删除
    private List<Transaction> currentDisplayedTransactions = new ArrayList<>();


    private Color accentColor = new Color(0, 122, 255);
    private Color backgroundColor = new Color(248, 248, 248);
    private Color textColor = new Color(50, 50, 50);
    private static final DecimalFormat BALANCE_FORMATTER = new DecimalFormat("￥#,##0.00");

    public AccountPanel(User user, TransactionService transactionService) {
        this.currentUser = user;
        this.transactionService = transactionService;
        this.transactionService.addPropertyChangeListener(this);
        setupUI();
        if (accountComboBoxModel.getSize() > 0) {
            accountComboBox.setSelectedIndex(0);
            updateAccountDetails(accountComboBoxModel.getElementAt(0));
        } else {
            updateAccountDetails(null);
        }
    }

    public DefaultComboBoxModel<Account> getAccountComboBoxModel() {
        return this.accountComboBoxModel;
    }

    private void setupUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(backgroundColor);

        JPanel topPanel = new JPanel(new BorderLayout(15, 15));
        topPanel.setBackground(backgroundColor);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel accountSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        accountSelectionPanel.setBackground(backgroundColor);
        JLabel accountLabelText = new JLabel("选择账户:");
        accountLabelText.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        accountLabelText.setForeground(textColor);

        accountComboBoxModel = new DefaultComboBoxModel<>();
        accountComboBox = new JComboBox<>(accountComboBoxModel);

        Account bankAccount = new Account("中国银行储蓄卡", Account.AccountType.BANK);
        Account alipayAccount = new Account("支付宝", Account.AccountType.ALIPAY);
        Account weChatAccount = new Account("微信支付", Account.AccountType.WECHAT_PAY);
        bankAccount.addPropertyChangeListener(this);
        alipayAccount.addPropertyChangeListener(this);
        weChatAccount.addPropertyChangeListener(this);
        accountComboBoxModel.addElement(bankAccount);
        accountComboBoxModel.addElement(alipayAccount);
        accountComboBoxModel.addElement(weChatAccount);

        accountComboBox.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        accountComboBox.setBackground(Color.WHITE);
        accountComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        accountComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(new EmptyBorder(6, 10, 6, 10));
                if (value instanceof Account) setText(((Account) value).getName());
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
        accountSelectionPanel.add(accountLabelText);
        accountSelectionPanel.add(accountComboBox);

        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        balancePanel.setBackground(backgroundColor);
        JLabel balanceTitleLabel = new JLabel("账户余额:");
        balanceTitleLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        balanceTitleLabel.setForeground(textColor);
        balanceLabel = new JLabel(BALANCE_FORMATTER.format(0.0));
        balanceLabel.setFont(new Font("SF Pro Display", Font.BOLD, 18));
        balanceLabel.setForeground(accentColor);
        balancePanel.add(balanceTitleLabel);
        balancePanel.add(balanceLabel);
        topPanel.add(accountSelectionPanel, BorderLayout.WEST);
        topPanel.add(balancePanel, BorderLayout.EAST);

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
        String[] columnNames = {"日期", "描述", "类别", "金额", "类型"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        transactionsTable = new JTable(tableModel);
        transactionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 确保单选
        transactionsTable.setRowHeight(40);
        transactionsTable.setShowGrid(false);
        transactionsTable.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        transactionsTable.setBackground(Color.WHITE);
        transactionsTable.setSelectionBackground(new Color(240, 245, 255));
        transactionsTable.setSelectionForeground(textColor);
        transactionsTable.setIntercellSpacing(new Dimension(0, 0));
        JTableHeader header = transactionsTable.getTableHeader();
        header.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        header.setBackground(new Color(245, 245, 247));
        header.setForeground(new Color(100, 100, 100));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        transactionsTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                        BorderFactory.createEmptyBorder(0, 12, 0, 12)));
                if (column == 3) { // Amount column
                    label.setHorizontalAlignment(SwingConstants.RIGHT);
                    String textValue = value.toString();
                    if (textValue.contains("-")) {
                        label.setForeground(new Color(255, 59, 48));
                    } else {
                        label.setForeground(new Color(76, 217, 100));
                    }
                } else {
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                    label.setForeground(isSelected ? textColor : new Color(60, 60, 60));
                }
                if (isSelected) label.setBackground(new Color(0, 122, 255, 20));
                else label.setBackground(Color.WHITE);
                return label;
            }
        });
        JScrollPane tableScrollPane = new JScrollPane(transactionsTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableScrollPane.getViewport().setBackground(Color.WHITE);
        JScrollBar verticalScrollBar = tableScrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.putClientProperty("JScrollBar.showButtons", false);
        verticalScrollBar.putClientProperty("Scrollbar.thumbArc", 8);
        verticalScrollBar.putClientProperty("Scrollbar.minimumThumbSize", new Dimension(8, 40));
        verticalScrollBar.setPreferredSize(new Dimension(8, 0));
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(15, 0));
        bottomPanel.setBackground(backgroundColor);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        // --- 修改：添加交易按钮面板 ---
        JPanel addTransactionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        addTransactionButtonsPanel.setBackground(backgroundColor);
        JButton addIncomeButton = createStyledButton("添加收入", new Color(76, 217, 100));
        JButton addExpenseButton = createStyledButton("添加支出", new Color(255, 59, 48));
        deleteTransactionButton = createStyledButton("删除交易", new Color(142, 142, 147)); // 灰色

        addTransactionButtonsPanel.add(addIncomeButton);
        addTransactionButtonsPanel.add(addExpenseButton);
        addTransactionButtonsPanel.add(deleteTransactionButton); // 添加删除按钮
        // --- 修改结束 ---

        JPanel manageAccountsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        manageAccountsPanel.setBackground(backgroundColor);
        JButton addAccountButton = createStyledButton("添加账户", accentColor);
        JButton editAccountButton = createStyledButton("编辑账户", accentColor);
        JButton deleteAccountButton = createStyledButton("删除账户", new Color(142, 142, 147));
        manageAccountsPanel.add(addAccountButton);
        manageAccountsPanel.add(editAccountButton);
        manageAccountsPanel.add(deleteAccountButton);

        bottomPanel.add(addTransactionButtonsPanel, BorderLayout.WEST); // 使用修改后的面板
        bottomPanel.add(manageAccountsPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        accountComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateAccountDetails((Account) e.getItem());
            }
        });
        addIncomeButton.addActionListener(e -> showAddTransactionDialog(true));
        addExpenseButton.addActionListener(e -> showAddTransactionDialog(false));
        deleteTransactionButton.addActionListener(e -> deleteSelectedTransaction()); // 绑定事件
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
                if (getModel().isPressed()) g2.setColor(buttonColor.darker());
                else if (getModel().isRollover()) g2.setColor(buttonColor.brighter());
                else g2.setColor(buttonColor);
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

    private void refreshTransactionTableForAccount(Account account) {
        tableModel.setRowCount(0);
        currentDisplayedTransactions.clear(); // 清空辅助列表
        if (account == null) return;

        List<Transaction> accountTransactions = account.getTransactions();
        currentDisplayedTransactions.addAll(accountTransactions); // 填充辅助列表

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Transaction tx : accountTransactions) {
            String typeStr = tx.getType() == Transaction.TransactionType.INCOME ? "收入" : "支出";
            String amountStr = BALANCE_FORMATTER.format(tx.getAmount());
            String categoryName = tx.getCategory() != null ? tx.getCategory().getName() : "未分类";
            tableModel.addRow(new Object[]{
                    tx.getDate().format(dateFormatter),
                    tx.getDescription(),
                    categoryName,
                    amountStr,
                    typeStr
            });
        }
    }

    private void updateAccountDetails(Account selectedAccount) {
        if (selectedAccount != null) {
            balanceLabel.setText(selectedAccount.getFormattedBalance());
            refreshTransactionTableForAccount(selectedAccount);
        } else {
            balanceLabel.setText(BALANCE_FORMATTER.format(0.0));
            tableModel.setRowCount(0);
            currentDisplayedTransactions.clear();
        }
    }

    private void showAddTransactionDialog(boolean isIncome) {
        Account currentSelectedAccount = (Account) accountComboBox.getSelectedItem();
        if (currentSelectedAccount == null) {
            JOptionPane.showMessageDialog(this, "请先选择一个账户。", "无账户选定", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), isIncome ? "添加收入" : "添加支出", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(new Color(245, 245, 247));
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("日期:"), gbc);
        gbc.gridx = 1;
        JTextField dateField = new JTextField(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        dateField.setToolTipText("YYYY-MM-DD");
        formPanel.add(dateField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("描述:"), gbc);
        gbc.gridx = 1;
        JTextField descriptionField = new JTextField(20);
        formPanel.add(descriptionField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("类别:"), gbc);
        gbc.gridx = 1;
        JComboBox<Category> categoryComboBox = new JComboBox<>();
        // Populate categories - ensure Category class has necessary static fields or a service
        categoryComboBox.addItem(Category.FOOD); categoryComboBox.addItem(Category.TRANSPORT);
        categoryComboBox.addItem(Category.SHOPPING); categoryComboBox.addItem(Category.SALARY);
        if (currentUser != null && currentUser.getCustomCategories() != null) {
            currentUser.getCustomCategories().forEach(categoryComboBox::addItem);
        }
        formPanel.add(categoryComboBox, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("金额 (￥):"), gbc);
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
                Category selectedCategory = (Category) categoryComboBox.getSelectedItem();
                double amountValue = Double.parseDouble(amountField.getText());
                if (description.isEmpty() || amountValue <= 0) {
                    JOptionPane.showMessageDialog(dialog, "描述和金额不能为空，金额必须大于0。", "输入错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (selectedCategory == null) {
                    JOptionPane.showMessageDialog(dialog, "请选择一个类别。", "输入错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double signedAmount = isIncome ? amountValue : -amountValue;
                String transactionId = "TX" + System.currentTimeMillis();
                Transaction newTransaction = new Transaction(transactionId, date, signedAmount, description, selectedCategory,
                        isIncome ? Transaction.TransactionType.INCOME : Transaction.TransactionType.EXPENSE,
                        currentSelectedAccount);
                currentSelectedAccount.addTransaction(newTransaction); // Add to account first (updates balance)
                transactionService.addTransaction(newTransaction);    // Then add to global service
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
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // --- 新增方法：删除选中的交易 ---
    private void deleteSelectedTransaction() {
        int selectedRow = transactionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先在表格中选择要删除的交易记录。", "未选择交易", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Account currentAccount = (Account) accountComboBox.getSelectedItem();
        if (currentAccount == null) {
            JOptionPane.showMessageDialog(this, "没有选定的账户。", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 从 currentDisplayedTransactions 获取原始 Transaction 对象
        // 这依赖于 refreshTransactionTableForAccount 正确填充了 currentDisplayedTransactions
        // 并且表格行顺序与 currentDisplayedTransactions 列表顺序一致
        if (selectedRow >= currentDisplayedTransactions.size()) {
            JOptionPane.showMessageDialog(this, "无法找到选定的交易，请刷新列表。", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Transaction transactionToDelete = currentDisplayedTransactions.get(selectedRow);


        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除选定的交易吗？\n" + transactionToDelete.toString(),
                "确认删除交易",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            currentAccount.removeTransaction(transactionToDelete); // 从账户移除，会更新余额并触发事件
            transactionService.removeTransaction(transactionToDelete); // 从全局服务移除，会触发事件

            // 表格和余额的更新应该由 propertyChange 方法处理
            // 无需在此处显式调用 refreshTransactionTableForAccount 或 updateAccountDetails
            // 因为 Account 和 TransactionService 的 removeTransaction 方法会触发事件
        }
    }
    // --- 新增方法结束 ---


    private void showAddAccountDialog() {
        JDialog addAccountDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "添加账户", true);
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(new JLabel("账户名称:"));
        JTextField nameField = new JTextField(15);
        panel.add(nameField);
        panel.add(new JLabel("账户类型:"));
        JComboBox<String> typeComboBox = new JComboBox<>();
        for (Account.AccountType accType : Account.AccountType.values()) typeComboBox.addItem(accType.getDisplayName());
        panel.add(typeComboBox);
        JButton cancelButton = new JButton("取消");
        JButton addButton = new JButton("添加");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton); buttonPanel.add(addButton);
        panel.add(new JLabel("")); panel.add(buttonPanel);
        addAccountDialog.add(panel);
        cancelButton.addActionListener(e -> addAccountDialog.dispose());
        addButton.addActionListener(e -> {
            String accountName = nameField.getText().trim();
            String selectedTypeDisplayName = (String) typeComboBox.getSelectedItem();
            if (accountName.isEmpty()) {
                JOptionPane.showMessageDialog(addAccountDialog, "请输入账户名称", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Account.AccountType typeEnum = null;
            for (Account.AccountType accType : Account.AccountType.values()) {
                if (accType.getDisplayName().equals(selectedTypeDisplayName)) {
                    typeEnum = accType; break;
                }
            }
            if (typeEnum == null) {
                JOptionPane.showMessageDialog(addAccountDialog, "无效的账户类型。", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Account newAccount = new Account(accountName, typeEnum);
            newAccount.addPropertyChangeListener(this);
            accountComboBoxModel.addElement(newAccount);
            accountComboBox.setSelectedItem(newAccount); // 选中新添加的账户
            JOptionPane.showMessageDialog(addAccountDialog, "添加了新账户: " + accountName, "成功", JOptionPane.INFORMATION_MESSAGE);
            addAccountDialog.dispose();
        });
        addAccountDialog.pack();
        addAccountDialog.setLocationRelativeTo(this);
        addAccountDialog.setVisible(true);
    }

    private void showEditAccountDialog() {
        Account selectedAccount = (Account) accountComboBox.getSelectedItem();
        if (selectedAccount == null) {
            JOptionPane.showMessageDialog(this, "请先选择一个账户", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JDialog editAccountDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "编辑账户", true);
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(new JLabel("账户名称:"));
        JTextField nameField = new JTextField(selectedAccount.getName(), 15);
        panel.add(nameField);
        panel.add(new JLabel("账户类型:"));
        JComboBox<String> typeComboBox = new JComboBox<>();
        for (Account.AccountType accType : Account.AccountType.values()) typeComboBox.addItem(accType.getDisplayName());
        typeComboBox.setSelectedItem(selectedAccount.getType().getDisplayName());
        panel.add(typeComboBox);
        JButton cancelButton = new JButton("取消");
        JButton saveButton = new JButton("保存");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton); buttonPanel.add(saveButton);
        panel.add(new JLabel("")); panel.add(buttonPanel);
        editAccountDialog.add(panel);
        cancelButton.addActionListener(e -> editAccountDialog.dispose());
        saveButton.addActionListener(e -> {
            String accountName = nameField.getText().trim();
            String selectedTypeDisplayName = (String) typeComboBox.getSelectedItem();
            if (accountName.isEmpty()) {
                JOptionPane.showMessageDialog(editAccountDialog, "请输入账户名称", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Account.AccountType typeEnum = null;
            for (Account.AccountType accType : Account.AccountType.values()) {
                if (accType.getDisplayName().equals(selectedTypeDisplayName)) {
                    typeEnum = accType; break;
                }
            }
            if (typeEnum == null) {
                JOptionPane.showMessageDialog(editAccountDialog, "无效的账户类型。", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            selectedAccount.setName(accountName); // This will fire "name" property change if implemented in Account
            selectedAccount.setType(typeEnum);   // This will fire "type" property change if implemented
            accountComboBox.repaint(); // Crucial to update display in ComboBox
            JOptionPane.showMessageDialog(editAccountDialog, "账户已更新: " + accountName, "成功", JOptionPane.INFORMATION_MESSAGE);
            editAccountDialog.dispose();
        });
        editAccountDialog.pack();
        editAccountDialog.setLocationRelativeTo(this);
        editAccountDialog.setVisible(true);
    }

    private void deleteAccount() {
        Account selectedAccount = (Account) accountComboBox.getSelectedItem();
        if (selectedAccount == null) {
            JOptionPane.showMessageDialog(this, "请先选择一个账户", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除账户 '" + selectedAccount.getName() + "' 吗？\n与此账户关联的所有交易将从该账户中移除，并从总交易记录中删除。",
                "确认删除", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            List<Transaction> transactionsToRemove = new ArrayList<>(selectedAccount.getTransactions());
            for (Transaction tx : transactionsToRemove) {
                transactionService.removeTransaction(tx);
            }
            selectedAccount.clearTransactions();
            selectedAccount.removePropertyChangeListener(this);
            accountComboBoxModel.removeElement(selectedAccount);
            JOptionPane.showMessageDialog(this, "账户已删除。", "成功", JOptionPane.INFORMATION_MESSAGE);
            if (accountComboBoxModel.getSize() > 0) {
                accountComboBox.setSelectedIndex(0);
            } else {
                updateAccountDetails(null);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> {
            Account currentSelectedAccount = (Account) accountComboBox.getSelectedItem();
            String propertyName = evt.getPropertyName();
            Object source = evt.getSource();

            if (source == transactionService && "transactions".equals(propertyName)) {
                // Global transaction list changed (e.g., transaction added/removed from service)
                // This might affect the current account if the transaction belonged to it.
                // The Account's own "balance" event should ideally handle the direct update.
                // However, ensure the table for the current account reflects the global state.
                if (currentSelectedAccount != null) {
                    // Re-fetch and display transactions for the current account,
                    // as a transaction might have been added/removed from it via the service.
                    // This also implicitly updates currentDisplayedTransactions.
                    refreshTransactionTableForAccount(currentSelectedAccount);
                    // The balance label should update via the Account's "balance" event listener.
                    // If not, explicitly update it:
                    // balanceLabel.setText(currentSelectedAccount.getFormattedBalance());
                }
            } else if (source instanceof Account && "balance".equals(propertyName)) {
                Account changedAccount = (Account) source;
                if (changedAccount == currentSelectedAccount) {
                    balanceLabel.setText(changedAccount.getFormattedBalance());
                    // When balance changes, it's good practice to refresh its transaction table too,
                    // as the balance change is a result of transaction modifications.
                    refreshTransactionTableForAccount(changedAccount);
                }
            } else if (source instanceof Account && ("name".equals(propertyName) || "type".equals(propertyName))) {
                // If account name or type changes, repaint the combo box to reflect the new toString() value.
                accountComboBox.repaint();
            }
        });
    }
}
