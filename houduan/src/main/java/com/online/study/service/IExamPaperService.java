package com.online.study.service;

import com.online.study.entity.ExamPaper;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 
 */
public interface IExamPaperService extends IService<ExamPaper> {

    void addPaper(ExamPaper examPaper);

}
