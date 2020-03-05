package com.atzuche.order.cashieraccount.service;

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
import com.atzuche.order.cashieraccount.exception.SettleAmountException;
import com.atzuche.order.cashieraccount.mapper.CashierMapper;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.cashieraccount.vo.req.CashierDeductDebtReqVO;
import com.atzuche.order.cashieraccount.vo.req.CashierRefundApplyReqVO;
import com.atzuche.order.cashieraccount.vo.res.CashierDeductDebtResVO;
import com.atzuche.order.cashieraccount.vo.res.pay.OrderPayCallBackSuccessVO;
import com.atzuche.order.commons.enums.*;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.cashier.CashierRefundApplyStatus;
import com.atzuche.order.commons.enums.cashier.OrderRefundStatusEnum;
import com.atzuche.order.commons.enums.cashier.TransStatusEnum;
import com.atzuche.order.commons.vo.req.income.AccountOwnerIncomeExamineOpReqVO;
import com.atzuche.order.commons.vo.req.income.AccountOwnerIncomeExamineReqVO;
import com.atzuche.order.commons.vo.res.account.income.AdjustOwnerIncomeResVO;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.mq.common.base.BaseProducer;
import com.atzuche.order.mq.common.base.OrderMessage;
import com.atzuche.order.mq.enums.PushMessageTypeEnum;
import com.atzuche.order.mq.enums.ShortMessageTypeEnum;
import com.atzuche.order.mq.util.SmsParamsMapUtil;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.settle.service.AccountDebtService;
import com.atzuche.order.settle.vo.req.AccountDeductDebtReqVO;
import com.atzuche.order.settle.vo.req.AccountInsertDebtReqVO;
import com.atzuche.order.settle.vo.res.AccountDebtResVO;
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

import java.util.List;
import java.util.Map;
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
        int depositDetailId = accountRenterDepositService.detainRenterDeposit(detainRenterDepositReqVO);
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
        Integer id = cashierRefundApplyNoTService.insertRefundDeposit(cashierRefundApplyReq);
        //2 记录费用平账
        DetainRenterDepositReqVO detainRenterDepositReqVO = new DetainRenterDepositReqVO();
        BeanUtils.copyProperties(cashierRefundApplyReq,detainRenterDepositReqVO);
        detainRenterDepositReqVO.setUniqueNo(id.toString());
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
        Integer id = cashierRefundApplyNoTService.insertRefundDeposit(cashierRefundApplyReq);
        return id;
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
        accountRenterCostDetail.setUniqueNo(id.toString());
        int accountRenterCostDetailId = accountRenterCostSettleService.refundRenterCostDetail(accountRenterCostDetail);
        //3 发消息通知 存在退款
        return accountRenterCostDetailId;
    }
    /**
     * 结算退还租车费用 钱包
     */
    @Transactional(rollbackFor=Exception.class)
    public int refundRentCostWallet(AccountRenterCostDetailReqVO accountRenterCostDetail){
        Assert.notNull(accountRenterCostDetail, ErrorCode.PARAMETER_ERROR.getText());
        int accountRenterCostDetailId = accountRenterCostSettleService.refundRenterCostDetail(accountRenterCostDetail);
        //3 发消息通知 存在退款
        return accountRenterCostDetailId;
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
		    id = this.refundDepositPreAuth(cashierRefundApply);  //仅仅提交预授权完成记录
		}
		
		return id;
	}
    
    /**
     * 租车押金消费退款
     * @param rentSurplusDepositAmt
     * @param cashierEntity
     * @param cashierRefundApply
     * @param cashCode
     */
    public int refundDepositPurchase(int rentSurplusDepositAmt,
			CashierEntity cashierEntity, CashierRefundApplyReqVO cashierRefundApply,RenterCashCodeEnum cashCode) {
		cashierRefundApply.setPayType(DataPayTypeConstant.PUR_RETURN); //退货
		cashierRefundApply.setAmt(-rentSurplusDepositAmt);
		cashierRefundApply.setRenterCashCodeEnum(cashCode);
		cashierRefundApply.setRemake(cashCode.getTxt());
		cashierRefundApply.setFlag(RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT.getCashNo());
		cashierRefundApply.setType(SysOrHandEnum.SYSTEM.getStatus());
		cashierRefundApply.setQn(cashierEntity.getQn());
		return this.refundDeposit(cashierRefundApply);
	}
    
    /**
     * 违章押金预授权退款方法
     * @param rentSurplusWzDepositAmt
     * @param cashierEntity
     * @param cashierRefundApply
     * @param cashCode
     */
    public int refundWzDepositPreAuthAll(int rentSurplusWzDepositAmt,CashierEntity cashierEntity, CashierRefundApplyReqVO cashierRefundApply,RenterCashCodeEnum cashCode) {
		//是否存在预授权完成操作
		boolean isExistsPreAuthFinish = (cashierEntity.getPayAmt() - Math.abs(rentSurplusWzDepositAmt) > 0);
		int id = 0;
		
		//预授权  退款就是解冻，余下的就是预授权完成
		//预授权解冻，金额不允许为0
		//考虑全额预授权解冻
		if(Math.abs(rentSurplusWzDepositAmt) != 0){
			//超出金额的做限制,退款的超出做全额解冻。
			int refundAmt = -rentSurplusWzDepositAmt;
			if(Math.abs(refundAmt) > cashierEntity.getPayAmt()) {
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
		    if(isExistsPreAuthFinish) {
		    	cashierRefundApply.setStatus(CashierRefundApplyStatus.STOP_FOR_REFUND.getCode());
		    }
		    
		    id = this.refundWZDeposit(cashierRefundApply);
		}
		
		//添加预授权完成记录 
		//添加预授权完成记录,金额不允许为0 
		//考虑全额预授权完成
		if(isExistsPreAuthFinish) {
		    cashierRefundApply.setPayType(DataPayTypeConstant.PRE_FINISH); //预授权完成
			cashierRefundApply.setAmt( cashierEntity.getPayAmt() - Math.abs(rentSurplusWzDepositAmt) );
			cashierRefundApply.setRenterCashCodeEnum(cashCode);
		    cashierRefundApply.setRemake(cashCode.getTxt());
		    cashierRefundApply.setFlag(RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT.getCashNo());
		    cashierRefundApply.setType(SysOrHandEnum.SYSTEM.getStatus());
		    cashierRefundApply.setQn(cashierEntity.getQn());
		    cashierRefundApply.setPayKind(DataPayKindConstant.DEPOSIT);
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
    public AdjustOwnerIncomeResVO examineOwnerIncomeExamine(AccountOwnerIncomeExamineOpReqVO accountOwnerIncomeExamineOpReq){
        AdjustOwnerIncomeResVO vo = new  AdjustOwnerIncomeResVO();
        BeanUtils.copyProperties(accountOwnerIncomeExamineOpReq,vo);
        int id = accountOwnerIncomeService.examineOwnerIncomeExamine(accountOwnerIncomeExamineOpReq);
        vo.setAccountOwnerIncomeDetailId(id);
        return vo;
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
            for(int i=0;i<lstNotifyDataVo.size();i++){
                NotifyDataVo notifyDataVo = lstNotifyDataVo.get(i);
                //2支付成功回调
                if(DataPayTypeConstant.PAY_PUR.equals(notifyDataVo.getPayType()) || DataPayTypeConstant.PAY_PRE.equals(notifyDataVo.getPayType())){
                	Integer settleAmount = notifyDataVo.getSettleAmount()==null?0:Integer.parseInt(notifyDataVo.getSettleAmount());
                	if(settleAmount.intValue() == 0) {
                		//金额为0的异常情况。
                		Cat.logError(new SettleAmountException());
                		log.error("支付异步通知rabbitmq接收到的金额为0异常,params=[{}],程序终止。",GsonUtils.toJson(notifyDataVo));
                	}else {
                		payOrderCallBackSuccess(notifyDataVo,vo);
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
            return;
        }
        //更新退款申请表的状态。
        cashierRefundApplyNoTService.updateRefundDepositSuccess(notifyDataVo);
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(notifyDataVo.getOrderNo());
        if(DataPayKindConstant.RENT.equals(notifyDataVo.getPayKind())){
            orderStatusDTO.setDepositRefundStatus(OrderRefundStatusEnum.REFUNDED.getStatus());
        }
        if(DataPayKindConstant.DEPOSIT.equals(notifyDataVo.getPayKind())){
            orderStatusDTO.setWzRefundStatus(OrderRefundStatusEnum.REFUNDED.getStatus());
        }
        if(DataPayKindConstant.RENT_AMOUNT.equals(notifyDataVo.getPayKind())){
            orderStatusDTO.setRentCarRefundStatus(OrderRefundStatusEnum.REFUNDED.getStatus());
        }
        saveCancelOrderStatusInfo(orderStatusDTO);
        //TODO 退款回调成功 push/或者短信 怎么处理
        cashierNoTService.sendOrderRefundSuccessMq(notifyDataVo.getOrderNo(), FineSubsidyCodeEnum.RENTER);
    }

    /**
     * 钱包支付成功订单状态
     * @param
     */
    public void saveWalletPaylOrderStatusInfo( String orderNo){
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderNo(orderNo);
        OrderStatusEntity entity = orderStatusService.getByOrderNo(orderNo);
        if(Objects.nonNull(entity)){
            orderStatusDTO.setRentCarPayStatus(OrderPayStatusEnum.PAYED.getStatus());
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
        if(Objects.isNull(notifyDataVo) || !TransStatusEnum.PAY_SUCCESS.getCode().equals(notifyDataVo.getTransStatus())){
            log.info("payOrderCallBackSuccess check fail :[{}]", GsonUtils.toJson(notifyDataVo));
            return;
        }
        vo.setOrderNo(notifyDataVo.getOrderNo());
        vo.setMemNo(notifyDataVo.getMemNo());
        //1.1 租车押金 01
        if(Objects.nonNull(notifyDataVo) && DataPayKindConstant.RENT.equals(notifyDataVo.getPayKind())){
            //1 对象初始化转换
            PayedOrderRenterDepositReqVO payedOrderRenterDeposit = cashierNoTService.getPayedOrderRenterDepositReq(notifyDataVo);
            //2 收银台记录更新
            cashierNoTService.updataCashierAndRenterDeposit(notifyDataVo,payedOrderRenterDeposit);
            vo.setDepositPayStatus(OrderPayStatusEnum.PAYED.getStatus());
            sendOrderPayDepositSuccess(NewOrderMQActionEventEnum.RENTER_ORDER_PAYFEESUCCESS,2,vo);
        }
        //1.2 违章押金 02
        if(Objects.nonNull(notifyDataVo) && DataPayKindConstant.DEPOSIT.equals(notifyDataVo.getPayKind())){
            //1 对象初始化转换
            PayedOrderRenterWZDepositReqVO payedOrderRenterWZDeposit = cashierNoTService.getPayedOrderRenterWZDepositReq(notifyDataVo);
            //2 收银台记录更新
            cashierNoTService.updataCashierAndRenterWzDeposit(notifyDataVo,payedOrderRenterWZDeposit);
            vo.setWzPayStatus(OrderPayStatusEnum.PAYED.getStatus());
            sendOrderPayDepositSuccess(NewOrderMQActionEventEnum.RENTER_ORDER_PAYFEESUCCESS,1,vo);
        }
        //1.3 租车费用
        if(Objects.nonNull(notifyDataVo) && DataPayKindConstant.RENT_AMOUNT.equals(notifyDataVo.getPayKind()) ){
            //1 对象初始化转换
            AccountRenterCostReqVO accountRenterCostReq = cashierNoTService.getAccountRenterCostReq(notifyDataVo, RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST);
            //2 收银台记录更新
            cashierNoTService.updataCashierAndRenterCost(notifyDataVo,accountRenterCostReq);
            vo.setRentCarPayStatus(OrderPayStatusEnum.PAYED.getStatus());
            sendOrderPayRentCostSuccess(NewOrderMQActionEventEnum.RENTER_ORDER_PAYSUCCESS,vo,1);
        }
        //1.4 补付租车费用
        if(Objects.nonNull(notifyDataVo) && DataPayKindConstant.RENT_INCREMENT.equals(notifyDataVo.getPayKind()) ){
            //1 对象初始化转换
            AccountRenterCostReqVO accountRenterCostReq = cashierNoTService.getAccountRenterCostReq(notifyDataVo, RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN);
            //2 收银台记录更新
            cashierNoTService.updataCashierAndRenterCost(notifyDataVo,accountRenterCostReq);
            vo.setRentCarPayStatus(OrderPayStatusEnum.PAYED.getStatus());
            vo.setIsPayAgain(YesNoEnum.YES.getCode());
            sendOrderPayRentCostSuccess(NewOrderMQActionEventEnum.RENTER_ORDER_PAYSUCCESS,vo,2);

        }



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
            String renterTextCode = type == 1 ? ShortMessageTypeEnum.PAY_ILLEGAL_DEPOSIT_2_RENTER.getValue() : ShortMessageTypeEnum.PAY_RENT_CAR_DEPOSIT_2_RENTER.getValue();
            String ownerTextCode = type == 1 ? ShortMessageTypeEnum.PAY_ILLEGAL_DEPOSIT_2_OWNERSERVICE.getValue() : ShortMessageTypeEnum.PAY_RENT_CAR_DEPOSIT_2_OWNER.getValue();
            Map map = SmsParamsMapUtil.getParamsMap(vo.getOrderNo(), renterTextCode, ownerTextCode, null);
            orderMessage.setMap(map);
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
        //push车主租客已支付费用
        if (1 == type) {
            Map map = SmsParamsMapUtil.getParamsMap(vo.getOrderNo(), PushMessageTypeEnum.RENTER_PAY_CAR_SUCCESS.getValue(), PushMessageTypeEnum.RENTER_PAY_CAR_2_OWNER.getValue(), null);
            orderMessage.setPushMap(map);
        }
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

}
