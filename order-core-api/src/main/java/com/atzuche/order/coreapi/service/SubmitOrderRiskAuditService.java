package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.coreapi.entity.vo.req.SubmitOrderRiskCheckReqVO;
import com.atzuche.order.coreapi.submitOrder.exception.SubmitOrderException;
import com.atzuche.order.coreapi.utils.BizAreaUtil;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.riskCheckService.api.RiskCheckServiceFeignService;
import com.autoyol.riskCheckService.api.VO.CreateOrderRiskCheckReponseVO;
import com.autoyol.riskCheckService.api.VO.CreateOrderRiskCheckRequestVO;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
     * @param submitOrderRiskCheckReqVO 请求参数
     * @return Integer riskAuditId
     */
    public Integer check(SubmitOrderRiskCheckReqVO submitOrderRiskCheckReqVO) {
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

            if (null == responseData || !StringUtils.equals(responseData.getResCode(), ErrorCode.SUCCESS.getCode())) {
                throw new SubmitOrderException(responseData.getResCode(), responseData.getResMsg(), responseData.getData());
            } else {
                t.setStatus(Transaction.SUCCESS);
                return null == responseData.getData() ? null : Integer.valueOf(responseData.getData().toString());
            }
        } catch (SubmitOrderException soe) {
            logger.error("下单调用风控服务审核不通过.param is, reqVo:[{}]", req, soe);
            t.setStatus(soe);
            Cat.logError("下单调用风控服务审核不通过.", soe);
        } catch (Exception e) {
            logger.error("下单调用风控服务异常.param is, reqVo:[{}]", req, e);
            t.setStatus(e);
            Cat.logError("下单调用风控服务异常.", e);
        } finally {
            t.complete();
        }
        return null;
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

        createOrderRiskCheckRequestVO.setRenterGpsAddr(BizAreaUtil.getReqAddrFromLonLat(submitOrderRiskCheckReqVO.getPublicLatitude(), submitOrderRiskCheckReqVO.getPublicLongitude()));
        createOrderRiskCheckRequestVO.setRenterGpsAddrLat(submitOrderRiskCheckReqVO.getPublicLatitude());
        createOrderRiskCheckRequestVO.setRenterGpsAddrLon(submitOrderRiskCheckReqVO.getPublicLongitude());

        createOrderRiskCheckRequestVO.setUseCarCityCode(submitOrderRiskCheckReqVO.getCityCode());
        createOrderRiskCheckRequestVO.setUseCarCityName(submitOrderRiskCheckReqVO.getCityName());
        createOrderRiskCheckRequestVO.setAverageDailyPrice(String.valueOf(submitOrderRiskCheckReqVO.getWeekendPrice()));

        return createOrderRiskCheckRequestVO;
    }

}
