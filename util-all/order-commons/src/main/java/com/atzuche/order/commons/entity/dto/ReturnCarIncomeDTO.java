package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class ReturnCarIncomeDTO{

    @AutoDocProperty("结算方式key,1-按照订单结束时间结算,2-按照实际还车时间结算")
    private String settleFlag;

    @AutoDocProperty(value = "结算方式标题,如 按照订单结束时间结算预期收益")
    private String settleTitle;

    @AutoDocProperty(value = "结算方式提示文案,如预期收益:300元")
    private String expectIncome;

    @AutoDocProperty(value = "是否可选标示,0-否,1-是")
    private String chooseFlag;

}
