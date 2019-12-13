package com.atzuche.order.service.notservice;

import com.atzuche.order.dto.AccountDeductDebtDTO;
import com.atzuche.order.entity.AccountDebtEntity;
import com.atzuche.order.exception.AccountDebtException;
import com.atzuche.order.mapper.AccountDebtMapper;
import com.atzuche.order.vo.res.AccountDebtResVO;
import com.autoyol.commons.web.ErrorCode;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * @throws AccountDebtException
     */
    public AccountDebtResVO getAccountDebtByMemNo(Integer memNo) throws AccountDebtException {
        if(Objects.isNull(memNo)){
            throw new AccountDebtException(ErrorCode.PARAMETER_ERROR);
        }
        LocalDateTime now = LocalDateTime.now();
        AccountDebtEntity accountDebtEntity =  accountDebtMapper.getAccountDebtByMemNo(memNo);
        if(Objects.isNull(accountDebtEntity) || Objects.isNull(accountDebtEntity.getId())){
            accountDebtEntity = new AccountDebtEntity();
            accountDebtEntity.setMemNo(memNo);
            accountDebtEntity.setCreateTime(now);
            accountDebtEntity.setUpdateTime(now);
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
    public void updateAccountDebt(AccountDeductDebtDTO accountDeductDebt) {
        //1 查询用户欠款总和
        AccountDebtEntity accountDebtEntity =  accountDebtMapper.getAccountDebtByMemNo(accountDeductDebt.getMemNo());
        if(Objects.isNull(accountDebtEntity) || Objects.isNull(accountDebtEntity.getId())){
            throw new AccountDebtException(ErrorCode.FAILED);
        }
        accountDebtEntity.setDebtAmt(accountDebtEntity.getDebtAmt()-Math.abs(accountDeductDebt.getAmtReal()));
        int result = accountDebtMapper.updateByPrimaryKey(accountDebtEntity);
        if(result==0){
            throw new AccountDebtException(ErrorCode.FAILED);
        }

    }
}
