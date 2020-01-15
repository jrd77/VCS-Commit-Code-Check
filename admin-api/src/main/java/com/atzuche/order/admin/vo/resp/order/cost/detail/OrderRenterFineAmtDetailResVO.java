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
public class OrderRenterFineAmtDetailResVO {
	//租客提前还车罚金  租客延迟还车罚金   租客违约罚金   租客取还车违约金
	@AutoDocProperty(value="租客提前还车罚金")
	private String renterBeforeReturnCarFineAmt;
	
	@AutoDocProperty(value="租客延迟还车罚金")
	private String renterDelayReturnCarFineAmt;
	
	@AutoDocProperty(value="租客违约罚金")
	private String renterFineAmt;
	
	@AutoDocProperty(value="租客取还车违约金")
	private String renterGetReturnCarFineAmt;
	
}
