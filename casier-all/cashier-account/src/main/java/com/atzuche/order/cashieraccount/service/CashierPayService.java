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
import com.atzuche.order.cashieraccount.exception.OrderPaySignFailException;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.cashieraccount.service.remote.RefundRemoteService;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPayReqVO;
import com.atzuche.order.cashieraccount.vo.req.pay.OrderPaySignReqVO;
import com.atzuche.order.cashieraccount.vo.res.AccountPayAbleResVO;
import com.atzuche.order.cashieraccount.vo.res.OrderPayableAmountResVO;
import com.atzuche.order.cashieraccount.vo.res.pay.OrderPayCallBackSuccessVO;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.enums.OrderPayStatusEnum;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.YesNoEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.commons.service.OrderPayCallBack;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercost.entity.vo.PayableVO;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.wallet.WalletProxyService;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.autopay.gateway.constant.DataPayTypeConstant;
import com.autoyol.autopay.gateway.util.MD5;
import com.autoyol.autopay.gateway.vo.req.BatchNotifyDataVo;
import com.autoyol.autopay.gateway.vo.req.NotifyDataVo;
import com.autoyol.autopay.gateway.vo.req.PayVo;
import com.autoyol.autopay.gateway.vo.req.RefundVo;
import com.autoyol.autopay.gateway.vo.res.AutoPayResultVo;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ErrorCode;

import ch.qos.logback.classic.Logger;
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
    @Autowired AccountRenterCostSettleService accountRenterCostSettleService;
    @Autowired RenterOrderCostCombineService renterOrderCostCombineService;
    @Autowired CashierNoTService cashierNoTService;
    @Autowired WalletProxyService walletProxyService;
    @Autowired RefundRemoteService refundRemoteService;
    @Autowired CashierRefundApplyNoTService cashierRefundApplyNoTService;
    @Autowired private OrderStatusService orderStatusService;
    @Autowired private OrderFlowService orderFlowService;


    /**
     * 支付系统回调（支付回调，退款回调到时一个）
     * MQ 异步回调
     */
    public void payCallBack(BatchNotifyDataVo batchNotifyDataVo, OrderPayCallBack callBack){
        if(Objects.nonNull(batchNotifyDataVo) && !CollectionUtils.isEmpty(batchNotifyDataVo.getLstNotifyDataVo())){
            // 1 支付信息落库
            OrderPayCallBackSuccessVO vo = cashierService.callBackSuccess(batchNotifyDataVo.getLstNotifyDataVo());
            //2 获取透传值 用户订单流程更新数据
            getExtendParamsParam(vo,batchNotifyDataVo);
           // 3 订单流程 数据更新
            log.info("payCallBack OrderPayCallBackSuccessVO :[{}]", GsonUtils.toJson(vo));
            orderPayCallBack(vo,callBack);
        }
    }

    /**
     * 获取 透传值 set 到 OrderPayCallBackSuccessVO vo
     * @param vo
     * @param batchNotifyDataVo
     */
    private void getExtendParamsParam(OrderPayCallBackSuccessVO vo, BatchNotifyDataVo batchNotifyDataVo) {
        if(Objects.nonNull(batchNotifyDataVo) && !CollectionUtils.isEmpty(batchNotifyDataVo.getLstNotifyDataVo())){
            List<NotifyDataVo> notifyDataVos = batchNotifyDataVo.getLstNotifyDataVo();
            for(int i=0;i<notifyDataVos.size();i++){
                NotifyDataVo notifyDataVo = notifyDataVos.get(i);
                String renterOrderNo = notifyDataVo.getExtendParams();
                //补付需要 子单号
                 if(Objects.nonNull(notifyDataVo) && DataPayKindConstant.RENT_INCREMENT.equals(notifyDataVo.getPayKind())){
                     //返回应付 （包含补付） 费用列表
                     vo.setRenterOrderNo(renterOrderNo);
                }
                if(Objects.nonNull(notifyDataVo) && DataPayKindConstant.RENT_AMOUNT.equals(notifyDataVo.getPayKind())){
                    //返回应付 （包含补付） 费用列表
                    vo.setRenterOrderNo(renterOrderNo);
                }
            }
        }
    }

    private String getExtendParamsRentOrderNo(OrderPayableAmountResVO payVO){
        //返回应付 （包含补付） 费用列表
        List<PayableVO> payableVOs = payVO.getPayableVOs();
        for(int j=0;j<payableVOs.size();j++){
            PayableVO payableVO = payableVOs.get(j);
            if(Objects.nonNull(payableVO.getType()) && 1==payableVO.getType()){
                return payableVO.getUniqueNo();
            }
        }
        RenterOrderEntity renterOrderEntity = cashierNoTService.getRenterOrderNoByOrderNo(payVO.getOrderNo());
        if(Objects.nonNull(renterOrderEntity) && Objects.nonNull(renterOrderEntity.getRenterOrderNo())){
            return renterOrderEntity.getRenterOrderNo();
        }
        return "";
    }

    /**
     * 订单流程 数据更新
     * @param vo
     * @param callBack
     */
    private void orderPayCallBack(OrderPayCallBackSuccessVO vo, OrderPayCallBack callBack) {
        //支付成功更新 订单支付状态
        if(Objects.nonNull(vo)&&Objects.nonNull(vo.getOrderNo())){
            OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
            BeanUtils.copyProperties(vo,orderStatusDTO);
            //当支付成功（当车辆押金，违章押金，租车费用都支付成功，更新订单状态 待取车），更新主订单状态待取车
            if(isChangeOrderStatus(orderStatusDTO)){
                orderStatusDTO.setStatus(OrderStatusEnum.TO_GET_CAR.getStatus());
                vo.setIsGetCar(YesNoEnum.YES);
                //记录订单流程
                orderFlowService.inserOrderStatusChangeProcessInfo(orderStatusDTO.getOrderNo(), OrderStatusEnum.TO_GET_CAR);
            }
            orderStatusService.saveOrderStatusInfo(orderStatusDTO);
            //更新配送 订单补付等信息 只有订单状态为已支付
            if(isGetCar(vo)){
                callBack.callBack(vo.getMemNo(),vo.getOrderNo(),vo.getRenterOrderNo(),vo.getIsPayAgain(),vo.getIsGetCar());
            }
            log.info("payOrderCallBackSuccess saveOrderStatusInfo :[{}]", GsonUtils.toJson(orderStatusDTO));
        }
    }
    /**
     * 判断 租车费用 押金 违章押金 是否全部成功支付
     * @param orderStatusDTO
     * @return
     */
    private Boolean isChangeOrderStatus(OrderStatusDTO orderStatusDTO){
        OrderStatusEntity entity = orderStatusService.getByOrderNo(orderStatusDTO.getOrderNo());
        boolean getCar = false;
        Integer rentCarPayStatus = Objects.isNull(orderStatusDTO.getRentCarPayStatus())?entity.getRentCarPayStatus():orderStatusDTO.getRentCarPayStatus();
        Integer depositPayStatus = Objects.isNull(orderStatusDTO.getDepositPayStatus())?entity.getDepositPayStatus():orderStatusDTO.getDepositPayStatus();
        Integer wzPayStatus = Objects.isNull(orderStatusDTO.getWzPayStatus())?entity.getWzPayStatus():orderStatusDTO.getWzPayStatus();
        if(
                (Objects.nonNull(rentCarPayStatus) && OrderPayStatusEnum.PAYED.getStatus() == rentCarPayStatus)&&
                        ( Objects.nonNull(depositPayStatus) && OrderPayStatusEnum.PAYED.getStatus() == depositPayStatus )&&
                        (Objects.nonNull(wzPayStatus)  && OrderPayStatusEnum.PAYED.getStatus() == wzPayStatus)
        ){
            getCar =true;
        }
        return getCar;
    }
    /**
     * 判断 租车费用 是否成功支付
     * @param orderPayCallBackSuccessVO
     * @return
     */
    private boolean isGetCar(OrderPayCallBackSuccessVO orderPayCallBackSuccessVO){
        boolean getCar = false;
        Integer rentCarPayStatus = orderPayCallBackSuccessVO.getRentCarPayStatus();
        if(Objects.nonNull(rentCarPayStatus) && OrderPayStatusEnum.PAYED.getStatus() == rentCarPayStatus){
            getCar =true;
        }
        return getCar;
    }

    /**
     * 获取支付验签数据
     * @param orderPaySign
     * @return
     */
    @Transactional(rollbackFor=Exception.class)
    public String getPaySignStr(OrderPaySignReqVO orderPaySign,OrderPayCallBack orderPayCallBack){
        //1校验
        Assert.notNull(orderPaySign, ErrorCode.PARAMETER_ERROR.getText());
        orderPaySign.check();
        //3 查询应付
        OrderPayReqVO orderPayReqVO = new OrderPayReqVO();
        BeanUtils.copyProperties(orderPaySign,orderPayReqVO);
        OrderPayableAmountResVO orderPayable = getOrderPayableAmount(orderPayReqVO);
        //4 抵扣钱包
        if(YesNoEnum.YES.getCode()==orderPayable.getIsUseWallet()){
           int payBalance = walletProxyService.getWalletByMemNo(orderPaySign.getMenNo());
           //判断余额大于0
           if(payBalance>0){
               //5 抵扣钱包落库 （收银台落库、费用落库）
               int amtWallet = walletProxyService.orderDeduct(orderPaySign.getMenNo(),orderPaySign.getOrderNo(),orderPayable.getAmtWallet());
               //6收银台 钱包支付落库
               cashierNoTService.insertRenterCostByWallet(orderPaySign,amtWallet);
               //钱包未抵扣部分
               int amtPaying =0;
               if(amtWallet<orderPayable.getAmtWallet()){
                   amtPaying = amtWallet - orderPayable.getAmtWallet();
               }
               orderPayable.setAmtWallet(amtWallet);
               orderPayable.setAmt(orderPayable.getAmt() + amtPaying);
               orderPayable.setAmtRent(orderPayable.getAmtRent() + amtPaying);
               // 钱包支付完租车费用  租车费用为0 状态变更
               if(orderPayable.getAmtRent()==0){
                   cashierService.saveWalletPaylOrderStatusInfo(orderPaySign.getOrderNo());
               }

               //如果待支付 金额等于 0 即 钱包抵扣完成
               if(orderPayable.getAmt()==0){
                   List<String> payKind = orderPaySign.getPayKind();
                   // 如果 支付款项 只有租车费用一个  并且使用钱包支付 ，当待支付金额完全被 钱包抵扣直接返回支付完成
                   if(!CollectionUtils.isEmpty(payKind) && orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_AMOUNT)){
                       //修改子订单费用信息
                       String renterOrderNo = getExtendParamsRentOrderNo(orderPayable);
                       orderPayCallBack.callBack(orderPaySign.getMenNo(),orderPaySign.getOrderNo(),renterOrderNo,orderPayable.getIsPayAgain(),YesNoEnum.NO);
                       return "";
                   }

               }
           }

        }
        //7 签名串
        List<PayVo> payVo = getOrderPayVO(orderPaySign,orderPayable);
        log.info("CashierPayService 加密前费用列表打印 getPaySignStr payVo [{}] ",GsonUtils.toJson(payVo));
        if(CollectionUtils.isEmpty(payVo)){
            throw new OrderPaySignFailException();
        }
        String signStr = cashierNoTService.getPaySignByPayVos(payVo);
        return signStr;
    }

    /**
     * 查询支付款项信息
     */
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
        int rentAmt = 0;
        int rentIncrementAmt = 0;
        //已付租车费用
        int rentAmtPayed = 0;
        //已付租车费用(shifu  租车费用的实付)
        //放在外面，对结果产生了影响。需要内置。
//        rentAmtPayed = accountRenterCostSettleService.getCostPaidRent(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo());

        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_AMOUNT)){  //修改订单的补付
            List<PayableVO> payableVOs = renterOrderCostCombineService.listPayablebGlobalPayVO(orderPayReqVO.getOrderNo(),renterOrderEntity.getRenterOrderNo(),orderPayReqVO.getMenNo());
            result.setPayableVOs(payableVOs);
            //应付租车费用（已经求和）
            rentAmt = cashierNoTService.sumRentOrderCost(payableVOs);
            
            //已付租车费用(shifu  租车费用的实付)
            rentAmtPayed = accountRenterCostSettleService.getCostPaidRent(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo());
            if(!CollectionUtils.isEmpty(payableVOs) && rentAmt+rentAmtPayed < 0){   // 
                for(int i=0;i<payableVOs.size();i++){
                    PayableVO payableVO = payableVOs.get(i);
                    //判断是租车费用、还是补付 租车费用 并记录 详情
//                    RenterCashCodeEnum type = rentAmtPayed>0?RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN:RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST;
                    RenterCashCodeEnum type = RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST;
//                    if(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN.equals(type)){
//                        result.setIsPayAgain(YesNoEnum.YES.getCode());
//                    }
                    result.setIsPayAgain(YesNoEnum.NO.getCode());
                    accountPayAbles.add(new AccountPayAbleResVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo(),payableVO.getAmt(),type,payableVO.getTitle()));
                }
            }
        }
        
        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_INCREMENT)){  //修改订单的补付
            List<PayableVO> payableVOs = renterOrderCostCombineService.listPayablebBasePayVO(orderPayReqVO.getOrderNo(),renterOrderEntity.getRenterOrderNo(),orderPayReqVO.getMenNo());
            result.setPayableVOs(payableVOs);
            //应付租车费用
            rentIncrementAmt = cashierNoTService.sumRentOrderCost(payableVOs);
            //已付租车费用(shifu  租车费用的实付)
            rentAmtPayed = accountRenterCostSettleService.getCostPaidRent(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo());
            if(!CollectionUtils.isEmpty(payableVOs) && rentIncrementAmt+rentAmtPayed < 0){   // +rentAmtPayed
                for(int i=0;i<payableVOs.size();i++){
                    PayableVO payableVO = payableVOs.get(i);
                    //判断是租车费用、还是补付 租车费用 并记录 详情
//                    RenterCashCodeEnum type = rentAmtPayed>0?RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN:RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST;
                    RenterCashCodeEnum type = RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN;
                    if(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN.equals(type)){
                        result.setIsPayAgain(YesNoEnum.YES.getCode());
                    }
                    accountPayAbles.add(new AccountPayAbleResVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo(),payableVO.getAmt(),type,payableVO.getTitle()));
                }
            }
        }
        
        
        //待支付总额
        int amtTotal = amtDeposit + amtWZDeposit + rentAmt;
        //实际待支付租车费用总额 即真实应付租车费用
        int amtRent = rentAmt + rentAmtPayed;
        //补付
        int amtIncrementRent = rentIncrementAmt + rentAmtPayed;
        
        // 计算钱包 支付 目前支付抵扣租费费用
        int amtWallet =0;
        if(YesNoEnum.YES.getCode()==result.getIsUseWallet()){
            int payBalance = walletProxyService.getWalletByMemNo(orderPayReqVO.getMenNo());
            //预计钱包抵扣金额 = amtWallet
            amtWallet = amtRent + payBalance < 0 ? payBalance : Math.abs(amtRent);
            // 抵扣钱包后  应付租车费用金额
            amtRent =  amtRent + amtWallet;
            accountPayAbles.add(new AccountPayAbleResVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo(),amtWallet,RenterCashCodeEnum.ACCOUNT_WALLET_COST,RenterCashCodeEnum.ACCOUNT_WALLET_COST.getTxt()));
        }
        result.setAmtWallet(amtWallet);
        result.setAmtRent(amtRent);
        result.setAmtIncrementRent(amtIncrementRent);
        result.setAmtDeposit(amtDeposit);
        result.setAmtWzDeposit(amtWZDeposit);
        result.setAmtTotal(amtTotal);
        result.setAmtPay(rentAmtPayed);
        result.setAmt(amtRent + amtDeposit + amtWZDeposit);  //result.getAmt()取值。
        result.setMemNo(orderPayReqVO.getMenNo());
        result.setOrderNo(orderPayReqVO.getOrderNo());
        result.setTitle("待支付金额：" +Math.abs(result.getAmt()) + "，订单号："  + result.getOrderNo());
        result.setAccountPayAbles(accountPayAbles);
        //支付显示 文案处理
        handCopywriting(result,orderPayReqVO);
        return result;
    }

    /**
     * 支付显示 文案处理
     * @param result
     */
    private void handCopywriting(OrderPayableAmountResVO result,OrderPayReqVO orderPayReqVO) {
        result.setButtonName("去支付");
        RenterOrderEntity renterOrderEntity = cashierNoTService.getRenterOrderNoByOrderNo(orderPayReqVO.getOrderNo());
        // 租车费用倒计时 单位秒
        long countdown=0L;
        if(Objects.nonNull(renterOrderEntity) && Objects.nonNull(renterOrderEntity.getCreateTime())){
            countdown = DateUtils.getDateLatterCompareNowScoend(renterOrderEntity.getCreateTime(),1);
        }
        String costText ="";
        //租车费用
        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_AMOUNT) || orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_INCREMENT)){
            costText =costText+"租车费用"+ Math.abs(result.getAmtRent()+result.getAmtIncrementRent());
            result.setHints("请于1小时内完成支付，超时未支付订单将自动取消");
            result.setCountdown(countdown);
        }
        //车辆押金
        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT)){
            costText =costText+"车辆押金"+ Math.abs(result.getAmtDeposit());
            result.setHints("交易结束后24小时内，车辆押金将返还到支付账户");
        }
        //违章押金
        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.DEPOSIT)){
            costText =costText+" 违章押金"+Math.abs(result.getAmtWzDeposit());
            result.setHints("交易结束后24小时内，车辆押金将返还到支付账户");
        }
        //车辆押金 + 违章押金
        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT) && orderPayReqVO.getPayKind().contains(DataPayKindConstant.DEPOSIT)){
            costText ="车辆押金"+ Math.abs(result.getAmtDeposit()) + " + " + " 违章押金"+Math.abs(result.getAmtWzDeposit());
            result.setHints("交易结束后24小时内，车辆押金将返还到支付账户");
        }
        //车辆押金 + 租车费用 一起支付
        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT) &&
            (orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_AMOUNT) || orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_INCREMENT))
        ){
            costText ="租车费用"+ Math.abs(result.getAmtRent()+result.getAmtIncrementRent()) + " + " + " 车辆押金"+Math.abs(result.getAmtDeposit());
            result.setHints("交易结束后24小时内，车辆押金将返还到支付账户");
            result.setCountdown(countdown);
        }
        result.setCostText(costText);

    }

//    /**
//     * 查应付
//     * @param orderNo
//     * @param memNo
//     * @return
//     */
//    public int getRentCost(String orderNo,String memNo){
//        RenterOrderEntity renterOrderEntity = cashierNoTService.getRenterOrderNoByOrderNo(orderNo);
//
//        if(Objects.isNull(renterOrderEntity) || Objects.isNull(renterOrderEntity.getRenterOrderNo())){
//            return 0;
//        }
//        //查询应付租车费用列表
//        List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableVO(orderNo,renterOrderEntity.getRenterOrderNo(),memNo);
//        //应付租车费用
//        int rentAmt = cashierNoTService.sumRentOrderCost(payableVOs);
//        //已付租车费用
//        int rentAmtPayed = accountRenterCostSettleService.getCostPaidRent(orderNo,memNo);
//        return rentAmt - rentAmtPayed;
//    }
    
    /**
     * 从getRentCost方法中剥离出来的。 补付金额
     * @param orderNo
     * @param memNo
     * @return
     */
    public int getRentCostBufu(String orderNo,String memNo){
        RenterOrderEntity renterOrderEntity = cashierNoTService.getRenterOrderNoByOrderNo(orderNo);

        if(Objects.isNull(renterOrderEntity) || Objects.isNull(renterOrderEntity.getRenterOrderNo())){
            return 0;
        }
        //查询应付租车费用列表
        List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableVO(orderNo,renterOrderEntity.getRenterOrderNo(),memNo);
        //应付租车费用
        int rentAmt = cashierNoTService.sumRentOrderCost(payableVOs);
        //已付租车费用
        int rentAmtPayed = accountRenterCostSettleService.getCostPaidRent(orderNo,memNo);
        return rentAmt - rentAmtPayed<0?0:(rentAmt - rentAmtPayed);
    }
    
    public int getRealRentCost(String orderNo,String memNo){
        RenterOrderEntity renterOrderEntity = cashierNoTService.getRenterOrderNoByOrderNo(orderNo);
        if(renterOrderEntity==null){
            throw new OrderNotFoundException(orderNo);
        }
        //查询应付租车费用列表
        List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableVO(orderNo,renterOrderEntity.getRenterOrderNo(),memNo);
        //应付租车费用
        int rentAmt = cashierNoTService.sumRentOrderCost(payableVOs);

        return rentAmt;
    }

    /**
     * 查询包装 待支付签名对象
     */
    public List<PayVo> getOrderPayVO(OrderPaySignReqVO orderPaySign,OrderPayableAmountResVO payVO){
        log.info("getOrderPayVO OrderPayableAmountResVO payVO [{}]",GsonUtils.toJson(payVO));
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
                    PayVo vo = cashierNoTService.getPayVO(cashierEntity,orderPaySign,payVO.getAmtDeposit(),payVO.getTitle(),DataPayKindConstant.RENT,payIdStr,GsonUtils.toJson(accountRenterDeposit),accountRenterDeposit.getFreeDepositType());
                    String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
                    vo.setPayMd5(payMd5);
                    payVo.add(vo);
                }else if(RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT.equals(accountPayAbleResVO.getRenterCashCode())){
                    //违章押金
                	CashierEntity cashierEntity = cashierNoTService.getCashierEntity(orderPaySign.getOrderNo(),orderPaySign.getMenNo(), DataPayKindConstant.DEPOSIT);
                    AccountRenterWZDepositResVO accountRenterWZDepositRes = cashierService.getRenterWZDepositEntity(orderPaySign.getOrderNo(),orderPaySign.getMenNo());
                    Integer payId = Objects.isNull(accountRenterWZDepositRes)?0:accountRenterWZDepositRes.getId();
                    String payIdStr = Objects.isNull(payId)?"":String.valueOf(payId);
                    PayVo vo = cashierNoTService.getPayVO(cashierEntity,orderPaySign,payVO.getAmtWzDeposit(),payVO.getTitle(),DataPayKindConstant.DEPOSIT,payIdStr,GsonUtils.toJson(accountRenterWZDepositRes),accountRenterWZDepositRes.getFreeDepositType());
                    String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
                    vo.setPayMd5(payMd5);
                    payVo.add(vo);
                }
            }
        }

        //待付租车费用   租车费用默认是消费
        if(orderPaySign.getPayKind().contains(DataPayKindConstant.RENT_AMOUNT) && payVO.getAmtRent()<0){
            //待付租车费用
            int amt = payVO.getAmtRent();
            orderPaySign.setPayType(DataPayTypeConstant.PAY_PUR);
            //去掉该条件，根据入参来。
//            String payKind = YesNoEnum.YES.getCode().equals(payVO.getIsPayAgain())?DataPayKindConstant.RENT_INCREMENT:DataPayKindConstant.RENT_AMOUNT;
            String payKind = DataPayKindConstant.RENT_AMOUNT;
            AccountRenterCostSettleEntity entity = cashierService.getAccountRenterCostSettle(orderPaySign.getOrderNo(),orderPaySign.getMenNo());
            Integer payId = Objects.isNull(entity)?0:entity.getId();
            String payIdStr = Objects.isNull(payId)?"":String.valueOf(payId);
            PayVo vo = cashierNoTService.getPayVO(null,orderPaySign,payVO.getAmtRent(),payVO.getTitle(),payKind,payIdStr,GsonUtils.toJson(entity),3);
            String paySn = cashierNoTService.getCashierRentCostPaySn(orderPaySign.getOrderNo(),orderPaySign.getMenNo(),payKind);
            vo.setPaySn(paySn);
            String renterOrderNo = getExtendParamsRentOrderNo(payVO);
            vo.setExtendParams(renterOrderNo);
            vo.setPayTitle("待支付金额："+amt+"，订单号："+vo.getOrderNo());
            String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
            vo.setPayMd5(payMd5);
            payVo.add(vo);
        }else if(orderPaySign.getPayKind().contains(DataPayKindConstant.RENT_INCREMENT) && payVO.getAmtIncrementRent()<0){
            //待付租车费用
            int amt = payVO.getAmtIncrementRent();
            //统一按消费来处理(忽略前端的来值),消费考虑到车主拒绝修改订单会退款。仍然保持老系统的。 考虑收款及补充租车费用。
            orderPaySign.setPayType(DataPayTypeConstant.PAY_PUR);
            
            //去掉该条件，根据入参来。
//            String payKind = YesNoEnum.YES.getCode().equals(payVO.getIsPayAgain())?DataPayKindConstant.RENT_INCREMENT:DataPayKindConstant.RENT_AMOUNT;
            String payKind = DataPayKindConstant.RENT_INCREMENT;
            AccountRenterCostSettleEntity entity = cashierService.getAccountRenterCostSettle(orderPaySign.getOrderNo(),orderPaySign.getMenNo());
            Integer payId = Objects.isNull(entity)?0:entity.getId();
            String payIdStr = Objects.isNull(payId)?"":String.valueOf(payId);
            PayVo vo = cashierNoTService.getPayVO(null,orderPaySign,payVO.getAmtIncrementRent(),payVO.getTitle(),payKind,payIdStr,GsonUtils.toJson(entity),3);
            String paySn = cashierNoTService.getCashierRentCostPaySn(orderPaySign.getOrderNo(),orderPaySign.getMenNo(),payKind);
            vo.setPaySn(paySn);
            String renterOrderNo = getExtendParamsRentOrderNo(payVO);
            vo.setExtendParams(renterOrderNo);
            vo.setPayTitle("待支付金额："+amt+"，订单号："+vo.getOrderNo());
            String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
            vo.setPayMd5(payMd5);
            payVo.add(vo);
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
        //更新退款次数，最多允许退3次。 num<3 LIMIT 100
        cashierRefundApplyNoTService.updateCashierRefundApplyEntity(cashierRefundApply);
        //2 构造退款参数
        RefundVo refundVo = cashierNoTService.getRefundVo(cashierRefundApply);
       //3退款
        AutoPayResultVo vo = refundRemoteService.refundOrderPay(refundVo);
        if(Objects.nonNull(vo)){
        	log.info("退款返回的结果vo=[{}],params=[{}]",GsonUtils.toJson(vo),GsonUtils.toJson(refundVo));
        	
            NotifyDataVo notifyDataVo = new NotifyDataVo();
            BeanUtils.copyProperties(vo,notifyDataVo);
            notifyDataVo.setSettleAmount(vo.getRefundAmt());
            //退款调用成功操作
            cashierService.refundCallBackSuccess(vo);
        }else {
        	log.error("退款返回的结果vo为null异常,params=[{}]",GsonUtils.toJson(refundVo));
        }
    }

}

