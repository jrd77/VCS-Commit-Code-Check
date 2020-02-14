package com.atzuche.order.admin.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/13 7:52 下午
 **/
public class FienAmtUpdateReqVO {
    @NotBlank(message = "订单号不能为空")
//    @NotNull(message = "订单号不能为空")
    @AutoDocProperty("主订单号")
    private String orderNo;
    
    @NotBlank(message = "车主子订单号不能为空")
//    @NotNull(message = "车主子订单号不能为空")
    @AutoDocProperty("车主子单号")
    private String ownerOrderNo;
    
//    @NotNull(message="车主会员号不能为空")
    @NotBlank(message="车主会员号不能为空")
    @AutoDocProperty("车主会员号")
    private String ownerMemNo;
    
    @AutoDocProperty("车主取还车违约金")
//    @NotNull(message = "车主取还车违约金不能为空")
    @NotBlank(message="车主取还车违约金不能为空")
    private Integer ownerGetReturnCarFienAmt;
    
    @AutoDocProperty("车主修改地址费用")
//    @NotNull(message = "车主修改地址费用不能为空")
    @NotBlank(message="车主修改地址费用不能为空")
    private Integer ownerModifyAddrAmt;
}
