<template>
  <div>
    <div style="margin: 10px 0">
      <el-input style="width: 200px" placeholder="请输入名称" suffix-icon="el-icon-search" v-model="name"></el-input>
<!--      <el-input style="width: 200px" placeholder="请输入" suffix-icon="el-icon-message" class="ml-5" v-model="email"></el-input>-->
<!--      <el-input style="width: 200px" placeholder="请输入" suffix-icon="el-icon-position" class="ml-5" v-model="address"></el-input>-->
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>

    <div style="margin: 10px 0">
      <el-button type="primary" @click="handleAdd">新增 <i class="el-icon-circle-plus-outline"></i></el-button>

      <el-popconfirm
          class="ml-5"
          confirm-button-text='确定'
          cancel-button-text='我再想想'
          icon="el-icon-info"
          icon-color="red"
          title="您确定批量删除这些数据吗？"
          @confirm="delBatch"
      >
        <el-button type="danger" slot="reference">批量删除 <i class="el-icon-remove-outline"></i></el-button>
      </el-popconfirm>
      <el-upload action="http://localhost:9090/paper/import" :show-file-list="false" accept="xlsx" :on-success="handleExcelImportSuccess" style="display: inline-block">
        <el-button type="primary" class="ml-5">导入 <i class="el-icon-bottom"></i></el-button>
      </el-upload>
      <el-button type="primary" @click="exp" class="ml-5">导出 <i class="el-icon-top"></i></el-button>
    </div>

    <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"  @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="80" sortable></el-table-column>
      <el-table-column prop="name" label="名称"></el-table-column>
      <el-table-column prop="score" label="总分"></el-table-column>
      <el-table-column prop="duration" label="时长(分)"></el-table-column>
      <el-table-column label="课程">
        <template v-slot="scope">
          <span>{{ typeOptions.find(v => v.id === scope.row.ctypeId) ? typeOptions.find(v => v.id === scope.row.ctypeId).typeName : ''}}</span>
        </template>
      </el-table-column>
       <el-table-column prop="test" label="类型">
        <template v-slot="scope">
          <span v-if="scope.row.test === 0">单元测试</span>
          <span v-if="scope.row.test === 1">作业测试</span>
        </template>
      </el-table-column>
      <el-table-column label="考卷">
        <template v-slot="scope">
          <el-button type="primary" @click="viewPaper(scope.row.id)">查看考卷</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作"  width="380" align="center">
        <template slot-scope="scope">
          <el-button type="primary" plain @click="handlePaper(scope.row,scope.row.id, scope.row.courseId)">手动组卷 <i class="el-icon-document"></i></el-button>
          <el-button type="primary" @click="takePaper(scope.row,scope.row.id, scope.row.ctypeId)">自动组卷 <i class="el-icon-document"></i></el-button>
          <el-button type="success" @click="handleEdit(scope.row)">编辑 <i class="el-icon-edit"></i></el-button>
          <el-popconfirm
              class="ml-5"
              confirm-button-text='确定'
              cancel-button-text='我再想想'
              icon="el-icon-info"
              icon-color="red"
              title="您确定删除吗？"
              @confirm="del(scope.row.id)"
          >
          <el-button type="danger" slot="reference">删除 <i class="el-icon-remove-outline"></i></el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
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
    <el-dialog title="手动组卷" :visible.sync="dialogFormVisible3" width="55%" :close-on-click-modal="false">
      <div style="width: 90%; padding-left: 120px">
        <el-transfer  :titles="['题库', '已选']" filter-placeholder="请输入类型：1选择，2判断，3问答"  filterable :filter-method="filterMethod"
                     v-model="form2.handleQuestionIds" :data="paperQuestions"  :props="{key: 'id',label: 'name'}"></el-transfer>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible3 = false">取 消</el-button>
        <el-button type="primary" @click="generateHandPaper">确 定</el-button>
      </div>
    </el-dialog>
    <el-dialog title="考卷" :visible.sync="dialogFormVisible2" width="60%" :close-on-click-modal="false">
      <el-card>
        <div v-for="(item, index) in questiones" :key="item.id"  style="margin: 20px 0">
          <div style="margin: 10px 0; font-size: 20px"><span>{{ index+1 }}.</span> {{ item.name }}
            <span style="font-size: 14px" v-if="item.type === 1">（选择题）</span>
            <span style="font-size: 14px"  v-if="item.type === 2">（判断题）</span>
            <span style="font-size: 14px"  v-if="item.type === 3">（问答题）</span>
          </div>
          <div v-if="item.type === 1" style="margin: 10px">
            <span style="margin-right: 20px">A. {{ item.a }}</span>
            <span  style="margin-right: 20px">B. {{ item.b }}</span>
            <span  style="margin-right: 20px">C. {{ item.c }}</span>
            <span  style="margin-right: 20px">D. {{ item.d }}</span>
          </div>
          <div style="margin: 10px">
            答案： {{ item.answer }}
          </div>
          <div style="margin: 10px">
            解析： {{ item.detail }}
          </div>
        </div>
      </el-card>
    </el-dialog>

    <!-- dialog:组卷 -->
    <el-dialog title="组卷" :visible.sync="dialogFormVisible1" width="40%" :close-on-click-modal="false">
     <el-form label-width="100px" size="small" style="width: 90%">
       <el-form-item label="选择题数量">
         <el-input v-model="form1.type1" autocomplete="off"></el-input>
       </el-form-item>
       <el-form-item label="选择题分值">
         <el-input v-model="form1.type1Score"  placeholder="要和数据库分值一致" autocomplete="off"></el-input>
       </el-form-item>
       <el-form-item label="填空(判断)题数量">
         <el-input v-model="form1.type2" autocomplete="off"></el-input>
       </el-form-item>
       <el-form-item label="填空(判断)题分值">
         <el-input v-model="form1.type2Score" placeholder="要和数据库分值一致" autocomplete="off"></el-input>
       </el-form-item>
       <el-form-item label="问答题数量">
         <el-input v-model="form1.type3" autocomplete="off"></el-input>
       </el-form-item>
       <el-form-item label="问答题分值">
         <el-input v-model="form1.type3Score" placeholder="要和数据库分值一致" autocomplete="off"></el-input>
       </el-form-item>
       <!-- <el-form-item label="难度系数">
         <el-input v-model="form1.difficulty"  
         placeholder="请输入值为0.3-1的小数"  
         autocomplete="off"
         oninput="value=value.replace(/^0[0-9]|^[2-9]|^1[0-9]|^1\.|[^\d.]/g,'')"
         ></el-input>
       </el-form-item> -->
     </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible1 = false">取 消</el-button>
        <el-button type="primary" @click="generatePaper">确 定</el-button>
      </div>
    </el-dialog>


    <!-- dialog:新增考试 -->
    <el-dialog title="新增考试/作业" :visible.sync="dialogFormVisible" width="30%" :close-on-click-modal="false">
      <el-form label-width="100px" size="small" style="width: 90%">
        <el-form-item label="名称">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="时长(分钟)">
          <el-input v-model="form.duration" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="课程">
          <el-select  v-model="typeIds" placeholder="请选择类型" style="width: 200px" class="ml-5">
              <el-option v-for="item in typeOptions" 
                :key="item.id" 
                :label="item.typeName" 
                :value="item.id">
              </el-option>
          </el-select>
         </el-form-item>
        <el-form-item label="类型">
          <el-select clearable v-model="form.test" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in [{name: '单元测试', value: 0}, {name: '作业测试', value: 1}]"
                       :key="item.value" :label="item.name" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Paper",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      name: "",
      form: {},
      form1: {},
      form2: {},
      dialogFormVisible: false,
      dialogFormVisible1: false,
      dialogFormVisible2: false,
      dialogFormVisible3: false,
      multipleSelection: [],
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      courses: [],
      questiones: [],
      courseId: null,
      type: null,
      //试卷类型
      test:null,
      //课程
      typeOptions:[],
      typeIds:"",
      ctypeId:"",
      paperQuestions: [],
    
    }
  },
  created() {
    this.load()

  },
  methods: {
    filterMethod(query, item) {
      return !query || query == item.type;
    },

    handlePaper(row,paperId, courseId) {
      this.request.get("/question").then(res => {
        this.paperQuestions = res.data.filter(v => v.courseId === courseId)
       
      })

      this.form2 = {handleQuestionIds: [], paperId: paperId}
      this.dialogFormVisible3 = true
    },
    viewPaper(paperId) {
      this.request.get("/paper/view/" + paperId).then(res => {
        this.questiones = res.data
        this.dialogFormVisible2 = true
      })
    },
    takePaper(row,paperId, courseId) {
      this.restForm1();
      this.form1 = {type1: this.form1.type1,type1Score:this.form1.type1Score, type2: this.form1.type2,type2Score:this.form2.type2Score, type3: this.form1.type3,type3Score:this.form1.type3Score, paperId: paperId, ctypeId: courseId}
      this.dialogFormVisible1 = true
    },
    load() {
      this.request.get("/paper/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })

      this.request.get("/course").then(res => {
        this.courses = res.data
      })
      //课程数据
      this.request.get("/resourceType").then(res => {
        this.typeOptions = res.data
      })
    },
    generatePaper() {
     
      this.request.post("/paper/takePaperAuto", this.form1).then(res => {
        if (res.code === '200') {
          this.$message.success("组卷成功")
          this.dialogFormVisible1 = false
          
          this.load();
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    generateHandPaper() {
      this.request.post("/paper/handPaper", this.form2).then(res => {
        if (res.code === '200') {
          this.$message.success("组卷成功")
          this.dialogFormVisible3 = false
          this.load();
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    save() {
      this.form.ctypeId=this.typeIds;
      this.request.post("/paper", this.form).then(res => {
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
      this.form = JSON.parse(JSON.stringify(row));
      this.typeIds=this.form.ctypeId;
      this.request.get("/resourceType").then(res => {
        this.typeOptions = res.data
      })
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
      this.request.delete("/paper/" + id).then(res => {
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
      this.request.post("/paper/del/batch", ids).then(res => {
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
    restForm1(){
      this.form1="";
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
      window.open("http://localhost:9090/paper/export")
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
  background: #eee!important;
}
</style>
