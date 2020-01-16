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
public class PlatformToRenterSubsidyResVO {
	@AutoDocProperty(value="升级车辆补贴")
    String dispatchingSubsidy;
	@AutoDocProperty(value="油费补贴")
    String oilSubsidy;
	@AutoDocProperty(value="洗车费补贴")
    String cleanCarSubsidy;
	@AutoDocProperty(value="取还车迟到补贴")
    String getReturnDelaySubsidy;
	@AutoDocProperty(value="延时补贴")
    String delaySubsidy;
	@AutoDocProperty(value="交通费补贴")
    String trafficSubsidy;
	@AutoDocProperty(value="保费补贴")
    String insureSubsidy;
	@AutoDocProperty(value="租金补贴")
    String rentAmtSubsidy;
	@AutoDocProperty(value="减免前车辆押金")
    String otherSubsidy;
	
}
