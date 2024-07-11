package com.online.study.mapper;

import com.online.study.controller.dto.UserPasswordDTO;
import com.online.study.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author

 */
public interface UserMapper extends BaseMapper<User> {

    @Update("update sys_user set password = #{newPassword} where username = #{username} and password = #{password}")
    int updatePassword(UserPasswordDTO userPasswordDTO);
    @Update("update sys_user set password = #{newPassword} where username = #{username}")
    int updatePasswordForAdmin(UserPasswordDTO userPasswordDTO);
}
