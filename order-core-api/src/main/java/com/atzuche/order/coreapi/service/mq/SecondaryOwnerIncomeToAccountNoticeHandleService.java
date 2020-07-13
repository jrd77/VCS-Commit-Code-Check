package com.atzuche.order.coreapi.service.mq;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeEntity;
import com.atzuche.order.accountownerincome.entity.AccountOwnerSecondaryIncomeInoutDetailEntity;
import com.atzuche.order.accountownerincome.service.AccountOwnerSecondaryIncomeInoutDetailService;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeNoTService;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.ErrorCode;
import com.atzuche.order.coreapi.common.exception.SecondaryNoticeMqException;
import com.atzuche.order.coreapi.entity.mq.OwnerIncomeAmtNoticeMq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author pengcheng.fu
 * @date 2020/7/13 10:38
 */
@Service
@Slf4j
public class SecondaryOwnerIncomeToAccountNoticeHandleService {

    @Autowired
    private AccountOwnerSecondaryIncomeInoutDetailService accountOwnerSecondaryIncomeInoutDetailService;
    @Autowired
    private AccountOwnerIncomeNoTService accountOwnerIncomeNoTService;

    @Transactional(rollbackFor = Exception.class)
    public void process(OwnerIncomeAmtNoticeMq noticeMq) {
        log.info("SecondaryOwnerIncomeToAccountNoticeHandleService.process >> noticeMq:[{}]", JSON.toJSONString(noticeMq));
        if (Objects.isNull(noticeMq)) {
            log.info("SecondaryOwnerIncomeToAccountNoticeHandleService.process >> noticeMq is invalid.");
            throw new SecondaryNoticeMqException(com.autoyol.commons.web.ErrorCode.PARAMETER_ERROR.getCode(),
                    com.autoyol.commons.web.ErrorCode.PARAMETER_ERROR.getText());
        }

        AccountOwnerIncomeEntity accountOwnerIncomeEntity =
                accountOwnerIncomeNoTService.getOwnerIncomeByMemNo(noticeMq.getMemNo());

        if (Objects.isNull(accountOwnerIncomeEntity)) {
            log.info("SecondaryOwnerIncomeToAccountNoticeHandleService.process >> Not found accountOwnerIncomeEntity." +
                    " memNo:[{}]", noticeMq.getMemNo());
            throw new SecondaryNoticeMqException(ErrorCode.SECONDARY_MQ_NOTFOUND_OWNER_INCOME);
        }
        // 车主收益信息更新
        int receiveAmt = StringUtils.isBlank(noticeMq.getReceiveAmt()) ? OrderConstant.ZERO :
                Integer.parseInt(noticeMq.getReceiveAmt());
        int secondaryFreezeIncomeAmt = Objects.nonNull(accountOwnerIncomeEntity.getSecondaryFreezeIncomeAmt()) ?
                accountOwnerIncomeEntity.getSecondaryFreezeIncomeAmt() : OrderConstant.ZERO;

        if (receiveAmt > secondaryFreezeIncomeAmt) {
            log.info("SecondaryOwnerIncomeToAccountNoticeHandleService.process >> Freeze incomeAmt is less than.");
            throw new SecondaryNoticeMqException(ErrorCode.SECONDARY_MQ_INCOME_IS_LESS);
        }
        int secondaryIncomeAmt = Objects.nonNull(accountOwnerIncomeEntity.getSecondaryIncomeAmt()) ?
                accountOwnerIncomeEntity.getSecondaryIncomeAmt() : OrderConstant.ZERO;

        accountOwnerIncomeEntity.setSecondaryIncomeAmt(secondaryIncomeAmt + receiveAmt);
        accountOwnerIncomeEntity.setSecondaryFreezeIncomeAmt(secondaryFreezeIncomeAmt - receiveAmt);
        accountOwnerIncomeNoTService.updateOwnerIncomeAmtForCashWith(accountOwnerIncomeEntity);

        // 记录二清收益进出明细
        AccountOwnerSecondaryIncomeInoutDetailEntity inoutDetail = new AccountOwnerSecondaryIncomeInoutDetailEntity();
        inoutDetail.setMemNo(noticeMq.getMemNo());
        inoutDetail.setOwnerIncomeId(accountOwnerIncomeEntity.getId());
        inoutDetail.setIncomeAmtBefore(secondaryFreezeIncomeAmt);
        inoutDetail.setInOutAmt(-receiveAmt);
        inoutDetail.setIncomeAmtAfter(accountOwnerIncomeEntity.getSecondaryFreezeIncomeAmt());
        accountOwnerSecondaryIncomeInoutDetailService.addSecondaryIncomeInoutDetail(inoutDetail);
    }
}
