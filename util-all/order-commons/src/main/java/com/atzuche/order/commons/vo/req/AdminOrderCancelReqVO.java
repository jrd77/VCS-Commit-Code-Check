package com.atzuche.order.commons.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 平台取消请求参数
 *
 * @author pengcheng.fu
 * @date 2020/1/15 19:45
 */
public class AdminOrderCancelReqVO extends BaseVO {

    private static final long serialVersionUID = 2111449941876253623L;

    @AutoDocProperty(value = "订单号", required = true)
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @AutoDocProperty(value = "取消原因,如:0,租客审核不通过（风控）1,车载设备有问题 2,车辆已下架 3,租客取消不产生违约金（代步车）4,测试订单（交易）5,机械故障结束订单（交易）6,调度成功重新成单（交易）等",
            required = true)
    @NotBlank(message = "取消原因不能为空")
    private String cancelType;

    @AutoDocProperty(value = "操作人", required = true)
    @NotBlank(message = "操作人不能为空")
    private String operator;


    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCancelType() {
        return cancelType;
    }

    public void setCancelType(String cancelType) {
        this.cancelType = cancelType;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
