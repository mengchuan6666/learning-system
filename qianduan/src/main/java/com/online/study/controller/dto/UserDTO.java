package com.online.study.controller.dto;

import com.online.study.entity.Menu;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 接受前端登录请求的参数
 */
@Data
public class UserDTO {
    private Integer id;
    private String username;
    private String password;
    private String nickname;
    private String avatarUrl;
    private String token;
    private String role;
    private String email;
    private String phone;
    private List<Menu> menus;
}
