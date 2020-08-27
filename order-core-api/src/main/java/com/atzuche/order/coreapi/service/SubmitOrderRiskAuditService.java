package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.coreapi.entity.vo.req.SubmitOrderRiskCheckReqVO;
import com.atzuche.order.coreapi.utils.BizAreaUtil;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.riskCheckService.api.RiskCheckServiceFeignService;
import com.autoyol.riskCheckService.api.VO.CreateOrderRiskCheckReponseVO;
import com.autoyol.riskCheckService.api.VO.CreateOrderRiskCheckRequestVO;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;

/**
 * 风控审核api
 *
 * @author pengcheng.fu
 * @date 2020/1/6 14:32
 */

@Service
public class SubmitOrderRiskAuditService {

    private static Logger logger = LoggerFactory.getLogger(SubmitOrderRiskAuditService.class);


    @Autowired
    private RiskCheckServiceFeignService riskCheckServiceFeignService;

    /**
     * 风控审核
     *
     * @param context 请求参数
     * @return Integer riskAuditId
     */
    public String check(OrderReqContext context) {
    	//提前返回
    	if(context == null) {
    		return "";
    	}
    	SubmitOrderRiskCheckReqVO submitOrderRiskCheckReqVO = buildSubmitOrderRiskCheckReqVO(context.getOrderReqVO(), context.getOrderReqVO().getReqTime(),context.getRenterGoodsDetailDto().getWeekendPrice());
        logger.info("Submit order risk audit check.param is,submitOrderRiskCheckReqVO:[{}]",
                JSON.toJSONString(submitOrderRiskCheckReqVO));
        
        CreateOrderRiskCheckRequestVO req = buildCreateOrderRiskCheckRequestVO(submitOrderRiskCheckReqVO);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "风控服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "riskCheckServiceFeignService.checkWhenCreateOrder");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(req));
            logger.info("Invoke riskCheckServiceFeignService.checkWhenCreateOrder api. param is, req:[{}]", JSON.toJSONString(req));
            ResponseData<CreateOrderRiskCheckReponseVO> responseData =
                    riskCheckServiceFeignService.checkWhenCreateOrder(req);
            logger.info("Invoke riskCheckServiceFeignService.checkWhenCreateOrder api. result is, responseData:[{}]",
                    JSON.toJSONString(responseData));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(responseData));
            
            //封装RiskAuditId  200521
            if(responseData != null && responseData.getData() != null) {
            	context.setRiskAuditId(responseData.getData().getRiskReqId());
            }
            ResponseCheckUtil.checkResponse(responseData);

            t.setStatus(Transaction.SUCCESS);
            return responseData.getData().getRiskReqId();

        } catch (Exception e) {
            logger.error("下单调用风控服务异常.param is, reqVo:[{}]", req, e);
            Cat.logError("下单调用风控服务异常.", e);
            t.setStatus(e);
            throw e;
        } finally {
            t.complete();
        }
    }
    
    
    private SubmitOrderRiskCheckReqVO buildSubmitOrderRiskCheckReqVO(OrderReqVO orderReqVO, LocalDateTime reqTime,Integer weekendPrice) {
		SubmitOrderRiskCheckReqVO submitOrderRiskCheckReqVO = new SubmitOrderRiskCheckReqVO();
		BeanCopier beanCopier = BeanCopier.create(OrderReqVO.class, SubmitOrderRiskCheckReqVO.class, false);
		beanCopier.copy(orderReqVO, submitOrderRiskCheckReqVO, null);
		submitOrderRiskCheckReqVO.setReqTime(LocalDateTimeUtils.localDateTimeToDate(reqTime));
		submitOrderRiskCheckReqVO.setWeekendPrice(weekendPrice);
		return submitOrderRiskCheckReqVO;
	}

    private CreateOrderRiskCheckRequestVO buildCreateOrderRiskCheckRequestVO(SubmitOrderRiskCheckReqVO submitOrderRiskCheckReqVO) {
        CreateOrderRiskCheckRequestVO createOrderRiskCheckRequestVO = new CreateOrderRiskCheckRequestVO();
        createOrderRiskCheckRequestVO.setMemNoRenter(Integer.valueOf(submitOrderRiskCheckReqVO.getMemNo()));
        createOrderRiskCheckRequestVO.setReqOrderTime(submitOrderRiskCheckReqVO.getReqTime());
        createOrderRiskCheckRequestVO.setOrderInsideType(submitOrderRiskCheckReqVO.getOrderCategory());
        createOrderRiskCheckRequestVO.setOrderBusinessParentType(submitOrderRiskCheckReqVO.getBusinessParentType());
        createOrderRiskCheckRequestVO.setOrderBusinessChildType(submitOrderRiskCheckReqVO.getBusinessChildType());
        createOrderRiskCheckRequestVO.setOrderPlatformParentType(submitOrderRiskCheckReqVO.getPlatformParentType());
        createOrderRiskCheckRequestVO.setOrderPlatformChildType(submitOrderRiskCheckReqVO.getPlatformChildType());
        createOrderRiskCheckRequestVO.setMemClientIp(submitOrderRiskCheckReqVO.getSrcIp());
        createOrderRiskCheckRequestVO.setCarNo(Integer.valueOf(submitOrderRiskCheckReqVO.getCarNo()));
        createOrderRiskCheckRequestVO.setCarOwnerType(submitOrderRiskCheckReqVO.getCarOwnerType());
        createOrderRiskCheckRequestVO.setRentTime(LocalDateTimeUtils.localDateTimeToDate(submitOrderRiskCheckReqVO.getRentTime()));
        createOrderRiskCheckRequestVO.setRevertTime(LocalDateTimeUtils.localDateTimeToDate(submitOrderRiskCheckReqVO.getRevertTime()));
        createOrderRiskCheckRequestVO.setGetFlag(submitOrderRiskCheckReqVO.getSrvGetFlag());
        createOrderRiskCheckRequestVO.setReturnFlag(submitOrderRiskCheckReqVO.getSrvReturnFlag());
        createOrderRiskCheckRequestVO.setRenterGetAddr(submitOrderRiskCheckReqVO.getSrvGetAddr());
        createOrderRiskCheckRequestVO.setRenterGetAddrLat(submitOrderRiskCheckReqVO.getSrvGetLat());
        createOrderRiskCheckRequestVO.setRenterGetAddrLon(submitOrderRiskCheckReqVO.getSrvGetLon());
        createOrderRiskCheckRequestVO.setRenterReturnAddr(submitOrderRiskCheckReqVO.getSrvReturnAddr());
        createOrderRiskCheckRequestVO.setRenterReturnAddrLat(submitOrderRiskCheckReqVO.getSrvReturnLat());
        createOrderRiskCheckRequestVO.setRenterReturnAddrLon(submitOrderRiskCheckReqVO.getSrvReturnLon());

        createOrderRiskCheckRequestVO.setRenterGpsAddr(BizAreaUtil.getReqAddrFromLonLat(submitOrderRiskCheckReqVO.getPublicLongitude(), submitOrderRiskCheckReqVO.getPublicLatitude()));
        createOrderRiskCheckRequestVO.setRenterGpsAddrLat(submitOrderRiskCheckReqVO.getPublicLatitude());
        createOrderRiskCheckRequestVO.setRenterGpsAddrLon(submitOrderRiskCheckReqVO.getPublicLongitude());

        createOrderRiskCheckRequestVO.setUseCarCityCode(submitOrderRiskCheckReqVO.getCityCode());
        createOrderRiskCheckRequestVO.setUseCarCityName(submitOrderRiskCheckReqVO.getCityName());
        createOrderRiskCheckRequestVO.setAverageDailyPrice(String.valueOf(submitOrderRiskCheckReqVO.getWeekendPrice()));

        return createOrderRiskCheckRequestVO;
    }

}
