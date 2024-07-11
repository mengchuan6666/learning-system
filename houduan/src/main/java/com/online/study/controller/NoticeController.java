package com.online.study.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.study.common.Result;
import com.online.study.entity.Notice;
import com.online.study.entity.User;
import com.online.study.service.INoticeService;
import com.online.study.utils.EasyPOI;
import com.online.study.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 

 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Resource
    private INoticeService noticeService;

    private final String now = DateUtil.now();

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Notice notice) {
        if (notice.getId() == null) {
            notice.setTime(DateUtil.now());
            notice.setUser(TokenUtils.getCurrentUser().getUsername());
        }
        noticeService.saveOrUpdate(notice);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        noticeService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        noticeService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(noticeService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(noticeService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }

        return Result.success(noticeService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }


    /**导出*/
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        String fileName="";
        List<Notice> list=noticeService.list();
        fileName = URLEncoder.encode("公告信息表", "UTF-8");
        EasyPOI.EasyExport(response, Notice.class, list,fileName + ".xls","公告信息");
    }

    /**
     * excel 导入
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        List<Notice> list=EasyPOI.EasyImport(file.getInputStream(), Notice.class);
        boolean b=noticeService.saveBatch(list);
        return Result.success(b);
    }




    private User getUser() {
        return TokenUtils.getCurrentUser();
    }

}

