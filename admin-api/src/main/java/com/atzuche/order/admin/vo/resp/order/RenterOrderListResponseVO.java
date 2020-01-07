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
public class RenterOrderListResponseVO implements Serializable{

	@AutoDocProperty(value="租客子订单列表")
	private List<RenterOrderListResponseVO> renterOrderList;


}
