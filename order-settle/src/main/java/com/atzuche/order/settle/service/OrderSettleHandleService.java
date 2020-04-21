package com.atzuche.order.settle.service;

import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositDetailEntity;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositDetailNoTService;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositNoTService;
import com.atzuche.order.commons.ListUtil;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.settle.entity.AccountDebtDetailEntity;
import com.atzuche.order.settle.entity.AccountDebtReceivableaDetailEntity;
import com.atzuche.order.settle.service.notservice.AccountDebtDetailNoTService;
import com.atzuche.order.settle.service.notservice.AccountDebtNoTService;
import com.atzuche.order.settle.service.notservice.AccountDebtReceivableaDetailNoTService;
import com.atzuche.order.settle.vo.req.AccountDeductDebtReqVO;
import com.atzuche.order.settle.vo.req.AccountOldDebtReqVO;
import com.atzuche.order.settle.vo.req.SettleOrderRenterDepositReqVO;
import com.atzuche.order.settle.vo.res.AccountOldDebtResVO;
import com.atzuche.order.settle.vo.res.OrderSettleResVO;
import com.atzuche.order.wallet.WalletProxyService;
import com.dianping.cat.Cat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 订单结算钱包抵扣相关操作
 *
 * @author pengcheng.fu
 * @date 2020/4/16 16:30
 */
@Service
@Slf4j
public class OrderSettleHandleService {

    public static final int DEPOSIT_SETTLE_TYPE = 1;
    public static final int DEPOSIT_WZ_SETTLE_TYPE = 2;

    @Autowired
    private WalletProxyService walletProxyService;
    @Autowired
    private AccountDebtService accountDebtService;
    @Autowired
    private AccountDebtDetailNoTService accountDebtDetailNoTService;
    @Autowired
    private AccountDebtNoTService accountDebtNoTService;
    @Autowired
    private AccountDebtReceivableaDetailNoTService accountDebtReceivableaDetailNoTService;
    @Autowired
    private RemoteOldSysDebtService remoteOldSysDebtService;
    @Autowired
    private AccountRenterWzDepositDetailNoTService accountRenterWzDepositDetailNoTService;
    @Autowired
    private AccountRenterWzDepositNoTService accountRenterWzDepositNoTService;

    /**
     * 违章押金结算抵扣欠款处理
     * type = 1 租车押金结算
     * type = 2 违章押金结算
     */
    public OrderSettleResVO commonDeductionDebtHandle(String memNo, String orderNo, int type) {
        int newTotalRealDebtAmt = 0;
        int oldTotalRealDebtAmt = 0;
        // 新系统欠款信息处理
        if (type == DEPOSIT_SETTLE_TYPE) {
            newTotalRealDebtAmt = deductionNewDebtHandle(memNo, orderNo, RenterCashCodeEnum.SETTLE_DEPOSIT_TO_HISTORY_AMT);
            // 老系统欠款信息处理
            oldTotalRealDebtAmt = deductionOldDebtHandle(memNo, orderNo, RenterCashCodeEnum.SETTLE_DEPOSIT_TO_OLD_HISTORY_AMT);
        } else if (type == DEPOSIT_WZ_SETTLE_TYPE) {
            newTotalRealDebtAmt = deductionNewDebtHandle(memNo, orderNo, RenterCashCodeEnum.SETTLE_WZ_TO_HISTORY_AMT);
            // 老系统欠款信息处理
            oldTotalRealDebtAmt = deductionOldDebtHandle(memNo, orderNo, RenterCashCodeEnum.SETTLE_WZ_DEPOSIT_TO_OLD_HISTORY_AMT);
        }

        SettleStatusEnum settleStatus = SettleStatusEnum.SETTLED;
        if (newTotalRealDebtAmt == OrderConstant.SPECIAL_MARK || oldTotalRealDebtAmt == OrderConstant.SPECIAL_MARK) {
            if (newTotalRealDebtAmt == OrderConstant.SPECIAL_MARK) {
                newTotalRealDebtAmt = OrderConstant.ZERO;
            }

            if (oldTotalRealDebtAmt == OrderConstant.SPECIAL_MARK) {
                oldTotalRealDebtAmt = OrderConstant.ZERO;
            }
            settleStatus = SettleStatusEnum.SETTL_WALLET_DEDUCT;
        }
        return new OrderSettleResVO(settleStatus, newTotalRealDebtAmt, oldTotalRealDebtAmt);
    }


    /**
     * 抵扣钱包处理
     *
     * @param memNo        租客会员号
     * @param orderNo      订单号
     * @param deductionAmt 预计抵扣金额
     * @return int 真实抵扣金额
     */
    public int deductionWalletHandle(String memNo, String orderNo, int deductionAmt) {
        log.info("Deduction wallet.param is,memNo:[{}],orderNo:[{}],deductionAmt:[{}]", memNo, orderNo, deductionAmt);
        if (OrderConstant.ZERO == deductionAmt) {
            log.info("Deduction wallet. deductionAmt is zero!");
            return OrderConstant.ZERO;
        }

        deductionAmt = Math.abs(deductionAmt);
        int realDeductionAmt = walletProxyService.orderDeduct(memNo, orderNo, deductionAmt);
        if (realDeductionAmt == OrderConstant.ZERO) {
            //扣减失败,重新计算抵扣金额并再次扣减
            int balance = walletProxyService.getWalletByMemNo(memNo);
            if (OrderConstant.ZERO == balance) {
                log.info("Deduction wallet. balance is zero!");
                return OrderConstant.ZERO;
            }
            int newDeductionAmt = balance >= deductionAmt ? deductionAmt : balance;
            log.info("Deduction wallet.realDeductionAmt:[{}],balance:[{}],deductionAmt:[{}]", realDeductionAmt, balance, deductionAmt);
//            realDeductionAmt = walletProxyService.orderDeduct(memNo, orderNo, deductionAmt);
            //按新的钱包金额来抵扣。
            realDeductionAmt = walletProxyService.orderDeduct(memNo, orderNo, newDeductionAmt);
        }
        log.info("Deduction wallet.result is,realDeductionAmt:[{}]", realDeductionAmt);
        return realDeductionAmt;
    }

    /**
     * 新系统欠款抵扣处理
     *
     * @param memNo   租客会员号
     * @param orderNo 订单号
     */
    public int deductionNewDebtHandle(String memNo, String orderNo, RenterCashCodeEnum renterCashCodeEnum) {
        // 获取会员欠款总金额
        int debtAmt = accountDebtService.getAccountDebtNumByMemNo(memNo);
        if (debtAmt >= OrderConstant.ZERO) {
            log.info("NEW.The amount owed by the member(memNo:[{}]) is zero!", memNo);
            return OrderConstant.ZERO;
        }
        // 获取钱包真实抵扣欠款金额
        int realDeductionAmt = deductionWalletHandle(memNo, orderNo, debtAmt);
        if (OrderConstant.ZERO == realDeductionAmt) {
            log.info("NEW.钱包余额不足.realDeductionAmt:[{}],debtAmt:[{}]", realDeductionAmt, debtAmt);
            Cat.logEvent("违章押金结算(钱包抵扣欠款)", "NEW.钱包余额不足,orderNo:" + orderNo + " memNo:" + memNo + " debtAmt:" + debtAmt +
                    " realDeductionAmt:" + realDeductionAmt);
            return OrderConstant.ZERO;
        }
        // 获取欠款明细
        List<AccountDebtDetailEntity> accountDebtDetailAlls = accountDebtDetailNoTService.getDebtListByMemNo(memNo);
        // 欠款明细处理
        AccountDeductDebtReqVO accountDeductDebt = new AccountDeductDebtReqVO(memNo, realDeductionAmt,
                renterCashCodeEnum.getCashNo(), renterCashCodeEnum.getTxt(), OrderConstant.SYSTEM_OPERATOR_JOB);
        List<AccountDebtDetailEntity> accountDebtDetails =
                accountDebtDetailNoTService.getDebtListByDebtAll(accountDebtDetailAlls, accountDeductDebt);
        if (accountDeductDebt.getRealAmt() != realDeductionAmt) {
            log.info("NEW.总账和明细不一致.realDeductionAmt:[{}],realAmt:[{}]", realDeductionAmt, accountDeductDebt.getRealAmt());
            Cat.logEvent("违章押金结算(钱包抵扣欠款)", "NEW.总账和明细不一致,orderNo:" + orderNo + " memNo:" + memNo + " debtAmt:" + debtAmt +
                    " realDeductionAmt:" + realDeductionAmt + " realAmt:" + accountDeductDebt.getRealAmt());

            return OrderConstant.SPECIAL_MARK;
        }
        // 新增欠款收款明细
        if (CollectionUtils.isNotEmpty(accountDeductDebt.getAccountDebtReceivableaDetails())) {
            accountDeductDebt.getAccountDebtReceivableaDetails().forEach(d -> d.setPayWay(OrderConstant.ONE));
            accountDebtReceivableaDetailNoTService.insertAlreadyReceivablea(accountDeductDebt.getAccountDebtReceivableaDetails());
        }
        // 更新欠款明细
        accountDebtDetailNoTService.updateAlreadyDeductDebt(accountDebtDetails);
        // 更新欠款总金额
        accountDebtNoTService.deductAccountDebt(accountDeductDebt);

        return accountDeductDebt.getRealAmt();
    }


    /**
     * 老系统欠款抵扣处理
     *
     * @param memNo   租客会员号
     * @param orderNo 订单号
     * @return int 真实抵扣金额
     */
    public int deductionOldDebtHandle(String memNo, String orderNo, RenterCashCodeEnum renterCashCodeEnum) {
        // 获取会员欠款总金额
        Integer debtAmt = remoteOldSysDebtService.getMemBalance(memNo);
        if (Objects.isNull(debtAmt) || debtAmt == OrderConstant.ZERO) {
            log.info("OLD.The amount owed by the member(memNo:[{}]) is zero!", memNo);
            return OrderConstant.ZERO;
        }
        // 获取钱包真实抵扣欠款金额
        int realDeductionAmt = deductionWalletHandle(memNo, orderNo, debtAmt);
        if (OrderConstant.ZERO == realDeductionAmt) {
            log.info("OLD.钱包余额不足.realDeductionAmt:[{}],debtAmt:[{}]", realDeductionAmt, debtAmt);
            Cat.logEvent("违章押金结算(钱包抵扣欠款)", "OLD.钱包余额不足,orderNo:" + orderNo + " memNo:" + memNo + " debtAmt:" + debtAmt +
                    " realDeductionAmt:" + realDeductionAmt);
            return OrderConstant.ZERO;
        }

        List<AccountOldDebtReqVO> oldDebtList = new ArrayList<AccountOldDebtReqVO>();
        AccountOldDebtReqVO accountOldDebtReqVO = new AccountOldDebtReqVO();
        accountOldDebtReqVO.setOrderNo(orderNo);
        accountOldDebtReqVO.setMemNo(memNo);
        accountOldDebtReqVO.setSurplusAmt(realDeductionAmt);
        accountOldDebtReqVO.setCahsCodeEnum(renterCashCodeEnum);
        oldDebtList.add(accountOldDebtReqVO);
        List<AccountOldDebtResVO> debtResList = accountDebtService.calDeductOldDebt(oldDebtList);
        int totalRealDebtAmt = OrderConstant.ZERO;
        if (CollectionUtils.isNotEmpty(debtResList)) {
            totalRealDebtAmt = debtResList.stream().mapToInt(AccountOldDebtResVO::getRealDebtAmt).sum();
        }
        if (totalRealDebtAmt != realDeductionAmt) {
            log.info("OLD.总账和明细不一致.realDeductionAmt:[{}],totalRealDebtAmt:[{}]", realDeductionAmt, totalRealDebtAmt);
            Cat.logEvent("违章押金结算(钱包抵扣欠款)",
                    "OLD.总账和明细不一致,orderNo:" + orderNo + " memNo:" + memNo + " debtAmt:" + debtAmt +
                            " realDeductionAmt:" + realDeductionAmt + " totalRealDebtAmt:" + totalRealDebtAmt);
            return OrderConstant.SPECIAL_MARK;
        }

        // 新增欠款收款明细
        List<AccountDebtReceivableaDetailEntity> accountDebtReceivableaDetailEntityList =
                accountDebtService.convertToDebtReceivableaDetail(debtResList);
        if (CollectionUtils.isNotEmpty(accountDebtReceivableaDetailEntityList)) {
            accountDebtReceivableaDetailEntityList.forEach(d -> d.setPayWay(OrderConstant.ONE));
            // 保存欠款收款详情
            accountDebtReceivableaDetailNoTService.insertAlreadyReceivablea(accountDebtReceivableaDetailEntityList);
        }
        return totalRealDebtAmt;
    }


    /**
     * 租客押金处理并返回应扣金额(钱包抵扣)
     *
     * @param reqVO 参数
     * @return int yingkouAmt
     */
    public int accountRentetDepositHandle(SettleOrderRenterDepositReqVO reqVO) {
        int realDeductAmt = Objects.nonNull(reqVO.getRealDeductAmt()) ? reqVO.getRealDeductAmt() : OrderConstant.ZERO;
        int shouldTakeAmt = Objects.nonNull(reqVO.getShouldTakeAmt()) ? reqVO.getShouldTakeAmt() : OrderConstant.ZERO;

        if (realDeductAmt > OrderConstant.ZERO) {
            // 获取uniqueNo
            List<AccountDebtReceivableaDetailEntity> list =
                    accountDebtReceivableaDetailNoTService.getByOrderNoAndMemNo(reqVO.getOrderNo(), reqVO.getMemNo());
            String uniqueNo = null;
            if (CollectionUtils.isNotEmpty(list)) {
                List<Integer> ids;

                if (StringUtils.equals(reqVO.getCostEnum().getCashNo(), RenterCashCodeEnum.SETTLE_WALLET_TO_WZ_COST.getCashNo())) {
                    ids = list.stream()
                            .filter(x -> Objects.nonNull(x.getPayWay()) && x.getPayWay() == OrderConstant.ONE)
                            .filter(x -> StringUtils.equals(x.getSourceCode(),
                                    RenterCashCodeEnum.SETTLE_WZ_TO_HISTORY_AMT.getCashNo()) || StringUtils.equals(x.getSourceCode(),
                                    RenterCashCodeEnum.SETTLE_WZ_DEPOSIT_TO_OLD_HISTORY_AMT.getCashNo()))
                            .map(AccountDebtReceivableaDetailEntity::getId)
                            .collect(Collectors.toList());
                } else {
                    ids = list.stream()
                            .filter(x -> Objects.nonNull(x.getPayWay()) && x.getPayWay() == OrderConstant.ONE)
                            .filter(x -> StringUtils.equals(x.getSourceCode(),
                                    RenterCashCodeEnum.SETTLE_DEPOSIT_TO_HISTORY_AMT.getCashNo()) || StringUtils.equals(x.getSourceCode(),
                                    RenterCashCodeEnum.SETTLE_DEPOSIT_TO_OLD_HISTORY_AMT.getCashNo()))
                            .map(AccountDebtReceivableaDetailEntity::getId)
                            .collect(Collectors.toList());
                }
                uniqueNo = ListUtil.reduce(ids, ",");
            }
            // 新增account_renter_wz_deposit_detail
            accountRenterWzDepositDetailNoTService.insertRenterDepositDetailEntity(buildAccountRenterWzDepositDetailEntity(reqVO, uniqueNo));
            // 更新account_renter_wz_deposit.shishou_deposit
            accountRenterWzDepositNoTService.updateShishouDepositSettle(reqVO.getOrderNo(), reqVO.getMemNo(),
                    reqVO.getRealDeductAmt());
        }
        int yingkouAmt = shouldTakeAmt;
        if (realDeductAmt >= shouldTakeAmt) {
            yingkouAmt = realDeductAmt;
        }
        return yingkouAmt;
    }

    /**
     * 构建AccountRenterWzDepositDetailEntity
     *
     * @param reqVO 参数
     * @return AccountRenterWzDepositDetailEntity
     */
    private AccountRenterWzDepositDetailEntity buildAccountRenterWzDepositDetailEntity(SettleOrderRenterDepositReqVO reqVO, String uniqueNo) {
        if (Objects.isNull(reqVO.getRealDeductAmt()) || reqVO.getRealDeductAmt() == OrderConstant.ZERO) {
            return null;
        }
        AccountRenterWzDepositDetailEntity accountRenterDepositDetailEntity =
                new AccountRenterWzDepositDetailEntity();
        accountRenterDepositDetailEntity.setOrderNo(reqVO.getOrderNo());
        accountRenterDepositDetailEntity.setMemNo(reqVO.getMemNo());
        accountRenterDepositDetailEntity.setCostCode(reqVO.getCostEnum().getCashNo());
        accountRenterDepositDetailEntity.setCostDetail(reqVO.getCostEnum().getTxt());
        accountRenterDepositDetailEntity.setSourceCode(reqVO.getSourceEnum().getCashNo());
        accountRenterDepositDetailEntity.setSourceDetail(reqVO.getSourceEnum().getTxt());
        accountRenterDepositDetailEntity.setUniqueNo(uniqueNo);
        accountRenterDepositDetailEntity.setAmt(Math.abs(reqVO.getRealDeductAmt()));
        return accountRenterDepositDetailEntity;
    }

}
