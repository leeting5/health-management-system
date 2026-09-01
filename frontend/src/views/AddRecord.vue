<template>
  <div class="add-record-page">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card shadow="never">
          <template #header>
            <span>✍️ 录入健康数据</span>
          </template>

          <el-form :model="form" ref="formRef" :rules="rules" label-width="120px">
            <el-form-item label="记录日期" prop="recordDate">
              <el-date-picker
                v-model="form.recordDate"
                type="date"
                placeholder="选择记录日期"
                style="width: 300px;"
              />
            </el-form-item>

            <el-divider content-position="left">身体数据</el-divider>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="身高" prop="height">
                  <el-input-number
                    v-model="form.height"
                    :min="0"
                    :max="300"
                    :precision="1"
                    :step="0.5"
                    style="width: 100%;"
                  />
                  <span class="unit">cm</span>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="体重" prop="weight">
                  <el-input-number
                    v-model="form.weight"
                    :min="0"
                    :max="500"
                    :precision="1"
                    :step="0.5"
                    style="width: 100%;"
                  />
                  <span class="unit">kg</span>
                </el-form-item>
              </el-col>
            </el-row>

            <!-- BMI 实时显示 -->
            <el-form-item label="BMI 指数">
              <div class="bmi-display">
                <el-tag v-if="calculatedBmi" :type="bmiTagType" size="large" effect="dark">
                  BMI: {{ calculatedBmi }}
                </el-tag>
                <el-tag v-else type="info" size="large">暂无数据</el-tag>
                <span class="bmi-level">{{ bmiLevel }}</span>
              </div>
            </el-form-item>

            <el-divider content-position="left">血压数据</el-divider>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="收缩压" prop="systolicPressure">
                  <el-input-number
                    v-model="form.systolicPressure"
                    :min="0"
                    :max="300"
                    :step="1"
                    style="width: 100%;"
                  />
                  <span class="unit">mmHg</span>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="舒张压" prop="diastolicPressure">
                  <el-input-number
                    v-model="form.diastolicPressure"
                    :min="0"
                    :max="200"
                    :step="1"
                    style="width: 100%;"
                  />
                  <span class="unit">mmHg</span>
                </el-form-item>
              </el-col>
            </el-row>

            <el-divider content-position="left">其他指标</el-divider>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="空腹血糖" prop="bloodSugar">
                  <el-input-number
                    v-model="form.bloodSugar"
                    :min="0"
                    :max="50"
                    :precision="1"
                    :step="0.1"
                    style="width: 100%;"
                  />
                  <span class="unit">mmol/L</span>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="静息心率" prop="heartRate">
                  <el-input-number
                    v-model="form.heartRate"
                    :min="0"
                    :max="300"
                    :step="1"
                    style="width: 100%;"
                  />
                  <span class="unit">次/分</span>
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="备注">
              <el-input
                v-model="form.remark"
                type="textarea"
                :rows="4"
                placeholder="记录身体状况、饮食运动等信息..."
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" size="large" :loading="submitting" @click="handleSubmit">
                💾 保存记录
              </el-button>
              <el-button size="large" @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="never">
          <template #header>
            <span>📖 参考标准</span>
          </template>
          <div class="reference-list">
            <div class="ref-item">
              <h4>BMI 指数（中国标准）</h4>
              <ul>
                <li><el-tag type="warning" size="small">偏瘦</el-tag> BMI < 18.5</li>
                <li><el-tag type="success" size="small">正常</el-tag> 18.5 ≤ BMI < 24</li>
                <li><el-tag type="warning" size="small">超重</el-tag> 24 ≤ BMI < 28</li>
                <li><el-tag type="danger" size="small">肥胖</el-tag> BMI ≥ 28</li>
              </ul>
            </div>
            <div class="ref-item">
              <h4>血压分级</h4>
              <ul>
                <li><el-tag type="success" size="small">正常</el-tag> <120/80 mmHg</li>
                <li><el-tag type="warning" size="small">正常高值</el-tag> 120-139/80-89</li>
                <li><el-tag type="danger" size="small">高血压</el-tag> ≥140/90 mmHg</li>
              </ul>
            </div>
            <div class="ref-item">
              <h4>空腹血糖</h4>
              <ul>
                <li><el-tag type="success" size="small">正常</el-tag> 3.9-6.1 mmol/L</li>
                <li><el-tag type="warning" size="small">受损</el-tag> 6.1-7.0 mmol/L</li>
                <li><el-tag type="danger" size="small">糖尿病</el-tag> ≥7.0 mmol/L</li>
              </ul>
            </div>
            <div class="ref-item">
              <h4>静息心率</h4>
              <ul>
                <li><el-tag type="success" size="small">正常</el-tag> 60-100 次/分</li>
                <li><el-tag type="info" size="small">运动员</el-tag> 50-60 次/分</li>
              </ul>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { addHealthRecord } from '@/api/health'

const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)

const form = reactive({
  recordDate: new Date(),
  height: null,
  weight: null,
  systolicPressure: null,
  diastolicPressure: null,
  bloodSugar: null,
  heartRate: null,
  remark: ''
})

const rules = {
  recordDate: [{ required: true, message: '请选择记录日期', trigger: 'change' }]
}

// 计算BMI
const calculatedBmi = computed(() => {
  if (form.height && form.weight && form.height > 0) {
    const heightM = form.height / 100
    const bmi = form.weight / (heightM * heightM)
    return bmi.toFixed(1)
  }
  return null
})

// BMI标签类型
const bmiTagType = computed(() => {
  if (!calculatedBmi.value) return 'info'
  const value = parseFloat(calculatedBmi.value)
  if (value < 18.5) return 'warning'
  if (value < 24) return 'success'
  if (value < 28) return 'warning'
  return 'danger'
})

// BMI等级描述
const bmiLevel = computed(() => {
  if (!calculatedBmi.value) return ''
  const value = parseFloat(calculatedBmi.value)
  if (value < 18.5) return '体重偏轻'
  if (value < 24) return '体重正常'
  if (value < 28) return '体重超重'
  return '体重肥胖'
})

// 提交
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    await addHealthRecord(form)
    ElMessage.success('记录保存成功！')
    router.push('/records')
  } catch (err) {
    console.error('保存失败:', err)
  } finally {
    submitting.value = false
  }
}

// 重置
const handleReset = () => {
  formRef.value?.resetFields()
  form.recordDate = new Date()
  form.height = null
  form.weight = null
  form.systolicPressure = null
  form.diastolicPressure = null
  form.bloodSugar = null
  form.heartRate = null
  form.remark = ''
}
</script>

<style scoped lang="scss">
.add-record-page {
  .unit {
    color: #909399;
    font-size: 13px;
    margin-left: 8px;
  }

  .bmi-display {
    display: flex;
    align-items: center;
    gap: 12px;

    .bmi-level {
      font-size: 14px;
      color: #606266;
    }
  }

  .reference-list {
    .ref-item {
      margin-bottom: 16px;
      padding-bottom: 16px;
      border-bottom: 1px solid #ebeef5;

      &:last-child {
        margin-bottom: 0;
        padding-bottom: 0;
        border-bottom: none;
      }

      h4 {
        font-size: 14px;
        color: #303133;
        margin-bottom: 8px;
      }

      ul {
        list-style: none;
        padding: 0;
        margin: 0;

        li {
          font-size: 13px;
          color: #606266;
          padding: 4px 0;
          display: flex;
          align-items: center;
          gap: 8px;
        }
      }
    }
  }
}
</style>
