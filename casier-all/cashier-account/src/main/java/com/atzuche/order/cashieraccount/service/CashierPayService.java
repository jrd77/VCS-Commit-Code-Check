package com.atzuche.order.cashieraccount.service;

import com.atzuche.order.accountrenterdeposit.service.AccountRenterDepositService;
import com.atzuche.order.accountrenterdeposit.vo.res.AccountRenterDepositResVO;
import com.atzuche.order.accountrenterrentcost.entity.AccountRenterCostSettleEntity;
import com.atzuche.order.accountrenterrentcost.service.AccountRenterCostSettleService;
import com.atzuche.order.accountrenterrentcost.service.notservice.AccountRenterCostDetailNoTService;
import com.atzuche.order.accountrenterwzdepost.service.AccountRenterWzDepositService;
import com.atzuche.order.accountrenterwzdepost.vo.res.AccountRenterWZDepositResVO;
import com.atzuche.order.cashieraccount.common.FasterJsonUtil;
import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import com.atzuche.order.cashieraccount.exception.OrderPaySignFailException;
import com.atzuche.order.cashieraccount.service.notservice.AccountVirtualPayService;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.cashieraccount.service.notservice.CashierRefundApplyNoTService;
import com.atzuche.order.cashieraccount.service.remote.RefundRemoteService;
import com.atzuche.order.cashieraccount.vo.req.pay.*;
import com.atzuche.order.cashieraccount.vo.res.AccountPayAbleResVO;
import com.atzuche.order.cashieraccount.vo.res.OrderPayableAmountResVO;
import com.atzuche.order.cashieraccount.vo.res.pay.OrderPayCallBackSuccessVO;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.enums.OrderPayStatusEnum;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.YesNoEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.cashier.PaySourceEnum;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.commons.service.OrderPayCallBack;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.delivery.vo.delivery.ChangeOrderInfoDTO;
import com.atzuche.order.flow.service.OrderFlowService;
import com.atzuche.order.parentorder.dto.OrderStatusDTO;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercost.entity.vo.PayableVO;
import com.atzuche.order.rentercost.service.OrderConsoleSubsidyDetailService;
import com.atzuche.order.rentercost.service.OrderSupplementDetailService;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.wallet.WalletProxyService;
import com.autoyol.autopay.gateway.constant.DataAppIdConstant;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.autopay.gateway.constant.DataPaySourceConstant;
import com.autoyol.autopay.gateway.constant.DataPayTypeConstant;
import com.autoyol.autopay.gateway.util.MD5;
import com.autoyol.autopay.gateway.util.PayTransIdCreator;
import com.autoyol.autopay.gateway.vo.req.*;
import com.autoyol.autopay.gateway.vo.res.AutoPayResultVo;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


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
    @Autowired
    private AccountVirtualPayService virtualPayService;
    @Autowired
    private OrderSupplementDetailService orderSupplementDetailService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private AccountRenterCostDetailNoTService accountRenterCostDetailNoTService;
    @Autowired
    private RenterMemberService renterMemberService;
    @Autowired
	private RestTemplate restTemplate;
	@Value("${new.order.pay.gateway.url}")
	private String newOrderPayGatewayURL;
	@Autowired
	private CashierPayService cashierPayService;
	@Autowired
	private OrderConsoleSubsidyDetailService orderConsoleSubsidyDetailService;
	@Autowired
	private OrderPaySuccessService orderPaySuccessService;
	@Autowired
	private DeliveryCarService deliveryCarService;


    public void virtualPay(VirtualPayDTO virtualPayVO,OrderPayCallBack callBack){
        VirtualNotifyDataVO notifyDataVo = new VirtualNotifyDataVO();
        String orderNo = virtualPayVO.getOrderNo();
        String memNo = virtualPayVO.getMemNo();
        notifyDataVo.setOrderNo(orderNo);
        notifyDataVo.setMemNo(memNo);
        notifyDataVo.setAtappId("20");
        notifyDataVo.setPayKind(virtualPayVO.getCashType().getValue());
        notifyDataVo.setPayType(virtualPayVO.getPayType().getValue());
        notifyDataVo.setPaySource("91");
        notifyDataVo.setTransStatus("00");
        notifyDataVo.setSettleAmount(String.valueOf(virtualPayVO.getPayAmt()));
        notifyDataVo.setPayLine(2);
        notifyDataVo.setVirtualAccountNo(virtualPayVO.getAccountEnum().getAccountNo());
        notifyDataVo.setExtendParams(virtualPayVO.getRenterNo());
        notifyDataVo.setOrderTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(LocalDateTimeUtils.YYYYMMDDHHMMSSS_PATTERN)));



        List<NotifyDataVo> list = new ArrayList<>();
        list.add(notifyDataVo);
        BatchNotifyDataVo batchNotifyDataVo = new BatchNotifyDataVo();
        batchNotifyDataVo.setLstNotifyDataVo(list);

        payCallBack(batchNotifyDataVo,callBack);
        
        //根据支付状态来处理，租车费用已支付，则刷新。200512
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        if(orderStatusEntity != null && orderStatusEntity.getRentCarPayStatus() != null && orderStatusEntity.getRentCarPayStatus().intValue() == 1) {
        	 RenterOrderEntity renterOrderEntity = cashierNoTService.getRenterOrderNoByOrderNo(orderNo);
             boolean isEnterpriseUserOrder = renterMemberService.isEnterpriseUserOrder(renterOrderEntity.getRenterOrderNo());
             //企业用户
             if(isEnterpriseUserOrder) {
            	 cashierPayService.getPaySignStrNewOfflinePay(orderNo, memNo, isEnterpriseUserOrder);
             }
        }
    }


    public void offlinePay(OfflinePayDTO payVO,OrderPayCallBack callBack){
        OfflineNotifyDataVO  notifyDataVo = new OfflineNotifyDataVO();
        String orderNo = payVO.getOrderNo();
        String memNo = payVO.getMemNo();
        notifyDataVo.setOrderNo(orderNo);
        notifyDataVo.setMemNo(memNo);
        notifyDataVo.setAtappId("20");
        notifyDataVo.setPayKind(payVO.getCashType().getValue());
        notifyDataVo.setPayType(payVO.getPayType().getCode());
        notifyDataVo.setPaySource("02");
        notifyDataVo.setTransStatus("00");
        notifyDataVo.setSettleAmount(String.valueOf(payVO.getPayAmt()));
        notifyDataVo.setPayLine(1);
        notifyDataVo.setExtendParams(payVO.getRenterNo());
        notifyDataVo.setInternalNo(payVO.getInternalNo());
        notifyDataVo.setQn(payVO.getQn());
        notifyDataVo.setPayChannel(payVO.getPayChannel());
        notifyDataVo.setOrderTime(payVO.getPayTime());


        List<NotifyDataVo> list = new ArrayList<>();
        list.add(notifyDataVo);
        BatchNotifyDataVo batchNotifyDataVo = new BatchNotifyDataVo();
        batchNotifyDataVo.setLstNotifyDataVo(list);

        payCallBack(batchNotifyDataVo,callBack);
        
        //根据支付状态来处理，租车费用已支付，则刷新。200512
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        
        //租车费用支付成功
        if(orderStatusEntity != null && orderStatusEntity.getRentCarPayStatus() != null && orderStatusEntity.getRentCarPayStatus().intValue() == 1) {
            RenterOrderEntity renterOrderEntity = cashierNoTService.getRenterOrderNoByOrderNo(orderNo);
            boolean isEnterpriseUserOrder = renterMemberService.isEnterpriseUserOrder(renterOrderEntity.getRenterOrderNo());
            //企业用户
            if(isEnterpriseUserOrder) {
            	cashierPayService.getPaySignStrNewOfflinePay(orderNo, memNo, isEnterpriseUserOrder);
            }
        }
        
    }

    



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
            log.info("payCallBack OrderPayCallBackSuccessVO start:[{}]", GsonUtils.toJson(vo));
            orderPaySuccessService.orderPayCallBack(vo,callBack);
            log.info("payCallBack OrderPayCallBackSuccessVO end:[{}]", GsonUtils.toJson(vo));
        }
    }
    
    public void refundCallBack(BatchNotifyDataVo batchNotifyDataVo, OrderPayCallBack callBack){
        if(Objects.nonNull(batchNotifyDataVo) && !CollectionUtils.isEmpty(batchNotifyDataVo.getLstNotifyDataVo())){
            // 1 支付信息落库
        	OrderPayCallBackSuccessVO vo = cashierService.callBackSuccess(batchNotifyDataVo.getLstNotifyDataVo());
            
            //2 获取透传值 用户订单流程更新数据
//            getExtendParamsParam(vo,batchNotifyDataVo);
           // 3 订单流程 数据更新
            log.info("(退款异步处理收银台)callBackSuccess:[{}]", GsonUtils.toJson(vo));
//            orderPayCallBack(vo,callBack);
            
            //异步通知更新的是收银台的状态。 200408 
            
            //2.参考同步方法,同步方法更新是 退款申请表
            if(!CollectionUtils.isEmpty(batchNotifyDataVo.getLstNotifyDataVo())){
	            for(int i=0;i<batchNotifyDataVo.getLstNotifyDataVo().size();i++){
	                NotifyDataVo notifyDataVo = batchNotifyDataVo.getLstNotifyDataVo().get(i);
	                log.info("退款的notifyDataVo=[{}]",GsonUtils.toJson(notifyDataVo));
	                AutoPayResultVo autoPayResultVo = new AutoPayResultVo();
	                BeanUtils.copyProperties(notifyDataVo, autoPayResultVo);
	                //退款ID
	                autoPayResultVo.setRefundId(notifyDataVo.getPayId());
	                log.info("退款的autoPayResultVo=[{}]",GsonUtils.toJson(autoPayResultVo));
	                //退款调用成功操作
	                cashierService.refundCallBackSuccess(autoPayResultVo);
	                log.info("(退款异步处理退款申请表)refundCallBackSuccess:[{}]", GsonUtils.toJson(autoPayResultVo));
	            }
            }
            
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
            //根据金额匹配来修改状态。
        	List<NotifyDataVo> supplementIds = new ArrayList<NotifyDataVo>();
        	List<NotifyDataVo> debtIds = new ArrayList<NotifyDataVo>();
        	List<String> rentAmountAfterRenterOrderNos = new ArrayList<String>();

        	//转换为集合的概念
            for(int i=0;i<notifyDataVos.size();i++){
                NotifyDataVo notifyDataVo = notifyDataVos.get(i);
                //扩展参数
                String extendParams = notifyDataVo.getExtendParams();
                if(Objects.nonNull(notifyDataVo) && DataPayKindConstant.RENT_AMOUNT.equals(notifyDataVo.getPayKind())){
                    //返回应付 （包含补付） 费用列表
                    vo.setRenterOrderNo(extendParams);
                }
                //APP补付需要 子单号
                if(Objects.nonNull(notifyDataVo) && DataPayKindConstant.RENT_INCREMENT.equals(notifyDataVo.getPayKind())){
                    //返回应付 （包含补付） 费用列表
                    vo.setRenterOrderNo(extendParams);
                }
                
                // ------------------------------------------------------------------------------- 分割线
                
                if(Objects.nonNull(notifyDataVo) && DataPayKindConstant.RENT_AMOUNT_AFTER.equals(notifyDataVo.getPayKind())){
                    //返回应付 （包含补付） 费用列表
                	rentAmountAfterRenterOrderNos.add(extendParams);
                }
                
                //管理后台修改订单补付 add 200312 
                if(Objects.nonNull(notifyDataVo) && DataPayKindConstant.RENT_INCREMENT_CONSOLE.equals(notifyDataVo.getPayKind())){
                    //返回应付 （包含补付） 费用列表
//                	Map<String,Integer> map = new HashMap<String,Integer>();
//                	map.put(extendParams, Integer.valueOf(notifyDataVo.getSettleAmount()));
                	supplementIds.add(notifyDataVo);
                }
                //支付欠款 add 200312 
                if(Objects.nonNull(notifyDataVo) && DataPayKindConstant.DEBT.equals(notifyDataVo.getPayKind())){
                    //返回应付 （包含补付） 费用列表
//                	Map<String,Integer> map = new HashMap<String,Integer>();
//                	map.put(extendParams, Integer.valueOf(notifyDataVo.getSettleAmount()));
                	debtIds.add(notifyDataVo);
                }
            }
            
            //数据封装
            vo.setRentAmountAfterRenterOrderNos(rentAmountAfterRenterOrderNos);
            vo.setSupplementIds(supplementIds);
            vo.setDebtIds(debtIds);
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
        if(Objects.nonNull(vo) && Objects.nonNull(vo.getOrderNo())){
        	//order_supplement_detail  支付欠款的逻辑处理,否则结算后补付会修改订单状态。分离。
        	//rentAmountAfterRenterOrderNo 暂不处理。
        	if( !CollectionUtils.isEmpty(vo.getSupplementIds()) || !CollectionUtils.isEmpty(vo.getDebtIds()) ) {
        		//补付总和  vo.getMemNo(), vo.getIsPayAgain(),vo.getIsGetCar(),
        		//支付欠款，不更新订单状态。
        		callBack.callBack(vo.getOrderNo(),vo.getRentAmountAfterRenterOrderNos(),vo.getSupplementIds(),vo.getDebtIds());
        	}else {
	            OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
	            BeanUtils.copyProperties(vo,orderStatusDTO);
	            //当支付成功（当车辆押金，违章押金，租车费用都支付成功，更新订单状态 待取车），更新主订单状态待取车
	            if(isChangeOrderStatus(orderStatusDTO)){
	                orderStatusDTO.setStatus(OrderStatusEnum.TO_GET_CAR.getStatus());
	                vo.setIsGetCar(YesNoEnum.YES);
	                //记录订单流程
	                orderFlowService.inserOrderStatusChangeProcessInfo(orderStatusDTO.getOrderNo(), OrderStatusEnum.TO_GET_CAR);
	            }
	            //更新支付状态（含批量修改，支付租车费用，租车押金，违章押金）
	            orderStatusService.saveOrderStatusInfo(orderStatusDTO);
                log.info("押金支付发送给任云orderStatusDTO={}",orderStatusDTO);
                deliveryCarService.changeRenYunFlowOrderInfo(new ChangeOrderInfoDTO().setOrderNo(vo.getOrderNo()));
	            //更新配送 订单补付等信息 只有订单状态为已支付
	            //callback
	            if(isGetCar(vo)){
	                //异步通知处理类
	            	callBack.callBack(vo.getMemNo(),vo.getOrderNo(),vo.getRenterOrderNo(),vo.getIsPayAgain(),vo.getIsGetCar());
	                
	            }
	            log.info("payOrderCallBackSuccess saveOrderStatusInfo :[{}]", GsonUtils.toJson(orderStatusDTO));
        	}
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
        if(entity != null) {
        	log.info("支付变更订单状态回调通知OrderStatusEntity result=[{}]",GsonUtils.toJson(entity));
        }
        //如果是调度中的状态，不修改为“待取车”
        if(entity != null && entity.getStatus() != null && OrderStatusEnum.TO_DISPATCH.getStatus() == entity.getStatus().intValue()) {
        	log.info("当前订单状态为调度中的状态，不修改status主订单状态。OrderStatusEntity result=[{}]",GsonUtils.toJson(entity));
        	return getCar;
        }
        
        //以参数的为准。
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
        log.info("getOrderPayableAmount result=[{}],params=[{}]",GsonUtils.toJson(orderPayable),GsonUtils.toJson(orderPaySign));
        //4 抵扣钱包
        boolean isWalletDebtRentCost = false;  //默认值
        
        //全额钱包抵扣
        if(orderPayable.getAmt() == 0) {
        	//成功返回抵扣钱包
        	paySourceUseWallet(orderPaySign,orderPayable,orderPayCallBack);
        	return "ALLWALLETPAY";
        }else {
	        //7 签名串
	        List<PayVo> payVo = getOrderPayVO(orderPaySign,orderPayable,isWalletDebtRentCost);
	        log.info("CashierPayService 加密前费用列表打印 getPaySignStr payVo [{}] ",GsonUtils.toJson(payVo));
	        if(CollectionUtils.isEmpty(payVo)){
	//            throw new OrderPaySignFailException();
	        	//小程序：之前说的是/cashier/getPaySignStr 返回dataStr空，就不走支付接口，我这边没办法判断走不走支付
	        	return "";
	        }
	        //成功返回抵扣钱包
	        paySourceUseWallet(orderPaySign,orderPayable,orderPayCallBack);
	        return cashierNoTService.getPaySignByPayVos(payVo);
        }
    }


    /**
          * 钱包支付的公共方法
     * @param orderPaySign
     * @param orderPayable
     * @param payCallbackService
     */
	private void paySourceUseWallet(OrderPaySignReqVO orderPaySign, OrderPayableAmountResVO orderPayable,OrderPayCallBack payCallbackService) {
    	if(orderPayable == null) {
    		return;
    	}

    	//钱包对象
    	List<PayVo> payVo = getOrderPayVOWalletPay(orderPaySign,orderPayable);
    	if(payVo == null) {
    		return;
    	}

    	if(orderPayable.getAmtWallet01() > 0) {
    		//需要检测实收和资金流水实付的金额。
    		//钱包和支付的累加处理
    		CashierEntity cashier = this.commonWalletDebt(orderPaySign, orderPayable.getAmtWallet01(), DataPayKindConstant.RENT);

    		commonNoticePayCallBack(payCallbackService, payVo, cashier, DataPayKindConstant.RENT);
    	}

    	if(orderPayable.getAmtWallet02() > 0) {
    		//需要检测实收和资金流水实付的金额。
    		//钱包和支付的累加处理
    		CashierEntity cashier = this.commonWalletDebt(orderPaySign, orderPayable.getAmtWallet02(), DataPayKindConstant.DEPOSIT);

    		commonNoticePayCallBack(payCallbackService, payVo, cashier, DataPayKindConstant.DEPOSIT);
    	}
    	if(orderPayable.getAmtWallet03() > 0) {
    		//APP修改订单需要钱包全部抵扣，才触发调用。
    		//需要检测实收和资金流水实付的金额。  ADD加上
    		CashierEntity cashier = this.commonWalletDebt(orderPaySign, orderPayable.getAmtWallet03(), DataPayKindConstant.RENT_INCREMENT);
//    		if(restAmtWallet == 0) {
//    			payCallBack(batchNotifyDataVo,payCallbackService);
//    		}

    		commonNoticePayCallBack(payCallbackService, payVo, cashier, DataPayKindConstant.RENT_INCREMENT);
    	}
    	if(orderPayable.getAmtWallet12() > 0) {
    		CashierEntity cashier = this.commonWalletDebt(orderPaySign, orderPayable.getAmtWallet12(), DataPayKindConstant.RENT_AMOUNT_AFTER);

    		commonNoticePayCallBack(payCallbackService, payVo, cashier, DataPayKindConstant.RENT_AMOUNT_AFTER);
    	}
    	if(orderPayable.getAmtWallet08() > 0) {
    		CashierEntity cashier = this.commonWalletDebt(orderPaySign, orderPayable.getAmtWallet08(), DataPayKindConstant.RENT_INCREMENT_CONSOLE);

    		commonNoticePayCallBack(payCallbackService, payVo, cashier, DataPayKindConstant.RENT_INCREMENT_CONSOLE);
    	}
    	if(orderPayable.getAmtWallet07() > 0) {
    		CashierEntity cashier = this.commonWalletDebt(orderPaySign, orderPayable.getAmtWallet07(), DataPayKindConstant.DEBT);

    		commonNoticePayCallBack(payCallbackService, payVo, cashier, DataPayKindConstant.DEBT);
    	}
    	if(orderPayable.getAmtWallet11() > 0) {
    		CashierEntity cashier = this.commonWalletDebt(orderPaySign, orderPayable.getAmtWallet11(), DataPayKindConstant.RENT_AMOUNT);

    		commonNoticePayCallBack(payCallbackService, payVo, cashier, DataPayKindConstant.RENT_AMOUNT);

    	}
	}

	/**
	 * 公共方法 钱包支付的同步通知(模拟异步发送)
	 * @param payCallbackService
	 * @param payVo
	 * @param cashier
	 * @param payKind
	 */
	public void commonNoticePayCallBack(OrderPayCallBack payCallbackService, List<PayVo> payVo,CashierEntity cashier,String payKind) {
		List<PayVo> filterPayVo = payVo.stream().filter(x -> payKind.equals(x.getPayKind())).collect(Collectors.toList());
		if(CollectionUtils.isEmpty(filterPayVo)) {
			return;
		}

		filterPayVo.stream().forEach(x -> {

			BatchNotifyDataVo batchNotifyDataVo = new BatchNotifyDataVo();
			List<NotifyDataVo> lstVo = new ArrayList<NotifyDataVo>();
			NotifyDataVo vo = new NotifyDataVo();
			BeanUtils.copyProperties(x,vo);
			BeanUtils.copyProperties(cashier,vo);
			//特殊字段赋值
			vo.setExtendParams(x.getExtendParams());
			vo.setSettleAmount(x.getPayAmt());
			vo.setOrderTime(LocalDateTimeUtils.dateToString(new Date()));
			vo.setRespCode("00");
			vo.setRespMsg("SUCCESS");
			vo.setTransStatus("00");
			vo.setTotalFreezeFundAmount(x.getPayAmt());
			vo.setTotalFreezeCreditAmount("0");
			//构造
			vo.setAtappId(DataAppIdConstant.APPID_SHORTRENT);
            String atpayNewTransId = PayTransIdCreator.getPayTransId(vo.getOrderNo(), vo.getAtappId(), vo.getPayKind(), vo.getPayType(), vo.getPaySource(), vo.getPayEnv());
            vo.setAtpayNewTransId(atpayNewTransId);
			//封装
			lstVo.add(vo);
			batchNotifyDataVo.setLstNotifyDataVo(lstVo);
			//调用
			payCallBack(batchNotifyDataVo,payCallbackService);
		});
	}


	/**
     * 获取支付验签数据
     * 下单和车主自动接单同意的时候，刷新钱包抵扣。
     * ADD:拒单进入调度的时候，刷新钱包抵扣。200512 
     * 内部方法
     *
     * @param orderPaySign     支付签名
     * @param orderPayCallBack 回调函数
     */
    public void getPaySignStrNew(OrderPaySignReqVO orderPaySign, OrderPayCallBack orderPayCallBack) {
        //1校验
        Assert.notNull(orderPaySign, ErrorCode.PARAMETER_ERROR.getText());
        orderPaySign.check();
        //3 查询应付
        OrderPayReqVO orderPayReqVO = new OrderPayReqVO();
        BeanUtils.copyProperties(orderPaySign, orderPayReqVO);
        OrderPayableAmountResVO orderPayable = getOrderPayableAmount(orderPayReqVO);
        log.info("getOrderPayableAmount new result=[{}],params=[{}]",GsonUtils.toJson(orderPayable),GsonUtils.toJson(orderPaySign));
        //公共的钱包抵扣方法。
        commonWalletDebt(orderPaySign, orderPayCallBack, orderPayable);
    }

    
    /**
     * 公共方法
     * @param orderPaySign
     * @param orderPayCallBack
     * @param orderPayable
     */
	public void commonWalletDebt(OrderPaySignReqVO orderPaySign, OrderPayCallBack orderPayCallBack,OrderPayableAmountResVO orderPayable) {
		//4 抵扣钱包
        if (YesNoEnum.YES.getCode() == orderPayable.getIsUseWallet()) {
            int payBalance = walletProxyService.getWalletByMemNo(orderPaySign.getMenNo());
            log.info("当前钱包金额payBalance=[{}],params=[{}]",payBalance,GsonUtils.toJson(orderPaySign));
            //判断余额大于0
            if (payBalance > 0) {
                //5 抵扣钱包落库 （收银台落库、费用落库）
                int amtWallet = walletProxyService.orderDeduct(orderPaySign.getMenNo(), orderPaySign.getOrderNo(), orderPayable.getAmtWallet(),RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST.getTxt());
                //6收银台 钱包支付落库
                cashierNoTService.insertRenterCostByWallet(orderPaySign, amtWallet,RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST,DataPayKindConstant.RENT_AMOUNT);
                //钱包未抵扣部分
                int amtPaying = 0;
                if (amtWallet < orderPayable.getAmtWallet()) {
                    amtPaying = amtWallet - orderPayable.getAmtWallet();
                }
                orderPayable.setAmtWallet(amtWallet);
                orderPayable.setAmt(orderPayable.getAmt() + amtPaying);
                orderPayable.setAmtRent(orderPayable.getAmtRent() + amtPaying);
                // 钱包支付完租车费用  租车费用为0 状态变更
                if (orderPayable.getAmtRent() == 0) {
                    cashierService.saveWalletPaylOrderStatusInfo(orderPaySign.getOrderNo());
                }
                
                log.info("当前orderPayable=[{}],params=[{}]",GsonUtils.toJson(orderPayable),GsonUtils.toJson(orderPaySign));
                //如果待支付 金额等于 0 即 钱包抵扣完成
                if (orderPayable.getAmt() == 0) {
                    List<String> payKind = orderPaySign.getPayKind();
                    // 如果 支付款项 只有租车费用一个  并且使用钱包支付 ，当待支付金额完全被 钱包抵扣直接返回支付完成
                    if (!CollectionUtils.isEmpty(payKind) && orderPaySign.getPayKind().contains(DataPayKindConstant.RENT_AMOUNT)) {
                        //修改子订单费用信息
                        String renterOrderNo = getExtendParamsRentOrderNo(orderPayable);
//                        orderPayCallBack.callBack(orderPaySign.getMenNo(), orderPaySign.getOrderNo(), renterOrderNo, orderPayable.getIsPayAgain(), YesNoEnum.NO);
                        orderPayCallBack.callBack(orderPaySign.getMenNo(), orderPaySign.getOrderNo(), renterOrderNo, YesNoEnum.NO.getCode(), YesNoEnum.NO);
                        
                        //公共抵扣企业用户的押金的方法
                        commonDebtEnterpriseDeposit(orderPaySign, orderPayable);
                        
                    }

                }
            }
        }
	}


	/**
	 * 公共方法  钱包支付的抵扣  公共方法
	 * @param orderPaySign
	 * @param needAmtWallet
	 * @return 除开钱包余下金额
	 */
	public CashierEntity commonWalletDebt(OrderPaySignReqVO orderPaySign,int needAmtWallet,String payKind) {
		CashierEntity cashier = null;
		log.info("当前钱包金额commonWalletDebt params orderPaySign=[{}],needAmtWallet=[{}],payKind=[{}]",GsonUtils.toJson(orderPaySign),needAmtWallet,payKind);
        //除开钱包余下金额
		int restAmtWallet = 0;
		int amtWallet = 0;
		//4 抵扣钱包
        int payBalance = walletProxyService.getWalletByMemNo(orderPaySign.getMenNo());
        //判断余额大于0
        if (payBalance > 0) {
        	//根据payKind查找cashCode
        	RenterCashCodeEnum renterCashCodeEnum = getRenterCashCodeNumByPayKind(payKind);
            //5 抵扣钱包落库 （收银台落库、费用落库）
            amtWallet = walletProxyService.orderDeduct(orderPaySign.getMenNo(), orderPaySign.getOrderNo(), needAmtWallet,renterCashCodeEnum.getTxt());
            /**
             * 钱包服务的异常处理，抛出异常处理
             */
            if(amtWallet == 0 || amtWallet != needAmtWallet) {
            	RuntimeException e = new RuntimeException("remote order wallet exception:mem_no="+orderPaySign.getMenNo()+",orderNo="+orderPaySign.getOrderNo()+",amt="+needAmtWallet);
            	throw e;
            }
            
            //6收银台 钱包支付落库
            cashier = cashierNoTService.insertRenterCostByWallet(orderPaySign, amtWallet,renterCashCodeEnum,payKind);
        }
        restAmtWallet = needAmtWallet - amtWallet;
        log.info("当前钱包金额commonWalletDebt params orderPaySign=[{}],needAmtWallet=[{}],payKind=[{}],amtWallet=[{}],restAmtWallet=[{}]",GsonUtils.toJson(orderPaySign),needAmtWallet,payKind,amtWallet,restAmtWallet);


        return cashier;
	}

	/**
	 * 类型转换
	 * @param payKind
	 * @return
	 */
	private RenterCashCodeEnum getRenterCashCodeNumByPayKind(String payKind) {
		if(DataPayKindConstant.RENT.equals(payKind)) {
			return RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST;
		}else if(DataPayKindConstant.DEPOSIT.equals(payKind)) {
			return RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT	;
		}else if(DataPayKindConstant.RENT_AMOUNT.equals(payKind)) {
			return RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST	;
		}else if(DataPayKindConstant.RENT_INCREMENT.equals(payKind)) {
			return RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN	;
		}else if(DataPayKindConstant.RENT_AMOUNT_AFTER.equals(payKind)) {
			return RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AFTER	;
		}else if(DataPayKindConstant.RENT_INCREMENT_CONSOLE.equals(payKind)) {
			return RenterCashCodeEnum.ACCOUNT_RENTER_SUPPLEMENT_COST_AGAIN	;
		}else if(DataPayKindConstant.DEBT.equals(payKind)) {
			return RenterCashCodeEnum.ACCOUNT_RENTER_DEBT_COST_AGAIN		;
		}else {
			//默认值
			return RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST;
		}
	}


	public void getPaySignStrNewOfflinePay(String orderNo,String memNo,boolean isEnterpriseUserOrder) {
		//构造
		OrderPaySignReqVO orderPaySign = this.buildOrderPaySignReqVOOfflinePay(orderNo,memNo);
		
        //1校验
        Assert.notNull(orderPaySign, ErrorCode.PARAMETER_ERROR.getText());
        orderPaySign.check();
        //3 查询应付
        OrderPayReqVO orderPayReqVO = new OrderPayReqVO();
        BeanUtils.copyProperties(orderPaySign, orderPayReqVO);
        OrderPayableAmountResVO orderPayable = getOrderPayableAmount(orderPayReqVO,isEnterpriseUserOrder);
        log.info("(offlinePay)getOrderPayableAmount new result=[{}],params=[{}]",GsonUtils.toJson(orderPayable),GsonUtils.toJson(orderPaySign));
        
        //刷新0押金的抵扣
        commonDebtEnterpriseDeposit(orderPaySign, orderPayable);
    }
	
	/**
	 * 公共抵扣企业用户的押金的方法
	 * 线下支付或者虚拟支付，需要调用该方法。
	 * @param orderPaySign
	 * @param orderPayable
	 */
	private void commonDebtEnterpriseDeposit(OrderPaySignReqVO orderPaySign, OrderPayableAmountResVO orderPayable) {
		boolean isWalletDebtRentCost;
		//扩展EXT>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		isWalletDebtRentCost = true;
		//目前限定只有企业用户的时候，才继续通过押金为0的支付。否则条件：必须支付第一笔。或第一笔附带两笔押金为0的支付。经过支付平台。
		//APPserver第一道拦截
		//该处第二道拦截。
		//在拉取押金金额的时候，根据企业用户标识来拉取。
		//避免押金为0的支付，绕过支付平台。
		if(orderPayable.getAccountPayAbles() != null && orderPayable.getAccountPayAbles().size() > 1 
			   && orderPayable.isEnterpriseUserOrder()) { //含多笔的情况。
		   //押金继续支付，含押金为0的情况。
		   log.info("押金继续支付，含押金为0的情况,params=[{}]",GsonUtils.toJson(orderPayable));
		   
		   
		   //7 签名串
		   List<PayVo> payVo = getOrderPayVO(orderPaySign,orderPayable,isWalletDebtRentCost);
		   log.info("CashierPayService 加密前费用列表打印 getPaySignStr payVo [{}] ",GsonUtils.toJson(payVo));
		   if(CollectionUtils.isEmpty(payVo)){
		       throw new OrderPaySignFailException();
		   }
		   String desString = cashierNoTService.getPaySignByPayVos(payVo);
		   //非空
		   if(org.apache.commons.lang3.StringUtils.isNotBlank(desString)){
		       //模拟发起支付 平台
		       PayCipherTextVo vo = new PayCipherTextVo();
				vo.setCipherText(desString);
				vo.setInternalNo(String.valueOf("0"));
				vo.setOpenId("");
//				vo.setPaySource(orderPaySign.getPaySource());
				vo.setPaySource("00");  //00 默认是钱包。
				vo.setReqOs(orderPaySign.getReqOs());  //需要处理
				
				
				log.info("tn远程获取支付信息params=[{}],newOrderPayGatewayURL=[{}]", GsonUtils.toJson(vo),newOrderPayGatewayURL + "/public/paygw/routingrules/payBatch");
				String json = restTemplate.postForObject(newOrderPayGatewayURL + "/public/paygw/routingrules/payBatch", vo, String.class);
				log.info("tn远程获取支付信息json=[{}]", json);
		   }else {
			   log.info("desString签名串为空，无法发起调用支付平台。params=[{}]",GsonUtils.toJson(payVo));
		   }
		   
		}
	}
	
	/**
	 * 兼容默认方法。
	 * 方法重载。
	 * @param orderPayReqVO
	 * @return
	 */
	public OrderPayableAmountResVO getOrderPayableAmount(OrderPayReqVO orderPayReqVO){
		return getOrderPayableAmount(orderPayReqVO, false);
	}
    /**
     * 查询支付款项信息
     */
	/**
	 * 
	 * @param orderPayReqVO
	 * @param isEnterpriseUserOrder  默认false，兼容线下支付兼容刷新双押金为0的情况。
	 * @return
	 */
    public OrderPayableAmountResVO getOrderPayableAmount(OrderPayReqVO orderPayReqVO,boolean isEnterpriseUserOrder){
    	OrderPayableAmountResVO result = new OrderPayableAmountResVO();
        Assert.notNull(orderPayReqVO, ErrorCode.PARAMETER_ERROR.getText());
        orderPayReqVO.check();
        //1 查询子单号
        RenterOrderEntity renterOrderEntity = null;
        //是否企业用户
//        isEnterpriseUserOrder = false;  //默认false,从外部传入。
        //查询子订单有关：进行中的子订单，其他的无关(针对结算后的补付功能。)。200410
        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_AMOUNT)){
	        renterOrderEntity = cashierNoTService.getRenterOrderNoByOrderNo(orderPayReqVO.getOrderNo());
	        if(StringUtils.isBlank(renterOrderEntity.getRenterOrderNo())) {
	        	 throw new OrderNotFoundException(orderPayReqVO.getOrderNo()+"支付子订单号");
	        }
	        result.setRenterOrderNo(renterOrderEntity.getRenterOrderNo());
	        isEnterpriseUserOrder = renterMemberService.isEnterpriseUserOrder(renterOrderEntity.getRenterOrderNo());
        }else if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_AMOUNT_AFTER)) {
        	//包括租客子订单状态3，4
        	renterOrderEntity = cashierNoTService.getRenterOrderNoByOrderNoAfter(orderPayReqVO.getOrderNo());
	        if(StringUtils.isBlank(renterOrderEntity.getRenterOrderNo())) {
	        	 throw new OrderNotFoundException(orderPayReqVO.getOrderNo()+"支付子订单号");
	        }
	        result.setRenterOrderNo(renterOrderEntity.getRenterOrderNo());
	        isEnterpriseUserOrder = renterMemberService.isEnterpriseUserOrder(renterOrderEntity.getRenterOrderNo());
        }else if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_INCREMENT)) {
        	//未支付不是有效订单
        	renterOrderEntity = cashierNoTService.getRenterOrderNoByOrderNoIncrement(orderPayReqVO.getOrderNo());
	        if(StringUtils.isBlank(renterOrderEntity.getRenterOrderNo())) {
	        	 throw new OrderNotFoundException(orderPayReqVO.getOrderNo()+"支付子订单号");
	        }
	        result.setRenterOrderNo(renterOrderEntity.getRenterOrderNo());
	        isEnterpriseUserOrder = renterMemberService.isEnterpriseUserOrder(renterOrderEntity.getRenterOrderNo());
        }
        //企业用户标识
        result.setEnterpriseUserOrder(isEnterpriseUserOrder);
        //扩展paykind,容错处理
        if(isEnterpriseUserOrder) {
        	if(!orderPayReqVO.getPayKind().contains(DataPayKindConstant.DEPOSIT)) {
        		orderPayReqVO.getPayKind().add(DataPayKindConstant.DEPOSIT);
        	}
        	if(!orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT)) {
        		orderPayReqVO.getPayKind().add(DataPayKindConstant.RENT);
        	}
        }
        
        // 判断是否支持 钱包支付 、页面传入是否使用钱包标记 优先
        Integer isUseWallet = 0;
        //补付不能使用钱包
//        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_AMOUNT_AFTER) || orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_INCREMENT_CONSOLE) || orderPayReqVO.getPayKind().contains(DataPayKindConstant.DEBT)) {
        //只有租车费用可以使用钱包。ONLY LIMIT
        //需求扩展：钱包支付方式。 全部放开。
//        if(!orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_AMOUNT)) {
//        	isUseWallet = 0;
//        }else {
        	//renterOrderEntity 如果该对象为空，则上面已经返回。


        	//兼容之前的处理。
        	if(CollectionUtils.isEmpty(orderPayReqVO.getPaySource())) {
            	if(renterOrderEntity != null) {
            		isUseWallet = Objects.isNull(orderPayReqVO.getIsUseWallet())?renterOrderEntity.getIsUseWallet():orderPayReqVO.getIsUseWallet();
            	}else {
            		isUseWallet = Objects.isNull(orderPayReqVO.getIsUseWallet())?0:orderPayReqVO.getIsUseWallet(); //默认否
            	}
        	}else {
	        	String paySourceConvert = orderPayReqVO.getPaySource().stream().filter(x -> "00".equals(x)).findFirst().orElse("");  //默认值
	        	if("00".equals(paySourceConvert)) {
	        		isUseWallet = 1;
	        	}
        	}
        	
//        	if(isEnterpriseUserOrder==false) {
//	        	//如果已经使用过钱包抵扣，不允许再次做抵扣。
//	        	int walletAmt = accountRenterCostDetailNoTService.getRentCostPayByWallet(orderPayReqVO.getOrderNo(), orderPayReqVO.getMenNo());
//	        	if(walletAmt > 0) {
//	        		isUseWallet = 0;
//	        		log.info("当前订单已经使用过钱包抵扣，无需再次抵扣。orderNo=[{}],walletAmt=[{}]",orderPayReqVO.getOrderNo(),walletAmt);
//	        	}
//        	}else {
//        		isUseWallet = 1;
//        		log.info("当前订单企业用户钱包允许多次抵扣。orderNo=[{}]",orderPayReqVO.getOrderNo());
//        	}
        	/**
        	 * 企业用户默认按钱包走。
        	 */
        	if(isEnterpriseUserOrder) {
        		isUseWallet = 1;
        		log.info("当前订单企业用户钱包允许多次抵扣。orderNo=[{}]",orderPayReqVO.getOrderNo());
        	}
        	


//        }
        result.setIsUseWallet(isUseWallet);
        
        int amtWallet01 =0;
        int amtWallet02 =0;
        int amtWallet11 =0;
        int amtWallet03 =0;
        int amtWallet12 =0;
        int amtWallet08 =0;
        int amtWallet07 =0;
        //查询钱包余额外置
        int payBalance = walletProxyService.getWalletByMemNo(orderPayReqVO.getMenNo());
        //查询当前的订单状态
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderPayReqVO.getOrderNo());
        //未结算标记
        boolean noSettleFlag = (orderStatusEntity != null && orderStatusEntity.getRentCarPayStatus() == 0 && orderStatusEntity.getSettleStatus() == 0);

        //待支付金额明细
        List<AccountPayAbleResVO> accountPayAbles = new ArrayList<>();
        //车辆押金 是否选择车辆押金
        int amtDeposit = 0;
        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT)){
            amtDeposit = cashierService.getRenterDeposit(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo());
            //兼容企业用户的情况，押金为0的情况。200421
           if(isEnterpriseUserOrder) {
        	   if(amtDeposit <= 0){
                   accountPayAbles.add(new AccountPayAbleResVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo(),amtDeposit, RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT,RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT.getTxt()));
               }   
           }else {
        	   //不能为0
        	   if(amtDeposit < 0){
                   accountPayAbles.add(new AccountPayAbleResVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo(),amtDeposit, RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT,RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT.getTxt()));
               }
           }
           
         //钱包抵扣
           if(YesNoEnum.YES.getCode()==result.getIsUseWallet()){
//               int payBalance = walletProxyService.getWalletByMemNo(orderPayReqVO.getMenNo());
               int realAmtWallet = amtWallet01 + amtWallet02 + amtWallet11 + amtWallet03 + amtWallet12 + amtWallet08 + amtWallet07;
               if(payBalance - realAmtWallet > 0) {
               	payBalance = (payBalance - realAmtWallet);
   	            //预计钱包抵扣金额 = amtWallet
   	            amtWallet01 = amtDeposit + payBalance < 0 ? payBalance : Math.abs(amtDeposit);
   	            // 抵扣钱包后  应付租车费用金额
   	            amtDeposit =  amtDeposit + amtWallet01;
               }
//               accountPayAbles.add(new AccountPayAbleResVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo(),amtWallet01,RenterCashCodeEnum.ACCOUNT_WALLET_COST,RenterCashCodeEnum.ACCOUNT_WALLET_COST.getTxt()));
           }
        }



        //违章押金 是否选择违章押金
        int amtWZDeposit = 0;
        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.DEPOSIT)){
            amtWZDeposit = cashierService.getRenterWZDeposit(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo());
            ////兼容企业用户的情况，押金为0的情况。200421
            if(isEnterpriseUserOrder) {
	            if(amtWZDeposit <= 0){
	                accountPayAbles.add(new AccountPayAbleResVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo(),amtWZDeposit, RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT,RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT.getTxt()));
	            }
            }else {
            	if(amtWZDeposit < 0){
	                accountPayAbles.add(new AccountPayAbleResVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo(),amtWZDeposit, RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT,RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT.getTxt()));
	            }
            }

          //钱包抵扣
            if(YesNoEnum.YES.getCode()==result.getIsUseWallet()){
//                int payBalance = walletProxyService.getWalletByMemNo(orderPayReqVO.getMenNo());
                //预计钱包抵扣金额 = amtWallet
                int realAmtWallet = amtWallet01 + amtWallet02 + amtWallet11 + amtWallet03 + amtWallet12 + amtWallet08 + amtWallet07;
                if(payBalance - realAmtWallet > 0) {
                	payBalance = (payBalance - realAmtWallet);
    	            amtWallet02 = amtWZDeposit + payBalance < 0 ? payBalance : Math.abs(amtWZDeposit);
    	            amtWZDeposit =  amtWZDeposit + amtWallet02;
                }
            }
        }



        
        //已付租车费用(shifu  租车费用的实付)
        //放在外面，对结果产生了影响。需要内置。
//        rentAmtPayed = accountRenterCostSettleService.getCostPaidRent(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo());
        
        //应付租车费用
        int rentAmt = 0;
        //已付租车费用
        int rentAmtPayed = 0;
      //实际待支付租车费用总额 即真实应付租车费用
        int amtRent = 0;
        int outsideAmt = 0;

        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_AMOUNT)){  //修改订单的补付
//            List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableGlobalVO(orderPayReqVO.getOrderNo(),renterOrderEntity.getRenterOrderNo(),orderPayReqVO.getMenNo());
        	//考虑到支付租车费用，不考虑管理后台补贴的方式。
        	//会导致租客支付第一笔租车费用为0的情况。管理后台补贴在结算的时候处理。200409 
        	List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableIncrementVO(orderPayReqVO.getOrderNo(),renterOrderEntity.getRenterOrderNo(),orderPayReqVO.getMenNo());

            //应付租车费用（已经求和）
            rentAmt = cashierNoTService.sumRentOrderCost(payableVOs);
            
            //已付租车费用(shifu  租车费用的实付)
            //该情况只会有一种情况：钱包 shifu
            rentAmtPayed = accountRenterCostSettleService.getCostPaidRent(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo());

            if(noSettleFlag) {
            	//外部来金(无需支付的管理后台补付记录)
            	List<PayableVO> payableVOsOutSide = renterOrderCostCombineService.listPayableSupplementVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo());
            	outsideAmt = cashierNoTService.sumRentOrderCost(payableVOsOutSide);
            }

            if(!CollectionUtils.isEmpty(payableVOs) && (rentAmt+rentAmtPayed+outsideAmt) < 0){   //
                if(result.getPayableVOs() != null) {
                	result.getPayableVOs().addAll(payableVOs);
                }else {
                	result.setPayableVOs(payableVOs);
                }

                for(int i=0;i<payableVOs.size();i++){
                    PayableVO payableVO = payableVOs.get(i);
                    //判断是租车费用、还是补付 租车费用 并记录 详情
//                    RenterCashCodeEnum type = rentAmtPayed>0?RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN:RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST;
                    RenterCashCodeEnum type = RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST;
//                    if(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN.equals(type)){
//                        result.setIsPayAgain(YesNoEnum.YES.getCode());
//                    }
                    result.setIsPayAgain(YesNoEnum.NO.getCode());
                    //抵扣掉钱包的部分，下单的时候抵扣钱包金额。钱包部分抵扣的情况。
                    accountPayAbles.add(new AccountPayAbleResVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo(),(payableVO.getAmt()+rentAmtPayed+outsideAmt),type,payableVO.getTitle()));
                }

                amtRent = rentAmt + rentAmtPayed + outsideAmt;
            }




         // 计算钱包 支付 目前支付抵扣租费费用
            //租车费用
            if(YesNoEnum.YES.getCode()==result.getIsUseWallet()){
//                int payBalance = walletProxyService.getWalletByMemNo(orderPayReqVO.getMenNo());
                //预计钱包抵扣金额 = amtWallet
                int realAmtWallet = amtWallet01 + amtWallet02 + amtWallet11 + amtWallet03 + amtWallet12 + amtWallet08 + amtWallet07;
                if(payBalance - realAmtWallet > 0) {
                	payBalance = (payBalance - realAmtWallet);
    	            amtWallet11 = amtRent + payBalance < 0 ? payBalance : Math.abs(amtRent);
    	            // 抵扣钱包后  应付租车费用金额
    	            amtRent =  amtRent + amtWallet11;
    	            accountPayAbles.add(new AccountPayAbleResVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo(),amtWallet11,RenterCashCodeEnum.ACCOUNT_WALLET_COST,RenterCashCodeEnum.ACCOUNT_WALLET_COST.getTxt()));
                }
            }
        }


        
        //APP修改订单补付
        int rentIncrementAmt = 0;
        int rentAmtPayedIncrement = 0;
        int amtIncrementRent = 0;
        int outsideAmtIncrement = 0;

        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_INCREMENT)){  //修改订单的补付
            List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableIncrementVO(orderPayReqVO.getOrderNo(),renterOrderEntity.getRenterOrderNo(),orderPayReqVO.getMenNo());
            
            //应付租车费用
            rentIncrementAmt = cashierNoTService.sumRentOrderCost(payableVOs);
            // 平台给租客的补贴总额
    		int platformToRenterAmt = orderConsoleSubsidyDetailService.getPlatformToRenterSubsidyAmt(orderPayReqVO.getOrderNo(), orderPayReqVO.getMenNo());
    		rentIncrementAmt = rentIncrementAmt + platformToRenterAmt;
            //已付租车费用(shifu  租车费用的实付)
            rentAmtPayedIncrement = accountRenterCostSettleService.getCostPaidRent(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo());
            if(noSettleFlag) {
            	//外部来金(无需支付的管理后台补付记录)
            	List<PayableVO> payableVOsOutSide = renterOrderCostCombineService.listPayableSupplementVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo());
            	outsideAmtIncrement = cashierNoTService.sumRentOrderCost(payableVOsOutSide);
            }

            if(!CollectionUtils.isEmpty(payableVOs) && (platformToRenterAmt+rentIncrementAmt+rentAmtPayedIncrement+outsideAmtIncrement) < 0){   // +rentAmtPayed  （加上平台补贴）
//              result.setPayableVOs(payableVOs);
                if(result.getPayableVOs() != null) {
                	result.getPayableVOs().addAll(payableVOs);
                }else {
                	result.setPayableVOs(payableVOs);
                }

                for(int i=0;i<payableVOs.size();i++){
                    PayableVO payableVO = payableVOs.get(i);
                    //判断是租车费用、还是补付 租车费用 并记录 详情
                    RenterCashCodeEnum type = RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN;
                    if(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN.equals(type)){
                        result.setIsPayAgain(YesNoEnum.YES.getCode());
                    }
                    accountPayAbles.add(new AccountPayAbleResVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo(),(platformToRenterAmt+payableVO.getAmt()+rentAmtPayedIncrement+outsideAmtIncrement),type,payableVO.getTitle()));
                }

              //补付修改订单（加上平台补贴）
                amtIncrementRent = platformToRenterAmt + rentIncrementAmt + rentAmtPayedIncrement + outsideAmtIncrement;
            }
            

         //钱包抵扣
           if(YesNoEnum.YES.getCode()==result.getIsUseWallet()){
//               int payBalance = walletProxyService.getWalletByMemNo(orderPayReqVO.getMenNo());
               //预计钱包抵扣金额 = amtWallet
               int realAmtWallet = amtWallet01 + amtWallet02 + amtWallet11 + amtWallet03 + amtWallet12 + amtWallet08 + amtWallet07;
               if(payBalance - realAmtWallet > 0) {
               	payBalance = (payBalance - realAmtWallet);
   	            amtWallet03 = amtIncrementRent + payBalance < 0 ? payBalance : Math.abs(amtIncrementRent);
   	            amtIncrementRent =  amtIncrementRent + amtWallet03;
               }
           }
        }




        //---------------------------------------------------------------------------------------- 与RENT_AMOUNT分离 ---------------------------------- 三大块
        ///费用补付 
        int rentAmtAfter = 0;
        int rentAmtPayedAfter = 0;
        int amtRentAfter = 0;
        int outsideAmtAfter = 0;
        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_AMOUNT_AFTER)){  //管理后台修改订单的补付
//            List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableGlobalVO(orderPayReqVO.getOrderNo(),renterOrderEntity.getRenterOrderNo(),orderPayReqVO.getMenNo());
            List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableIncrementVO(orderPayReqVO.getOrderNo(),renterOrderEntity.getRenterOrderNo(),orderPayReqVO.getMenNo());

            //应付租车费用（已经求和）
            rentAmtAfter = cashierNoTService.sumRentOrderCost(payableVOs);

            //已付租车费用(shifu  租车费用的实付)
            rentAmtPayedAfter = accountRenterCostSettleService.getCostPaidRent(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo());

            if(noSettleFlag) {
            	//外部来金(无需支付的管理后台补付记录)
            	List<PayableVO> payableVOsOutSide = renterOrderCostCombineService.listPayableSupplementVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo());
            	outsideAmtAfter = cashierNoTService.sumRentOrderCost(payableVOsOutSide);
            }

            if(!CollectionUtils.isEmpty(payableVOs) && (rentAmtAfter+rentAmtPayedAfter+outsideAmtAfter) < 0){   //


//              result.setPayableVOs(payableVOs);
              if(result.getPayableVOs() != null) {
              	result.getPayableVOs().addAll(payableVOs);
              }else {
              	result.setPayableVOs(payableVOs);
              }

                for(int i=0;i<payableVOs.size();i++){
                    PayableVO payableVO = payableVOs.get(i);
                    //判断是租车费用、还是补付 租车费用 并记录 详情
//                    RenterCashCodeEnum type = rentAmtPayed>0?RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN:RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST;
                    RenterCashCodeEnum type = RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AFTER;
                    result.setIsPayAgain(YesNoEnum.NO.getCode());
                    accountPayAbles.add(new AccountPayAbleResVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo(),(payableVO.getAmt()+rentAmtPayedAfter+outsideAmtAfter),type,payableVO.getTitle(),payableVO.getUniqueNo()));
                }

              //在方法体内统计
                amtRentAfter = rentAmtAfter + rentAmtPayedAfter + outsideAmtAfter;
            }



          //钱包抵扣
            if(YesNoEnum.YES.getCode()==result.getIsUseWallet()){
//                int payBalance = walletProxyService.getWalletByMemNo(orderPayReqVO.getMenNo());
                //预计钱包抵扣金额 = amtWallet
                int realAmtWallet = amtWallet01 + amtWallet02 + amtWallet11 + amtWallet03 + amtWallet12 + amtWallet08 + amtWallet07;
                if(payBalance - realAmtWallet > 0) {
                	payBalance = (payBalance - realAmtWallet);
    	            amtWallet12 = amtRentAfter + payBalance < 0 ? payBalance : Math.abs(amtRentAfter);
    	            amtRentAfter =  amtRentAfter + amtWallet12;
                }
            }
        }


        
        //管理后台补付，等于管理后台的补付   08  order_supplement_detail
        int rentIncrementSupplementAmt = 0;
        int rentAmtPayedSupplement = 0;
        int amtRentIncrementSupplement = 0;
        int outsideAmtSupplement = 0;
        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_INCREMENT_CONSOLE)){  //修改订单的补付
        	List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableSupplementVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo());

            //应付租车费用,保存为负数
            rentIncrementSupplementAmt = cashierNoTService.sumRentOrderCost(payableVOs);
            //已付租车费用(shifu  租车费用的实付)
            //仅仅未结算的时候才处理。租车押金结算后，只收不退，跟实收无关。200819
            if(noSettleFlag) {
            	//外部来金(动态记录)
            	List<PayableVO> payableVOsOutSide = renterOrderCostCombineService.listPayableIncrementVO(orderPayReqVO.getOrderNo(),renterOrderEntity.getRenterOrderNo(),orderPayReqVO.getMenNo());
            	outsideAmtSupplement = cashierNoTService.sumRentOrderCost(payableVOsOutSide);

            	rentAmtPayedSupplement = accountRenterCostSettleService.getCostPaidRent(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo());
            }

            if(!CollectionUtils.isEmpty(payableVOs) && (rentIncrementSupplementAmt + rentAmtPayedSupplement + outsideAmtSupplement) < 0){    //大于0的情况就不考虑了。兼容负数的情况。
//              result.setPayableVOs(payableVOs);
            	if(result.getPayableVOs() != null) {
                	result.getPayableVOs().addAll(payableVOs);
                }else {
                	result.setPayableVOs(payableVOs);
                }

                for(int i=0;i<payableVOs.size();i++){
                    PayableVO payableVO = payableVOs.get(i);
                    //判断是租车费用、还是补付 租车费用 并记录 详情
                    RenterCashCodeEnum type = RenterCashCodeEnum.ACCOUNT_RENTER_SUPPLEMENT_COST_AGAIN;
//                    if(RenterCashCodeEnum.ACCOUNT_RENTER_SUPPLEMENT_COST_AGAIN.equals(type)){
                        result.setIsPayAgain(YesNoEnum.NO.getCode());
//                    }
                    accountPayAbles.add(new AccountPayAbleResVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo(),(payableVO.getAmt()+rentAmtPayedSupplement+outsideAmtSupplement),type,payableVO.getTitle(),payableVO.getUniqueNo()));
                }

                amtRentIncrementSupplement = rentIncrementSupplementAmt + rentAmtPayedSupplement + outsideAmtSupplement;
            }


            //钱包抵扣
            if(YesNoEnum.YES.getCode()==result.getIsUseWallet()){
//                int payBalance = walletProxyService.getWalletByMemNo(orderPayReqVO.getMenNo());
                //预计钱包抵扣金额 = amtWallet
                int realAmtWallet = amtWallet01 + amtWallet02 + amtWallet11 + amtWallet03 + amtWallet12 + amtWallet08 + amtWallet07;
                if(payBalance - realAmtWallet > 0) {
                	payBalance = (payBalance - realAmtWallet);
    	            amtWallet08 = amtRentIncrementSupplement + payBalance < 0 ? payBalance : Math.abs(amtRentIncrementSupplement);
    	            amtRentIncrementSupplement =  amtRentIncrementSupplement + amtWallet08;
                }
            }
        }




        //支付欠款 
        int rentIncrementDebtAmt = 0;
        int amtRentIncrementDebt = 0;
        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.DEBT)){  //修改订单的补付
            List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableDebtPayVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo());

            //应付租车费用（已经求和）
            rentIncrementDebtAmt = cashierNoTService.sumRentOrderCost(payableVOs);

            //已付租车费用(shifu  租车费用的实付)
            if(!CollectionUtils.isEmpty(payableVOs) && rentIncrementDebtAmt < 0){
//              result.setPayableVOs(payableVOs);
                if(result.getPayableVOs() != null) {
                	result.getPayableVOs().addAll(payableVOs);
                }else {
                	result.setPayableVOs(payableVOs);
                }

                for(int i=0;i<payableVOs.size();i++){
                    PayableVO payableVO = payableVOs.get(i);
                    //判断是租车费用、还是补付 租车费用 并记录 详情
                    RenterCashCodeEnum type = RenterCashCodeEnum.ACCOUNT_RENTER_DEBT_COST_AGAIN;
                    result.setIsPayAgain(YesNoEnum.NO.getCode());
                    accountPayAbles.add(new AccountPayAbleResVO(orderPayReqVO.getOrderNo(),orderPayReqVO.getMenNo(),payableVO.getAmt(),type,payableVO.getTitle(),payableVO.getUniqueNo()));
                }

                amtRentIncrementDebt = rentIncrementDebtAmt;
            }


          //钱包抵扣
            if(YesNoEnum.YES.getCode()==result.getIsUseWallet()){
//                int payBalance = walletProxyService.getWalletByMemNo(orderPayReqVO.getMenNo());
                //预计钱包抵扣金额 = amtWallet
                int realAmtWallet = amtWallet01 + amtWallet02 + amtWallet11 + amtWallet03 + amtWallet12 + amtWallet08 + amtWallet07;
                if(payBalance - realAmtWallet > 0) {
                	payBalance = (payBalance - realAmtWallet);
    	            amtWallet07 = amtRentIncrementDebt + payBalance < 0 ? payBalance : Math.abs(amtRentIncrementDebt);
    	            amtRentIncrementDebt =  amtRentIncrementDebt + amtWallet07;
                }
            }
        }
        

        //管理后台补付
        //支付欠款
        //待支付总额
        int amtTotal = amtDeposit + amtWZDeposit + amtRent + amtRentAfter + amtIncrementRent + amtRentIncrementSupplement + amtRentIncrementDebt;
        

        //钱包抵扣封装
        result.setAmtWallet11(amtWallet11);
        result.setAmtWallet01(amtWallet01);
        result.setAmtWallet02(amtWallet02);
        result.setAmtWallet03(amtWallet03);
        result.setAmtWallet12(amtWallet12);
        result.setAmtWallet08(amtWallet08);
        result.setAmtWallet07(amtWallet07);

        //7个费用项目
        result.setAmtRent(amtRent);
        //补充
        result.setAmtRentAfter(amtRentAfter);
        result.setAmtIncrementRent(amtIncrementRent);
        ///add 管理后台补付，支付欠款 200311
        result.setAmtIncrementRentSupplement(rentIncrementSupplementAmt);
        result.setAmtIncrementRentDebt(rentIncrementDebtAmt);
        
        result.setAmtDeposit(amtDeposit);
        result.setAmtWzDeposit(amtWZDeposit);

//        result.setAmtTotal(amtTotal);  //amt字段
        //已经支付的金额
        result.setAmtPay(rentAmtPayed);
        result.setAmt(amtTotal);  //result.getAmt()取值。 APPSERVER取值
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
    public void handCopywriting(OrderPayableAmountResVO result,OrderPayReqVO orderPayReqVO) {
        result.setButtonName("去支付");
        RenterOrderEntity renterOrderEntity = cashierNoTService.getRenterOrderNoByOrderNo(orderPayReqVO.getOrderNo());
        // 租车费用倒计时 单位秒
        long countdown=0L;
//        if(Objects.nonNull(renterOrderEntity) && Objects.nonNull(renterOrderEntity.getCreateTime())){
//            countdown = DateUtils.getDateLatterCompareNowScoend(renterOrderEntity.getCreateTime(),1);
//        }
        String costText ="";
        
        int carDepositAmt = Math.abs(result.getAmtDeposit());
        int wzDepositAmt = Math.abs(result.getAmtWzDeposit());
        int totalDepositAmt = carDepositAmt + wzDepositAmt;
        //租车费用
        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_AMOUNT) || orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_INCREMENT)){
            costText =costText+"租车费用"+ Math.abs(result.getAmtRent()+result.getAmtIncrementRent());
            result.setHints("请于1小时内完成支付，超时未支付订单将自动取消");
            
            //同老订单的租车押金
            LocalDateTime reqTime = null;
            /**
             * # 2020-03-27 14:30:52   下单
				# 2020-03-27 14:29:55   当前时间
             */
//            if(Objects.nonNull(renterOrderEntity) && Objects.nonNull(renterOrderEntity.getCreateTime())){
//	            reqTime = renterOrderEntity.getCreateTime();
//	            log.error("renterOrderEntity查询到记录,orderNo=[{}],reqTime=[{}]",orderPayReqVO.getOrderNo(),reqTime);
//            	
//            }else {
            	OrderEntity orderEntity = orderService.getOrderEntity(orderPayReqVO.getOrderNo());
            	if(Objects.nonNull(orderEntity) && Objects.nonNull(orderEntity.getReqTime())){
	            	reqTime = orderEntity.getReqTime();
	            	log.error("orderEntity查询到记录,orderNo=[{}],reqTime=[{}]",orderPayReqVO.getOrderNo(),reqTime);
            	}else {
            		log.error("orderEntity未查询到记录,orderNo=[{}]",orderPayReqVO.getOrderNo());
            	}
//            }
            
            if(reqTime != null) {
	            LocalDateTime reqTimeNext = reqTime.plusHours(1);  //1小时为截止时间
	            
	            log.info("reqTime=" + reqTime);
	            log.info("reqTimeNext=" + reqTimeNext);
	            log.info("now="+LocalDateTime.now());
	            
	            //这样才可以倒计时，时间越来越少。
	            long secondRent = ChronoUnit.SECONDS.between(LocalDateTime.now(),reqTimeNext);
	            log.info("secondRent=" + secondRent);
	            
	            if (0 <= secondRent && secondRent <= 60 * 60) {//小于等于1h
	                countdown = secondRent;
	            }
            }
            
            result.setCountdown(countdown);
          
        }
        
        //车辆押金，单笔支付必须金额大于0
        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT) && carDepositAmt > 0){
            costText =costText+"车辆押金"+ Math.abs(result.getAmtDeposit());
            result.setHints("交易结束后24小时内，车辆押金将返还到支付账户");
            
            //同老订单的违章押金
            if(Objects.nonNull(renterOrderEntity) && Objects.nonNull(renterOrderEntity.getExpRentTime())){
            	LocalDateTime rentTime = renterOrderEntity.getExpRentTime(); 
                long secondRent = ChronoUnit.SECONDS.between(LocalDateTime.now(), rentTime);
                if (0 <= secondRent && secondRent <= 60 * 60) {//小于等于1h
                    countdown = secondRent;
                }
            }
            result.setCountdown(countdown);
        }
        
        //违章押金，单笔支付必须金额大于0
        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.DEPOSIT) && wzDepositAmt > 0){
            costText =costText+" 违章押金"+Math.abs(result.getAmtWzDeposit());
            result.setHints("交易结束后24小时内，车辆押金将返还到支付账户");
            
            //同老订单的违章押金
            if(Objects.nonNull(renterOrderEntity) && Objects.nonNull(renterOrderEntity.getExpRentTime())){
            	LocalDateTime rentTime = renterOrderEntity.getExpRentTime(); 
                long secondRent = ChronoUnit.SECONDS.between(LocalDateTime.now(), rentTime);
                if (0 <= secondRent && secondRent <= 60 * 60) {//小于等于1h
                    countdown = secondRent;
                }
            }
            result.setCountdown(countdown);
            
        }
        
        //合并
        //车辆押金 + 违章押金
        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT) && orderPayReqVO.getPayKind().contains(DataPayKindConstant.DEPOSIT) && totalDepositAmt > 0){
            costText ="车辆押金"+ Math.abs(result.getAmtDeposit()) + " + " + " 违章押金"+Math.abs(result.getAmtWzDeposit());
            result.setHints("交易结束后24小时内，车辆押金将返还到支付账户");
        }
        
        //车辆押金 + 违章押金 + 租车费用 一起支付
        //合并支付(企业用户)
        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT) && totalDepositAmt > 0 &&
                (orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_AMOUNT) && orderPayReqVO.getPayKind().contains(DataPayKindConstant.DEPOSIT))
            ){
                costText ="租车费用"+ Math.abs(result.getAmtRent()+result.getAmtIncrementRent()) + " + " + " 押金" + totalDepositAmt;
                result.setHints("交易结束后24小时内，押金将返还到支付账户");
                result.setCountdown(countdown);
            }
        
        //租车费用和车辆押金
        if(orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT) && carDepositAmt > 0 &&
            (orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_AMOUNT) || orderPayReqVO.getPayKind().contains(DataPayKindConstant.RENT_INCREMENT))
        ){
            costText ="租车费用"+ Math.abs(result.getAmtRent()+result.getAmtIncrementRent()) + " + " + " 车辆押金" + carDepositAmt;
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
        RenterOrderEntity renterOrderEntity = cashierNoTService.getRenterOrderNoByOrderNoAndFinish(orderNo);

        if(Objects.isNull(renterOrderEntity) || Objects.isNull(renterOrderEntity.getRenterOrderNo())){
            return 0;
        }
        //查询应付租车费用列表
        List<PayableVO> payableVOs = renterOrderCostCombineService.listPayableVO(orderNo,renterOrderEntity.getRenterOrderNo(),memNo);
        //应付租车费用
        int rentAmt = cashierNoTService.sumRentOrderCost(payableVOs);
        //已付租车费用
        int rentAmtPayed = accountRenterCostSettleService.getCostPaidRent(orderNo,memNo);
        return Math.abs(rentAmt) - rentAmtPayed<0?0:(Math.abs(rentAmt) - rentAmtPayed);
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
    public List<PayVo> getOrderPayVO(OrderPaySignReqVO orderPaySign,OrderPayableAmountResVO payVO,boolean isWalletDebtRentCost){
        log.info("getOrderPayVO OrderPayableAmountResVO payVO [{}]",GsonUtils.toJson(payVO));
        //待支付金额明细
        List<PayVo> payVo = new ArrayList<>();
        List<AccountPayAbleResVO> accountPayAbles = payVO.getAccountPayAbles();
        if(!CollectionUtils.isEmpty(accountPayAbles)){
        	//循环遍历
            for(int i =0;i<accountPayAbles.size();i++){
                AccountPayAbleResVO accountPayAbleResVO =  accountPayAbles.get(i);
                String orderNo = accountPayAbleResVO.getOrderNo();
                //车辆押金
                if(RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT.equals(accountPayAbleResVO.getRenterCashCode())){
                	//车辆押金
                	////根据是否选择信用减免来处理是否信用预授权
                	int amt = payVO.getAmtDeposit();
                	if(amt > 0) {
                        List<CashierEntity> list = cashierNoTService.getCashierEntity(orderNo,
                                orderPaySign.getMenNo(), DataPayKindConstant.RENT);

                        Optional<CashierEntity> cashierEntityOptional =
                                list.stream().filter(c -> !StringUtils.equalsIgnoreCase(c.getPaySource(),
                                        PaySourceEnum.WALLET_PAY.getCode())).findFirst();

                        CashierEntity cashierEntity = null;
                        if(cashierEntityOptional.isPresent()) {
                            cashierEntity = cashierEntityOptional.get();
                        }
                        AccountRenterDepositResVO accountRenterDeposit = cashierService.getRenterDepositEntity(orderNo,orderPaySign.getMenNo());
                        Integer payId = Objects.isNull(accountRenterDeposit)?0:accountRenterDeposit.getId();
                        String payIdStr = String.valueOf(payId);
                        PayVo vo = cashierNoTService.getPayVOChangeReqOs(orderNo,cashierEntity,orderPaySign,amt,payVO.getTitle(),DataPayKindConstant.RENT,payIdStr,GsonUtils.toJson(accountRenterDeposit),accountRenterDeposit.getFreeDepositType());
                        String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
                        vo.setPayMd5(payMd5);
                        payVo.add(vo);
                    }
                }else if(RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT.equals(accountPayAbleResVO.getRenterCashCode())){
                    //违章押金
                	//根据是否选择信用减免来处理是否信用预授权
                	int amt = payVO.getAmtWzDeposit();
                	if(amt > 0) {
                        List<CashierEntity> list = cashierNoTService.getCashierEntity(orderNo,orderPaySign.getMenNo(), DataPayKindConstant.DEPOSIT);
                        Optional<CashierEntity> cashierEntityOptional =
                                list.stream().filter(c -> !StringUtils.equalsIgnoreCase(c.getPaySource(),
                                        PaySourceEnum.WALLET_PAY.getCode())).findFirst();
                        CashierEntity cashierEntity = null;
                        if(cashierEntityOptional.isPresent()) {
                            cashierEntity = cashierEntityOptional.get();
                        }
	                    AccountRenterWZDepositResVO accountRenterWZDepositRes = cashierService.getRenterWZDepositEntity(orderNo,orderPaySign.getMenNo());
	                    Integer payId = Objects.isNull(accountRenterWZDepositRes)?0:accountRenterWZDepositRes.getId();
	                    String payIdStr = Objects.isNull(payId)?"":String.valueOf(payId);
	                    PayVo vo = cashierNoTService.getPayVOChangeReqOs(orderNo,cashierEntity,orderPaySign,amt,payVO.getTitle(),DataPayKindConstant.DEPOSIT,payIdStr,GsonUtils.toJson(accountRenterWZDepositRes),accountRenterWZDepositRes.getFreeDepositType());
	                    String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
	                    vo.setPayMd5(payMd5);
	                    payVo.add(vo);
                	}

                }else if(isWalletDebtRentCost==false && RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST.equals(accountPayAbleResVO.getRenterCashCode())){
                	////待付租车费用   租车费用默认是消费
                	
                    //待付租车费用
                    int amt = payVO.getAmtRent();
                    if(amt > 0) {
	                    //默认消费
	                    orderPaySign.setPayType(DataPayTypeConstant.PAY_PUR);
	                    //去掉该条件，根据入参来。
	//                    String payKind = YesNoEnum.YES.getCode().equals(payVO.getIsPayAgain())?DataPayKindConstant.RENT_INCREMENT:DataPayKindConstant.RENT_AMOUNT;
	                    String payKind = DataPayKindConstant.RENT_AMOUNT;
	                    AccountRenterCostSettleEntity entity = cashierService.getAccountRenterCostSettle(orderNo,orderPaySign.getMenNo());
	                    Integer payId = Objects.isNull(entity)?0:entity.getId();
	                    String payIdStr = Objects.isNull(payId)?"":String.valueOf(payId);
	                    PayVo vo = cashierNoTService.getPayVO(orderNo,null,orderPaySign,amt,payVO.getTitle(),payKind,payIdStr,GsonUtils.toJson(entity),3);
	                    String paySn = cashierNoTService.getCashierRentCostPaySn(orderNo,orderPaySign.getMenNo(),payKind);
	                    vo.setPaySn(paySn);
	                    String renterOrderNo = getExtendParamsRentOrderNo(payVO);
	                    vo.setExtendParams(renterOrderNo);
	                    vo.setPayTitle("待支付金额："+Math.abs(amt)+"，订单号："+vo.getOrderNo());
	                    String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
	                    vo.setPayMd5(payMd5);
	                    payVo.add(vo);
                    }
                    
                }else if(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN.equals(accountPayAbleResVO.getRenterCashCode())){
                    //待付租车费用  APP修改订单补付
                    int amt = payVO.getAmtIncrementRent();
                    if(amt > 0) {
	                    //统一按消费来处理(忽略前端的来值),消费考虑到车主拒绝修改订单会退款。仍然保持老系统的。 考虑收款及补充租车费用。
	                    //默认消费
	                    orderPaySign.setPayType(DataPayTypeConstant.PAY_PUR);

	                    //去掉该条件，根据入参来。
	//                    String payKind = YesNoEnum.YES.getCode().equals(payVO.getIsPayAgain())?DataPayKindConstant.RENT_INCREMENT:DataPayKindConstant.RENT_AMOUNT;
	                    String payKind = DataPayKindConstant.RENT_INCREMENT;
	                    AccountRenterCostSettleEntity entity = cashierService.getAccountRenterCostSettle(orderNo,orderPaySign.getMenNo());
	                    Integer payId = Objects.isNull(entity)?0:entity.getId();
	                    String payIdStr = Objects.isNull(payId)?"":String.valueOf(payId);
	                    PayVo vo = cashierNoTService.getPayVO(orderNo,null,orderPaySign,amt,payVO.getTitle(),payKind,payIdStr,GsonUtils.toJson(entity),3);
	                    String paySn = cashierNoTService.getCashierRentCostPaySn(orderNo,orderPaySign.getMenNo(),payKind);
	                    vo.setPaySn(paySn);
	                    String renterOrderNo = getExtendParamsRentOrderNo(payVO);
	                    vo.setExtendParams(renterOrderNo);
	                    vo.setPayTitle("待支付金额："+Math.abs(amt)+"，订单号："+vo.getOrderNo());
	                    String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
	                    vo.setPayMd5(payMd5);
	                    payVo.add(vo);
                    }
                    
                    //----------------------------------------------------------------------------------------------------------------- 以下为三大块 ---------------------------------------
                    
                    
                } else if(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AFTER.equals(accountPayAbleResVO.getRenterCashCode())){
                	////待付租车费用   租车费用默认是消费
                	
                    //待付租车费用
                    int amt = payVO.getAmtRentAfter();
                    if(amt > 0) {
	                    //默认消费
	                    orderPaySign.setPayType(DataPayTypeConstant.PAY_PUR);
	                    //去掉该条件，根据入参来。
	//                        String payKind = YesNoEnum.YES.getCode().equals(payVO.getIsPayAgain())?DataPayKindConstant.RENT_INCREMENT:DataPayKindConstant.RENT_AMOUNT;
	                    String payKind = DataPayKindConstant.RENT_AMOUNT_AFTER;
	                    AccountRenterCostSettleEntity entity = cashierService.getAccountRenterCostSettle(orderNo,orderPaySign.getMenNo());
	                    Integer payId = Objects.isNull(entity)?0:entity.getId();
	                    String payIdStr = Objects.isNull(payId)?"":String.valueOf(payId);
	                    PayVo vo = cashierNoTService.getPayVO(orderNo,null,orderPaySign,amt,payVO.getTitle(),payKind,payIdStr,GsonUtils.toJson(entity),3);
	                    String paySn = cashierNoTService.getCashierRentCostPaySn(orderNo,orderPaySign.getMenNo(),payKind);
	                    vo.setPaySn(paySn);
	                    String renterOrderNo = getExtendParamsRentOrderNo(payVO);
	                    vo.setExtendParams(renterOrderNo);
	                    vo.setPayTitle("待支付金额："+Math.abs(amt)+"，订单号："+vo.getOrderNo());
	                    String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
	                    vo.setPayMd5(payMd5);
	                    payVo.add(vo);
                    }
                }else if(RenterCashCodeEnum.ACCOUNT_RENTER_SUPPLEMENT_COST_AGAIN.equals(accountPayAbleResVO.getRenterCashCode())){
                	////管理后台补付
                    //待付租车费用
                    int amt = accountPayAbleResVO.getAmt();
                    if(amt > 0) {
	                    //统一按消费来处理(忽略前端的来值),消费考虑到车主拒绝修改订单会退款。仍然保持老系统的。 考虑收款及补充租车费用。
	                    //默认消费
	                    orderPaySign.setPayType(DataPayTypeConstant.PAY_PUR);

	                    //去掉该条件，根据入参来。
	//                    String payKind = YesNoEnum.YES.getCode().equals(payVO.getIsPayAgain())?DataPayKindConstant.RENT_INCREMENT:DataPayKindConstant.RENT_AMOUNT;
	                    String payKind = DataPayKindConstant.RENT_INCREMENT_CONSOLE;
	                    AccountRenterCostSettleEntity entity = cashierService.getAccountRenterCostSettle(orderNo,orderPaySign.getMenNo());
	                    Integer payId = Objects.isNull(entity)?0:entity.getId();
	                    String payIdStr = Objects.isNull(payId)?"":String.valueOf(payId);
	                    //特殊处理
	                    PayVo vo = cashierNoTService.getPayVOForOrderSupplementDetail(orderNo,null,orderPaySign,(-amt),accountPayAbleResVO.getTitle(),payKind,payIdStr,GsonUtils.toJson(entity),3);
	                    String paySn = cashierNoTService.getCashierRentCostPaySn(orderNo,orderPaySign.getMenNo(),payKind);
	                    vo.setPaySn(paySn);

	//                    String renterOrderNo = getExtendParamsRentOrderNo(payVO);   //扩展参数
	                    vo.setExtendParams(accountPayAbleResVO.getUniqueNo());   //order_supplement_detail  id字段
	//                    vo.setPayTitle("待支付金额："+Math.abs(amt)+"，订单号："+vo.getOrderNo());
	                    vo.setPayTitle("待支付金额："+ (-amt) +"，订单号："+vo.getOrderNo());  // order_supplement_detai 存在对冲的情况，其他的没有。

	                    String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
	                    vo.setPayMd5(payMd5);
	                    payVo.add(vo);
                    }
                    
                }else if(RenterCashCodeEnum.ACCOUNT_RENTER_DEBT_COST_AGAIN.equals(accountPayAbleResVO.getRenterCashCode())){ 
                	//支付订单欠款
                    //待付租车费用
                    int amt = accountPayAbleResVO.getAmt();
                    if(amt > 0) {
	                    //统一按消费来处理(忽略前端的来值),消费考虑到车主拒绝修改订单会退款。仍然保持老系统的。 考虑收款及补充租车费用。
	                    //默认消费
	                    orderPaySign.setPayType(DataPayTypeConstant.PAY_PUR);

	                    //去掉该条件，根据入参来。
	                    String payKind = DataPayKindConstant.DEBT;
	                    AccountRenterCostSettleEntity entity = cashierService.getAccountRenterCostSettle(orderNo,orderPaySign.getMenNo());
	                    Integer payId = Objects.isNull(entity)?0:entity.getId();
	                    String payIdStr = Objects.isNull(payId)?"":String.valueOf(payId);
	                    PayVo vo = cashierNoTService.getPayVO(orderNo,null,orderPaySign,amt,accountPayAbleResVO.getTitle(),payKind,payIdStr,GsonUtils.toJson(entity),3);
	                    String paySn = cashierNoTService.getCashierRentCostPaySn(orderNo,orderPaySign.getMenNo(),payKind);
	                    vo.setPaySn(paySn);
	//                    String renterOrderNo = getExtendParamsRentOrderNo(payVO);  //扩展参数
	                    //覆盖扩展参数
	                    vo.setExtendParams(accountPayAbleResVO.getUniqueNo()); //account_debt_detail id字段。
	                    vo.setPayTitle("待支付金额："+Math.abs(amt)+"，订单号："+vo.getOrderNo());
	                    String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
	                    vo.setPayMd5(payMd5);
	                    payVo.add(vo);
                    }
                }
            }
        }

        
        return payVo;
    }

    //钱包支付的获取异步通知的debtID SupplementId
    public List<PayVo> getOrderPayVOWalletPay(OrderPaySignReqVO orderPaySign,OrderPayableAmountResVO payVO){
        log.info("getOrderPayVO OrderPayableAmountResVO payVO [{}]",GsonUtils.toJson(payVO));
        //待支付金额明细
        List<PayVo> payVo = new ArrayList<>();
        List<AccountPayAbleResVO> accountPayAbles = payVO.getAccountPayAbles();
        if(!CollectionUtils.isEmpty(accountPayAbles)){
        	//循环遍历
            for(int i =0;i<accountPayAbles.size();i++){
                AccountPayAbleResVO accountPayAbleResVO =  accountPayAbles.get(i);
                String orderNo = accountPayAbleResVO.getOrderNo();
                //车辆押金
                if(RenterCashCodeEnum.ACCOUNT_RENTER_DEPOSIT.equals(accountPayAbleResVO.getRenterCashCode())){
                	//车辆押金
                	////根据是否选择信用减免来处理是否信用预授权
                	int amt = payVO.getAmtWallet01();
                	if(amt > 0) {

                        List<CashierEntity> list = cashierNoTService.getCashierEntity(orderNo,
                                orderPaySign.getMenNo(), DataPayKindConstant.RENT);

                        Optional<CashierEntity> cashierEntityOptional =
                                list.stream().filter(c -> !StringUtils.equalsIgnoreCase(c.getPaySource(),
                                        PaySourceEnum.WALLET_PAY.getCode())).findFirst();

                        CashierEntity cashierEntity = null;
                        if(cashierEntityOptional.isPresent()) {
                            cashierEntity = cashierEntityOptional.get();
                        }
	                    AccountRenterDepositResVO accountRenterDeposit = cashierService.getRenterDepositEntity(orderNo,orderPaySign.getMenNo());
	                    Integer payId = Objects.isNull(accountRenterDeposit)?0:accountRenterDeposit.getId();
	                    String payIdStr = Objects.isNull(payId)?"":String.valueOf(payId);
	                    PayVo vo = cashierNoTService.getPayVOChangeReqOs(orderNo,cashierEntity,orderPaySign,amt,payVO.getTitle(),DataPayKindConstant.RENT,payIdStr,GsonUtils.toJson(accountRenterDeposit),accountRenterDeposit.getFreeDepositType());
	                    String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
	                    vo.setPayMd5(payMd5);
	                    payVo.add(vo);
                	}
                }else if(RenterCashCodeEnum.ACCOUNT_RENTER_WZ_DEPOSIT.equals(accountPayAbleResVO.getRenterCashCode())){
                    //违章押金
                	//根据是否选择信用减免来处理是否信用预授权
                	int amt = payVO.getAmtWallet02();
                	if(amt > 0) {
                        List<CashierEntity> list = cashierNoTService.getCashierEntity(orderNo,
                                orderPaySign.getMenNo(), DataPayKindConstant.DEPOSIT);

                        Optional<CashierEntity> cashierEntityOptional =
                                list.stream().filter(c -> !StringUtils.equalsIgnoreCase(c.getPaySource(),
                                        PaySourceEnum.WALLET_PAY.getCode())).findFirst();

                        CashierEntity cashierEntity = null;
                        if(cashierEntityOptional.isPresent()) {
                            cashierEntity = cashierEntityOptional.get();
                        }
	                    AccountRenterWZDepositResVO accountRenterWZDepositRes = cashierService.getRenterWZDepositEntity(orderNo,orderPaySign.getMenNo());
	                    Integer payId = Objects.isNull(accountRenterWZDepositRes)?0:accountRenterWZDepositRes.getId();
	                    String payIdStr = Objects.isNull(payId)?"":String.valueOf(payId);
	                    PayVo vo = cashierNoTService.getPayVOChangeReqOs(orderNo,cashierEntity,orderPaySign,amt,payVO.getTitle(),DataPayKindConstant.DEPOSIT,payIdStr,GsonUtils.toJson(accountRenterWZDepositRes),accountRenterWZDepositRes.getFreeDepositType());
	                    String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
	                    vo.setPayMd5(payMd5);
	                    payVo.add(vo);
                	}
                }else if(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST.equals(accountPayAbleResVO.getRenterCashCode())){  //isWalletDebtRentCost==false &&
                	////待付租车费用   租车费用默认是消费

                    //待付租车费用
                    int amt = payVO.getAmtWallet11();
                    if(amt > 0) {
	                    //默认消费
	                    orderPaySign.setPayType(DataPayTypeConstant.PAY_PUR);
	                    //去掉该条件，根据入参来。
	//                    String payKind = YesNoEnum.YES.getCode().equals(payVO.getIsPayAgain())?DataPayKindConstant.RENT_INCREMENT:DataPayKindConstant.RENT_AMOUNT;
	                    String payKind = DataPayKindConstant.RENT_AMOUNT;
	                    AccountRenterCostSettleEntity entity = cashierService.getAccountRenterCostSettle(orderNo,orderPaySign.getMenNo());
	                    Integer payId = Objects.isNull(entity)?0:entity.getId();
	                    String payIdStr = Objects.isNull(payId)?"":String.valueOf(payId);
	                    PayVo vo = cashierNoTService.getPayVO(orderNo,null,orderPaySign,amt,payVO.getTitle(),payKind,payIdStr,GsonUtils.toJson(entity),3);
	                    String paySn = cashierNoTService.getCashierRentCostPaySn(orderNo,orderPaySign.getMenNo(),payKind);
	                    vo.setPaySn(paySn);
	                    String renterOrderNo = getExtendParamsRentOrderNo(payVO);
	                    vo.setExtendParams(renterOrderNo);
	                    vo.setPayTitle("待支付金额："+Math.abs(amt)+"，订单号："+vo.getOrderNo());
	                    String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
	                    vo.setPayMd5(payMd5);
	                    payVo.add(vo);
                    }

                }else if(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN.equals(accountPayAbleResVO.getRenterCashCode())){
                    //待付租车费用  APP修改订单补付
                    int amt = payVO.getAmtWallet03();
                    if(amt > 0) {
	                    //统一按消费来处理(忽略前端的来值),消费考虑到车主拒绝修改订单会退款。仍然保持老系统的。 考虑收款及补充租车费用。
	                    //默认消费
	                    orderPaySign.setPayType(DataPayTypeConstant.PAY_PUR);

	                    //去掉该条件，根据入参来。
	//                    String payKind = YesNoEnum.YES.getCode().equals(payVO.getIsPayAgain())?DataPayKindConstant.RENT_INCREMENT:DataPayKindConstant.RENT_AMOUNT;
	                    String payKind = DataPayKindConstant.RENT_INCREMENT;
	                    AccountRenterCostSettleEntity entity = cashierService.getAccountRenterCostSettle(orderNo,orderPaySign.getMenNo());
	                    Integer payId = Objects.isNull(entity)?0:entity.getId();
	                    String payIdStr = Objects.isNull(payId)?"":String.valueOf(payId);
	                    PayVo vo = cashierNoTService.getPayVO(orderNo,null,orderPaySign,amt,payVO.getTitle(),payKind,payIdStr,GsonUtils.toJson(entity),3);
	                    String paySn = cashierNoTService.getCashierRentCostPaySn(orderNo,orderPaySign.getMenNo(),payKind);
	                    vo.setPaySn(paySn);
	                    String renterOrderNo = getExtendParamsRentOrderNo(payVO);
	                    vo.setExtendParams(renterOrderNo);
	                    vo.setPayTitle("待支付金额："+Math.abs(amt)+"，订单号："+vo.getOrderNo());
	                    String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
	                    vo.setPayMd5(payMd5);
	                    payVo.add(vo);
                    }

                    //----------------------------------------------------------------------------------------------------------------- 以下为三大块 ---------------------------------------


                } else if(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AFTER.equals(accountPayAbleResVO.getRenterCashCode())){
                	////待付租车费用   租车费用默认是消费

                    //待付租车费用
                    int amt = payVO.getAmtWallet12();
                    if(amt > 0) {
	                    //默认消费
	                    orderPaySign.setPayType(DataPayTypeConstant.PAY_PUR);
	                    //去掉该条件，根据入参来。
	//                        String payKind = YesNoEnum.YES.getCode().equals(payVO.getIsPayAgain())?DataPayKindConstant.RENT_INCREMENT:DataPayKindConstant.RENT_AMOUNT;
	                    String payKind = DataPayKindConstant.RENT_AMOUNT_AFTER;
	                    AccountRenterCostSettleEntity entity = cashierService.getAccountRenterCostSettle(orderNo,orderPaySign.getMenNo());
	                    Integer payId = Objects.isNull(entity)?0:entity.getId();
	                    String payIdStr = Objects.isNull(payId)?"":String.valueOf(payId);
	                    PayVo vo = cashierNoTService.getPayVO(orderNo,null,orderPaySign,amt,payVO.getTitle(),payKind,payIdStr,GsonUtils.toJson(entity),3);
	                    String paySn = cashierNoTService.getCashierRentCostPaySn(orderNo,orderPaySign.getMenNo(),payKind);
	                    vo.setPaySn(paySn);
	                    String renterOrderNo = getExtendParamsRentOrderNo(payVO);
	                    vo.setExtendParams(renterOrderNo);
	                    vo.setPayTitle("待支付金额："+Math.abs(amt)+"，订单号："+vo.getOrderNo());
	                    String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
	                    vo.setPayMd5(payMd5);
	                    payVo.add(vo);
                    }
                }else if(RenterCashCodeEnum.ACCOUNT_RENTER_SUPPLEMENT_COST_AGAIN.equals(accountPayAbleResVO.getRenterCashCode())){
                	////管理后台补付
                    //待付租车费用
                    int amt = payVO.getAmtWallet08();
                    if(amt > 0) {
	                    //统一按消费来处理(忽略前端的来值),消费考虑到车主拒绝修改订单会退款。仍然保持老系统的。 考虑收款及补充租车费用。
	                    //默认消费
	                    orderPaySign.setPayType(DataPayTypeConstant.PAY_PUR);

	                    //去掉该条件，根据入参来。
	//                    String payKind = YesNoEnum.YES.getCode().equals(payVO.getIsPayAgain())?DataPayKindConstant.RENT_INCREMENT:DataPayKindConstant.RENT_AMOUNT;
	                    String payKind = DataPayKindConstant.RENT_INCREMENT_CONSOLE;
	                    AccountRenterCostSettleEntity entity = cashierService.getAccountRenterCostSettle(orderNo,orderPaySign.getMenNo());
	                    Integer payId = Objects.isNull(entity)?0:entity.getId();
	                    String payIdStr = Objects.isNull(payId)?"":String.valueOf(payId);
	                    //特殊处理
	                    PayVo vo = cashierNoTService.getPayVOForOrderSupplementDetail(orderNo,null,orderPaySign,(-amt),accountPayAbleResVO.getTitle(),payKind,payIdStr,GsonUtils.toJson(entity),3);
	                    String paySn = cashierNoTService.getCashierRentCostPaySn(orderNo,orderPaySign.getMenNo(),payKind);
	                    vo.setPaySn(paySn);

	//                    String renterOrderNo = getExtendParamsRentOrderNo(payVO);   //扩展参数
	                    vo.setExtendParams(accountPayAbleResVO.getUniqueNo());   //order_supplement_detail  id字段
	//                    vo.setPayTitle("待支付金额："+Math.abs(amt)+"，订单号："+vo.getOrderNo());
	                    vo.setPayTitle("待支付金额："+ (-amt) +"，订单号："+vo.getOrderNo());  // order_supplement_detai 存在对冲的情况，其他的没有。

	                    String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
	                    vo.setPayMd5(payMd5);
	                    payVo.add(vo);
                    }

                }else if(RenterCashCodeEnum.ACCOUNT_RENTER_DEBT_COST_AGAIN.equals(accountPayAbleResVO.getRenterCashCode())){
                	//支付订单欠款
                    //待付租车费用
                    int amt = payVO.getAmtWallet07();
                    if(amt > 0) {
	                    //统一按消费来处理(忽略前端的来值),消费考虑到车主拒绝修改订单会退款。仍然保持老系统的。 考虑收款及补充租车费用。
	                    //默认消费
	                    orderPaySign.setPayType(DataPayTypeConstant.PAY_PUR);

	                    //去掉该条件，根据入参来。
	                    String payKind = DataPayKindConstant.DEBT;
	                    AccountRenterCostSettleEntity entity = cashierService.getAccountRenterCostSettle(orderNo,orderPaySign.getMenNo());
	                    Integer payId = Objects.isNull(entity)?0:entity.getId();
	                    String payIdStr = Objects.isNull(payId)?"":String.valueOf(payId);
	                    PayVo vo = cashierNoTService.getPayVO(orderNo,null,orderPaySign,amt,accountPayAbleResVO.getTitle(),payKind,payIdStr,GsonUtils.toJson(entity),3);
	                    String paySn = cashierNoTService.getCashierRentCostPaySn(orderNo,orderPaySign.getMenNo(),payKind);
	                    vo.setPaySn(paySn);
	//                    String renterOrderNo = getExtendParamsRentOrderNo(payVO);  //扩展参数
	                    //覆盖扩展参数
	                    vo.setExtendParams(accountPayAbleResVO.getUniqueNo()); //account_debt_detail id字段。
	                    vo.setPayTitle("待支付金额："+Math.abs(amt)+"，订单号："+vo.getOrderNo());
	                    String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
	                    vo.setPayMd5(payMd5);
	                    payVo.add(vo);
                    }
                }
            }
        }

        return payVo;
    }


    /**
     * 退款操作
     * @param cashierRefundApply
     */
    public void refundOrderPay(CashierRefundApplyEntity cashierRefundApply){
        try {
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
	            log.info("(退款同步处理退款申请表)refundCallBackSuccess:[{}]", GsonUtils.toJson(vo));
	            
	            //更新收银台
	            List<NotifyDataVo> lstNotifyDataVo = new ArrayList<NotifyDataVo>();
	            lstNotifyDataVo.add(notifyDataVo);
	            OrderPayCallBackSuccessVO orderPayCallBackSuccessVO = cashierService.callBackSuccess(lstNotifyDataVo);
	            log.info("(退款同步处理收银台)callBackSuccess:[{}]", GsonUtils.toJson(orderPayCallBackSuccessVO));
            }else {
            	log.error("退款返回的结果vo为null异常,params=[{}]",GsonUtils.toJson(refundVo));
            }
		} catch (Exception e) {
			log.error("refundOrderPay exception: params=[{}]",GsonUtils.toJson(cashierRefundApply),e);
		}
    }
    
    public void refundOrderPayPreAuth(CashierRefundApplyEntity cashierRefundApply){
        try {
        	if(Objects.isNull(cashierRefundApply) || Objects.isNull(cashierRefundApply.getId())){
                return;
            }
            //更新退款次数，最多允许退3次。 num<3 LIMIT 100
            cashierRefundApplyNoTService.updateCashierRefundApplyEntity(cashierRefundApply);
            //2 构造退款参数
            RefundVo refundVo = cashierNoTService.getRefundVo(cashierRefundApply);
            //3退款
            AutoPayResultVo vo = refundRemoteService.refundOrderPay(refundVo);
            //超出15天的，需要重新添加收银台记录，重新发起扣款流程。
            
            if(Objects.nonNull(vo)){
            	//根据错误码来重新生成。
            	if(vo.getResResult()!=null) {
            		log.info("退款返回的结果vo=[{}],params=[{}]，resResult=[{}]",GsonUtils.toJson(vo),GsonUtils.toJson(refundVo),vo.getResResult());
            	} else {
            		log.info("退款返回的结果vo=[{}],params=[{}]",GsonUtils.toJson(vo),GsonUtils.toJson(refundVo));
            	}
            	boolean flagCreate = false;
            	
            	String subCode = vo.getResResult()!=null?(String) vo.getResResult().get("subCode"):"";
                log.info("subCode="+ subCode);
     ////"subCode":"ACQ.BUYER_BALANCE_NOT_ENOUGH",
                ////ACQ.BUYER_BALANCE_NOT_ENOUGH 测试流程!!     || "ACQ.BUYER_BALANCE_NOT_ENOUGH".equals(subCode)
                if("ACQ.TRADE_HAS_CLOSE".equals(subCode) || "ACQ.CONTEXT_INCONSISTENT".equals(subCode) || "UNIQUE_VIOLATION".equals(subCode)){   //|| "ACQ.BUYER_BALANCE_NOT_ENOUGH".equals(subCode)
                	flagCreate = true;
                }
            	if(flagCreate) {		
            		cashierRefundApplyNoTService.deleteOrInitNewCashierRefundApplyEntity(cashierRefundApply);
            		log.info("(重新生成退款记录)params:[{}]", GsonUtils.toJson(cashierRefundApply));
            	}else {
            		
	                NotifyDataVo notifyDataVo = new NotifyDataVo();
	                BeanUtils.copyProperties(vo,notifyDataVo);
	                notifyDataVo.setSettleAmount(vo.getRefundAmt());
	                //退款调用成功操作
	                cashierService.refundCallBackSuccess(vo);
	                log.info("(退款同步处理退款申请表)refundCallBackSuccess:[{}]", GsonUtils.toJson(vo));
	                
	                //更新收银台
	                List<NotifyDataVo> lstNotifyDataVo = new ArrayList<NotifyDataVo>();
	                lstNotifyDataVo.add(notifyDataVo);
	                OrderPayCallBackSuccessVO orderPayCallBackSuccessVO = cashierService.callBackSuccess(lstNotifyDataVo);
	                log.info("(退款同步处理收银台)callBackSuccess:[{}]", GsonUtils.toJson(orderPayCallBackSuccessVO));
            	}
                
            }else {
            	log.error("退款返回的结果vo为null异常,params=[{}]",GsonUtils.toJson(refundVo));
            }
		} catch (Exception e) {
			log.error("refundOrderPay exception: params=[{}]",GsonUtils.toJson(cashierRefundApply),e);
		}
    }
    
    
    /**
     * 线下支付
     * @param orderNo
     * @param memNo
     * @return
     */
    public OrderPaySignReqVO buildOrderPaySignReqVOOfflinePay(String orderNo,String memNo) {
		OrderPaySignReqVO vo = new OrderPaySignReqVO();
		vo.setMenNo(memNo);
		vo.setIsUseWallet(0);  //线下支付或虚拟支付，租车费用。
		vo.setOrderNo(orderNo);
		////配置参数
		
		List<String> payKinds = new ArrayList<String>();
//		payKinds.add(DataPayKindConstant.RENT_AMOUNT);
//		boolean isEnterpriseUserOrder = renterMemberService.isEnterpriseUserOrder(renterOrderEntity.getRenterOrderNo());
		//扩展paykind
//        if(isEnterpriseUserOrder) {
		/**
		 * 刷新双押金为0的情况。
		 */
        	payKinds.add(DataPayKindConstant.RENT);
        	payKinds.add(DataPayKindConstant.DEPOSIT);
//        }
		
		vo.setPayKind(payKinds);
		//////////////////////////// 以上为公共参数
		vo.setOperator(1);
		vo.setOperatorName("COREAPI");
		vo.setOpenId("");
		vo.setPayType("01");   //默认 消费
		vo.setReqOs("IOS");  //默认

		List<String> paySources = new ArrayList<String>();
		paySources.add(DataPaySourceConstant.WALLETPAY);
		vo.setPaySource(paySources);
//		vo.setPaySource(DataPaySourceConstant.ALIPAY);  //默认
//		vo.setPaySource(DataPaySourceConstant.WEIXIN_APP);
		
		return vo;
	}
    
    /**
     * 刷新钱包抵扣的参数封装
     * @param renterOrderEntity
     * @return
     */
    public OrderPaySignReqVO buildOrderPaySignReqVO(RenterOrderEntity renterOrderEntity) {
		OrderPaySignReqVO vo = new OrderPaySignReqVO();
		vo.setMenNo(String.valueOf(renterOrderEntity.getRenterMemNo()));
		vo.setIsUseWallet(renterOrderEntity.getIsUseWallet());
		vo.setOrderNo(renterOrderEntity.getOrderNo());
		////配置参数
		
		List<String> payKinds = new ArrayList<String>();
		payKinds.add(DataPayKindConstant.RENT_AMOUNT);
		boolean isEnterpriseUserOrder = renterMemberService.isEnterpriseUserOrder(renterOrderEntity.getRenterOrderNo());
		//扩展paykind
        if(isEnterpriseUserOrder) {
        	payKinds.add(DataPayKindConstant.RENT);
        	payKinds.add(DataPayKindConstant.DEPOSIT);
        }
		
		vo.setPayKind(payKinds);
		//////////////////////////// 以上为公共参数
		vo.setOperator(1);
		vo.setOperatorName("COREAPI");
		vo.setOpenId("");
		vo.setPayType("01");   //默认 消费
		vo.setReqOs("IOS");  //默认
//		vo.setPaySource(DataPaySourceConstant.WALLETPAY);
//		vo.setPaySource(DataPaySourceConstant.ALIPAY);  //默认
//		vo.setPaySource(DataPaySourceConstant.WEIXIN_APP);
//		vo.setPaySource("00");

		List<String> paySources = new ArrayList<>();
		paySources.add(DataPaySourceConstant.WALLETPAY);
		vo.setPaySource(paySources);
		return vo;
	}
    
    
    public OrderPaySignReqVO buildOrderPaySignReqVO(String orderNo,String memNo,Integer isUseWallet) {
		OrderPaySignReqVO vo = new OrderPaySignReqVO();
		vo.setMenNo(memNo);
		vo.setIsUseWallet(isUseWallet);
		vo.setOrderNo(orderNo);
		////配置参数
		
		List<String> payKinds = new ArrayList<String>();
		payKinds.add(DataPayKindConstant.RENT_AMOUNT);
		//
		RenterOrderEntity renterOrderEntity = cashierNoTService.getRenterOrderNoByOrderNo(orderNo);
		if(renterOrderEntity != null) {
			boolean isEnterpriseUserOrder = renterMemberService.isEnterpriseUserOrder(renterOrderEntity.getRenterOrderNo());
			//扩展paykind
	        if(isEnterpriseUserOrder) {
	        	payKinds.add(DataPayKindConstant.RENT);
	        	payKinds.add(DataPayKindConstant.DEPOSIT);
	        }
		}
		vo.setPayKind(payKinds);
		//////////////////////////// 以上为公共参数
		vo.setOperator(1);
		vo.setOperatorName("COREAPI");
		vo.setOpenId("");
		vo.setPayType("01");   //默认 消费
		vo.setReqOs("IOS");  //默认
//		vo.setPaySource(DataPaySourceConstant.ALIPAY);
//		vo.setPaySource(DataPaySourceConstant.WEIXIN_APP);
//		vo.setPaySource("00");

		List<String> paySources = new ArrayList<String>();
		paySources.add(DataPaySourceConstant.WALLETPAY);
		vo.setPaySource(paySources);
		return vo;
	}


	public int getWalletDeductAmt(String orderNo, List<String> payKind) {
		return cashierService.getWalletDeductAmt(orderNo,payKind);
	}


	/**
	 * 管理后台修改订单使用钱包组装支付数据
	 * @param orderPaySign
	 * @param amt
	 * @return List<PayVo>
	 */
	public List<PayVo> getPayVOListForConsoleUseWallet(OrderPaySignReqVO orderPaySign,int amt) {
		//待支付金额明细
        List<PayVo> payVo = new ArrayList<PayVo>();
		//统一按消费来处理(忽略前端的来值),消费考虑到车主拒绝修改订单会退款。仍然保持老系统的。 考虑收款及补充租车费用。
        //默认消费
        orderPaySign.setPayType(DataPayTypeConstant.PAY_PUR);

        //去掉该条件，根据入参来。
//                    String payKind = YesNoEnum.YES.getCode().equals(payVO.getIsPayAgain())?DataPayKindConstant.RENT_INCREMENT:DataPayKindConstant.RENT_AMOUNT;
        String payKind = DataPayKindConstant.RENT_INCREMENT;
        AccountRenterCostSettleEntity entity = cashierService.getAccountRenterCostSettle(orderPaySign.getOrderNo(),orderPaySign.getMenNo());
        Integer payId = Objects.isNull(entity)?0:entity.getId();
        String payIdStr = Objects.isNull(payId)?"":String.valueOf(payId);
        PayVo vo = cashierNoTService.getPayVO(orderPaySign.getOrderNo(),null,orderPaySign,amt,"",payKind,payIdStr,GsonUtils.toJson(entity),3);
        String paySn = cashierNoTService.getCashierRentCostPaySn(orderPaySign.getOrderNo(),orderPaySign.getMenNo(),payKind);
        vo.setPaySn(paySn);
        //String renterOrderNo = getExtendParamsRentOrderNo(payVO);
        //vo.setExtendParams(renterOrderNo);
        vo.setPayTitle("待支付金额："+Math.abs(amt)+"，订单号："+vo.getOrderNo());
        String payMd5 = MD5.MD5Encode(FasterJsonUtil.toJson(vo));
        vo.setPayMd5(payMd5);
        payVo.add(vo);
        return payVo;
	}

}

