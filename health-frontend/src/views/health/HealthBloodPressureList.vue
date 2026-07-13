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
          <span>血压记录</span>
          <el-button type="primary" @click="showDialog()">新增记录</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="records" border stripe>
        <el-table-column prop="systolic" label="收缩压" width="80" />
        <el-table-column prop="diastolic" label="舒张压" width="80" />
        <el-table-column label="评估" width="100">
          <template #default="{ row }">
            <span :class="bpClass(row)">{{ bpLevel(row) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="heartRateBpm" label="心率(bpm)" width="90" />
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

    <el-dialog :title="isEdit ? '编辑血压记录' : '新增血压记录'" :visible.sync="dialogVisible" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="收缩压(mmHg)" prop="systolic">
          <el-input-number v-model="form.systolic" :min="50" :max="250" />
        </el-form-item>
        <el-form-item label="舒张压(mmHg)" prop="diastolic">
          <el-input-number v-model="form.diastolic" :min="30" :max="150" />
        </el-form-item>
        <el-form-item label="心率(bpm)">
          <el-input-number v-model="form.heartRateBpm" :min="30" :max="220" />
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
import { getBloodPressureList, addBloodPressure, updateBloodPressure, deleteBloodPressure } from '@/api/health'

export default {
  name: 'HealthBloodPressureList',
  data() {
    return {
      searchForm: { dateRange: null, pageNum: 1, pageSize: 10 },
      records: [], total: 0, loading: false,
      dialogVisible: false, isEdit: false, editId: null, submitting: false,
      form: { systolic: null, diastolic: null, heartRateBpm: null, recordDate: '', recordTime: '', note: '' },
      rules: {
        systolic: [{ required: true, message: '请输入收缩压', trigger: 'blur' }],
        diastolic: [{ required: true, message: '请输入舒张压', trigger: 'blur' }],
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
        const res = await getBloodPressureList(params)
        this.records = res.data.records
        this.total = res.data.total
      } finally { this.loading = false }
    },
    handleSearch() { this.searchForm.pageNum = 1; this.fetchData() },
    handleReset() { this.searchForm.dateRange = null; this.searchForm.pageNum = 1; this.fetchData() },
    showDialog(row) {
      if (row) {
        this.isEdit = true; this.editId = row.id
        this.form = { systolic: row.systolic, diastolic: row.diastolic, heartRateBpm: row.heartRateBpm,
                      recordDate: row.recordDate, recordTime: row.recordTime, note: row.note || '' }
      } else {
        this.isEdit = false; this.editId = null
        this.form = { systolic: null, diastolic: null, heartRateBpm: null, recordDate: '', recordTime: '', note: '' }
      }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.formRef?.clearValidate())
    },
    async handleSubmit() {
      try { await this.$refs.formRef.validate() } catch { return }
      this.submitting = true
      try {
        this.isEdit ? await updateBloodPressure(this.editId, this.form) : await addBloodPressure(this.form)
        this.$message.success(this.isEdit ? '修改成功' : '新增成功')
        this.dialogVisible = false; this.fetchData()
      } finally { this.submitting = false }
    },
    async handleDelete(id) { await deleteBloodPressure(id); this.$message.success('删除成功'); this.fetchData() },
    bpLevel(row) {
      const s = row.systolic, d = row.diastolic
      if (!s || !d) return '--'
      if (s < 90 || d < 60) return '偏低'
      if (s < 120 && d < 80) return '理想'
      if (s < 130 && d < 85) return '正常'
      if (s < 140 || d < 90) return '正常高值'
      return '高血压'
    },
    bpClass(row) {
      const l = this.bpLevel(row)
      if (l === '理想' || l === '正常') return 'text-success'
      if (l === '正常高值') return 'text-warning'
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
