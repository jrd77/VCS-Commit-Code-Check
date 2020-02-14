/**
 * 
 */
package com.atzuche.order.commons.vo.res;

import java.util.List;

import com.atzuche.order.commons.vo.res.ownercosts.OwnerOrderFineDeatailEntity;
import com.atzuche.order.commons.vo.res.ownercosts.OwnerOrderIncrementDetailEntity;
import com.atzuche.order.commons.vo.res.ownercosts.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.commons.vo.res.ownercosts.OwnerOrderSubsidyDetailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.ConsoleRenterOrderFineDeatailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.OrderCouponEntity;

import lombok.Data;

/**
 * @author jing.huang
 *
 */
@Data
public class OrderOwnerCostResVO {
	/**
     * 车主端代管车服务费
     */
     OwnerOrderPurchaseDetailEntity proxyExpense;
    /**
     * 车主端平台服务费
     */
     OwnerOrderPurchaseDetailEntity serviceExpense;
     
    /**
     * 获取车主补贴明细列表
     */
     List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetail;
    /**
     * 获取车主费用列表
     */
     List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetail;
    /**
     * 获取车主增值服务费用列表
     */
     List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetail;

    /**
     * 获取gps服务费
     */
     List<OwnerOrderPurchaseDetailEntity> gpsCost;

    /**
     * 获取车主油费
     */
     OwnerOrderPurchaseDetailEntity ownerOrderCostDetail;
     //------------------------------------------------------------- 分割线
    /**
     * 管理后台补贴
     */
    List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails;
    
//    /**
//     * 管理后台补贴
//     */
//    List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails;
    
	/**
	 * 优惠券
	 */
	List<OrderCouponEntity> orderCouponList;
	
    /**
     * 全局的车主订单罚金明细
     */
    List<ConsoleRenterOrderFineDeatailEntity> consoleRenterOrderFineDeatails;

    /**
     * 车主订单罚金明细
     */
    List<OwnerOrderFineDeatailEntity> ownerOrderFineDeatails;
    
    ///add huangjing  来源：SettleOrdersDefinition类字段。 200214
    private int ownerCostAmtFinal;
    ///
    List<com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleCostDetailEntity> orderConsoleCostDetails;
}
