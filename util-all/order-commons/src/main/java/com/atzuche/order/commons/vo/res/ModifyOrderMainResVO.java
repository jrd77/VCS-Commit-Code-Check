/**
 * 
 */
package com.atzuche.order.commons.vo.res;

import java.util.List;

import com.atzuche.order.commons.vo.res.account.AccountRenterCostDetailResVO;
import com.atzuche.order.commons.vo.res.account.AccountRenterDepositResVO;
import com.atzuche.order.commons.vo.res.account.AccountRenterWZDepositResVO;
import com.atzuche.order.commons.vo.res.order.RenterOrderResVO;

import lombok.Data;

/**
 * @author jing.huang
 * 主订单信息
 */
@Data
public class ModifyOrderMainResVO {
	/**
	 * 需要补付金额
	 */
	private Integer needIncrementAmt;
	/**
	 * 子订单列表
	 */
	List<RenterOrderResVO> renterOrderLst;
	/**
	 * 违章押金信息
	 */
	private AccountRenterWZDepositResVO wzVo;
	/**
	 * 租车押金信息
	 */
	private AccountRenterDepositResVO rentVo;
	/**
	 * 钱包抵扣费用
	 */
	private AccountRenterCostDetailResVO walletCostDetail;
	
}
