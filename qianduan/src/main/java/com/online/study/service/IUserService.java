package com.online.study.service;

import com.online.study.controller.dto.UserDTO;
import com.online.study.controller.dto.UserPasswordDTO;
import com.online.study.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author

 */
public interface IUserService extends IService<User> {

    UserDTO login(UserDTO userDTO);

    User register(UserDTO userDTO);

    void updatePassword(UserPasswordDTO userPasswordDTO);
}
