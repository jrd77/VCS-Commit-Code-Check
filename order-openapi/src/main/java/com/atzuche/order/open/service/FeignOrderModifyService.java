/**
 * 
 */
package com.atzuche.order.open.service;

import com.atzuche.order.commons.vo.req.ModifyApplyHandleReq;
import com.atzuche.order.commons.vo.req.ModifyOrderMainQueryReqVO;
import com.atzuche.order.commons.vo.req.ModifyOrderQueryReqVO;
import com.atzuche.order.commons.vo.req.ModifyOrderReqVO;
import com.atzuche.order.commons.vo.res.ModifyOrderMainResVO;
import com.atzuche.order.commons.vo.res.ModifyOrderResVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author jing.huang
 *
 */
//@FeignClient(url = "http://localhost:1412" ,name="order-center-api")  //本地测试
@FeignClient(url = "http://10.0.3.235:1412" ,name="order-center-api")
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


    @PostMapping("/order/modifyconsole")
    ResponseData<?> modifyOrderForConsole(@Valid @RequestBody ModifyOrderReqVO modifyOrderReqVO);

	@PostMapping("/order/modifyconfirm")
	public ResponseData<?> ownerHandleModifyApplication(@Valid @RequestBody ModifyApplyHandleReq req);
}
