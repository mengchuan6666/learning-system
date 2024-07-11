package com.online.study.controller;

import com.online.study.common.Result;
import com.online.study.service.IStatisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statis")
public class StatisController {

    @Autowired
    private IStatisService statisService;
    @GetMapping
    public Result getStatis(){
        return Result.success(statisService.getStatis());
    }
}
