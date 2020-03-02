package com.atzuche.order.admin.vo.req.order;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 车主同意/拒绝订单请求参数
 *
 * @author pengcheng.fu
 * @date 2020/2/24 10:27
 */

@Data
public class OwnerAgreeOrRefuseOrderReqVO {

    @AutoDocProperty(value = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

}
