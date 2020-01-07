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
public class OrderStatusResponseVO implements Serializable{

	@AutoDocProperty(value="订单状态描述")
	private String statusDescription;

	@AutoDocProperty(value="状态变更时间")
	private String changeTime;

	@AutoDocProperty(value="来源")
	private String source;


}
