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

    ACCOUT_RENTET_DEPOSIT_FAIL("961001","车俩应收押金操作失败"),
    CHANGE_ACCOUT_RENTET_DEPOSIT_FAIL("961003","车俩押金资金进出操作失败"),

    ACCOUT_RENTER_DETAIL_DETAIL("891001","暂扣押金失败"),

    ACCOUT_RENTER_COST_DETAIL("951001","租车费用明细操作失败"),
    ACCOUT_RENTER_COST_SETTLE("951002","租车费用操作失败"),

    ACCOUT_RENTER_STOP_DETAIL("871001","租客停运费用操作失败"),

    ACCOUT_RENTET_WZ_DEPOSIT_FAIL("971001","违章押金操作失败"),
    ACCOUT_RENTET_WZ_COST_FAIL("971002","违章费用操作失败"),
    CHANGE_ACCOUT_RENTET_WZ_DEPOSIT_FAIL("971003","违章押金资金进出操作失败"),

    PLATFORM_SETTLE_SUBSIDY_AND_PROFIT("871001","结算平台费用出错"),


    ORDER_RENTER_ORDERNO_CREATE_ERROR("600001","订单编码创建异常"),

    ;

    private String code;
    private String text;

    private ErrorCode(String code, String text) {
        this.code = code;
        this.text = text;
    }
}
