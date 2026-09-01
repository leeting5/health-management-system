package com.health.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.health.entity.OperationLog;

/**
 * 操作日志服务接口
 *
 * @author health-team
 */
public interface OperationLogService {

    void record(OperationLog operationLog);

    IPage<OperationLog> getLogPage(Integer pageNum, Integer pageSize, String keyword);
}
