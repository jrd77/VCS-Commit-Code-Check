package com.atzuche.order.accountdebt.service;

import com.atzuche.order.accountdebt.dto.AccountDeductDebtDTO;
import com.atzuche.order.accountdebt.entity.AccountDebtDetailEntity;
import com.atzuche.order.accountdebt.exception.AccountDebtException;
import com.atzuche.order.accountdebt.service.notservice.AccountDebtDetailNoTService;
import com.atzuche.order.accountdebt.service.notservice.AccountDebtNoTService;
import com.atzuche.order.accountdebt.service.notservice.AccountDebtReceivableaDetailNoTService;
import com.atzuche.order.accountdebt.vo.req.AccountDeductDebtReqVO;
import com.atzuche.order.accountdebt.vo.req.AccountInsertDebtReqVO;
import com.atzuche.order.accountdebt.vo.res.AccountDebtResVO;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;


/**
 * 个人历史总额表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:34:34
 */
@Service
@Slf4j
public class AccountDebtService{
    @Autowired
    private AccountDebtNoTService accountDebtNoTService;
    @Autowired
    private AccountDebtDetailNoTService accountDebtDetailNoTService;
    @Autowired
    private AccountDebtReceivableaDetailNoTService accountDebtReceivableaDetailNoTService;


    /**
     * 根据会员号查询用户总欠款信息
     * @param memNo
     * @return
     * @throws AccountDebtException
     */
    public AccountDebtResVO getAccountDebtByMemNo(Integer memNo) throws AccountDebtException {
        return accountDebtNoTService.getAccountDebtByMemNo(memNo);
    }

    /**
     * 查看账户欠款总和
     * @param memNo
     * @return
     */
    public Integer getAccountDebtNumByMemNo(Integer memNo){
        AccountDebtResVO res = getAccountDebtByMemNo(memNo);
        if(Objects.isNull(res) || Objects.isNull(res.getDebtAmt())){
            return NumberUtils.INTEGER_ZERO;
        }
        return res.getDebtAmt();
    }

    /**
     * 抵扣历史欠款
     * @return
     */
    public void deductDebt(AccountDeductDebtReqVO accountDeductDebt) {
        log.info("AccountOwnerCostSettleService insertAccountOwnerCostSettle param", GsonUtils.toJson(accountDeductDebt));

        // 1 参数校验
        Assert.notNull(accountDeductDebt, ErrorCode.PARAMETER_ERROR.getText());
        accountDeductDebt.check();
        //1.1幂等支持
//        boolean isSuccess = accountDebtReceivableaDetailNoTService.idempotentByUniqueAndSourceCode(accountDeductDebt.getSourceCode(),accountDeductDebt.getUniqueNo());
//        if(isSuccess){
//            return;
//        }
        // 2 查询用户所以代还的记录
        List<AccountDebtDetailEntity> accountDebtDetails =  accountDebtDetailNoTService.getDebtListByMemNo(accountDeductDebt.getMemNo());
        //3 从用户所有待还款记录中 过滤本次 待还款的记录
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
        log.info("AccountOwnerCostSettleService insertAccountOwnerCostSettle param", GsonUtils.toJson(accountInsertDebt));
        //1校验
        Assert.notNull(accountInsertDebt, ErrorCode.PARAMETER_ERROR.getText());
        accountInsertDebt.check();
        //2 查询账户欠款
        accountDebtNoTService.productAccountDebt(accountInsertDebt);
        //3 新增欠款明细
        accountDebtDetailNoTService.insertDebtDetail(accountInsertDebt);

    }
}
