<template>
  <div>
  
    <div style="margin: 10px 0">
      <span>  
        <a-ico type="sync"/> 
        <div :style="{opacity}">99+</div>  
      </span>
      <el-input style="width: 200px" placeholder="请输入名称" suffix-icon="el-icon-search" v-model="resourceName"></el-input>
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>
    
    <!-- <div style="margin: 10px 0">
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
    </div> -->

    <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"
              @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="80" sortable></el-table-column>
      <el-table-column prop="resourceName" label="课程名称"></el-table-column>
      <el-table-column prop="introduction" label="简介">
        <template v-slot="scope">
          <el-button type="primary" @click="view(scope.row.introduction)">查看简介</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="createTimeStr" label="发布时间"></el-table-column>
      <el-table-column prop="userName" label="发布人"></el-table-column>
      <el-table-column prop="status" label="状态">
           <template slot-scope="scope">
            <span v-if="scope.row.status==0">未审核</span>
            <span v-if="scope.row.status==1">已审核</span>
            <span v-if="scope.row.status==3">未公布</span>
            <span v-if="scope.row.status==4">已公布</span>
            <span v-if="scope.row.status==9">已删除</span>
          </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center">
        <template slot-scope="scope">
          <!-- <el-button type="success" @click="handleCheck(scope.row)">审核 <i class="el-icon-edit"></i></el-button> -->
          <el-popconfirm
              class="ml-5"
              confirm-button-text='确定'
              cancel-button-text='我再想想'
              icon="el-icon-info"
              icon-color="red"
              title="您确定审核吗？"
              @confirm="handleCheck(scope.row.id)"
          >
            <el-button type="danger" slot="reference">审核 <i class="el-icon-remove-outline"></i></el-button>
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

    <!-- 新增弹窗 -->
    <el-dialog title="学习资料" :visible.sync="dialogFormVisible" width="60%" :close-on-click-modal="false">
      <el-form label-width="100px" size="small" style="width: 90%">
        <el-form-item label="课程名称">
          <el-input v-model="form.resourceName" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="资料文件">
          <el-upload
            class="upload-demo"
            action="http://localhost:9090/file/upload"
            :key="fileUpdate"
            :on-preview="handlePreview"
            :on-remove="handleRemove"
            :before-remove="beforeRemove"
            multiple
            :limit="1"
            :on-exceed="handleExceed"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
            :file-list="fileList">
              <el-button size="small" type="primary">点击上传</el-button>
              <!-- <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div> -->
          </el-upload>
        </el-form-item>
        <el-form-item label="资料简介">
          <div id="richText"></div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="简介" :visible.sync="dialogFormVisible1" width="60%" :close-on-click-modal="false">
      <el-card>
        <div v-html="introductionViewContent"></div>
      </el-card>
    </el-dialog>
  </div>
</template>
<p v-html="test"></p>
<script>
import E from "wangeditor"
let editor;

export default {
  name: "StudyResource",
  data() {
    return {
      opacity: 1,
      fileUpdate: 0,
      fileList: [],
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      resourceName: "",
      state:"0",
      form: {},
      dialogFormVisible: false,
      dialogFormVisible1: false,
      introductionViewContent: '',
      multipleSelection: [],
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
    }
  },
  created() {
    this.load()
  },
  mounted(){
    this.change()
  },
  methods: {
    change () {
      setInterval(() => {
        this.opacity -= 0.01   
        if (this.opacity <= 0) this.opacity = 1  
      }, 16)
    },
    view(introduction){
      this.introductionViewContent = introduction
      this.dialogFormVisible1 = true;
    },
    load() {
      this.request.get("/resource/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          state:this.state,
          resourceName: this.resourceName,
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },
    save() {
      // 富文本编辑的内容需要手动赋值
      this.form.introduction = editor.txt.html()
    
      this.request.post("/resource", this.form).then(res => {
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
      this.form = {img: ''}
      this.$nextTick(() => {
        if (!editor){
          editor  = new E("#richText")
          // 配置 server 接口地址
          editor.config.uploadImgServer = 'http://localhost:9090/file/upload'
          editor.config.uploadFileName = 'editorFile@' + this.name
          editor.create()
        }
        editor.txt.html('')

        if (this.$refs.img) {
          this.$refs.img.clearFiles();
        }
        if (this.$refs.file) {
          this.$refs.file.clearFiles();
        }
      })
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
      this.$nextTick(() => {
   
        if (!editor){
          editor = new E("#richText")
          // todo 配置 server 接口地址
          editor.config.uploadImgServer = 'http://localhost:9090/file/upload'
          editor.config.uploadFileName = 'editorFile@' + this.name
          editor.create()
        }
        editor.txt.html(this.form.introduction)
        if (this.$refs.img) {
          this.$refs.img.clearFiles();
        }
        if (this.$refs.file) {
          this.$refs.file.clearFiles();
        }
        this.fileList = this.fileList.filter(e=>{return false});
        this.fileUpdate++;
        this.request.get("/file/" + row.sysFileId+"/info").then(res => {
          if (res.code === '200') {
            this.fileList.push(res.data);
            this.fileUpdate++;
          } else {
            this.$message.error("加载资源文件信息失败")
          }
        })
      })
    },
    handleCheck(id) {
      this.request.put("/resource/check/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("审核成功")
          this.load()
        } else {
          this.$message.error("审核失败")
        }
      })
    },
    del(id) {
      this.request.delete("/resource/" + id).then(res => {
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
      this.request.post("/resource/del/batch", ids).then(res => {
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
    handleUploadSuccess(res, file, fileList) {
      this.form.fileUrl = res
      this.fileList.add(file);
    },
    download(url) {
      window.open(url)
    },
    handleRemove(file, fileList) {
      console.log(file, fileList);
    },
    handlePreview(file) {
      console.log(file);
    },
    handleExceed(files, fileList) {
      this.$message.warning(`只能上传 1 个文件，重新上传请点击已上传文件右侧按钮删除后重试`);
    },
    beforeRemove(file, fileList) {
      return this.$confirm(`确定移除 ${ file.name }？`);
    },
    beforeUpload(file,fileList) {
        const typeExtensions= ['doc', 'docx','jpg','jpeg','png','mp4']; // 可以自定义需要支持的音频格式
        const fileExtension = file.name.split('.').pop().toLowerCase(); // 根据文件名获取文件扩展名并转换为小写字母形式
        const maxSize=2*1024*1024
        if(file.size>maxSize){
            this.$message.warning("请上传2MB以内的文件！")
        }
        if (!typeExtensions.includes(fileExtension)) {
            this.$message.warning("上传文件格式只能是doc/docx/jpg/jpeg/png/ppt/mp4！")
            return false;
        }
        
    },
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
</style>
