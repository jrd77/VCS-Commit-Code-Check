package com.atzuche.order.cashieraccount.service;

import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeDetailEntity;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeExamineEntity;
import com.atzuche.order.accountownerincome.service.AccountOwnerIncomeService;
import com.atzuche.order.accountrenterdeposit.entity.AccountRenterDepositEntity;
import com.atzuche.order.accountrenterdeposit.service.AccountRenterDepositService;
import com.atzuche.order.accountrenterdeposit.service.notservice.AccountRenterDepositNoTService;

import com.atzuche.order.accountrenterdeposit.vo.res.AccountRenterDepositResVO;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostDetailEntity;
import com.atzuche.order.accountrenterrentcost.service.AccountRenterCostSettleService;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostDetailNoTService;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostEntity;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositDetailEntity;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositEntity;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositCostNoTService;

import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositDetailNoTService;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositNoTService;
import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.cashieraccount.vo.res.WzDepositMsgResVO;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.cashier.PayTypeEnum;
import com.atzuche.order.commons.enums.cashier.TransStatusEnum;
import com.atzuche.order.commons.vo.DepostiDetailVO;
import com.atzuche.order.commons.vo.res.account.income.AccountOwnerIncomeRealResVO;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.platformcost.LocalDateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;


/**
 * 收银台操作 查询专用
 *
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Service
@Slf4j
public class CashierQueryService {
    @Autowired
    private AccountRenterWzDepositNoTService accountRenterWzDepositNoTService;
    @Autowired
    private AccountRenterDepositNoTService accountRenterDepositNoTService;
    @Autowired private CashierNoTService cashierNoTService;
    @Autowired private AccountRenterWzDepositDetailNoTService accountRenterWzDepositDetailNoTService;
    @Autowired private AccountRenterDepositService accountRenterDepositService;
    @Autowired private AccountRenterCostDetailNoTService accountRenterCostDetailNoTService;
    @Autowired private AccountRenterCostSettleService accountRenterCostSettleService;
    @Autowired private AccountOwnerIncomeService accountOwnerIncomeService;


    /**
     * 查询应收车辆押金
     */
    public AccountRenterDepositResVO getRenterDepositEntity(String orderNo, String memNo){
        return accountRenterDepositService.getAccountRenterDepositEntity(orderNo, memNo);
    }

    public DepostiDetailVO getRenterDepositVO(String orderNo,String memNo){
        AccountRenterDepositResVO renterDepositResVO = accountRenterDepositService.getAccountRenterDepositEntity(orderNo, memNo);
        DepostiDetailVO depostiDetailVO = new DepostiDetailVO();
        BeanUtils.copyProperties(renterDepositResVO,depostiDetailVO);
        if(renterDepositResVO.getPayTime()!=null){
            depostiDetailVO.setPayTime(LocalDateTimeUtils.localDateTimeToDate(renterDepositResVO.getPayTime()));
        }
        if(renterDepositResVO.getSettleTime()!=null){
            depostiDetailVO.setSettleTime(LocalDateTimeUtils.localDateTimeToDate(renterDepositResVO.getSettleTime()));
        }
        return depostiDetailVO;
    }

    /**
     * 查询违章押金
     */
    public AccountRenterWzDepositEntity queryWzDeposit(String orderNo){
        AccountRenterWzDepositEntity entity = accountRenterWzDepositNoTService.getAccountRenterWZDepositByOrder(orderNo);
        return entity;
    }

    /**
     * 获取应付的违章押金
     * @param orderNo
     * @return
     */
    public  AccountRenterWzDepositEntity getTotalToPayWzDepositAmt(String orderNo){
        AccountRenterWzDepositEntity entity = accountRenterWzDepositNoTService.getAccountRenterWZDepositByOrder(orderNo);
        return entity;
    }
    public WzDepositMsgResVO queryWzDepositMsg(String orderNo){
        WzDepositMsgResVO result = new WzDepositMsgResVO();
        result.setOrderNo(orderNo);
        AccountRenterWzDepositEntity entity = queryWzDeposit(orderNo);
        if(Objects.isNull(entity) || Objects.isNull(entity.getOrderNo())){
            return result;
        }

        result.setWzDepositAmt(entity.getShishouDeposit());
        result.setReductionAmt(0);
        result.setMemNo(entity.getMemNo());
        result.setYingshouWzDepositAmt(entity.getYingshouDeposit());
        CashierEntity cashierEntity = cashierNoTService.getCashierEntity(orderNo,entity.getMemNo(), DataPayKindConstant.DEPOSIT);

        if(Objects.nonNull(cashierEntity)){
            result.setPayStatus("支付成功");
            result.setPayTime(DateUtils.formate(cashierEntity.getCreateTime(),DateUtils.DATE_DEFAUTE1));
            result.setPayType(PayTypeEnum.getFlagText(cashierEntity.getPayType()));
        }

        List<AccountRenterWzDepositDetailEntity> list = accountRenterWzDepositDetailNoTService.findByOrderNo(orderNo);
        //剩余可用违章押金
        int wzDepositSurplusAmt = list.stream().mapToInt(AccountRenterWzDepositDetailEntity::getAmt).sum();
        //结算时候抵扣历史欠款
        int debtAmt = list.stream().filter(obj ->{
            return RenterCashCodeEnum.CANCEL_WZ_DEPOSIT_TO_HISTORY_AMT.getCashNo().equals(obj.getCostCode());
        }).mapToInt(AccountRenterWzDepositDetailEntity::getAmt).sum();


        result.setDebtStatus("成功");
        result.setWzDepositSurplusAmt(wzDepositSurplusAmt);
        result.setDebtAmt(debtAmt);
        result.setRefundAmt(entity.getRealReturnDeposit());
        return result;
    }

    /**
     * 查询车辆押金信息
     */
    public AccountRenterDepositEntity queryDeposit(String orderNo){
        AccountRenterDepositEntity entity = accountRenterDepositNoTService.queryDeposit(orderNo);
        return entity;
    }

    /**
     * 获得订单应付的金额
     * @param orderNo
     * @return
     */
    public AccountRenterDepositEntity getTotalToPayDepositAmt(String orderNo){
        AccountRenterDepositEntity entity = accountRenterDepositNoTService.queryDeposit(orderNo);
        return entity;
    }
    /**
     * 查询租车费用 支付记录
     * @param orderNo
     * @return
     */
    public List<AccountRenterCostDetailEntity> getRenterCostDetails(String orderNo){
        return accountRenterCostDetailNoTService.getAccountRenterCostDetailsByOrderNo(orderNo);
    }

    /**
     * 查询已付租车费用
     * @param orderNo
     * @return
     */
    public int getRenterCost(String orderNo,String memNo){
        return accountRenterCostSettleService.getCostPaidRent(orderNo,memNo);
    }

    /**
     * 查询订单  车主 真实收益和 待审核收益
     * @param orderNo
     * @return
     */
    public AccountOwnerIncomeRealResVO getOwnerRealIncomeByOrder(String orderNo){
        AccountOwnerIncomeRealResVO resVO = new AccountOwnerIncomeRealResVO();
        resVO.setOrderNo(orderNo);
        List<AccountOwnerIncomeDetailEntity> accountOwnerIncomeDetails = accountOwnerIncomeService.getOwnerRealIncomeByOrder(orderNo);
        if(!CollectionUtils.isEmpty(accountOwnerIncomeDetails)){
            int incomeAmt = accountOwnerIncomeDetails.stream().mapToInt(AccountOwnerIncomeDetailEntity::getAmt).sum();
            resVO.setIncomeAmt(incomeAmt);
        }
        List<AccountOwnerIncomeExamineEntity> accountOwnerIncomeExamines = accountOwnerIncomeService.getOwnerIncomeByOrder(orderNo);
        if(!CollectionUtils.isEmpty(accountOwnerIncomeExamines)){
            int incomeExamineAmt = accountOwnerIncomeExamines.stream().mapToInt(AccountOwnerIncomeExamineEntity::getAmt).sum();
            resVO.setIncomeExamineAmt(incomeExamineAmt);
        }
        return resVO;
    }

}
