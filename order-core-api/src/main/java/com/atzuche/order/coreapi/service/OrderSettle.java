package com.atzuche.order.coreapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.settle.service.OrderSettleService;
import com.atzuche.order.settle.service.OrderWzSettleService;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.search.api.OrderSearchService;
import com.autoyol.search.entity.ResponseData;
import com.autoyol.search.entity.ViolateBO;
import com.autoyol.search.vo.OrderVO;
import com.autoyol.search.vo.ViolateVO;

import lombok.extern.slf4j.Slf4j;

/***
 * 处理相互依赖问题
 */
@Slf4j
@Service
public class OrderSettle {
    @Autowired OrderSettleService orderSettleService;
    @Autowired 
    PayCallbackService payCallbackService;
    
    @Autowired
    OrderWzSettleService orderWzSettleService;
    
	@Autowired
	OrderSearchService orderSearchService;
	
    /**
     * order-core-api 入口 车辆押金结算
     * @param orderNo
     */
    public void settleOrder(String orderNo){
        orderSettleService.settleOrder(orderNo,payCallbackService);
    }

    public void settleWzOrder(String orderNo){
    	orderWzSettleService.settleWzOrder(orderNo);
    }
    
    public void testWzEs(Integer minDay) {
    	ViolateVO reqVO = new ViolateVO();
        reqVO.setPageNum(1);
        reqVO.setPageSize(10000);
        reqVO.setType("10");
        reqVO.setDate(DateUtils.minDays(minDay));
        
		ResponseData<OrderVO<ViolateBO>> orderResponseData = orderSearchService.violateProcessOrder(reqVO);
        List<ViolateBO> orderList = orderResponseData.getData().getOrderList();
        for (ViolateBO violateBO : orderList) {
			log.info("violateBO="+GsonUtils.toJson(violateBO));
		}
        
    }
    
}
