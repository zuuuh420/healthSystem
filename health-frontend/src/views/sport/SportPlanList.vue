<template>
  <div class="sport-plan-list">
    <!-- 筛选区域 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="进行中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已过期" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 运动计划列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>运动计划</span>
          <el-button type="primary" @click="handleAdd">创建计划</el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="plans"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="planName" label="计划名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="sportTypeName" label="运动类型" width="120" />
        <el-table-column prop="targetMinutes" label="目标时长(分钟)" width="140" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空数据提示 -->
      <div v-if="!loading && plans.length === 0" class="empty-state">
        <i class="el-icon-date"></i>
        <p>暂无运动计划，制定一个计划开始锻炼吧！</p>
        <el-button type="primary" @click="handleAdd">创建计划</el-button>
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
      width="550px"
      @close="handleDialogClose"
    >
      <el-form
        ref="planForm"
        :model="planForm"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="计划名称" prop="planName">
          <el-input v-model="planForm.planName" placeholder="请输入计划名称" />
        </el-form-item>
        <el-form-item label="运动类型" prop="sportTypeId">
          <el-select v-model="planForm.sportTypeId" placeholder="请选择运动类型" filterable>
            <el-option
              v-for="item in sportTypes"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="目标时长(分钟)" prop="targetMinutes">
          <el-input-number v-model="planForm.targetMinutes" :min="1" :max="9999" />
        </el-form-item>
        <el-form-item label="计划周期" prop="dateRange">
          <el-date-picker
            v-model="planForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="planForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
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
import { getSportPlans, addSportPlan, updateSportPlan, deleteSportPlan, getSportTypes } from '@/api/sport'

export default {
  name: 'SportPlanList',
  data() {
    return {
      loading: false,
      plans: [],
      sportTypes: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      searchForm: {
        status: null
      },
      dialogVisible: false,
      dialogTitle: '创建运动计划',
      submitLoading: false,
      planForm: {
        id: null,
        planName: '',
        sportTypeId: null,
        targetMinutes: 30,
        dateRange: [],
        remark: ''
      },
      rules: {
        planName: [
          { required: true, message: '请输入计划名称', trigger: 'blur' },
          { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        sportTypeId: [
          { required: true, message: '请选择运动类型', trigger: 'change' }
        ],
        targetMinutes: [
          { required: true, message: '请输入目标时长', trigger: 'blur' }
        ],
        dateRange: [
          { required: true, message: '请选择计划周期', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.loadSportTypes()
    this.loadPlans()
  },
  methods: {
    // 加载运动类型
    async loadSportTypes() {
      try {
        const res = await getSportTypes({ pageSize: 100 })
        if (res.code === 200) {
          this.sportTypes = res.data.records
        }
      } catch (error) {
        console.error('加载运动类型失败', error)
      }
    },
    // 加载运动计划列表
    async loadPlans() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
        if (this.searchForm.status !== null) {
          params.status = this.searchForm.status
        }

        const res = await getSportPlans(params)
        if (res.code === 200) {
          this.plans = res.data.records
          this.total = res.data.total
        }
      } catch (error) {
        this.$message.error('加载运动计划失败')
      } finally {
        this.loading = false
      }
    },
    // 获取状态类型
    getStatusType(status) {
      const map = {
        1: 'primary',
        2: 'success',
        3: 'info'
      }
      return map[status] || ''
    },
    // 获取状态文本
    getStatusText(status) {
      const map = {
        1: '进行中',
        2: '已完成',
        3: '已过期'
      }
      return map[status] || '未知'
    },
    // 搜索
    handleSearch() {
      this.pageNum = 1
      this.loadPlans()
    },
    // 重置
    handleReset() {
      this.searchForm = {
        status: null
      }
      this.handleSearch()
    },
    // 新增
    handleAdd() {
      this.dialogTitle = '创建运动计划'
      this.planForm = {
        id: null,
        planName: '',
        sportTypeId: null,
        targetMinutes: 30,
        dateRange: [],
        remark: ''
      }
      this.dialogVisible = true
    },
    // 编辑
    handleEdit(row) {
      this.dialogTitle = '编辑运动计划'
      this.planForm = {
        id: row.id,
        planName: row.planName,
        sportTypeId: row.sportTypeId,
        targetMinutes: row.targetMinutes,
        dateRange: [row.startDate, row.endDate],
        remark: row.remark
      }
      this.dialogVisible = true
    },
    // 删除
    async handleDelete(row) {
      try {
        await this.$confirm('确定删除该运动计划吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const res = await deleteSportPlan(row.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadPlans()
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
      this.$refs.planForm.validate(async (valid) => {
        if (!valid) return

        this.submitLoading = true
        try {
          const data = {
            planName: this.planForm.planName,
            sportTypeId: this.planForm.sportTypeId,
            targetMinutes: this.planForm.targetMinutes,
            startDate: this.planForm.dateRange[0],
            endDate: this.planForm.dateRange[1],
            remark: this.planForm.remark
          }

          let res
          if (this.planForm.id) {
            res = await updateSportPlan(this.planForm.id, data)
          } else {
            res = await addSportPlan(data)
          }

          if (res.code === 200) {
            this.$message.success(this.planForm.id ? '修改成功' : '创建成功')
            this.dialogVisible = false
            this.loadPlans()
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
      this.$refs.planForm && this.$refs.planForm.resetFields()
    },
    // 分页大小变化
    handleSizeChange(val) {
      this.pageSize = val
      this.loadPlans()
    },
    // 页码变化
    handleCurrentChange(val) {
      this.pageNum = val
      this.loadPlans()
    }
  }
}
</script>

<style scoped>
.sport-plan-list {
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
