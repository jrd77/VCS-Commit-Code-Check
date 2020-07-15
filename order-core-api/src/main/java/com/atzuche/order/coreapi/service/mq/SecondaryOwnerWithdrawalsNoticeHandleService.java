package com.atzuche.order.coreapi.service.mq;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeEntity;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeNoTService;
import com.atzuche.order.cashieraccount.entity.AccountOwnerCashExamine;
import com.atzuche.order.cashieraccount.service.AccountOwnerCashExamineHandleService;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.ErrorCode;
import com.atzuche.order.commons.enums.account.income.AccountOwnerCashExamineStatus;
import com.atzuche.order.coreapi.common.exception.SecondaryNoticeMqException;
import com.atzuche.order.coreapi.entity.mq.WithdrawalsNoticeMq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author pengcheng.fu
 * @date 2020/7/10 10:35
 */
@Service
@Slf4j
public class SecondaryOwnerWithdrawalsNoticeHandleService {

    @Autowired
    private AccountOwnerCashExamineHandleService accountOwnerCashExamineHandleService;
    @Autowired
    private AccountOwnerIncomeNoTService accountOwnerIncomeNoTService;

    /**
     * 提现结果处理
     *
     * @param noticeMq 消息内容
     */
    @Transactional(rollbackFor = Exception.class)
    public void process(WithdrawalsNoticeMq noticeMq) {
        log.info("SecondaryOwnerWithdrawalsNoticeHandleService.process >> noticeMq:[{}]", JSON.toJSONString(noticeMq));
        if (Objects.isNull(noticeMq) || StringUtils.isBlank(noticeMq.getSerialNumber()) || StringUtils.isBlank(noticeMq.getMemNo())) {
            log.info("SecondaryOwnerWithdrawalsNoticeHandleService.process >> noticeMq is invalid.");

            throw new SecondaryNoticeMqException(com.autoyol.commons.web.ErrorCode.PARAMETER_ERROR.getCode(),
                    com.autoyol.commons.web.ErrorCode.PARAMETER_ERROR.getText());
        }

        AccountOwnerCashExamine accountOwnerCashExamine =
                accountOwnerCashExamineHandleService.selectBySerialNumberAndMemNo(noticeMq.getSerialNumber(), noticeMq.getMemNo());
        log.info("SecondaryOwnerWithdrawalsNoticeHandleService.process >> accountOwnerCashExamine:[{}]", JSON.toJSONString(accountOwnerCashExamine));
        if (Objects.isNull(accountOwnerCashExamine)) {
            log.info("SecondaryOwnerWithdrawalsNoticeHandleService.process >> Not found accountOwnerCashExamine. " +
                    "serialNumber:[{}], memNo:[{}]", noticeMq.getSerialNumber(), noticeMq.getMemNo());
            throw new SecondaryNoticeMqException(ErrorCode.SECONDARY_MQ_NOTFOUND_CASHEXAMINE);
        }

        // 金额不一致情况处理
        if (accountOwnerCashExamine.getAmt() != Integer.parseInt(noticeMq.getAmt())) {
            log.info("SecondaryOwnerWithdrawalsNoticeHandleService.process >> Amount discrepancy. " +
                            "accountOwnerCashExamine.amt:[{}], noticeMq.amt:[{}]", accountOwnerCashExamine.getAmt(),
                    noticeMq.getAmt());
            throw new SecondaryNoticeMqException(ErrorCode.SECONDARY_MQ_AMOUNT_DISCREPANCY);
        }

        // 同步车主收益(打款失败需要退还扣除的金额)
        if (StringUtils.equals(noticeMq.getTranStatus(), String.valueOf(OrderConstant.TWO)) &&
                StringUtils.equals(noticeMq.getRefundFlag(), String.valueOf(OrderConstant.NO))) {
            AccountOwnerIncomeEntity accountOwnerIncomeEntity =
                    accountOwnerIncomeNoTService.getOwnerIncomeByMemNo(noticeMq.getMemNo());

            if (Objects.isNull(accountOwnerIncomeEntity)) {
                log.info("SecondaryOwnerWithdrawalsNoticeHandleService.process >> Not found accountOwnerIncomeEntity." +
                        " memNo:[{}]", noticeMq.getMemNo());
                throw new SecondaryNoticeMqException(ErrorCode.SECONDARY_MQ_NOTFOUND_OWNER_INCOME);
            }

            accountOwnerIncomeEntity.setSecondaryIncomeAmt(accountOwnerIncomeEntity.getSecondaryIncomeAmt() + Integer.parseInt(noticeMq.getAmt()));
            accountOwnerIncomeNoTService.updateOwnerIncomeAmtForCashWith(accountOwnerIncomeEntity);
        }

        // 更新提现流水状态
        AccountOwnerCashExamine record = new AccountOwnerCashExamine();
        record.setId(accountOwnerCashExamine.getId());
        if (StringUtils.equals(noticeMq.getTranStatus(), String.valueOf(OrderConstant.ONE))) {
            record.setStatus(AccountOwnerCashExamineStatus.PAY_SUCCESS.getStatus());
        } else if (StringUtils.equals(noticeMq.getTranStatus(), String.valueOf(OrderConstant.TWO))) {
            record.setStatus(AccountOwnerCashExamineStatus.PAY_FAIL.getStatus());
        } else {
            record.setStatus(AccountOwnerCashExamineStatus.PAY_ING.getStatus());
        }
        record.setErrTxt(noticeMq.getTranStatus());
        record.setErrCode(noticeMq.getTranMeg());
        int result = accountOwnerCashExamineHandleService.updateAccountOwnerCashExamine(record);
        log.info("result:[{}]", result);
    }

}
