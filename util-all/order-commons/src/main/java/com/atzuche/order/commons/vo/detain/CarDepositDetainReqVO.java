package com.atzuche.order.commons.vo.detain;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * 违章押金暂扣处理请求参数
 *
 * @author pengcheng.fu
 * @date 2020/4/28 14:39
 */

@Data
public class CarDepositDetainReqVO {

    @AutoDocProperty(value = "订单号", required = true)
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @AutoDocProperty(value = "操作人", required = true)
    private String operator;

    @AutoDocProperty(value = "风控-暂扣车辆押金 0-否 1-是", required = true)
    @NotBlank(message = "风控-暂扣车辆押金不能为空")
    private String fkDetainFlag;

    @AutoDocProperty(value = "风控-暂扣原因")
    private String fkDetainReason;

    @AutoDocProperty(value = "交易-暂扣车辆押金 0-否 1-是", required = true)
    @NotBlank(message = "交易-暂扣车辆押金不能为空")
    private String jyDetainFlag;

    @AutoDocProperty(value = "交易-暂扣原因")
    private String jyDetainReason;

    @AutoDocProperty(value = "理赔-暂扣车辆押金 0-否 1-是", required = true)
    @NotBlank(message = "理赔-暂扣车辆押金不能为空")
    private String lpDetainFlag;

    @AutoDocProperty(value = "理赔-暂扣原因")
    private String lpDetainReason;

    @AutoDocProperty(value = "车辆押金暂扣状态 1：暂扣 2：取消暂扣", required = true)
    @NotNull(message = "车辆押金暂扣状态不能为空")
    private Integer detainStatus;

}
