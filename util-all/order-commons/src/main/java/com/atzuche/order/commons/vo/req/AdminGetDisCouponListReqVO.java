package com.atzuche.order.commons.vo.req;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 获取订单内优惠券抵扣信息请求参数
 *
 * @author pengcheng.fu
 * @date 2020/2/13 12:24
 */
public class AdminGetDisCouponListReqVO implements Serializable {

	private static final long serialVersionUID = 820965410787650039L;

	@NotBlank(message="订单号不能空")
	@Pattern(regexp="\\d*$",message="订单号必须数字")
	private String orderNo;

	@NotBlank(message="租客会员号不能为空")
	@Pattern(regexp="\\d*$",message="租客会员号必须数字")
	private String memNo;


	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getMemNo() {
		return memNo;
	}

	public void setMemNo(String memNo) {
		this.memNo = memNo;
	}
}
