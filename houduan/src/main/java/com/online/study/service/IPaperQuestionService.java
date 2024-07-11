package com.online.study.service;

import com.online.study.entity.PaperQuestion;
import com.baomidou.mybatisplus.extension.service.IService;
import com.online.study.entity.Question;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 
 */
public interface IPaperQuestionService extends IService<PaperQuestion> {

    List<Question> selectQuestions(Integer paperId);

}
