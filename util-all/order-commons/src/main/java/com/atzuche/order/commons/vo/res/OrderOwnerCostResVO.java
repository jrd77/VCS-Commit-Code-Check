/**
 * 
 */
package com.atzuche.order.commons.vo.res;

import com.atzuche.order.commons.entity.dto.OwnerCouponLongDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OwnerOrderDTO;
import com.atzuche.order.commons.vo.res.ownercosts.*;
import com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.OrderCouponEntity;
import lombok.Data;

import java.util.List;

/**
 * @author jing.huang
 *
 */
@Data
public class OrderOwnerCostResVO {
	/**
     * 车主端代管车服务费（暂不使用，从增值表中根据主订单号查询，结算实时计算）
     */
     OwnerOrderPurchaseDetailEntity proxyExpense;
    /**
     * 车主端平台服务费（暂不使用，从增值表中根据主订单号查询，结算实时计算）
     */
     OwnerOrderPurchaseDetailEntity serviceExpense;
     /**
      * 获取gps服务费（暂不使用，从增值表中根据主订单号查询，结算实时计算）
      */
      List<OwnerOrderPurchaseDetailEntity> gpsCost;
    /**
     * gps押金
     */
    private Integer  gpsDepositTotal;

     ///----------------------------------------------------------  6大块
    /**
     * 获取车主补贴明细列表
     */
     List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetail;
     /**
      * 管理后台补贴
      */
     List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails;
     /**
      * 全局的车主订单罚金明细
      */
     List<ConsoleOwnerOrderFineDeatailEntity> consoleOwnerOrderFineDeatails;
     /**
      * 车主订单罚金明细
      */
     List<OwnerOrderFineDeatailEntity> ownerOrderFineDeatails;
     /*
      * 车主费用 
      * */
     List<com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleCostDetailEntity> orderConsoleCostDetails;

    /**
     * 获取车主费用列表  车主端采购费用明细表
     */
     List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetail;
    /**
     * 获取车主增值服务费用列表  车主增值订单明细表
     */
     List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetail;
   ///----------------------------------------------------------  6大块
     
   

     //------------------------------------------------------------- 分割线

	/**
	 * 优惠券
	 */
	List<OrderCouponEntity> orderCouponList;
	

    ///add huangjing  来源：SettleOrdersDefinition类字段。 200214
    private int ownerCostAmtFinal;
    //settleIncomeAmt 结算金额，从account_owner_income_examine表中获取。 200215
    private int ownerCostAmtSettleAfter;
    /**
     * 平台加油服务费用
     */
    private int ownerPlatFormOilService;
    /**
     * 获取车主油费
     */
    private int ownerOilDifferenceCrashAmt;
    /**
     * 交接车-获取超里程费用（车主端依托租客的参数来计算。）
     */
    private int mileageAmt;
    /**
     * 长租折扣
     */
    private OwnerCouponLongDTO ownerCouponLongDTO;
    /**
     * 车主订单
     */
    private OwnerOrderDTO ownerOrderDTO;
}
