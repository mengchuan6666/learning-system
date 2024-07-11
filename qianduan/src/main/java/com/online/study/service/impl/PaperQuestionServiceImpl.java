package com.online.study.service.impl;

import com.online.study.entity.PaperQuestion;
import com.online.study.entity.Question;
import com.online.study.mapper.PaperQuestionMapper;
import com.online.study.service.IPaperQuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 
 */
@Service
public class PaperQuestionServiceImpl extends ServiceImpl<PaperQuestionMapper, PaperQuestion> implements IPaperQuestionService {

    @Resource
    private PaperQuestionMapper paperQuestionMapper;


    @Override
    public List<Question> selectQuestions(Integer paperId) {
        return paperQuestionMapper.selectQuestions(paperId);
    }
}
