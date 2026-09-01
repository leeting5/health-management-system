package com.health.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.health.entity.HealthRecord;

import java.util.List;
import java.util.Map;

/**
 * 健康记录服务接口
 *
 * @author health-team
 */
public interface HealthRecordService extends IService<HealthRecord> {

    /**
     * 新增健康记录
     *
     * @param record 健康记录
     * @return 是否成功
     */
    boolean addRecord(HealthRecord record);

    /**
     * 更新健康记录
     *
     * @param record 健康记录
     * @return 是否成功
     */
    boolean updateRecord(HealthRecord record);

    /**
     * 删除健康记录
     *
     * @param id 记录ID
     * @return 是否成功
     */
    boolean deleteRecord(Long id);

    /**
     * 获取健康记录详情
     *
     * @param id 记录ID
     * @return 健康记录
     */
    HealthRecord getRecordById(Long id);

    /**
     * 分页查询健康记录
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    IPage<HealthRecord> getRecordPage(Integer pageNum, Integer pageSize);

    /**
     * 获取指定日期范围内的记录
     *
     * @param days 最近天数
     * @return 健康记录列表
     */
    List<HealthRecord> getRecordsByDays(Integer days);

    /**
     * 获取健康评估结果（基于最新记录）
     *
     * @return 评估结果
     */
    Map<String, Object> getHealthAssessment();

    /**
     * 获取趋势数据（用于图表）
     *
     * @param days 最近天数
     * @return 趋势数据
     */
    Map<String, Object> getTrendData(Integer days);
}
