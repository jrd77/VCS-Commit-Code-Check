package com.atzuche.order.admin.vo.req.car;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString
public class CarOrderReqVO {
    @AutoDocProperty(value="orderNo,必填")
    @NotNull(message="订单号不能为空")
    private Integer orderNo;
    @AutoDocProperty(value="memNo,必填")
    @NotNull(message="会员号不能为空")
    private String memNo;


}