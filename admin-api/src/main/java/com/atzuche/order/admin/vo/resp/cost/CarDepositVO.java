package com.atzuche.order.admin.vo.resp.cost;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 车辆押金
 */
@Data
@ToString
public class CarDepositVO {

    @AutoDocProperty("应收")
    private String receiveFee;
    @AutoDocProperty("实收")
    private String realFee;
    @AutoDocProperty("应退/应扣")
    private String refund;
    @AutoDocProperty("实退/实扣")
    private String realFund;
    @AutoDocProperty("车辆押金")
    private String carDeposit;
    @AutoDocProperty("平台任务减免")
    private String platformTaskRelief;
    @AutoDocProperty("子订单编号")
    private String renterOrderNo;

}
