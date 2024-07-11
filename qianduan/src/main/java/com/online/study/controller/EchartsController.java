package com.online.study.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Quarter;
import com.online.study.common.Result;
import com.online.study.entity.StudentPaper;
import com.online.study.entity.User;
import com.online.study.service.IStudentPaperService;
import com.online.study.service.IStudyOnlineTimeService;
import com.online.study.service.IUserService;
import com.online.study.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/echarts")
public class EchartsController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IStudentPaperService studentPaperService;

    @Autowired
    private IStudyOnlineTimeService studyOnlineTimeService;


    @GetMapping("/example")
    public Result get() {
        Map<String, Object> map = new HashMap<>();
        map.put("x", CollUtil.newArrayList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));
        map.put("y", CollUtil.newArrayList(150, 230, 224, 218, 135, 147, 260));
        return Result.success(map);
    }

    @GetMapping("/members")
    public Result members() {
        List<User> list = userService.list();
        int q1 = 0; // 第一季度
        int q2 = 0; // 第二季度
        int q3 = 0; // 第三季度
        int q4 = 0; // 第四季度
        for (User user : list) {
            Date createTime = user.getCreateTime();
            Quarter quarter = DateUtil.quarterEnum(createTime);
            switch (quarter) {
                case Q1: q1 += 1; break;
                case Q2: q2 += 1; break;
                case Q3: q3 += 1; break;
                case Q4: q4 += 1; break;
                default: break;
            }
        }
        return Result.success(CollUtil.newArrayList(q1, q2, q3, q4));
    }

    /***单元测试分数统计*/
    @GetMapping("/score/{id}")
    public Result score(@PathVariable int id){
        User user = TokenUtils.getCurrentUser();
        String role = user.getRole();
        Integer uid=null;
        if(role.equals("ROLE_USER")) {
            uid=id;
        }
        StudentPaper studentPaper = new StudentPaper();
        studentPaper.setUserId(uid);
        studentPaper.setTest("0");
        List<StudentPaper> score = studentPaperService.findScore(studentPaper);
        return Result.success(score);
    }

    /***作业测试分数统计*/
    @GetMapping("/homeworkScore/{id}")
    public Result homeworkScore(@PathVariable int id){
        User user = TokenUtils.getCurrentUser();
        String role = user.getRole();
        Integer uid=null;
        if(role.equals("ROLE_USER")) {
            uid=id;
        }
        StudentPaper studentPaper = new StudentPaper();
        studentPaper.setUserId(uid);
        studentPaper.setTest("1");
        List<StudentPaper> score = studentPaperService.findScore(studentPaper);
        return Result.success(score);
    }

    @GetMapping(value = {"/studyonlinetime/{id}","/studyonlinetime"})
    public Result studyonlinetime(@PathVariable(required = false) Integer id){
        return Result.success(studyOnlineTimeService.selctForEcharts(id));
    }

}
