/**
 * 
 */
package com.atzuche.order.coreapi.controller;

import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositEntity;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositEntity;
import com.atzuche.order.cashieraccount.service.CashierQueryService;
import com.atzuche.order.commons.AuthorizeEnum;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.entity.dto.ExtraDriverDTO;
import com.atzuche.order.commons.entity.ownerOrderDetail.RenterRentDetailDTO;
import com.atzuche.order.commons.entity.rentCost.RenterCostDetailDTO;
import com.atzuche.order.commons.exceptions.AccountDepositException;
import com.atzuche.order.commons.exceptions.AccountWzDepositException;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.commons.exceptions.OwnerOrderNotFoundException;
import com.atzuche.order.commons.vo.rentercost.GetReturnAndOverFeeVO;
import com.atzuche.order.commons.vo.rentercost.OwnerToPlatformCostReqVO;
import com.atzuche.order.commons.vo.rentercost.OwnerToRenterSubsidyReqVO;
import com.atzuche.order.commons.vo.rentercost.PlatformToOwnerSubsidyReqVO;
import com.atzuche.order.commons.vo.rentercost.PlatformToRenterSubsidyReqVO;
import com.atzuche.order.commons.vo.rentercost.RenterCostReqVO;
import com.atzuche.order.commons.vo.rentercost.RenterFineCostReqVO;
import com.atzuche.order.commons.vo.rentercost.RenterToPlatformCostReqVO;
import com.atzuche.order.commons.vo.req.OrderCostReqVO;
import com.atzuche.order.commons.vo.req.OwnerCostSettleDetailReqVO;
import com.atzuche.order.commons.vo.req.RenterAdjustCostReqVO;
import com.atzuche.order.commons.vo.res.*;
import com.atzuche.order.coreapi.entity.vo.RenterAndConsoleFineVO;
import com.atzuche.order.coreapi.entity.vo.RenterAndConsoleSubsidyVO;
import com.atzuche.order.coreapi.service.OrderCostAggregateService;
import com.atzuche.order.coreapi.service.OrderCostService;
import com.atzuche.order.coreapi.service.OwnerCostFacadeService;
import com.atzuche.order.coreapi.service.RenterCostFacadeService;
import com.atzuche.order.open.vo.RenterCostShortDetailVO;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.rentercost.entity.OrderConsoleCostDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.vo.GetReturnAndOverFeeDetailVO;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.settle.vo.res.RenterCostVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author jing.huang
 *
 */
@RestController
@Slf4j
public class OrderCostController {
	@Autowired
	private OrderCostService orderCostService;

	@Autowired
	private RenterCostFacadeService facadeService;
	@Autowired
	private OrderService orderService;

	@Autowired
	private RenterOrderService renterOrderService;

	@Autowired
	private OwnerOrderService ownerOrderService;

	@Autowired
	private CashierQueryService cashierQueryService;
	@Autowired
	private OwnerCostFacadeService ownerCostFacadeService;
	@Autowired
	private RenterOrderCostCombineService renterOrderCostCombineService;
	@Autowired
	private OrderCostAggregateService orderCostAggregateService;
	
	@PostMapping("/order/cost/renter/get")
	public ResponseData<OrderRenterCostResVO> orderCostRenterGet(@Valid @RequestBody OrderCostReqVO req, BindingResult bindingResult) {
		log.info("租客子订单费用详细 orderCostRenterGet params=[{}]", req.toString());
		BindingResultUtil.checkBindingResult(bindingResult);
		OrderRenterCostResVO resVo = orderCostService.orderCostRenterGet(req);
		return ResponseData.success(resVo);
	}

	@GetMapping("/order/renter/cost/fullDetail")
	public ResponseData<RenterCostDetailVO> getRenterCostFullDetail(@RequestParam("orderNo") String orderNo){
		OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
		if(orderEntity==null){
			throw new OrderNotFoundException(orderNo);
		}
		String memNo = orderEntity.getMemNoRenter();
		RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
		if(renterOrderEntity==null){
			throw new OrderNotFoundException(orderNo);
		}
		String renterOrderNo = renterOrderEntity.getRenterOrderNo();

		RenterCostDetailVO renterBasicCostDetailVO = facadeService.getRenterCostFullDetail(orderNo,renterOrderNo,memNo);

		return ResponseData.success(renterBasicCostDetailVO);
	}
	@GetMapping("/order/owner/cost/fullDetail")
	public ResponseData<OwnerCostDetailVO> getRenterCostFullDetail(@RequestParam("orderNo") String orderNo,
																   @RequestParam("ownerOrderNo") String ownerOrderNo,
																   @RequestParam("ownerMemNo") String ownerMemNo){
		OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
		if(orderEntity==null){
			throw new OrderNotFoundException(orderNo);
		}
		if((ownerOrderNo == null || ownerOrderNo.trim().length()<=0) && (ownerMemNo == null || ownerMemNo.trim().length()<=0)){
			ResponseData responseData = new ResponseData();
			responseData.setResCode(ErrorCode.INPUT_ERROR.getCode());
			responseData.setResMsg("车主子订单号或车主会员号必须有一个不为空！");
			return responseData;
		}
		OwnerCostDetailVO ownerCostDetailVO = ownerCostFacadeService.getOwnerCostFullDetail(orderNo,ownerOrderNo,ownerMemNo);
		return ResponseData.success(ownerCostDetailVO);
	}

	public ResponseData getCarOwnerIncomeFullDetail(@RequestParam("orderNo")String orderNo,@RequestParam("ownerNo")String ownerNo){
		OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
		if(orderEntity==null){
			throw new OrderNotFoundException(orderNo);
		}

		OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
		if(ownerOrderEntity==null){
			throw new OwnerOrderNotFoundException(orderNo);
		}

		if(!ownerNo.equals(ownerOrderEntity.getMemNo())){
           log.warn("查到的车主号码:{}和前端请求的车主号码:{}不一致",ownerOrderEntity.getMemNo(),ownerNo);
           throw new OwnerOrderNotFoundException(orderNo);
		}

		//FIXME:
        return null;
	}




	/**
	 * 获取租客的费用简况
	 * @param orderNo
	 * @return
	 */
	@GetMapping("/order/renter/cost/shortDetail")
	public ResponseData<RenterCostShortDetailVO> getRenterCostShortDetail(String orderNo){
         OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
         if(orderEntity==null){
         	throw new OrderNotFoundException(orderNo);
		 }
         String memNo = orderEntity.getMemNoRenter();
		 RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
		 if(renterOrderEntity==null){
			 throw new OrderNotFoundException(orderNo);
		 }
		 String renterOrderNo = renterOrderEntity.getRenterOrderNo();

		 int totalRentCostAmtWithoutFine = facadeService.getTotalRenterCostWithoutFine(orderNo,renterOrderNo,memNo);
		 int totalFineAmt = facadeService.getTotalFine(orderNo,renterOrderNo,memNo);

		AccountRenterDepositEntity depositEntity = cashierQueryService.getTotalToPayDepositAmt(orderNo);
		if(depositEntity == null){
            throw new AccountDepositException();
        }
		AccountRenterWzDepositEntity wzDepositEntity = cashierQueryService.getTotalToPayWzDepositAmt(orderNo);
		if(wzDepositEntity == null){
            throw new AccountWzDepositException();
        }
        Integer isAuthorize = depositEntity.getIsAuthorize();
        Integer wzIsAuthorize = wzDepositEntity.getIsAuthorize();
        int expReturnDeposit = 0;
        int expReturnWzDeposit = 0;
        if(isAuthorize == null){
            expReturnDeposit = depositEntity.getShifuDepositAmt()==null?0:depositEntity.getShifuDepositAmt();
        }else if(AuthorizeEnum.IS.getCode() == isAuthorize){
            expReturnDeposit = depositEntity.getAuthorizeDepositAmt()==null?0:depositEntity.getAuthorizeDepositAmt();
        }else if(AuthorizeEnum.NOT.getCode() == isAuthorize){
            expReturnDeposit = depositEntity.getShifuDepositAmt()==null?0:depositEntity.getShifuDepositAmt();;
        }else if(AuthorizeEnum.CREDIT.getCode() == isAuthorize){
            int authorizeDepositAmt = depositEntity.getAuthorizeDepositAmt() == null ? 0 : depositEntity.getAuthorizeDepositAmt();
            expReturnDeposit = depositEntity.getCreditPayAmt()==null?0+authorizeDepositAmt:depositEntity.getCreditPayAmt()+authorizeDepositAmt;;
        }else{
            expReturnDeposit = depositEntity.getShifuDepositAmt()==null?0:depositEntity.getShifuDepositAmt();;
        }
        if(wzIsAuthorize == null){
            expReturnWzDeposit = wzDepositEntity.getShishouDeposit()==null?0:wzDepositEntity.getShishouDeposit();
        }else if(AuthorizeEnum.IS.getCode() == wzIsAuthorize){
            expReturnWzDeposit =  wzDepositEntity.getAuthorizeDepositAmt() == null?0: wzDepositEntity.getAuthorizeDepositAmt();
        }else if(AuthorizeEnum.NOT.getCode() == wzIsAuthorize){
            expReturnWzDeposit =  wzDepositEntity.getShishouDeposit()==null?0: wzDepositEntity.getShishouDeposit();
        }else if(AuthorizeEnum.CREDIT.getCode() == wzIsAuthorize){
            int authorizeDepositAmt = wzDepositEntity.getAuthorizeDepositAmt() == null ? 0 : wzDepositEntity.getAuthorizeDepositAmt();
            expReturnWzDeposit =  wzDepositEntity.getCreditPayAmt()==null?0+authorizeDepositAmt: wzDepositEntity.getCreditPayAmt()+authorizeDepositAmt;
        }else{
            expReturnWzDeposit =  wzDepositEntity.getShishouDeposit()==null?0: wzDepositEntity.getShishouDeposit();
        }

        RenterCostShortDetailVO shortDetail = new RenterCostShortDetailVO();

		shortDetail.setTotalRentCostAmt(-totalRentCostAmtWithoutFine);
		shortDetail.setTotalFineAmt(-totalFineAmt);
		shortDetail.setYingFuDeposit(-depositEntity.getYingfuDepositAmt());
		shortDetail.setShiFuDeposit(depositEntity.getShifuDepositAmt());
		shortDetail.setYingFuWzDeposit(-wzDepositEntity.getYingshouDeposit());
		shortDetail.setShiFuWzDeposit(wzDepositEntity.getShishouDeposit());
		shortDetail.setToPayDeposit(-(depositEntity.getYingfuDepositAmt()+depositEntity.getShifuDepositAmt()));
		shortDetail.setToPayWzDeposit(-(wzDepositEntity.getYingshouDeposit()+wzDepositEntity.getShishouDeposit()));
		shortDetail.setExpReturnDeposit(expReturnDeposit);
		shortDetail.setExpReturnWzDeposit(expReturnWzDeposit);
		shortDetail.setOrderNo(orderNo);

		return ResponseData.success(shortDetail);

	}


	
	@PostMapping("/order/cost/owner/get")
	public ResponseData<OrderOwnerCostResVO> orderCostOwnerGet(@Valid @RequestBody OrderCostReqVO req, BindingResult bindingResult) {
		log.info("车主子订单费用详细 orderCostOwnerGet params=[{}]", req.toString());
		BindingResultUtil.checkBindingResult(bindingResult);
        OwnerOrderEntity ownerOrderEntity = null;
        if(StringUtils.isNotBlank(req.getSubOrderNo())) {
            ownerOrderEntity = ownerOrderService.getOwnerOrderByOwnerOrderNo(req.getSubOrderNo());
            if(ownerOrderEntity == null){
                log.error("获取订单数据(车主)为空orderNo={}",req.getOrderNo());
                throw new OwnerOrderNotFoundException(ownerOrderEntity.getOwnerOrderNo());
            }
        }
		OrderOwnerCostResVO resVo = orderCostService.orderCostOwnerGet(req);
		return ResponseData.success(resVo);

	}
	
	@GetMapping("/order/owner/cost/settle/detail/get")
	public ResponseData<OwnerCostSettleDetailVO> getOwnerCostSettleDetail(@RequestParam("orderNo") String orderNo,@RequestParam("ownerNo") String ownerNo){
		OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
		if(orderEntity==null){
			throw new OrderNotFoundException(orderNo);
		}
		OwnerCostSettleDetailVO ownerCostSettleDetailVO = ownerCostFacadeService.getOwnerCostSettleDetail(orderNo,ownerNo);
		return ResponseData.success(ownerCostSettleDetailVO);
	}
	
	
	@PostMapping("/order/owner/cost/settle/detail/list")
	public ResponseData<List<OwnerCostSettleDetailVO>> listOwnerCostSettleDetail(@RequestBody OwnerCostSettleDetailReqVO req){
		List<OwnerCostSettleDetailVO> listVo = ownerCostFacadeService.listOwnerCostSettleDetail(req);
		return ResponseData.success(listVo);
	}
	

    @GetMapping("/order/renter/cost/detail")
	public ResponseData<RenterCostDetailDTO> renterCostDetail(@RequestParam("orderNo") String orderNo){
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        if(orderEntity==null){
            throw new OrderNotFoundException(orderNo);
        }
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        if(renterOrderEntity==null){
            throw new OrderNotFoundException(orderNo);
        }
        RenterCostDetailDTO renterCostDetailDTO = facadeService.renterCostDetail(orderNo,renterOrderEntity.getRenterOrderNo(),renterOrderEntity.getRenterMemNo());
        return ResponseData.success(renterCostDetailDTO);
    }
    
    /**
     * 实收应收mock
     * @param orderNo
     * @return
     */
    @GetMapping("/order/renter/cost/shishouDetail")
	public ResponseData<RenterCostVO> renterCostShishouDetail(@RequestParam("orderNo") String orderNo){
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        if(orderEntity==null){
            throw new OrderNotFoundException(orderNo);
        }
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        if(renterOrderEntity==null){
            throw new OrderNotFoundException(orderNo);
        }
        RenterCostVO renterCostVO = facadeService.renterCostShishouDetail(orderNo,renterOrderEntity.getRenterOrderNo(),renterOrderEntity.getRenterMemNo());
        return ResponseData.success(renterCostVO);
    }
    
    
    /**
     * 获取取还车费用和超运能费用
     * @param req
     */
    @PostMapping("/order/renter/cost/getreturnfee/detail")
	public ResponseData<GetReturnAndOverFeeDetailVO> getGetReturnFeeDetail(@RequestBody GetReturnAndOverFeeVO req){
    	GetReturnAndOverFeeDetailVO getReturnAndOverFeeDetailVO = renterOrderCostCombineService.getGetReturnAndOverFeeDetailVO(req);
		return ResponseData.success(getReturnAndOverFeeDetailVO);
	}
    
    
    /**
     * 获取附加驾驶员保障费
     * @param req
     */
    @PostMapping("/order/renter/cost/extraDriverInsure/detail")
	public ResponseData<RenterOrderCostDetailEntity> getExtraDriverInsureDetail(@RequestBody ExtraDriverDTO req){
    	RenterOrderCostDetailEntity renterOrderCostDetailEntity = renterOrderCostCombineService.getExtraDriverInsureAmtEntity(req);
		return ResponseData.success(renterOrderCostDetailEntity);
	}
    
    
    /**
     * 获取租客补贴
     * @param orderNo
     * @param renterOrderNo
     */
    @GetMapping("/order/renter/cost/renterAndConsoleSubsidy")
	public ResponseData<RenterAndConsoleSubsidyVO> getRenterAndConsoleSubsidyVO(@RequestParam("orderNo") String orderNo,@RequestParam("renterOrderNo") String renterOrderNo){
    	RenterAndConsoleSubsidyVO renterAndConsoleSubsidyVO = orderCostService.getRenterAndConsoleSubsidyVO(orderNo, renterOrderNo);
    	return ResponseData.success(renterAndConsoleSubsidyVO);
    }
    
    
    /**
     * 获取管理费用
     * @param orderNo
     */
    @GetMapping("/order/renter/cost/listOrderConsoleCostDetail")
	public ResponseData<List<OrderConsoleCostDetailEntity>> listOrderConsoleCostDetailEntity(@RequestParam("orderNo") String orderNo){
    	List<OrderConsoleCostDetailEntity> list = orderCostService.listOrderConsoleCostDetailEntity(orderNo);
    	return ResponseData.success(list);
    }
    
    
    /**
     * 保存调价
     * @param req
     */
    @PostMapping("/order/renter/cost/updateRenterPriceAdjustmentByOrderNo")
	public ResponseData<?> updateRenterPriceAdjustmentByOrderNo(@RequestBody RenterAdjustCostReqVO req){
    	orderCostService.updateRenterPriceAdjustmentByOrderNo(req);
		return ResponseData.success();
	}
    
    
    /**
     * 租客需支付给平台的费用 修改
     * @param req
     */
    @PostMapping("/order/renter/cost/updateRenterToPlatFormListByOrderNo")
	public ResponseData<?> updateRenterToPlatFormListByOrderNo(@RequestBody RenterToPlatformCostReqVO req){
    	orderCostService.updateRenterToPlatFormListByOrderNo(req);
		return ResponseData.success();
	}
    
    
    /**
     * 添加，车主需支付给平台的费用
     * @param req
     */
    @PostMapping("/order/owner/cost/updateOwnerToPlatFormListByOrderNo")
	public ResponseData<?> updateOwnerToPlatFormListByOrderNo(@RequestBody OwnerToPlatformCostReqVO req){
    	orderCostService.updateOwnerToPlatFormListByOrderNo(req);
		return ResponseData.success();
	}
    
    
    /**
     * 租客租金明细
     * @param req
     */
    @PostMapping("/order/renter/cost/findRenterRentAmtListByOrderNo")
	public ResponseData<RenterRentDetailDTO> findRenterRentAmtListByOrderNo(@RequestBody RenterCostReqVO req){
    	RenterRentDetailDTO renterRentDetailDTO = orderCostService.findRenterRentAmtListByOrderNo(req);
		return ResponseData.success(renterRentDetailDTO);
	}
    
    
    /**
     * 获取租客罚金
     * @param orderNo
     * @param renterOrderNo
     */
    @GetMapping("/order/renter/cost/renterAndConsoleFine")
	public ResponseData<RenterAndConsoleFineVO> getRenterAndConsoleFineVO(@RequestParam("orderNo") String orderNo,@RequestParam("renterOrderNo") String renterOrderNo){
    	RenterAndConsoleFineVO renterAndConsoleFineVO = orderCostService.getRenterAndConsoleFineVO(orderNo, renterOrderNo);
    	return ResponseData.success(renterAndConsoleFineVO);
    }
    
    
    /**
     * 违约罚金 修改违约罚金
     * @param req
     */
    @PostMapping("/order/renterowner/cost/updatefineAmtListByOrderNo")
	public ResponseData<?> updatefineAmtListByOrderNo(@RequestBody RenterFineCostReqVO req){
    	orderCostAggregateService.updatefineAmtListByOrderNo(req);
		return ResponseData.success();
	}
    
    
    /**
     * 平台给租客的补贴
     * @param req
     */
    @PostMapping("/order/renter/cost/updatePlatFormToRenterListByOrderNo")
	public ResponseData<?> updatePlatFormToRenterListByOrderNo(@RequestBody PlatformToRenterSubsidyReqVO req){
    	orderCostAggregateService.updatePlatFormToRenterListByOrderNo(req);
		return ResponseData.success();
	}
    
    /**
     * 租金补贴
     * @param req
     */
    @PostMapping("/order/renter/cost/ownerToRenterRentAmtSubsidy")
	public ResponseData<?> ownerToRenterRentAmtSubsidy(@RequestBody OwnerToRenterSubsidyReqVO req){
    	orderCostAggregateService.ownerToRenterRentAmtSubsidy(req);
		return ResponseData.success();
	}
    
    
    /**
     * 平台给车主的补贴
     * @param req
     */
    @PostMapping("/order/renter/cost/updatePlatFormToOwnerListByOrderNo")
	public ResponseData<?> updatePlatFormToOwnerListByOrderNo(@RequestBody PlatformToOwnerSubsidyReqVO req){
    	orderCostAggregateService.updatePlatFormToOwnerListByOrderNo(req);
		return ResponseData.success();
	}
    
}
