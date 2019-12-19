package com.atzuche.order.commons.enums;

import lombok.Getter;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/14 3:20 下午
 **/
@Getter
public enum RenterMemRightEnum {
    //mamber_flag
    HIGH_QUALITY_OWNER(1,"MF_1","会员标识:优质车主"),
    HIGH_QUALITY_RENTER(2,"MF_2","会员标识:优质租客"),
    HIGH_QUALITY_OWNER_RENTER(3,"MF_3","会员标识:优质租客+优质车主"),
    VIP(4,"MF_4","会员标识:VIP"),
    TD(5,"MF_5","会员标识:免TD用户"),

    //任务
    BIND_WECHAT(1,"T_1","绑定微信"),
    INVITE_FRIENDS(2,"T_2","邀请还有"),
    SUCCESS_RENTCAR(3,"T_3","成功租车次数"),
    MEMBER_LEVEL(4,"T_4","会员的等级"),

    STAFF(1,"3","内部员工"),

    CPIC_MEM(1,"4","太保会员"),
    OTA_MEM(1,"5","OTA会员"),
    INSURANCE_CLIENT(1,"6","保险公司客户")

    ;
    /**
     * 序号
     */
    private int index;
    /**
     * 权益编码
     */
    private String rightCode;
    /**
     * 权益名称
     */
    private String rightName;

    RenterMemRightEnum(int index, String rightCode, String rightName) {
        this.index = index;
        this.rightCode = rightCode;
        this.rightName = rightName;
    }


}
