
<template>
    <el-card style="height: 1000px;">
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
    <div
        class="comment-main"
        style="
            max-width: 600px;
            width: 40%;
            vertical-align: middle;
            position: absolute;
            left: 30%;
        "
    >
        <div v-clickoutside="hideReplyBtn" @click="inputFocus" class="my-reply">
            <el-avatar
                class="header-img"
                :size="40"
                :src="myHeader"
            ></el-avatar>
            <div class="reply-info">
                <div
                    tabindex="0"
                    contenteditable="true"
                    id="replyInput"
                    spellcheck="false"
                    placeholder="输入给老师的留言..."
                    class="reply-input"
                    @focus="showReplyBtn"
                    @input="onDivInput($event)"
                ></div>
            </div>
            <div class="reply-btn-box" v-show="btnShow">
                <el-button
                    class="reply-btn"
                    size="medium"
                    @click="sendComment"
                    type="primary"
                    >发表留言</el-button
                >
            </div>
        </div>
        <!-- 留言列表 -->
        <div
            v-for="(item, i) in comments"
            style="text-align: left"
            :key="item.id"
            class="author-title reply-father"
        >
            <el-avatar
                class="header-img"
                :size="40"
                :src="item.img"
            ></el-avatar>
            <div class="author-info">
                <span class="author-name">{{ item.username }}</span>
                <span class="author-time">{{ item.createtime }}</span>
            </div>
            <div class="icon-btn">
                <span @click="showReplyInput(i, item.username, item.id)"
                    ><i class="iconfont el-icon-s-comment"></i
                    >{{ item.replyNum }}</span
                >
            </div>
            <div class="talk-box">
                <p>
                    <span class="reply">{{ item.content }}</span>
                </p>
            </div>
            <!-- 留言回复栏 -->
            <div class="reply-box">
                <div
                    v-for="reply in item.replies"
                    :key="reply.id"
                    class="author-title"
                >
                    <el-avatar
                        class="header-img"
                        :size="40"
                        :src="reply.img"
                    ></el-avatar>
                    <div class="author-info">
                        <span class="author-name">{{ reply.username }}</span>
                        <span class="author-time">{{ reply.createtime }}</span>
                    </div>
                    <div class="icon-btn">
                        <span
                            @click="showReplyInput(i, reply.username, item.id)"
                            ><i class="iconfont el-icon-s-comment"></i
                        ></span>
                    </div>
                    <div class="talk-box">
                        <p>
                            <span>回复 {{ reply.target }}:</span>
                            <span class="reply">{{ reply.content }}</span>
                        </p>
                    </div>
                    <div class="reply-box"></div>
                </div>
            </div>
            <!-- 留言回复输入 :key触发v-show的刷新 -->
            <div
                v-show="replyInputShow(i)"
                class="my-reply my-comment-reply"
                :key="index"
            >
                <el-avatar
                    class="header-img"
                    :size="40"
                    :src="myHeader"
                ></el-avatar>
                <div class="reply-info">
                    <div
                        tabindex="0"
                        contenteditable="true"
                        spellcheck="false"
                        placeholder="输入留言..."
                        @input="onDivInput($event)"
                        class="reply-input reply-comment-input"
                    ></div>
                </div>
                <div class="reply-btn-box">
                    <el-button
                        class="reply-btn"
                        size="medium"
                        @click="sendCommentReply(i)"
                        type="primary"
                        >发表留言</el-button
                    >
                </div>
            </div>
        </div>
    </div>
</el-card>
</template>

<script>
import '@/assets/css/front.css'

const clickoutside = {
    // 初始化指令
    bind(el, binding, vnode) {
        function documentHandler(e) {
            // 这里判断点击的元素是否是本身，是本身，则返回
            if (el.contains(e.target)) {
                return false
            }
            // 判断指令中是否绑定了函数
            if (binding.expression) {
                // 如果绑定了函数 则调用那个函数，此处binding.value就是handleClose方法
                binding.value(e)
            }
        }
        // 给当前元素绑定个私有变量，方便在unbind中可以解除事件监听
        el.vueClickOutside = documentHandler
        document.addEventListener('click', documentHandler)
    },
    update() {},
    unbind(el, binding) {
        // 解除事件监听
        document.removeEventListener('click', el.vueClickOutside)
        delete el.vueClickOutside
    }
}
export default {
    data() {
        return {
            isInit: false,
            btnShow: false,
            index: -1,
            replyComment: '',
            user: {},
            myName: '',
            myHeader: '',
            myUserId: 0,
            replyTarget: '',
            pid: 0,
            comments: []
        }
    },
    created() {
        this.load()
    },
    directives: { clickoutside },
    methods: {
        load() {
            this.user = localStorage.getItem('user')
                ? JSON.parse(localStorage.getItem('user'))
                : {}
            this.myName = this.user.username
            this.myHeader = this.user.avatarUrl
            this.myUserId = this.user.id
            if (this.myUserId != undefined) {
                this.request
                    .get('/comment?foreignId=' + this.myUserId)
                    .then((res) => {
                        if (res.status === 500) {
                            this.$notify.error('系统错误')
                            return {}
                        }
                        this.comments = res.data
                        // 初始的inputShow为false
                        this.comments.forEach((e) => {
                            e.inputShow = false
                        })
                        this.isInit = true
                    })
            }
        },
        inputFocus() {
            var replyInput = document.getElementById('replyInput')
            replyInput.style.padding = '8px 8px'
            replyInput.style.border = '2px solid blue'
            replyInput.focus()
        },
        showReplyBtn() {
            this.btnShow = true
            this.comments.forEach((e) => {
                e.inputShow = false
            })
            this.index = -1
        },
        hideReplyBtn() {
            this.btnShow = false
            replyInput.style.padding = '10px'
            replyInput.style.border = 'none'
        },
        /**
         * 弹出回复 i是div的index
         */
        showReplyInput(i, replyTarget, pid) {
            // 隐藏之前显示的回复 显示当前回复
            if (this.index >= 0) {
                this.comments[this.index].inputShow = false
            }
            this.index = i
            this.comments[i].inputShow = true
            // 设置好回复对象名称，回复留言的id
            this.replyTarget = replyTarget
            this.pid = pid
            this.hideReplyBtn()
            var replyInput = document.getElementsByClassName(
                'reply-comment-input'
            )[i]
            replyInput.focus()
        },
        replyInputShow(i) {
            if (!this.isInit) {
                return false
            }
            return this.comments[i].inputShow
        },
        //发送留言
        sendComment() {
            if (this.myUserId != undefined) {
                if (!this.replyComment) {
                    this.$message({
                        showClose: true,
                        type: 'warning',
                        message: '留言不能为空'
                    })
                } else {
                    let a = {}
                    var input = document.getElementById('replyInput')
                    a.username = this.myName
                    a.userId = this.myUserId
                    a.content = this.replyComment
                    // 父级留言
                    a.pid = 0
                    a.target = ''
                    //发送留言请求
                    this.request.post('/comment', a).then((res) => {
                        if (res.code == 200) {
                            this.replyComment = ''
                            input.innerHTML = ''
                            this.$notify.success('成功')
                            this.load()
                            this.dialogFormVisible = false
                        }
                    })
                }
            } else {
                this.$message({
                    showClose: true,
                    type: 'warning',
                    message: '请先登录！'
                })
            }
        },
        //发送留言的回复
        sendCommentReply(i) {
            if (!this.replyComment) {
                this.$message({
                    showClose: true,
                    type: 'warning',
                    message: '留言不能为空'
                })
            } else {
                let a = {}
                a.username = this.myName
                a.userId = this.myUserId
                a.content = this.replyComment
                // 父级留言
                a.pid = this.pid
                a.target = this.replyTarget
                //发送留言请求
                this.request.post('/comment', a).then((res) => {
                    if (res.code == 200) {
                        debugger
                        console.log(1111)
                        this.replyComment = ''
                        this.$notify.success('成功')
                        this.dialogFormVisible = false
                        document.getElementsByClassName('reply-comment-input')[
                            i
                        ].innerHTML = ''
                        this.load()
                    }
                })
            }
        },
        // div输入时，动态更新replyComment
        onDivInput: function (e) {
            this.replyComment = e.target.innerHTML
        }
    }
}
</script>

<style scoped>
.comment-main {
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
[hidden] {
    display: none !important;
}
.my-reply {
    padding: 10px;
    background-color: #fafbfc;
}
.my-reply .header-img {
    display: inline-block;
    vertical-align: top;
}
.my-reply .reply-info {
    display: inline-block;
    margin-left: 5px;
    width: 90%;
}
@media screen and (max-width: 1200px) {
    .my-reply .reply-info {
        width: 80%;
    }
}
.my-reply .reply-info .reply-input {
    min-height: 20px;
    line-height: 22px;
    padding: 10px 10px;
    color: #ccc;
    background-color: #fff;
    border-radius: 5px;
}
.my-reply .reply-info .reply-input:empty:before {
    content: attr(placeholder);
}
.my-reply .reply-info .reply-input:focus:before {
    content: none;
}
.my-reply .reply-info .reply-input:focus {
    padding: 8px 8px;
    border: 2px solid #00f;
    box-shadow: none;
    outline: none;
}
.my-reply .reply-btn-box {
    height: 25px;
    margin: 10px 0;
}
.my-reply .reply-btn-box .reply-btn {
    position: relative;
    float: right;
    margin-right: 15px;
}
.my-comment-reply {
    margin-left: 50px;
}
.my-comment-reply .reply-input {
    width: flex;
}
.author-title:not(:last-child) {
    border-bottom: 1px solid rgba(178, 186, 194, 0.3);
}
.author-title {
    padding: 10px;
}
.author-title .header-img {
    display: inline-block;
    vertical-align: top;
}
.author-title .author-info {
    display: inline-block;
    margin-left: 5px;
    width: 60%;
    height: 40px;
    line-height: 20px;
}
.author-title .author-info > span {
    display: block;
    cursor: pointer;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}
.author-title .author-info .author-name {
    color: #000;
    font-size: 18px;
    font-weight: bold;
}
.author-title .author-info .author-time {
    font-size: 14px;
}
.author-title .icon-btn {
    width: 30%;
    padding: 0 !important;
    float: right;
}
@media screen and (max-width: 1200px) {
    .author-title .icon-btn {
        width: 20%;
        padding: 7px;
    }
}
.author-title .icon-btn > span {
    cursor: pointer;
}
.author-title .icon-btn .iconfont {
    margin: 0 5px;
}
.author-title .talk-box {
    margin: 0 50px;
}
.author-title .talk-box > p {
    margin: 0;
}
.author-title .talk-box .reply {
    font-size: 16px;
    color: #000;
}
.author-title .reply-box {
    margin: 10px 0 0 50px;
    background-color: #efefef;
}
.el-card__body {
    padding: 20px;
    height: 1000px;
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
