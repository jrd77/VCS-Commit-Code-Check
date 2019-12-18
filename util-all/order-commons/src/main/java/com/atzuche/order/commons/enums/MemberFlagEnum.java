package com.atzuche.order.commons.enums;

import lombok.Getter;
/*
 * @Author ZhangBin
 * @Date 2019/12/18 15:32
 * @Description: 会员标识枚举类
 *
 **/
@Getter
public enum MemberFlagEnum {
   // member_flag null comment '会员标识 1:优质车主, 2:优质租客, 3:优质租客+优质车主,4:VIP ,5.免TD用户 ',
    high_quality_owner(1,"member_flag","会员标识","会员标识:优质车主"),
    high_quality_renter(2,"member_flag","会员标识","会员标识:优质租客"),
    high_quality_owner_renter(3,"member_flag","会员标识","会员标识:优质租客+优质车主"),
    vip(4,"member_flag","会员标识","会员标识-VIP"),
    td(5,"member_flag","会员标识","免TD用户")
    ;

    MemberFlagEnum(int index, String rightCode, String rightName, String rightDesc) {
        this.index = index;
        this.rightCode = rightCode;
        this.rightName = rightName;
        this.rightDesc = rightDesc;
    }

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
    /**
     * 权益描述
     */
    private String rightDesc;


}
