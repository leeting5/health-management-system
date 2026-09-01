package com.health.controller;

import com.health.annotation.OperationLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.health.entity.HealthRecord;
import com.health.entity.Result;
import com.health.service.HealthRecordService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Map;

/**
 * 健康记录控制器
 * 处理健康数据的增删改查、统计分析等接口
 *
 * @author health-team
 */
@RestController
@RequestMapping("/health/record")
@Validated
public class HealthRecordController {

    @Resource
    private HealthRecordService healthRecordService;

    /**
     * 新增健康记录
     *
     * @param record 健康记录数据
     * @return 新增结果
     */
    @PostMapping
    @OperationLog("新增健康记录")
    public Result<String> addRecord(@Valid @RequestBody HealthRecord record) {
        boolean success = healthRecordService.addRecord(record);
        return success ? Result.success("添加成功") : Result.fail("添加失败");
    }

    /**
     * 更新健康记录
     *
     * @param record 健康记录数据
     * @return 更新结果
     */
    @PutMapping
    @OperationLog("更新健康记录")
    public Result<String> updateRecord(@Valid @RequestBody HealthRecord record) {
        boolean success = healthRecordService.updateRecord(record);
        return success ? Result.success("更新成功") : Result.fail("更新失败");
    }

    /**
     * 删除健康记录
     *
     * @param id 记录ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @OperationLog("删除健康记录")
    public Result<String> deleteRecord(@PathVariable Long id) {
        boolean success = healthRecordService.deleteRecord(id);
        return success ? Result.success("删除成功") : Result.fail("删除失败");
    }

    /**
     * 获取记录详情
     *
     * @param id 记录ID
     * @return 记录详情
     */
    @GetMapping("/{id}")
    public Result<HealthRecord> getRecordDetail(@PathVariable Long id) {
        HealthRecord record = healthRecordService.getRecordById(id);
        return Result.success(record);
    }

    /**
     * 分页查询记录列表
     *
     * @param pageNum  页码，默认1
     * @param pageSize 每页数量，默认10
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<IPage<HealthRecord>> getRecordList(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码不能小于1") Integer pageNum,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量不能小于1")
            @Max(value = 100, message = "每页数量不能超过100") Integer pageSize) {
        IPage<HealthRecord> page = healthRecordService.getRecordPage(pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 获取健康评估结果
     *
     * @return 评估结果
     */
    @GetMapping("/assessment")
    public Result<Map<String, Object>> getHealthAssessment() {
        Map<String, Object> assessment = healthRecordService.getHealthAssessment();
        return Result.success(assessment);
    }

    /**
     * 获取趋势数据（用于ECharts图表）
     *
     * @param days 最近天数，默认30天
     * @return 趋势数据
     */
    @GetMapping("/trend")
    public Result<Map<String, Object>> getTrendData(
            @RequestParam(defaultValue = "30") @Min(value = 1, message = "查询天数不能小于1")
            @Max(value = 365, message = "查询天数不能超过365") Integer days) {
        Map<String, Object> trendData = healthRecordService.getTrendData(days);
        return Result.success(trendData);
    }

    /**
     * 获取最近N条记录
     *
     * @param days 最近天数
     * @return 记录列表
     */
    @GetMapping("/recent")
    public Result<List<HealthRecord>> getRecentRecords(
            @RequestParam(defaultValue = "7") @Min(value = 1, message = "查询天数不能小于1")
            @Max(value = 365, message = "查询天数不能超过365") Integer days) {
        List<HealthRecord> records = healthRecordService.getRecordsByDays(days);
        return Result.success(records);
    }
}
