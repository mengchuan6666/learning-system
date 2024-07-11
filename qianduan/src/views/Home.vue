<template>
  <div style="color: #666;font-size: 14px;">
    <div style="padding-bottom: 20px">
      <b>您好！{{ user.nickname }}</b>
    </div>
    <el-card>
      欢迎使用本系统 | <a href="/front/home"><span style="color: #E6A23C">跳转到前端页面</span></a>
      <el-divider />
      大多数人想要改造这个世界，但却罕有人想改造自己
    </el-card>

  </div>
</template>

<script>
export default {
  name: "Home",
  data() {
    return {
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
    }
  }
}
</script>
