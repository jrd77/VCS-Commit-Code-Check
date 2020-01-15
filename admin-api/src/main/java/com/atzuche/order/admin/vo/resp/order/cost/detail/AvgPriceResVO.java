/**
 * 
 */
package com.atzuche.order.admin.vo.resp.order.cost.detail;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;
import lombok.ToString;

/**
 * @author jing.huang
 *
 */
@ToString
@Data
public class AvgPriceResVO {
	@AutoDocProperty(value="日期")
	private String day;
	
	@AutoDocProperty(value="元")
	private String amt;
	
	@AutoDocProperty(value="小时")
	private String hour;
}
