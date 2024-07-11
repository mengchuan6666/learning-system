<template>
  <div>
    <div class="content" v-show="isShow">
      <label >学生姓名：</label>
      <select id="dropdown" v-model="selectedOption" @change="getUserList($event.target.value)">
        <option disabled value="" >请选择</option>
        <option v-for="(item, index) in options" :key="index.id" :value="item.id"  >{{ item.username }}</option>
      </select>
    </div>

    <el-card class="mt20">
    <div id="main" style="width: 600px; height: 400px"></div>
    </el-card>
  </div>
  
</template>
 
<script>
import * as echarts from 'echarts'
export default {
  name: "c1",
  data() {
    return {
      isShow:false,
      title:[],
      number:[],
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      selectedOption:'',
      options: [],
      id:"",
    }
  },
  mounted() {
    //this.drawLine("");
    this.show(this.user);
    this.getUserList("");
  },
methods: {
        clear(){
           this.title=[];
           this.number=[];
        },
        //获取学生下拉列的数据
        getUserList(id){
          console.log("===================:"+id);
          let url="/user/forSelect";
          this.request.get(url).then(res => {
            //debugger
             console.log("-----------------"+res.data);
             this.options = res.data;
          })
          this.clear();
          this.drawLine(id);
        },
        //判断是否是管理员
        show(user){
          console.log("======"+user.role);
          if(user.role=='ROLE_ADMIN' || user.role=='ROLE_ADROLE_MANAGERMIN' || user.role=='ROLE_TEACHER'){
                   this.isShow=true;
          }
        },
        //Echarts部分
        drawLine(id) {
                //初始化echarts实例
                let myChart = echarts.init(document.getElementById('main'));
                myChart.setOption({
                      
                     xAxis: {
                        data: []
                      },
                     yAxis: {},
                      series: [
                          {
                            data: [],
                            type: 'line',
                            smooth: true
                          }
                        ]
                })
                  this.findById(id);  
            },
        findById(id){
                  
                  this.request.get("/echarts/studyonlinetime/"+id).then(res => {
                 
                  //debugger
                  //console.log(this.user.id);
                  for(var i=0;i<res.data.length;i++){
                      this.title.push(res.data[i].uname);
                      this.number.push(res.data[i].time);
                  }
                  let myChart = echarts.init(document.getElementById('main'));
                  // 绘制图表
                  myChart.setOption({
                      xAxis: {
                        data: this.title
                      },
                     
                      series: [{
                        // 根据名字对应到相应的系列
                        name: '成绩统计',
                        data:  this.number,
                        //样式
                        itemStyle: {
                            normal: {
                                color: function(params) {
                                    // build a color map as your need.
                                    var colorList = [
                                      '#C1232B','#B5C334','#FCCE10','#E87C25','#27727B',
                                      '#FE8463','#9BCA63','#FAD860','#F3A43B','#60C0DD',
                                      '#D7504B','#C6E579','#F4E001','#F0805A','#26C0C0'
                                    ];
                                    return colorList[params.dataIndex]
                                },
                                label: {
                                    show: true,
                                    position: 'top',
                                    formatter: '{b}\n{c}'
                                }
                            }
                        }
                      }]
                  });
              })
            }
             
        }
};
</script>