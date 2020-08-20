/**
 * 
 */
package com.atzuche.order.settle.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountrenterdeposit.service.AccountRenterDepositService;
import com.atzuche.order.accountrenterdeposit.vo.req.DetainRenterDepositReqVO;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.cashieraccount.service.CashierSettleService;
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
import com.atzuche.order.settle.vo.req.SettleOrders;
import com.atzuche.order.settle.vo.req.SettleOrdersAccount;

/**
 * @author jing.huang
 *
 */
@Service
public class OrderSettleSupplementHandleService {
	private static Logger logger = LoggerFactory.getLogger(OrderSettleSupplementHandleService.class);

    @Autowired
    private OrderSupplementDetailService orderSupplementDetailService;
    @Autowired
    private CashierSettleService cashierSettleService;
    @Autowired
    private CashierService cashierService;
    @Autowired
    private AccountRenterDepositService accountRenterDepositService;
    
    String FEE = "fee";
    String DEPOSIT = "deposit";
    
    public String getFEE() {
		return FEE;
	}
	public String getDEPOSIT() {
		return DEPOSIT;
	}


	/**
     * 处理订单未支付的补付记录
     *
     * @param settleOrders        订单信息
     * @param settleOrdersAccount 结算信息
     * @param pointType  1租车押金  2租车费用
     */
    public void supplementCostHandle(SettleOrders settleOrders, SettleOrdersAccount settleOrdersAccount,String pointType) {
        logger.info("settlement of orders, supplementary payment of fees. settleOrdersAccount:[{}]", JSON.toJSONString(settleOrdersAccount));
        List<OrderSupplementDetailEntity> entityList = orderSupplementDetailService.queryNotPaySupplementByOrderNoAndMemNo(settleOrdersAccount.getOrderNo());
        if (CollectionUtils.isEmpty(entityList)) {
            logger.warn("No record of supplement was found.");
        } else {
            List<OrderSupplementDetailEntity> debtList= new ArrayList<>();
            List<OrderSupplementDetailEntity> deductList = new ArrayList<>();
            
            /**
             	* 该方法内部：settleOrdersAccount.setDepositSurplusAmt(depositSurplusAmt); 已经重新设置了该参数
             */
            if(DEPOSIT.equals(pointType)) {
	            int needPayAmt = handleSupplementDetail(entityList, debtList, deductList, settleOrdersAccount);
	            logger.info("settlement of orders, supplementary payment of fees. needPayAmt:[{}]，settleOrdersAccount:[{}]", needPayAmt,JSON.toJSONString(settleOrdersAccount));
            }else if(FEE.equals(pointType)) {
            	int needPayAmt = handleSupplementDetailFee(entityList, debtList, deductList, settleOrdersAccount);
	            logger.info("settlement of orders, supplementary payment of fees. needPayAmt:[{}]，settleOrdersAccount:[{}]", needPayAmt,JSON.toJSONString(settleOrdersAccount));
            }
            
            //应扣
//            settleOrders.setShouldTakeCost(settleOrders.getShouldTakeCost() + needPayAmt);
//            settleOrdersAccount.setDepositSurplusAmt(settleOrdersAccount.getDepositSurplusAmt() + needPayAmt);
            
            if (CollectionUtils.isNotEmpty(debtList)) {
            	
                debtList.forEach(entity -> {
                	
                	SupplementPayFlagEnum emunFlag = SupplementPayFlagEnum.PAY_FLAG_ZUCHE_DEPOSIT_SETTLE_INTO_DEBT;
                	if(FEE.equals(pointType)) {
                		emunFlag = SupplementPayFlagEnum.PAY_FLAG_ZUCHEFEE_DEPOSIT_SETTLE_INTO_DEBT;
                	}
                	
                    orderSupplementDetailService.updatePayFlagById(entity.getId(),emunFlag.getCode(), null);
                    AccountInsertDebtReqVO accountInsertDebt = buildAccountInsertDebtReqVO(settleOrders, entity.getAmt());
                    cashierService.createDebt(accountInsertDebt);
                });
            }
            
            if (CollectionUtils.isNotEmpty(deductList)) {
                deductList.forEach(entity -> {
                	SupplementPayFlagEnum emunFlag = SupplementPayFlagEnum.PAY_FLAG_ZUCHE_DEPOSIT_SETTLE_DEDUCT;
                	if(FEE.equals(pointType)) {
                		emunFlag = SupplementPayFlagEnum.PAY_FLAG_ZUCHEFEE_DEPOSIT_SETTLE_DEDUCT;
                	}
                	
                    orderSupplementDetailService.updatePayFlagById(entity.getId(),emunFlag.getCode(), null);
                    // 更新违章押金抵扣信息
                    if(null != entity.getPayFlag() && entity.getPayFlag() != OrderConstant.ZERO) {
                    	
                    	RenterCashCodeEnum emun = RenterCashCodeEnum.SETTLE_ZUCHE_TO_SUPPLEMENT_AMT;
                    	if(FEE.equals(pointType)) {
                    		emun = RenterCashCodeEnum.SETTLE_ZUCHEFEE_TO_SUPPLEMENT_AMT;
                    	}
                    	DetainRenterDepositReqVO detainRenterDepositReqVO = buildPayedOrderRenterDepositDetailReqVO(settleOrders, Math.abs(entity.getAmt()),emun);
                        detainRenterDepositReqVO.setUniqueNo(String.valueOf(entity.getId()));
                        
                        int renterDepositDetailId = accountRenterDepositService.detainRenterDeposit(detainRenterDepositReqVO);

                        // 添加结算明细
                        AccountRenterCostSettleDetailEntity settleDetail = new AccountRenterCostSettleDetailEntity();   //WzDeposit
                        settleDetail.setOrderNo(settleOrders.getOrderNo());
                        settleDetail.setRenterOrderNo(settleOrders.getRenterOrderNo());
                        settleDetail.setMemNo(settleOrders.getRenterMemNo());
                        settleDetail.setAmt(-Math.abs(detainRenterDepositReqVO.getAmt()));
                        settleDetail.setCostCode(emun.getCashNo());
                        settleDetail.setCostDetail(emun.getTxt());
                        settleDetail.setType(1);
                        settleDetail.setUniqueNo(String.valueOf(renterDepositDetailId));
                        cashierSettleService.insertAccountRenterCostSettleDetail(settleDetail); //wzDeposit
                    }
                });
            }
        }
    }


    /**
     * 处理未支付的补付记录
     *
     * @param entityList 订单补付记录
     * @param debtList   转款欠款记录
     * @param deductList 押金抵扣记录
     * @param settleOrdersAccount 结算信息
     */
    public int handleSupplementDetail(List<OrderSupplementDetailEntity> entityList,
                                       List<OrderSupplementDetailEntity> debtList,
                                       List<OrderSupplementDetailEntity> deductList,
                                       SettleOrdersAccount settleOrdersAccount) {
    	//无需支付
        List<OrderSupplementDetailEntity> noNeedPayList =
                entityList.stream().filter(d -> null != d.getPayFlag() && d.getPayFlag() == OrderConstant.ZERO).collect(Collectors.toList());
        //补付未支付
        List<OrderSupplementDetailEntity> noPayList =
                entityList.stream().filter(d -> null != d.getPayFlag() && d.getPayFlag() != OrderConstant.ZERO).collect(Collectors.toList());
        
        //无需支付
        int noNeedPayAmt = Optional.ofNullable(noNeedPayList).orElseGet(ArrayList::new).stream().mapToInt(OrderSupplementDetailEntity::getAmt).sum();
        //未支付
        int noPayAmt = Optional.ofNullable(noPayList).orElseGet(ArrayList::new).stream().mapToInt(OrderSupplementDetailEntity::getAmt).sum();
        
        
        if (Math.abs(noNeedPayAmt) >= Math.abs(noPayAmt)) {
            logger.warn("No need handle.noNeedPayAmt:[{}],noPayAmt:[{}]", noNeedPayAmt, noPayAmt);
            entityList.forEach(entity ->
                    orderSupplementDetailService.updatePayFlagById(entity.getId(),
                            SupplementPayFlagEnum.PAY_FLAG_ZUCHE_DEPOSIT_SETTLE_DEDUCT.getCode(), null)
            );
        } else {
            int depositSurplusAmt = settleOrdersAccount.getDepositSurplusAmt() + noNeedPayAmt;
            if (depositSurplusAmt > OrderConstant.ZERO) {
                if (depositSurplusAmt >= Math.abs(noPayAmt)) {
                    // 更新补付记录支付状态(已完成抵扣的改为:20,违章押金结算抵扣)
                    deductList.addAll(entityList);
                    depositSurplusAmt = depositSurplusAmt - Math.abs(noPayAmt);
                } else {
                    deductList.addAll(noNeedPayList);
                    OrderSupplementDetailEntity splitCriticalPoint = getSplitCriticalPoint(noPayList,
                            depositSurplusAmt);
                    if (null != splitCriticalPoint) {
                        boolean mark = false;
                        for (OrderSupplementDetailEntity entity : noPayList) {
                            if (mark || entity.getId().intValue() == splitCriticalPoint.getId()) {
                                if (mark) {
                                    // 临界点之后的数据直接记欠款
                                    debtList.add(entity);
                                } else {
                                    if (depositSurplusAmt > OrderConstant.ZERO) {
                                        // 临界点拆分,满足的部分进行抵扣；不满足的部分转入欠款
                                        orderSupplementDetailService.updateOpStatusByPrimaryKey(splitCriticalPoint.getId(),
                                                SupplemOpStatusEnum.OP_STATUS_LOSE_EFFECT.getCode());
                                        OrderSupplementDetailEntity supplementMeet =
                                                buildOrderSupplementDetailEntity(splitCriticalPoint,
                                                        depositSurplusAmt,
                                                        SupplementPayFlagEnum.PAY_FLAG_ZUCHE_DEPOSIT_SETTLE_DEDUCT.getCode(), "租车押金结算抵扣");
                                        orderSupplementDetailService.saveOrderSupplementDetail(supplementMeet);
                                        //需要转入欠款的部分
                                        int debtAmt = Math.abs(splitCriticalPoint.getAmt()) - depositSurplusAmt;
                                        OrderSupplementDetailEntity supplementNotMeet =
                                                buildOrderSupplementDetailEntity(splitCriticalPoint, debtAmt,
                                                        SupplementPayFlagEnum.PAY_FLAG_ZUCHE_DEPOSIT_SETTLE_INTO_DEBT.getCode(), "租车押金结算转欠款");
                                        orderSupplementDetailService.saveOrderSupplementDetail(supplementNotMeet);

                                        debtList.add(supplementNotMeet);
                                        deductList.add(supplementMeet);
                                        depositSurplusAmt = OrderConstant.ZERO;
                                    } else {
                                        // 临界点数据直接记欠款
                                        debtList.add(entity);
                                    }
                                    mark = true;
                                }
                            } else {
                                // 临界点之前的数据直接记抵扣
                                deductList.add(entity);
                                depositSurplusAmt = depositSurplusAmt - Math.abs(entity.getAmt());
                            }
                        }
                    } else {
                        //重置剩余押金
                        depositSurplusAmt = settleOrdersAccount.getDepositSurplusAmt();
                        logger.warn("Split critical point is empty.");
                    }
                }
                settleOrdersAccount.setDepositSurplusAmt(depositSurplusAmt);
            } else {
                // 转入个人欠款(剩余押金不足抵扣补付金额)
                // 更新补付记录支付状态(剩余押金不足抵扣补付金额):30,违章押金结算转欠款
                debtList.addAll(noPayList);
                deductList.addAll(noNeedPayList);
            }
        }

        int needPayAmt = noNeedPayAmt + noPayAmt;
        return needPayAmt > OrderConstant.ZERO ? OrderConstant.ZERO : Math.abs(needPayAmt);
    }
    
    
    /**
         * 处理未支付的补付记录  处理租车费用
     *
     * @param entityList 订单补付记录
     * @param debtList   转款欠款记录
     * @param deductList 押金抵扣记录
     * @param settleOrdersAccount 结算信息
     */
    public int handleSupplementDetailFee(List<OrderSupplementDetailEntity> entityList,
                                       List<OrderSupplementDetailEntity> debtList,
                                       List<OrderSupplementDetailEntity> deductList,
                                       SettleOrdersAccount settleOrdersAccount) {
    	//无需支付
        List<OrderSupplementDetailEntity> noNeedPayList =
                entityList.stream().filter(d -> null != d.getPayFlag() && d.getPayFlag() == OrderConstant.ZERO).collect(Collectors.toList());
        //补付未支付
        List<OrderSupplementDetailEntity> noPayList =
                entityList.stream().filter(d -> null != d.getPayFlag() && d.getPayFlag() != OrderConstant.ZERO).collect(Collectors.toList());
        
        //无需支付
        int noNeedPayAmt = Optional.ofNullable(noNeedPayList).orElseGet(ArrayList::new).stream().mapToInt(OrderSupplementDetailEntity::getAmt).sum();
        //未支付
        int noPayAmt = Optional.ofNullable(noPayList).orElseGet(ArrayList::new).stream().mapToInt(OrderSupplementDetailEntity::getAmt).sum();
        
        
        if (Math.abs(noNeedPayAmt) >= Math.abs(noPayAmt)) {
            logger.warn("No need handle.noNeedPayAmt:[{}],noPayAmt:[{}]", noNeedPayAmt, noPayAmt);
            entityList.forEach(entity ->
                    orderSupplementDetailService.updatePayFlagById(entity.getId(),
                            SupplementPayFlagEnum.PAY_FLAG_ZUCHEFEE_DEPOSIT_SETTLE_DEDUCT.getCode(), null)
            );
        } else {
            int depositSurplusAmt = settleOrdersAccount.getRentCostSurplusAmt() + noNeedPayAmt;
            if (depositSurplusAmt > OrderConstant.ZERO) {
                if (depositSurplusAmt >= Math.abs(noPayAmt)) {
                    // 更新补付记录支付状态(已完成抵扣的改为:20,违章押金结算抵扣)
                    deductList.addAll(entityList);
                    depositSurplusAmt = depositSurplusAmt - Math.abs(noPayAmt);
                } else {
                    deductList.addAll(noNeedPayList);
                    OrderSupplementDetailEntity splitCriticalPoint = getSplitCriticalPoint(noPayList,
                            depositSurplusAmt);
                    if (null != splitCriticalPoint) {
                        boolean mark = false;
                        for (OrderSupplementDetailEntity entity : noPayList) {
                            if (mark || entity.getId().intValue() == splitCriticalPoint.getId()) {
                                if (mark) {
                                    // 临界点之后的数据直接记欠款
                                    debtList.add(entity);
                                } else {
                                    if (depositSurplusAmt > OrderConstant.ZERO) {
                                        // 临界点拆分,满足的部分进行抵扣；不满足的部分转入欠款
                                        orderSupplementDetailService.updateOpStatusByPrimaryKey(splitCriticalPoint.getId(),
                                                SupplemOpStatusEnum.OP_STATUS_LOSE_EFFECT.getCode());
                                        OrderSupplementDetailEntity supplementMeet =
                                                buildOrderSupplementDetailEntity(splitCriticalPoint,
                                                        depositSurplusAmt,
                                                        SupplementPayFlagEnum.PAY_FLAG_ZUCHEFEE_DEPOSIT_SETTLE_DEDUCT.getCode(), "租车费用结算抵扣");
                                        orderSupplementDetailService.saveOrderSupplementDetail(supplementMeet);
                                        //需要转入欠款的部分
                                        int debtAmt = Math.abs(splitCriticalPoint.getAmt()) - depositSurplusAmt;
                                        OrderSupplementDetailEntity supplementNotMeet =
                                                buildOrderSupplementDetailEntity(splitCriticalPoint, debtAmt,
                                                        SupplementPayFlagEnum.PAY_FLAG_ZUCHEFEE_DEPOSIT_SETTLE_INTO_DEBT.getCode(), "租车费用结算转欠款");
                                        orderSupplementDetailService.saveOrderSupplementDetail(supplementNotMeet);

                                        debtList.add(supplementNotMeet);
                                        deductList.add(supplementMeet);
                                        depositSurplusAmt = OrderConstant.ZERO;
                                    } else {
                                        // 临界点数据直接记欠款
                                        debtList.add(entity);
                                    }
                                    mark = true;
                                }
                            } else {
                                // 临界点之前的数据直接记抵扣
                                deductList.add(entity);
                                depositSurplusAmt = depositSurplusAmt - Math.abs(entity.getAmt());
                            }
                        }
                    } else {
                        //重置剩余押金
                        depositSurplusAmt = settleOrdersAccount.getRentCostSurplusAmt();
                        logger.warn("Split critical point is empty.");
                    }
                }
                settleOrdersAccount.setRentCostSurplusAmt(depositSurplusAmt);
            } else {
                // 转入个人欠款(剩余押金不足抵扣补付金额)
                // 更新补付记录支付状态(剩余押金不足抵扣补付金额):30,违章押金结算转欠款
                debtList.addAll(noPayList);
                deductList.addAll(noNeedPayList);
            }
        }

        int needPayAmt = noNeedPayAmt + noPayAmt;
        return needPayAmt > OrderConstant.ZERO ? OrderConstant.ZERO : Math.abs(needPayAmt);
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
            sum = sum + Math.abs(entity.getAmt());
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
    private AccountInsertDebtReqVO buildAccountInsertDebtReqVO(SettleOrders settleOrders, int debtAmt) {
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
        supplement.setAmt(-amt);
        supplement.setRemark(remark);
        supplement.setPayFlag(payFlag);
        supplement.setSupplementType(SupplementTypeEnum.SYSTEM_CREATE.getCode());
        supplement.setOpType(SupplementOpTypeEnum.RENTSETTLE_CREATE.getCode());
        return supplement;
    }

    /**
     * 租车押金抵扣信息
     *
     * @param settleOrders 结算订单信息
     * @param amt 抵扣金额
     * @param emun 
     * @return PayedOrderRenterDepositWZDetailReqVO
     */
    private DetainRenterDepositReqVO buildPayedOrderRenterDepositDetailReqVO(SettleOrders settleOrders, int amt, RenterCashCodeEnum emun){
    	DetainRenterDepositReqVO detainRenterDepositReqVO = new DetainRenterDepositReqVO();
    	detainRenterDepositReqVO.setOrderNo(settleOrders.getOrderNo());
    	detainRenterDepositReqVO.setMemNo(settleOrders.getRenterMemNo());
    	detainRenterDepositReqVO.setAmt(-amt);
    	detainRenterDepositReqVO.setRenterCashCodeEnum(emun);
        return detainRenterDepositReqVO;
    }
    
}
