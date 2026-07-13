<template>
  <div class="food-library">
    <h2 class="page-title">食物库管理</h2>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索食物名称" style="width: 200px" clearable @clear="fetchData" @keyup.enter.native="fetchData" />
      <el-select v-model="category" placeholder="食物分类" style="width: 150px" clearable @change="fetchData">
        <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
      </el-select>
      <el-button type="primary" @click="fetchData">搜索</el-button>
      <el-button v-if="isAdmin" type="success" @click="showDialog()">添加食物</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="食物名称" width="140" />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column prop="caloriesPer100g" label="热量(kcal/100g)" width="130" />
      <el-table-column prop="proteinPer100g" label="蛋白质(g)" width="100" />
      <el-table-column prop="fatPer100g" label="脂肪(g)" width="90" />
      <el-table-column prop="carbsPer100g" label="碳水(g)" width="90" />
      <el-table-column prop="unit" label="单位" width="70" />
      <el-table-column prop="description" label="描述" min-width="160" show-overflow-tooltip />
      <el-table-column v-if="isAdmin" label="操作" width="140" fixed="right">
        <template slot-scope="{ row }">
          <el-button size="small" @click="showDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top: 16px; text-align: right;">
      <el-pagination
        :current-page="page"
        :page-size="size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="onSizeChange"
        @current-change="onPageChange"
      />
    </div>

    <el-dialog :title="editingId ? '编辑食物' : '添加食物'" :visible.sync="dialogVisible" width="520px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="食物名称" prop="name">
          <el-input v-model="form.name" placeholder="如：鸡胸肉" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="form.category" placeholder="如：肉类、蔬菜" />
        </el-form-item>
        <el-form-item label="热量(kcal/100g)" prop="caloriesPer100g">
          <el-input-number v-model="form.caloriesPer100g" :min="0" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="蛋白质(g/100g)">
          <el-input-number v-model="form.proteinPer100g" :min="0" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="脂肪(g/100g)">
          <el-input-number v-model="form.fatPer100g" :min="0" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="碳水(g/100g)">
          <el-input-number v-model="form.carbsPer100g" :min="0" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" placeholder="g / ml" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" />
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
import { getFoodList, getAllFoods, getFoodCategories, createFood, updateFood, deleteFood } from '@/api/food'

export default {
  name: 'FoodLibrary',
  data() {
    return {
      loading: false, saving: false,
      tableData: [], categories: [],
      keyword: '', category: '',
      page: 1, size: 20, total: 0,
      dialogVisible: false, editingId: null,
      form: { name: '', category: '', caloriesPer100g: 0, proteinPer100g: 0, fatPer100g: 0, carbsPer100g: 0, unit: 'g', description: '' },
      rules: {
        name: [{ required: true, message: '请输入食物名称', trigger: 'blur' }],
        category: [{ required: true, message: '请输入分类', trigger: 'blur' }],
        caloriesPer100g: [{ required: true, message: '请输入热量', trigger: 'blur' }]
      }
    }
  },
  computed: {
    isAdmin() {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      return userInfo.role === 'admin'
    }
  },
  created() {
    this.fetchData()
    this.loadCategories()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const res = await getFoodList({ keyword: this.keyword, category: this.category, page: this.page, size: this.size })
        if (res.code === 200) {
          this.tableData = res.data.records
          this.total = res.data.total
        }
      } finally { this.loading = false }
    },
    async loadCategories() {
      try {
        const res = await getFoodCategories()
        if (res.code === 200) this.categories = res.data
      } catch (e) {}
    },
    onSizeChange(val) { this.size = val; this.fetchData() },
    onPageChange(val) { this.page = val; this.fetchData() },
    showDialog(row) {
      this.editingId = row ? row.id : null
      if (row) {
        this.form = { ...row }
      } else {
        this.resetForm()
      }
      this.dialogVisible = true
    },
    resetForm() {
      this.editingId = null
      this.form = { name: '', category: '', caloriesPer100g: 0, proteinPer100g: 0, fatPer100g: 0, carbsPer100g: 0, unit: 'g', description: '' }
      this.$nextTick(() => { if (this.$refs.formRef) this.$refs.formRef.clearValidate() })
    },
    async handleSave() {
      const valid = await this.$refs.formRef.validate().catch(() => false)
      if (!valid) return
      this.saving = true
      try {
        if (this.editingId) {
          await updateFood(this.editingId, this.form)
          this.$message.success('修改成功')
        } else {
          await createFood(this.form)
          this.$message.success('添加成功')
        }
        this.dialogVisible = false
        this.fetchData()
        this.loadCategories()
      } finally { this.saving = false }
    },
    async handleDelete(id) {
      await this.$confirm('确定删除该食物吗？', '确认删除', { type: 'warning' })
      await deleteFood(id)
      this.$message.success('删除成功')
      this.fetchData()
    }
  }
}
</script>

<style scoped>
.food-library { padding: 20px; }
.page-title { margin-bottom: 20px; color: #303133; }
.search-bar { margin-bottom: 16px; display: flex; gap: 12px; flex-wrap: wrap; }
</style>
