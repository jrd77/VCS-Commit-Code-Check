/**
 * 
 */
package com.atzuche.order.open.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.atzuche.order.commons.vo.req.ModifyOrderMainQueryReqVO;
import com.atzuche.order.commons.vo.req.ModifyOrderQueryReqVO;
import com.atzuche.order.commons.vo.res.ModifyOrderMainResVO;
import com.atzuche.order.commons.vo.res.ModifyOrderResVO;
import com.autoyol.commons.web.ResponseData;

/**
 * @author jing.huang
 *
 */
@FeignClient(url = "http://localhost:7777" ,name="order-center-api")  //本地测试
//@FeignClient(url = "http://10.0.3.235:1412" ,name="order-center-api")
public interface FeignOrderModifyService {
	
	/**
	 * 查询列表
	 * @param req
	 * @return
	 */
	@PostMapping("/order/modify/query")
	public ResponseData<ModifyOrderResVO> queryModifyOrderList(@RequestBody ModifyOrderQueryReqVO req);
	
	@PostMapping("/order/modify/get")
	public ResponseData<ModifyOrderMainResVO> getModifyOrderMain(@RequestBody ModifyOrderMainQueryReqVO req);

}
