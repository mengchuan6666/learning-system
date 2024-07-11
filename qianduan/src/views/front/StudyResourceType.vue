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
      <el-input style="width: 200px" placeholder="请输入名称" suffix-icon="el-icon-search" v-model="typeName"></el-input>
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>

    

    <el-table :data="tableData"   :border="true" @selection-change="handleSelectionChange"   tooltip-effect="dark">
   
      <el-table-column prop="typeName" align="center"  label="课程类型">
         <template slot-scope="scope">
           <a :href="'/front/StudyResource?type='+scope.row.id" target="_self">{{scope.row.typeName}}</a>
        </template>
      </el-table-column>
      <el-table-column prop="teacherName" align="center"   label="老师姓名"></el-table-column>
      <el-table-column prop="createTimeStr" align="center"  label="创建时间"></el-table-column>
      <el-table-column prop="updateTimeStr" align="center"  label="更新时间"></el-table-column>
      <el-table-column prop="number" align="center"  label="材料数量"></el-table-column>
      <!-- <el-table-column label="操作" width="180" align="center">
        <template slot-scope="scope">
          <el-button type="success" @click="handleEdit(scope.row)">编辑 <i class="el-icon-edit"></i></el-button>
        </template>
      </el-table-column> -->
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
    <el-dialog title="课程类型" :visible.sync="dialogFormVisible" width="60%" :close-on-click-modal="false">
      <el-form label-width="100px" size="small" style="width: 90%">
        <el-form-item label="类型名称">
          <el-input v-model="form.typeName" autocomplete="off"></el-input>
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
<p v-html="test"></p>
<script>
export default {
  name: "StudyResourceType",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      typeName: "",
      form: {},
      dialogFormVisible: false,
      multipleSelection: [],
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
    }
  },
  created() {
    this.load()
  },
  methods: {

    load() {
      this.request.get("/resourceType/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          typeName: this.typeName,
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
        
      })
    },
    save() {
      this.request.post("/resourceType", this.form).then(res => {
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
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
    },
    del(id) {
      this.request.delete("/resourceType/" + id).then(res => {
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
      this.request.post("/resourceType/del/batch", ids).then(res => {
        if (res.code === '200') {
          this.$message.success("批量删除成功")
          this.load()
        } else {
          this.$message.error("批量删除失败")
        }
      })
    },
    reset() {
      this.typeName = ""
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
