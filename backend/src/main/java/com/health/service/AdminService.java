package com.health.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.health.entity.HealthRecord;
import com.health.entity.User;

import java.util.List;

/**
 * 管理员服务接口
 *
 * @author health-team
 */
public interface AdminService {

    /**
     * 分页查询用户列表
     */
    IPage<User> getUserPage(Integer pageNum, Integer pageSize, String keyword);

    /**
     * 查询指定用户的健康记录
     */
    List<HealthRecord> getUserRecords(Long userId);
}
