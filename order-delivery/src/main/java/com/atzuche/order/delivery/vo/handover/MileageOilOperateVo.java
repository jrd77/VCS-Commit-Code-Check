package com.atzuche.order.delivery.vo.handover;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import java.io.Serializable;

/**
 * @author 胡春林
 * 仁云油耗数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MileageOilOperateVo implements Serializable {

    private static final long serialVersionUID = -805546047970619649L;
    @ApiModelProperty(value="订单号,必填")
    private String orderNo;

    @ApiModelProperty(value="里程数,如:12000")
    private String kms;
    @ApiModelProperty(value="油量,5.8之前油量刻度传小数（当前油量刻度/油表总刻度（固定16）），5.8之后传整数（当前油量刻度）")
    private String oil;
    @ApiModelProperty(value="服务类型（take:取车服务 back:还车服务）")
    private String stype;
    @ApiModelProperty(value="流程步骤（3：取车时,4：送达时）")
    private String btype;
}
