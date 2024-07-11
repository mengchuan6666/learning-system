package com.online.study.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.study.entity.News;
import com.online.study.mapper.NewsMapper;
import com.online.study.service.INewsService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 

 */
@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements INewsService {

}
