<template>
  <div class="records-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>📋 健康记录列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增记录
          </el-button>
        </div>
      </template>

      <!-- 搜索和筛选 -->
      <div class="search-bar">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          size="default"
          @change="handleDateChange"
        />
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="recordDate" label="记录日期" width="130" sortable />
        <el-table-column prop="height" label="身高(cm)" width="100">
          <template #default="{ row }">
            {{ row.height || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="weight" label="体重(kg)" width="100">
          <template #default="{ row }">
            {{ row.weight || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="bmi" label="BMI" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.bmi" :type="getBmiTagType(row.bmi)" size="small">
              {{ row.bmi }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="血压(mmHg)" width="120">
          <template #default="{ row }">
            <span v-if="row.systolicPressure && row.diastolicPressure">
              {{ row.systolicPressure }}/{{ row.diastolicPressure }}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="bloodSugar" label="血糖(mmol/L)" width="120">
          <template #default="{ row }">
            {{ row.bloodSugar || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="heartRate" label="心率(次/分)" width="110">
          <template #default="{ row }">
            {{ row.heartRate || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.remark || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑记录' : '新增记录'"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="form" ref="formRef" :rules="rules" label-width="100px">
        <el-form-item label="记录日期" prop="recordDate">
          <el-date-picker
            v-model="form.recordDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%;"
          />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身高" prop="height">
              <el-input-number v-model="form.height" :min="0" :max="300" :precision="1" style="width: 100%;" />
              <span style="color: #909399; font-size: 12px; margin-left: 8px;">cm</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体重" prop="weight">
              <el-input-number v-model="form.weight" :min="0" :max="500" :precision="1" style="width: 100%;" />
              <span style="color: #909399; font-size: 12px; margin-left: 8px;">kg</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="收缩压" prop="systolicPressure">
              <el-input-number v-model="form.systolicPressure" :min="0" :max="300" style="width: 100%;" />
              <span style="color: #909399; font-size: 12px; margin-left: 8px;">mmHg</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="舒张压" prop="diastolicPressure">
              <el-input-number v-model="form.diastolicPressure" :min="0" :max="200" style="width: 100%;" />
              <span style="color: #909399; font-size: 12px; margin-left: 8px;">mmHg</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="血糖" prop="bloodSugar">
              <el-input-number v-model="form.bloodSugar" :min="0" :max="50" :precision="1" style="width: 100%;" />
              <span style="color: #909399; font-size: 12px; margin-left: 8px;">mmol/L</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="心率" prop="heartRate">
              <el-input-number v-model="form.heartRate" :min="0" :max="300" style="width: 100%;" />
              <span style="color: #909399; font-size: 12px; margin-left: 8px;">次/分</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="BMI">
          <el-tag :type="getBmiTagType(calculatedBmi)" size="large">
            {{ calculatedBmi || '暂无数据' }}
          </el-tag>
          <span style="color: #909399; font-size: 12px; margin-left: 8px;">（自动计算）</span>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getHealthRecordList, addHealthRecord, updateHealthRecord, deleteHealthRecord } from '@/api/health'

const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const dateRange = ref([])

// 对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const form = reactive({
  id: null,
  recordDate: null,
  height: null,
  weight: null,
  bmi: null,
  systolicPressure: null,
  diastolicPressure: null,
  bloodSugar: null,
  heartRate: null,
  remark: ''
})

const rules = {
  recordDate: [{ required: true, message: '请选择记录日期', trigger: 'change' }]
}

// 自动计算BMI
const calculatedBmi = computed(() => {
  if (form.height && form.weight && form.height > 0) {
    const heightM = form.height / 100
    const bmi = form.weight / (heightM * heightM)
    return bmi.toFixed(1)
  }
  return null
})

// 获取BMI标签类型
const getBmiTagType = (bmi) => {
  if (!bmi) return 'info'
  const value = parseFloat(bmi)
  if (value < 18.5) return 'warning'
  if (value < 24) return 'success'
  if (value < 28) return 'warning'
  return 'danger'
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getHealthRecordList({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (err) {
    console.error('获取记录列表失败:', err)
  } finally {
    loading.value = false
  }
}

// 日期筛选
const handleDateChange = () => {
  pageNum.value = 1
  fetchData()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  form.recordDate = new Date()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    recordDate: row.recordDate ? new Date(row.recordDate) : null,
    height: row.height,
    weight: row.weight,
    bmi: row.bmi,
    systolicPressure: row.systolicPressure,
    diastolicPressure: row.diastolicPressure,
    bloodSugar: row.bloodSugar,
    heartRate: row.heartRate,
    remark: row.remark || ''
  })
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？删除后无法恢复。', '确认删除', {
    type: 'warning',
    confirmButtonText: '确定删除',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      await deleteHealthRecord(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (err) {
      console.error('删除失败:', err)
    }
  }).catch(() => {})
}

// 提交
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true

    if (isEdit.value) {
      await updateHealthRecord(form)
      ElMessage.success('更新成功')
    } else {
      await addHealthRecord(form)
      ElMessage.success('添加成功')
    }

    dialogVisible.value = false
    fetchData()
  } catch (err) {
    console.error('提交失败:', err)
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null,
    recordDate: null,
    height: null,
    weight: null,
    bmi: null,
    systolicPressure: null,
    diastolicPressure: null,
    bloodSugar: null,
    heartRate: null,
    remark: ''
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.records-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .search-bar {
    margin-bottom: 20px;
    display: flex;
    gap: 12px;
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
