/**
 * 
 */
package com.atzuche.order.open.service;

import com.atzuche.order.commons.vo.req.PaymentReqVO;
import com.atzuche.order.commons.vo.res.PaymentRespVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author jing.huang
 *
 */
@FeignClient(name="order-center-api")
public interface FeignPaymentService {
	
	 @PostMapping("/order/payment/queryByOrderNo")
	 ResponseData<PaymentRespVO> queryByOrderNo(@RequestBody PaymentReqVO vo);
	 
}
