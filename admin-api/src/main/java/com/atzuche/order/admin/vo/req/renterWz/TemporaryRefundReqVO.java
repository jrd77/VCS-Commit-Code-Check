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
public class TemporaryRefundReqVO {

    @AutoDocProperty("订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @AutoDocProperty("违章押金暂扣状态 1：暂扣 2：取消暂扣")
    private Integer detainStatus;
}
