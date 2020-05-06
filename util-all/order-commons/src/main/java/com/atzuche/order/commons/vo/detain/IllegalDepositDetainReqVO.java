package com.atzuche.order.commons.vo.detain;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 违章押金暂扣处理请求参数
 *
 * @author pengcheng.fu
 * @date 2020/4/2814:06
 */

@Data
public class IllegalDepositDetainReqVO {

    @AutoDocProperty("订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @AutoDocProperty("违章押金暂扣状态 1：暂扣 2：取消暂扣")
    @NotBlank(message = "违章押金暂扣状态不能为空")
    private Integer detainStatus;

    @AutoDocProperty(value = "操作人")
    private String operatorName;

}
