/**
 * 
 */
package com.atzuche.order.open.service;

import com.atzuche.order.commons.vo.req.OrderCostReqVO;
import com.atzuche.order.commons.vo.res.OrderOwnerCostResVO;
import com.atzuche.order.commons.vo.res.OrderRenterCostResVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author jing.huang
 *
 */
@FeignClient(name="order-center-api")
public interface FeignOrderSettleService {
    /**
     *  车辆押金结算
     * @param orderNo
     * @return
     */
	@GetMapping("/order/settle/depositSettle")
	public ResponseData depositSettle(@RequestParam(value="orderNo",required = true) String orderNo);
    
	/*
	 * @Author ZhangBin
	 * @Date 2020/5/7 14:45
	 * @Description: 手动结算违章押金
	 * 
	 **/
	@GetMapping("/order/settle/settleOrderWz")
    public ResponseData<?> settleOrderWz(@RequestParam("orderNo") String orderNo);
}
