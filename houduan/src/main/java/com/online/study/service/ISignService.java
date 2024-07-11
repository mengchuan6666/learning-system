package com.online.study.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.study.common.Result;
import com.online.study.entity.Sign;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 

 */
public interface ISignService extends IService<Sign> {

     Page<Sign> getSignPage(String examName, Integer pageNum, Integer pageSize) ;

     Result deleteByIds(List<Integer> ids);

     Result saveSign(Sign sign);

     Result auditSign(Integer id, String auditType);
}

