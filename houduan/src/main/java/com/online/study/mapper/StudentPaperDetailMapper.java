package com.online.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.online.study.entity.Comment;
import com.online.study.entity.StudentPaperDetail;
import com.online.study.entity.dto.AnswerDTO;
import com.online.study.entity.vo.MarkPaperVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface StudentPaperDetailMapper extends BaseMapper<StudentPaperDetail> {
    @Select("")
    List<MarkPaperVO> findMarkPaperVOsByStudentPaperId(Integer foreignId);

    @Select("select ifNull(sum(mark_score),0) from student_Paper_Detail where student_paper_id = #{studentPaperId}")
    Integer sumScoreByStudentPaperId(Integer studentPaperId);

    @Select(" select question.*, spd.id as paperDetailId, spd.answer as studentAnswer, spd.remark as remark, spd.mark_score as markScore " +
            " ,question.answer as standardAnswer,question.score as score" +
            " ,question.detail as questionDetail,question.type as questionType,question.name as questionName " +
            " from student_Paper_Detail spd " +
            " left join question question on question.id = spd.question_id " +
            " where spd.student_paper_id = #{studentPaperId} ")
    List<AnswerDTO> selectStudentAnswerByPaperId(Integer id);
}
