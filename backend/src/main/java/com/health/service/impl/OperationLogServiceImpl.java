package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.entity.OperationLog;
import com.health.mapper.OperationLogMapper;
import com.health.service.OperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * 操作日志服务实现类
 *
 * @author health-team
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Resource
    private OperationLogMapper operationLogMapper;

    @Override
    public void record(OperationLog operationLog) {
        operationLogMapper.insert(operationLog);
    }

    @Override
    public IPage<OperationLog> getLogPage(Integer pageNum, Integer pageSize, String keyword) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(query -> query
                    .like(OperationLog::getUsername, keyword)
                    .or()
                    .like(OperationLog::getOperation, keyword)
                    .or()
                    .like(OperationLog::getPath, keyword));
        }
        wrapper.orderByDesc(OperationLog::getCreateTime);
        return operationLogMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }
}
