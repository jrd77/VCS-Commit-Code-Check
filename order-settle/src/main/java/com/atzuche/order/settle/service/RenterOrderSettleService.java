package com.atzuche.order.settle.service;

import com.atzuche.order.accountrenterdeposit.vo.req.OrderCancelRenterDepositReqVO;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleEntity;
import com.atzuche.order.accountrenterrentcost.service.AccountRenterCostSettleService;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostSettleNoTService;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostToFineReqVO;
import com.atzuche.order.accountrenterwzdepost.vo.req.RenterCancelWZDepositCostReqVO;
import com.atzuche.order.cashieraccount.service.CashierService;
import com.atzuche.order.cashieraccount.service.CashierSettleService;
import com.atzuche.order.commons.enums.FineTypeEnum;
import com.atzuche.order.commons.enums.RenterChildStatusEnum;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import com.atzuche.order.commons.enums.account.debt.DebtTypeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.exceptions.RenterOrderNotFoundException;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercost.entity.ConsoleRenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.settle.exception.RenterCancelSettleException;
import com.atzuche.order.settle.service.notservice.OrderSettleNoTService;
import com.atzuche.order.settle.vo.req.AccountInsertDebtReqVO;
import com.atzuche.order.settle.vo.req.RentCosts;
import com.atzuche.order.settle.vo.req.SettleCancelOrdersAccount;
import com.atzuche.order.settle.vo.req.SettleOrders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/*
 * @Author ZhangBin
 * @Date 2020/3/5 14:20
 * @Description: 租客端结算
 *
 **/
@Service
@Slf4j
public class RenterOrderSettleService {
    @Autowired
    private RenterOrderService renterOrderService;
    @Autowired
    private OrderSettleNoTService orderSettleNoTService;
    @Autowired
    private CashierSettleService cashierSettleService;
    @Autowired
    private CashierService cashierService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private AccountRenterCostSettleService accountRenterCostSettleService;
    @Autowired
    private AccountRenterCostSettleNoTService accountRenterCostSettleNoTService;

    /*
     * @Author ZhangBin
     * @Date 2020/3/5 10:20
     * @Description: 订单取消-车主结算
     * @param orderNo 主订单号
     * @param ownerOrderNo 租客子订单号
     * @param ownerMemNo 租客会员号
     * @return
     **/
    public void settleRenterOrderCancel(String orderNo,String renterOrderNo){
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(orderNo);
        RenterOrderEntity renterOrder = null;
        try{
            renterOrder = renterOrderService.getRenterOrderByRenterOrderNo(renterOrderNo);
            if(renterOrder == null){
                log.error("车租客子订单获取为空 renterOrderNo={}",renterOrderNo);
                throw new RenterOrderNotFoundException(renterOrderNo);
            }
            //1、获取子订单信息
            SettleOrders settleOrders = new SettleOrders();
            settleOrders.setOrderNo(orderNo);
            settleOrders.setRenterOrderNo(renterOrderNo);
            settleOrders.setRenterMemNo(renterOrder.getRenterMemNo());
            settleOrders.setRenterOrder(renterOrder);

            //2 查询租客罚金明细  及 凹凸币补贴
            orderSettleNoTService.getCancelRenterCostSettleDetail(settleOrders);

            //3、获取租客罚金、收益、钱包、租车费用、车辆押金，违章押金
            SettleCancelOrdersAccount settleCancelOrdersAccount = this.initRenterSettleCancelOrdersAccount(settleOrders);

            //4、租客、罚金与收益处理
            this.rentFinehandler(settleOrders,settleCancelOrdersAccount);

            //5、租客、历史欠款处理
            orderSettleNoTService.repayHistoryDebtRentCancel(settleOrders,settleCancelOrdersAccount);

            //6、租客金额 退还 包含 凹凸币，钱包 租车费用 押金 违章押金 退还 （优惠券退还 ->不在结算中做,在取消订单中完成）
            orderSettleNoTService.refundCancelCost(settleOrders,settleCancelOrdersAccount,orderStatusDTO);

            //10 修改订单状态表
            updateOrderStatus(orderStatusDTO,settleOrders,settleCancelOrdersAccount);

        }catch (Exception e){
            e.printStackTrace();
            log.error("订单取消-车主端结算异常orderNo={}，renterOrderNo={}，renterMemNo={}",orderNo,renterOrderNo,renterOrder.getRenterMemNo(),e);
            throw new RenterCancelSettleException();
        }
    }

    private void updateOrderStatus(OrderStatusDTO orderStatusDTO,SettleOrders settleOrders,SettleCancelOrdersAccount settleCancelOrdersAccount){
        //1更新 订单流转状态
        LocalDateTime now = LocalDateTime.now();
        orderStatusDTO.setSettleStatus(SettleStatusEnum.SETTLED.getCode());
        orderStatusDTO.setSettleTime(now);
        orderStatusDTO.setCarDepositSettleStatus(SettleStatusEnum.SETTLED.getCode());
        orderStatusDTO.setCarDepositSettleTime(now);
        orderStatusDTO.setWzSettleStatus(SettleStatusEnum.SETTLED.getCode());
        orderStatusDTO.setWzSettleTime(now);
        orderStatusService.saveOrderStatusInfo(orderStatusDTO);

        //更新应扣
        AccountRenterCostSettleEntity byOrderNo = accountRenterCostSettleNoTService.getByOrderNo(orderStatusDTO.getOrderNo(), settleOrders.getRenterMemNo());
        if(byOrderNo != null){
            int rentCostAmt = settleCancelOrdersAccount.getRentCostAmt();
            int rentSurplusCostAmt = settleCancelOrdersAccount.getRentSurplusCostAmt();
            int rentDepositAmt = settleCancelOrdersAccount.getRentDepositAmt();
            int rentSurplusDepositAmt = settleCancelOrdersAccount.getRentSurplusDepositAmt();
            int rentWzDepositAmt = settleCancelOrdersAccount.getRentWzDepositAmt();
            int rentSurplusWzDepositAmt = settleCancelOrdersAccount.getRentSurplusWzDepositAmt();
            int renWalletAmt = settleCancelOrdersAccount.getRenWalletAmt();
            int rentSurplusWalletAmt = settleCancelOrdersAccount.getRentSurplusWalletAmt();

            int yingKou = (rentCostAmt + rentDepositAmt + rentWzDepositAmt + renWalletAmt)
                    -(rentSurplusCostAmt + rentSurplusDepositAmt + rentSurplusWzDepositAmt + rentSurplusWalletAmt);

            AccountRenterCostSettleEntity accountRenterCostSettleEntity = new AccountRenterCostSettleEntity();
            accountRenterCostSettleEntity.setId(byOrderNo.getId());
            accountRenterCostSettleEntity.setVersion(byOrderNo.getVersion());
            accountRenterCostSettleEntity.setYingkouAmt(yingKou);
            accountRenterCostSettleNoTService.updateAccountRenterCostSettle(accountRenterCostSettleEntity);
                 /*log.error("订单结算-获取租客结算表数据失败，数据为空 orderNo={}",orderStatusDTO.getOrderNo());
            throw  new RenterCancelSettleException();*/
        }

        //更新租客订单状态
        renterOrderService.updateChildStatusByRenterOrderNo(settleOrders.getRenterOrderNo(), RenterChildStatusEnum.SETTLED);
    }

    private SettleCancelOrdersAccount initRenterSettleCancelOrdersAccount(SettleOrders settleOrders) {
        String orderNo = settleOrders.getOrderNo();
        String renterMemNo = settleOrders.getRenterMemNo();
        SettleCancelOrdersAccount settleCancelOrdersAccount = new SettleCancelOrdersAccount();
        // 实付车俩押金金额
        int rentDepositAmt = cashierSettleService.getRentDeposit(orderNo, renterMemNo);
        // 实付钱包金额
        int rentWalletAmt = cashierSettleService.getRentCostPayByWallet(orderNo, renterMemNo);
        // 实付违章押金金额
        int rentWzDepositAmt = cashierSettleService.getSurplusWZDepositCostAmt(orderNo, renterMemNo);
        // 查询实付租车费用金额
        int rentCostAmt = cashierSettleService.getCostPaidRentRefundAmt(orderNo, renterMemNo);
        RentCosts rentCosts = settleOrders.getRentCosts();

        // 租客罚金
        int rentFineAmt =0;
        if(Objects.nonNull(rentCosts) && !CollectionUtils.isEmpty(rentCosts.getRenterOrderFineDeatails())){
            int amt = rentCosts.getRenterOrderFineDeatails()
                    .stream()
                    .filter(obj -> SubsidySourceCodeEnum.RENTER.getCode().equals(obj.getFineSubsidySourceCode()) && FineTypeEnum.CANCEL_FINE.getFineType().equals(obj.getFineType()))
                    .mapToInt(RenterOrderFineDeatailEntity::getFineAmount).sum();
            rentFineAmt = rentFineAmt +amt;
        }
        if(Objects.nonNull(rentCosts) && !CollectionUtils.isEmpty(rentCosts.getConsoleRenterOrderFineDeatails())){
            int amt = rentCosts.getConsoleRenterOrderFineDeatails()
                    .stream()
                    .filter(obj ->SubsidySourceCodeEnum.RENTER.getCode().equals(obj.getFineSubsidySourceCode()) && FineTypeEnum.CANCEL_FINE.getFineType().equals(obj.getFineType()))
                    .mapToInt(ConsoleRenterOrderFineDeatailEntity::getFineAmount).sum();
            rentFineAmt = rentFineAmt +amt;
        }

        // 计算租客凹凸币使用金额
        int renCoinAmt =0;
        if(Objects.nonNull(rentCosts) && !CollectionUtils.isEmpty(rentCosts.getRenterOrderSubsidyDetails())){
            renCoinAmt = rentCosts.getRenterOrderSubsidyDetails()
                    .stream()
                    .filter(obj -> RenterCashCodeEnum.AUTO_COIN_DEDUCT.getCashNo().equals(obj.getSubsidyCostCode()))
                    .mapToInt(RenterOrderSubsidyDetailEntity::getSubsidyAmount).sum();
        }

        //租客收益
        int rentFineIncomeAmt = 0;
        if(Objects.nonNull(rentCosts) && !CollectionUtils.isEmpty(rentCosts.getRenterOrderFineDeatails())){
            int amt = rentCosts.getRenterOrderFineDeatails().stream()
                    .filter(obj ->SubsidySourceCodeEnum.RENTER.getCode().equals(obj.getFineSubsidyCode()) && FineTypeEnum.CANCEL_FINE.getFineType().equals(obj.getFineType()))
                    .mapToInt(RenterOrderFineDeatailEntity::getFineAmount).sum();
            rentFineIncomeAmt = rentFineIncomeAmt +amt;
        }
        if(Objects.nonNull(rentCosts) && !CollectionUtils.isEmpty(rentCosts.getConsoleRenterOrderFineDeatails())){
            int amt = rentCosts.getConsoleRenterOrderFineDeatails()
                    .stream()
                    .filter(obj -> SubsidySourceCodeEnum.RENTER.getCode().equals(obj.getFineSubsidyCode()) && FineTypeEnum.CANCEL_FINE.getFineType().equals(obj.getFineType()))
                    .mapToInt(ConsoleRenterOrderFineDeatailEntity::getFineAmount).sum();
            rentFineIncomeAmt = rentFineIncomeAmt +amt;
        }

        //平台罚金收入
        /*
        1、平台罚金都是收入不会负出
        2、因为平台买了基础保障费，取消订单后，需要收取基础保障费罚金，包含在取消订单的罚金中，找罚金补贴方是平台的即可。
        取消订单，平台罚金收益只会存在下面两张表中：
        `owner_order_fine_deatail`
        `renter_order_fine_deatail`

        3、以下两张表中并不会存平台相关的取消订单罚金费用
         `console_owner_order_fine_deatail`
         `console_renter_order_fine_deatail`
        */
        int platformFineImconeAmt=0;
        if(Objects.nonNull(rentCosts) && !CollectionUtils.isEmpty(rentCosts.getRenterOrderFineDeatails())){
            int amt = rentCosts.getConsoleRenterOrderFineDeatails().stream()
                    .filter(obj -> SubsidySourceCodeEnum.PLATFORM.getCode().equals(obj.getFineSubsidyCode()) && FineTypeEnum.CANCEL_FINE.getFineType().equals(obj.getFineType()))
                    .mapToInt(ConsoleRenterOrderFineDeatailEntity::getFineAmount).sum();
            platformFineImconeAmt = platformFineImconeAmt +amt;
        }

        settleCancelOrdersAccount.setRentFineIncomeAmt(rentFineIncomeAmt);
        settleCancelOrdersAccount.setRentFineAmt(rentFineAmt);
        settleCancelOrdersAccount.setRentFineTotal(rentFineIncomeAmt + rentFineAmt);
        settleCancelOrdersAccount.setRentCostAmt(rentCostAmt);
        settleCancelOrdersAccount.setRentSurplusCostAmt(rentCostAmt);
        settleCancelOrdersAccount.setRentDepositAmt(rentDepositAmt);
        settleCancelOrdersAccount.setRentSurplusDepositAmt(rentDepositAmt);
        settleCancelOrdersAccount.setRentWzDepositAmt(rentWzDepositAmt);
        settleCancelOrdersAccount.setRentSurplusWzDepositAmt(rentWzDepositAmt);
        settleCancelOrdersAccount.setRenWalletAmt(rentWalletAmt);
        settleCancelOrdersAccount.setRentSurplusWalletAmt(rentWalletAmt);
        settleCancelOrdersAccount.setRenCoinAmt(renCoinAmt);
        settleCancelOrdersAccount.setPlatformFineImconeAmt(platformFineImconeAmt);

        return settleCancelOrdersAccount;
    }

    /**
     租客存在罚金 （抵扣优先级 钱包》租车费用》车辆押金》违章押金）
     * @param settleOrders
     * @param settleCancelOrdersAccount
     */
    public void rentFinehandler(SettleOrders settleOrders, SettleCancelOrdersAccount settleCancelOrdersAccount) {
        int rentCostAmt = settleCancelOrdersAccount.getRentCostAmt();
        int rentDepositAmt =  settleCancelOrdersAccount.getRentDepositAmt();
        int rentWzDepositAmt = settleCancelOrdersAccount.getRentWzDepositAmt();
        int renWalletAmt =  settleCancelOrdersAccount.getRenWalletAmt();

        //2 租客收益与罚金对冲值
        int rentFineTotal = settleCancelOrdersAccount.getRentFineTotal();
        //收益进入钱包
        if(rentFineTotal > 0){
            settleCancelOrdersAccount.setRentSurplusWalletAmt(settleCancelOrdersAccount.getRentSurplusWalletAmt() + rentFineTotal);
        }
        //2.1 钱包抵罚金
        if(renWalletAmt>0 && rentFineTotal<0){
            AccountRenterCostToFineReqVO vo = new AccountRenterCostToFineReqVO();
            BeanUtils.copyProperties(settleOrders,vo);
            vo.setMemNo(settleOrders.getRenterMemNo());
            int debtAmt = renWalletAmt + rentFineTotal;
            //计算抵扣金额
            int amt = debtAmt>=0?rentFineTotal:-renWalletAmt;
            vo.setAmt(amt);
            //钱包支付金额抵扣 罚金 insert account_renter_cost_detail
            cashierSettleService.deductWalletCostToRentFine(vo);
            rentFineTotal = renWalletAmt + rentFineTotal;
            settleCancelOrdersAccount.setRentSurplusWalletAmt(settleCancelOrdersAccount.getRentSurplusWalletAmt()+amt);
        }
        //2.2 租车费用抵罚金
        if(rentCostAmt>0 && rentFineTotal<0){
            AccountRenterCostToFineReqVO vo = new AccountRenterCostToFineReqVO();
            BeanUtils.copyProperties(settleOrders,vo);
            vo.setMemNo(settleOrders.getRenterMemNo());
            int debtAmt = rentCostAmt + rentFineTotal;
            //计算抵扣金额
            int amt = debtAmt>=0?rentFineTotal:-rentCostAmt;
            vo.setAmt(amt);
            //租车费用抵扣 罚金 insert account_renter_cost_detail
            cashierSettleService.deductRentCostToRentFine(vo);
            rentFineTotal = rentCostAmt + rentFineTotal;
            settleCancelOrdersAccount.setRentSurplusCostAmt(settleCancelOrdersAccount.getRentSurplusCostAmt()+amt);
        }
        //2.2 车辆押金抵扣
        if(rentDepositAmt>0 && rentFineTotal<0){
            OrderCancelRenterDepositReqVO vo = new OrderCancelRenterDepositReqVO();
            BeanUtils.copyProperties(settleOrders,vo);
            vo.setMemNo(settleOrders.getRenterMemNo());
            int debtAmt = rentDepositAmt + rentFineTotal;
            //计算抵扣金额
            int amt = debtAmt>=0?rentFineTotal:-rentDepositAmt;
            vo.setAmt(amt);
            //押金抵扣抵扣 罚金
            // insert account_renter_deposit_detail
            // update account_renter_deposit
            cashierSettleService.deductRentDepositToRentFine(vo);
            rentFineTotal = rentDepositAmt + rentFineTotal;
            settleCancelOrdersAccount.setRentSurplusDepositAmt(settleCancelOrdersAccount.getRentSurplusDepositAmt()+amt);
        }
        /**
         * 1、不取消的情况下，车辆押金抵扣欠平台的租车费用，车辆押金不够的情况下，会将欠款记录到历史欠款，到违章结算的时候才去抵扣历史欠款
         * 2、当取消订单的情况下，车辆押金结算，违章押金结算是同时进行的，但是基本不会走到这一个if判断
         */
        //2.2 违章押金抵扣
        if(rentWzDepositAmt>0 && rentFineTotal<0){
            RenterCancelWZDepositCostReqVO vo = new RenterCancelWZDepositCostReqVO();
            BeanUtils.copyProperties(settleOrders,vo);
            vo.setMemNo(settleOrders.getRenterMemNo());
            int debtAmt = rentWzDepositAmt + rentFineTotal;
            //计算抵扣金额
            int amt = debtAmt>=0?rentFineTotal:-rentWzDepositAmt;
            vo.setAmt(amt);
            //押金抵扣抵扣 罚金
            //insert account_renter_wz_deposit_detail 违章押金进出明细表
            cashierSettleService.deductRentWzDepositToRentFine(vo);
            rentFineTotal = rentWzDepositAmt + rentFineTotal;
            settleCancelOrdersAccount.setRentSurplusWzDepositAmt(settleCancelOrdersAccount.getRentSurplusWzDepositAmt()+amt);
        }
        //3 钱包，租车费用、车辆押金、违章押金抵扣之后，罚金还有剩余，则罚金走个人历史欠款
        if(rentFineTotal<0){
            //2 记录历史欠款
            AccountInsertDebtReqVO accountInsertDebt = new AccountInsertDebtReqVO();
            BeanUtils.copyProperties(settleOrders,accountInsertDebt);
            accountInsertDebt.setType(DebtTypeEnum.CANCEL.getCode());
            accountInsertDebt.setMemNo(settleOrders.getRenterMemNo());
            accountInsertDebt.setSourceCode(RenterCashCodeEnum.HISTORY_AMT.getCashNo());
            accountInsertDebt.setSourceDetail(RenterCashCodeEnum.HISTORY_AMT.getTxt());
            accountInsertDebt.setAmt(rentFineTotal);
            //历史欠款 有就跟心欠款总额，没有就插入历史欠款。都需要添加欠款流水记录
            cashierService.createDebt(accountInsertDebt);
        }


    }
}
