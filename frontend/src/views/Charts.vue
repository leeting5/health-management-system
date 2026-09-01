<template>
  <div class="charts-page">
    <el-card shadow="never" class="mb-20">
      <div class="chart-header">
        <span>📈 数据趋势图</span>
        <div class="chart-controls">
          <span>时间范围：</span>
          <el-radio-group v-model="days" size="default" @change="fetchData">
            <el-radio-button :label="7">近7天</el-radio-button>
            <el-radio-button :label="14">近14天</el-radio-button>
            <el-radio-button :label="30">近30天</el-radio-button>
            <el-radio-button :label="90">近90天</el-radio-button>
          </el-radio-group>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20">
      <el-col :span="12" class="mb-20">
        <el-card shadow="never">
          <template #header>
            <span>⚖️ BMI 趋势</span>
          </template>
          <div ref="bmiChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <el-col :span="12" class="mb-20">
        <el-card shadow="never">
          <template #header>
            <span>🩺 血压趋势</span>
          </template>
          <div ref="bpChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <el-col :span="12" class="mb-20">
        <el-card shadow="never">
          <template #header>
            <span>🩸 血糖趋势</span>
          </template>
          <div ref="bsChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <el-col :span="12" class="mb-20">
        <el-card shadow="never">
          <template #header>
            <span>❤️ 心率趋势</span>
          </template>
          <div ref="hrChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getTrendData } from '@/api/health'

const days = ref(30)
const trendData = ref({
  dates: [],
  bmi: [],
  systolicPressure: [],
  diastolicPressure: [],
  bloodSugar: [],
  heartRate: []
})

// 图表引用
const bmiChartRef = ref(null)
const bpChartRef = ref(null)
const bsChartRef = ref(null)
const hrChartRef = ref(null)

let bmiChart = null
let bpChart = null
let bsChart = null
let hrChart = null

// 获取趋势数据
const fetchData = async () => {
  try {
    const res = await getTrendData(days.value)
    trendData.value = res.data || {
      dates: [],
      bmi: [],
      systolicPressure: [],
      diastolicPressure: [],
      bloodSugar: [],
      heartRate: []
    }
    // 数据更新后重新渲染图表
    await nextTick()
    renderCharts()
  } catch (err) {
    console.error('获取趋势数据失败:', err)
  }
}

// 渲染所有图表
const renderCharts = () => {
  renderBmiChart()
  renderBpChart()
  renderBsChart()
  renderHrChart()
}

// BMI图表
const renderBmiChart = () => {
  if (!bmiChartRef.value) return
  if (!bmiChart) {
    bmiChart = echarts.init(bmiChartRef.value)
  }

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: trendData.value.dates,
      axisLabel: {
        rotate: 45,
        fontSize: 10
      }
    },
    yAxis: {
      type: 'value',
      name: 'BMI',
      min: 15,
      max: 35
    },
    series: [
      {
        name: 'BMI',
        type: 'line',
        smooth: true,
        data: trendData.value.bmi,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
          ])
        },
        lineStyle: {
          color: '#67c23a',
          width: 2
        },
        itemStyle: {
          color: '#67c23a'
        },
        markLine: {
          silent: true,
          data: [
            { yAxis: 18.5, lineStyle: { color: '#e6a23c', type: 'dashed' }, label: { formatter: '偏瘦线' } },
            { yAxis: 24, lineStyle: { color: '#e6a23c', type: 'dashed' }, label: { formatter: '超重线' } },
            { yAxis: 28, lineStyle: { color: '#f56c6c', type: 'dashed' }, label: { formatter: '肥胖线' } }
          ]
        }
      }
    ]
  }

  bmiChart.setOption(option)
}

// 血压图表
const renderBpChart = () => {
  if (!bpChartRef.value) return
  if (!bpChart) {
    bpChart = echarts.init(bpChartRef.value)
  }

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['收缩压', '舒张压']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: trendData.value.dates,
      axisLabel: {
        rotate: 45,
        fontSize: 10
      }
    },
    yAxis: {
      type: 'value',
      name: 'mmHg'
    },
    series: [
      {
        name: '收缩压',
        type: 'line',
        smooth: true,
        data: trendData.value.systolicPressure,
        lineStyle: { color: '#f56c6c', width: 2 },
        itemStyle: { color: '#f56c6c' }
      },
      {
        name: '舒张压',
        type: 'line',
        smooth: true,
        data: trendData.value.diastolicPressure,
        lineStyle: { color: '#409eff', width: 2 },
        itemStyle: { color: '#409eff' }
      }
    ]
  }

  bpChart.setOption(option)
}

// 血糖图表
const renderBsChart = () => {
  if (!bsChartRef.value) return
  if (!bsChart) {
    bsChart = echarts.init(bsChartRef.value)
  }

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: trendData.value.dates,
      axisLabel: {
        rotate: 45,
        fontSize: 10
      }
    },
    yAxis: {
      type: 'value',
      name: 'mmol/L'
    },
    series: [
      {
        name: '空腹血糖',
        type: 'line',
        smooth: true,
        data: trendData.value.bloodSugar,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(230, 162, 60, 0.3)' },
            { offset: 1, color: 'rgba(230, 162, 60, 0.05)' }
          ])
        },
        lineStyle: {
          color: '#e6a23c',
          width: 2
        },
        itemStyle: {
          color: '#e6a23c'
        },
        markLine: {
          silent: true,
          data: [
            { yAxis: 3.9, lineStyle: { color: '#67c23a', type: 'dashed' }, label: { formatter: '下限' } },
            { yAxis: 6.1, lineStyle: { color: '#e6a23c', type: 'dashed' }, label: { formatter: '正常上限' } },
            { yAxis: 7.0, lineStyle: { color: '#f56c6c', type: 'dashed' }, label: { formatter: '糖尿病' } }
          ]
        }
      }
    ]
  }

  bsChart.setOption(option)
}

// 心率图表
const renderHrChart = () => {
  if (!hrChartRef.value) return
  if (!hrChart) {
    hrChart = echarts.init(hrChartRef.value)
  }

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: trendData.value.dates,
      axisLabel: {
        rotate: 45,
        fontSize: 10
      }
    },
    yAxis: {
      type: 'value',
      name: '次/分',
      min: 40,
      max: 140
    },
    series: [
      {
        name: '心率',
        type: 'line',
        smooth: true,
        data: trendData.value.heartRate,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        },
        lineStyle: {
          color: '#409eff',
          width: 2
        },
        itemStyle: {
          color: '#409eff'
        },
        markLine: {
          silent: true,
          data: [
            { yAxis: 60, lineStyle: { color: '#67c23a', type: 'dashed' }, label: { formatter: '正常下限' } },
            { yAxis: 100, lineStyle: { color: '#67c23a', type: 'dashed' }, label: { formatter: '正常上限' } }
          ]
        }
      }
    ]
  }

  hrChart.setOption(option)
}

// 窗口大小变化时重新调整图表
const handleResize = () => {
  bmiChart?.resize()
  bpChart?.resize()
  bsChart?.resize()
  hrChart?.resize()
}

onMounted(() => {
  fetchData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  bmiChart?.dispose()
  bpChart?.dispose()
  bsChart?.dispose()
  hrChart?.dispose()
})
</script>

<style scoped lang="scss">
.charts-page {
  .chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .chart-controls {
      display: flex;
      align-items: center;
      gap: 12px;
    }
  }

  .chart-container {
    width: 100%;
    height: 300px;
  }
}
</style>
