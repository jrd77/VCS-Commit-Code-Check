/**
 * 
 */
package com.atzuche.order.open.service;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.atzuche.order.commons.vo.req.OwnerCostSettleDetailReqVO;
import com.atzuche.order.commons.vo.res.OwnerCostSettleDetailVO;
import com.autoyol.commons.web.ResponseData;

/**
 * @author jing.huang
 *
 */
@FeignClient(url = "http://localhost:1412" ,name="order-center-api")  //本地测试
//@FeignClient(url = "http://10.0.3.235:1412" ,name="order-center-api")
//@FeignClient(name="order-center-api")
public interface FeignOwnerCostFacadeService {
	
	@GetMapping("/order/owner/cost/settle/detail/get")
	public ResponseData<OwnerCostSettleDetailVO> getOwnerCostSettleDetail(@RequestParam("orderNo") String orderNo,@RequestParam("ownerNo") String ownerNo);
	
	@PostMapping("/order/owner/cost/settle/detail/list")
	public ResponseData<List<OwnerCostSettleDetailVO>> listOwnerCostSettleDetail(@RequestBody OwnerCostSettleDetailReqVO req);
	
}
