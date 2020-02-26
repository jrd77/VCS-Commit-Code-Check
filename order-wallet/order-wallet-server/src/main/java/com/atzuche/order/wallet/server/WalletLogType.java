package com.atzuche.order.wallet.server;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/9 5:04 下午
 **/
public enum WalletLogType {
    ORDER_EXPENSE_PAY(21,"租车消费"),
    ORDER_EXPENSE_GIVE(22,"租车消费"),
    ORDER_CANCEL_PAY(31,"取消订单退还"),
    ORDER_CANCEL_GIVE(32,"取消订单退还"),
    SETTLE_PAY(31,"取消订单退还"),
    SETTLE_GIVE(32,"取消订单退还");
    private int flag;
    private String flagTxt;

    WalletLogType(int flag, String flagTxt) {
        this.flag = flag;
        this.flagTxt = flagTxt;
    }
}
