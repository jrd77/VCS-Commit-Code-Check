/**
 * 
 */
package com.atzuche.order.cashieraccount.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositDetailEntity;
import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositEntity;
import com.atzuche.order.accountrenterdeposit.mapper.AccountRenterDepositDetailMapper;
import com.atzuche.order.accountrenterdeposit.mapper.AccountRenterDepositMapper;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostDetailEntity;
import com.atzuche.order.accountrenterrentcost.mapper.AccountRenterCostDetailMapper;
import com.atzuche.order.accountrenterrentcost.mapper.AccountRenterCostSettleMapper;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositDetailEntity;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositEntity;
import com.atzuche.order.accountrenterwzdepost.mapper.AccountRenterWzDepositDetailMapper;
import com.atzuche.order.accountrenterwzdepost.mapper.AccountRenterWzDepositMapper;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.rentercost.entity.vo.PayableVO;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jing.huang
 *
 */
@Service
@Slf4j
public class CashierShishouService {
    @Autowired
    private AccountRenterCostDetailMapper accountRenterCostDetailMapper;
    @Autowired
    private AccountRenterCostSettleMapper accountRenterCostSettleMapper;
    
    @Autowired
    private AccountRenterDepositMapper accountRenterDepositMapper;
    @Autowired
    private AccountRenterDepositDetailMapper accountRenterDepositDetailMapper;
    
    @Autowired
    private AccountRenterWzDepositMapper accountRenterWzDepositMapper; 
    @Autowired
    private AccountRenterWzDepositDetailMapper accountRenterWzDepositDetailMapper;
    @Autowired
    private CashierNoTService cashierNoTService;
    @Autowired
    private RenterOrderCostCombineService renterOrderCostCombineService;
    
    /**
     * 租车费用实收统计
     * @param orderNo
     * @param memNo
     * @return
     */
    public boolean checkRentAmountShishou(String orderNo,String memNo) {
    	RenterOrderEntity renterOrderEntity = cashierNoTService.getRenterOrderNoByOrderNo(orderNo);
    	if(renterOrderEntity == null) {
    		return false;
    	}
    	//需要统计
    	List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableIncrementVO(orderNo,renterOrderEntity.getRenterOrderNo(),memNo);
        //应付租车费用（已经求和）
        int rentCarAmt = cashierNoTService.sumRentOrderCost(payableVOs);
        
        //已付租车费用(shifu  租车费用的实付)
        //该情况只会有一种情况：钱包 shifu
//        rentAmtPayed = accountRenterCostSettleService.getCostPaidRent(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo());
//		amtRent = rentAmt + rentAmtPayed;
		
//    	AccountRenterCostSettleEntity accountRenterCostSettleEntity = accountRenterCostSettleMapper.selectByOrderNo(orderNo,memNo);
//    	if(accountRenterCostSettleEntity == null) {
//    		return false;
//    	}
    	List<AccountRenterCostDetailEntity> list = accountRenterCostDetailMapper.getAccountRenterCostDetailsByOrderNo(orderNo);
    	if(list == null) {
    		return false;
    	}
    	
    	//都存在的情况
//    	int rentCarAmt = accountRenterCostSettleEntity.getShifuAmt();
    	//求和
    	int payAmt = list.stream().mapToInt(AccountRenterCostDetailEntity::getAmt).sum();
//    	for (AccountRenterCostDetailEntity accountRenterCostDetailEntity : list) {
//			payAmt += accountRenterCostDetailEntity.getAmt();
//		}
    	
    	log.info("checkRentAmountShishou payAmt=[{}],rentCarAmt=[{}],params orderNo=[{}],memNo=[{}]",Math.abs(payAmt),Math.abs(rentCarAmt),orderNo,memNo);
    	//支付流水大于实收，才返回真。
    	if(Math.abs(payAmt) >= Math.abs(rentCarAmt)) {
    		return true;
    	}
		return false;
	}
    
    /**
     * 租车押金实收统计
     * @param orderNo
     * @param memNo
     * @return
     */
    public boolean checkRentShishou(String orderNo,String memNo) {
    	AccountRenterDepositEntity accountRenterDepositEntity = accountRenterDepositMapper.selectByOrderAndMemNo(orderNo,memNo);
    	if(accountRenterDepositEntity == null) {
    		return false;
    	}
    	List<AccountRenterDepositDetailEntity> list = accountRenterDepositDetailMapper.findByOrderNo(orderNo);
    	if(list == null) {
    		return false;
    	}
    	
    	//都存在的情况
    	int rentAmt = accountRenterDepositEntity.getYingfuDepositAmt();
    	//求和
    	int payAmt = list.stream().mapToInt(AccountRenterDepositDetailEntity::getAmt).sum();
    	
    	log.info("checkRentShishou payAmt=[{}],rentAmt=[{}],params orderNo=[{}],memNo=[{}]",Math.abs(payAmt),Math.abs(rentAmt),orderNo,memNo);
    	//支付流水大于实收，才返回真。
    	if(Math.abs(payAmt) >= Math.abs(rentAmt)) {
    		return true;
    	}
		return false;
	}
    
    
    /**
     * 违章押金实收统计
     * @param orderNo
     * @param memNo
     * @return
     */
    public boolean checkDepositShishou(String orderNo,String memNo) {
    	AccountRenterWzDepositEntity accountRenterDepositEntity = accountRenterWzDepositMapper.selectByOrderAndMemNo(orderNo,memNo);
    	if(accountRenterDepositEntity == null) {
    		return false;
    	}
    	List<AccountRenterWzDepositDetailEntity> list = accountRenterWzDepositDetailMapper.findByOrderNo(orderNo);
    	if(list == null) {
    		return false;
    	}
    	
    	//都存在的情况
    	int depositAmt = accountRenterDepositEntity.getYingshouDeposit();
    	//求和
    	int payAmt = list.stream().mapToInt(AccountRenterWzDepositDetailEntity::getAmt).sum();
    	
    	log.info("checkDepositShishou payAmt=[{}],depositAmt=[{}],params orderNo=[{}],memNo=[{}]",Math.abs(payAmt),Math.abs(depositAmt),orderNo,memNo);
    	//支付流水大于实收，才返回真。
    	if(Math.abs(payAmt) >= Math.abs(depositAmt)) {
    		return true;
    	}
		return false;
	}
}
