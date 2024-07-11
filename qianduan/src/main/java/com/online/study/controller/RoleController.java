package com.online.study.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.study.common.Result;
import com.online.study.entity.Role;
import com.online.study.service.IRoleService;
import com.online.study.utils.EasyPOI;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author

 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    private IRoleService roleService;

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Role role) {
        roleService.saveOrUpdate(role);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        roleService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        roleService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(roleService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(roleService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", name);
        queryWrapper.orderByDesc("id");
        return Result.success(roleService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 绑定角色和菜单的关系
     * @param roleId 角色id
     * @param menuIds 菜单id数组
     * @return
     */
    @PostMapping("/roleMenu/{roleId}")
    public Result roleMenu(@PathVariable Integer roleId, @RequestBody List<Integer> menuIds) {
        roleService.setRoleMenu(roleId, menuIds);
        return Result.success();
    }

    @GetMapping("/roleMenu/{roleId}")
    public Result getRoleMenu(@PathVariable Integer roleId) {
        return Result.success( roleService.getRoleMenu(roleId));
    }
    /**导出*/
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        String fileName="";
        List<Role> list=roleService.list();
        fileName = URLEncoder.encode("角色信息表", "UTF-8");
        EasyPOI.EasyExport(response, Role.class, list,fileName + ".xls","角色信息");
    }
}

