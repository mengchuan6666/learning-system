package com.online.study.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.online.study.common.Result;
import com.online.study.entity.Exam;
import com.online.study.entity.User;
import com.online.study.service.IExamService;
import com.online.study.service.ISignService;
import com.online.study.utils.EasyPOI;
import com.online.study.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 
 */
@RestController
@RequestMapping("/exam")
public class ExamController {

    @Resource
    private IExamService examService;

    @Resource
    private ISignService signService;

    private final String now = DateUtil.now();

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Exam exam) {
        return Result.success(examService.saveExam(exam));
    }

    @PutMapping("/{id}/finish")
    public Result finishExam(@PathVariable Integer id) {
        return examService.finishExamById(id);
    }

    @PatchMapping("/{id}")
    public Result enableSwitch(@PathVariable Integer id) {
        return examService.enableSwitchById(id);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return examService.deleteExamByIds(Arrays.asList(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return examService.deleteExamByIds(ids);
    }

    @GetMapping
    public Result findAll() {
        return Result.success(examService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(examService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        return Result.success(examService.getAdminPage(name, pageNum, pageSize));
    }

    @GetMapping("/page/front")
    public Result findPageFront(@RequestParam(defaultValue = "") String examName,
                           @RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<Exam> page = examService.getExamFrontPage(examName, pageNum, pageSize);
        return Result.success(page);
    }
    @GetMapping("/page/front/test")
    public Result findPageFrontTest(@RequestParam Integer id,
                                    @RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<Exam> page = examService.getExamFrontPageTest(id, pageNum, pageSize);
        return Result.success(page);
    }


    /**导出*/
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        String fileName="";
        List<Exam> list=examService.list();
        fileName = URLEncoder.encode("测试管理信息表", "UTF-8");
        EasyPOI.EasyExport(response, Exam.class, list,fileName + ".xls","测试管理信息");
    }

    /**
     * excel 导入
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        List<Exam> list=EasyPOI.EasyImport(file.getInputStream(), Exam.class);
        boolean b=examService.saveBatch(list);
        return Result.success(b);
    }

    private User getUser() {
        return TokenUtils.getCurrentUser();
    }

    @GetMapping("/getTestExamPaper")
    public Result getTestExamPaper(@RequestParam Integer ctypeId){
        List<Exam> list = examService.getTestExamPaper(ctypeId);
        return Result.success(list);
    }

}

