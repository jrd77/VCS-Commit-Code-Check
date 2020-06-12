package com.atzuche.order.renterwz.vo;

import lombok.Data;

@Data
public class WzillegalVO {
    private String illegalTime;
    private String illegalAddr;
    private String illegalReason;
    private Integer illegalFine = 0;
    private String illegalDeduct;
    private Integer illegalStatus;

}
