package com.atzuche.order.settle.vo.req;

import java.util.List;

import com.atzuche.order.renterwz.entity.RenterOrderWzCostDetailEntity;

import lombok.Data;

/**
 * 租客费用信息
 */
@Data
public class RentCostsWz {
    /**
     * 查询租车费用(违章)
     */
    private List<RenterOrderWzCostDetailEntity> renterOrderWzCostDetails;

//    /**
//     * 交接车-油费
//     */
//    private RenterGetAndReturnCarDTO oilAmt;
//
//    /**
//     * 交接车-获取超里程费用
//     */
//    private FeeResult mileageAmt;

    /**
     * 补贴
     */
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
