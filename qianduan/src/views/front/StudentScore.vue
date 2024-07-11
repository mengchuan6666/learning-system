<template>
    <el-card>
    <vue-particles
          class="lizibackground"
          color="#97D0F2"
          :particleOpacity="1"
          :colorVariance="10" 
          :particlesNumber="50"
          shapeType="circle"
          :particleSize="4"
          linesColor="#97D0F2"
          :linesWidth="1"
          :lineLinked="true"
          :lineOpacity="0.4"
          :linesDistance="150"
          :moveSpeed="3"
          :hoverEffect="true"
          hoverMode="grab"
          :clickEffect="true"
          clickMode="push">
        </vue-particles>
 
  <div>
   <div style="margin: 10px 0">
      <el-input style="width: 200px" placeholder="请输入名称" suffix-icon="el-icon-search" v-model="name"></el-input>

      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
   </div>
 
    <div style="margin: 10px 0" v-if="user.role != 'ROLE_USER'">

    </div>

    <el-table :data="tableData"  :border="true"   @selection-change="handleSelectionChange" tooltip-effect="dark">
      <el-table-column v-if="user.role != 'ROLE_USER'" type="selection" width="55"></el-table-column>
      <el-table-column prop="id" v-if="user.role != 'ROLE_USER'" label="ID" width="80" sortable></el-table-column>
      <el-table-column label="考试">
        <template v-slot="scope">
          <span v-if="exams && exams.length">{{ exams.find(v => v.id === scope.row.examId) ? exams.find(v => v.id === scope.row.examId).name : '' }}</span>
        </template>
      </el-table-column>
<!--      <el-table-column prop="paper" label="试卷内容"></el-table-column>-->
      <el-table-column prop="user" label="学生姓名"></el-table-column>
      <el-table-column prop="time" label="提交时间"></el-table-column>
      <el-table-column prop="score" label="得分"></el-table-column>


    </el-table>
    <div style="padding: 10px 0">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[2, 5, 10, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>

    <el-dialog title="信息" :visible.sync="dialogFormVisible" width="30%" :close-on-click-modal="false">
      <el-form label-width="100px" size="small" style="width: 90%">
        <el-form-item label="考试id">
          <el-input v-model="form.examId" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="试卷内容">
          <el-input v-model="form.paper" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="学生id">
          <el-input v-model="form.userId" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="提交时间">
          <el-date-picker v-model="form.time" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择日期时间"></el-date-picker>
        </el-form-item>
        <el-form-item label="得分">
          <el-input v-model="form.score" autocomplete="off"></el-input>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>
  </div>
  </el-card>
</template>

<script>
export default {
  name: "studentpaper",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      name: "",
      form: {},
      dialogFormVisible: false,
      multipleSelection: [],
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      exams: []
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      
      this.request.get("/studentpaper/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
          test:''
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })
      this.request.get("/exam").then(res => {
        this.exams = res.data
      })
    },
    save() {
      this.request.post("/studentpaper", this.form).then(res => {
        if (res.code === '200') {
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          this.load()
        } else {
          this.$message.error("保存失败")
        }
      })
    },
    handleAdd() {
      this.dialogFormVisible = true
      this.form = {}
      this.$nextTick(() => {
        if(this.$refs.img) {
           this.$refs.img.clearFiles();
         }
         if(this.$refs.file) {
          this.$refs.file.clearFiles();
         }
      })
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
       this.$nextTick(() => {
         if(this.$refs.img) {
           this.$refs.img.clearFiles();
         }
         if(this.$refs.file) {
          this.$refs.file.clearFiles();
         }
       })
    },
    del(id) {
      this.request.delete("/studentpaper/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    handleSelectionChange(val) {
      console.log(val)
      this.multipleSelection = val
    },
    delBatch() {
      if (!this.multipleSelection.length) {
        this.$message.error("请选择需要删除的数据")
        return
      }
      let ids = this.multipleSelection.map(v => v.id)  // [{}, {}, {}] => [1,2,3]
      this.request.post("/studentpaper/del/batch", ids).then(res => {
        if (res.code === '200') {
          this.$message.success("批量删除成功")
          this.load()
        } else {
          this.$message.error("批量删除失败")
        }
      })
    },
    reset() {
      this.name = ""
      this.load()
    },
    handleSizeChange(pageSize) {
      console.log(pageSize)
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      console.log(pageNum)
      this.pageNum = pageNum
      this.load()
    },
    handleFileUploadSuccess(res) {
      this.form.file = res
    },
    handleImgUploadSuccess(res) {
      this.form.img = res
    },
    download(url) {
      window.open(url)
    },
    exp() {
      window.open("http://localhost:9090/studentpaper/export")
    },
    handleExcelImportSuccess() {
      this.$message.success("导入成功")
      this.load()
    }
  }
}
</script>


<style>
.headerBg {
  background: #eee !important;
}
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
.lizibackground {
    background: linear-gradient(-180deg, #ffffff 0%, #ffffff 100%);
    opacity: 0.3;
    width: 100%;
    height: auto;
    position: absolute;
    pointer-events: none; /* 防止粒子影响鼠标事件 */
    z-index: 0; /* 确保z-index足够高 */
  }
</style>