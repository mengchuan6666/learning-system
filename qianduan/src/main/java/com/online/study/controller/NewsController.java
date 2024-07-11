package com.online.study.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.study.common.Result;
import com.online.study.entity.News;
import com.online.study.entity.User;
import com.online.study.service.INewsService;
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
@RequestMapping("/news")
public class NewsController {

    @Resource
    private INewsService newsService;

    private final String now = DateUtil.now();

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody News news) {
        if (news.getId() == null) {
            news.setTime(DateUtil.now());
            news.setUser(TokenUtils.getCurrentUser().getUsername());
            newsService.save(news);
        }
        newsService.updateById(news);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        newsService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        newsService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(newsService.list());
    }

    @GetMapping("/findOne")
    public Result findOne(@RequestParam Integer id) {
        return Result.success(newsService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<News> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
//        User currentUser = TokenUtils.getCurrentUser();
//        if (currentUser.getRole().equals("ROLE_USER")) {
//            queryWrapper.eq("user", currentUser.getUsername());
//        }
        return Result.success(newsService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**导出*/
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        String fileName="";
        List<News> list=newsService.list();
        fileName = URLEncoder.encode("新闻信息表", "UTF-8");
        EasyPOI.EasyExport(response, News.class, list,fileName + ".xls","新闻信息");
    }

    /**
     * excel 导入
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        List<News> list=EasyPOI.EasyImport(file.getInputStream(), News.class);
        boolean b=newsService.saveBatch(list);
        return Result.success(b);
    }

    private User getUser() {
        return TokenUtils.getCurrentUser();
    }

}

