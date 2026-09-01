<template>
  <div class="dashboard-page">
    <section class="hero-card">
      <div class="hero-content">
        <p class="hero-eyebrow">HEALTH OVERVIEW</p>
        <h2>你好，{{ userStore.username }}</h2>
        <p class="hero-subtitle">{{ currentDate }} · 关注每日健康数据，掌握身体变化</p>
        <div class="hero-actions">
          <el-button type="primary" @click="$router.push('/add-record')">
            <el-icon><Plus /></el-icon>
            录入今日数据
          </el-button>
          <el-button class="hero-ghost-button" @click="$router.push('/charts')">
            查看趋势图
          </el-button>
        </div>
      </div>

      <div class="score-panel">
        <el-progress
          type="circle"
          :percentage="assessment.totalScore || 0"
          :width="132"
          :stroke-width="10"
          :color="scoreColor"
        >
          <template #default>
            <div class="score-inner">
              <span>{{ assessment.hasData ? assessment.totalScore : '--' }}</span>
              <small>健康评分</small>
            </div>
          </template>
        </el-progress>
        <el-tag
          v-if="assessment.hasData"
          :type="levelType"
          effect="dark"
          class="score-level"
        >
          {{ assessment.overallLevel }}
        </el-tag>
      </div>
    </section>

    <section v-if="announcements.length" class="announcement-section">
      <el-alert
        v-for="item in announcements"
        :key="item.id"
        :title="item.title"
        :description="item.content"
        type="warning"
        show-icon
        :closable="false"
      />
    </section>

    <el-row :gutter="20" class="assessment-row" v-loading="assessmentLoading">
      <el-col
        v-for="item in assessmentItems"
        :key="item.key"
        :xs="24"
        :sm="12"
        :lg="6"
      >
        <div class="metric-card" :class="`metric-card--${item.tone}`">
          <div class="metric-header">
            <div class="metric-icon">{{ item.icon }}</div>
            <el-tag :type="getTagType(item.level)" size="small" effect="light">
              {{ item.level || '未知' }}
            </el-tag>
          </div>
          <div class="metric-label">{{ item.label }}</div>
          <div class="metric-value">{{ item.value }}</div>
          <p class="metric-suggestion">{{ item.suggestion || '请先录入相关健康数据' }}</p>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-row">
      <el-col :xs="24" :lg="16">
        <el-card class="panel-card" shadow="never">
          <template #header>
            <div class="card-header">
              <div>
                <h3>最近记录</h3>
                <p>最近 7 天健康指标变化</p>
              </div>
              <el-button type="primary" link @click="$router.push('/records')">查看全部</el-button>
            </div>
          </template>

          <el-table :data="recentRecords" v-loading="loading" stripe>
            <el-table-column prop="recordDate" label="日期" width="120" />
            <el-table-column prop="bmi" label="BMI" width="90">
              <template #default="{ row }">{{ row.bmi || '-' }}</template>
            </el-table-column>
            <el-table-column label="血压" width="110">
              <template #default="{ row }">
                {{ row.systolicPressure || '-' }}/{{ row.diastolicPressure || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="bloodSugar" label="血糖" />
            <el-table-column prop="heartRate" label="心率" />
          </el-table>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card class="panel-card quick-card" shadow="never">
          <template #header>
            <div>
              <h3>快捷操作</h3>
              <p>常用功能快速入口</p>
            </div>
          </template>

          <div class="quick-actions">
            <button class="quick-action" @click="$router.push('/add-record')">
              <span class="quick-icon quick-icon--primary"><el-icon><Plus /></el-icon></span>
              <span>
                <strong>录入今日数据</strong>
                <small>记录身高、体重、血压等指标</small>
              </span>
            </button>
            <button class="quick-action" @click="$router.push('/charts')">
              <span class="quick-icon quick-icon--success"><el-icon><TrendCharts /></el-icon></span>
              <span>
                <strong>查看趋势图</strong>
                <small>观察健康指标变化趋势</small>
              </span>
            </button>
            <button class="quick-action" @click="$router.push('/profile')">
              <span class="quick-icon quick-icon--warning"><el-icon><User /></el-icon></span>
              <span>
                <strong>个人信息</strong>
                <small>维护个人资料与账号安全</small>
              </span>
            </button>
          </div>
        </el-card>

        <el-card class="panel-card advice-card" shadow="never" v-if="assessment.bmi">
          <template #header>
            <div>
              <h3>健康建议</h3>
              <p>根据最新数据生成</p>
            </div>
          </template>
          <div class="advice-list">
            <div v-if="assessment.bmi?.suggestion" class="advice-item">
              <span class="advice-dot advice-dot--blue"></span>
              <p><strong>体重：</strong>{{ assessment.bmi.suggestion }}</p>
            </div>
            <div v-if="assessment.bloodPressure?.suggestion" class="advice-item">
              <span class="advice-dot advice-dot--red"></span>
              <p><strong>血压：</strong>{{ assessment.bloodPressure.suggestion }}</p>
            </div>
            <div v-if="assessment.bloodSugar?.suggestion" class="advice-item">
              <span class="advice-dot advice-dot--orange"></span>
              <p><strong>血糖：</strong>{{ assessment.bloodSugar.suggestion }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getHealthAssessment, getRecentRecords } from '@/api/health'
import { getActiveAnnouncements } from '@/api/announcement'

const userStore = useUserStore()
const loading = ref(false)
const assessmentLoading = ref(false)
const announcements = ref([])

const currentDate = computed(() => {
  const date = new Date()
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
})

const assessment = reactive({
  hasData: false,
  totalScore: 0,
  overallLevel: '未知',
  bmi: null,
  bloodPressure: null,
  bloodSugar: null,
  heartRate: null,
  latestRecord: null
})

const recentRecords = ref([])

const scoreColor = computed(() => {
  if (!assessment.hasData) return '#c0c4cc'
  if (assessment.totalScore >= 85) return '#67c23a'
  if (assessment.totalScore >= 70) return '#409eff'
  if (assessment.totalScore >= 60) return '#e6a23c'
  return '#f56c6c'
})

const levelType = computed(() => {
  const level = assessment.overallLevel
  if (level === '优秀') return 'success'
  if (level === '良好') return 'primary'
  if (level === '一般') return 'warning'
  return 'danger'
})

const assessmentItems = computed(() => [
  {
    key: 'bmi',
    label: 'BMI 指数',
    icon: '⚖️',
    value: assessment.latestRecord?.bmi || '--',
    level: assessment.bmi?.level,
    suggestion: assessment.bmi?.suggestion,
    tone: 'blue'
  },
  {
    key: 'bloodPressure',
    label: '血压',
    icon: '🩺',
    value: assessment.latestRecord
      ? `${assessment.latestRecord.systolicPressure || '-'}/${assessment.latestRecord.diastolicPressure || '-'}`
      : '--',
    level: assessment.bloodPressure?.level,
    suggestion: assessment.bloodPressure?.suggestion,
    tone: 'red'
  },
  {
    key: 'bloodSugar',
    label: '血糖',
    icon: '🩸',
    value: assessment.latestRecord?.bloodSugar
      ? `${assessment.latestRecord.bloodSugar} mmol/L`
      : '--',
    level: assessment.bloodSugar?.level,
    suggestion: assessment.bloodSugar?.suggestion,
    tone: 'orange'
  },
  {
    key: 'heartRate',
    label: '心率',
    icon: '❤️',
    value: assessment.latestRecord?.heartRate
      ? `${assessment.latestRecord.heartRate} 次/分`
      : '--',
    level: assessment.heartRate?.level,
    suggestion: assessment.heartRate?.suggestion,
    tone: 'green'
  }
])

const getTagType = (level) => {
  if (!level) return 'info'
  if (['正常', '优秀', '良好'].includes(level)) return 'success'
  if (['超重', '正常高值', '糖耐量受损', '一般', '心动过缓', '心动过速'].includes(level)) return 'warning'
  if (['肥胖', '高血压', '糖尿病范围', '需关注'].includes(level)) return 'danger'
  if (['偏瘦', '偏低'].includes(level)) return 'warning'
  return 'info'
}

const fetchAssessment = async () => {
  assessmentLoading.value = true
  try {
    const res = await getHealthAssessment()
    if (res.data?.hasData) {
      Object.assign(assessment, res.data)
    }
  } catch (err) {
    console.error('获取评估数据失败:', err)
  } finally {
    assessmentLoading.value = false
  }
}

const fetchRecentRecords = async () => {
  loading.value = true
  try {
    const res = await getRecentRecords(7)
    recentRecords.value = res.data || []
  } catch (err) {
    console.error('获取最近记录失败:', err)
  } finally {
    loading.value = false
  }
}

const fetchAnnouncements = async () => {
  try {
    const res = await getActiveAnnouncements()
    announcements.value = res.data || []
  } catch (err) {
    console.error('获取公告失败:', err)
  }
}

onMounted(() => {
  fetchAssessment()
  fetchRecentRecords()
  fetchAnnouncements()
})
</script>

<style scoped lang="scss">
.dashboard-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.hero-card {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 220px;
  padding: 36px 40px;
  overflow: hidden;
  color: #fff;
  background: linear-gradient(135deg, #3a7bd5 0%, #5a4fcf 52%, #8b5cf6 100%);
  border-radius: 18px;
  box-shadow: 0 18px 40px rgba(79, 70, 229, 0.22);

  &::after {
    position: absolute;
    right: -60px;
    bottom: -90px;
    width: 260px;
    height: 260px;
    content: '';
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
  }
}

.hero-content {
  position: relative;
  z-index: 1;

  .hero-eyebrow {
    margin-bottom: 10px;
    font-size: 12px;
    font-weight: 700;
    letter-spacing: 2px;
    opacity: 0.72;
  }

  h2 {
    margin-bottom: 10px;
    font-size: 30px;
    line-height: 1.25;
  }

  .hero-subtitle {
    margin-bottom: 22px;
    font-size: 15px;
    opacity: 0.85;
  }

  .hero-actions {
    display: flex;
    gap: 12px;
  }

  .hero-ghost-button {
    color: #fff;
    background: rgba(255, 255, 255, 0.14);
    border: 1px solid rgba(255, 255, 255, 0.28);

    &:hover {
      color: #fff;
      background: rgba(255, 255, 255, 0.22);
      border-color: rgba(255, 255, 255, 0.45);
    }
  }
}

.score-panel {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;

  .score-inner {
    display: flex;
    flex-direction: column;
    align-items: center;

    span {
      font-size: 34px;
      font-weight: 700;
      color: #303133;
    }

    small {
      margin-top: 4px;
      font-size: 12px;
      color: #909399;
    }
  }
}

.announcement-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.assessment-row {
  margin-bottom: 0;
}

.metric-card {
  position: relative;
  height: 100%;
  padding: 20px;
  overflow: hidden;
  background: #fff;
  border: 1px solid #eef0f4;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
  transition: transform 0.2s ease, box-shadow 0.2s ease;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 14px 30px rgba(15, 23, 42, 0.1);
  }

  &::before {
    position: absolute;
    top: 0;
    right: 0;
    left: 0;
    height: 4px;
    content: '';
  }

  &.metric-card--blue::before { background: #409eff; }
  &.metric-card--red::before { background: #f56c6c; }
  &.metric-card--orange::before { background: #e6a23c; }
  &.metric-card--green::before { background: #67c23a; }

  .metric-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 18px;
  }

  .metric-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 46px;
    height: 46px;
    font-size: 23px;
    background: #f5f7fa;
    border-radius: 14px;
  }

  .metric-label {
    margin-bottom: 6px;
    font-size: 13px;
    color: #909399;
  }

  .metric-value {
    margin-bottom: 8px;
    font-size: 22px;
    font-weight: 700;
    color: #1f2937;
  }

  .metric-suggestion {
    min-height: 40px;
    margin: 0;
    font-size: 13px;
    line-height: 1.55;
    color: #6b7280;
  }
}

.content-row {
  .panel-card {
    height: 100%;
    border: 1px solid #eef0f4;
    border-radius: 16px;
  }

  :deep(.el-card__header) {
    padding: 20px 22px;
    border-bottom: 1px solid #f1f3f6;
  }

  :deep(.el-card__body) {
    padding: 22px;
  }
}

.card-header {
  display: flex;
  align-items: flex-start;
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
}

.quick-card {
  margin-bottom: 20px;
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.quick-action {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  padding: 14px;
  text-align: left;
  cursor: pointer;
  background: #f8fafc;
  border: 1px solid #eef0f4;
  border-radius: 14px;
  transition: background 0.2s ease, border-color 0.2s ease;

  &:hover {
    background: #f1f5f9;
    border-color: #dbe2ea;
  }

  strong {
    display: block;
    margin-bottom: 3px;
    font-size: 14px;
    color: #1f2937;
  }

  small {
    font-size: 12px;
    color: #8a94a6;
  }
}

.quick-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  color: #fff;
  border-radius: 12px;

  &.quick-icon--primary { background: #409eff; }
  &.quick-icon--success { background: #67c23a; }
  &.quick-icon--warning { background: #e6a23c; }
}

.advice-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.advice-item {
  display: flex;
  gap: 10px;

  p {
    margin: 0;
    font-size: 13px;
    line-height: 1.6;
    color: #606266;
  }

  strong {
    color: #303133;
  }
}

.advice-dot {
  flex: 0 0 auto;
  width: 8px;
  height: 8px;
  margin-top: 7px;
  border-radius: 50%;

  &.advice-dot--blue { background: #409eff; }
  &.advice-dot--red { background: #f56c6c; }
  &.advice-dot--orange { background: #e6a23c; }
}

@media (max-width: 768px) {
  .hero-card {
    align-items: flex-start;
    flex-direction: column;
    padding: 28px;
  }

  .score-panel {
    align-self: center;
    margin-top: 24px;
  }
}
</style>
