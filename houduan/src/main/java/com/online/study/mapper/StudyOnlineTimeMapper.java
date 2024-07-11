package com.online.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.online.study.entity.StudyOnlineTime;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author

 */
public interface StudyOnlineTimeMapper extends BaseMapper<StudyOnlineTime> {


   List<StudyOnlineTime> selectForEcharts(Integer id);
}
