package com.atzuche.order.commons.entity.ownerOrderDetail;

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
    @AutoDocProperty("车主租金")
    private Integer rentAmt;
    @AutoDocProperty("车主租金明细")
    private OwnerRentDetailDTO ownerRentDetailDTO;
    @AutoDocProperty("违约罚金")
    private Integer fienAmt;
    @AutoDocProperty("违约罚金明细")
    private FienAmtDetailDTO fienAmtDetailDTO;

    @AutoDocProperty("车主租客互相调价")
    private Integer ownerRenterPrice;
    @AutoDocProperty("租客对车主的调价（明细）")
    private Integer renterToOwnerPrice;
    @AutoDocProperty("车主对租客的调价（明细）")
    private Integer ownerToRenterPrice;

    @AutoDocProperty("超里程费用")
    private Integer mileageAmt;
    @AutoDocProperty("油费")
    private Integer oilAmt;
    @AutoDocProperty("加油服务费")
    private Integer oilServiceAmt;

    @AutoDocProperty("扣款")
    private Integer deductionAmt;
    @AutoDocProperty("服务费")
    private Integer serviceAmt;
    @AutoDocProperty("服务费明细")
    private ServiceDetailDTO serviceDetailDTO;

    @AutoDocProperty("平台加油服务费")
    private Integer platformOilServiceAmt;
    @AutoDocProperty("车主需要支付给平台的费用")
    private Integer ownerPayPlatformAmt;
    @AutoDocProperty("GPS服务费")
    private Integer gpsServiceAmt;
    @AutoDocProperty("GPS押金费用")
    private Integer gpsDepositAmt;
    @AutoDocProperty("配送费用")
    private Integer deliveryAmt;

    @AutoDocProperty("给租客的优惠")
    private Integer renterDiscountAmt;
    @AutoDocProperty("车主券名称")
    private Integer ownerCouponName;
    @AutoDocProperty("车主券金额")
    private Integer ownerCouponAmt;

    @AutoDocProperty("平台补贴")
    private Integer platformSubsidyAmt;
    @AutoDocProperty("平台对车主的补贴")
    private Integer platformToOwnerSubsidyAmt;
    @AutoDocProperty("平台给车主的补贴明细")
    private PlatformToOwnerSubsidyDTO platformToOwnerSubsidyDTO;

}
