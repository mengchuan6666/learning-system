package com.online.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.study.common.Result;
import com.online.study.common.state.ExamSignState;
import com.online.study.entity.Exam;
import com.online.study.entity.Sign;
import com.online.study.entity.User;
import com.online.study.exception.ServiceException;
import com.online.study.mapper.SignMapper;
import com.online.study.service.IExamService;
import com.online.study.service.ISignService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.study.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author

 */
@Service
public class SignServiceImpl extends ServiceImpl<SignMapper, Sign> implements ISignService {
    @Autowired
    private IExamService examService;

    @Override
    public Page<Sign> getSignPage(String examName, Integer pageNum, Integer pageSize) {
        //todo 联表查询
        QueryWrapper<Sign> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(examName)) {
            queryWrapper.like("name", examName);
        }

        Page<Sign> page = this.page(new Page<>(pageNum, pageSize), queryWrapper);
        page.getRecords().forEach(e->{
            e.setStateDesc(ExamSignState.getDescByState(e.getState()));
        });

        return page;
    }

    @Override
    public Result deleteByIds(List<Integer> ids) {
        LambdaUpdateChainWrapper<Sign> updateWrapper = lambdaUpdate()
                .in(Sign::getId, ids)
                .set(Sign::getState, ExamSignState.DELETED.getState());
        return Result.getResult(updateWrapper.update());
    }

    @Override
    public Result saveSign(Sign sign) {
        User curUser = TokenUtils.getCurrentUser();
        if (curUser == null) {
            throw new ServiceException("-1", "请登录");
        }
        Sign signInDB = this.lambdaQuery()
                .eq(Sign::getExamId, sign.getExamId())
                .eq(Sign::getUserId, curUser.getId())
                .eq(Sign::getState,ExamSignState.AUDITED_REFUSED.getState()).one();
        if(signInDB != null) {
            sign = signInDB;
        }
        sign.setUserId(curUser.getId());
        sign.setState(ExamSignState.WAIT_AUDIT.getState());
        return Result.success(this.saveOrUpdate(sign));
    }

    @Override
    public Result auditSign(Integer id, String auditType) {
        Sign signForAudit = this.getById(id);
        if (signForAudit == null || !ExamSignState.WAIT_AUDIT.getState().equals(signForAudit.getState())) {
            throw new ServiceException("-1", "当前记录不可审核");
        } else if (!ExamSignState.AUDITED_PASSED.getState().equals(auditType)
                && !ExamSignState.AUDITED_REFUSED.getState().equals(auditType)) {
            throw new ServiceException("-1", "审核失败");
        }

        return Result.getResult(
                this
                .lambdaUpdate()
                .eq(Sign::getId, id)
                .set(Sign::getState, auditType)
                .update());
    }
}
