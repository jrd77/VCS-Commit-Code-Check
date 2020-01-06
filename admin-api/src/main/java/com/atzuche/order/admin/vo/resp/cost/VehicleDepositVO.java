package com.atzuche.order.admin.vo.resp.cost;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 违章押金
 */
@Data
@ToString
public class VehicleDepositVO {

    @AutoDocProperty("应收")
    private String receiveFee;
    @AutoDocProperty("实收")
    private String realFee;
    @AutoDocProperty("应退/应扣")
    private String refund;
    @AutoDocProperty("实退/实扣")
    private String realFund;
    @AutoDocProperty("违章押金")
    private String vehicleDeposit;
    @AutoDocProperty("子订单编号")
    private String renterOrderNo;
}
