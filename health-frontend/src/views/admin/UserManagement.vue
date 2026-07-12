<template>
  <div class="user-management">
    <h2 class="page-title">用户管理</h2>

    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索用户名/昵称" style="width: 240px" clearable @keyup.enter.native="fetchData" />
      <el-button type="primary" @click="fetchData">搜索</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="用户名" width="140" />
      <el-table-column prop="nickname" label="昵称" width="140" />
      <el-table-column prop="role" label="角色" width="100">
        <template slot-scope="{ row }">
          <el-tag :type="row.role === 'admin' ? 'danger' : 'primary'" size="small">
            {{ row.role === 'admin' ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="email" label="邮箱" width="180" show-overflow-tooltip />
      <el-table-column prop="createTime" label="注册时间" width="170" />
      <el-table-column label="操作" width="260" fixed="right">
        <template slot-scope="{ row }">
          <el-button size="small" @click="toggleRole(row)" :type="row.role === 'admin' ? 'warning' : 'success'">
            {{ row.role === 'admin' ? '降为用户' : '升为管理员' }}
          </el-button>
          <el-button size="small" @click="handleResetPwd(row.id)">重置密码</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)" :disabled="row.role === 'admin'">删除</el-button>
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
  </div>
</template>

<script>
import { getUserList, updateUserRole, deleteUser, resetUserPassword } from '@/api/admin'

export default {
  name: 'UserManagement',
  data() {
    return {
      loading: false, tableData: [],
      keyword: '', page: 1, size: 20, total: 0
    }
  },
  created() { this.fetchData() },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const res = await getUserList({ keyword: this.keyword, page: this.page, size: this.size })
        if (res.code === 200) { this.tableData = res.data.records; this.total = res.data.total }
      } finally { this.loading = false }
    },
    onSizeChange(val) { this.size = val; this.fetchData() },
    onPageChange(val) { this.page = val; this.fetchData() },
    async toggleRole(row) {
      const newRole = row.role === 'admin' ? 'user' : 'admin'
      const action = newRole === 'admin' ? '升级为管理员' : '降级为普通用户'
      await this.$confirm(`确定将"${row.nickname}"${action}吗？`, '确认操作', { type: 'warning' })
      await updateUserRole(row.id, newRole)
      this.$message.success('操作成功')
      this.fetchData()
    },
    async handleResetPwd(id) {
      await this.$confirm('确定重置该用户的密码为"123456"吗？', '确认重置', { type: 'warning' })
      await resetUserPassword(id)
      this.$message.success('密码已重置为123456')
    },
    async handleDelete(row) {
      await this.$confirm(`确定删除用户"${row.nickname}"吗？此操作不可恢复。`, '确认删除', { type: 'warning' })
      await deleteUser(row.id)
      this.$message.success('删除成功')
      this.fetchData()
    }
  }
}
</script>

<style scoped>
.user-management { padding: 20px; }
.page-title { margin-bottom: 20px; color: #303133; }
.search-bar { margin-bottom: 16px; display: flex; gap: 12px; }
</style>
