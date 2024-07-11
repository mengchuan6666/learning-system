package com.online.study.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.study.entity.Exam;
import com.online.study.entity.dto.ExamStatisDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.poi.ss.formula.functions.T;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author
 */
public interface ExamMapper extends BaseMapper<Exam> {
    @Select("SELECT ifNull(sum(q.score),0) FROM paper_question pq left join question q on pq.question_id = q.id left join `exam_paper` ep on pq.paper_id = ep.paper_id where ep.exam_id = #{examId} limit 1")
    Integer sumScoreByExamId(Integer examId);
//any_value(e.name)
    @Select("select e.name as examName,VARIANCE(a.totalScore) as varianceScore, max(a.totalScore) as maxScore ,avg(a.totalScore) as avgScore from \n" +
            "             (select sp.user_id , sp.exam_id as exam_id, sum(ifNull(spd.mark_score,0)) as totalScore \n" +
            "             from student_paper sp \n" +
            "             left join student_paper_detail spd on spd.student_paper_id = sp.id \n" +
            "             where sp.state = '01' \n" +
            "             group by sp.id ) as a \n" +
            "             inner join exam e on e.id = a.exam_id \n" +
            "             where e.end_time between #{stTime} and #{edTime} " +
            "             group by a.exam_id ")
    List<ExamStatisDTO> selectStatisDTO(LocalDateTime stTime, LocalDateTime edTime);

    /**在线考试*/
    @Select("SELECT em.id,em.`name`,em.exam_desc,em.start_time,em.end_time,em.pass_score,pa.duration,em.teacher,em.state FROM exam em\n" +
            "            LEFT JOIN exam_paper ep on em.id=ep.exam_id\n" +
            "            LEFT JOIN paper pa on pa.id=ep.paper_id\n" +
            "            where pa.test=0 and ${ew.sqlSegment} ")
    IPage<Exam> getExamFront(IPage<T> page, @Param(Constants.WRAPPER) Wrapper<Exam> queryWarpper);


    /**在线作业*/
    @Select("SELECT em.id,em.`name`\n" +
            "FROM exam em\n" +
            "LEFT JOIN exam_paper ep on em.id=ep.exam_id\n" +
            "LEFT JOIN paper pa on pa.id=ep.paper_id\n" +
            "where pa.test=1 and pa.ctype_id=#{ctypeId}")
    List<Exam> getTestExamPaper(Integer ctypeId);

    /**在线作业*/
    @Select("SELECT em.id,em.`name`\n" +
            "FROM exam em\n" +
            "LEFT JOIN exam_paper ep on em.id=ep.exam_id\n" +
            "LEFT JOIN paper pa on pa.id=ep.paper_id\n" +
            "where pa.test=1 and pa.ctype_id=#{ctypeId}")
    IPage<Exam> getHomeWorkTest(Page<Object> objectPage, QueryWrapper<Exam> queryWrapper,@Param("ctypeId")Integer ctypeId);
}
