package com.online.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.study.entity.Files;
import com.online.study.entity.StudyResource;
import com.online.study.entity.StudyResourceType;
import com.online.study.entity.dto.ResourceStatisDTO;
import com.online.study.mapper.StudyResourceMapper;
import com.online.study.mapper.StudyResourceTypeMapper;
import com.online.study.service.IFileService;
import com.online.study.service.IStudyResourceService;
import com.online.study.service.IStudyResourceTypeService;
import com.online.study.utils.TokenUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class StudyResourceTypeServiceImpl extends ServiceImpl<StudyResourceTypeMapper, StudyResourceType> implements IStudyResourceTypeService {

}
