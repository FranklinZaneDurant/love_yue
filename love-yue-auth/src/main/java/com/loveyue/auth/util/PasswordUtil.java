package com.loveyue.auth.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.regex.Pattern;

/**
 * 密码工具类
 * 
 * @author loveyue
 * @since 2025-07-13
 */
@Slf4j
@Component
public class PasswordUtil {

    /**
     * BCrypt加密强度
     */
    private static final int BCRYPT_ROUNDS = 12;

    /**
     * 密码最小长度
     */
    private static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * 密码最大长度
     */
    private static final int MAX_PASSWORD_LENGTH = 128;

    /**
     * 密码复杂度正则表达式
     * 至少包含一个大写字母、一个小写字母、一个数字和一个特殊字符
     */
    private static final String COMPLEX_PASSWORD_PATTERN = 
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{" + MIN_PASSWORD_LENGTH + "," + MAX_PASSWORD_LENGTH + "}$";

    /**
     * 中等复杂度密码正则表达式
     * 至少包含字母和数字
     */
    private static final String MEDIUM_PASSWORD_PATTERN = 
        "^(?=.*[a-zA-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{" + MIN_PASSWORD_LENGTH + "," + MAX_PASSWORD_LENGTH + "}$";

    /**
     * 简单密码正则表达式
     * 只要求长度
     */
    private static final String SIMPLE_PASSWORD_PATTERN = 
        "^.{" + MIN_PASSWORD_LENGTH + "," + MAX_PASSWORD_LENGTH + "}$";

    /**
     * 常见弱密码列表
     */
    private static final String[] WEAK_PASSWORDS = {
        "password", "123456", "123456789", "12345678", "12345", "1234567", "1234567890",
        "qwerty", "abc123", "111111", "123123", "admin", "root", "user", "guest",
        "password123", "admin123", "root123", "123qwe", "qwe123", "asd123", "zxc123"
    };

    /**
     * 密码强度枚举
     */
    @Getter
    public enum PasswordStrength {
        WEAK("弱"),
        MEDIUM("中等"),
        STRONG("强"),
        VERY_STRONG("很强");

        private final String description;

        PasswordStrength(String description) {
            this.description = description;
        }

    }

    /**
     * 加密密码
     */
    public String encryptPassword(String plainPassword) {
        if (StrUtil.isBlank(plainPassword)) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * 验证密码
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (StrUtil.isBlank(plainPassword) || StrUtil.isBlank(hashedPassword)) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            log.error("密码验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 生成随机密码
     */
    public String generateRandomPassword(int length) {
        if (length < MIN_PASSWORD_LENGTH) {
            length = MIN_PASSWORD_LENGTH;
        }
        if (length > MAX_PASSWORD_LENGTH) {
            length = MAX_PASSWORD_LENGTH;
        }

        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "@$!%*?&";
        String allChars = upperCase + lowerCase + digits + specialChars;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // 确保至少包含每种类型的字符
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        // 填充剩余长度
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // 打乱字符顺序
        return RandomUtil.randomString(password.toString(), length);
    }

    /**
     * 生成简单随机密码（只包含字母和数字）
     */
    public String generateSimpleRandomPassword(int length) {
        if (length < MIN_PASSWORD_LENGTH) {
            length = MIN_PASSWORD_LENGTH;
        }
        if (length > MAX_PASSWORD_LENGTH) {
            length = MAX_PASSWORD_LENGTH;
        }

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        return RandomUtil.randomString(chars, length);
    }

    /**
     * 验证密码复杂度
     */
    public boolean validatePasswordComplexity(String password, PasswordComplexityLevel level) {
        if (StrUtil.isBlank(password)) {
            return false;
        }

        return switch (level) {
            case SIMPLE -> Pattern.matches(SIMPLE_PASSWORD_PATTERN, password);
            case MEDIUM -> Pattern.matches(MEDIUM_PASSWORD_PATTERN, password);
            case COMPLEX -> Pattern.matches(COMPLEX_PASSWORD_PATTERN, password);
        };
    }

    /**
     * 密码复杂度级别
     */
    public enum PasswordComplexityLevel {
        SIMPLE,   // 简单：只要求长度
        MEDIUM,   // 中等：要求字母和数字
        COMPLEX   // 复杂：要求大小写字母、数字和特殊字符
    }

    /**
     * 评估密码强度
     */
    public PasswordStrength evaluatePasswordStrength(String password) {
        if (StrUtil.isBlank(password)) {
            return PasswordStrength.WEAK;
        }

        int score = 0;

        // 长度评分
        if (password.length() >= 8) score += 1;
        if (password.length() >= 12) score += 1;
        if (password.length() >= 16) score += 1;

        // 字符类型评分
        if (password.matches(".*[a-z].*")) score += 1; // 小写字母
        if (password.matches(".*[A-Z].*")) score += 1; // 大写字母
        if (password.matches(".*\\d.*")) score += 1;    // 数字
        if (password.matches(".*[@$!%*?&].*")) score += 1; // 特殊字符

        // 复杂度评分
        if (!isCommonPassword(password)) score += 1;
        if (!hasRepeatingChars(password)) score += 1;
        if (!hasSequentialChars(password)) score += 1;

        // 根据评分确定强度
        if (score <= 3) {
            return PasswordStrength.WEAK;
        } else if (score <= 6) {
            return PasswordStrength.MEDIUM;
        } else if (score <= 8) {
            return PasswordStrength.STRONG;
        } else {
            return PasswordStrength.VERY_STRONG;
        }
    }

    /**
     * 检查是否为常见弱密码
     */
    public boolean isCommonPassword(String password) {
        if (StrUtil.isBlank(password)) {
            return true;
        }

        String lowerPassword = password.toLowerCase();
        for (String weakPassword : WEAK_PASSWORDS) {
            if (lowerPassword.equals(weakPassword) || lowerPassword.contains(weakPassword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否有重复字符
     */
    private boolean hasRepeatingChars(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            if (password.charAt(i) == password.charAt(i + 1) && 
                password.charAt(i + 1) == password.charAt(i + 2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否有连续字符
     */
    private boolean hasSequentialChars(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            char c1 = password.charAt(i);
            char c2 = password.charAt(i + 1);
            char c3 = password.charAt(i + 2);
            
            // 检查连续递增或递减
            if ((c2 == c1 + 1 && c3 == c2 + 1) || (c2 == c1 - 1 && c3 == c2 - 1)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 生成密码重置令牌
     */
    public String generatePasswordResetToken() {
        return RandomUtil.randomString(32);
    }

    /**
     * 生成临时密码
     */
    public String generateTempPassword() {
        return generateSimpleRandomPassword(8);
    }

    /**
     * 检查密码是否需要更新（基于最后修改时间）
     */
    public boolean isPasswordExpired(java.time.LocalDateTime lastPasswordChangeTime, int maxDays) {
        if (lastPasswordChangeTime == null) {
            return true;
        }
        return lastPasswordChangeTime.plusDays(maxDays).isBefore(java.time.LocalDateTime.now());
    }

    /**
     * 获取密码强度描述
     */
    public String getPasswordStrengthDescription(String password) {
        PasswordStrength strength = evaluatePasswordStrength(password);
        return strength.getDescription();
    }

    /**
     * 获取密码复杂度建议
     */
    public String getPasswordComplexityAdvice(String password) {
        if (StrUtil.isBlank(password)) {
            return "密码不能为空";
        }

        StringBuilder advice = new StringBuilder();

        if (password.length() < MIN_PASSWORD_LENGTH) {
            advice.append("密码长度至少需要").append(MIN_PASSWORD_LENGTH).append("位；");
        }

        if (!password.matches(".*[a-z].*")) {
            advice.append("建议包含小写字母；");
        }

        if (!password.matches(".*[A-Z].*")) {
            advice.append("建议包含大写字母；");
        }

        if (!password.matches(".*\\d.*")) {
            advice.append("建议包含数字；");
        }

        if (!password.matches(".*[@$!%*?&].*")) {
            advice.append("建议包含特殊字符(@$!%*?&)；");
        }

        if (isCommonPassword(password)) {
            advice.append("避免使用常见密码；");
        }

        if (hasRepeatingChars(password)) {
            advice.append("避免连续重复字符；");
        }

        if (hasSequentialChars(password)) {
            advice.append("避免连续顺序字符；");
        }

        return !advice.isEmpty() ? advice.toString() : "密码强度良好";
    }
}