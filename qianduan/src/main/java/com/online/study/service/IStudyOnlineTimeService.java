package com.online.study.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.online.study.entity.StudyOnlineTime;


import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author

 */
public interface IStudyOnlineTimeService extends IService<StudyOnlineTime> {

  List<StudyOnlineTime> selctForEcharts(Integer uid);
}
