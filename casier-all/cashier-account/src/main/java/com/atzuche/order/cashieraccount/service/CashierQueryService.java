package com.atzuche.order.cashieraccount.service;

import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositEntity;
import com.atzuche.order.accountrenterdeposit.service.notservice.AccountRenterDepositNoTService;

import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostEntity;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositCostNoTService;

import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.cashieraccount.vo.res.WzDepositMsgResVO;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.enums.cashier.PayTypeEnum;
import com.atzuche.order.commons.enums.cashier.TransStatusEnum;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * 收银台操作 查询专用
 *
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Service
@Slf4j
public class CashierQueryService {
    @Autowired
    private AccountRenterWzDepositCostNoTService accountRenterWzDepositCostNoTService;
    @Autowired
    private AccountRenterDepositNoTService accountRenterDepositNoTService;
    @Autowired private CashierNoTService cashierNoTService;


    /**
     * 查询违章押金
     */
    public AccountRenterWzDepositCostEntity queryWzDeposit(String orderNo){
        AccountRenterWzDepositCostEntity entity = accountRenterWzDepositCostNoTService.queryWzDeposit(orderNo);
        return entity;
    }
    public WzDepositMsgResVO queryWzDepositMsg(String orderNo){
        WzDepositMsgResVO result = new WzDepositMsgResVO();
        result.setOrderNo(orderNo);
        AccountRenterWzDepositCostEntity entity = queryWzDeposit(orderNo);
        if(Objects.isNull(entity) || Objects.isNull(entity.getOrderNo())){
            return result;
        }

        result.setWzDepositAmt(entity.getShifuAmt());
        result.setReductionAmt(0);
        result.setMemNo(entity.getMemNo());
        result.setYingshouWzDepositAmt(entity.getYingfuAmt());
        CashierEntity cashierEntity = cashierNoTService.getCashierEntity(orderNo,entity.getMemNo(), DataPayKindConstant.DEPOSIT);

        if(Objects.nonNull(cashierEntity)){
            result.setPayStatus("支付成功");
            result.setPayTime(DateUtils.formate(cashierEntity.getCreateTime(),DateUtils.DATE_DEFAUTE1));
            result.setPayType(PayTypeEnum.getFlagText(cashierEntity.getPayType()));
        }
        return result;
    }

    /**
     * 查询车辆押金信息
     */
    public AccountRenterDepositEntity queryDeposit(String orderNo){
        AccountRenterDepositEntity entity = accountRenterDepositNoTService.queryDeposit(orderNo);
        return entity;
    }

}
