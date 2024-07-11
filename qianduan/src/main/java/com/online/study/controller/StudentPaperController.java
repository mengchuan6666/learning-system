package com.online.study.controller;

import cn.hutool.core.date.DateUtil;
import com.online.study.common.Result;
import com.online.study.entity.StudentPaper;
import com.online.study.entity.User;
import com.online.study.entity.dto.MarkPaperDTO;
import com.online.study.service.IStudentPaperService;
import com.online.study.utils.EasyPOI;
import com.online.study.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author
 */
@RestController
@RequestMapping("/studentpaper")
public class StudentPaperController {

    @Resource
    private IStudentPaperService studentPaperService;

    private final String now = DateUtil.now();

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody StudentPaper studentPaper) {
        return Result.success(studentPaperService.saveStudentPaper(studentPaper));
    }

    @PutMapping("/{id}/mark")
    public Result mark(@RequestBody List<MarkPaperDTO> markDTO) {
        studentPaperService.mark(markDTO);
        return Result.success();
    }

    @GetMapping("/grade/{examId}")
    public Result getGrade(@PathVariable Integer examId) {
        return Result.success(studentPaperService.getGradeByExamId(examId));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        studentPaperService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        studentPaperService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(studentPaperService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(studentPaperService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam(defaultValue = "",required = false) String test,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        return studentPaperService.getStudentPaperForAdmin(name,test, pageNum, pageSize);
    }

    /***获取统计的分数*/
    @GetMapping("/getScore")
    public Result getScore(@RequestParam(defaultValue = "") String test){
        return Result.success(studentPaperService.getScore(test));
    }

    /**
     * 导出接口
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response,@RequestParam(defaultValue = "") String test) throws Exception {
        // 从数据库查询出所有的数据
        String fileName="";
        List<StudentPaper> list=studentPaperService.getExcelData(test);
        if(test.equals("1")){
            fileName = URLEncoder.encode("学生作业成绩信息表", "UTF-8");
        }else{
            fileName = URLEncoder.encode("学生测试成绩信息表", "UTF-8");
        }
        EasyPOI.EasyExport(response, StudentPaper.class, list,fileName + ".xls","成绩信息");
    }

    /**
     * excel 导入
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        List<StudentPaper> list=EasyPOI.EasyImport(file.getInputStream(), StudentPaper.class);
        boolean b=studentPaperService.saveBatch(list);
        return Result.success(b);
    }

    private User getUser() {
        return TokenUtils.getCurrentUser();
    }

}

