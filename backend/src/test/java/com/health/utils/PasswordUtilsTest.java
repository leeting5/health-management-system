package com.health.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 密码工具类单元测试
 */
class PasswordUtilsTest {

    @Test
    void encodeAndMatchesShouldReturnTrue() {
        String encoded = PasswordUtils.encode("123456");
        assertTrue(PasswordUtils.matches("123456", encoded));
        assertFalse(PasswordUtils.matches("654321", encoded));
        assertFalse(PasswordUtils.isLegacyHash(encoded));
    }

    @Test
    void legacyMd5HashShouldStillMatch() {
        String legacyMd5 = "742852cc9915b9a6957b5351719fa3b4";
        assertTrue(PasswordUtils.matches("123456", legacyMd5));
        assertTrue(PasswordUtils.isLegacyHash(legacyMd5));
    }
}
