package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    ACCOUT_DEBT_DEDUCT_DEBT("921001","抵扣历史欠款出错"),
    ACCOUT_DEBT_INSERT_DEBT("921002","记录历史欠款出错"),

    ACCOUT_OWNER_COST_SETTLE("931001","车主结算出错"),

    ACCOUT_OWNER_INCOME_SETTLE("941001","车主收益结算出错"),
    ACCOUT_OWNER_INCOME_EXAMINE("941002","车主收益审核出错"),

    ACCOUT_RENTER_CLAIM_DETAIL("881001","租客理赔费用操作失败"),

    ACCOUT_RENTET_DEPOSIT_FAIL("961001","车辆应收押金操作失败"),
    ACCOUT_RENTET_COST_FAIL("961004","车辆租车费用收银台操作失败"),
    CHANGE_ACCOUT_RENTET_DEPOSIT_FAIL("961003","车辆押金资金进出操作失败"),

    ACCOUT_RENTER_DETAIL_DETAIL("891001","暂扣押金失败"),

    ACCOUT_RENTER_COST_DETAIL("951001","租车费用明细操作失败"),
    ACCOUT_RENTER_COST_SETTLE("951002","租车费用操作失败"),
    ACCOUT_RENTER_COST_SETTLE_REFUND("951003","租车费用退还操作失败"),

    ACCOUT_RENTER_STOP_DETAIL("871001","租客停运费用操作失败"),

    ACCOUT_RENTET_WZ_DEPOSIT_FAIL("971001","违章押金操作失败"),
    ACCOUT_RENTET_WZ_COST_FAIL("971002","违章费用操作失败"),
    CHANGE_ACCOUT_RENTET_WZ_DEPOSIT_FAIL("971003","违章押金资金进出操作失败"),
    RENTER_WZ_SETTLED("971004","违章押金已结算"),

    PLATFORM_SETTLE_SUBSIDY_AND_PROFIT("871001","结算平台费用出错"),

    CASHIER_REFUND_APPLY("981001","退款申请出错"),
    CASHIER_PAY_REFUND_CALL_BACK_FAIL("981003","支付系统退款回调操作失败"),
    CASHIER_PAY_CALL_BACK_FAIL("981004","支付系统支付回调操作失败"),
    CASHIER_PAY_SIGN_PARAM_ERRER("981007","支付参数签名失败"),
    CASHIER_PAY_SIGN_FAIL_ERRER("981009","没有待支付记录"),
    CASHIER_PAY_SETTLEAMT_FAIL_ERRER("981010","支付系统返回的支付金额异常"),
    

    ORDER_SETTLE_FLAT_ACCOUNT("811001","结算费用未平账"),

    GET_WALLETR_MSG("981005","查询钱包信息出错"),
    DEDUCT_WALLETR_MSG("981006","扣减钱包信息出错"),
    SETTLE_RETURN_WALLETR_MSG("981008","结算扣减钱包出错"),
    NOT_ENOUGH_BALANCE("981009","提现余额不足"),
    NOT_EXIST_ACCOUNT("981010","绑卡信息不存在"),

    ORDER_RENTER_ORDERNO_CREATE_ERROR("600001","订单编码创建异常"),


    COST_GET_RETUIRN_ERROR("700001","获取取还车费用系统异常"),
    COST_GET_RETUIRN_FAIL("700002","获取取还车费用失败"),
    COST_GET_RETUIRN_OVER_ERROR("700003","获取取还车费用异常"),
    COST_GET_RETUIRN_OVER_FAIL("700004","获取取还车费用失败"),
    IS_GET_CAR_OVER_FAIL("700005","取车是否超运能获取失败"),
    IS_GET_CAR_OVER_ERROR("700006","取车是否超运能接口异常"),
    IS_RETURN_CAR_OVER_FAIL("700007","还车是否超运能获取失败"),
    IS_RETURN_CAR_OVER_ERROR("700008","还车是否超运能接口异常"),
    REMOTE_CALL_FAIL("700008","远程操作失败"),


    FEIGN_MEMBER_DRIVER_fAIL("700100","获取附加驾驶人信息失败"),
    FEIGN_MEMBER_DRIVER_ERROR("700101","获取附加驾驶人信息异常"),
    FEIGN_RENTER_CAR_ERROR("700102","获取租客车辆信息异常"),
    FEIGN_RENTER_CAR_FAIL("700103","获取租客车辆信息失败"),
    FEIGN_OWNER_MEMBER_ERROR("700104","获取车主会员信息异常"),
    FEIGN_OWNER_MEMBER_FAIL("700105","获取车主会员信息失败"),
    FEIGN_RENTER_MEMBER_ERROR("700106","获取租客会员信息异常"),
    FEIGN_RENTER_MEMBER_FAIL("700107","获取租客会员信息失败"),

    FEIGN_CHECK_CAR_STOCK_ERROR("700108","库存校验异常"),
    FEIGN_CHECK_CAR_STOCK_FAIL("700109","库存校验失败"),
    FEIGN_CUT_CAR_STOCK_ERROR("700110","扣减库存异常"),
    FEIGN_CUT_CAR_STOCK_FAIL("700111","扣减库存失败"),
    FEIGN_RELEASE_CAR_STOCK_ERROR("700112","释放库存异常"),
    FEIGN_RELEASE_CAR_STOCK_FAIL("700113","释放库存失败"),
    NOT_STOCK_EXCEPTION("700114","库存不足"),
    LOCK_STOCK_FAIL("700114","锁定库存失败"),
    RELEASE_STOCK_FAIL("700114","释放库存失败"),

    ORDER_QUERY_FAIL("700115","获取订单失败"),

    ADMIN_CAR_DEPOSIT_QUERY_FAIL("700116","车辆押金获取失败"),
    ADMIN_ORDER_CANCEL_ERR("700117","取消订单异常"),
    ADMIN_ORDER_CANCEL_FAIL("700118","取消订单失败"),
    ADMIN_ORDER_QUERY_HISTORY_ERR("700119","查询子订单历史列表异常"),
    ADMIN_ORDER_QUERY_HISTORY_FAIL("700120","查询历史订单列表失败"),
    ADMIN_CAR_DEPOSIT_QUERY_ERR("700121","车辆押金查询异常"),
    ADMIN_ORDER_MODIFY_ERR("700122","修改订单异常"),
    ADMIN_ORDER_MODIFY_FAIL("700123","修改订单异常"),
    NO_EFFECTIVE_ERR("700124","有效子订单异常"),

    OWNER_RENT_DETAIL_ERR("700125","获取车主租金详情异常"),
    OWNER_RENT_DETAIL_FAIL("700126","获取车主租金详情失败"),
    OWNER_RENT_PRICE_ERR("700127","获取车主租客的相互调价异常"),
    OWNER_RENT_PRICE_FAIL("700128","获取车主租客的相互调价失败"),
    OWNER_FINE_DETAIL_ERR("700129","获取违约罚金异常"),
    OWNER_FINE_DETAIL_FAIL("700130","获取违约罚金失败"),
    OWNER_SERVICE_FEE_FAIL("700131","获取服务费失败"),
    OWNER_SERVICE_FEE_ERR("700132","获取服务费异常"),
    OWNER_PLATFORM_ERR("700133","获取车主付给平台费用异常"),
    OWNER_PLATFORM_FAIL("700134","获取车主付给平台费用失败"),
    OWNER_PLATFORM_SUBSIDY_ERR("700135","获取平台给车主的补贴明细异常"),
    OWNER_PLATFORM_SUBSIDY_FAIL("700136","获取平台给车主的补贴明细失败"),
    ADMIN_ORDER_SUBMIT_ERR("700137","后台管理系统下单异常"),
    ADMIN_ORDER_SUBMIT_FAIL("700138","后台管理系统下单失败"),
    ADMIN_ORDER_QUERY_R_HISTORY_ERR("700139","人工调度查询子订单历史列表异常"),
    ADMIN_ORDER_QUERY_R_HISTORY_FAIL("700140","人工调度查询历史订单列表失败"),
    ADMIN_OWNER_UPDATE_FIEN_ERR("700141","修改车主罚金异常"),
    ADMIN_OWNER_UPDATE_FIEN_FAIL("700142","修改车主罚金失败"),
    OWNER_ORDER_GOODS_NOT_EXIST("700143","车主订单商品信息为空。"),
    OWNER_ORDER_NOT_FOUND_MEMNO("700144","车主订单获取失败"),
    RENT_TIME_LESS_CURR_TIME_2HOUR("500055","起租时间应晚于当前时间2小时"),
    REVERT_TIME_LESS_RENT_TIME_1HOUR("500056","“还车时间”应晚于“起租时间”1小时"),
    RENT_TIME_LESS_CURR_TIME("500006","起租时间应晚于当前时间"),
    NO_RANGE_POINT("600040","地址不在范围内"),
    SERVICE_NOT_OPEN("988888","当前城市还未开放服务哦，我们会尽快开放哒~!"),
    ;

    private String code;
    private String text;

    private ErrorCode(String code, String text) {
        this.code = code;
        this.text = text;
    }
}
