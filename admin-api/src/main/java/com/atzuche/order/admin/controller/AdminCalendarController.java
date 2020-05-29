package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.req.calendar.CalendarRequestVO;
import com.atzuche.order.admin.vo.resp.calendar.CalendarListResponseVO;
import com.atzuche.order.commons.BindingResultUtil;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/console/order/")
@RestController
@AutoDocVersion(version = "订单接口文档")
public class AdminCalendarController {

    private static final Logger logger = LoggerFactory.getLogger(AdminCalendarController.class);


	@AutoDocMethod(description = "获取车辆价格日历", value = "获取车辆价格日历", response = CalendarListResponseVO.class)
	@GetMapping("car/calendar/price")
	public ResponseData carCalendarPrice(CalendarRequestVO calendarRequestVO, BindingResult bindingResult) {
        BindingResultUtil.checkBindingResult(bindingResult);
        //TODO:
		return ResponseData.success(null);
	}




}
