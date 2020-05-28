package com.atzuche.order.delivery.vo.delivery;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ChangeOrderInfoDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3317104467263364694L;
	
	/**
	 * 订单号
	 */
	private String orderNo;

	/**
     * 超级补充全
     **/
    private String ssaRisks;
    /**
     * 平台保障服务是否购买
     */
    private String baseInsurFlag;
    /**
     * 附加驾驶人保障服务是否购买
     */
    private String extraDriverFlag;
    /**
     * 轮胎保障服务是否购买
     */
    private String tyreInsurFlag;
    /**
     * 驾乘无忧保障服务是否购买
     */
    private String driverInsurFlag;
	
}