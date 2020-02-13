/**
 * 车辆押金结算 admin api 调用
 */
package com.atzuche.order.coreapi.controller;

import com.atzuche.order.coreapi.service.OrderSettle;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author haibao.yan
 *
 */
@RestController
@Slf4j
@RequestMapping("/order/settle")
public class OrderSettleController {
	@Autowired
	private OrderSettle orderSettle;

    @AutoDocMethod(description = "车辆押金结算", value = "车辆押金结算")
	@GetMapping("/depositSettle")
	public ResponseData getRenterCostFullDetail(@RequestParam("orderNo") String orderNo){
        log.info("OrderSettleController depositSettle start param [{}]", orderNo);
        orderSettle.settleOrder(orderNo);
        log.info("OrderSettleController depositSettle end ");
        return ResponseData.success();
	}

}
