package com.atzuche.order.service.notservice;

import com.atzuche.order.entity.AccountOwnerIncomeDetailEntity;
import com.atzuche.order.entity.AccountOwnerIncomeEntity;
import com.atzuche.order.mapper.AccountOwnerIncomeMapper;
import com.atzuche.order.vo.res.AccountOwnerIncomeResVO;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
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

    }
}
