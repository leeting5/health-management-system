package com.health.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 健康评估工具类单元测试
 */
class HealthAssessUtilsTest {

    @Test
    void calculateBmiShouldReturnExpectedValue() {
        BigDecimal bmi = HealthAssessUtils.calculateBMI(
                new BigDecimal("175"), new BigDecimal("68"));
        assertEquals(new BigDecimal("22.2"), bmi);
    }

    @Test
    void assessBmiShouldReturnNormalForHealthyValue() {
        String level = HealthAssessUtils.assessBMI(new BigDecimal("22.2")).get("level");
        assertEquals("正常", level);
    }

    @Test
    void assessBloodPressureShouldReturnNormal() {
        String level = HealthAssessUtils.assessBloodPressure(118, 78).get("level");
        assertEquals("正常", level);
    }

    @Test
    void comprehensiveAssessShouldReturnPositiveScore() {
        int score = (int) HealthAssessUtils.comprehensiveAssess(
                new BigDecimal("22.2"), 118, 78, new BigDecimal("5.2"), 72)
                .get("totalScore");
        assertTrue(score >= 90);
    }
}
