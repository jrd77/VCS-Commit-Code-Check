/**
 * 
 */
package com.atzuche.order.cashieraccount.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.atzuche.order.accountrenterrentcost.service.AccountRenterCostSettleService;
import com.atzuche.order.cashieraccount.exception.OrderPaySignFailException;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPayBatchReqVO;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPaySignBatchReqVO;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPaySignReqVO;
import com.atzuche.order.cashieraccount.vo.res.AccountPayAbleResVO;
import com.atzuche.order.cashieraccount.vo.res.OrderPayableAmountResVO;
import com.atzuche.order.commons.enums.YesNoEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.service.OrderPayCallBack;
import com.atzuche.order.rentercost.entity.vo.PayableVO;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.autopay.gateway.vo.req.PayVo;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ErrorCode;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jing.huang
 *
 */
@Service
@Slf4j
public class CashierBatchPayService {
    @Autowired 
    RenterOrderCostCombineService renterOrderCostCombineService;
    @Autowired 
    CashierNoTService cashierNoTService;
    @Autowired 
    CashierService cashierService;
    @Autowired 
    AccountRenterCostSettleService accountRenterCostSettleService;
    @Autowired
    CashierPayService cashierPayService;
    
    
	public OrderPayableAmountResVO getOrderPayableAmount(OrderPayBatchReqVO orderPayReqVO) {
		Assert.notNull(orderPayReqVO, ErrorCode.PARAMETER_ERROR.getText());
		orderPayReqVO.check();
		
		int amtTotal = 0;
		int amtRent = 0;
		int amtIncrementRent =0;
		int amtIncrementSupplementAmt = 0; 
		int amtIncrementDebtAmt = 0;
		//已付租车费用
        int rentAmtPayed = 0;
        int amtTotalDeposit = 0;
        int amtTotalWZDeposit = 0;
        
		List<String> orderNos = orderPayReqVO.getOrderNos();
        OrderPayableAmountResVO result = new OrderPayableAmountResVO();
        //待支付金额明细
        List<AccountPayAbleResVO> accountPayAbles = new ArrayList<>();
        
		for (String orderNo : orderNos) {
			//1 查询子单号
	        RenterOrderEntity renterOrderEntity = cashierNoTService.getRenterOrderNoByOrderNo(orderNo);

	        //车辆押金 是否选择车辆押金
	        int amtDeposit = 0;
	        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT)){
	            amtDeposit = cashierService.getRenterDeposit(orderNo,orderPayReqVO.getMenNo());
	           if(amtDeposit < 0){
	               accountPayAbles.add(new AccountPayAbleResVO(orderNo,orderPayReqVO.getMenNo(),amtDeposit, RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT,RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT.getTxt()));
	           }
	        }
	        
	        //违章押金 是否选择违章押金
	        int amtWZDeposit = 0;
	        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.DEPOSIT)){
	            amtWZDeposit = cashierService.getRenterWZDeposit(orderNo,orderPayReqVO.getMenNo());
	            if(amtWZDeposit < 0){
	                accountPayAbles.add(new AccountPayAbleResVO(orderNo,orderPayReqVO.getMenNo(),amtWZDeposit, RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT,RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT.getTxt()));
	            }
	        }

	        //应付租车费用
	        int rentAmt = 0;
	        int rentIncrementAmt = 0;
	        int rentIncrementSupplementAmt = 0;
	        int rentIncrementDebtAmt = 0;

	        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_AMOUNT)){  //修改订单的补付
	            List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableGlobalVO(orderNo,renterOrderEntity.getRenterOrderNo(),orderPayReqVO.getMenNo());
	            result.setPayableVOs(payableVOs);
	            //应付租车费用（已经求和）
	            rentAmt = cashierNoTService.sumRentOrderCost(payableVOs);
	            
	            //已付租车费用(shifu  租车费用的实付)
	            rentAmtPayed = accountRenterCostSettleService.getCostPaidRent(orderNo,orderPayReqVO.getMenNo());
	            if(!CollectionUtils.isEmpty(payableVOs) && rentAmt+rentAmtPayed < 0){   // 
	                for(int i=0;i<payableVOs.size();i++){
	                    PayableVO payableVO = payableVOs.get(i);
	                    //判断是租车费用、还是补付 租车费用 并记录 详情
	                    RenterCashCodeEnum type = RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST;
	                    result.setIsPayAgain(YesNoEnum.NO.getCode());
	                    accountPayAbles.add(new AccountPayAbleResVO(orderNo,orderPayReqVO.getMenNo(),payableVO.getAmt(),type,payableVO.getTitle(),payableVO.getUniqueNo()));
	                }
	            }
	        }
	        
	        //APP修改订单补付
	        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_INCREMENT)){  //修改订单的补付
	            List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableIncrementVO(orderNo,renterOrderEntity.getRenterOrderNo(),orderPayReqVO.getMenNo());
	            result.setPayableVOs(payableVOs);
	            //应付租车费用
	            rentIncrementAmt = cashierNoTService.sumRentOrderCost(payableVOs);
	            //已付租车费用(shifu  租车费用的实付)
	            rentAmtPayed = accountRenterCostSettleService.getCostPaidRent(orderNo,orderPayReqVO.getMenNo());
	            if(!CollectionUtils.isEmpty(payableVOs) && rentIncrementAmt+rentAmtPayed < 0){   // +rentAmtPayed
	                for(int i=0;i<payableVOs.size();i++){
	                    PayableVO payableVO = payableVOs.get(i);
	                    //判断是租车费用、还是补付 租车费用 并记录 详情
	                    RenterCashCodeEnum type = RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN;
	                    if(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN.equals(type)){
	                        result.setIsPayAgain(YesNoEnum.YES.getCode());
	                    }
	                    accountPayAbles.add(new AccountPayAbleResVO(orderNo,orderPayReqVO.getMenNo(),payableVO.getAmt(),type,payableVO.getTitle(),payableVO.getUniqueNo()));
	                }
	            }
	        }
	        
	        //管理后台补付，等于管理后台的补付   08
	        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_INCREMENT_CONSOLE)){  //修改订单的补付
	        	List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableSupplementVO(orderNo,renterOrderEntity.getRenterOrderNo(),orderPayReqVO.getMenNo());
	            result.setPayableVOs(payableVOs);
	            //应付租车费用,保存为负数
	            rentIncrementSupplementAmt = cashierNoTService.sumRentOrderCost(payableVOs);
	            //已付租车费用(shifu  租车费用的实付)
	            if(!CollectionUtils.isEmpty(payableVOs) && rentIncrementSupplementAmt < 0){ 
	                for(int i=0;i<payableVOs.size();i++){
	                    PayableVO payableVO = payableVOs.get(i);
	                    //判断是租车费用、还是补付 租车费用 并记录 详情
	                    RenterCashCodeEnum type = RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN;
	                    if(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN.equals(type)){
	                        result.setIsPayAgain(YesNoEnum.YES.getCode());
	                    }
	                    accountPayAbles.add(new AccountPayAbleResVO(orderNo,orderPayReqVO.getMenNo(),payableVO.getAmt(),type,payableVO.getTitle(),payableVO.getUniqueNo()));
	                }
	            }
	        }
	        
	        //支付欠款 
	        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.DEBT)){  //修改订单的补付
	            List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableDebtPayVO(orderNo,renterOrderEntity.getRenterOrderNo(),orderPayReqVO.getMenNo());
	            result.setPayableVOs(payableVOs);
	            //应付租车费用（已经求和）
	            rentIncrementDebtAmt = cashierNoTService.sumRentOrderCost(payableVOs);
	            
	            //已付租车费用(shifu  租车费用的实付)
	            if(!CollectionUtils.isEmpty(payableVOs) && rentIncrementDebtAmt < 0){
	                for(int i=0;i<payableVOs.size();i++){
	                    PayableVO payableVO = payableVOs.get(i);
	                    //判断是租车费用、还是补付 租车费用 并记录 详情
	                    RenterCashCodeEnum type = RenterCashCodeEnum.ACCOUNT_RENTER_DEBT_COST_AGAIN;
	                    result.setIsPayAgain(YesNoEnum.NO.getCode());
	                    accountPayAbles.add(new AccountPayAbleResVO(orderNo,orderPayReqVO.getMenNo(),payableVO.getAmt(),type,payableVO.getTitle(),payableVO.getUniqueNo()));
	                }
	            }
	        }
	        
	        
	        
	        //待支付总额
	         amtTotal += amtDeposit + amtWZDeposit + rentAmt + rentIncrementAmt + rentIncrementSupplementAmt + rentIncrementDebtAmt;
	        //实际待支付租车费用总额 即真实应付租车费用
	         amtRent += rentAmt + rentAmtPayed;
	        //补付修改订单
	         amtIncrementRent += rentIncrementAmt + rentAmtPayed;
	        //管理后台补付
	         amtIncrementSupplementAmt += rentIncrementSupplementAmt;
	        //支付欠款
	         amtIncrementDebtAmt += rentIncrementDebtAmt;
	         //租车押金
	         amtTotalDeposit += amtDeposit;
	         //违章押金
	         amtTotalWZDeposit += amtWZDeposit;
		}
		
        // 计算钱包 支付 目前支付抵扣租费费用   去掉钱包
        int amtWallet =0;
        
        result.setAmtWallet(amtWallet);
        result.setAmtRent(amtRent);
        result.setAmtIncrementRent(amtIncrementRent);
        ///add 管理后台补付，支付欠款 200311
        result.setAmtIncrementRentSupplement(amtIncrementSupplementAmt);
        result.setAmtIncrementRentDebt(amtIncrementDebtAmt);
        
        result.setAmtDeposit(amtTotalDeposit);
        result.setAmtWzDeposit(amtTotalWZDeposit);
        result.setAmtTotal(amtTotal);
        result.setAmtPay(rentAmtPayed);
        result.setAmt(amtTotal);  //result.getAmt()取值。
        result.setMemNo(orderPayReqVO.getMenNo());
        result.setOrderNo(orderPayReqVO.getOrderNos().get(0));  //默认取第一个
        result.setTitle("待支付金额：" +Math.abs(result.getAmt()) + "，订单号："  + result.getOrderNo());
        result.setAccountPayAbles(accountPayAbles);
        //支付显示 文案处理   去掉文案。
//        cashierPayService.handCopywriting(result,orderPayReqVO);
        return result;
	}

	public String getPaySignStr(OrderPaySignBatchReqVO orderPaySign, OrderPayCallBack payCallbackService) {
		//1校验
        Assert.notNull(orderPaySign, ErrorCode.PARAMETER_ERROR.getText());
        orderPaySign.check();
        //3 查询应付
        OrderPayBatchReqVO orderPayReqVO = new OrderPayBatchReqVO();
        BeanUtils.copyProperties(orderPaySign,orderPayReqVO);
        OrderPayableAmountResVO orderPayable = getOrderPayableAmount(orderPayReqVO);
        
        //7 签名串
        OrderPaySignReqVO orderPaySignSimple = new OrderPaySignReqVO(); //简化对象
        BeanUtils.copyProperties(orderPaySign,orderPaySignSimple);
        orderPaySignSimple.setOrderNo(orderPaySign.getOrderNos().get(0));  //默认取第一个对象
        
        List<PayVo> payVo = cashierPayService.getOrderPayVO(orderPaySignSimple,orderPayable);
        log.info("CashierPayService 加密前费用列表打印 getPaySignStr payVo [{}] ",GsonUtils.toJson(payVo));
        if(CollectionUtils.isEmpty(payVo)){
            throw new OrderPaySignFailException();
        }
        String signStr = cashierNoTService.getPaySignByPayVos(payVo);
        return signStr;
	}

}
