package com.atzuche.order.admin.vo.req.renterWz;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * TemporaryRefundReqVO
 *
 * @author shisong
 * @date 2020/1/6
 */
@Data
@ToString
public class TemporaryRefundReqVO {

    @AutoDocProperty("订单号")
    private String orderNo;

    @AutoDocProperty("暂扣返还金额")
    private String amount;

    @AutoDocProperty("暂扣返还原因")
    private String reason;

}
