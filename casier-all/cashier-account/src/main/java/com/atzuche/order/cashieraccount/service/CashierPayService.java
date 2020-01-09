package com.atzuche.order.cashieraccount.service;

import com.atzuche.order.accountrenterdeposit.service.AccountRenterDepositService;
import com.atzuche.order.accountrenterdeposit.vo.res.AccountRenterDepositResVO;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleEntity;
import com.atzuche.order.accountrenterrentcost.service.AccountRenterCostSettleService;
import com.atzuche.order.accountrenterwzdepost.service.AccountRenterWzDepositService;
import com.atzuche.order.accountrenterwzdepost.vo.res.AccountRenterWZDepositResVO;
import com.atzuche.order.cashieraccount.common.FasterJsonUtil;
import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.exception.OrderPayCallBackAsnyException;
import com.atzuche.order.cashieraccount.exception.OrderPaySignFailException;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.cashieraccount.service.remote.RefundRemoteService;
import com.atzuche.order.cashieraccount.service.remote.WalletRemoteService;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPayReqVO;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPaySignReqVO;
import com.atzuche.order.cashieraccount.vo.res.AccountPayAbleResVO;
import com.atzuche.order.cashieraccount.vo.res.OrderPayableAmountResVO;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.YesNoEnum;
import com.atzuche.order.commons.service.OrderPayCallBack;
import com.atzuche.order.commons.service.RabbitMsgLogService;
import com.atzuche.order.rentercost.entity.vo.PayableVO;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.autopay.gateway.util.MD5;
import com.autoyol.autopay.gateway.vo.req.BatchNotifyDataVo;
import com.autoyol.autopay.gateway.vo.req.NotifyDataVo;
import com.autoyol.autopay.gateway.vo.req.PayVo;
import com.autoyol.autopay.gateway.vo.req.RefundVo;
import com.autoyol.autopay.gateway.vo.res.AutoPayResultVo;
import com.autoyol.cat.CatAnnotation;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.vo.req.WalletDeductionReqVO;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
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
    @Autowired RefundRemoteService refundRemoteService;
    @Autowired CashierRefundApplyNoTService cashierRefundApplyNoTService;

    /**
     * 支付系统回调（支付回调，退款回调到时一个）
     * MQ 异步回调
     */
    public void payCallBack(BatchNotifyDataVo batchNotifyDataVo, OrderPayCallBack callBack){
        Transaction t = Cat.getProducer().newTransaction(CatConstants.RABBIT_MQ_CALL, "支付系统rabbitMQ异步回调payCallBackAsyn");
        try {
            Cat.logEvent(CatConstants.RABBIT_MQ_METHOD,"OrderPayCallBackRabbitConfig.payCallBackAsyn");
            Cat.logEvent(CatConstants.RABBIT_MQ_PARAM,GsonUtils.toJson(batchNotifyDataVo));
            //1 校验是否 为空
            if(Objects.nonNull(batchNotifyDataVo) && !CollectionUtils.isEmpty(batchNotifyDataVo.getLstNotifyDataVo())){
                cashierService.callBackSuccess(batchNotifyDataVo.getLstNotifyDataVo(),callBack);
            }
            //3 更新rabbitMQ 记录已消费
            String reqContent = FasterJsonUtil.toJson(batchNotifyDataVo);
            String md5 =  MD5.MD5Encode(reqContent);
            rabbitMsgLogService.updateConsume(md5);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            log.info("OrderPayCallBack payCallBackAsyn start param;[{}]", GsonUtils.toJson(batchNotifyDataVo));
            t.setStatus(e);
            Cat.logError("异步处理支付系统回调 失败",e);
            throw new OrderPayCallBackAsnyException();
        } finally {
            log.info("OrderPayCallBack payCallBackAsyn start end;[{}]", GsonUtils.toJson(batchNotifyDataVo));
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

        //3 查询应付
        OrderPayReqVO orderPayReqVO = new OrderPayReqVO();
        BeanUtils.copyProperties(orderPaySign,orderPayReqVO);
        OrderPayableAmountResVO payVO = getOrderPayableAmount(orderPayReqVO);
        //4 抵扣钱包
        if(YesNoEnum.YES.getCode()==payVO.getIsUseWallet()){
           int payBalance = walletRemoteService.getWalletPayBalanceByMemNo(orderPaySign.getMenNo());
           //判断余额大于0
           if(payBalance>0){
               //返回待支付次数
               int paySn = cashierNoTService.payOrderByWallet(orderPaySign);
               //5 抵扣钱包落库 （收银台落库、费用落库）
               WalletDeductionReqVO walletDeduction = cashierNoTService.getWalletDeductionReqVO(orderPaySign,payVO,paySn);
               walletRemoteService.updateWalletByDeduct(walletDeduction);
               //6收银台 钱包支付落库
               cashierNoTService.insertRenterCostByWallet(orderPaySign,payVO.getAmtWallet());
           }
        }
        //7 签名串
        List<PayVo> payVo = getOrderPayVO(orderPaySign,payVO);
        if(CollectionUtils.isEmpty(payVo)){
            throw new OrderPaySignFailException();
        }
        String signStr = cashierNoTService.getPaySignByPayVos(payVo);
        return signStr;
    }

    /**
     * 查询支付款项信息
     */
    @CatAnnotation
    public OrderPayableAmountResVO getOrderPayableAmount(OrderPayReqVO orderPayReqVO){
        Assert.notNull(orderPayReqVO, ErrorCode.PARAMETER_ERROR.getText());
        orderPayReqVO.check();
        //1 查询子单号
        RenterOrderEntity renterOrderEntity = cashierNoTService.getRenterOrderNoByOrderNo(orderPayReqVO.getOrderNo());
        OrderPayableAmountResVO result = new OrderPayableAmountResVO();
        // 判断是否支持 钱包支付 、页面传入是否使用钱包标记 优先
        Integer isUseWallet = Objects.isNull(orderPayReqVO.getIsUseWallet())?renterOrderEntity.getIsUseWallet():orderPayReqVO.getIsUseWallet();
        result.setIsUseWallet(isUseWallet);

        //待支付金额明细
        List<AccountPayAbleResVO> accountPayAbles = new ArrayList<>();
        //车辆押金 是否选择车辆押金
        int amtDeposit = 0;
        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT)){
            amtDeposit = cashierService.getRenterDeposit(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo());
           if(amtDeposit < 0){
               accountPayAbles.add(new AccountPayAbleResVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo(),amtDeposit, RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT,RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT.getTxt()));
           }
        }

        //违章押金 是否选择违章押金
        int amtWZDeposit = 0;
        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.DEPOSIT)){
            amtWZDeposit = cashierService.getRenterWZDeposit(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo());
            if(amtWZDeposit < 0){
                accountPayAbles.add(new AccountPayAbleResVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo(),amtWZDeposit, RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT,RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT.getTxt()));
            }
        }

        //应付租车费用
        int rentAmt =0;
        //已付租车费用
        int rentAmtPayed = 0;
        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_AMOUNT)){
            List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableVO(orderPayReqVO.getOrderNo(),renterOrderEntity.getRenterOrderNo(),orderPayReqVO.getMenNo());
            result.setPayableVOs(payableVOs);
            //应付租车费用
            rentAmt = cashierNoTService.sumRentOrderCost(payableVOs);
            //已付租车费用
            rentAmtPayed = accountRenterCostSettleService.getCostPaidRent(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo());
            if(!CollectionUtils.isEmpty(payableVOs) && rentAmt+rentAmtPayed <0){
                for(int i=0;i<payableVOs.size();i++){
                    PayableVO payableVO = payableVOs.get(i);
                    //判断是租车费用、还是补付 租车费用 并记录 详情
                    RenterCashCodeEnum type = rentAmtPayed>0?RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN:RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST;
                    accountPayAbles.add(new AccountPayAbleResVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo(),payableVO.getAmt(),type,payableVO.getTitle()));
                }
            }
        }
        //待支付总额
        int amtTotal = amtDeposit + amtWZDeposit + rentAmt;
        //实际待支付总额
        Integer amtRent = rentAmt+rentAmtPayed;
        // 计算钱包 支付
        int amtWallet =0;
        if(YesNoEnum.YES.getCode()==result.getIsUseWallet()){
            int payBalance = walletRemoteService.getWalletPayBalanceByMemNo(orderPayReqVO.getMenNo());
            //预计钱包抵扣金额 = amtWallet
            amtWallet = amtRent + payBalance < 0 ? -payBalance : amtRent;
            // 抵扣钱包后  应付金额
            amtRent =  amtRent + payBalance < 0 ? amtRent + payBalance : 0;
            accountPayAbles.add(new AccountPayAbleResVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo(),amtRent,RenterCashCodeEnum.ACCOUNT_WALLET_COST,RenterCashCodeEnum.ACCOUNT_WALLET_COST.getTxt()));
        }
        result.setAmtWallet(amtWallet);
        result.setAmtRent(amtRent);
        result.setAmtDeposit(amtDeposit);
        result.setAmtWzDeposit(amtWZDeposit);
        result.setAmtTotal(amtTotal);
        result.setAmtPay(rentAmtPayed);
        result.setAmt(amtTotal + rentAmtPayed);
        result.setMemNo(orderPayReqVO.getMenNo());
        result.setOrderNo(orderPayReqVO.getOrderNo());
        result.setTitle("待支付金额：" +result.getAmt() + "，订单号："  + result.getOrderNo());
        result.setAccountPayAbles(accountPayAbles);
        return result;
    }

    /**
     * 查询包装 待支付签名对象
     */
    @CatAnnotation
    public  List<PayVo> getOrderPayVO(OrderPaySignReqVO orderPaySign,OrderPayableAmountResVO payVO){
        //待支付金额明细
        List<PayVo> payVo = new ArrayList<>();
        List<AccountPayAbleResVO> accountPayAbles = payVO.getAccountPayAbles();
        if(!CollectionUtils.isEmpty(accountPayAbles)){
            for(int i =0;i<accountPayAbles.size();i++){
                AccountPayAbleResVO accountPayAbleResVO =  accountPayAbles.get(i);
                //车俩押金
                if(RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT.equals(accountPayAbleResVO.getRenterCashCode())){
                    CashierEntity cashierEntity = cashierNoTService.getCashierEntity(orderPaySign.getOrderNo(),orderPaySign.getMenNo(), DataPayKindConstant.RENT);
                    AccountRenterDepositResVO accountRenterDeposit = cashierService.getRenterDepositEntity(orderPaySign.getOrderNo(),orderPaySign.getMenNo());
                    Integer payId = Objects.isNull(accountRenterDeposit)?0:accountRenterDeposit.getId();
                    String payIdStr = Objects.isNull(payId)?"":String.valueOf(payId);
                    PayVo vo = cashierNoTService.getPayVO(cashierEntity,orderPaySign,payVO.getAmtDeposit(),payVO.getTitle(),DataPayKindConstant.RENT,payIdStr,GsonUtils.toJson(accountRenterDeposit));
                    String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
                    vo.setPayMd5(payMd5);
                    payVo.add(vo);
                }
                //违章押金
                if(RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT.equals(accountPayAbleResVO.getRenterCashCode())){
                    CashierEntity cashierEntity = cashierNoTService.getCashierEntity(orderPaySign.getOrderNo(),orderPaySign.getMenNo(), DataPayKindConstant.DEPOSIT);
                    AccountRenterWZDepositResVO accountRenterWZDepositRes = cashierService.getRenterWZDepositEntity(orderPaySign.getOrderNo(),orderPaySign.getMenNo());
                    Integer payId = Objects.isNull(accountRenterWZDepositRes)?0:accountRenterWZDepositRes.getId();
                    String payIdStr = Objects.isNull(payId)?"":String.valueOf(payId);
                    PayVo vo = cashierNoTService.getPayVO(cashierEntity,orderPaySign,payVO.getAmtWzDeposit(),payVO.getTitle(),DataPayKindConstant.DEPOSIT,payIdStr,GsonUtils.toJson(accountRenterWZDepositRes));
                    String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
                    vo.setPayMd5(payMd5);
                    payVo.add(vo);
                }
            }
        }
//        //车辆押金 是否选择车辆押金
//        if(orderPaySign.getPayKind().contains(DataPayKindConstant.RENT) && payVO.getAmtDeposit()<0){
//          CashierEntity cashierEntity = cashierNoTService.getCashierEntity(orderPaySign.getOrderNo(),orderPaySign.getMenNo(), DataPayKindConstant.RENT);
//            if(Objects.nonNull(cashierEntity)){
//              PayVo vo = cashierNoTService.getPayVO(cashierEntity,orderPaySign,payVO.getAmtDeposit(),payVO.getTitle(),DataPayKindConstant.RENT);
//              String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
//              vo.setPayMd5(payMd5);
//              payVo.add(vo);
//          }
//        }
//
//        //违章押金 是否选择违章押金
//        if(orderPaySign.getPayKind().contains(DataPayKindConstant.DEPOSIT) && payVO.getAmtWzDeposit()<0){
//            CashierEntity cashierEntity = cashierNoTService.getCashierEntity(orderPaySign.getOrderNo(),orderPaySign.getMenNo(), DataPayKindConstant.DEPOSIT);
//            if(Objects.nonNull(cashierEntity)){
//                PayVo vo = cashierNoTService.getPayVO(cashierEntity,orderPaySign,payVO.getAmtWzDeposit(),payVO.getTitle(),DataPayKindConstant.DEPOSIT);
//                String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
//                vo.setPayMd5(payMd5);
//                payVo.add(vo);
//            }
//
//        }

        //待付租车费用
        if(orderPaySign.getPayKind().contains(DataPayKindConstant.RENT_AMOUNT) && payVO.getAmtRent()<0){
            List<PayableVO> payableVOs = payVO.getPayableVOs();
            //待付租车费用
            int amt = payVO.getAmt();
            if(amt<0){
                CashierEntity cashierEntity = cashierNoTService.getCashierEntity(orderPaySign.getOrderNo(),orderPaySign.getMenNo(), DataPayKindConstant.RENT_AMOUNT);
                AccountRenterCostSettleEntity entity = cashierService.getAccountRenterCostSettle(orderPaySign.getOrderNo(),orderPaySign.getMenNo());
                Integer payId = Objects.isNull(entity)?0:entity.getId();
                String payIdStr = Objects.isNull(payId)?"":String.valueOf(payId);
                PayVo vo = cashierNoTService.getPayVO(cashierEntity,orderPaySign,payVO.getAmtRent(),payVO.getTitle(),DataPayKindConstant.RENT_AMOUNT,payIdStr,GsonUtils.toJson(entity));
                String paySn = cashierNoTService.getCashierRentCostPaySn(orderPaySign.getOrderNo(),orderPaySign.getMenNo());
                vo.setPaySn(paySn);
                vo.setExtendParams(GsonUtils.toJson(payableVOs));
                String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
                vo.setPayMd5(payMd5);
                payVo.add(vo);
            }
        }
        return payVo;
    }

    /**
     * 退款操作
     * @param cashierRefundApply
     */
    public void refundOrderPay(CashierRefundApplyEntity cashierRefundApply){
        if(Objects.isNull(cashierRefundApply) || Objects.isNull(cashierRefundApply.getId())){
            return;
        }
        //更新退款次数
        cashierRefundApplyNoTService.updateCashierRefundApplyEntity(cashierRefundApply);
        //2 构造退款参数
        RefundVo refundVo = cashierNoTService.getRefundVo(cashierRefundApply);
       //3退款
        AutoPayResultVo vo = refundRemoteService.refundOrderPay(refundVo);
        if(Objects.nonNull(vo)){
            NotifyDataVo notifyDataVo = new NotifyDataVo();
            BeanUtils.copyProperties(vo,notifyDataVo);
            notifyDataVo.setSettleAmount(vo.getRefundAmt());
            //退款成功操作
            cashierService.refundCallBackSuccess(vo);
        }
    }

}

