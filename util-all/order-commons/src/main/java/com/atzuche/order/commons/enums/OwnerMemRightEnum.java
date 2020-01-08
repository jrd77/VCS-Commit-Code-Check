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
    HIGH_QUALITY_OWNER("1","优质车主"),
    HIGH_QUALITY_RENTER("2","优质租客"),
    HIGH_QUALITY_OWNER_RENTER("3","优质租客+优质车主"),
    VIP("4","VIP"),
    TD("5","免TD用户"),

    //任务
    BIND_WECHAT("1","绑定微信"),
    INVITE_FRIENDS("2","邀请好友"),
    SUCCESS_RENTCAR("3","成功租车次数"),
    MEMBER_LEVEL("4","会员的等级"),

    STAFF("3","内部员工"),

    CPIC_MEM("4","太保会员"),
    OTA_MEM("5","OTA会员"),
    INSURANCE_CLIENT("6","保险公司客户");

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
