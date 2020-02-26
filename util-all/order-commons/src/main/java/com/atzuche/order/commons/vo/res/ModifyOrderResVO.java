/**
 * 
 */
package com.atzuche.order.commons.vo.res;

import java.util.List;

import com.atzuche.order.commons.vo.res.cost.RenterOrderCostDetailResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderDeliveryResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderFineDeatailResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderSubsidyDetailResVO;

import lombok.Data;

/**
 * @author jing.huang
 * 管理后台修改展示
 */
@Data
public class ModifyOrderResVO {
	/**
	 * 租客罚金列表
	 */
	private List<RenterOrderFineDeatailResVO> fineLst;
	/**
	 * 取车配送
	 */
	private RenterOrderDeliveryResVO renterOrderDeliveryGet;
	/**
	 * 还车配送
	 */
	private RenterOrderDeliveryResVO renterOrderDeliveryReturn;
	/**
	 * 租客费用明细列表
	 */
	private List<RenterOrderCostDetailResVO> renterOrderCostDetailList;
	
	/**
	 * 抵扣明细
	 */
	List<RenterOrderSubsidyDetailResVO> subsidyLst;
}
