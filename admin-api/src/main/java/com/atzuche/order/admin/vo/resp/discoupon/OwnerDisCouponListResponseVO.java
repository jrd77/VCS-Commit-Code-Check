package com.atzuche.order.admin.vo.resp.discoupon;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;


@Data
@ToString
public class OwnerDisCouponListResponseVO implements Serializable {

	@AutoDocProperty(value="车主优惠券列表")
	private List<OwnerDisCouponResponseVO> ownerDisCouponList;

	
	

	
	

}
