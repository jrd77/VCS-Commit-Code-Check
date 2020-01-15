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
public class RenterRentAmtResVO {
	@AutoDocProperty(value="车牌号")
	private String plateNum;
	
	@AutoDocProperty(value="工作日价")
	private String dayPrice;
	@AutoDocProperty(value="周末价")
	private String weekPrice;
	@AutoDocProperty(value="元旦价")
	private String yuandanPrice;
	@AutoDocProperty(value="春节价")
	private String newyearPrice;
	
	@AutoDocProperty(value="日均价明细")
	private List<RentAmtDetailResVO> rentAmtDetailList;
}
