package com.atzuche.order.settle.service;

import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleDetailEntity;
import com.atzuche.order.accountrenterdeposit.vo.res.AccountRenterDepositResVO;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostDetailEntity;
import com.atzuche.order.accountrenterwzdepost.vo.res.AccountRenterWZDepositResVO;
import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.service.CashierPayService;
import com.atzuche.order.cashieraccount.service.CashierQueryService;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.cashieraccount.service.CashierSettleService;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import com.atzuche.order.commons.service.OrderPayCallBack;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercost.entity.OrderSupplementDetailEntity;
import com.atzuche.order.rentercost.service.OrderSupplementDetailService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.settle.service.notservice.OrderSettleNoTService;
import com.atzuche.order.settle.vo.req.*;
import com.atzuche.order.settle.vo.res.RenterCostVO;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
    @Autowired private RenterOrderService renterOrderService;
    @Autowired private CashierSettleService cashierSettleService;
    @Autowired private CashierRefundApplyNoTService cashierRefundApplyNoTService;
    @Autowired private CashierPayService cashierPayService;
    @Autowired private CashierQueryService cashierQueryService;
    @Autowired private OrderSupplementDetailService orderSupplementDetailService;


    /**
     * 查询所以费用
     */
    public RenterCostVO getRenterCostByOrderNo(String orderNo){
        RenterOrderEntity renterOrder = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        Assert.notNull(renterOrder,"订单信息不存在");
        Assert.notNull(renterOrder.getRenterOrderNo(),"订单信息不存在");

        RenterCostVO vo = new RenterCostVO();
        vo.setOrderNo(orderNo);
        AccountRenterDepositResVO accountRenterDepositResVO = cashierService.getRenterDepositEntity(orderNo,renterOrder.getRenterMemNo());
        int rentWzDepositAmt = cashierSettleService.getSurplusWZDepositCostAmt(orderNo,renterOrder.getRenterMemNo());
        AccountRenterWZDepositResVO accountRenterWZDeposit = cashierService.getRenterWZDepositEntity(orderNo,renterOrder.getRenterMemNo());
        //车辆押金
        vo.setDepositCost(Math.abs(accountRenterDepositResVO.getSurplusDepositAmt()));
        vo.setDepositCostShifu(Math.abs(accountRenterDepositResVO.getShifuDepositAmt()));
        vo.setDepositCostYingfu(Math.abs(accountRenterDepositResVO.getYingfuDepositAmt()));
        //违章押金
        vo.setDepositWzCost(Math.abs(rentWzDepositAmt));
        vo.setDepositWzCostShifu(Math.abs(accountRenterWZDeposit.getShishouDeposit()));
        vo.setDepositWzCostYingFu(Math.abs(accountRenterWZDeposit.getYingshouDeposit()));
        RentCosts rentCosts = preRenterSettleOrder(orderNo, renterOrder.getRenterOrderNo());
        log.info("查询租客应收 getRenterCostByOrderNo rentCosts [{}]",GsonUtils.toJson(rentCosts));
        //租车费用
        if(Objects.nonNull(rentCosts)){
            //应付
            int yingfuAmt =  orderSettleNewService.getYingfuRenterCost(rentCosts);
            List<AccountRenterCostDetailEntity> renterCostDetails = cashierQueryService.getRenterCostDetails(orderNo);
            // 实付
            int renterCostAmtEd = cashierQueryService.getRenterCost(orderNo,renterOrder.getRenterMemNo());
            if(!CollectionUtils.isEmpty(renterCostDetails)){
                List<OrderSupplementDetailEntity> orderSupplementDetails = orderSupplementDetailService.listOrderSupplementDetailByOrderNo(orderNo);
                if(!CollectionUtils.isEmpty(orderSupplementDetails)){
                    int renterCostBufuShifu = orderSupplementDetails.stream().filter(obj ->{
                        return Objects.nonNull(obj.getOpStatus()) && obj.getOpStatus()==1&&
                                Objects.nonNull(obj.getCashType()) && obj.getCashType()==1&&
                                Objects.nonNull(obj.getPayFlag()) && obj.getPayFlag()==3
                                ;
                    }).mapToInt(OrderSupplementDetailEntity::getAmt).sum();
                    int renterCostBufuYingfu = orderSupplementDetails.stream().filter(obj ->{
                        return Objects.nonNull(obj.getOpStatus()) && obj.getOpStatus()==1&&
                                Objects.nonNull(obj.getCashType()) && obj.getCashType()==1&&
                                Objects.nonNull(obj.getPayFlag()) && obj.getPayFlag()==1&&
                                Objects.nonNull(obj.getPayFlag()) && obj.getPayFlag()==2&&
                                Objects.nonNull(obj.getPayFlag()) && obj.getPayFlag()==3&&
                                Objects.nonNull(obj.getPayFlag()) && obj.getPayFlag()==4&&
                                Objects.nonNull(obj.getPayFlag()) && obj.getPayFlag()==5
                                ;
                    }).mapToInt(OrderSupplementDetailEntity::getAmt).sum();
                    vo.setRenterCostBufu(renterCostBufuYingfu);
                    vo.setRenterCostShishou(renterCostBufuShifu);
                }
            }
            vo.setRenterCostYingshou(Math.abs(yingfuAmt));
            vo.setRenterCostShishou(Math.abs(renterCostAmtEd));
            int renterCost = yingfuAmt + renterCostAmtEd;
            vo.setRenterCost(renterCost>0?renterCost:0);
        }
        List<CashierRefundApplyEntity> cashierRefundApplys = cashierRefundApplyNoTService.getRefundApplyByOrderNo(orderNo);
        if(!CollectionUtils.isEmpty(cashierRefundApplys)){
           // 获取实退 租车费用
            int renterCostReal =  cashierRefundApplys.stream().filter(obj ->{
                return DataPayKindConstant.RENT_AMOUNT.equals(obj.getPayKind()) || DataPayKindConstant.RENT_INCREMENT.equals(obj.getPayKind());
            }).mapToInt(CashierRefundApplyEntity::getAmt).sum();
            vo.setRenterCostReal(renterCostReal);
            // 获取实退 车俩押金
            int depositCostReal =  cashierRefundApplys.stream().filter(obj ->{
                return DataPayKindConstant.RENT.equals(obj.getPayKind());
            }).mapToInt(CashierRefundApplyEntity::getAmt).sum();
            vo.setDepositCostReal(depositCostReal);
            // 获取实退 违章押金
            int depositWzCostReal =  cashierRefundApplys.stream().filter(obj ->{
                return DataPayKindConstant.DEPOSIT.equals(obj.getPayKind());
            }).mapToInt(CashierRefundApplyEntity::getAmt).sum();
            vo.setDepositWzCostReal(depositWzCostReal);
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
        return settleOrders.getRentCosts();
    }
    
    /**
     * 获取租客预结算数据 huangjing
     * @param orderNo
     */
    public OwnerCosts preOwnerSettleOrder(String orderNo,String ownerOrderNo) {
    	SettleOrders settleOrders =  orderSettleNoTService.preInitSettleOrders(orderNo,null,ownerOrderNo);
    	//1 查询所有租客费用明细  （需要计算车主的平台服务费，需要获取租金）
//        orderSettleNoTService.getRenterCostSettleDetailSimpleForOwnerPlatformSrvFee(settleOrders);
        
        //3.5 查询所有车主费用明细 TODO 暂不支持 多个车主
    	orderSettleNoTService.getOwnerCostSettleDetail(settleOrders);

    	//车主预计收益 200214
    	SettleOrdersDefinition settleOrdersDefinition = new SettleOrdersDefinition();
    	//2统计 车主结算费用明细， 补贴，费用总额
    	orderSettleNoTService.handleOwnerAndPlatform(settleOrdersDefinition,settleOrders);
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
    /**
     * 车辆押金结算
     * 先注释调事务
     */
    public void settleOrder(String orderNo, OrderPayCallBack callBack) {
        log.info("OrderSettleService settleOrder orderNo [{}]",orderNo);
        Transaction t = Cat.getProducer().newTransaction(CatConstants.FEIGN_CALL, "车俩结算服务");
        SettleOrders settleOrders = new SettleOrders();
        try {
            Cat.logEvent("settleOrder",orderNo);
            //1 初始化操作 校验操作
            orderSettleNoTService.initSettleOrders(orderNo,settleOrders);
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
            orderSettleNewService.sendOrderSettleMq(orderNo,settleOrders.getRenterMemNo(),settleOrders.getRentCosts(),0,settleOrders.getOwnerMemNo());
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            log.error("OrderSettleService settleOrder,e={},",e);
            OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
            orderStatusDTO.setOrderNo(orderNo);
            orderStatusDTO.setSettleStatus(SettleStatusEnum.SETTL_FAIL.getCode());
            orderStatusDTO.setSettleTime(LocalDateTime.now());
            orderStatusService.saveOrderStatusInfo(orderStatusDTO);
            t.setStatus(e);
            Cat.logError("结算失败  :{}",e);
            orderSettleNewService.sendOrderSettleMq(orderNo,settleOrders.getRenterMemNo(),settleOrders.getRentCosts(),1,settleOrders.getOwnerMemNo());
            throw new RuntimeException("结算失败 ,不能结算");
        } finally {
            t.complete();
        }
        log.info("OrderPayCallBack payCallBack end " );
    }
    /*
     * @Author ZhangBin
     * @Date 2020/3/5 10:20
     * @Description: 订单取消-车主结算
     * @param orderNo 主订单号
     * @param ownerOrderNo 车主子订单号
     * @param ownerMemNo 车主会员号
     * @return
     **/
    @Transactional(rollbackFor=Exception.class)
    public boolean settleOwnerOrderCancel(String orderNo,String ownerOrderNo,String ownerMemNo){
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(orderNo);

        return false;
    }

    public boolean settleRenterOrderCancel(String orderNo,String renterOrderno,String renterMemNo){
        return false;
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
            //2 查询租客罚金明细  及 凹凸币补贴
            orderSettleNoTService.getCancelRenterCostSettleDetail(settleOrders);
            Cat.logEvent("settleOrders",GsonUtils.toJson(settleOrders));
            log.info("OrderPayCallBack settleOrderCancel settleOrders [{}] ",GsonUtils.toJson(settleOrders));
            //3 查询车主罚金明细
            orderSettleNoTService.getCancelOwnerCostSettleDetail(settleOrders);
            Cat.logEvent("settleOrdersFine",GsonUtils.toJson(settleOrders));
            log.info("OrderPayCallBack settleOrderCancel settleOrders [{}] ",GsonUtils.toJson(settleOrders));
            //4 查询 租客实际 付款金额（包含 租车费用，车俩押金，违章押金，钱包，罚金）
            SettleCancelOrdersAccount settleCancelOrdersAccount = orderSettleNoTService.initSettleCancelOrdersAccount(settleOrders);
            Cat.logEvent("settleCancelOrdersAccount",GsonUtils.toJson(settleCancelOrdersAccount));
            log.info("OrderPayCallBack settleCancelOrdersAccount settleCancelOrdersAccount [{}] ",GsonUtils.toJson(settleCancelOrdersAccount));
            if(true)return false;
            //5 处理 租客 车主 平台 罚金收入（将三方金额统计到结算表中）
            orderSettleNoTService.handleIncomeFine(settleOrders,settleCancelOrdersAccount);
            Cat.logEvent("handleIncomeFine",GsonUtils.toJson(settleCancelOrdersAccount));
            log.info("OrderPayCallBack handleIncomeFine handleIncomeFine [{}] ",GsonUtils.toJson(settleCancelOrdersAccount));

            //6 车主罚金走历史欠款
            orderSettleNoTService.handleOwnerFine(settleOrders,settleCancelOrdersAccount);
            Cat.logEvent("handleOwnerFine",GsonUtils.toJson(settleCancelOrdersAccount));
            log.info("OrderPayCallBack handleOwnerFine settleCancelOrdersAccount [{}] ",GsonUtils.toJson(settleCancelOrdersAccount));
            
            //7 租客罚金抵扣 钱包 > 租车费用 > 车辆押金 > 违章押金
            orderSettleNoTService.handleRentFine(settleOrders,settleCancelOrdersAccount);
            Cat.logEvent("handleRentFine",GsonUtils.toJson(settleCancelOrdersAccount));
            log.info("OrderPayCallBack handleRentFine settleCancelOrdersAccount [{}] ",GsonUtils.toJson(settleCancelOrdersAccount));

            //8 租客历史欠款抵扣 钱包 > 租车费用 > 车辆押金 > 违章押金
            orderSettleNoTService.repayHistoryDebtRentCancel(settleOrders,settleCancelOrdersAccount);
            Cat.logEvent("repayHistoryDebtRentCancel",GsonUtils.toJson(settleCancelOrdersAccount));
            log.info("OrderPayCallBack repayHistoryDebtRentCancel settleCancelOrdersAccount [{}] ",GsonUtils.toJson(settleCancelOrdersAccount));

            //9 租客金额 退还 包含 凹凸币，钱包 租车费用 押金 违章押金 退还 （优惠券退还 ->不在结算中做,在取消订单中完成）
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
