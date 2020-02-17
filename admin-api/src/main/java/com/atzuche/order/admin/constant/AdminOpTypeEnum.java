package com.atzuche.order.admin.constant;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/15 11:22 上午
 **/
public enum AdminOpTypeEnum {
    CHANGE_ODER_REQ(1,"修改订单条件"),
    CHANGE_ODER_FEE(2,"修改订单费用"),
    CHANGE_WZ_FEE(3,"修改违章费用"),
    WZ_OP(4,"违章操作"),
    SUBMIT_ORDER(5,"下单");
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
