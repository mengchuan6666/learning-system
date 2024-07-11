package com.online.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.study.entity.Files;
import com.online.study.entity.StudyResource;
import com.online.study.entity.dto.ResourceStatisDTO;
import com.online.study.mapper.StudyResourceMapper;
import com.online.study.service.IFileService;
import com.online.study.service.IStudyResourceService;
import com.online.study.utils.TokenUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class StudyResourceServiceImpl extends ServiceImpl<StudyResourceMapper, StudyResource> implements IStudyResourceService {
    @Autowired
    private IFileService fileService;

    @Override
    public Boolean saveResource(StudyResource studyResource) {
        // todo 事务回滚
        studyResource.setCreateUser(TokenUtils.getCurrentUser().getId());
        studyResource.setUpdateUser(TokenUtils.getCurrentUser().getId());
        studyResource.setCreateTime(new Date());
        studyResource.setUpdateTime(new Date());
        //查找文件
        QueryWrapper<Files> fileQuery = new QueryWrapper<>();
        fileQuery.lambda()
                .eq(Files::getUrl, studyResource.getFileUrl())
                .orderBy(true, false,Files::getId)
                .last(" limit 1 ");
        //查找视频
        QueryWrapper<Files> fileQuery1 = new QueryWrapper<>();
        fileQuery1.lambda()
                .eq(Files::getUrl, studyResource.getMp4Url())
                .orderBy(true, false,Files::getId)
                .last(" limit 1 ");
        // 使用Jsoup库进行输入过滤和验证
        String sanitizedInput = Jsoup.clean(studyResource.getIntroduction(), Whitelist.basic());
        String title = Jsoup.clean(studyResource.getResourceName(), Whitelist.basic());
        // 输出经过转义和过滤的富文本内容
        System.out.println("Sanitized Input: " + sanitizedInput);
        studyResource.setResourceName(title);
        studyResource.setIntroduction(sanitizedInput);
        Files sysFile = fileService.getOne(fileQuery);
        Files sysFile1 = fileService.getOne(fileQuery1);
        if(null!=sysFile) {
            studyResource.setSysFileId(sysFile.getId());
        }else{
            studyResource.setSysFileId(0);
        }
        //添加视频ID
        if(null!=sysFile1) {
            studyResource.setSysMp4Id(sysFile1.getId());
        }else{
            studyResource.setSysMp4Id(0);
        }
        return save(studyResource);
    }

    @Override
    public Boolean updateResource(StudyResource studyResource) {
        // todo 事务回滚
        studyResource.setUpdateUser(TokenUtils.getCurrentUser().getId());
        studyResource.setUpdateTime(new Date());
        //查询文件
        QueryWrapper<Files> fileQuery = new QueryWrapper<>();
        fileQuery.lambda()
                .eq(Files::getUrl, studyResource.getFileUrl())
                .orderBy(true, false,Files::getId)
                .last(" limit 1 ");
        Files sysFile = fileService.getOne(fileQuery);
        //查找视频
        QueryWrapper<Files> fileQuery1 = new QueryWrapper<>();
        fileQuery1.lambda()
                .eq(Files::getUrl, studyResource.getMp4Url())
                .orderBy(true, false,Files::getId)
                .last(" limit 1 ");
        Files sysFile1 = fileService.getOne(fileQuery1);

        // 使用Jsoup库进行输入过滤和验证
        String sanitizedInput = Jsoup.clean(studyResource.getIntroduction(), Whitelist.basic());
        String title = Jsoup.clean(studyResource.getResourceName(), Whitelist.basic());
        // 输出经过转义和过滤的富文本内容
        studyResource.setResourceName(title);
        studyResource.setIntroduction(sanitizedInput);
        //文件
        if(null!=sysFile) {
            studyResource.setSysFileId(sysFile.getId());
        }else{
            studyResource.setSysFileId(0);
        }
        //视频
        if(null!=sysFile1) {
            studyResource.setSysMp4Id(sysFile1.getId());
        }else{
            studyResource.setSysMp4Id(0);
        }
        return updateById(studyResource);
    }

    @Override
    public Boolean updateResourceCheck(StudyResource studyResource) {
        studyResource.setUpdateUser(TokenUtils.getCurrentUser().getId());
        studyResource.setUpdateTime(new Date());
        return updateById(studyResource);
    }

    @Override
    public Boolean download(Integer studyResourceId, HttpServletResponse response) throws IOException {
        StudyResource studyResource = this.getById(studyResourceId);
        Files sysFile = fileService.getById(studyResource.getSysFileId());
        String fileUUID = Arrays.stream(sysFile.getUrl().split("/")).reduce((a,b) -> b).orElse("invalid download url");
        fileService.downloadFile(fileUUID, response);
        return true;
    }

    @Override
    public List<ResourceStatisDTO> getStatis(LocalDateTime stTime, LocalDateTime edTime) {
        return this.baseMapper.selectStatisDTO(stTime,edTime);
    }

    @Transactional
    @Override
    public Boolean deleteByRSId(Integer id,Integer rsId,String type) {
        boolean b=false;
        StudyResource sr=new StudyResource();
        sr.setId(rsId);
        if(type.equals("mp4")) {
            sr.setSysMp4Id(0);
        }else{
            sr.setSysFileId(0);
        }
        int i = this.baseMapper.updateById(sr);
        if(i>0){
             b = fileService.removeById(id);
        }
        return b;
    }
}
