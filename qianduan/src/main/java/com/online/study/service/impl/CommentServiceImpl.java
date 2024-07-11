package com.online.study.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.study.common.Result;
import com.online.study.common.RoleEnum;
import com.online.study.entity.Comment;
import com.online.study.entity.User;
import com.online.study.mapper.CommentMapper;
import com.online.study.service.ICommentService;
import com.online.study.service.IRoleService;
import com.online.study.service.IUserService;
import com.online.study.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {
    @Autowired
    private IUserService userService;
    @Autowired
    private IRoleService roleService;
    public Result getCommentByUser(Integer userId) {
        User user = TokenUtils.getCurrentUser();
        List<Comment> comments = new ArrayList<>();
        // 鉴权
        if(!RoleEnum.ROLE_ADMIN.name().equals(user.getRole()) && !RoleEnum.ROLE_TEACHER.name().equals(user.getRole())) {
            //comments = this.getBaseMapper().findAllByUserId(userId);
        } else {
            //comments = this.lambdaQuery().orderByDesc(Comment::getId).list();
        }
        comments = this.lambdaQuery().orderByDesc(Comment::getId).list();

        if(CollectionUtil.isEmpty(comments)) {
            return Result.success(new ArrayList<>());
        }

        List<Comment> rootComments = comments
                .stream()
                .filter(comment -> comment.getPid().equals(0))
                .collect(Collectors.toList());

        List<Comment> replyComments = this
                .getBaseMapper()
                .findAllByPids(rootComments
                        .stream()
                        .map(Comment::getId)
                        .collect(Collectors.toList()));
        // 根据pid设置回复列表
        for (Comment rootComment : rootComments) {
            List<Comment> curReplies = replyComments
                    .stream()
                    .filter(comment -> rootComment.getId().equals(comment.getPid()))
                    .collect(Collectors.toList());
            rootComment.setReplies(curReplies);
            rootComment.setImg(userService.getById(rootComment.getUserId()).getAvatarUrl());
            rootComment.setReplyNum(rootComment.getReplies().size());
        }

        // 根据pid设置用户头像
        for (Comment reply : replyComments) {
            reply.setImg(userService.getById(reply.getUserId()).getAvatarUrl());
        }

        return Result.success(rootComments);
    }
}
