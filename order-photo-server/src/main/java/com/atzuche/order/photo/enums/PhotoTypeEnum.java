package com.atzuche.order.photo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PhotoTypeEnum {
    GET_CAR(1,"取车"),
    RETURN_CAR(2,"还车"),
    GET_OIL_METER(6,"取车-油表"),
    GET_HAND_CAR_LIST(7,"   取车-交接车单子"),
    GET_CAR_SERVICE_VOUCHER(8,"取车-服务凭证"),
    RETURN_CAR_SERVICE_VOUCHER(9,"还车-服务凭证"),
    RETURN_HAND_CAR_LIST(10,"还车-交接车单子"),
    RETURN_OIL_METER(11,"还车-油表"),
    ;
   private int type;

   private String desc;

}
