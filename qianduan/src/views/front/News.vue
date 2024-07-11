<template>
  <div>
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
  <el-card>
   
  <section class="blog-sec">
    <el-dialog title="内容" :visible.sync="dialogContentVisible" width="60%" :close-on-click-modal="false">
      <el-card>
        <div style="text-align:left ;line-height: 25px" v-html="content"></div>
      </el-card>
    </el-dialog>
    <div class="container">
        <div class="main-content">
          <div style="margin: 10px 0">
            <el-input size="medium" clearable style="width: 400px" placeholder="输入新闻标题"  v-model="name"></el-input>
            <el-button icon="el-icon-search" style="height:36px" @click="load" ></el-button>
          </div>
            <h1>新闻资讯</h1>
            <div v-for="news in newsData" :key="news.id">
              <el-card>
                  <div class="post_item">
                      <img style="max-width: 60%; min-width: 40%; display: table-cell; vertical-align: middle;position: relative; left: 20%;" :src="news.img" alt="blog">
                      <!-- 文章类别 todo line-height放入style -->
                      <a href="javascript:void(0);" class="category-ttl">学习新闻</a>
                      <div class="shared-sec right">
                          <ul>
                              <li> 分享至 :</li>
                              <li> <a href="javascript:void(0);"><i class="fa fa-weixin svg-inline--fa fa-w-14" aria-hidden="true"></i></a></li>
                              <li> <a href="javascript:void(0);"><i class="fa fa-weibo svg-inline--fa fa-w-14" aria-hidden="true"></i></a></li>
                              <li> <a href="javascript:void(0);"><i class="fa fa-qq svg-inline--fa fa-w-14" aria-hidden="true"></i></a></li>
                          </ul>
                      </div>
                      <h2><a href="javascript:void(0);" @click="view(news.content)" >{{news.name}}</a></h2>
                      <!-- 文章作者与发布日期 -->
                      <ul class="post-tools">
                          <li class="admin"><a href="#">{{news.user}} </a></li>
                          <li class="date">{{news.time}}</li>
                      </ul>
                      <h6>{{getAbstract(news.content,300)}}</h6>
                      <a href="javascript:void(0);" @click="view(news.content)" class="readmore-btn">详情...</a>
                  </div>
               </el-card>
            </div>
            <button class="load-more-btn" @click="loadMore()">更多...</button>
        </div>

        <!-- 侧边栏内容（放公告） -->
        <aside class="aside-sec">
            <div class="popular-posts">
                <h2>近期公告</h2>
                <div v-for="notice in noticeData" :key="notice.id">
                  <div class="p_post-item">
                      <h3>{{notice.name}}</h3>
                      <h6>{{getAbstract(notice.content,150)}}</h6>
                      <ul class="post-tools">
                          <li class="admin"><a href="#">{{notice.user}} </a></li>
                          <li class="date">{{notice.time}}</li>
                      </ul>
                  </div>
                </div>
            </div>
        </aside>
    </div>
  </section>
</el-card>
</div>
</template>
<script scoped>
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.min.js'
import '@/assets/static/css/news-style.css'
import '@/assets/static/fa/css/font-awesome.min.css'
export default {
  name: "News",
  data() {
    return {
      newsData: [],
      noticeData: [],
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
      this.request.get("/news/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        if(res.data.records.length > 0) {
          this.pageNum++
          this.newsData = this.newsData.concat(res.data.records)
          this.total = res.data.total
        } else {
          this.$message.warning("无更多新闻")
        }
      })

      this.request.get("/notice/page", {
        params: {
          pageNum: 1,
          pageSize: 5
        }
      }).then(res => {
        this.noticeData = res.data.records
      })
    },
    load() {
      this.pageNum = 1;
      this.newsData = [];
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
  background: #fff!important;
}

.category-ttl,.readmore-btn {
  line-height: 1;
}

.post_item,.p_post-item {
  text-align: left;
}
.card {
  position: relative;
  z-index: 10; 
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
