package com.financetracker.controller;

import com.financetracker.model.User;
import com.financetracker.util.PasswordUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器 - 处理用户登录、注册和认证
 */
public class UserController {

    // 使用内存中的用户存储，实际应用中应使用文件或数据库
    private Map<String, User> userMap;
    private User currentUser;

    public UserController() {
        userMap = new HashMap<>();
        // 添加一个默认测试用户
        String defaultUsername = "test";
        String defaultPassword = "password";
        String passwordHash = PasswordUtils.hashPassword(defaultPassword);
        User defaultUser = new User(defaultUsername, passwordHash, "test@example.com");
        userMap.put(defaultUsername, defaultUser);
    }

    /**
     * 验证用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录是否成功
     */
    public boolean validateLogin(String username, String password) {
        User user = userMap.get(username);

        if (user != null) {
            String passwordHash = user.getPasswordHash();
            boolean isValid = PasswordUtils.verifyPassword(password, passwordHash);

            if (isValid) {
                currentUser = user;
                return true;
            }
        }

        return false;
    }

    /**
     * 注册新用户
     *
     * @param username 用户名
     * @param password 密码
     * @param email    电子邮箱
     * @return 注册是否成功
     */
    public boolean registerUser(String username, String password, String email) {
        // 检查用户名是否已存在
        if (userMap.containsKey(username)) {
            return false;
        }

        // 创建新用户
        String passwordHash = PasswordUtils.hashPassword(password);
        User newUser = new User(username, passwordHash, email);
        userMap.put(username, newUser);

        return true;
    }

    /**
     * 注册新用户（带手机号）
     *
     * @param username    用户名
     * @param password    密码
     * @param email       电子邮箱
     * @param phoneNumber 手机号码
     * @return 注册是否成功
     */
    public boolean registerUser(String username, String password, String email, String phoneNumber) {
        // 检查用户名是否已存在
        if (userMap.containsKey(username)) {
            return false;
        }

        // 创建新用户
        String passwordHash = PasswordUtils.hashPassword(password);
        User newUser = new User(username, passwordHash, email, phoneNumber);
        userMap.put(username, newUser);

        return true;
    }

    /**
     * 获取当前登录用户
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * 退出登录
     */
    public void logout() {
        currentUser = null;
    }
}