package com.atzuche.order.admin.constant;

import com.atzuche.order.commons.AdditionalDriver;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/15 11:22 上午
 **/
public enum AdminOpTypeEnum {
    CHANGE_ODER_REQ(1,"修改订单条件"),
    CHANGE_ODER_FEE(2,"修改订单费用"),
    CHANGE_WZ_FEE(3,"修改违章费用"),
    WZ_OP(4,"违章操作"),
    SUBMIT_ORDER(5,"下单"),
    TEMPORARY_WZ_REFUND(6,"暂扣违章押金操作"),
    TEMPORARY_CAR_DEPOSIT_REFUND(7,"暂扣租车押金操作"),
    TEMPORARY_CAR_DEPOSIT(8,"车辆押金暂扣扣款操作"),
    ADDITIONAL_DRIVER_ADD(9,"添加附加驾驶人操作"),
    RENTER_UPDATE_FINE(10,"租客端修改罚金操作"),
    RENTER_TO_PLATFORM(11,"租客需支付给平台的费用修改操作"),
    RENTER_PRICE_ADJUSTMENT(12,"租客车主相互调价操作"),
    PLATFORM_TO_RENTER(13,"平台给租客的补贴修改操作"),
    COUPON_EDIT(14,"券编辑操作")
    ;
    private int opCode;
    private String opType;

    AdminOpTypeEnum(int opCode, String opType) {
        this.opCode = opCode;
        this.opType = opType;
    }

    public int getOpCode() {
        return opCode;
    }

    public String getOpType() {
        return opType;
    }

}
