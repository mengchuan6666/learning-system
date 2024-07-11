<template>
  <el-container>
    <el-header height="220">
      <div style="margin: 10px 0">
        <el-input style="width: 200px" placeholder="请输入名称" suffix-icon="el-icon-search" :key="searchChange" v-model="examName"></el-input>
        <el-button class="ml-5" type="primary" @click="getExamInfo">搜索</el-button>
        <el-button type="warning" @click="queryReset">重置</el-button>
      </div>
    </el-header>
    <el-main>
      <!-- 考试列表 -->
      <el-table
        ref="questionTable"
        highlight-current-row
        v-loading="loading"
        :border="true"
        :data="examInfo"
        tooltip-effect="dark"
        style="width: 100%;">
        <el-table-column align="center" label="考试名称">
          <template slot-scope="scope">
            {{ scope.row.name }}
          </template>
        </el-table-column>
        <el-table-column align="center" label="考试简介">
          <template slot-scope="scope">
            {{ scope.row.examDesc }}
          </template>
        </el-table-column>
        <el-table-column align="center" label="考试时间">
          <template slot-scope="scope">
            {{
              scope.row.startTime !== 'null' && scope.row.endTime !== 'null' ?
                scope.row.startTime + ' ~' + scope.row.endTime : '不限时'
            }}
          </template>
        </el-table-column>
        <el-table-column align="center" label="总分">
          <template slot-scope="scope">
            {{ scope.row.totalScore + '分' }}
          </template>
        </el-table-column>
        <el-table-column align="center" label="老师">
          <template slot-scope="scope">
            {{ scope.row.teacher }}
          </template>
        </el-table-column> 
        <el-table-column align="center" label="状态">
          <template slot-scope="scope">
            <!-- {{ scope.row.stateDesc +
              (scope.row.signDesc === '' ? '': ('(' + scope.row.markDesc + ')'))
            }} -->
             {{ scope.row.stateDesc }}
          </template>
        </el-table-column>
        <el-table-column align="center" label="操作">
          <template slot-scope="scope">
            <el-button size="small" v-if="scope.row.isAllowExam" @click="toStartExam(scope.row)"
                       :icon="'el-icon-caret-right'"
                       :type="'primary'">
              {{ '去考试' }}
            </el-button>
            <el-button size="small" v-if="scope.row.isAllowView" @click="toViewGrade(scope.row)"
                       :icon="'el-icon-caret-right'"
                       :type="'success'">
              {{ '查成绩' }}
            </el-button>
            <el-button type="primary" :icon="'el-icon-caret-right'" v-if="scope.row.isAllowSign" @click="sign(scope.row)">报名</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!--分页-->
      <el-pagination style="margin-top: 25px"
                     @size-change="handleSizeChange"
                     @current-change="handleCurrentChange"
                     :current-page="queryInfo.pageNo"
                     :page-sizes="[10, 20, 30, 50]"
                     :page-size="queryInfo.pageSize"
                     layout="total, sizes, prev, pager, next, jumper"
                     :total="total">
      </el-pagination>
    </el-main>
    <!-- 考试弹窗 -->
    <el-dialog
      title="考试提示"
      :visible.sync="startExamDialog" center
      width="50%">
      <el-alert
        title="点击`开始考试`后将自动进入考试，请诚信考试！"
        type="error">
      </el-alert>
      <el-card style="margin-top: 25px">
        <span>考试名称：</span>{{ currentSelectedExam.name }}
        <br>
        <span>考试描述：</span>{{ currentSelectedExam.examDesc }}
        <br>
        <span>及格分数：</span>{{ currentSelectedExam.passScore}}分
        <br>
        <span>试卷总分：</span>{{ currentSelectedExam.totalScore }}分
        <br>
      </el-card>
      <span slot="footer" class="dialog-footer">
    <el-button @click="startExamDialog = false">返 回</el-button>
    <el-button type="primary" @click="$router.push('/front/testpaper?examId=' + currentSelectedExam.id)">开始考试</el-button>
  </span>
    </el-dialog>
  </el-container>
</template>

<script>
import exam from '@/api/exam'
import "@/assets/css/front.css"
export default {
  name: 'ExamOnline',
  data () {
    return {
      examName:'',
      queryInfo: {
        'examName': null,
        'examType': null,
        'startTime': null,
        'endTime': null,
        'examName': null,
        'pageNo': 0,
        'pageSize': 10
      },
      //表格是否在加载
      loading: true,
      //考试类型信息
      examType: [
        {
          info: '公开考试',
          type: 1
        },
        {
          info: '需要密码',
          type: 2
        }
      ],
     
      //考试信息
      examInfo: [],
      //查询到的考试总数
      total: 0,
      //开始考试的提示框
      startExamDialog: false,
      //当前选中的考试的信息
      currentSelectedExam: {},
      searchChange:0,
    }
  },
  created () {

    this.getExamInfo()
  },
  methods: {
    sign(row) {
      let form = {examId: row.id}
      this.request.post("/sign", form).then(res => {
        if (res.code === '200') {
          this.$message.success("报名成功")
          this.getExamInfo(this.typeId)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    toStartExam(row) {
      this.currentSelectedExam = row;
      this.startExamDialog = true;
    },
    toViewGrade(row) {
      this.$router.push('/front/testGrade?examId=' + row.id)
    },
    //考试类型搜索
    typeChange (val) {
      this.queryInfo.examType = val
      this.getExamInfo()
    },
    //重置搜索框
    queryReset() {
      this.queryInfo = {
        'examType': null,
        'startTime': null,
        'endTime': null,
        'examName': null,
        'testType':null,
        'pageNo': 0,
        'pageSize': 10
      }
      
      this.getExamInfo()
    },
    //查询考试信息
    getExamInfo () {
      this.searchChange++;
      this.queryInfo.examName = this.examName;
      this.queryInfo.testType = this.$route.query.testType;      
      exam.getTestInfo(this.queryInfo).then((resp) => {
        if (resp.code === 200 || resp.code === "200") {
          this.examInfo = resp.data.records
          this.total = resp.data.total
          this.loading = false
        }
      })
    },
    //分页页面大小改变
    handleSizeChange (val) {
      this.queryInfo.pageSize = val
      this.getExamInfo()
    },
    //分页插件的页数
    handleCurrentChange (val) {
      this.queryInfo.pageNo = val
      this.getExamInfo()
    }
  }
}
</script>

<style lang="css" scoped>
.el-container {
  width: 100%;
  height: 100%;
}
.el-container {
  animation: leftMoveIn 0.7s ease-in;
}
@keyframes leftMoveIn {
  0% {
    transform: translateX(-100%);
    opacity: 0;
  }
  100% {
    transform: translateX(0%);
    opacity: 1;
  }
}
span {
  font-weight: 500;
  display: inline-block;
  font-size: 16px;
  padding: 0.5px ;
}


</style>
