package com.atzuche.order.commons.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 车主交车请求参数
 *
 * @author pengcheng.fu
 * @date 2020/1/16 16:47
 */
public class ReturnCarReqVO extends BaseVO {

    private static final long serialVersionUID = -3023946450235330400L;

    @AutoDocProperty(value = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @AutoDocProperty(value = "操作人")
    private String operatorName;
    
    @AutoDocProperty(value="还车时间,格式 yyyyMMddHHmmss")
    private String revertTime;

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

	public String getRevertTime() {
		return revertTime;
	}

	public void setRevertTime(String revertTime) {
		this.revertTime = revertTime;
	}
    
}
