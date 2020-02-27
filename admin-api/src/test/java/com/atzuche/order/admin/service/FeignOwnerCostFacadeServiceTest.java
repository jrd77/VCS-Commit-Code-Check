/**
 * 
 */
package com.atzuche.order.admin.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.atzuche.order.admin.AdminSpringBoot;
import com.atzuche.order.commons.vo.req.OwnerCostSettleDetailDataVO;
import com.atzuche.order.commons.vo.req.OwnerCostSettleDetailReqVO;
import com.atzuche.order.commons.vo.res.OwnerCostSettleDetailVO;
import com.atzuche.order.open.service.FeignOwnerCostFacadeService;
import com.autoyol.commons.web.ResponseData;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jing.huang
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest(classes=AdminSpringBoot.class)
@WebAppConfiguration
@Slf4j
public class FeignOwnerCostFacadeServiceTest {
	@Autowired
	FeignOwnerCostFacadeService feignOwnerCostFacadeService;
	
	@Test
	public void testSingle() {
		ResponseData<OwnerCostSettleDetailVO> data = feignOwnerCostFacadeService.getOwnerCostSettleDetail("29949381200299", "345491001");
		if(data != null) {
			OwnerCostSettleDetailVO vo = data.getData();
			log.info("print single vo toString=" + vo.toString());
		}
	}
	
	@Test
	public void testBatch() {
		OwnerCostSettleDetailReqVO req = new OwnerCostSettleDetailReqVO();
		List<OwnerCostSettleDetailDataVO> listOwnerCostSettleDetailDataVO = Lists.newArrayList();
		OwnerCostSettleDetailDataVO vo = new OwnerCostSettleDetailDataVO("84495281200299","537383084");
		
		OwnerCostSettleDetailDataVO vo2 = new OwnerCostSettleDetailDataVO("79614302200299","318071581");
		
		OwnerCostSettleDetailDataVO vo3 = new OwnerCostSettleDetailDataVO("74273242200299","345491001");
		listOwnerCostSettleDetailDataVO.add(vo);
		listOwnerCostSettleDetailDataVO.add(vo2);
		listOwnerCostSettleDetailDataVO.add(vo3);
		//封装参数
		req.setListOwnerCostSettleDetailDataVO(listOwnerCostSettleDetailDataVO);
		
		
		
		ResponseData<List<OwnerCostSettleDetailVO>> data = feignOwnerCostFacadeService.listOwnerCostSettleDetail(req);
		if(data != null) {
			List<OwnerCostSettleDetailVO> list = data.getData();
			int i = 1;
			for (OwnerCostSettleDetailVO tmp : list) {
				log.info("print batch 序号:" + (i++) + "- tmp toString=" + tmp.toString());
			}
			
		}
	}
}
