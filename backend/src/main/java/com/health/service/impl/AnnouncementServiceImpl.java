package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.common.BusinessException;
import com.health.entity.Announcement;
import com.health.mapper.AnnouncementMapper;
import com.health.service.AnnouncementService;
import com.health.dto.AnnouncementRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 公告服务实现类
 *
 * @author health-team
 */
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Resource
    private AnnouncementMapper announcementMapper;

    @Override
    public IPage<Announcement> getAnnouncementPage(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Announcement::getCreateTime);
        return announcementMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public Announcement createAnnouncement(AnnouncementRequest request, String publisher) {
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle().trim());
        announcement.setContent(request.getContent().trim());
        announcement.setStatus(request.getStatus() == null ? 0 : request.getStatus());
        announcement.setPublisher(publisher);
        announcementMapper.insert(announcement);
        return announcement;
    }

    @Override
    public Announcement updateAnnouncement(Long id, AnnouncementRequest request, String publisher) {
        Announcement announcement = getById(id);
        announcement.setTitle(request.getTitle().trim());
        announcement.setContent(request.getContent().trim());
        announcement.setStatus(request.getStatus() == null ? announcement.getStatus() : request.getStatus());
        announcement.setPublisher(publisher);
        announcementMapper.updateById(announcement);
        return announcement;
    }

    @Override
    public void deleteAnnouncement(Long id) {
        getById(id);
        announcementMapper.deleteById(id);
    }

    @Override
    public List<Announcement> getActiveAnnouncements() {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getStatus, 1)
                .orderByDesc(Announcement::getCreateTime)
                .last("LIMIT 5");
        return announcementMapper.selectList(wrapper);
    }

    private Announcement getById(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw BusinessException.notFound("公告不存在");
        }
        return announcement;
    }
}
