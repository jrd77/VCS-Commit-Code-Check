/**
 * 
 */
package com.atzuche.order.coreapi.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.atzuche.order.delivery.vo.delivery.rep.OwnerGetAndReturnCarDTO;
import com.atzuche.order.delivery.vo.delivery.rep.RenterGetAndReturnCarDTO;
import com.atzuche.order.ownercost.entity.ConsoleOwnerOrderFineDeatailEntity;
import com.autoyol.doc.util.StringUtil;
import com.autoyol.platformcost.model.FeeResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.accountrenterdeposit.service.AccountRenterDepositService;
import com.atzuche.order.accountrenterdeposit.vo.res.AccountRenterDepositResVO;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleEntity;
import com.atzuche.order.accountrenterrentcost.service.AccountRenterCostSettleService;
import com.atzuche.order.accountrenterwzdepost.service.AccountRenterWzDepositService;
import com.atzuche.order.accountrenterwzdepost.vo.res.AccountRenterWZDepositResVO;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.commons.enums.DeliveryOrderTypeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.req.OrderCostReqVO;
import com.atzuche.order.commons.vo.res.OrderOwnerCostResVO;
import com.atzuche.order.commons.vo.res.OrderRenterCostResVO;
import com.atzuche.order.commons.vo.res.account.AccountRenterCostDetailResVO;
import com.atzuche.order.commons.vo.res.account.AccountRenterCostSettleResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderCostDetailResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderDeliveryResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderFineDeatailResVO;
import com.atzuche.order.commons.vo.res.cost.RenterOrderSubsidyDetailResVO;
import com.atzuche.order.commons.vo.res.ownercosts.OwnerOrderFineDeatailEntity;
import com.atzuche.order.commons.vo.res.ownercosts.OwnerOrderIncrementDetailEntity;
import com.atzuche.order.commons.vo.res.ownercosts.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.commons.vo.res.ownercosts.OwnerOrderSubsidyDetailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.ConsoleRenterOrderFineDeatailEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.rentercost.entity.OrderSupplementDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.service.OrderSupplementDetailService;
import com.atzuche.order.rentercost.service.RenterOrderCostDetailService;
import com.atzuche.order.rentercost.service.RenterOrderSubsidyDetailService;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.service.OrderCouponService;
import com.atzuche.order.settle.service.OrderSettleService;
import com.atzuche.order.settle.vo.req.OwnerCosts;
import com.atzuche.order.settle.vo.req.RentCosts;

import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jing.huang
 *
 */
@Slf4j
@Service
public class OrderCostService {
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
	private OrderSettleService orderSettleService;     
	@Autowired
	private OrderCouponService orderCouponService;
	@Autowired
	private OrderSupplementDetailService orderSupplementDetailService;
	
	public OrderRenterCostResVO orderCostRenterGet(OrderCostReqVO req){
		OrderRenterCostResVO resVo = new OrderRenterCostResVO();
		
		//参数定义
		String orderNo = req.getOrderNo();
		String memNo = req.getMemNo();
		String renterOrderNo = req.getSubOrderNo();
		//-------------------------------------------------------------------- 以下是主订单费用
		//根据订单号查询封装
		//需补付金额
		try {
			int needIncrementAmt = cashierPayService.getRealRentCost(orderNo, memNo);  //getRealRentCost )  海豹提供
			resVo.setNeedIncrementAmt(needIncrementAmt);
		} catch (Exception e) {
			log.error("实际租车费用:",e);
			resVo.setNeedIncrementAmt(0);
		}
		
		
		
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
        
        //-------------------------------------------------------------------- 以下是子订单费用
        
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
        
	      //-----------------------------------------------------------------
	      AccountRenterCostSettleEntity arcse = accountRenterCostSettleService.selectByOrderNo(orderNo, memNo);
	      AccountRenterCostSettleResVO  renterSettleVo = new AccountRenterCostSettleResVO();
	      if(arcse != null) {
		      	BeanUtils.copyProperties(renterSettleVo, arcse);
		  }
		  resVo.setRenterSettleVo(renterSettleVo);
		  ///油费，超里程，加油服务费
		  RentCosts rentCost = orderSettleService.preRenterSettleOrder(orderNo,renterOrderNo);
//		  com.atzuche.order.commons.vo.res.RentCosts rentCostsReal = new com.atzuche.order.commons.vo.res.RentCosts();
//		  BeanUtils.copyProperties(rentCostsReal, rentCost);
//		  resVo.setRentCosts(rentCostsReal);
		  /**
		   * 简化处理 !!!
		   */
		    /**
		     * 交接车-油费
		     */
            RenterGetAndReturnCarDTO oilAmt = rentCost.getOilAmt();
            com.atzuche.order.commons.vo.res.rentcosts.RenterOrderCostDetailEntity oilAmtReal = new com.atzuche.order.commons.vo.res.rentcosts.RenterOrderCostDetailEntity();
            if(oilAmt != null) {
                BeanUtils.copyProperties(oilAmtReal, oilAmt);
                String oilDifferenceCrash = oilAmt.getOilDifferenceCrash();
                oilDifferenceCrash = StringUtil.isBlank(oilDifferenceCrash)?"0":oilDifferenceCrash;
                //oilDifferenceCrash may be "0.0" format
                oilAmtReal.setTotalAmount((int)Float.parseFloat(oilDifferenceCrash));
            }
		    resVo.setOilAmt(oilAmtReal);

		    /*
		     * 交接车-获取超里程费用
		     */
            FeeResult mileageAmt = rentCost.getMileageAmt();
		    com.atzuche.order.commons.vo.res.rentcosts.RenterOrderCostDetailEntity mileageAmtReal = new com.atzuche.order.commons.vo.res.rentcosts.RenterOrderCostDetailEntity();
		    if(mileageAmt != null) {
		    	BeanUtils.copyProperties(mileageAmtReal, mileageAmt);
                mileageAmtReal.setTotalAmount(mileageAmt.getTotalFee());
		    }
		    resVo.setMileageAmt(mileageAmtReal);
		    
		    /**
		     * 管理后台补贴
		     */
		    List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails = rentCost.getOrderConsoleSubsidyDetails();
		    List<com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetailsReal = new ArrayList<com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity>();
		    if(orderConsoleSubsidyDetails != null) {
		    	orderConsoleSubsidyDetails.stream().forEach(x->{
		    		com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity real = new com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity();
			      		try {
							BeanUtils.copyProperties(real, x);
						} catch (Exception e) {
							log.error("对象属性赋值报错:",e);
						}
			      		orderConsoleSubsidyDetailsReal.add(real);
			          });
			      }
			  resVo.setOrderConsoleSubsidyDetails(orderConsoleSubsidyDetailsReal);
			  
		  
		// 获取修改前租客使用的优惠券列表
		  List<OrderCouponEntity> orderCouponList = orderCouponService.listOrderCouponByRenterOrderNo(renterOrderNo);
		  List<com.atzuche.order.commons.vo.res.rentcosts.OrderCouponEntity> orderCouponListReal = new ArrayList<com.atzuche.order.commons.vo.res.rentcosts.OrderCouponEntity>();
		  if(orderCouponList != null) {
			  orderCouponList.stream().forEach(x->{
				  com.atzuche.order.commons.vo.res.rentcosts.OrderCouponEntity real = new com.atzuche.order.commons.vo.res.rentcosts.OrderCouponEntity();
		      		try {
						BeanUtils.copyProperties(real, x);
					} catch (Exception e) {
						log.error("对象属性赋值报错:",e);
					}
		      		orderCouponListReal.add(real);
		          });
		      }
		  resVo.setOrderCouponList(orderCouponListReal);
	      
		  //补付费用
		  List<OrderSupplementDetailEntity> supplementList = orderSupplementDetailService.listOrderSupplementDetailByOrderNoAndMemNo(orderNo, memNo);
		  List<com.atzuche.order.commons.vo.res.rentcosts.OrderSupplementDetailEntity> supplementListReal = new ArrayList<com.atzuche.order.commons.vo.res.rentcosts.OrderSupplementDetailEntity>();
		  if(supplementList != null) {
			  supplementList.stream().forEach(x->{
				  com.atzuche.order.commons.vo.res.rentcosts.OrderSupplementDetailEntity real = new com.atzuche.order.commons.vo.res.rentcosts.OrderSupplementDetailEntity();
		      		try {
						BeanUtils.copyProperties(real, x);
					} catch (Exception e) {
						log.error("对象属性赋值报错:",e);
					}
		      		supplementListReal.add(real);
		          });
		      }
		  resVo.setSupplementList(supplementListReal);
		  
		return resVo;
	}

	public OrderOwnerCostResVO orderCostOwnerGet(OrderCostReqVO req) throws Exception {
		OrderOwnerCostResVO resVo = new OrderOwnerCostResVO();
		
		//参数定义
		String orderNo = req.getOrderNo();  //仅仅一个订单号
//		String memNo = req.getMemNo();
		String ownerOrderNo = req.getSubOrderNo();
		// ----------------------------------------------------- 结算前查询
		OwnerCosts ownerCosts = orderSettleService.preOwnerSettleOrder(orderNo,ownerOrderNo);
		if(ownerCosts != null) {
			log.info("ownerCosts===============不为空");
		}else {
			log.info("ownerCosts===============为空");
		}
		//数据封装
		putOwnerCosts(resVo,ownerCosts);
		log.info("ownerCosts===============数据封装");
		
		// 获取修改前租客使用的优惠券列表
		  List<OrderCouponEntity> orderCouponList = orderCouponService.listOrderCouponByOrderNo(orderNo);
		  List<com.atzuche.order.commons.vo.res.rentcosts.OrderCouponEntity> orderCouponListReal = new ArrayList<com.atzuche.order.commons.vo.res.rentcosts.OrderCouponEntity>();
		  if(orderCouponList != null) {
			  orderCouponList.stream().forEach(x->{
				  com.atzuche.order.commons.vo.res.rentcosts.OrderCouponEntity real = new com.atzuche.order.commons.vo.res.rentcosts.OrderCouponEntity();
		      		try {
						BeanUtils.copyProperties(real, x);
					} catch (Exception e) {
						log.error("对象属性赋值报错:",e);
					}
		      		orderCouponListReal.add(real);
		          });
		      }
		  resVo.setOrderCouponList(orderCouponListReal);
		
		return resVo;
	}
	
	private void putOwnerCosts(OrderOwnerCostResVO resVo, OwnerCosts ownerCosts) throws Exception {
		/**
	     * 管理后台补贴
	     */
	    List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails = ownerCosts.getOrderConsoleSubsidyDetails();
	    List<com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetailsReal = new ArrayList<com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity>();
	    if(orderConsoleSubsidyDetails != null) {
	    	orderConsoleSubsidyDetails.stream().forEach(x->{
	    		com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity real = new com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity();
		      		try {
						BeanUtils.copyProperties(real, x);
					} catch (Exception e) {
						log.error("对象属性赋值报错:",e);
					}
		      		orderConsoleSubsidyDetailsReal.add(real);
		          });
		      }
		  resVo.setOrderConsoleSubsidyDetails(orderConsoleSubsidyDetailsReal);
		  ///
	  /**
	     * 全局的车主订单罚金明细
	     */
	    List<ConsoleOwnerOrderFineDeatailEntity> consoleRenterOrderFineDeatails = ownerCosts.getConsoleOwnerOrderFineDeatailEntitys();
	    List<ConsoleRenterOrderFineDeatailEntity> consoleRenterOrderFineDeatailsReal = new ArrayList<ConsoleRenterOrderFineDeatailEntity>();
	    if(consoleRenterOrderFineDeatails != null) {
	    	consoleRenterOrderFineDeatails.stream().forEach(x->{
	    		ConsoleRenterOrderFineDeatailEntity real = new ConsoleRenterOrderFineDeatailEntity();
		      		try {
						BeanUtils.copyProperties(real, x);
					} catch (Exception e) {
						log.error("对象属性赋值报错:",e);
					}
		      		consoleRenterOrderFineDeatailsReal.add(real);
		          });
		      }
	    resVo.setConsoleRenterOrderFineDeatails(consoleRenterOrderFineDeatailsReal);
	    
	    
	    /**
	     * 车主订单罚金明细
	     */
	    List<com.atzuche.order.ownercost.entity.OwnerOrderFineDeatailEntity> ownerOrderFineDeatails = ownerCosts.getOwnerOrderFineDeatails();
	    List<OwnerOrderFineDeatailEntity> ownerOrderFineDeatailsReal = new ArrayList<OwnerOrderFineDeatailEntity>();
	    if(ownerOrderFineDeatails != null) {
	    	ownerOrderFineDeatails.stream().forEach(x->{
	    		OwnerOrderFineDeatailEntity real = new OwnerOrderFineDeatailEntity();
		      		try {
						BeanUtils.copyProperties(real, x);
					} catch (Exception e) {
						log.error("对象属性赋值报错:",e);
					}
		      		ownerOrderFineDeatailsReal.add(real);
		          });
		      }
	    resVo.setOwnerOrderFineDeatails(ownerOrderFineDeatailsReal);
	    
	    
	    /**
	     * 车主端代管车服务费
	     */
	     com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity proxyExpense = ownerCosts.getProxyExpense();
	     OwnerOrderPurchaseDetailEntity proxyExpenseReal = null;
	     if(proxyExpense != null) {
	    	 proxyExpenseReal = new OwnerOrderPurchaseDetailEntity();
	    	 BeanUtils.copyProperties(proxyExpenseReal, proxyExpense);
	     }
	     resVo.setProxyExpense(proxyExpenseReal);
	     
	    /**
	     * 车主端平台服务费
	     */
	     com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity serviceExpense = ownerCosts.getServiceExpense();
	     OwnerOrderPurchaseDetailEntity serviceExpenseReal = null;
	     if(serviceExpense != null) {
	    	 serviceExpenseReal = new OwnerOrderPurchaseDetailEntity();
	    	 BeanUtils.copyProperties(serviceExpenseReal, serviceExpense);
	     }
	     resVo.setServiceExpense(serviceExpenseReal);
	     
	    /**
	     * 获取车主补贴明细列表
	     */
	     List<com.atzuche.order.ownercost.entity.OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetail = ownerCosts.getOwnerOrderSubsidyDetail();
	     List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetailReal = new ArrayList<OwnerOrderSubsidyDetailEntity>();
		    if(ownerOrderSubsidyDetail != null) {
		    	ownerOrderSubsidyDetail.stream().forEach(x->{
		    		OwnerOrderSubsidyDetailEntity real = new OwnerOrderSubsidyDetailEntity();
			      		try {
							BeanUtils.copyProperties(real, x);
						} catch (Exception e) {
							log.error("对象属性赋值报错:",e);
						}
			      		ownerOrderSubsidyDetailReal.add(real);
			          });
			      }
		    resVo.setOwnerOrderSubsidyDetail(ownerOrderSubsidyDetailReal);
		    
	    /**
	     * 获取车主费用列表
	     */
	     List<com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetail = ownerCosts.getOwnerOrderPurchaseDetail();
	     List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetailReal = new ArrayList<OwnerOrderPurchaseDetailEntity>();
		    if(ownerOrderPurchaseDetail != null) {
		    	ownerOrderPurchaseDetail.stream().forEach(x->{
		    		OwnerOrderPurchaseDetailEntity real = new OwnerOrderPurchaseDetailEntity();
			      		try {
							BeanUtils.copyProperties(real, x);
						} catch (Exception e) {
							log.error("对象属性赋值报错:",e);
						}
			      		ownerOrderPurchaseDetailReal.add(real);
			          });
			      }
		    resVo.setOwnerOrderPurchaseDetail(ownerOrderPurchaseDetailReal);
		    
	    /**
	     * 获取车主增值服务费用列表
	     */
	     List<com.atzuche.order.ownercost.entity.OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetail = ownerCosts.getOwnerOrderIncrementDetail();
	     List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetailReal = new ArrayList<OwnerOrderIncrementDetailEntity>();
		    if(ownerOrderIncrementDetail != null) {
		    	ownerOrderIncrementDetail.stream().forEach(x->{
		    		OwnerOrderIncrementDetailEntity real = new OwnerOrderIncrementDetailEntity();
			      		try {
							BeanUtils.copyProperties(real, x);
						} catch (Exception e) {
							log.error("对象属性赋值报错:",e);
						}
			      		ownerOrderIncrementDetailReal.add(real);
			          });
			      }
		    resVo.setOwnerOrderIncrementDetail(ownerOrderIncrementDetailReal);
		    
	    /**
	     * 获取gps服务费
	     */
	     List<com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity> gpsCost = ownerCosts.getGpsCost();
	     List<OwnerOrderPurchaseDetailEntity> gpsCostReal = new ArrayList<OwnerOrderPurchaseDetailEntity>();
		    if(gpsCost != null) {
		    	gpsCost.stream().forEach(x->{
		    		OwnerOrderPurchaseDetailEntity real = new OwnerOrderPurchaseDetailEntity();
			      		try {
							BeanUtils.copyProperties(real, x);
						} catch (Exception e) {
							log.error("对象属性赋值报错:",e);
						}
			      		gpsCostReal.add(real);
			          });
			      }
		    resVo.setGpsCost(gpsCostReal);

	    /**
	     * 获取车主油费
	     */
	     OwnerGetAndReturnCarDTO renterOrderCostDetail = ownerCosts.getOwnerGetAndReturnCarDTO();  //海豹命名错误
	     OwnerOrderPurchaseDetailEntity renterOrderCostDetailReal = null;
	     if(renterOrderCostDetail != null) {
	    	 renterOrderCostDetailReal = new OwnerOrderPurchaseDetailEntity();
	    	 BeanUtils.copyProperties(renterOrderCostDetailReal, renterOrderCostDetail);
             String oilDifferenceCrash = renterOrderCostDetail.getOilDifferenceCrash();
             oilDifferenceCrash = StringUtil.isBlank(oilDifferenceCrash)?"0":oilDifferenceCrash;
             renterOrderCostDetailReal.setTotalAmount(Integer.valueOf(oilDifferenceCrash));
	     }
	     resVo.setOwnerOrderCostDetail(renterOrderCostDetailReal);
	    
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
