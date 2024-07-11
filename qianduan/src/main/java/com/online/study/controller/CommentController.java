package com.online.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.study.common.Result;
import com.online.study.entity.Comment;
import com.online.study.mapper.CommentMapper;
import com.online.study.service.ICommentService;
import com.online.study.utils.EasyPOI;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private ICommentService commentService;


    @PostMapping
    public Result save(@RequestBody Comment comment) {
        comment.setCreatetime(LocalDateTime.now());
        return Result.success(commentMapper.insert(comment));
    }

    @GetMapping
    public Result list(@RequestParam Integer foreignId) {
        return commentService.getCommentByUser(foreignId);
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        commentService.removeByIds(ids);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        commentService.removeById(id);
        return Result.success();
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("username", name);
        }

        return Result.success(commentService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**导出*/
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        String fileName="";
        List<Comment> list=commentService.list();
        fileName = URLEncoder.encode("留言信息表", "UTF-8");
        EasyPOI.EasyExport(response, Comment.class, list,fileName + ".xls","留言信息");
    }

    /**
     * excel 导入
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        List<Comment> list=EasyPOI.EasyImport(file.getInputStream(), Comment.class);
        boolean b=commentService.saveBatch(list);
        return Result.success(b);
    }

}
