package com.financetracker.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码工具类 - 处理密码加密和验证
 */
public class PasswordUtils {

    /**
     * 对密码进行加密哈希
     *
     * @param password 原始密码
     * @return 哈希后的密码
     */
    public static String hashPassword(String password) {
        try {
            // 生成随机盐
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            // 使用SHA-256加密
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

            // 将盐和哈希值合并
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);

            // 将字节数组转换为Base64字符串
            return Base64.getEncoder().encodeToString(combined);

        } catch (NoSuchAlgorithmException e) {
            // 实际应用中应使用日志系统记录异常
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 验证密码是否匹配
     *
     * @param password       原始密码
     * @param hashedPassword 哈希后的密码
     * @return 密码是否匹配
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        try {
            // 将Base64字符串转换回字节数组
            byte[] combined = Base64.getDecoder().decode(hashedPassword);

            // 提取盐和哈希值
            byte[] salt = new byte[16];
            byte[] hash = new byte[combined.length - 16];
            System.arraycopy(combined, 0, salt, 0, salt.length);
            System.arraycopy(combined, salt.length, hash, 0, hash.length);

            // 使用相同的盐对密码进行哈希
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] newHash = md.digest(password.getBytes(StandardCharsets.UTF_8));

            // 比较两个哈希值
            return MessageDigest.isEqual(hash, newHash);

        } catch (NoSuchAlgorithmException | IllegalArgumentException e) {
            // 实际应用中应使用日志系统记录异常
            e.printStackTrace();
            return false;
        }
    }
}