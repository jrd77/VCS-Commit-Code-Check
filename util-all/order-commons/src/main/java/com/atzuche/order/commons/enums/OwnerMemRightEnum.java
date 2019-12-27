package com.atzuche.order.commons.enums;

import lombok.Getter;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/14 3:20 下午
 **/
@Getter
public enum OwnerMemRightEnum {
    BIND_WECHAT("T_1", "绑定微信") //任务
    ,
    CPIC_MEM("4", "太保会员"),
    HIGH_QUALITY_OWNER("MF_1", "会员标识:优质车主") //mamber_flag
    ,
    HIGH_QUALITY_OWNER_RENTER("MF_3", "会员标识:优质租客+优质车主"),
    HIGH_QUALITY_RENTER("MF_2", "会员标识:优质租客"),

    INSURANCE_CLIENT("6", "保险公司客户"),
    INVITE_FRIENDS("T_2", "邀请还有"),
    MEMBER_LEVEL("T_4", "会员的等级"),
    OTA_MEM("5", "OTA会员"),

    STAFF("3", "内部员工"),

    SUCCESS_RENTCAR("T_3", "成功租车次数"),
    TD("MF_5", "会员标识:免TD用户"),
    VIP("MF_4", "会员标识:VIP");
    /**
     * 权益编码
     */
    private String rightCode;
    /**
     * 权益名称
     */
    private String rightName;

    OwnerMemRightEnum(String rightCode, String rightName) {
        this.rightCode = rightCode;
        this.rightName = rightName;
    }

}
