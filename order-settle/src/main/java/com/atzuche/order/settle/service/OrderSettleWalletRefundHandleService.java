package com.atzuche.order.settle.service;

import com.atzuche.order.commons.constant.OrderConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 订单结算钱包退回处理(结算、取消订单)
 *
 * <p>租车费用使用钱包结算退回</p>
 * <p>车辆押金使用钱包结算退回</p>
 * <p>违章押金使用钱包结算退回</p>
 *
 * @author pengcheng.fu
 * @date 2020/6/11 10:16
 */
@Service
@Slf4j
public class OrderSettleWalletRefundHandleService {


    /**
     * 租车费用使用钱包结算退回
     *
     * @param surplusRentCarCost 剩余租车费用
     */
    public void walletRefundForRentCarCost(int surplusRentCarCost) {
        if (surplusRentCarCost <= OrderConstant.ZERO) {
            log.info("Surplus rental fee is zero.");
            return;
        }







    }

    /**
     * 车辆押金使用钱包结算退回
     *
     * @param surplusCarDepositAmt 剩余车辆押金
     */
    public void walletRefundForCarDeposit(int surplusCarDepositAmt) {

        if (surplusCarDepositAmt <= OrderConstant.ZERO) {
            log.info("Surplus car deposit is zero.");
            return;
        }

    }


    /**
     * 违章押金使用钱包结算退回
     *
     * @param surplusWzDepositAmt 剩余违章押金
     */
    public void walletRefundForWzDeposit(int surplusWzDepositAmt) {

        if (surplusWzDepositAmt <= OrderConstant.ZERO) {
            log.info("Surplus wz deposit is zero.");
            return;
        }

    }

}
