/**
 * 
 */
package com.atzuche.order.admin.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.common.AdminUserUtil;
import com.atzuche.order.admin.exception.RenterCostFailException;
import com.atzuche.order.admin.vo.req.cost.*;
import com.atzuche.order.admin.vo.resp.cost.AdditionalDriverInsuranceVO;
import com.atzuche.order.admin.vo.resp.income.RenterToPlatformVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.*;
import com.atzuche.order.commons.*;
import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.commons.entity.orderDetailDto.OrderConsoleCostDetailDTO;
import com.atzuche.order.commons.entity.ownerOrderDetail.RenterRentDetailDTO;
import com.atzuche.order.commons.entity.rentCost.RenterCostDetailDTO;
import com.atzuche.order.commons.enums.*;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import com.atzuche.order.commons.enums.cashcode.ConsoleCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.FineTypeCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.exceptions.NotAllowedEditException;
import com.atzuche.order.commons.vo.rentercost.RenterAndConsoleFineVO;
import com.atzuche.order.commons.vo.rentercost.RenterAndConsoleSubsidyVO;
import com.atzuche.order.commons.vo.rentercost.RenterOrderCostDetailEntity;
import com.atzuche.order.commons.vo.req.AdditionalDriverInsuranceIdsReqVO;
import com.atzuche.order.commons.vo.req.RenterAdjustCostReqVO;
import com.atzuche.order.commons.vo.res.rentcosts.ConsoleRenterOrderFineDeatailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleCostDetailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.RenterOrderFineDeatailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.mem.MemProxyService;
import com.atzuche.order.open.service.FeignAdditionDriverService;
import com.atzuche.order.open.service.FeignMemberService;
import com.atzuche.order.open.service.FeignOrderCostService;
import com.atzuche.order.ownercost.service.ConsoleOwnerOrderFineDeatailService;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.ownercost.service.OwnerOrderSubsidyDetailService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.renterorder.entity.RenterDepositDetailEntity;
import com.atzuche.order.renterorder.service.RenterAdditionalDriverService;
import com.atzuche.order.renterorder.service.RenterDepositDetailService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.settle.service.OrderSettleService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.platformcost.OrderSubsidyDetailUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author jing.huang
 *
 */
@Slf4j
@Service
public class OrderCostDetailService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private OrderService orderService;
	@Autowired
	private RenterDepositDetailService renterDepositDetailService;
    @Autowired
    private MemProxyService memberService;
    @Autowired
    RenterAdditionalDriverService renterAdditionalDriverService;
    @Autowired
    OrderSettleService orderSettleService;
    @Autowired
    OwnerOrderService ownerOrderService;
    @Autowired
    OwnerOrderSubsidyDetailService ownerOrderSubsidyDetailService;
    @Autowired
    RenterOrderService renterOrderService;
    @Autowired
    ConsoleOwnerOrderFineDeatailService consoleOwnerOrderFineDeatailService;
    @Autowired
    FeignOrderCostService feignOrderCostService;
    @Autowired
    private FeignAdditionDriverService feignAdditionDriverService;
    @Autowired
    private OrderCostRemoteService orderCostRemoteService;
    @Autowired
    private FeignMemberService feignMemberService;

	public ReductionDetailResVO findReductionDetailsListByOrderNo(RenterCostReqVO renterCostReqVO) throws Exception {
		ReductionDetailResVO resVo = new ReductionDetailResVO();
	     //根据订单号查询会员号
		//主订单
	      OrderEntity orderEntity = orderService.getOrderEntity(renterCostReqVO.getOrderNo());
	      if(orderEntity == null){
	      	logger.error("获取订单数据为空orderNo={}",renterCostReqVO.getOrderNo());
	          throw new Exception("获取订单数据为空");
	      }
		      
		//减免前车辆押金
	    String reductionBeforeRentDepost="";
		//减免后车辆押金
	    String reductionAfterRentDepost="";
		//减免项目列表
		List<ReductionTaskResVO> reductTaskList=null;
		//年份系数
	    String yearCoefficient="";
	    //品牌系数
	    String brandCoefficient="";
	    //平台减免金额
	    String reductionAmt="";
		
		//押金比例
        RenterDepositDetailEntity renterDepositDetailEntity = renterDepositDetailService.queryByOrderNo(renterCostReqVO.getOrderNo());
        if(renterDepositDetailEntity != null) {
        	reductionBeforeRentDepost = renterDepositDetailEntity.getOriginalDepositAmt()!=null?String.valueOf(renterDepositDetailEntity.getOriginalDepositAmt()):"";
        	reductionAmt = renterDepositDetailEntity.getReductionDepositAmt()!=null?String.valueOf(renterDepositDetailEntity.getReductionDepositAmt()):"";
        	reductionAfterRentDepost = "";
        	if(renterDepositDetailEntity.getOriginalDepositAmt()!=null && renterDepositDetailEntity.getReductionDepositAmt()!=null) {
        		reductionAfterRentDepost = String.valueOf(renterDepositDetailEntity.getOriginalDepositAmt().intValue() - renterDepositDetailEntity.getReductionDepositAmt().intValue());
        	}
        	yearCoefficient = String.valueOf(renterDepositDetailEntity.getNewCarCoefficient());
        	brandCoefficient = String.valueOf(renterDepositDetailEntity.getCarSpecialCoefficient());
        	
        }

        //封装任务列表
        reductTaskList = new ArrayList<ReductionTaskResVO>(); //初始化
        //租客会员信息
//        RenterMemberDTO renterMemberDTO = memberService.getRenterMemberInfo(orderEntity.getMemNoRenter());
//        List<RenterMemberRightDTO> renterMemberRightDTOList = renterMemberDTO.getRenterMemberRightDTOList();
        //会员权益,从落库表中获取
        //RenterMemberDTO renterMemberDTO = renterMemberService.selectrenterMemberByRenterOrderNo(renterCostReqVO.getRenterOrderNo(), true);
        RenterMemberDTO renterMemberDTO = getRenterMemeberFromRemot(renterCostReqVO.getRenterOrderNo(), true);
        List<RenterMemberRightDTO> renterMemberRightDTOList = renterMemberDTO.getRenterMemberRightDTOList();
        //数据封装
        putTaskRight(reductTaskList,renterMemberRightDTOList);
        
        
        resVo.setReductionBeforeRentDepost(reductionBeforeRentDepost);
        resVo.setReductionAfterRentDepost(reductionAfterRentDepost);
        resVo.setReductTaskList(reductTaskList);
        resVo.setYearCoefficient(yearCoefficient);
        resVo.setBrandCoefficient(brandCoefficient);
        resVo.setReductionAmt(reductionAmt);
		return resVo;
	}

	private void putTaskRight(List<ReductionTaskResVO> reductTaskList,
			List<RenterMemberRightDTO> renterMemberRightDTOList) {
		
		int totalReductionOrderRatio = 0;
		int totalReductionItemGetRatio = 0;
		for (RenterMemberRightDTO renterMemberRightDTO : renterMemberRightDTOList) {
			//任务
			if(renterMemberRightDTO.getRightType().intValue() == RightTypeEnum.TASK.getCode().intValue()) {
				ReductionTaskResVO task = new ReductionTaskResVO();
				task.setReductionItemGetRatio(renterMemberRightDTO.getRightValue());
				task.setReductionItemName(renterMemberRightDTO.getRightName());
				task.setReductionItemRule(renterMemberRightDTO.getRightDesc());
				task.setReductionOrderRatio(renterMemberRightDTO.getRightValue());
				///
				reductTaskList.add(task);

                totalReductionOrderRatio += task.getReductionOrderRatio()==null?0:Integer.valueOf(task.getReductionOrderRatio());
                totalReductionItemGetRatio += task.getReductionItemGetRatio()==null?0:Integer.valueOf(task.getReductionItemGetRatio());
			}
		}
        String reductionOrderRatio = String.valueOf(totalReductionOrderRatio>=70?70:totalReductionOrderRatio);
		String reductionItemGetRatio = String.valueOf(totalReductionItemGetRatio>=70?70:totalReductionItemGetRatio);
        ReductionTaskResVO task = new ReductionTaskResVO();
        task.setReductionItemGetRatio(reductionItemGetRatio);
        task.setReductionItemName("总计");
        task.setReductionItemRule("最高减免比例70%（总计超过70%，按70%计算）");
        task.setReductionOrderRatio(reductionOrderRatio);
        reductTaskList.add(task);
	}
	
	
	public AdditionalDriverInsuranceVO findAdditionalDriverInsuranceByOrderNo(RenterCostReqVO renterCostReqVO) throws Exception {
		//根据订单号查询会员号
		//主订单
	      OrderEntity orderEntity = orderService.getOrderEntity(renterCostReqVO.getOrderNo());
	      if(orderEntity == null){
	      	logger.error("获取订单数据为空orderNo={}",renterCostReqVO.getOrderNo());
	          throw new Exception("获取订单数据为空");
	      }
			      
        //租客会员信息
      RenterMemberDTO renterMemberDTO = memberService.getRenterMemberInfo(orderEntity.getMemNoRenter());
      List<CommUseDriverInfoDTO> commUseDriverList = renterMemberDTO.getCommUseDriverList();
      
      //封装对象
      ExtraDriverDTO extraDriverDTO = new ExtraDriverDTO();
      CostBaseDTO costBaseDTO = new CostBaseDTO();
      costBaseDTO.setOrderNo(renterCostReqVO.getOrderNo());
      costBaseDTO.setRenterOrderNo(renterCostReqVO.getRenterOrderNo());
      costBaseDTO.setMemNo(orderEntity.getMemNoRenter());
      costBaseDTO.setStartTime(orderEntity.getExpRentTime());
      costBaseDTO.setEndTime(orderEntity.getExpRevertTime());
      extraDriverDTO.setCostBaseDTO(costBaseDTO);
      
      List<String> lstDriverId = renterAdditionalDriverService.listDriverIdByRenterOrderNo(renterCostReqVO.getRenterOrderNo());
      
      AdditionalDriverInsuranceVO resVo = new AdditionalDriverInsuranceVO();
      List<String> driverIds = new ArrayList<String>();
	  driverIds.add("1");// 计算一个人的价格
	  extraDriverDTO.setDriverIds(driverIds);
	  RenterOrderCostDetailEntity extraCost = orderCostRemoteService.getExtraDriverInsureDetail(extraDriverDTO);
	  String unitExtra = extraCost == null ? "0" : String.valueOf( NumberUtils.convertNumberToZhengshu(extraCost.getTotalAmount()));
	  putComUseDriverListAlreaySave(resVo,commUseDriverList,lstDriverId,unitExtra);
      
      putComUseDriverList(resVo,commUseDriverList,lstDriverId,unitExtra);
		return resVo;
	}
	
	/**
	 * 已经保存的。
	 * @param resVo
	 * @param commUseDriverList
	 * @param extraDriverDTO
	 * @param lstDriverId
	 * @throws Exception
	 */
	private void putComUseDriverListAlreaySave(AdditionalDriverInsuranceVO resVo, List<CommUseDriverInfoDTO> commUseDriverList,List<String> lstDriverId, String unitExtra) throws Exception {
		if(lstDriverId != null && lstDriverId.size() > 0) {
			List<CommUseDriverInfoStringDateDTO> listCommUseDriverInfoDTO = new ArrayList<CommUseDriverInfoStringDateDTO>();
			
			for (CommUseDriverInfoDTO commUseDriverInfoDTO : commUseDriverList) {
				//已经入库的lstDriverId
				if(lstDriverId.contains(commUseDriverInfoDTO.getId().toString())) {
					CommUseDriverInfoStringDateDTO dto = new CommUseDriverInfoStringDateDTO();
					//获取时间来转换
					Date validityEndDate = commUseDriverInfoDTO.getValidityEndDate();
					Date validityStartDate = commUseDriverInfoDTO.getValidityStartDate();
					BeanUtils.copyProperties(commUseDriverInfoDTO,dto);
					
					LocalDate localDateEnd = validityEndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					String end = LocalDateTimeUtils.localdateToStringChinese(localDateEnd);
					LocalDate localDateStart = validityStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					String start = LocalDateTimeUtils.localdateToStringChinese(localDateStart);
					//数据封装
					dto.setValidityEndDate(end);
					dto.setValidityStartDate(start);
					dto.setAmt(unitExtra);
					listCommUseDriverInfoDTO.add(dto);
				}
			}
			//已经保存的列表
			resVo.setListCommUseDriverInfoAlreadySaveDTO(listCommUseDriverInfoDTO);
		}
		
	}
	
	/**
	 * 数据封装
	 * @param resVo
	 * @param commUseDriverList
	 * @throws Exception
	 */
	private void putComUseDriverList(AdditionalDriverInsuranceVO resVo, List<CommUseDriverInfoDTO> commUseDriverList,List<String> lstDriverId, String unitExtra) throws Exception {
			List<CommUseDriverInfoStringDateDTO> listCommUseDriverInfoDTO = new ArrayList<CommUseDriverInfoStringDateDTO>();
			for (CommUseDriverInfoDTO commUseDriverInfoDTO : commUseDriverList) {
				//已经入库的lstDriverId
				//允许全部的展示。@邵大宏 前端会做处理。20200211
//				if(!lstDriverId.contains(commUseDriverInfoDTO.getId().toString())) {  //不存在的，则允许再次新增。
					CommUseDriverInfoStringDateDTO dto = new CommUseDriverInfoStringDateDTO();
					//获取时间来转换
					Date validityEndDate = commUseDriverInfoDTO.getValidityEndDate();
					Date validityStartDate = commUseDriverInfoDTO.getValidityStartDate();
					BeanUtils.copyProperties(commUseDriverInfoDTO,dto);
					
					LocalDate localDateEnd = validityEndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					String end = LocalDateTimeUtils.localdateToStringChinese(localDateEnd);
					LocalDate localDateStart = validityStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					String start = LocalDateTimeUtils.localdateToStringChinese(localDateStart);
					//数据封装
					dto.setValidityEndDate(end);
					dto.setValidityStartDate(start);
					dto.setAmt(unitExtra);
					listCommUseDriverInfoDTO.add(dto);
//				}
			}
			
			resVo.setListCommUseDriverInfoDTO(listCommUseDriverInfoDTO);
//		}
		
	}
	
	/**
	 * 新增附加驾驶人
	 * @param renterCostReqVO
	 * @return
	 * @throws Exception 
	 */
	public void insertAdditionalDriverInsuranceByOrderNo(AdditionalDriverInsuranceIdsReqVO renterCostReqVO) throws Exception {
        //统一设置修改人名称。20200205 huangjing
        String userName = AdminUserUtil.getAdminUser().getAuthName(); // 获取的管理后台的用户名。
        renterCostReqVO.setUpdateOp(userName);
        renterCostReqVO.setCreateOp(userName);
        renterCostReqVO.setOperatorId(userName);
        insertAdditionalDriverFromRemot(renterCostReqVO);
	}


	public PlatformToRenterSubsidyResVO findPlatFormToRenterListByOrderNo(RenterCostReqVO renterCostReqVO) throws Exception {
		//根据费用编码获取
		PlatformToRenterSubsidyResVO resVo = new PlatformToRenterSubsidyResVO();
		//根据订单号查询会员号
		//主订单
	      OrderEntity orderEntity = orderService.getOrderEntity(renterCostReqVO.getOrderNo());
	      if(orderEntity == null){
	      	logger.error("获取订单数据为空orderNo={}",renterCostReqVO.getOrderNo());
	          throw new Exception("获取订单数据为空");
	      }
		 
	    int dispatching = 0;
	    int oil = 0;
	    int cleancar = 0;
	    int getReturnDelay = 0;
	    int delay=0;
	    int traffic=0;
	    int insure=0;
	    int rentamt=0;
	    int other=0;
	    int abatement=0;
	    int fee=0;
	    // 远程调用获取租客补贴
	    RenterAndConsoleSubsidyVO renterAndConsoleSubsidyVO = orderCostRemoteService.getRenterAndConsoleSubsidyVO(renterCostReqVO.getOrderNo(), renterCostReqVO.getRenterOrderNo());
	    // 管理后台补贴
	 	List<OrderConsoleSubsidyDetailEntity> consoleSubsidyList = renterAndConsoleSubsidyVO == null ? null:renterAndConsoleSubsidyVO.getConsoleSubsidyList();
		for (OrderConsoleSubsidyDetailEntity orderConsoleSubsidyDetailEntity : consoleSubsidyList) {
			//补贴来源方 1、租客 2、车主 3、平台
			//补贴方名称 1、租客 2、车主 3、平台
			if("3".equals(orderConsoleSubsidyDetailEntity.getSubsidySourceCode()) && "1".equals(orderConsoleSubsidyDetailEntity.getSubsidyTargetCode())){
				if(RenterCashCodeEnum.SUBSIDY_DISPATCHING_AMT.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					dispatching += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}else if(RenterCashCodeEnum.SUBSIDY_OIL.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					oil += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}else if(RenterCashCodeEnum.SUBSIDY_CLEANCAR.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					cleancar += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}else if(RenterCashCodeEnum.SUBSIDY_GETRETURNDELAY.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					getReturnDelay += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}else if(RenterCashCodeEnum.SUBSIDY_DELAY.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					delay += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}else if(RenterCashCodeEnum.SUBSIDY_TRAFFIC.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					traffic += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}else if(RenterCashCodeEnum.SUBSIDY_INSURE.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					insure += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}else if(RenterCashCodeEnum.SUBSIDY_RENTAMT.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					rentamt += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}else if(RenterCashCodeEnum.SUBSIDY_OTHER.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					other += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}else if(RenterCashCodeEnum.SUBSIDY_ABATEMENT.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					abatement += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}else if(RenterCashCodeEnum.SUBSIDY_FEE.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					fee += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}
			}
		}


        List<RenterOrderSubsidyDetailEntity> renterOrderSubsidyDetailEntityList = renterAndConsoleSubsidyVO == null ? null:renterAndConsoleSubsidyVO.getRenterOrderSubsidyDetailEntityList();
        int renterUpateSubsidyAmt = OrderSubsidyDetailUtils.getConsoleRenterUpateSubsidySystemAmt(consoleSubsidyList);
        int renterSubsidyAmt = OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.INSURE_TOTAL_PRICES);
        int abatementInsureAmt = OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.ABATEMENT_INSURE);
        int longGetReturnCarCostSubsidy = OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SRV_GET_COST) +
                OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.SRV_RETURN_COST) +
                OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT) +
                OrderSubsidyDetailUtils.getRenterSubsidyAmt(renterOrderSubsidyDetailEntityList, RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT);
        //数据封装
		resVo.setDispatchingSubsidy(String.valueOf(dispatching));
        resVo.setDispatchingSubsidySystem(String.valueOf(renterUpateSubsidyAmt));
	 	resVo.setOilSubsidy(String.valueOf(oil));
	 	resVo.setCleanCarSubsidy(String.valueOf(cleancar));
	 	resVo.setGetReturnDelaySubsidy(String.valueOf(getReturnDelay));
	 	resVo.setDelaySubsidy(String.valueOf(delay));
	 	resVo.setTrafficSubsidy(String.valueOf(traffic));
	 	resVo.setInsureSubsidy(String.valueOf(insure));
        resVo.setInsureSubsidySystem(String.valueOf(renterSubsidyAmt));
	 	resVo.setRentAmtSubsidy(String.valueOf(rentamt));
	 	resVo.setOtherSubsidy(String.valueOf(other));
	 	resVo.setAbatementSubsidy(String.valueOf(abatement));
        resVo.setAbatementSubsidySystem(String.valueOf(abatementInsureAmt));
	 	resVo.setFeeSubsidy(String.valueOf(fee));
        resVo.setLongGetReturnCarCostSubsidy(String.valueOf(longGetReturnCarCostSubsidy));//长租特有字段(运营习惯看到正数，取正值展示)
		return resVo;
	}
	
	
	/**
	 * 租客车主互相调价 车主租客互相调价展示
	 * @param renterCostReqVO
	 * @return RenterPriceAdjustmentResVO
	 */
	public RenterPriceAdjustmentResVO findRenterPriceAdjustmentByOrderNo(RenterCostReqVO renterCostReqVO) {
		RenterPriceAdjustmentResVO resVo = new RenterPriceAdjustmentResVO();
	    // 远程调用获取租客补贴
		RenterAndConsoleSubsidyVO renterAndConsoleSubsidyVO = orderCostRemoteService.getRenterAndConsoleSubsidyVO(renterCostReqVO.getOrderNo(), renterCostReqVO.getRenterOrderNo());
		// 管理后台补贴
		List<OrderConsoleSubsidyDetailEntity> consoleSubsidyList = renterAndConsoleSubsidyVO == null ? null:renterAndConsoleSubsidyVO.getConsoleSubsidyList();
	    //租客给车主的调价
		int renterToOwnerAdjustAmount = 0;
		//车主给租客的调价
	    int ownerToRenterAdjustAmount = 0;
	    
	    for (OrderConsoleSubsidyDetailEntity orderConsoleSubsidyDetailEntity : consoleSubsidyList) {
			//补贴来源方 1、租客 2、车主 3、平台
			//补贴方名称 1、租客 2、车主 3、平台
	    	//租客给车主的调价
			if("1".equals(orderConsoleSubsidyDetailEntity.getSubsidySourceCode()) && "2".equals(orderConsoleSubsidyDetailEntity.getSubsidyTargetCode())){
				if(RenterCashCodeEnum.SUBSIDY_RENTERTOOWNER_ADJUST.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
//					renterToOwnerAdjustAmount += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
					//不需要累计，只是查询记录
					renterToOwnerAdjustAmount = Math.abs(orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue());
					break;
				}
			}
		}
	    
	    for (OrderConsoleSubsidyDetailEntity orderConsoleSubsidyDetailEntity : consoleSubsidyList) {
		  //车主给租客的调价
			if("2".equals(orderConsoleSubsidyDetailEntity.getSubsidySourceCode()) && "1".equals(orderConsoleSubsidyDetailEntity.getSubsidyTargetCode())){
				if(RenterCashCodeEnum.SUBSIDY_OWNERTORENTER_ADJUST.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
//					ownerToRenterAdjustAmount += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
					//不需要累计，只是查询记录
					ownerToRenterAdjustAmount = Math.abs(orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue());
					break;
				}
			}
	    }
	    
	    //封装数据
	    resVo.setOwnerToRenterAdjustAmt(String.valueOf(ownerToRenterAdjustAmount));
	    resVo.setRenterToOwnerAdjustAmt(String.valueOf(renterToOwnerAdjustAmount));
		return resVo;
	}

	/**
	 * 保存调价
	 * @param renterCostReqVO
	 */
	@Transactional
	public void updateRenterPriceAdjustmentByOrderNo(RenterAdjustCostReqVO renterCostReqVO) {
		//获取的管理后台的用户名。
	    String userName = AdminUserUtil.getAdminUser().getAuthName();  
	    renterCostReqVO.setOperateName(userName);
	    orderCostRemoteService.updateRenterPriceAdjustmentByOrderNo(renterCostReqVO);
	}
	
	
	/**
	 * 查询租客需支付给平台的费用
	 * @param renterCostReqVO
	 * @return RenterToPlatformVO
	 */
	public RenterToPlatformVO findRenterToPlatFormListByOrderNo(RenterCostReqVO renterCostReqVO) {
		
		List<OrderConsoleCostDetailEntity> list = orderCostRemoteService.listOrderConsoleCostDetailEntity(renterCostReqVO.getOrderNo());
        List<OrderConsoleCostDetailDTO> orderConsoleCostDetailDTOS = new ArrayList<>();
        Optional.ofNullable(list).orElseGet(ArrayList::new).forEach(x->{
            OrderConsoleCostDetailDTO orderConsoleCostDetailDTO = new OrderConsoleCostDetailDTO();
            try {
				BeanUtils.copyProperties(x,orderConsoleCostDetailDTO);
			} catch (Exception e) {
				logger.error("e:",e);
			} 
            orderConsoleCostDetailDTOS.add(orderConsoleCostDetailDTO);
        });
        
        int oil = CostStatUtils.calConsoleAmtByCashNo(ConsoleCashCodeEnum.RENTER_OIL_FEE, orderConsoleCostDetailDTOS);
        int timeOut = CostStatUtils.calConsoleAmtByCashNo(ConsoleCashCodeEnum.RENTER_TIME_OUT, orderConsoleCostDetailDTOS);
        int modifyOrderTimeAndAddrAmt = CostStatUtils.calConsoleAmtByCashNo(ConsoleCashCodeEnum.RENTER_MODIFY_ADDR_TIME, orderConsoleCostDetailDTOS);
        int carWash = CostStatUtils.calConsoleAmtByCashNo(ConsoleCashCodeEnum.RENTER_CAR_WASH, orderConsoleCostDetailDTOS);
        int dlayWait = CostStatUtils.calConsoleAmtByCashNo(ConsoleCashCodeEnum.RENTER_DLAY_WAIT, orderConsoleCostDetailDTOS);
        int stopCar = CostStatUtils.calConsoleAmtByCashNo(ConsoleCashCodeEnum.RENTER_STOP_CAR, orderConsoleCostDetailDTOS);
        int extraMileage = CostStatUtils.calConsoleAmtByCashNo(ConsoleCashCodeEnum.RENTER_EXTRA_MILEAGE, orderConsoleCostDetailDTOS);
        
        //封装数据  显示的时候需要显示正数，保存为负数
        RenterToPlatformVO resVo = new RenterToPlatformVO();
        resVo.setOliAmt(String.valueOf(NumberUtils.convertNumberToZhengshu(oil)));
        resVo.setTimeOut(String.valueOf(NumberUtils.convertNumberToZhengshu(timeOut)));
        resVo.setModifyOrderTimeAndAddrAmt(String.valueOf(NumberUtils.convertNumberToZhengshu(modifyOrderTimeAndAddrAmt)));
        resVo.setCarWash(String.valueOf(NumberUtils.convertNumberToZhengshu(carWash)));
        resVo.setDlayWait(String.valueOf(NumberUtils.convertNumberToZhengshu(dlayWait)));
        resVo.setStopCar(String.valueOf(NumberUtils.convertNumberToZhengshu(stopCar)));
        resVo.setExtraMileage(String.valueOf(NumberUtils.convertNumberToZhengshu(extraMileage)));
		return resVo;
	}
	
	
	
	/**
	 * 修改租客需支付给平台的费用
	 * @param renterCostReqVO
	 */
	public void updateRenterToPlatFormListByOrderNo(com.atzuche.order.commons.vo.rentercost.RenterToPlatformCostReqVO renterCostReqVO) {
		//获取的管理后台的用户名。
		String userName = AdminUserUtil.getAdminUser().getAuthName();
		renterCostReqVO.setOperatorName(userName);
		orderCostRemoteService.updateRenterToPlatFormListByOrderNo(renterCostReqVO);
	}
	
	
	
	/**
	 * 添加，车主需支付给平台的费用
	 * @param ownerCostReqVO
	 */
	public void updateOwnerToPlatFormListByOrderNo(com.atzuche.order.commons.vo.rentercost.OwnerToPlatformCostReqVO ownerCostReqVO) {
		//获取的管理后台的用户名。
		String userName = AdminUserUtil.getAdminUser().getAuthName();
		ownerCostReqVO.setOperatorName(userName);
		orderCostRemoteService.updateOwnerToPlatFormListByOrderNo(ownerCostReqVO);
	}
		
	
	

	/**
	 * 租客租金明细
	 * @param renterCostReqVO
	 * @return RenterRentDetailDTO
	 */
	public RenterRentDetailDTO findRenterRentAmtListByOrderNo(com.atzuche.order.commons.vo.rentercost.RenterCostReqVO renterCostReqVO) {
		return orderCostRemoteService.findRenterRentAmtListByOrderNo(renterCostReqVO);
	}
	
	
	/**
	 * 违约罚金 违约罚金明细
	 * @param renterCostReqVO
	 * @return OrderRenterFineAmtDetailResVO
	 */
	public OrderRenterFineAmtDetailResVO findfineAmtListByOrderNo(RenterCostReqVO renterCostReqVO) {
		OrderRenterFineAmtDetailResVO resVo = new OrderRenterFineAmtDetailResVO();

		String renterBeforeReturnCarFineAmt = "";
		String renterDelayReturnCarFineAmt = "";
		String renterFineAmt = "";
		String renterGetReturnCarFineAmt = "";

		// 根据订单号查询会员号
		// 主订单
		OrderEntity orderEntity = orderService.getOrderEntity(renterCostReqVO.getOrderNo());
		if (orderEntity == null) {
			logger.error("获取订单数据为空orderNo={}", renterCostReqVO.getOrderNo());
			return null;
		}

		int renterBeforeReturnCarFineAmount = 0;
		int renterDelayReturnCarFineAmount = 0;

		// 取消订单违约金。
		int renterFineAmount = 0;
		// 取还车违约金
		int renterGetReturnCarFineAmount = 0;
		RenterAndConsoleFineVO fineList = orderCostRemoteService.getRenterAndConsoleFineVO(renterCostReqVO.getOrderNo(),
				renterCostReqVO.getRenterOrderNo());
		List<ConsoleRenterOrderFineDeatailEntity> list = fineList == null ? null : fineList.getConsoleFineList();
		// 累计求和
		for (ConsoleRenterOrderFineDeatailEntity consoleRenterOrderFineDeatailEntity : list) {
			if (consoleRenterOrderFineDeatailEntity.getFineType().intValue() == FineTypeCashCodeEnum.MODIFY_GET_FINE
					.getFineType().intValue()) {
				renterGetReturnCarFineAmount += consoleRenterOrderFineDeatailEntity.getFineAmount().intValue();
			} else if (consoleRenterOrderFineDeatailEntity.getFineType()
					.intValue() == FineTypeCashCodeEnum.MODIFY_RETURN_FINE.getFineType().intValue()) {
				renterGetReturnCarFineAmount += consoleRenterOrderFineDeatailEntity.getFineAmount().intValue();
			} else if (consoleRenterOrderFineDeatailEntity.getFineType()
					.intValue() == FineTypeCashCodeEnum.MODIFY_ADVANCE.getFineType().intValue()) {
				renterBeforeReturnCarFineAmount += consoleRenterOrderFineDeatailEntity.getFineAmount().intValue();
			} else if (consoleRenterOrderFineDeatailEntity.getFineType().intValue() == FineTypeCashCodeEnum.CANCEL_FINE
					.getFineType().intValue()) {
				renterFineAmount += consoleRenterOrderFineDeatailEntity.getFineAmount().intValue();
			} else if (consoleRenterOrderFineDeatailEntity.getFineType().intValue() == FineTypeCashCodeEnum.DELAY_FINE
					.getFineType().intValue()) {
				renterDelayReturnCarFineAmount += consoleRenterOrderFineDeatailEntity.getFineAmount().intValue();
			}
		}

		// 还需要从renter_order_fine_deatail获取
		// 租客罚金列表
		List<RenterOrderFineDeatailEntity> fineLst = fineList == null ? null : fineList.getFineList();
		// 累计求和
		for (RenterOrderFineDeatailEntity renterOrderFineDeatailEntity : fineLst) {
			if (renterOrderFineDeatailEntity.getFineType().intValue() == FineTypeCashCodeEnum.MODIFY_GET_FINE
					.getFineType().intValue()) {
				renterGetReturnCarFineAmount += renterOrderFineDeatailEntity.getFineAmount().intValue();
			} else if (renterOrderFineDeatailEntity.getFineType().intValue() == FineTypeCashCodeEnum.MODIFY_RETURN_FINE
					.getFineType().intValue()) {
				renterGetReturnCarFineAmount += renterOrderFineDeatailEntity.getFineAmount().intValue();
			} else if (renterOrderFineDeatailEntity.getFineType().intValue() == FineTypeCashCodeEnum.MODIFY_ADVANCE
					.getFineType().intValue()) {
//				renterBeforeReturnCarFineAmount += renterOrderFineDeatailEntity.getFineAmount().intValue();
				// 暂时先归到这个里面来。因为是不同的来源，console_ 否则修改的时候会有问题。 20200212
				renterFineAmount += renterOrderFineDeatailEntity.getFineAmount().intValue();
			} else if (renterOrderFineDeatailEntity.getFineType().intValue() == FineTypeCashCodeEnum.CANCEL_FINE
					.getFineType().intValue()) {
				renterFineAmount += renterOrderFineDeatailEntity.getFineAmount().intValue();
//			}else if(renterOrderFineDeatailEntity.getFineType().intValue() == FineTypeEnum.DELAY_FINE.getFineType().intValue()) {
//				renterDelayReturnCarFineAmount += renterOrderFineDeatailEntity.getFineAmount().intValue();
			}
		}

		// 取反,显示正数。
		renterBeforeReturnCarFineAmt = String
				.valueOf(NumberUtils.convertNumberToZhengshu(renterBeforeReturnCarFineAmount));// renterBeforeReturnCarFineAmount<0?String.valueOf(-renterBeforeReturnCarFineAmount):String.valueOf(renterBeforeReturnCarFineAmount);
		renterDelayReturnCarFineAmt = String
				.valueOf(NumberUtils.convertNumberToZhengshu(renterDelayReturnCarFineAmount));// renterDelayReturnCarFineAmount<0?String.valueOf(-renterDelayReturnCarFineAmount):String.valueOf(renterDelayReturnCarFineAmount);
		renterFineAmt = String.valueOf(NumberUtils.convertNumberToZhengshu(renterFineAmount));// renterFineAmount<0?String.valueOf(-renterFineAmount):String.valueOf(renterFineAmount);
		renterGetReturnCarFineAmt = String.valueOf(NumberUtils.convertNumberToZhengshu(renterGetReturnCarFineAmount)); // renterGetReturnCarFineAmount<0?String.valueOf(-renterGetReturnCarFineAmount):String.valueOf(renterGetReturnCarFineAmount);

		resVo.setRenterBeforeReturnCarFineAmt(renterBeforeReturnCarFineAmt);
		resVo.setRenterDelayReturnCarFineAmt(renterDelayReturnCarFineAmt);
		resVo.setRenterFineAmt(renterFineAmt);
		resVo.setRenterGetReturnCarFineAmt(renterGetReturnCarFineAmt);
		return resVo;
	}
	
	

	/**
	 * 违约罚金 修改违约罚金
	 * @param renterCostReqVO
	 */
	public void updatefineAmtListByOrderNo(com.atzuche.order.commons.vo.rentercost.RenterFineCostReqVO renterCostReqVO) {
		//统一设置修改人名称。20200205 huangjing
        String userName = AdminUserUtil.getAdminUser().getAuthName();
        renterCostReqVO.setOperatorName(userName);
        orderCostRemoteService.updatefineAmtListByOrderNo(renterCostReqVO);
	}
	
	
	/**
	 * 平台给租客的补贴
	 * @param renterCostReqVO
	 * @throws Exception 
	 */
	public void updatePlatFormToRenterListByOrderNo(com.atzuche.order.commons.vo.rentercost.PlatformToRenterSubsidyReqVO renterCostReqVO) {
		//获取的管理后台的用户名
    	String userName = AdminUserUtil.getAdminUser().getAuthName();
    	renterCostReqVO.setOperatorName(userName);
    	orderCostRemoteService.updatePlatFormToRenterListByOrderNo(renterCostReqVO);
		
	
	}
	
	
	/**
	 * 租金补贴
	 * @param ownerCostReqVO
	 * @throws Exception
	 */
	public void ownerToRenterRentAmtSubsidy(com.atzuche.order.commons.vo.rentercost.OwnerToRenterSubsidyReqVO ownerCostReqVO) {
		orderCostRemoteService.ownerToRenterRentAmtSubsidy(ownerCostReqVO);
	}
	
	/**
	 * 平台给车主的补贴
	 * @param ownerCostReqVO
	 * @throws Exception 
	 */
	public void updatePlatFormToOwnerListByOrderNo(com.atzuche.order.commons.vo.rentercost.PlatformToOwnerSubsidyReqVO ownerCostReqVO) {
		//获取的管理后台的用户名
    	String userName = AdminUserUtil.getAdminUser().getAuthName();
    	ownerCostReqVO.setOperatorName(userName);
    	orderCostRemoteService.updatePlatFormToOwnerListByOrderNo(ownerCostReqVO);
	}

    public RenterCostDetailDTO renterOrderCostDetail(String orderNo) {
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取租客费用详情");
        ResponseData<RenterCostDetailDTO> responseData = null;
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.cancelOrder");
            log.info("Feign 开始获取获取租客费用详情,orderNo={}", orderNo);
            Cat.logEvent(CatConstants.FEIGN_PARAM,orderNo);
            responseData = feignOrderCostService.renterCostDetail(orderNo);
            Cat.logEvent(CatConstants.FEIGN_RESULT,orderNo);
            if(responseData ==null || !ErrorCode.SUCCESS.getCode().equals(responseData.getResCode())){
                log.error("获取获取租客费用详情失败responseObject={},orderNo={}",JSON.toJSONString(responseData),orderNo);
                throw new RenterCostFailException();
            }
            t.setStatus(Transaction.SUCCESS);
            return responseData.getData();
        }catch (Exception e){
            log.error("Feign 获取租客费用详情,responseObject={},orderNo={}", JSON.toJSONString(responseData),orderNo,e);
            Cat.logError("Feign 获取租客费用详情",e);
            t.setStatus(e);
            throw e;
        }finally {
            t.complete();
        }
    }


    private RenterMemberDTO getRenterMemeberFromRemot(String renterOrderNo,boolean isNeedRight){
        ResponseData<RenterMemberDTO> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "获取租客会员信息");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.cancelOrder");
            log.info("Feign 开始获取租客会员信息,renterOrderNo={},isNeedRight={}", renterOrderNo,isNeedRight);
            Cat.logEvent(CatConstants.FEIGN_PARAM,renterOrderNo);
            responseObject =  feignMemberService.queryRenterMemberByOwnerOrderNo(renterOrderNo,isNeedRight);
            Cat.logEvent(CatConstants.FEIGN_RESULT,renterOrderNo);
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
            return responseObject.getData();
        }catch (Exception e){
            log.error("Feign 获取租客会员信息异常,responseObject={},renterOrderNo={}",JSON.toJSONString(responseObject),renterOrderNo,e);
            Cat.logError("Feign 获取租客会员信息异常",e);
            throw e;
        }finally {
            t.complete();
        }
    }

    private void insertAdditionalDriverFromRemot(AdditionalDriverInsuranceIdsReqVO renterCostReqVO){
        ResponseData<?> responseObject = null;
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "增加附加驾驶人");
        try{
            Cat.logEvent(CatConstants.FEIGN_METHOD,"feignOrderUpdateService.cancelOrder");
            log.info("Feign 开始增加附加驾驶人renterCostReqVO={}", renterCostReqVO);
            Cat.logEvent(CatConstants.FEIGN_PARAM,JSON.toJSONString(renterCostReqVO));
            responseObject =  feignAdditionDriverService.insertAdditionalDriver(renterCostReqVO);
            Cat.logEvent(CatConstants.FEIGN_RESULT,JSON.toJSONString(responseObject));
            ResponseCheckUtil.checkResponse(responseObject);
            t.setStatus(Transaction.SUCCESS);
           ;
        }catch (Exception e){
            log.error("Feign 增加附加驾驶人异常,responseObject={},renterCostReqVO={}",JSON.toJSONString(responseObject),JSON.toJSONString(renterCostReqVO),e);
            Cat.logError("Feign 增加附加驾驶人",e);
            throw e;
        }finally {
            t.complete();
        }
    }
}
