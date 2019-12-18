package com.atzuche.order.accountrenterwzdepost.common;

/**
 * 车主收益常量
 */
public class Constant {
    /**
     *  下单成功记录应付违章押金 cat监控
     */
    public static final String CAT_TRANSACTION_INSERT_RENTER_WZ_DEPOSIT = "insertRenterWZDeposit";

    /**
     *  支付成功后记录实付押金信息 和押金资金进出信息 cat监控
     */
    public static final String CAT_TRANSACTION_UPDATE_RENTER_WZ_DEPOSIT = "updateRenterWZDeposit";

    /**
     *  支户头押金资金进出 操作 cat监控
     */
    public static final String CAT_TRANSACTION_UPDATE_RENTER_WZ_DEPOSIT_CHANGE = "updateRenterWZDepositChange";

}
