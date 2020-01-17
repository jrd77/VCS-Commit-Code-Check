/**
 * 
 */
package com.atzuche.order.coreapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.accountrenterdeposit.service.AccountRenterDepositService;
import com.atzuche.order.accountrenterdeposit.vo.res.AccountRenterDepositResVO;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostDetailEntity;
import com.atzuche.order.accountrenterrentcost.service.AccountRenterCostSettleService;
import com.atzuche.order.accountrenterwzdepost.service.AccountRenterWzDepositService;
import com.atzuche.order.accountrenterwzdepost.vo.res.AccountRenterWZDepositResVO;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.commons.enums.DeliveryOrderTypeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.res.ModifyOrderMainResVO;
import com.atzuche.order.commons.vo.res.ModifyOrderResVO;
import com.atzuche.order.commons.vo.res.account.AccountRenterCostDetailResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderCostDetailResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderDeliveryResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderFineDeatailResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderSubsidyDetailResVO;
import com.atzuche.order.commons.vo.res.order.RenterOrderResVO;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostDetailService;
import com.atzuche.order.rentercost.service.RenterOrderSubsidyDetailService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jing.huang
 *
 */
@Service
@Slf4j
public class ModifyOrderQueryListService {
	@Autowired
	private CashierPayService cashierPayService;
	@Autowired
	private RenterOrderDeliveryService renterOrderDeliveryService;
	@Autowired
	private RenterOrderCostDetailService renterOrderCostDetailService;
	@Autowired
	private AccountRenterCostSettleService accountRenterCostSettleService;
	@Autowired
	private AccountRenterDepositService accountRenterDepositService;
	@Autowired
	private AccountRenterWzDepositService accountRenterWzDepositService;
	@Autowired
	private RenterOrderSubsidyDetailService renterOrderSubsidyDetailService;
	@Autowired
	private RenterOrderService renterOrderService;
	
	
	public ModifyOrderResVO queryModifyOrderList(String orderNo,String renterOrderNo) throws Exception{
		ModifyOrderResVO resVo = new ModifyOrderResVO();

		//租客罚金列表
		List<RenterOrderFineDeatailEntity> fineLst = renterOrderCostDetailService.queryRentOrderFineDetail(orderNo, renterOrderNo);
		List<RenterOrderFineDeatailResVO> fineLstReal = new ArrayList<RenterOrderFineDeatailResVO>();
		if(fineLst != null) {
			fineLst.stream().forEach(x->{
				RenterOrderFineDeatailResVO real = new RenterOrderFineDeatailResVO();
        		try {
					BeanUtils.copyProperties(real, x);
				} catch (Exception e) {
					log.error("对象属性赋值报错:",e);
				}
        		fineLstReal.add(real);
            });
        }
		resVo.setFineLst(fineLstReal);
		
		
		//配送订单
        List<RenterOrderDeliveryEntity> renterOrderDeliveryList = renterOrderDeliveryService.selectByRenterOrderNo(renterOrderNo);
        RenterOrderDeliveryEntity renterOrderDeliveryGet = filterDeliveryOrderByType(renterOrderDeliveryList, DeliveryOrderTypeEnum.GET_CAR);
        RenterOrderDeliveryEntity renterOrderDeliveryReturn = filterDeliveryOrderByType(renterOrderDeliveryList, DeliveryOrderTypeEnum.RETURN_CAR);
        RenterOrderDeliveryResVO renterOrderDeliveryGetReal = new RenterOrderDeliveryResVO();
        RenterOrderDeliveryResVO renterOrderDeliveryReturnReal = new RenterOrderDeliveryResVO();
        
        if(renterOrderDeliveryGet != null) {
        	BeanUtils.copyProperties(renterOrderDeliveryGetReal, renterOrderDeliveryGet);
        }
        if(renterOrderDeliveryReturn != null) {
        	BeanUtils.copyProperties(renterOrderDeliveryReturnReal, renterOrderDeliveryReturn);
        }
        resVo.setRenterOrderDeliveryGet(renterOrderDeliveryGetReal);
        resVo.setRenterOrderDeliveryReturn(renterOrderDeliveryReturnReal);
        
        
        //租客费用明细
        List<RenterOrderCostDetailEntity> renterOrderCostDetailList = renterOrderCostDetailService.listRenterOrderCostDetail(orderNo, renterOrderNo);
        List<RenterOrderCostDetailResVO> renterOrderCostDetailListReal = new ArrayList<RenterOrderCostDetailResVO>();
        if(renterOrderCostDetailList != null) {
        	renterOrderCostDetailList.stream().forEach(x->{
        		RenterOrderCostDetailResVO real = new RenterOrderCostDetailResVO();
        		try {
					BeanUtils.copyProperties(real, x);
				} catch (Exception e) {
					log.error("对象属性赋值报错:",e);
				}
        		renterOrderCostDetailListReal.add(real);
            });
        }
        
        //抵扣明细
    	List<RenterOrderSubsidyDetailEntity> subsidyLst = renterOrderSubsidyDetailService.listRenterOrderSubsidyDetail(orderNo, renterOrderNo);
        List<RenterOrderSubsidyDetailResVO> subsidyLstReal = new ArrayList<RenterOrderSubsidyDetailResVO>();;
        if(subsidyLst != null) {
        	subsidyLst.stream().forEach(x->{
        		RenterOrderSubsidyDetailResVO real = new RenterOrderSubsidyDetailResVO();
        		try {
					BeanUtils.copyProperties(real, x);
				} catch (Exception e) {
					log.error("对象属性赋值报错:",e);
				}
                subsidyLstReal.add(real);
            });
        }
        resVo.setSubsidyLst(subsidyLstReal);
        
		return resVo;
	}
	
	
	public ModifyOrderMainResVO getModifyOrderMain(String orderNo,String memNo) throws Exception{
		ModifyOrderMainResVO resVo = new ModifyOrderMainResVO();
		/**
		 * 根据订单号查询封装
		 */
		//需补付金额
		int needIncrementAmt = cashierPayService.getRentCost(orderNo, memNo);
		resVo.setNeedIncrementAmt(needIncrementAmt);
		
		
		//租客订单
		//租客订单
		List<RenterOrderEntity> rentLst = renterOrderService.queryRenterOrderByOrderNo(orderNo);
		List<RenterOrderResVO> rentLstReal = new ArrayList<RenterOrderResVO>();
		if(rentLst != null) {
			rentLst.stream().forEach(x->{
				RenterOrderResVO real = new RenterOrderResVO();
        		try {
					BeanUtils.copyProperties(real, x);
				} catch (Exception e) {
					log.error("对象属性赋值报错:",e);
				}
        		rentLstReal.add(real);
            });
        }
		resVo.setRenterOrderLst(rentLstReal);

        //违章押金
        AccountRenterWZDepositResVO wzVo =  accountRenterWzDepositService.getAccountRenterWZDeposit(orderNo, memNo);
        com.atzuche.order.commons.vo.res.account.AccountRenterWZDepositResVO wzVoReal = new com.atzuche.order.commons.vo.res.account.AccountRenterWZDepositResVO();
        if(wzVo != null) {
        	BeanUtils.copyProperties(wzVoReal, wzVo);
        }else {
        	wzVoReal.setYingshouDeposit(0);
        }
        resVo.setWzVo(wzVoReal);
        
        //租车押金
        AccountRenterDepositResVO rentVo = accountRenterDepositService.getAccountRenterDepositEntity(orderNo, memNo);
        com.atzuche.order.commons.vo.res.account.AccountRenterDepositResVO rentVoReal = new com.atzuche.order.commons.vo.res.account.AccountRenterDepositResVO();
        if(rentVo != null) {
        	BeanUtils.copyProperties(rentVoReal, rentVo);
        }else {
        	rentVoReal.setYingfuDepositAmt(0);
        }
        resVo.setRentVo(rentVoReal);
        
        //钱包
        List<AccountRenterCostDetailEntity> lstCostDetail =  accountRenterCostSettleService.getAccountRenterCostDetailsByOrderNo(orderNo);
        AccountRenterCostDetailEntity walletCostDetail = null; //仅仅关心的是钱包的。
        for (AccountRenterCostDetailEntity accountRenterCostDetailEntity : lstCostDetail) {
        	if(RenterCashCodeEnum.WALLET_DEDUCT.equals(accountRenterCostDetailEntity.getSourceCode())) {
        		walletCostDetail = new AccountRenterCostDetailEntity();
        		BeanUtils.copyProperties(walletCostDetail, accountRenterCostDetailEntity);
        	}
		}
        AccountRenterCostDetailResVO walletCostDetailReal = new AccountRenterCostDetailResVO();
        if(walletCostDetail != null) {
        	BeanUtils.copyProperties(walletCostDetailReal, walletCostDetail);
        }else {
        	walletCostDetailReal.setAmt(0); //默认值。
        }
        resVo.setWalletCostDetail(walletCostDetailReal);

		return resVo;
	}
	
	
	
	
	/**
	 * 来源订单详情接口
	 * @param renterOrderDeliveryList
	 * @param deliveryTypeEnum
	 * @return
	 */
	private RenterOrderDeliveryEntity filterDeliveryOrderByType(List<RenterOrderDeliveryEntity> renterOrderDeliveryList, DeliveryOrderTypeEnum deliveryTypeEnum){
        List<RenterOrderDeliveryEntity> list = Optional.ofNullable(renterOrderDeliveryList).orElseGet(ArrayList::new)
                .stream()
                .filter(x -> deliveryTypeEnum.getCode() == x.getType())
                .collect(Collectors.toList());
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }
	
}
