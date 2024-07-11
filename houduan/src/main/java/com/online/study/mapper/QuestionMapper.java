package com.online.study.mapper;

import com.online.study.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 
 */
public interface QuestionMapper extends BaseMapper<Question> {


    List<Question> getQuestionArray(int type, Integer ctypeId,String pointId);

    List<Question> getQuestionListWithOutSId(Question tmpQuestion);

    List<Question> listByCtypeID(Integer ctypeId);
}
