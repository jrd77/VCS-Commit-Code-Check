package com.atzuche.order.admin.vo.req.renterWz;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

/**
 * TemporaryRefundReqVO
 *
 * @author shisong
 * @date 2020/1/6
 */
@Data
@ToString
public class CarDepositTemporaryRefundReqVO {

    @AutoDocProperty(value = "订单号", required = true)
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @AutoDocProperty(value = "操作人", hidden = true)
    private String operator;

    @AutoDocProperty(value = "风控-暂扣车辆押金 0-否 1-是")
    @NotBlank(message = "风控-暂扣车辆押金不能为空")
    private String fkDetainFlag;

    @AutoDocProperty(value = "风控-暂扣原因")
    private String fkDetainReason;

    @AutoDocProperty(value = "交易-暂扣车辆押金 0-否 1-是")
    @NotBlank(message = "交易-暂扣车辆押金不能为空")
    private String jyDetainFlag;

    @AutoDocProperty(value = "交易-暂扣原因")
    private String jyDetainReason;

    @AutoDocProperty(value = "理赔-暂扣车辆押金 0-否 1-是")
    @NotBlank(message = "理赔-暂扣车辆押金不能为空")
    private String lpDetainFlag;

    @AutoDocProperty(value = "理赔-暂扣原因")
    private String lpDetainReason;


}
