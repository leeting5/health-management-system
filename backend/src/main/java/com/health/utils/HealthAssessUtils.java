package com.health.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康评估工具类
 * 根据各项健康指标给出评估结果
 *
 * @author health-team
 */
public class HealthAssessUtils {

    /**
     * BMI分级标准（中国标准）
     */
    public static final String BMI_UNDERWEIGHT = "偏瘦";
    public static final String BMI_NORMAL = "正常";
    public static final String BMI_OVERWEIGHT = "超重";
    public static final String BMI_OBESE = "肥胖";

    /**
     * 血压分级标准
     */
    public static final String BP_LOW = "偏低";
    public static final String BP_NORMAL = "正常";
    public static final String BP_NORMAL_HIGH = "正常高值";
    public static final String BP_HIGH = "高血压";

    /**
     * 血糖分级标准（空腹）
     */
    public static final String BS_LOW = "偏低";
    public static final String BS_NORMAL = "正常";
    public static final String BS_IMPAIRED = "糖耐量受损";
    public static final String BS_HIGH = "糖尿病范围";

    /**
     * 心率分级标准
     */
    public static final String HR_LOW = "心动过缓";
    public static final String HR_NORMAL = "正常";
    public static final String HR_HIGH = "心动过速";

    /**
     * 计算BMI指数
     * BMI = 体重(kg) / 身高(m)²
     *
     * @param height 身高（cm）
     * @param weight 体重（kg）
     * @return BMI值（保留1位小数）
     */
    public static BigDecimal calculateBMI(BigDecimal height, BigDecimal weight) {
        if (height == null || weight == null || height.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        // 身高转换为米
        BigDecimal heightInMeter = height.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        // BMI = weight / (height * height)
        return weight.divide(heightInMeter.multiply(heightInMeter), 1, RoundingMode.HALF_UP);
    }

    /**
     * BMI评估
     *
     * @param bmi BMI值
     * @return 评估结果：level(等级), description(描述), suggestion(建议)
     */
    public static Map<String, String> assessBMI(BigDecimal bmi) {
        Map<String, String> result = new HashMap<>();
        if (bmi == null || bmi.compareTo(BigDecimal.ZERO) <= 0) {
            result.put("level", "未知");
            result.put("description", "数据不足");
            result.put("suggestion", "请先记录身高体重数据");
            return result;
        }

        double bmiValue = bmi.doubleValue();
        if (bmiValue < 18.5) {
            result.put("level", BMI_UNDERWEIGHT);
            result.put("description", "您的体重偏轻");
            result.put("suggestion", "建议增加营养摄入，适当进行力量训练增加肌肉量");
        } else if (bmiValue < 24.0) {
            result.put("level", BMI_NORMAL);
            result.put("description", "您的体重在正常范围内");
            result.put("suggestion", "继续保持健康的饮食和运动习惯");
        } else if (bmiValue < 28.0) {
            result.put("level", BMI_OVERWEIGHT);
            result.put("description", "您的体重略微超重");
            result.put("suggestion", "建议控制饮食，增加有氧运动，每周至少150分钟中等强度运动");
        } else {
            result.put("level", BMI_OBESE);
            result.put("description", "您的体重属于肥胖范围");
            result.put("suggestion", "建议及时就医，制定科学的减重计划，关注血糖血压等相关指标");
        }
        return result;
    }

    /**
     * 血压评估
     *
     * @param systolic  收缩压
     * @param diastolic 舒张压
     * @return 评估结果
     */
    public static Map<String, String> assessBloodPressure(Integer systolic, Integer diastolic) {
        Map<String, String> result = new HashMap<>();
        if (systolic == null || diastolic == null) {
            result.put("level", "未知");
            result.put("description", "数据不足");
            result.put("suggestion", "请先记录血压数据");
            return result;
        }

        if (systolic < 90 || diastolic < 60) {
            result.put("level", BP_LOW);
            result.put("description", "血压偏低");
            result.put("suggestion", "建议适当增加盐分摄入，多喝水，避免突然站立引起头晕");
        } else if (systolic < 120 && diastolic < 80) {
            result.put("level", BP_NORMAL);
            result.put("description", "血压正常");
            result.put("suggestion", "继续保持健康生活方式，定期监测血压");
        } else if (systolic < 140 && diastolic < 90) {
            result.put("level", BP_NORMAL_HIGH);
            result.put("description", "血压处于正常高值");
            result.put("suggestion", "建议低盐饮食，控制体重，增加运动，定期监测血压变化");
        } else {
            result.put("level", BP_HIGH);
            result.put("description", "血压偏高，可能患有高血压");
            result.put("suggestion", "建议及时就医确诊，遵医嘱服药，严格控制盐的摄入（每日<5g）");
        }
        return result;
    }

    /**
     * 血糖评估（空腹血糖）
     *
     * @param bloodSugar 血糖值（mmol/L）
     * @return 评估结果
     */
    public static Map<String, String> assessBloodSugar(BigDecimal bloodSugar) {
        Map<String, String> result = new HashMap<>();
        if (bloodSugar == null) {
            result.put("level", "未知");
            result.put("description", "数据不足");
            result.put("suggestion", "请先记录血糖数据");
            return result;
        }

        double bsValue = bloodSugar.doubleValue();
        if (bsValue < 3.9) {
            result.put("level", BS_LOW);
            result.put("description", "血糖偏低");
            result.put("suggestion", "注意规律饮食，随身携带糖果以备低血糖时食用");
        } else if (bsValue < 6.1) {
            result.put("level", BS_NORMAL);
            result.put("description", "血糖正常");
            result.put("suggestion", "继续保持健康饮食习惯，控制精制糖摄入");
        } else if (bsValue < 7.0) {
            result.put("level", BS_IMPAIRED);
            result.put("description", "空腹血糖受损（糖尿病前期）");
            result.put("suggestion", "建议控制饮食，减少碳水化合物摄入，增加运动，定期复查");
        } else {
            result.put("level", BS_HIGH);
            result.put("description", "血糖偏高，可能患有糖尿病");
            result.put("suggestion", "建议及时就医进行OGTT检查确诊，控制饮食，遵医嘱治疗");
        }
        return result;
    }

    /**
     * 心率评估
     *
     * @param heartRate 心率（次/分钟）
     * @return 评估结果
     */
    public static Map<String, String> assessHeartRate(Integer heartRate) {
        Map<String, String> result = new HashMap<>();
        if (heartRate == null) {
            result.put("level", "未知");
            result.put("description", "数据不足");
            result.put("suggestion", "请先记录心率数据");
            return result;
        }

        if (heartRate < 60) {
            result.put("level", HR_LOW);
            result.put("description", "心率偏慢");
            result.put("suggestion", "如为运动员可能是正常现象，如有头晕乏力等症状建议就医检查");
        } else if (heartRate <= 100) {
            result.put("level", HR_NORMAL);
            result.put("description", "心率正常");
            result.put("suggestion", "继续保持规律作息，适度运动增强心肺功能");
        } else {
            result.put("level", HR_HIGH);
            result.put("description", "心率偏快");
            result.put("suggestion", "注意休息，避免剧烈运动和情绪激动，如持续偏快建议就医");
        }
        return result;
    }

    /**
     * 综合健康评估
     *
     * @param bmi       BMI
     * @param systolic  收缩压
     * @param diastolic 舒张压
     * @param bloodSugar 血糖
     * @param heartRate 心率
     * @return 综合评估结果
     */
    public static Map<String, Object> comprehensiveAssess(BigDecimal bmi,
                                                          Integer systolic,
                                                          Integer diastolic,
                                                          BigDecimal bloodSugar,
                                                          Integer heartRate) {
        Map<String, Object> result = new HashMap<>();
        result.put("bmi", assessBMI(bmi));
        result.put("bloodPressure", assessBloodPressure(systolic, diastolic));
        result.put("bloodSugar", assessBloodSugar(bloodSugar));
        result.put("heartRate", assessHeartRate(heartRate));

        // 计算健康评分（简单加权）
        int score = 100;
        if (bmi != null) {
            double bmiValue = bmi.doubleValue();
            if (bmiValue < 18.5 || bmiValue >= 28) score -= 15;
            else if (bmiValue >= 24) score -= 8;
        }
        if (systolic != null && diastolic != null) {
            if (systolic >= 140 || diastolic >= 90) score -= 20;
            else if (systolic >= 120 || diastolic >= 80) score -= 10;
            else if (systolic < 90 || diastolic < 60) score -= 10;
        }
        if (bloodSugar != null) {
            double bs = bloodSugar.doubleValue();
            if (bs >= 7.0) score -= 20;
            else if (bs >= 6.1) score -= 10;
            else if (bs < 3.9) score -= 10;
        }
        if (heartRate != null) {
            if (heartRate > 100 || heartRate < 60) score -= 10;
        }

        result.put("totalScore", Math.max(score, 0));
        result.put("overallLevel", score >= 85 ? "优秀" : score >= 70 ? "良好" : score >= 60 ? "一般" : "需关注");
        return result;
    }
}
