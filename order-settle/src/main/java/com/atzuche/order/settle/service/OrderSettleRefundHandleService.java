package com.atzuche.order.settle.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostSettleDetailEntity;
import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.cashieraccount.service.CashierSettleService;
import com.atzuche.order.cashieraccount.service.CashierWzSettleService;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.cashieraccount.vo.req.CashierRefundApplyReqVO;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.cashier.OrderRefundStatusEnum;
import com.atzuche.order.commons.enums.cashier.PayLineEnum;
import com.atzuche.order.commons.enums.cashier.PaySourceEnum;
import com.atzuche.order.settle.dto.OrderSettleCommonParamDTO;
import com.atzuche.order.settle.dto.OrderSettleCommonResultDTO;
import com.atzuche.order.settle.service.notservice.OrderSettleProxyService;
import com.atzuche.order.settle.vo.req.RefundApplyVO;
import com.atzuche.order.settle.vo.req.SettleOrders;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.autopay.gateway.constant.DataPayTypeConstant;
import com.autoyol.commons.utils.GsonUtils;

import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 订单结算退款统一处理
 * <p>涉及:订单正常结算、订单取消结算</p>
 *
 * @author pengcheng.fu
 * @date 2020/8/19 16:57
 */

@Service
@Slf4j
public class OrderSettleRefundHandleService {

    @Autowired
    private OrderSettleWalletRefundHandleService orderSettleWalletRefundHandleService;
    @Autowired
    private CashierNoTService cashierNoTService;
    @Autowired
    private CashierService cashierService;
    @Autowired
    private CashierSettleService cashierSettleService;
    @Autowired
    private CashierWzSettleService cashierWzSettleService;
    @Autowired
    private OrderSettleProxyService orderSettleProxyService;


    /**
     * 租车费用统一处理
     *
     * @param common      公共参数
     * @param surplusCost 处理结果
     * @return OrderSettleCommonResultDTO
     */
    public OrderSettleCommonResultDTO rentCarCostRefundHandle(OrderSettleCommonParamDTO common, int surplusCost) {
        log.info("OrderSettleRefundHandleService.rentCarCostRefundHandle >> param is, common:[{}]," +
                "surplusCost:[{}].", JSON.toJSONString(common), surplusCost);
        int surplusAmt = surplusCost;
        Integer payLine = null;
        List<AccountRenterCostSettleDetailEntity> renterCostSettleDetails = new ArrayList<>();

        SettleOrders settleOrders = new SettleOrders();
        settleOrders.setOrderNo(common.getOrderNo());
        settleOrders.setRenterMemNo(common.getMemNo());
        List<CashierRefundApplyReqVO> cashierRefundApplyReqs =
                orderSettleProxyService.getCashierRefundApply(new RefundApplyVO(settleOrders, -surplusAmt, common.getRentCarCostCashCodeEnum(),
                        "结算订单退还"));
        if (!CollectionUtils.isEmpty(cashierRefundApplyReqs)) {
            for (CashierRefundApplyReqVO cashierRefundApplyReq : cashierRefundApplyReqs) {
                int id = cashierService.refundRentCost(cashierRefundApplyReq);
                AccountRenterCostSettleDetailEntity entity = getAccountRenterCostSettleDetailEntityForRentCost(common, cashierRefundApplyReq.getAmt(), id);
                renterCostSettleDetails.add(entity);
            }

            //计算 实际支付 金额 退钱总额
            int returnAmt = cashierRefundApplyReqs.stream().mapToInt(CashierRefundApplyReqVO::getAmt).sum();
            //重置剩余租车费用
            surplusAmt = surplusAmt + returnAmt;
            log.info("OrderSettleRefundHandleService.rentCarCostRefundHandle >> surplusAmt:[{}]", surplusAmt);
            if (cashierRefundApplyReqs.get(0) != null) {
                payLine = cashierRefundApplyReqs.get(0).getPayLine();
            }
        }

        // 钱包退还
        List<CashierRefundApplyReqVO> walletRefundApplyReqList =
                orderSettleWalletRefundHandleService.walletRefundForRentCarCost(common.getOrderNo(),
                        common.getMemNo(), surplusAmt);
        int walletRefundTotalAmt = OrderConstant.ZERO;
        if (CollectionUtils.isNotEmpty(walletRefundApplyReqList)) {
            for (CashierRefundApplyReqVO cashierRefundApplyReq : cashierRefundApplyReqs) {
                int id = cashierService.refundRentCost(cashierRefundApplyReq);
                AccountRenterCostSettleDetailEntity entity = getAccountRenterCostSettleDetailEntityForRentCost(common,
                        cashierRefundApplyReq.getAmt(), id);
                renterCostSettleDetails.add(entity);
            }
            walletRefundTotalAmt = walletRefundApplyReqList.stream().mapToInt(CashierRefundApplyReqVO::getAmt).sum();
            //重置剩余租车费用
            surplusAmt = surplusAmt + walletRefundTotalAmt;
            log.info("OrderSettleRefundHandleService.rentCarCostRefundHandle >> surplusAmt:[{}]", surplusAmt);
        }

        // 结算费用明细
        cashierSettleService.insertAccountRenterCostSettleDetails(renterCostSettleDetails);
        return resultHandle(payLine, Math.abs(walletRefundTotalAmt), surplusAmt);
    }


    /**
     * 车辆押金退款统一处理
     *
     * @param common 公共参数
     * @return OrderSettleCommonResultDTO 处理结果
     */
    public OrderSettleCommonResultDTO rentCarDepositRefundHandle(OrderSettleCommonParamDTO common, int surplusDepositAmt) {
        log.info("OrderSettleRefundHandleService.rentCarDepositRefundHandle >> param is, common:[{}]," +
                "surplusDepositAmt:[{}].", JSON.toJSONString(common), surplusDepositAmt);
        
        //支付记录
        List<CashierEntity> list = cashierNoTService.getCashierEntity(common.getOrderNo(), common.getMemNo(), DataPayKindConstant.RENT);
        
        if (CollectionUtils.isEmpty(list)) {
            log.info("OrderSettleRefundHandleService.rentCarDepositRefundHandle >> Not found pay records.");
            return new OrderSettleCommonResultDTO(OrderRefundStatusEnum.REFUNDING.getStatus());
        }
        
        int totalAmt =
                list.stream().filter(c -> Objects.nonNull(c.getPayAmt())).mapToInt(CashierEntity::getPayAmt).sum();
        int walletPayTotalAmt =
                list.stream().filter(c -> StringUtils.equalsIgnoreCase(c.getPaySource(),
                        PaySourceEnum.WALLET_PAY.getCode()) && Objects.nonNull(c.getPayAmt())).mapToInt(CashierEntity::getPayAmt).sum();
        
        log.info("OrderSettleRefundHandleService.rentCarDepositRefundHandle >> surplusDepositAmt:[{}], totalAmt:[{}], " +
                "walletPayTotalAmt:[{}]", surplusDepositAmt, totalAmt, walletPayTotalAmt);

        //非钱包支付可退总金额
        int refundableAmtForNotWallet = surplusDepositAmt >= totalAmt - walletPayTotalAmt ?
                totalAmt - walletPayTotalAmt : surplusDepositAmt;
        log.info("OrderSettleRefundHandleService.rentCarDepositRefundHandle >> refundableAmtForNotWallet:[{}]", refundableAmtForNotWallet);
        Integer payLine = null;
        //非钱包支付金额退款操作 目前押金最多只存在一条数据
        if (refundableAmtForNotWallet > OrderConstant.ZERO) {
            Optional<CashierEntity> cashierEntityOptional =
                    list.stream().filter(c -> !StringUtils.equalsIgnoreCase(c.getPaySource(),
                            PaySourceEnum.WALLET_PAY.getCode()) && Objects.nonNull(c.getPayAmt())).findFirst();
            if (cashierEntityOptional.isPresent()) {
            	//仅仅一条记录,除开钱包之外的。
                CashierEntity cashierEntity = cashierEntityOptional.get();
                CashierRefundApplyReqVO cashierRefundApply = new CashierRefundApplyReqVO();
                BeanUtils.copyProperties(cashierEntity, cashierRefundApply);
                int id;
                if (DataPayTypeConstant.PAY_PRE.equals(cashierEntity.getPayType())) {
                    //预授权处理
                    id = cashierService.refundDepositPreAuthAll(refundableAmtForNotWallet, cashierEntity, cashierRefundApply, common.getCashCodeEnum());
                } else {
                    //消费 退货
                    id = cashierService.refundDepositPurchase(refundableAmtForNotWallet, cashierEntity, cashierRefundApply, common.getCashCodeEnum());
                }
                // 记录退还 租车押金 结算费用明细
                AccountRenterCostSettleDetailEntity entity = new AccountRenterCostSettleDetailEntity();
                entity.setOrderNo(common.getOrderNo());
                entity.setRenterOrderNo(common.getRenterOrderNo());
                entity.setMemNo(common.getMemNo());
                entity.setAmt(-refundableAmtForNotWallet);
                entity.setCostCode(common.getCashCodeEnum().getCashNo());
                entity.setCostDetail(common.getCashCodeEnum().getTxt());
                entity.setUniqueNo(String.valueOf(id));
                cashierSettleService.insertAccountRenterCostSettleDetail(entity);
                payLine = cashierRefundApply.getPayLine();
            }
        }else if(refundableAmtForNotWallet == OrderConstant.ZERO){  // ==0 的情况
        	//bugfix:处理无退款的支付宝预授权完成的情况。200820
        	Optional<CashierEntity> cashierEntityOptional =
                    list.stream().filter(c -> !StringUtils.equalsIgnoreCase(c.getPaySource(),
                            PaySourceEnum.WALLET_PAY.getCode()) && Objects.nonNull(c.getPayAmt())).findFirst();
            if (cashierEntityOptional.isPresent()) {
            	//仅仅一条记录,除开钱包之外的。
                CashierEntity cashierEntity = cashierEntityOptional.get();
                //仅仅处理预授权完成的情况(无退款的情况)
                if(Objects.nonNull(cashierEntity) && DataPayTypeConstant.PAY_PRE.equals(cashierEntity.getPayType())) {
	                CashierRefundApplyReqVO cashierRefundApply = new CashierRefundApplyReqVO();
	                BeanUtils.copyProperties(cashierEntity, cashierRefundApply);
	                //预授权处理
	                int id = cashierService.refundDepositPreAuthAll(refundableAmtForNotWallet, cashierEntity, cashierRefundApply, common.getCashCodeEnum());
	                log.info("处理无退款的支付宝预授权完成的情况,params=[{}],result=[{}]",GsonUtils.toJson(cashierEntity),id);
	                
	                // 记录退还 租车押金 结算费用明细， refundableAmtForNotWallet金额为0无需记录
//	                AccountRenterCostSettleDetailEntity entity = new AccountRenterCostSettleDetailEntity();
//	                entity.setOrderNo(common.getOrderNo());
//	                entity.setRenterOrderNo(common.getRenterOrderNo());
//	                entity.setMemNo(common.getMemNo());
//	                entity.setAmt(-refundableAmtForNotWallet);
//	                entity.setCostCode(common.getCashCodeEnum().getCashNo());
//	                entity.setCostDetail(common.getCashCodeEnum().getTxt());
//	                entity.setUniqueNo(String.valueOf(id));
//	                cashierSettleService.insertAccountRenterCostSettleDetail(entity);
//	                payLine = cashierRefundApply.getPayLine();
                }
            }
        }

        //钱包支付可退总金额
        int refundableAmtForWallet = surplusDepositAmt - refundableAmtForNotWallet;
        log.info("OrderSettleRefundHandleService.rentCarDepositRefundHandle >> refundableAmtForWallet:[{}]", refundableAmtForWallet);
        //钱包支付金额退款 可以存在多条数据
        if (refundableAmtForWallet > OrderConstant.ZERO) {
            List<CashierEntity> walletPayRecords =
                    list.stream().filter(c -> StringUtils.equalsIgnoreCase(c.getPaySource(),
                            PaySourceEnum.WALLET_PAY.getCode())).collect(Collectors.toList());
            orderSettleWalletRefundHandleService.walletRefundForCarDeposit(common.getRenterOrderNo()
                    , refundableAmtForWallet, walletPayRecords);
        }

        return resultHandle(payLine, refundableAmtForWallet, OrderConstant.ZERO);
    }


    /**
     * 违章押金退款统一处理
     *
     * @param common 公共参数
     * @return OrderSettleCommonResultDTO 处理结果
     */
    public OrderSettleCommonResultDTO wzDepositRefundHandle(OrderSettleCommonParamDTO common, int surplusDepositAmt) {
        log.info("OrderSettleRefundHandleService.wzDepositRefundHandle >> param is, common:[{}]," +
                "surplusDepositAmt:[{}].", JSON.toJSONString(common), surplusDepositAmt);
        List<CashierEntity> list = cashierNoTService.getCashierEntity(common.getOrderNo(),
                common.getMemNo(), DataPayKindConstant.DEPOSIT);
        if (CollectionUtils.isEmpty(list)) {
            log.info("OrderSettleRefundHandleService.wzDepositRefundHandle >> Not found pay records.");
            return new OrderSettleCommonResultDTO(OrderRefundStatusEnum.REFUNDING.getStatus());
        }
        int totalAmt =
                list.stream().filter(c -> Objects.nonNull(c.getPayAmt())).mapToInt(CashierEntity::getPayAmt).sum();
        int walletPayTotalAmt =
                list.stream().filter(c -> StringUtils.equalsIgnoreCase(c.getPaySource(),
                        PaySourceEnum.WALLET_PAY.getCode()) && Objects.nonNull(c.getPayAmt())).mapToInt(CashierEntity::getPayAmt).sum();
        log.info("OrderSettleRefundHandleService.wzDepositRefundHandle >> surplusDepositAmt:[{}], totalAmt:[{}], " +
                "walletPayTotalAmt:[{}]", surplusDepositAmt, totalAmt, walletPayTotalAmt);

        //非钱包支付可退总金额
        int refundableAmtForNotWallet = surplusDepositAmt >= totalAmt - walletPayTotalAmt ?
                totalAmt - walletPayTotalAmt : surplusDepositAmt;
        log.info("OrderSettleRefundHandleService.wzDepositRefundHandle >> refundableAmtForNotWallet:[{}]", refundableAmtForNotWallet);

        Integer payLine = null;
        //非钱包支付金额退款操作 目前押金最多只存在一条数据
        if (refundableAmtForNotWallet > OrderConstant.ZERO) {
            Optional<CashierEntity> cashierEntityOptional =
                    list.stream().filter(c -> !StringUtils.equalsIgnoreCase(c.getPaySource(),
                            PaySourceEnum.WALLET_PAY.getCode()) && Objects.nonNull(c.getPayAmt())).findFirst();
            if (cashierEntityOptional.isPresent()) {
                CashierEntity cashierEntity = cashierEntityOptional.get();
                CashierRefundApplyReqVO cashierRefundApply = new CashierRefundApplyReqVO();
                BeanUtils.copyProperties(cashierEntity, cashierRefundApply);
                int id;
                if (DataPayTypeConstant.PAY_PRE.equals(cashierEntity.getPayType())) {
                    //预授权处理
                    id = cashierWzSettleService.refundWzDepositPreAuthAll(refundableAmtForNotWallet, cashierEntity, cashierRefundApply, common.getCashCodeEnum());
                } else {
                    //消费 退货
                    id = cashierWzSettleService.refundWzDepositPurchase(refundableAmtForNotWallet, cashierEntity, cashierRefundApply, common.getCashCodeEnum());
                }
                // 结算费用明细
                AccountRenterWzDepositCostSettleDetailEntity entity = new AccountRenterWzDepositCostSettleDetailEntity();
                entity.setOrderNo(common.getOrderNo());
                entity.setRenterOrderNo(common.getRenterOrderNo());
                entity.setMemNo(common.getMemNo());
                entity.setUnit(OrderConstant.ONE);
                entity.setPrice(refundableAmtForNotWallet);
                entity.setWzAmt(-refundableAmtForNotWallet);
                entity.setCostCode(common.getCashCodeEnum().getCashNo());
                entity.setCostDetail(common.getCashCodeEnum().getTxt());
                entity.setUniqueNo(String.valueOf(id));
                entity.setType(10);
                cashierWzSettleService.insertAccountRenterWzDepositCostSettleDetail(entity);
                payLine = cashierRefundApply.getPayLine();
            }
        } else if(refundableAmtForNotWallet == OrderConstant.ZERO){  // ==0 的情况
        	//bugfix:处理无退款的支付宝预授权完成的情况。200820
        	 Optional<CashierEntity> cashierEntityOptional =
                     list.stream().filter(c -> !StringUtils.equalsIgnoreCase(c.getPaySource(),
                             PaySourceEnum.WALLET_PAY.getCode()) && Objects.nonNull(c.getPayAmt())).findFirst();
             if (cashierEntityOptional.isPresent()) {
            	//仅仅一条记录,除开钱包之外的。
                CashierEntity cashierEntity = cashierEntityOptional.get();
                //仅仅处理预授权完成的情况(无退款的情况)
                if(Objects.nonNull(cashierEntity) && DataPayTypeConstant.PAY_PRE.equals(cashierEntity.getPayType())) {
	                CashierRefundApplyReqVO cashierRefundApply = new CashierRefundApplyReqVO();
	                BeanUtils.copyProperties(cashierEntity, cashierRefundApply);
	                //预授权处理
	                int id = cashierWzSettleService.refundWzDepositPreAuthAll(refundableAmtForNotWallet, cashierEntity, cashierRefundApply, common.getCashCodeEnum());
	                log.info("处理WZ无退款的支付宝预授权完成的情况,params=[{}],result=[{}]",GsonUtils.toJson(cashierEntity),id);
                }
            }
        }

        //钱包支付可退总金额
        int refundableAmtForWallet = surplusDepositAmt - refundableAmtForNotWallet;
        log.info("OrderSettleRefundHandleService.wzDepositRefundHandle >> refundableAmtForWallet:[{}]", refundableAmtForWallet);
        //钱包支付金额退款 可以存在多条数据
        if (refundableAmtForWallet > OrderConstant.ZERO) {
            List<CashierEntity> walletPayRecords =
                    list.stream().filter(c -> StringUtils.equalsIgnoreCase(c.getPaySource(),
                            PaySourceEnum.WALLET_PAY.getCode())).collect(Collectors.toList());
            orderSettleWalletRefundHandleService.walletRefundForWzDeposit(common.getRenterOrderNo(),
                    refundableAmtForWallet, walletPayRecords);
        }

        return resultHandle(payLine, refundableAmtForWallet, OrderConstant.ZERO);
    }


    /**
     * 结果处理
     *
     * @param payLine                支付方式
     * @param refundableAmtForWallet 钱包支付金额
     * @return OrderSettleCommonResultDTO
     */
    private OrderSettleCommonResultDTO resultHandle(Integer payLine, int refundableAmtForWallet, int surplusAmt) {
        OrderSettleCommonResultDTO result = new OrderSettleCommonResultDTO();
        if (refundableAmtForWallet > OrderConstant.ZERO) {
            return new OrderSettleCommonResultDTO(OrderRefundStatusEnum.REFUNDING.getStatus(), surplusAmt);
        } else {
            if (payLine != null) {
                if (payLine.equals(PayLineEnum.OFF_LINE_PAY.getCode()) ||
                        payLine.equals(PayLineEnum.VIRTUAL_PAY.getCode())) {
                    return new OrderSettleCommonResultDTO(OrderRefundStatusEnum.REFUNDED.getStatus(), surplusAmt);
                }
            }
        }
        return new OrderSettleCommonResultDTO(OrderRefundStatusEnum.REFUNDING.getStatus(), surplusAmt);
    }


    /**
     * 结算退还租车费用金额 费用明细
     *
     * @param common             公共参数
     * @param rentCostSurplusAmt 剩余租车费用
     * @return AccountRenterCostSettleDetailEntity
     */
    private AccountRenterCostSettleDetailEntity getAccountRenterCostSettleDetailEntityForRentCost(OrderSettleCommonParamDTO common, int rentCostSurplusAmt, int id) {
        AccountRenterCostSettleDetailEntity entity = new AccountRenterCostSettleDetailEntity();
        entity.setOrderNo(common.getOrderNo());
        entity.setRenterOrderNo(common.getRenterOrderNo());
        entity.setMemNo(common.getMemNo());
        entity.setAmt(rentCostSurplusAmt);
        entity.setCostCode(common.getCashCodeEnum().getCashNo());
        entity.setCostDetail(common.getCashCodeEnum().getTxt());
        entity.setUniqueNo(String.valueOf(id));
        return entity;
    }
}
