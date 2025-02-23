package com.online.study.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.study.common.Result;
import com.online.study.common.state.ExamSignState;
import com.online.study.entity.Sign;
import com.online.study.entity.User;
import com.online.study.exception.ServiceException;
import com.online.study.service.ISignService;
import com.online.study.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author

 */
@RestController
@RequestMapping("/sign")
public class SignController {
    @Resource
    private ISignService signService;

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Sign sign) {
        try {
            return this.signService.saveSign(sign);
        } catch (Exception e) {
            throw new ServiceException("-1", "您已报过名了");
        }
    }

    @PatchMapping("/{id}")
    public Result audit(@PathVariable Integer id, @RequestParam String auditType) {
        try {
            return this.signService.auditSign(id, auditType);
        } catch (Exception e) {
            throw new ServiceException("-1", "审核失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(signService.deleteByIds(Collections.singletonList(id)));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(signService.deleteByIds(ids));
    }

    @GetMapping
    public Result findAll() {
        return Result.success(signService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(signService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String examName,
                           @RequestParam(defaultValue = "") String studentName,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        Page<Sign> page = signService.getSignPage(examName, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 导出接口
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        List<Sign> list = signService.list();
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("Sign信息表", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();

    }

    /**
     * excel 导入
     *
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // 通过 javabean的方式读取Excel内的对象，但是要求表头必须是英文，跟javabean的属性要对应起来
        List<Sign> list = reader.readAll(Sign.class);
        signService.saveBatch(list);
        return Result.success();
    }

    private User getUser() {
        return TokenUtils.getCurrentUser();
    }

}

