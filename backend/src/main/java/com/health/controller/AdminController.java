package com.health.controller;

import com.health.annotation.OperationLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.health.common.BusinessException;
import com.health.dto.AnnouncementRequest;
import com.health.entity.Announcement;
import com.health.entity.HealthRecord;
import com.health.entity.Result;
import com.health.entity.User;
import com.health.service.AdminService;
import com.health.service.AnnouncementService;
import com.health.service.OperationLogService;
import com.health.utils.UserHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * 管理员接口
 *
 * @author health-team
 */
@RestController
@RequestMapping("/admin")
@Validated
public class AdminController {

    private static final String ADMIN_ROLE = "ADMIN";

    @Resource
    private AdminService adminService;

    @Resource
    private OperationLogService operationLogService;

    @Resource
    private AnnouncementService announcementService;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/users")
    @OperationLog("查看用户列表")
    public Result<IPage<User>> getUserPage(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码不能小于1") Integer pageNum,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量不能小于1")
            @Max(value = 100, message = "每页数量不能超过100") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        requireAdmin();
        IPage<User> page = adminService.getUserPage(pageNum, pageSize, keyword);
        page.getRecords().forEach(user -> user.setPassword(null));
        return Result.success(page);
    }

    /**
     * 查询指定用户的健康记录
     */
    @GetMapping("/users/{userId}/records")
    @OperationLog("查看用户健康记录")
    public Result<List<HealthRecord>> getUserRecords(@PathVariable Long userId) {
        requireAdmin();
        return Result.success(adminService.getUserRecords(userId));
    }

    /**
     * 分页查询操作日志
     */
    @GetMapping("/logs")
    @OperationLog("查看操作日志")
    public Result<IPage<com.health.entity.OperationLog>> getLogPage(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码不能小于1") Integer pageNum,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量不能小于1")
            @Max(value = 100, message = "每页数量不能超过100") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        requireAdmin();
        return Result.success(operationLogService.getLogPage(pageNum, pageSize, keyword));
    }

    /**
     * 分页查询公告
     */
    @GetMapping("/announcements")
    @OperationLog("查看公告列表")
    public Result<IPage<Announcement>> getAnnouncementPage(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码不能小于1") Integer pageNum,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量不能小于1")
            @Max(value = 100, message = "每页数量不能超过100") Integer pageSize) {
        requireAdmin();
        return Result.success(announcementService.getAnnouncementPage(pageNum, pageSize));
    }

    /**
     * 创建公告
     */
    @PostMapping("/announcements")
    @OperationLog("创建公告")
    public Result<Announcement> createAnnouncement(@Valid @RequestBody AnnouncementRequest request) {
        requireAdmin();
        String publisher = UserHolder.getUsername();
        return Result.success("公告创建成功", announcementService.createAnnouncement(request, publisher));
    }

    /**
     * 更新公告
     */
    @PutMapping("/announcements/{id}")
    @OperationLog("更新公告")
    public Result<Announcement> updateAnnouncement(@PathVariable Long id,
                                                   @Valid @RequestBody AnnouncementRequest request) {
        requireAdmin();
        String publisher = UserHolder.getUsername();
        return Result.success("公告更新成功", announcementService.updateAnnouncement(id, request, publisher));
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/announcements/{id}")
    @OperationLog("删除公告")
    public Result<String> deleteAnnouncement(@PathVariable Long id) {
        requireAdmin();
        announcementService.deleteAnnouncement(id);
        return Result.success("公告已删除");
    }

    private void requireAdmin() {
        if (!ADMIN_ROLE.equals(UserHolder.getRole())) {
            throw BusinessException.forbidden("需要管理员权限");
        }
    }
}
