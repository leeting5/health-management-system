import request from '@/utils/request'

// 健康记录相关 API

// 新增健康记录
export function addHealthRecord(data) {
  return request({
    url: '/health/record',
    method: 'post',
    data
  })
}

// 更新健康记录
export function updateHealthRecord(data) {
  return request({
    url: '/health/record',
    method: 'put',
    data
  })
}

// 删除健康记录
export function deleteHealthRecord(id) {
  return request({
    url: `/health/record/${id}`,
    method: 'delete'
  })
}

// 获取记录详情
export function getHealthRecordDetail(id) {
  return request({
    url: `/health/record/${id}`,
    method: 'get'
  })
}

// 分页查询记录列表
export function getHealthRecordList(params) {
  return request({
    url: '/health/record/list',
    method: 'get',
    params
  })
}

// 获取健康评估结果
export function getHealthAssessment() {
  return request({
    url: '/health/record/assessment',
    method: 'get'
  })
}

// 获取趋势数据
export function getTrendData(days) {
  return request({
    url: '/health/record/trend',
    method: 'get',
    params: { days }
  })
}

// 获取最近记录
export function getRecentRecords(days) {
  return request({
    url: '/health/record/recent',
    method: 'get',
    params: { days }
  })
}
