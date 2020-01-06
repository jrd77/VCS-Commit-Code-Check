package com.atzuche.order.admin.vo.resp.income;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 收益
 */
@Data
@ToString
public class BaseIncomeVO {
    @AutoDocProperty("车主租金")
    private String ownerAmt;
    @AutoDocProperty("违约罚金")
    private String payToPlatFormFee;
    @AutoDocProperty("租客车主互相调价")
    private String renterOWnerAdjustmentFee;
    @AutoDocProperty("超里程费用")
    private String extraMileageFee;
    @AutoDocProperty("油费")
    private String oilFee;
    @AutoDocProperty("加油服务费")
    private String oilServiceFee;

}
