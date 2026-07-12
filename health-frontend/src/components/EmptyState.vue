<template>
  <div class="empty-state">
    <div class="empty-icon">
      <i :class="icon"></i>
    </div>
    <p class="empty-text">{{ text }}</p>
    <el-button v-if="showAction" type="primary" size="small" @click="$emit('action')">
      {{ actionText }}
    </el-button>
  </div>
</template>

<script>
export default {
  name: 'EmptyState',
  props: {
    icon: {
      type: String,
      default: 'el-icon-document'
    },
    text: {
      type: String,
      default: '暂无数据'
    },
    showAction: {
      type: Boolean,
      default: false
    },
    actionText: {
      type: String,
      default: '去添加'
    }
  }
}
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}

.empty-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.empty-icon i {
  font-size: 40px;
  color: #c0c4cc;
}

.empty-text {
  color: #909399;
  font-size: 14px;
  margin-bottom: 20px;
}
</style>
