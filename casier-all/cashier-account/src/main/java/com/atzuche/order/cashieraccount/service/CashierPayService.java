package com.atzuche.order.cashieraccount.service;

import com.atzuche.order.accountrenterdeposit.service.AccountRenterDepositService;
import com.atzuche.order.accountrenterrentcost.service.AccountRenterCostSettleService;
import com.atzuche.order.accountrenterwzdepost.service.AccountRenterWzDepositService;
import com.atzuche.order.cashieraccount.exception.OrderPayCallBackAsnyException;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.cashieraccount.service.remote.WalletRemoteService;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPaySignReqVO;
import com.atzuche.order.cashieraccount.vo.res.AccountPayAbleResVO;
import com.atzuche.order.cashieraccount.vo.res.OrderPayableAmountResVO;
import com.atzuche.order.cashieraccount.vo.res.pay.OrderPayAsynResVO;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.commons.service.RabbitMsgLogService;
import com.atzuche.order.rentercost.entity.vo.PayableVO;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.autoyol.api.WalletFeignService;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.autopay.gateway.constant.DataPayTypeConstant;
import com.autoyol.cat.CatAnnotation;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.vo.req.WalletDeductionReqVO;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * 收银台支付退款操作
 *
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Service
@Slf4j
public class CashierPayService{

    @Autowired AccountRenterDepositService accountRenterDepositService;
    @Autowired AccountRenterWzDepositService accountRenterWzDepositService;
    @Autowired CashierService cashierService;
    @Autowired RabbitMsgLogService rabbitMsgLogService;
    @Autowired AccountRenterCostSettleService accountRenterCostSettleService;
    @Autowired RenterOrderCostCombineService renterOrderCostCombineService;
    @Autowired CashierNoTService cashierNoTService;
    @Autowired WalletRemoteService walletRemoteService;


    /**
     * 支付系统回调（支付回调，退款回调到时一个）
     * MQ 异步回调
     */
    @Async
    public void payCallBackAsyn(OrderPayAsynResVO orderPayAsynVO){
        Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "支付系统rabbitMQ异步回调payCallBackAsyn");
        try {
            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD,"OrderPayCallBackRabbitConfig.payCallBackAsyn");
            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM,GsonUtils.toJson(orderPayAsynVO));
            //1 校验是否 支付/退款成功
            if(Objects.nonNull(orderPayAsynVO) && "".equals(orderPayAsynVO.getTransStatus())){
                //1 退款
                if(DataPayTypeConstant.PUR_RETURN.equals(orderPayAsynVO.getPayType())){
                    cashierService.refundCallBackSuccess(orderPayAsynVO);
                }
                //2支付成功回调
                if(DataPayTypeConstant.PAY_PUR.equals(orderPayAsynVO.getPayType()) || DataPayTypeConstant.PAY_PRE.equals(orderPayAsynVO.getPayType())){
                    cashierService.payOrderCallBackSuccess(orderPayAsynVO);
                }
            }
            //3 更新rabbitMQ 记录已消费
            rabbitMsgLogService.updateConsume(orderPayAsynVO.getPayType(),orderPayAsynVO.getQn());
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            log.info("OrderPayCallBack payCallBackAsyn start param;[{}]", GsonUtils.toJson(orderPayAsynVO));
            t.setStatus(e);
            Cat.logError("异步处理支付系统回调 失败",e);
            throw new OrderPayCallBackAsnyException();
        } finally {
            log.info("OrderPayCallBack payCallBackAsyn start end;[{}]", GsonUtils.toJson(orderPayAsynVO));
            t.complete();
        }
    }


    /**
     * 获取支付验签数据
     * @param orderPaySign
     * @return
     */
    @Transactional(rollbackFor=Exception.class)
    public String getPaySignStr(OrderPaySignReqVO orderPaySign){
        //1校验
        Assert.notNull(orderPaySign, ErrorCode.PARAMETER_ERROR.getText());
        orderPaySign.check();
        //2 查询子单号
        String rentOrderNo = cashierNoTService.getRenterOrderNoByOrderNo(orderPaySign.getOrderNo());
        //3 查询应付
        OrderPayableAmountResVO payVO = getOrderPayableAmount(orderPaySign,rentOrderNo);
        //4 抵扣钱包
        if(Boolean.TRUE.equals(orderPaySign.getIsUseWallet())){
           int payBalance = walletRemoteService.getWalletPayBalanceByMemNo(orderPaySign.getMenNo());
           //判断余额大于0
           if(payBalance>0){
               int num = cashierNoTService.payOrderByWallet(payBalance,orderPaySign,payVO);
               WalletDeductionReqVO walletDeduction = cashierNoTService.getWalletDeductionReqVO(orderPaySign,payVO,payBalance);
               //5 抵扣钱包落库 （收银台落库、费用落库）
               walletRemoteService.updateWalletByDeduct(walletDeduction);
           }
        }


        //6 签名串
        //TODO
        return null;
    }



    /**
     * 当前需要支付的相关信息供支付平台使用
     */
    @CatAnnotation
    public OrderPayableAmountResVO getOrderPayableAmount(OrderPaySignReqVO orderPaySign, String renterOrderNo){
        OrderPayableAmountResVO result = new OrderPayableAmountResVO();
        //待支付金额明细
        List<AccountPayAbleResVO> accountPayAbles = new ArrayList<>();
        //车辆押金 是否选择车辆押金
        int amtDeposit = 0;
        if(orderPaySign.getPayKind().contains(DataPayKindConstant.RENT)){
            amtDeposit = cashierNoTService.getPayDeposit(orderPaySign.getOrderNo(),orderPaySign.getMenNo(), DataPayKindConstant.RENT);
            accountPayAbles.add(new AccountPayAbleResVO(orderPaySign.getOrderNo(),orderPaySign.getMenNo(),amtDeposit, RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT,RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT.getTxt()));
        }

        //违章押金 是否选择违章押金
        int amtWZDeposit = 0;
        if(orderPaySign.getPayKind().contains(DataPayKindConstant.RENT)){
            amtWZDeposit =  cashierNoTService.getPayDeposit(orderPaySign.getOrderNo(),orderPaySign.getMenNo(),DataPayKindConstant.DEPOSIT);
            accountPayAbles.add(new AccountPayAbleResVO(orderPaySign.getOrderNo(),orderPaySign.getMenNo(),amtWZDeposit, RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT,RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT.getTxt()));
        }

        //应付租车费用
        int rentAmt =0;
        //已付租车费用
        int rentAmtPayed = 0;
        if(orderPaySign.getPayKind().contains(DataPayKindConstant.TK_FEE)){
            List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableVO(orderPaySign.getOrderNo(),renterOrderNo,orderPaySign.getMenNo());
            //应付租车费用
            rentAmt = cashierNoTService.sumRentOrderCost(payableVOs);
            //已付租车费用
            rentAmtPayed = accountRenterCostSettleService.getCostPaidRent(orderPaySign.getOrderNo(),orderPaySign.getMenNo());
            if(!CollectionUtils.isEmpty(payableVOs)){
                for(int i=0;i<payableVOs.size();i++){
                    PayableVO payableVO = payableVOs.get(i);
                    //判断是租车费用、还是补付 租车费用 并记录 详情
                    RenterCashCodeEnum type = rentAmtPayed>0?RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN:RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST;
                    accountPayAbles.add(new AccountPayAbleResVO(orderPaySign.getOrderNo(),orderPaySign.getMenNo(),payableVO.getAmt(),type,payableVO.getTitle()));
                }
            }
        }

        int amtTotal = amtDeposit + amtWZDeposit + rentAmt;
        result.setAmtTotal(amtTotal);
        result.setAmtPay(rentAmtPayed);
        result.setAmt(amtTotal + rentAmtPayed);
        result.setMemNo(orderPaySign.getMenNo());
        result.setOrderNo(orderPaySign.getOrderNo());
        result.setTitle("待支付金额：" +result.getAmt() + "，订单号："  + result.getOrderNo());
        result.setAccountPayAbles(accountPayAbles);
        return result;
    }




}

