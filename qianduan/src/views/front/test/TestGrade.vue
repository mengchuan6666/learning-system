<template>
  <div style="margin-bottom: 100px; max-width: 50%; left: 20%; position:relative;">
    <div style="margin: 20px 0">
      <span style="font-size: 20px; color: #333">{{ exam.name }}</span>
      <span style="font-size: 14px; color: #333; margin-left: 20px">及格分数：{{ exam.passScore }} 分</span>
      <span style="font-size: 14px; color: #333; margin-left: 20px">总分：{{ exam.totalScore }} 分</span>
      <span style="font-size: 14px; color: red; margin-left: 20px">得分：{{ grade.markScore }} 分</span>
      <span style="font-size: 14px; color: #333; margin-left: 20px">老师：{{ exam.teacher }}</span>
    </div>
   <div style="margin: 10px 0; text-align:left">
     <el-card>
       <div v-for="(item, index) in grade.answerList" :key="item.id"  style="margin: 20px 0">
         <div style="margin: 10px 0; font-size: 20px"><span>{{ index+1 }}.</span> 
          <span style="font-size: 14px" > {{ item.questionName + ' ('}}</span>
          <span style="font-size: 14px; color: red;" >{{ item.markScore}} </span>
           <span style="font-size: 14px" >{{ '/' + item.score + '分)'}}</span>
           <span style="font-size: 14px" v-if="item.questionType === 1">（选择题）</span>
           <span style="font-size: 14px"  v-if="item.questionType === 2">（判断题）</span>
           <span style="font-size: 14px"  v-if="item.questionType === 3">（问答题）</span>
         </div>
         <div v-if="item.questionType === 1" style="margin: 10px">
           <span style="margin-right: 20px"><el-radio disabled v-model="item.studentAnswer" label="A">A. {{ item.a }}</el-radio></span>
           <span  style="margin-right: 20px"><el-radio disabled v-model="item.studentAnswer" label="B">B. {{ item.b }}</el-radio></span>
           <span  style="margin-right: 20px"><el-radio disabled v-model="item.studentAnswer" label="C">C. {{ item.c }}</el-radio></span>
           <span  style="margin-right: 20px"><el-radio disabled v-model="item.studentAnswer" label="D">D. {{ item.d }}</el-radio></span>
         </div>
         <div v-if="item.questionType === 2" style="margin: 10px">
           <span style="margin-right: 20px"><el-radio disabled v-model="item.studentAnswer" label="是">是</el-radio></span>
           <span  style="margin-right: 20px"><el-radio disabled v-model="item.studentAnswer" label="否">否</el-radio></span>
         </div>
         <div v-if="item.questionType === 3" style="margin: 10px">
           <el-input type="textarea" style="width:60%" disabled v-model="item.studentAnswer"></el-input>
         </div>
         <div style="margin: 10px 0; font-size: 14px; color: #333; max-width:60%; ">正确答案：</div>
         <div  style="margin: 10px 0; width:60%; "><el-input disabled v-model="item.standardAnswer" label="standardAnswer"></el-input></div>
         <div style="margin: 10px 0; font-size: 14px; width:60%; color: #333; ">评语：</div>
         <div  style="margin: 10px 0; width:60%"><el-input disabled v-model="item.remark" label="remark"></el-input></div>
       </div>
     </el-card>
     <div style="margin: 20px; text-align: center">
       <el-button size="medium" type="warning" @click="$router.push('/front/testOnline')">返回</el-button>
     </div>
   </div>
  </div>
</template>

<script>
export default {
  name: "Paper",
  data() {
    return {
      grade: {},
      examId: this.$route.query.examId,
      exam: {}
    }
  },
  created() {
    this.request.get("/studentpaper/grade/" + this.examId).then(res => {
      this.grade = res.data
    })

    this.request.get("/testGrade/" + this.examId).then(res => {
      this.exam = res.data
    })
  },
  methods: {
  }
}
</script>

<style scoped>

</style>