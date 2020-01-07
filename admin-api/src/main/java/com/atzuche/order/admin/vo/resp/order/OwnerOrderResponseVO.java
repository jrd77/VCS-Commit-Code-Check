package com.atzuche.order.admin.vo.resp.order;

import com.autoyol.doc.annotation.AutoDocProperty;
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
public class OwnerOrderResponseVO implements Serializable{
	@AutoDocProperty(value="子订单编号")
	private String renterOrderNo;

	@AutoDocProperty(value="车主姓名")
	private String name;

	@AutoDocProperty(value="车牌号")
	private String plateNum;

	@AutoDocProperty(value="起租时间")
	private String rentTime;

	@AutoDocProperty(value="结束时间")
	private String revertTime;

	@AutoDocProperty(value="订单状态")
	private String status;

}
