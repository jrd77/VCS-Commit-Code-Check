package com.atzuche.order.admin.vo.resp.payment;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;


@Data
@ToString
public class PaymentResponseVO implements Serializable{
	@AutoDocProperty(value="序号")
	private String number;

	@AutoDocProperty(value="创建时间yyyy-mm-dd HH:mm:ss")
	private String createTime;

	@AutoDocProperty(value="款项:租车费用,车辆押金,违章押金")
	private String item;

	@AutoDocProperty(value="支付类型:预授权,消费")
	private String paymentType;

	@AutoDocProperty(value="金额")
	private String amount ;

	@AutoDocProperty(value="交易流水号")
	private String serialNumber;

	@AutoDocProperty(value="操作类型:支付,退款")
	private String orderType;

	@AutoDocProperty(value="操作时间yyyy-mm-dd HH:mm:ss")
	private String operatorTime;

	@AutoDocProperty(value="最新操作时间yyyy-mm-dd HH:mm:ss")
	private String recentlyOperatorTime;

	@AutoDocProperty(value="支付/结算/退款/扣款状态:成功，失败")
	private String stauts;

	@AutoDocProperty(value="支付ID")
	private String paymentId;

	@AutoDocProperty(value="支付来源:微信，支付宝")
	private String paymentSource;

	@AutoDocProperty(value="银联响应码/响应信息")
	private String responseCode;


}
