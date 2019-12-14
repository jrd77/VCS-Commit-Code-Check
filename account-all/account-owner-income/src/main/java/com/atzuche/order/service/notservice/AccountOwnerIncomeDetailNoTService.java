package com.atzuche.order.service.notservice;

import com.atzuche.order.entity.AccountOwnerIncomeDetailEntity;
import com.atzuche.order.entity.AccountOwnerIncomeExamineEntity;
import com.atzuche.order.enums.AccountOwnerIncomeDetailType;
import com.atzuche.order.enums.AccountOwnerIncomeExamineStatus;
import com.atzuche.order.enums.OrderCoseSourceCode;
import com.atzuche.order.exception.AccountOwnerIncomeException;
import com.atzuche.order.mapper.AccountOwnerIncomeDetailMapper;
import com.atzuche.order.vo.req.AccountOwnerIncomeExamineOpReqVO;
import com.autoyol.commons.web.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;


/**
 * 车主收益资金进出明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:44:19
 */
@Service
public class AccountOwnerIncomeDetailNoTService {
    @Autowired
    private AccountOwnerIncomeDetailMapper accountOwnerIncomeDetailMapper;
    @Autowired
    private AccountOwnerIncomeExamineNoTService accountOwnerIncomeExamineNoTService;


    public AccountOwnerIncomeDetailEntity insertAccountOwnerIncomeDetail(AccountOwnerIncomeExamineOpReqVO accountOwnerIncomeExamineOpReq) {
        //1 查询审核通过的  收益审核信息
        AccountOwnerIncomeExamineEntity accountOwnerIncomeExamine = accountOwnerIncomeExamineNoTService.getAccountOwnerIncomeExamineById(accountOwnerIncomeExamineOpReq.getAccountOwnerIncomeExamineId());
        if(Objects.isNull(accountOwnerIncomeExamine) || Objects.isNull(accountOwnerIncomeExamine.getId())){
            throw new AccountOwnerIncomeException(ErrorCode.FAILED);
        }
        //1.1 审核不通过 抛异常 不能增加收益
        if(!AccountOwnerIncomeExamineStatus.PASS_EXAMINE.equals(accountOwnerIncomeExamine.getStatus())){
            throw new AccountOwnerIncomeException(ErrorCode.FAILED);
        }
        LocalDateTime now = LocalDateTime.now();
        AccountOwnerIncomeDetailEntity accountOwnerIncomeDetail = new AccountOwnerIncomeDetailEntity();
        accountOwnerIncomeDetail.setAmt(accountOwnerIncomeExamine.getAmt());
        accountOwnerIncomeDetail.setIncomeExamineId(accountOwnerIncomeExamine.getId());
        accountOwnerIncomeDetail.setCreateOp(accountOwnerIncomeExamineOpReq.getOpName());
        accountOwnerIncomeDetail.setCreateTime(now);
        accountOwnerIncomeDetail.setDetail(accountOwnerIncomeExamineOpReq.getDetail());
        accountOwnerIncomeDetail.setMemNo(accountOwnerIncomeExamine.getMemNo());
        accountOwnerIncomeDetail.setOrderNo(accountOwnerIncomeExamine.getOrderNo());
        accountOwnerIncomeDetail.setTime(now);
        accountOwnerIncomeDetail.setType(AccountOwnerIncomeDetailType.INCOME.getType());
        accountOwnerIncomeDetail.setCostCode(OrderCoseSourceCode.OWNER_COST_SETTLE.getCode());
        accountOwnerIncomeDetail.setCostDetail(OrderCoseSourceCode.OWNER_COST_SETTLE.getDesc());
        int result = accountOwnerIncomeDetailMapper.insert(accountOwnerIncomeDetail);
        if(result==0){
            throw new AccountOwnerIncomeException(ErrorCode.FAILED);
        }
        return accountOwnerIncomeDetail;
    }
}
