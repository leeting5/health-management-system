package com.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.health.entity.HealthRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 健康记录数据访问层
 *
 * @author health-team
 */
@Mapper
public interface HealthRecordMapper extends BaseMapper<HealthRecord> {

    /**
     * 查询指定日期范围内的记录（按日期升序）
     *
     * @param userId    用户ID
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 健康记录列表
     */
    @Select("SELECT * FROM health_record WHERE user_id = #{userId} AND deleted = 0 " +
            "AND record_date BETWEEN #{startDate} AND #{endDate} ORDER BY record_date ASC")
    List<HealthRecord> selectByDateRange(@Param("userId") Long userId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    /**
     * 查询最近N条记录
     *
     * @param userId 用户ID
     * @param limit  数量
     * @return 健康记录列表
     */
    @Select("SELECT * FROM health_record WHERE user_id = #{userId} AND deleted = 0 " +
            "ORDER BY record_date DESC LIMIT #{limit}")
    List<HealthRecord> selectRecentRecords(@Param("userId") Long userId,
                                           @Param("limit") Integer limit);
}
