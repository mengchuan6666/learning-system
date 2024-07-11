package com.online.study.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.online.study.entity.Paper;
import com.online.study.mapper.PaperMapper;
import com.online.study.service.IPaperService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 
 */
@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements IPaperService {

    @Override
    public Integer sumScoreByPaperId(Integer id) {
        return ObjectUtil.defaultIfNull(this.baseMapper.sumScoreByPaperId(id),0);
    }


}
