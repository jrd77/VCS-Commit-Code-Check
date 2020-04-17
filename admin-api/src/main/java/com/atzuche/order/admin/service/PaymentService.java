/**
 * 
 */
package com.atzuche.order.admin.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.admin.vo.req.payment.PaymentRequestVO;
import com.atzuche.order.admin.vo.resp.payment.PaymentInformationResponseVO;
import com.atzuche.order.admin.vo.resp.payment.PaymentResponseVO;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.vo.req.PaymentReqVO;
import com.atzuche.order.commons.vo.res.CashierResVO;
import com.atzuche.order.open.service.FeignPaymentService;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.autoyol.commons.utils.StringUtils;
import com.autoyol.commons.web.ResponseData;

/**
 * @author jing.huang
 *
 */
@Service
public class PaymentService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	OrderStatusService orderStatusService;
	@Autowired
	FeignPaymentService feignPaymentService;
	
//	@Autowired
//	PaymentCashierService paymentCashierService;
	
	public PaymentInformationResponseVO platformPaymentList(PaymentRequestVO paymentRequestVO) {
		String orderNo = paymentRequestVO.getOrderNo();
		PaymentInformationResponseVO respVo = new PaymentInformationResponseVO();
		
		/**
		 * 租车费用/车辆押金结算前,支付信息列表
		 */
		List<PaymentResponseVO> beforeDepositSettlementPaymentList = new ArrayList<PaymentResponseVO>();

		/**
		 * 租车费用/车辆押金结算时/后,支付信息列表
		 */
		List<PaymentResponseVO> afterDepositSettlementPaymentList = new ArrayList<PaymentResponseVO>();

		/**
		 * 违章押金结算时/后,支付信息列表
		 */
		List<PaymentResponseVO> violationDepositSettlementPaymentList = new ArrayList<PaymentResponseVO>();
		
		//根据结算时间来切分
//		logger.info("orderStatus toString={}",orderStatusService.toString());
		OrderStatusEntity orderStatus = orderStatusService.getByOrderNo(orderNo);
		//非空处理
//		if(orderStatus == null) {
//			return null;
//		}
		
//		logger.info("orderStatus toString={}",orderStatus.toString());
		/**
		 * 违章结算时间
		 */
		LocalDateTime wzSettleTime = null;
		/**
		 * 车辆押金结算时间，租车费用和车辆押金是一起结算的。
		 */
//		LocalDateTime carDepositSettleTime = null;
		/**
		 * 租车费用结算时间
		 */
		LocalDateTime settleTime = null;
		LocalDateTime depositSettleTime = null;
		//租车费用结算状态:0,否 1,是 
		if(orderStatus != null && orderStatus.getSettleStatus().intValue() != 0) {
			settleTime = orderStatus.getSettleTime();
		}

		if(orderStatus != null && orderStatus.getCarDepositSettleStatus().intValue() != 0){
            depositSettleTime = orderStatus.getCarDepositSettleTime();
        }
//		if(orderStatus.getCarDepositSettleStatus().intValue() == 1) {  //车辆押金结算状态:0,否 1,是
//			carDepositSettleTime = orderStatus.getCarDepositSettleTime();
//		}
		
		if(orderStatus != null && orderStatus.getWzSettleStatus().intValue() != 0) {  //违章结算状态:0,否 1,是
			wzSettleTime = orderStatus.getWzSettleTime();
		}
		
//		RestTemplate restTemplate = new RestTemplate();
		PaymentReqVO vo2 = new PaymentReqVO();
		vo2.setOrderNo(orderNo);	    
//		String result2 = restTemplate.postForObject(url, vo, String.class);
		
//		logger.info("feignPaymentService toString={}",feignPaymentService.toString());
		ResponseData<List<CashierResVO>> resData = feignPaymentService.queryByOrderNo(vo2); //paymentCashierService.queryPaymentList(orderNo);
		
		/**
		 * 不等于空
		 */
		if(resData != null) {
			List<CashierResVO> lst = resData.getData();
			for (CashierResVO cashierEntity : lst) {
				String payTime = cashierEntity.getPayTime();
				if(StringUtils.isBlank(payTime)) {
					LocalDateTime payTimeLdt = LocalDateTimeUtils.dateToLocalDateTime(new Date());
					//中间段的。
//					PaymentResponseVO vo = convertPaymentResponseVO(cashierEntity,payTimeLdt);
//					afterDepositSettlementPaymentList.add(vo);
					PaymentResponseVO vo = convertPaymentResponseVO(cashierEntity,payTimeLdt);
					beforeDepositSettlementPaymentList.add(vo);
				}else {
					/**
					 * 根据支付时间来切换
					 */
					LocalDateTime payTimeLdt = LocalDateTimeUtils.parseStringToDateTime(payTime, "yyyyMMddHHmmss");
					//未结算
					if(settleTime == null) {
						PaymentResponseVO vo = convertPaymentResponseVO(cashierEntity,payTimeLdt);
						beforeDepositSettlementPaymentList.add(vo);
					}else {
					    if(wzSettleTime != null){
                            PaymentResponseVO vo = convertPaymentResponseVO(cashierEntity,payTimeLdt);
                            violationDepositSettlementPaymentList.add(vo);
                        }else if(settleTime != null || depositSettleTime != null){
                            PaymentResponseVO vo = convertPaymentResponseVO(cashierEntity,payTimeLdt);
                            afterDepositSettlementPaymentList.add(vo);
                        }else{
                            PaymentResponseVO vo = convertPaymentResponseVO(cashierEntity,payTimeLdt);
                            beforeDepositSettlementPaymentList.add(vo);
                        }
						/*if(settleTime != null &&  payTimeLdt.isBefore(settleTime)) {
							PaymentResponseVO vo = convertPaymentResponseVO(cashierEntity,payTimeLdt);
							beforeDepositSettlementPaymentList.add(vo);
						}else if(wzSettleTime != null && payTimeLdt.isAfter(wzSettleTime)) {
							PaymentResponseVO vo = convertPaymentResponseVO(cashierEntity,payTimeLdt);
							violationDepositSettlementPaymentList.add(vo);
						}else {
							//中间段的。
							PaymentResponseVO vo = convertPaymentResponseVO(cashierEntity,payTimeLdt);
							afterDepositSettlementPaymentList.add(vo);
						}*/
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
	
	
}
