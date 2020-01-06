package com.atzuche.order.admin.vo.req.cost;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author 胡春林
 * 租车费用接口参数
 */
@Data
@ToString
public class RentalCostReqVO {

    @ApiModelProperty(value="子订单号",required=true)
    @NotBlank(message="renterOrderNo不能为空")
    private String renterOrderNo;
    @ApiModelProperty(value="子订单状态",required=true)
    @NotBlank(message="renterOrderStatus不能为空")
    private Integer renterOrderStatus;


}
