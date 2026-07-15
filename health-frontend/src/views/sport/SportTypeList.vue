<template>
  <section class="module-page">
    <header><div><h1>运动库</h1><p>选择运动时将依据这里的单位消耗自动估算卡路里。</p></div></header>
    <el-card><el-table v-loading="loading" :data="items" stripe>
      <el-table-column prop="name" label="运动名称" min-width="140"/><el-table-column prop="category" label="分类" width="120"/>
      <el-table-column prop="caloriesPerMinute" label="每分钟消耗" width="140"><template slot-scope="scope">{{ scope.row.caloriesPerMinute }} kcal</template></el-table-column>
      <el-table-column prop="description" label="说明" min-width="220" show-overflow-tooltip/>
    </el-table><el-empty v-if="!loading && !items.length" description="运动库暂无数据"/></el-card>
  </section>
</template>
<script>
import { getSportTypes } from '@/api/sport'
export default { name:'SportTypeList', data:()=>({loading:false,items:[]}), created(){this.load()}, methods:{async load(){this.loading=true;try{const res=await getSportTypes({pageNum:1,pageSize:100});this.items=(res.data&&res.data.records)||res.data||[]}finally{this.loading=false}}} }
</script>
<style scoped>.module-page{max-width:1100px;margin:0 auto}.module-page header{margin-bottom:20px}.module-page h1{font-size:27px;margin-bottom:7px}.module-page p{color:#7d8983;line-height:1.5}</style>
