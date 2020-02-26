package com.atzuche.order.admin.vo.resp.order;

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
public class OrderStatusListResponseVO implements Serializable{

	@AutoDocProperty(value="当前状态描述")
	private String statusDescription;

	@AutoDocProperty(value="订单状态流转列表")
	private List<OrderStatusResponseVO> orderStatusList;


	

}
