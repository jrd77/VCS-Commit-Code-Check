package com.atzuche.order.cashieraccount.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.cashieraccount.entity.AccountOwnerCashExamineHandleLogEntity;
import com.atzuche.order.cashieraccount.mapper.AccountOwnerCashExamineHandleLogMapper;
import com.atzuche.order.commons.constant.OrderConstant;
import com.autoyol.commons.web.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * 提现记录处理日志
 *
 * @author pengcheng.fu
 * @date 2020-07-17 11:48:27
 */
@Service
@Slf4j
public class AccountOwnerCashExamineHandleLogService {

    @Autowired
    private AccountOwnerCashExamineHandleLogMapper accountOwnerCashExamineHandleLogMapper;

    /**
     * 添加提现记录处理结果
     *
     * @param handleLogEntity 处理日志
     * @return int 成功记录数
     */
    public int addHandleLog(AccountOwnerCashExamineHandleLogEntity handleLogEntity) {
        log.info("Add accountOwnerCashExamine handle log >> handleLogEntity:[{}]", JSON.toJSONString(handleLogEntity));
        if (Objects.isNull(handleLogEntity)) {
            log.info("Add accountOwnerCashExamine handle log >> data is empty.");
            return OrderConstant.ZERO;
        }
        return accountOwnerCashExamineHandleLogMapper.insertSelective(handleLogEntity);
    }


    public void addHandleLog(Integer memNo, Integer id, int handleResult, String failCode, String failMessage) {
        log.info("Add accountOwnerCashExamine handle log >> memNo:[{}],id:[{}],handleResult:[{}],failCode:[{}]," +
                "failMessage:[{}]", memNo, id, handleResult, failCode, failMessage);

        AccountOwnerCashExamineHandleLogEntity handleLogEntity = new AccountOwnerCashExamineHandleLogEntity();
        handleLogEntity.setMemNo(memNo);
        handleLogEntity.setCashExamineId(id);
        handleLogEntity.setHandleResult(handleResult);
        if (handleResult == OrderConstant.TWO) {
            handleLogEntity.setFailCode(StringUtils.isBlank(failCode) ? ErrorCode.SYS_ERROR.getCode() : failCode);
            handleLogEntity.setFailMessage(StringUtils.isBlank(failMessage) ? ErrorCode.SYS_ERROR.getText() :
                    failMessage);
        }
        int result = addHandleLog(handleLogEntity);
        log.info("Add accountOwnerCashExamine handle log >> result is:[{}]", result);
    }


    public void addHandleLog(Integer memNo, Integer id, int handleResult) {
        log.info("Add accountOwnerCashExamine handle log >> memNo:[{}],id:[{}],handleResult:[{}]", memNo, id, handleResult);
        addHandleLog(memNo, id, handleResult, null, null);
    }
}
