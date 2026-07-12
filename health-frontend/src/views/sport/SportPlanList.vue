<template>
  <section class="module-page">
    <header><div><h1>运动计划</h1><p>按周期安排运动内容，建立更稳定的执行节奏。</p></div></header>
    <el-card><el-table v-loading="loading" :data="items" stripe>
      <el-table-column prop="planName" label="计划名称" min-width="160"/><el-table-column prop="sportTypeName" label="运动类型" width="130"/>
      <el-table-column prop="durationMinutes" label="单次时长" width="120"><template slot-scope="scope">{{ scope.row.durationMinutes }} 分钟</template></el-table-column>
      <el-table-column prop="frequencyPerWeek" label="每周频次" width="120"><template slot-scope="scope">{{ scope.row.frequencyPerWeek }} 次</template></el-table-column>
      <el-table-column prop="startDate" label="开始日期" width="120"/><el-table-column prop="endDate" label="结束日期" width="120"/>
    </el-table><el-empty v-if="!loading && !items.length" description="暂时没有运动计划"/></el-card>
  </section>
</template>
<script>
import { getSportPlans } from '@/api/sport'
export default { name:'SportPlanList', data:()=>({loading:false,items:[]}), created(){this.load()}, methods:{async load(){this.loading=true;try{const res=await getSportPlans({pageNum:1,pageSize:100});this.items=(res.data&&res.data.records)||res.data||[]}finally{this.loading=false}}} }
</script>
<style scoped>.module-page{max-width:1100px;margin:0 auto}.module-page header{margin-bottom:20px}.module-page h1{font-size:27px;margin-bottom:7px}.module-page p{color:#7d8983;line-height:1.5}</style>
