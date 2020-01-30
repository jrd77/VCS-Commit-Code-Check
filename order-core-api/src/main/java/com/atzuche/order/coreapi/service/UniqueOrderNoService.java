package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.commons.ResponseCheckUtil;
import com.atzuche.order.coreapi.submitOrder.exception.UniqueOrderNoException;
import com.autoyol.api.UniqueNoFeignClient;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.enums.OrderNoTypeEnum;
import com.autoyol.enums.UniqueNoTypeEnum;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * 订单号获取
 *
 * @author pengcheng.fu
 * @date 2019/12/24 14:12
 */
@Service
public class UniqueOrderNoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UniqueOrderNoService.class);


    @Resource
    private UniqueNoFeignClient uniqueNoFeignClient;


    /**
     * 获取主订单编码
     *
     * @return String
     */
    String getOrderNo() {
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单号创建服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_PARAM, "ORDER_CENTER_RENTER_ORDER_NO");
            Cat.logEvent(CatConstants.FEIGN_METHOD,"uniqueNoFeignClient.getUniqueNo");
            ResponseData<Object> response = uniqueNoFeignClient.getUniqueNo(UniqueNoTypeEnum.ORDER_CENTER_RENTER_ORDER_NO);
            LOGGER.info("Invoke UniqueOrderNoService.getOrderNo. result is,response:[{}]", JSON.toJSONString(response));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(response));
            ResponseCheckUtil.checkResponse(response);
            t.setStatus(Transaction.SUCCESS);
            return String.valueOf(response.getData());
        } catch (Exception e) {
            LOGGER.error("Feign 获取主订单号失败", e);
            t.setStatus(e);
            Cat.logError("Feign 获取主订单号失败", e);
            throw e;
        } finally {
            t.complete();
        }
    }

    /**
     * 获取租客订单编码
     *
     * @param orderNo 主订单编码
     * @return String 租客订单编码
     */
    String getRenterOrderNo(String orderNo) {
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单号创建服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_PARAM, "orderNo=" + orderNo + "&orderNoType=RENTER_NO");
            Cat.logEvent(CatConstants.FEIGN_METHOD,"uniqueNoFeignClient.getChildOrderNoByOrderNo");
            ResponseData<Object> response = uniqueNoFeignClient.getChildOrderNoByOrderNo(orderNo, OrderNoTypeEnum.RENTER_NO);
            LOGGER.info("Invoke UniqueOrderNoService.getRenterOrderNo. result is,response:[{}]", JSON.toJSONString(response));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(response));
            ResponseCheckUtil.checkResponse(response);
            t.setStatus(Transaction.SUCCESS);
            return String.valueOf(response.getData());
        } catch (Exception e) {
            LOGGER.error("Feign 获取租客订单编码失败,order:[{}]", orderNo, e);
            t.setStatus(e);
            Cat.logError("Feign 获取租客订单编码失败", e);
            throw e;
        } finally {
            t.complete();
        }
    }

    /**
     * 获取车主订单编码
     *
     * @param orderNo 主订单编码
     * @return String 车主订单编码
     */
    String getOwnerOrderNo(String orderNo) {
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "订单号创建服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_PARAM, "orderNo=" + orderNo + "&orderNoType=OWNER_NO");
            Cat.logEvent(CatConstants.FEIGN_METHOD,"uniqueNoFeignClient.getChildOrderNoByOrderNo");
            ResponseData<Object> response = uniqueNoFeignClient.getChildOrderNoByOrderNo(orderNo, OrderNoTypeEnum.OWNER_NO);
            LOGGER.info("Invoke UniqueOrderNoService.getOwnerOrderNo. result is,response:[{}]", JSON.toJSONString(response));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(response));
            ResponseCheckUtil.checkResponse(response);
            t.setStatus(Transaction.SUCCESS);
            return String.valueOf(response.getData());
        } catch (Exception e) {
            LOGGER.error("Feign 获取车主订单编码失败,order:[{}]", orderNo, e);
            t.setStatus(e);
            Cat.logError("Feign 获取车主订单编码失败", e);
            throw e;
        } finally {
            t.complete();
        }
    }


}
