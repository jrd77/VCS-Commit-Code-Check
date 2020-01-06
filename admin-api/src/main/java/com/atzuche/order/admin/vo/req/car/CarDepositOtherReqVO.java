package com.atzuche.order.admin.vo.req.car;

import com.autoyol.doc.annotation.AutoDocProperty;

import javax.validation.constraints.NotNull;

public class CarDepositOtherReqVO {
//    @AutoDocProperty(value="carNo,必填")
//    @NotNull(message="车辆注册号不能为空")
//    private Integer carNo;
    @AutoDocProperty(value="订单编号,必填")
    @NotNull(message="订单编号不能为空")
    private Integer orderNo;
}
