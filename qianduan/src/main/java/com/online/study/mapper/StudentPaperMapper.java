package com.online.study.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.online.study.entity.StudentPaper;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 

 */
public interface StudentPaperMapper extends BaseMapper<StudentPaper> {


    IPage<StudentPaper>selectStudentPaper(IPage<T> page, @Param(Constants.WRAPPER) Wrapper<StudentPaper> queryWarpper,@Param("studentPaper") StudentPaper studentPaper);

    List<StudentPaper> getScore(@Param("test") String test);

    List<StudentPaper> getExcelData(@Param("test") String test);

    List<StudentPaper> findscore(StudentPaper studentPaper);

    List<StudentPaper> findscore2(StudentPaper studentPaper);
}
