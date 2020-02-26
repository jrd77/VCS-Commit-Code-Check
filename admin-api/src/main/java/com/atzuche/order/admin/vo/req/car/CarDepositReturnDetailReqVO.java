package com.atzuche.order.admin.vo.req.car;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CarDepositReturnDetailReqVO implements java.io.Serializable {
    @AutoDocProperty(value="订单编号，必填")
    @NotNull(message="订单编号不能为空")
    private String orderNo;

    @AutoDocProperty(value="返还金额，必填")
    @NotNull(message="返还金额不能为空")
    private Integer monty;

    @AutoDocProperty(value="返还备注")
    private String remark;

}
