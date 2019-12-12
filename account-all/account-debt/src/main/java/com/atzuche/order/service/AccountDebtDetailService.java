package com.atzuche.order.service;

import com.atzuche.order.dto.AccountDeductDebtDTO;
import com.atzuche.order.exception.AccountDebtException;
import com.atzuche.order.service.notservice.AccountDebtDetailNoTService;
import com.atzuche.order.service.notservice.AccountDebtNoTService;
import com.atzuche.order.service.notservice.AccountDebtReceivableaDetailNoTService;
import com.atzuche.order.vo.req.AccountDeductDebtReqVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountDebtDetailMapper;
import com.atzuche.order.entity.AccountDebtDetailEntity;

import java.util.List;


/**
 * 个人历史欠款明细
 *
 * @author ZhangBin
 * @date 2019-12-11 17:34:34
 */
@Service
public class AccountDebtDetailService{
    @Autowired
    private AccountDebtNoTService accountDebtNoTService;
    @Autowired
    private AccountDebtDetailNoTService accountDebtDetailNoTService;
    @Autowired
    private AccountDebtReceivableaDetailNoTService accountDebtReceivableaDetailNoTService;

    /**
     * 抵扣历史欠款
     * @return
     */
    public void deductDebt(AccountDeductDebtReqVO accountDeductDebt) {
        // 1 查询用户所以代还的记录
        List<AccountDebtDetailEntity> accountDebtDetails =  accountDebtDetailNoTService.getDebtListByMemNo(accountDeductDebt.getMemNo());
        //2 清洗包装数据
        AccountDeductDebtDTO accountDeductDebtDTO = new AccountDeductDebtDTO(accountDeductDebt,accountDebtDetails);
        //3更新欠款表 当前欠款数
        accountDebtDetailNoTService.updateAlreadyDeductDebt(accountDeductDebtDTO);
        //3 记录欠款收款详情
        accountDebtReceivableaDetailNoTService.insertAlreadyReceivablea(accountDeductDebtDTO);
        //4 更新总欠款表
        accountDebtNoTService.updateAccountDebt(accountDeductDebtDTO);
    }

}
