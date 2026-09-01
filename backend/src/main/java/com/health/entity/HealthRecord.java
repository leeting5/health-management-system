package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 健康记录实体类
 * 记录用户的各项健康指标数据
 *
 * @author health-team
 */
@Data
@TableName("health_record")
public class HealthRecord {

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 记录日期
     */
    @NotNull(message = "记录日期不能为空")
    private LocalDate recordDate;

    /**
     * 身高（cm）
     */
    @DecimalMin(value = "30", message = "身高不能低于30cm")
    @DecimalMax(value = "300", message = "身高不能高于300cm")
    private BigDecimal height;

    /**
     * 体重（kg）
     */
    @DecimalMin(value = "1", message = "体重不能低于1kg")
    @DecimalMax(value = "500", message = "体重不能高于500kg")
    private BigDecimal weight;

    /**
     * BMI指数（自动计算）
     */
    private BigDecimal bmi;

    /**
     * 收缩压（mmHg）
     */
    @Min(value = 40, message = "收缩压数值不合理")
    @Max(value = 300, message = "收缩压数值不合理")
    private Integer systolicPressure;

    /**
     * 舒张压（mmHg）
     */
    @Min(value = 20, message = "舒张压数值不合理")
    @Max(value = 200, message = "舒张压数值不合理")
    private Integer diastolicPressure;

    /**
     * 血糖（mmol/L）
     */
    @DecimalMin(value = "0.1", message = "血糖数值不合理")
    @DecimalMax(value = "50", message = "血糖数值不合理")
    private BigDecimal bloodSugar;

    /**
     * 心率（次/分钟）
     */
    @Min(value = 20, message = "心率数值不合理")
    @Max(value = 300, message = "心率数值不合理")
    private Integer heartRate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;
}
