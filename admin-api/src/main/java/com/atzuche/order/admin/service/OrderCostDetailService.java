/**
 * 
 */
package com.atzuche.order.admin.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.admin.vo.req.cost.AdditionalDriverInsuranceIdsReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterAdjustCostReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterCostReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterFineCostReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterToPlatformCostReqVO;
import com.atzuche.order.admin.vo.resp.cost.AdditionalDriverInsuranceVO;
import com.atzuche.order.admin.vo.resp.income.OwnerToPlatFormVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.OrderRenterFineAmtDetailResVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.PlatformToRenterSubsidyResVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.ReductionDetailResVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.ReductionTaskResVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.RenterPriceAdjustmentResVO;
import com.atzuche.order.admin.vo.resp.order.cost.detail.RenterRentAmtResVO;
import com.atzuche.order.commons.entity.dto.CommUseDriverInfoDTO;
import com.atzuche.order.commons.entity.dto.CommUseDriverInfoSimpleDTO;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.ExtraDriverDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberRightDTO;
import com.atzuche.order.commons.enums.FineSubsidyCodeEnum;
import com.atzuche.order.commons.enums.FineSubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.FineTypeEnum;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.RightTypeEnum;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.SubsidyTypeCodeEnum;
import com.atzuche.order.mem.MemProxyService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.rentercost.entity.ConsoleRenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.service.ConsoleRenterOrderFineDeatailService;
import com.atzuche.order.rentercost.service.OrderConsoleSubsidyDetailService;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.rentercost.service.RenterOrderCostDetailService;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.entity.RenterDepositDetailEntity;
import com.atzuche.order.renterorder.service.RenterAdditionalDriverService;
import com.atzuche.order.renterorder.service.RenterDepositDetailService;

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
        RenterMemberDTO renterMemberDTO = renterMemberService.selectrenterMemberByRenterOrderNo(orderEntity.getMemNoRenter(), true);
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
		ReductionTaskResVO task = new ReductionTaskResVO();
		
		for (RenterMemberRightDTO renterMemberRightDTO : renterMemberRightDTOList) {
			//任务
			if(renterMemberRightDTO.getRightType().intValue() == RightTypeEnum.TASK.getCode().intValue()) {
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
      
      AdditionalDriverInsuranceVO resVo = new AdditionalDriverInsuranceVO();
      putComUseDriverList(resVo,commUseDriverList,extraDriverDTO);
      
		return resVo;
	}
	
	/**
	 * 数据封装
	 * @param resVo
	 * @param commUseDriverList
	 * @throws Exception
	 */
	private void putComUseDriverList(AdditionalDriverInsuranceVO resVo, List<CommUseDriverInfoDTO> commUseDriverList,ExtraDriverDTO extraDriverDTO) throws Exception {
		List<CommUseDriverInfoDTO> listCommUseDriverInfoDTO = new ArrayList<CommUseDriverInfoDTO>();
		List<String> driverIds = new ArrayList<String>();
		driverIds.add("1");//默认一个
		extraDriverDTO.setDriverIds(driverIds);
		
		for (CommUseDriverInfoDTO commUseDriverInfoDTO : listCommUseDriverInfoDTO) {
			CommUseDriverInfoDTO dto = new CommUseDriverInfoDTO();
			BeanUtils.copyProperties(dto, commUseDriverInfoDTO);
			//计算费用
			String amt = "0";
			RenterOrderCostDetailEntity extraDriverInsureAmtEntity = renterOrderCostCombineService.getExtraDriverInsureAmtEntity(extraDriverDTO);
			if(extraDriverInsureAmtEntity != null) {
				amt = String.valueOf(extraDriverInsureAmtEntity.getTotalAmount());
			}
			dto.setAmt(amt);
			listCommUseDriverInfoDTO.add(dto);
		}
		
		resVo.setListCommUseDriverInfoDTO(listCommUseDriverInfoDTO);
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
		//添加租客费用.
		renterOrderCostDetailService.saveRenterOrderCostDetail(extraDriverInsureAmtEntity);
		logger.info("附加驾驶人保险金额SUCCESS");
		
		//添加附加驾驶人记录
        //保存附加驾驶人信息
		List<CommUseDriverInfoDTO> commUseDriverList = new ArrayList<CommUseDriverInfoDTO>();
		for (CommUseDriverInfoSimpleDTO commUseDriverInfoDTO : listCommUseDriverIds) {
			CommUseDriverInfoDTO dto = new CommUseDriverInfoDTO();
			dto.setId(commUseDriverInfoDTO.getId());
			dto.setRealName(commUseDriverInfoDTO.getRealName());
			dto.setMobile(commUseDriverInfoDTO.getMobile());
		}
        renterAdditionalDriverService.insertBatchAdditionalDriver(renterCostReqVO.getOrderNo(),
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
				}else if(RenterCashCodeEnum.subsidy_oilSubsidy.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					oil += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}else if(RenterCashCodeEnum.subsidy_cleanCarSubsidy.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					cleancar += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}else if(RenterCashCodeEnum.subsidy_getReturnDelaySubsidy.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					getReturnDelay += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}else if(RenterCashCodeEnum.subsidy_delaySubsidy.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					delay += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}else if(RenterCashCodeEnum.subsidy_trafficSubsidy.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					traffic += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}else if(RenterCashCodeEnum.subsidy_insureSubsidy.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					insure += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}else if(RenterCashCodeEnum.subsidy_rentAmtSubsidy.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					rentamt += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}else if(RenterCashCodeEnum.subsidy_otherSubsidy.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					other += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}else if(RenterCashCodeEnum.subsidy_abatementSubsidy.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					abatement += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}else if(RenterCashCodeEnum.subsidy_fee.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
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
				if(RenterCashCodeEnum.subsidy_renterToOwnerAdjust.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					renterToOwnerAdjustAmount += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}
			}
			
			//车主给租客的调价
			if("2".equals(orderConsoleSubsidyDetailEntity.getSubsidySourceCode()) && "1".equals(orderConsoleSubsidyDetailEntity.getSubsidyTargetCode())){
				if(RenterCashCodeEnum.subsidy_ownerToRenterAdjust.getCashNo().equals(orderConsoleSubsidyDetailEntity.getSubsidyCostCode())) {
					ownerToRenterAdjustAmount += orderConsoleSubsidyDetailEntity.getSubsidyAmount().intValue();
				}
			}
			
		}
	    
	    //封装数据
	    resVo.setOwnerToRenterAdjustAmt(String.valueOf(renterToOwnerAdjustAmount));
	    resVo.setRenterToOwnerAdjustAmt(String.valueOf(ownerToRenterAdjustAmount));
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
	      

	   if(StringUtils.isNotBlank(renterCostReqVO.getRenterToOwnerAdjustAmt())) {
		   SubsidySourceCodeEnum targetEnum = SubsidySourceCodeEnum.OWNER;
		   SubsidySourceCodeEnum sourceEnum = SubsidySourceCodeEnum.RENTER;
		   RenterCashCodeEnum cash = RenterCashCodeEnum.subsidy_renterToOwnerAdjust;
		   
		   CostBaseDTO costBaseDTO = new CostBaseDTO();
	    	costBaseDTO.setOrderNo(renterCostReqVO.getOrderNo());
	    	costBaseDTO.setMemNo(orderEntity.getMemNoRenter());
	    	OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.subsidyDataConvert(costBaseDTO, Integer.valueOf(renterCostReqVO.getRenterToOwnerAdjustAmt()), targetEnum, sourceEnum, SubsidyTypeCodeEnum.ADJUST_AMT, cash);
	    	orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetailAdjust(record);
	    	
	   }else if(StringUtils.isNotBlank(renterCostReqVO.getOwnerToRenterAdjustAmt())) {
		   SubsidySourceCodeEnum targetEnum = SubsidySourceCodeEnum.RENTER;
		   SubsidySourceCodeEnum sourceEnum = SubsidySourceCodeEnum.OWNER;
		   RenterCashCodeEnum cash = RenterCashCodeEnum.subsidy_ownerToRenterAdjust;
		   
		   CostBaseDTO costBaseDTO = new CostBaseDTO();
	    	costBaseDTO.setOrderNo(renterCostReqVO.getOrderNo());
	    	costBaseDTO.setMemNo(orderEntity.getMemNoRenter());
	    	OrderConsoleSubsidyDetailEntity record = orderConsoleSubsidyDetailService.subsidyDataConvert(costBaseDTO, Integer.valueOf(renterCostReqVO.getOwnerToRenterAdjustAmt()), targetEnum, sourceEnum, SubsidyTypeCodeEnum.ADJUST_AMT, cash);
	    	orderConsoleSubsidyDetailService.saveOrUpdateOrderConsoleSubsidyDetailAdjust(record);
	    	
	   }
	      
			      
    	
	}
	


	public RenterRentAmtResVO findRenterRentAmtListByOrderNo(RenterCostReqVO renterCostReqVO) {
		// TODO Auto-generated method stub
		return null;
	}








	public OwnerToPlatFormVO findRenterToPlatFormListByOrderNo(RenterCostReqVO renterCostReqVO) {
		// TODO Auto-generated method stub
		return null;
	}

	public OwnerToPlatFormVO findRenterToPlatFormListByOrderNo(RenterToPlatformCostReqVO renterCostReqVO) {
		// TODO Auto-generated method stub
		return null;
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
	        consoleRenterOrderFineDeatailService.saveOrUpdateConsoleRenterOrderFineDeatail(consoleRenterOrderFineDeatailEntity);
		}
		
		if(StringUtils.isNotBlank(renterDelayReturnCarFineAmt)) {
	        ConsoleRenterOrderFineDeatailEntity consoleRenterOrderFineDeatailEntity =
	                consoleRenterOrderFineDeatailService.fineDataConvert(costBaseDTO, Integer.valueOf(renterDelayReturnCarFineAmt),
	                        FineSubsidyCodeEnum.PLATFORM, FineSubsidySourceCodeEnum.RENTER, FineTypeEnum.DELAY_FINE);
	        consoleRenterOrderFineDeatailService.saveOrUpdateConsoleRenterOrderFineDeatail(consoleRenterOrderFineDeatailEntity);
		}
		
	}
	
	
}
