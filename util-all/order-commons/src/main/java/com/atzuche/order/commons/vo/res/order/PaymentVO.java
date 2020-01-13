package com.atzuche.order.commons.vo.res.order;

import com.autoyol.doc.annotation.AutoDocProperty;

import java.io.Serializable;

/**
 * 待支付费用展示区
 * @author pengcheng.fu
 * @date 2020/1/11 15:16
 */
public class PaymentVO implements Serializable {

    private static final long serialVersionUID = 1499927626886025113L;

    @AutoDocProperty(value = "待支付金额,如:500")
	private String paymentAmt;



}
