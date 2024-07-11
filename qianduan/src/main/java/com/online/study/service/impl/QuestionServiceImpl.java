package com.online.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.study.entity.Question;
import com.online.study.mapper.QuestionMapper;
import com.online.study.service.IQuestionService;
import com.online.study.service.IStudyResourceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements IQuestionService {

    @Resource
    private IStudyResourceTypeService studyResourceTypeService;

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public Page<Question> list(Page<Question> tPage, QueryWrapper<Question> queryWrapper) {

        Page<Question> page = this.page(tPage, queryWrapper);
        for (int i = 0; i <page.getSize() ; i++) {
            Question question=page.getRecords().get(i);
            question.setCtypeName(studyResourceTypeService.getById(question.getCtypeId()).getTypeName());
        }
        return page;
    }

    @Override
    public Question[] getQuestionArray(int type,Integer ctypeId,String pointId) {

        List<Question> questionArray = questionMapper.getQuestionArray(type,ctypeId,pointId);
        return questionArray.toArray(new Question[questionArray.size()]);
    }

    @Override
    public List<Question> getQuestionListWithOutSId(Question tmpQuestion) {

        return questionMapper.getQuestionListWithOutSId(tmpQuestion);
    }

    @Override
    public List<Question> listByCtypeID(Integer ctypeId) {
        return questionMapper.listByCtypeID(ctypeId);
    }
}
