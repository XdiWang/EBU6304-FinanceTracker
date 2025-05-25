package com.financetracker.model;

import java.util.ArrayList;
import java.util.List;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

/**
 * 账户模型类 - 表示用户的财务账户（如银行账户，支付宝，微信支付等）
 */
public class Account {
    private String name;
    private AccountType type;
    private double balance;
    private List<Transaction> transactions;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private static final DecimalFormat BALANCE_FORMATTER = new DecimalFormat("￥#,##0.00");


    public enum AccountType {
        BANK("银行账户"),
        ALIPAY("支付宝"),
        WECHAT_PAY("微信支付"),
        CASH("现金"),
        OTHER("其他");

        private final String displayName;

        AccountType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public Account(String name, AccountType type) {
        this.name = name;
        this.type = type;
        this.balance = 0.0; // Initial balance is 0
        this.transactions = new ArrayList<>();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    /**
     * 添加交易并更新账户余额。
     * 确保交易确实属于此账户。
     * @param transaction 要添加的交易
     */
    public void addTransaction(Transaction transaction) {
        // 确保交易与此账户关联，或者将其关联到此账户
        if (transaction != null) {
            if (transaction.getAccount() == null) {
                transaction.setAccount(this); // 如果交易没有账户，则分配给此账户
            } else if (transaction.getAccount() != this) {
                // 如果交易已关联到其他账户，则不应添加到此账户的内部列表
                // 或者根据业务逻辑处理（例如，抛出异常或记录警告）
                System.err.println("警告: 尝试将已属于账户 '" + transaction.getAccount().getName() +
                        "' 的交易添加到账户 '" + this.name + "'。操作被忽略。");
                return;
            }

            if (!this.transactions.contains(transaction)) { //避免重复添加
                this.transactions.add(transaction);
                calculateBalance(); // 添加后重新计算余额
            }
        }
    }

    /**
     * 从交易记录中移除交易并更新余额
     * @param transaction 要移除的交易
     */
    public void removeTransaction(Transaction transaction) {
        if (transaction != null && this.transactions.remove(transaction)) {
            calculateBalance();
        }
    }

    /**
     * 根据交易记录计算并更新账户余额.
     * 同时触发 "balance" 属性变化事件.
     */
    public void calculateBalance() {
        double oldBalance = this.balance;
        double newBalance = 0.0;
        for (Transaction transaction : this.transactions) {
            // 假设 Transaction.getAmount() 对于支出已经是负数，对于收入是正数
            newBalance += transaction.getAmount();
        }
        this.balance = newBalance;
        this.pcs.firePropertyChange("balance", oldBalance, this.balance);
    }

    // Getters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        // 如果名称更改，可能需要通知监听器，但这通常不直接影响余额
        this.name = name;
        this.pcs.firePropertyChange("name", null, this.name); // 示例：通知名称更改
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
        this.pcs.firePropertyChange("type", null, this.type); // 示例：通知类型更改
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        // 返回一个副本以防止外部直接修改内部列表
        return new ArrayList<>(transactions);
    }

    /**
     * 清空此账户的所有交易并重置余额。
     */
    public void clearTransactions() {
        double oldBalance = this.balance;
        List<Transaction> oldTransactions = new ArrayList<>(this.transactions);

        this.transactions.clear();
        this.balance = 0.0;

        this.pcs.firePropertyChange("balance", oldBalance, this.balance);
        // 通知交易列表本身已更改（例如，变为空）
        this.pcs.firePropertyChange("transactionsList", oldTransactions, new ArrayList<>(this.transactions));
    }

    @Override
    public String toString() {
        // 用于 JComboBox 等处的显示
        return name + " (" + type.getDisplayName() + ")";
    }

    /**
     * 获取格式化后的余额字符串。
     * @return 格式化为 "￥#,##0.00" 的余额字符串。
     */
    public String getFormattedBalance() {
        return BALANCE_FORMATTER.format(this.balance);
    }
}
