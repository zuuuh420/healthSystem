<template>
  <div class="sport-record-form">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑运动记录' : '新增运动记录' }}</span>
        </div>
      </template>

      <el-form
        ref="form"
        :model="form"
        :rules="rules"
        label-width="120px"
        v-loading="loading"
      >
        <el-form-item label="运动类型" prop="sportTypeId">
          <el-select v-model="form.sportTypeId" placeholder="请选择运动类型" @change="handleSportTypeChange">
            <el-option
              v-for="item in sportTypes"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
              <span>{{ item.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ item.category }}</span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="运动时长" prop="durationMinutes">
          <el-input-number
            v-model="form.durationMinutes"
            :min="1"
            :max="999"
            placeholder="请输入运动时长"
            @change="calculateCalories"
          />
          <span class="unit">分钟</span>
        </el-form-item>

        <el-form-item label="消耗卡路里">
          <el-input :value="caloriesDisplay" disabled>
            <template slot="suffix">千卡</template>
          </el-input>
          <span class="hint">根据运动类型和时长自动计算</span>
        </el-form-item>

        <el-form-item label="运动日期" prop="sportDate">
          <el-date-picker
            v-model="form.sportDate"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
            :picker-options="pickerOptions"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注（选填）"
            maxlength="500"
            show-word-limit
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
import { getSportRecords, addSportRecord, updateSportRecord, getSportTypes } from '@/api/sport'

export default {
  name: 'SportRecordForm',
  data() {
    return {
      loading: false,
      submitting: false,
      isEdit: false,
      sportTypes: [],
      selectedSportType: null,
      form: {
        sportTypeId: null,
        durationMinutes: 30,
        sportDate: '',
        remark: ''
      },
      rules: {
        sportTypeId: [
          { required: true, message: '请选择运动类型', trigger: 'change' }
        ],
        durationMinutes: [
          { required: true, message: '请输入运动时长', trigger: 'blur' },
          { type: 'number', min: 1, message: '时长必须大于0', trigger: 'blur' }
        ],
        sportDate: [
          { required: true, message: '请选择运动日期', trigger: 'change' }
        ]
      },
      pickerOptions: {
        disabledDate(date) {
          // 不能选择30天前的日期
          const thirtyDaysAgo = new Date()
          thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30)
          return date < thirtyDaysAgo
        }
      }
    }
  },
  computed: {
    caloriesDisplay() {
      if (this.selectedSportType && this.form.durationMinutes) {
        const calories = this.selectedSportType.caloriesPerMinute * this.form.durationMinutes
        return calories.toFixed(2)
      }
      return '0.00'
    }
  },
  created() {
    this.loadSportTypes()
    // 判断是否是编辑模式
    if (this.$route.params.id) {
      this.isEdit = true
      this.loadRecord(this.$route.params.id)
    } else {
      // 新增模式，默认今天
      this.form.sportDate = new Date().toISOString().split('T')[0]
    }
  },
  methods: {
    // 加载运动类型列表
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
    // 加载运动记录详情
    async loadRecord(id) {
      this.loading = true
      try {
        const res = await getSportRecords({ pageNum: 1, pageSize: 100 })
        if (res.code === 200) {
          const record = res.data.records.find(r => r.id === parseInt(id))
          if (record) {
            this.form = {
              sportTypeId: record.sportTypeId,
              durationMinutes: record.durationMinutes,
              sportDate: record.sportDate,
              remark: record.remark || ''
            }
            this.selectedSportType = this.sportTypes.find(t => t.id === record.sportTypeId)
          } else {
            this.$message.error('运动记录不存在')
            this.$router.go(-1)
          }
        }
      } catch (error) {
        this.$message.error('加载运动记录失败')
      } finally {
        this.loading = false
      }
    },
    // 运动类型变化
    handleSportTypeChange(val) {
      this.selectedSportType = this.sportTypes.find(t => t.id === val)
      this.calculateCalories()
    },
    // 计算卡路里（用于显示）
    calculateCalories() {
      // 由 computed 自动计算
    },
    // 提交表单
    handleSubmit() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          this.submitting = true
          try {
            let res
            if (this.isEdit) {
              res = await updateSportRecord(this.$route.params.id, this.form)
            } else {
              res = await addSportRecord(this.form)
            }

            if (res.code === 200) {
              this.$message.success(this.isEdit ? '修改成功' : '新增成功')
              this.$router.push('/sport/records')
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
.sport-record-form {
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

.hint {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}
</style>
