package com.online.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.online.study.entity.StudyResource;
import com.online.study.entity.dto.ResourceStatisDTO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 
 */
public interface IStudyResourceService extends IService<StudyResource> {

    Boolean saveResource(StudyResource studyResource);

    Boolean updateResource(StudyResource studyResource);

    Boolean updateResourceCheck(StudyResource studyResource);

    Boolean download(Integer studyResource, HttpServletResponse response) throws IOException;

    List<ResourceStatisDTO> getStatis(LocalDateTime stTime, LocalDateTime edTime);

    Boolean deleteByRSId(Integer id,Integer rsId,String type);
}
