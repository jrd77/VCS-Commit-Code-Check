package com.atzuche.order.admin.vo.req.cost;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author 胡春林
 * 车主收益参数
 */
@Data
@ToString
public class OwnerInComeReqVO {

    @ApiModelProperty(value="子订单号",required=true)
    @NotBlank(message="ownerOrderNo不能为空")
    private String ownerOrderNo;
    @ApiModelProperty(value="子订单状态",required=true)
    @NotBlank(message="ownerOrderStatus不能为空")
    private Integer ownerOrderStatus;
}
