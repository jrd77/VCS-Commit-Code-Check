package com.atzuche.order.settle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.settle.service.notservice.OrderSettleNoTService;
import com.atzuche.order.settle.vo.req.OwnerCosts;
import com.atzuche.order.settle.vo.req.RentCosts;
import com.atzuche.order.settle.vo.req.SettleCancelOrdersAccount;
import com.atzuche.order.settle.vo.req.SettleOrders;
import com.atzuche.order.settle.vo.req.SettleOrdersDefinition;
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
    /**
     * 获取租客预结算数据 huangjing
     * @param orderNo
     */
    public RentCosts preRenterSettleOrder(String orderNo,String renterOrderNo) {
    	SettleOrders settleOrders =  orderSettleNoTService.preInitSettleOrders(orderNo,renterOrderNo,null);
    	 //3.4 查询所有租客费用明细
        orderSettleNoTService.getRenterCostSettleDetail(settleOrders);
        return settleOrders.getRentCosts();
    }
    
    /**
     * 获取租客预结算数据 huangjing
     * @param orderNo
     */
    public OwnerCosts preOwnerSettleOrder(String orderNo,String ownerOrderNo) {
    	SettleOrders settleOrders =  orderSettleNoTService.preInitSettleOrders(orderNo,null,ownerOrderNo);
        //3.5 查询所有车主费用明细 TODO 暂不支持 多个车主
    	orderSettleNoTService.getOwnerCostSettleDetail(settleOrders);
    	return settleOrders.getOwnerCosts();
    }
    /**
     * 车辆押金结算
     * 先注释调事务
     */
    public void settleOrder(String orderNo,OrderPayCallBack callBack) {
        log.info("OrderSettleService settleOrder orderNo [{}]",orderNo);
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "车俩结算服务");
        try {
            Cat.logEvent("settleOrder",orderNo);
            //1 初始化操作 校验操作
            SettleOrders settleOrders =  orderSettleNoTService.initSettleOrders(orderNo);
            log.info("OrderSettleService settleOrders settleOrders [{}]",GsonUtils.toJson(settleOrders));
            Cat.logEvent("settleOrders",GsonUtils.toJson(settleOrders));

            //2 无事务操作 查询租客车主费用明细 ，处理费用明细到 结算费用明细  并落库   然后平账校验
            SettleOrdersDefinition settleOrdersDefinition = orderSettleNewService.settleOrderFirst(settleOrders);
            log.info("OrderSettleService settleOrdersDefinition [{}]",GsonUtils.toJson(settleOrdersDefinition));
            Cat.logEvent("settleOrders",GsonUtils.toJson(settleOrdersDefinition));

            //3 事务操作结算主逻辑  //开启事务
            orderSettleNewService.settleOrder(settleOrders,settleOrdersDefinition,callBack);
            log.info("OrderSettleService settleOrdersenced [{}]",GsonUtils.toJson(settleOrdersDefinition));
            Cat.logEvent("settleOrdersenced",GsonUtils.toJson(settleOrdersDefinition));

            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            log.error("OrderSettleService settleOrder,e={},",e);
            OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
            orderStatusDTO.setOrderNo(orderNo);
            orderStatusDTO.setSettleStatus(SettleStatusEnum.SETTL_FAIL.getCode());
            orderStatusService.saveOrderStatusInfo(orderStatusDTO);
            t.setStatus(e);
            Cat.logError("结算失败  :{}",e);
            throw new RuntimeException("结算失败 ,不能结算");
        } finally {
            t.complete();
        }
        log.info("OrderPayCallBack payCallBack end " );
    }

    /**
     * 取消订单结算
     * @param orderNo
     * @return
     */
    @Transactional(rollbackFor=Exception.class)
    public boolean settleOrderCancel(String orderNo) {
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "取消订单结算服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "OrderSettleService.settleOrderCancel");
            Cat.logEvent(CatConstants.FEIGN_PARAM, orderNo);
            OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
            orderStatusDTO.setOrderNo(orderNo);
            // 1 取消订单初始化
            SettleOrders settleOrders =  orderSettleNoTService.initCancelSettleOrders(orderNo);
            Cat.logEvent("settleOrderCancel",GsonUtils.toJson(settleOrders));
            log.info("OrderPayCallBack settleOrderCancel settleOrders [{}] ",GsonUtils.toJson(settleOrders));
            //2 查询所有租客罚金明细  及 凹凸币补贴
            orderSettleNoTService.getCancelRenterCostSettleDetail(settleOrders);
            Cat.logEvent("settleOrders",GsonUtils.toJson(settleOrders));
            log.info("OrderPayCallBack settleOrderCancel settleOrders [{}] ",GsonUtils.toJson(settleOrders));
            //3 查询所有车主罚金明细
            orderSettleNoTService.getCancelOwnerCostSettleDetail(settleOrders);
            Cat.logEvent("settleOrdersFine",GsonUtils.toJson(settleOrders));
            log.info("OrderPayCallBack settleOrderCancel settleOrders [{}] ",GsonUtils.toJson(settleOrders));
            //4 查询 租客实际 付款金额（包含 租车费用，车俩押金，违章押金，钱包，罚金）
            SettleCancelOrdersAccount settleCancelOrdersAccount = orderSettleNoTService.initSettleCancelOrdersAccount(settleOrders);
            Cat.logEvent("settleCancelOrdersAccount",GsonUtils.toJson(settleCancelOrdersAccount));
            log.info("OrderPayCallBack settleCancelOrdersAccount settleCancelOrdersAccount [{}] ",GsonUtils.toJson(settleCancelOrdersAccount));

            //5 处理 租客 车主 平台 罚金收入
            orderSettleNoTService.handleIncomeFine(settleOrders,settleCancelOrdersAccount);
            Cat.logEvent("handleIncomeFine",GsonUtils.toJson(settleCancelOrdersAccount));
            log.info("OrderPayCallBack handleIncomeFine handleIncomeFine [{}] ",GsonUtils.toJson(settleCancelOrdersAccount));

            //6 车主罚金处理
            orderSettleNoTService.handleOwnerFine(settleOrders,settleCancelOrdersAccount);
            Cat.logEvent("handleOwnerFine",GsonUtils.toJson(settleCancelOrdersAccount));
            log.info("OrderPayCallBack handleOwnerFine settleCancelOrdersAccount [{}] ",GsonUtils.toJson(settleCancelOrdersAccount));
            //7 租客罚金处理
            orderSettleNoTService.handleRentFine(settleOrders,settleCancelOrdersAccount);
            Cat.logEvent("handleRentFine",GsonUtils.toJson(settleCancelOrdersAccount));
            log.info("OrderPayCallBack handleRentFine settleCancelOrdersAccount [{}] ",GsonUtils.toJson(settleCancelOrdersAccount));
            //8 租客还历史欠款
            orderSettleNoTService.repayHistoryDebtRentCancel(settleOrders,settleCancelOrdersAccount);
            Cat.logEvent("repayHistoryDebtRentCancel",GsonUtils.toJson(settleCancelOrdersAccount));
            log.info("OrderPayCallBack repayHistoryDebtRentCancel settleCancelOrdersAccount [{}] ",GsonUtils.toJson(settleCancelOrdersAccount));
            //9 租客金额 退还 包含 凹凸币，钱包 租车费用 押金 违章押金 退还 （优惠卷退还 TODO）
            orderSettleNoTService.refundCancelCost(settleOrders,settleCancelOrdersAccount,orderStatusDTO);
            Cat.logEvent("refundCancelCost",GsonUtils.toJson(settleCancelOrdersAccount));
            log.info("OrderPayCallBack refundCancelCost settleCancelOrdersAccount [{}] ",GsonUtils.toJson(settleCancelOrdersAccount));
            //10 修改订单状态表
            cashierService.saveCancelOrderStatusInfo(orderStatusDTO);

            log.info("OrderSettleService initSettleOrders settleOrders [{}]", GsonUtils.toJson(settleOrders));
            Cat.logEvent("settleOrders",GsonUtils.toJson(settleOrders));
        } catch (Exception e) {
            log.error("OrderSettleService settleOrderCancel,e={},",e);
            t.setStatus(e);
            Cat.logError("结算失败  :{}",e);
            throw new RuntimeException("结算失败 ,不能结算");
        } finally {
            t.complete();
        }
        return true;
    }
}
