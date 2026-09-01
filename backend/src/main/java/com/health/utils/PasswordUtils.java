package com.health.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码工具类
 * <p>
 * 新密码使用 PBKDF2WithHmacSHA256 + 随机盐进行不可逆哈希，避免使用容易被暴力破解的 MD5。
 * 为了兼容历史数据，仍支持校验旧的 MD5(密码 + 固定盐) 格式。
 *
 * @author health-team
 */
public final class PasswordUtils {

    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final String LEGACY_SALT = "health_management_salt_2024";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordUtils() {
    }

    /**
     * 生成 PBKDF2 密码哈希。
     * 存储格式：iterations$saltBase64$hashBase64
     */
    public static String encode(String rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("rawPassword must not be null");
        }

        byte[] salt = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        byte[] hash = pbkdf2(rawPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

        return ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    /**
     * 校验原始密码与数据库中的密码哈希是否匹配。
     */
    public static boolean matches(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null || storedPassword.isEmpty()) {
            return false;
        }
        return storedPassword.contains("$")
                ? matchesPbkdf2(rawPassword, storedPassword)
                : matchesLegacyMd5(rawPassword, storedPassword);
    }

    /**
     * 判断是否为旧版 MD5 哈希，便于登录成功后触发密码升级。
     */
    public static boolean isLegacyHash(String storedPassword) {
        return storedPassword != null && !storedPassword.isEmpty() && !storedPassword.contains("$");
    }

    private static boolean matchesPbkdf2(String rawPassword, String storedPassword) {
        try {
            String[] parts = storedPassword.split("\\$");
            if (parts.length != 3) {
                return false;
            }

            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[2]);
            byte[] actualHash = pbkdf2(rawPassword.toCharArray(), salt, iterations, expectedHash.length * 8);

            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean matchesLegacyMd5(String rawPassword, String storedPassword) {
        String expected = md5Hex(rawPassword + LEGACY_SALT);
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                storedPassword.toLowerCase().getBytes(StandardCharsets.UTF_8)
        );
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            return factory.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hash password", e);
        } finally {
            spec.clearPassword();
        }
    }

    private static String md5Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to calculate MD5", e);
        }
    }
}
