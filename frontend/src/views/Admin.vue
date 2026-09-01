<template>
  <div class="admin-page">
    <el-card class="admin-card" shadow="never">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="用户管理" name="users">
          <div class="toolbar">
            <div class="search-box">
              <el-input
                v-model="userKeyword"
                placeholder="搜索用户名或昵称"
                clearable
                @keyup.enter="handleUserSearch"
                @clear="handleUserSearch"
              />
              <el-button type="primary" @click="handleUserSearch">搜索</el-button>
            </div>
          </div>

          <el-table v-loading="userLoading" :data="users" stripe>
            <el-table-column prop="id" label="ID" width="70" />
            <el-table-column prop="username" label="用户名" min-width="120" />
            <el-table-column prop="nickname" label="昵称" min-width="120" />
            <el-table-column label="性别" width="80">
              <template #default="{ row }">{{ genderText(row.gender) }}</template>
            </el-table-column>
            <el-table-column label="年龄" width="80">
              <template #default="{ row }">{{ row.age || '-' }}</template>
            </el-table-column>
            <el-table-column prop="phone" label="手机号" min-width="130">
              <template #default="{ row }">{{ row.phone || '-' }}</template>
            </el-table-column>
            <el-table-column label="角色" width="110">
              <template #default="{ row }">
                <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'" effect="plain">
                  {{ row.role === 'ADMIN' ? '管理员' : '普通用户' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="注册时间" min-width="160" />
            <el-table-column label="操作" width="110" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="viewRecords(row)">查看记录</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination">
            <el-pagination
              v-model:current-page="userPageNum"
              v-model:page-size="userPageSize"
              :page-sizes="[10, 20, 50]"
              :total="userTotal"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="fetchUsers"
              @current-change="fetchUsers"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="操作日志" name="logs">
          <div class="toolbar">
            <div class="search-box">
              <el-input
                v-model="logKeyword"
                placeholder="搜索用户、操作或路径"
                clearable
                @keyup.enter="handleLogSearch"
                @clear="handleLogSearch"
              />
              <el-button type="primary" @click="handleLogSearch">搜索</el-button>
            </div>
          </div>

          <el-table v-loading="logLoading" :data="logs" stripe>
            <el-table-column prop="createTime" label="操作时间" min-width="160" />
            <el-table-column prop="username" label="用户" min-width="110" />
            <el-table-column prop="operation" label="操作" min-width="130" />
            <el-table-column prop="method" label="方法" width="80" />
            <el-table-column prop="path" label="路径" min-width="220" show-overflow-tooltip />
            <el-table-column prop="ip" label="IP" min-width="120" />
            <el-table-column label="结果" width="80">
              <template #default="{ row }">
                <el-tag :type="row.success === 1 ? 'success' : 'danger'" effect="plain">
                  {{ row.success === 1 ? '成功' : '失败' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination">
            <el-pagination
              v-model:current-page="logPageNum"
              v-model:page-size="logPageSize"
              :page-sizes="[10, 20, 50]"
              :total="logTotal"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="fetchLogs"
              @current-change="fetchLogs"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="公告管理" name="announcements">
          <div class="toolbar toolbar-between">
            <div class="search-box">
              <el-button type="primary" @click="openAnnouncementDialog()">新建公告</el-button>
            </div>
          </div>

          <el-table v-loading="announcementLoading" :data="announcements" stripe>
            <el-table-column prop="id" label="ID" width="70" />
            <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="plain">
                  {{ row.status === 1 ? '已发布' : '草稿' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="publisher" label="发布人" width="120" />
            <el-table-column prop="createTime" label="创建时间" min-width="160" />
            <el-table-column label="操作" width="160" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="openAnnouncementDialog(row)">编辑</el-button>
                <el-button type="danger" link @click="handleDeleteAnnouncement(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination">
            <el-pagination
              v-model:current-page="announcementPageNum"
              v-model:page-size="announcementPageSize"
              :page-sizes="[10, 20, 50]"
              :total="announcementTotal"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="fetchAnnouncements"
              @current-change="fetchAnnouncements"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-drawer
      v-model="drawerVisible"
      :title="`${currentUser?.nickname || currentUser?.username || ''} 的健康记录`"
      size="62%"
    >
      <el-table v-loading="recordsLoading" :data="records" stripe>
        <el-table-column prop="recordDate" label="日期" width="120" />
        <el-table-column prop="bmi" label="BMI" width="90" />
        <el-table-column label="血压" width="110">
          <template #default="{ row }">
            {{ row.systolicPressure || '-' }}/{{ row.diastolicPressure || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="bloodSugar" label="血糖" />
        <el-table-column prop="heartRate" label="心率" />
        <el-table-column prop="remark" label="备注" min-width="140" show-overflow-tooltip />
      </el-table>
    </el-drawer>

    <el-dialog
      v-model="announcementDialogVisible"
      :title="announcementForm.id ? '编辑公告' : '新建公告'"
      width="560px"
      @closed="resetAnnouncementForm"
    >
      <el-form
        ref="announcementFormRef"
        :model="announcementForm"
        :rules="announcementRules"
        label-position="top"
      >
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="announcementForm.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input
            v-model="announcementForm.content"
            type="textarea"
            :rows="5"
            placeholder="请输入公告内容"
          />
        </el-form-item>
        <el-form-item label="发布状态" prop="status">
          <el-radio-group v-model="announcementForm.status">
            <el-radio :label="1">立即发布</el-radio>
            <el-radio :label="0">保存为草稿</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="announcementDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="announcementSaving" @click="saveAnnouncement">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminUsers,
  getAdminUserRecords,
  getAdminLogs,
  getAdminAnnouncements,
  createAnnouncement,
  updateAnnouncement,
  deleteAnnouncement
} from '@/api/admin'

const activeTab = ref('users')

const userLoading = ref(false)
const userKeyword = ref('')
const userPageNum = ref(1)
const userPageSize = ref(10)
const userTotal = ref(0)
const users = ref([])

const logLoading = ref(false)
const logKeyword = ref('')
const logPageNum = ref(1)
const logPageSize = ref(10)
const logTotal = ref(0)
const logs = ref([])

const announcementLoading = ref(false)
const announcementPageNum = ref(1)
const announcementPageSize = ref(10)
const announcementTotal = ref(0)
const announcements = ref([])

const recordsLoading = ref(false)
const drawerVisible = ref(false)
const records = ref([])
const currentUser = ref(null)

const announcementDialogVisible = ref(false)
const announcementSaving = ref(false)
const announcementFormRef = ref(null)
const announcementForm = reactive({
  id: null,
  title: '',
  content: '',
  status: 1
})

const announcementRules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
}

const genderText = (gender) => {
  if (gender === 1) return '男'
  if (gender === 2) return '女'
  return '保密'
}

const handleTabChange = (name) => {
  if (name === 'logs') fetchLogs()
  if (name === 'announcements') fetchAnnouncements()
}

const fetchUsers = async () => {
  userLoading.value = true
  try {
    const res = await getAdminUsers({
      pageNum: userPageNum.value,
      pageSize: userPageSize.value,
      keyword: userKeyword.value || undefined
    })
    users.value = res.data?.records || []
    userTotal.value = res.data?.total || 0
  } finally {
    userLoading.value = false
  }
}

const handleUserSearch = () => {
  userPageNum.value = 1
  fetchUsers()
}

const fetchLogs = async () => {
  logLoading.value = true
  try {
    const res = await getAdminLogs({
      pageNum: logPageNum.value,
      pageSize: logPageSize.value,
      keyword: logKeyword.value || undefined
    })
    logs.value = res.data?.records || []
    logTotal.value = res.data?.total || 0
  } finally {
    logLoading.value = false
  }
}

const handleLogSearch = () => {
  logPageNum.value = 1
  fetchLogs()
}

const fetchAnnouncements = async () => {
  announcementLoading.value = true
  try {
    const res = await getAdminAnnouncements({
      pageNum: announcementPageNum.value,
      pageSize: announcementPageSize.value
    })
    announcements.value = res.data?.records || []
    announcementTotal.value = res.data?.total || 0
  } finally {
    announcementLoading.value = false
  }
}

const viewRecords = async (user) => {
  currentUser.value = user
  drawerVisible.value = true
  recordsLoading.value = true
  try {
    const res = await getAdminUserRecords(user.id)
    records.value = res.data || []
  } finally {
    recordsLoading.value = false
  }
}

const openAnnouncementDialog = (row) => {
  if (row) {
    announcementForm.id = row.id
    announcementForm.title = row.title
    announcementForm.content = row.content
    announcementForm.status = row.status
  }
  announcementDialogVisible.value = true
}

const resetAnnouncementForm = () => {
  announcementForm.id = null
  announcementForm.title = ''
  announcementForm.content = ''
  announcementForm.status = 1
  announcementFormRef.value?.resetFields()
}

const saveAnnouncement = async () => {
  try {
    await announcementFormRef.value.validate()
    announcementSaving.value = true
    const data = {
      title: announcementForm.title,
      content: announcementForm.content,
      status: announcementForm.status
    }
    if (announcementForm.id) {
      await updateAnnouncement(announcementForm.id, data)
      ElMessage.success('公告更新成功')
    } else {
      await createAnnouncement(data)
      ElMessage.success('公告创建成功')
    }
    announcementDialogVisible.value = false
    fetchAnnouncements()
  } finally {
    announcementSaving.value = false
  }
}

const handleDeleteAnnouncement = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该公告吗？', '删除公告', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
    await deleteAnnouncement(row.id)
    ElMessage.success('公告已删除')
    fetchAnnouncements()
  } catch (err) {
    if (err !== 'cancel' && err !== 'close') console.error('删除公告失败:', err)
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped lang="scss">
.admin-page {
  .admin-card {
    border: 1px solid #eef0f4;
    border-radius: 16px;

    :deep(.el-card__body) {
      padding: 24px;
    }
  }

  .toolbar {
    margin-bottom: 16px;
  }

  .search-box {
    display: flex;
    gap: 10px;
    width: 320px;
  }

  .pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
}

@media (max-width: 768px) {
  .search-box {
    width: 100%;
  }
}
</style>
