package com.atzuche.order.admin.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class AdminOwnerOrderDetailDTO {
    @AutoDocProperty("预计收益")
    private Integer expIncome;
    @AutoDocProperty("结算收益")
    private Integer settleincome;
    @AutoDocProperty("收益")
    private Integer income;
    @AutoDocProperty("租车费用")
    private Integer rentAmt;


}
