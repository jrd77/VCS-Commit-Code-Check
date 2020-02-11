/**
 * 车辆押金结算 admin api 调用
 */
package com.atzuche.order.coreapi.controller;

import com.atzuche.order.cashieraccount.service.CashierQueryService;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.commons.vo.req.OrderCostReqVO;
import com.atzuche.order.commons.vo.res.CashierResVO;
import com.atzuche.order.commons.vo.res.OrderOwnerCostResVO;
import com.atzuche.order.commons.vo.res.OrderRenterCostResVO;
import com.atzuche.order.commons.vo.res.RenterCostDetailVO;
import com.atzuche.order.coreapi.service.OrderCostService;
import com.atzuche.order.coreapi.service.OrderSettle;
import com.atzuche.order.open.vo.RenterCostShortDetailVO;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.rentercost.service.RenterCostFacadeService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
