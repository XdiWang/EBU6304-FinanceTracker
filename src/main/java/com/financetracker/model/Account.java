package com.financetracker.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 账户模型类 - 表示用户的财务账户（如银行账户，支付宝，微信支付等）
 */
public class Account {
    private String name;
    private AccountType type;
    private double balance;
    private List<Transaction> transactions;

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
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
    }

    /**
     * 添加交易并更新账户余额
     */
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        updateBalance();
    }

    /**
     * 根据交易记录更新账户余额
     */
    private void updateBalance() {
        balance = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.getType() == Transaction.TransactionType.INCOME) {
                balance += transaction.getAmount();
            } else {
                balance -= transaction.getAmount();
            }
        }
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public String toString() {
        return name + " (" + type.getDisplayName() + ")";
    }
}