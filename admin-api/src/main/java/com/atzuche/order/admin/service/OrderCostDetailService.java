/**
 * 
 */
package com.atzuche.order.admin.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.admin.common.AdminUserUtil;
import com.atzuche.order.admin.vo.req.cost.AdditionalDriverInsuranceIdsReqVO;
import com.atzuche.order.admin.vo.req.cost.OwnerToPlatformCostReqVO;
import com.atzuche.order.admin.vo.req.cost.OwnerToRenterSubsidyReqVO;
import com.atzuche.order.admin.vo.req.cost.PlatformToOwnerSubsidyReqVO;
import com.atzuche.order.admin.vo.req.cost.PlatformToRenterSubsidyReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterAdjustCostReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterCostReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterFineCostReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterToPlatformCostReqVO;
import com.atzuche.order.admin.vo.resp.cost.AdditionalDriverInsuranceVO;
import com.atzuche.order.admin.vo.resp.income.RenterToPlatformVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.OrderRenterFineAmtDetailResVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.PlatformToRenterSubsidyResVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.ReductionDetailResVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.ReductionTaskResVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.RenterPriceAdjustmentResVO;
import com.atzuche.order.commons.CostStatUtils;
import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.CommUseDriverInfoDTO;
import com.atzuche.order.commons.entity.dto.CommUseDriverInfoSimpleDTO;
import com.atzuche.order.commons.entity.dto.CommUseDriverInfoStringDateDTO;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.ExtraDriverDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberRightDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderConsoleCostDetailDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderDTO;
import com.atzuche.order.commons.entity.ownerOrderDetail.RenterRentDetailDTO;
import com.atzuche.order.commons.enums.FineSubsidyCodeEnum;
import com.atzuche.order.commons.enums.FineSubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.FineTypeEnum;
import com.atzuche.order.commons.enums.RightTypeEnum;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.SubsidyTypeCodeEnum;
import com.atzuche.order.commons.enums.cashcode.ConsoleCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.mem.MemProxyService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderSubsidyDetailEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.ownercost.service.OwnerOrderSubsidyDetailService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentercost.entity.ConsoleRenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.OrderConsoleCostDetailEntity;
import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.service.ConsoleRenterOrderFineDeatailService;
import com.atzuche.order.rentercost.service.OrderConsoleCostDetailService;
import com.atzuche.order.rentercost.service.OrderConsoleSubsidyDetailService;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.rentercost.service.RenterOrderCostDetailService;
import com.atzuche.order.rentercost.service.RenterOrderSubsidyDetailService;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.entity.RenterDepositDetailEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterAdditionalDriverService;
import com.atzuche.order.renterorder.service.RenterDepositDetailService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.settle.service.OrderSettleService;

/**
 * @author jing.huang
 *
 */
@Service
public class OrderCostDetailService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ConsoleRenterOrderFineDeatailService consoleRenterOrderFineDeatailService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private RenterDepositDetailService renterDepositDetailService;
    @Autowired
    private MemProxyService memberService;
    @Autowired
    private RenterMemberService renterMemberService;
    @Autowired
    private RenterOrderCostCombineService renterOrderCostCombineService;
    @Autowired
    RenterAdditionalDriverService renterAdditionalDriverService;
    @Autowired
    RenterOrderCostDetailService renterOrderCostDetailService;
    @Autowired
    OrderConsoleSubsidyDetailService orderConsoleSubsidyDetailService;
    @Autowired
    OrderConsoleCostDetailService orderConsoleCostDetailService;
    @Autowired
    OrderSettleService orderSettleService;
    @Autowired
    RenterGoodsService renterGoodsService;
    @Autowired
    OwnerOrderService ownerOrderService;
    @Autowired
    RenterOrderSubsidyDetailService renterOrderSubsidyDetailService;
    @Autowired
    OwnerOrderSubsidyDetailService ownerOrderSubsidyDetailService;
    @Autowired
    RenterOrderService renterOrderService;
    
    
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
        	yearCoefficient = String.valueOf(renterDepositDetailEntity.getSuggestTotal());
        	brandCoefficient = String.valueOf(renterDepositDetailEntity.getCarSpecialCoefficient());
        	
        }

        //封装任务列表
        reductTaskList = new ArrayList<ReductionTaskResVO>(); //初始化
        //租客会员信息
//        RenterMemberDTO renterMemberDTO = memberService.getRenterMemberInfo(orderEntity.getMemNoRenter());
//        List<RenterMemberRightDTO> renterMemberRightDTOList = renterMemberDTO.getRenterMemberRightDTOList();
        //会员权益,从落库表中获取
        RenterMemberDTO renterMemberDTO = renterMemberService.selectrenterMemberByRenterOrderNo(renterCostReqVO.getRenterOrderNo(), true);
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
			}
		}
		
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
      putComUseDriverListAlreaySave(resVo,commUseDriverList,extraDriverDTO,lstDriverId);
      
      putComUseDriverList(resVo,commUseDriverList,extraDriverDTO,lstDriverId);
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
	private void putComUseDriverListAlreaySave(AdditionalDriverInsuranceVO resVo, List<CommUseDriverInfoDTO> commUseDriverList,ExtraDriverDTO extraDriverDTO,List<String> lstDriverId) throws Exception {
		if(lstDriverId != null && lstDriverId.size() > 0) {
			List<CommUseDriverInfoStringDateDTO> listCommUseDriverInfoDTO = new ArrayList<CommUseDriverInfoStringDateDTO>();
			List<String> driverIds = new ArrayList<String>();
			driverIds.add("1");//默认一个
			extraDriverDTO.setDriverIds(driverIds);
			///
//			extraDriverDTO.setDriverIds(lstDriverId);
			
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
					
					//计算费用
					String amt = "0";
					RenterOrderCostDetailEntity extraDriverInsureAmtEntity = renterOrderCostCombineService.getExtraDriverInsureAmtEntity(extraDriverDTO);
					if(extraDriverInsureAmtEntity != null) {
						amt = String.valueOf(extraDriverInsureAmtEntity.getTotalAmount());
					}
					dto.setAmt(amt);
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
	private void putComUseDriverList(AdditionalDriverInsuranceVO resVo, List<CommUseDriverInfoDTO> commUseDriverList,ExtraDriverDTO extraDriverDTO,List<String> lstDriverId) throws Exception {
		if(lstDriverId != null && lstDriverId.size() > 0) {
			List<CommUseDriverInfoStringDateDTO> listCommUseDriverInfoDTO = new ArrayList<CommUseDriverInfoStringDateDTO>();
			List<String> driverIds = new ArrayList<String>();
			driverIds.add("1");//默认一个
			extraDriverDTO.setDriverIds(driverIds);
			///
//			extraDriverDTO.setDriverIds(lstDriverId);
			
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
					
					//计算费用
					String amt = "0";
					RenterOrderCostDetailEntity extraDriverInsureAmtEntity = renterOrderCostCombineService.getExtraDriverInsureAmtEntity(extraDriverDTO);
					if(extraDriverInsureAmtEntity != null) {
						amt = String.valueOf(extraDriverInsureAmtEntity.getTotalAmount());
					}
					dto.setAmt(amt);
					listCommUseDriverInfoDTO.add(dto);
//				}
			}
			
			resVo.setListCommUseDriverInfoDTO(listCommUseDriverInfoDTO);
		}
		
	}
	
	/**
	 * 新增附加驾驶人
	 * @param renterCostReqVO
	 * @return
	 * @throws Exception 
	 */
	public void insertAdditionalDriverInsuranceByOrderNo(AdditionalDriverInsuranceIdsReqVO renterCostReqVO) throws Exception {
		//根据订单号查询会员号
		//主订单
	      OrderEntity orderEntity = orderService.getOrderEntity(renterCostReqVO.getOrderNo());
	      if(orderEntity == null){
	      	logger.error("获取订单数据为空orderNo={}",renterCostReqVO.getOrderNo());
	          throw new Exception("获取订单数据为空");
	      }
	      
	    //封装对象
	      ExtraDriverDTO extraDriverDTO = new ExtraDriverDTO();
	      CostBaseDTO costBaseDTO = new CostBaseDTO();
	      costBaseDTO.setOrderNo(renterCostReqVO.getOrderNo());
	      costBaseDTO.setRenterOrderNo(renterCostReqVO.getRenterOrderNo());
	      costBaseDTO.setMemNo(orderEntity.getMemNoRenter());
	      costBaseDTO.setStartTime(orderEntity.getExpRentTime());
	      costBaseDTO.setEndTime(orderEntity.getExpRevertTime());
	      extraDriverDTO.setCostBaseDTO(costBaseDTO);
		  
	      List<String> driverIds = new ArrayList<String>();
	      List<CommUseDriverInfoSimpleDTO> listCommUseDriverIds = renterCostReqVO.getListCommUseDriverIds();
	      for (CommUseDriverInfoSimpleDTO commUseDriverInfoSimpleDTO : listCommUseDriverIds) {
	    	  driverIds.add(String.valueOf(commUseDriverInfoSimpleDTO.getId()));
	      }
		  extraDriverDTO.setDriverIds(driverIds);
			
	      
//		计算费用
		
		//获取附加驾驶人保险金额
		//封装数据
		RenterOrderCostDetailEntity extraDriverInsureAmtEntity = renterOrderCostCombineService.getExtraDriverInsureAmtEntity(extraDriverDTO);
		
		//统一设置修改人名称。20200205 huangjing
        String userName = AdminUserUtil.getAdminUser().getAuthName(); // 获取的管理后台的用户名。
        extraDriverInsureAmtEntity.setUpdateOp(userName);
        extraDriverInsureAmtEntity.setCreateOp(userName);
        extraDriverInsureAmtEntity.setOperatorId(userName);
        
		//添加租客费用.
		int i = renterOrderCostDetailService.saveOrUpdateRenterOrderCostDetail(extraDriverInsureAmtEntity);
		if(i>0) {
			logger.info("附加驾驶人保险金额SUCCESS");
		}else {
			logger.info("附加驾驶人保险金额FAILURE");
		}
		
		//添加附加驾驶人记录
        //保存附加驾驶人信息
		List<CommUseDriverInfoDTO> commUseDriverList = new ArrayList<CommUseDriverInfoDTO>();
		for (CommUseDriverInfoSimpleDTO commUseDriverInfoDTO : listCommUseDriverIds) {
			CommUseDriverInfoDTO dto = new CommUseDriverInfoDTO();
			dto.setId(commUseDriverInfoDTO.getId());
			dto.setRealName(commUseDriverInfoDTO.getRealName());
			dto.setMobile(commUseDriverInfoDTO.getMobile());
			//记录操作人
			dto.setConsoleOperatorName(userName);
			
			commUseDriverList.add(dto); //注意封装数据
		}
//        renterAdditionalDriverService.insertBatchAdditionalDriver(renterCostReqVO.getOrderNo(),
//        		renterCostReqVO.getRenterOrderNo(),driverIds,commUseDriverList);
		
        renterAdditionalDriverService.insertBatchAdditionalDriverBeforeDel(renterCostReqVO.getOrderNo(),
        		renterCostReqVO.getRenterOrderNo(),driverIds,commUseDriverList);
        
        logger.info("保存附加驾驶人信息SUCCESS");
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
		// 管理后台补贴
	 	List<OrderConsoleSubsidyDetailEntity> consoleSubsidyList = orderConsoleSubsidyDetailService.listOrderConsoleSubsidyDetailByOrderNoAndMemNo(renterCostReqVO.getOrderNo(), orderEntity.getMemNoRenter());
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
		
		//数据封装
		resVo.setDispatchingSubsidy(String.valueOf(dispatching));
	 	resVo.setOilSubsidy(String.valueOf(oil));
	 	resVo.setCleanCarSubsidy(String.valueOf(cleancar));
	 	resVo.setGetReturnDelaySubsidy(String.valueOf(getReturnDelay));
	 	resVo.setDelaySubsidy(String.valueOf(delay));
	 	resVo.setTrafficSubsidy(String.valueOf(traffic));
	 	resVo.setInsureSubsidy(String.valueOf(insure));
	 	resVo.setRentAmtSubsidy(String.valueOf(rentamt));
	 	resVo.setOtherSubsidy(String.valueOf(other));
	 	resVo.setAbatementSubsidy(String.valueOf(abatement));
	 	resVo.setFeeSubsidy(String.valueOf(fee));
		return resVo;
	}
	
	public RenterPriceAdjustmentResVO findRenterPriceAdjustmentByOrderNo(RenterCostReqVO renterCostReqVO) throws Exception {
		RenterPriceAdjustmentResVO resVo = new RenterPriceAdjustmentResVO();
		
		//根据订单号查询会员号
		//主订单
	      OrderEntity orderEntity = orderService.getOrderEntity(renterCostReqVO.getOrderNo());
	      if(orderEntity == null){
	      	logger.error("获取订单数据为空orderNo={}",renterCostReqVO.getOrderNo());
	          throw new Exception("获取订单数据为空");
	      }
			      
		// 管理后台补贴
		List<OrderConsoleSubsidyDetailEntity> consoleSubsidyList = orderConsoleSubsidyDetailService.listOrderConsoleSubsidyDetailByOrderNoAndMemNo(renterCostReqVO.getOrderNo(), orderEntity.getMemNoRenter());
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
	 * @return
	 * @throws Exception 
	 */
	public void updateRenterPriceAdjustmentByOrderNo(RenterAdjustCostReqVO renterCostReqVO) throws Exception {
		//根据订单号查询会员号
		//主订单  
	      OrderEntity orderEntity = orderService.getOrderEntity(renterCostReqVO.getOrderNo());
	      if(orderEntity == null){
	      	logger.error("获取订单数据为空orderNo={}",renterCostReqVO.getOrderNo());
	          throw new Exception("获取订单数据为空");
	      }
	    
	      OwnerOrderEntity orderEntityOwner = null;  
	    if(StringUtils.isNotBlank(renterCostReqVO.getOwnerOrderNo())) {  
		    orderEntityOwner = ownerOrderService.getOwnerOrderByOwnerOrderNo(renterCostReqVO.getOwnerOrderNo());
	        if(orderEntityOwner == null){
	        	logger.error("获取订单数据(车主)为空orderNo={}",renterCostReqVO.getOrderNo());
	            throw new Exception("获取订单数据(车主)为空");
	        }
	    }else {
	    	//否则根据主订单号查询
	    	orderEntityOwner = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(renterCostReqVO.getOrderNo());
	    	
	    }
	    
	    String userName = AdminUserUtil.getAdminUser().getAuthName();  //获取的管理后台的用户名。
	    
	   /**	
	    * 租客给车主的调价
	    */
	   if(StringUtils.isNotBlank(renterCostReqVO.getRenterToOwnerAdjustAmt())) {
		   SubsidySourceCodeEnum targetEnum = SubsidySourceCodeEnum.OWNER;
		   SubsidySourceCodeEnum sourceEnum = SubsidySourceCodeEnum.RENTER;
		   RenterCashCodeEnum cash = RenterCashCodeEnum.SUBSIDY_RENTERTOOWNER_ADJUST;
		   
		   CostBaseDTO costBaseDTO = new CostBaseDTO();
	    	costBaseDTO.setOrderNo(renterCostReqVO.getOrderNo());
	    	costBaseDTO.setMemNo(orderEntity.getMemNoRenter());
	    	OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, -Integer.valueOf(renterCostReqVO.getRenterToOwnerAdjustAmt()), targetEnum, sourceEnum, SubsidyTypeCodeEnum.ADJUST_AMT, cash);
	    	//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
	    	orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
	    	
	    	if(orderEntityOwner != null) {
		    	//反向记录
	    		costBaseDTO.setMemNo(orderEntityOwner.getMemNo());
		    	OrderConsoleSubsidyDetailEntity recordConvert = orderConsoleSubsidyDetailService.buildData(costBaseDTO, Integer.valueOf(renterCostReqVO.getRenterToOwnerAdjustAmt()), targetEnum, sourceEnum, SubsidyTypeCodeEnum.ADJUST_AMT, cash);
		    	//
		    	recordConvert.setCreateOp(userName);
		    	recordConvert.setUpdateOp(userName);
		    	recordConvert.setOperatorId(userName);
		    	orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(recordConvert);
	    	}
	   }
	   
	   /**
	    * 车主给租客的调价
	    */
	   if(StringUtils.isNotBlank(renterCostReqVO.getOwnerToRenterAdjustAmt())) {
		   SubsidySourceCodeEnum targetEnum = SubsidySourceCodeEnum.RENTER;
		   SubsidySourceCodeEnum sourceEnum = SubsidySourceCodeEnum.OWNER;
		   RenterCashCodeEnum cash = RenterCashCodeEnum.SUBSIDY_OWNERTORENTER_ADJUST;
		   
		   CostBaseDTO costBaseDTO = new CostBaseDTO();
	    	costBaseDTO.setOrderNo(renterCostReqVO.getOrderNo());
	    	if(orderEntityOwner != null) {
		    	costBaseDTO.setMemNo(orderEntityOwner.getMemNo());
		    	OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, -Integer.valueOf(renterCostReqVO.getOwnerToRenterAdjustAmt()), targetEnum, sourceEnum, SubsidyTypeCodeEnum.ADJUST_AMT, cash);
		    	//
				record.setCreateOp(userName);
				record.setUpdateOp(userName);
				record.setOperatorId(userName);
		    	orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
	    	}
	    	
	    	//反向记录
	    	costBaseDTO.setMemNo(orderEntity.getMemNoRenter());
	    	OrderConsoleSubsidyDetailEntity recordConvert = orderConsoleSubsidyDetailService.buildData(costBaseDTO, Integer.valueOf(renterCostReqVO.getOwnerToRenterAdjustAmt()), targetEnum, sourceEnum, SubsidyTypeCodeEnum.ADJUST_AMT, cash);
	    	//
	    	recordConvert.setCreateOp(userName);
	    	recordConvert.setUpdateOp(userName);
	    	recordConvert.setOperatorId(userName);
	    	orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(recordConvert);
	   }
    	
	}
	
	public RenterToPlatformVO findRenterToPlatFormListByOrderNo(RenterCostReqVO renterCostReqVO) throws Exception {
		//主订单
	      OrderEntity orderEntity = orderService.getOrderEntity(renterCostReqVO.getOrderNo());
	      if(orderEntity == null){
	      	logger.error("获取订单数据为空orderNo={}",renterCostReqVO.getOrderNo());
	          throw new Exception("获取订单数据为空");
	      }
	      
		String orderNo = renterCostReqVO.getOrderNo();
		List<OrderConsoleCostDetailEntity> list = orderConsoleCostDetailService.getOrderConsoleCostDetaiByOrderNo(orderNo);
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
        
        //作为常量，不应该跟计算的结果来处理。
//        com.atzuche.order.settle.vo.req.RentCosts ownerCosts = orderSettleService.preRenterSettleOrder(orderNo, orderEntity.getMemNoRenter());
//        OwnerOrderPurchaseDetailEntity renterOrderCostDetail = null;
//        if(ownerCosts != null){
//            renterOrderCostDetail = ownerCosts.getRenterOrderCostDetail();
//        }
        
        
        //封装数据
        RenterToPlatformVO resVo = new RenterToPlatformVO();
        resVo.setOliAmt(String.valueOf(oil));
        resVo.setTimeOut(String.valueOf(timeOut));
        resVo.setModifyOrderTimeAndAddrAmt(String.valueOf(modifyOrderTimeAndAddrAmt));
        resVo.setCarWash(String.valueOf(carWash));
        resVo.setDlayWait(String.valueOf(dlayWait));
        resVo.setStopCar(String.valueOf(stopCar));
        resVo.setExtraMileage(String.valueOf(extraMileage));
		return resVo;
	}
	
	
	
	//添加
	public void updateRenterToPlatFormListByOrderNo(RenterToPlatformCostReqVO renterCostReqVO) throws Exception {
		CostBaseDTO costBaseDTO = new CostBaseDTO();
		//根据订单号查询会员号
		//主订单
        OrderEntity orderEntity = orderService.getOrderEntity(renterCostReqVO.getOrderNo());
        if(orderEntity == null){
        	logger.error("获取订单数据为空orderNo={}",renterCostReqVO.getOrderNo());
            throw new Exception("获取订单数据为空");
        }
        
		//封装订单号和会员号
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
		 
		 String userName = AdminUserUtil.getAdminUser().getAuthName();  //获取的管理后台的用户名。

		if(StringUtils.isNotBlank(oliAmt)) {
			OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO, Integer.valueOf(oliAmt), target, source, ConsoleCashCodeEnum.RENTER_OIL_FEE);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
			
			//反向记录
//			OrderConsoleCostDetailEntity recordConvert = orderConsoleCostDetailService.buildData(costBaseDTO, -Integer.valueOf(oliAmt), SubsidySourceCodeEnum.RENTER, SubsidySourceCodeEnum.PLATFORM, ConsoleCashCodeEnum.RENTER_OIL_FEE);
//			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(recordConvert);
		}
		
		if(StringUtils.isNotBlank(timeOut)) {
			OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO, Integer.valueOf(timeOut), target, source, ConsoleCashCodeEnum.RENTER_TIME_OUT);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
			
			//反向记录
//			OrderConsoleCostDetailEntity recordConvert = orderConsoleCostDetailService.buildData(costBaseDTO, -Integer.valueOf(timeOut), SubsidySourceCodeEnum.RENTER, SubsidySourceCodeEnum.PLATFORM, ConsoleCashCodeEnum.RENTER_TIME_OUT);
//			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(recordConvert);
		}
		
		if(StringUtils.isNotBlank(modifyOrderTimeAndAddrAmt)) {
			OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO, Integer.valueOf(modifyOrderTimeAndAddrAmt), target, source, ConsoleCashCodeEnum.RENTER_MODIFY_ADDR_TIME);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
			
			//反向记录
//			OrderConsoleCostDetailEntity recordConvert = orderConsoleCostDetailService.buildData(costBaseDTO, -Integer.valueOf(modifyOrderTimeAndAddrAmt), SubsidySourceCodeEnum.RENTER, SubsidySourceCodeEnum.PLATFORM, ConsoleCashCodeEnum.RENTER_MODIFY_ADDR_TIME);
//			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(recordConvert);
		}
		
		if(StringUtils.isNotBlank(carWash)) {
			OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO, Integer.valueOf(carWash), target, source, ConsoleCashCodeEnum.RENTER_CAR_WASH);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
			
			//反向记录
//			OrderConsoleCostDetailEntity recordConvert = orderConsoleCostDetailService.buildData(costBaseDTO, -Integer.valueOf(carWash), SubsidySourceCodeEnum.RENTER, SubsidySourceCodeEnum.PLATFORM, ConsoleCashCodeEnum.RENTER_CAR_WASH);
//			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(recordConvert);
		}
		
		if(StringUtils.isNotBlank(dlayWait)) {
			OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO, Integer.valueOf(dlayWait), target, source, ConsoleCashCodeEnum.RENTER_DLAY_WAIT);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
			
			//反向记录
//			OrderConsoleCostDetailEntity recordConvert = orderConsoleCostDetailService.buildData(costBaseDTO, -Integer.valueOf(dlayWait), SubsidySourceCodeEnum.RENTER, SubsidySourceCodeEnum.PLATFORM, ConsoleCashCodeEnum.RENTER_DLAY_WAIT);
//			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(recordConvert);
		}
		
		if(StringUtils.isNotBlank(stopCar)) {
			OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO, Integer.valueOf(stopCar), target, source, ConsoleCashCodeEnum.RENTER_STOP_CAR);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
			
			//反向记录
//			OrderConsoleCostDetailEntity recordConvert = orderConsoleCostDetailService.buildData(costBaseDTO, -Integer.valueOf(stopCar), SubsidySourceCodeEnum.RENTER, SubsidySourceCodeEnum.PLATFORM, ConsoleCashCodeEnum.RENTER_STOP_CAR);
//			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(recordConvert);
		}
		
		if(StringUtils.isNotBlank(extraMileage)) {
			OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO, Integer.valueOf(extraMileage), target, source, ConsoleCashCodeEnum.RENTER_EXTRA_MILEAGE);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
			
			//反向记录
//			OrderConsoleCostDetailEntity recordConvert = orderConsoleCostDetailService.buildData(costBaseDTO, -Integer.valueOf(extraMileage), SubsidySourceCodeEnum.RENTER, SubsidySourceCodeEnum.PLATFORM, ConsoleCashCodeEnum.RENTER_EXTRA_MILEAGE);
//			orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(recordConvert);
		}
		
	}
	
	
	
	//添加，车主需支付给平台的费用。
		public void updateOwnerToPlatFormListByOrderNo(OwnerToPlatformCostReqVO ownerCostReqVO) throws Exception {
			CostBaseDTO costBaseDTO = new CostBaseDTO();
			//根据订单号查询会员号
			//主订单
//	        OrderEntity orderEntity = orderService.getOrderEntity(ownerCostReqVO.getOrderNo());
			OwnerOrderEntity orderEntity = ownerOrderService.getOwnerOrderByOwnerOrderNo(ownerCostReqVO.getOwnerOrderNo());
	        if(orderEntity == null){
	        	logger.error("获取订单数据(车主)为空orderNo={}",ownerCostReqVO.getOrderNo());
	            throw new Exception("获取订单数据(车主)为空");
	        }
	        
			//封装订单号和会员号
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
			 
			 String userName = AdminUserUtil.getAdminUser().getAuthName();  //获取的管理后台的用户名。
			 
			if(StringUtils.isNotBlank(oliAmt)) {
				OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO, Integer.valueOf(oliAmt), target, source, ConsoleCashCodeEnum.OIL_FEE);
				//
				record.setCreateOp(userName);
				record.setUpdateOp(userName);
				record.setOperatorId(userName);
				orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
				
				//反向记录
//				OrderConsoleCostDetailEntity recordConvert = orderConsoleCostDetailService.buildData(costBaseDTO, -Integer.valueOf(oliAmt), SubsidySourceCodeEnum.OWNER, SubsidySourceCodeEnum.PLATFORM, ConsoleCashCodeEnum.OIL_FEE);
//				orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(recordConvert);
			}
			
			if(StringUtils.isNotBlank(timeOut)) {
				OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO, Integer.valueOf(timeOut), target, source, ConsoleCashCodeEnum.TIME_OUT);
				//
				record.setCreateOp(userName);
				record.setUpdateOp(userName);
				record.setOperatorId(userName);
				orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
				
				//反向记录
//				OrderConsoleCostDetailEntity recordConvert = orderConsoleCostDetailService.buildData(costBaseDTO, -Integer.valueOf(timeOut), SubsidySourceCodeEnum.OWNER, SubsidySourceCodeEnum.PLATFORM, ConsoleCashCodeEnum.TIME_OUT);
//				orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(recordConvert);
			}
			
			if(StringUtils.isNotBlank(modifyOrderTimeAndAddrAmt)) {
				OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO, Integer.valueOf(modifyOrderTimeAndAddrAmt), target, source, ConsoleCashCodeEnum.MODIFY_ADDR_TIME);
				//
				record.setCreateOp(userName);
				record.setUpdateOp(userName);
				record.setOperatorId(userName);
				orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
				
				//反向记录
//				OrderConsoleCostDetailEntity recordConvert = orderConsoleCostDetailService.buildData(costBaseDTO, -Integer.valueOf(modifyOrderTimeAndAddrAmt), SubsidySourceCodeEnum.OWNER, SubsidySourceCodeEnum.PLATFORM, ConsoleCashCodeEnum.MODIFY_ADDR_TIME);
//				orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(recordConvert);
			}
			
			if(StringUtils.isNotBlank(carWash)) {
				OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO, Integer.valueOf(carWash), target, source, ConsoleCashCodeEnum.CAR_WASH);
				//
				record.setCreateOp(userName);
				record.setUpdateOp(userName);
				record.setOperatorId(userName);
				orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
				
				//反向记录
//				OrderConsoleCostDetailEntity recordConvert = orderConsoleCostDetailService.buildData(costBaseDTO, -Integer.valueOf(carWash), SubsidySourceCodeEnum.OWNER, SubsidySourceCodeEnum.PLATFORM, ConsoleCashCodeEnum.CAR_WASH);
//				orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(recordConvert);
			}
			
			if(StringUtils.isNotBlank(dlayWait)) {
				OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO, Integer.valueOf(dlayWait), target, source, ConsoleCashCodeEnum.DLAY_WAIT);
				//
				record.setCreateOp(userName);
				record.setUpdateOp(userName);
				record.setOperatorId(userName);
				orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
				
				//反向记录
//				OrderConsoleCostDetailEntity recordConvert = orderConsoleCostDetailService.buildData(costBaseDTO, -Integer.valueOf(dlayWait), SubsidySourceCodeEnum.OWNER, SubsidySourceCodeEnum.PLATFORM, ConsoleCashCodeEnum.DLAY_WAIT);
//				orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(recordConvert);
			}
			
			if(StringUtils.isNotBlank(stopCar)) {
				OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO, Integer.valueOf(stopCar), target, source, ConsoleCashCodeEnum.STOP_CAR);
				//
				record.setCreateOp(userName);
				record.setUpdateOp(userName);
				record.setOperatorId(userName);
				orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
				
				//反向记录
//				OrderConsoleCostDetailEntity recordConvert = orderConsoleCostDetailService.buildData(costBaseDTO, -Integer.valueOf(stopCar), SubsidySourceCodeEnum.OWNER, SubsidySourceCodeEnum.PLATFORM, ConsoleCashCodeEnum.STOP_CAR);
//				orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(recordConvert);
			}
			
			if(StringUtils.isNotBlank(extraMileage)) {
				OrderConsoleCostDetailEntity record = orderConsoleCostDetailService.buildData(costBaseDTO, Integer.valueOf(extraMileage), target, source, ConsoleCashCodeEnum.EXTRA_MILEAGE);
				//
				record.setCreateOp(userName);
				record.setUpdateOp(userName);
				record.setOperatorId(userName);
				orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(record);
				
				//反向记录
//				OrderConsoleCostDetailEntity recordConvert = orderConsoleCostDetailService.buildData(costBaseDTO, -Integer.valueOf(extraMileage), SubsidySourceCodeEnum.OWNER, SubsidySourceCodeEnum.PLATFORM, ConsoleCashCodeEnum.EXTRA_MILEAGE);
//				orderConsoleCostDetailService.saveOrUpdateOrderConsoleCostDetaiByOrderNo(recordConvert);
			}
			
		}
		
	
	


	public RenterRentDetailDTO findRenterRentAmtListByOrderNo(RenterCostReqVO renterCostReqVO) throws Exception {
      //主订单
        OrderEntity orderEntity = orderService.getOrderEntity(renterCostReqVO.getOrderNo());
        if(orderEntity == null){
        	logger.error("获取订单数据为空orderNo={}",renterCostReqVO.getOrderNo());
            throw new Exception("获取订单数据为空");
        }
        
        RenterGoodsDetailDTO renterGoodsDetail = renterGoodsService.getRenterGoodsDetail(renterCostReqVO.getRenterOrderNo(), true); 
        RenterRentDetailDTO renterRentDetailDTO = new RenterRentDetailDTO();
        if(renterGoodsDetail != null && renterGoodsDetail.getRenterGoodsPriceDetailDTOList()!=null && renterGoodsDetail.getRenterGoodsPriceDetailDTOList().size()>0){
            List<RenterGoodsPriceDetailDTO> renterGoodsPriceDetailDTOList = renterGoodsDetail.getRenterGoodsPriceDetailDTOList();
            renterGoodsPriceDetailDTOList
                   .forEach(x->{
                       LocalDate carDay = x.getCarDay();
                       x.setCarDayStr(carDay!=null?LocalDateTimeUtils.localdateToString(carDay):null);
                   });
            
            //从renter_order_cost_detail获取 ，而不是从renter_goods_price_detail获取天价格。20200205 huangjing
//            renterRentDetailDTO.setDayAverageAmt(renterGoodsPriceDetailDTOList.get(0).getCarUnitPrice());
            renterRentDetailDTO.setRenterGoodsPriceDetailDTOS(renterGoodsPriceDetailDTOList);
            renterRentDetailDTO.setCarPlateNum(renterGoodsDetail.getCarPlateNum());
        }
        
        //111 租客租金组成 日均价
        List<RenterOrderCostDetailEntity> lstRenterCostDetail = renterOrderCostDetailService.listRenterOrderCostDetail(renterCostReqVO.getOrderNo(), renterCostReqVO.getRenterOrderNo());
        for (RenterOrderCostDetailEntity renterOrderCostDetailEntity : lstRenterCostDetail) {
			if(RenterCashCodeEnum.RENT_AMT.getCashNo().equals(renterOrderCostDetailEntity.getCostCode())) {
				renterRentDetailDTO.setDayAverageAmt(renterOrderCostDetailEntity.getUnitPrice());
				break;
			}
		}


        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderEntity,orderDTO);
        renterRentDetailDTO.setReqTimeStr(orderDTO.getReqTime()!=null? LocalDateTimeUtils.localdateToString(orderDTO.getReqTime(), GlobalConstant.FORMAT_DATE_STR1):null);
        renterRentDetailDTO.setRevertTimeStr(orderDTO.getExpRevertTime()!=null? LocalDateTimeUtils.localdateToString(orderDTO.getExpRevertTime(), GlobalConstant.FORMAT_DATE_STR1):null);
        renterRentDetailDTO.setRentTimeStr(orderDTO.getExpRentTime()!=null?LocalDateTimeUtils.localdateToString(orderDTO.getExpRentTime(), GlobalConstant.FORMAT_DATE_STR1):null);
        
        return renterRentDetailDTO;
	}
	
	
	public OrderRenterFineAmtDetailResVO findfineAmtListByOrderNo(RenterCostReqVO renterCostReqVO) throws Exception {
		OrderRenterFineAmtDetailResVO resVo = new OrderRenterFineAmtDetailResVO();
		
		 String renterBeforeReturnCarFineAmt="";
		 String renterDelayReturnCarFineAmt="";
		 String renterFineAmt="";
		 String renterGetReturnCarFineAmt="";
		
		//根据订单号查询会员号
		//主订单
        OrderEntity orderEntity = orderService.getOrderEntity(renterCostReqVO.getOrderNo());
        if(orderEntity == null){
        	logger.error("获取订单数据为空orderNo={}",renterCostReqVO.getOrderNo());
            throw new Exception("获取订单数据为空");
        }
		
		 int renterBeforeReturnCarFineAmount=0;
		 int renterDelayReturnCarFineAmount=0;
		 int renterFineAmount=0;
		 int renterGetReturnCarFineAmount=0;
		List<ConsoleRenterOrderFineDeatailEntity> list = consoleRenterOrderFineDeatailService.listConsoleRenterOrderFineDeatail(renterCostReqVO.getOrderNo(), orderEntity.getMemNoRenter());
		//累计求和
		for (ConsoleRenterOrderFineDeatailEntity consoleRenterOrderFineDeatailEntity : list) {
			if(consoleRenterOrderFineDeatailEntity.getFineType().intValue() == FineTypeEnum.MODIFY_GET_FINE.getFineType().intValue()) {
				renterGetReturnCarFineAmount += consoleRenterOrderFineDeatailEntity.getFineAmount().intValue();
			}else if(consoleRenterOrderFineDeatailEntity.getFineType().intValue() == FineTypeEnum.MODIFY_RETURN_FINE.getFineType().intValue()) {
				renterGetReturnCarFineAmount += consoleRenterOrderFineDeatailEntity.getFineAmount().intValue();
			}else if(consoleRenterOrderFineDeatailEntity.getFineType().intValue() == FineTypeEnum.MODIFY_ADVANCE.getFineType().intValue()) {
				renterBeforeReturnCarFineAmount += consoleRenterOrderFineDeatailEntity.getFineAmount().intValue();
			}else if(consoleRenterOrderFineDeatailEntity.getFineType().intValue() == FineTypeEnum.CANCEL_FINE.getFineType().intValue()) {
				renterFineAmount += consoleRenterOrderFineDeatailEntity.getFineAmount().intValue();
			}else if(consoleRenterOrderFineDeatailEntity.getFineType().intValue() == FineTypeEnum.DELAY_FINE.getFineType().intValue()) {
				renterDelayReturnCarFineAmount += consoleRenterOrderFineDeatailEntity.getFineAmount().intValue();
			}
		}
		
		renterBeforeReturnCarFineAmt = String.valueOf(renterBeforeReturnCarFineAmount);
		renterDelayReturnCarFineAmt = String.valueOf(renterDelayReturnCarFineAmount);
		renterFineAmt = String.valueOf(renterFineAmount);
		renterGetReturnCarFineAmt = String.valueOf(renterGetReturnCarFineAmount);
		
		
		resVo.setRenterBeforeReturnCarFineAmt(renterBeforeReturnCarFineAmt);
		resVo.setRenterDelayReturnCarFineAmt(renterDelayReturnCarFineAmt);
		resVo.setRenterFineAmt(renterFineAmt);
		resVo.setRenterGetReturnCarFineAmt(renterGetReturnCarFineAmt);
		return resVo;
	}
	
	

	public void updatefineAmtListByOrderNo(RenterFineCostReqVO renterCostReqVO) throws Exception {
		
		CostBaseDTO costBaseDTO = new CostBaseDTO();
		//根据订单号查询会员号
		//主订单
        OrderEntity orderEntity = orderService.getOrderEntity(renterCostReqVO.getOrderNo());
        if(orderEntity == null){
        	logger.error("获取订单数据为空orderNo={}",renterCostReqVO.getOrderNo());
            throw new Exception("获取订单数据为空");
        }
        
		//封装订单号和会员号
		costBaseDTO.setOrderNo(renterCostReqVO.getOrderNo());
		costBaseDTO.setMemNo(orderEntity.getMemNoRenter());
		
		//租客提前还车罚金
		String renterBeforeReturnCarFineAmt = renterCostReqVO.getRenterBeforeReturnCarFineAmt();
		//租客延迟还车罚金
		String renterDelayReturnCarFineAmt = renterCostReqVO.getRenterDelayReturnCarFineAmt();
		
		//租客收益处理
		if(StringUtils.isNotBlank(renterBeforeReturnCarFineAmt)) {
	        ConsoleRenterOrderFineDeatailEntity consoleRenterOrderFineDeatailEntity =
	                consoleRenterOrderFineDeatailService.fineDataConvert(costBaseDTO, Integer.valueOf(renterBeforeReturnCarFineAmt),
	                        FineSubsidyCodeEnum.PLATFORM, FineSubsidySourceCodeEnum.RENTER, FineTypeEnum.MODIFY_ADVANCE);
	        
	        //统一设置修改人名称。20200205 huangjing
	        String userName = AdminUserUtil.getAdminUser().getAuthName(); // 获取的管理后台的用户名。
	        consoleRenterOrderFineDeatailEntity.setUpdateOp(userName);
	        consoleRenterOrderFineDeatailEntity.setCreateOp(userName);
	        consoleRenterOrderFineDeatailEntity.setOperatorId(userName);
	        consoleRenterOrderFineDeatailService.saveOrUpdateConsoleRenterOrderFineDeatail(consoleRenterOrderFineDeatailEntity);
		}
		
		if(StringUtils.isNotBlank(renterDelayReturnCarFineAmt)) {
	        ConsoleRenterOrderFineDeatailEntity consoleRenterOrderFineDeatailEntity =
	                consoleRenterOrderFineDeatailService.fineDataConvert(costBaseDTO, Integer.valueOf(renterDelayReturnCarFineAmt),
	                        FineSubsidyCodeEnum.PLATFORM, FineSubsidySourceCodeEnum.RENTER, FineTypeEnum.DELAY_FINE);
	        
	        //统一设置修改人名称。20200205 huangjing
	        String userName = AdminUserUtil.getAdminUser().getAuthName(); // 获取的管理后台的用户名。
	        consoleRenterOrderFineDeatailEntity.setUpdateOp(userName);
	        consoleRenterOrderFineDeatailEntity.setCreateOp(userName);
	        consoleRenterOrderFineDeatailEntity.setOperatorId(userName);
	        
	        consoleRenterOrderFineDeatailService.saveOrUpdateConsoleRenterOrderFineDeatail(consoleRenterOrderFineDeatailEntity);
		}
		
	}
	
	
	/**
	 * 平台给租客的补贴
	 * @param renterCostReqVO
	 * @throws Exception 
	 */
	public void updatePlatFormToRenterListByOrderNo(PlatformToRenterSubsidyReqVO renterCostReqVO) throws Exception {
		CostBaseDTO costBaseDTO = new CostBaseDTO();
		//根据订单号查询会员号
		//主订单
	    OrderEntity orderEntity = orderService.getOrderEntity(renterCostReqVO.getOrderNo());
	    if(orderEntity == null){
	    	logger.error("获取订单数据为空orderNo={}",renterCostReqVO.getOrderNo());
	        throw new Exception("获取订单数据为空");
	    }
	    
	    int dispatching = renterCostReqVO.getDispatchingSubsidy()!=null?Integer.valueOf(renterCostReqVO.getDispatchingSubsidy()):0;
	    int oil = renterCostReqVO.getOilSubsidy()!=null?Integer.valueOf(renterCostReqVO.getOilSubsidy()):0;
	    int cleancar = renterCostReqVO.getCleanCarSubsidy()!=null?Integer.valueOf(renterCostReqVO.getCleanCarSubsidy()):0;
	    int getReturnDelay = renterCostReqVO.getGetReturnDelaySubsidy()!=null?Integer.valueOf(renterCostReqVO.getGetReturnDelaySubsidy()):0;
	    int delay = renterCostReqVO.getDelaySubsidy()!=null?Integer.valueOf(renterCostReqVO.getDelaySubsidy()):0;
	    
	    int traffic = renterCostReqVO.getTrafficSubsidy()!=null?Integer.valueOf(renterCostReqVO.getTrafficSubsidy()):0;
	    int insure = renterCostReqVO.getInsureSubsidy()!=null?Integer.valueOf(renterCostReqVO.getInsureSubsidy()):0;
	    
	    int rentamt = renterCostReqVO.getRentAmtSubsidy()!=null?Integer.valueOf(renterCostReqVO.getRentAmtSubsidy()):0;
	    int other = renterCostReqVO.getOtherSubsidy()!=null?Integer.valueOf(renterCostReqVO.getOtherSubsidy()):0;
	    
	    int abatement = renterCostReqVO.getAbatementSubsidy()!=null?Integer.valueOf(renterCostReqVO.getAbatementSubsidy()):0;
	    int fee = renterCostReqVO.getFeeSubsidy()!=null?Integer.valueOf(renterCostReqVO.getFeeSubsidy()):0;
	    
		//封装订单号和会员号
		costBaseDTO.setOrderNo(renterCostReqVO.getOrderNo());
		costBaseDTO.setMemNo(orderEntity.getMemNoRenter());
		
		SubsidySourceCodeEnum sourceEnum = SubsidySourceCodeEnum.PLATFORM; //固定
    	SubsidySourceCodeEnum targetEnum = SubsidySourceCodeEnum.RENTER;
    	
    	 String userName = AdminUserUtil.getAdminUser().getAuthName();  //获取的管理后台的用户名。
    	/**
    	 * 全局补贴
    	 */
		//升级车辆补贴
		if(dispatching != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, dispatching, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_DISPATCHING_AMT);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
			
			//添加反向记录
//			RenterOrderSubsidyDetailEntity renterOrderSubsidyDetailEntity =  renterOrderSubsidyDetailService.buildData(costBaseDTO, -dispatching, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_DISPATCHING_AMT);
//			renterOrderSubsidyDetailService.saveOrUpdateRenterOrderSubsidyDetail(renterOrderSubsidyDetailEntity);
		}
		
		if(oil != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, oil, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_OIL);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
			
			//添加反向记录
//			RenterOrderSubsidyDetailEntity renterOrderSubsidyDetailEntity =  renterOrderSubsidyDetailService.buildData(costBaseDTO, -oil, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_OIL);
//			renterOrderSubsidyDetailService.saveOrUpdateRenterOrderSubsidyDetail(renterOrderSubsidyDetailEntity);
		}
		
		if(cleancar != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, cleancar, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_CLEANCAR);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
			
			//添加反向记录
//			RenterOrderSubsidyDetailEntity renterOrderSubsidyDetailEntity =  renterOrderSubsidyDetailService.buildData(costBaseDTO, -cleancar, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_CLEANCAR);
//			renterOrderSubsidyDetailService.saveOrUpdateRenterOrderSubsidyDetail(renterOrderSubsidyDetailEntity);
		}
		
		if(getReturnDelay != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, getReturnDelay, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_GETRETURNDELAY);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
			
			//添加反向记录
//			RenterOrderSubsidyDetailEntity renterOrderSubsidyDetailEntity =  renterOrderSubsidyDetailService.buildData(costBaseDTO, -getReturnDelay, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_GETRETURNDELAY);
//			renterOrderSubsidyDetailService.saveOrUpdateRenterOrderSubsidyDetail(renterOrderSubsidyDetailEntity);
		}
		
		if(delay != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, delay, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_DELAY);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
			
			//添加反向记录
//			RenterOrderSubsidyDetailEntity renterOrderSubsidyDetailEntity =  renterOrderSubsidyDetailService.buildData(costBaseDTO, -delay, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_DELAY);
//			renterOrderSubsidyDetailService.saveOrUpdateRenterOrderSubsidyDetail(renterOrderSubsidyDetailEntity);
		}
		
		if(traffic != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, traffic, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_TRAFFIC);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
			
			//添加反向记录
//			RenterOrderSubsidyDetailEntity renterOrderSubsidyDetailEntity =  renterOrderSubsidyDetailService.buildData(costBaseDTO, -traffic, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_TRAFFIC);
//			renterOrderSubsidyDetailService.saveOrUpdateRenterOrderSubsidyDetail(renterOrderSubsidyDetailEntity);
		}
		
		if(insure != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, insure, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_INSURE);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
			
			//添加反向记录
//			RenterOrderSubsidyDetailEntity renterOrderSubsidyDetailEntity =  renterOrderSubsidyDetailService.buildData(costBaseDTO, -insure, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_INSURE);
//			renterOrderSubsidyDetailService.saveOrUpdateRenterOrderSubsidyDetail(renterOrderSubsidyDetailEntity);
		}
		
		if(rentamt != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, rentamt, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_RENTAMT);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
			
			//添加反向记录
//			RenterOrderSubsidyDetailEntity renterOrderSubsidyDetailEntity =  renterOrderSubsidyDetailService.buildData(costBaseDTO, -rentamt, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_RENTAMT);
//			renterOrderSubsidyDetailService.saveOrUpdateRenterOrderSubsidyDetail(renterOrderSubsidyDetailEntity);
		}
		
		if(other != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, other, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_OTHER);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
			
			//添加反向记录
//			RenterOrderSubsidyDetailEntity renterOrderSubsidyDetailEntity =  renterOrderSubsidyDetailService.buildData(costBaseDTO, -other, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_OTHER);
//			renterOrderSubsidyDetailService.saveOrUpdateRenterOrderSubsidyDetail(renterOrderSubsidyDetailEntity);
		}
		
		if(abatement != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, abatement, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_ABATEMENT);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
			
			//添加反向记录
//			RenterOrderSubsidyDetailEntity renterOrderSubsidyDetailEntity =  renterOrderSubsidyDetailService.buildData(costBaseDTO, -abatement, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_ABATEMENT);
//			renterOrderSubsidyDetailService.saveOrUpdateRenterOrderSubsidyDetail(renterOrderSubsidyDetailEntity);
		}
		
		if(fee != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildData(costBaseDTO, fee, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_FEE);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
			
			//添加反向记录
//			RenterOrderSubsidyDetailEntity renterOrderSubsidyDetailEntity =  renterOrderSubsidyDetailService.buildData(costBaseDTO, -fee, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_FEE);
//			renterOrderSubsidyDetailService.saveOrUpdateRenterOrderSubsidyDetail(renterOrderSubsidyDetailEntity);
		}
		
	
	}
	
	
	/**
	 * 租金补贴
	 * @param ownerCostReqVO
	 * @throws Exception
	 */
	public void ownerToRenterRentAmtSubsidy(OwnerToRenterSubsidyReqVO ownerCostReqVO) throws Exception {
		CostBaseDTO costBaseDTO = new CostBaseDTO();
		//根据订单号查询会员号
		//主订单
//	    OrderEntity orderEntity = orderService.getOrderEntity(ownerCostReqVO.getOrderNo());
//	    if(orderEntity == null){
//	    	logger.error("获取订单数据(租客)为空orderNo={}",ownerCostReqVO.getOrderNo());
//	        throw new Exception("获取订单数据为空");
//	    }
	    
		/**
		 * 查询有效的租客子订单
		 */
		RenterOrderEntity orderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(ownerCostReqVO.getOrderNo());
		if(orderEntity == null){
	    	logger.error("获取订单数据(租客)为空orderNo={}",ownerCostReqVO.getOrderNo());
	        throw new Exception("获取订单数据为空");
	    }
	    
	    OwnerOrderEntity orderEntityOwner = ownerOrderService.getOwnerOrderByOwnerOrderNo(ownerCostReqVO.getOwnerOrderNo());
        if(orderEntityOwner == null){
        	logger.error("获取订单数据(车主)为空orderNo={}",ownerCostReqVO.getOrderNo());
            throw new Exception("获取订单数据(车主)为空");
        }
        
        int rentAmt = ownerCostReqVO.getOwnerSubsidyRentAmt()!=null?Integer.valueOf(ownerCostReqVO.getOwnerSubsidyRentAmt()):0;
        
        
        SubsidySourceCodeEnum sourceEnum = SubsidySourceCodeEnum.OWNER; //固定
    	SubsidySourceCodeEnum targetEnum = SubsidySourceCodeEnum.RENTER;
		//封装订单号和会员号
		costBaseDTO.setOrderNo(ownerCostReqVO.getOrderNo());
		costBaseDTO.setMemNo(orderEntityOwner.getMemNo());
		///
		costBaseDTO.setOwnerOrderNo(orderEntityOwner.getOwnerOrderNo());
    	OwnerOrderSubsidyDetailEntity ownerOrderSubsidyDetailEntity =  ownerOrderSubsidyDetailService.buildData(costBaseDTO, -rentAmt, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_OWNER_TORENTER_RENTAMT);
		ownerOrderSubsidyDetailService.saveOrUpdateRenterOrderSubsidyDetail(ownerOrderSubsidyDetailEntity);
    	
		
    	//反向记录
		//封装订单号和会员号
		costBaseDTO.setOrderNo(ownerCostReqVO.getOrderNo());
		costBaseDTO.setMemNo(orderEntity.getRenterMemNo());
		///
		costBaseDTO.setRenterOrderNo(orderEntity.getRenterOrderNo());
    	RenterOrderSubsidyDetailEntity renterOrderSubsidyDetailEntity =  renterOrderSubsidyDetailService.buildData(costBaseDTO, rentAmt, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, RenterCashCodeEnum.SUBSIDY_OWNER_TORENTER_RENTAMT);
		renterOrderSubsidyDetailService.saveOrUpdateRenterOrderSubsidyDetail(renterOrderSubsidyDetailEntity);
		
        
	}
	
	/**
	 * 平台给车主的补贴
	 * @param ownerCostReqVO
	 * @throws Exception 
	 */
	public void updatePlatFormToOwnerListByOrderNo(PlatformToOwnerSubsidyReqVO ownerCostReqVO) throws Exception {
		CostBaseDTO costBaseDTO = new CostBaseDTO();
		//根据订单号查询会员号
		//主订单
		OwnerOrderEntity orderEntityOwner = ownerOrderService.getOwnerOrderByOwnerOrderNo(ownerCostReqVO.getOwnerOrderNo());
        if(orderEntityOwner == null){
        	logger.error("获取订单数据(车主)为空orderNo={}",ownerCostReqVO.getOrderNo());
            throw new Exception("获取订单数据(车主)为空");
        }
	    
	    int mileageAmt = ownerCostReqVO.getMileageAmt()!=null?Integer.valueOf(ownerCostReqVO.getMileageAmt()):0;
	    int oilSubsidyAmt = ownerCostReqVO.getOilSubsidyAmt()!=null?Integer.valueOf(ownerCostReqVO.getOilSubsidyAmt()):0;
	    int washCarSubsidyAmt = ownerCostReqVO.getWashCarSubsidyAmt()!=null?Integer.valueOf(ownerCostReqVO.getWashCarSubsidyAmt()):0;
	    int carGoodsLossSubsidyAmt = ownerCostReqVO.getCarGoodsLossSubsidyAmt()!=null?Integer.valueOf(ownerCostReqVO.getCarGoodsLossSubsidyAmt()):0;
	    int delaySubsidyAmt = ownerCostReqVO.getDelaySubsidyAmt()!=null?Integer.valueOf(ownerCostReqVO.getDelaySubsidyAmt()):0;
	    
	    int trafficSubsidyAmt = ownerCostReqVO.getTrafficSubsidyAmt()!=null?Integer.valueOf(ownerCostReqVO.getTrafficSubsidyAmt()):0;
	    int incomeSubsidyAmt = ownerCostReqVO.getIncomeSubsidyAmt()!=null?Integer.valueOf(ownerCostReqVO.getIncomeSubsidyAmt()):0;
	    int otherSubsidyAmt = ownerCostReqVO.getOtherSubsidyAmt()!=null?Integer.valueOf(ownerCostReqVO.getOtherSubsidyAmt()):0;

	    
		//封装订单号和会员号
		costBaseDTO.setOrderNo(ownerCostReqVO.getOrderNo());
		costBaseDTO.setMemNo(orderEntityOwner.getMemNo());
		
		SubsidySourceCodeEnum sourceEnum = SubsidySourceCodeEnum.PLATFORM; //固定
    	SubsidySourceCodeEnum targetEnum = SubsidySourceCodeEnum.OWNER;
    	
    	String userName = AdminUserUtil.getAdminUser().getAuthName();  //获取的管理后台的用户名。
    	/**
    	 * 全局补贴，单条记录
    	 */
		//车主超里程补贴
		if(mileageAmt != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildDataOwner(costBaseDTO, mileageAmt, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, OwnerCashCodeEnum.OWNER_MILEAGE_COST_SUBSIDY);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}
		
		//车主油费补贴
		if(oilSubsidyAmt != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildDataOwner(costBaseDTO, oilSubsidyAmt, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, OwnerCashCodeEnum.OWNER_OIL_SUBSIDY);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}
		
		//车主物品损失补贴
		if(carGoodsLossSubsidyAmt != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildDataOwner(costBaseDTO, carGoodsLossSubsidyAmt, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, OwnerCashCodeEnum.OWNER_GOODS_SUBSIDY);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}
		
		//车主延时补贴
		if(delaySubsidyAmt != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildDataOwner(costBaseDTO, delaySubsidyAmt, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, OwnerCashCodeEnum.OWNER_DELAY_SUBSIDY);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}
		
		//车主交通费补贴
		if(trafficSubsidyAmt != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildDataOwner(costBaseDTO, trafficSubsidyAmt, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, OwnerCashCodeEnum.OWNER_TRAFFIC_SUBSIDY);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}
		
		//车主收益补贴
		if(incomeSubsidyAmt != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildDataOwner(costBaseDTO, incomeSubsidyAmt, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, OwnerCashCodeEnum.OWNER_INCOME_SUBSIDY);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}
		
		//车主洗车补贴
		if(washCarSubsidyAmt != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildDataOwner(costBaseDTO, washCarSubsidyAmt, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, OwnerCashCodeEnum.OWNER_WASH_CAR_SUBSIDY);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}
		
		//其他补贴
		if(otherSubsidyAmt != 0) {
			OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.buildDataOwner(costBaseDTO, otherSubsidyAmt, targetEnum, sourceEnum, SubsidyTypeCodeEnum.CONSOLE_AMT, OwnerCashCodeEnum.OWNER_OTHER_SUBSIDY);
			//
			record.setCreateOp(userName);
			record.setUpdateOp(userName);
			record.setOperatorId(userName);
			orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetail(record);
		}
	}

}
