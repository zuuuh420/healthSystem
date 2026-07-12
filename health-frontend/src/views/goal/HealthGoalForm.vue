<template>
  <div class="health-goal-form">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑健康目标' : '创建健康目标' }}</span>
        </div>
      </template>

      <el-form
        ref="form"
        :model="form"
        :rules="rules"
        label-width="120px"
        v-loading="loading"
      >
        <el-form-item label="目标类型" prop="goalType">
          <el-select v-model="form.goalType" placeholder="请选择目标类型">
            <el-option label="体重目标" value="weight" />
            <el-option label="运动目标" value="exercise" />
            <el-option label="步数目标" value="steps" />
          </el-select>
        </el-form-item>

        <el-form-item label="目标名称" prop="goalName">
          <el-input v-model="form.goalName" placeholder="请输入目标名称" />
        </el-form-item>

        <el-form-item label="目标值" prop="targetValue">
          <el-input-number
            v-model="form.targetValue"
            :min="0.01"
            :precision="2"
            placeholder="请输入目标值"
          />
          <span class="unit">{{ unitPlaceholder }}</span>
        </el-form-item>

        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" :placeholder="unitPlaceholder" />
        </el-form-item>

        <el-form-item label="目标周期">
          <el-date-picker
            v-model="form.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '保存修改' : '立即创建' }}
          </el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getHealthGoals, addHealthGoal, updateHealthGoal } from '@/api/goal'

export default {
  name: 'HealthGoalForm',
  data() {
    return {
      loading: false,
      submitting: false,
      isEdit: false,
      form: {
        goalType: '',
        goalName: '',
        targetValue: null,
        unit: '',
        dateRange: []
      },
      rules: {
        goalType: [
          { required: true, message: '请选择目标类型', trigger: 'change' }
        ],
        goalName: [
          { required: true, message: '请输入目标名称', trigger: 'blur' },
          { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        targetValue: [
          { required: true, message: '请输入目标值', trigger: 'blur' }
        ],
        unit: [
          { required: true, message: '请输入单位', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    unitPlaceholder() {
      const unitMap = {
        weight: 'kg',
        exercise: '分钟',
        steps: '步'
      }
      return unitMap[this.form.goalType] || '单位'
    }
  },
  watch: {
    'form.goalType'(val) {
      if (!this.isEdit) {
        const nameMap = {
          weight: '减重目标',
          exercise: '运动目标',
          steps: '步数目标'
        }
        this.form.goalName = nameMap[val] || ''
        this.form.unit = this.unitPlaceholder
      }
    }
  },
  created() {
    // 判断是否是编辑模式
    if (this.$route.params.id) {
      this.isEdit = true
      this.loadGoal(this.$route.params.id)
    }
  },
  methods: {
    // 加载目标详情
    async loadGoal(id) {
      this.loading = true
      try {
        const res = await getHealthGoals({ pageNum: 1, pageSize: 100 })
        if (res.code === 200) {
          const goal = res.data.records.find(g => g.id === parseInt(id))
          if (goal) {
            this.form = {
              goalType: goal.goalType,
              goalName: goal.goalName,
              targetValue: goal.targetValue,
              unit: goal.unit,
              dateRange: goal.startDate ? [goal.startDate, goal.endDate] : []
            }
          } else {
            this.$message.error('健康目标不存在')
            this.$router.go(-1)
          }
        }
      } catch (error) {
        this.$message.error('加载健康目标失败')
      } finally {
        this.loading = false
      }
    },
    // 提交表单
    handleSubmit() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          this.submitting = true
          try {
            const data = {
              goalType: this.form.goalType,
              goalName: this.form.goalName,
              targetValue: this.form.targetValue,
              unit: this.form.unit
            }

            if (this.form.dateRange && this.form.dateRange.length === 2) {
              data.startDate = this.form.dateRange[0]
              data.endDate = this.form.dateRange[1]
            }

            let res
            if (this.isEdit) {
              res = await updateHealthGoal(this.$route.params.id, data)
            } else {
              res = await addHealthGoal(data)
            }

            if (res.code === 200) {
              this.$message.success(this.isEdit ? '修改成功' : '创建成功')
              this.$router.push('/goal/list')
            } else {
              this.$message.error(res.msg || '操作失败')
            }
          } catch (error) {
            this.$message.error('操作失败')
          } finally {
            this.submitting = false
          }
        }
      })
    },
    // 取消
    handleCancel() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.health-goal-form {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.unit {
  margin-left: 10px;
  color: #606266;
}
</style>
