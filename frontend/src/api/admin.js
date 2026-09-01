import request from '@/utils/request'

// 分页查询用户
export function getAdminUsers(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

// 查询指定用户的健康记录
export function getAdminUserRecords(userId) {
  return request({
    url: `/admin/users/${userId}/records`,
    method: 'get'
  })
}

// 分页查询操作日志
export function getAdminLogs(params) {
  return request({
    url: '/admin/logs',
    method: 'get',
    params
  })
}

// 分页查询公告
export function getAdminAnnouncements(params) {
  return request({
    url: '/admin/announcements',
    method: 'get',
    params
  })
}

// 创建公告
export function createAnnouncement(data) {
  return request({
    url: '/admin/announcements',
    method: 'post',
    data
  })
}

// 更新公告
export function updateAnnouncement(id, data) {
  return request({
    url: `/admin/announcements/${id}`,
    method: 'put',
    data
  })
}

// 删除公告
export function deleteAnnouncement(id) {
  return request({
    url: `/admin/announcements/${id}`,
    method: 'delete'
  })
}
