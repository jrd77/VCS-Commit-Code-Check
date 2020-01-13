/**
 * 
 */
package com.atzuche.order.admin.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.atzuche.order.admin.vo.req.order.ModificationOrderRequestVO;
import com.atzuche.order.admin.vo.resp.order.ModificationOrderListResponseVO;
import com.atzuche.order.admin.vo.resp.order.ModificationOrderResponseVO;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;

/**
 * @author jing.huang
 *
 */
@Service
public class ModificationOrderService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	public ModificationOrderListResponseVO queryModifyList(ModificationOrderRequestVO modificationOrderRequestVO) {
		ModificationOrderListResponseVO respVo = new ModificationOrderListResponseVO();
		List<ModificationOrderResponseVO> modificationOrderList =  new ArrayList<ModificationOrderResponseVO>();
		
		//查询子订单表
		
		ModificationOrderResponseVO tmpVo = new ModificationOrderResponseVO();
		
		
		/**
		 * 费用明细表renter_order_cost_detail   
		 */
		//租客租金
		String rentAmtCashNo =  RenterCashCodeEnum.RENT_AMT.getCashNo();
		//平台保障费
		String insureCashNo = RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo();
		//全面保障服务费
		String abatementCashNo = RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo();
		//附加驾驶员险
		String driverCashNo = RenterCashCodeEnum.EXTRA_DRIVER_INSURE.getCashNo();
		//手续费
		String feeCashNo = RenterCashCodeEnum.FEE.getCashNo();
		//配送费用
		String getCost = RenterCashCodeEnum.SRV_GET_COST.getCashNo();
		String returnCost = RenterCashCodeEnum.SRV_RETURN_COST.getCashNo();
		String getBeyondCost = RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT.getCashNo();
		String returnBeyondCost = RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT.getCashNo();
		
		/**
		 * renter_order_delivery 查询配送信息表
		 */
		
		/**
		 * 优惠抵扣 券，凹凸币，钱包   钱包抵扣
		 */
		
		
		/**
		 * 车辆押金
		 */
		
		
		/**
		 * 违章押金
		 */
		
		/**
		 * renter_order_fine_deatail  取还车服务违约金
		 */
		
		
		/**
		 * 需补付金额
		 */
		
		
		//封装数据
		respVo.setModificationOrderList(modificationOrderList);
		return respVo;
	}
	
}
