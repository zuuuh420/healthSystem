<template>
  <div class="sport-record-list">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
          />
        </el-form-item>
        <el-form-item label="运动类型">
          <el-select v-model="searchForm.sportTypeId" placeholder="请选择" clearable>
            <el-option
              v-for="item in sportTypes"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>运动记录</span>
          <el-button type="primary" @click="handleAdd">新增记录</el-button>
        </div>
      </template>

      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="records"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="sportTypeName" label="运动类型" width="120" />
        <el-table-column prop="sportCategory" label="分类" width="100" />
        <el-table-column prop="durationMinutes" label="时长(分钟)" width="120" />
        <el-table-column prop="caloriesBurned" label="消耗卡路里" width="120">
          <template #default="{ row }">
            {{ row.caloriesBurned ? row.caloriesBurned.toFixed(2) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="sportDate" label="运动日期" width="120" />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空数据提示 -->
      <empty-state
        v-if="!loading && records.length === 0"
        icon="el-icon-basketball"
        text="暂无运动记录，快去运动吧！"
        show-action
        action-text="新增记录"
        @action="handleAdd"
      />

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
  </div>
</template>

<script>
import { getSportRecords, deleteSportRecord, getSportTypes } from '@/api/sport'
import EmptyState from '@/components/EmptyState.vue'

export default {
  name: 'SportRecordList',
  components: { EmptyState },
  data() {
    return {
      loading: false,
      records: [],
      sportTypes: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      searchForm: {
        dateRange: [],
        sportTypeId: null
      }
    }
  },
  created() {
    this.loadSportTypes()
    this.loadRecords()
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
    // 加载运动记录
    async loadRecords() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
        if (this.searchForm.dateRange && this.searchForm.dateRange.length === 2) {
          params.startDate = this.searchForm.dateRange[0]
          params.endDate = this.searchForm.dateRange[1]
        }
        if (this.searchForm.sportTypeId) {
          params.sportTypeId = this.searchForm.sportTypeId
        }

        const res = await getSportRecords(params)
        if (res.code === 200) {
          this.records = res.data.records
          this.total = res.data.total
        }
      } catch (error) {
        this.$message.error('加载运动记录失败')
      } finally {
        this.loading = false
      }
    },
    // 搜索
    handleSearch() {
      this.pageNum = 1
      this.loadRecords()
    },
    // 重置
    handleReset() {
      this.searchForm = {
        dateRange: [],
        sportTypeId: null
      }
      this.handleSearch()
    },
    // 新增
    handleAdd() {
      this.$router.push('/sport/records/add')
    },
    // 编辑
    handleEdit(row) {
      this.$router.push(`/sport/records/edit/${row.id}`)
    },
    // 删除
    async handleDelete(row) {
      try {
        await this.$confirm('确定删除该运动记录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const res = await deleteSportRecord(row.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadRecords()
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
      this.loadRecords()
    },
    // 页码变化
    handleCurrentChange(val) {
      this.pageNum = val
      this.loadRecords()
    }
  }
}
</script>

<style scoped>
.sport-record-list {
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
</style>
