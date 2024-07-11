package com.online.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.online.study.entity.ExamPaper;
import com.online.study.mapper.ExamPaperMapper;
import com.online.study.service.IExamPaperService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 
 */
@Service
public class ExamPaperServiceImpl extends ServiceImpl<ExamPaperMapper, ExamPaper> implements IExamPaperService {

    @Transactional
    @Override
    public void addPaper(ExamPaper examPaper) {
        // 先删除本考试的记录
        remove(new UpdateWrapper<ExamPaper>().eq("exam_id", examPaper.getExamId()));
        save(examPaper);
    }
}
