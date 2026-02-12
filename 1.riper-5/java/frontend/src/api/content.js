import request from '@/utils/request'

/**
 * 查询内容列表
 * @param {string} status - 可选，筛选状态（DRAFT/PUBLISHED）
 */
export function getContents(status) {
  return request({
    url: '/contents',
    method: 'get',
    params: { status }
  })
}

/**
 * 查询单个内容
 */
export function getContent(id) {
  return request({
    url: `/contents/${id}`,
    method: 'get'
  })
}

/**
 * 创建内容
 */
export function createContent(data) {
  return request({
    url: '/contents',
    method: 'post',
    data
  })
}

/**
 * 更新内容
 */
export function updateContent(id, data) {
  return request({
    url: `/contents/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除内容
 */
export function deleteContent(id) {
  return request({
    url: `/contents/${id}`,
    method: 'delete'
  })
}

/**
 * 发布内容
 */
export function publishContent(id) {
  return request({
    url: `/contents/${id}/publish`,
    method: 'put'
  })
}

/**
 * 取消发布内容
 */
export function unpublishContent(id) {
  return request({
    url: `/contents/${id}/unpublish`,
    method: 'put'
  })
}

/**
 * 查询已发布内容列表（前台使用）
 */
export function getPublishedContents() {
  return request({
    url: '/frontend/contents',
    method: 'get'
  })
}

/**
 * 查询已发布内容详情（前台使用）
 */
export function getPublishedContent(id) {
  return request({
    url: `/frontend/contents/${id}`,
    method: 'get'
  })
}
