/**
 * 
 */
package com.atzuche.order.admin.vo.resp.order.cost.detail;

import java.util.List;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

/**
 * @author jing.huang
 *
 */
@ToString
@Data
public class RentAmtDetailResVO {
	@AutoDocProperty(value="请求时间")
	private String reqTime;
	
	@AutoDocProperty(value="开始时间")
	private String startTime;
	
	@AutoDocProperty(value="结束时间")
	private String endTime;
		
	@AutoDocProperty(value="车牌号")
	private String plateNum;
	
	@AutoDocProperty(value="日均价")
	private String dayAvgPrice;
	@AutoDocProperty(value="计费租期")
	private String rentTotalDay;
	
	@AutoDocProperty(value="租金")
	private String rentAmt;
	
	@AutoDocProperty(value="日均价列表")
	private List<AvgPriceResVO> avgPriceList;
	
}
