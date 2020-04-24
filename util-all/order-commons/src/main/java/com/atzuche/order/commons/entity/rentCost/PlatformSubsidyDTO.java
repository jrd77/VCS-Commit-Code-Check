package com.atzuche.order.commons.entity.rentCost;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PlatformSubsidyDTO {
    @AutoDocProperty("升级车辆补贴")
    public Integer updateCarSubsidy = 0;
    @AutoDocProperty("油费补贴")
    public Integer oilSubsidySubsidy = 0;
    @AutoDocProperty("洗车费补贴")
    public Integer carWashSubsidy = 0;
    @AutoDocProperty("取还车迟到补贴")
    public Integer deliveryLateSubsidy = 0;
    @AutoDocProperty("延时补贴")
    public Integer delaySubsidy = 0;
    @AutoDocProperty("交通费补贴")
    public Integer carFeeSubsidy = 0;
    @AutoDocProperty("基础保障费补贴")
    public Integer premiumSubsidy = 0;
    @AutoDocProperty("全面保障费补贴")
    public Integer comprehensiveEnsureSubsidy = 0;
    @AutoDocProperty("租金补贴")
    public Integer rentSubsidy = 0;
    @AutoDocProperty("手续费补贴")
    public Integer ServiceSubsidy = 0;
    @AutoDocProperty("其他补贴")
    public Integer otherSubsidy = 0;
    @AutoDocProperty(value = "长租-取还车费用补贴")
    public Integer longGetReturnCarCostSubsidy;

}
