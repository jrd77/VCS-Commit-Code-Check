package com.atzuche.order.commons.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * 订单取消入参
 *
 * @author pengcheng.fu
 * @date 2020/1/7 11:55
 */
public class CancelOrderReqVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = 6012677342611619028L;

    @AutoDocProperty(value = "订单号", required = true)
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @AutoDocProperty(value = "使用角色:1.车主 2.租客", required = true)
    @NotBlank(message = "使用角色不能为空")
    private String memRole;

    @AutoDocProperty(value = "取消原因", required = true)
    @NotBlank(message = "取消原因不能为空")
    private String cancelReason;

    @AutoDocProperty(value = "操作人")
    private String operatorName;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getMemRole() {
        return memRole;
    }

    public void setMemRole(String memRole) {
        this.memRole = memRole;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
}
