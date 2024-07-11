<template>
    <div>
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
        </div>

        <div style="margin: 10px 0">
            <el-button type="primary" @click="handleAdd"
                >新增 <i class="el-icon-circle-plus-outline"></i
            ></el-button>
            <el-popconfirm
                class="ml-5"
                confirm-button-text="确定"
                cancel-button-text="我再想想"
                icon="el-icon-info"
                icon-color="red"
                title="您确定批量删除这些数据吗？"
                @confirm="delBatch"
            >
            <el-button type="danger" slot="reference"
                    >批量删除 <i class="el-icon-remove-outline"></i
                ></el-button>
            </el-popconfirm>
            <el-upload action="http://localhost:9090/resource/import" :show-file-list="false" accept="xlsx" :on-success="handleExcelImportSuccess" style="display: inline-block">
                <el-button type="primary" class="ml-5">导入 <i class="el-icon-bottom"></i></el-button>
            </el-upload>
            <el-button type="primary" @click="exp" class="ml-5">导出 <i class="el-icon-top"></i></el-button>
        </div>

        <el-table
            :data="tableData"
            border
            stripe
            :header-cell-class-name="'headerBg'"
            @selection-change="handleSelectionChange"
        >
            <el-table-column type="selection" width="55"></el-table-column>
            <el-table-column
                prop="id"
                label="ID"
                width="80"
                sortable
            ></el-table-column>
            <el-table-column label="课程类型">
                <template v-slot="scope">
                    <span>{{
                        typeOptions.find((v) => v.id === scope.row.type)
                            ? typeOptions.find((v) => v.id === scope.row.type)
                                  .typeName
                            : ''
                    }}</span>
                </template>
            </el-table-column>
            <el-table-column
                prop="resourceName"
                label="课程名称"
            ></el-table-column>
            <el-table-column prop="introduction" label="简介">
                <template v-slot="scope">
                    <el-button
                        type="primary"
                        @click="view(scope.row.introduction)"
                        >查看简介</el-button
                    >
                </template>
            </el-table-column>
            <el-table-column
                prop="createTimeStr"
                label="发布时间"
            ></el-table-column>
            <el-table-column prop="userName" label="发布人"></el-table-column>
            <el-table-column prop="status" label="状态">
                <template slot-scope="scope">
                    <span v-if="scope.row.status == 0">未审核</span>
                    <span v-if="scope.row.status == 1">已审核</span>
                    <span v-if="scope.row.status == 3">未公布</span>
                    <span v-if="scope.row.status == 4">已公布</span>
                    <span v-if="scope.row.status == 9">已删除</span>
                </template>
            </el-table-column>
            <el-table-column prop="mp4Url" label="视频">
                <template slot-scope="scope">
                    <span v-if="scope.row.mp4Url != ''">
                        <Video
                            height="40px"
                            width="40px"
                            id="myVideo"
                            class="自定义"
                            controls
                            autoplay="true"
                            preload="auto"
                            poster=""
                        >
                            <source :src="scope.row.mp4Url" type="video/mp4" />
                        </Video>
                    </span>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="300" align="center">
                <template slot-scope="scope">
                    <el-button type="primary" @click="handleUpdate(scope.row)"
                        >添加试卷 <i class="el-icon-edit"></i
                    ></el-button>
                    <el-button type="success" @click="handleEdit(scope.row)"
                        >编辑 <i class="el-icon-edit"></i
                    ></el-button>
                    <el-popconfirm
                        class="ml-5"
                        confirm-button-text="确定"
                        cancel-button-text="我再想想"
                        icon="el-icon-info"
                        icon-color="red"
                        title="您确定删除吗？"
                        @confirm="del(scope.row.id)"
                    >
                        <el-button type="danger" slot="reference"
                            >删除 <i class="el-icon-remove-outline"></i
                        ></el-button>
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
                :total="total"
            >
            </el-pagination>
        </div>

        <!-- 新增弹窗 -->
        <el-dialog
            title="学习资料"
            :visible.sync="dialogFormVisible"
            width="60%"
            :close-on-click-modal="false"
        >
            <el-form label-width="100px" size="small" style="width: 90%">
                <el-form-item label="课程类型">
                    <el-select
                        v-model="typeIds"
                        placeholder="请选择类型"
                        style="width: 200px"
                        class="ml-5"
                    >
                        <el-option
                            v-for="item in typeOptions"
                            :key="item.id"
                            :label="item.typeName"
                            :value="item.id"
                        >
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="课程名称">
                    <el-input
                        v-model="form.resourceName"
                        autocomplete="off"
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
                        :file-list="fileList"
                    >
                        <el-button size="small" type="primary"
                            >点击上传</el-button
                        >
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

        <el-dialog
            title="简介"
            :visible.sync="dialogFormVisible1"
            width="60%"
            :close-on-click-modal="false"
        >
            <el-card>
                <div v-html="introductionViewContent"></div>
            </el-card>
        </el-dialog>

        <el-dialog
            title="如果没有测试试卷请先添加"
            :visible.sync="dialogFormVisible4"
            width="60%"
            :close-on-click-modal="false"
        >
            <el-form label-width="100px" size="small" style="width: 90%">
                <el-form-item label="测试试卷">
                    <el-select
                        v-model="examIds"
                        placeholder="请选择测试试卷"
                        style="width: 200px"
                        class="ml-5"
                    >
                        <el-option
                            v-for="item in examOptions"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id"
                        >
                        </el-option>
                    </el-select>
                    <el-button @click="dialogFormVisible4 = false"
                        >取 消</el-button
                    >
                    <el-button type="primary" @click="updateExam"
                        >确 定</el-button
                    >
                </el-form-item>
            </el-form>
        </el-dialog>
    </div>
</template>
<p v-html="test"></p>
<script>
import E from 'wangeditor'
let editor

export default {
    name: 'StudyResource',
    data() {
        return {
            fileUpdate: 0,
            fileUpdate1: 0,
            fileList: [],
            fileList1: [],
            tableData: [],
            //类型
            typeOptions: [],
            typeIds: '',
            rsId:'',
            //测试试卷
            examOptions: [],
            examIds: '',
            testType: '',
            total: 0,
            pageNum: 1,
            pageSize: 10,
            resourceName: '',
            form: {},
            dialogFormVisible: false,
            dialogFormVisible1: false,
            dialogFormVisible4: false,
            introductionViewContent: '',
            multipleSelection: [],
            user: localStorage.getItem('user')
                ? JSON.parse(localStorage.getItem('user'))
                : {}
        }
    },
    created() {
        this.load()
    },
    methods: {
        //添加试卷
        handleUpdate(row) {
            this.reset()
            this.form = JSON.parse(JSON.stringify(row))
            this.request
                .get('/exam/getTestExamPaper?ctypeId=' + row.type)
                .then((res) => {
                    this.examOptions = res.data
                })
            this.dialogFormVisible4 = true
        },
        //保存添加的试卷
        updateExam() {
            this.form.testType = this.examIds
            this.request.post('/resource', this.form).then((res) => {
                if (res.code === '200') {
                    this.$message.success('试卷添加成功！')
                    this.dialogFormVisible = false
                    this.load()
                } else {
                    this.$message.error('试卷添加失败！')
                }
            })
            this.dialogFormVisible4 = false
        },
        view(introduction) {
            this.introductionViewContent = introduction
            this.dialogFormVisible1 = true
        },
        load() {
            this.request
                .get('/resource/page', {
                    params: {
                        pageNum: this.pageNum,
                        pageSize: this.pageSize,
                        resourceName: this.resourceName
                    }
                })
                .then((res) => {
                    this.tableData = res.data.records
                    this.total = res.data.total
                })
            this.request.get('/resourceType').then((res) => {
                this.typeOptions = res.data
            })
        },
        save() {
            // 富文本编辑的内容需要手动赋值
            this.form.introduction = editor.txt.html()
            this.form.type = this.typeIds
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
        handleAdd() {
            this.reset()
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
        handleEdit(row) {
            this.reset()
            this.form = JSON.parse(JSON.stringify(row))
            this.typeIds = this.form.type
            this.rsId=this.form.id;
            debugger
            console.log(this.form);

            this.request.get('/resourceType').then((res) => {
                this.typeOptions = res.data
            })
            this.dialogFormVisible = true
            this.$nextTick(() => {
                if (!editor) {
                    editor = new E('#richText')
                    // todo 配置 server 接口地址
                    editor.config.uploadImgServer =
                        'http://localhost:9090/file/upload'
                    editor.config.uploadFileName = 'editorFile@' + this.name
                    editor.create()
                }
                editor.txt.html(this.form.introduction)
                if (this.$refs.img) {
                    this.$refs.img.clearFiles()
                }
                if (this.$refs.file) {
                    this.$refs.file.clearFiles()
                }
                //文件或图片
                this.fileList = this.fileList.filter((e) => {
                    return false
                })
                this.fileUpdate++
                this.request
                    .get('/file/' + row.sysFileId + '/info')
                    .then((res) => {
                        if (res.code === '200') {
                            if(res.data!=null){
                                this.fileList.push(res.data)
                            }
                            this.fileUpdate++
                        } else {
                            this.$message.error('加载资源文件信息失败')
                        }
                    })

                //视频
                this.fileList1 = this.fileList1.filter((e) => {
                    return false
                })
                this.fileUpdate1++
                this.request
                    .get('/file/' + row.sysMp4Id + '/info')
                    .then((res) => {
                        if (res.code === '200') {
                            if(res.data!=null){
                               this.fileList1.push(res.data)
                            }
                            this.fileUpdate1++
                        } else {
                            this.$message.error('加载资源文件信息失败')
                        }
                    })
            })
        },
        del(id) {
            this.request.delete('/resource/' + id).then((res) => {
                if (res.code === '200') {
                    this.$message.success('删除成功')
                    this.load()
                } else {
                    this.$message.error('删除失败')
                }
            })
        },
        handleSelectionChange(val) {
            console.log(val)
            this.multipleSelection = val
        },
        delBatch() {
            if (!this.multipleSelection.length) {
                this.$message.error('请选择需要删除的数据')
                return
            }
            let ids = this.multipleSelection.map((v) => v.id) // [{}, {}, {}] => [1,2,3]
            this.request.post('/resource/del/batch', ids).then((res) => {
                if (res.code === '200') {
                    this.$message.success('批量删除成功')
                    this.load()
                } else {
                    this.$message.error('批量删除失败')
                }
            })
        },
        reset() {
            this.name = ''
            this.examIds = ''
            this.resourceName = ''
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
        handleUploadSuccess(res, file) {
            this.form.fileUrl = res
            this.fileList.push(file)
        },
        handleUploadSuccess1(res, file) {
            this.form.mp4Url = res
            this.fileList1.push(file)
        },
        download(url) {
            window.open(url)
        },
        //删除上传的文件
        handleRemove(file) {
            this.request
                    .delete('/resource/deleteByRSId/' +file.id+'/'+this.rsId+'/'+file.type)
                    .then((res) => {
                        if (res.code === '200') {
                            this.$message.success('删除成功！')
                        } else {
                            this.$message.error('删除失败！')
                        }
                    })

        },
        //删除上传的视频
        handleRemove1(file) {
            this.request
                    .delete('/resource/deleteByRSId/' +file.id+'/'+this.rsId+'/'+file.type)
                    .then((res) => {
                        if (res.code === '200') {
                            this.$message.success('删除成功！')
                        } else {
                            this.$message.error('删除失败！')
                        }
                    })
           
        },
        handlePreview(file) {
            console.log(file)
        },
        handlePreview1(file) {
            console.log(file)
        },
        handleExceed() {
            this.$message.warning(
                `只能上传 1 个文件，重新上传请点击已上传文件右侧按钮删除后重试`
            )
        },
        handleExceed1() {
            this.$message.warning(
                `只能上传 1 个文件，重新上传请点击已上传文件右侧按钮删除后重试`
            )
        },
        beforeRemove(file) {
            return this.$confirm(`确定移除 ${file.name}？`)
        },
        beforeRemove1(file) {
            return this.$confirm(`确定移除 ${file.name}？`)
        },
        beforeUpload(file) {
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
        exp() {
        window.open("http://localhost:9090/resource/export")
        },
        handleExcelImportSuccess() {
        this.$message.success("导入成功")
        this.load()
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
