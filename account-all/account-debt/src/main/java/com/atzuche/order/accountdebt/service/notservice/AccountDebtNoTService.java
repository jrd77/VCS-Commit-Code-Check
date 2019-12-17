package com.atzuche.order.accountdebt.service.notservice;

import com.atzuche.order.accountdebt.exception.AccountDeductDebtDBException;
import com.atzuche.order.accountdebt.exception.AccountInsertDebtDBException;
import com.atzuche.order.accountdebt.vo.req.AccountDeductDebtReqVO;
import com.atzuche.order.accountdebt.vo.req.AccountInsertDebtReqVO;
import com.atzuche.order.accountdebt.vo.res.AccountDebtResVO;
import com.atzuche.order.accountdebt.entity.AccountDebtEntity;
import com.atzuche.order.accountdebt.mapper.AccountDebtMapper;
import com.autoyol.commons.web.ErrorCode;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Objects;


/**
 * 个人历史总额表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:34:34
 */
@Service
public class AccountDebtNoTService {
    @Autowired
    private AccountDebtMapper accountDebtMapper;


    /**
     * 根据会员号查询用户总欠款信息
     * @param memNo
     * @return
     * @throws AccountDeductDebtDBException
     */
    public AccountDebtResVO getAccountDebtByMemNo(String memNo) {
        Assert.notNull(memNo, ErrorCode.PARAMETER_ERROR.getText());
        AccountDebtEntity accountDebtEntity =  accountDebtMapper.getAccountDebtByMemNo(memNo);
        if(Objects.isNull(accountDebtEntity) || Objects.isNull(accountDebtEntity.getId())){
            accountDebtEntity = new AccountDebtEntity();
            accountDebtEntity.setMemNo(memNo);
            accountDebtEntity.setDebtAmt(NumberUtils.INTEGER_ZERO);
            accountDebtEntity.setVersion(NumberUtils.INTEGER_ONE);
            accountDebtMapper.insert(accountDebtEntity);
        }
        AccountDebtResVO res = new AccountDebtResVO();
        BeanUtils.copyProperties(accountDebtEntity,res);
        return res;
    }



    /**
     * 抵扣还款  欠款表记录更新
     * @param accountDeductDebt
     */
    public void deductAccountDebt(AccountDeductDebtReqVO accountDeductDebt) {
        //1 查询用户欠款总和
        AccountDebtEntity accountDebtEntity =  accountDebtMapper.getAccountDebtByMemNo(accountDeductDebt.getMemNo());
        if(Objects.isNull(accountDebtEntity) || Objects.isNull(accountDebtEntity.getId())){
            throw new AccountDeductDebtDBException();
        }
        accountDebtEntity.setDebtAmt(accountDebtEntity.getDebtAmt()-Math.abs(accountDeductDebt.getAmt()));
        int result = accountDebtMapper.updateByPrimaryKeySelective(accountDebtEntity);
        if(result==0){
            throw new AccountDeductDebtDBException();
        }
    }

    /**
     *  新产生欠款  更新总欠款
     * @param accountInsertDebt
     */
    public void productAccountDebt(AccountInsertDebtReqVO accountInsertDebt) {
        //1 查询用户欠款总和
        AccountDebtEntity accountDebtEntity =  accountDebtMapper.getAccountDebtByMemNo(accountInsertDebt.getMemNo());
        if(Objects.isNull(accountDebtEntity) || Objects.isNull(accountDebtEntity.getId())){
            throw new AccountInsertDebtDBException();
        }
        int amt = accountDebtEntity.getDebtAmt()+Math.abs(accountInsertDebt.getAmt());
        accountDebtEntity.setDebtAmt(amt);
        int result = accountDebtMapper.updateByPrimaryKeySelective(accountDebtEntity);
        if(result==0){
            throw new AccountInsertDebtDBException();
        }
    }
}
