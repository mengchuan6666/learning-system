package com.online.study.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.study.common.Result;
import com.online.study.entity.StudyResource;
import com.online.study.entity.User;
import com.online.study.service.IFileService;
import com.online.study.service.IStudyResourceService;
import com.online.study.service.IUserService;
import com.online.study.utils.EasyPOI;
import com.online.study.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/resource")
public class  StudyResourceController {

    @Resource
    private IStudyResourceService studyResourceService;
    @Resource
    private IUserService userService;
    @Autowired
    private IFileService fileService;

    @GetMapping("/{id}/file")
    public Result download(@PathVariable Integer resourceId, HttpServletResponse response) throws IOException {
        return Result.getResult(studyResourceService.download(resourceId,response));
    }

    @PostMapping
    public Result save(@RequestBody StudyResource studyResource) {
        Boolean bool=false;
        if(null==studyResource.getId()){
            bool = studyResourceService.saveResource(studyResource);
        }else{
            bool =studyResourceService.updateResource(studyResource);
        }
        return Result.getResult(bool);
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        studyResourceService.removeByIds(ids);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        studyResourceService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/deleteByRSId/{id}/{rsId}/{type}")
    public Result deleteByRSId(@PathVariable Integer id,@PathVariable Integer rsId,@PathVariable String type) {
        studyResourceService.deleteByRSId(id,rsId,type);
        return Result.success();
    }

    @PutMapping("/check/{id}")
    public Result check(@PathVariable Integer id) {
        StudyResource resource = studyResourceService.getById(id);
        resource.setStatus(1);
        studyResourceService.updateResourceCheck(resource);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(studyResourceService.getById(id));
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Integer id, @RequestBody StudyResource studyResource) {
        return Result.success(studyResourceService.updateResource(studyResource));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String resourceName,
                           @RequestParam(defaultValue = "") String state,

                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {

        QueryWrapper<StudyResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(resourceName)) {
            queryWrapper.like("resource_name", resourceName);
        }
        if(!"".equals(state)){
            queryWrapper.eq("status",state);
        }
        //todo 换sql关联查询
        IPage<StudyResource> page =  studyResourceService.page(new Page<>(pageNum, pageSize), queryWrapper);
        page.getRecords().forEach(e->{
            e.setCreateTimeStr(DateUtil.formatDateTime(e.getCreateTime()));
            e.setUserName(userService.getById(e.getCreateUser()).getUsername());

            if(null!=e.getSysFileId() && e.getSysFileId()!=0){
                e.setFileUrl(fileService.getById(e.getSysFileId()).getUrl());
            }
            if(null!=e.getSysMp4Id() && e.getSysMp4Id()!=0) {
                System.out.println("e.getSysMp4Id()======="+e.getSysMp4Id());
                e.setMp4Url(fileService.getById(e.getSysMp4Id()).getUrl());
            }else{
                e.setMp4Url("");
            }
        });
        return Result.success(page);
    }
    @GetMapping("/front/page")
    public Result frontPage(@RequestParam(defaultValue = "") String resourceName,
                           @RequestParam(defaultValue = "") String state,
                           @RequestParam Integer type,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        User user = TokenUtils.getCurrentUser();
        //String role = user.getRole();
        QueryWrapper<StudyResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(resourceName)) {
            queryWrapper.like("resource_name", resourceName);
        }
        //if((null!=type && type!=0) && role.equals("ROLE_USER")){
            queryWrapper.eq("type",type);
       // }
        if(!"".equals(state)){
            queryWrapper.eq("status",state);
        }
        //todo 换sql关联查询
        IPage<StudyResource> page =  studyResourceService.page(new Page<>(pageNum, pageSize), queryWrapper);
        page.getRecords().forEach(e->{
            e.setCreateTimeStr(DateUtil.formatDateTime(e.getCreateTime()));
            e.setUserName(userService.getById(e.getCreateUser()).getUsername());

            if(null!=e.getSysFileId() && e.getSysFileId()!=0){
                e.setFileUrl(fileService.getById(e.getSysFileId()).getUrl());
            }
            if(null!=e.getSysMp4Id() && e.getSysMp4Id()!=0) {
                e.setMp4Url(fileService.getById(e.getSysMp4Id()).getUrl());
            }else{
                e.setMp4Url("");
            }
        });
        return Result.success(page);
    }
    /**导出*/
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        String fileName="";
        List<StudyResource> list=studyResourceService.list();
        fileName = URLEncoder.encode("课程管理信息表", "UTF-8");
        EasyPOI.EasyExport(response, StudyResource.class, list,fileName + ".xls","课程管理信息");
    }

    /**
     * excel 导入
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        List<StudyResource> list=EasyPOI.EasyImport(file.getInputStream(), StudyResource.class);
        boolean b=studyResourceService.saveBatch(list);
        return Result.success(b);
    }

}
