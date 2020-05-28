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

    @AutoDocProperty(value = "轮胎保障服务费补贴")
    private String tyreInsurSubsidy;
    @AutoDocProperty(value = "轮胎保障服务费补贴(系统自动计算)")
    private String tyreInsurSubsidySystem;
    @AutoDocProperty(value = "驾乘无忧保障服务费补贴")
    private String driverInsurSubsidy;
    @AutoDocProperty(value = "驾乘无忧保障服务费补贴(系统自动计算)")
    private String driverInsurSubsidySystem;

	@AutoDocProperty(value="租金补贴")
    String rentAmtSubsidy;
	@AutoDocProperty(value="其他补贴")
    String otherSubsidy;
	@AutoDocProperty(value="补充（全面）保障服务费补贴")
    String abatementSubsidy;
    @AutoDocProperty(value="补充（全面）保障服务费补贴（系统自动计算）")
    String abatementSubsidySystem;
	@AutoDocProperty(value="手续费补贴")
    String feeSubsidy;
    @AutoDocProperty(value = "长租-取还车费用补贴")
    String longGetReturnCarCostSubsidy;

    /*
     * @Author ZhangBin
     * @Date 2020/5/28 13:40
     * @Description: adminlog记录日志用
     *
     **/
    public static void setDefaultValue(PlatformToRenterSubsidyResVO oldData){
        oldData.setDispatchingSubsidy(oldData.getDispatchingSubsidy()==null?"0":oldData.getDispatchingSubsidy());
        oldData.setDispatchingSubsidySystem(oldData.getDispatchingSubsidySystem()==null?"0":oldData.getDispatchingSubsidySystem());
        oldData.setOilSubsidy(oldData.getOilSubsidy()==null?"0":oldData.getOilSubsidy());
        oldData.setCleanCarSubsidy(oldData.getCleanCarSubsidy()==null?"0":oldData.getCleanCarSubsidy());
        oldData.setGetReturnDelaySubsidy(oldData.getGetReturnDelaySubsidy()==null?"0":oldData.getGetReturnDelaySubsidy());
        oldData.setDelaySubsidy(oldData.getDelaySubsidy()==null?"0":oldData.getDelaySubsidy());
        oldData.setTrafficSubsidy(oldData.getTrafficSubsidy()==null?"0":oldData.getTrafficSubsidy());
        oldData.setInsureSubsidy(oldData.getInsureSubsidy()==null?"0":oldData.getInsureSubsidy());
        oldData.setInsureSubsidySystem(oldData.getInsureSubsidySystem()==null?"0":oldData.getInsureSubsidySystem());
        oldData.setTyreInsurSubsidy(oldData.getTyreInsurSubsidy()==null?"0":oldData.getTyreInsurSubsidy());
        oldData.setTyreInsurSubsidySystem(oldData.getTyreInsurSubsidySystem()==null?"0":oldData.getTyreInsurSubsidySystem());
        oldData.setDriverInsurSubsidy(oldData.getDriverInsurSubsidy()==null?"0":oldData.getDriverInsurSubsidy());
        oldData.setDriverInsurSubsidySystem(oldData.getDriverInsurSubsidySystem()==null?"0":oldData.getDriverInsurSubsidySystem());
        oldData.setRentAmtSubsidy(oldData.getRentAmtSubsidy()==null?"0":oldData.getRentAmtSubsidy());
        oldData.setOtherSubsidy(oldData.getOtherSubsidy()==null?"0":oldData.getOtherSubsidy());
        oldData.setAbatementSubsidy(oldData.getAbatementSubsidy()==null?"0":oldData.getAbatementSubsidy());
        oldData.setAbatementSubsidySystem(oldData.getAbatementSubsidySystem()==null?"0":oldData.getAbatementSubsidySystem());
        oldData.setFeeSubsidy(oldData.getFeeSubsidy()==null?"0":oldData.getFeeSubsidy());
        oldData.setLongGetReturnCarCostSubsidy(oldData.getLongGetReturnCarCostSubsidy()==null?"0":oldData.getLongGetReturnCarCostSubsidy());
    }
}
