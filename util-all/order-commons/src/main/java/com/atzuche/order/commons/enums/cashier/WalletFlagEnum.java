package com.atzuche.order.commons.enums.cashier;

import java.util.Objects;

public enum WalletFlagEnum {

    //1租车预授权 ,2租车消费 ,3罚金收入 ,4违章押金预授权,5押金预授权扣除,6罚金支出,7礼品卡充值,8邀请奖励,9分享订单,10车主拒绝补偿,11充值,
    // 12体验券充值,21租车消费(真实钱包),22租车消费(虚拟钱包),23欠款抵扣 ,33取消订单退还（真实钱包）,34取消订单退还（虚拟钱包）,35恢复订单消费（真实钱包）
    // ,36恢复订单消费（虚拟钱包）,37撤销罚金收入

    RENTER_PRE_AUTHORIZE(1,"租车预授权"),
    RENTER_CONSUME(2,"租车消费"),
    FINE_INCOME(3,"罚金收入"),
    VIOLATION_DEPOSIT_PRE_AUTHORIZE(4,"违章押金预授权"),
    DEPOSIT_PRE_AUTHORIZE_DEDUCT(5,"违章押金预授权"),
    FINE_PAY(6,"罚金支出"),
    GIFT_CARD_RECHARGED(7,"礼品卡充值"),
    INVITATION_REWARD(8,"邀请奖励"),
    SHORE_ORDER(9,"分享订单"),
    CAR_OWN_REFUST_COMPENSATE(10,"车主拒绝补偿"),
    RECHARGE(11,"充值"),
    EXPERIENCE_TICKET_RECHARGE(12,"体验券充值"),

    //真实钱包
    RENTER_CONSUME_REAL(21,"租车消费"),
    //虚拟钱包
    RENTER_CONSUME_INVENTED(22,"租车消费"),
    ARREARS_DEDUCTION(23,"欠款抵扣"),
    //真实钱包
    SETTLE_ORDER_REFUST_REAL(31,"结算订单退还"),

    //真实钱包
    CANCEL_ORDER_REFUST_REAL(33,"取消订单退还"),
    //虚拟钱包
    CANCEL_ORDER_REFUST_INVENTED(34,"取消订单退还"),

    //真实钱包
    RECOVERY_ORDER_REFUST_REAL(35,"恢复订单消费"),
    //虚拟钱包
    RECOVERY_ORDER_REFUST_INVENTED(36,"恢复订单消费"),
    REVOKE_FINE_INCOME(37,"撤销罚金收入")

    ;

    private Integer code;
    private String text;


    private WalletFlagEnum(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    /**
     * 根据code取text
     * @param code
     * @return
     */
    public static String getFlagText(Integer code){
        if(Objects.isNull(code)){
            return null;
        }
        for(WalletFlagEnum walletFlagEnum :WalletFlagEnum.values()){
            if(walletFlagEnum.code.equals(code)){
                return walletFlagEnum.text;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
