package com.atzuche.order.cashieraccount.service;

import com.atzuche.order.accountrenterdeposit.service.AccountRenterDepositService;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.PayedOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.service.AccountRenterWzDepositService;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterWZDepositReqVO;
import com.atzuche.order.cashieraccount.vo.res.pay.OrderPayAsynResVO;
import com.autoyol.commons.utils.GsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 收银台支付退款操作
 *
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Service
public class CashierPayService{

    @Autowired AccountRenterDepositService accountRenterDepositService;
    @Autowired AccountRenterWzDepositService accountRenterWzDepositService;



    /**
     * 支付系统回调
     * MQ 异步回调
     */
    @Async
    @Transactional(rollbackFor=Exception.class)
    public void payCallBackAsyn(String orderPayAsynStr){
        OrderPayAsynResVO orderPayAsynVO = GsonUtils.convertObj(orderPayAsynStr, OrderPayAsynResVO.class);


    }


    /**
     * 记录应收车俩押金
     * 下单成功  调收银台 记录 车俩押金应付
     */
    @Transactional(rollbackFor=Exception.class)
    public void insertRenterDeposit(CreateOrderRenterDepositReqVO createOrderRenterDepositReqVO){
        accountRenterDepositService.insertRenterDeposit(createOrderRenterDepositReqVO);
    }

    /**
     * 车俩押金支付成功回调
     */
    @Transactional(rollbackFor=Exception.class)
    public void updateRenterDeposit(PayedOrderRenterDepositReqVO payedOrderRenterDeposit){
        accountRenterDepositService.updateRenterDeposit(payedOrderRenterDeposit);
    }

    /**
     * 支付成功后记录 实付违章押金信息 和违章押金资金进出信息
     */
    @Transactional(rollbackFor=Exception.class)
    public void updateRenterWZDeposit(PayedOrderRenterWZDepositReqVO payedOrderWZRenterDeposit){
        accountRenterWzDepositService.updateRenterWZDeposit(payedOrderWZRenterDeposit);
    }

}

