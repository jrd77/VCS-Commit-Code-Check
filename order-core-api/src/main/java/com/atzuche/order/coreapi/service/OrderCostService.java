/**
 * 
 */
package com.atzuche.order.coreapi.service;

import com.atzuche.order.accountownerincome.service.notservice.AccountOwnerIncomeExamineNoTService;
import com.atzuche.order.accountrenterdeposit.service.AccountRenterDepositService;
import com.atzuche.order.accountrenterdeposit.vo.res.AccountRenterDepositResVO;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleEntity;
import com.atzuche.order.accountrenterrentcost.service.AccountRenterCostSettleService;
import com.atzuche.order.accountrenterwzdepost.service.AccountRenterWzDepositService;
import com.atzuche.order.accountrenterwzdepost.vo.res.AccountRenterWZDepositResVO;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.OwnerCouponLongDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterOrderDTO;
import com.atzuche.order.commons.entity.ownerOrderDetail.RenterRentDetailDTO;
import com.atzuche.order.commons.enums.DeliveryOrderTypeEnum;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.SubsidyTypeCodeEnum;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import com.atzuche.order.commons.enums.cashcode.ConsoleCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.exceptions.NotAllowedEditException;
import com.atzuche.order.commons.vo.rentercost.OwnerToPlatformCostReqVO;
import com.atzuche.order.commons.vo.rentercost.RenterCostReqVO;
import com.atzuche.order.commons.vo.rentercost.RenterToPlatformCostReqVO;
import com.atzuche.order.commons.vo.req.OrderCostReqVO;
import com.atzuche.order.commons.vo.req.RenterAdjustCostReqVO;
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
import com.atzuche.order.coreapi.entity.vo.RenterAndConsoleFineVO;
import com.atzuche.order.coreapi.entity.vo.RenterAndConsoleSubsidyVO;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderRenterOrderNotFindException;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.vo.delivery.rep.OwnerGetAndReturnCarDTO;
import com.atzuche.order.delivery.vo.delivery.rep.RenterGetAndReturnCarDTO;
import com.atzuche.order.owner.commodity.entity.OwnerGoodsEntity;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.ownercost.entity.ConsoleOwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentercost.entity.*;
import com.atzuche.order.rentercost.service.*;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.entity.OwnerCouponLongEntity;
import com.atzuche.order.renterorder.entity.RenterDepositDetailEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.OrderCouponService;
import com.atzuche.order.renterorder.service.OwnerCouponLongService;
import com.atzuche.order.renterorder.service.RenterDepositDetailService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.settle.service.OrderSettleService;
import com.atzuche.order.settle.vo.req.OwnerCosts;
import com.atzuche.order.settle.vo.req.RentCosts;
import com.autoyol.doc.util.StringUtil;
import com.autoyol.platformcost.CommonUtils;
import com.autoyol.platformcost.model.FeeResult;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
	@Autowired
	private ConsoleRenterOrderFineDeatailService consoleRenterOrderFineDeatailService;
	@Autowired
	private RenterOrderFineDeatailService renterOrderFineDeatailService;
	@Autowired
	private RenterDepositDetailService renterDepositDetailService;
	@Autowired
	private OrderConsoleCostDetailService orderConsoleCostDetailService;
	@Autowired
	private AccountOwnerIncomeExamineNoTService accountOwnerIncomeExamineNoTService;
    @Autowired
	private OwnerCouponLongService ownerCouponLongService;
    @Autowired
    private OwnerOrderService ownerOrderService;
    @Autowired
    private OrderConsoleSubsidyDetailService orderConsoleSubsidyDetailService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private RenterGoodsService renterGoodsService;
    @Autowired
    private OwnerGoodsService ownerGoodsService;
    @Autowired
    private OwnerMemberService ownerMemberService;
    @Autowired
    private RenterOrderService renterOrderService;

	public OrderRenterCostResVO orderCostRenterGet(OrderCostReqVO req){
		OrderRenterCostResVO resVo = new OrderRenterCostResVO();
		
		//参数定义
		String orderNo = req.getOrderNo();
		String memNo = req.getMemNo();
		String renterOrderNo = req.getSubOrderNo();

        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByRenterOrderNo(renterOrderNo);
        RenterOrderDTO renterOrderDTO = null;
        if(renterOrderEntity != null){
            renterOrderDTO = new RenterOrderDTO();
            BeanUtils.copyProperties(renterOrderEntity,renterOrderDTO);
            resVo.setRenterOrderDTO(renterOrderDTO);
        }

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

        OwnerCouponLongEntity ownerCouponLongEntity = ownerCouponLongService.getByRenterOrderNo(renterOrderNo);
        if(ownerCouponLongEntity != null){
            OwnerCouponLongDTO ownerCouponLongDTO = new OwnerCouponLongDTO();
            BeanUtils.copyProperties(ownerCouponLongEntity,ownerCouponLongDTO);
            resVo.setOwnerCouponLongDTO(ownerCouponLongDTO);
        }
        //违章押金
        AccountRenterWZDepositResVO wzVo =  accountRenterWzDepositService.getAccountRenterWZDeposit(orderNo, memNo);
        com.atzuche.order.commons.vo.res.account.AccountRenterWZDepositResVO wzVoReal = new com.atzuche.order.commons.vo.res.account.AccountRenterWZDepositResVO();
        if(wzVo != null) {
        	BeanUtils.copyProperties(wzVo,wzVoReal);
        }else {
        	wzVoReal.setYingshouDeposit(0);
        }
        resVo.setWzVo(wzVoReal);
        
        //租车押金
        AccountRenterDepositResVO rentVo = accountRenterDepositService.getAccountRenterDepositEntity(orderNo, memNo);
        com.atzuche.order.commons.vo.res.account.AccountRenterDepositResVO rentVoReal = new com.atzuche.order.commons.vo.res.account.AccountRenterDepositResVO();
        if(rentVo != null) {
        	BeanUtils.copyProperties(rentVo,rentVoReal);
        }else {
        	rentVoReal.setYingfuDepositAmt(0);
        }
        //封装减免金额

        RenterDepositDetailEntity entity = renterDepositDetailService.queryByOrderNo(orderNo);
        if(entity != null) {
            rentVoReal.setReductionAmt(entity.getReductionDepositAmt());
            rentVoReal.setOriginalDepositAmt(entity.getOriginalDepositAmt());
        }


        resVo.setRentVo(rentVoReal);
        
        //钱包
        List<AccountRenterCostDetailEntity> lstCostDetail =  accountRenterCostSettleService.getAccountRenterCostDetailsByOrderNo(orderNo);
        AccountRenterCostDetailEntity walletCostDetail = null; //仅仅关心的是钱包的。
        for (AccountRenterCostDetailEntity accountRenterCostDetailEntity : lstCostDetail) {
        	if(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST.getCashNo().equals(accountRenterCostDetailEntity.getSourceCode()) && "00".equals(accountRenterCostDetailEntity.getPaySourceCode())) {
        		walletCostDetail = new AccountRenterCostDetailEntity();
        		BeanUtils.copyProperties(accountRenterCostDetailEntity,walletCostDetail);
        	}
		}
        AccountRenterCostDetailResVO walletCostDetailReal = new AccountRenterCostDetailResVO();
        if(walletCostDetail != null) {
        	BeanUtils.copyProperties(walletCostDetail,walletCostDetailReal);
        }else {
        	walletCostDetailReal.setAmt(0); //默认值。
        }
        resVo.setWalletCostDetail(walletCostDetailReal);
        
        
		
		//配送订单
	      List<RenterOrderDeliveryEntity> renterOrderDeliveryList = renterOrderDeliveryService.selectByRenterOrderNo(renterOrderNo);
	      RenterOrderDeliveryEntity renterOrderDeliveryGet = filterDeliveryOrderByType(renterOrderDeliveryList, DeliveryOrderTypeEnum.GET_CAR);
	      RenterOrderDeliveryEntity renterOrderDeliveryReturn = filterDeliveryOrderByType(renterOrderDeliveryList, DeliveryOrderTypeEnum.RETURN_CAR);
	      RenterOrderDeliveryResVO renterOrderDeliveryGetReal = new RenterOrderDeliveryResVO();
	      RenterOrderDeliveryResVO renterOrderDeliveryReturnReal = new RenterOrderDeliveryResVO();
	      
	      if(renterOrderDeliveryGet != null) {
	      	BeanUtils.copyProperties(renterOrderDeliveryGet,renterOrderDeliveryGetReal);
	      }
	      if(renterOrderDeliveryReturn != null) {
	      	BeanUtils.copyProperties(renterOrderDeliveryReturn,renterOrderDeliveryReturnReal);
	      }
	      resVo.setRenterOrderDeliveryGet(renterOrderDeliveryGetReal);
	      resVo.setRenterOrderDeliveryReturn(renterOrderDeliveryReturnReal);
	      
	   // 获取修改前租客使用的优惠券列表
		  List<OrderCouponEntity> orderCouponList = orderCouponService.listOrderCouponByRenterOrderNo(renterOrderNo);
		  List<com.atzuche.order.commons.vo.res.rentcosts.OrderCouponEntity> orderCouponListReal = new ArrayList<com.atzuche.order.commons.vo.res.rentcosts.OrderCouponEntity>();
		  if(orderCouponList != null) {
			  orderCouponList.stream().forEach(x->{
				  com.atzuche.order.commons.vo.res.rentcosts.OrderCouponEntity real = new com.atzuche.order.commons.vo.res.rentcosts.OrderCouponEntity();
		      		try {
						BeanUtils.copyProperties(x, real);
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
						BeanUtils.copyProperties(x,real);
					} catch (Exception e) {
						log.error("对象属性赋值报错:",e);
					}
		      		supplementListReal.add(real);
		          });
		      }
		  resVo.setSupplementList(supplementListReal);
	      
	      AccountRenterCostSettleEntity arcse = accountRenterCostSettleService.selectByOrderNo(orderNo, memNo);
	      AccountRenterCostSettleResVO  renterSettleVo = new AccountRenterCostSettleResVO();
	      if(arcse != null) {
		      	BeanUtils.copyProperties(arcse,renterSettleVo);
		  }
		  resVo.setRenterSettleVo(renterSettleVo);
		  
	      
        
	      //-----------------------------------------------------------------

		  ///油费，超里程，加油服务费
		  RentCosts rentCost = orderSettleService.preRenterSettleOrder(orderNo,renterOrderNo);
//		  com.atzuche.order.commons.vo.res.RentCosts rentCostsReal = new com.atzuche.order.commons.vo.res.RentCosts();
//		  BeanUtils.copyProperties(rentCostsReal, rentCost);
//		  resVo.setRentCosts(rentCostsReal);
		  
		  //租客费用统计 200309
		  resVo.setRenterCostAmtFinal(rentCost.getRenterCostAmtFinal());
		  
		  /**
		   * 简化处理 !!!
		   */
		    /**
		     * 交接车-油费
		     */
            RenterGetAndReturnCarDTO oilAmt = rentCost.getOilAmt();
//            com.atzuche.order.commons.vo.res.rentcosts.RenterOrderCostDetailEntity oilAmtReal = new com.atzuche.order.commons.vo.res.rentcosts.RenterOrderCostDetailEntity();
//            if(oilAmt != null) {
//                BeanUtils.copyProperties(oilAmt,oilAmtReal);
//                String oilDifferenceCrash = oilAmt.getOilDifferenceCrash();
//                oilDifferenceCrash = StringUtil.isBlank(oilDifferenceCrash)?"0":oilDifferenceCrash;
//                //oilDifferenceCrash may be "0.0" format
//                oilAmtReal.setTotalAmount((int)Float.parseFloat(oilDifferenceCrash));
//            }
//		    resVo.setOilAmt(oilAmtReal);
            if(oilAmt != null) {
            	String oilDifferenceCrash = oilAmt.getOilDifferenceCrash();
            	oilDifferenceCrash = StringUtil.isBlank(oilDifferenceCrash)?"0":oilDifferenceCrash;
            	//oilDifferenceCrash may be "0.0" format
            	resVo.setOilAmt((int)Float.parseFloat(oilDifferenceCrash));
            }

		    /*
		     * 交接车-获取超里程费用
		     */
            FeeResult mileageAmt = rentCost.getMileageAmt();
//		    com.atzuche.order.commons.vo.res.rentcosts.RenterOrderCostDetailEntity mileageAmtReal = new com.atzuche.order.commons.vo.res.rentcosts.RenterOrderCostDetailEntity();
//		    if(mileageAmt != null) {
//		    	BeanUtils.copyProperties(mileageAmt,mileageAmtReal);
//                mileageAmtReal.setTotalAmount(mileageAmt.getTotalFee());
//		    }
            if(mileageAmt != null) {
            	resVo.setMileageAmt(mileageAmt.getTotalFee());
            }
		    
          //-------------------------------------------------------------------- 以下是子订单费用
          //租客费用明细,代码重构，避免重复查询。
		      List<RenterOrderCostDetailEntity> renterOrderCostDetailList = rentCost.getRenterOrderCostDetails();//renterOrderCostDetailService.listRenterOrderCostDetail(orderNo, renterOrderNo);
		      List<RenterOrderCostDetailResVO> renterOrderCostDetailListReal = new ArrayList<RenterOrderCostDetailResVO>();
		      if(renterOrderCostDetailList != null) {
		      	renterOrderCostDetailList.stream().forEach(x->{
		      		RenterOrderCostDetailResVO real = new RenterOrderCostDetailResVO();
		      		try {
						BeanUtils.copyProperties(x,real);
					} catch (Exception e) {
						log.error("对象属性赋值报错:",e);
					}
		      		renterOrderCostDetailListReal.add(real);
		          });
		      }
		      //数据封装 20200211
		      resVo.setRenterOrderCostDetailList(renterOrderCostDetailListReal);
		      
		      
            //租客罚金列表,代码重构，避免重复查询。
      		List<RenterOrderFineDeatailEntity> fineLst = rentCost.getRenterOrderFineDeatails(); //renterOrderFineDeatailService.listRenterOrderFineDeatail(orderNo, renterOrderNo);
      		List<RenterOrderFineDeatailResVO> fineLstReal = new ArrayList<RenterOrderFineDeatailResVO>();
      		if(fineLst != null) {
      			fineLst.stream().forEach(x->{
      				RenterOrderFineDeatailResVO real = new RenterOrderFineDeatailResVO();
      	      		try {
      					BeanUtils.copyProperties(x,real);
      				} catch (Exception e) {
      					log.error("对象属性赋值报错:",e);
      				}
      	      		fineLstReal.add(real);
      	          });
      	      }
      		resVo.setFineLst(fineLstReal);
      		
      		
      		//管理后台罚金列表,代码重构，避免重复查询。
      		List<com.atzuche.order.rentercost.entity.ConsoleRenterOrderFineDeatailEntity> consoleFineLst = rentCost.getConsoleRenterOrderFineDeatails(); //consoleRenterOrderFineDeatailService.listConsoleRenterOrderFineDeatail(orderNo, memNo);
      		List<ConsoleRenterOrderFineDeatailEntity> consolefineLstReal = new ArrayList<ConsoleRenterOrderFineDeatailEntity>();
      		if(consoleFineLst != null) {
      			consoleFineLst.stream().forEach(x->{
      				ConsoleRenterOrderFineDeatailEntity real = new ConsoleRenterOrderFineDeatailEntity();
      	      		try {
      					BeanUtils.copyProperties(x,real);
      				} catch (Exception e) {
      					log.error("对象属性赋值报错:",e);
      				}
      	      		consolefineLstReal.add(real);
      	          });
      		}
      		//封装
      		resVo.setConsoleFineLst(consolefineLstReal);
      		
      		
      		//管理后台费用列表,代码重构，避免重复查询。
      		List<OrderConsoleCostDetailEntity> consoleCostLst = rentCost.getOrderConsoleCostDetailEntity();//orderConsoleCostDetailService.selectByOrderNoAndMemNo(orderNo,memNo);
      		List<com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleCostDetailEntity> consoleCostLstReal = new ArrayList<com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleCostDetailEntity>();
      		if(consoleCostLst != null) {
      			consoleCostLst.stream().forEach(x->{
      				com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleCostDetailEntity real = new com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleCostDetailEntity();
      	      		try {
      					BeanUtils.copyProperties(x,real);
      				} catch (Exception e) {
      					log.error("对象属性赋值报错:",e);
      				}
      	      		consoleCostLstReal.add(real);
      	          });
      	      }
      		resVo.setOrderConsoleCostDetails(consoleCostLstReal);
      		
      		
      		 //抵扣明细 租客补贴明细列表,代码重构，避免重复查询。
		  	List<RenterOrderSubsidyDetailEntity> subsidyLst = rentCost.getRenterOrderSubsidyDetails();//renterOrderSubsidyDetailService.listRenterOrderSubsidyDetail(orderNo, renterOrderNo);
		      List<RenterOrderSubsidyDetailResVO> subsidyLstReal = new ArrayList<RenterOrderSubsidyDetailResVO>();;
		      if(subsidyLst != null) {
		      	subsidyLst.stream().forEach(x->{
		      		RenterOrderSubsidyDetailResVO real = new RenterOrderSubsidyDetailResVO();
		      		try {
						BeanUtils.copyProperties(x,real);
					} catch (Exception e) {
						log.error("对象属性赋值报错:",e);
					}
		              subsidyLstReal.add(real);
		          });
		      }
		      resVo.setSubsidyLst(subsidyLstReal);
		      
		      
		    
		     //管理后台补贴,代码重构，避免重复查询。
		    List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails = rentCost.getOrderConsoleSubsidyDetails();
		    List<com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetailsReal = new ArrayList<com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity>();
		    if(orderConsoleSubsidyDetails != null) {
		    	orderConsoleSubsidyDetails.stream().forEach(x->{
		    		com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity real = new com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity();
			      		try {
							BeanUtils.copyProperties(x,real);
						} catch (Exception e) {
							log.error("对象属性赋值报错:",e);
						}
			      		orderConsoleSubsidyDetailsReal.add(real);
			          });
			      }
			  resVo.setOrderConsoleSubsidyDetails(orderConsoleSubsidyDetailsReal);
			   
		return resVo;
	}

	public OrderOwnerCostResVO orderCostOwnerGet(OrderCostReqVO req)  {
		OrderOwnerCostResVO resVo = new OrderOwnerCostResVO();
		
		//参数定义
		String orderNo = req.getOrderNo();  //仅仅一个订单号
//		String memNo = req.getMemNo();
		String ownerOrderNo = req.getSubOrderNo();
		// ----------------------------------------------------- 结算前查询
		OwnerCosts ownerCosts = orderSettleService.preOwnerSettleOrder(orderNo,ownerOrderNo);
		String ownerNo = "0";
		if(ownerCosts != null) {
			log.info("ownerCosts===============不为空");
			ownerNo = ownerCosts.getOwnerNo();
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
						BeanUtils.copyProperties(x,real);
					} catch (Exception e) {
						log.error("对象属性赋值报错:",e);
					}
		      		orderCouponListReal.add(real);
		          });
		      }
		  resVo.setOrderCouponList(orderCouponListReal);
		  
		  ///
			List<OrderConsoleCostDetailEntity> consoleCostLst = orderConsoleCostDetailService.selectByOrderNoAndMemNo(orderNo,ownerNo);
			List<com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleCostDetailEntity> consoleCostLstReal = new ArrayList<com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleCostDetailEntity>();
			if(consoleCostLst != null) {
				consoleCostLst.stream().forEach(x->{
					com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleCostDetailEntity real = new com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleCostDetailEntity();
		      		try {
						BeanUtils.copyProperties(x,real);
					} catch (Exception e) {
						log.error("对象属性赋值报错:",e);
					}
		      		consoleCostLstReal.add(real);
		          });
		      }
			resVo.setOrderConsoleCostDetails(consoleCostLstReal);
            OwnerOrderEntity ownerOrderByOwnerOrderNo = ownerOrderService.getOwnerOrderByOwnerOrderNo(ownerOrderNo);
            String renterOrderNo = ownerOrderByOwnerOrderNo.getRenterOrderNo();
            if(renterOrderNo != null){
                OwnerCouponLongEntity ownerCouponLongEntity = ownerCouponLongService.getByRenterOrderNo(renterOrderNo);
                if(ownerCouponLongEntity != null){
                    OwnerCouponLongDTO ownerCouponLongDTO = new OwnerCouponLongDTO();
                    BeanUtils.copyProperties(ownerCouponLongEntity,ownerCouponLongDTO);
                    resVo.setOwnerCouponLongDTO(ownerCouponLongDTO);
                }
            }

			///车主的结算后收益 200215  结算收益有多条记录的情况。
//			AccountOwnerIncomeExamineEntity examine = accountOwnerIncomeExamineNoTService.getAccountOwnerIncomeExamineByOrderNo(orderNo);
//			if(examine != null) {
//				ownerCostAmtSettleAfter = examine.getAmt().intValue();
//			}
			Integer ownerCostAmtSettleAfter = accountOwnerIncomeExamineNoTService.getTotalAccountOwnerIncomeExamineByOrderNo(orderNo);
			resVo.setOwnerCostAmtSettleAfter(ownerCostAmtSettleAfter);
            resVo.setGpsDepositTotal(ownerCosts.getGpsDepositDetail()==null?0:ownerCosts.getGpsDepositDetail().getTotalAmount());
		return resVo;
	}
	
	private void putOwnerCosts(OrderOwnerCostResVO resVo, OwnerCosts ownerCosts)  {
		/**
	     * 管理后台补贴
	     */
	    List<OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetails = ownerCosts.getOrderConsoleSubsidyDetails();
	    List<com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity> orderConsoleSubsidyDetailsReal = new ArrayList<com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity>();
	    if(orderConsoleSubsidyDetails != null) {
	    	orderConsoleSubsidyDetails.stream().forEach(x->{
	    		com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity real = new com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity();
		      		try {
						BeanUtils.copyProperties(x,real);
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
	    List<ConsoleOwnerOrderFineDeatailEntity> consoleOwnerOrderFineDeatails = ownerCosts.getConsoleOwnerOrderFineDeatailEntitys();
	    List<com.atzuche.order.commons.vo.res.ownercosts.ConsoleOwnerOrderFineDeatailEntity> consoleOwnerOrderFineDeatailsReal = new ArrayList<com.atzuche.order.commons.vo.res.ownercosts.ConsoleOwnerOrderFineDeatailEntity>();
	    if(consoleOwnerOrderFineDeatails != null) {
	    	consoleOwnerOrderFineDeatails.stream().forEach(x->{
	    		com.atzuche.order.commons.vo.res.ownercosts.ConsoleOwnerOrderFineDeatailEntity real = new com.atzuche.order.commons.vo.res.ownercosts.ConsoleOwnerOrderFineDeatailEntity();
		      		try {
						BeanUtils.copyProperties(x,real);
					} catch (Exception e) {
						log.error("对象属性赋值报错:",e);
					}
		      		consoleOwnerOrderFineDeatailsReal.add(real);
		          });
		      }
	    resVo.setConsoleOwnerOrderFineDeatails(consoleOwnerOrderFineDeatailsReal);
	    
	    
	    /**
	     * 车主订单罚金明细
	     */
	    List<com.atzuche.order.ownercost.entity.OwnerOrderFineDeatailEntity> ownerOrderFineDeatails = ownerCosts.getOwnerOrderFineDeatails();
	    List<OwnerOrderFineDeatailEntity> ownerOrderFineDeatailsReal = new ArrayList<OwnerOrderFineDeatailEntity>();
	    if(ownerOrderFineDeatails != null) {
	    	ownerOrderFineDeatails.stream().forEach(x->{
	    		OwnerOrderFineDeatailEntity real = new OwnerOrderFineDeatailEntity();
		      		try {
						BeanUtils.copyProperties(x,real);
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
	    	 BeanUtils.copyProperties(proxyExpense,proxyExpenseReal);
	     }
	     resVo.setProxyExpense(proxyExpenseReal);
	     
	    /**
	     * 车主端平台服务费
	     */
	     com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity serviceExpense = ownerCosts.getServiceExpense();
	     OwnerOrderPurchaseDetailEntity serviceExpenseReal = null;
	     if(serviceExpense != null) {
	    	 serviceExpenseReal = new OwnerOrderPurchaseDetailEntity();
	    	 BeanUtils.copyProperties(serviceExpense,serviceExpenseReal);
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
							BeanUtils.copyProperties(x,real);
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
							BeanUtils.copyProperties(x,real);
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
							BeanUtils.copyProperties(x,real);
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
							BeanUtils.copyProperties(x,real);
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
		 int oilDifferenceCrashAmt = 0;
	     OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO = ownerCosts.getOwnerGetAndReturnCarDTO();  //海豹命名错误
	     if(ownerGetAndReturnCarDTO != null) {
             String oilDifferenceCrash = ownerGetAndReturnCarDTO.getOilDifferenceCrash();
             oilDifferenceCrash = StringUtil.isBlank(oilDifferenceCrash)?"0":oilDifferenceCrash;
             oilDifferenceCrashAmt = Integer.valueOf(oilDifferenceCrash);
	     }
	     resVo.setOwnerOilDifferenceCrashAmt(oilDifferenceCrashAmt);
	    /**
	              * 超里程费用
	     */
	     FeeResult mileageAmt = ownerCosts.getMileageAmt();
	     if(mileageAmt != null) {
	    	 //如果是代管车，该超里程显示为0，归平台收益。
	    	 if (CommonUtils.isEscrowCar(ownerCosts.getCarOwnerType())) {
	    		 resVo.setMileageAmt(0);
	    	 }else {
	    		 resVo.setMileageAmt(mileageAmt.getTotalFee()!=null?mileageAmt.getTotalFee():0);
	    	 }
	     }
	     
	     /*车主预计收益*/
	     resVo.setOwnerCostAmtFinal(ownerCosts.getOwnerCostAmtFinal());
	     //平台加油服务费
	     resVo.setOwnerPlatFormOilService(ownerCosts.getOwnerPlatFormOilService());
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
	
	
	/**
	 * 获取租客补贴
	 * @param orderNo
	 * @param renterOrderNo
	 * @return RenterAndConsoleSubsidyVO
	 */
	public RenterAndConsoleSubsidyVO getRenterAndConsoleSubsidyVO(String orderNo, String renterOrderNo) {
		  //主订单
	      OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
	      // 管理后台补贴
		  List<OrderConsoleSubsidyDetailEntity> consoleSubsidyList = orderConsoleSubsidyDetailService.listOrderConsoleSubsidyDetailByOrderNoAndMemNo(orderNo, orderEntity.getMemNoRenter());
		  // 租客子订单补贴
		  List<RenterOrderSubsidyDetailEntity> renterOrderSubsidyDetailEntityList = renterOrderSubsidyDetailService.listRenterOrderSubsidyDetail(orderNo,renterOrderNo); 
		  RenterAndConsoleSubsidyVO renterAndConsoleSubsidyVO = new RenterAndConsoleSubsidyVO();
		  renterAndConsoleSubsidyVO.setConsoleSubsidyList(consoleSubsidyList);
		  renterAndConsoleSubsidyVO.setRenterOrderSubsidyDetailEntityList(renterOrderSubsidyDetailEntityList);
		  return renterAndConsoleSubsidyVO;
	}
	
	
	/**
	 * 获取管理后台费用
	 * @param orderNo
	 * @return List<OrderConsoleCostDetailEntity>
	 */
	public List<OrderConsoleCostDetailEntity> listOrderConsoleCostDetailEntity(String orderNo) {
		//主订单
	    OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
	    if(orderEntity == null){
	      	log.error("获取订单数据为空orderNo={}",orderNo);
	        return null;
	    }
		List<OrderConsoleCostDetailEntity> list = orderConsoleCostDetailService.selectByOrderNoAndMemNo(orderNo,orderEntity.getMemNoRenter());
		return list;
	}
	
	
	/**
	 * 保存调价
	 * @param renterCostReqVO
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Exception.class)
	public void updateRenterPriceAdjustmentByOrderNo(RenterAdjustCostReqVO renterCostReqVO) {
		//根据订单号查询会员号
		//主订单  
	     OrderEntity orderEntity = orderService.getOrderEntity(renterCostReqVO.getOrderNo());
	     if(orderEntity == null){
	      	 log.error("获取订单数据为空orderNo={}",renterCostReqVO.getOrderNo());
	         throw new ModifyOrderRenterOrderNotFindException();
	     }
	    
	    OwnerOrderEntity orderEntityOwner = null;  
	    if(StringUtils.isNotBlank(renterCostReqVO.getOwnerOrderNo())) {  
		    orderEntityOwner = ownerOrderService.getOwnerOrderByOwnerOrderNo(renterCostReqVO.getOwnerOrderNo());
	        if(orderEntityOwner == null){
	        	log.error("获取订单数据(车主)为空orderNo={}",renterCostReqVO.getOrderNo());
	        	throw new ModifyOrderRenterOrderNotFindException();
	        }
	    }
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(renterCostReqVO.getOrderNo());
        if(SettleStatusEnum.SETTLED.getCode() == orderStatusEntity.getSettleStatus() || orderStatusEntity.getStatus() == OrderStatusEnum.CLOSED.getStatus()){
            log.error("已经结算不允许编辑orderNo={}",renterCostReqVO.getOrderNo());
            throw new NotAllowedEditException();
        }
        // 租客给车主的调价
 	   if(StringUtils.isNotBlank(renterCostReqVO.getRenterToOwnerAdjustAmt())) {
 		    SubsidySourceCodeEnum targetEnum = SubsidySourceCodeEnum.OWNER;
 		    SubsidySourceCodeEnum sourceEnum = SubsidySourceCodeEnum.RENTER;
 		    RenterCashCodeEnum cash = RenterCashCodeEnum.SUBSIDY_RENTERTOOWNER_ADJUST;
 		    CostBaseDTO costBaseDTO = new CostBaseDTO();
 	        costBaseDTO.setOrderNo(renterCostReqVO.getOrderNo());
 	    	costBaseDTO.setMemNo(orderEntity.getMemNoRenter());
 	    	OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, -Integer.valueOf(renterCostReqVO.getRenterToOwnerAdjustAmt()), targetEnum, sourceEnum, SubsidyTypeCodeEnum.ADJUST_AMT, cash);
 			record.setCreateOp(renterCostReqVO.getOperateName());
 			record.setUpdateOp(renterCostReqVO.getOperateName());
 			record.setOperatorId(renterCostReqVO.getOperateName());
            orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetailByMemNo(record);
            RenterGoodsDetailDTO renterGoodsDetail = renterGoodsService.getRenterGoodsDetail(renterCostReqVO.getRenterOrderNo(), false);
            OwnerGoodsEntity ownerGoodsEntity = ownerGoodsService.getOwnerGoodsByCarNoAndOrderNo(renterGoodsDetail.getCarNo(), renterCostReqVO.getOrderNo());
            OwnerMemberDTO ownerMemberDTO = ownerMemberService.selectownerMemberByOwnerOrderNo(ownerGoodsEntity.getOwnerOrderNo(), false);
            costBaseDTO.setMemNo(ownerMemberDTO.getMemNo());
            OrderConsoleSubsidyDetailEntity recordConvert = orderConsoleSubsidyDetailService.buildData(costBaseDTO, Integer.valueOf(renterCostReqVO.getRenterToOwnerAdjustAmt()),targetEnum,sourceEnum, SubsidyTypeCodeEnum.ADJUST_AMT, cash);
            recordConvert.setCreateOp(renterCostReqVO.getOperateName());
            recordConvert.setUpdateOp(renterCostReqVO.getOperateName());
            recordConvert.setOperatorId(renterCostReqVO.getOperateName());
            orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetailByMemNo(recordConvert);
 	   }
 	   // 车主给租客的调价
 	   if(StringUtils.isNotBlank(renterCostReqVO.getOwnerToRenterAdjustAmt())) {
 		    SubsidySourceCodeEnum targetEnum = SubsidySourceCodeEnum.RENTER;
 		    SubsidySourceCodeEnum sourceEnum = SubsidySourceCodeEnum.OWNER;
 		    RenterCashCodeEnum cash = RenterCashCodeEnum.SUBSIDY_OWNERTORENTER_ADJUST;
 		   
 		    CostBaseDTO costBaseDTO = new CostBaseDTO();
 	    	costBaseDTO.setOrderNo(renterCostReqVO.getOrderNo());
 	    	if(orderEntityOwner != null) {
 		    	costBaseDTO.setMemNo(orderEntityOwner.getMemNo());
 		    	com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, -Integer.valueOf(renterCostReqVO.getOwnerToRenterAdjustAmt()), targetEnum, sourceEnum, SubsidyTypeCodeEnum.ADJUST_AMT, cash);
 				record.setCreateOp(renterCostReqVO.getOperateName());
 				record.setUpdateOp(renterCostReqVO.getOperateName());
 				record.setOperatorId(renterCostReqVO.getOperateName());
 		    	orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
 	    	}
 	    	
 	    	//反向记录
 	    	costBaseDTO.setMemNo(orderEntity.getMemNoRenter());
 	    	com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity recordConvert = orderConsoleSubsidyDetailService.buildData(costBaseDTO, Integer.valueOf(renterCostReqVO.getOwnerToRenterAdjustAmt()), targetEnum, sourceEnum, SubsidyTypeCodeEnum.ADJUST_AMT, cash);
 	    	recordConvert.setCreateOp(renterCostReqVO.getOperateName());
 	    	recordConvert.setUpdateOp(renterCostReqVO.getOperateName());
 	    	recordConvert.setOperatorId(renterCostReqVO.getOperateName());
 	    	orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(recordConvert);
 	   }
	}
	
	
	/**
	 * 租客需支付给平台的费用 修改
	 * @param renterCostReqVO
	 */
	public void updateRenterToPlatFormListByOrderNo(RenterToPlatformCostReqVO renterCostReqVO){
		CostBaseDTO costBaseDTO = new CostBaseDTO();
		// 根据订单号查询会员号
		// 主订单
		OrderEntity orderEntity = orderService.getOrderEntity(renterCostReqVO.getOrderNo());
		if (orderEntity == null) {
			log.error("获取订单数据为空orderNo={}", renterCostReqVO.getOrderNo());
			throw new ModifyOrderRenterOrderNotFindException();
		}
		OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(renterCostReqVO.getOrderNo());
		if (SettleStatusEnum.SETTLED.getCode() == orderStatusEntity.getSettleStatus()
				|| orderStatusEntity.getStatus() == OrderStatusEnum.CLOSED.getStatus()) {
			log.error("已经结算不允许编辑orderNo={}", renterCostReqVO.getOrderNo());
			throw new NotAllowedEditException();
		}
		// 封装订单号和会员号
		costBaseDTO.setOrderNo(renterCostReqVO.getOrderNo());
		costBaseDTO.setMemNo(orderEntity.getMemNoRenter());

		String oliAmt = renterCostReqVO.getOliAmt();
		String timeOut = renterCostReqVO.getTimeOut();
		String modifyOrderTimeAndAddrAmt = renterCostReqVO.getModifyOrderTimeAndAddrAmt();
		String carWash = renterCostReqVO.getCarWash();
		String dlayWait = renterCostReqVO.getDlayWait();
		String stopCar = renterCostReqVO.getStopCar();
		String extraMileage = renterCostReqVO.getExtraMileage();

		SubsidySourceCodeEnum target = SubsidySourceCodeEnum.PLATFORM;
		SubsidySourceCodeEnum source = SubsidySourceCodeEnum.RENTER;

		String userName = renterCostReqVO.getOperatorName(); // 获取的管理后台的用户名。

		if (StringUtils.isNotBlank(oliAmt)) {
			OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO,
					-Integer.valueOf(oliAmt), target, source, ConsoleCashCodeEnum.RENTER_OIL_FEE);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
		}

		if (StringUtils.isNotBlank(timeOut)) {
			OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO,
					-Integer.valueOf(timeOut), target, source, ConsoleCashCodeEnum.RENTER_TIME_OUT);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
		}

		if (StringUtils.isNotBlank(modifyOrderTimeAndAddrAmt)) {
			OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO,
					-Integer.valueOf(modifyOrderTimeAndAddrAmt), target, source,
					ConsoleCashCodeEnum.RENTER_MODIFY_ADDR_TIME);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
		}

		if (StringUtils.isNotBlank(carWash)) {
			OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO,
					-Integer.valueOf(carWash), target, source, ConsoleCashCodeEnum.RENTER_CAR_WASH);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
		}

		if (StringUtils.isNotBlank(dlayWait)) {
			OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO,
					-Integer.valueOf(dlayWait), target, source, ConsoleCashCodeEnum.RENTER_DLAY_WAIT);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
		}

		if (StringUtils.isNotBlank(stopCar)) {
			OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO,
					-Integer.valueOf(stopCar), target, source, ConsoleCashCodeEnum.RENTER_STOP_CAR);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
		}

		if (StringUtils.isNotBlank(extraMileage)) {
			OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO,
					-Integer.valueOf(extraMileage), target, source, ConsoleCashCodeEnum.RENTER_EXTRA_MILEAGE);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
		}

	}
	
	
	/**
	 * 添加，车主需支付给平台的费用
	 * @param ownerCostReqVO
	 */
	public void updateOwnerToPlatFormListByOrderNo(OwnerToPlatformCostReqVO ownerCostReqVO) {
		CostBaseDTO costBaseDTO = new CostBaseDTO();
		// 根据订单号查询会员号
		OwnerOrderEntity orderEntity = ownerOrderService.getOwnerOrderByOwnerOrderNo(ownerCostReqVO.getOwnerOrderNo());
		if (orderEntity == null) {
			log.error("获取订单数据(车主)为空orderNo={}", ownerCostReqVO.getOrderNo());
			throw new ModifyOrderRenterOrderNotFindException();
		}
		// 封装订单号和会员号
		costBaseDTO.setOrderNo(ownerCostReqVO.getOrderNo());
		costBaseDTO.setMemNo(orderEntity.getMemNo());
		String oliAmt = ownerCostReqVO.getOliAmt();
		String timeOut = ownerCostReqVO.getTimeOut();
		String modifyOrderTimeAndAddrAmt = ownerCostReqVO.getModifyOrderTimeAndAddrAmt();
		String carWash = ownerCostReqVO.getCarWash();
		String dlayWait = ownerCostReqVO.getDlayWait();
		String stopCar = ownerCostReqVO.getStopCar();
		String extraMileage = ownerCostReqVO.getExtraMileage();
		SubsidySourceCodeEnum target = SubsidySourceCodeEnum.PLATFORM;
		SubsidySourceCodeEnum source = SubsidySourceCodeEnum.OWNER;
		String userName = ownerCostReqVO.getOperatorName(); // 获取的管理后台的用户名。
		if (StringUtils.isNotBlank(oliAmt)) {
			OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO,
					-Integer.valueOf(oliAmt), target, source, ConsoleCashCodeEnum.OIL_FEE);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
		}

		if (StringUtils.isNotBlank(timeOut)) {
			OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO,
					-Integer.valueOf(timeOut), target, source, ConsoleCashCodeEnum.TIME_OUT);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
		}

		if (StringUtils.isNotBlank(modifyOrderTimeAndAddrAmt)) {
			OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO,
					-Integer.valueOf(modifyOrderTimeAndAddrAmt), target, source, ConsoleCashCodeEnum.MODIFY_ADDR_TIME);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
		}

		if (StringUtils.isNotBlank(carWash)) {
			OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO,
					-Integer.valueOf(carWash), target, source, ConsoleCashCodeEnum.CAR_WASH);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
		}

		if (StringUtils.isNotBlank(dlayWait)) {
			OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO,
					-Integer.valueOf(dlayWait), target, source, ConsoleCashCodeEnum.DLAY_WAIT);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
		}

		if (StringUtils.isNotBlank(stopCar)) {
			OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO,
					-Integer.valueOf(stopCar), target, source, ConsoleCashCodeEnum.STOP_CAR);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
		}

		if (StringUtils.isNotBlank(extraMileage)) {
			OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO,
					-Integer.valueOf(extraMileage), target, source, ConsoleCashCodeEnum.EXTRA_MILEAGE);
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
		}

	}
	
	
	/**
	 * 租客租金明细
	 * @param renterCostReqVO
	 * @return RenterRentDetailDTO
	 */
	public RenterRentDetailDTO findRenterRentAmtListByOrderNo(RenterCostReqVO renterCostReqVO) {
		// 主订单
		OrderEntity orderEntity = orderService.getOrderEntity(renterCostReqVO.getOrderNo());
		if (orderEntity == null) {
			log.error("获取订单数据为空orderNo={}", renterCostReqVO.getOrderNo());
			return null;
		}
		RenterGoodsDetailDTO renterGoodsDetail = renterGoodsService.getRenterGoodsDetail(renterCostReqVO.getRenterOrderNo(), true);
		RenterRentDetailDTO renterRentDetailDTO = new RenterRentDetailDTO();
		if (renterGoodsDetail != null && renterGoodsDetail.getRenterGoodsPriceDetailDTOList() != null
				&& renterGoodsDetail.getRenterGoodsPriceDetailDTOList().size() > 0) {
			List<RenterGoodsPriceDetailDTO> renterGoodsPriceDetailDTOList = renterGoodsDetail
					.getRenterGoodsPriceDetailDTOList();
			renterGoodsPriceDetailDTOList.forEach(x -> {
				LocalDate carDay = x.getCarDay();
				x.setCarDayStr(carDay != null ? LocalDateTimeUtils.localdateToString(carDay) : null);
			});
			renterRentDetailDTO.setRenterGoodsPriceDetailDTOS(renterGoodsPriceDetailDTOList);
			renterRentDetailDTO.setCarPlateNum(renterGoodsDetail.getCarPlateNum());
		}

		// 111 租客租金组成 日均价
		List<RenterOrderCostDetailEntity> lstRenterCostDetail = renterOrderCostDetailService
				.listRenterOrderCostDetail(renterCostReqVO.getOrderNo(), renterCostReqVO.getRenterOrderNo());
		for (RenterOrderCostDetailEntity renterOrderCostDetailEntity : lstRenterCostDetail) {
			if (RenterCashCodeEnum.RENT_AMT.getCashNo().equals(renterOrderCostDetailEntity.getCostCode())) {
				renterRentDetailDTO.setDayAverageAmt(renterOrderCostDetailEntity.getUnitPrice());
				break;
			}
		}

		RenterOrderEntity renterOrderEntity = renterOrderService
				.getRenterOrderByRenterOrderNo(renterCostReqVO.getRenterOrderNo());
		OrderDTO orderDTO = new OrderDTO();
		BeanUtils.copyProperties(orderEntity, orderDTO);
		renterRentDetailDTO
				.setReqTimeStr(renterOrderEntity.getCreateTime() != null
						? LocalDateTimeUtils.localdateToString(renterOrderEntity.getCreateTime(),
								GlobalConstant.FORMAT_DATE_STR1)
						: null);
		renterRentDetailDTO
				.setRevertTimeStr(renterOrderEntity.getExpRevertTime() != null
						? LocalDateTimeUtils.localdateToString(renterOrderEntity.getExpRevertTime(),
								GlobalConstant.FORMAT_DATE_STR1)
						: null);
		renterRentDetailDTO
				.setRentTimeStr(renterOrderEntity.getExpRentTime() != null
						? LocalDateTimeUtils.localdateToString(renterOrderEntity.getExpRentTime(),
								GlobalConstant.FORMAT_DATE_STR1)
						: null);

		return renterRentDetailDTO;
	}
	
	
	/**
	 * 获取租客罚金
	 * @param orderNo
	 * @param renterOrderNo
	 * @return RenterAndConsoleSubsidyVO
	 */
	public RenterAndConsoleFineVO getRenterAndConsoleFineVO(String orderNo, String renterOrderNo) {
		  //主订单
	      OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
	      // 管理后台罚金
		  List<com.atzuche.order.rentercost.entity.ConsoleRenterOrderFineDeatailEntity> consoleFineList = consoleRenterOrderFineDeatailService.listConsoleRenterOrderFineDeatail(orderNo, orderEntity.getMemNoRenter());
		  // 租客子订单罚金
		  List<RenterOrderFineDeatailEntity> fineList = renterOrderFineDeatailService.listRenterOrderFineDeatail(orderNo,renterOrderNo); 
		  RenterAndConsoleFineVO renterAndConsoleFineVO = new RenterAndConsoleFineVO();
		  renterAndConsoleFineVO.setConsoleFineList(consoleFineList);
		  renterAndConsoleFineVO.setFineList(fineList);
		  return renterAndConsoleFineVO;
	}

}
