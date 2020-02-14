/**
 * 
 */
package com.atzuche.order.open.service;

import com.atzuche.order.commons.vo.req.NormalOrderCostCalculateReqVO;
import com.atzuche.order.commons.vo.res.NormalOrderCostCalculateResVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.atzuche.order.commons.vo.req.OrderCostReqVO;
import com.atzuche.order.commons.vo.res.OrderOwnerCostResVO;
import com.atzuche.order.commons.vo.res.OrderRenterCostResVO;
import com.autoyol.commons.web.ResponseData;

import javax.validation.Valid;

/**
 * @author jing.huang
 *
 */
//@FeignClient(url = "http://localhost:1412" ,name="order-center-api")  //本地测试
//@FeignClient(url = "http://10.0.3.235:1412" ,name="order-center-api")
@FeignClient(name="order-center-api")
public interface FeignOrderCostService {
	
	@PostMapping("/order/cost/renter/get")
	public ResponseData<OrderRenterCostResVO> orderCostRenterGet(@RequestBody OrderCostReqVO req);
	
	@PostMapping("/order/cost/owner/get")
	public ResponseData<OrderOwnerCostResVO> orderCostOwnerGet(@RequestBody OrderCostReqVO req);

	/**
	 * 下单前费用计算
	 * @param reqVO
	 * @return
	 */
	@PostMapping("/normal/pre/cost/calculate")
	public ResponseData<NormalOrderCostCalculateResVO> submitOrderBeforeCostCalculate(@Valid @RequestBody NormalOrderCostCalculateReqVO reqVO);
	
}
