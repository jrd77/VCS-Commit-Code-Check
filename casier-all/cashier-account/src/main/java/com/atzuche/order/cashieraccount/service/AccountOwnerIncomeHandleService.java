package com.atzuche.order.cashieraccount.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeDetailEntity;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeEntity;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeNoTService;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.wallet.api.MemBalanceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 追加负值收益抵充操作
 *
 * @author pengcheng.fu
 * @date 2020/7/23 15:35
 */

@Service
@Slf4j
public class AccountOwnerIncomeHandleService {

    @Autowired
    private AccountOwnerIncomeNoTService accountOwnerIncomeNoTService;
    @Autowired
    private RemoteAccountService remoteAccountService;

    /**
     * 老订单收益抵充追加收益(收益金额负值)
     *
     * @param record       收益明細
     * @param addIncomeAmt 追加收益金额(抵充后的金额)
     * @return int 抵充后的追加收益金额
     */
    public int oldIncomeCompensateHandle(AccountOwnerIncomeDetailEntity record, int addIncomeAmt) {
        log.info("Old income compensate handle >> record:[{}], addIncomeAmt:[{}]", JSON.toJSONString(record), addIncomeAmt);
        if (addIncomeAmt >= OrderConstant.ZERO) {
            log.info("Old income compensate handle >> Dispense with compensate......");
            return addIncomeAmt;
        }
        MemBalanceVO memBalanceVO = remoteAccountService.getMemBalance(record.getMemNo());
        if (Objects.isNull(memBalanceVO) || Objects.isNull(memBalanceVO.getBalance()) || memBalanceVO.getBalance() <= OrderConstant.ZERO) {
            log.info("Old income compensate handle >> Member income not found......");
            return addIncomeAmt;
        }

        int surplusAddIncomeAmt = OrderConstant.ZERO;
        int incomeAmt;
        if (memBalanceVO.getBalance() >= Math.abs(addIncomeAmt)) {
            incomeAmt = addIncomeAmt;
        } else {
            incomeAmt = -memBalanceVO.getBalance();
            surplusAddIncomeAmt = addIncomeAmt + memBalanceVO.getBalance();
        }
        // 添加收益明细
        AccountOwnerIncomeDetailEntity entity = new AccountOwnerIncomeDetailEntity();
        BeanUtils.copyProperties(record, entity);
        entity.setCostCode(RenterCashCodeEnum.ADD_INCOME_PRODUCE_INCOME_OLD.getCashNo());
        entity.setCostDetail(RenterCashCodeEnum.ADD_INCOME_PRODUCE_INCOME_OLD.getTxt());
        entity.setAmt(incomeAmt);
        int result = accountOwnerIncomeNoTService.addAccountOwnerIncomeDetail(entity);
        // 更新会员收益信息
        remoteAccountService.deductBalance(String.valueOf(record.getMemNo()), Math.abs(incomeAmt));
        log.info("Old income compensate handle >> surplusAddIncomeAmt:[{}]", surplusAddIncomeAmt);
        return surplusAddIncomeAmt;
    }


    /**
     * 新订单收益抵充追加收益(收益金额负值)
     *
     * @param record       收益明細
     * @param incomeEntity 车主收益信息
     * @param addIncomeAmt 追加收益金额(抵充后的金额)
     * @return int 抵充后的追加收益金额
     */
    public int newIncomeCompensateHandle(AccountOwnerIncomeDetailEntity record, AccountOwnerIncomeEntity incomeEntity, int addIncomeAmt) {
        log.info("NEW income compensate handle >> record:[{}], incomeEntity:[{}], addIncomeAmt:[{}]",
                JSON.toJSONString(incomeEntity), JSON.toJSONString(record), addIncomeAmt);
        if (addIncomeAmt >= OrderConstant.ZERO) {
            log.info("NEW income compensate handle >> Dispense with compensate......");
            return addIncomeAmt;
        }

        if (Objects.isNull(incomeEntity) || Objects.isNull(incomeEntity.getIncomeAmt()) || incomeEntity.getIncomeAmt() <= OrderConstant.ZERO) {
            log.info("NEW income compensate handle >> Owner income not found......");
            return addIncomeAmt;
        }

        int surplusAddIncomeAmt = OrderConstant.ZERO;
        int incomeAmt;
        if (incomeEntity.getIncomeAmt() >= Math.abs(addIncomeAmt)) {
            incomeAmt = addIncomeAmt;
        } else {
            incomeAmt = -incomeEntity.getIncomeAmt();
            surplusAddIncomeAmt = addIncomeAmt + incomeEntity.getIncomeAmt();
        }
        // 更新车主收益并添加收益明细
        AccountOwnerIncomeDetailEntity entity = new AccountOwnerIncomeDetailEntity();
        BeanUtils.copyProperties(record, entity);
        entity.setCostCode(RenterCashCodeEnum.ADD_INCOME_PRODUCE_INCOME_NEW.getCashNo());
        entity.setCostDetail(RenterCashCodeEnum.ADD_INCOME_PRODUCE_INCOME_NEW.getTxt());
        entity.setAmt(incomeAmt);
        accountOwnerIncomeNoTService.updateOwnerIncomeInfo(entity, false);
        log.info("NEW income compensate handle >> surplusAddIncomeAmt:[{}]", surplusAddIncomeAmt);
        return surplusAddIncomeAmt;
    }


    /**
     * 二清订单收益抵充追加收益(收益金额负值)
     *
     * @param record       收益明細
     * @param incomeEntity 车主收益信息
     * @param addIncomeAmt 追加收益金额(抵充后的金额)
     * @return int 抵充后的追加收益金额
     */
    public int secondaryIncomeCompensateHandle(AccountOwnerIncomeDetailEntity record, AccountOwnerIncomeEntity incomeEntity, int addIncomeAmt) {
        log.info("Secondary income compensate handle >> record:[{}], incomeEntity:[{}], addIncomeAmt:[{}]",
                JSON.toJSONString(incomeEntity), JSON.toJSONString(record), addIncomeAmt);
        if (addIncomeAmt >= OrderConstant.ZERO) {
            log.info("Secondary income compensate handle >> Dispense with compensate......");
            return addIncomeAmt;
        }

        if (Objects.isNull(incomeEntity) || Objects.isNull(incomeEntity.getIncomeAmt()) || incomeEntity.getSecondaryIncomeAmt() <= OrderConstant.ZERO) {
            log.info("Secondary income compensate handle >> Owner income not found......");
            return addIncomeAmt;
        }

        int surplusAddIncomeAmt = OrderConstant.ZERO;
        int incomeAmt;
        if (incomeEntity.getSecondaryIncomeAmt() >= Math.abs(addIncomeAmt)) {
            incomeAmt = addIncomeAmt;
        } else {
            incomeAmt = -incomeEntity.getSecondaryIncomeAmt();
            surplusAddIncomeAmt = addIncomeAmt + incomeEntity.getSecondaryIncomeAmt();
        }
        // 更新车主收益并添加收益明细
        AccountOwnerIncomeDetailEntity entity = new AccountOwnerIncomeDetailEntity();
        BeanUtils.copyProperties(record, entity);
        entity.setCostCode(RenterCashCodeEnum.ADD_INCOME_PRODUCE_INCOME_SECONDARY.getCashNo());
        entity.setCostDetail(RenterCashCodeEnum.ADD_INCOME_PRODUCE_INCOME_SECONDARY.getTxt());
        entity.setAmt(incomeAmt);
        accountOwnerIncomeNoTService.updateOwnerIncomeInfo(entity, true);
        log.info("Secondary income compensate handle >> surplusAddIncomeAmt:[{}]", surplusAddIncomeAmt);
        return surplusAddIncomeAmt;
    }

}
