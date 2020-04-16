package com.atzuche.order.admin.vo.resp.discoupon;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 用车的条件
 * 计算费用
 * @author xiaoxu.wang
 *
 *
 */
@Data
@ToString
public class PlatformDisCouponListResponseVO implements Serializable{
	@AutoDocProperty(value="优惠券列表")
	private List<PlatformDisCouponResponseVO> platformDisCouponList;


	
}