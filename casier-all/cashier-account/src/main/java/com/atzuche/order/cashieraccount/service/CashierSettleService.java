package com.atzuche.order.cashieraccount.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleDetailEntity;
import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleEntity;
import com.atzuche.order.accountownercost.service.AccountOwnerCostSettleService;
import com.atzuche.order.accountownercost.service.notservice.AccountOwnerCostSettleDetailNoTService;
import com.atzuche.order.accountownerincome.service.AccountOwnerIncomeService;
import com.atzuche.order.accountplatorm.entity.AccountPlatformProfitDetailEntity;
import com.atzuche.order.accountplatorm.entity.AccountPlatformProfitEntity;
import com.atzuche.order.accountplatorm.entity.AccountPlatformSubsidyDetailEntity;
import com.atzuche.order.accountplatorm.service.notservice.AccountPlatformProfitDetailNotService;
import com.atzuche.order.accountplatorm.service.notservice.AccountPlatformProfitNoTService;
import com.atzuche.order.accountplatorm.service.notservice.AccountPlatformSubsidyDetailNoTService;
import com.atzuche.order.accountrenterclaim.entity.AccountRenterClaimCostSettleEntity;
import com.atzuche.order.accountrenterclaim.service.notservice.AccountRenterClaimCostSettleNoTService;
import com.atzuche.order.accountrenterdeposit.service.AccountRenterDepositService;
import com.atzuche.order.accountrenterdeposit.service.notservice.AccountRenterDepositDetailNoTService;
import com.atzuche.order.accountrenterdeposit.vo.req.DetainRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.OrderCancelRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.res.AccountRenterDepositResVO;
import com.atzuche.order.accountrenterdetain.entity.AccountRenterDetainCostEntity;
import com.atzuche.order.accountrenterdetain.service.notservice.AccountRenterDetainCostNoTService;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleEntity;
import com.atzuche.order.accountrenterrentcost.exception.AccountRenterRentCostSettleException;
import com.atzuche.order.accountrenterrentcost.service.AccountRenterCostSettleService;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostDetailNoTService;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostSettleDetailNoTService;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostSettleNoTService;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostDetailReqVO;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostToFineReqVO;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostEntity;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositCostSettleDetailEntity;
import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositEntity;
import com.atzuche.order.accountrenterwzdepost.exception.RenterWZDepositCostException;
import com.atzuche.order.accountrenterwzdepost.service.AccountRenterWzDepositCostService;
import com.atzuche.order.accountrenterwzdepost.service.AccountRenterWzDepositService;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositCostNoTService;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositCostSettleDetailNoTService;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositDetailNoTService;
import com.atzuche.order.accountrenterwzdepost.service.notservice.AccountRenterWzDepositNoTService;
import com.atzuche.order.accountrenterwzdepost.vo.req.AccountRenterWzCostDetailReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.DetainRenterWZDepositReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.RenterCancelWZDepositCostReqVO;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.cashieraccount.vo.req.DeductDepositToRentCostReqVO;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.settle.service.AccountDebtService;
import com.autoyol.commons.utils.GsonUtils;
import com.dianping.cat.Cat;

import lombok.extern.slf4j.Slf4j;


/**
 * 结算收银台操作
 *
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Service
@Slf4j
public class CashierSettleService {
    @Autowired AccountRenterDepositService accountRenterDepositService;
    @Autowired AccountRenterWzDepositService accountRenterWzDepositService;
    @Autowired AccountRenterWzDepositNoTService accountRenterWzDepositNoTService;
    @Autowired AccountDebtService accountDebtService;
    @Autowired CashierRefundApplyNoTService cashierRefundApplyNoTService;
    @Autowired AccountOwnerIncomeService accountOwnerIncomeService;
    @Autowired AccountRenterCostSettleService accountRenterCostSettleService;
    @Autowired AccountRenterWzDepositCostService accountRenterWzDepositCostService;
    @Autowired RenterOrderCostCombineService renterOrderCostCombineService;
    @Autowired CashierNoTService cashierNoTService;
    @Autowired AccountRenterCostSettleDetailNoTService accountRenterCostSettleDetailNoTService;
    @Autowired AccountOwnerCostSettleDetailNoTService accountOwnerCostSettleDetailNoTService;
    @Autowired AccountPlatformSubsidyDetailNoTService accountPlatformSubsidyDetailNoTService;
    @Autowired AccountPlatformProfitDetailNotService accountPlatformProfitDetailNotService;
    @Autowired AccountPlatformProfitNoTService accountPlatformProfitNoTService;
    @Autowired private AccountRenterCostSettleNoTService accountRenterCostSettleNoTService;
    @Autowired private AccountRenterCostDetailNoTService accountRenterCostDetailNoTService;
    @Autowired private AccountOwnerCostSettleService accountOwnerCostSettleService;
    @Autowired private AccountRenterClaimCostSettleNoTService accountRenterClaimCostSettleNoTService;
    @Autowired private AccountRenterDetainCostNoTService accountRenterDetainCostNoTService;
    @Autowired private AccountRenterWzDepositDetailNoTService accountRenterWzDepositDetailNoTService;
    @Autowired private AccountRenterDepositDetailNoTService accountRenterDepositDetailNoTService;
    
    @Autowired
    AccountRenterWzDepositCostSettleDetailNoTService accountRenterWzDepositCostSettleDetailNoTService;
    @Autowired
    AccountRenterWzDepositCostNoTService accountRenterWzDepositCostNoTService;

    /**
     * 车辆结算
     * @param orderNo
     * @return
     */
    public List<AccountRenterCostDetailEntity> getAccountRenterCostDetailsByOrderNo(String orderNo){
        return accountRenterCostSettleService.getAccountRenterCostDetailsByOrderNo(orderNo);
    }

    /**
     * 车俩结算 租客费用明细落库
     * @param accountRenterCostSettleDetails
     */
    public void insertAccountRenterCostSettleDetails(List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetails) {
        if(!CollectionUtils.isEmpty(accountRenterCostSettleDetails)){
            accountRenterCostSettleDetailNoTService.insertAccountRenterCostSettleDetails(accountRenterCostSettleDetails);
        }

    }
    
    public void insertAccountRenterWzDepoistCostSettleDetails(List<AccountRenterWzDepositCostSettleDetailEntity> accountRenterWzDepositCostSettleDetails) {
        if(!CollectionUtils.isEmpty(accountRenterWzDepositCostSettleDetails)){
        	accountRenterWzDepositCostSettleDetailNoTService.insertAccountRenterWzDepositCostSettleDetails(accountRenterWzDepositCostSettleDetails);
        }

    }
    
    public void insertAccountRenterCostSettleDetail(AccountRenterCostSettleDetailEntity entity) {
        if(Objects.nonNull(entity)){
            accountRenterCostSettleDetailNoTService.insertAccountRenterCostSettleDetail(entity);
        }
    }

    /**
     * 车俩结算 车主费用明细落库
     * @param accountOwnerCostSettleDetails
     */
    public void insertAccountOwnerCostSettleDetails(List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetails) {
        if(!CollectionUtils.isEmpty(accountOwnerCostSettleDetails)){
            accountOwnerCostSettleDetailNoTService.insertAccountOwnerCostSettleDetails(accountOwnerCostSettleDetails);
        }
    }

    /**
     * 车俩结算 车主费用明细落库
     * @param accountPlatformSubsidyDetails
     */
    public void insertAccountPlatformSubsidyDetails(List<AccountPlatformSubsidyDetailEntity> accountPlatformSubsidyDetails) {
        if(!CollectionUtils.isEmpty(accountPlatformSubsidyDetails)){
            accountPlatformSubsidyDetailNoTService.insertAccountPlatformSubsidyDetails(accountPlatformSubsidyDetails);
        }
    }

    /**
     * 平台收益明细 落库
     * @param accountPlatformProfitDetails
     */
    public void insertAccountPlatformProfitDetails(List<AccountPlatformProfitDetailEntity> accountPlatformProfitDetails) {
        if(!CollectionUtils.isEmpty(accountPlatformProfitDetails)){
            accountPlatformProfitDetailNotService.insertAccountPlatformProfitDetails(accountPlatformProfitDetails);
        }
    }
    /**
     * 查询租车费用
     */
    public AccountRenterCostSettleEntity getAccountRenterCostSettleEntity(String orderNo,String renterMemNo){
      return accountRenterCostSettleNoTService.getCostPaidRentSettle(orderNo,renterMemNo);
    }

    /**
     * 计算租客 租车费用  平台补贴费用  车主补贴费用 手续费 基础保障费用 等 并落库
     * @param accountRenterCostSettleDetails
     */
    public AccountRenterCostSettleEntity updateRentSettleCost(String orderNo,String renterMemNo,List<AccountRenterCostSettleDetailEntity> accountRenterCostSettleDetails) {
        AccountRenterCostSettleEntity entity = accountRenterCostSettleNoTService.getCostPaidRentSettle(orderNo,renterMemNo);
        if(Objects.isNull(entity) || Objects.isNull(entity.getId())){
            throw new AccountRenterRentCostSettleException() ;
        }
        if(!CollectionUtils.isEmpty(accountRenterCostSettleDetails)){
            // 平台补贴费用
            int platformSubsidyAmount = accountRenterCostSettleDetails.stream().filter(obj ->{
                return RenterCashCodeEnum.ACCOUNT_CONSOLE_RENTER_SUBSIDY_COST.getCashNo().equals(obj.getCostCode());
            }).mapToInt(AccountRenterCostSettleDetailEntity::getAmt).sum();
            //车主补贴费用
            int carOwnerSubsidyAmount = accountRenterCostSettleDetails.stream().filter(obj ->{
                return RenterCashCodeEnum.ACCOUNT_RENTER_SUBSIDY_COST.getCashNo().equals(obj.getCostCode());
            }).mapToInt(AccountRenterCostSettleDetailEntity::getAmt).sum();
            //附加驾驶人保证费用
            int additionalDrivingEnsureAmount = accountRenterCostSettleDetails.stream().filter(obj ->{
                return RenterCashCodeEnum.EXTRA_DRIVER_INSURE.getCashNo().equals(obj.getCostCode());
            }).mapToInt(AccountRenterCostSettleDetailEntity::getAmt).sum();
            //全面保障费用
            int comprehensiveEnsureAmount = accountRenterCostSettleDetails.stream().filter(obj ->{
                return RenterCashCodeEnum.ABATEMENT_INSURE.getCashNo().equals(obj.getCostCode());
            }).mapToInt(AccountRenterCostSettleDetailEntity::getAmt).sum();
            //基础保障费用
            int basicEnsureAmount = accountRenterCostSettleDetails.stream().filter(obj ->{
                return RenterCashCodeEnum.INSURE_TOTAL_PRICES.getCashNo().equals(obj.getCostCode());
            }).mapToInt(AccountRenterCostSettleDetailEntity::getAmt).sum();
            //手续费
            int yongjinAmt = accountRenterCostSettleDetails.stream().filter(obj ->{
                return RenterCashCodeEnum.FEE.getCashNo().equals(obj.getCostCode());
            }).mapToInt(AccountRenterCostSettleDetailEntity::getAmt).sum();
            //租车费用
            int rentAmt = accountRenterCostSettleDetails.stream().mapToInt(AccountRenterCostSettleDetailEntity::getAmt).sum();
            entity.setYongjinAmt(yongjinAmt);
            entity.setRentAmt(rentAmt);
            entity.setPlatformSubsidyAmount(platformSubsidyAmount);
            entity.setCarOwnerSubsidyAmount(carOwnerSubsidyAmount);
            entity.setAdditionalDrivingEnsureAmount(additionalDrivingEnsureAmount);
            entity.setComprehensiveEnsureAmount(comprehensiveEnsureAmount);
            entity.setBasicEnsureAmount(basicEnsureAmount);
            accountRenterCostSettleNoTService.updateAccountRenterCostSettle(entity);
        }
        return entity;


    }
    
    /**
     * 计算租客 租车费用  平台补贴费用  车主补贴费用 手续费 基础保障费用 等 并落库
     * @param account_renter_wz_deposit_cost
     */
    public AccountRenterWzDepositCostEntity updateWzRentSettleCost(String orderNo,String renterMemNo,int wzTotalAmt) {
    	//account_renter_wz_deposit
    	AccountRenterWzDepositEntity depositEntity = accountRenterWzDepositNoTService.getAccountRenterWZDeposit(orderNo, renterMemNo);
    	if(Objects.isNull(depositEntity) || Objects.isNull(depositEntity.getId())){
            throw new RenterWZDepositCostException() ;
        }
    	
    	AccountRenterWzDepositCostEntity entity = accountRenterWzDepositCostNoTService.queryWzDeposit(orderNo,renterMemNo);
        if(Objects.isNull(entity) || Objects.isNull(entity.getId())){
//            throw new AccountRenterRentCostSettleException() ;
        	entity = new AccountRenterWzDepositCostEntity();
        	entity.setOrderNo(orderNo);
        	entity.setMemNo(renterMemNo);
        	entity.setYingfuAmt(wzTotalAmt);
        	entity.setShifuAmt(depositEntity.getShishouDeposit()); //需要获取实付金额
        	entity.setDebtAmt(0); //当前默认为0，后续产生欠款的时候，需要回写该字段。
        	entity.setIsDelete(0);
        	
        	//新增记录
        	accountRenterWzDepositCostNoTService.insertAccountRenterWzDepositCost(entity);
        }else {
        	//修改记录
        	entity.setYingfuAmt(wzTotalAmt);
        	if(!(entity.getShifuAmt() != null && entity.getShifuAmt().intValue() != 0)) {
        		//没有数据的时候才赋值。
        		entity.setShifuAmt(depositEntity.getShishouDeposit()); //需要获取实付金额
        	}
        	accountRenterWzDepositCostNoTService.updateAccountRenterWzDepositCost(entity);
        }
        return entity;


    }
    

    /**
     * 结算返回租客 实付车辆押金
     * @param orderNo
     * @param renterMemNo
     * @return
     */
    public int getRentDeposit(String orderNo, String renterMemNo) {
        int amt = accountRenterDepositDetailNoTService.getRentDeposit(orderNo,renterMemNo);
        return amt;
    }


    /**
     * 结算时候，应付金额大于实付金额，存在欠款，车辆押金抵扣
     */
    public void deductDepositToRentCost(DeductDepositToRentCostReqVO vo) {
        // 1 记录押金流水记录
        DetainRenterDepositReqVO detainRenterDepositReqVO = new DetainRenterDepositReqVO();
        BeanUtils.copyProperties(vo,detainRenterDepositReqVO);
        detainRenterDepositReqVO.setRenterCashCodeEnum(RenterCashCodeEnum.SETTLE_DEPOSIT_TO_RENT_COST);
        int renterDepositDetailId = accountRenterDepositService.detainRenterDeposit(detainRenterDepositReqVO);
        //2 记录 更新 租客户头 租车费用
        AccountRenterCostDetailReqVO accountRenterCostChangeReqVO = new AccountRenterCostDetailReqVO();
        BeanUtils.copyProperties(detainRenterDepositReqVO,accountRenterCostChangeReqVO);
        accountRenterCostChangeReqVO.setUniqueNo(String.valueOf(renterDepositDetailId));
        accountRenterCostChangeReqVO.setAmt(Math.abs(vo.getAmt()));
        accountRenterCostChangeReqVO.setRenterCashCodeEnum(RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT);
        int rentCostDetailId = accountRenterCostSettleService.deductDepositToRentCost(accountRenterCostChangeReqVO);
        //3 更新押金流水 UniqueNo 字段
        accountRenterDepositService.updateRenterDepositUniqueNo(String.valueOf(rentCostDetailId),renterDepositDetailId);
        // 4 租客结算费用明细 落库
        AccountRenterCostSettleDetailEntity entity = new AccountRenterCostSettleDetailEntity();
        BeanUtils.copyProperties(vo,entity);
        entity.setAmt(Math.abs(vo.getAmt()));
        entity.setCostCode(RenterCashCodeEnum.SETTLE_DEPOSIT_TO_RENT_COST.getCashNo());
        entity.setCostDetail(RenterCashCodeEnum.SETTLE_DEPOSIT_TO_RENT_COST.getTxt());
        entity.setUniqueNo(String.valueOf(renterDepositDetailId));
        insertAccountRenterCostSettleDetail(entity);
    }
    
    /**
     * 结算时候，应付金额大于实付金额，存在欠款，车辆押金抵扣
     */
    public void deductWzDepositToRentCost(DeductDepositToRentCostReqVO vo) {
        // 1 记录押金流水记录
    	DetainRenterWZDepositReqVO detainRenterDepositReqVO = new DetainRenterWZDepositReqVO();
        BeanUtils.copyProperties(vo,detainRenterDepositReqVO);
        detainRenterDepositReqVO.setRenterCashCodeEnum(RenterCashCodeEnum.SETTLE_WZ_DEPOSIT_TO_WZ_COST);
        int renterDepositDetailId = accountRenterWzDepositService.detainRenterDeposit(detainRenterDepositReqVO);
        
        //2 记录 更新 租客户头 租车费用
        AccountRenterWzCostDetailReqVO accountRenterCostChangeReqVO = new AccountRenterWzCostDetailReqVO();
        BeanUtils.copyProperties(detainRenterDepositReqVO,accountRenterCostChangeReqVO);
        accountRenterCostChangeReqVO.setUniqueNo(String.valueOf(renterDepositDetailId));
        accountRenterCostChangeReqVO.setAmt(Math.abs(vo.getAmt()));
        accountRenterCostChangeReqVO.setRenterCashCodeEnum(RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT);
        int rentCostDetailId = accountRenterWzDepositCostService.deductDepositToRentCost(accountRenterCostChangeReqVO);
   
        //3 更新押金流水 UniqueNo 字段
        accountRenterWzDepositService.updateRenterDepositUniqueNo(String.valueOf(rentCostDetailId),renterDepositDetailId);
        
        //wzTotalCost-todo
        // 4 租客结算费用明细 落库  account_renter_wz_deposit_cost_settle_detail 表结构不同。。 先记入费用总表
        AccountRenterCostSettleDetailEntity entity = new AccountRenterCostSettleDetailEntity();
        BeanUtils.copyProperties(vo,entity);
        entity.setAmt(Math.abs(vo.getAmt()));
        entity.setCostCode(RenterCashCodeEnum.SETTLE_WZ_DEPOSIT_TO_WZ_COST.getCashNo());
        entity.setCostDetail(RenterCashCodeEnum.SETTLE_WZ_DEPOSIT_TO_WZ_COST.getTxt());
        entity.setUniqueNo(String.valueOf(renterDepositDetailId));
        entity.setType(10); //默认10
        insertAccountRenterCostSettleDetail(entity);
        
    }
    

    /**
     * 根据订单号 和会员号 查询 订单 钱包支付租车费用金额
     * @param orderNo
     * @param renterMemNo
     */
    public int getRentCostPayByWallet(String orderNo, String renterMemNo) {
       return accountRenterCostDetailNoTService.getRentCostPayByWallet(orderNo, renterMemNo);
    }


    /**
     * 计算车主费用
     * @param accountOwnerCostSettleDetails
     */
    public void insertAccountOwnerCostSettle(String orderNo, String ownerMemNo, String ownerOrderNo,List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetails) {
        AccountOwnerCostSettleEntity entity = new AccountOwnerCostSettleEntity();
        entity.setMemNo(ownerMemNo);
        entity.setOrderNo(orderNo);
        entity.setOwnerOrderNo(ownerOrderNo);
        if(!CollectionUtils.isEmpty(accountOwnerCostSettleDetails)){
            //1车主端代管车服务费
            int proxyExpenseAmt =accountOwnerCostSettleDetails.stream().filter(obj ->{return OwnerCashCodeEnum.ACCOUNT_OWNER_PROXY_EXPENSE_COST.getCashNo().equals(obj.getSourceCode());})
                    .mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            //2车主端平台服务费
            int serviceExpenseAmt =accountOwnerCostSettleDetails.stream().filter(obj ->{return OwnerCashCodeEnum.ACCOUNT_OWNER_SERVICE_EXPENSE_COST.getCashNo().equals(obj.getSourceCode());})
                    .mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            //3获取车主补贴
            int subsidyAmount =accountOwnerCostSettleDetails.stream().filter(obj ->{return OwnerCashCodeEnum.ACCOUNT_OWNER_SUBSIDY_COST.getCashNo().equals(obj.getSourceCode());})
                    .mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            //4获取车主费用
            int purchaseAmt =accountOwnerCostSettleDetails.stream().filter(obj ->{return OwnerCashCodeEnum.ACCOUNT_OWNER_DEBT.getCashNo().equals(obj.getSourceCode());})
                    .mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            //5获取车主增值服务费用
            int incrementAmt =accountOwnerCostSettleDetails.stream().filter(obj ->{return OwnerCashCodeEnum.ACCOUNT_OWNER_INCREMENT_COST.getCashNo().equals(obj.getSourceCode());})
                    .mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            //6 获取gps服务费
            int gpsAmt =accountOwnerCostSettleDetails.stream().filter(obj ->{return OwnerCashCodeEnum.ACCOUNT_OWNER_GPS_COST.getCashNo().equals(obj.getSourceCode());})
                    .mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            //7 获取车主油费
            int oilAmt =accountOwnerCostSettleDetails.stream().filter(obj ->{return OwnerCashCodeEnum.ACCOUNT_OWNER_SETTLE_OIL_COST.getCashNo().equals(obj.getSourceCode());})
                    .mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            //8 管理后台补贴
            int consoleSubsidyAmt =accountOwnerCostSettleDetails.stream().filter(obj ->{return RenterCashCodeEnum.ACCOUNT_CONSOLE_RENTER_SUBSIDY_COST.getCashNo().equals(obj.getSourceCode());})
                    .mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            //9 全局的车主订单罚金明细
            int consoleFineAmt =accountOwnerCostSettleDetails.stream().filter(obj ->{return OwnerCashCodeEnum.ACCOUNT_WHOLE_RENTER_FINE_COST.getCashNo().equals(obj.getSourceCode());})
                    .mapToInt(AccountOwnerCostSettleDetailEntity::getAmt).sum();
            entity.setProxyExpenseAmt(proxyExpenseAmt);
            entity.setServiceExpenseAmt(serviceExpenseAmt);
            entity.setSubsidyAmt(subsidyAmount);
            entity.setPurchaseAmt(purchaseAmt);
            entity.setIncrementAmt(incrementAmt);
            entity.setGpsAmt(gpsAmt);
            entity.setOilAmt(oilAmt);
            entity.setConsoleSubsidyAmt(consoleSubsidyAmt);
            entity.setConsoleFineAmt(consoleFineAmt);
            log.info("OrderSettleService insertAccountOwnerCostSettle [{}]", GsonUtils.toJson(entity));
            Cat.logEvent("insertAccountOwnerCostSettle",GsonUtils.toJson(entity));
            accountOwnerCostSettleService.insertAccountOwnerCostSettle(entity);
        }
    }

    /**
     * 根据订单号查询订单 理赔信息
     * @param orderNo
     */
    public boolean getOrderClaim(String orderNo) {
        boolean result = false;
        AccountRenterClaimCostSettleEntity accountRenterClaimCostSettle = accountRenterClaimCostSettleNoTService.getRenterClaimCost(orderNo);
        if(Objects.nonNull(accountRenterClaimCostSettle) && Objects.nonNull(accountRenterClaimCostSettle.getId())){
            result = true;
        }
        return result;
    }

    /**
     * 根据订单号查询订单 暂扣
     * @param orderNo
     * @return
     */
    public boolean getOrderDetain(String orderNo) {
        boolean result = false;
        AccountRenterDetainCostEntity accountRenterDetainCostEntity = accountRenterDetainCostNoTService.getRenterDetaint(orderNo);
        if(Objects.nonNull(accountRenterDetainCostEntity) && Objects.nonNull(accountRenterDetainCostEntity.getId())){
            result = true;
        }
        return result;
    }

    /**
     * 查询实付 违章押金金额
     * @param orderNo
     * @param renterMemNo
     * @return
     */
    public int getWZDepositCostAmt(String orderNo, String renterMemNo) {
        return accountRenterWzDepositNoTService.getAccountRenterWZDepositAmt(orderNo,renterMemNo);
    }

    /**
     * 查询实付 违章押金金额()
     * @param orderNo
     * @param renterMemNo
     * @return
     */
    public int getSurplusWZDepositCostAmt(String orderNo, String renterMemNo) {
        return accountRenterWzDepositDetailNoTService.getSurplusWZDepositCostAmt(orderNo,renterMemNo);
    }

    /**
     * 查询实付 租车费用
     * @param orderNo
     * @param renterMemNo
     * @return
     */
    public int getRentCost(String orderNo, String renterMemNo) {
       return accountRenterCostSettleNoTService.getCostPaidRent(orderNo,renterMemNo);
    }

    /**
     * 查询实付 租车费用
     * @param orderNo
     * @param renterMemNo
     * @return
     */
    public int getCostPaidRentRefundAmt(String orderNo, String renterMemNo) {
          return accountRenterCostDetailNoTService.getRentCostPayByPay(orderNo,renterMemNo);
//        return accountRenterCostSettleNoTService.getCostPaidRentRefundAmt(orderNo,renterMemNo);
    }

    /**
     * 租车费用 抵扣罚金
     * @param vo
     */
    public void deductRentCostToRentFine(AccountRenterCostToFineReqVO vo) {
        accountRenterCostSettleService.deductRentCostToRentFine(vo);
    }

    /**
     * 钱包抵扣 罚金
     * @param vo
     */
    public void deductWalletCostToRentFine(AccountRenterCostToFineReqVO vo) {
        // 1记录钱包抵扣罚金流水
        accountRenterCostSettleService.deductWalletCostToRentFine(vo);
    }

    /**
     * 车俩押金抵扣 罚金
     * @param vo
     */
    public void deductRentDepositToRentFine(OrderCancelRenterDepositReqVO vo) {
        accountRenterDepositService.deductRentDepositToRentFine(vo);
    }
    /**
     * 违章押金抵扣 罚金
     * @param vo
     */
    public void deductRentWzDepositToRentFine(RenterCancelWZDepositCostReqVO vo) {
        accountRenterWzDepositService.deductRentWzDepositToRentFine(vo);
    }

    public void insertAccountPlatformProfit(AccountPlatformProfitEntity accountPlatformProfitEntity) {
        accountPlatformProfitNoTService.insertAccountPlatformProfitEntity(accountPlatformProfitEntity);
    }

    /**
     * 查询实付租车押金
     * @param orderNo
     * @param renterMemNo
     * @return
     */
    public int getRentDepositRealPay(String orderNo, String renterMemNo) {
        AccountRenterDepositResVO vo = accountRenterDepositService.getAccountRenterDepositEntity(orderNo,renterMemNo);
        if(Objects.isNull(vo) || Objects.isNull(vo.getShifuDepositAmt())){
            return 0;
        }
        return vo.getShifuDepositAmt();
    }
}
