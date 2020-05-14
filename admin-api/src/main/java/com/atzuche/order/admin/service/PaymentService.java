/**
 * 
 */
package com.atzuche.order.admin.service;

import com.atzuche.order.admin.vo.req.payment.PaymentRequestVO;
import com.atzuche.order.admin.vo.resp.payment.PaymentInformationResponseVO;
import com.atzuche.order.admin.vo.resp.payment.PaymentResponseVO;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.orderDetailDto.*;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.commons.enums.PayCashTypeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.cashier.PaySourceEnum;
import com.atzuche.order.commons.enums.cashier.PayTypeEnum;
import com.atzuche.order.commons.exceptions.OrderStatusNotFoundException;
import com.atzuche.order.commons.vo.req.PaymentReqVO;
import com.atzuche.order.commons.vo.res.CashierResVO;
import com.atzuche.order.commons.vo.res.PaymentRespVO;
import com.atzuche.order.open.service.FeignPaymentService;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.commons.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jing.huang
 *
 */
@Slf4j
@Service
public class PaymentService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	FeignPaymentService feignPaymentService;
    @Autowired
    private RemoteFeignService remoteFeignService;

	public PaymentInformationResponseVO platformPaymentList(PaymentRequestVO paymentRequestVO) {
		String orderNo = paymentRequestVO.getOrderNo();
		PaymentInformationResponseVO respVo = new PaymentInformationResponseVO();

		/**
		 * 租车费用/车辆押金结算前,支付信息列表
		 */
		List<PaymentResponseVO> beforeDepositSettlementPaymentList = new ArrayList<>();

		/**
		 * 租车费用/车辆押金结算时/后,支付信息列表
		 */
		List<PaymentResponseVO> afterDepositSettlementPaymentList = new ArrayList<>();

		/**
		 * 违章押金结算时/后,支付信息列表
		 */
		List<PaymentResponseVO> violationDepositSettlementPaymentList = new ArrayList<>();


        PaymentReqVO paymentReqVO = new PaymentReqVO();
        paymentReqVO.setOrderNo(orderNo);
        PaymentRespVO paymentRespVO = remoteFeignService.queryPaymentFromRemote(paymentReqVO);
        OrderStatusDTO orderStatusDTO = paymentRespVO.getOrderStatusDTO();

        if(orderStatusDTO == null){
            log.error("订单状态查询为空 orderNo={}",orderNo);
            throw new OrderStatusNotFoundException();
        }
        /**
		 * 违章结算时间
		 */
		LocalDateTime wzSettleTime = null;
		/**
		 * 租车费用结算时间
		 */
		LocalDateTime settleTime = null;
        /*
        * 取消时候的wz结算时间特殊处理
        * */
        LocalDateTime wzSettleTimeRefound = null;

		//租车费用结算状态:0,否 1,是 
		if(orderStatusDTO != null && orderStatusDTO.getSettleStatus().intValue() != 0) {
			settleTime = orderStatusDTO.getSettleTime();
		}

		if(orderStatusDTO != null && orderStatusDTO.getWzSettleStatus().intValue() != 0) {  //违章结算状态:0,否 1,是
			wzSettleTime = orderStatusDTO.getWzSettleTime();
            wzSettleTimeRefound = wzSettleTime;
		}
        if(orderStatusDTO.getStatus() == OrderStatusEnum.CLOSED.getStatus()){
            wzSettleTimeRefound = null;
        }


		if(paymentRespVO == null || paymentRespVO.getCashierResVOList().size()<=0) {
            respVo.setAfterDepositSettlementPaymentList(afterDepositSettlementPaymentList);
            respVo.setBeforeDepositSettlementPaymentList(beforeDepositSettlementPaymentList);
            respVo.setViolationDepositSettlementPaymentList(violationDepositSettlementPaymentList);
            return respVo;
        }
        List<CashierResVO> cashierResVOList = paymentRespVO.getCashierResVOList();
        List<AccountVirtualPayDetailDTO> accountVirtualPayDetailDTOList = paymentRespVO.getAccountVirtualPayDetailDTOList();
        List<CashierRefundApplyDTO> cashierRefundApplyDTOList = paymentRespVO.getCashierRefundApplyDTOList();
        List<OfflineRefundApplyDTO> offlineRefundApplyDTOList = paymentRespVO.getOfflineRefundApplyDTOList();

        for (CashierResVO cashierEntity : cashierResVOList) {
            String payKind = cashierEntity.getPayKind();
            String paySource = cashierEntity.getPaySource();
            String payTime = cashierEntity.getPayTime();
            if(DataPayKindConstant.RENT_AMOUNT.equals(payKind) && PaySourceEnum.WALLET_PAY.getCode().equals(paySource)){
                payTime = LocalDateTimeUtils.formatDateTime(cashierEntity.getCreateTime(),"yyyyMMddHHmmss") ;
            }

            if(StringUtils.isBlank(payTime)) {
                LocalDateTime payTimeLdt = LocalDateTimeUtils.dateToLocalDateTime(new Date());
                PaymentResponseVO vo = convertPaymentResponseVO(cashierEntity,payTimeLdt);
                beforeDepositSettlementPaymentList.add(vo);
            }else {
                /**
                 * 根据支付时间来切换
                 */
                LocalDateTime payTimeLdt = LocalDateTimeUtils.parseStringToDateTime(payTime, "yyyyMMddHHmmss");
                if(settleTime == null) {
                    PaymentResponseVO vo = convertPaymentResponseVO(cashierEntity,payTimeLdt);
                    beforeDepositSettlementPaymentList.add(vo);
                }else {

                    if(settleTime != null &&  payTimeLdt.isBefore(settleTime)) {
                        PaymentResponseVO vo = convertPaymentResponseVO(cashierEntity,payTimeLdt);
                        beforeDepositSettlementPaymentList.add(vo);
                    }else if(wzSettleTime != null && payTimeLdt.isAfter(wzSettleTime)) {
                        PaymentResponseVO vo = convertPaymentResponseVO(cashierEntity,payTimeLdt);
                        violationDepositSettlementPaymentList.add(vo);
                    }else {  //中间段的。
                        //收银台
                        PaymentResponseVO vo = convertPaymentResponseVO(cashierEntity,payTimeLdt);
                        afterDepositSettlementPaymentList.add(vo);
                    }
                    //退款（线下+虚拟支付+真实支付）

                    if(DataPayKindConstant.DEPOSIT.equals(payKind)){//违章押金
                        if(PaySourceEnum.AT_OFFLINE.getCode().equals(paySource)){//线下支付
                            OfflineRefundApplyDTO offlineRefundApplyDTO = filterBySourceCode(offlineRefundApplyDTOList, Arrays.asList(RenterCashCodeEnum.SETTLE_WZ_DEPOSIT_TO_RETURN_AMT,RenterCashCodeEnum.CANCEL_RENT_WZ_DEPOSIT_TO_RETURN_AMT), null, wzSettleTimeRefound);
                            PaymentResponseVO vo = convertPaymentFromOfflineRefundApply(offlineRefundApplyDTO);
                            if(vo != null)afterDepositSettlementPaymentList.add(vo);
                        }else if(PaySourceEnum.VIRTUAL_PAY.getCode().equals(paySource)){//虚拟支付
                            AccountVirtualPayDetailDTO accountVirtualPayDetailDTO = filterByPayCashTypeAndPayType(accountVirtualPayDetailDTOList, PayCashTypeEnum.WZ_DEPOSIT, PayTypeEnum.PUR_RETURN, null, wzSettleTimeRefound);
                            PaymentResponseVO vo = convertPaymentFromVirtualPayDetail(accountVirtualPayDetailDTO);
                            if(vo != null)afterDepositSettlementPaymentList.add(vo);
                        }else{
                            CashierRefundApplyDTO cashierRefundApplyDTO = filterCashierRefound(cashierRefundApplyDTOList, DataPayKindConstant.DEPOSIT, PayTypeEnum.PUR_RETURN, null, wzSettleTimeRefound);
                            PaymentResponseVO vo = convertPaymentFromCashierRefundApply(cashierRefundApplyDTO);
                            if(vo != null)afterDepositSettlementPaymentList.add(vo);
                        }
                    }else if(DataPayKindConstant.RENT.equals(payKind)){//车辆押金
                        if(PaySourceEnum.AT_OFFLINE.getCode().equals(paySource)){//线下支付
                            OfflineRefundApplyDTO offlineRefundApplyDTO = filterBySourceCode(offlineRefundApplyDTOList,Arrays.asList(RenterCashCodeEnum.SETTLE_RENT_DEPOSIT_TO_RETURN_AMT,RenterCashCodeEnum.CANCEL_RENT_DEPOSIT_TO_RETURN_AMT), null, wzSettleTimeRefound);
                            PaymentResponseVO vo = convertPaymentFromOfflineRefundApply(offlineRefundApplyDTO);
                            if(vo != null)afterDepositSettlementPaymentList.add(vo);
                        }else if(PaySourceEnum.VIRTUAL_PAY.getCode().equals(paySource)){//虚拟支付
                            AccountVirtualPayDetailDTO accountVirtualPayDetailDTO = filterByPayCashTypeAndPayType(accountVirtualPayDetailDTOList, PayCashTypeEnum.DEPOSIT, PayTypeEnum.PUR_RETURN, null, wzSettleTimeRefound);
                            PaymentResponseVO vo = convertPaymentFromVirtualPayDetail(accountVirtualPayDetailDTO);
                            if(vo != null)afterDepositSettlementPaymentList.add(vo);
                        }else{
                            CashierRefundApplyDTO cashierRefundApplyDTO = filterCashierRefound(cashierRefundApplyDTOList, DataPayKindConstant.RENT, PayTypeEnum.PUR_RETURN, null, wzSettleTimeRefound);
                            PaymentResponseVO vo = convertPaymentFromCashierRefundApply(cashierRefundApplyDTO);
                            if(vo != null)afterDepositSettlementPaymentList.add(vo);
                        }
                    }else if(DataPayKindConstant.RENT_AMOUNT.equals(payKind)){//租车费用
                        if(PaySourceEnum.AT_OFFLINE.getCode().equals(paySource)){//线下支付
                            OfflineRefundApplyDTO offlineRefundApplyDTO = filterBySourceCode(offlineRefundApplyDTOList,Arrays.asList(RenterCashCodeEnum.SETTLE_RENT_COST_TO_RETURN_AMT,RenterCashCodeEnum.CANCEL_RENT_COST_TO_RETURN_AMT), null, wzSettleTimeRefound);
                            PaymentResponseVO vo = convertPaymentFromOfflineRefundApply(offlineRefundApplyDTO);
                            if(vo != null)afterDepositSettlementPaymentList.add(vo);
                        }else if(PaySourceEnum.VIRTUAL_PAY.getCode().equals(paySource)){//虚拟支付
                            AccountVirtualPayDetailDTO accountVirtualPayDetailDTO = filterByPayCashTypeAndPayType(accountVirtualPayDetailDTOList, PayCashTypeEnum.RENTER_COST, PayTypeEnum.PUR_RETURN, null, wzSettleTimeRefound);
                            PaymentResponseVO vo = convertPaymentFromVirtualPayDetail(accountVirtualPayDetailDTO);
                            if(vo != null)afterDepositSettlementPaymentList.add(vo);
                        }else if(PaySourceEnum.WALLET_PAY.getCode().equals(paySource)){//钱包支付
                            List<AccountRenterCostDetailDTO> accountRenterCostDetailDTOList = paymentRespVO.getAccountRenterCostDetailDTOList();
                            AccountRenterCostDetailDTO accountRenterCostDetailDTO = filterRenterCost(accountRenterCostDetailDTOList, PaySourceEnum.WALLET_PAY, PayTypeEnum.PUR_RETURN, null, wzSettleTimeRefound);
                            PaymentResponseVO vo = convertAccountRenterCostDetail(accountRenterCostDetailDTO);
                            if(vo != null)afterDepositSettlementPaymentList.add(vo);
                        }else{
                            CashierRefundApplyDTO cashierRefundApplyDTO = filterCashierRefound(cashierRefundApplyDTOList, DataPayKindConstant.RENT_AMOUNT, PayTypeEnum.PUR_RETURN, null, wzSettleTimeRefound);
                            PaymentResponseVO vo = convertPaymentFromCashierRefundApply(cashierRefundApplyDTO);
                            if(vo != null)afterDepositSettlementPaymentList.add(vo);
                        }
                    }
                }
            }
        }

		/**
		 * 数据封装
		 */
		respVo.setAfterDepositSettlementPaymentList(afterDepositSettlementPaymentList);
		respVo.setBeforeDepositSettlementPaymentList(beforeDepositSettlementPaymentList);
		respVo.setViolationDepositSettlementPaymentList(violationDepositSettlementPaymentList);
		return respVo;
	}
    /**
     * 对象转换
     * @param OfflineRefundApplyEntity
     * @return
     */
    private PaymentResponseVO convertAccountRenterCostDetail(AccountRenterCostDetailDTO accountRenterCostDetailDTO) {
        PaymentResponseVO vo = new PaymentResponseVO();
        if(accountRenterCostDetailDTO == null){
            return null;
        }
        vo.setCreateTime(LocalDateTimeUtils.formatDateTime(accountRenterCostDetailDTO.getCreateTime()));
        vo.setItem("租车费用");
        vo.setPaymentType(convertType(accountRenterCostDetailDTO.getPayType()));
        vo.setAmount(String.valueOf(accountRenterCostDetailDTO.getAmt()));
        vo.setSerialNumber("");
        vo.setOrderType(convertOperate(accountRenterCostDetailDTO.getPayType()));
        vo.setOperatorTime(LocalDateTimeUtils.formatDateTime(accountRenterCostDetailDTO.getCreateTime()));
        vo.setRecentlyOperatorTime(LocalDateTimeUtils.formatDateTime(accountRenterCostDetailDTO.getUpdateTime()));
        vo.setStauts("已退款");
        vo.setPaymentId("---");
        vo.setPaymentSource(convertSource(accountRenterCostDetailDTO.getPaySource()));
        vo.setResponseCode("---");
        return vo;
    }
    /**
     * 对象转换
     * @param cashierEntity
     * @return
     */
    private PaymentResponseVO convertPaymentFromVirtualPayDetail(AccountVirtualPayDetailDTO accountVirtualPayDetailDTO) {
        PaymentResponseVO vo = new PaymentResponseVO();
        if(accountVirtualPayDetailDTO == null){
            return null;
        }
        vo.setCreateTime(LocalDateTimeUtils.formatDateTime(accountVirtualPayDetailDTO.getCreateTime()));
        vo.setItem(PayCashTypeEnum.fromValue(accountVirtualPayDetailDTO.getPayCashType()).getName());
        vo.setPaymentType(convertType(accountVirtualPayDetailDTO.getPayType()));
        vo.setAmount(String.valueOf(accountVirtualPayDetailDTO.getAmt()));
        vo.setSerialNumber("");
        vo.setOrderType(convertOperate(accountVirtualPayDetailDTO.getPayType()));
        vo.setOperatorTime(LocalDateTimeUtils.formatDateTime(accountVirtualPayDetailDTO.getCreateTime()));
        vo.setRecentlyOperatorTime(LocalDateTimeUtils.formatDateTime(accountVirtualPayDetailDTO.getUpdateTime()));
        vo.setStauts("");
        vo.setPaymentId("---");
        vo.setPaymentSource("");
        vo.setResponseCode("---");
        return vo;
    }
    /**
     * 对象转换
     * @param
     * @return
     */
    private PaymentResponseVO convertPaymentFromCashierRefundApply(CashierRefundApplyDTO cashierRefundApplyDTO) {
        PaymentResponseVO vo = new PaymentResponseVO();
        if(cashierRefundApplyDTO == null){
            return null;
        }
        vo.setCreateTime(LocalDateTimeUtils.formatDateTime(cashierRefundApplyDTO.getCreateTime()));
        vo.setItem(convertItem(cashierRefundApplyDTO.getPayKind()));
        vo.setPaymentType(convertType(cashierRefundApplyDTO.getPayType()));
        vo.setAmount(String.valueOf(cashierRefundApplyDTO.getAmt()));
        vo.setSerialNumber(cashierRefundApplyDTO.getQn());
        vo.setOrderType(convertOperate(cashierRefundApplyDTO.getPayType()));
        vo.setOperatorTime(LocalDateTimeUtils.formatDateTime(cashierRefundApplyDTO.getCreateTime()));
        vo.setRecentlyOperatorTime(LocalDateTimeUtils.formatDateTime(cashierRefundApplyDTO.getUpdateTime()));
        vo.setStauts(convertOfflineStatus(cashierRefundApplyDTO.getStatus()));
        vo.setPaymentId("---");
        vo.setPaymentSource(convertSource(cashierRefundApplyDTO.getPaySource()));
        vo.setResponseCode("---");
        return vo;
    }
    /**
     * 对象转换
     * @param OfflineRefundApplyEntity
     * @return
     */
    private PaymentResponseVO convertPaymentFromOfflineRefundApply(OfflineRefundApplyDTO offlineRefundApplyDTO) {
        PaymentResponseVO vo = new PaymentResponseVO();
        if(offlineRefundApplyDTO == null){
            return null;
        }
        vo.setCreateTime(LocalDateTimeUtils.formatDateTime(offlineRefundApplyDTO.getCreateTime()));
        vo.setItem(convertItem(offlineRefundApplyDTO.getPayKind()));
        vo.setPaymentType(convertType(offlineRefundApplyDTO.getPayType()));
        vo.setAmount(String.valueOf(offlineRefundApplyDTO.getAmt()));
        vo.setSerialNumber(offlineRefundApplyDTO.getQn());
        vo.setOrderType(convertOperate(offlineRefundApplyDTO.getPayType()));
        vo.setOperatorTime(LocalDateTimeUtils.formatDateTime(offlineRefundApplyDTO.getCreateTime()));
        vo.setRecentlyOperatorTime(LocalDateTimeUtils.formatDateTime(offlineRefundApplyDTO.getUpdateTime()));
        vo.setStauts(convertOfflineStatus(offlineRefundApplyDTO.getStatus()));
        vo.setPaymentId("---");
        vo.setPaymentSource(convertSource(offlineRefundApplyDTO.getPaySource()));
        vo.setResponseCode("---");
        return vo;
    }
	/**
	 * 对象转换
	 * @param cashierEntity
	 * @return
	 */
	private PaymentResponseVO convertPaymentResponseVO(CashierResVO cashierEntity,LocalDateTime payTimeLdt) {
		PaymentResponseVO vo = new PaymentResponseVO();
		vo.setCreateTime(LocalDateTimeUtils.formatDateTime(payTimeLdt));
		vo.setItem(convertItem(cashierEntity.getPayKind()));
		vo.setPaymentType(convertType(cashierEntity.getPayType()));
		vo.setAmount(String.valueOf(cashierEntity.getPayAmt()));
		vo.setSerialNumber(cashierEntity.getQn());
		vo.setOrderType(convertOperate(cashierEntity.getPayType()));
		vo.setOperatorTime(LocalDateTimeUtils.formatDateTime(cashierEntity.getCreateTime()));
		vo.setRecentlyOperatorTime(LocalDateTimeUtils.formatDateTime(cashierEntity.getUpdateTime()));
		vo.setStauts(convertStatus(cashierEntity.getTransStatus()));
		vo.setPaymentId("---");
		vo.setPaymentSource(convertSource(cashierEntity.getPaySource()));
		vo.setResponseCode("---");
		return vo;
	}
	
	/**
	 * 支付来源:微信，支付宝
	 * @param paySource
	 * @return
	 */
	private String convertSource(String paySource) {
		//00：钱包 ，01：手机银联 02.:新银联（含银联和applepay统一商户号） 06:支付宝支付， 07:微信支付(App), 3
		//08:快捷支付（快钱） 11.快捷支付（H5）     
		//仅仅是source值不同。 12:Apple Pay 13. 微信支付(公众号) 14.连连支付 15. 微信支付(H5)
		String payName="未知";
		switch (paySource){
			case "00":payName="钱包支付";break;
			case "01":payName="银联";break;
			case "02":payName="线下支付";break;
			case "06":payName="支付宝普通预授权";break;
			case "07": payName="微信APP";break;
			case "08": payName="快钱支付(统一app和H5)";break;
			case "12": payName="Apple Pay";break;
			case "13": payName="微信公众号";break;
			case "14": payName="连连支付(统一app和H5)";break;
			case "15": payName="微信H5";break;
			case "16": payName="支付宝信用预授权免押，芝麻信用";break;
			case "91": payName="虚拟支付";break;
			case "21": payName="银联POS机";break;
			case "22": payName="快钱POS机";break;
			case "23": payName="转账";break;
			case "24": payName="现金";break;
			case "25": payName="银联后台";break;
			case "26": payName="支付宝线下支付";break;
			case "28": payName="押金延续";break;
			case "29": payName="渠道支付";break;
			case "30": payName="线下真实支付";break;
			case "99": payName="默认";break;

			default:payName="未知";
		}


		return payName;
	}
	
	/**
	 * 支付/结算/退款/扣款状态:成功，失败
	 * @param transStatus
	 * @return
	 */
	private String convertStatus(String transStatus) {
		if("00".equals(transStatus)) {
			return "成功";
		}else if("01".equals(transStatus)) {
			return "进行中";
		}else if("03".equals(transStatus)) {
			return "失败";
		}
		return "未知";
	}

    private String convertOfflineStatus(String transStatus) {
        if("00".equals(transStatus)) {
            return "已退款";
        }else if("01".equals(transStatus)) {
            return "待退款";
        }else if("02".equals(transStatus)) {
            return "暂停退款";
        }
        return "未知";
    }

	/**
	 * 操作类型:支付,退款
	 * @param payType
	 * @return
	 */
	private String convertOperate(String payType) {
		if("01".equals(payType) || "02".equals(payType)) {
			return "支付";
		}else{
			return "退款";
		}
		
	}
	
	/**
	 * 支付类型:预授权,消费
	 * @param payType
	 * @return
	 */
	private String convertType(String payType) {
		//支付方式：transType "01"：消费，"02"：预授权， 消费方式："31"：消费撤销，"32"：预授权撤销，"03"：预授权完成，"04"：退货
		if("01".equals(payType)) {
			return "消费";
		}else if("02".equals(payType)) {
			return "预授权";
		}else if("31".equals(payType)) {
			return "消费撤销";
		}else if("32".equals(payType)) {
			return "预授权撤销";
		}else if("03".equals(payType)) {
			return "预授权完成";
		}else if("04".equals(payType)) {
			return "退货";
		}
		
		return "未知";
	}

	/**
	 * 款项:租车费用,车辆押金,违章押金
	 * @param payKind
	 * @return
	 */
	private String convertItem(String payKind) {
		//租车押金:01,违章押金:02,补付租车押金:03,坦客-租车费用:04,坦客-押金费用:05,充值:06,
		//欠款:07,补付租车押金,管理后台v5.11:08, 长租线上费用支付:09,PMS:10,默认:99
		
		if("01".equals(payKind)) {
			return "租车押金";
		}else if("02".equals(payKind)) {
			return "违章押金";
		}else if("03".equals(payKind)) {
			return "APP补付";
		}else if("06".equals(payKind)) {
			return "充值";
		}else if("07".equals(payKind)) {
			return "欠款";
		}else if("08".equals(payKind)) {
			return "线下补付";
		}else if("09".equals(payKind)) {
			return "长租";
		}else if("10".equals(payKind)) {
			return "PMS";
		}else if("11".equals(payKind)) {
			return "租车费用";
		}
		return "未知";
	}


    /*
     * @Author ZhangBin
     * @Date 2020/4/23 19:16
     * @Description: 过滤虚拟支付记录
     *
     **/
    public static AccountVirtualPayDetailDTO filterByPayCashTypeAndPayType(List<AccountVirtualPayDetailDTO> list,
                                                                           PayCashTypeEnum payCashTypeEnum,
                                                                           PayTypeEnum payTypeEnum,
                                                                           LocalDateTime startTime,
                                                                           LocalDateTime endTime){
        Optional<AccountVirtualPayDetailDTO> first = Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x ->payCashTypeEnum.getValue().equals(x.getPayCashType()))
                .filter(x->payTypeEnum.getCode().equals(x.getPayType()))
                .filter(x->startTime==null?true:(x.getCreateTime().isAfter(startTime) || x.getCreateTime().isEqual(startTime)))
                .filter(x->endTime==null?true:x.getCreateTime().isBefore(endTime) || x.getCreateTime().isEqual(endTime))
                .findFirst();
        if(first.isPresent()){
            return first.get();
        }
        return null;
    }

    /*
     * @Author ZhangBin
     * @Date 2020/4/23 19:23
     * @Description: 过滤线下支付
     *
     **/
    public static OfflineRefundApplyDTO filterBySourceCode(List<OfflineRefundApplyDTO> list,
                                                           List<RenterCashCodeEnum> renterCashCodeEnum,
                                                           LocalDateTime startTime,
                                                           LocalDateTime endTime){
        List<String> renterCashCodeEnumList = Optional.ofNullable(renterCashCodeEnum)
                .orElseGet(ArrayList::new).stream()
                .map(x -> x.getCashNo()).collect(Collectors.toList());
        Optional<OfflineRefundApplyDTO> first = Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> renterCashCodeEnumList.contains(x.getSourceCode()))
                .filter(x -> startTime == null ? true : (x.getCreateTime().isAfter(startTime) || x.getCreateTime().isEqual(startTime)))
                .filter(x -> endTime == null ? true : (x.getCreateTime().isBefore(endTime) || x.getCreateTime().isEqual(endTime)))
                .findFirst();
        if(first.isPresent()){
            return first.get();
        }
        return null;
    }

    /*
     * @Author ZhangBin
     * @Date 2020/4/23 19:23
     * @Description: 退款申请表
     *
     **/
    public static CashierRefundApplyDTO filterCashierRefound(List<CashierRefundApplyDTO> list,
                                                             String dataPayKindConstant,
                                                             PayTypeEnum payTypeEnum,
                                                             LocalDateTime startTime,
                                                             LocalDateTime endTime){
        Optional<CashierRefundApplyDTO> first = Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x ->dataPayKindConstant==null?true:dataPayKindConstant.equals(x.getPayKind()))
                .filter(x->payTypeEnum.getCode().equals(x.getPayType()))
                .filter(x->startTime==null?true:(x.getCreateTime().isAfter(startTime) || x.getCreateTime().isEqual(startTime)))
                .filter(x->endTime==null?true:x.getCreateTime().isBefore(endTime) || x.getCreateTime().isEqual(endTime))
                .findFirst();
        if(first.isPresent()){
            return first.get();
        }
        return null;
    }

    /*
     * @Author ZhangBin
     * @Date 2020/4/24 11:46
     * @Description: 过滤租车费用记录
     *
     **/
    public static AccountRenterCostDetailDTO filterRenterCost(List<AccountRenterCostDetailDTO> list,
                                                              PaySourceEnum paySourceEnum,
                                                              PayTypeEnum payTypeEnum,
                                                              LocalDateTime startTime,
                                                              LocalDateTime endTime){
        Optional<AccountRenterCostDetailDTO> first = Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> paySourceEnum.getCode().equals(x.getPaySourceCode()))
                .filter(x -> payTypeEnum.getCode().equals(x.getPayTypeCode()))
                .filter(x -> startTime == null ? true : (x.getCreateTime().isAfter(startTime) || x.getCreateTime().isEqual(startTime)))
                .filter(x -> endTime == null ? true : (x.getCreateTime().isBefore(endTime) || x.getCreateTime().isEqual(endTime)))
                .findFirst();
        if(first.isPresent()){
            return first.get();
        }
        return null;
    }
}
