package com.online.study.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.study.entity.StudyOnlineTime;
import com.online.study.entity.User;
import com.online.study.mapper.StudyOnlineTimeMapper;
import com.online.study.service.IStudyOnlineTimeService;
import com.online.study.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author
 */
@Service
public class StudyOnlineTimeServiceImpl extends ServiceImpl<StudyOnlineTimeMapper, StudyOnlineTime> implements IStudyOnlineTimeService {


    @Autowired
    private StudyOnlineTimeMapper studyOnlineTimeMapper;
    @Override
    public List<StudyOnlineTime> selctForEcharts(Integer id) {
        User user = TokenUtils.getCurrentUser();
        List<StudyOnlineTime> studyOnlineTimes =null;
        //获取登录账号的角色
        String role = user.getRole();
        if(role.equals("ROLE_STUDENT")){
            studyOnlineTimes = studyOnlineTimeMapper.selectForEcharts(user.getId());
        }
        if((role.equals("ROLE_MANAGER") || role.equals("ROLE_ADMIN") || role.equals("ROLE_TEACHER")) && id!=null){
            studyOnlineTimes = studyOnlineTimeMapper.selectForEcharts(id);
        }
        return studyOnlineTimes;
    }
}
