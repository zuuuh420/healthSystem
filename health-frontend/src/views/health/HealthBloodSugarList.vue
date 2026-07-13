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
          <span>血糖记录</span>
          <el-button type="primary" @click="showDialog()">新增记录</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="records" border stripe>
        <el-table-column prop="sugarLevel" label="血糖(mmol/L)" width="110">
          <template #default="{ row }">
            <span :class="sugarClass(row)">{{ row.sugarLevel }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="measureType" label="测量类型" width="90" />
        <el-table-column label="评估" width="100">
          <template #default="{ row }">
            <span :class="sugarClass(row)">{{ sugarLevel(row) }}</span>
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

    <el-dialog :title="isEdit ? '编辑血糖记录' : '新增血糖记录'" :visible.sync="dialogVisible" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="血糖(mmol/L)" prop="sugarLevel">
          <el-input-number v-model="form.sugarLevel" :min="0.5" :max="30" :precision="1" :step="0.1" />
        </el-form-item>
        <el-form-item label="测量类型" prop="measureType">
          <el-select v-model="form.measureType">
            <el-option label="空腹" value="空腹" />
            <el-option label="餐后" value="餐后" />
            <el-option label="随机" value="随机" />
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
import { getBloodSugarList, addBloodSugar, updateBloodSugar, deleteBloodSugar } from '@/api/health'

export default {
  name: 'HealthBloodSugarList',
  data() {
    return {
      searchForm: { dateRange: null, pageNum: 1, pageSize: 10 },
      records: [], total: 0, loading: false,
      dialogVisible: false, isEdit: false, editId: null, submitting: false,
      form: { sugarLevel: null, measureType: '空腹', recordDate: '', recordTime: '', note: '' },
      rules: {
        sugarLevel: [{ required: true, message: '请输入血糖值', trigger: 'blur' }],
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
        const res = await getBloodSugarList(params)
        this.records = res.data.records
        this.total = res.data.total
      } finally { this.loading = false }
    },
    handleSearch() { this.searchForm.pageNum = 1; this.fetchData() },
    handleReset() { this.searchForm.dateRange = null; this.searchForm.pageNum = 1; this.fetchData() },
    showDialog(row) {
      if (row) {
        this.isEdit = true; this.editId = row.id
        this.form = { sugarLevel: row.sugarLevel, measureType: row.measureType,
                      recordDate: row.recordDate, recordTime: row.recordTime, note: row.note || '' }
      } else {
        this.isEdit = false; this.editId = null
        this.form = { sugarLevel: null, measureType: '空腹', recordDate: '', recordTime: '', note: '' }
      }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.formRef?.clearValidate())
    },
    async handleSubmit() {
      try { await this.$refs.formRef.validate() } catch { return }
      this.submitting = true
      try {
        this.isEdit ? await updateBloodSugar(this.editId, this.form) : await addBloodSugar(this.form)
        this.$message.success(this.isEdit ? '修改成功' : '新增成功')
        this.dialogVisible = false; this.fetchData()
      } finally { this.submitting = false }
    },
    async handleDelete(id) { await deleteBloodSugar(id); this.$message.success('删除成功'); this.fetchData() },
    sugarLevel(row) {
      const v = row.sugarLevel, t = row.measureType
      if (!v) return '--'
      const threshold = t === '空腹' ? 6.1 : 7.8
      if (v < 3.9) return '低血糖'
      if (v <= threshold) return '正常'
      if (v <= 11.1) return '偏高'
      return '糖尿病风险'
    },
    sugarClass(row) {
      const l = this.sugarLevel(row)
      if (l === '正常') return 'text-success'
      if (l === '低血糖') return 'text-primary'
      if (l === '偏高') return 'text-warning'
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
.text-primary { color: #409EFF; font-weight: bold; }
</style>
