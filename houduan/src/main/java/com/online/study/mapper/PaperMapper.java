package com.online.study.mapper;

import com.online.study.entity.Paper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 
 */
public interface PaperMapper extends BaseMapper<Paper> {

    @Select("SELECT ifNull(sum(q.score),0) FROM paper_question pq left join question q on pq.question_id = q.id where pq.paper_id = #{paperId} limit 1")
    Integer sumScoreByPaperId(Integer paperId);

}
