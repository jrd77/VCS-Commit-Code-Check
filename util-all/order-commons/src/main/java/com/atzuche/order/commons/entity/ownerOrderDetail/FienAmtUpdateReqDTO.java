package com.atzuche.order.commons.entity.ownerOrderDetail;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class FienAmtUpdateReqDTO {
    @NotBlank(message = "订单号不能为空")
    @NotNull(message = "订单号不能为空")
    @AutoDocProperty("主订单号")
    private String orderNo;
    @NotBlank(message = "子订单号不能为空")
    @NotNull(message = "子订单号不能为空")
    @AutoDocProperty("车主子单号")
    private String ownerOrderNo;
    @AutoDocProperty("车主取还车违约金")
    @NotBlank(message = "车主取还车违约金不能为空")
    @NotNull(message = "车主取还车违约金不能为空")
    private Integer ownerGetReturnCarFienAmt;
    @AutoDocProperty("车主取还车违约金编码")
    @NotBlank(message = "车主取还车违约金编码不能为空")
    @NotNull(message = "车主取还车违约金编码不能为空")
    private String ownerGetReturnCarFienCashNo;
    @AutoDocProperty("车主修改地址费用")
    @NotBlank(message = "车主修改地址费用不能为空")
    @NotNull(message = "车主修改地址费用不能为空")
    private Integer ownerModifyAddrAmt;
    @AutoDocProperty("车主取还车违约金编码")
    @NotBlank(message = "车主取还车违约金编码不能为空")
    @NotNull(message = "车主取还车违约金编码不能为空")
    private String ownerModifyAddrAmtCashNo;
}
