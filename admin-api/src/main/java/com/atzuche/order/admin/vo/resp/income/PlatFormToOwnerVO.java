package com.atzuche.order.admin.vo.resp.income;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 平台给车主的补贴
 */
@Data
@ToString
public class PlatFormToOwnerVO {

    @AutoDocProperty("超里程补贴")
    private String extraMileageSubsidy;
    @AutoDocProperty("洗车费补贴")
    private String carWashSubsidy;
    @AutoDocProperty("延时补贴")
    private String delaySubsidy;
    @AutoDocProperty("收益补贴")
    private String profitSubsidy;
    @AutoDocProperty("油费补贴")
    private String oilSubsidySubsidy;
    @AutoDocProperty("车上物品损失补贴")
    private String carLostSubsidy;
    @AutoDocProperty("交通费补贴")
    private String carFeeSubsidy;
    @AutoDocProperty("其他补贴")
    private String oherSubsidy;
}
