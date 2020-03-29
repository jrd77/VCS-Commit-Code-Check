/**
 * 
 */
package com.atzuche.order.coreapi.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountrenterrentcost.service.AccountRenterCostSettleService;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.cashieraccount.vo.res.AccountPayAbleResVO;
import com.atzuche.order.commons.enums.YesNoEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.SupplementCheckReqVO;
import com.atzuche.order.commons.vo.req.SupplementReqVO;
import com.atzuche.order.rentercost.entity.OrderSupplementDetailEntity;
import com.atzuche.order.rentercost.entity.vo.PayableVO;
import com.atzuche.order.rentercost.service.OrderSupplementDetailService;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jing.huang
 *
 */
@Slf4j
@RequestMapping("/order/supplement")
@RestController
@AutoDocVersion(version = "订单接口文档")
public class OrderSupplementDetailController {
	@Autowired
	private OrderSupplementDetailService orderSupplementDetailService;
	@Autowired
	private CashierNoTService cashierNoTService;
	@Autowired
	private RenterOrderCostCombineService renterOrderCostCombineService;
	@Autowired
	private AccountRenterCostSettleService accountRenterCostSettleService;
	
	//参考CashierBatchPayService
	@AutoDocMethod(description = "查询补付记录", value = "查询补付记录", response = OrderSupplementDetailEntity.class)
    @PostMapping("/querySupplementByMemNo")
    public ResponseData<List<OrderSupplementDetailEntity>> querySupplementByMemNo(@Valid @RequestBody SupplementReqVO vo, BindingResult bindingResult) throws Exception {
		log.info("querySupplementByMemNo:params=[{}]", JSON.toJSONString(vo));
        try {
        	List<OrderSupplementDetailEntity> lsEntity = orderSupplementDetailService.listOrderSupplementDetailByMemNo(vo.getMemNo());
        	if(lsEntity == null) {
        		lsEntity = new ArrayList<OrderSupplementDetailEntity>();
        	}else {
        		for (OrderSupplementDetailEntity orderSupplementDetailEntity : lsEntity) {
        			//正负号取反
        			orderSupplementDetailEntity.setAmt(-orderSupplementDetailEntity.getAmt());
				}
        	}
        	log.info("1.order supplement[op_status = 1 AND pay_flag IN (1,4,5)] size=[{}],params=[{}]",lsEntity.size(),GsonUtils.toJson(vo));
        	
        	//1 查询子单号 ,根据会员号查询。区别:数据源
	        List<RenterOrderEntity> listRenterOrderEntity = cashierNoTService.getRenterOrderNoByMemNo(vo.getMemNo());
	        //管理后台修改订单
	        handleListRenterOrderEntity(vo.getMemNo(), lsEntity, listRenterOrderEntity);
	        log.info("2.order supplement from console modify order size=[{}],params=[{}]",lsEntity.size(),GsonUtils.toJson(vo));
	        
	      //支付欠款,跟子订单号无关。根据会员号查询。区别:数据源
			List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableDebtPayVOByMemNo(vo.getMemNo());
            //已付租车费用(shifu  租车费用的实付)
            handleRentIncrementDebtAmt(vo.getMemNo(), lsEntity, payableVOs);
            log.info("3.order supplement[order_debt_amt + repaid_debt_amt <>0 ] from order debt size=[{}],params=[{}]",lsEntity.size(),GsonUtils.toJson(vo));
	        
            log.info("4.lsEntity result=[{}]",GsonUtils.toJson(lsEntity));
            return ResponseData.success(lsEntity);
		} catch (Exception e) {
			log.error("查询补付记录异常:params=[{}]",GsonUtils.toJson(vo),e);
			return ResponseData.error();
		}
	}

	
	
	@AutoDocMethod(description = "Check查询补付记录", value = "Check查询补付记录", response = OrderSupplementDetailEntity.class)
    @PostMapping("/queryCheckSupplementByMemNo")
    public ResponseData<List<OrderSupplementDetailEntity>> queryCheckSupplementByMemNo(@Valid @RequestBody SupplementCheckReqVO vo, BindingResult bindingResult) throws Exception {
		log.info("queryCheckSupplementByMemNo:params=[{}]", JSON.toJSONString(vo));
        try {
        	List<OrderSupplementDetailEntity> lsEntity = orderSupplementDetailService.listOrderSupplementDetailByMemNoAndOrderNos(vo.getMemNo(), vo.getOrderNoList());
        	if(lsEntity == null) {
        		lsEntity = new ArrayList<OrderSupplementDetailEntity>();
        	} else {
        		for (OrderSupplementDetailEntity orderSupplementDetailEntity : lsEntity) {
        			//正负号取反
        			orderSupplementDetailEntity.setAmt(-orderSupplementDetailEntity.getAmt());
				}
        	}
        	log.info("1.check order supplement[op_status = 1 AND pay_flag IN (1,4,5)] size=[{}],params=[{}]",lsEntity.size(),GsonUtils.toJson(vo));
        	
        	//1 查询子单号 根据会员号和订单号列表查询。区别:数据源
	        List<RenterOrderEntity> listRenterOrderEntity = cashierNoTService.getRenterOrderNoByMemNoAndOrderNos(vo.getMemNo(),vo.getOrderNoList());
	        //管理后台修改订单
	        handleListRenterOrderEntity(vo.getMemNo(), lsEntity, listRenterOrderEntity);
	        log.info("2.check order supplement from console modify order size=[{}],params=[{}]",lsEntity.size(),GsonUtils.toJson(vo));
	        
	        //支付欠款,跟子订单号无关。根据会员号和订单号列表查询。区别:数据源
			List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableDebtPayVOByMemNoAndOrderNos(vo.getMemNo(),vo.getOrderNoList());
            //已付租车费用(shifu  租车费用的实付)
            handleRentIncrementDebtAmt(vo.getMemNo(), lsEntity, payableVOs);
            log.info("3.check order supplement[order_debt_amt + repaid_debt_amt <>0 ] from order debt size=[{}],params=[{}]",lsEntity.size(),GsonUtils.toJson(vo));
            
            log.info("4.check lsEntity result=[{}]",GsonUtils.toJson(lsEntity));
        	return ResponseData.success(lsEntity);
		} catch (Exception e) {
			log.error("查询补付记录异常:params=[{}]",GsonUtils.toJson(vo),e);
			return ResponseData.error();
		}
	}



	private void handleListRenterOrderEntity(String memNo, List<OrderSupplementDetailEntity> lsEntity,List<RenterOrderEntity> listRenterOrderEntity) {
		
		for (RenterOrderEntity renterOrderEntity : listRenterOrderEntity) {
			//管理后台修改补付
		    if(renterOrderEntity != null) {
		    	String orderNo = renterOrderEntity.getOrderNo();
		        List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableGlobalVO(orderNo,renterOrderEntity.getRenterOrderNo(),memNo);
		        //应付租车费用（已经求和）
		        int rentAmtAfter = cashierNoTService.sumRentOrderCost(payableVOs);
		        
		        //已付租车费用(shifu  租车费用的实付)
		        int rentAmtPayed = accountRenterCostSettleService.getCostPaidRent(orderNo,memNo);
		        log.info("租客[orderNo={},memNo={}]正常实收:[toPay={}]",orderNo,memNo,rentAmtPayed);

		        if(!CollectionUtils.isEmpty(payableVOs) && rentAmtAfter+rentAmtPayed < 0){   // 
		            for(int i=0;i<payableVOs.size();i++){
		                PayableVO payableVO = payableVOs.get(i);
		                //判断是租车费用、还是补付 租车费用 并记录 详情
		                RenterCashCodeEnum type = RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AFTER;	                    
		                //数据封装,正负号取反,需要加上已经实付金额 rentAmtPayed
		                OrderSupplementDetailEntity entity = orderSupplementDetailService.handleConsoleData(-(payableVO.getAmt()+rentAmtPayed), type, memNo, orderNo);
		                if(entity != null) {
		            		lsEntity.add(entity);
		            		log.info("当前实收和应付之间的差额订单信息:需补付 entity=[{}]",entity.toString());
		            	}
		            }
		        }
		    }
		}
	}
	
	private void handleRentIncrementDebtAmt(String memNo, List<OrderSupplementDetailEntity> lsEntity, List<PayableVO> payableVOs) {
		 //应付租车费用（已经求和）
        int rentIncrementDebtAmt = cashierNoTService.sumRentOrderCost(payableVOs);
		if(!CollectionUtils.isEmpty(payableVOs) && rentIncrementDebtAmt < 0){
		    for(int i=0;i<payableVOs.size();i++){
		        PayableVO payableVO = payableVOs.get(i);
		        //判断是租车费用、还是补付 租车费用 并记录 详情
		        RenterCashCodeEnum type = RenterCashCodeEnum.ACCOUNT_RENTER_DEBT_COST_AGAIN;	                    
		        //数据封装,正负号取反
		        OrderSupplementDetailEntity entity = orderSupplementDetailService.handleDebtData(-payableVO.getAmt(), type, memNo, payableVO.getOrderNo());
		        if(entity != null) {
		    		lsEntity.add(entity);
		    		log.info("当前订单欠款信息:需补付 entity=[{}]",entity.toString());
		    	}
		    }
		}
	}
	
	
}
