package com.online.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.online.study.entity.Comment;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {
    @Select("select * from comment where foreign_id = #{foreignId} order by createtime desc")
    List<Comment> findAllByForeignId(Integer foreignId);

    @Select("select * from comment where user_id = #{userId} order by createtime desc")
    List<Comment> findAllByUserId(Integer userId);

    @Select({
            "<script>",
            "SELECT * FROM comment c where c.pid in",
            "<foreach collection='pids' item='pid' open='(' separator=',' close=')'>",
            "#{pid}",
            "</foreach>",
            " order by createtime desc",
            "</script>"
    })
    List<Comment> findAllByPids(List<Integer> pids);
}
