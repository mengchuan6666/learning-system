package com.online.study.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.study.common.Result;
import com.online.study.entity.StudyResourceType;
import com.online.study.service.IStudyResourceTypeService;
import com.online.study.utils.EasyPOI;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * @method: $
 * @description: 课程类型管理
 * @date: $
 * @author: myth
 * @return $
 */
@CrossOrigin
@RestController
@RequestMapping("/resourceType")
public class StudyResourceTypeController {

    @Resource
    private IStudyResourceTypeService studyResourceTypeService;


    @PostMapping
    public Result add(@RequestBody StudyResourceType studyResourceType){

        if(null!=studyResourceType.getId()){
            studyResourceType.setUpdateTime(new Date());
        }else{
            studyResourceType.setCreateTime(new Date());
        }
        boolean b = studyResourceTypeService.saveOrUpdate(studyResourceType);
        return Result.success(b);
    }
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        studyResourceTypeService.removeById(id);
        return Result.success();
    }
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        studyResourceTypeService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        List<StudyResourceType> list = studyResourceTypeService.list();

        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(studyResourceTypeService.getById(id));
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Integer id, @RequestBody StudyResourceType studyResourceType) {
        return Result.success(studyResourceTypeService.update(studyResourceType,null));
    }
    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String typeName,
                           @RequestParam(defaultValue = "") String state,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {

        QueryWrapper<StudyResourceType> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(typeName)) {
            queryWrapper.like("type_name", typeName);
        }
        //todo 换sql关联查询
        IPage<StudyResourceType> page =  studyResourceTypeService.page(new Page<>(pageNum, pageSize), queryWrapper);
        page.getRecords().forEach(e->{
            e.setCreateTimeStr(DateUtil.formatDateTime(e.getCreateTime()));
            e.setUpdateTimeStr(DateUtil.formatDateTime(e.getUpdateTime()));

        });
        return Result.success(page);
    }

    /**导出*/
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        String fileName="";
        List<StudyResourceType> list=studyResourceTypeService.list();
        fileName = URLEncoder.encode("课程信息表", "UTF-8");
        EasyPOI.EasyExport(response, StudyResourceType.class, list,fileName + ".xls","课程信息");
    }

    /**
     * excel 导入
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        List<StudyResourceType> list=EasyPOI.EasyImport(file.getInputStream(), StudyResourceType.class);
        boolean b=studyResourceTypeService.saveBatch(list);
        return Result.success(b);
    }


}
