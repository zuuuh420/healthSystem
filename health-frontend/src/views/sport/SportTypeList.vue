<template>
  <div class="sport-type-list">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="分类">
          <el-select v-model="searchForm.category" placeholder="请选择" clearable>
            <el-option label="有氧运动" value="有氧运动" />
            <el-option label="力量训练" value="力量训练" />
            <el-option label="柔韧拉伸" value="柔韧拉伸" />
            <el-option label="球类运动" value="球类运动" />
            <el-option label="水上运动" value="水上运动" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="搜索运动名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 运动类型列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>运动库</span>
          <el-button type="primary" @click="handleAdd">新增运动类型</el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="sportTypes"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="name" label="运动名称" width="150" />
        <el-table-column prop="category" label="分类" width="120">
          <template #default="{ row }">
            <el-tag :type="getCategoryTagType(row.category)">{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="caloriesPerMinute" label="每分钟消耗(千卡)" width="160" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空数据提示 -->
      <div v-if="!loading && sportTypes.length === 0" class="empty-state">
        <i class="el-icon-basketball"></i>
        <p>暂无运动类型数据</p>
        <el-button type="primary" @click="handleAdd">新增运动类型</el-button>
      </div>

      <!-- 分页 -->
      <el-pagination
        v-if="total > 0"
        class="pagination"
        :current-page="pageNum"
        :page-sizes="[10, 20, 50]"
        :page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        ref="sportTypeForm"
        :model="sportTypeForm"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="运动名称" prop="name">
          <el-input v-model="sportTypeForm.name" placeholder="请输入运动名称" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="sportTypeForm.category" placeholder="请选择分类">
            <el-option label="有氧运动" value="有氧运动" />
            <el-option label="力量训练" value="力量训练" />
            <el-option label="柔韧拉伸" value="柔韧拉伸" />
            <el-option label="球类运动" value="球类运动" />
            <el-option label="水上运动" value="水上运动" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="每分钟消耗(千卡)" prop="caloriesPerMinute">
          <el-input-number v-model="sportTypeForm.caloriesPerMinute" :min="0" :max="999" :precision="2" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="sportTypeForm.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getSportTypes, addSportType, updateSportType, deleteSportType } from '@/api/sport'

export default {
  name: 'SportTypeList',
  data() {
    return {
      loading: false,
      sportTypes: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      searchForm: {
        category: '',
        keyword: ''
      },
      dialogVisible: false,
      dialogTitle: '新增运动类型',
      submitLoading: false,
      sportTypeForm: {
        id: null,
        name: '',
        category: '',
        caloriesPerMinute: 5.00,
        description: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入运动名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        category: [
          { required: true, message: '请选择分类', trigger: 'change' }
        ],
        caloriesPerMinute: [
          { required: true, message: '请输入每分钟消耗卡路里', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.loadSportTypes()
  },
  methods: {
    // 加载运动类型列表
    async loadSportTypes() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
        if (this.searchForm.category) {
          params.category = this.searchForm.category
        }
        if (this.searchForm.keyword) {
          params.keyword = this.searchForm.keyword
        }

        const res = await getSportTypes(params)
        if (res.code === 200) {
          this.sportTypes = res.data.records
          this.total = res.data.total
        }
      } catch (error) {
        this.$message.error('加载运动类型失败')
      } finally {
        this.loading = false
      }
    },
    // 获取分类标签类型
    getCategoryTagType(category) {
      const map = {
        '有氧运动': 'success',
        '力量训练': 'danger',
        '柔韧拉伸': 'warning',
        '球类运动': 'primary',
        '水上运动': 'info'
      }
      return map[category] || ''
    },
    // 搜索
    handleSearch() {
      this.pageNum = 1
      this.loadSportTypes()
    },
    // 重置
    handleReset() {
      this.searchForm = {
        category: '',
        keyword: ''
      }
      this.handleSearch()
    },
    // 新增
    handleAdd() {
      this.dialogTitle = '新增运动类型'
      this.sportTypeForm = {
        id: null,
        name: '',
        category: '',
        caloriesPerMinute: 5.00,
        description: ''
      }
      this.dialogVisible = true
    },
    // 编辑
    handleEdit(row) {
      this.dialogTitle = '编辑运动类型'
      this.sportTypeForm = {
        id: row.id,
        name: row.name,
        category: row.category,
        caloriesPerMinute: row.caloriesPerMinute,
        description: row.description
      }
      this.dialogVisible = true
    },
    // 删除
    async handleDelete(row) {
      try {
        await this.$confirm('确定删除该运动类型吗？删除后相关运动记录的类型将显示为未知。', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const res = await deleteSportType(row.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadSportTypes()
        } else {
          this.$message.error(res.msg || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },
    // 提交表单
    handleSubmit() {
      this.$refs.sportTypeForm.validate(async (valid) => {
        if (!valid) return

        this.submitLoading = true
        try {
          let res
          if (this.sportTypeForm.id) {
            res = await updateSportType(this.sportTypeForm.id, this.sportTypeForm)
          } else {
            res = await addSportType(this.sportTypeForm)
          }

          if (res.code === 200) {
            this.$message.success(this.sportTypeForm.id ? '修改成功' : '新增成功')
            this.dialogVisible = false
            this.loadSportTypes()
          } else {
            this.$message.error(res.msg || '操作失败')
          }
        } catch (error) {
          this.$message.error('操作失败')
        } finally {
          this.submitLoading = false
        }
      })
    },
    // 关闭对话框
    handleDialogClose() {
      this.$refs.sportTypeForm && this.$refs.sportTypeForm.resetFields()
    },
    // 分页大小变化
    handleSizeChange(val) {
      this.pageSize = val
      this.loadSportTypes()
    },
    // 页码变化
    handleCurrentChange(val) {
      this.pageNum = val
      this.loadSportTypes()
    }
  }
}
</script>

<style scoped>
.sport-type-list {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
  color: #909399;
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state p {
  margin-bottom: 16px;
}
</style>
