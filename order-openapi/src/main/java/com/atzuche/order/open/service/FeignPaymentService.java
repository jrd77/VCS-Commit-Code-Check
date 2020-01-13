/**
 * 
 */
package com.atzuche.order.open.service;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.atzuche.order.commons.vo.req.PaymentVo;
import com.atzuche.order.commons.vo.res.CashierResVo;
import com.autoyol.commons.web.ResponseData;

/**
 * @author jing.huang
 *
 */
//@FeignClient(url = "http://localhost:7777" ,name="order-center-api")  //本地测试
@FeignClient(url = "http://10.0.3.235:1412" ,name="order-center-api")
public interface FeignPaymentService {
	
	 @PostMapping("/order/payment/queryByOrderNo")
	 public ResponseData<List<CashierResVo>> queryByOrderNo(@RequestBody PaymentVo vo);
	 
}
