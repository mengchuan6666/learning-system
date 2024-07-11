<template>
  <div>

  <el-container>
    <el-header>
      <!--头部-->
      <div style="display: flex; height: 60px; line-height: 60px; border-bottom: 1px solid #333;">
        <div style="width: 300px; display: flex; padding-left: 30px">
          <div style="width: 60px">
            <img src="../../assets/logo.png"  style="width: 50px; position: relative; top: 8px; right: 0">
          </div>
          <div style="flex: 1;font-size: 20px;">欢迎访问在线学习系统</div>
        </div>
        <div class="navi-menu" style="flex: 1; ">
          <el-menu :default-active="'1'" class="el-menu-demo navi-menu" mode="horizontal" router>
            <el-menu-item index="/front/notices"><img src="../../assets/static/imag/6.png" style="width: 20px; position: relative; margin-bottom: 7px; right: 0">公告信息</el-menu-item>
            <el-menu-item index="/front/comment"><img src="../../assets/static/imag/4.png" style="width: 20px; position: relative; margin-bottom: 7px; right: 0">在线留言</el-menu-item>
            <el-menu-item index="/front/news"><img src="../../assets/static/imag/29.png" style="width: 20px; position: relative; margin-bottom: 7px; right: 0">学习新闻</el-menu-item>
            <el-menu-item index="/front/StudyResourceType"><img src="../../assets/static/imag/155.png" style="width: 20px; position: relative; margin-bottom: 7px; right: 0">学习资料</el-menu-item>
            
            <el-menu-item v-if="user.role!='ROLE_TEACHER' && user.role!='ROLE_USER' && user.role!=undefined" index="/front/exam"><img src="../../assets/static/imag/3.png" style="width: 20px; position: relative; margin-bottom: 7px; right: 0">在线考试</el-menu-item>
            <!-- <el-menu-item v-if="user.role=='ROLE_TEACHER' && user.role!='ROLE_USER' && user.role!=undefined" @click="visitAdmin">考试出题</el-menu-item> -->
            <el-menu-item v-if="user.role=='ROLE_STUDENT'" index="/front/score" ><img src="../../assets/static/imag/32.png" style="width: 20px; position: relative; margin-bottom: 7px; right: 0">查看成绩</el-menu-item>
            
          
            <el-menu-item v-if="user.role=='ROLE_MANAGER' || user.role=='ROLE_ADMIN'" @click="visitAdmin"><img src="../../assets/static/imag/1.png" style="width: 20px; position: relative; margin-bottom: 7px; right: 0">进入后台系统</el-menu-item>
          </el-menu>
        </div>
        <div style="width: 200px">
          <!-- 匿名访问 -->
          <div v-if="!user.username" style="text-align: right; padding-right: 30px">
            <el-button @click="$router.push('/login')">登录</el-button>
            <el-button @click="$router.push('/register')">注册</el-button>
          </div>
          <!-- 登录用户 -->
          <div v-else>
            <el-dropdown style="width: 150px; cursor: pointer; text-align: right">
              <div style="display: inline-block">
                <img :src="user.avatarUrl" alt=""
                    style="width: 30px; border-radius: 50%; position: relative; top: 10px; right: 5px">
                <span>{{ user.nickname }}</span><i class="el-icon-arrow-down" style="margin-left: 5px"></i>
              </div>
              <!-- 下拉菜单 -->
              <el-dropdown-menu slot="dropdown" style="width: 100px; text-align: center">
                <el-dropdown-item style="font-size: 14px; padding: 5px 0">
                  <router-link to="/front/password">修改密码</router-link>
                </el-dropdown-item>
                <el-dropdown-item style="font-size: 14px; padding: 5px 0">
                  <router-link to="/front/person">个人信息</router-link>
                </el-dropdown-item>
                 <el-dropdown-item style="font-size: 14px; padding: 5px 0" v-if="user.role=='ROLE_MANAGER' || user.role=='ROLE_ADMIN'">
                  <router-link to="/">进入后台系统</router-link>
                </el-dropdown-item>
                <el-dropdown-item style="font-size: 14px; padding: 5px 0">
                  <span style="text-decoration: none" @click.stop="logout">退出</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
      </div>
    </el-header>
    <el-main>
     
        <div style="width: 100%; margin: 0 auto;">
          <router-view />
        </div>
     
    </el-main>
    <el-footer></el-footer>
  </el-container>
</div>
</template>

<script>
import "@/assets/css/front.css"

export default {
  name: "Front",
  data() {
    return {
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
    }
  },
  created() {
  
  },
  methods: {
    logout() {
      this.$store.commit("logout")
      this.$message.success("退出成功")
    },
    visitAdmin(){
      window.location.href="/home"
    }
  }
}
</script>

<style>
.el-menu.el-menu--horizontal {
  border-bottom: none;
  border-bottom-width: initial;
  border-bottom-style: none;
  border-bottom-color:#1E90FF;
}

.item{
  display: inline-block;
  width: 100px;
  text-align: center;
  cursor: pointer;
}
.el-menu--horizontal>.el-menu-item.is-active {
    border-bottom: 2px solid #409EFF;
    color: #397cc9;
}
.item a {
  color: 	#1E90FF;
}
.item:hover{
  background-color: 	LightPink;
}

@import url("//unpkg.com/element-ui@2.15.14/lib/theme-chalk/index.css");
.el-header, .el-footer {
    background-color: #ffffff;
    color: #333;
    text-align: center;
    line-height: 38px;
  }
  
  .el-aside {
    background-color: #D3DCE6;
    color: #333;
    text-align: center;
    line-height: 200px;
  }
  
  .el-main {

    background-color: #ffffff;
    color: #333;
    text-align: center;
    /* text-align: center; */
    line-height: 50px !important
  }
  
  body > .el-container {
    margin-bottom: 40px;
  }
  
  .el-container:nth-child(5) .el-aside,
  .el-container:nth-child(6) .el-aside {
    line-height: 260px;
  }
  
  .el-container:nth-child(7) .el-aside {
    line-height: 320px;
  }

  .navi-menu{
    font-weight: bold;
    background-color: rgba(133, 117, 117, 0);
  }

</style>