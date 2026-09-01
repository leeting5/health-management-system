import request from '@/utils/request'

// 获取已发布公告
export function getActiveAnnouncements() {
  return request({
    url: '/announcements/active',
    method: 'get'
  })
}
