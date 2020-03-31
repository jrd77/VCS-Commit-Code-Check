package com.atzuche.order.cashieraccount.vo.res.pay;

import java.util.List;

import com.atzuche.order.commons.enums.YesNoEnum;
import com.autoyol.autopay.gateway.vo.req.NotifyDataVo;

import lombok.Data;

/**
 * 支付系统回调
 */
@Data
public class OrderPayCallBackSuccessVO {
	/**
	 * supplement 及debt_detail id字段，更新
	 */
	private List<NotifyDataVo> supplementIds;
	
	private List<NotifyDataVo> debtIds;
	
	private List<String> rentAmountAfterRenterOrderNos;
	
    /**
     * memNo
     */
    private String memNo;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 子订单号 补付支付 对应的 租客子订单
     */
    private String renterOrderNo;

    /**
     * 租车费用支付状态（待支付、已支付）
     */
    private Integer rentCarPayStatus;

    /**
     * 车辆押金支付状态
     */
    private Integer depositPayStatus;
    /**
     * 违章押金支付状态
     */
    private Integer wzPayStatus;

    /**
     * 租车费用是否 补付 1 是 0 否
     */
    private Integer isPayAgain;
    /**
     * 是否发送 订单状态MQ 1 是 0 否
     * 租车费用  押金 违章押金  都是支付
     */
    private YesNoEnum isGetCar;
    
    //退款状态变更 200304  
    /**
     * 租车费用支付状态（待支付、已支付）
     */
    private Integer rentCarRefundStatus;

    /**
     * 车辆押金支付状态
     */
    private Integer depositRefundStatus;
    /**
     * 违章押金支付状态
     */
    private Integer wzRefundStatus;
    
}
