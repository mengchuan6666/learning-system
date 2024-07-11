package com.online.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.online.study.entity.StudyResource;
import com.online.study.entity.dto.ResourceStatisDTO;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

public interface StudyResourceMapper extends BaseMapper<StudyResource> {

    @Select("select t.nickname as uploaderName,ifNull(count(r.id),0) as uploadCount " +
            " from study_resource r inner join sys_user t on t.id = r.create_user " +
            " where r.create_time between #{stTime} and #{edTime}" +
            " group by t.id ")
    List<ResourceStatisDTO> selectStatisDTO(LocalDateTime stTime, LocalDateTime edTime);
}
