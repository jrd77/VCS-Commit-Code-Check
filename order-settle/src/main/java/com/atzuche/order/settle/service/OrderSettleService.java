package com.atzuche.order.settle.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleDetailEntity;
import com.atzuche.order.accountrenterdeposit.vo.res.AccountRenterDepositResVO;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleEntity;
import com.atzuche.order.accountrenterrentcost.service.AccountRenterCostSettleService;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostDetailNoTService;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostEntity;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositCostNoTService;
import com.atzuche.order.accountrenterwzdepost.vo.res.AccountRenterWZDepositResVO;
import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.cashieraccount.service.MemberSecondSettleService;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.enums.ErrorCode;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import com.atzuche.order.commons.service.OrderPayCallBack;
import com.atzuche.order.commons.vo.res.RenterCostVO;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercost.entity.OrderSupplementDetailEntity;
import com.atzuche.order.rentercost.service.OrderSupplementDetailService;
import com.atzuche.order.renterwz.service.RenterOrderWzCostDetailService;
import com.atzuche.order.settle.exception.CancelOrderSettleParamException;
import com.atzuche.order.settle.exception.OrderSettleFlatAccountException;
import com.atzuche.order.settle.service.notservice.OrderOwnerSettleNoTService;
import com.atzuche.order.settle.service.notservice.OrderSettleNoTService;
import com.atzuche.order.settle.vo.req.CancelOrderReqDTO;
import com.atzuche.order.settle.vo.req.OwnerCosts;
import com.atzuche.order.settle.vo.req.RentCosts;
import com.atzuche.order.settle.vo.req.SettleCancelOrdersAccount;
import com.atzuche.order.settle.vo.req.SettleOrders;
import com.atzuche.order.settle.vo.req.SettleOrdersDefinition;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import lombok.extern.slf4j.Slf4j;

/**
 * 车辆结算
 * @author haibao.yan
 */
@Service
@Slf4j
public class OrderSettleService{
    @Autowired private OrderStatusService orderStatusService;
    @Autowired private OrderSettleNoTService orderSettleNoTService;
    @Autowired private CashierService cashierService;
    @Autowired private OrderSettleNewService orderSettleNewService;
//    @Autowired private RenterOrderService renterOrderService;
//    @Autowired private CashierSettleService cashierSettleService;
    @Autowired private CashierRefundApplyNoTService cashierRefundApplyNoTService;
//    @Autowired private CashierPayService cashierPayService;
//    @Autowired private CashierQueryService cashierQueryService;
    @Autowired private OrderSupplementDetailService orderSupplementDetailService;
    @Autowired private OwnerOrderSettleService ownerOrderSettleService;
    @Autowired private RenterOrderSettleService renterOrderSettleService;
    @Autowired
    private OrderOwnerSettleNoTService orderOwnerSettleNoTService;
    @Autowired
    private RemoteOldSysDebtService remoteOldSysDebtService;
    @Autowired
    private AccountRenterCostSettleService accountRenterCostSettleService;
    @Autowired
    private AccountRenterWzDepositCostNoTService accountRenterWzDepositCostNoTService;
    @Autowired
    private RenterOrderWzCostDetailService renterOrderWzCostDetailService;
    @Autowired
    private AccountRenterCostDetailNoTService accountRenterCostDetailNoTService;
    @Autowired
    private MemberSecondSettleService memberSecondSettleService;
    
    /**
     * 查询所以费用
     */
    public RenterCostVO getRenterCostByOrderNo(String orderNo,String renterOrderNo,String renterNo,Integer renterCostAmtFinalForYingshou){
    	log.info("getRenterCostByOrderNo params orderNo=[{}],renterOrderNo=[{}],renterNo=[{}],renterCostAmtFinalForYingshou=[{}]",orderNo,renterOrderNo,renterNo,renterCostAmtFinalForYingshou);
//        RenterOrderEntity renterOrder = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
//        Assert.notNull(renterOrder,"订单信息不存在");
//        Assert.notNull(renterOrder.getRenterOrderNo(),"订单信息不存在");
    	
    	if(renterCostAmtFinalForYingshou == null) {
    		renterCostAmtFinalForYingshou = 0;//设置默认值。
    	}
    	
        RenterCostVO vo = new RenterCostVO();
        vo.setOrderNo(orderNo);
        
     // 实付 租车费用
//      int renterCostAmtEd = cashierQueryService.getRenterCost(orderNo,renterNo);
	  	AccountRenterCostSettleEntity accountRenterCostSettleEntity = accountRenterCostSettleService.selectByOrderNo(orderNo,renterNo);
	  	OrderStatusEntity orderStatus = orderStatusService.getByOrderNo(orderNo);
	  	int feeShishouOri = 0;
	  	int feeYingkouOri = 0;
	  	int feeYingtui = 0;
	  	//renterCostAmtFinalForYingshou  yingshou
	  	
	  	//非空处理
	  	if(accountRenterCostSettleEntity != null) {
	  		feeShishouOri = accountRenterCostSettleEntity.getShifuAmt()!=null?accountRenterCostSettleEntity.getShifuAmt():0;
	  		//结算前：租车费用应扣等于应收
	  		//需要根据结算状态来标记,已结算存在金额为0的情况
	  		if(SettleStatusEnum.SETTLEING.getCode() == orderStatus.getSettleStatus() || SettleStatusEnum.SETTLEING.getCode() == orderStatus.getCarDepositSettleStatus() ){
	  			//未结算
//	  			feeYingkouOri = accountRenterCostSettleEntity.getYingkouAmt() != null && accountRenterCostSettleEntity.getYingkouAmt() != 0?Math.abs(accountRenterCostSettleEntity.getYingkouAmt()):Math.abs(renterCostAmtFinalForYingshou);  //取绝对值。
	  			feeYingkouOri = Math.abs(renterCostAmtFinalForYingshou);
	  		}else {
	  			//已结算,存在金额为0的情况。
	  			//已经结算
	  			feeYingkouOri = accountRenterCostSettleEntity.getYingkouAmt() != null?Math.abs(accountRenterCostSettleEntity.getYingkouAmt()):0;  //取绝对值。
	  		}
	  		
	  		
	  	}
	  	log.info("feeShishouOri=[{}],feeYingkouOri=[{}],orderNo=[{}],memNo=[{}]",feeShishouOri,feeYingkouOri,orderNo,renterNo);
	  	
	  	//实收(必定是消费，无免押预授权的情况。)
	      int feeShishou = Math.abs(feeShishouOri);
	      //应扣
	      int feeYingkou = Math.abs(feeYingkouOri);
	      //计算应退
	      if(feeShishou >= feeYingkou) {
	      	feeYingtui = feeShishou - feeYingkou;
	      }
	      log.info("feeShishou=[{}],feeYingkou=[{}],feeYingshou=[{}],feeYingtui=[{}],orderNo=[{}],memNo=[{}]",feeShishou,feeYingkou,renterCostAmtFinalForYingshou,feeYingtui,orderNo,renterNo);
	      //应收
	      vo.setRenterCostFeeYingshou(Math.abs(renterCostAmtFinalForYingshou));
	      vo.setRenterCostFeeShishou(feeShishou);
	      vo.setRenterCostFeeYingtui(feeYingtui);
	      vo.setRenterCostFeeYingkou(feeYingkou);
      
      
        //车辆押金
        AccountRenterDepositResVO accountRenterDepositResVO = cashierService.getRenterDepositEntity(orderNo,renterNo);
//        vo.setDepositCost(Math.abs(accountRenterDepositResVO.getSurplusDepositAmt()));
        int depositShishouOri = 0;
        int depositYingshouOri = 0;
        
        int depositYingkouOri = 0; //应扣
        int depositYingtuiOri = 0;
        int depositShishouAuthOri = 0;
        
        if(accountRenterDepositResVO != null) {
        	depositShishouOri = accountRenterDepositResVO.getShifuDepositAmt()!=null?Math.abs(accountRenterDepositResVO.getShifuDepositAmt()):0;
        	depositYingshouOri = accountRenterDepositResVO.getYingfuDepositAmt()!=null?Math.abs(accountRenterDepositResVO.getYingfuDepositAmt()):0;
        	//普通预授权
        	if(accountRenterDepositResVO.getIsAuthorize() != null && accountRenterDepositResVO.getIsAuthorize() == 1) {
        		depositShishouAuthOri = accountRenterDepositResVO.getAuthorizeDepositAmt()!=null?Math.abs(accountRenterDepositResVO.getAuthorizeDepositAmt()):0;
        	}else if(accountRenterDepositResVO.getIsAuthorize() != null && accountRenterDepositResVO.getIsAuthorize() == 2){
        		//信用预授权，一半一半的情况。
        		int tmpAuthOri = accountRenterDepositResVO.getAuthorizeDepositAmt()!=null?Math.abs(accountRenterDepositResVO.getAuthorizeDepositAmt()):0;
        		depositShishouAuthOri = accountRenterDepositResVO.getCreditPayAmt()!=null?Math.abs(accountRenterDepositResVO.getCreditPayAmt()):0;
        		//累加
        		depositShishouAuthOri += tmpAuthOri;
        	}
        	
        }
        log.info("depositShishouOri=[{}],depositYingshouOri=[{}],depositShishouAuthOri=[{}],orderNo=[{}],memNo=[{}]",depositShishouOri,depositYingshouOri,depositShishouAuthOri,orderNo,renterNo);
        
        //应扣取值
        //结算前：默认按应收和应扣来处理，不干涉到车辆
        //包含结算后的 结算金额为0的情况。
//        if(accountRenterCostSettleEntity != null) {
//	        if(accountRenterCostSettleEntity.getYingkouAmt() != null && accountRenterCostSettleEntity.getYingkouAmt() != 0) {
        		
        		//计算应退-PART2
		        if(feeYingkouOri > feeShishou) {
		        	//费用不够的情况下从租车押金中扣除。
		        	depositYingkouOri = feeYingkouOri - feeShishou;
		        	//原来的车辆费用中的应扣需要修改成实收,多出的部分从车辆押金中扣除。
		        	//应扣大于实收,应扣小于等于实收
		        	feeYingkou = feeShishou;
		        	//重新赋值。
		        	vo.setRenterCostFeeYingkou(feeYingkou);
		        	//depositYingkouOri 大于 shishou的情况，累计到违章押金中扣除。
		        }
//	        }
//        }
		        
        log.info("depositShishouOri=[{}],depositYingshouOri=[{}],depositShishouAuthOri=[{}],depositYingkouOri=[{}],orderNo=[{}],memNo=[{}]",depositShishouOri,depositYingshouOri,depositShishouAuthOri,depositYingkouOri,orderNo,renterNo);
        
        //计算应退
        if(depositShishouOri >= depositYingkouOri) {
        	depositYingtuiOri = depositShishouOri - depositYingkouOri;
        } else {  
        	//应扣大于实收,应扣小于等于实收
        	//处理预授权的情况。200420
        	if(accountRenterDepositResVO.getIsAuthorize()!=null && accountRenterDepositResVO.getIsAuthorize().intValue() != 0) {
        		depositYingkouOri = depositShishouOri;
        	}
        }
        log.info("depositShishouOri=[{}],depositYingshouOri=[{}],depositShishouAuthOri=[{}],depositYingkouOri=[{}],depositYingtuiOri=[{}],orderNo=[{}],memNo=[{}]",depositShishouOri,depositYingshouOri,depositShishouAuthOri,depositYingkouOri,depositYingtuiOri,orderNo,renterNo);

        vo.setDepositCostShishou(depositShishouOri);
        vo.setDepositCostYingshou(depositYingshouOri);
        vo.setDepositCostYingkou(depositYingkouOri);
        vo.setDepositCostYingtui(depositYingtuiOri);
        vo.setDepositCostShishouAuth(depositShishouAuthOri);
        
        
        //违章押金
//        int rentWzDepositAmt = cashierSettleService.getSurplusWZDepositCostAmt(orderNo,renterNo);
        AccountRenterWZDepositResVO accountRenterWZDeposit = cashierService.getRenterWZDepositEntity(orderNo,renterNo);
//        vo.setDepositWzCost(Math.abs(rentWzDepositAmt));
        int wzShishouOri = 0;  //收银台落库的时候，做处理。
        int wzYingshouOri = 0;
        int wzYingkouOri = 0; //应扣
        int wzYingtuiOri = 0;
        int wzShishouAuthOri = 0;
        if(accountRenterWZDeposit != null) {
        	wzShishouOri = accountRenterWZDeposit.getShishouDeposit()!=null?Math.abs(accountRenterWZDeposit.getShishouDeposit()):0;
        	wzYingshouOri = accountRenterWZDeposit.getYingshouDeposit()!=null?Math.abs(accountRenterWZDeposit.getYingshouDeposit()):0;
        	if(accountRenterWZDeposit.getIsAuthorize() != null && accountRenterWZDeposit.getIsAuthorize() == 1) {
        		wzShishouAuthOri = accountRenterWZDeposit.getAuthorizeDepositAmt()!=null?Math.abs(accountRenterWZDeposit.getAuthorizeDepositAmt()):0;
        	}else if(accountRenterWZDeposit.getIsAuthorize() != null && accountRenterWZDeposit.getIsAuthorize() == 2) {
        		//信用预授权，一半一半的情况。
        		int tmpAuthOri = accountRenterWZDeposit.getAuthorizeDepositAmt()!=null?Math.abs(accountRenterWZDeposit.getAuthorizeDepositAmt()):0;
        		wzShishouAuthOri = accountRenterWZDeposit.getAuthorizeDepositAmt()!=null?Math.abs(accountRenterWZDeposit.getCreditPayAmt()):0;
        		//累加
        		wzShishouAuthOri += tmpAuthOri;
        	}
        }
        log.info("wzShishouOri=[{}],wzYingshouOri=[{}],wzShishouAuthOri=[{}],orderNo=[{}],memNo=[{}]",wzShishouOri,wzYingshouOri,wzShishouAuthOri,orderNo,renterNo);
        
        //应扣
        AccountRenterWzDepositCostEntity wzEntity = accountRenterWzDepositCostNoTService.queryWzDeposit(orderNo,renterNo);
        if(wzEntity != null) {
        	//结算前：应扣等于0，应收等于应退，默认押金是要退的。
        	if(SettleStatusEnum.SETTLEING.getCode() == orderStatus.getWzSettleStatus()) {
        		//结算前，默认取值方式。
        		Integer wzYingkouOriSum = renterOrderWzCostDetailService.sumQuerySettleInfoByOrder(orderNo);
        		if(wzYingkouOriSum != null) {
        			wzYingkouOri = wzYingkouOriSum.intValue();
        		}
        	}else {
        		//结算后
        		wzYingkouOri = wzEntity.getYingkouAmt() !=null?Math.abs(wzEntity.getYingkouAmt()):0;  //负数 取绝对值   wzYingshouOri
        	}
        	//wzYingkouOri 为0，代表的是结算前，从renter_order_wz_cost_detail
        	//需要根据结算状态来取，case:结算的时候，yingkou_amt本身就是0的情况。
        	
        	
        }
        log.info("wzShishouOri=[{}],wzYingshouOri=[{}],wzShishouAuthOri=[{}],wzYingkouOri=[{}],orderNo=[{}],memNo=[{}]",wzShishouOri,wzYingshouOri,wzShishouAuthOri,wzYingkouOri,orderNo,renterNo);
        
        //计算应退
        if(wzShishouOri >= wzYingkouOri) {
        	wzYingtuiOri = wzShishouOri - wzYingkouOri;
        } else {
        	//应扣大于实收,应扣小于等于实收
        	//处理预授权的情况。200420
        	if(accountRenterWZDeposit.getIsAuthorize().intValue() != 0) {
        		wzYingkouOri = wzShishouOri;
        	}
        }
        log.info("wzShishouOri=[{}],wzYingshouOri=[{}],wzShishouAuthOri=[{}],wzYingkouOri=[{}],wzYingtuiOri=[{}],orderNo=[{}],memNo=[{}]",wzShishouOri,wzYingshouOri,wzShishouAuthOri,wzYingkouOri,wzYingtuiOri,orderNo,renterNo);
        
        vo.setDepositWzCostShishou(wzShishouOri);
        vo.setDepositWzCostYingshou(wzYingshouOri);
        vo.setDepositWzCostYingkou(wzYingkouOri);
        vo.setDepositWzCostYingtui(wzYingtuiOri);
        //预授权
        vo.setDepositWzCostShishouAuth(wzShishouAuthOri);
        
        
        
        //避免重复调用。200306
//        RentCosts rentCosts = preRenterSettleOrder(orderNo, renterOrderNo);
//        log.info("查询租客应收 getRenterCostByOrderNo rentCosts [{}]",GsonUtils.toJson(rentCosts));
        //租车费用
//        if(Objects.nonNull(rentCosts)){
            //应付,超里程算重复了。
//            int yingfuAmt =  orderSettleNewService.getYingfuRenterCost(rentCosts);
        	//代码重构 200309 huangjing
//        	int yingfuAmt = rentCosts.getRenterCostAmtFinal();
        	
            
            
            
//            int renterCost = yingfuAmt + renterCostAmtEd;
//            vo.setRenterCost(renterCost>0?renterCost:0);
            
            
            //统计补付
//            List<AccountRenterCostDetailEntity> renterCostDetails = cashierQueryService.getRenterCostDetails(orderNo);
//            if(!CollectionUtils.isEmpty(renterCostDetails)){
            
            //补付
                List<OrderSupplementDetailEntity> orderSupplementDetails = orderSupplementDetailService.listOrderSupplementDetailByOrderNo(orderNo);
                if(!CollectionUtils.isEmpty(orderSupplementDetails)){
                    int renterCostBufuShishou = orderSupplementDetails.stream().filter(obj ->{
                        return Objects.nonNull(obj.getOpStatus()) && obj.getOpStatus()==1&&
//                                Objects.nonNull(obj.getCashType()) && obj.getCashType()==1&&
                                Objects.nonNull(obj.getPayFlag()) && obj.getPayFlag()==3
                                ;
                    }).mapToInt(OrderSupplementDetailEntity::getAmt).sum();
                    
                    int renterCostBufuYingfu = orderSupplementDetails.stream()./*filter(obj ->{
                        return
                                ( Objects.nonNull(obj.getOpStatus()) && obj.getOpStatus()==1&&
                                Objects.nonNull(obj.getCashType()) && obj.getCashType()==1)  &&

                                (Objects.nonNull(obj.getPayFlag()) && obj.getPayFlag()==1 ||
                                Objects.nonNull(obj.getPayFlag()) && obj.getPayFlag()==0 ||
                                Objects.nonNull(obj.getPayFlag()) && obj.getPayFlag()==4 ||
                                Objects.nonNull(obj.getPayFlag()) && obj.getPayFlag()==5 )
                                ;
                    }).*/mapToInt(OrderSupplementDetailEntity::getAmt).sum();
                    
                    vo.setRenterCostBufuYingshou(renterCostBufuYingfu);
                    vo.setRenterCostBufuShishou(Math.abs(renterCostBufuShishou));
                    
                    log.info("renterCostBufuYingfu=[{}],renterCostBufuShishou=[{}],orderNo=[{}],memNo=[{}]",renterCostBufuYingfu,renterCostBufuShishou,orderNo,renterNo);
                    
                }
//            }
            
//        }
            
        //===================================== 退款部分 yingtui shitui  =====================================
    	int feeShituiOri = 0;
    	int feeShikouOri = 0;
    	
    	int wzShituiOri = 0;
    	int wzShikouOri = 0;
    	
    	int depositShituiOri = 0;
    	int depositShikouOri = 0;
        
        List<CashierRefundApplyEntity> cashierRefundApplys = cashierRefundApplyNoTService.getRefundApplyByOrderNo(orderNo);
        if(!CollectionUtils.isEmpty(cashierRefundApplys)){
           // 获取实退 租车费用
//            int renterCostReal =  cashierRefundApplys.stream().filter(obj ->{
//                return DataPayKindConstant.RENT_AMOUNT.equals(obj.getPayKind()) || DataPayKindConstant.RENT_INCREMENT.equals(obj.getPayKind()) || DataPayKindConstant.RENT_AMOUNT_AFTER.equals(obj.getPayKind());
//            }).mapToInt(CashierRefundApplyEntity::getAmt).sum();
            
            for (CashierRefundApplyEntity obj : cashierRefundApplys) {
            	//一级分类
            	if(DataPayKindConstant.RENT_AMOUNT.equals(obj.getPayKind()) || DataPayKindConstant.RENT_INCREMENT.equals(obj.getPayKind()) || DataPayKindConstant.RENT_AMOUNT_AFTER.equals(obj.getPayKind())) {
            		//二级分类
            		//租车费用
                	//消费方式
            		if("00".equals(obj.getStatus())){
            			//三级分类
            			if("04".equals(obj.getPayType())) {
            				feeShituiOri += Math.abs(obj.getAmt());
            			}else if("03".equals(obj.getPayType())){     //预授权方式，实扣
            				feeShikouOri += Math.abs(obj.getAmt());
            			}
    				}
            	}else if(DataPayKindConstant.DEPOSIT.equals(obj.getPayKind())) {
    				//违章押金,32预授权的不处理
                	//消费方式
            		if("00".equals(obj.getStatus())){
            			if("04".equals(obj.getPayType())) {
            				wzShituiOri += Math.abs(obj.getAmt());
            			}else if("03".equals(obj.getPayType())) {   ////预授权方式，实扣
            				wzShikouOri += Math.abs(obj.getAmt());
            			}
            		}
            	}else if(DataPayKindConstant.RENT.equals(obj.getPayKind())) {
            		//租车押金,32预授权的不处理
    				//消费方式
            		if("00".equals(obj.getStatus())){
            			if("04".equals(obj.getPayType())) {
            				depositShituiOri += Math.abs(obj.getAmt());
            			}else if("03".equals(obj.getPayType())) {   ////预授权方式，实扣
            				depositShikouOri += Math.abs(obj.getAmt());
            			}
            		}
            	}
            	
			}
            
            //租车费用
            //实扣
            //只有结算后才有退款记录
            if(SettleStatusEnum.SETTLEING.getCode() != orderStatus.getCarDepositSettleStatus()) {  //已结算
            	
                //钱包退款目前不在退款申请表中，单独查询。200423  查询的是租车费用表。
                int walletRefundAmt = accountRenterCostDetailNoTService.getRentCostRefundByWallet(orderNo, renterNo);
                log.info("1.getRentCostRefundByWallet params orderNo=[{}],renterNo=[{}],walletRefundAmt=[{}]",orderNo,renterNo,walletRefundAmt);
                feeShituiOri += Math.abs(walletRefundAmt);
                log.info("2.getRentCostRefundByWallet params orderNo=[{}],renterNo=[{}],walletRefundAmt=[{}],feeShituiOri=[{}]",orderNo,renterNo,walletRefundAmt,feeShituiOri);
                
	            if(feeShishou > 0 && feeShituiOri >= 0) {  //只有消费的情况。   feeShituiOri等于0代表的是没有退款记录，实扣
	            	//全退的情况
	            	if(feeShishou >= feeShituiOri) {
	            		feeShikouOri += feeShishou - feeShituiOri;
	            	}
	            }
            }
            log.info("feeShikouOri=[{}],feeShituiOri=[{}],orderNo=[{}],memNo=[{}]",feeShikouOri,feeShituiOri,orderNo,renterNo);
            vo.setRenterCostFeeShikou(feeShikouOri);
            //实退
            vo.setRenterCostFeeShitui(feeShituiOri); //预授权0
            
            //租车押金
            //实扣, 实收大于0代表的是消费。预授权默认实收为0
            //只有结算后才有退款记录
            if(SettleStatusEnum.SETTLEING.getCode() != orderStatus.getSettleStatus()) { //已结算
	            if(depositShishouOri > 0 && depositShituiOri >= 0) {
	            	if(depositShishouOri >= depositShituiOri) {
	            		depositShikouOri += depositShishouOri - depositShituiOri;
	            	}
	            }
            }
            log.info("depositShikouOri=[{}],depositShituiOri=[{}],orderNo=[{}],memNo=[{}]",depositShikouOri,depositShituiOri,orderNo,renterNo);
            vo.setDepositCostShikou(depositShikouOri);
            vo.setDepositCostShitui(depositShituiOri);
            
            //违章押金
            //实扣, 实收大于0代表的是消费。预授权默认实收为0
            //只有结算后才有退款记录
            if(SettleStatusEnum.SETTLEING.getCode() != orderStatus.getWzSettleStatus()) { //已结算
	            if(wzShishouOri > 0 && wzShituiOri >= 0) {
	            	if(wzShishouOri >= wzShituiOri) {
	            		wzShikouOri += wzShishouOri - wzShituiOri;
	            	}
	            }
            }
            log.info("wzShikouOri=[{}],wzShituiOri=[{}],orderNo=[{}],memNo=[{}]",wzShikouOri,wzShituiOri,orderNo,renterNo);
            vo.setDepositWzCostShikou(wzShikouOri);
            vo.setDepositWzCostShitui(wzShituiOri);
            
            // 获取实退 车俩押金
//            int depositCostReal =  cashierRefundApplys.stream().filter(obj ->{
//                return DataPayKindConstant.RENT.equals(obj.getPayKind());
//            }).mapToInt(CashierRefundApplyEntity::getAmt).sum();
//            vo.setDepositCostReal(depositCostReal);
            
            // 获取实退 违章押金
//            int depositWzCostReal =  cashierRefundApplys.stream().filter(obj ->{
//                return DataPayKindConstant.DEPOSIT.equals(obj.getPayKind());
//            }).mapToInt(CashierRefundApplyEntity::getAmt).sum();
//            vo.setDepositWzCostReal(depositWzCostReal);
        }


        return vo;
    }
    /**
     * 获取租客预结算数据 huangjing
     * @param orderNo
     */
    public RentCosts preRenterSettleOrder(String orderNo,String renterOrderNo) {
    	SettleOrders settleOrders =  orderSettleNoTService.preInitSettleOrders(orderNo,renterOrderNo,null);
    	//3.4 查询所有租客费用明细
        orderSettleNoTService.getRenterCostSettleDetail(settleOrders);
        
        SettleOrdersDefinition settleOrdersDefinition = new SettleOrdersDefinition();
    	//2统计 车主结算费用明细， 补贴，费用总额
    	orderSettleNoTService.handleRentAndPlatform(settleOrdersDefinition, settleOrders);
    	log.info("preRenterSettleOrder settleOrdersDefinition [{}]",GsonUtils.toJson(settleOrdersDefinition));
    	
    	//租客总账
        List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetails = settleOrdersDefinition.getAccountRenterCostSettleDetails();
        for (AccountRenterCostSettleDetailEntity accountRenterCostSettleDetailEntity : accountRenterCostSettleDetails) {
			log.info("打印租客费用清单:"+accountRenterCostSettleDetailEntity.toString());
		}
        
        //1租客总账
        if(!CollectionUtils.isEmpty(accountRenterCostSettleDetails)){
        	//租客结算的总费用
            int renterCostAmtFinal = accountRenterCostSettleDetails.stream().mapToInt(AccountRenterCostSettleDetailEntity::getAmt).sum();
//            settleOrdersDefinition.setRenterCostAmtFinal(renterCostAmtFinal);
            settleOrders.getRentCosts().setRenterCostAmtFinal(renterCostAmtFinal);
        }
        //封装租客的会员号
        settleOrders.getRentCosts().setRenterNo(settleOrders.getRenterMemNo());
        return settleOrders.getRentCosts();
    }
    
    /**
     * 获取车主预结算数据 huangjing
     * @param orderNo
     */
    public OwnerCosts preOwnerSettleOrder(String orderNo,String ownerOrderNo) {
    	SettleOrders settleOrders =  orderSettleNoTService.preInitSettleOrders(orderNo,null,ownerOrderNo);
    	//1 查询所有租客费用明细  （需要计算车主的平台服务费，需要获取租金）
//        orderSettleNoTService.getRenterCostSettleDetailSimpleForOwnerPlatformSrvFee(settleOrders);
        
        //3.5 查询所有车主费用明细 TODO 暂不支持 多个车主
    	orderOwnerSettleNoTService.getOwnerCostSettleDetail(settleOrders,"pre");//pre  预算

    	//车主预计收益 200214
    	SettleOrdersDefinition settleOrdersDefinition = new SettleOrdersDefinition();
    	//2统计 车主结算费用明细， 补贴，费用总额
    	orderOwnerSettleNoTService.handleOwnerAndPlatform(settleOrdersDefinition,settleOrders);
    	log.info("preOwnerSettleOrder settleOrdersDefinition [{}]",GsonUtils.toJson(settleOrdersDefinition));
        //2车主总账
        List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetails = settleOrdersDefinition.getAccountOwnerCostSettleDetails();
        for (AccountOwnerCostSettleDetailEntity accountOwnerCostSettleDetailEntity : accountOwnerCostSettleDetails) {
			log.info("打印车主费用清单:"+accountOwnerCostSettleDetailEntity.toString());
		}
        if(!CollectionUtils.isEmpty(accountOwnerCostSettleDetails)){
            int ownerCostAmtFinal = accountOwnerCostSettleDetails.stream().mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            settleOrders.getOwnerCosts().setOwnerCostAmtFinal(ownerCostAmtFinal);
        }
        //封装车主会员号 200305 huangjing
        settleOrders.getOwnerCosts().setOwnerNo(settleOrders.getOwnerMemNo());
    	return settleOrders.getOwnerCosts();
    }
    
    public void settleOrder(String orderNo, OrderPayCallBack callBack) {
    	settleOrder(orderNo, callBack, null);
    }
    /**
     * 车辆押金结算(默认租客)
     * 需要平账检测，保持一个入口，仅仅结算结构上做调整。
     * 先注释调事务
     */
    public void settleOrder(String orderNo, OrderPayCallBack callBack,List<String> listOrderNos) {
        log.info("OrderSettleService settleOrder orderNo [{}]",orderNo);
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "车俩结算服务");
        SettleOrders settleOrders = new SettleOrders();
        try {
            Cat.logEvent("settleOrder",orderNo);
            // 初始化操作 校验操作
            orderSettleNoTService.initSettleOrders(orderNo,settleOrders);
            log.info("OrderSettleService settleOrders settleOrders [{}]",GsonUtils.toJson(settleOrders));
            Cat.logEvent("settleOrders",GsonUtils.toJson(settleOrders));
            
            orderOwnerSettleNoTService.initSettleOrdersSeparateOwner(orderNo,settleOrders);
            log.info("OrderSettleService settleOrders settleOrdersSeparateOwner [{}]",GsonUtils.toJson(settleOrders));
            Cat.logEvent("settleOrdersSeparateOwner",GsonUtils.toJson(settleOrders));
            // 检查是否可以结算。 外置。
            orderSettleNoTService.check(settleOrders,listOrderNos);

            // 无事务操作 查询租客车主费用明细 ，处理费用明细到 结算费用明细  并落库   然后平账校验
            SettleOrdersDefinition settleOrdersDefinition = new SettleOrdersDefinition();
            orderSettleNoTService.settleOrderFirst(settleOrders,settleOrdersDefinition);
            log.info("OrderSettleService settleOrdersDefinition [{}]",GsonUtils.toJson(settleOrdersDefinition));
            Cat.logEvent("settleOrders",GsonUtils.toJson(settleOrdersDefinition));
            
            // 无事务操作 查询租客车主费用明细 ，处理费用明细到 结算费用明细  并落库   然后平账校验
            orderOwnerSettleNoTService.settleOrderFirstSeparateOwner(settleOrders,settleOrdersDefinition);
            log.info("OrderSettleService settleOrdersDefinition [{}]",GsonUtils.toJson(settleOrdersDefinition));
            Cat.logEvent("settleOrderSeparateOwner",GsonUtils.toJson(settleOrdersDefinition));
            
            // 平账检测
            // 费用平账 平台收入 + 平台补贴 + 车主费用 + 车主补贴 + 租客费用 + 租客补贴 = 0
            int totleAmt = settleOrdersDefinition.getPlatformProfitAmt() + settleOrdersDefinition.getPlatformSubsidyAmt()
                    + settleOrdersDefinition.getOwnerCostAmt() + settleOrdersDefinition.getOwnerSubsidyAmt()
                    + settleOrdersDefinition.getRentCostAmt() + settleOrdersDefinition.getRentSubsidyAmt();
            if(totleAmt != 0){
                Cat.logEvent("pingzhang","平账失败");
                log.error("平账失败");
                //更新标识。
                updateFailStatusFlag(orderNo,ErrorCode.ORDER_SETTLE_FLAT_ACCOUNT.getText());
                throw new OrderSettleFlatAccountException();
            }

            // 事务操作结算主逻辑  //开启事务
            orderSettleNoTService.settleOrderAfter(settleOrders,settleOrdersDefinition,callBack);
            log.info("OrderSettleService settleOrderAfter [{}]",GsonUtils.toJson(settleOrdersDefinition));
            Cat.logEvent("settleOrderAfter",GsonUtils.toJson(settleOrdersDefinition));
            
            orderOwnerSettleNoTService.settleOrderAfterSeparateOwner(settleOrders,settleOrdersDefinition,callBack);
            log.info("OrderSettleService settleOrderAfterSeparateOwner [{}]",GsonUtils.toJson(settleOrdersDefinition));
            Cat.logEvent("settleOrderAfterSeparateOwner",GsonUtils.toJson(settleOrdersDefinition));
            
            // 调远程增加车辆gps押金
            orderOwnerSettleNoTService.updateCarDeposit(settleOrders);
            // 调远程抵扣老系统租客欠款
            remoteOldSysDebtService.deductBalance(settleOrders.getRenterMemNo(), settleOrders.getRenterTotalOldRealDebtAmt());
            // 调远程抵扣老系统车主欠款
            remoteOldSysDebtService.deductBalance(settleOrders.getOwnerMemNo(), settleOrders.getOwnerTotalOldRealDebtAmt());
            
            orderSettleNewService.sendOrderSettleMq(orderNo,settleOrders.getRenterMemNo(),settleOrders.getRentCosts(),0,settleOrders.getOwnerMemNo(),settleOrders.getRenterOrder());
            
            //记录分流标识(已车主的会员号为准。) 200616
            memberSecondSettleService.initDepositMemberSecondSettle(orderNo, Integer.valueOf(settleOrders.getRenterMemNo()));
            
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            log.error("OrderSettleService settleOrder,orderNo={},",orderNo, e);
            //更新标识。
            updateFailStatusFlag(orderNo,e.getMessage());
            t.setStatus(e);
            Cat.logError("结算失败  :orderNo="+orderNo, e);
            orderSettleNewService.sendOrderSettleMq(orderNo,settleOrders.getRenterMemNo(),settleOrders.getRentCosts(),1,settleOrders.getOwnerMemNo(),settleOrders.getRenterOrder());
            throw new RuntimeException("结算失败 ,不能结算");
        } finally {
            t.complete();
        }
        log.info("OrderPayCallBack payCallBack end " );
    }
    
	private void updateFailStatusFlag(String orderNo,String msg) {
		//更新结算标识
		OrderStatusEntity entity = orderStatusService.getByOrderNo(orderNo);
		OrderStatusEntity record = new OrderStatusEntity();
		record.setId(entity.getId());
		record.setSettleStatus(SettleStatusEnum.SETTL_FAIL.getCode());
		record.setSettleTime(LocalDateTime.now());
		//车辆押金状态
		record.setCarDepositSettleStatus(SettleStatusEnum.SETTL_FAIL.getCode());
		record.setCarDepositSettleTime(LocalDateTime.now());
		//记录结算消息，错误码
		record.setSettleMsg(msg);
		orderStatusService.updateByPrimaryKeySelective(record);
	}
    

    /**
     * 订单取消-组合结算
     *
     **/
    @Transactional(rollbackFor=Exception.class)
    public void orderCancelSettleCombination(CancelOrderReqDTO cancelOrderReqDTO){
        log.info("订单取消结算-START-cancelOrderReqDTO={}", JSON.toJSONString(cancelOrderReqDTO));
        String orderNo = cancelOrderReqDTO.getOrderNo();
        String ownerOrderNo = cancelOrderReqDTO.getOwnerOrderNo();
        String renterOrderNo = cancelOrderReqDTO.getRenterOrderNo();
        if(StringUtils.isEmpty(orderNo)){
            log.error("主订单号不能为空");
            throw new CancelOrderSettleParamException();
        }
        if(cancelOrderReqDTO.isSettleOwnerFlg() && StringUtils.isEmpty(ownerOrderNo)){
            log.error("车主端结算，车主子订单号不能为空");
            throw new CancelOrderSettleParamException();
        }
        if(cancelOrderReqDTO.isSettleRenterFlg() && (StringUtils.isEmpty(renterOrderNo) || StringUtils.isEmpty(ownerOrderNo))){
            log.error("租客端结算，租客子订单号和车主子订单号不能为空");
            throw new CancelOrderSettleParamException();
        }
        if(cancelOrderReqDTO.isSettleRenterFlg()){
            OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
            if(orderStatusEntity != null && SettleStatusEnum.SETTLED.getCode() == (orderStatusEntity.getSettleStatus())){
                log.info("订单已经结算过orderNo={}",orderNo);
                return;
            }
            ownerOrderSettleService.settleOwnerOrderCancel(orderNo,ownerOrderNo);
            renterOrderSettleService.settleRenterOrderCancel(orderNo,renterOrderNo);
        }else{
            ownerOrderSettleService.settleOwnerOrderCancel(orderNo,ownerOrderNo);
        }
        log.info("订单取消结算-END-cancelOrderReqDTO={}", JSON.toJSONString(cancelOrderReqDTO));
    }

}
