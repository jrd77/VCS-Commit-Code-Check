/**
 * 
 */
package com.atzuche.order.coreapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleDetailEntity;
import com.atzuche.order.accountownercost.service.notservice.AccountOwnerCostSettleDetailNoTService;
import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeExamineNoTService;
import com.atzuche.order.commons.enums.account.CostTypeEnum;
import com.atzuche.order.commons.enums.cashcode.ConsoleCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.exceptions.InputErrorException;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.commons.vo.req.OwnerCostSettleDetailDataVO;
import com.atzuche.order.commons.vo.req.OwnerCostSettleDetailReqVO;
import com.atzuche.order.commons.vo.res.OwnerCostSettleDetailVO;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.settle.entity.AccountDebtDetailEntity;
import com.atzuche.order.settle.service.notservice.AccountDebtDetailNoTService;
import com.google.common.collect.Lists;

/**
 * @author jing.huang
 *
 */
@Service
public class OwnerCostFacadeService {
	
	@Autowired
	private AccountOwnerCostSettleDetailNoTService accountOwnerCostSettleDetailNoTService;
	@Autowired
	private AccountOwnerIncomeExamineNoTService accountOwnerIncomeExamineNoTService;
	@Autowired
	private AccountDebtDetailNoTService  accountDebtDetailNoTService;
	@Autowired
	private OrderService orderService;
	
	public OwnerCostSettleDetailVO getOwnerCostSettleDetail(String orderNo, String ownerNo) {
		// 定义返回对象
		OwnerCostSettleDetailVO vo = new OwnerCostSettleDetailVO();
		//参数回写
		vo.setOrderNo(orderNo);
		vo.setMemNo(ownerNo);
		
		//查询车主罚金
        List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetails = accountOwnerCostSettleDetailNoTService.getAccountOwnerCostSettleDetails(orderNo,ownerNo);
        // 车主结算记录存在 且 车主收益 已审核通过  返回  罚金 金额
        if(!CollectionUtils.isEmpty(accountOwnerCostSettleDetails)){
        	//车主端代管车服务费
            int proxyExpenseAmt = getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.PROXY_CHARGE);
            //车主端平台服务费
            int serviceExpenseAmt = getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.SERVICE_CHARGE);
            //服务费
            vo.setPlatformSrvAmt(proxyExpenseAmt+serviceExpenseAmt);
            
            //GPS服务费
            int gpsSrvAmt = getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.GPS_SERVICE_AMT);
            vo.setGpsSrvAmt(gpsSrvAmt);
            
            //GPS押金
            int gpsDepositAmt = getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.HW_DEPOSIT_DEBT);
            vo.setGpsDepositAmt(gpsDepositAmt);
            
            //调价  //车主给租客的调价
            int adjustAmt = getSettleAmtByCashCode(accountOwnerCostSettleDetails, RenterCashCodeEnum.SUBSIDY_OWNERTORENTER_ADJUST);
            ////租客给车主的调价
            int adjustAmt2 = getSettleAmtByCashCode(accountOwnerCostSettleDetails, RenterCashCodeEnum.SUBSIDY_RENTERTOOWNER_ADJUST);
            vo.setAdjustAmt(adjustAmt+adjustAmt2);
            
            //租金补贴
            int subsidyAmt = getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.SUBSIDY_OWNER_TORENTER_RENTAMT);
            //车主券补贴  
            int subsidyAmt2 = getSettleAmtByCashCode(accountOwnerCostSettleDetails, RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST);
            vo.setSubsidyToRenterAmt(subsidyAmt+subsidyAmt2);

            //3获取车主补贴
            int subsidyAmount = accountOwnerCostSettleDetails.stream().filter(obj ->{
                return CostTypeEnum.OWNER_SUBSIDY.getCode().equals(obj.getCostType());
            }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            //8 管理后台补贴
            int consoleSubsidyAmt =accountOwnerCostSettleDetails.stream().filter(obj ->{
                return CostTypeEnum.CONSOLE_SUBSIDY.getCode().equals(obj.getCostType());
            }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            //平台给车主的补贴
            vo.setFromPlatformSubsidyAmt(subsidyAmount+consoleSubsidyAmt);
            
            //4获取车主费用
//            int purchaseAmt =accountOwnerCostSettleDetails.stream().filter(obj ->{return OwnerCashCodeEnum.ACCOUNT_OWNER_DEBT.getCashNo().equals(obj.getSourceCode());})
//                    .mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
//            //5获取车主增值服务费用
//            int incrementAmt =accountOwnerCostSettleDetails.stream().filter(obj ->{return OwnerCashCodeEnum.ACCOUNT_OWNER_INCREMENT_COST.getCashNo().equals(obj.getSourceCode());})
//                    .mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();

            //车主罚金  违约罚金
            int fineAmt =accountOwnerCostSettleDetails.stream().filter(obj ->{
                return CostTypeEnum.OWNER_FINE.getCode().equals(obj.getCostType());
            }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            int fineAmt2 =accountOwnerCostSettleDetails.stream().filter(obj ->{
                return CostTypeEnum.CONSOLE_FINE.getCode().equals(obj.getCostType());
            }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            vo.setFineAmt(fineAmt+fineAmt2);
                   
            
        	//租金
        	int rentAmt = getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.RENT_AMT);
        	vo.setRentAmt(rentAmt);
        	
            //油费
            int oilCost =  getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.OIL_COST_OWNER);
            //7 获取车主油费(交接车油费)
            int oilAmt = getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.ACCOUNT_OWNER_SETTLE_OIL_COST);
            vo.setOilAmt(oilCost+oilAmt);
            
            //平台加油服务费
            int platformOilCostAmt  = getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.OWNER_PLANT_OIL_SERVICE_FEE);
            vo.setPlatformOilCostAmt(platformOilCostAmt);
            //超里程费用
            int mileageCostAmt  = getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.MILEAGE_COST_OWNER);
            vo.setMileageCostAmt(mileageCostAmt);  
            
            //配送服务费
            int getCarAmt  = getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.SRV_GET_COST_OWNER);
            int returnCarAmt  = getSettleAmtByCashCode(accountOwnerCostSettleDetails, OwnerCashCodeEnum.SRV_RETURN_COST_OWNER);
            vo.setGetReturnCarAmt(getCarAmt+returnCarAmt);

            //本订单收益
            int incomeAmt = accountOwnerIncomeExamineNoTService.getTotalAccountOwnerIncomeExamineByOrderNo(orderNo);
            vo.setIncomeAmt(incomeAmt);
            
            
            int payToPlatformAmt1 = getSettleAmtByCashCode(accountOwnerCostSettleDetails, ConsoleCashCodeEnum.TIME_OUT);
            int payToPlatformAmt2 = getSettleAmtByCashCode(accountOwnerCostSettleDetails, ConsoleCashCodeEnum.CAR_WASH);
            int payToPlatformAmt3 = getSettleAmtByCashCode(accountOwnerCostSettleDetails, ConsoleCashCodeEnum.DLAY_WAIT);
            int payToPlatformAmt4 = getSettleAmtByCashCode(accountOwnerCostSettleDetails, ConsoleCashCodeEnum.STOP_CAR);
            int payToPlatformAmt5 = getSettleAmtByCashCode(accountOwnerCostSettleDetails, ConsoleCashCodeEnum.EXTRA_MILEAGE);
            int payToPlatformAmt6 = getSettleAmtByCashCode(accountOwnerCostSettleDetails, ConsoleCashCodeEnum.MODIFY_ADDR_TIME);
            int payToPlatformAmt7 = getSettleAmtByCashCode(accountOwnerCostSettleDetails, ConsoleCashCodeEnum.OIL_FEE);
            
            int payToPlatformAmt = payToPlatformAmt1 + payToPlatformAmt2 + payToPlatformAmt3 + payToPlatformAmt4 + payToPlatformAmt5 + payToPlatformAmt6 + payToPlatformAmt7;
            //车主需支付给平台的费用
            vo.setPayToPlatformAmt(payToPlatformAmt);
            
            //欠款原欠费金额
            //新欠费金额
            AccountDebtDetailEntity entity = accountDebtDetailNoTService.getTotalAccountDebtDetailEntity(orderNo, ownerNo);
            int oldDebtAmt = 0;
            int newDebtAmt = 0;
            if(entity != null) {
            	oldDebtAmt = entity.getOrderDebtAmt();
            	newDebtAmt = entity.getCurrentDebtAmt();
            }
            vo.setOldDebtAmt(oldDebtAmt);
            vo.setNewDebtAmt(newDebtAmt);
        }
        
		return vo;
	}
	
	/**
	 * 根据费用编码来获取
	 * @param accountOwnerCostSettleDetails
	 * @param cashCode
	 * @return
	 */
	private int getSettleAmtByCashCode(List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetails,OwnerCashCodeEnum cashCode) {
		int amt  = accountOwnerCostSettleDetails.stream().filter(obj ->{
            return cashCode.getCashNo().equals(obj.getSourceCode());
        }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
		return amt;
	}
	/**
	 * 方法重载
	 * @param accountOwnerCostSettleDetails
	 * @param cashCode
	 * @return
	 */
	private int getSettleAmtByCashCode(List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetails,RenterCashCodeEnum cashCode) {
		int amt  = accountOwnerCostSettleDetails.stream().filter(obj ->{
            return cashCode.getCashNo().equals(obj.getSourceCode());
        }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
		return amt;
	}
	private int getSettleAmtByCashCode(List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetails,ConsoleCashCodeEnum cashCode) {
		int amt  = accountOwnerCostSettleDetails.stream().filter(obj ->{
            return cashCode.getCashNo().equals(obj.getSourceCode());
        }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
		return amt;
	}
	
	
	/**
     * 判断车主结算明细 是否是罚金费用 记录
     */
    private boolean getOwnerFineCostType(Integer costType){
        boolean result = false;
        if(CostTypeEnum.CONSOLE_FINE.getCode().equals(costType)){
            result = true;
        }
        if(CostTypeEnum.OWNER_FINE.getCode().equals(costType)){
            result = true;
        }
        if(CostTypeEnum.RENTER_FINE.getCode().equals(costType)){
            result = true;
        }
        return result;
    }
    
	public List<OwnerCostSettleDetailVO> listOwnerCostSettleDetail(OwnerCostSettleDetailReqVO req) {
		List<OwnerCostSettleDetailVO> backList = Lists.newArrayList();
		List<OwnerCostSettleDetailDataVO> listOwnerCostSettleDetailDataVO = req.getListOwnerCostSettleDetailDataVO();
		if(listOwnerCostSettleDetailDataVO==null){
			throw new InputErrorException();
		}
		
		for (OwnerCostSettleDetailDataVO ownerCostSettleDetailDataVO : listOwnerCostSettleDetailDataVO) {
			String orderNo = ownerCostSettleDetailDataVO.getOrderNo();
			String ownerNo = ownerCostSettleDetailDataVO.getOwnerNo();

			OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
			if(orderEntity==null){
				//默认对象
				OwnerCostSettleDetailVO vo = new OwnerCostSettleDetailVO();
				vo.setOrderNo(orderNo);
				vo.setMemNo(ownerNo);
				backList.add(vo);
			}else {
				//查询
				backList.add(getOwnerCostSettleDetail(orderNo, ownerNo));
			}
		}
		
		return backList;
	}
}
