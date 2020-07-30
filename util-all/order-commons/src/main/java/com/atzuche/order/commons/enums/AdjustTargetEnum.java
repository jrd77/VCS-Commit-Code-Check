package com.atzuche.order.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdjustTargetEnum {
    RENTER_TO_OWNER(1,"租客给车主调价"),
    OWNER_TO_RENTER(2,"车主给租客调价");

    private int type;

    private String desc;
}
