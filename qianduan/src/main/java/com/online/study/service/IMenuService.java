package com.online.study.service;

import com.online.study.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author

 */
public interface IMenuService extends IService<Menu> {

    List<Menu> findMenus(String name);
}
