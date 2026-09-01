package com.health.controller;

import com.health.entity.Announcement;
import com.health.entity.Result;
import com.health.service.AnnouncementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 公告公开接口
 *
 * @author health-team
 */
@RestController
@RequestMapping("/announcements")
public class AnnouncementController {

    @Resource
    private AnnouncementService announcementService;

    /**
     * 获取已发布公告
     */
    @GetMapping("/active")
    public Result<List<Announcement>> getActiveAnnouncements() {
        return Result.success(announcementService.getActiveAnnouncements());
    }
}
