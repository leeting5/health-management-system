package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.health.common.BusinessException;
import com.health.entity.HealthRecord;
import com.health.mapper.HealthRecordMapper;
import com.health.service.HealthRecordService;
import com.health.utils.HealthAssessUtils;
import com.health.utils.UserHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * 健康记录服务实现类
 *
 * @author health-team
 */
@Service
public class HealthRecordServiceImpl extends ServiceImpl<HealthRecordMapper, HealthRecord> implements HealthRecordService {

    @Override
    public boolean addRecord(HealthRecord record) {
        Long userId = UserHolder.getUserId();
        if (userId == null) {
            throw BusinessException.unauthorized("用户未登录");
        }

        HealthRecord recordToSave = new HealthRecord();
        recordToSave.setUserId(userId);
        recordToSave.setRecordDate(record.getRecordDate() != null ? record.getRecordDate() : LocalDate.now());
        recordToSave.setHeight(record.getHeight());
        recordToSave.setWeight(record.getWeight());
        recordToSave.setSystolicPressure(record.getSystolicPressure());
        recordToSave.setDiastolicPressure(record.getDiastolicPressure());
        recordToSave.setBloodSugar(record.getBloodSugar());
        recordToSave.setHeartRate(record.getHeartRate());
        recordToSave.setRemark(record.getRemark());

        // 自动计算BMI
        if (record.getHeight() != null && record.getWeight() != null) {
            recordToSave.setBmi(HealthAssessUtils.calculateBMI(record.getHeight(), record.getWeight()));
        }

        return save(recordToSave);
    }

    @Override
    public boolean updateRecord(HealthRecord record) {
        Long userId = UserHolder.getUserId();
        if (userId == null) {
            throw BusinessException.unauthorized("用户未登录");
        }
        if (record.getId() == null) {
            throw BusinessException.badRequest("记录ID不能为空");
        }

        // 验证记录归属
        HealthRecord existRecord = getById(record.getId());
        if (existRecord == null) {
            throw BusinessException.notFound("记录不存在");
        }
        if (!existRecord.getUserId().equals(userId)) {
            throw BusinessException.forbidden("无权限修改此记录");
        }

        HealthRecord update = new HealthRecord();
        update.setId(record.getId());
        update.setRecordDate(record.getRecordDate());
        update.setHeight(record.getHeight());
        update.setWeight(record.getWeight());
        update.setSystolicPressure(record.getSystolicPressure());
        update.setDiastolicPressure(record.getDiastolicPressure());
        update.setBloodSugar(record.getBloodSugar());
        update.setHeartRate(record.getHeartRate());
        update.setRemark(record.getRemark());

        // 自动计算BMI
        if (record.getHeight() != null && record.getWeight() != null) {
            update.setBmi(HealthAssessUtils.calculateBMI(record.getHeight(), record.getWeight()));
        }

        return updateById(update);
    }

    @Override
    public boolean deleteRecord(Long id) {
        Long userId = UserHolder.getUserId();
        if (userId == null) {
            throw BusinessException.unauthorized("用户未登录");
        }

        HealthRecord record = getById(id);
        if (record == null) {
            throw BusinessException.notFound("记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw BusinessException.forbidden("无权限删除此记录");
        }

        return removeById(id);
    }

    @Override
    public HealthRecord getRecordById(Long id) {
        Long userId = UserHolder.getUserId();
        if (userId == null) {
            throw BusinessException.unauthorized("用户未登录");
        }

        HealthRecord record = getById(id);
        if (record == null) {
            throw BusinessException.notFound("记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw BusinessException.forbidden("无权限查看此记录");
        }
        return record;
    }

    @Override
    public IPage<HealthRecord> getRecordPage(Integer pageNum, Integer pageSize) {
        Long userId = UserHolder.getUserId();
        if (userId == null) {
            throw BusinessException.unauthorized("用户未登录");
        }

        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthRecord::getUserId, userId);
        wrapper.orderByDesc(HealthRecord::getRecordDate);
        wrapper.orderByDesc(HealthRecord::getCreateTime);

        Page<HealthRecord> page = new Page<>(pageNum, pageSize);
        return page(page, wrapper);
    }

    @Override
    public List<HealthRecord> getRecordsByDays(Integer days) {
        Long userId = UserHolder.getUserId();
        if (userId == null) {
            throw BusinessException.unauthorized("用户未登录");
        }

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        return baseMapper.selectByDateRange(userId, startDate, endDate);
    }

    @Override
    public Map<String, Object> getHealthAssessment() {
        Long userId = UserHolder.getUserId();
        if (userId == null) {
            throw BusinessException.unauthorized("用户未登录");
        }

        // 获取最新一条记录
        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthRecord::getUserId, userId);
        wrapper.orderByDesc(HealthRecord::getRecordDate);
        wrapper.last("LIMIT 1");
        HealthRecord latestRecord = getOne(wrapper);

        if (latestRecord == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("hasData", false);
            result.put("message", "暂无健康数据，请先添加记录");
            return result;
        }

        // 进行综合评估
        Map<String, Object> assessment = HealthAssessUtils.comprehensiveAssess(
                latestRecord.getBmi(),
                latestRecord.getSystolicPressure(),
                latestRecord.getDiastolicPressure(),
                latestRecord.getBloodSugar(),
                latestRecord.getHeartRate()
        );

        assessment.put("hasData", true);
        assessment.put("latestRecord", latestRecord);
        assessment.put("recordDate", latestRecord.getRecordDate());
        return assessment;
    }

    @Override
    public Map<String, Object> getTrendData(Integer days) {
        List<HealthRecord> records = getRecordsByDays(days);

        Map<String, Object> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<BigDecimal> bmiList = new ArrayList<>();
        List<Integer> systolicList = new ArrayList<>();
        List<Integer> diastolicList = new ArrayList<>();
        List<BigDecimal> bloodSugarList = new ArrayList<>();
        List<Integer> heartRateList = new ArrayList<>();

        for (HealthRecord record : records) {
            dates.add(record.getRecordDate().toString());
            bmiList.add(record.getBmi());
            systolicList.add(record.getSystolicPressure());
            diastolicList.add(record.getDiastolicPressure());
            bloodSugarList.add(record.getBloodSugar());
            heartRateList.add(record.getHeartRate());
        }

        result.put("dates", dates);
        result.put("bmi", bmiList);
        result.put("systolicPressure", systolicList);
        result.put("diastolicPressure", diastolicList);
        result.put("bloodSugar", bloodSugarList);
        result.put("heartRate", heartRateList);
        return result;
    }
}
