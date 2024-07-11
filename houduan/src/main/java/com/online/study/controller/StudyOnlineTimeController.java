package com.online.study.controller;


import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.online.study.common.Result;
import com.online.study.entity.StudyOnlineTime;
import com.online.study.entity.User;
import com.online.study.service.IStudyOnlineTimeService;
import com.online.study.utils.TokenUtils;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @method: $
 * @description: 描述一下方法的作用
 * @date: $
 * @author: myth
 * @return $
 */
@Controller
@RequestMapping("/studyOnlineTime")
public class StudyOnlineTimeController {

    @Autowired
    private IStudyOnlineTimeService studyOnlineTimeService;

    /**
     * 新增在线学习时间
     * @param studyOnlineTime
     * @return
     */
    @PostMapping("/add")
    public Result addStudyOnlineTime(@RequestBody StudyOnlineTime studyOnlineTime){
        User user = TokenUtils.getCurrentUser();
        studyOnlineTime.setUid(user.getId());
        studyOnlineTime.setStudayDate(DateUtil.today());
        boolean b = studyOnlineTimeService.save(studyOnlineTime);
        return Result.success(b);
    }

}
