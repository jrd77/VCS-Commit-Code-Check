package com.atzuche.order.settle.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.atzuche.order.accountrenterdetain.service.notservice.AccountRenterDetainDetailNoTService;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostEntity;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostSettleDetailEntity;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositCostSettleDetailNoTService;
import com.atzuche.order.cashieraccount.service.CashierWzSettleService;
import com.atzuche.order.commons.NumberUtils;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import com.atzuche.order.mq.common.base.BaseProducer;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.renterwz.entity.RenterOrderWzCostDetailEntity;
import com.atzuche.order.renterwz.entity.RenterOrderWzSettleFlagEntity;
import com.atzuche.order.renterwz.service.RenterOrderWzSettleFlagService;
import com.atzuche.order.settle.exception.OrderSettleFlatAccountException;
import com.atzuche.order.settle.service.notservice.OrderWzSettleNoTService;
import com.atzuche.order.settle.vo.req.RentCostsWz;
import com.atzuche.order.settle.vo.req.SettleOrdersAccount;
import com.atzuche.order.settle.vo.req.SettleOrdersWz;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.doc.util.StringUtil;
import com.autoyol.event.rabbit.neworder.NewOrderMQActionEventEnum;
import com.autoyol.event.rabbit.neworder.OrderWzSettlementMq;
import com.dianping.cat.Cat;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderWzSettleNewService {
	@Autowired
	private CashierWzSettleService cashierWzSettleService;
	@Autowired
	private AccountRenterWzDepositCostSettleDetailNoTService accountRenterWzDepositCostSettleDetailNoTService;
	@Autowired
	private OrderWzSettleNoTService orderWzSettleNoTService;
	@Autowired
	private BaseProducer baseProducer;
	// 更新订单状态和订单流转
	@Autowired
	private OrderStatusService orderStatusService;

	// 查询租客有效订单
	@Autowired
	private RenterOrderService renterOrderService;
	// 判断是否暂扣
	@Autowired
	private AccountRenterDetainDetailNoTService accountRenterDetainDetailNoTService;
	@Autowired
	private RenterOrderWzSettleFlagService renterOrderWzSettleFlagService;

	/**
	 * 初始化结算对象
	 * 
	 * @param orderNo
	 */
	public SettleOrdersWz initSettleOrders(String orderNo) {
		// 1 校验参数
		if (StringUtil.isBlank(orderNo)) {
			throw new OrderSettleFlatAccountException();
		}
		RenterOrderEntity renterOrder = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
		if (Objects.isNull(renterOrder) || Objects.isNull(renterOrder.getRenterOrderNo())) {
			throw new OrderSettleFlatAccountException();
		}

		// 2 校验订单状态 以及是否存在 理赔暂扣 存在不能进行结算 并CAT告警
		this.check(renterOrder);
		// 3 初始化数据

		// 3.1获取租客子订单 和 租客会员号
		String renterOrderNo = renterOrder.getRenterOrderNo();
		String renterMemNo = renterOrder.getRenterMemNo();

		SettleOrdersWz settleOrdersWz = new SettleOrdersWz();
		settleOrdersWz.setOrderNo(orderNo);
		settleOrdersWz.setRenterOrderNo(renterOrderNo);
		settleOrdersWz.setRenterMemNo(renterMemNo);
		settleOrdersWz.setRenterOrder(renterOrder);
		return settleOrdersWz;
	}

	/**
	 * 校验是否可以结算 校验订单状态 以及是否存在 理赔暂扣 存在不能进行结算 并CAT告警
	 * 
	 * @param renterOrder
	 */
	public void check(RenterOrderEntity renterOrder) {
		// 1 先查询 发现 有结算数据停止结算 手动处理
		this.checkIsSettle(renterOrder.getOrderNo());
		
		// 2 校验是否存在 理赔 存在不结算 这个跟违章是一起的。
		boolean isClaim = cashierWzSettleService.getOrderClaim(renterOrder.getOrderNo());
		if (isClaim) {
			throw new RuntimeException("租客存在理赔信息不能结算");
		}
		/*
		 * 3.根据费用编码来判断是否暂扣。
		 */
		boolean isDetain = accountRenterDetainDetailNoTService.isWzDepositDetain(renterOrder.getOrderNo());
		if (isDetain) {
			throw new RuntimeException("租客存在暂扣信息不能结算");
		}
		
		//4.是否有仁云的推送数据
		// com.atzuche.order.renterwz.service.RenterOrderWzCostDetailService#querySettleInfoByOrder
		List<RenterOrderWzSettleFlagEntity> settleInfos = renterOrderWzSettleFlagService.getIllegalSettleInfosByOrderNo(renterOrder.getOrderNo());
		// 过滤未结算和结算失败的
		if (CollectionUtils.isEmpty(settleInfos)) {
			// 无法结算
			throw new RuntimeException("租客未找到违章记录信息不能结算");
		}
	}

	/**
	 * 先查询 发现 有结算数据停止结算 手动处理
	 * 
	 * @param orderNo
	 */
	public void checkIsSettle(String orderNo) {
		// 1 订单校验是否可以结算
		OrderStatusEntity orderStatus = orderStatusService.getByOrderNo(orderNo);
		if (OrderStatusEnum.TO_WZ_SETTLE.getStatus() != orderStatus.getStatus() || SettleStatusEnum.SETTLEING.getCode() != orderStatus.getWzSettleStatus()) {
			throw new RuntimeException("租客订单状态不是待结算，不能结算");
		}
		
		// 违章
		/*
		 * 违章费用结算明细表 account_renter_wz_deposit_cost_settle_detail
		 */
		List<AccountRenterWzDepositCostSettleDetailEntity> accountRenterWzDepositCostSettleDetailEntitys = accountRenterWzDepositCostSettleDetailNoTService.getAccountRenterWzDepositCostSettleDetail(orderNo);

		/**
		 * todo 应该根据违章押金的部分的补贴和收益来处理，代收代付？？ 先注释掉。
		 */

		if (!CollectionUtils.isEmpty(accountRenterWzDepositCostSettleDetailEntitys)) {
			throw new RuntimeException("有违章结算数据停止结算");
		}

	}

	/**
	 * 车辆结算 校验费用落库等无实物操作
	 */
	public void settleOrderFirst(SettleOrdersWz settleOrders) {
		// 1 查询所有租客费用明细
		orderWzSettleNoTService.getRenterWzCostSettleDetail(settleOrders);
		log.info("wz OrderSettleService getRenterWzCostSettleDetail settleOrders [{}]", GsonUtils.toJson(settleOrders));
		Cat.logEvent("settleOrders", GsonUtils.toJson(settleOrders));

		RentCostsWz rentCosts = settleOrders.getRentCostsWz();//违章费用对象
		if (Objects.nonNull(rentCosts)) {
			// 1.1 查询违章费用

			List<RenterOrderWzCostDetailEntity> renterOrderWzCostDetails = rentCosts.getRenterOrderWzCostDetails();
			if (!CollectionUtils.isEmpty(renterOrderWzCostDetails)) {

				List<AccountRenterWzDepositCostSettleDetailEntity> accountRenterWzDepositCostSettleDetails = new ArrayList<AccountRenterWzDepositCostSettleDetailEntity>();

				List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetails = new ArrayList<AccountRenterCostSettleDetailEntity>();

				for (int i = 0; i < renterOrderWzCostDetails.size(); i++) {
					RenterOrderWzCostDetailEntity renterOrderWzCostDetail = renterOrderWzCostDetails.get(i);
					AccountRenterWzDepositCostSettleDetailEntity accountRenterWzDepositCostSettleDetail = new AccountRenterWzDepositCostSettleDetailEntity();
					// 赋值
					accountRenterWzDepositCostSettleDetail.setOrderNo(renterOrderWzCostDetail.getOrderNo());
					accountRenterWzDepositCostSettleDetail.setMemNo(String.valueOf(renterOrderWzCostDetail.getMemNo()));
					accountRenterWzDepositCostSettleDetail.setUniqueNo(String.valueOf(renterOrderWzCostDetail.getId()));
					accountRenterWzDepositCostSettleDetail.setPrice(renterOrderWzCostDetail.getAmount());
					accountRenterWzDepositCostSettleDetail.setWzAmt(renterOrderWzCostDetail.getAmount());
					accountRenterWzDepositCostSettleDetail.setUnit(1); // 默认1

					accountRenterWzDepositCostSettleDetails.add(accountRenterWzDepositCostSettleDetail);

					// 封装费用
					// wzTotalCost-todo
					AccountRenterCostSettleDetailEntity entity = new AccountRenterCostSettleDetailEntity();
					BeanUtils.copyProperties(renterOrderWzCostDetail, entity);
					entity.setType(10); // 代表违章
					entity.setAmt(NumberUtils.convertNumberToFushu(renterOrderWzCostDetail.getAmount())); // 显示负数,代表的是支出
					entity.setCostCode(renterOrderWzCostDetail.getCostCode());
					entity.setCostDetail(renterOrderWzCostDetail.getCostDesc());
					accountRenterCostSettleDetails.add(entity);

				}

				// 同时让租客费用总表里面记录。

				if (accountRenterWzDepositCostSettleDetails.size() > 0) {
					// 落库
					cashierWzSettleService.insertAccountRenterWzDepoistCostSettleDetails(accountRenterWzDepositCostSettleDetails);
					log.info("租客违章费用结算明细落库，orderNo=[{}]",settleOrders.getOrderNo());
				}
				
				// wzTotalCost-todo
				if (accountRenterCostSettleDetails.size() > 0) {
					cashierWzSettleService.insertAccountRenterCostSettleDetails(accountRenterCostSettleDetails);
					log.info("租客费用结算明细总表落库(flag默认为10)，orderNo=[{}]",settleOrders.getOrderNo());
				}
			}
		}

	}

	/**
	 * 结算逻辑
	 */
	@Transactional(rollbackFor = Exception.class)
	public void settleOrderAfter(SettleOrdersWz settleOrders) {
		// 7.1 违章费用 总费用 信息落库 并返回最新租车费用 实付
		/**
		 * 违章费用总表及其结算总表 account_renter_wz_deposit_cost
		 */
		AccountRenterWzDepositCostEntity accountRenterCostSettle = cashierWzSettleService.updateWzRentSettleCost(settleOrders.getOrderNo(), settleOrders.getRenterMemNo(), settleOrders.getRenterOrderCostWz());
		log.info("OrderSettleService updateRentSettleCost 更新违章费用总表的应收，实收，欠款。 [{}]", GsonUtils.toJson(accountRenterCostSettle));
		Cat.logEvent("updateWzRentSettleCost", GsonUtils.toJson(accountRenterCostSettle));

		// 8 获取租客 实付 违章押金
		int wzDepositAmt = cashierWzSettleService.getSurplusWZDepositCostAmt(settleOrders.getOrderNo(),settleOrders.getRenterMemNo());
		log.info("(统计违章押金的资金明细表)当前订单和会员的累计支付的违章押金金额 [{}], orderNo=[{}],renterMemNo=[{}]",wzDepositAmt,settleOrders.getOrderNo(),settleOrders.getRenterMemNo());
		
		SettleOrdersAccount settleOrdersAccount = new SettleOrdersAccount();
		BeanUtils.copyProperties(settleOrders, settleOrdersAccount);
		settleOrdersAccount.setRentCostAmtFinal(accountRenterCostSettle.getYingfuAmt()); // 应付 违章费用
		settleOrdersAccount.setRentCostPayAmt(0); // 实付 0,没有违章押金的单独支付
		settleOrdersAccount.setDepositAmt(wzDepositAmt);
		settleOrdersAccount.setDepositSurplusAmt(wzDepositAmt);

		// 按0处理，违章费用没有单独支付。
		int rentCostSurplusAmt = 0;// (accountRenterCostSettle.getYingfuAmt() +
									// accountRenterCostSettle.getShifuAmt())<=0?0:(accountRenterCostSettle.getRentAmt()
									// + accountRenterCostSettle.getShifuAmt());
		settleOrdersAccount.setRentCostSurplusAmt(rentCostSurplusAmt);

		log.info("(各费用赋值，应付，实收，违章押金，剩余违章押金) OrderSettleService settleOrderAfter settleOrdersAccount one [{}]",GsonUtils.toJson(settleOrdersAccount));
		Cat.logEvent("settleOrdersAccount", GsonUtils.toJson(settleOrdersAccount));

		OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
		orderStatusDTO.setOrderNo(settleOrders.getOrderNo());
		/// add
		orderStatusDTO.setWzSettleTime(LocalDateTime.now());
		orderStatusDTO.setStatus(OrderStatusEnum.TO_CLAIM_SETTLE.getStatus());
		orderStatusDTO.setWzSettleStatus(SettleStatusEnum.SETTLED.getCode());

		// 1 租客违章费用 结余处理
		log.info("OrderSettleService wzCostSettle 抵扣违章费用或产生违章欠款。settleOrders [{}], settleOrdersAccount [{}]", GsonUtils.toJson(settleOrders),GsonUtils.toJson(settleOrdersAccount));
		orderWzSettleNoTService.wzCostSettle(settleOrders, settleOrdersAccount);
		
		
		log.info("OrderSettleService repayWzHistoryDebtRent 抵扣历史欠款。settleOrdersAccount [{}]", GsonUtils.toJson(settleOrdersAccount));
		// 2租客剩余违章押金 结余历史欠款
		orderWzSettleNoTService.repayWzHistoryDebtRent(settleOrdersAccount);
		
		
		log.info("OrderSettleService refundWzDepositAmt 退还违章押金。settleOrdersAccount [{}]", GsonUtils.toJson(settleOrdersAccount));
		// 3 违章押金 退还
		orderWzSettleNoTService.refundWzDepositAmt(settleOrdersAccount, orderStatusDTO);
		
		
		log.info("OrderSettleService 结算结束 settleOrdersAccount [{}],orderStatusDTO [{}]", GsonUtils.toJson(settleOrdersAccount),GsonUtils.toJson(orderStatusDTO));
		
		// 4 更新订单状态
		settleOrdersAccount.setOrderStatusDTO(orderStatusDTO);
		orderWzSettleNoTService.saveOrderStatusInfo(settleOrdersAccount);
		log.info("OrderSettleService settleOrdersDefinition settleOrdersAccount two [{}]",GsonUtils.toJson(settleOrdersAccount));
	}

	/**
	 * 订单违章结算成功事件
	 * 
	 * @param orderNo
	 */
	public void sendOrderWzSettleSuccessMq(String orderNo) {
		log.info("sendOrderWzSettleSuccessMq start [{}]", orderNo);
		OrderWzSettlementMq orderSettlementMq = new OrderWzSettlementMq();
		orderSettlementMq.setStatus(0);
		orderSettlementMq.setOrderNo(orderNo);
		OrderMessage orderMessage = OrderMessage.builder().build();
		orderMessage.setMessage(orderSettlementMq);
		// TODO 发短信
		log.info("sendOrderWzSettleSuccessMq remote start [{}] ,[{}] ", GsonUtils.toJson(orderMessage),
				NewOrderMQActionEventEnum.ORDER_WZ_SETTLEMENT_SUCCESS);
		baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_WZ_SETTLEMENT_SUCCESS.exchange,
				NewOrderMQActionEventEnum.ORDER_WZ_SETTLEMENT_SUCCESS.routingKey, orderMessage);
		log.info("sendOrderWzSettleSuccessMq remote end [{}]", orderNo);
	}

	/**
	 * 订单结算失败事件
	 * 
	 * @param orderNo
	 */
	public void sendOrderWzSettleFailMq(String orderNo) {
		log.info("sendOrderWzSettleFailMq start [{}]", orderNo);
		OrderWzSettlementMq orderSettlementMq = new OrderWzSettlementMq();
		orderSettlementMq.setStatus(1);
		orderSettlementMq.setOrderNo(orderNo);
		OrderMessage orderMessage = OrderMessage.builder().build();
		orderMessage.setMessage(orderSettlementMq);
		// TODO 发短信
		log.info("sendOrderWzSettleFailMq remote start [{}] ,[{}] ", GsonUtils.toJson(orderMessage),
				NewOrderMQActionEventEnum.ORDER_WZ_SETTLEMENT_FAIL);
		baseProducer.sendTopicMessage(NewOrderMQActionEventEnum.ORDER_WZ_SETTLEMENT_FAIL.exchange,
				NewOrderMQActionEventEnum.ORDER_WZ_SETTLEMENT_FAIL.routingKey, orderMessage);
		log.info("sendOrderWzSettleFailMq remote end [{}]", orderNo);

	}
	// ----------------------------------------------------------------------------------------------------------------------------------

}
