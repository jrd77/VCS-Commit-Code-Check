package com.atzuche.order.rentercost.entity;

import lombok.Data;

@Data
public class HttpResult {
    private String resCode;
    private String resMsg;
    private Object data;
}
