package com.online.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.online.study.entity.Files;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 
 */
public interface IFileService extends IService<Files> {
    Files saveFile(MultipartFile file) throws IOException;

    Files getFileByMd5(String md5);

    void downloadFile(String fileUUID, HttpServletResponse response) throws IOException;


}
