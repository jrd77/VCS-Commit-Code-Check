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

    @AutoDocProperty(value="升级车辆补贴（系统自动计算）")
	String dispatchingSubsidySystem;
	
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
	
	
	@AutoDocProperty(value="基础保障费补贴")
    String insureSubsidy;
    @AutoDocProperty(value="基础保障费补贴（系统自动计算）")
    String insureSubsidySystem;

	@AutoDocProperty(value="租金补贴")
    String rentAmtSubsidy;
	@AutoDocProperty(value="其他补贴")
    String otherSubsidy;
	@AutoDocProperty(value="全面保障服务费补贴")
    String abatementSubsidy;
    @AutoDocProperty(value="全面保障服务费补贴（系统自动计算）")
    String abatementSubsidySystem;
	@AutoDocProperty(value="手续费补贴")
    String feeSubsidy;
    @AutoDocProperty(value = "长租-取还车费用补贴")
    String longGetReturnCarCostSubsidy;
}
