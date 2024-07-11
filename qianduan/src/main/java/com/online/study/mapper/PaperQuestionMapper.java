package com.online.study.mapper;

import com.online.study.entity.PaperQuestion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.online.study.entity.Question;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 
 */
public interface PaperQuestionMapper extends BaseMapper<PaperQuestion> {

    List<Question> selectQuestions(Integer paperId);

}
