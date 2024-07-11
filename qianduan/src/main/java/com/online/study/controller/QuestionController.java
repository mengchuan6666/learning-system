package com.online.study.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.study.common.Result;
import com.online.study.entity.Question;
import com.online.study.entity.User;
import com.online.study.service.IQuestionService;
import com.online.study.utils.EasyPOI;
import com.online.study.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;
import java.util.Random;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 
 
 */
@RestController
@RequestMapping("/question")
public class QuestionController {

    @Resource
    private IQuestionService questionService;

    private final String now = DateUtil.now();

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Question question) {
        if (question.getId() == null) {
            question.setTime(DateUtil.now());
            question.setPointId(new Random().nextInt(2)+1);
            question.setDifficulty(new Random().nextDouble());
            question.setUserId(TokenUtils.getCurrentUser().getId());
        }
        questionService.saveOrUpdate(question);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        questionService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        questionService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(questionService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(questionService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam(required = false) Integer ctypeId,
                           @RequestParam(required = false) Integer type,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }

        if (ctypeId != null) {
            queryWrapper.eq("ctype_id", ctypeId);
        }
        if (type != null) {
            queryWrapper.eq("type", type);
        }
//        User currentUser = TokenUtils.getCurrentUser();
//        if (currentUser.getRole().equals("ROLE_USER")) {
//            queryWrapper.eq("user", currentUser.getUsername());
//        }

        return Result.success(questionService.list(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**导出*/
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        String fileName="";
        List<Question> list=questionService.list();
        fileName = URLEncoder.encode("题目信息表", "UTF-8");
        EasyPOI.EasyExport(response, Question.class, list,fileName + ".xls","题目信息");
    }

    /**
     * excel 导入
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        List<Question> list=EasyPOI.EasyImport(file.getInputStream(), Question.class);
        boolean b=questionService.saveBatch(list);
        return Result.success(b);
    }

    private User getUser() {
        return TokenUtils.getCurrentUser();
    }

}

