package com.atzuche.order.commons.vo.res;

import com.atzuche.order.commons.vo.res.rentcosts.RenterOrderCostDetailEntity;
import lombok.Data;

/**
 * 租客费用信息
 */
@Data
public class RentCosts {
    /**
     * 查询租车费用
     */
//    private List<RenterOrderCostDetailEntity> renterOrderCostDetails;

    /**	
     * 交接车-油费
     */
    private RenterOrderCostDetailEntity oilAmt;

    /**
     * 交接车-获取超里程费用
     */
    private RenterOrderCostDetailEntity mileageAmt;

//    /**
//     * 补贴
//     */
//    private List<RenterOrderSubsidyDetailEntity> renterOrderSubsidyDetails;
//
//    /**
//     * 租客罚金
//     */
//    private List<RenterOrderFineDeatailEntity> renterOrderFineDeatails;
//
//    /**
//     * 管理后台补贴
//     */
//    private List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails;
//
//    /**
//     * 获取全局的租客订单罚金明细
//     */
//    private List<ConsoleRenterOrderFineDeatailEntity> consoleRenterOrderFineDeatails;
}