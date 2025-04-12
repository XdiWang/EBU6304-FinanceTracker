package com.financetracker.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 交易模型类 - 表示用户的收入和支出交易
 */
public class Transaction {
    private String id;
    private LocalDate date;
    private double amount;
    private String description;
    private Category category;
    private TransactionType type;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static int transactionCounter = 1000;

    public enum TransactionType {
        INCOME("收入"),
        EXPENSE("支出");

        private final String displayName;

        TransactionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public Transaction(LocalDate date, double amount, String description, Category category, TransactionType type) {
        this.id = generateId();
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.type = type;
    }

    /**
     * 生成唯一交易ID
     */
    private String generateId() {
        return "TXN" + (++transactionCounter);
    }

    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }

    public boolean isExpense() {
        return type == TransactionType.EXPENSE;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        String formattedAmount = String.format("%.2f", amount);
        String sign = type == TransactionType.INCOME ? "+" : "-";
        return date.format(DATE_FORMATTER) + " " + sign + "￥" + formattedAmount + " " + category.getName() + ": "
                + description;
    }
}