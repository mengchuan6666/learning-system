package com.online.study.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.study.entity.Question;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 
 */
public interface IQuestionService extends IService<Question> {

    public Page<Question> list(Page<Question> tPage, QueryWrapper<Question> queryWrapper);



    Question[] getQuestionArray(int type,Integer ctypeId,String pointId);

    List<Question> getQuestionListWithOutSId(Question tmpQuestion);

    List<Question> listByCtypeID(Integer ctypeId);
}
