package com.atzuche.order.photo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PhotoTypeEnum {
   GET_CAR(1,"取车"),
   RETURN_CAR(2,"还车"),
   OIL_METER(6,"油表"),
   HAND_CAR_LIST(7,"交接车单子"),
   GET_CAR_SERVICE_VOUCHER(8,"取车-服务凭证"),
   RETURN_CAR_SERVICE_VOUCHER(9,"还车-服务凭证");

   private int type;

   private String desc;

}
