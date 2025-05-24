package com.financetracker.service;

import com.financetracker.model.Transaction;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {
    private List<Transaction> transactions;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    // 在实际应用中，应该从数据存储（如数据库）加载初始数据
    // 为简化起见，这里初始化一个空列表
    public TransactionService() {
        this.transactions = new ArrayList<>();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public List<Transaction> getTransactions() {
        // 返回一个副本以防止外部直接修改
        return new ArrayList<>(transactions);
    }

    public void addTransaction(Transaction transaction) {
        if (transaction == null) {
            return;
        }
        List<Transaction> oldTransactions = new ArrayList<>(this.transactions);
        this.transactions.add(transaction);
        // 通常，"transactions" 作为属性名，表示整个列表的更改
        // 你也可以为单个添加/删除/更新使用不同的属性名，如果需要更细粒度的控制
        this.pcs.firePropertyChange("transactions", oldTransactions, new ArrayList<>(this.transactions));
    }

    public void removeTransaction(Transaction transaction) {
        if (transaction == null || !this.transactions.contains(transaction)) {
            return;
        }
        List<Transaction> oldTransactions = new ArrayList<>(this.transactions);
        this.transactions.remove(transaction);
        this.pcs.firePropertyChange("transactions", oldTransactions, new ArrayList<>(this.transactions));
    }

    public void updateTransaction(Transaction oldTransaction, Transaction newTransaction) {
        if (oldTransaction == null || newTransaction == null || !this.transactions.contains(oldTransaction)) {
            return;
        }
        List<Transaction> oldTransactionsList = new ArrayList<>(this.transactions);
        int index = this.transactions.indexOf(oldTransaction);
        if (index != -1) {
            this.transactions.set(index, newTransaction);
            this.pcs.firePropertyChange("transactions", oldTransactionsList, new ArrayList<>(this.transactions));
        }
    }

    // 未来可以添加从持久化存储加载/保存交易的方法
    // public void loadTransactions() { ... }
    // public void saveTransactions() { ... }
}
