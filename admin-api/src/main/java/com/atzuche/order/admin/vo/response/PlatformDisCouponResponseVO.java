package com.atzuche.order.admin.vo.response;

import com.autoyol.doc.annotation.AutoDocProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 用车的条件
 * 计算费用
 * @author xiaoxu.wang
 *
 *
 */
@Data
@ToString
public class PlatformDisCouponResponseVO implements Serializable{
	@AutoDocProperty(value="抵扣金额")
	private String disAmt;//	减(元)
	@AutoDocProperty(value="优惠券标题")
	private String disName;//优惠券标题
	@AutoDocProperty(value="优惠券id")
	private String disCouponId;//优惠券id
	@AutoDocProperty(value="结束时间")
	private String endDate;//结束时间
	@AutoDocProperty(value="开始时间（yyyyMMddHHmmss）")
	private String startDate;//开始时间（yyyyMMddHHmmss）

	
}
