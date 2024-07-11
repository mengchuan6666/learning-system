package com.online.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.online.study.common.Result;
import com.online.study.entity.Comment;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 
 */
public interface ICommentService extends IService<Comment> {

    Result getCommentByUser(Integer foreignId);
}
