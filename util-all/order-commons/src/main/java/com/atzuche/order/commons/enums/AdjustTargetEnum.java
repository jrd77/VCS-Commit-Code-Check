package com.atzuche.order.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdjustTargetEnum {
    OWNER_TO_RENTER(1,"车主给租客调价"),
    RENTER_TO_OWNER(2,"租客给车主调价");


    private int type;

    private String desc;
}
