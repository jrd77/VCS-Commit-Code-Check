package com.atzuche.order.delivery.vo.handover;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import java.io.Serializable;

/**
 * @author 胡春林
 * 油耗里程数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MileageOilVO implements Serializable {

    private static final long serialVersionUID = -805546047970619649L;
    @ApiModelProperty(value="订单号")
    private String orderNo;

    @ApiModelProperty(value="里程数,如:12000")
    private String mileage;
    @ApiModelProperty(value="油量刻度分子,如：0-16,0表示空油")
    private String oilScale;
    @ApiModelProperty(value="取还车类型类型,1:表示取车，2:表示还车")
    private String type;
    @ApiModelProperty(value="油量刻度分母,如：16/20")
    private String oilScaleDenominator;
}
