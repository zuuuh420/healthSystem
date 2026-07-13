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
          <span>体重记录</span>
          <el-button type="primary" @click="showDialog()">新增记录</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="records" border stripe>
        <el-table-column prop="weight" label="体重(kg)" width="100" />
        <el-table-column prop="bmi" label="BMI" width="80">
          <template #default="{ row }">
            <span :class="bmiClass(row.bmi)">{{ row.bmi }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="heightCm" label="身高(cm)" width="100" />
        <el-table-column prop="recordDate" label="记录日期" width="120" />
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

    <el-dialog :title="isEdit ? '编辑体重记录' : '新增体重记录'" :visible.sync="dialogVisible" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="体重(kg)" prop="weight">
          <el-input-number v-model="form.weight" :min="20" :max="300" :precision="1" :step="0.5" />
        </el-form-item>
        <el-form-item label="身高(cm)" prop="heightCm">
          <el-input-number v-model="form.heightCm" :min="100" :max="250" :precision="1" :step="0.5" />
        </el-form-item>
        <el-form-item label="记录日期" prop="recordDate">
          <el-date-picker v-model="form.recordDate" type="date" value-format="yyyy-MM-dd" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.note" maxlength="255" />
        </el-form-item>
        <div v-if="form.weight && form.heightCm" class="bmi-preview">
          预计BMI：{{ (form.weight / (form.heightCm/100) / (form.heightCm/100)).toFixed(1) }}
          <span :class="bmiClass((form.weight / (form.heightCm/100) / (form.heightCm/100)).toFixed(1))">
            ({{ bmiLevel((form.weight / (form.heightCm/100) / (form.heightCm/100)).toFixed(1)) }})
          </span>
        </div>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getWeightList, addWeight, updateWeight, deleteWeight } from '@/api/health'

export default {
  name: 'HealthWeightList',
  data() {
    return {
      searchForm: { dateRange: null, pageNum: 1, pageSize: 10 },
      records: [], total: 0, loading: false,
      dialogVisible: false, isEdit: false, editId: null, submitting: false,
      form: { weight: null, heightCm: null, recordDate: '', note: '' },
      rules: {
        weight: [{ required: true, message: '请输入体重', trigger: 'blur' }],
        heightCm: [{ required: true, message: '请输入身高', trigger: 'blur' }],
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
        const res = await getWeightList(params)
        this.records = res.data.records
        this.total = res.data.total
      } finally { this.loading = false }
    },
    handleSearch() { this.searchForm.pageNum = 1; this.fetchData() },
    handleReset() { this.searchForm.dateRange = null; this.searchForm.pageNum = 1; this.fetchData() },
    showDialog(row) {
      if (row) {
        this.isEdit = true; this.editId = row.id
        this.form = { weight: row.weight, heightCm: row.heightCm, recordDate: row.recordDate, note: row.note || '' }
      } else {
        this.isEdit = false; this.editId = null
        this.form = { weight: null, heightCm: null, recordDate: '', note: '' }
      }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.formRef?.clearValidate())
    },
    async handleSubmit() {
      try { await this.$refs.formRef.validate() } catch { return }
      this.submitting = true
      try {
        if (this.isEdit) {
          await updateWeight(this.editId, this.form)
          this.$message.success('修改成功')
        } else {
          await addWeight(this.form)
          this.$message.success('新增成功')
        }
        this.dialogVisible = false; this.fetchData()
      } finally { this.submitting = false }
    },
    async handleDelete(id) {
      await deleteWeight(id)
      this.$message.success('删除成功')
      this.fetchData()
    },
    bmiClass(bmi) {
      if (!bmi) return ''; bmi = parseFloat(bmi)
      if (bmi < 18.5) return 'text-primary'
      if (bmi < 24) return 'text-success'
      if (bmi < 28) return 'text-warning'
      return 'text-danger'
    },
    bmiLevel(bmi) {
      if (!bmi) return ''; bmi = parseFloat(bmi)
      if (bmi < 18.5) return '偏瘦'
      if (bmi < 24) return '正常'
      if (bmi < 28) return '偏胖'
      return '肥胖'
    }
  }
}
</script>

<style scoped>
.health-list { padding: 10px 0; }
.search-card { margin-bottom: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination { margin-top: 16px; text-align: right; }
.bmi-preview { margin-top: 10px; padding: 8px 15px; background: #f5f7fa; border-radius: 4px; }
.text-success { color: #67C23A; font-weight: bold; }
.text-warning { color: #E6A23C; font-weight: bold; }
.text-danger { color: #F56C6C; font-weight: bold; }
.text-primary { color: #409EFF; font-weight: bold; }
</style>
