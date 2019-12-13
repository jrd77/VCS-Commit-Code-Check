package com.atzuche.order.service;

import com.atzuche.order.dto.AccountDeductDebtDTO;
import com.atzuche.order.exception.AccountDebtException;
import com.atzuche.order.service.notservice.AccountDebtDetailNoTService;
import com.atzuche.order.service.notservice.AccountDebtNoTService;
import com.atzuche.order.service.notservice.AccountDebtReceivableaDetailNoTService;
import com.atzuche.order.vo.req.AccountDeductDebtReqVO;
import com.atzuche.order.vo.req.AccountInsertDebtReqVO;
import com.atzuche.order.vo.res.AccountDebtResVO;
import com.autoyol.commons.web.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.AccountDebtDetailMapper;
import com.atzuche.order.entity.AccountDebtDetailEntity;
import org.springframework.util.Assert;

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
        // 1 参数校验
        Assert.notNull(accountDeductDebt, ErrorCode.PARAMETER_ERROR.getText());
        accountDeductDebt.check();
        // 2 查询用户所以代还的记录
        List<AccountDebtDetailEntity> accountDebtDetails =  accountDebtDetailNoTService.getDebtListByMemNo(accountDeductDebt.getMemNo());
        //3 清洗包装数据
        AccountDeductDebtDTO accountDeductDebtDTO = new AccountDeductDebtDTO(accountDeductDebt,accountDebtDetails);
        //4更新欠款表 当前欠款数
        accountDebtDetailNoTService.updateAlreadyDeductDebt(accountDeductDebtDTO);
        //5 记录欠款收款详情
        accountDebtReceivableaDetailNoTService.insertAlreadyReceivablea(accountDeductDebtDTO);
        //6 更新总欠款表
        accountDebtNoTService.deductAccountDebt(accountDeductDebtDTO);
    }

    /**
     * 记录用户历史欠款
     */
    public void insertDebt(AccountInsertDebtReqVO accountInsertDebt){
        //1校验
        Assert.notNull(accountInsertDebt, ErrorCode.PARAMETER_ERROR.getText());
        accountInsertDebt.check();
        //2 查询账户欠款
        accountDebtNoTService.productAccountDebt(accountInsertDebt);
        //3 新增欠款明细
        accountDebtDetailNoTService.insertDebtDetail(accountInsertDebt);

    }

}
