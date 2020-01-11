package com.atzuche.order.admin.vo.resp.cost;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 平台补贴
 */
@Data
@ToString
public class PlatformSubsidyVO {

    @AutoDocProperty("平台给租客的补贴")
    private String platformRenterSubsidy;
    @AutoDocProperty("平台实际给租客的补贴")
    private String platformRenterRealSubsidy;
    @AutoDocProperty("升级车辆补贴")
    private String updateCarSubsidy;
    @AutoDocProperty("油费补贴")
    private String oilSubsidySubsidy;
    @AutoDocProperty("洗车费补贴")
    private String carWashSubsidy;
    @AutoDocProperty("取还车迟到补贴")
    private String deliveryLateSubsidy;
    @AutoDocProperty("延时补贴")
    private String delaySubsidy;
    @AutoDocProperty("交通费补贴")
    private String carFeeSubsidy;
    @AutoDocProperty("保费补贴")
    private String premiumSubsidy;
    @AutoDocProperty("租金补贴")
    private String rentSubsidy;
    @AutoDocProperty("其他补贴")
    private String otherSubsidy;
    @AutoDocProperty("子订单编号")
    private String renterOrderNo;
}
