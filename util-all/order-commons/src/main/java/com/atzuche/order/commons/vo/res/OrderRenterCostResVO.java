/**
 * 
 */
package com.atzuche.order.commons.vo.res;

import com.atzuche.order.commons.entity.dto.OwnerCouponLongDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderStatusDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterOrderDTO;
import com.atzuche.order.commons.vo.res.account.AccountRenterCostDetailResVO;
import com.atzuche.order.commons.vo.res.account.AccountRenterCostSettleResVO;
import com.atzuche.order.commons.vo.res.account.AccountRenterDepositResVO;
import com.atzuche.order.commons.vo.res.account.AccountRenterWZDepositResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderCostDetailResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderDeliveryResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderFineDeatailResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderSubsidyDetailResVO;
import com.atzuche.order.commons.vo.res.rentcosts.*;
import lombok.Data;

import java.util.List;

/**
 * @author jing.huang
 *
 */
@Data
public class OrderRenterCostResVO {
    /*
    * 订单状态
    * */
    private OrderStatusDTO orderStatusDTO;

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
	
	
	///----------------------------------------------------------  6大块
	/*
	 * 租客罚金列表
	 */
	private List<RenterOrderFineDeatailResVO> fineLst;
	/*管理后台罚金列表*/
	private List<ConsoleRenterOrderFineDeatailEntity> consoleFineLst;
	/*
	 * 租客费用明细列表
	 */
	private List<RenterOrderCostDetailResVO> renterOrderCostDetailList;
    /*
     *管理后台费用
     */
	List<OrderConsoleCostDetailEntity> orderConsoleCostDetails;

	/*
	 * 抵扣明细 补贴
	 */
	List<RenterOrderSubsidyDetailResVO> subsidyLst;
    /*
     * 管理后台补贴
     */
    List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails;
    ///----------------------------------------------------------  6大块
    
    
	/*
	 * 取车配送
	 */
	private RenterOrderDeliveryResVO renterOrderDeliveryGet;
	/*
	 * 还车配送
	 */
	private RenterOrderDeliveryResVO renterOrderDeliveryReturn;

    
	// ---------------------------------------------------------------
	private AccountRenterCostSettleResVO renterSettleVo;
	
//	RentCosts rentCosts;
	
    /*
     * 交接车-油费
     */
    private int oilAmt;

    /*
     * 交接车-获取超里程费用
     */
    private int mileageAmt;
    
	/*
	 * 优惠券
	 */
	List<OrderCouponEntity> orderCouponList;
	
	/*
	 * 补付费用
	 */
	List<OrderSupplementDetailEntity> supplementList;
	
	/**
	     * 租客最终收益金额（应收）
	*/
	private int renterCostAmtFinal;

    /**
     * 长租折扣
     */
    private OwnerCouponLongDTO ownerCouponLongDTO;
    /**
     * 租客子订单信息
     */
    private RenterOrderDTO renterOrderDTO;
    /**
     * 租客租车费用  押金  违章押金  信息
     */
    private RenterCostVO  renterCostVO;
    /*
    *
    * 租客商品信息
    * */
    private RenterGoodsDetailDTO renterGoodsDetailDTO;
    /*
    * 平台给租客的实际补贴
    * */
    private Integer realConsoleSubsidyAmt;
}
