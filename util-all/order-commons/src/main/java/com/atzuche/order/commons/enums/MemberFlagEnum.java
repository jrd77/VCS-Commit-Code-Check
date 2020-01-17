package com.atzuche.order.commons.enums;

import lombok.Getter;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum MemberFlagEnum {
    HIGH_QUALITY_OWNER(1,"1","会员标识:优质车主"),
    HIGH_QUALITY_RENTER(2,"2","会员标识:优质租客"),
    HIGH_QUALITY_OWNER_RENTER(3,"3","会员标识:优质租客+优质车主"),
    VIP(4,"4","会员标识:VIP"),
    TD(5,"5","会员标识:免TD用户");


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

    MemberFlagEnum(int index, String rightCode, String rightName) {
        this.index = index;
        this.rightCode = rightCode;
        this.rightName = rightName;
    }

    public static MemberFlagEnum getRightByIndex(int index){
        return Stream.of(MemberFlagEnum.values())
                .filter(x->x.getIndex() == index)
                .limit(1)
                .collect(Collectors.toList())
                .get(0);
    }
}
