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
public class MainOrderResponseVO implements Serializable{
	@AutoDocProperty(value="订单编号")
	private String orderNo;

	@AutoDocProperty(value="订单状态")
	private String status;

	@AutoDocProperty(value="状态变更时间")
	private String changeTime;

	@AutoDocProperty(value="起租时间")
	private String rentTime;

	@AutoDocProperty(value="结束时间")
	private String revertTime;

	@AutoDocProperty(value="总租期（单位小时）")
	private String totalHour;

	@AutoDocProperty(value="订单类型：线上/线下")
	private String orderType;

	@AutoDocProperty(value="订单来源")
	private String orderSource;

}
