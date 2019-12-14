package com.atzuche.order.accountownerincome.service.notservice;

import com.atzuche.order.accountownerincome.exception.AccountOwnerIncomeException;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeDetailEntity;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeEntity;
import com.atzuche.order.accountownerincome.mapper.AccountOwnerIncomeMapper;
import com.autoyol.commons.web.ErrorCode;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;


/**
 * 车主收益总表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:44:19
 */
@Service
public class AccountOwnerIncomeNoTService {
    @Autowired
    private AccountOwnerIncomeMapper accountOwnerIncomeMapper;

    /**
     * 查询车主收益信息
     * @param memNo
     * @return
     */
    public int getOwnerIncomeAmt(Integer memNo) {
        AccountOwnerIncomeEntity accountOwnerIncome = getOwnerIncome(memNo);
        return accountOwnerIncome.getIncomeAmt();
    }

    public AccountOwnerIncomeEntity getOwnerIncome(Integer memNo) {
        if(Objects.isNull(memNo)){
            throw new AccountOwnerIncomeException(ErrorCode.FAILED);
        }
        AccountOwnerIncomeEntity accountOwnerIncome = accountOwnerIncomeMapper.selectByMemNo(memNo);
        if(Objects.isNull(accountOwnerIncome) || Objects.isNull(accountOwnerIncome.getId())){
            accountOwnerIncome = new AccountOwnerIncomeEntity();
            accountOwnerIncome.setMemNo(memNo);
            accountOwnerIncome.setVersion(NumberUtils.INTEGER_ONE);
            accountOwnerIncome.setIncomeAmt(NumberUtils.INTEGER_ZERO);
            accountOwnerIncome.setCreateTime(LocalDateTime.now());
            accountOwnerIncomeMapper.insert(accountOwnerIncome);
        }
        return accountOwnerIncome;
    }

    /**
     * 更新车主收益
     * @param accountOwnerIncomeDetail
     */
    public void updateOwnerIncomeAmt(AccountOwnerIncomeDetailEntity accountOwnerIncomeDetail) {
        AccountOwnerIncomeEntity accountOwnerIncome = getOwnerIncome(accountOwnerIncomeDetail.getMemNo());
        int amt = accountOwnerIncome.getIncomeAmt() + accountOwnerIncomeDetail.getAmt();
        if(amt<0){
            throw new AccountOwnerIncomeException(ErrorCode.GREATER_THAN_WITHDRAWAL_AMOUNT);
        }
        accountOwnerIncome.setIncomeAmt(amt);
        int result = accountOwnerIncomeMapper.updateByPrimaryKey(accountOwnerIncome);
        if(result==0){
            throw new AccountOwnerIncomeException(ErrorCode.FAILED);
        }
    }
}
