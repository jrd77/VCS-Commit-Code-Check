package com.atzuche.order.commons.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 拒单请求参数
 *
 * @author pengcheng.fu
 * @date 2020/1/9 16:47
 */
public class RefuseOrderReqVO extends BaseVO {

    private static final long serialVersionUID = 6959142673221753586L;

    @AutoDocProperty(value = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @AutoDocProperty(value = "操作人")
    private String operatorName;

    @AutoDocProperty(value = "管理后台请求特殊标识:0,否 1,是")
    private Integer isConsoleInvoke;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Integer getIsConsoleInvoke() {
        return isConsoleInvoke;
    }

    public void setIsConsoleInvoke(Integer isConsoleInvoke) {
        this.isConsoleInvoke = isConsoleInvoke;
    }
}