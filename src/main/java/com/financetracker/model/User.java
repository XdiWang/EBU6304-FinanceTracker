package com.financetracker.model;

import java.util.ArrayList;
import java.util.List;
import com.financetracker.util.LanguageUtil;
import com.financetracker.util.LanguageUtil.Language;

/**
 * 用户模型类 - 表示系统用户
 */
public class User {
    private String username;
    private String passwordHash;
    private String email;
    private String phoneNumber;
    private List<Account> accounts;
    private List<Category> customCategories;
    private Language preferredLanguage;

    public User(String username, String passwordHash, String email) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.phoneNumber = "";
        this.accounts = new ArrayList<>();
        this.customCategories = new ArrayList<>();
        this.preferredLanguage = LanguageUtil.ENGLISH; // 默认语言为英语
    }

    public User(String username, String passwordHash, String email, String phoneNumber) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.accounts = new ArrayList<>();
        this.customCategories = new ArrayList<>();
        this.preferredLanguage = LanguageUtil.ENGLISH; // 默认语言为英语
    }

    // 添加账户
    public void addAccount(Account account) {
        accounts.add(account);
    }

    // 添加自定义分类
    public void addCategory(Category category) {
        customCategories.add(category);
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public List<Category> getCustomCategories() {
        return customCategories;
    }

    public Language getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(Language preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
}