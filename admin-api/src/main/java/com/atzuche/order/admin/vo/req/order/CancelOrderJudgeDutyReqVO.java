package com.atzuche.order.admin.vo.req.order;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author pengcheng.fu
 * @date 2020/2/26 18:17
 */

@Data
public class CancelOrderJudgeDutyReqVO {

    @AutoDocProperty(value = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @AutoDocProperty(value = "租客订单号", required = true)
    @NotBlank(message = "租客订单号不能为空")
    private String renterOrderNo;

    @AutoDocProperty(value = "车主订单号", required = true)
    @NotBlank(message = "车主订单号不能为空")
    private String ownerOrderNo;

    @AutoDocProperty(value = "责任方:1,租客责任 2,车主责任 6,双方无责、平台承担保险", required = true)
    @NotBlank(message = "责任方不能为空")
    private String wrongdoer;

}
