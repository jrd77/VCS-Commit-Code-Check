package com.atzuche.order.cashieraccount.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleDetailEntity;
import com.atzuche.order.accountownercost.service.notservice.AccountOwnerCostSettleDetailNoTService;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeExamineEntity;
import com.atzuche.order.accountownerincome.service.AccountOwnerIncomeService;
import com.atzuche.order.accountrenterdeposit.service.AccountRenterDepositService;
import com.atzuche.order.accountrenterdeposit.vo.req.CreateOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.DetainRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.req.PayedOrderRenterDepositReqVO;
import com.atzuche.order.accountrenterdeposit.vo.res.AccountRenterDepositResVO;
import com.atzuche.order.accountrenterdetain.service.AccountRenterDetainService;
import com.atzuche.order.accountrenterdetain.vo.req.ChangeDetainRenterDepositReqVO;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleDetailEntity;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleEntity;
import com.atzuche.order.accountrenterrentcost.service.AccountRenterCostSettleService;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostSettleDetailNoTService;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostSettleNoTService;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostDetailReqVO;
import com.atzuche.order.accountrenterrentcost.vo.req.AccountRenterCostReqVO;
import com.atzuche.order.accountrenterwzdepost.service.AccountRenterWzDepositCostService;
import com.atzuche.order.accountrenterwzdepost.service.AccountRenterWzDepositService;
import com.atzuche.order.accountrenterwzdepost.vo.req.*;
import com.atzuche.order.accountrenterwzdepost.vo.res.AccountRenterWZDepositResVO;
import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.exception.SettleAmountException;
import com.atzuche.order.cashieraccount.mapper.CashierMapper;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.cashieraccount.vo.req.CashierDeductDebtReqVO;
import com.atzuche.order.cashieraccount.vo.req.CashierRefundApplyReqVO;
import com.atzuche.order.cashieraccount.vo.res.CashierDeductDebtResVO;
import com.atzuche.order.cashieraccount.vo.res.pay.OrderPayCallBackSuccessVO;
import com.atzuche.order.commons.enums.OrderPayStatusEnum;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.SysOrHandEnum;
import com.atzuche.order.commons.enums.YesNoEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.cashier.CashierRefundApplyStatus;
import com.atzuche.order.commons.enums.cashier.OrderRefundStatusEnum;
import com.atzuche.order.commons.enums.cashier.TransStatusEnum;
import com.atzuche.order.commons.vo.req.income.AccountOwnerIncomExamineVO;
import com.atzuche.order.commons.vo.req.income.AccountOwnerIncomeExamineOpDTO;
import com.atzuche.order.commons.vo.req.income.AccountOwnerIncomeExamineOpReqVO;
import com.atzuche.order.commons.vo.req.income.AccountOwnerIncomeExamineReqVO;
import com.atzuche.order.commons.vo.res.account.income.AdjustOwnerIncomeResVO;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.mq.common.base.BaseProducer;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.settle.service.AccountDebtService;
import com.atzuche.order.settle.vo.req.AccountDeductDebtReqVO;
import com.atzuche.order.settle.vo.req.AccountInsertDebtReqVO;
import com.atzuche.order.settle.vo.res.AccountDebtResVO;
import com.atzuche.order.settle.vo.res.AccountOldDebtResVO;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.autopay.gateway.constant.DataPayTypeConstant;
import com.autoyol.autopay.gateway.vo.req.NotifyDataVo;
import com.autoyol.autopay.gateway.vo.res.AutoPayResultVo;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.event.rabbit.neworder.NewOrderMQActionEventEnum;
import com.autoyol.event.rabbit.neworder.OrderRenterPayAmtSuccessMq;
import com.autoyol.event.rabbit.neworder.OrderRenterPaySuccessMq;
import com.dianping.cat.Cat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 收银台操作
 *
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Service
@Slf4j
public class CashierService {
    @Autowired AccountRenterDepositService accountRenterDepositService;
    @Autowired AccountRenterWzDepositService accountRenterWzDepositService;
    @Autowired AccountDebtService accountDebtService;
    @Autowired CashierRefundApplyNoTService cashierRefundApplyNoTService;
    @Autowired AccountOwnerIncomeService accountOwnerIncomeService;
    @Autowired AccountRenterCostSettleService accountRenterCostSettleService;
    @Autowired AccountRenterWzDepositCostService accountRenterWzDepositCostService;
    @Autowired RenterOrderCostCombineService renterOrderCostCombineService;
    @Autowired CashierNoTService cashierNoTService;
    @Autowired private AccountRenterCostSettleDetailNoTService accountRenterCostSettleDetailNoTService;
    @Autowired private AccountOwnerCostSettleDetailNoTService accountOwnerCostSettleDetailNoTService;
    @Autowired private OrderStatusService orderStatusService;
    @Autowired private OrderFlowService orderFlowService;
    @Autowired private AccountRenterCostSettleNoTService accountRenterCostSettleNoTService;
    @Autowired private CashierMapper cashierMapper;
    @Autowired private AccountRenterDetainService accountRenterDetainService;
    @Autowired
    private BaseProducer baseProducer;
    @Autowired
    private CashierShishouService cashierShishouService;
    @Autowired
    private MemberSecondSettleService memberSecondSettleService;
    
    /**  *************************************** 租车费用 start****************************************************/

    public AccountRenterCostSettleEntity getAccountRenterCostSettle(String orderNo, String memNo){
       return accountRenterCostSettleNoTService.getCostPaidRentSettle(orderNo,memNo);
    }

    /**  *************************************** 租车费用 end****************************************************/

    /**  *************************************** 车辆押金 start****************************************************/
    /**
     * 记录应收车俩押金
     * 下单成功  调收银台 记录 车俩押金应付
     */
    @Transactional(rollbackFor=Exception.class)
    public void insertRenterDeposit(CreateOrderRenterDepositReqVO createOrderRenterDepositReqVO){
        Assert.notNull(createOrderRenterDepositReqVO, ErrorCode.PARAMETER_ERROR.getText());
        createOrderRenterDepositReqVO.check();
        createOrderRenterDepositReqVO.setPayKind(DataPayKindConstant.RENT);
//        //1 收银台记录违章押金 应付
//        cashierNoTService.insertRenterDeposit(createOrderRenterDepositReqVO);
        //2 车俩押金记录应付
        accountRenterDepositService.insertRenterDeposit(createOrderRenterDepositReqVO);
    }

    /**
     * 扣减/暂扣 车俩押金
     */
    @Transactional(rollbackFor=Exception.class)
    public void detainRenterDeposit(DetainRenterDepositReqVO detainRenterDepositReqVO){
        //1 扣除全部 剩余可用车辆押金
        int depositDetailId = accountRenterDepositService.detainRenterDepositNew(detainRenterDepositReqVO);
        //2 暂扣表记录暂扣车辆押金
        ChangeDetainRenterDepositReqVO changeDetainRenterDepositReqVO = getCangeDetainRenterDepositReqVO(detainRenterDepositReqVO,depositDetailId);
        accountRenterDetainService.changeRenterDetainCost(changeDetainRenterDepositReqVO);
    }

    /**
     * 构造暂扣资金参数
     * @param detainRenterDepositReqVO
     * @param depositDetailId
     * @return
     */
    private ChangeDetainRenterDepositReqVO getCangeDetainRenterDepositReqVO(DetainRenterDepositReqVO detainRenterDepositReqVO,int depositDetailId) {
        ChangeDetainRenterDepositReqVO result = new ChangeDetainRenterDepositReqVO();
        BeanUtils.copyProperties(detainRenterDepositReqVO,result);
        result.setIsDetain(YesNoEnum.YES);
        result.setUniqueNo(String.valueOf(depositDetailId));
        result.setRenterCashCodeEnum(RenterCashCodeEnum.ACCOUNT_DEPOSIT_DETAIN_CAR_AMT);
        return result;
    }

    private ChangeDetainRenterDepositReqVO getCangeDetainRenterWZDepositReqVO(OrderRenterDepositWZDetainReqVO orderRenterDepositWZDetainReqVO, int detailId) {
        ChangeDetainRenterDepositReqVO result = new ChangeDetainRenterDepositReqVO();
        BeanUtils.copyProperties(orderRenterDepositWZDetainReqVO,result);
        result.setIsDetain(YesNoEnum.YES);
        result.setUniqueNo(String.valueOf(detailId));
        result.setRenterCashCodeEnum(RenterCashCodeEnum.ACCOUNT_WZ_DEPOSIT_DETAIN_CAR_AMT);
        return result;
    }


    /**
     * 查询应收车辆押金
     */
    public AccountRenterDepositResVO getRenterDepositEntity(String orderNo, String memNo){
        return accountRenterDepositService.getAccountRenterDepositEntity(orderNo, memNo);
    }
    /**
     * 查询应收车辆押金
     */
    public int getRenterDeposit(String orderNo, String memNo){
        return accountRenterDepositService.getRenterDeposit(orderNo, memNo);
    }

    /**  ***************************************** 押金 end *************************************************     */
    /**  ***************************************** 违章押金 start *************************************************     */

    /**
     * 记录应收违章押金
     * 下单成功  调收银台 记录 违章押金应付
     */
    @Transactional(rollbackFor=Exception.class)
    public void insertRenterWZDeposit(CreateOrderRenterWZDepositReqVO createOrderRenterWZDepositReq){
        Assert.notNull(createOrderRenterWZDepositReq, ErrorCode.PARAMETER_ERROR.getText());
        createOrderRenterWZDepositReq.check();
        createOrderRenterWZDepositReq.setPayKind(DataPayKindConstant.DEPOSIT);
//        //1 收银台记录违章押金 应付
//        cashierNoTService.insertRenterWZDeposit(createOrderRenterWZDepositReq);
        //2 违章押金记录应付
        accountRenterWzDepositService.insertRenterWZDeposit(createOrderRenterWZDepositReq);
    }


    /**
     * 扣减/暂扣 违章押金
     */
    @Transactional(rollbackFor=Exception.class)
    public void detainRenterWZDeposit(OrderRenterDepositWZDetainReqVO orderRenterDepositWZDetainReqVO){
        //1 违章账户金额扣除
        int detailId = accountRenterWzDepositService.updateRenterWZDepositDetain(orderRenterDepositWZDetainReqVO);
        //2 暂扣记录表数据更新
        ChangeDetainRenterDepositReqVO changeDetainRenterDepositReqVO = getCangeDetainRenterWZDepositReqVO(orderRenterDepositWZDetainReqVO,detailId);
        accountRenterDetainService.changeRenterDetainCost(changeDetainRenterDepositReqVO);
    }


    /**
     * 查询暂扣金额
     */
    public int getRenterDetain(String orderNo,RenterCashCodeEnum renterCashCode){
        return accountRenterDetainService.getRenterDetain(orderNo,renterCashCode);
    }
    /**
     * 查询违章押金
     */
    public int getRenterWZDeposit(String orderNo, String memNo){
        return accountRenterWzDepositService.getRenterWZDeposit(orderNo, memNo);
    }

    /**
     * 查询违章押金余额
     */
    public AccountRenterWZDepositResVO getRenterWZDepositEntity(String orderNo, String memNo){
        return accountRenterWzDepositService.getAccountRenterWZDeposit(orderNo, memNo);
    }

    /**  ***************************************** 违章押金 end *************************************************     */

    /**  ***************************************** 历史欠款 start  *************************************************    */
   
    /**
     * 7）押金抵扣历史欠款
     */
    @Transactional(rollbackFor=Exception.class)
    public CashierDeductDebtResVO deductDebt(CashierDeductDebtReqVO cashierDeductDebtReq){
        Assert.notNull(cashierDeductDebtReq, ErrorCode.PARAMETER_ERROR.getText());
        cashierDeductDebtReq.check();
        //1 查询历史总欠款
        int debtAmt = accountDebtService.getAccountDebtNumByMemNo(cashierDeductDebtReq.getMemNo());
        if(debtAmt>=0){
            return null;
        }
        //2 抵扣
        AccountDeductDebtReqVO accountDeductDebt = new AccountDeductDebtReqVO();
        BeanUtils.copyProperties(cashierDeductDebtReq,accountDeductDebt);
        accountDeductDebt.setSourceCode(cashierDeductDebtReq.getRenterCashCodeEnum().getCashNo());
        accountDeductDebt.setSourceDetail(cashierDeductDebtReq.getRenterCashCodeEnum().getTxt());
        //返回真实抵扣金额
        int debtedAmt = accountDebtService.deductDebt(accountDeductDebt);
        //3 记录押金资金明细 抵扣记录
        DetainRenterDepositReqVO detainRenterDepositReqVO = new DetainRenterDepositReqVO();
        BeanUtils.copyProperties(cashierDeductDebtReq,detainRenterDepositReqVO);
        detainRenterDepositReqVO.setAmt(-debtedAmt);
        detainRenterDepositReqVO.setRenterCashCodeEnum(RenterCashCodeEnum.SETTLE_DEPOSIT_TO_HISTORY_AMT);
        //update account_renter_deposit
        //insert account_renter_deposit_detail
        int id = accountRenterDepositService.detainRenterDeposit(detainRenterDepositReqVO);
        // 4 记录结算费用 抵扣记录
        AccountRenterCostSettleDetailEntity renterCostSettleDetail = new AccountRenterCostSettleDetailEntity();
        BeanUtils.copyProperties(cashierDeductDebtReq,renterCostSettleDetail);
        renterCostSettleDetail.setUniqueNo(String.valueOf(id));
        renterCostSettleDetail.setCostCode(RenterCashCodeEnum.SETTLE_DEPOSIT_TO_HISTORY_AMT.getCashNo());
        renterCostSettleDetail.setCostDetail(RenterCashCodeEnum.SETTLE_DEPOSIT_TO_HISTORY_AMT.getTxt());
        renterCostSettleDetail.setAmt(-debtedAmt);
        //insert account_renter_cost_settle_detail
        accountRenterCostSettleDetailNoTService.insertAccountRenterCostSettleDetail(renterCostSettleDetail);
        return new CashierDeductDebtResVO(cashierDeductDebtReq, debtedAmt,id);
    }
    /**
     * 7）剩余租车费用抵扣历史欠款
     */
    @Transactional(rollbackFor=Exception.class)
    public CashierDeductDebtResVO deductDebtByRentCost(CashierDeductDebtReqVO cashierDeductDebtReq){
        Assert.notNull(cashierDeductDebtReq, ErrorCode.PARAMETER_ERROR.getText());
        cashierDeductDebtReq.check();
        //1 查询历史总欠款  account_debt
        int debtAmt = accountDebtService.getAccountDebtNumByMemNo(cashierDeductDebtReq.getMemNo());
        if(debtAmt>=0){
            return null;
        }
        //2 抵扣
        AccountDeductDebtReqVO accountDeductDebt = new AccountDeductDebtReqVO();
        BeanUtils.copyProperties(cashierDeductDebtReq,accountDeductDebt);
        accountDeductDebt.setSourceCode(cashierDeductDebtReq.getRenterCashCodeEnum().getCashNo());
        accountDeductDebt.setSourceDetail(cashierDeductDebtReq.getRenterCashCodeEnum().getTxt());
        //返回真实抵扣金额
        int debtedAmt = accountDebtService.deductDebt(accountDeductDebt);
        //3 记录租车费用资金 进出记录
        AccountRenterCostDetailReqVO accountRenterCostChangeReqVO = new AccountRenterCostDetailReqVO();
        BeanUtils.copyProperties(cashierDeductDebtReq,accountRenterCostChangeReqVO);
        accountRenterCostChangeReqVO.setAmt(-debtedAmt);
        accountRenterCostChangeReqVO.setRenterCashCodeEnum(RenterCashCodeEnum.SETTLE_RENT_COST_TO_HISTORY_AMT);
        //insert  account_renter_cost_detail
        int id = accountRenterCostSettleService.deductDepositToRentCost(accountRenterCostChangeReqVO);
        // 4 记录结算费用 抵扣记录
        AccountRenterCostSettleDetailEntity renterCostSettleDetail = new AccountRenterCostSettleDetailEntity();
        BeanUtils.copyProperties(cashierDeductDebtReq,renterCostSettleDetail);
        renterCostSettleDetail.setUniqueNo(String.valueOf(id));
        renterCostSettleDetail.setCostCode(RenterCashCodeEnum.SETTLE_RENT_COST_TO_HISTORY_AMT.getCashNo());
        renterCostSettleDetail.setCostDetail(RenterCashCodeEnum.SETTLE_RENT_COST_TO_HISTORY_AMT.getTxt());
        renterCostSettleDetail.setAmt(-debtedAmt);
        //insert account_renter_cost_settle_detail
        accountRenterCostSettleDetailNoTService.insertAccountRenterCostSettleDetail(renterCostSettleDetail);
        return new CashierDeductDebtResVO(cashierDeductDebtReq, debtedAmt,id);
    }
    /**
     * 车主收益 抵扣历史欠款
     */
    @Transactional(rollbackFor=Exception.class)
    public CashierDeductDebtResVO deductDebtByOwnerIncome(CashierDeductDebtReqVO cashierDeductDebtReq){
        Assert.notNull(cashierDeductDebtReq, ErrorCode.PARAMETER_ERROR.getText());
        cashierDeductDebtReq.check();
        //1 查询历史总欠款
        int debtAmt = accountDebtService.getAccountDebtNumByMemNo(cashierDeductDebtReq.getMemNo());
        if(debtAmt>=0){
            return null;
        }
        //2 抵扣
        AccountDeductDebtReqVO accountDeductDebt = new AccountDeductDebtReqVO();
        BeanUtils.copyProperties(cashierDeductDebtReq,accountDeductDebt);
        accountDeductDebt.setSourceCode(cashierDeductDebtReq.getRenterCashCodeEnum().getCashNo());
        accountDeductDebt.setSourceDetail(cashierDeductDebtReq.getRenterCashCodeEnum().getTxt());
        //真实抵扣金额 正值
        int debtedAmt = accountDebtService.deductDebt(accountDeductDebt);
        //3 记录车主结算费用总额 及 车主费用结算明细表
        AccountOwnerCostSettleDetailEntity entity = new AccountOwnerCostSettleDetailEntity();
        BeanUtils.copyProperties(cashierDeductDebtReq,entity);
        entity.setSourceCode(RenterCashCodeEnum.SETTLE_OWNER_INCOME_TO_HISTORY_AMT.getCashNo());
        entity.setSourceDetail(RenterCashCodeEnum.SETTLE_OWNER_INCOME_TO_HISTORY_AMT.getTxt());
        entity.setAmt(-debtedAmt);
        int id = accountOwnerCostSettleDetailNoTService.insertAccountOwnerCostSettleDetail(entity);
        return new CashierDeductDebtResVO(cashierDeductDebtReq, debtedAmt,id);
    }
    /**
     * 查询用户历史欠款信息
     * @param memNo
     * @return
     */
    public AccountDebtResVO getAccountDebtByMemNo(String memNo) {
        return accountDebtService.getAccountDebtByMemNo(memNo);
    }
    /**
     * 用户订单结算 产生历史欠款
     */
    public int createDebt(AccountInsertDebtReqVO accountInsertDebt){
       return accountDebtService.insertDebt(accountInsertDebt);
    }

    /**  ***************************************** 历史欠款 end ************************************************* */
    /**  ***************************************** 退还押金 start ************************************************* */

    /**
     * 结算退还车辆押金
     */
    @Transactional(rollbackFor=Exception.class)
    public int refundDeposit(CashierRefundApplyReqVO cashierRefundApplyReq){
        Assert.notNull(cashierRefundApplyReq, ErrorCode.PARAMETER_ERROR.getText());
        cashierRefundApplyReq.check();
        //1 记录退还记录
        int id = cashierRefundApplyNoTService.insertRefundDeposit(cashierRefundApplyReq);
        //2 记录费用平账
        DetainRenterDepositReqVO detainRenterDepositReqVO = new DetainRenterDepositReqVO();
        BeanUtils.copyProperties(cashierRefundApplyReq,detainRenterDepositReqVO);
        detainRenterDepositReqVO.setUniqueNo(Integer.toString(id));
        //2 押金账户资金转移接口
        int depositDetailId = accountRenterDepositService.detainRenterDeposit(detainRenterDepositReqVO);
        // 3 更新押金结算状态
        accountRenterDepositService.updateOrderDepositSettle(detainRenterDepositReqVO);
        return depositDetailId;
    }
    
//    @Transactional(rollbackFor=Exception.class)
    public int refundDepositPreAuth(CashierRefundApplyReqVO cashierRefundApplyReq){
        Assert.notNull(cashierRefundApplyReq, ErrorCode.PARAMETER_ERROR.getText());
        cashierRefundApplyReq.check();
        //1 记录退还记录
        return cashierRefundApplyNoTService.insertRefundDeposit(cashierRefundApplyReq);
    }
    
    
    
    
    /**
     * 结算退还租车费用
     */
    @Transactional(rollbackFor=Exception.class)
    public int refundRentCost(CashierRefundApplyReqVO cashierRefundApplyReq){
        Assert.notNull(cashierRefundApplyReq, ErrorCode.PARAMETER_ERROR.getText());
        cashierRefundApplyReq.check();
        //1 记录退还记录
        Integer id = cashierRefundApplyNoTService.insertRefundDeposit(cashierRefundApplyReq);
        //2 记录费用平账
        AccountRenterCostDetailReqVO accountRenterCostDetail = new AccountRenterCostDetailReqVO();
        BeanUtils.copyProperties(cashierRefundApplyReq,accountRenterCostDetail);
        //3 发消息通知 存在退款
        return accountRenterCostSettleService.refundRenterCostDetail(accountRenterCostDetail);
    }

    /**
     * 结算退还租车费用 钱包
     */
    @Transactional(rollbackFor=Exception.class)
    public int refundRentCostWallet(AccountRenterCostDetailReqVO accountRenterCostDetail){
        Assert.notNull(accountRenterCostDetail, ErrorCode.PARAMETER_ERROR.getText());
        return accountRenterCostSettleService.refundRenterCostDetail(accountRenterCostDetail);
    }
    /**
     * 退还违章押金
     */
    @Transactional(rollbackFor=Exception.class)
    public int refundWZDeposit(CashierRefundApplyReqVO cashierRefundApplyReq){
        Assert.notNull(cashierRefundApplyReq, ErrorCode.PARAMETER_ERROR.getText());
        cashierRefundApplyReq.check();

        //1 记录退还记录
        Integer id = cashierRefundApplyNoTService.insertRefundDeposit(cashierRefundApplyReq);
        //2 记录费用平账
        PayedOrderRenterDepositWZDetailReqVO payedOrderRenterWZDepositDetail = new PayedOrderRenterDepositWZDetailReqVO();
        BeanUtils.copyProperties(cashierRefundApplyReq,payedOrderRenterWZDepositDetail);
        payedOrderRenterWZDepositDetail.setUniqueNo(id.toString());
        accountRenterWzDepositService.updateRenterWZDepositChange(payedOrderRenterWZDepositDetail);
        
        return id;
    }
    
//    @Transactional(rollbackFor=Exception.class)
    public int refundWZDepositPreAuth(CashierRefundApplyReqVO cashierRefundApplyReq){
        Assert.notNull(cashierRefundApplyReq, ErrorCode.PARAMETER_ERROR.getText());
        cashierRefundApplyReq.check();
        //1 记录退还记录
        Integer id = cashierRefundApplyNoTService.insertRefundDeposit(cashierRefundApplyReq);
        return id;
    }
    
    /**
     * 租车押金预授权退款
     * @param rentSurplusDepositAmt
     * @param cashierEntity
     * @param cashierRefundApply
     * @param cashCode
     */
    public int refundDepositPreAuthAll(int rentSurplusDepositAmt, CashierEntity cashierEntity, CashierRefundApplyReqVO cashierRefundApply,RenterCashCodeEnum cashCode) {
		//是否存在预授权完成操作
		boolean isExistsPreAuthFinish = (cashierEntity.getPayAmt() - Math.abs(rentSurplusDepositAmt) > 0);
		
		int id = 0;
		//预授权解冻，金额不允许为0
		//考虑全额预授权解冻
		if(Math.abs(rentSurplusDepositAmt) != 0){
			//超出金额的做限制,退款的超出做全额解冻。
			int refundAmt = -rentSurplusDepositAmt;
			if(Math.abs(refundAmt) > cashierEntity.getPayAmt()) {
				refundAmt = cashierEntity.getPayAmt();
			}
			
			//预授权  退款就是解冻，余下的就是预授权完成
			cashierRefundApply.setPayType(DataPayTypeConstant.PRE_VOID); //解冻
			cashierRefundApply.setAmt(refundAmt);
		    cashierRefundApply.setRenterCashCodeEnum(cashCode);
		    cashierRefundApply.setRemake(cashCode.getTxt());
		    cashierRefundApply.setFlag(RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT.getCashNo());
		    cashierRefundApply.setType(SysOrHandEnum.SYSTEM.getStatus());
		    cashierRefundApply.setQn(cashierEntity.getQn());
		    cashierRefundApply.setPayKind(DataPayKindConstant.RENT);
		    //需要在预授权完成之后再做解冻操作
		    if(isExistsPreAuthFinish) {  //存在预授权完成的时候，暂停解冻。
		    	cashierRefundApply.setStatus(CashierRefundApplyStatus.STOP_FOR_REFUND.getCode());
		    }
		    
		    id = this.refundDeposit(cashierRefundApply);
		}
		
		//添加预授权完成记录,金额不允许为0 
		//考虑全额预授权完成
		//预授权完成金额不会比 支付的金额大
		if(isExistsPreAuthFinish) {  // > 0
		    cashierRefundApply.setPayType(DataPayTypeConstant.PRE_FINISH); //预授权完成
			cashierRefundApply.setAmt( cashierEntity.getPayAmt() - Math.abs(rentSurplusDepositAmt) );
		    cashierRefundApply.setRenterCashCodeEnum(cashCode);
		    cashierRefundApply.setRemake(cashCode.getTxt());
		    cashierRefundApply.setFlag(RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT.getCashNo());
		    cashierRefundApply.setType(SysOrHandEnum.SYSTEM.getStatus());
		    cashierRefundApply.setQn(cashierEntity.getQn());
		    cashierRefundApply.setPayKind(DataPayKindConstant.RENT);
		    //预授权完成 进行中
		    cashierRefundApply.setStatus(CashierRefundApplyStatus.WAITING_FOR_REFUND.getCode());
		    id = this.refundDepositPreAuth(cashierRefundApply);  //仅仅提交预授权完成记录
		}
		
		return id;
	}

    /**
     * 租车押金消费退款
     *
     * @param rentSurplusDepositAmt 退还金额
     * @param cashierEntity         收银信息
     * @param cashierRefundApply    退款申请信息
     * @param cashCode              费用编码
     * @return int
     */
    public int refundDepositPurchase(int rentSurplusDepositAmt,
                                     CashierEntity cashierEntity, CashierRefundApplyReqVO cashierRefundApply, RenterCashCodeEnum cashCode) {
        log.info("CashierService.refundDepositPurchase >> rentSurplusDepositAmt:[{}], cashierEntity:[{}], " +
                        "cashierRefundApply:[{}], cashCode:[{}]", rentSurplusDepositAmt, JSON.toJSONString(cashierEntity),
                JSON.toJSONString(cashierRefundApply), JSON.toJSONString(cashCode));
        //退货
        cashierRefundApply.setPayType(DataPayTypeConstant.PUR_RETURN);
        cashierRefundApply.setAmt(-rentSurplusDepositAmt);
        cashierRefundApply.setRenterCashCodeEnum(cashCode);
        cashierRefundApply.setRemake(cashCode.getTxt());
        cashierRefundApply.setFlag(RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT.getCashNo());
        cashierRefundApply.setType(SysOrHandEnum.SYSTEM.getStatus());
        cashierRefundApply.setQn(cashierEntity.getQn());
        log.info("CashierService.refundDepositPurchase >> reset cashierRefundApply:[{}]",
                JSON.toJSONString(cashierRefundApply));
        return this.refundDeposit(cashierRefundApply);
    }

    /**
     * 违章押金预授权退款方法
     *
     * @param rentSurplusWzDepositAmt 退还金额
     * @param cashierEntity           收银信息
     * @param cashierRefundApply      退款申请信息
     * @param cashCode                费用编码
     * @return int
     */
    public int refundWzDepositPreAuthAll(int rentSurplusWzDepositAmt, CashierEntity cashierEntity, CashierRefundApplyReqVO cashierRefundApply, RenterCashCodeEnum cashCode) {
        log.info("CashierService.refundDepositPurchase >> rentSurplusWzDepositAmt:[{}], cashierEntity:[{}], " +
                        "cashierRefundApply:[{}], cashCode:[{}]", rentSurplusWzDepositAmt, JSON.toJSONString(cashierEntity),
                JSON.toJSONString(cashierRefundApply), JSON.toJSONString(cashCode));
        //是否存在预授权完成操作
        boolean isExistsPreAuthFinish = (cashierEntity.getPayAmt() - Math.abs(rentSurplusWzDepositAmt) > 0);
        int id = 0;

        //预授权  退款就是解冻，余下的就是预授权完成
        //预授权解冻，金额不允许为0
        //考虑全额预授权解冻
        if (Math.abs(rentSurplusWzDepositAmt) != 0) {
            //超出金额的做限制,退款的超出做全额解冻。
            int refundAmt = -rentSurplusWzDepositAmt;
            if (Math.abs(refundAmt) > cashierEntity.getPayAmt()) {
                refundAmt = cashierEntity.getPayAmt();
            }

            cashierRefundApply.setPayType(DataPayTypeConstant.PRE_VOID); //解冻
            cashierRefundApply.setAmt(refundAmt);
            cashierRefundApply.setRenterCashCodeEnum(cashCode);
            cashierRefundApply.setRemake(cashCode.getTxt());
            cashierRefundApply.setFlag(RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT.getCashNo());
            cashierRefundApply.setType(SysOrHandEnum.SYSTEM.getStatus());
            cashierRefundApply.setQn(cashierEntity.getQn());
            cashierRefundApply.setPayKind(DataPayKindConstant.DEPOSIT);

            //需要在预授权完成之后再做解冻操作
            if (isExistsPreAuthFinish) {
                cashierRefundApply.setStatus(CashierRefundApplyStatus.STOP_FOR_REFUND.getCode());
            }

            id = this.refundWZDeposit(cashierRefundApply);
        }

        //添加预授权完成记录
        //添加预授权完成记录,金额不允许为0
        //考虑全额预授权完成
        if (isExistsPreAuthFinish) {
            cashierRefundApply.setPayType(DataPayTypeConstant.PRE_FINISH); //预授权完成
            cashierRefundApply.setAmt(cashierEntity.getPayAmt() - Math.abs(rentSurplusWzDepositAmt));
            cashierRefundApply.setRenterCashCodeEnum(cashCode);
            cashierRefundApply.setRemake(cashCode.getTxt());
            cashierRefundApply.setFlag(RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT.getCashNo());
            cashierRefundApply.setType(SysOrHandEnum.SYSTEM.getStatus());
            cashierRefundApply.setQn(cashierEntity.getQn());
            cashierRefundApply.setPayKind(DataPayKindConstant.DEPOSIT);
            //预授权完成 进行中
            cashierRefundApply.setStatus(CashierRefundApplyStatus.WAITING_FOR_REFUND.getCode());
            id = this.refundWZDepositPreAuth(cashierRefundApply);  //仅仅提交预授权完成记录
        }
        return id;
    }
    
    /**
     * 违章押金的消费退货处理
     * @param rentSurplusWzDepositAmt
     * @param cashierEntity
     * @param cashierRefundApply
     * @param cashCode
     */
	public int refundWzDepositPurchase(int rentSurplusWzDepositAmt,
			CashierEntity cashierEntity, CashierRefundApplyReqVO cashierRefundApply,RenterCashCodeEnum cashCode) {
		cashierRefundApply.setPayType(DataPayTypeConstant.PUR_RETURN); //退货
		cashierRefundApply.setAmt(-rentSurplusWzDepositAmt);
		cashierRefundApply.setRenterCashCodeEnum(cashCode);
		cashierRefundApply.setRemake(cashCode.getTxt());
		cashierRefundApply.setFlag(RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT.getCashNo());
		cashierRefundApply.setType(SysOrHandEnum.SYSTEM.getStatus());
		cashierRefundApply.setQn(cashierEntity.getQn());
		cashierRefundApply.setPayKind(DataPayKindConstant.DEPOSIT);
		return this.refundWZDeposit(cashierRefundApply);
	}
	
    /**  ***************************************** 退还押金 end ************************************************* */

    /**  ***************************************** 车主收益 start ************************************************* */

    /**
     * 结算产生车主待审核收益
     */
    public void insertOwnerIncomeExamine(AccountOwnerIncomeExamineReqVO accountOwnerIncomeExamineReq){
        accountOwnerIncomeService.insertOwnerIncomeExamine(accountOwnerIncomeExamineReq);
    }
    /**
     * 收益审核通过 更新车主收益信息
     */
    public List<AdjustOwnerIncomeResVO> examineOwnerIncomeExamine(AccountOwnerIncomeExamineOpDTO accountOwnerIncomeExamineOpDTO){
        List<AdjustOwnerIncomeResVO> adjustOwnerIncomeResVOS = new ArrayList<>();
        List<AccountOwnerIncomExamineVO> accountOwnerIncomExamineVOS = accountOwnerIncomeExamineOpDTO.getAccountOwnerIncomExamineVOS();
        AccountOwnerIncomeExamineOpReqVO accountOwnerIncomeExamineOpReq = null;
        for(AccountOwnerIncomExamineVO accountOwnerIncomExamineVO : accountOwnerIncomExamineVOS){
            Integer examineId = accountOwnerIncomExamineVO.getAccountOwnerIncomeExamineId();
            try{
                accountOwnerIncomeExamineOpReq = new AccountOwnerIncomeExamineOpReqVO();
                accountOwnerIncomeExamineOpReq.setOrderNo(accountOwnerIncomExamineVO.getOrderNo());
                accountOwnerIncomeExamineOpReq.setMemNo(accountOwnerIncomExamineVO.getMemNo());
                accountOwnerIncomeExamineOpReq.setStatus(accountOwnerIncomeExamineOpDTO.getStatus());
                accountOwnerIncomeExamineOpReq.setUpdateOp(accountOwnerIncomeExamineOpDTO.getUpdateOp());
                accountOwnerIncomeExamineOpReq.setOpName(accountOwnerIncomeExamineOpDTO.getOpName());
                accountOwnerIncomeExamineOpReq.setAccountOwnerIncomeExamineId(examineId);
                
                boolean isSecondFlag =
                        memberSecondSettleService.judgeIsSecond(accountOwnerIncomeExamineOpReq.getOrderNo());
                int id = accountOwnerIncomeService.examineOwnerIncomeExamine(accountOwnerIncomeExamineOpReq,isSecondFlag);

                AdjustOwnerIncomeResVO adjustOwnerIncomeResVO = new AdjustOwnerIncomeResVO();
                adjustOwnerIncomeResVO.setMemNo(accountOwnerIncomExamineVO.getMemNo());
                adjustOwnerIncomeResVO.setOrderNo(accountOwnerIncomExamineVO.getOrderNo());
                adjustOwnerIncomeResVO.setExamineId(examineId);
                ///
                adjustOwnerIncomeResVO.setAccountOwnerIncomeDetailId(id);
                
                adjustOwnerIncomeResVOS.add(adjustOwnerIncomeResVO);
            }catch (Exception e){
                log.error("本条订单收益审核失败accountOwnerIncomeExamineOpReq={}", JSON.toJSONString(accountOwnerIncomeExamineOpReq),e);
            }
        }
        return adjustOwnerIncomeResVOS;
    }

    /**
     * 查询收益信息
     */
    public List<AccountOwnerIncomeExamineEntity> getOwnerIncomeByOrderAndType(String orderNo, String memNo, int type){
        return accountOwnerIncomeService.getOwnerIncomeByOrderAndType(orderNo,memNo,type);
    }


    /**  ***************************************** 车主收益 end ************************************************* */

    /**  ***************************************** 违章费用 start ************************************************* */

    /**
     * 违章费用资金进出
     */
    @Transactional(rollbackFor=Exception.class)
    public void changeWZDepositCost(RenterWZDepositCostReqVO renterWZDepositCost){
        accountRenterWzDepositCostService.changeWZDepositCost(renterWZDepositCost);
    }

    /**  ***************************************** 违章费用 end ************************************************* */

    /**  ***************************************** 结算租车费用（三方） start ************************************************* */


    /**  ***************************************** 结算租车费用（三方） end ************************************************* */

    /**
     * MQ成功异步回调
     * @param lstNotifyDataVo
     */
    @Transactional(rollbackFor=Exception.class)
    public OrderPayCallBackSuccessVO callBackSuccess(List<NotifyDataVo> lstNotifyDataVo) {
        OrderPayCallBackSuccessVO vo = new OrderPayCallBackSuccessVO();
        if(!CollectionUtils.isEmpty(lstNotifyDataVo)){
            for (NotifyDataVo notifyDataVo : lstNotifyDataVo) {
                //2支付成功回调
                if (DataPayTypeConstant.PAY_PUR.equals(notifyDataVo.getPayType()) || DataPayTypeConstant.PAY_PRE.equals(notifyDataVo.getPayType())) {
                    int settleAmount = notifyDataVo.getSettleAmount() == null ? 0 : Integer.parseInt(notifyDataVo.getSettleAmount());
                    //负数1 特殊情况。
                    //企业用户金额为0,允许通过。
                    if (settleAmount == -1) {
                        //金额为0的异常情况。
                        Cat.logError("params=" + GsonUtils.toJson(notifyDataVo), new SettleAmountException());
                        log.error("支付异步通知rabbitmq接收到的金额为0异常,params=[{}],程序终止。", GsonUtils.toJson(notifyDataVo));
                    } else {
                        payOrderCallBackSuccess(notifyDataVo, vo);
                    }
                } else { //退款
                    int settleAmount = notifyDataVo.getSettleAmount() == null ? 0 : Integer.parseInt(notifyDataVo.getSettleAmount());
                    if (settleAmount <= 0) {  //含-1的情况
                        //金额为0的异常情况。
                        Cat.logError("params=" + GsonUtils.toJson(notifyDataVo), new SettleAmountException());
                        log.error("退款异步通知rabbitmq接收到的金额为0异常,params=[{}],程序终止。", GsonUtils.toJson(notifyDataVo));
                    } else {
                        //更新收银台数据和发送mq
                        CashierEntity cashierEntity = cashierMapper.selectCashierEntity(notifyDataVo.getPayMd5());
                        if (cashierEntity == null || !"00".equals(cashierEntity.getTransStatus())) {
                            refundOrderCallBackSuccess(notifyDataVo, vo);
                        }
                    }
                }
            }
        }
        return vo;
    }

    /**
     * 退款成功异步回调
     * @param notifyDataVo
     */
    @Transactional(rollbackFor=Exception.class)
    public void refundCallBackSuccess(AutoPayResultVo notifyDataVo) {
        log.info("refundCallBackSuccess param :[{}]", GsonUtils.toJson(notifyDataVo));
        //没有成功的 不处理
        if(Objects.isNull(notifyDataVo) || !TransStatusEnum.PAY_SUCCESS.getCode().equals(notifyDataVo.getTransStatus())){
        	log.info("refundCallBackSuccess params notifyDataVo is null or notifyDataVo=[{}],status fail",GsonUtils.toJson(notifyDataVo));
            return;
        }
        
        //更新退款申请表的状态。
        CashierRefundApplyEntity cashierRefundApplyEntity = cashierRefundApplyNoTService.updateRefundDepositSuccess(notifyDataVo);
        //记录原本就已经是退款成功的状态，避免重复操作。
        if(cashierRefundApplyEntity != null && CashierRefundApplyStatus.RECEIVED_REFUND.getCode().equals(cashierRefundApplyEntity.getStatus())){
        	log.info("refundCallBackSuccess params=[{}],cashierRefundApplyEntity=[{}],success again",GsonUtils.toJson(notifyDataVo),GsonUtils.toJson(cashierRefundApplyEntity));
        	return;
        }
        
        //退款成功。//32预授权解冻不算成功。 全额算,优先预授权完成，后预授权解冻。
        if(CashierRefundApplyStatus.RECEIVED_REFUND.getCode().equals(notifyDataVo.getTransStatus())) {
	        //更新退款状态
	        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
	        orderStatusDTO.setOrderNo(notifyDataVo.getOrderNo());
	        
	        OrderPayCallBackSuccessVO vo = new OrderPayCallBackSuccessVO();
	        vo.setOrderNo(notifyDataVo.getOrderNo());
	        vo.setMemNo(notifyDataVo.getMemNo());
	        
	        if(DataPayKindConstant.RENT.equals(notifyDataVo.getPayKind())){
	            orderStatusDTO.setDepositRefundStatus(OrderRefundStatusEnum.REFUNDED.getStatus());
	            sendOrderPayDepositSuccess(NewOrderMQActionEventEnum.ORDER_REFUND_SUCCESS,2,vo);
	        }
	        if(DataPayKindConstant.DEPOSIT.equals(notifyDataVo.getPayKind())){
	            orderStatusDTO.setWzRefundStatus(OrderRefundStatusEnum.REFUNDED.getStatus());
	            sendOrderPayDepositSuccess(NewOrderMQActionEventEnum.ORDER_REFUND_SUCCESS,1,vo);
	        }
	        if(DataPayKindConstant.RENT_AMOUNT.equals(notifyDataVo.getPayKind())){
	            orderStatusDTO.setRentCarRefundStatus(OrderRefundStatusEnum.REFUNDED.getStatus());
	            sendOrderPayRentCostSuccess(NewOrderMQActionEventEnum.ORDER_REFUND_SUCCESS,vo,3);
	        }
	        if(DataPayKindConstant.RENT_INCREMENT.equals(notifyDataVo.getPayKind())){
	        	sendOrderPayRentCostSuccess(NewOrderMQActionEventEnum.ORDER_REFUND_SUCCESS,vo,4);
	        }
	        saveCancelOrderStatusInfo(orderStatusDTO);
        }
    }

    /**
     * 钱包支付成功订单状态
     * @param
     */
    public void saveWalletPaylOrderStatusInfo( String orderNo){
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(orderNo);
        orderStatusDTO.setRentCarPayStatus(OrderPayStatusEnum.PAYED.getStatus());  //默认值。
        
        //下单的时候查不出来?
        OrderStatusEntity entity = orderStatusService.getByOrderNo(orderNo);
        if(Objects.nonNull(entity)){
            orderStatusDTO.setDepositPayStatus(entity.getDepositPayStatus());
            orderStatusDTO.setWzPayStatus(entity.getWzPayStatus());
            if(
                 ( Objects.nonNull(orderStatusDTO.getDepositPayStatus()) && OrderPayStatusEnum.PAYED.getStatus() == orderStatusDTO.getDepositPayStatus() )&&
                  (Objects.nonNull(orderStatusDTO.getWzPayStatus())  && OrderPayStatusEnum.PAYED.getStatus() == orderStatusDTO.getWzPayStatus())
            ){
                orderStatusDTO.setStatus(OrderStatusEnum.TO_GET_CAR.getStatus());
                //2记录订单流传信息
                orderFlowService.inserOrderStatusChangeProcessInfo(orderStatusDTO.getOrderNo(), OrderStatusEnum.TO_GET_CAR);

            }
        } else {
        	log.error("orderStatusEntity未查询到记录,orderNo=[{}]",orderNo);
        }
        //1更新 订单流转状态
        orderStatusService.saveOrderStatusInfo(orderStatusDTO);

    }
    /**
     * 取消订单结算
     * @param orderStatusDTO
     */
    public void saveCancelOrderStatusInfo(OrderStatusDTO orderStatusDTO){
        //1更新 订单流转状态
        orderStatusService.saveOrderStatusInfo(orderStatusDTO);
    }
    /**
     * 支付成功回调 更新收银台及费用
     * @param notifyDataVo
     */
    public void payOrderCallBackSuccess(NotifyDataVo notifyDataVo,OrderPayCallBackSuccessVO vo) {
        log.info("payOrderCallBackSuccess param :[{}]", GsonUtils.toJson(notifyDataVo));
        //没有成功的 不处理
        //只处理成功的。
        //只处理成功支付的记录。
        if(Objects.isNull(notifyDataVo) || !TransStatusEnum.PAY_SUCCESS.getCode().equals(notifyDataVo.getTransStatus())){
            log.info("payOrderCallBackSuccess check fail :[{}]", GsonUtils.toJson(notifyDataVo));
            return;
        }
        vo.setOrderNo(notifyDataVo.getOrderNo());
        vo.setMemNo(notifyDataVo.getMemNo());
        
        // --------------------------------------------------------------- 两大押金 ---------------------------------------------------------------
        //1.1 租车押金 01
        if(Objects.nonNull(notifyDataVo) && DataPayKindConstant.RENT.equals(notifyDataVo.getPayKind())){
            //1 对象初始化转换，数据准备。
            PayedOrderRenterDepositReqVO payedOrderRenterDeposit = cashierNoTService.getPayedOrderRenterDepositReq(notifyDataVo,RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT);
            //2 收银台记录更新
            cashierNoTService.updataCashierAndRenterDeposit(notifyDataVo,payedOrderRenterDeposit);
            
            //需要检测实收和资金流水实付的金额。
            if(cashierShishouService.checkRentShishou(payedOrderRenterDeposit.getOrderNo(), payedOrderRenterDeposit.getMemNo())) {
	            //支付状态，上面已经做了拦截判断。
		        vo.setDepositPayStatus(OrderPayStatusEnum.PAYED.getStatus());
		        sendOrderPayDepositSuccess(NewOrderMQActionEventEnum.RENTER_ORDER_PAYFEESUCCESS,2,vo);
            }
        }
        
        //1.2 违章押金 02
        if(Objects.nonNull(notifyDataVo) && DataPayKindConstant.DEPOSIT.equals(notifyDataVo.getPayKind())){
            //1 对象初始化转换，数据准备。
            PayedOrderRenterWZDepositReqVO payedOrderRenterWZDeposit = cashierNoTService.getPayedOrderRenterWZDepositReq(notifyDataVo,RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT);
            //2 收银台记录更新
            cashierNoTService.updataCashierAndRenterWzDeposit(notifyDataVo,payedOrderRenterWZDeposit);
            
            //需要检测实收和资金流水实付的金额。
            if(cashierShishouService.checkDepositShishou(payedOrderRenterWZDeposit.getOrderNo(), payedOrderRenterWZDeposit.getMemNo())) {
	            //支付状态
		        vo.setWzPayStatus(OrderPayStatusEnum.PAYED.getStatus());
		        sendOrderPayDepositSuccess(NewOrderMQActionEventEnum.RENTER_ORDER_PAYFEESUCCESS,1,vo);
            }
        }
        
        
        /**
         * DataPayKindConstant.RENT_AMOUNT
			DataPayKindConstant.RENT_INCREMENT
			DataPayKindConstant.RENT_AMOUNT_AFTER   更新实收字段。   动态计算的。
			欠款 和 管理后台的补付supplement 不更新实收shishou，只收不退。
         */
        // -------------------------------------------------------- 支付租车费用和APP修改订单补付组合,更新的是实收
        //1.3 租车费用 11
        if(Objects.nonNull(notifyDataVo) && DataPayKindConstant.RENT_AMOUNT.equals(notifyDataVo.getPayKind()) ){
            //1 对象初始化转换
            AccountRenterCostReqVO accountRenterCostReq = cashierNoTService.getAccountRenterCostReq(notifyDataVo, RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST);
            //2 收银台记录更新
            cashierNoTService.updataCashierAndRenterCost(notifyDataVo,accountRenterCostReq);
            
            //需要检测实收和资金流水实付的金额。
            if(cashierShishouService.checkRentAmountShishou(accountRenterCostReq.getOrderNo(), accountRenterCostReq.getMemNo())) {
	            //支付状态
		        vo.setRentCarPayStatus(OrderPayStatusEnum.PAYED.getStatus());
		        sendOrderPayRentCostSuccess(NewOrderMQActionEventEnum.RENTER_ORDER_PAYSUCCESS,vo,1);
            }
        }
        
        
        //1.4 补付租车费用 03  APP修改订单。
        if(Objects.nonNull(notifyDataVo) && DataPayKindConstant.RENT_INCREMENT.equals(notifyDataVo.getPayKind()) ){
            //1 对象初始化转换
            AccountRenterCostReqVO accountRenterCostReq = cashierNoTService.getAccountRenterCostReq(notifyDataVo, RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN);
            //2 收银台记录更新
            cashierNoTService.updataCashierAndRenterCost(notifyDataVo,accountRenterCostReq);
            
            //需要检测实收和资金流水实付的金额。
            if(cashierShishouService.checkRentAmountShishou(accountRenterCostReq.getOrderNo(), accountRenterCostReq.getMemNo())) {
	            //支付状态(callback需更新状态)
		        vo.setRentCarPayStatus(OrderPayStatusEnum.PAYED.getStatus());
		        vo.setIsPayAgain(YesNoEnum.YES.getCode());
		        sendOrderPayRentCostSuccess(NewOrderMQActionEventEnum.RENTER_ORDER_PAYSUCCESS,vo,2);
            }
        }
        
        // -------------------------------------------------------- 三大补付组合,  DataPayKindConstant.RENT_AMOUNT_AFTER 更新的是实收
        //补充
        if(Objects.nonNull(notifyDataVo) && DataPayKindConstant.RENT_AMOUNT_AFTER.equals(notifyDataVo.getPayKind()) ){
            //1 对象初始化转换
            AccountRenterCostReqVO accountRenterCostReq = cashierNoTService.getAccountRenterCostReq(notifyDataVo, RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AFTER);
            //2 收银台记录更新
            cashierNoTService.updataCashierAndRenterCost(notifyDataVo,accountRenterCostReq);
            //支付状态（无需该状态）
//	        vo.setRentCarPayStatus(OrderPayStatusEnum.PAYED.getStatus());
//	        sendOrderPayRentCostSuccess(NewOrderMQActionEventEnum.RENTER_ORDER_PAYSUCCESS,vo,1);
        }
        
        /**
         * 这一块的收款不能计入 租车费用的实收,否则欠款的金额重复使用了。200403
         */
        //1.5管理后台补付 add 200312 
        //需求放开，结算前计入，结算后不计入
        //只处理未结算的订单。 200819
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(notifyDataVo.getOrderNo());
        if(orderStatusEntity != null && orderStatusEntity.getSettleStatus() == 0 && orderStatusEntity.getCarDepositSettleStatus() == 0) {
	        if(Objects.nonNull(notifyDataVo) && DataPayKindConstant.RENT_INCREMENT_CONSOLE.equals(notifyDataVo.getPayKind())){
	            //1 对象初始化转换
	            AccountRenterCostReqVO accountRenterCostReq = cashierNoTService.getAccountRenterCostReq(notifyDataVo, RenterCashCodeEnum.ACCOUNT_RENTER_SUPPLEMENT_COST_AGAIN);
	            //2 收银台记录更新
	            cashierNoTService.updataCashierAndRenterCost(notifyDataVo,accountRenterCostReq);
	            
	            //支付状态(callback需更新状态)
	//	        vo.setRentCarPayStatus(OrderPayStatusEnum.PAYED.getStatus());
	//	        vo.setIsPayAgain(YesNoEnum.YES.getCode());
	//	        sendOrderPayRentCostSuccess(NewOrderMQActionEventEnum.RENTER_ORDER_PAYSUCCESS,vo,2);
	        }
        }
        
        /**
         * 这一块的收款不能计入 租车费用的实收,否则欠款的金额重复使用了。200403
         */
        //1.6支付欠款 add 200312 
//        if(Objects.nonNull(notifyDataVo) && DataPayKindConstant.DEBT.equals(notifyDataVo.getPayKind())){
//            //1 对象初始化转换
//            AccountRenterCostReqVO accountRenterCostReq = cashierNoTService.getAccountRenterCostReq(notifyDataVo, RenterCashCodeEnum.ACCOUNT_RENTER_DEBT_COST_AGAIN);
//            //2 收银台记录更新
//            cashierNoTService.updataCashierAndRenterCost(notifyDataVo,accountRenterCostReq);
//            
//            //支付状态(callback需更新状态)
////	        vo.setRentCarPayStatus(OrderPayStatusEnum.PAYED.getStatus());
////	        vo.setIsPayAgain(YesNoEnum.YES.getCode());
////	        sendOrderPayRentCostSuccess(NewOrderMQActionEventEnum.RENTER_ORDER_PAYSUCCESS,vo,2);
//        }
        
    }
    


	/**
     * 欠款，不存在退款
     * 补付租车押金,管理后台。v5.11  ，不存在退款
     * 该方法 仅仅 更新收银台
     * @param notifyDataVo
     * @param vo
     */
    public void refundOrderCallBackSuccess(NotifyDataVo notifyDataVo,OrderPayCallBackSuccessVO vo) {
        log.info("refundOrderCallBackSuccess param :[{}]", GsonUtils.toJson(notifyDataVo));
        //没有成功的 不处理
        //只处理成功退款的记录。
        if(Objects.isNull(notifyDataVo) || !TransStatusEnum.PAY_SUCCESS.getCode().equals(notifyDataVo.getTransStatus())){
            log.info("refundOrderCallBackSuccess check fail :[{}]", GsonUtils.toJson(notifyDataVo));
            return;
        }
        vo.setOrderNo(notifyDataVo.getOrderNo());
        vo.setMemNo(notifyDataVo.getMemNo());
        //1.1 租车押金 01
        if(DataPayKindConstant.RENT.equals(notifyDataVo.getPayKind())){
            //1 对象初始化转换,在退款的地方押金增加了资金进出明细。
//            PayedOrderRenterDepositReqVO payedOrderRenterDeposit = cashierNoTService.getPayedOrderRenterDepositReq(notifyDataVo,RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT);
            //2 收银台记录更新
            cashierNoTService.updataCashierAndRenterDeposit(notifyDataVo,null);
            //支付状态
//	        vo.setDepositRefundStatus(OrderRefundStatusEnum.REFUNDED.getStatus());
            //在申请退款的回调中已经发送了  退款成功过的 MQ
	        sendOrderPayDepositSuccess(NewOrderMQActionEventEnum.ORDER_REFUND_SUCCESS,2,vo);
        }
        
        //1.2 违章押金 02
        if(DataPayKindConstant.DEPOSIT.equals(notifyDataVo.getPayKind())){
            //1 对象初始化转换,在退款的地方押金增加了资金进出明细。
//            PayedOrderRenterWZDepositReqVO payedOrderRenterWZDeposit = cashierNoTService.getPayedOrderRenterWZDepositReq(notifyDataVo,RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT);
            //2 收银台记录更新
            cashierNoTService.updataCashierAndRenterWzDeposit(notifyDataVo,null);
            //支付状态
//	        vo.setWzRefundStatus(OrderRefundStatusEnum.REFUNDED.getStatus());
	        sendOrderPayDepositSuccess(NewOrderMQActionEventEnum.ORDER_REFUND_SUCCESS,1,vo);
        }
        
        //1.3 租车费用
        if(DataPayKindConstant.RENT_AMOUNT.equals(notifyDataVo.getPayKind())){
            //1 对象初始化转换
//            AccountRenterCostReqVO accountRenterCostReq = cashierNoTService.getAccountRenterCostReq(notifyDataVo, RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST);
            //2 收银台记录更新
            cashierNoTService.updataCashierAndRenterCost(notifyDataVo,null);
            //支付状态
//	        vo.setRentCarRefundStatus(OrderRefundStatusEnum.REFUNDED.getStatus());
	        sendOrderPayRentCostSuccess(NewOrderMQActionEventEnum.ORDER_REFUND_SUCCESS,vo,1);
        }
        
        //1.4 补付租车费用
        if(DataPayKindConstant.RENT_INCREMENT.equals(notifyDataVo.getPayKind())){
            //1 对象初始化转换
//            AccountRenterCostReqVO accountRenterCostReq = cashierNoTService.getAccountRenterCostReq(notifyDataVo, RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN);
            //2 收银台记录更新
            cashierNoTService.updataCashierAndRenterCost(notifyDataVo,null);
            //支付状态
//	        vo.setRentCarRefundStatus(OrderRefundStatusEnum.REFUNDED.getStatus());
//	        vo.setIsPayAgain(YesNoEnum.YES.getCode());
	        sendOrderPayRentCostSuccess(NewOrderMQActionEventEnum.ORDER_REFUND_SUCCESS,vo,2);
        }
        
        //已核实，支付的时候没有发送MQ，退款的也不发送。
        //1.5 补充
        if(DataPayKindConstant.RENT_AMOUNT_AFTER.equals(notifyDataVo.getPayKind())){
            //1 对象初始化转换
//            AccountRenterCostReqVO accountRenterCostReq = cashierNoTService.getAccountRenterCostReq(notifyDataVo, RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AFTER);
            //2 收银台记录更新
            cashierNoTService.updataCashierAndRenterCost(notifyDataVo,null);
            //支付状态
//	        vo.setRentCarRefundStatus(OrderRefundStatusEnum.REFUNDED.getStatus());
//	        vo.setIsPayAgain(YesNoEnum.YES.getCode());
//	        sendOrderPayRentCostSuccess(NewOrderMQActionEventEnum.ORDER_REFUND_SUCCESS,vo,2);
        }
        
        //07 支付欠款 和 08 supplement是结算后，不涉及到退款。
        
    }
    
    /**
     * 发送订单支付成功事件 （支付押金/违章押金成功）
     * type 1 违章押金 2 车辆押金
     */
    public void sendOrderPayDepositSuccess(NewOrderMQActionEventEnum event,int type,OrderPayCallBackSuccessVO vo){
        log.info("sendOrderPayDepositSuccess start [{}] ,[{}] ,[{}]",event,type,GsonUtils.toJson(vo));
        OrderRenterPayAmtSuccessMq orderRenterPayAmtSuccessMq = new OrderRenterPayAmtSuccessMq();
        orderRenterPayAmtSuccessMq.setOrderNo(vo.getOrderNo());
        orderRenterPayAmtSuccessMq.setRenterMemNo(Integer.valueOf(vo.getMemNo()));
        orderRenterPayAmtSuccessMq.setType(type);
        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderRenterPayAmtSuccessMq);
        log.info("发送订单支付成功事件 （支付押金/违章押金成功）.mq:,message=[{}]",event,
                GsonUtils.toJson(orderMessage));
        try {
            baseProducer.sendTopicMessage(event.exchange, event.routingKey, orderMessage);
        } catch (Exception e) {
            log.error("支付押金成功，但事件发送失败 error [{}] ,[{}] ,[{}],[{}]", event, type, GsonUtils.toJson(vo), e);
        }
    }
    /**
     * 发送订单支付成功事件 （租车费用）
     *   1：租车费用  2：租车补付费用
     */
    public void sendOrderPayRentCostSuccess(NewOrderMQActionEventEnum event,OrderPayCallBackSuccessVO vo,Integer type){
        log.info("sendOrderPayRentCostSuccess start [{}] ,[{}]",event,GsonUtils.toJson(vo));
        OrderRenterPaySuccessMq orderRenterPay = new OrderRenterPaySuccessMq();
        orderRenterPay.setType(type);
        orderRenterPay.setOrderNo(vo.getOrderNo());
        orderRenterPay.setRenterMemNo(Integer.valueOf(vo.getMemNo()));
        OrderMessage orderMessage = OrderMessage.builder().build();
        orderMessage.setMessage(orderRenterPay);
        log.info("发送订单支付成功事件 （支付押金/违章押金成功）.mq:,message=[{}]",event,
                GsonUtils.toJson(orderMessage));
        try {
            baseProducer.sendTopicMessage(event.exchange, event.routingKey, orderMessage);
        } catch (Exception e) {
            log.error("支付租车费用成功，但事件发送失败 error [{}] ,[{}] ,[{}]",event,GsonUtils.toJson(vo),e);
        }
    }
    /**
     * 当支付成功（当车辆押金，违章押金，租车费用都支付成功，更新订单状态 待取车），更新主订单状态待取车
     * @param orderStatusDTO
     */
    private void setOrderStatus(OrderStatusDTO orderStatusDTO){
        OrderStatusEntity entity = orderStatusService.getByOrderNo(orderStatusDTO.getOrderNo());
        if(
                OrderPayStatusEnum.PAYED.getStatus()== entity.getRentCarPayStatus() &&
                OrderPayStatusEnum.PAYED.getStatus()== entity.getWzPayStatus() &&
                OrderPayStatusEnum.PAYED.getStatus()== entity.getDepositPayStatus()
        ){
            orderStatusDTO.setStatus(OrderStatusEnum.TO_GET_CAR.getStatus());
            //记录订单流程
            orderFlowService.inserOrderStatusChangeProcessInfo(orderStatusDTO.getOrderNo(), OrderStatusEnum.TO_GET_CAR);
        }

    }
    
    
    public List<CashierEntity> getCashierRentCostsByOrderNo(String orderNo){
    	return cashierMapper.getCashierRentCostsByOrderNo(orderNo);
    }
    
    
    /**
     * 老系统欠款抵扣操作(租车费用)
     * @param debtRes
     */
    public void saveRentCostDebt(AccountOldDebtResVO debtRes) {
    	//  记录租车费用资金 进出记录
        AccountRenterCostDetailReqVO accountRenterCostChangeReqVO = new AccountRenterCostDetailReqVO();
        accountRenterCostChangeReqVO.setMemNo(debtRes.getMemNo());
        accountRenterCostChangeReqVO.setOrderNo(debtRes.getOrderNo());
        accountRenterCostChangeReqVO.setAmt(-debtRes.getRealDebtAmt());
        accountRenterCostChangeReqVO.setRenterCashCodeEnum(debtRes.getCahsCodeEnum());
        // insert  account_renter_cost_detail
        int id = accountRenterCostSettleService.deductDepositToRentCost(accountRenterCostChangeReqVO);
        // 记录结算费用 抵扣记录
        AccountRenterCostSettleDetailEntity renterCostSettleDetail = new AccountRenterCostSettleDetailEntity();
        renterCostSettleDetail.setMemNo(debtRes.getMemNo());
        renterCostSettleDetail.setOrderNo(debtRes.getOrderNo());
        renterCostSettleDetail.setRenterOrderNo(debtRes.getRenterOrderNo());
        renterCostSettleDetail.setUniqueNo(String.valueOf(id));
        renterCostSettleDetail.setCostCode(debtRes.getCahsCodeEnum().getCashNo());
        renterCostSettleDetail.setCostDetail(debtRes.getCahsCodeEnum().getTxt());
        renterCostSettleDetail.setAmt(-debtRes.getRealDebtAmt());
        // insert account_renter_cost_settle_detail
        accountRenterCostSettleDetailNoTService.insertAccountRenterCostSettleDetail(renterCostSettleDetail);
    }
    
    
    /**
     * 老系统欠款抵扣操作(车辆押金)
     * @param debtRes
     */
    public void saveDepositDebt(AccountOldDebtResVO debtRes) {
    	//  记录押金资金明细 抵扣记录
        DetainRenterDepositReqVO detainRenterDepositReqVO = new DetainRenterDepositReqVO();
        detainRenterDepositReqVO.setMemNo(debtRes.getMemNo());
        detainRenterDepositReqVO.setOrderNo(debtRes.getOrderNo());
        detainRenterDepositReqVO.setAmt(-debtRes.getRealDebtAmt());
        detainRenterDepositReqVO.setRenterCashCodeEnum(debtRes.getCahsCodeEnum());
        // update account_renter_deposit
        // insert account_renter_deposit_detail
        int id = accountRenterDepositService.detainRenterDeposit(detainRenterDepositReqVO);
        // 记录结算费用 抵扣记录
        AccountRenterCostSettleDetailEntity renterCostSettleDetail = new AccountRenterCostSettleDetailEntity();
        renterCostSettleDetail.setMemNo(debtRes.getMemNo());
        renterCostSettleDetail.setOrderNo(debtRes.getOrderNo());
        renterCostSettleDetail.setRenterOrderNo(debtRes.getRenterOrderNo());
        renterCostSettleDetail.setUniqueNo(String.valueOf(id));
        renterCostSettleDetail.setCostCode(debtRes.getCahsCodeEnum().getCashNo());
        renterCostSettleDetail.setCostDetail(debtRes.getCahsCodeEnum().getTxt());
        renterCostSettleDetail.setAmt(-debtRes.getRealDebtAmt());
        // insert account_renter_cost_settle_detail
        accountRenterCostSettleDetailNoTService.insertAccountRenterCostSettleDetail(renterCostSettleDetail);
    }
    
    
    /**
     * 老系统欠款抵扣操作（车主收益）
     * @param debtRes
     */
    public void saveOwnerIncomeDebt(AccountOldDebtResVO debtRes) {
    	//3 记录车主结算费用总额 及 车主费用结算明细表
        AccountOwnerCostSettleDetailEntity entity = new AccountOwnerCostSettleDetailEntity();
        entity.setMemNo(debtRes.getMemNo());
        entity.setOrderNo(debtRes.getOrderNo());
        entity.setOwnerOrderNo(debtRes.getOwnerOrderNo());
        entity.setSourceCode(debtRes.getCahsCodeEnum().getCashNo());
        entity.setSourceDetail(debtRes.getCahsCodeEnum().getTxt());
        entity.setAmt(-debtRes.getRealDebtAmt());
        int id = accountOwnerCostSettleDetailNoTService.insertAccountOwnerCostSettleDetail(entity);
    }

    
	public int getWalletDeductAmt(String orderNo, List<String> payKind) {
		return cashierMapper.getWalletDeductAmt(orderNo,payKind);
	}





}
