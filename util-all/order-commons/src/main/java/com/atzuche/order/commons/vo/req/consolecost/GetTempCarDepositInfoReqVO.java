package com.atzuche.order.commons.vo.req.consolecost;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 获取订单暂扣车辆押金费用明细请求参数
 *
 * @author pengcheng.fu
 * @date 2020/4/23 16:59
 */

@Data
public class GetTempCarDepositInfoReqVO {

    @AutoDocProperty(value = "订单号", required = true)
    @NotBlank(message = "订单号不能为空")
    private String orderNo;
}
