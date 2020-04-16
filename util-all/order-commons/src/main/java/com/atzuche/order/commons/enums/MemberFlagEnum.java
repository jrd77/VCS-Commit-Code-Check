package com.atzuche.order.commons.enums;

import lombok.Getter;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum MemberFlagEnum {
    HIGH_QUALITY_OWNER(1,"1","优质车主"),
    HIGH_QUALITY_RENTER(2,"2","优质租客"),
    HIGH_QUALITY_OWNER_RENTER(3,"3","优质租客+优质车主"),
    VIP(4,"4","VIP"),
    QYYH(5,"5","企业用户"),
    DBCZH(6,"6","代步车账号"),
    TD(7,"7","免TD用户"),
    SZTBYG(8,"8","深证太保员工"),
    ZXYH(9,"9","已注销用户");

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