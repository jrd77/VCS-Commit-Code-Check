package com.atzuche.order.admin.vo.resp.discoupon;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;


@Data
@ToString
public class OwnerDisCouponResponseVO implements Serializable {

	@AutoDocProperty(value="优惠券id")
	private String disCouponId;

	@AutoDocProperty(value="满减条件金额")
	private String condAmount;

	@AutoDocProperty(value="抵扣金额")
	private String discount;

	@AutoDocProperty(value="有效开始时间")
	private String validBeginTime;

	@AutoDocProperty(value="有效结束时间")
	private String validEndTime;

	@AutoDocProperty(value="优惠券名称")
	private String couponName;
	
	

	
	

}
