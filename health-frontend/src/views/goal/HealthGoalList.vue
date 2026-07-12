<template>
  <div class="health-goal-list">
    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="全部" clearable>
            <el-option label="进行中" :value="0" />
            <el-option label="已完成" :value="1" />
            <el-option label="已过期" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标类型">
          <el-select v-model="filterForm.goalType" placeholder="全部" clearable>
            <el-option label="体重" value="weight" />
            <el-option label="运动" value="exercise" />
            <el-option label="步数" value="steps" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 目标卡片列表 -->
    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span>健康目标</span>
          <el-button type="primary" @click="handleAdd">创建目标</el-button>
        </div>
      </template>

      <div v-loading="loading" class="goal-cards">
        <el-row :gutter="20">
          <el-col :span="8" v-for="goal in goals" :key="goal.id">
            <el-card class="goal-card" shadow="hover">
              <div class="goal-header">
                <span class="goal-type">{{ getGoalTypeLabel(goal.goalType) }}</span>
                <el-tag :type="getStatusType(goal.status)" size="small">
                  {{ getStatusLabel(goal.status) }}
                </el-tag>
              </div>
              <h3 class="goal-name">{{ goal.goalName }}</h3>
              <div class="goal-progress">
                <div class="progress-info">
                  <span>进度：{{ goal.currentValue }} / {{ goal.targetValue }} {{ goal.unit }}</span>
                  <span>{{ goal.progress ? goal.progress.toFixed(1) : 0 }}%</span>
                </div>
                <el-progress
                  :percentage="goal.progress || 0"
                  :status="goal.status === 1 ? 'success' : ''"
                />
              </div>
              <div class="goal-dates" v-if="goal.startDate">
                <span>{{ goal.startDate }} 至 {{ goal.endDate }}</span>
              </div>
              <div class="goal-actions">
                <el-button size="small" type="primary" @click="handleEdit(goal)">编辑</el-button>
                <el-button size="small" type="danger" @click="handleDelete(goal)">删除</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 空数据提示 -->
        <el-empty v-if="!loading && goals.length === 0" description="暂无健康目标" />
      </div>

      <!-- 分页 -->
      <el-pagination
        v-if="total > 0"
        class="pagination"
        :current-page="pageNum"
        :page-sizes="[6, 12, 24]"
        :page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
  </div>
</template>

<script>
import { getHealthGoals, deleteHealthGoal } from '@/api/goal'

export default {
  name: 'HealthGoalList',
  data() {
    return {
      loading: false,
      goals: [],
      total: 0,
      pageNum: 1,
      pageSize: 6,
      filterForm: {
        status: null,
        goalType: null
      }
    }
  },
  created() {
    this.loadGoals()
  },
  methods: {
    // 加载目标列表
    async loadGoals() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
        if (this.filterForm.status !== null && this.filterForm.status !== '') {
          params.status = this.filterForm.status
        }
        if (this.filterForm.goalType) {
          params.goalType = this.filterForm.goalType
        }

        const res = await getHealthGoals(params)
        if (res.code === 200) {
          this.goals = res.data.records
          this.total = res.data.total
        }
      } catch (error) {
        this.$message.error('加载健康目标失败')
      } finally {
        this.loading = false
      }
    },
    // 搜索
    handleSearch() {
      this.pageNum = 1
      this.loadGoals()
    },
    // 重置
    handleReset() {
      this.filterForm = {
        status: null,
        goalType: null
      }
      this.handleSearch()
    },
    // 新增
    handleAdd() {
      this.$router.push('/goal/add')
    },
    // 编辑
    handleEdit(goal) {
      this.$router.push(`/goal/edit/${goal.id}`)
    },
    // 删除
    async handleDelete(goal) {
      try {
        await this.$confirm('确定删除该健康目标吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const res = await deleteHealthGoal(goal.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadGoals()
        } else {
          this.$message.error(res.msg || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },
    // 分页大小变化
    handleSizeChange(val) {
      this.pageSize = val
      this.loadGoals()
    },
    // 页码变化
    handleCurrentChange(val) {
      this.pageNum = val
      this.loadGoals()
    },
    // 获取目标类型标签
    getGoalTypeLabel(type) {
      const typeMap = {
        weight: '体重',
        exercise: '运动',
        steps: '步数'
      }
      return typeMap[type] || type
    },
    // 获取状态标签
    getStatusLabel(status) {
      const statusMap = {
        0: '进行中',
        1: '已完成',
        2: '已过期'
      }
      return statusMap[status] || '未知'
    },
    // 获取状态类型
    getStatusType(status) {
      const typeMap = {
        0: 'primary',
        1: 'success',
        2: 'danger'
      }
      return typeMap[status] || 'info'
    }
  }
}
</script>

<style scoped>
.health-goal-list {
  padding: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.goal-cards {
  min-height: 200px;
}

.goal-card {
  margin-bottom: 20px;
}

.goal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.goal-type {
  font-size: 12px;
  color: #909399;
  background: #f5f7fa;
  padding: 2px 8px;
  border-radius: 4px;
}

.goal-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin: 10px 0;
}

.goal-progress {
  margin: 15px 0;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 13px;
  color: #606266;
}

.goal-dates {
  font-size: 12px;
  color: #909399;
  margin-bottom: 15px;
}

.goal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
