package com.atzuche.order.admin.vo.resp.cost;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 租客费用明细
 */
@Data
@ToString
public class RentalCostRepVO {


    @AutoDocProperty("应收")
    private BaseCostVO shouldReceiveAmt;
    @AutoDocProperty("实收")
    private BaseCostVO realReceiveAmt;
    @AutoDocProperty("基础费用")
    private BaseCostVO baseCostVO;
    @AutoDocProperty("优惠抵扣")
    private CouponDeductionVO couponDeductionVO;
    @AutoDocProperty("平台补贴")
    private PlatformSubsidyVO platformSubsidyVO;
    @AutoDocProperty("违章押金")
    private VehicleDepositVO vehicleDepositVO;
    @AutoDocProperty("车辆押金")
    private CarDepositVO carDepositVO;
    @AutoDocProperty("补付费用应收")
    private String supplementCost;
    @AutoDocProperty("补付费用实收")
    private String realSupplementCost;
    @AutoDocProperty("子订单编号")
    private String renterOrderNo;





}
