package com.atzuche.order.coreapi.enums;

/**
 * OrderStatusEnums
 *
 * @author shisong
 * @date 2020/1/14
 */
public enum OrderStatusEnums {
    //'主订单状态: 1,待确认 4,待支付 8,待调度  16,待取车 32,待还车 64,待结算 128,待违章结算 256,待理赔处理 512,完成 0结束',

    //交易步骤状态，5：承租人“委托取车”，6：承租人“委托还车”，7：承租人发起延时申请，8：延时申请被接受，9：延时申请被拒绝，10：车主“委托交车”，11：车主“委托收车”，12：车主已交车，13：交易（正常）结束，14：（车主）拒绝交易请求，15：租车人取消订单，16：车主取消订单，17:起租时间超过当前时间，订单自动失效，18：承租人已评价，19：车主已评价，20：双方已评，21：消费/租客支付订金, 22:平台取消-风控原因, 23：邀请抢单已结束,24:平台取消-订单匹配原因
    status_1(1,1,"待确认"),
    status_4(4,2,"待支付"),
    status_8(8,3,"待调度"),
    status_16(16,4,"待取车"),
    status_32(32,12,"待还车"),
    status_64(64,13,"待结算"),
    status_128(128,13,"待违章结算"),
    status_256(256,13,"待理赔处理"),
    status_512(512,13,"完成"),
    status_0(0,24,"结束"),
    ;

    private Integer newStatus;

    private Integer oldStatus;

    private String statusDescription;

    OrderStatusEnums(Integer newStatus, Integer oldStatus, String statusDescription) {
        this.newStatus = newStatus;
        this.oldStatus = oldStatus;
        this.statusDescription = statusDescription;
    }

    public Integer getNewStatus() {
        return newStatus;
    }

    public Integer getOldStatus() { return oldStatus; }

    public String getStatusDescription() { return statusDescription; }

    public static Integer getOldStatus(Integer newStatus){
        if(newStatus == null){
            return null;
        }
        OrderStatusEnums[] values = OrderStatusEnums.values();
        for (OrderStatusEnums value : values) {
            if(newStatus.equals(value.getNewStatus())){
                return value.getOldStatus();
            }
        }
        return null;
    }

    /**
     * 获取枚举描述信息
     * @param status
     * @return
     */
    public static String getDescriptionByStatus(Integer status){
        for (OrderStatusEnums orderStatus : OrderStatusEnums.values()) {
            if(orderStatus.getNewStatus().equals(status)){
                return orderStatus.getStatusDescription();
            }
        }
        return "";
    }

}
