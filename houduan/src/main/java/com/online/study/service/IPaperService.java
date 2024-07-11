package com.online.study.service;

import com.online.study.entity.Paper;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 
 */
public interface IPaperService extends IService<Paper> {

    Integer sumScoreByPaperId(Integer id);
}
