<template>
  <div class="diet-record">
    <h2 class="page-title">饮食记录</h2>

    <div class="search-bar">
      <el-date-picker v-model="selectedDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" @change="fetchData" />
      <el-button type="primary" @click="showDialog()">记录饮食</el-button>
    </div>

    <el-row v-if="todaySummary" :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ todaySummary.totalCalories }}</div>
          <div class="stat-label">总热量(kcal)</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ todaySummary.totalProtein }}g</div>
          <div class="stat-label">蛋白质</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ todaySummary.totalFat }}g</div>
          <div class="stat-label">脂肪</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ todaySummary.totalCarbs }}g</div>
          <div class="stat-label">碳水</div>
        </el-card>
      </el-col>
    </el-row>

    <el-tabs v-model="mealTab">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="早餐" name="breakfast" />
      <el-tab-pane label="午餐" name="lunch" />
      <el-tab-pane label="晚餐" name="dinner" />
      <el-tab-pane label="加餐" name="snack" />
    </el-tabs>

    <el-table :data="filteredData" border stripe v-loading="loading">
      <el-table-column prop="foodName" label="食物" width="160" />
      <el-table-column label="餐次" width="100">
        <template slot-scope="{ row }">{{ mealTypeMap[row.mealType] || row.mealType }}</template>
      </el-table-column>
      <el-table-column prop="quantityG" label="食用量(g)" width="100" />
      <el-table-column prop="calories" label="热量(kcal)" width="110" />
      <el-table-column prop="protein" label="蛋白质(g)" width="100" />
      <el-table-column prop="fat" label="脂肪(g)" width="90" />
      <el-table-column prop="carbs" label="碳水(g)" width="90" />
      <el-table-column prop="note" label="备注" min-width="140" show-overflow-tooltip />
      <el-table-column label="操作" width="140" fixed="right">
        <template slot-scope="{ row }">
          <el-button size="small" @click="showDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top: 16px; text-align: right;">
      <el-pagination
        :current-page="page" :page-size="size" :total="total"
        :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next"
        @size-change="onSizeChange" @current-change="onPageChange"
      />
    </div>

    <el-dialog :title="editingId ? '编辑饮食记录' : '记录饮食'" :visible.sync="dialogVisible" width="480px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="日期" prop="recordDate">
          <el-date-picker v-model="form.recordDate" type="date" value-format="yyyy-MM-dd" style="width: 100%" />
        </el-form-item>
        <el-form-item label="餐次" prop="mealType">
          <el-select v-model="form.mealType" style="width: 100%">
            <el-option label="早餐" value="breakfast" />
            <el-option label="午餐" value="lunch" />
            <el-option label="晚餐" value="dinner" />
            <el-option label="加餐" value="snack" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择食物" prop="foodId">
          <el-select v-model="form.foodId" filterable placeholder="搜索食物" style="width: 100%">
            <el-option v-for="f in foodList" :key="f.id" :label="`${f.name} (${f.caloriesPer100g}kcal/100g)`" :value="f.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="食用克数" prop="quantityG">
          <el-input-number v-model="form.quantityG" :min="1" :max="5000" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预估热量" v-if="estimatedCalories > 0">
          <span style="color: #409eff; font-weight: bold;">{{ estimatedCalories }} kcal</span>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.note" />
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
import { getDietRecords, createDietRecord, updateDietRecord, deleteDietRecord } from '@/api/dietRecord'
import { getAllFoods } from '@/api/food'

export default {
  name: 'DietRecord',
  data() {
    return {
      loading: false, saving: false,
      tableData: [], foodList: [],
      selectedDate: new Date().toISOString().slice(0, 10),
      mealTab: 'all', page: 1, size: 20, total: 0,
      mealTypeMap: { breakfast: '早餐', lunch: '午餐', dinner: '晚餐', snack: '加餐' },
      dialogVisible: false, editingId: null,
      form: { recordDate: new Date().toISOString().slice(0, 10), mealType: 'lunch', foodId: null, quantityG: 100, note: '' },
      rules: {
        recordDate: [{ required: true, message: '请选择日期', trigger: 'blur' }],
        mealType: [{ required: true, message: '请选择餐次', trigger: 'change' }],
        foodId: [{ required: true, message: '请选择食物', trigger: 'change' }],
        quantityG: [{ required: true, message: '请输入克数', trigger: 'blur' }]
      }
    }
  },
  computed: {
    filteredData() {
      if (this.mealTab === 'all') return this.tableData
      return this.tableData.filter(r => r.mealType === this.mealTab)
    },
    todaySummary() {
      const today = this.tableData.filter(r => r.recordDate === this.selectedDate)
      if (!today.length) return null
      return {
        totalCalories: Math.round(today.reduce((s, r) => s + (r.calories || 0), 0) * 10) / 10,
        totalProtein: Math.round(today.reduce((s, r) => s + (r.protein || 0), 0) * 10) / 10,
        totalFat: Math.round(today.reduce((s, r) => s + (r.fat || 0), 0) * 10) / 10,
        totalCarbs: Math.round(today.reduce((s, r) => s + (r.carbs || 0), 0) * 10) / 10
      }
    },
    estimatedCalories() {
      if (!this.form.foodId || !this.form.quantityG) return 0
      const food = this.foodList.find(f => f.id === this.form.foodId)
      if (!food) return 0
      return Math.round(food.caloriesPer100g * this.form.quantityG / 100 * 10) / 10
    }
  },
  created() {
    this.fetchData()
    this.loadFoods()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const res = await getDietRecords({ date: this.selectedDate, page: this.page, size: this.size })
        if (res.code === 200) { this.tableData = res.data.records; this.total = res.data.total }
      } finally { this.loading = false }
    },
    async loadFoods() {
      try {
        const res = await getAllFoods()
        if (res.code === 200) this.foodList = res.data
      } catch (e) {}
    },
    onSizeChange(val) { this.size = val; this.fetchData() },
    onPageChange(val) { this.page = val; this.fetchData() },
    showDialog(row) {
      this.editingId = row ? row.id : null
      if (row) {
        this.form = { recordDate: row.recordDate, mealType: row.mealType, foodId: row.foodId, quantityG: row.quantityG, note: row.note || '' }
      } else {
        this.form = { recordDate: this.selectedDate, mealType: 'lunch', foodId: null, quantityG: 100, note: '' }
      }
      this.dialogVisible = true
    },
    resetForm() {
      this.editingId = null
      this.form = { recordDate: new Date().toISOString().slice(0, 10), mealType: 'lunch', foodId: null, quantityG: 100, note: '' }
      this.$nextTick(() => { if (this.$refs.formRef) this.$refs.formRef.clearValidate() })
    },
    async handleSave() {
      const valid = await this.$refs.formRef.validate().catch(() => false)
      if (!valid) return
      this.saving = true
      try {
        const data = { recordDate: this.form.recordDate, mealType: this.form.mealType, foodId: this.form.foodId, quantityG: this.form.quantityG, note: this.form.note }
        if (this.editingId) {
          await updateDietRecord(this.editingId, data)
          this.$message.success('修改成功')
        } else {
          await createDietRecord(data)
          this.$message.success('记录成功')
        }
        this.dialogVisible = false
        this.fetchData()
      } finally { this.saving = false }
    },
    async handleDelete(id) {
      await this.$confirm('确定删除该记录吗？', '确认删除', { type: 'warning' })
      await deleteDietRecord(id)
      this.$message.success('删除成功')
      this.fetchData()
    }
  }
}
</script>

<style scoped>
.diet-record { padding: 20px; }
.page-title { margin-bottom: 20px; color: #303133; }
.search-bar { margin-bottom: 16px; display: flex; gap: 12px; flex-wrap: wrap; }
.stat-cards { margin-bottom: 20px; }
.stat-card { text-align: center; }
.stat-value { font-size: 24px; font-weight: bold; color: #409eff; }
.stat-label { font-size: 13px; color: #909399; margin-top: 6px; }
</style>
