package com.atzuche.order.cashieraccount.service;

import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositEntity;
import com.atzuche.order.accountrenterdeposit.service.notservice.AccountRenterDepositNoTService;

import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostEntity;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositDetailEntity;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositEntity;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositCostNoTService;

import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositDetailNoTService;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositNoTService;
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

import java.util.List;
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
    private AccountRenterWzDepositNoTService accountRenterWzDepositNoTService;
    @Autowired
    private AccountRenterDepositNoTService accountRenterDepositNoTService;
    @Autowired private CashierNoTService cashierNoTService;
    @Autowired private AccountRenterWzDepositDetailNoTService accountRenterWzDepositDetailNoTService;


    /**
     * 查询违章押金
     */
    public AccountRenterWzDepositEntity queryWzDeposit(String orderNo){
        AccountRenterWzDepositEntity entity = accountRenterWzDepositNoTService.getAccountRenterWZDepositByOrder(orderNo);
        return entity;
    }

    /**
     * 获取应付的违章押金
     * @param orderNo
     * @return
     */
    public  int getTotalToPayWzDepositAmt(String orderNo){
        AccountRenterWzDepositEntity entity = accountRenterWzDepositNoTService.getAccountRenterWZDepositByOrder(orderNo);
        return entity.getYingshouDeposit()+entity.getShishouDeposit();
    }
    public WzDepositMsgResVO queryWzDepositMsg(String orderNo){
        WzDepositMsgResVO result = new WzDepositMsgResVO();
        result.setOrderNo(orderNo);
        AccountRenterWzDepositEntity entity = queryWzDeposit(orderNo);
        if(Objects.isNull(entity) || Objects.isNull(entity.getOrderNo())){
            return result;
        }

        result.setWzDepositAmt(entity.getShishouDeposit());
        result.setReductionAmt(0);
        result.setMemNo(entity.getMemNo());
        result.setYingshouWzDepositAmt(entity.getYingshouDeposit());
        CashierEntity cashierEntity = cashierNoTService.getCashierEntity(orderNo,entity.getMemNo(), DataPayKindConstant.DEPOSIT);

        if(Objects.nonNull(cashierEntity)){
            result.setPayStatus("支付成功");
            result.setPayTime(DateUtils.formate(cashierEntity.getCreateTime(),DateUtils.DATE_DEFAUTE1));
            result.setPayType(PayTypeEnum.getFlagText(cashierEntity.getPayType()));
        }

        List<AccountRenterWzDepositDetailEntity> list = accountRenterWzDepositDetailNoTService.findByOrderNo(orderNo);
        //剩余可用违章押金
        int wzDepositSurplusAmt = list.stream().mapToInt(AccountRenterWzDepositDetailEntity::getAmt).sum();
//        int debtAmt = list.stream().filter(obj ->{
//
//        }).mapToInt(AccountRenterWzDepositDetailEntity::getAmt).sum();

        result.setDebtStatus("成功");
        result.setWzDepositSurplusAmt(wzDepositSurplusAmt);
        result.setDebtAmt(0);
        result.setRefundAmt(entity.getRealReturnDeposit());
        result.setDebtStatus("1");
        return result;
    }

    /**
     * 查询车辆押金信息
     */
    public AccountRenterDepositEntity queryDeposit(String orderNo){
        AccountRenterDepositEntity entity = accountRenterDepositNoTService.queryDeposit(orderNo);
        return entity;
    }

    /**
     * 获得订单应付的金额
     * @param orderNo
     * @return
     */
    public int getTotalToPayDepositAmt(String orderNo){
        AccountRenterDepositEntity entity = accountRenterDepositNoTService.queryDeposit(orderNo);

        return entity.getYingfuDepositAmt()+entity.getShifuDepositAmt();
    }

}
