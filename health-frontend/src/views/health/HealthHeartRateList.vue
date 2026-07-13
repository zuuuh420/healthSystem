<template>
  <div class="health-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="日期范围">
          <el-date-picker v-model="searchForm.dateRange" type="daterange"
            range-separator="至" start-placeholder="开始" end-placeholder="结束"
            value-format="yyyy-MM-dd" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>心率记录</span>
          <el-button type="primary" @click="showDialog()">新增记录</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="records" border stripe>
        <el-table-column prop="heartRate" label="心率(bpm)" width="100">
          <template #default="{ row }">
            <span :class="hrClass(row)">{{ row.heartRate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="measureType" label="测量类型" width="90" />
        <el-table-column label="评估" width="100">
          <template #default="{ row }">
            <span :class="hrClass(row)">{{ hrLevel(row) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="recordDate" label="日期" width="120" />
        <el-table-column prop="recordTime" label="时间" width="100" />
        <el-table-column prop="note" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="text" @click="showDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="text" style="color: #F56C6C">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="pagination" background
        layout="total, sizes, prev, pager, next"
        :total="total" :page-size="searchForm.pageSize" :current-page="searchForm.pageNum"
        :page-sizes="[5, 10, 20, 50]"
        @size-change="v => { searchForm.pageSize = v; fetchData() }"
        @current-change="v => { searchForm.pageNum = v; fetchData() }" />
    </el-card>

    <el-dialog :title="isEdit ? '编辑心率记录' : '新增心率记录'" :visible.sync="dialogVisible" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="心率(bpm)" prop="heartRate">
          <el-input-number v-model="form.heartRate" :min="25" :max="250" />
        </el-form-item>
        <el-form-item label="测量类型" prop="measureType">
          <el-select v-model="form.measureType">
            <el-option label="静息" value="静息" />
            <el-option label="运动后" value="运动后" />
            <el-option label="睡眠" value="睡眠" />
          </el-select>
        </el-form-item>
        <el-form-item label="记录日期" prop="recordDate">
          <el-date-picker v-model="form.recordDate" type="date" value-format="yyyy-MM-dd" />
        </el-form-item>
        <el-form-item label="记录时间">
          <el-time-picker v-model="form.recordTime" value-format="HH:mm:ss" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.note" maxlength="255" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getHeartRateList, addHeartRate, updateHeartRate, deleteHeartRate } from '@/api/health'

export default {
  name: 'HealthHeartRateList',
  data() {
    return {
      searchForm: { dateRange: null, pageNum: 1, pageSize: 10 },
      records: [], total: 0, loading: false,
      dialogVisible: false, isEdit: false, editId: null, submitting: false,
      form: { heartRate: null, measureType: '静息', recordDate: '', recordTime: '', note: '' },
      rules: {
        heartRate: [{ required: true, message: '请输入心率', trigger: 'blur' }],
        measureType: [{ required: true, message: '请选择类型', trigger: 'change' }],
        recordDate: [{ required: true, message: '请选择日期', trigger: 'change' }]
      }
    }
  },
  mounted() { this.fetchData() },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const params = { pageNum: this.searchForm.pageNum, pageSize: this.searchForm.pageSize }
        if (this.searchForm.dateRange && this.searchForm.dateRange.length === 2) {
          params.startDate = this.searchForm.dateRange[0]
          params.endDate = this.searchForm.dateRange[1]
        }
        const res = await getHeartRateList(params)
        this.records = res.data.records
        this.total = res.data.total
      } finally { this.loading = false }
    },
    handleSearch() { this.searchForm.pageNum = 1; this.fetchData() },
    handleReset() { this.searchForm.dateRange = null; this.searchForm.pageNum = 1; this.fetchData() },
    showDialog(row) {
      if (row) {
        this.isEdit = true; this.editId = row.id
        this.form = { heartRate: row.heartRate, measureType: row.measureType,
                      recordDate: row.recordDate, recordTime: row.recordTime, note: row.note || '' }
      } else {
        this.isEdit = false; this.editId = null
        this.form = { heartRate: null, measureType: '静息', recordDate: '', recordTime: '', note: '' }
      }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.formRef?.clearValidate())
    },
    async handleSubmit() {
      try { await this.$refs.formRef.validate() } catch { return }
      this.submitting = true
      try {
        this.isEdit ? await updateHeartRate(this.editId, this.form) : await addHeartRate(this.form)
        this.$message.success(this.isEdit ? '修改成功' : '新增成功')
        this.dialogVisible = false; this.fetchData()
      } finally { this.submitting = false }
    },
    async handleDelete(id) { await deleteHeartRate(id); this.$message.success('删除成功'); this.fetchData() },
    hrLevel(row) {
      const v = row.heartRate, t = row.measureType
      if (!v) return '--'
      if (t === '静息' || t === '睡眠') {
        if (v < 40) return '严重过缓'
        if (v < 50) return '心动过缓'
        if (v <= 100) return '正常'
        return '心动过速'
      }
      if (v < 50) return '恢复良好'
      if (v <= 130) return '正常反应'
      if (v <= 170) return '高强度'
      return '需注意'
    },
    hrClass(row) {
      const l = this.hrLevel(row)
      if (l === '正常' || l === '恢复良好' || l === '正常反应') return 'text-success'
      if (l === '高强度') return 'text-warning'
      return 'text-danger'
    }
  }
}
</script>

<style scoped>
.health-list { padding: 10px 0; }
.search-card { margin-bottom: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination { margin-top: 16px; text-align: right; }
.text-success { color: #67C23A; font-weight: bold; }
.text-warning { color: #E6A23C; font-weight: bold; }
.text-danger { color: #F56C6C; font-weight: bold; }
</style>
