/**
 * 
 */
package com.atzuche.order.admin.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.atzuche.order.admin.service.OrderCostService;
import com.atzuche.order.admin.vo.req.cost.OwnerCostReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterCostReqVO;
import com.atzuche.order.admin.vo.resp.order.cost.OrderOwnerCostResVO;
import com.atzuche.order.admin.vo.resp.order.cost.OrderRenterCostResVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.dianping.cat.Cat;

/**
 * @author jing.huang
 * 订单详细信息  租车费用  车主费用
 */
@RequestMapping("/console/ordercost/")
@RestController
@AutoDocVersion(version = "订单详细信息  租车费用  车主费用接口文档")
public class AdminOrderCostController {
	private static final Logger logger = LoggerFactory.getLogger(AdminOrderCostController.class);
	
	@Autowired
	OrderCostService orderCostService;
	
	@AutoDocMethod(description = "计算租客子订单费用", value = "计算租客子订单费用", response = OrderRenterCostResVO.class)
	@RequestMapping(value="calculateRenterOrderCost",method = RequestMethod.POST)
	public ResponseData calculateRenterOrderCost(@RequestBody RenterCostReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
        logger.info("calculateRenterOrderCost controller params={}",renterCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	
        	OrderRenterCostResVO resp = orderCostService.calculateRenterOrderCost(renterCostReqVO);
        	return ResponseData.success(resp);
		} catch (Exception e) {
			Cat.logError("calculateRenterOrderCost exception params="+renterCostReqVO.toString(),e);
			logger.error("calculateRenterOrderCost exception params="+renterCostReqVO.toString(),e);
			return ResponseData.error();
		}
		
	}
	
	
	@AutoDocMethod(description = "计算车主子订单费用", value = "计算车主子订单费用", response = OrderOwnerCostResVO.class)
	@RequestMapping(value="calculateOwnerOrderCost",method = RequestMethod.POST)
	public ResponseData calculateOwnerOrderCost(@RequestBody OwnerCostReqVO ownerCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
        logger.info("calculateOwnerOrderCost controller params={}",ownerCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	
        	OrderOwnerCostResVO resp = orderCostService.calculateOwnerOrderCost(ownerCostReqVO);
        	logger.info("resp = " + resp.toString());
        	return ResponseData.success(resp);
		} catch (Exception e) {
			Cat.logError("calculateOwnerOrderCost exception params="+ownerCostReqVO.toString(),e);
			logger.error("calculateOwnerOrderCost exception params="+ownerCostReqVO.toString(),e);
			return ResponseData.error();
		}
		
	}
	
	
}
