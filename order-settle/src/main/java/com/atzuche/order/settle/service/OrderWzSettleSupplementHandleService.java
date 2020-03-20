package com.atzuche.order.settle.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountrenterwzdepost.service.AccountRenterWzDepositService;
import com.atzuche.order.accountrenterwzdepost.vo.req.PayedOrderRenterDepositWZDetailReqVO;
import com.atzuche.order.cashieraccount.service.CashierWzSettleService;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.SupplemOpStatusEnum;
import com.atzuche.order.commons.enums.SupplementOpTypeEnum;
import com.atzuche.order.commons.enums.SupplementPayFlagEnum;
import com.atzuche.order.commons.enums.SupplementTypeEnum;
import com.atzuche.order.commons.enums.account.debt.DebtTypeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.rentercost.entity.OrderSupplementDetailEntity;
import com.atzuche.order.rentercost.service.OrderSupplementDetailService;
import com.atzuche.order.settle.vo.req.AccountInsertDebtReqVO;
import com.atzuche.order.settle.vo.req.SettleOrdersAccount;
import com.atzuche.order.settle.vo.req.SettleOrdersWz;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 违章押金结算,补付记录处理
 *
 * @author pengcheng.fu
 * @date 2020/3/18 16:15
 */

@Service
public class OrderWzSettleSupplementHandleService {

    private static Logger logger = LoggerFactory.getLogger(OrderWzSettleSupplementHandleService.class);

    @Autowired
    private OrderSupplementDetailService orderSupplementDetailService;
    @Autowired
    private CashierWzSettleService cashierWzSettleService;
    @Autowired
    private AccountRenterWzDepositService accountRenterWzDepositService;

    /**
     * 处理订单未支付的补付记录
     *
     * @param settleOrders        订单信息
     * @param settleOrdersAccount 结算信息
     */
    public void supplementCostHandle(SettleOrdersWz settleOrders, SettleOrdersAccount settleOrdersAccount) {
        logger.info("Illegal settlement of orders, supplementary payment of fees. settleOrdersAccount:[{}]", JSON.toJSONString(settleOrdersAccount));
        List<OrderSupplementDetailEntity> entityList =
                orderSupplementDetailService.queryNotPaySupplementByOrderNoAndMemNo(settleOrdersAccount.getOrderNo());
        if (CollectionUtils.isEmpty(entityList)) {
            logger.warn("No record of supplement was found.");
        } else {
            List<OrderSupplementDetailEntity> debtList = new ArrayList<>();
            List<OrderSupplementDetailEntity> deductList = new ArrayList<>();
            if (settleOrdersAccount.getDepositSurplusAmt() > OrderConstant.ZERO) {
                int depositSurplusAmt = settleOrdersAccount.getDepositSurplusAmt();
                int totalSupplementAmt = entityList.stream().mapToInt(OrderSupplementDetailEntity::getAmt).sum();
                if (settleOrdersAccount.getDepositSurplusAmt() >= totalSupplementAmt) {
                    // 更新补付记录支付状态(已完成抵扣的改为:20,违章押金结算抵扣)
                    deductList.addAll(entityList);
                    depositSurplusAmt = settleOrdersAccount.getDepositSurplusAmt() - totalSupplementAmt;
                } else {
                    OrderSupplementDetailEntity splitCriticalPoint = getSplitCriticalPoint(entityList,
                            settleOrdersAccount.getDepositSurplusAmt());
                    if (null != splitCriticalPoint) {
                        boolean mark = false;
                        for (OrderSupplementDetailEntity entity : entityList) {
                            if (mark || entity.getId().intValue() == splitCriticalPoint.getId()) {
                                if (mark) {
                                    // 临界点之后的数据直接记欠款
                                    debtList.add(entity);
                                } else {
                                    if (depositSurplusAmt > OrderConstant.ZERO) {
                                        depositSurplusAmt = OrderConstant.ZERO;
                                        // 临界点拆分,满足的部分进行抵扣；不满足的部分转入欠款
                                        orderSupplementDetailService.updateOpStatusByPrimaryKey(splitCriticalPoint.getId(),
                                                SupplemOpStatusEnum.OP_STATUS_LOSE_EFFECT.getCode());
                                        OrderSupplementDetailEntity supplementMeet =
                                                buildOrderSupplementDetailEntity(splitCriticalPoint,
                                                        depositSurplusAmt,
                                                        SupplementPayFlagEnum.PAY_FLAG_VIOLATION_DEPOSIT_SETTLE_DEDUCT.getCode(), "违章押金结算抵扣");
                                        orderSupplementDetailService.saveOrderSupplementDetail(supplementMeet);
                                        //需要转入欠款的部分
                                        int debtAmt = splitCriticalPoint.getAmt() - depositSurplusAmt;
                                        OrderSupplementDetailEntity supplementNotMeet =
                                                buildOrderSupplementDetailEntity(splitCriticalPoint, debtAmt,
                                                        SupplementPayFlagEnum.PAY_FLAG_VIOLATION_DEPOSIT_SETTLE_INTO_DEBT.getCode(), "违章押金结算转欠款");
                                        orderSupplementDetailService.saveOrderSupplementDetail(supplementNotMeet);

                                        debtList.add(supplementNotMeet);
                                        deductList.add(supplementMeet);
                                    } else {
                                        // 临界点数据直接记欠款
                                        debtList.add(entity);
                                    }
                                    mark = true;
                                }
                            } else {
                                // 临界点之前的数据直接记抵扣
                                deductList.add(entity);
                                depositSurplusAmt = depositSurplusAmt - entity.getAmt();
                            }
                        }
                    } else {
                        logger.warn("Split critical point is empty.");
                    }
                }
                settleOrdersAccount.setDepositSurplusAmt(depositSurplusAmt);
            } else {
                // 转入个人欠款(剩余押金不足抵扣补付金额)
                // 更新补付记录支付状态(剩余押金不足抵扣补付金额):30,违章押金结算转欠款
                debtList.addAll(entityList);
            }
            if (CollectionUtils.isNotEmpty(debtList)) {
                debtList.forEach(entity -> {
                    orderSupplementDetailService.updatePayFlagById(entity.getId(),
                            SupplementPayFlagEnum.PAY_FLAG_VIOLATION_DEPOSIT_SETTLE_INTO_DEBT.getCode(), null);
                    AccountInsertDebtReqVO accountInsertDebt = buildAccountInsertDebtReqVO(settleOrders, -entity.getAmt());
                    cashierWzSettleService.createWzDebt(accountInsertDebt);
                });
            }

            if (CollectionUtils.isNotEmpty(deductList)) {
                deductList.forEach(entity -> {
                    orderSupplementDetailService.updatePayFlagById(entity.getId(),
                            SupplementPayFlagEnum.PAY_FLAG_VIOLATION_DEPOSIT_SETTLE_DEDUCT.getCode(), null);
                    //todo 更新违章押金抵扣信息
                    PayedOrderRenterDepositWZDetailReqVO payedOrderRenterWzDepositDetail =
                            new PayedOrderRenterDepositWZDetailReqVO();
                    payedOrderRenterWzDepositDetail.setOrderNo(settleOrders.getOrderNo());


                    accountRenterWzDepositService.updateRenterWZDepositChange(payedOrderRenterWzDepositDetail);
                });
            }
        }
    }

    /**
     * 有序List依次叠加刚好满足surplusAmt(total >= surplusAmt)对应的数据
     *
     * @param list       叠加数据
     * @param surplusAmt 剩余押金(临界阈值)
     * @return OrderSupplementDetailEntity 临界点数据
     */
    private OrderSupplementDetailEntity getSplitCriticalPoint(List<OrderSupplementDetailEntity> list, int surplusAmt) {
        int sum = 0;
        for (OrderSupplementDetailEntity entity : list) {
            sum = sum + entity.getAmt();
            if (sum > surplusAmt) {
                return entity;
            }

        }
        return null;
    }


    /**
     * 构建欠款信息VO
     *
     * @param settleOrders 违章结算信息
     * @param debtAmt      欠款金额
     * @return AccountInsertDebtReqVO
     */
    private AccountInsertDebtReqVO buildAccountInsertDebtReqVO(SettleOrdersWz settleOrders, int debtAmt) {

        AccountInsertDebtReqVO accountInsertDebt = new AccountInsertDebtReqVO();
        BeanUtils.copyProperties(settleOrders, accountInsertDebt);
        accountInsertDebt.setMemNo(settleOrders.getRenterMemNo());
        accountInsertDebt.setType(DebtTypeEnum.SETTLE.getCode());
        accountInsertDebt.setAmt(-Math.abs(debtAmt));
        accountInsertDebt.setSourceCode(RenterCashCodeEnum.HISTORY_AMT.getCashNo());
        accountInsertDebt.setSourceDetail(RenterCashCodeEnum.HISTORY_AMT.getTxt());
        return accountInsertDebt;
    }


    /**
     * 临界点拆分
     *
     * @param splitCriticalPoint 临界点数据
     * @param amt                金额
     * @param payFlag            支付状态
     * @param remark             备注
     * @return OrderSupplementDetailEntity
     */
    private OrderSupplementDetailEntity buildOrderSupplementDetailEntity(OrderSupplementDetailEntity splitCriticalPoint, int amt, int payFlag, String remark) {
        OrderSupplementDetailEntity supplement = new OrderSupplementDetailEntity();
        BeanUtils.copyProperties(splitCriticalPoint, supplement);
        supplement.setId(null);
        supplement.setAmt(amt);
        supplement.setRemark(remark);
        supplement.setPayFlag(payFlag);
        supplement.setSupplementType(SupplementTypeEnum.SYSTEM_CREATE.getCode());
        supplement.setOpType(SupplementOpTypeEnum.ILLEGALSETTLE_CREATE.getCode());
        return supplement;
    }
}
