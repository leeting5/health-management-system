package com.health.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.health.dto.AnnouncementRequest;
import com.health.entity.Announcement;

import java.util.List;

/**
 * 公告服务接口
 *
 * @author health-team
 */
public interface AnnouncementService {

    IPage<Announcement> getAnnouncementPage(Integer pageNum, Integer pageSize);

    Announcement createAnnouncement(AnnouncementRequest request, String publisher);

    Announcement updateAnnouncement(Long id, AnnouncementRequest request, String publisher);

    void deleteAnnouncement(Long id);

    List<Announcement> getActiveAnnouncements();
}
