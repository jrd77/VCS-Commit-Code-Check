/**
 * 
 */
package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.rentCost.RenterCostDetailDTO;
import com.atzuche.order.commons.vo.req.AdminOrderReqVO;
import com.atzuche.order.commons.vo.req.NormalOrderCostCalculateReqVO;
import com.atzuche.order.commons.vo.req.OrderCostReqVO;
import com.atzuche.order.commons.vo.res.NormalOrderCostCalculateResVO;
import com.atzuche.order.commons.vo.res.OrderOwnerCostResVO;
import com.atzuche.order.commons.vo.res.OrderRenterCostResVO;
import com.atzuche.order.commons.vo.res.OrderResVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
	@PostMapping("/order/normal/pre/cost/calculate")
	public ResponseData<NormalOrderCostCalculateResVO> submitOrderBeforeCostCalculate(@Valid @RequestBody NormalOrderCostCalculateReqVO reqVO);

	@PostMapping("/order/admin/req")
	public ResponseData<OrderResVO> submitOrder(@RequestBody AdminOrderReqVO adminOrderReqVO);

    @GetMapping("/order/renter/cost/detail")
    public ResponseData<RenterCostDetailDTO> renterCostDetail(@RequestParam("orderNo") String orderNo);

}
