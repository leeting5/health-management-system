<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <el-col :xs="24" :lg="8">
        <el-card class="profile-card" shadow="never">
          <div class="profile-cover"></div>
          <div class="profile-main">
            <el-avatar :size="92" class="profile-avatar">
              {{ avatarText }}
            </el-avatar>
            <h3>{{ userStore.username }}</h3>
            <p>{{ userInfo.nickname || '健康管理用户' }}</p>
            <div class="profile-tags">
              <el-tag size="small" effect="plain">{{ genderText }}</el-tag>
              <el-tag size="small" effect="plain">
                {{ userInfo.age ? `${userInfo.age} 岁` : '年龄未设置' }}
              </el-tag>
            </div>
          </div>

          <el-divider />

          <el-descriptions :column="1" class="profile-info" size="small">
            <el-descriptions-item label="用户名">
              {{ userStore.username }}
            </el-descriptions-item>
            <el-descriptions-item label="手机号">
              {{ userInfo.phone || '未设置' }}
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">
              {{ userInfo.email || '未设置' }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="16">
        <el-card class="setting-card" shadow="never">
          <template #header>
            <div class="card-header">
              <div>
                <h3>基本信息</h3>
                <p>完善个人资料，便于生成更准确的分析</p>
              </div>
              <el-icon class="header-icon"><User /></el-icon>
            </div>
          </template>

          <el-form
            :model="userForm"
            ref="userFormRef"
            :rules="userRules"
            label-position="top"
            class="profile-form"
          >
            <el-row :gutter="20">
              <el-col :xs="24" :sm="12">
                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="userForm.nickname" placeholder="请输入昵称" clearable />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="userForm.gender">
                    <el-radio-button :label="1">男</el-radio-button>
                    <el-radio-button :label="2">女</el-radio-button>
                    <el-radio-button :label="0">保密</el-radio-button>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :xs="24" :sm="12">
                <el-form-item label="年龄" prop="age">
                  <el-input-number
                    v-model="userForm.age"
                    :min="0"
                    :max="150"
                    controls-position="right"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="userForm.phone" placeholder="请输入手机号" clearable />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="邮箱" prop="email">
              <el-input v-model="userForm.email" placeholder="请输入邮箱" clearable />
            </el-form-item>

            <el-form-item class="form-actions">
              <el-button type="primary" :loading="saving" @click="saveUserInfo">
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="setting-card password-card" shadow="never">
          <template #header>
            <div class="card-header">
              <div>
                <h3>账号安全</h3>
                <p>定期更换密码可以提升账号安全性</p>
              </div>
              <el-icon class="header-icon"><Lock /></el-icon>
            </div>
          </template>

          <el-form
            :model="passwordForm"
            ref="passwordFormRef"
            :rules="passwordRules"
            label-position="top"
            class="profile-form"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                show-password
                placeholder="请输入原密码"
              />
            </el-form-item>

            <el-row :gutter="20">
              <el-col :xs="24" :sm="12">
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    show-password
                    placeholder="6-20位新密码"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    show-password
                    placeholder="再次输入新密码"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item class="form-actions">
              <el-button type="primary" :loading="changingPwd" @click="changePassword">
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { updateUserInfo, changePassword as changePasswordApi } from '@/api/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const saving = ref(false)
const changingPwd = ref(false)
const userFormRef = ref(null)
const passwordFormRef = ref(null)

const userInfo = computed(() => userStore.userInfo || {})

const avatarText = computed(() => {
  const nickname = userInfo.value.nickname || userStore.username || 'U'
  return nickname.charAt(0)
})

const genderText = computed(() => {
  const g = userInfo.value.gender
  if (g === 1) return '男'
  if (g === 2) return '女'
  return '保密'
})

const userForm = reactive({
  nickname: '',
  gender: 0,
  age: null,
  phone: '',
  email: ''
})

const userRules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [
    { pattern: /^$|^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const loadUserInfo = () => {
  const info = userInfo.value
  userForm.nickname = info.nickname || ''
  userForm.gender = info.gender || 0
  userForm.age = info.age || null
  userForm.phone = info.phone || ''
  userForm.email = info.email || ''
}

const saveUserInfo = async () => {
  try {
    await userFormRef.value.validate()
    saving.value = true
    await updateUserInfo(userForm)
    ElMessage.success('保存成功')
    const updatedInfo = { ...userStore.userInfo, ...userForm }
    userStore.updateUserInfo(updatedInfo)
  } catch (err) {
    console.error('保存失败:', err)
  } finally {
    saving.value = false
  }
}

const changePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    changingPwd.value = true
    await changePasswordApi(passwordForm)
    ElMessage.success('密码修改成功，请重新登录')
    userStore.logout()
    setTimeout(() => {
      window.location.href = '/login'
    }, 1000)
  } catch (err) {
    console.error('密码修改失败:', err)
  } finally {
    changingPwd.value = false
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped lang="scss">
.profile-page {
  .profile-card,
  .setting-card {
    overflow: hidden;
    border: 1px solid #eef0f4;
    border-radius: 16px;
  }

  .profile-card {
    margin-bottom: 20px;

    :deep(.el-card__body) {
      padding: 0;
    }

    .profile-cover {
      height: 96px;
      background: linear-gradient(135deg, #409eff 0%, #8b5cf6 100%);
    }

    .profile-main {
      position: relative;
      padding: 0 24px 22px;
      text-align: center;

      .profile-avatar {
        margin-top: -46px;
        font-size: 34px;
        font-weight: 700;
        color: #fff;
        background: linear-gradient(135deg, #409eff 0%, #8b5cf6 100%);
        border: 4px solid #fff;
        box-shadow: 0 10px 24px rgba(59, 130, 246, 0.25);
      }

      h3 {
        margin: 14px 0 4px;
        font-size: 21px;
        color: #1f2937;
      }

      p {
        margin: 0 0 12px;
        font-size: 14px;
        color: #909399;
      }

      .profile-tags {
        display: flex;
        justify-content: center;
        gap: 8px;
      }
    }

    .el-divider {
      margin: 0 24px 20px;
    }

    .profile-info {
      padding: 0 24px 24px;
    }
  }

  .setting-card {
    margin-bottom: 20px;

    :deep(.el-card__header) {
      padding: 20px 24px;
      border-bottom: 1px solid #f1f3f6;
    }

    :deep(.el-card__body) {
      padding: 24px;
    }
  }

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;

    h3 {
      margin: 0 0 4px;
      font-size: 17px;
      color: #1f2937;
    }

    p {
      margin: 0;
      font-size: 13px;
      color: #909399;
    }

    .header-icon {
      font-size: 22px;
      color: #409eff;
    }
  }

  .profile-form {
    .form-actions {
      margin-top: 8px;
      margin-bottom: 0;
    }

    :deep(.el-form-item__label) {
      font-weight: 600;
      color: #374151;
    }
  }

  .password-card {
    .header-icon {
      color: #e6a23c;
    }
  }
}

@media (max-width: 768px) {
  .profile-page {
    .profile-card {
      margin-bottom: 20px;
    }
  }
}
</style>
