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
public class ReductionTaskResVO {
	@AutoDocProperty(value="减免项目名称")
	private String reductionItemName;
	
	@AutoDocProperty(value="减免规则")
	private String reductionItemRule;
	
	@AutoDocProperty(value="已获得减免比例")
	private String reductionItemGetRatio;
	
	@AutoDocProperty(value="本订单减免比例")
	private String reductionOrderRatio;
	
}
