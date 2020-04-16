package com.atzuche.order.cashieraccount.service;

import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleDetailEntity;
import com.atzuche.order.accountownercost.service.notservice.AccountOwnerCostSettleDetailNoTService;
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
import com.atzuche.order.commons.enums.FineTypeEnum;
import com.atzuche.order.commons.enums.account.CostTypeEnum;
import com.atzuche.order.commons.enums.account.income.AccountOwnerIncomeExamineStatus;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.cashier.PaySourceEnum;
import com.atzuche.order.commons.enums.cashier.PayTypeEnum;
import com.atzuche.order.commons.enums.cashier.TransStatusEnum;
import com.atzuche.order.commons.vo.DepostiDetailVO;
import com.atzuche.order.commons.vo.res.account.income.AccountOwnerIncomeRealResVO;
import com.atzuche.order.commons.vo.res.account.income.AccountOwnerSettleCostDetailResVO;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.platformcost.LocalDateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


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
    @Autowired private AccountOwnerCostSettleDetailNoTService accountOwnerCostSettleDetailNoTService;


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
        
        //加上成功标识
        if(Objects.nonNull(cashierEntity) && "00".equals(cashierEntity.getTransStatus())){  //00 
            result.setPayStatus("支付成功");
            result.setPayTime(DateUtils.formate(cashierEntity.getCreateTime(),DateUtils.DATE_DEFAUTE1));
            result.setPayType(PayTypeEnum.getFlagText(cashierEntity.getPayType()));
            result.setPaySource(PaySourceEnum.getFlagText(cashierEntity.getPaySource()));
        }

        List<AccountRenterWzDepositDetailEntity> list = accountRenterWzDepositDetailNoTService.findByOrderNo(orderNo);
        //剩余可用违章押金
        int wzDepositSurplusAmt = list.stream().mapToInt(AccountRenterWzDepositDetailEntity::getAmt).sum();
        //结算时候抵扣历史欠款
        int debtAmt = list.stream().filter(obj ->
                    RenterCashCodeEnum.SETTLE_WZ_TO_HISTORY_AMT.getCashNo().equals(obj.getCostCode())
        ).mapToInt(AccountRenterWzDepositDetailEntity::getAmt).sum();


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
    public AccountOwnerIncomeRealResVO getOwnerRealIncomeByOrder(String orderNo,String memNo){
        AccountOwnerIncomeRealResVO resVO = new AccountOwnerIncomeRealResVO();
        resVO.setOrderNo(orderNo);
        //查询收益审核后收益
        List<AccountOwnerIncomeDetailEntity> accountOwnerIncomeDetails = accountOwnerIncomeService.getOwnerRealIncomeByOrder(orderNo,memNo);
        if(!CollectionUtils.isEmpty(accountOwnerIncomeDetails)){
            int incomeAmt = accountOwnerIncomeDetails.stream().mapToInt(AccountOwnerIncomeDetailEntity::getAmt).sum();
            resVO.setIncomeAmt(incomeAmt);
        }
//        boolean exsitPassed = false;
        //查询待审核收益
        List<AccountOwnerIncomeExamineEntity> accountOwnerIncomeExamines = accountOwnerIncomeService.getOwnerIncomeByOrder(orderNo,memNo);
        if(!CollectionUtils.isEmpty(accountOwnerIncomeExamines)){
            int incomeExamineAmt = accountOwnerIncomeExamines.stream().mapToInt(AccountOwnerIncomeExamineEntity::getAmt).sum();
            resVO.setIncomeExamineAmt(incomeExamineAmt);
            List<AccountOwnerIncomeExamineEntity> ownerIncomeExaminesPassed = accountOwnerIncomeExamines.stream().filter(obj ->{
                return AccountOwnerIncomeExamineStatus.PASS_EXAMINE.getStatus()==obj.getStatus();
            }).collect(Collectors.toList());
//            exsitPassed = !CollectionUtils.isEmpty(ownerIncomeExaminesPassed);
        }


        //查询车主罚金
        List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetails = accountOwnerCostSettleDetailNoTService.getAccountOwnerCostSettleDetails(orderNo,memNo);
        // 车主结算记录存在 且 车主收益 已审核通过  返回  罚金 金额
        if(!CollectionUtils.isEmpty(accountOwnerCostSettleDetails)){
            //油费
            int oilCost=  accountOwnerCostSettleDetails.stream().filter(obj ->{
                return OwnerCashCodeEnum.ACCOUNT_OWNER_SETTLE_OIL_COST.getCashNo().equals(obj.getSourceCode());
            }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            //取消订单罚金
            int cancelFineAmt =  accountOwnerCostSettleDetails.stream().filter(obj ->{
                return FineTypeEnum.CANCEL_FINE.getFineType().equals(Integer.valueOf(obj.getSourceCode())) && getOwnerFineCostType(obj.getCostType());
            }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            //提前还车违约金
            int fineAmt =  accountOwnerCostSettleDetails.stream().filter(obj ->{
                return FineTypeEnum.RENTER_ADVANCE_RETURN.getFineType().equals(Integer.valueOf(obj.getSourceCode()))&& getOwnerFineCostType(obj.getCostType());
            }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            //违约金收益
            int fineYield = accountOwnerCostSettleDetails.stream().filter(obj ->{
                return getOwnerFineCostType(obj.getCostType());
            }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            //历史欠款
            int ownerDebt = accountOwnerCostSettleDetails.stream().filter(obj ->{
                return RenterCashCodeEnum.SETTLE_OWNER_INCOME_TO_HISTORY_AMT.getCashNo().equals(obj.getSourceCode());
            }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            //取还车违约金
            int getCarAndReturnCarFineAmt = accountOwnerCostSettleDetails.stream().filter(obj ->{
                return FineTypeEnum.GET_RETURN_CAR.getFineType().equals(Integer.valueOf(obj.getSourceCode()))&& getOwnerFineCostType(obj.getCostType());
            }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            //平台加油服务费
            int platformRefuelServiceCharge  = accountOwnerCostSettleDetails.stream().filter(obj ->{
                return OwnerCashCodeEnum.OWNER_PLANT_OIL_SERVICE_FEE.getCashNo().equals(obj.getSourceCode());

            }).mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            resVO.setOilCost(oilCost);
            resVO.setCancelOrderPenalty(cancelFineAmt);
            resVO.setOwnerModifySrvAddrCost(getCarAndReturnCarFineAmt);
            resVO.setFineAmt(fineAmt);
            resVO.setFineYield(fineYield);
            resVO.setOwnerDebt(ownerDebt);
            resVO.setPlatformRefuelServiceCharge(platformRefuelServiceCharge);
        }
        List<AccountOwnerSettleCostDetailResVO> ownerSettleCostDetailResVOs = new ArrayList<>();
        if(!CollectionUtils.isEmpty(accountOwnerCostSettleDetails)){
            for(int i =0;i<accountOwnerCostSettleDetails.size();i++){
                AccountOwnerSettleCostDetailResVO vo = new AccountOwnerSettleCostDetailResVO();
                AccountOwnerCostSettleDetailEntity entity = accountOwnerCostSettleDetails.get(i);
                BeanUtils.copyProperties(entity,vo);
                ownerSettleCostDetailResVOs.add(vo);
            }
        }
        resVO.setOwnerSettleCostDetailResVOs(ownerSettleCostDetailResVOs);
        return resVO;
    }

    /**
     * 判断车主结算明细 是否是罚金费用 记录
     */
    private boolean getOwnerFineCostType(Integer costType){
        boolean result = false;
        if(CostTypeEnum.CONSOLE_FINE.getCode().equals(costType)){
            result = true;
        }if(CostTypeEnum.OWNER_FINE.getCode().equals(costType)){
            result = true;
        }
        if(CostTypeEnum.RENTER_FINE.getCode().equals(costType)){
            result = true;
        }
        return result;
    }
}
