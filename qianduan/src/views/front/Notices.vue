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
  <section class="blog-sec">
    <el-dialog title="内容" :visible.sync="dialogContentVisible" width="60%" :close-on-click-modal="false">
      <el-card>
        <div style="text-align:left ;line-height: 25px" v-html="content"></div>
      </el-card>
    </el-dialog>
    <div class="container">
        <div class="main-content">
         
            <div style="margin: 10px 0">
              <el-input size="medium" clearable style="width: 400px;" placeholder="输入公告标题" v-model="name"></el-input>
              <el-button icon="el-icon-search" style="height:36px" @click="load" ></el-button>
            </div>
            <h1>公告信息</h1>
            
              <div v-for="notice in tableData" :key="notice.id">
                <el-card style="margin-top: 5px;">
                  <div class="post_item">
                      <h2><a href="javascript:void(0);" @click="view(notice.content)">{{notice.name}}</a></h2>
                      <!-- 文章作者与发布日期 -->
                      <ul class="post-tools">
                          <li class="admin"><a href="javascript:void(0);">{{notice.user}} </a></li>
                          <li class="date">{{notice.time}}</li>
                      </ul>
                      <h6>{{getAbstract(notice.content,300)}}</h6>
                      <a href="javascript:void(0);" @click="view(notice.content)" class="readmore-btn">详情...</a>
                  </div>
                </el-card>
              </div>
              <button class="load-more-btn" @click="loadMore()">更多...</button>
          
        </div>
  
    </div>
  </section>
 </el-card>
</template>

<script scoped>
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.min.js'
import '@/assets/static/css/news-style.css'
import '@/assets/static/fa/css/font-awesome.min.css'
export default {
  name: "Notice",

  data() {
    return {
      tableData: [],
      goodsList: [],
      total: 0,
      pageNum: 1,
      pageSize: 5,
      name: "",
      form: {},
      dialogContentVisible: false,
      multipleSelection: [],
      content: '',
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
    }
  },
  created() {
    this.load()
  },
  methods: {
    detail(data){
      this.$router.push({name:'Detail',params:{id:data.id}})
    },
    view(data){
      this.content = data
      this.dialogContentVisible = true;
    },
    loadMore() {
      this.request.get("/notice/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        if(res.data.records.length > 0) {
          this.pageNum++
          this.tableData = this.tableData.concat(res.data.records)
          this.total = res.data.total
        } else {
          this.$message.warning("无更多公告")
        }
      })
    },

    load() {
      this.pageNum=1;
      this.tableData=[];
      this.loadMore();
    },
    handleSelectionChange(val) {
      console.log(val)
      this.multipleSelection = val
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
      window.open("http://localhost:9090/orders/export")
    },
    handleExcelImportSuccess() {
      this.$message.success("导入成功")
      this.load()
    },
    getAbstract(str,limit){
      if(typeof str === "undefined" || str === null || str.trim() === ""){
        return "";
      }

      var abstract = str.replace(/<.*?>/g,"");
      if(str.length > limit) {
        abstract = abstract.substr(0,limit) ;
        abstract+= '...';
      }
      return abstract;
    }
  }
}
</script>


<style scoped>

.container {
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
.headerBg {
  background: #ffffff!important;
}

.category-ttl,.readmore-btn {
  line-height: 1;
}

.post_item,.p_post-item {
  text-align: left;
}
.post-tools h6{
    color: #000;
    font-size: 18px;
    font-weight: bold;
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
