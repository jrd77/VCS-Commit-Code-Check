package com.atzuche.order.commons.enums;

import lombok.Getter;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/14 3:20 下午
 **/
@Getter
public enum MemRightEnum {
    //mamber_flag
    HIGH_QUALITY_OWNER("1","优质车主"),
    HIGH_QUALITY_RENTER("2","优质租客"),
    HIGH_QUALITY_OWNER_RENTER("3","优质租客+优质车主"),
    VIP("4","VIP"),
    TD("5","免TD用户"),

    //任务
    BIND_WECHAT("21","绑定微信"),
    INVITE_FRIENDS("22","邀请好友"),
    SUCCESS_RENTCAR("23","成功租车次数"),
    MEMBER_LEVEL("24","会员的等级"),

    STAFF("6","内部员工"),

    CPIC_MEM("7","太保会员"),
    OTA_MEM("8","OTA会员"),
    INSURANCE_CLIENT("9","保险公司客户"),
    ENTERPISE_CLIENT("10","企业用户"),
    DAIBUCHE_CLIENT("11","代步车账号"),
    SHENZHEN_CPIC_CLIENT("12","深圳太保员工"),
    CANCELED_CLIENT("13","9已注销用户")

    ;
    /**
     * 权益编码
     */
    private String rightCode;
    /**
     * 权益名称
     */
    private String rightName;

    MemRightEnum(String rightCode, String rightName) {
        this.rightCode = rightCode;
        this.rightName = rightName;
    }


}
