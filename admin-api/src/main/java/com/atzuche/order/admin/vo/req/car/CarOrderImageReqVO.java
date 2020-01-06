package com.atzuche.order.admin.vo.req.car;

import com.autoyol.doc.annotation.AutoDocProperty;

import javax.validation.constraints.NotNull;

/***
 * 车辆订单图片（订单照片）
 */
public class CarOrderImageReqVO implements java.io.Serializable{
    @AutoDocProperty(value="订单编号,必填")
    @NotNull(message="订单编号不能为空")
    private String orderNo;
}
