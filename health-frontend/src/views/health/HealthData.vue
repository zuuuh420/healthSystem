<template>
  <div class="health-data">
    <h2 class="page-title">健康数据管理</h2>

    <div class="search-bar">
      <el-button type="primary" @click="showDialog()">记录健康数据</el-button>
      <el-date-picker
        v-model="dateRange" type="daterange" range-separator="至"
        start-placeholder="开始日期" end-placeholder="结束日期"
        value-format="yyyy-MM-dd" @change="fetchByRange"
        style="margin-left: 12px"
      />
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="recordDate" label="日期" width="120" />
      <el-table-column prop="height" label="身高(cm)" width="100" />
      <el-table-column prop="weight" label="体重(kg)" width="100" />
      <el-table-column prop="bmi" label="BMI" width="80" />
      <el-table-column label="血压(mmHg)" width="130">
        <template slot-scope="{ row }">{{ row.systolicPressure }}/{{ row.diastolicPressure }}</template>
      </el-table-column>
      <el-table-column prop="bloodSugar" label="血糖(mmol/L)" width="110" />
      <el-table-column prop="heartRate" label="心率(次/分)" width="110" />
      <el-table-column prop="note" label="备注" min-width="160" show-overflow-tooltip />
      <el-table-column label="操作" width="140" fixed="right">
        <template slot-scope="{ row }">
          <el-button size="small" @click="showDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="editingId ? '编辑健康数据' : '记录健康数据'" :visible.sync="dialogVisible" width="480px" @close="resetForm">
      <el-form :model="form" ref="formRef" label-width="120px">
        <el-form-item label="日期" prop="recordDate">
          <el-date-picker v-model="form.recordDate" type="date" value-format="yyyy-MM-dd" style="width: 100%" />
        </el-form-item>
        <el-form-item label="身高(cm)">
          <el-input-number v-model="form.height" :min="50" :max="250" :precision="1" style="width: 100%" placeholder="请输入身高" />
        </el-form-item>
        <el-form-item label="体重(kg)">
          <el-input-number v-model="form.weight" :min="20" :max="300" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="收缩压(mmHg)">
          <el-input-number v-model="form.systolicPressure" :min="60" :max="260" style="width: 100%" placeholder="高压" />
        </el-form-item>
        <el-form-item label="舒张压(mmHg)">
          <el-input-number v-model="form.diastolicPressure" :min="30" :max="160" style="width: 100%" placeholder="低压" />
        </el-form-item>
        <el-form-item label="血糖(mmol/L)">
          <el-input-number v-model="form.bloodSugar" :min="1" :max="30" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="心率(次/分)">
          <el-input-number v-model="form.heartRate" :min="30" :max="220" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.note" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getHealthRecords, getHealthRecordsByRange, createHealthRecord, updateHealthRecord, deleteHealthRecord } from '@/api/healthRecord'

export default {
  name: 'HealthData',
  data() {
    return {
      loading: false, saving: false,
      tableData: [], dateRange: null,
      dialogVisible: false, editingId: null,
      form: { recordDate: '', height: null, weight: null, systolicPressure: null, diastolicPressure: null, bloodSugar: null, heartRate: null, note: '' }
    }
  },
  created() { this.fetchData() },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const res = await getHealthRecords()
        if (res.code === 200) this.tableData = res.data
      } finally { this.loading = false }
    },
    async fetchByRange() {
      if (!this.dateRange) { this.fetchData(); return }
      this.loading = true
      try {
        const res = await getHealthRecordsByRange(this.dateRange[0], this.dateRange[1])
        if (res.code === 200) this.tableData = res.data
      } finally { this.loading = false }
    },
    showDialog(row) {
      this.editingId = row ? row.id : null
      this.form = row ? { ...row } : { recordDate: new Date().toISOString().slice(0, 10), height: null, weight: null, systolicPressure: null, diastolicPressure: null, bloodSugar: null, heartRate: null, note: '' }
      this.dialogVisible = true
    },
    resetForm() {
      this.editingId = null
      this.form = { recordDate: '', height: null, weight: null, systolicPressure: null, diastolicPressure: null, bloodSugar: null, heartRate: null, note: '' }
    },
    async handleSave() {
      this.saving = true
      try {
        if (this.editingId) {
          await updateHealthRecord(this.editingId, this.form)
          this.$message.success('修改成功')
        } else {
          await createHealthRecord(this.form)
          this.$message.success('记录成功')
        }
        this.dialogVisible = false
        this.fetchData()
      } catch (e) {
        this.$message.error(e.response?.data?.msg || '操作失败')
      } finally { this.saving = false }
    },
    async handleDelete(id) {
      await this.$confirm('确定删除该记录吗？', '确认删除', { type: 'warning' })
      await deleteHealthRecord(id)
      this.$message.success('删除成功')
      this.fetchData()
    }
  }
}
</script>

<style scoped>
.health-data { padding: 20px; }
.page-title { margin-bottom: 20px; color: #303133; }
.search-bar { margin-bottom: 16px; display: flex; gap: 12px; }
</style>
