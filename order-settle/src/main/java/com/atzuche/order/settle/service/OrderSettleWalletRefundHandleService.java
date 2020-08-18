package com.atzuche.order.settle.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.cashieraccount.vo.req.CashierRefundApplyReqVO;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.autopay.gateway.constant.DataPayTypeConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private CashierNoTService cashierNoTService;


    /**
     * 租车费用使用钱包结算退回
     *
     * @param orderNo            订单号
     * @param renterMemNo        租客会员号
     * @param surplusRentCarCost 剩余租车费用
     */
    public List<CashierRefundApplyReqVO> walletRefundForRentCarCost(String orderNo, String renterMemNo, int surplusRentCarCost) {
        log.info("OrderSettleWalletRefundHandleService.walletRefundForRentCarCost >> orderNo:[{}],renterMemNo:[{}]," +
                        "surplusRentCarCost:[{}]", orderNo, renterMemNo, surplusRentCarCost);
        if (surplusRentCarCost <= OrderConstant.ZERO) {
            log.info("Surplus rental fee is zero.");
            return null;
        }

        List<CashierEntity> records = cashierNoTService.queryWalletPayRentCarCostByOrderNoAndMemNo(orderNo,
                renterMemNo);
        if (CollectionUtils.isEmpty(records)) {
            log.info("Not found wallet pay record.");
            return null;
        }

        int surplusAmt = -surplusRentCarCost;
        List<CashierRefundApplyReqVO> cashierRefundApplys = new ArrayList<>();
        for (CashierEntity entity : records) {
            if (surplusAmt < 0) {
                CashierRefundApplyReqVO vo = new CashierRefundApplyReqVO();
                BeanUtils.copyProperties(entity, vo);
                vo.setFlag(getCashNoByPayKind(entity.getPayKind()));
                vo.setRenterCashCodeEnum(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_REFUND);
                vo.setPaySource(entity.getPaySource());
                //固定04 退货 200407
                vo.setPayType(DataPayTypeConstant.PUR_RETURN);
                vo.setRemake("结算订单退还钱包");
                int amt = surplusAmt + entity.getPayAmt();
                vo.setAmt(amt >= OrderConstant.ZERO ? surplusAmt : -entity.getPayAmt());
                cashierRefundApplys.add(vo);
                surplusAmt = surplusAmt + entity.getPayAmt();
                log.info("OrderSettleWalletRefundHandleService.walletRefundForRentCarCost >> surplusAmt:[{}]", surplusAmt);
            }
        }
        log.info("OrderSettleWalletRefundHandleService.walletRefundForRentCarCost >> orderNo:[{}],renterMemNo:[{}]," +
                "cashierRefundApplys:[{}]", orderNo, renterMemNo, JSON.toJSONString(cashierRefundApplys));
        return cashierRefundApplys;
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

    /**
     * 依据payKind获取cashNo
     *
     * @param payKind 支付类型
     * @return String
     */
    private String getCashNoByPayKind(String payKind) {

        if (StringUtils.equalsIgnoreCase(payKind, DataPayKindConstant.RENT_AMOUNT)) {
            return RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST.getCashNo();
        }

        if (StringUtils.equalsIgnoreCase(payKind, DataPayKindConstant.RENT_INCREMENT)) {
            return RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN.getCashNo();
        }

        if (StringUtils.equalsIgnoreCase(payKind, DataPayKindConstant.RENT_AMOUNT_AFTER)) {
            return RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AFTER.getCashNo();
        }

        if (StringUtils.equalsIgnoreCase(payKind, DataPayKindConstant.RENT_INCREMENT_CONSOLE)) {
            return RenterCashCodeEnum.ACCOUNT_RENTER_SUPPLEMENT_COST_AGAIN.getCashNo();
        }
        return "";
    }


}
