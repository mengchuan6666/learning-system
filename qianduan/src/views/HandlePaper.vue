<template>
  <div>
    <el-table :data="paper" border stripe :header-cell-class-name="'headerBg'">
      <el-table-column prop="questionName" label="题目名称"></el-table-column>
      <el-table-column prop="questionType" label="题目类型">
        <template v-slot="scope">
          <span v-if="scope.row.type === 1">选择题</span>
          <span v-if="scope.row.type === 2">判断题</span>
          <span v-if="scope.row.type === 3">问答题</span>
        </template>
      </el-table-column>
      <el-table-column prop="score" label="标准分数"></el-table-column>
      <el-table-column prop="standardAnswer" label="标准答案"></el-table-column>
      <el-table-column prop="studentAnswer" label="学生答案"></el-table-column>
      <el-table-column prop="questionDetail" label="解析"></el-table-column>
      <el-table-column prop="markScore" label="得分">
        <template v-slot="scope">
          <el-input v-model="scope.row.markScore"></el-input>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注">
        <template v-slot="scope">
          <el-input v-model="scope.row.remark"></el-input>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin: 20px 0">
      <el-button type="primary" @click="submitScore">提 交</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: "HandlePaper",
  data() {
    return {
      sp: this.$route.query.sp,
      paper: []
    }
  },
  created() {
    this.request.get("/studentpaper/" + this.sp).then(res => {
      this.paper = res.data.answerList
      if (this.paper && this.paper.length) {
        this.paper.forEach(item => {  // 自动阅卷
          if (item.answer === item.studentAnswer) {
            item.markScore = item.score
          } else {
            item.markScore = 0
          }
        })
      }
    })
  },
  methods: {
    submitScore() {
      let sum = 0
      this.paper.forEach(item => {
       
        if (item.markScore == null) {
          item.markScore = 0
        }
        sum += parseInt(item.markScore)
      })
      this.request.put("/studentpaper/"+this.sp+'/mark', this.paper).then(res => {
        this.$message.success("打分成功")
        //this.$router.push("/studentpaper")
      })
    }
  }
}
</script>

<style scoped>

</style>