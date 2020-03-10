package com.atzuche.order.commons.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 申诉请求参数
 *
 * @author pengcheng.fu
 * @date 2020/3/2 14:17
 */
public class OrderCancelAppealReqVO extends BaseVO {

    private static final long serialVersionUID = 4633480559698170543L;


    @AutoDocProperty(value = "订单号", required = true)
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @AutoDocProperty(value = "使用角色:1.车主 2.租客", required = true)
    @NotBlank(message = "使用角色不能为空")
    private String memRole;

    @AutoDocProperty(value = "申诉原因", required = true)
    @NotBlank(message = "申诉原因不能为空")
    private String appealReason;


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

    public String getAppealReason() {
        return appealReason;
    }

    public void setAppealReason(String appealReason) {
        this.appealReason = appealReason;
    }
}
