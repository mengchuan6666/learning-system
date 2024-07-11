package com.online.study.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.study.entity.Files;
import com.online.study.mapper.FileMapper;
import com.online.study.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, Files> implements IFileService {
    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Autowired
    private FileMapper fileMapper;

    @Override
    public Files saveFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);
        long size = file.getSize();


        // 定义一个文件唯一的标识码
        String fileUUID = IdUtil.fastSimpleUUID() + StrUtil.DOT + type;
        String upPath = fileUploadPath;
        if (!upPath.endsWith(String.valueOf(File.separatorChar))){
            upPath += File.separatorChar;
        }
        File uploadFile = new File(upPath + fileUUID);
        // 判断配置的文件目录是否存在，若不存在则创建一个新的文件目录
        File parentFile = uploadFile.getParentFile();
        if(!parentFile.exists()) {
            parentFile.mkdirs();
        }

        String url;
        // 获取文件的md5
        String md5 = SecureUtil.md5(file.getInputStream());
        // 从数据库查询是否存在相同的记录
        Files dbFiles = this.getFileByMd5(md5);
        if (dbFiles != null) {
            url = dbFiles.getUrl();
        } else {
            // 上传文件到磁盘
            file.transferTo(uploadFile);
            // 数据库若不存在重复文件，则不删除刚才上传的文件 todo 更改为可配置项
            url = "http://localhost:9090/file/" + fileUUID;
        }

        // 存储数据库
        Files saveFile = new Files();
        saveFile.setName(originalFilename);
        saveFile.setType(type);
        saveFile.setSize(size/1024); // 单位 kb
        saveFile.setUrl(url);
        saveFile.setMd5(md5);
        this.getBaseMapper().insert(saveFile);
        return saveFile;
    }

    @Override
    public Files getFileByMd5(String md5) {
        // 查询文件的md5是否存在
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5", md5);
        List<Files> filesList = this.getBaseMapper().selectList(queryWrapper);
        return filesList.size() == 0 ? null : filesList.get(0);
    }

    @Override
    public void downloadFile(String fileUUID, HttpServletResponse response) throws IOException {
        String downPath = fileUploadPath;
        if (!downPath.endsWith(String.valueOf(File.separatorChar))){
            downPath += File.separatorChar;
        }

        File uploadFile = new File(downPath + fileUUID);
        // 设置输出流的格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileUUID, "UTF-8"));
        response.setContentType("application/octet-stream");
        // 读取文件的字节流
        try {
            os.write(FileUtil.readBytes(uploadFile));
        } catch (Exception e) {
            System.err.println("文件下载失败，文件不存在");
        }
        os.flush();
        os.close();
    }
}
