<template>
    <div>
        <div>
            在线学习时间：{{ formatTime(time) }}
            <!--<el-button type="dan" @click="startTimer">开始学习</el-button>--><el-button
                type="dan"
                @click="stopTimer"
                >学习结束</el-button
            >
        </div>
        <div style="margin: 10px 0">
            <el-input
                style="width: 200px"
                placeholder="请输入名称"
                suffix-icon="el-icon-search"
                v-model="resourceName"
            ></el-input>
            <el-button class="ml-5" type="primary" @click="load"
                >搜索</el-button
            >
            <el-button type="warning" @click="reset">重置</el-button>
            <!-- <el-button v-if="user.role!='ROLE_REACHER' && user.role!=undefined" type="primary" @click="handleAdd">上传资料<i class="el-icon-upload"></i></el-button> -->
        </div>
        <el-table
            highlight-current-row
            v-loading="loading"
            tooltip-effect="dark"
            style="width: 100%"
            :border="true"
            :data="tableData"
            border
            stripe
            @selection-change="handleSelectionChange"
        >
            <el-table-column
                align="center"
                prop="resourceName"
                label="课程名称"
            ></el-table-column>
            <el-table-column align="center" prop="introduction" label="简介">
                <template v-slot="scope">
                    <el-button
                        type="primary"
                        @click="view(scope.row.introduction)"
                        >查看简介</el-button
                    >
                </template>
            </el-table-column>
            <el-table-column
                align="center"
                prop="createTimeStr"
                label="发布时间"
            ></el-table-column>
            <el-table-column
                align="center"
                prop="userName"
                label="发布人"
            ></el-table-column>
            <el-table-column prop="mp4Url" label="视频">
                <template slot-scope="scope">
                    <span v-if="scope.row.mp4Url != ''">
                        <Video
                            height="40px"
                            width="40px"
                            id="myVideo"
                            class="自定义"
                            controls
                            autoplay="muted"
                            preload="auto"
                            poster=""
                        >
                            <source :src="scope.row.mp4Url" type="video/mp4" />
                        </Video>
                    </span>
                </template>
            </el-table-column>
            <el-table-column align="center" label="操作" width="200">
                <template slot-scope="scope">
                    <el-button
                        type="primary"
                        @click="download(scope.row.fileUrl)"
                        >下载 <i class="el-icon-download"></i
                    ></el-button>
                    <el-button
                        type="danger"
                        v-if="user.role == 'ROLE_STUDENT'"
                        @click="goTest(scope.row.testType)"
                        >作业</el-button
                    >
                    <!-- <router-link to="/front/test/TestOnline?">去测试</router-link> -->
                </template>
            </el-table-column>
        </el-table>
        <!-- 分页 -->
        <div style="padding: 10px 0">
            <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="pageNum"
                :page-sizes="[2, 5, 10, 20]"
                :page-size="pageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total"
            >
            </el-pagination>
        </div>

        <!-- 新增弹窗 -->
        <el-dialog
            title="上传学习资料"
            :visible.sync="dialogFormVisible"
            width="60%"
            :close-on-click-modal="false"
        >
            <el-form
                label-width="100px"
                size="small"
                style="width: 90%; text-align: left"
            >
                <el-form-item label="课程名称">
                    <el-input
                        v-model="form.resourceName"
                        autocomplete="off"
                        accept=".doc,.docx"
                    ></el-input>
                </el-form-item>
                <el-form-item label="视频文件">
                    <el-upload
                        class="upload-demo"
                        action="http://localhost:9090/file/upload"
                        :key="fileUpdate1"
                        :on-preview="handlePreview1"
                        :on-remove="handleRemove1"
                        :before-remove="beforeRemove1"
                        multiple
                        :limit="1"
                        :on-exceed="handleExceed1"
                        :on-success="handleUploadSuccess1"
                        :before-upload="beforeUpload1"
                        :file-list="fileList1"
                    >
                        <el-button size="small" type="primary"
                            >点击上传</el-button
                        >
                        <!-- <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div> -->
                    </el-upload>
                </el-form-item>
                <el-form-item label="资料文件">
                    <el-upload
                        style="text-align: left"
                        class="upload-demo"
                        accept="doc,docx"
                        action="http://localhost:9090/file/upload"
                        :key="fileUpdate"
                        :on-preview="handlePreview"
                        :on-remove="handleRemove"
                        :before-remove="beforeRemove"
                        multiple
                        :limit="1"
                        :before-upload="beforeUpload"
                        :on-exceed="handleExceed"
                        :on-success="handleUploadSuccess"
                        :file-list="fileList"
                    >
                        <el-button size="small" type="primary"
                            >点击上传<i class="el-icon-upload"></i
                        ></el-button>
                    </el-upload>
                </el-form-item>
                <el-form-item label="资料简介">
                    <div id="richText"></div>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="uploadResource"
                    >确 定</el-button
                >
            </div>
        </el-dialog>

        <el-dialog
            title="资料简介"
            :visible.sync="dialogFormVisible1"
            width="60%"
            :close-on-click-modal="false"
        >
            <el-card>
                <div v-html="introductionViewContent"></div>
            </el-card>
        </el-dialog>
    </div>
</template>
  
  <script>
import E from 'wangeditor'
let editor

export default {
    name: 'StudyResource',
    data() {
        return {
            onlineStudayTime: 0, // 用于存储时间
            entity: { onlineStudayTime: 0 },
            time: 0,
            timer: null, // 用于存储定时器
            fileUpdate: 0,
            fileUpdate1: 0,
            fileList: [],
            fileList1: [],
            tableData: [],
            loading: '',
            total: 0,
            pageNum: 1,
            pageSize: 10,
            state: '1',
            resourceName: '',
            form: {},
            dialogFormVisible: false,
            dialogFormVisible1: false,
            introductionViewContent: '',
            multipleSelection: [],
            user: localStorage.getItem('user')
                ? JSON.parse(localStorage.getItem('user'))
                : {}
        }
    },
    beforeDestroy() {
        // 组件销毁前清除定时器以避免内存泄露
        clearInterval(this.timer)
    },

    created() {
        this.load()
        this.startTimer()
    },
    mounted() {},
    methods: {
        startTimer() {
            if (this.timer == null) {
                this.timer = setInterval(() => {
                    this.time++
                }, 1000)
            }
        },
        stopTimer() {
            this.entity = { onlineStudayTime: this.time }
            if (this.timer != null) {
                clearInterval(this.timer)
                this.timer = null
            }
            //将时间保存到数据库
            this.request
                .post('/studyOnlineTime/add/', this.entity)
                .then((res) => {
                    if (res.code === '200') {
                        this.$message.success('保存成功')
                    } else {
                        this.$message.error('保存失败')
                    }
                })
        },

        formatTime(time) {
            // 将时间格式化为 hh:mm:ss 的形式
            let hour = Math.floor(time / 3600)
            let minute = Math.floor((time - hour * 3600) / 60)
            let second = Math.floor(time - hour * 3600 - minute * 60)
            return `${hour.toString().padStart(2, '0')}:${minute
                .toString()
                .padStart(2, '0')}:${second.toString().padStart(2, '0')}`
        },
        view(introduction) {
            this.introductionViewContent = introduction
            this.dialogFormVisible1 = true
        },

        load() {
            this.request
                .get('/resource/front/page/', {
                    params: {
                        pageNum: this.pageNum,
                        pageSize: this.pageSize,
                        state: this.state,
                        resourceName: this.resourceName,
                        type: this.$route.query.type
                    }
                })
                .then((res) => {
                    this.tableData = res.data.records
                    this.total = res.data.total
                })
        },
        handleSelectionChange(val) {
            console.log(val)
            this.multipleSelection = val
        },

        reset() {
            this.name = ''
            this.load()
        },
        handleAdd() {
            this.dialogFormVisible = true
            this.form = { img: '' }
            this.$nextTick(() => {
                if (!editor) {
                    editor = new E('#richText')
                    // 配置 server 接口地址
                    editor.config.uploadImgServer =
                        'http://localhost:9090/file/upload'
                    editor.config.uploadFileName = 'editorFile@' + this.name
                    editor.create()
                }
                editor.txt.html('')

                if (this.$refs.img) {
                    this.$refs.img.clearFiles()
                }
                if (this.$refs.file) {
                    this.$refs.file.clearFiles()
                }
            })
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
            this.fileList.add(file)
        },
        handleUploadSuccess1(res, file) {
            this.form.mp4Url = res
            this.fileList1.push(file)
        },
        download(url) {
            window.open(url)
        },
        handleRemove(file, fileList) {
            console.log(file, fileList)
        },
        handleRemove1(file, fileList1) {
            console.log(file, fileList1)
        },
        handlePreview(file) {
            console.log(file)
        },
        handlePreview1(file) {
            console.log(file)
        },
        handleExceed(files, fileList) {
            this.$message.warning(
                `只能上传 1 个文件，重新上传请点击已上传文件右侧按钮删除后重试`
            )
        },
        handleExceed1() {
            this.$message.warning(
                `只能上传 1 个文件，重新上传请点击已上传文件右侧按钮删除后重试`
            )
        },
        beforeRemove(file, fileList) {
            return this.$confirm(`确定移除 ${file.name}？`)
        },
        beforeRemove1(file) {
            return this.$confirm(`确定移除 ${file.name}？`)
        },
        beforeUpload(file, fileList) {
            const typeExtensions = ['doc', 'docx', 'jpg', 'jpeg', 'png', 'mp4'] // 可以自定义需要支持的音频格式
            const fileExtension = file.name.split('.').pop().toLowerCase() // 根据文件名获取文件扩展名并转换为小写字母形式
            const maxSize = 2 * 1024 * 1024
            if (file.size > maxSize) {
                this.$message.warning('请上传2MB以内的文件！')
            }
            if (!typeExtensions.includes(fileExtension)) {
                this.$message.warning(
                    '上传文件格式只能是doc/docx/jpg/jpeg/png/ppt/mp4！'
                )
                return false
            }
        },
        beforeUpload1(file) {
            const typeExtensions = ['mp4'] // 可以自定义需要支持的音频格式
            const fileExtension = file.name.split('.').pop().toLowerCase() // 根据文件名获取文件扩展名并转换为小写字母形式
            const maxSize = 10 * 1024 * 1024
            if (file.size > maxSize) {
                this.$message.warning('请上传10MB以内的文件！')
            }
            if (!typeExtensions.includes(fileExtension)) {
                this.$message.warning('上传文件格式只能是mp4！')
                return false
            }
        },

        uploadResource() {
            // 富文本编辑的内容需要手动赋值
            this.form.introduction = editor.txt.html()
            this.request.post('/resource', this.form).then((res) => {
                if (res.code === '200') {
                    this.$message.success('保存成功')
                    this.dialogFormVisible = false
                    this.load()
                } else {
                    this.$message.error('保存失败')
                }
            })
        },
        //作业测试
        goTest(testType) {
            if (null != testType) {
                window.location.href = '/front/TestOnline?testType=' + testType
            } else {
                this.$message.error('还没有添加测试试卷！')
            }
        }
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
    border-color: #409eff;
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
  