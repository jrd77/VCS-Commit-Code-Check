package com.atzuche.order.admin.vo.resp.income;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 租车收益
 */
@Data
@ToString
public class OwnerInComeRepVO {

    @AutoDocProperty("服务费")
    private String estimatedRevenue;
    @AutoDocProperty("收益")
    private BaseIncomeVO baseIncomeVO;
    @AutoDocProperty("扣款")
    private BaseDeductionVO baseDeductionVO;
    @AutoDocProperty("车主券")
    private String ownerCoupon;
    @AutoDocProperty("车主券实际抵扣金额")
    private String ownerRealCouponAmt;
    @AutoDocProperty("平台给车主的补贴")
    private String platFormToOwnerSubsidy;

}
