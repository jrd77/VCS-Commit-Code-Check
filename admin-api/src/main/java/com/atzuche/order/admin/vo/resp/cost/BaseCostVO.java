package com.atzuche.order.admin.vo.resp.cost;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 基础费用
 */
@Data
@ToString
public class BaseCostVO {

    @AutoDocProperty("租客租金")
    private String renterAmt;
    @AutoDocProperty("基础保障费")
    private String basicGuaranteeFee;
    @AutoDocProperty("手续费")
    private String chargeFee;
    @AutoDocProperty("全面保障服务费")
    private String allGuaranteeFee;
    @AutoDocProperty("附加驾驶员险")
    private String driverInsurance;
    @AutoDocProperty("配送费用")
    private String distributionCost;
    @AutoDocProperty("违约罚金")
    private String penaltyBreachContract;
    @AutoDocProperty("租客需支付给平台的费用")
    private String payToPlatFormFee;
    @AutoDocProperty("租客车主互相调价")
    private String renterOWnerAdjustmentFee;
    @AutoDocProperty("超里程费用")
    private String extraMileageFee;
    @AutoDocProperty("油费")
    private String oilFee;
    @AutoDocProperty("加油服务费")
    private String oilServiceFee;
    @AutoDocProperty("子订单编号")
    private String renterOrderNo;



}
