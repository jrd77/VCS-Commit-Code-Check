package com.atzuche.order.commons.enums;

import lombok.Getter;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/14 3:20 下午
 **/
@Getter
public enum OwnerMemRightEnum {
    // member_flag null comment '会员标识 1:优质车主, 2:优质租客, 3:优质租客+优质车主,4:VIP ,5.免TD用户 '
    VIP("1","会员标识"),
    STAFF("2","内部员工"),
    MEM_LEVEL("3","会员等级"),
    CPIC_MEM("4","太保会员"),
    OTA_MEM("5","OTA会员"),
    INSURANCE_CLIENT("6","保险公司客户");
    ;
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

    public int deposit(int depositAmt){
        //是否免押判断

        return depositAmt;
    }


}
