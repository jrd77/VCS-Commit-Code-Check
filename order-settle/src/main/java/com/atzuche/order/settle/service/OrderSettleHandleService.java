package com.atzuche.order.settle.service;

import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.wallet.WalletProxyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 订单结算相关操作
 *
 * @author pengcheng.fu
 * @date 2020/4/16 16:30
 */
@Service
@Slf4j
public class OrderSettleHandleService {

    @Autowired
    private WalletProxyService walletProxyService;

    /**
     * 抵扣欠款处理
     */
    public int deductionDebtHandle() {
        //todo 新系统欠款信息处理
        //todo 获取新系统欠款信息


        //todo 计算欠款总额
        int newDebtAmt = 0;

        //todo 钱包抵扣欠款
        int realDeductionAmt = deductionWalletHandle("","", newDebtAmt);
        //todo 新系统欠款信息更新



        //todo 老系统欠款信息处理
        //todo 获取老系统亲款信息
        //todo 计算欠款总额
        int oldDebtAmt = 0;
        //todo 钱包抵扣欠款
        realDeductionAmt = deductionWalletHandle("","", oldDebtAmt);
        //todo 老系统欠款信息更新


        return realDeductionAmt;
    }


    /**
     * 抵扣钱包处理
     *
     * @param memNo        租客会员号
     * @param orderNo      订单号
     * @param deductionAmt 预计抵扣金额
     * @return int 真实抵扣金额
     */
    public int deductionWalletHandle(String memNo, String orderNo, int deductionAmt) {
        log.info("Deduction wallet.param is,memNo:[{}],orderNo:[{}],deductionAmt:[{}]", memNo, orderNo, deductionAmt);
        if (OrderConstant.ZERO == deductionAmt) {
            log.info("Deduction wallet. deductionAmt is zero!");
            return OrderConstant.ZERO;
        }

        int realDeductionAmt = walletProxyService.orderDeduct(memNo, orderNo, deductionAmt);
        if (realDeductionAmt == OrderConstant.ZERO) {
            //扣减失败,重新计算抵扣金额并再次扣减
            int balance = walletProxyService.getWalletByMemNo(memNo);
            if (OrderConstant.ZERO == balance) {
                log.info("Deduction wallet. balance is zero!");
                return OrderConstant.ZERO;
            }
            realDeductionAmt = balance >= deductionAmt ? deductionAmt : balance;
            log.info("Deduction wallet.realDeductionAmt:[{}],balance:[{}],deductionAmt:[{}]", realDeductionAmt, balance, deductionAmt);
            realDeductionAmt = walletProxyService.orderDeduct(memNo, orderNo, deductionAmt);
        }
        log.info("Deduction wallet.result is,realDeductionAmt:[{}]", realDeductionAmt);
        return realDeductionAmt;
    }



}
