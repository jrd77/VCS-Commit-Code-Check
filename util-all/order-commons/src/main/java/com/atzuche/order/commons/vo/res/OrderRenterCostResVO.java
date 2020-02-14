/**
 * 
 */
package com.atzuche.order.commons.vo.res;

import java.util.List;

import com.atzuche.order.commons.vo.res.account.AccountRenterCostDetailResVO;
import com.atzuche.order.commons.vo.res.account.AccountRenterCostSettleResVO;
import com.atzuche.order.commons.vo.res.account.AccountRenterDepositResVO;
import com.atzuche.order.commons.vo.res.account.AccountRenterWZDepositResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderCostDetailResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderDeliveryResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderFineDeatailResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderSubsidyDetailResVO;
import com.atzuche.order.commons.vo.res.rentcosts.ConsoleRenterOrderFineDeatailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleCostDetailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.OrderCouponEntity;
import com.atzuche.order.commons.vo.res.rentcosts.OrderSupplementDetailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.RenterOrderCostDetailEntity;

import lombok.Data;

/**
 * @author jing.huang
 *
 */
@Data
public class OrderRenterCostResVO {
	/*
	 * 需要补付金额
	 */
	private Integer needIncrementAmt;
	/*
	 * 违章押金信息
	 */
	private AccountRenterWZDepositResVO wzVo;
	/*
	 * 租车押金信息
	 */
	private AccountRenterDepositResVO rentVo;
	/*
	 * 钱包抵扣费用
	 */
	private AccountRenterCostDetailResVO walletCostDetail;
	
	
	///----------------------------------------------------------
	/*
	 * 租客罚金列表
	 */
	private List<RenterOrderFineDeatailResVO> fineLst;
	/*管理后台罚金列表*/
	private List<ConsoleRenterOrderFineDeatailEntity> consoleFineLst;
	
	/*
	 * 取车配送
	 */
	private RenterOrderDeliveryResVO renterOrderDeliveryGet;
	/*
	 * 还车配送
	 */
	private RenterOrderDeliveryResVO renterOrderDeliveryReturn;
	/*
	 * 租客费用明细列表
	 */
	private List<RenterOrderCostDetailResVO> renterOrderCostDetailList;
	
	/*
	 * 抵扣明细
	 */
	List<RenterOrderSubsidyDetailResVO> subsidyLst;
    /*
     * 管理后台补贴
     */
    List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails;
    
    /*
                *管理后台费用
     */
    List<OrderConsoleCostDetailEntity> orderConsoleCostDetails;
    
	// ---------------------------------------------------------------
	private AccountRenterCostSettleResVO renterSettleVo;
	
//	RentCosts rentCosts;
	
    /*
     * 交接车-油费
     */
    private RenterOrderCostDetailEntity oilAmt;

    /*
     * 交接车-获取超里程费用
     */
    private RenterOrderCostDetailEntity mileageAmt;
    
	/*
	 * 优惠券
	 */
	List<OrderCouponEntity> orderCouponList;
	
	/*
	 * 补付费用
	 */
	List<OrderSupplementDetailEntity> supplementList;
	
}
