package com.online.study.service.impl;

import com.online.study.common.RoleEnum;
import com.online.study.entity.Exam;
import com.online.study.entity.News;
import com.online.study.entity.User;
import com.online.study.entity.vo.StatisVO;
import com.online.study.service.IExamService;
import com.online.study.service.INewsService;
import com.online.study.service.IStatisService;
import com.online.study.service.IStudyResourceService;
import com.online.study.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StatisServiceImpl implements IStatisService {
    @Autowired
    private IUserService userService;
    @Autowired
    private INewsService newsService;
    @Autowired
    private IExamService examService;
    @Autowired
    private IStudyResourceService resourceService;


    @Override
    public StatisVO getStatis() {
        return StatisVO
                .builder()
//                .userCount(userService.lambdaQuery().eq(User::getRole, RoleEnum.ROLE_USER.name()).count())
                .userCount(userService.lambdaQuery().count())
                .teacherCount(userService.lambdaQuery().eq(User::getRole, RoleEnum.ROLE_TEACHER.name()).count())
                .newsCountMonthly(newsService
                        .lambdaQuery()
                        .between(News::getTime
                                , LocalDateTime
                                        .now()
                                        .withDayOfMonth(1)
                                        .withHour(0).withMinute(0).withSecond(0).withNano(0)
                                , LocalDateTime
                                        .now()
                                        .plusMonths(1)
                                        .withDayOfMonth(1)
                                        .plusDays(-1)
                                        .withHour(23).withMinute(59).withSecond(59).withNano(9999)).count())
                .examCountMonthly(examService
                        .lambdaQuery()
                        .between(Exam::getCreateTime
                                , LocalDateTime
                                        .now()
                                        .withDayOfMonth(1)
                                        .withHour(0).withMinute(0).withSecond(0).withNano(0)
                                , LocalDateTime
                                        .now()
                                        .plusMonths(1)
                                        .withDayOfMonth(1)
                                        .plusDays(-1)
                                        .withHour(23).withMinute(59).withSecond(59).withNano(9999)).count())
                .resStatisMonthly(resourceService.getStatis(LocalDateTime
                                .now()
                                .withDayOfMonth(1)
                                .withHour(0).withMinute(0).withSecond(0).withNano(0)
                        , LocalDateTime
                                .now()
                                .plusMonths(1)
                                .withDayOfMonth(1)
                                .plusDays(-1)
                                .withHour(23).withMinute(59).withSecond(59).withNano(9999)))
                .examStatisMonthly(examService.getStatis(LocalDateTime
                                .now()
                                .withDayOfMonth(1)
                                .withHour(0).withMinute(0).withSecond(0).withNano(0)
                        , LocalDateTime
                                .now()
                                .plusMonths(1)
                                .withDayOfMonth(1)
                                .plusDays(-1)
                                .withHour(23).withMinute(59).withSecond(59).withNano(9999)))
                .build();
    }
}
