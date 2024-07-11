<template>
    <el-row :gutter="20">
        <el-col :span="30">
            <el-row :gutter="30" style="margin-top: 10px">
                <el-col :span="12">
                    <el-card style="height: 500px">
                        <div id="score" style="width: 800px; height: 200px">
                            <div
                                ref="jbxx"
                                style="width: 800px; height: 500px"
                            />
                        </div>
                    </el-card>
                </el-col>
                <el-col :span="12">
                    <el-card style="height: 500px">
                        <div ref="cjxx" style="width: 800px; height: 500px" />
                    </el-card>
                </el-col>
            </el-row>
            <el-row :gutter="30" style="margin-top: 10px">
                <el-col :span="12">
                    <el-card style="height: 500px">
                        <div ref="zycsxx3" style="width: 800px; height: 500px" />
                    </el-card>
                </el-col>
                <el-col :span="12">
                    <el-card style="height: 500px">
                        <div ref="zycsxx4" style="width: 800px; height: 500px" />
                    </el-card>
                </el-col>
                
            </el-row>
            <el-row :gutter="30" style="margin-top: 10px">
                <el-col :span="12">
                    <el-card style="height: 500px">
                        <div ref="zycsxx5" style="width: 800px; height: 500px" />
                    </el-card>
                </el-col>
                <el-col :span="12">
                    <el-card style="height: 500px">
                        <div ref="zycsxx6" style="width: 800px; height: 500px" />
                    </el-card>
                </el-col>
                
            </el-row>
        </el-col>
    </el-row>
</template>

<script>
import * as echarts from 'echarts'
export default {
    name: 'c1',

    data() {
        return {
            charts1: '',
            charts2: '',
            charts3: '',
            charts4: '',
            charts5: '',
            charts6: '',
            title: [],
            number: [],
            arrayData:[],
            test: '0',
            form: {
                maxScore: '',
                minScore: '',
                avScoreg: '',
                midScoreg: ''
            },
            user: localStorage.getItem('user')
                ? JSON.parse(localStorage.getItem('user'))
                : {}
        }
    },
    mounted() {
        this.listScore()
        this.listScoreBase()
    },
    methods: {
        listScoreBase() {
            this.request
                .get('/studentpaper/getScore?test=' + this.test)
                .then((res) => {
                    this.form = res.data
                    this.drawJbxx()
                })
        },
        listScore() {
            this.request.get('/echarts/score/' + this.user.id).then((res) => {
                for (var i = 0; i < res.data.length; i++) {
                    this.title.push(res.data[i].name)
                    this.number.push(res.data[i].value)
                }
                this.arrayData=res.data;
                    this.drawLine()
                    this.drawPie1()
                    this.drawSymbol()
                    this.drawRadar()
                    this.drawBar()
            })
        },
       
        drawJbxx() {
            const data = this.form
            const legend = ['最高分', '最低分', '平均分', '中位分']
            this.charts1 = echarts.init(this.$refs.jbxx)
            let option = {
                tooltip: {
                    trigger: 'axis' // 坐标轴触发
                },
                title: {
                    text: '考试基本信息'
                },
                xAxis: {
                    type: 'category',
                   
                    data: legend
                },
                yAxis: {
                    type: 'value'
                },
                series: [
                    {
                        data: [
                            data.maxScore,
                            data.minScore,
                            data.avScoreg,
                            data.midScoreg
                        ],
                        type: 'line'
                    }
                ]
            }
            this.charts1.setOption(option)
        },
        drawLine() {
            //初始化echarts实例
            this.charts2 = echarts.init(this.$refs.cjxx)
            let option = {
                tooltip: {
                    trigger: 'axis' // 坐标轴触发
                },
                title: {
                    text: '考试成绩信息'
                },
                xAxis: {
                    type: 'category',
                    axisLabel: { interval: 0, rotate: 10 },
                    data: this.title
                },
                yAxis: {
                    type: 'value'
                },
                series: [
                    {
                        name: '成绩',
                        data: this.number,
                        type: 'bar'
                    }
                ]
            }
            this.charts2.setOption(option)
        },
        drawPie1(){
             //初始化echarts实例
            this.charts3 = echarts.init(this.$refs.zycsxx3)
            let option = {
                    tooltip: {
                        trigger: 'axis'
                    },
                    title: {
                        text: '作业成绩信息'
                    },
                    legend: {
                        top: 'bottom'
                    },
                    toolbox: {
                        show: true,
                        feature: {
                        mark: { show: true },
                        dataView: { show: true, readOnly: false },
                        restore: { show: true },
                        saveAsImage: { show: true }
                        }
                    },
                    series: [
                        {
                        name: 'Nightingale Chart',
                        type: 'pie',
                        radius: [30, 150],
                        center: ['50%', '50%'],
                        roseType: 'area',
                        itemStyle: {
                            borderRadius: 8
                        },
                        data: this.arrayData
                        }
                     ]
             };
             this.charts3.setOption(option)
        },
        drawSymbol(){
            this.charts4 = echarts.init(this.$refs.zycsxx4)
            let option = {
                tooltip: {
                    trigger: 'axis' 
                },
                title: {
                        text: '作业成绩信息'
                },
                xAxis: {
                    type: 'category',
                    axisLabel: { interval: 0, rotate: 10 },
                    data: this.title,
                },
                yAxis: {},
                series: [
                    {
                    symbolSize: 10,
                    data: this.number,
                    type: 'scatter'
                    }
                ]
            };
           this.charts4.setOption(option)
        },
        drawRadar(){
            this.charts5 = echarts.init(this.$refs.zycsxx5)
            let option = {
                tooltip: {
                    trigger: 'axis' 
                },
                title: {
                        text: '作业成绩信息',
                },
                tooltip: {
                    trigger: 'item'
                },
                legend: {
                        top: 'bottom'
                    },
                series: [
                    {
                    name: '',
                    type: 'pie',
                    radius: '50%',
                    data: this.arrayData,
                    emphasis: {
                        itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    }
                    }
                ]
             };
             this.charts5.setOption(option)
        },
        drawBar(){
            this.charts6 = echarts.init(this.$refs.zycsxx6)
            let option = {
                tooltip: {
                    trigger: 'axis' 
                },
                title: {
                        text: '作业成绩信息',
                },
                legend: {},
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: {
                    type: 'value',
                    boundaryGap: [0, 0.01]
                },
                yAxis: {
                    type: 'category',
                    data: this.title
                },
                series: [
                    {
                    name: this.title,
                    type: 'bar',
                    data: this.number,
                    itemStyle:{
                        normal: {
                            //这里是颜色
                            color: function(params) {
                                var colorList = ['#00A3E0','#FFA100', '#ffc0cb', '#CCCCCC', '#BBFFAA','#749f83', '#ca8622'];
                                return colorList[params.dataIndex]
                            }
                        }
                    }
                    },
                    
                ]
                };
            this.charts6.setOption(option)
        },
    }
}
</script>