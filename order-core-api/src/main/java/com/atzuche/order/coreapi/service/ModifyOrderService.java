package com.atzuche.order.coreapi.service;

import com.atzuche.order.accountrenterrentcost.service.AccountRenterCostSettleService;
import com.atzuche.order.car.CarProxyService;
import com.atzuche.order.coin.service.AccountRenterCostCoinService;
import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.commons.enums.*;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.coreapi.entity.dto.ModifyOrderDTO;
import com.atzuche.order.coreapi.entity.request.ModifyOrderReq;
import com.atzuche.order.coreapi.entity.vo.CostDeductVO;
import com.atzuche.order.coreapi.entity.vo.req.CarRentTimeRangeReqVO;
import com.atzuche.order.coreapi.entity.vo.req.OwnerCouponBindReqVO;
import com.atzuche.order.coreapi.modifyorder.exception.ModifyOrderParameterException;
import com.atzuche.order.coreapi.service.remote.CarRentalTimeApiProxyService;
import com.atzuche.order.coreapi.service.remote.UniqueOrderNoProxyService;
import com.atzuche.order.coreapi.utils.ModifyOrderUtils;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryMode;
import com.atzuche.order.delivery.service.RenterOrderDeliveryModeService;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.delivery.vo.delivery.OrderDeliveryDTO;
import com.atzuche.order.delivery.vo.delivery.RenterDeliveryAddrDTO;
import com.atzuche.order.delivery.vo.delivery.UpdateOrderDeliveryVO;
import com.atzuche.order.mem.MemProxyService;
import com.atzuche.order.open.vo.ModifyOrderFeeVO;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderSourceStatEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderSourceStatService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercommodity.service.RenterCommodityService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.entity.dto.OrderCouponDTO;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.rentercost.service.*;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.entity.dto.OrderChangeItemDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostReqDTO;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
import com.atzuche.order.renterorder.service.*;
import com.atzuche.order.renterorder.vo.RenterOrderReqVO;
import com.atzuche.order.renterorder.vo.owner.OwnerCouponGetAndValidReqVO;
import com.atzuche.order.renterorder.vo.platform.MemAvailCouponRequestVO;
import com.autoyol.coupon.api.CouponSettleRequest;
import com.autoyol.platformcost.CommonUtils;
import com.dianping.cat.Cat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ModifyOrderService {
	@Autowired
	private UniqueOrderNoProxyService uniqueOrderNoService;
	@Autowired
	private RenterOrderService renterOrderService;
	@Autowired
	private RenterMemberService renterMemberService;
	@Autowired
	private CarProxyService goodsService;
	@Autowired
	private RenterCommodityService commodityService;
	@Autowired
	private OrderCouponService orderCouponService;
	@Autowired
	private RenterOrderDeliveryService renterOrderDeliveryService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private RenterOrderCalCostService renterOrderCalCostService;
	@Autowired
	private RenterOrderFineDeatailService renterOrderFineDeatailService;
	@Autowired
	private RenterOrderCostDetailService renterOrderCostDetailService;
	@Autowired
	private RenterOrderCostCombineService renterOrderCostCombineService;
	@Autowired
	private RenterOrderSubsidyDetailService renterOrderSubsidyDetailService;
	@Autowired
	private RenterAdditionalDriverService renterAdditionalDriverService;
	@Autowired
	private AutoCoinCostCalService autoCoinCostCalService;
	@Autowired
	private RenterGoodsService renterGoodsService;
	@Autowired
	private MemProxyService memProxyService;
	@Autowired
	private ModifyOrderForRenterService modifyOrderForRenterService;
	@Autowired
	private ModifyOrderConfirmService modifyOrderConfirmService;
	@Autowired
	private OrderChangeItemService orderChangeItemService;
	@Autowired
	private DeliveryCarService deliveryCarService;
	@Autowired
	private CouponAndCoinHandleService couponAndCoinHandleService;
	@Autowired
    private CarRentalTimeApiProxyService carRentalTimeApiService;
	@Autowired
	private AccountRenterCostCoinService accountRenterCostCoinService;
	@Autowired
	private ModifyOrderCheckService modifyOrderCheckService;
	@Autowired
	private OrderStatusService orderStatusService;
	@Autowired
	private OrderConsoleSubsidyDetailService orderConsoleSubsidyDetailService;
	@Autowired
	private ModifyOrderRiskService modifyOrderRiskService;
	@Autowired
	private ModifyOrderRabbitMQService modifyOrderRabbitMQService;
	@Autowired
	private InsurAbamentDiscountService insurAbamentDiscountService;
	@Autowired
	private ModifyOrderFeeService modifyOrderFeeService;
	@Autowired
	private AccountRenterCostSettleService accountRenterCostSettleService;
	@Autowired
	private LongRentSubsidyService longRentSubsidyService;
	@Autowired
	private LongRentCostHandleService longRentCostHandleService;
	@Autowired
	private OrderSourceStatService orderSourceStatService;
	@Autowired
    private RenterOrderDeliveryModeService renterOrderDeliveryModeService;
	@Autowired
	private SubmitOrderService submitOrderService;
	@Autowired
	private RenterInsureCoefficientService renterInsureCoefficientService;
	
	/**
	 * 修改订单主逻辑（含换车）
	 * @param modifyOrderReq
	 * @return ResponseData
	 */
	@Transactional(rollbackFor=Exception.class)
	public void modifyOrder(ModifyOrderReq modifyOrderReq) {
		log.info("modifyOrder修改订单主逻辑入参modifyOrderReq=[{}]", modifyOrderReq);
		// 主单号
		String orderNo = modifyOrderReq.getOrderNo();
		// 获取租客新订单号
		String renterOrderNo = uniqueOrderNoService.genRenterOrderNo(orderNo);
		// 获取修改前有效租客子订单信息
		RenterOrderEntity initRenterOrder = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
		// 获取租客配送订单信息
		List<RenterOrderDeliveryEntity> deliveryList = renterOrderDeliveryService.listRenterOrderDeliveryByRenterOrderNo(initRenterOrder.getRenterOrderNo());
		// DTO包装
		ModifyOrderDTO modifyOrderDTO = getModifyOrderDTO(modifyOrderReq, renterOrderNo, initRenterOrder, deliveryList);
		log.info("ModifyOrderService.modifyOrder modifyOrderDTO=[{}]", modifyOrderDTO);
		// 获取租客会员信息
		RenterMemberDTO renterMemberDTO = getRenterMemberDTO(initRenterOrder.getRenterOrderNo(), renterOrderNo);
		// 设置租客会员信息
		modifyOrderDTO.setRenterMemberDTO(renterMemberDTO);
		// 获取租客商品信息
		RenterGoodsDetailDTO renterGoodsDetailDTO = getRenterGoodsDetailDTO(modifyOrderDTO, initRenterOrder);
		// 设置商品信息
		modifyOrderDTO.setRenterGoodsDetailDTO(renterGoodsDetailDTO);
		// 获取组装后的新租客子单信息
		RenterOrderEntity renterOrderNew = convertToRenterOrderEntity(modifyOrderDTO, initRenterOrder);
		// 获取主订单信息
		OrderEntity orderEntity = orderService.getOrderByOrderNoAndMemNo(orderNo, modifyOrderReq.getMemNo());
		// 设置主订单信息
		modifyOrderDTO.setOrderEntity(orderEntity);
		// 查询主订单状态
		OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
		// 设置订单状态
		modifyOrderDTO.setOrderStatusEntity(orderStatusEntity);
		// 校验
		modifyOrderCheckService.modifyMainCheck(modifyOrderDTO);
		// 设置城市编号
		modifyOrderDTO.setCityCode(orderEntity.getCityCode());
		// 库存校验
		modifyOrderCheckService.checkCarStock(modifyOrderDTO);
		log.info("ModifyOrderService.modifyOrder again modifyOrderDTO=[{}]", modifyOrderDTO);
		// 获取修改前租客费用明细
		List<RenterOrderCostDetailEntity> initCostList = renterOrderCostDetailService.listRenterOrderCostDetail(modifyOrderDTO.getOrderNo(), initRenterOrder.getRenterOrderNo());
		// 获取修改前补贴信息
		List<RenterOrderSubsidyDetailEntity> initSubsidyList = renterOrderSubsidyDetailService.listRenterOrderSubsidyDetail(modifyOrderDTO.getOrderNo(), initRenterOrder.getRenterOrderNo());
		// 提前延后时间计算
		CarRentTimeRangeDTO carRentTimeRangeResVO = getCarRentTimeRangeResVO(modifyOrderDTO);
		// 设置提前延后时间
		modifyOrderDTO.setCarRentTimeRangeResVO(carRentTimeRangeResVO);
		// 封装计算用对象
		RenterOrderReqVO renterOrderReqVO = convertToRenterOrderReqVO(modifyOrderDTO, renterMemberDTO, renterGoodsDetailDTO, orderEntity, carRentTimeRangeResVO);
		// 构建参数
		RenterOrderCostReqDTO renterOrderCostReqDTO = renterOrderService.buildRenterOrderCostReqDTO(renterOrderReqVO);
		// 基础费用计算包含租金，手续费，基础保障费用，补充保障服务费，附加驾驶人保障费用，取还车费用计算和超运能费用计算
		RenterOrderCostRespDTO renterOrderCostRespDTO = getRenterOrderCostRespDTO(modifyOrderDTO, renterOrderReqVO, initCostList, initSubsidyList);
		// 调用风控审核
		modifyOrderRiskService.checkModifyRisk(modifyOrderDTO, renterOrderCostRespDTO);
		// 获取修改订单违约金
		List<RenterOrderFineDeatailEntity> renterFineList = getRenterFineList(modifyOrderDTO, initRenterOrder, deliveryList, renterOrderCostRespDTO);
		// 封装基础信息对象
		CostBaseDTO costBaseDTO = new CostBaseDTO(modifyOrderDTO.getOrderNo(), modifyOrderDTO.getRenterOrderNo(), modifyOrderDTO.getMemNo(), modifyOrderDTO.getRentTime(), modifyOrderDTO.getRevertTime());
		// 获取抵扣补贴(含车主券，限时立减，取还车券，平台券和凹凸币)
		CostDeductVO costDeductVO = getCostDeductVO(modifyOrderDTO, costBaseDTO, renterOrderCostRespDTO, renterOrderReqVO, orderEntity, initSubsidyList);
		// 聚合租客补贴
		renterOrderCostRespDTO.setRenterOrderSubsidyDetailDTOList(getPolymerizationSubsidy(renterOrderCostRespDTO, costDeductVO));
		// 计算补付金额
		Integer supplementAmt = getRenterSupplementAmt(modifyOrderDTO, initRenterOrder, renterOrderCostRespDTO, renterFineList);
		// 计算升级补贴（换车逻辑）
		OrderConsoleSubsidyDetailEntity consoleSubsidy = getDispatchingAmtSubsidy(modifyOrderDTO, costBaseDTO, supplementAmt);
		// 修改后费用
		ModifyOrderFeeVO updateModifyOrderFeeVO = modifyOrderFeeService.getUpdateModifyOrderFeeVO(renterOrderCostRespDTO, renterFineList);
		// 计算需补付多少钱
		int needSupplement = getNeedSupplementAmt(modifyOrderDTO, updateModifyOrderFeeVO);
		renterOrderNew.setSupplementAmt(needSupplement);
		/////////////////////////////////////////// 入库 ////////////////////////////////////////
		// 保存租客商品信息
		renterGoodsService.save(renterGoodsDetailDTO);
		// 保存租客会员信息
		renterMemberService.save(renterMemberDTO);
		// 保存租客子单信息
		renterOrderService.saveRenterOrder(renterOrderNew);
		// 保存基本费用和补贴
		renterOrderCalCostService.saveOrderCostAndDeailList(renterOrderCostRespDTO);
		// 保存罚金
		renterOrderFineDeatailService.saveRenterOrderFineDeatailBatch(renterFineList);
		// 保存升级补贴
		orderConsoleSubsidyDetailService.saveDispatchingSubsidy(orderNo, consoleSubsidy);
		// 保存附加驾驶人信息
		saveAdditionalDriver(modifyOrderDTO, initRenterOrder);
		// 保存优惠券信息
		orderCouponService.insertBatch(costDeductVO.getOrderCouponList());
		// 保存修改项目
		orderChangeItemService.saveOrderChangeItemBatch(modifyOrderDTO.getChangeItemList());
		// 保存保费系数
        renterInsureCoefficientService.saveCombineCoefficient(renterOrderCostReqDTO);
		// 保存配送订单信息
		saveRenterDelivery(modifyOrderDTO);
		// 保存区间配送信息
		saveSectionDelivery(modifyOrderDTO);
		// 修改后处理方法
		modifyPostProcess(modifyOrderDTO, renterOrderNew, initRenterOrder, supplementAmt, renterOrderCostRespDTO.getRenterOrderSubsidyDetailDTOList(), needSupplement);
		// 使用车主券
		bindOwnerCoupon(modifyOrderDTO, renterOrderCostRespDTO);
		// 使用取还车券
		bindGetCarFeeCoupon(modifyOrderDTO);
		// 使用平台券
		bindPlatformCoupon(modifyOrderDTO);
		// 补扣凹凸币 
		againAutoCoinDeduct(modifyOrderDTO, costDeductVO.getRenterSubsidyList());
		// 发送mq
		sendModifyMQ(modifyOrderDTO);
	}
	
	/**
	 * 发mq
	 * @param modifyOrderDTO
	 */
	public void sendModifyMQ(ModifyOrderDTO modifyOrderDTO) {
		if (modifyOrderDTO.getScanCodeFlag() != null && modifyOrderDTO.getScanCodeFlag()) {
			// 扫码还车不管
			return;
		}
		if (modifyOrderDTO.getTransferFlag() != null && modifyOrderDTO.getTransferFlag()) {
			// 换车mq
			modifyOrderRabbitMQService.sendOrderChangeCarMq(modifyOrderDTO);
		} else if (modifyOrderDTO.getConsoleFlag() != null && modifyOrderDTO.getConsoleFlag()) {
			// 管理后台修改订单mq
			modifyOrderRabbitMQService.sendOrderPlatModifyMq(modifyOrderDTO);
		} else {
			// 租客发起修改申请mq
			modifyOrderRabbitMQService.sendOrderRenterApplyModifyMq(modifyOrderDTO);
		}
		
	}
	
	
	/**
	 * 换车操作计算升级车辆补贴
	 * @param modifyOrderDTO
	 * @param supplementAmt
	 * @return RenterOrderSubsidyDetailDTO
	 */
	public OrderConsoleSubsidyDetailEntity getDispatchingAmtSubsidy(ModifyOrderDTO modifyOrderDTO, CostBaseDTO costBaseDTO, Integer supplementAmt) {
		if (modifyOrderDTO.getTransferFlag() != null && modifyOrderDTO.getTransferFlag()) {
			if (supplementAmt == null || supplementAmt < 0) {
				supplementAmt = 0;
			}
			// 如果是换车操作计算升级车辆补贴
        	RenterOrderSubsidyDetailDTO subsidyDetail = insurAbamentDiscountService.convertToRenterOrderSubsidyDetailDTO(costBaseDTO, supplementAmt, SubsidyTypeCodeEnum.RENT_COST_AMT, 
        			SubsidySourceCodeEnum.PLATFORM, SubsidySourceCodeEnum.RENTER, RenterCashCodeEnum.DISPATCHING_AMT, "换车补贴");
        	OrderConsoleSubsidyDetailEntity consoleSubsidy = new OrderConsoleSubsidyDetailEntity();
        	BeanUtils.copyProperties(subsidyDetail, consoleSubsidy);
        	return consoleSubsidy;
		}
		return null;
	}
	
	
	/**
	 * 组合升级补贴
	 * @param renterOrderCostRespDTO
	 * @param dispatchingAmtSubsidy
	 * @return RenterOrderCostRespDTO
	 */
	public RenterOrderCostRespDTO combinationDispatchingAmtSubsidy(RenterOrderCostRespDTO renterOrderCostRespDTO, RenterOrderSubsidyDetailDTO dispatchingAmtSubsidy) {
		// 补贴
		List<RenterOrderSubsidyDetailDTO> renterOrderSubsidyDetailDTOList = renterOrderCostRespDTO.getRenterOrderSubsidyDetailDTOList();
		List<RenterOrderSubsidyDetailDTO> rosdList = new ArrayList<RenterOrderSubsidyDetailDTO>();
		if (renterOrderSubsidyDetailDTOList != null && !renterOrderSubsidyDetailDTOList.isEmpty()) {
			rosdList.addAll(renterOrderSubsidyDetailDTOList);
		}
		if (dispatchingAmtSubsidy != null) {
			rosdList.add(dispatchingAmtSubsidy);
		}
		renterOrderCostRespDTO.setRenterOrderSubsidyDetailDTOList(rosdList);
		return renterOrderCostRespDTO;
	}
	
	
	
	/**
	 * 绑定车主券
	 * @param modifyOrderDTO
	 * @param renterOrderCostRespDTO
	 */
	public void bindOwnerCoupon(ModifyOrderDTO modifyOrderDTO, RenterOrderCostRespDTO renterOrderCostRespDTO) {
		List<OrderChangeItemDTO> changeItemList = modifyOrderDTO.getChangeItemList();
		if (changeItemList == null || changeItemList.isEmpty()) {
			return;
		}
		List<String> changeCodeList = modifyOrderConfirmService.listChangeCode(changeItemList);
		if (changeCodeList == null || changeCodeList.isEmpty() || 
				!changeCodeList.contains(OrderChangeItemEnum.MODIFY_CAROWNERCOUPON.getCode())) {
			return;
		}
		RenterGoodsDetailDTO renterGoodsDetailDTO = modifyOrderDTO.getRenterGoodsDetailDTO();
		RenterMemberDTO renterMemberDTO = modifyOrderDTO.getRenterMemberDTO();
		OwnerCouponBindReqVO ownerCouponBindReqVO = new OwnerCouponBindReqVO();
		ownerCouponBindReqVO.setOrderNo(modifyOrderDTO.getOrderNo());
		ownerCouponBindReqVO.setCarNo(renterGoodsDetailDTO.getCarNo());
		ownerCouponBindReqVO.setCouponNo(modifyOrderDTO.getCarOwnerCouponId());
		ownerCouponBindReqVO.setRentAmt(Math.abs(renterOrderCostRespDTO.getRentAmount()));
		ownerCouponBindReqVO.setRenterFirstName(renterMemberDTO.getFirstName());
		ownerCouponBindReqVO.setRenterName(renterMemberDTO.getRealName());
		if (renterMemberDTO.getGender() != null) {
			ownerCouponBindReqVO.setRenterSex(String.valueOf(renterMemberDTO.getGender()));
		}
		couponAndCoinHandleService.bindOwnerCoupon(ownerCouponBindReqVO);
	}
	
	
	/**
	 * 绑定平台券
	 * @param modifyOrderDTO
	 */
	public void bindPlatformCoupon(ModifyOrderDTO modifyOrderDTO) {
		List<OrderChangeItemDTO> changeItemList = modifyOrderDTO.getChangeItemList();
		if (changeItemList == null || changeItemList.isEmpty()) {
			return;
		}
		List<String> changeCodeList = modifyOrderConfirmService.listChangeCode(changeItemList);
		if (changeCodeList == null || changeCodeList.isEmpty() || 
				!changeCodeList.contains(OrderChangeItemEnum.MODIFY_PLATFORMCOUPON.getCode())) {
			return;
		}
		couponAndCoinHandleService.bindCoupon(modifyOrderDTO.getOrderNo(),modifyOrderDTO.getPlatformCouponId(), 1, true);
	}
	
	/**
	 * 绑定取还车优惠券
	 * @param modifyOrderDTO
	 */
	public void bindGetCarFeeCoupon(ModifyOrderDTO modifyOrderDTO) {
		List<OrderChangeItemDTO> changeItemList = modifyOrderDTO.getChangeItemList();
		if (changeItemList == null || changeItemList.isEmpty()) {
			return;
		}
		List<String> changeCodeList = modifyOrderConfirmService.listChangeCode(changeItemList);
		if (changeCodeList == null || changeCodeList.isEmpty() || 
				!changeCodeList.contains(OrderChangeItemEnum.MODIFY_GETRETURNCOUPON.getCode())) {
			return;
		}
		couponAndCoinHandleService.bindCoupon(modifyOrderDTO.getOrderNo(),modifyOrderDTO.getSrvGetReturnCouponId(), 2, true);
	}
	
	
	/**
	 * 计算提前延后时间
	 * @param modifyOrderDTO
	 * @return CarRentTimeRangeResVO
	 */
	public CarRentTimeRangeDTO getCarRentTimeRangeResVO(ModifyOrderDTO modifyOrderDTO) {
		if (modifyOrderDTO == null) {
			return null;
		}
		if ((modifyOrderDTO.getSrvGetFlag() == null || modifyOrderDTO.getSrvGetFlag() == 0) &&
				(modifyOrderDTO.getSrvReturnFlag() == null || modifyOrderDTO.getSrvReturnFlag() == 0)) {
			return null;
		}
		CarRentTimeRangeReqVO carRentTimeRangeReqVO = getCarRentTimeRangeReqVO(modifyOrderDTO);
		if (carRentTimeRangeReqVO == null) {
			return null;
		}
		return carRentalTimeApiService.getCarRentTimeRange(carRentTimeRangeReqVO);
	}
	
	/**
	 * 封装获取提前延后的对象
	 * @param modifyOrderDTO
	 * @return CarRentTimeRangeReqVO
	 */
	public CarRentTimeRangeReqVO getCarRentTimeRangeReqVO(ModifyOrderDTO modifyOrderDTO) {
		if (modifyOrderDTO == null) {
			return null;
		}
		RenterGoodsDetailDTO renterGoodsDetailDTO = modifyOrderDTO.getRenterGoodsDetailDTO();
		if (renterGoodsDetailDTO == null || renterGoodsDetailDTO.getCarNo() == null) {
			return null;
		}
		CarRentTimeRangeReqVO carRentTimeRangeReqVO = new CarRentTimeRangeReqVO();
		carRentTimeRangeReqVO.setCarNo(String.valueOf(renterGoodsDetailDTO.getCarNo()));
		carRentTimeRangeReqVO.setCityCode(modifyOrderDTO.getCityCode()+"");
		carRentTimeRangeReqVO.setRentTime(modifyOrderDTO.getRentTime());
		carRentTimeRangeReqVO.setRevertTime(modifyOrderDTO.getRevertTime());
		carRentTimeRangeReqVO.setSrvGetAddr(modifyOrderDTO.getGetCarAddress());
		carRentTimeRangeReqVO.setSrvGetFlag(modifyOrderDTO.getSrvGetFlag());
		carRentTimeRangeReqVO.setSrvGetLat(modifyOrderDTO.getGetCarLat());
		carRentTimeRangeReqVO.setSrvGetLon(modifyOrderDTO.getGetCarLon());
		carRentTimeRangeReqVO.setSrvReturnAddr(modifyOrderDTO.getRevertCarAddress());
		carRentTimeRangeReqVO.setSrvReturnFlag(modifyOrderDTO.getSrvReturnFlag());
		carRentTimeRangeReqVO.setSrvReturnLat(modifyOrderDTO.getRevertCarLat());
		carRentTimeRangeReqVO.setSrvReturnLon(modifyOrderDTO.getRevertCarLon());
		return carRentTimeRangeReqVO;
	}
	
	
	/**
	 * 补扣凹凸币
	 * @param modifyOrderDTO
	 * @param updSubsidyList
	 */
	public void againAutoCoinDeduct(ModifyOrderDTO modifyOrderDTO, List<RenterOrderSubsidyDetailDTO> updSubsidyList) {
		if (modifyOrderDTO == null) {
			return;
		}
		if (updSubsidyList == null || updSubsidyList.isEmpty()) {
			return;
		}
		// 修改后凹凸币抵扣金额
		Integer updAutoCoinSubsidyAmt = 0;
		for (RenterOrderSubsidyDetailDTO subsidy:updSubsidyList) {
			if (RenterCashCodeEnum.AUTO_COIN_DEDUCT.getCashNo().equals(subsidy.getSubsidyCostCode())) {
				updAutoCoinSubsidyAmt = subsidy.getSubsidyAmount();
				break;
			}
		}
		if (updAutoCoinSubsidyAmt == null || updAutoCoinSubsidyAmt == 0) {
			return;
		}
		// 抵扣凹凸币
		accountRenterCostCoinService.deductAutoCoin(modifyOrderDTO.getMemNo(), modifyOrderDTO.getOrderNo(), modifyOrderDTO.getRenterOrderNo(), updAutoCoinSubsidyAmt);
	}
	
	
	/**
	 * 
	 * @param modifyOrderDTO
	 * @param updateModifyOrderFeeVO
	 * @return
	 */
	public int getNeedSupplementAmt(ModifyOrderDTO modifyOrderDTO, ModifyOrderFeeVO updateModifyOrderFeeVO) {
		if (modifyOrderDTO.getTransferFlag() != null && modifyOrderDTO.getTransferFlag()) {
			// 换车不计算补付
			return 0;
		}
		// 已付租车费用(shifu  租车费用的实付)
		int rentAmtPayed = accountRenterCostSettleService.getCostPaidRent(modifyOrderDTO.getOrderNo(),modifyOrderDTO.getMemNo());
		// 应付
		int payable = modifyOrderFeeService.getTotalRentCarFee(updateModifyOrderFeeVO);
		if (rentAmtPayed >= Math.abs(payable)) {
			// 不需要补付
			return 0;
		}
		return Math.abs(payable) - rentAmtPayed;
	}
	
	
	/**
	 * 修改后处理方法
	 * @param modifyOrderDTO
	 * @param initRenterOrder
	 * @param supplementAmt
	 */
	public void modifyPostProcess(ModifyOrderDTO modifyOrderDTO, RenterOrderEntity renterOrderNew, RenterOrderEntity initRenterOrder, Integer supplementAmt, List<RenterOrderSubsidyDetailDTO> renterOrderSubsidyDetailDTOList,int needSupplementAmt) {
		// 管理后台操作标记
		Boolean consoleFlag = modifyOrderDTO.getConsoleFlag();
		// 订单状态
		OrderStatusEntity orderStatus = modifyOrderDTO.getOrderStatusEntity();
		// 租车费用支付状态:0,待支付 1,已支付
		Integer rentCarPayStatus = orderStatus == null ? null:orderStatus.getRentCarPayStatus();
		
		if ((consoleFlag != null && consoleFlag) || (rentCarPayStatus != null && rentCarPayStatus == 0)) {
			// 直接同意
			modifyOrderConfirmService.agreeModifyOrder(modifyOrderDTO, renterOrderNew, initRenterOrder, renterOrderSubsidyDetailDTOList);
		} else {
			if ((supplementAmt != null && supplementAmt <= 0) || needSupplementAmt == 0) {
				// 不需要补付
				modifyOrderForRenterService.supplementPayPostProcess(modifyOrderDTO.getOrderNo(), modifyOrderDTO.getRenterOrderNo(), modifyOrderDTO, renterOrderSubsidyDetailDTOList);
			}
		}
	}
	
	
	
	/**
	 * 计算本次修改补付
	 * @param modifyOrderDTO
	 * @param initRenterOrder
	 * @param renterOrderCostRespDTO
	 * @param renterFineList
	 * @return Integer
	 */
	public Integer getRenterSupplementAmt(ModifyOrderDTO modifyOrderDTO, RenterOrderEntity initRenterOrder, RenterOrderCostRespDTO renterOrderCostRespDTO, List<RenterOrderFineDeatailEntity> renterFineList) {
		// 修改前费用
		Integer initCost = renterOrderCostCombineService.getRenterNormalCost(modifyOrderDTO.getOrderNo(), initRenterOrder.getRenterOrderNo());
		// 修改后费用
		Integer updCost = 0;
		// 租车费用
		List<RenterOrderCostDetailEntity> renterOrderCostDetailDTOList = renterOrderCostRespDTO.getRenterOrderCostDetailDTOList();
		if (renterOrderCostDetailDTOList != null && !renterOrderCostDetailDTOList.isEmpty()) {
			updCost += renterOrderCostDetailDTOList.stream().mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
		}
		// 补贴
		List<RenterOrderSubsidyDetailDTO> renterOrderSubsidyDetailDTOList = renterOrderCostRespDTO.getRenterOrderSubsidyDetailDTOList();
		if (renterOrderSubsidyDetailDTOList != null && !renterOrderSubsidyDetailDTOList.isEmpty()) {
			updCost += renterOrderSubsidyDetailDTOList.stream().mapToInt(RenterOrderSubsidyDetailDTO::getSubsidyAmount).sum();
		}
		// 罚金
		if (renterFineList != null && !renterFineList.isEmpty()) {
			updCost += renterFineList.stream().mapToInt(RenterOrderFineDeatailEntity::getFineAmount).sum();
		}
		Integer supplementAmt = Math.abs(updCost) - Math.abs(initCost);
		return supplementAmt;
	}
	
	
	/**
	 * 聚合租客补贴
	 * @param renterOrderCostRespDTO
	 * @param costDeductVO
	 * @return List<RenterOrderSubsidyDetailDTO>
	 */
	public List<RenterOrderSubsidyDetailDTO> getPolymerizationSubsidy(RenterOrderCostRespDTO renterOrderCostRespDTO, CostDeductVO costDeductVO) {
		// 补贴
		List<RenterOrderSubsidyDetailDTO> renterOrderSubsidyDetailDTOList = renterOrderCostRespDTO.getRenterOrderSubsidyDetailDTOList();
		List<RenterOrderSubsidyDetailDTO> renterSubsidyList = costDeductVO.getRenterSubsidyList();
		List<RenterOrderSubsidyDetailDTO> rosdList = new ArrayList<RenterOrderSubsidyDetailDTO>();
		if (renterOrderSubsidyDetailDTOList != null && !renterOrderSubsidyDetailDTOList.isEmpty()) {
			rosdList.addAll(renterOrderSubsidyDetailDTOList);
		}
		if (renterSubsidyList != null && !renterSubsidyList.isEmpty()) {
			rosdList.addAll(renterSubsidyList);
		}
		return rosdList;
	}
	
	
	/**
	 * 保存附加驾驶人信息
	 * @param modifyOrderDTO
	 */
	public void saveAdditionalDriver(ModifyOrderDTO modifyOrderDTO, RenterOrderEntity initRenterOrder) {
		// 附加驾驶人列表
		List<String> driverIds = modifyOrderDTO.getDriverIds();
		if (driverIds == null || driverIds.isEmpty()) {
			return;
		}
		// 获取附加驾驶人信息
		List<CommUseDriverInfoDTO> useDriverList = memProxyService.getCommUseDriverList(modifyOrderDTO.getMemNo());
		// 保存
		renterAdditionalDriverService.insertBatchAdditionalDriverForModify(modifyOrderDTO.getOrderNo(), modifyOrderDTO.getRenterOrderNo(), initRenterOrder.getRenterOrderNo(), driverIds, useDriverList);
	}
	
	
	/**
	 * 获取组装后的租客商品详情
	 * @param modifyOrderDTO
	 * @param renterOrderEntity
	 * @return RenterGoodsDetailDTO
	 */
	public RenterGoodsDetailDTO getRenterGoodsDetailDTO(ModifyOrderDTO modifyOrderDTO, RenterOrderEntity renterOrderEntity) {
		// 调远程获取最新租客商品详情
		RenterGoodsDetailDTO renterGoodsDetailDTO = goodsService.getRenterGoodsDetail(convertToCarDetailReqVO(modifyOrderDTO, renterOrderEntity));
		if (renterGoodsDetailDTO == null) {
			log.error("getRenterGoodsDetailDTO renterGoodsDetailDTO为空");
			Cat.logError("getRenterGoodsDetailDTO renterGoodsDetailDTO为空",new ModifyOrderParameterException());
			throw new ModifyOrderParameterException();
		}
		renterGoodsDetailDTO.setOrderNo(modifyOrderDTO.getOrderNo());
		renterGoodsDetailDTO.setRenterOrderNo(renterOrderEntity.getRenterOrderNo());
		renterGoodsDetailDTO.setRentTime(modifyOrderDTO.getRentTime());
		renterGoodsDetailDTO.setRevertTime(modifyOrderDTO.getRevertTime());
		renterGoodsDetailDTO.setOldRentTime(renterOrderEntity.getExpRentTime());
		if (modifyOrderDTO.getTransferFlag() != null && modifyOrderDTO.getTransferFlag()) {
			// 换车操作不需要商品历史价格信息
			renterGoodsDetailDTO.setRenterOrderNo(null);
		}
		// 获取汇总后的租客商品详情
		renterGoodsDetailDTO = commodityService.setPriceAndGroup(renterGoodsDetailDTO);
		// 设置最新的租客订单号
		renterGoodsDetailDTO.setRenterOrderNo(modifyOrderDTO.getRenterOrderNo());
		// 每天价
		List<RenterGoodsPriceDetailDTO> renterGoodsPriceDetailDTOList = renterGoodsDetailDTO.getRenterGoodsPriceDetailDTOList();
        if (renterGoodsPriceDetailDTOList == null || renterGoodsPriceDetailDTOList.isEmpty()) {
			log.error("getRenterGoodsDetailDTO renterGoodsPriceDetailDTOList为空");
			Cat.logError("getRenterGoodsDetailDTO renterGoodsPriceDetailDTOList为空",new ModifyOrderParameterException());
			return renterGoodsDetailDTO;
		}
		for (RenterGoodsPriceDetailDTO rp:renterGoodsPriceDetailDTOList) {
			rp.setOrderNo(modifyOrderDTO.getOrderNo());
			rp.setRenterOrderNo(modifyOrderDTO.getRenterOrderNo());
		}
		return renterGoodsDetailDTO;
	}
	
	/**
	 * 转DTO
	 * @param modifyOrderReq
	 * @return ModifyOrderDTO
	 */
	public ModifyOrderDTO getModifyOrderDTO(ModifyOrderReq modifyOrderReq, String renterOrderNo, RenterOrderEntity initRenterOrder, List<RenterOrderDeliveryEntity> deliveryList) {
		ModifyOrderDTO modifyOrderDTO = new ModifyOrderDTO();
		BeanUtils.copyProperties(modifyOrderReq, modifyOrderDTO);
		// 获取订单来源信息
        OrderSourceStatEntity osse = orderSourceStatService.selectByOrderNo(modifyOrderReq.getOrderNo());
        if (osse != null) {
        	modifyOrderDTO.setLongCouponCode(osse.getLongRentCouponCode());
        }
        RenterOrderDeliveryMode mode = renterOrderDeliveryModeService.getDeliveryModeByRenterOrderNo(initRenterOrder.getRenterOrderNo());
		if (modifyOrderReq.getDistributionMode() == null && mode != null) {
			modifyOrderDTO.setDistributionMode(mode.getDistributionMode());
		}
		initRenterOrder.setDistributionMode(mode == null ? null:mode.getDistributionMode());
        // 设置租客子单号
		modifyOrderDTO.setRenterOrderNo(renterOrderNo);
		// 设置管理后台修改原因
		modifyOrderDTO.setChangeReason(modifyOrderReq.getModifyReason());
		// 是否使用全面保障
		Integer abatementFlag = modifyOrderReq.getAbatementFlag();
		if (abatementFlag == null) {
			modifyOrderDTO.setAbatementFlag(initRenterOrder.getIsAbatement());
		} 
		if (modifyOrderReq.getTyreInsurFlag() == null) {
			modifyOrderDTO.setTyreInsurFlag(initRenterOrder.getTyreInsurFlag());
		}
		if (modifyOrderReq.getDriverInsurFlag() == null) {
			modifyOrderDTO.setDriverInsurFlag(initRenterOrder.getDriverInsurFlag());
		}
		if (StringUtils.isBlank(modifyOrderReq.getRentTime())) {
			modifyOrderDTO.setRentTime(initRenterOrder.getExpRentTime());
		} else {
			modifyOrderDTO.setRentTime(CommonUtils.parseTime(modifyOrderReq.getRentTime(), CommonUtils.FORMAT_STR_LONG));
		}
		if (StringUtils.isBlank(modifyOrderReq.getRevertTime())) {
			modifyOrderDTO.setRevertTime(initRenterOrder.getExpRevertTime());
		} else {
			modifyOrderDTO.setRevertTime(CommonUtils.parseTime(modifyOrderReq.getRevertTime(), CommonUtils.FORMAT_STR_LONG));
		}
		// 获取修改前的附加驾驶人列表
		List<String> drivers = renterAdditionalDriverService.listDriverIdByRenterOrderNo(initRenterOrder.getRenterOrderNo());
		// 本次修改新增的附加驾驶人列表
		List<String> addDrivers = modifyOrderReq.getDriverIds();
		// 汇总后的附加驾驶人
		List<String> afterDrivers = new ArrayList<String>();
		if (drivers != null && !drivers.isEmpty()) {
			afterDrivers.addAll(drivers); 
		}
		if (addDrivers != null && !addDrivers.isEmpty()) {
			afterDrivers.addAll(addDrivers);
		}
		// 去重处理
		afterDrivers = afterDrivers.stream().distinct().collect(Collectors.toList());
		modifyOrderDTO.setDriverIds(afterDrivers);
		Map<Integer, RenterOrderDeliveryEntity> deliveryMap = null;
		if (deliveryList != null && !deliveryList.isEmpty()) {
			deliveryMap = deliveryList.stream().collect(Collectors.toMap(RenterOrderDeliveryEntity::getType, deliver -> {return deliver;}));
		}
		if (deliveryMap != null) {
			RenterOrderDeliveryEntity srvGetDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_GET_TYPE.getCode());
			RenterOrderDeliveryEntity srvReturnDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_RETURN_TYPE.getCode());
			if (StringUtils.isBlank(modifyOrderReq.getGetCarAddress()) && srvGetDelivery != null) {
				modifyOrderDTO.setGetCarAddress(srvGetDelivery.getRenterGetReturnAddr());
				modifyOrderDTO.setGetCarLat(srvGetDelivery.getRenterGetReturnAddrLat());
				modifyOrderDTO.setGetCarLon(srvGetDelivery.getRenterGetReturnAddrLon());
			}
			if (StringUtils.isBlank(modifyOrderReq.getRevertCarAddress()) && srvReturnDelivery != null) {
				modifyOrderDTO.setRevertCarAddress(srvReturnDelivery.getRenterGetReturnAddr());
				modifyOrderDTO.setRevertCarLat(srvReturnDelivery.getRenterGetReturnAddrLat());
				modifyOrderDTO.setRevertCarLon(srvReturnDelivery.getRenterGetReturnAddrLon());
			}
		}
		if (modifyOrderReq.getSrvGetFlag() == null) {
			modifyOrderDTO.setSrvGetFlag(initRenterOrder.getIsGetCar());
		}
		if (modifyOrderReq.getSrvReturnFlag() == null) {
			modifyOrderDTO.setSrvReturnFlag(initRenterOrder.getIsReturnCar());
		}
		if (modifyOrderReq.getUserCoinFlag() == null) {
			modifyOrderDTO.setUserCoinFlag(initRenterOrder.getIsUseCoin());
		}
		// 获取修改前租客使用的优惠券列表
		List<OrderCouponEntity> orderCouponList = orderCouponService.listOrderCouponByRenterOrderNo(initRenterOrder.getRenterOrderNo());
		String initCarOwnerCouponId = null;
		String initGetReturnCouponId = null;
		String initPlatformCouponId = null;
		if (orderCouponList != null && !orderCouponList.isEmpty()) {
			// 设置已使用的优惠券列表
			modifyOrderDTO.setOrderCouponList(orderCouponList);
			Map<Integer, String> orderCouponMap = orderCouponList.stream().collect(Collectors.toMap(OrderCouponEntity::getCouponType, OrderCouponEntity::getCouponId));
			initCarOwnerCouponId = orderCouponMap.get(CouponTypeEnum.ORDER_COUPON_TYPE_OWNER.getCode()); 
			initGetReturnCouponId = orderCouponMap.get(CouponTypeEnum.ORDER_COUPON_TYPE_GET_RETURN_SRV.getCode());
			initPlatformCouponId = orderCouponMap.get(CouponTypeEnum.ORDER_COUPON_TYPE_PLATFORM.getCode());
		}
		if (StringUtils.isBlank(modifyOrderReq.getCarOwnerCouponId())) {
			modifyOrderDTO.setCarOwnerCouponId(initCarOwnerCouponId);
		}
		if (StringUtils.isBlank(modifyOrderReq.getSrvGetReturnCouponId())) {
			modifyOrderDTO.setSrvGetReturnCouponId(initGetReturnCouponId);
		}
		if (StringUtils.isBlank(modifyOrderReq.getPlatformCouponId())) {
			modifyOrderDTO.setPlatformCouponId(initPlatformCouponId);
		}
		modifyOrderDTO.setInitDeliveryMode(mode);
		modifyOrderDTO.setChangeItemList(ModifyOrderUtils.listOrderChangeItemDTO(renterOrderNo, initRenterOrder, modifyOrderReq, orderCouponList, deliveryMap));
		return modifyOrderDTO;
	}
	
	/**
	 * 数据转化为CarDetailReqVO
	 * @param renterOrderEntity
	 * @param modifyOrderDTO
	 * @return CarDetailReqVO
	 */
	public CarProxyService.CarDetailReqVO convertToCarDetailReqVO(ModifyOrderDTO modifyOrderDTO, RenterOrderEntity renterOrderEntity) {
		if (modifyOrderDTO.getTransferFlag() != null && modifyOrderDTO.getTransferFlag()) {
			// 换车操作
			return convertToCarDetailReqVOTransfer(modifyOrderDTO);
		}
		// 租客子订单号
		String renterOrderNo = renterOrderEntity.getRenterOrderNo();
		// 获取租客商品信息
		RenterGoodsDetailDTO renterGoodsDetailDTO = commodityService.getRenterGoodsDetail(renterOrderNo, false);
		CarProxyService.CarDetailReqVO carDetailReqVO = new CarProxyService.CarDetailReqVO();
		if (renterGoodsDetailDTO.getCarAddrIndex() != null) {
			carDetailReqVO.setAddrIndex(renterGoodsDetailDTO.getCarAddrIndex());
		}
		carDetailReqVO.setCarNo(String.valueOf(renterGoodsDetailDTO.getCarNo()));
		carDetailReqVO.setRentTime(modifyOrderDTO.getRentTime());
		carDetailReqVO.setRevertTime(modifyOrderDTO.getRevertTime());
		Integer useSpecialPriceFlag = renterOrderEntity.getIsUseSpecialPrice();
		Boolean useSpecialPrice = (useSpecialPriceFlag != null && useSpecialPriceFlag == 1)?true:false;
		carDetailReqVO.setUseSpecialPrice(useSpecialPrice);
		return carDetailReqVO;
	}
	
	
	/**
	 * 数据转化为CarDetailReqVO（换车使用）
	 * @param modifyOrderDTO
	 * @return CarDetailReqVO
	 */
	public CarProxyService.CarDetailReqVO convertToCarDetailReqVOTransfer(ModifyOrderDTO modifyOrderDTO) {
		CarProxyService.CarDetailReqVO carDetailReqVO = new CarProxyService.CarDetailReqVO();
		carDetailReqVO.setCarNo(modifyOrderDTO.getCarNo());
		carDetailReqVO.setRentTime(modifyOrderDTO.getRentTime());
		carDetailReqVO.setRevertTime(modifyOrderDTO.getRevertTime());
		Integer useSpecialPriceFlag = modifyOrderDTO.getUseSpecialPriceFlag();
		Boolean useSpecialPrice = (useSpecialPriceFlag != null && useSpecialPriceFlag == 1)?true:false;
		carDetailReqVO.setUseSpecialPrice(useSpecialPrice);
		return carDetailReqVO;
	}
	
	
	/**
	 * 组装租客会员信息
	 * @param renterOrderNo 修改前
	 * @param updRenterOrderNo 修改后
	 * @return RenterMemberDTO
	 */
	public RenterMemberDTO getRenterMemberDTO(String renterOrderNo, String updRenterOrderNo) {
		// 获取租客会员信息
		RenterMemberDTO renterMemberDTO = renterMemberService.selectrenterMemberByRenterOrderNo(renterOrderNo, true);
		renterMemberDTO.setRenterOrderNo(updRenterOrderNo);
		// 会员权益
		List<RenterMemberRightDTO> renterMemberRightDTOList = renterMemberDTO.getRenterMemberRightDTOList();
		if (renterMemberRightDTOList == null || renterMemberRightDTOList.isEmpty()) {
			return renterMemberDTO;
		}
		for (RenterMemberRightDTO rr:renterMemberRightDTOList) {
			rr.setRenterOrderNo(updRenterOrderNo);
		}
		return renterMemberDTO;
	}
	
	
	/**
	 * 组装租客子单
	 * @param modifyOrderDTO
	 * @param renterOrderEntity
	 * @return RenterOrderEntity
	 */
	public RenterOrderEntity convertToRenterOrderEntity(ModifyOrderDTO modifyOrderDTO, RenterOrderEntity renterOrderEntity) {
		RenterOrderEntity renterOrderNew = new RenterOrderEntity();
		BeanUtils.copyProperties(renterOrderEntity, renterOrderNew);
		renterOrderNew.setExpRentTime(modifyOrderDTO.getRentTime());
		renterOrderNew.setExpRevertTime(modifyOrderDTO.getRevertTime());
		renterOrderNew.setRenterOrderNo(modifyOrderDTO.getRenterOrderNo());
		renterOrderNew.setId(null);
		renterOrderNew.setChildStatus(RenterChildStatusEnum.PROCESS_ING.getCode());
		renterOrderNew.setIsUseCoin(modifyOrderDTO.getUserCoinFlag());
		if (StringUtils.isNotBlank(modifyOrderDTO.getCarOwnerCouponId()) || 
				StringUtils.isNotBlank(modifyOrderDTO.getPlatformCouponId()) || 
				StringUtils.isNotBlank(modifyOrderDTO.getSrvGetReturnCouponId())) {
			renterOrderNew.setIsUseCoupon(1);
		} else {
			renterOrderNew.setIsUseCoupon(0);
		}
		renterOrderNew.setIsGetCar(modifyOrderDTO.getSrvGetFlag());
		renterOrderNew.setIsReturnCar(modifyOrderDTO.getSrvReturnFlag());
		int addDriver = modifyOrderDTO.getDriverIds() == null ? 0:modifyOrderDTO.getDriverIds().size();
		renterOrderNew.setAddDriver(addDriver);
		renterOrderNew.setIsAbatement(modifyOrderDTO.getAbatementFlag());
		renterOrderNew.setTyreInsurFlag(modifyOrderDTO.getTyreInsurFlag());
		renterOrderNew.setDriverInsurFlag(modifyOrderDTO.getDriverInsurFlag());
		renterOrderNew.setIsEffective(0);
		renterOrderNew.setAgreeFlag(0);
		renterOrderNew.setCreateOp(modifyOrderDTO.getOperator());
		renterOrderNew.setCreateTime(null);
		renterOrderNew.setUpdateOp(null);
		renterOrderNew.setUpdateTime(null);
		if (modifyOrderDTO.getConsoleFlag() != null && modifyOrderDTO.getConsoleFlag()) {
			renterOrderNew.setChangeSource(ChangeSourceEnum.CONSOLE.getCode());
		} else {
			renterOrderNew.setChangeSource(ChangeSourceEnum.RENTER.getCode());
		}
		// 管理后台修改原因
		renterOrderNew.setChangeReason(modifyOrderDTO.getChangeReason());
		if (modifyOrderDTO.getScanCodeFlag() != null && modifyOrderDTO.getScanCodeFlag()) {
			// 扫码还车
			renterOrderNew.setActRevertTime(modifyOrderDTO.getRevertTime());
			renterOrderNew.setExpRevertTime(renterOrderEntity.getExpRevertTime());

		}
		return renterOrderNew;
	}
	
	
	/**
	 * 基础费用计算
	 * @param modifyOrderDTO
	 * @param renterOrderReqVO
	 * @param initCostList
     * @param initSubsidyList
	 * @return RenterOrderCostRespDTO
	 */
	public RenterOrderCostRespDTO getRenterOrderCostRespDTO(ModifyOrderDTO modifyOrderDTO, RenterOrderReqVO renterOrderReqVO, List<RenterOrderCostDetailEntity> initCostList, List<RenterOrderSubsidyDetailEntity> initSubsidyList) {
		// 构建参数
		RenterOrderCostReqDTO renterOrderCostReqDTO = renterOrderService.buildRenterOrderCostReqDTO(renterOrderReqVO);
		List<RenterOrderSubsidyDetailDTO> subsidyList = new ArrayList<RenterOrderSubsidyDetailDTO>();
		if (StringUtils.isNotBlank(modifyOrderDTO.getLongCouponCode())) {
			// 长租订单
			Integer carNo = modifyOrderDTO.getRenterGoodsDetailDTO() == null ? null:modifyOrderDTO.getRenterGoodsDetailDTO().getCarNo();
			String carNoStr = carNo == null ? null:String.valueOf(carNo);
			List<RenterOrderSubsidyDetailDTO> rentAmtSubsidyList = longRentSubsidyService.getLongRentAmtSubsidy(carNoStr, modifyOrderDTO.getLongCouponCode(), renterOrderCostReqDTO, modifyOrderDTO.getRenterSubsidyList());
			if (rentAmtSubsidyList != null && !rentAmtSubsidyList.isEmpty()) {
				subsidyList.addAll(rentAmtSubsidyList);
			}
			if (modifyOrderDTO.getRenterSubsidyList() != null && !modifyOrderDTO.getRenterSubsidyList().isEmpty()) {
				subsidyList.addAll(modifyOrderDTO.getRenterSubsidyList());
			}
		} else {
			// 获取平台保障费和补充保障服务费折扣补贴
			List<RenterOrderSubsidyDetailDTO> insurDiscountSubsidyList = insurAbamentDiscountService.getInsureDiscountSubsidy(renterOrderCostReqDTO, modifyOrderDTO.getRenterSubsidyList());
			// 获取费用补贴列表
			subsidyList = getRenterSubsidyList(modifyOrderDTO, renterOrderCostReqDTO, modifyOrderDTO.getRenterSubsidyList(), initCostList, initSubsidyList, insurDiscountSubsidyList);
			if (insurDiscountSubsidyList != null && !insurDiscountSubsidyList.isEmpty()) {
				subsidyList.addAll(insurDiscountSubsidyList);
			}
		}
		renterOrderCostReqDTO.setSubsidyOutList(subsidyList);
		// 获取计算好的费用信息
		RenterOrderCostRespDTO renterOrderCostRespDTO = renterOrderCalCostService.calcBasicRenterOrderCostAndDeailList(renterOrderCostReqDTO);
		if (StringUtils.isNotBlank(modifyOrderDTO.getLongCouponCode())) {
			// 长租订单逻辑处理
			renterOrderCostRespDTO = longRentCostHandleService.handRentCost(renterOrderCostReqDTO, renterOrderCostRespDTO);
		}
		renterOrderCostRespDTO.setOrderNo(modifyOrderDTO.getOrderNo());
		renterOrderCostRespDTO.setRenterOrderNo(modifyOrderDTO.getRenterOrderNo());
		renterOrderCostRespDTO.setMemNo(modifyOrderDTO.getMemNo());
		return renterOrderCostRespDTO;
	}
	
	
	/**
	 * 获取费用补贴列表
	 * @param renterOrderCostReqDTO
	 * @param renterSubsidyList
	 * @param initCostList
	 * @param initSubsidyList
	 * @return List<RenterOrderSubsidyDetailDTO>
	 */
	public List<RenterOrderSubsidyDetailDTO> getRenterSubsidyList(ModifyOrderDTO modifyOrderDTO, RenterOrderCostReqDTO renterOrderCostReqDTO, List<RenterOrderSubsidyDetailDTO> renterSubsidyList, List<RenterOrderCostDetailEntity> initCostList, List<RenterOrderSubsidyDetailEntity> initSubsidyList, List<RenterOrderSubsidyDetailDTO> insurDiscountSubsidyList) {
		if (renterSubsidyList == null) {
			renterSubsidyList = new ArrayList<RenterOrderSubsidyDetailDTO>();
		}
		if (modifyOrderDTO == null) {
			return renterSubsidyList;
		}
		if (modifyOrderDTO.getTransferFlag() != null && modifyOrderDTO.getTransferFlag()) {
			// 换车操作
			//return getRenterSubsidyListTransfer(renterOrderCostReqDTO, initCostList, initSubsidyList);
			return renterSubsidyList;
		}
		// 订单状态
		OrderStatusEntity orderStatusEntity = modifyOrderDTO.getOrderStatusEntity();
		if (orderStatusEntity != null && orderStatusEntity.getStatus() != null && 
				orderStatusEntity.getStatus() < OrderStatusEnum.TO_RETURN_CAR.getStatus()) {
			// 订单开始前保险不计免赔退回
			return renterSubsidyList;
		}
		Map<String,RenterOrderSubsidyDetailDTO> subMap = renterSubsidyList.stream().collect(Collectors.toMap(RenterOrderSubsidyDetailDTO::getSubsidyCostCode, sub -> {return sub;}));
		if (subMap == null || subMap.get(RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo()) == null) {
			//获取平台保障费
	        RenterOrderCostDetailEntity insurAmtEntity = renterOrderCostCombineService.getInsurAmtEntity(renterOrderCostReqDTO.getInsurAmtDTO());
	        Integer insurAmt = insurAmtEntity.getTotalAmount();
	        if (insurDiscountSubsidyList != null) {
	        	insurAmt += insurDiscountSubsidyList.stream().filter(cost -> {return RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo().equals(cost.getSubsidyCostCode());}).mapToInt(RenterOrderSubsidyDetailDTO::getSubsidyAmount).sum();
	        }
	        // 修改前平台保障费
	        Integer initInsurAmt = 0;
	        if (initCostList != null && !initCostList.isEmpty()) {
	        	initInsurAmt += initCostList.stream().filter(cost -> {return RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo().equals(cost.getCostCode());}).mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
	        }
	        if (initSubsidyList != null && !initSubsidyList.isEmpty()) {
	        	initInsurAmt += initSubsidyList.stream().filter(cost -> {return RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo().equals(cost.getSubsidyCostCode());}).mapToInt(RenterOrderSubsidyDetailEntity::getSubsidyAmount).sum();
	        }
	        if (insurAmt != null && initInsurAmt != null && Math.abs(initInsurAmt) > Math.abs(insurAmt)) {
	        	Integer subsidyAmount = Math.abs(insurAmt) - Math.abs(initInsurAmt);
	        	// 产生补贴
	        	RenterOrderSubsidyDetailDTO subsidyDetail = insurAbamentDiscountService.convertToRenterOrderSubsidyDetailDTO(renterOrderCostReqDTO.getCostBaseDTO(), subsidyAmount, SubsidyTypeCodeEnum.INSURE_AMT, 
	        			SubsidySourceCodeEnum.RENTER, SubsidySourceCodeEnum.PLATFORM, RenterCashCodeEnum.INSURE_TOTAL_PRICES, "修改订单平台保障费不退还");
	        	renterSubsidyList.add(subsidyDetail);
	        }
		}
		if (subMap == null || subMap.get(RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo()) == null) {
			//获取补充保障服务费
	        List<RenterOrderCostDetailEntity> comprehensiveEnsureList = renterOrderCostCombineService.listAbatementAmtEntity(renterOrderCostReqDTO.getAbatementAmtDTO());
	        Integer comprehensiveEnsureAmount = comprehensiveEnsureList.stream().collect(Collectors.summingInt(RenterOrderCostDetailEntity::getTotalAmount));
	        if (insurDiscountSubsidyList != null) {
	        	comprehensiveEnsureAmount += insurDiscountSubsidyList.stream().filter(cost -> {return RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo().equals(cost.getSubsidyCostCode());}).mapToInt(RenterOrderSubsidyDetailDTO::getSubsidyAmount).sum();
	        }
	        // 修改前补充保障服务费
	        Integer initAbatementAmt = 0;
	        if (initCostList != null && !initCostList.isEmpty()) {
	        	initAbatementAmt += initCostList.stream().filter(cost -> {return RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo().equals(cost.getCostCode());}).mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
	        }
	        if (initSubsidyList != null && !initSubsidyList.isEmpty()) {
	        	initAbatementAmt += initSubsidyList.stream().filter(cost -> {return RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo().equals(cost.getSubsidyCostCode());}).mapToInt(RenterOrderSubsidyDetailEntity::getSubsidyAmount).sum();
	        }
	        if (comprehensiveEnsureAmount != null && initAbatementAmt != null && Math.abs(initAbatementAmt) > Math.abs(comprehensiveEnsureAmount)) {
	        	Integer subsidyAmount = Math.abs(comprehensiveEnsureAmount) - Math.abs(initAbatementAmt);
	        	// 产生补贴
	        	RenterOrderSubsidyDetailDTO subsidyDetail = insurAbamentDiscountService.convertToRenterOrderSubsidyDetailDTO(renterOrderCostReqDTO.getCostBaseDTO(), subsidyAmount, SubsidyTypeCodeEnum.ABATEMENT_INSURE, 
	        			SubsidySourceCodeEnum.RENTER, SubsidySourceCodeEnum.PLATFORM, RenterCashCodeEnum.ABATEMENT_INSURE, "修改订单补充保障服务费不退还");
	        	renterSubsidyList.add(subsidyDetail);
	        }
		}
		return renterSubsidyList;
	}
	
	/**
	 * 计算换车补贴
	 * @param renterOrderCostReqDTO
	 * @param initCostList
	 * @param initSubsidyList
	 * @return List<RenterOrderSubsidyDetailDTO>
	 */
	public List<RenterOrderSubsidyDetailDTO> getRenterSubsidyListTransfer(RenterOrderCostReqDTO renterOrderCostReqDTO, List<RenterOrderCostDetailEntity> initCostList, List<RenterOrderSubsidyDetailEntity> initSubsidyList) {
		List<RenterOrderSubsidyDetailDTO> renterSubsidyList = new ArrayList<RenterOrderSubsidyDetailDTO>();
		// 获取租金
        List<RenterOrderCostDetailEntity> renterOrderCostDetailEntities = renterOrderCostCombineService.listRentAmtEntity(renterOrderCostReqDTO.getRentAmtDTO());
        Integer rentAmt = renterOrderCostDetailEntities.stream().collect(Collectors.summingInt(RenterOrderCostDetailEntity::getTotalAmount));
		// 修改前租金
        Integer initRentAmt = 0;
        if (initCostList != null && !initCostList.isEmpty()) {
        	initRentAmt += initCostList.stream().filter(cost -> {return RenterCashCodeEnum.RENT_AMT.getCashNo().equals(cost.getCostCode());}).mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
        }
        if (initSubsidyList != null && !initSubsidyList.isEmpty()) {
        	initRentAmt += initSubsidyList.stream().filter(cost -> {return RenterCashCodeEnum.RENT_AMT.getCashNo().equals(cost.getSubsidyCostCode());}).mapToInt(RenterOrderSubsidyDetailEntity::getSubsidyAmount).sum();
        }
        if (rentAmt != null && initRentAmt != null && Math.abs(initRentAmt) < Math.abs(rentAmt)) {
        	Integer subsidyAmount = Math.abs(rentAmt) - Math.abs(initRentAmt);
        	// 产生补贴
        	RenterOrderSubsidyDetailDTO subsidyDetail = insurAbamentDiscountService.convertToRenterOrderSubsidyDetailDTO(renterOrderCostReqDTO.getCostBaseDTO(), subsidyAmount, SubsidyTypeCodeEnum.RENT_AMT, 
        			SubsidySourceCodeEnum.PLATFORM, SubsidySourceCodeEnum.RENTER, RenterCashCodeEnum.RENT_AMT, "换车补贴");
        	renterSubsidyList.add(subsidyDetail);
        }
		// 获取平台保障费
        RenterOrderCostDetailEntity insurAmtEntity = renterOrderCostCombineService.getInsurAmtEntity(renterOrderCostReqDTO.getInsurAmtDTO());
        Integer insurAmt = insurAmtEntity.getTotalAmount();
        // 修改前平台保障费
        Integer initInsurAmt = 0;
        if (initCostList != null && !initCostList.isEmpty()) {
        	initInsurAmt += initCostList.stream().filter(cost -> {return RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo().equals(cost.getCostCode());}).mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
        }
        if (initSubsidyList != null && !initSubsidyList.isEmpty()) {
        	initInsurAmt += initSubsidyList.stream().filter(cost -> {return RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo().equals(cost.getSubsidyCostCode());}).mapToInt(RenterOrderSubsidyDetailEntity::getSubsidyAmount).sum();
        }
        if (insurAmt != null && initInsurAmt != null && Math.abs(initInsurAmt) < Math.abs(insurAmt)) {
        	Integer subsidyAmount = Math.abs(insurAmt) - Math.abs(initInsurAmt);
        	// 产生补贴
        	RenterOrderSubsidyDetailDTO subsidyDetail = insurAbamentDiscountService.convertToRenterOrderSubsidyDetailDTO(renterOrderCostReqDTO.getCostBaseDTO(), subsidyAmount, SubsidyTypeCodeEnum.INSURE_AMT, 
        			SubsidySourceCodeEnum.PLATFORM, SubsidySourceCodeEnum.RENTER, RenterCashCodeEnum.INSURE_TOTAL_PRICES, "换车补贴");
        	renterSubsidyList.add(subsidyDetail);
        }
        
        // 获取补充保障服务费
        List<RenterOrderCostDetailEntity> comprehensiveEnsureList = renterOrderCostCombineService.listAbatementAmtEntity(renterOrderCostReqDTO.getAbatementAmtDTO());
        Integer comprehensiveEnsureAmount = comprehensiveEnsureList.stream().collect(Collectors.summingInt(RenterOrderCostDetailEntity::getTotalAmount));
        // 修改前补充保障服务费
        Integer initAbatementAmt = 0;
        if (initCostList != null && !initCostList.isEmpty()) {
        	initAbatementAmt += initCostList.stream().filter(cost -> {return RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo().equals(cost.getCostCode());}).mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
        }
        if (initSubsidyList != null && !initSubsidyList.isEmpty()) {
        	initAbatementAmt += initSubsidyList.stream().filter(cost -> {return RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo().equals(cost.getSubsidyCostCode());}).mapToInt(RenterOrderSubsidyDetailEntity::getSubsidyAmount).sum();
        }
        if (comprehensiveEnsureAmount != null && initAbatementAmt != null && Math.abs(initAbatementAmt) < Math.abs(comprehensiveEnsureAmount)) {
        	Integer subsidyAmount = Math.abs(comprehensiveEnsureAmount) - Math.abs(initAbatementAmt);
        	// 产生补贴
        	RenterOrderSubsidyDetailDTO subsidyDetail = insurAbamentDiscountService.convertToRenterOrderSubsidyDetailDTO(renterOrderCostReqDTO.getCostBaseDTO(), subsidyAmount, SubsidyTypeCodeEnum.ABATEMENT_INSURE, 
        			SubsidySourceCodeEnum.PLATFORM, SubsidySourceCodeEnum.RENTER, RenterCashCodeEnum.ABATEMENT_INSURE, "换车补贴");
        	renterSubsidyList.add(subsidyDetail);
        }
        
        return renterSubsidyList;
	}
	
	
	/**
	 * 获取修改订单违约金
	 * @param modifyOrderDTO
	 * @param initRenterOrder
	 * @param deliveryList
	 * @param renterOrderCostRespDTO
	 * @return List<RenterOrderFineDeatailEntity>
	 */
	public List<RenterOrderFineDeatailEntity> getRenterFineList(ModifyOrderDTO modifyOrderDTO, RenterOrderEntity initRenterOrder, List<RenterOrderDeliveryEntity> deliveryList, RenterOrderCostRespDTO renterOrderCostRespDTO) {
		// 获取取还车违约金
		List<RenterOrderFineDeatailEntity> renterFineList = getRenterGetReturnFineList(modifyOrderDTO, initRenterOrder, deliveryList);
		// 获取提前还车违约金
		RenterOrderFineDeatailEntity renterFine = getRenterFineEntity(modifyOrderDTO, renterOrderCostRespDTO, initRenterOrder);
		if (renterFineList == null) {
			renterFineList = new ArrayList<RenterOrderFineDeatailEntity>();
		}
		if (renterFine != null) {
			renterFineList.add(renterFine);
		}
		return renterFineList;
	}
	
	
	/**
	 * 获取取还车违约金
	 * @param modifyOrderDTO
	 * @param initRenterOrder
	 * @param deliveryList
	 * @return List<RenterOrderFineDeatailEntity>
	 */
	public List<RenterOrderFineDeatailEntity> getRenterGetReturnFineList(ModifyOrderDTO modifyOrderDTO, RenterOrderEntity initRenterOrder, List<RenterOrderDeliveryEntity> deliveryList) {
		// 封装修改前对象
		Map<Integer, RenterOrderDeliveryEntity> deliveryMap = null;
		if (deliveryList != null && !deliveryList.isEmpty()) {
			deliveryMap = deliveryList.stream().collect(Collectors.toMap(RenterOrderDeliveryEntity::getType, deliver -> {return deliver;}));
		}
		String getLon = null,getLat = null,returnLon = null,returnLat = null;
		if (deliveryMap != null) {
			RenterOrderDeliveryEntity srvGetDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_GET_TYPE.getCode());
			RenterOrderDeliveryEntity srvReturnDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_RETURN_TYPE.getCode());
			if (srvGetDelivery != null) {
				getLon = srvGetDelivery.getRenterGetReturnAddrLon();
				getLat = srvGetDelivery.getRenterGetReturnAddrLat();
			}
			if (srvReturnDelivery != null) {
				returnLon = srvReturnDelivery.getRenterGetReturnAddrLon();
				returnLat = srvReturnDelivery.getRenterGetReturnAddrLat();
			}
		}
		GetReturnAddressInfoDTO initInfo = new GetReturnAddressInfoDTO(initRenterOrder.getIsGetCar(), initRenterOrder.getIsReturnCar(), getLon, getLat, returnLon, returnLat);
		CostBaseDTO initBase = new CostBaseDTO(modifyOrderDTO.getOrderNo(), modifyOrderDTO.getRenterOrderNo(), modifyOrderDTO.getMemNo(), initRenterOrder.getExpRentTime(), initRenterOrder.getExpRevertTime());
		initInfo.setCostBaseDTO(initBase);
		// 封装修改后对象
		GetReturnAddressInfoDTO updateInfo = new GetReturnAddressInfoDTO(modifyOrderDTO.getGetCarLon(), modifyOrderDTO.getGetCarLat(), modifyOrderDTO.getRevertCarLon(), modifyOrderDTO.getRevertCarLat());
		CostBaseDTO updBase = new CostBaseDTO(modifyOrderDTO.getOrderNo(), modifyOrderDTO.getRenterOrderNo(), modifyOrderDTO.getMemNo(), modifyOrderDTO.getRentTime(), modifyOrderDTO.getRevertTime());
		updateInfo.setCostBaseDTO(updBase);
		// 取还车违约金
		Integer cityCode = StringUtils.isBlank(modifyOrderDTO.getCityCode())?null:Integer.valueOf(modifyOrderDTO.getCityCode());
		List<RenterOrderFineDeatailEntity> renterFineList = renterOrderFineDeatailService.calculateGetOrReturnFineAmt(initInfo, updateInfo, cityCode);
		return renterFineList;
	}
	
	/**
	 * 计算提前还车违约金
	 * @param modifyOrderDTO
	 * @param renterOrderCostRespDTO
	 * @param initRenterOrder
	 * @return RenterOrderFineDeatailEntity
	 */
	public RenterOrderFineDeatailEntity getRenterFineEntity(ModifyOrderDTO modifyOrderDTO, RenterOrderCostRespDTO renterOrderCostRespDTO, RenterOrderEntity initRenterOrder) {
		// 获取修改前租客费用明细
		List<RenterOrderCostDetailEntity> initCostList = renterOrderCostDetailService.listRenterOrderCostDetail(modifyOrderDTO.getOrderNo(), initRenterOrder.getRenterOrderNo());
		// 修改前租金
		Integer initAmt = initCostList.stream().filter(cost -> {return RenterCashCodeEnum.RENT_AMT.getCashNo().equals(cost.getCostCode());}).mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();
		// 修改后租金
		Integer updAmt = renterOrderCostRespDTO.getRentAmount();
		// 计算提前还车违约金
		CostBaseDTO updBase = new CostBaseDTO(modifyOrderDTO.getOrderNo(), modifyOrderDTO.getRenterOrderNo(), modifyOrderDTO.getMemNo(), modifyOrderDTO.getRentTime(), modifyOrderDTO.getRevertTime());
		RenterOrderFineDeatailEntity renterFine = renterOrderFineDeatailService.calcFineAmt(updBase, Math.abs(initAmt), Math.abs(updAmt), initRenterOrder.getExpRevertTime());
		return renterFine;
	}
	
	
	/**
	 * 抵扣补贴
	 * @param costBaseDTO
	 * @param renterOrderCostRespDTO
	 * @param renterOrderReqVO
	 * @param orderEntity
	 * @return CostDeductVO
	 */
	public CostDeductVO getCostDeductVO(ModifyOrderDTO modifyOrderDTO, CostBaseDTO costBaseDTO, RenterOrderCostRespDTO renterOrderCostRespDTO, RenterOrderReqVO renterOrderReqVO, OrderEntity orderEntity, List<RenterOrderSubsidyDetailEntity> initSubsidyList) {
		// 抵扣列表
		List<OrderCouponDTO> orderCouponList = new ArrayList<OrderCouponDTO>();
		// 补贴列表
		List<RenterOrderSubsidyDetailDTO> renterSubsidyList = new ArrayList<RenterOrderSubsidyDetailDTO>();
		// 剩余抵扣的租金
		Integer surplusRentAmt = Math.abs(renterOrderCostRespDTO.getRentAmount());
		// 修改前已使用的优惠券列表
		List<OrderCouponEntity> initOrderCouponList = modifyOrderDTO.getOrderCouponList();
		Map<Integer,OrderCouponEntity> couponMap = null;
		if (initOrderCouponList != null && !initOrderCouponList.isEmpty()) {
			couponMap = initOrderCouponList.stream().collect(Collectors.toMap(OrderCouponEntity::getCouponType, initCoupon -> initCoupon));
		}
		// 修改种类
		List<OrderChangeItemDTO> changeItemList = modifyOrderDTO.getChangeItemList();
		List<String> changeCodeList = modifyOrderConfirmService.listChangeCode(changeItemList);
		// 获取车主券抵扣
		OrderCouponEntity carOwnerCouponEntity = couponMap == null ? null:couponMap.get(CouponTypeEnum.ORDER_COUPON_TYPE_OWNER.getCode());
		OrderCouponDTO ownerCoupon = getOwnerCoupon(costBaseDTO, renterOrderReqVO, surplusRentAmt, carOwnerCouponEntity);
		if (ownerCoupon != null) {
			orderCouponList.add(ownerCoupon);
			int ownerCouponAmt = ownerCoupon.getAmount() == null ? 0:ownerCoupon.getAmount();
			surplusRentAmt = surplusRentAmt - ownerCouponAmt;
		}
		// 获取车主券补贴
		RenterOrderSubsidyDetailDTO ownerCouponSubsidy = getOwnerCouponSubsidy(costBaseDTO, ownerCoupon);
		if (ownerCouponSubsidy != null) {
			renterSubsidyList.add(ownerCouponSubsidy);
		}
		// 获取限时立减补贴
		RenterOrderSubsidyDetailDTO limitRedSubsidy = getLimitRedSubsidy(costBaseDTO, orderEntity, surplusRentAmt);
		if (limitRedSubsidy != null) {
			renterSubsidyList.add(limitRedSubsidy);
			int limitRedAmt = limitRedSubsidy.getSubsidyAmount() == null ? 0:limitRedSubsidy.getSubsidyAmount();
			surplusRentAmt = surplusRentAmt - limitRedAmt;
		}
		// 获取取还车券抵扣
		OrderCouponDTO getCarFeeCoupon = null;
		if (changeCodeList != null && !changeCodeList.isEmpty() && changeCodeList.contains(OrderChangeItemEnum.MODIFY_GETRETURNCOUPON.getCode())) {
			getCarFeeCoupon = getGetCarFeeCouponForConsole(costBaseDTO, renterOrderCostRespDTO, renterOrderReqVO);
		} else {
			if (couponMap != null) {
				getCarFeeCoupon = getGetCarFeeCoupon(costBaseDTO, renterOrderCostRespDTO, renterOrderReqVO, couponMap.get(CouponTypeEnum.ORDER_COUPON_TYPE_GET_RETURN_SRV.getCode()));
			}
		}
		if (getCarFeeCoupon != null) {
			orderCouponList.add(getCarFeeCoupon);
		}
		// 获取取还车补贴
		RenterOrderSubsidyDetailDTO getCarFeeCouponSubsidy = getGetCarFeeCouponSubsidy(costBaseDTO, getCarFeeCoupon);
		if (getCarFeeCouponSubsidy != null) {
			renterSubsidyList.add(getCarFeeCouponSubsidy);
		}
		// 获取平台券抵扣
		OrderCouponDTO platformCoupon = null;
		if (changeCodeList != null && !changeCodeList.isEmpty() && changeCodeList.contains(OrderChangeItemEnum.MODIFY_PLATFORMCOUPON.getCode())) {
			platformCoupon = getPlatformCouponForConsole(costBaseDTO, renterOrderCostRespDTO, renterOrderReqVO, getCarFeeCoupon, surplusRentAmt);
		} else {
			if (couponMap != null) {
				platformCoupon = getPlatformCoupon(costBaseDTO, renterOrderCostRespDTO, renterOrderReqVO, getCarFeeCoupon, surplusRentAmt, couponMap.get(CouponTypeEnum.ORDER_COUPON_TYPE_PLATFORM.getCode()));
			}
		}
		if (platformCoupon != null) {
			orderCouponList.add(platformCoupon);
		}
		// 获取平台券补贴
		RenterOrderSubsidyDetailDTO platformCouponSubsidy = getPlatformCouponSubsidy(costBaseDTO, platformCoupon);
		if (platformCouponSubsidy != null) {
			renterSubsidyList.add(platformCouponSubsidy);
			int platformCouponAmt = platformCouponSubsidy.getSubsidyAmount() == null ? 0:platformCouponSubsidy.getSubsidyAmount();
			surplusRentAmt = surplusRentAmt - platformCouponAmt;
		}
		// 凹凸币补贴
		RenterOrderSubsidyDetailDTO autoCoinSubsidy = getAutoCoinSubsidy(modifyOrderDTO, Math.abs(renterOrderCostRespDTO.getRentAmount()), surplusRentAmt);
		if (autoCoinSubsidy != null) {
			renterSubsidyList.add(autoCoinSubsidy);
		}
		CostDeductVO costDeductVO = new CostDeductVO();
		costDeductVO.setOrderCouponList(orderCouponList);
		costDeductVO.setRenterSubsidyList(renterSubsidyList);
		return costDeductVO;
	}
	
	
	/**
	 * 获取车主券抵扣
	 * @param costBaseDTO
	 * @param renterOrderReqVO
	 * @param surplusRentAmt
	 * @return OrderCouponDTO
	 */
	public OrderCouponDTO getOwnerCoupon(CostBaseDTO costBaseDTO, RenterOrderReqVO renterOrderReqVO, Integer surplusRentAmt, OrderCouponEntity ooupon) {
		OwnerCouponGetAndValidReqVO ownerCouponGetAndValidReqVO = renterOrderService.buildOwnerCouponGetAndValidReqVO(renterOrderReqVO,
				surplusRentAmt);
		// 修改标识
		ownerCouponGetAndValidReqVO.setMark(2);
        OrderCouponDTO ownerCoupon = renterOrderCalCostService.calOwnerCouponDeductInfo(ownerCouponGetAndValidReqVO, ooupon);
        if (ownerCoupon != null) {
        	ownerCoupon.setOrderNo(costBaseDTO.getOrderNo());
            ownerCoupon.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
        }
        return ownerCoupon;
	}
	
	/**
	 * 获取车主券补贴
	 * @param costBaseDTO
	 * @param ownerCoupon
	 * @return RenterOrderSubsidyDetailDTO
	 */
	public RenterOrderSubsidyDetailDTO getOwnerCouponSubsidy(CostBaseDTO costBaseDTO, OrderCouponDTO ownerCoupon) {
        if(ownerCoupon == null) {
        	return null;
        }
        //补贴明细
        RenterOrderSubsidyDetailDTO ownerCouponSubsidyInfo =
                renterOrderSubsidyDetailService.calOwnerCouponSubsidyInfo(Integer.valueOf(costBaseDTO.getMemNo()),
                ownerCoupon);
        if (ownerCouponSubsidyInfo != null) {
        	ownerCouponSubsidyInfo.setOrderNo(costBaseDTO.getOrderNo());
        	ownerCouponSubsidyInfo.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
        }
        return ownerCouponSubsidyInfo;
	}
	
	/**
	 * 限时立减补贴
	 * @param costBaseDTO
	 * @param orderEntity
	 * @param surplusRentAmt
	 * @return RenterOrderSubsidyDetailDTO
	 */
	public RenterOrderSubsidyDetailDTO getLimitRedSubsidy(CostBaseDTO costBaseDTO, OrderEntity orderEntity, Integer surplusRentAmt) {
		if (orderEntity == null) {
			return null;
		}
		// 限时红包limit_amt
        Integer reductiAmt = orderEntity.getLimitAmt() == null ? 0:orderEntity.getLimitAmt();
        if (reductiAmt == null || reductiAmt == 0) {
        	return null;
        }
        RenterOrderSubsidyDetailDTO limitRedSubsidyInfo =
                renterOrderSubsidyDetailService.calLimitRedSubsidyInfo(Integer.valueOf(costBaseDTO.getMemNo()),
                        surplusRentAmt,reductiAmt);
        if (limitRedSubsidyInfo != null) {
        	limitRedSubsidyInfo.setOrderNo(costBaseDTO.getOrderNo());
            limitRedSubsidyInfo.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
        }
        return limitRedSubsidyInfo;
	}
	
	
	/**
	 * 获取取还车券抵扣（管理后台用券）
	 * @param costBaseDTO
	 * @param renterOrderCostRespDTO
	 * @param renterOrderReqVO
	 * @return OrderCouponDTO
	 */
	public OrderCouponDTO getGetCarFeeCouponForConsole(CostBaseDTO costBaseDTO, RenterOrderCostRespDTO renterOrderCostRespDTO, RenterOrderReqVO renterOrderReqVO) {
		MemAvailCouponRequestVO getCarFeeCouponReqVO = renterOrderService.buildMemAvailCouponRequestVO(renterOrderCostRespDTO, renterOrderReqVO);
		getCarFeeCouponReqVO.setDisCouponId(renterOrderReqVO.getGetCarFreeCouponId());
		OrderCouponDTO getCarFeeCoupon = renterOrderCalCostService.calGetAndReturnSrvCouponDeductInfo(getCarFeeCouponReqVO);
		if (getCarFeeCoupon != null) {
			getCarFeeCoupon.setOrderNo(costBaseDTO.getOrderNo());
			getCarFeeCoupon.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
		}
		return getCarFeeCoupon;
	}
	
	
	/**
	 * 获取取还车券抵扣
	 * @param costBaseDTO
	 * @param renterOrderCostRespDTO
	 * @param renterOrderReqVO
	 * @return OrderCouponDTO
	 */
	public OrderCouponDTO getGetCarFeeCoupon(CostBaseDTO costBaseDTO, RenterOrderCostRespDTO renterOrderCostRespDTO, RenterOrderReqVO renterOrderReqVO, OrderCouponEntity coupon) {
		CouponSettleRequest getCarFeeCouponReqVO = renterOrderService.getCouponSettleRequest(renterOrderCostRespDTO, renterOrderReqVO);
		OrderCouponDTO getCarFeeCoupon = renterOrderCalCostService.checkGetCarFreeCouponAvailable(getCarFeeCouponReqVO, coupon);
		if (getCarFeeCoupon != null) {
			getCarFeeCoupon.setOrderNo(costBaseDTO.getOrderNo());
			getCarFeeCoupon.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
		}
		return getCarFeeCoupon;
	}
	
	
	/**
	 * 获取取还车优惠券补贴
	 * @param costBaseDTO
	 * @param getCarFeeCoupon
	 * @return RenterOrderSubsidyDetailDTO
	 */
	public RenterOrderSubsidyDetailDTO getGetCarFeeCouponSubsidy(CostBaseDTO costBaseDTO, OrderCouponDTO getCarFeeCoupon) {
		if (getCarFeeCoupon == null) {
			return null;
		}
        //记录送取服务券
        getCarFeeCoupon.setOrderNo(costBaseDTO.getOrderNo());
        getCarFeeCoupon.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
        //补贴明细
        RenterOrderSubsidyDetailDTO getCarFeeCouponSubsidyInfo =
                renterOrderSubsidyDetailService.calGetCarFeeCouponSubsidyInfo(Integer.valueOf(costBaseDTO.getMemNo())
                        , getCarFeeCoupon);
        if (getCarFeeCouponSubsidyInfo != null) {
        	getCarFeeCouponSubsidyInfo.setOrderNo(costBaseDTO.getOrderNo());
            getCarFeeCouponSubsidyInfo.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
        }
        return getCarFeeCouponSubsidyInfo;
	}
	
	
	/**
	 * 获取平台券抵扣（管理后台用券）
	 * @param costBaseDTO
	 * @param renterOrderCostRespDTO
	 * @param renterOrderReqVO
	 * @param getCarFeeCoupon
	 * @param surplusRentAmt
	 * @return OrderCouponDTO
	 */
	public OrderCouponDTO getPlatformCouponForConsole(CostBaseDTO costBaseDTO, RenterOrderCostRespDTO renterOrderCostRespDTO, RenterOrderReqVO renterOrderReqVO, OrderCouponDTO getCarFeeCoupon, Integer surplusRentAmt) {
		MemAvailCouponRequestVO platformCouponReqVO = renterOrderService.buildMemAvailCouponRequestVO(renterOrderCostRespDTO,
                renterOrderReqVO);
        platformCouponReqVO.setDisCouponId(renterOrderReqVO.getDisCouponIds());
        if (getCarFeeCoupon != null) {
        	platformCouponReqVO.setSrvGetCost(0);
        	platformCouponReqVO.setSrvReturnCost(0);
        }
        platformCouponReqVO.setRentAmt(surplusRentAmt);
        OrderCouponDTO platfromCoupon = renterOrderCalCostService.calPlatformCouponDeductInfo(platformCouponReqVO);
        if (platfromCoupon != null) {
        	platfromCoupon.setOrderNo(costBaseDTO.getOrderNo());
        	platfromCoupon.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
        }
        return platfromCoupon;
	}
	
	
	/**
	 * 获取平台券抵扣
	 * @param costBaseDTO
	 * @param renterOrderCostRespDTO
	 * @param renterOrderReqVO
	 * @param getCarFeeCoupon
	 * @param surplusRentAmt
	 * @return OrderCouponDTO
	 */
	public OrderCouponDTO getPlatformCoupon(CostBaseDTO costBaseDTO, RenterOrderCostRespDTO renterOrderCostRespDTO, RenterOrderReqVO renterOrderReqVO, OrderCouponDTO getCarFeeCoupon, Integer surplusRentAmt, OrderCouponEntity coupon) {
		CouponSettleRequest platformCouponReqVO = renterOrderService.getCouponSettleRequest(renterOrderCostRespDTO,
                renterOrderReqVO);
        if (getCarFeeCoupon != null) {
        	platformCouponReqVO.setSrvGetCost(0);
        	platformCouponReqVO.setSrvReturnCost(0);
        }
        platformCouponReqVO.setRentAmt(surplusRentAmt);
        OrderCouponDTO platfromCoupon = renterOrderCalCostService.checkCouponAvailable(platformCouponReqVO, coupon);
        if (platfromCoupon != null) {
        	platfromCoupon.setOrderNo(costBaseDTO.getOrderNo());
        	platfromCoupon.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
        }
        return platfromCoupon;
	}
	
	
	/**
	 * 获取平台券补贴
	 * @param costBaseDTO
	 * @param platformCoupon
	 * @return RenterOrderSubsidyDetailDTO
	 */
	public RenterOrderSubsidyDetailDTO getPlatformCouponSubsidy(CostBaseDTO costBaseDTO, OrderCouponDTO platformCoupon) {
		//补贴明细
        RenterOrderSubsidyDetailDTO platformCouponSubsidyInfo =
                renterOrderSubsidyDetailService.calPlatformCouponSubsidyInfo(Integer.valueOf(costBaseDTO.getMemNo()), platformCoupon);
        if (platformCouponSubsidyInfo == null) {
        	return null;
        }
        platformCouponSubsidyInfo.setOrderNo(costBaseDTO.getOrderNo());
        platformCouponSubsidyInfo.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
        return platformCouponSubsidyInfo;
	}
	
	/**
	 * 获取凹凸币补贴
	 * @param modifyOrderDTO
	 * @param rentAmt
	 * @param surplusRentAmt
	 * @return RenterOrderSubsidyDetailDTO
	 */
	public RenterOrderSubsidyDetailDTO getAutoCoinSubsidy(ModifyOrderDTO modifyOrderDTO, Integer rentAmt, Integer surplusRentAmt) {
		// 是否使用凹凸币
		Integer userAutoCoinFlag = modifyOrderDTO.getUserCoinFlag();
		if (userAutoCoinFlag == null || !userAutoCoinFlag.equals(1)) {
			return null;
		}
		// 凹凸币能抵扣的最大金额
		Integer totalAutoCoinAmt = accountRenterCostCoinService.getUserCoinTotalAmt(modifyOrderDTO.getMemNo(), modifyOrderDTO.getOrderNo());
		// 计算凹凸币补贴
		RenterOrderSubsidyDetailDTO autoCoinSubsidy = autoCoinCostCalService.calAutoCoinDeductInfo(rentAmt, surplusRentAmt, totalAutoCoinAmt, modifyOrderDTO.getMemNo());
		if (autoCoinSubsidy != null) {
			autoCoinSubsidy.setOrderNo(modifyOrderDTO.getOrderNo());
			autoCoinSubsidy.setRenterOrderNo(modifyOrderDTO.getRenterOrderNo());
		}
		return autoCoinSubsidy;
	}
	
	
	
	/**
	 * 对象转化RenterOrderReqVO
	 * @param modifyOrderDTO
	 * @param renterMemberDTO
	 * @param renterGoodsDetailDTO
	 * @param orderEntity
	 * @return RenterOrderReqVO
	 */
	public RenterOrderReqVO convertToRenterOrderReqVO(ModifyOrderDTO modifyOrderDTO, RenterMemberDTO renterMemberDTO,
                                                      RenterGoodsDetailDTO renterGoodsDetailDTO,
                                                      OrderEntity orderEntity,
                                                      CarRentTimeRangeDTO carRentTimeRangeResVO) {
		RenterOrderReqVO renterOrderReqVO = new RenterOrderReqVO();
		renterOrderReqVO.setAbatement(modifyOrderDTO.getAbatementFlag() == null ? 0 :
                modifyOrderDTO.getAbatementFlag());
		renterOrderReqVO.setCarLat(renterGoodsDetailDTO.getCarShowLat());
		renterOrderReqVO.setCarLon(renterGoodsDetailDTO.getCarShowLon());
		renterOrderReqVO.setCarRealLat(renterGoodsDetailDTO.getCarRealLat());
		renterOrderReqVO.setCarRealLon(renterGoodsDetailDTO.getCarRealLon());
		renterOrderReqVO.setCarShowLat(renterGoodsDetailDTO.getCarShowLat());
		renterOrderReqVO.setCarShowLon(renterGoodsDetailDTO.getCarShowLon());
		renterOrderReqVO.setCarOwnerCouponNo(modifyOrderDTO.getCarOwnerCouponId());
		renterOrderReqVO.setCertificationTime(renterMemberDTO.getCertificationTime());
		renterOrderReqVO.setCityCode(orderEntity.getCityCode());
		renterOrderReqVO.setDisCouponIds(modifyOrderDTO.getPlatformCouponId());
		renterOrderReqVO.setDriverIds(modifyOrderDTO.getDriverIds());
		renterOrderReqVO.setEntryCode(orderEntity.getEntryCode());
		if (carRentTimeRangeResVO != null) {
			// 提前分钟
			renterOrderReqVO.setGetCarBeforeTime(carRentTimeRangeResVO.getGetMinutes());
			// 延后分钟
			renterOrderReqVO.setReturnCarAfterTime(carRentTimeRangeResVO.getReturnMinutes());
		}
		
		renterOrderReqVO.setGetCarFreeCouponId(modifyOrderDTO.getSrvGetReturnCouponId());
		renterOrderReqVO.setGuidPrice(renterGoodsDetailDTO.getCarGuidePrice());
		renterOrderReqVO.setInmsrp(renterGoodsDetailDTO.getCarInmsrp());
		renterOrderReqVO.setLabelIds(renterGoodsDetailDTO.getLabelIds());
		renterOrderReqVO.setMemNo(modifyOrderDTO.getMemNo());
		renterOrderReqVO.setOrderNo(modifyOrderDTO.getOrderNo());
		renterOrderReqVO.setRenterGoodsPriceDetailDTOList(renterGoodsDetailDTO.getRenterGoodsPriceDetailDTOList());
		renterOrderReqVO.setRenterOrderNo(modifyOrderDTO.getRenterOrderNo());
		renterOrderReqVO.setRentTime(modifyOrderDTO.getRentTime());
		renterOrderReqVO.setRevertTime(modifyOrderDTO.getRevertTime());
		renterOrderReqVO.setSource(orderEntity.getSource());
		renterOrderReqVO.setSrvGetFlag(modifyOrderDTO.getSrvGetFlag());
		renterOrderReqVO.setSrvGetLat(modifyOrderDTO.getGetCarLat());
		renterOrderReqVO.setSrvGetLon(modifyOrderDTO.getGetCarLon());
		renterOrderReqVO.setSrvReturnFlag(modifyOrderDTO.getSrvReturnFlag());
		renterOrderReqVO.setSrvReturnLat(modifyOrderDTO.getRevertCarLat());
		renterOrderReqVO.setSrvReturnLon(modifyOrderDTO.getRevertCarLon());
		renterOrderReqVO.setUseAutoCoin(modifyOrderDTO.getUserCoinFlag());
		renterOrderReqVO.setIsNew((renterMemberDTO.getIsNew() != null && renterMemberDTO.getIsNew() == 1) ? true:false);
		if (orderEntity.getCategory() != null) {
			renterOrderReqVO.setOrderCategory(String.valueOf(orderEntity.getCategory()));
		}
		if (renterGoodsDetailDTO.getCarNo() != null) {
			renterOrderReqVO.setCarNo(String.valueOf(renterGoodsDetailDTO.getCarNo()));
		}
		renterOrderReqVO.setSeatNum(renterGoodsDetailDTO.getSeatNum());
		renterOrderReqVO.setTyreInsurFlag(modifyOrderDTO.getTyreInsurFlag());
		renterOrderReqVO.setDriverInsurFlag(modifyOrderDTO.getDriverInsurFlag());
		renterOrderReqVO.setDriverScore(renterMemberDTO.getDriverScore());
		renterOrderReqVO.setDistributionMode(modifyOrderDTO.getDistributionMode());
		renterOrderReqVO.setCarLevel(renterGoodsDetailDTO.getCarLevel());
		return renterOrderReqVO;
	}
	
	
	/**
	 * 保存配送订单信息
	 * @param modifyOrderDTO
	 */
	public void saveRenterDelivery(ModifyOrderDTO modifyOrderDTO) {
		// 修改项
		/*
		 * List<OrderChangeItemDTO> changeItemList = modifyOrderDTO.getChangeItemList();
		 * List<String> changeCodeList =
		 * modifyOrderConfirmService.listChangeCode(changeItemList); if (changeCodeList
		 * != null && !changeCodeList.isEmpty() &&
		 * (changeCodeList.contains(OrderChangeItemEnum.MODIFY_SRVGETFLAG.getCode()) ||
		 * changeCodeList.contains(OrderChangeItemEnum.MODIFY_SRVRETURNFLAG.getCode())))
		 * { return; }
		 */
		/*
		 * if (modifyOrderDTO.getTransferFlag() != null &&
		 * modifyOrderDTO.getTransferFlag()) { // 换车操作 return; }
		 */
		UpdateOrderDeliveryVO updateFlowOrderVO = new UpdateOrderDeliveryVO();
		// 配送地址
		RenterDeliveryAddrDTO deliveryAddr = getRenterDeliveryAddrDTO(modifyOrderDTO);
		updateFlowOrderVO.setRenterDeliveryAddrDTO(deliveryAddr);
		// 配送订单
		Map<Integer,OrderDeliveryDTO> deliveryMap = getOrderDeliveryDTO(modifyOrderDTO);
		updateFlowOrderVO.setOrderDeliveryDTO(deliveryMap.get(SrvGetReturnEnum.SRV_GET_TYPE.getCode()));
		// 保存配送订单信息
		deliveryCarService.updateFlowOrderInfo(updateFlowOrderVO);
		updateFlowOrderVO.setOrderDeliveryDTO(deliveryMap.get(SrvGetReturnEnum.SRV_RETURN_TYPE.getCode()));
		// 保存配送订单信息
		deliveryCarService.updateFlowOrderInfo(updateFlowOrderVO);
	}
	
	/**
	 * 封装RenterDeliveryAddrDTO
	 * @param modifyOrderDTO
	 * @return RenterDeliveryAddrDTO
	 */
	public RenterDeliveryAddrDTO getRenterDeliveryAddrDTO(ModifyOrderDTO modifyOrderDTO) {
		if (modifyOrderDTO == null) {
			return null;
		}
		RenterGoodsDetailDTO renterGoodsDetailDTO = modifyOrderDTO.getRenterGoodsDetailDTO();
		String getCarAddr = modifyOrderDTO.getGetCarAddress();
		String getCarLat = modifyOrderDTO.getGetCarLat();
		String getCarLon = modifyOrderDTO.getGetCarLon();
		String returnCarAddr = modifyOrderDTO.getRevertCarAddress();
		String returnCarLat = modifyOrderDTO.getRevertCarLat();
		String returnCarLon = modifyOrderDTO.getRevertCarLon();
		if (renterGoodsDetailDTO != null) {
			if (modifyOrderDTO.getSrvGetFlag() == null || modifyOrderDTO.getSrvGetFlag() == 0) {
				getCarAddr = renterGoodsDetailDTO.getCarRealAddr();
				getCarLat = renterGoodsDetailDTO.getCarRealLat();
				getCarLon = renterGoodsDetailDTO.getCarRealLon();
			}
			if (modifyOrderDTO.getSrvReturnFlag() == null || modifyOrderDTO.getSrvReturnFlag() == 0) {
				returnCarAddr = renterGoodsDetailDTO.getCarRealAddr();
				returnCarLat = renterGoodsDetailDTO.getCarRealLat();
				returnCarLon = renterGoodsDetailDTO.getCarRealLon();
			}
		}
		RenterDeliveryAddrDTO deliveryAddrDTO = new RenterDeliveryAddrDTO();
		deliveryAddrDTO.setOrderNo(modifyOrderDTO.getOrderNo());
		deliveryAddrDTO.setRenterOrderNo(modifyOrderDTO.getRenterOrderNo());
		deliveryAddrDTO.setActGetCarAddr(getCarAddr);
		deliveryAddrDTO.setActGetCarLat(getCarLat);
		deliveryAddrDTO.setActGetCarLon(getCarLon);
		deliveryAddrDTO.setActReturnCarAddr(returnCarAddr);
		deliveryAddrDTO.setActReturnCarLat(returnCarLat);
		deliveryAddrDTO.setActReturnCarLon(returnCarLon);
		deliveryAddrDTO.setExpGetCarAddr(getCarAddr);
		deliveryAddrDTO.setExpGetCarLat(getCarLat);
		deliveryAddrDTO.setExpGetCarLon(getCarLon);
		deliveryAddrDTO.setExpReturnCarAddr(returnCarAddr);
		deliveryAddrDTO.setExpReturnCarLat(returnCarLat);
		deliveryAddrDTO.setExpReturnCarLon(returnCarLon);
		return deliveryAddrDTO;
	}
	
	/**
	 * 封装 Map<Integer,OrderDeliveryDTO>
	 * @param modifyOrderDTO
	 * @return Map<Integer,OrderDeliveryDTO>
	 */
	public Map<Integer,OrderDeliveryDTO> getOrderDeliveryDTO(ModifyOrderDTO modifyOrderDTO) {
		Map<Integer,OrderDeliveryDTO> delivMap = new HashMap<Integer, OrderDeliveryDTO>();
		if (modifyOrderDTO == null) {
			return delivMap;
		}
		// 商品信息
		RenterGoodsDetailDTO renterGoodsDetailDTO = modifyOrderDTO.getRenterGoodsDetailDTO();
		// 提前延后时间
		CarRentTimeRangeDTO carRentTimeRangeResVO = modifyOrderDTO.getCarRentTimeRangeResVO();
		OrderDeliveryDTO getDelivery = new OrderDeliveryDTO();
		getDelivery.setRentTime(modifyOrderDTO.getRentTime());
		getDelivery.setRevertTime(modifyOrderDTO.getRevertTime());
		getDelivery.setRenterGetReturnAddr(modifyOrderDTO.getGetCarAddress());
		getDelivery.setRenterGetReturnAddrLat(modifyOrderDTO.getGetCarLat());
		getDelivery.setRenterGetReturnAddrLon(modifyOrderDTO.getGetCarLon());
		getDelivery.setOrderNo(modifyOrderDTO.getOrderNo());
		getDelivery.setRenterOrderNo(modifyOrderDTO.getRenterOrderNo());
		getDelivery.setType(SrvGetReturnEnum.SRV_GET_TYPE.getCode());
		if (carRentTimeRangeResVO != null && carRentTimeRangeResVO.getGetMinutes() != null) {
			getDelivery.setAheadOrDelayTime(carRentTimeRangeResVO.getGetMinutes());
			getDelivery.setAheadOrDelayLocalDateTime(carRentTimeRangeResVO.getAdvanceStartDate());
		}
		if (modifyOrderDTO.getSrvGetFlag() != null && modifyOrderDTO.getSrvGetFlag() == 1) {
			getDelivery.setIsNotifyRenyun(1);
		} else {
			getDelivery.setIsNotifyRenyun(0);
			getDelivery.setRenterGetReturnAddr(renterGoodsDetailDTO.getCarShowAddr());
			getDelivery.setRenterGetReturnAddrLat(renterGoodsDetailDTO.getCarShowLat());
			getDelivery.setRenterGetReturnAddrLon(renterGoodsDetailDTO.getCarShowLon());
		}
		
		delivMap.put(SrvGetReturnEnum.SRV_GET_TYPE.getCode(), getDelivery);
		OrderDeliveryDTO returnDelivery = new OrderDeliveryDTO();
		returnDelivery.setRentTime(modifyOrderDTO.getRentTime());
		returnDelivery.setRevertTime(modifyOrderDTO.getRevertTime());
		returnDelivery.setRenterGetReturnAddr(modifyOrderDTO.getRevertCarAddress());
		returnDelivery.setRenterGetReturnAddrLat(modifyOrderDTO.getRevertCarLat());
		returnDelivery.setRenterGetReturnAddrLon(modifyOrderDTO.getRevertCarLon());
		returnDelivery.setOrderNo(modifyOrderDTO.getOrderNo());
		returnDelivery.setRenterOrderNo(modifyOrderDTO.getRenterOrderNo());
		returnDelivery.setType(SrvGetReturnEnum.SRV_RETURN_TYPE.getCode());
		if (carRentTimeRangeResVO != null && carRentTimeRangeResVO.getReturnMinutes() != null) {
			returnDelivery.setAheadOrDelayTime(carRentTimeRangeResVO.getReturnMinutes());
			returnDelivery.setAheadOrDelayLocalDateTime(carRentTimeRangeResVO.getDelayEndDate());
		}
		if (modifyOrderDTO.getSrvReturnFlag() != null && modifyOrderDTO.getSrvReturnFlag() == 1) {
			returnDelivery.setIsNotifyRenyun(1);
		} else {
			returnDelivery.setIsNotifyRenyun(0);
			returnDelivery.setRenterGetReturnAddr(renterGoodsDetailDTO.getCarShowAddr());
			returnDelivery.setRenterGetReturnAddrLat(renterGoodsDetailDTO.getCarShowLat());
			returnDelivery.setRenterGetReturnAddrLon(renterGoodsDetailDTO.getCarShowLon());
		}
		delivMap.put(SrvGetReturnEnum.SRV_RETURN_TYPE.getCode(), returnDelivery);
		return delivMap;
	}
	
	
	/**
	 * 保存区间配送信息
	 * @param modifyOrderDTO
	 */
	public void saveSectionDelivery(ModifyOrderDTO modifyOrderDTO) {
		Integer distributionMode = modifyOrderDTO.getDistributionMode();
		if (distributionMode == null) {
			return;
		}
		OrderReqVO orderReqVO = new OrderReqVO();
		orderReqVO.setDistributionMode(distributionMode);
		orderReqVO.setSrvGetFlag(modifyOrderDTO.getSrvGetFlag());
		orderReqVO.setSrvReturnFlag(modifyOrderDTO.getSrvReturnFlag());
		orderReqVO.setCityCode(modifyOrderDTO.getCityCode());
		RenterOrderDeliveryMode mode = modifyOrderDTO.getInitDeliveryMode();
		if (mode != null) {
			List<String> changeItemList = modifyOrderConfirmService.listChangeCode(modifyOrderDTO.getChangeItemList());
			if (changeItemList != null && changeItemList.contains(OrderChangeItemEnum.MODIFY_RENTTIME.getCode())) {
				mode.setRenterProposalGetTime(null);
				mode.setOwnerProposalGetTime(null);
			}
			if (changeItemList != null && changeItemList.contains(OrderChangeItemEnum.MODIFY_REVERTTIME.getCode())) {
				mode.setRenterProposalReturnTime(null);
				mode.setOwnerProposalReturnTime(null);
			}
			
		}
		submitOrderService.saveSectionDelivery(orderReqVO, modifyOrderDTO.getOrderNo(), modifyOrderDTO.getRenterOrderNo(),mode);
	}
	
}
