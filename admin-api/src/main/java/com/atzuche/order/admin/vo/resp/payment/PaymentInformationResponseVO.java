package com.atzuche.order.admin.vo.resp.payment;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;


@Data
@ToString
public class PaymentInformationResponseVO implements Serializable{
	@AutoDocProperty(value="租车费用/车辆押金结算前,支付信息列表")
	private List<PaymentResponseVO> beforeDepositSettlementPaymentList;

	@AutoDocProperty(value="租车费用/车辆押金结算时/后,支付信息列表")
	private List<PaymentResponseVO> afterDepositSettlementPaymentList;

	@AutoDocProperty(value="违章押金结算时/后,支付信息列表")
	private List<PaymentResponseVO> violationDepositSettlementPaymentList;



}
