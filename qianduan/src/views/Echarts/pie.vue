<template>
  <div>
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
      echartsList:[]
    }
  },
  mounted() {
    this.groupByDid();    
  },
  methods: {
    groupByDid(){
       listDateForPie().then(res =>{
        //debugger;
          if (res.code == 200) {
            this.echartsList = res.data;
            console.log(res.data);
            this.getChar();
          }
       });
 
    },
    getChar() {
      // 初始化echarts实例
      var myChart = echarts.init(document.getElementById("main"));
      var option = {
        title: {
          text: "数据统计",
          subtext: "",
          left: "center",
        },
        tooltip: {
          trigger: "item",
        },
        legend: {
          orient: "vertical",
          left: "left",
        },
        series: [
          {
            name: "Access From",
            type: "pie",
            radius: "50%",
            data:this.echartsList,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: "rgba(0, 0, 0, 0.5)",
              },
            },
          },
        ],
      };
      // 使用刚指定的配置项和数据显示图表。
      myChart.setOption(option);
    },
  },
};
</script>