package com.atzuche.order.admin.constant;

import com.atzuche.order.commons.AdditionalDriver;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/15 11:22 上午
 **/
public enum AdminOpTypeEnum {
    OTHER(0,"其他"),
    CHANGE_ODER_REQ(1,"修改订单"),
    CHANGE_ODER_FEE(2,"修改订单费用"),
    CHANGE_WZ_FEE(3,"修改违章费用"),
    WZ_OP(4,"违章操作"),
    SUBMIT_ORDER(5,"下单操作"),
    TEMPORARY_WZ_REFUND(6,"暂扣违章押金操作"),
    TEMPORARY_CAR_DEPOSIT_REFUND(7,"暂扣租车押金操作"),
    TEMPORARY_CAR_DEPOSIT(8,"车辆押金暂扣扣款操作"),
    ADDITIONAL_DRIVER_ADD(9,"添加附加驾驶人操作"),
    RENTER_UPDATE_FINE(10,"租客端修改罚金操作"),
    RENTER_TO_PLATFORM(11,"租客需支付给平台的费用修改操作"),
    RENTER_PRICE_ADJUSTMENT(12,"租客车主相互调价操作"),
    PLATFORM_TO_RENTER(13,"平台给租客的补贴修改操作"),
    TRANSFER_CAR(14,"更换车辆"),
    OFFLINE_PAY(15,"线下支付"),
    VIRTUAL_PAY(16,"虚拟支付"),
    UPDATE_RENT_CITY(17,"修改用车城市"),
    UPDATE_RISK_STATUS(18,"修改是否风控事故"),
    UPDATE_GETRETURN_REMARK(19,"修改取送车备注"),
    COUPON_EDIT(20,"券编辑操作"),
    CANCEL_ORDER_PLAT(21,"取消订单-平台取消操作"),
    CANCEL_ORDER_RENTER_PLAT(22,"取消订单-待租客取消操作"),
    CANCEL_ORDER_OWNER_PLAT(23,"取消订单-待车主取消操作"),
    PLATFORM_TO_OWNER(24,"平台给车主的补贴操作"),
    OWNER_TO_RENTER(25,"车主给租客的优惠操作"),
    OWNER_RENTER_GET_RETURN_CAR(26,"车主租客取还车服务操作"),
    ABATEMENTFLAG(26,"补充保障服务费"),
    TYREINSURFLAG(27,"轮胎/轮毂保障服务费"),
    DRIVERINSURFLAG(28,"驾乘无忧保障费"),
    OWNER_FINE(29,"车主罚金操作"),
    OWNER_TO_PLATFORM(30,"车主需支付给平台的费用操作"),
    SUPERPOWER_CHANGE_ODER_REQ(31,"修改订单（超级权限）")
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
