package com.atzuche.order.delivery.service.delivery;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.delivery.common.DeliveryConstants;
import com.atzuche.order.delivery.common.DeliveryErrorCode;
import com.atzuche.order.delivery.config.RestTemplateConfig;
import com.atzuche.order.delivery.entity.DeliveryHttpLogEntity;
import com.atzuche.order.delivery.exception.DeliveryOrderException;
import com.atzuche.order.delivery.utils.DeliveryLogUtil;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;

/**
 * @author 胡春林
 */
@Service
@Slf4j
public class RetryDeliveryCarService {


    @Autowired
    RestTemplateConfig restTemplateConfig;

    @Autowired
    DeliveryLogUtil deliveryLogUtil;

    /**
     * 发送http请求到仁云
     *
     * @return
     */
    @Retryable(value = Exception.class, backoff = @Backoff(delay = 1000L, multiplier = 1))
    public String sendHttpToRenYun(String url, String object, Integer requestCode) {
        ResponseData responseData;
        DeliveryHttpLogEntity deliveryHttpLogEntity = new DeliveryHttpLogEntity();
        deliveryHttpLogEntity.setRequestParams(object);
        deliveryHttpLogEntity.setRequestUrl(url);
        deliveryHttpLogEntity.setRequestCode(requestCode);
        deliveryHttpLogEntity.setCreateTime(LocalDateTime.now());
        deliveryHttpLogEntity.setRequestMethodType(HttpMethod.POST.ordinal());
        deliveryHttpLogEntity.setSendTime(LocalDateTime.now());
        deliveryHttpLogEntity.setUpdateTime(LocalDateTime.now());
        Transaction t = Cat.newTransaction(CatConstants.URL_CALL, "取送车服务通知");
        try {
            Cat.logEvent(CatConstants.URL_PARAM,"url="+url+","+JSON.toJSONString(object));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type",MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            httpHeaders.set("user-agent", DeliveryConstants.USER_AGENT_OPERATECENTER);
            HttpEntity httpEntity = new HttpEntity(httpHeaders);
            log.info("参数params：{}",object);
            String responseStr = restTemplateConfig.restTemplate().postForObject(url+"?"+object, httpEntity, String.class);
            Cat.logEvent(CatConstants.URL_RESULT,responseStr);
            responseData =  JSONObject.parseObject(responseStr,ResponseData.class);
            if (ErrorCode.SUCCESS.getCode().equals(responseData.getResCode())) {
                deliveryHttpLogEntity.setReturnStatusCode(ErrorCode.SUCCESS.getCode());
                deliveryHttpLogEntity.setReturnContext(JSONObject.toJSONString(responseData.getData()));
                deliveryHttpLogEntity.setReturnStatusType("成功");
                deliveryHttpLogEntity.setReturnTime(LocalDateTime.now());
                deliveryLogUtil.addDeliveryLog(deliveryHttpLogEntity);
                log.info("请求仁云成功，各参数返回值,url:{},params:{},result:{}", url, JSON.toJSONString(object), JSON.toJSONString(responseData));
                t.setStatus(Transaction.SUCCESS);
                return JSONObject.toJSONString(responseData);
            } else {
                deliveryHttpLogEntity.setReturnStatusCode(ErrorCode.FAILED.getCode());
                deliveryHttpLogEntity.setReturnContext(JSONObject.toJSONString(responseData.getData()));
                deliveryHttpLogEntity.setReturnStatusType("失败");
                deliveryHttpLogEntity.setReturnTime(LocalDateTime.now());
                deliveryLogUtil.addDeliveryLog(deliveryHttpLogEntity);
                log.info("请求仁云失败，各参数返回值,url:{},params:{},result:{}", url, JSON.toJSONString(object), JSON.toJSONString(responseData));
                throw new DeliveryOrderException(DeliveryErrorCode.SEND_REN_YUN_HTTP_ERROR);
            }
        } catch (Exception e) {
            t.setStatus(e);
            deliveryHttpLogEntity.setReturnStatusCode(ErrorCode.SYS_ERROR.getCode());
            deliveryHttpLogEntity.setReturnContext(e.getMessage());
            deliveryHttpLogEntity.setReturnStatusType("请求仁云出现问题");
            deliveryHttpLogEntity.setReturnTime(LocalDateTime.now());
            deliveryLogUtil.addDeliveryLog(deliveryHttpLogEntity);
            log.error("请求仁云失败，失败原因：case:", e);
            Cat.logError("请求仁云失败，失败原因：case:" + e.getMessage(), e);
            throw e;

        } finally {
            t.complete();
        }
    }
}
