package com.atzuche.order.admin.service;

import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.admin.AdminSpringBoot;
import com.atzuche.order.admin.vo.req.cost.GetReturnRequestVO;
import com.atzuche.order.admin.vo.resp.cost.GetReturnCostVO;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;


@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=AdminSpringBoot.class)
@WebAppConfiguration
public class GetReturnCarServiceTest {
	@Autowired
	GetReturnCarService getReturnCarService;
	
	@Test
	public void testCalculateGetOrReturnCost() {
		GetReturnRequestVO vo2 = new GetReturnRequestVO();
		vo2.setCityCode("310100");
		vo2.setFlag("1");
		vo2.setGetCarLan("31.239141");
		vo2.setGetCarLon("121.484892");
		vo2.setOrderType("1");
		vo2.setRentTime("2020-03-27 09:00:00");
		vo2.setReturnCarLan("31.112834");
		vo2.setReturnCarLon("121.380857");
		vo2.setRevertTime("2020-03-28 09:00:00");
		vo2.setSceneCode("EX007");
		vo2.setSource("3");
		
		LocalDateTime startTime = null;
		LocalDateTime endTime = null;
		try {
			startTime = LocalDateTimeUtils.parseStringToDateTime(vo2.getRentTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			endTime = LocalDateTimeUtils.parseStringToDateTime(vo2.getRevertTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		GetReturnCostVO vo = getReturnCarService.calculateGetOrReturnCost(vo2, startTime, endTime);
		System.err.println("vo toString="+vo.toString());
	}

}
