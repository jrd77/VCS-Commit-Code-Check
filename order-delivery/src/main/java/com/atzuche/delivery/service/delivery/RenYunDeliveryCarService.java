package com.atzuche.delivery.service.delivery;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atzuche.delivery.common.DeliveryConstants;
import com.atzuche.delivery.config.RestTemplateConfig;
import com.atzuche.delivery.entity.DeliveryHttpLogEntity;
import com.atzuche.delivery.enums.DeliveryTypeEnum;
import com.atzuche.delivery.utils.DeliveryLogUtil;
import com.atzuche.delivery.vo.delivery.CancelFlowOrderDTO;
import com.atzuche.delivery.vo.delivery.RenYunFlowOrderDTO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 胡春林
 * 发送请求到仁云
 */
@Service
@EnableRetry
@Slf4j
public class RenYunDeliveryCarService {

    @Autowired
    RestTemplateConfig restTemplateConfig;

    @Autowired
    DeliveryLogUtil deliveryLogUtil;

    /**
     * 添加订单到仁云流程系统
     */
    @Retryable(value = Exception.class, maxAttempts = DeliveryConstants.REN_YUN_HTTP_RETRY_TIMES, backoff = @Backoff(delay = 1000L, multiplier = 1))
    public String addRenYunFlowOrderInfo(RenYunFlowOrderDTO renYunFlowOrderVO) {
        String result = sendHttpToRenYun(DeliveryConstants.ADD_FLOW_ORDER,renYunFlowOrderVO);
        return result;
    }

    /**
     * 更新订单到仁云流程系统
     */
    @Retryable(value = Exception.class, maxAttempts = DeliveryConstants.REN_YUN_HTTP_RETRY_TIMES, backoff = @Backoff(delay = 1000L, multiplier = 1))
    public String updateRenYunFlowOrderInfo(RenYunFlowOrderDTO renYunFlowOrderVO) {
        String result = sendHttpToRenYun(DeliveryConstants.CHANGE_FLOW_ORDER,renYunFlowOrderVO);
        return result;
    }

    /**
     * 取消订单到仁云流程系统
     */
    @Retryable(value = Exception.class, maxAttempts = DeliveryConstants.REN_YUN_HTTP_RETRY_TIMES, backoff = @Backoff(delay = 1000L, multiplier = 1))
    public String cancelRenYunFlowOrderInfo(CancelFlowOrderDTO cancelFlowOrderVO) {
        String result = sendHttpToRenYun(DeliveryConstants.CANCEL_FLOW_ORDER,cancelFlowOrderVO);
        return result;
    }

    /**
     * 发送http请求到仁云
     *
     * @return
     */
    public String sendHttpToRenYun(String url, Serializable object) {
        ResponseData responseData = null;
        DeliveryHttpLogEntity deliveryHttpLogEntity = new DeliveryHttpLogEntity();
        deliveryHttpLogEntity.setRequestParams(JSONObject.toJSONString(object));
        deliveryHttpLogEntity.setRequestUrl(url);
        deliveryHttpLogEntity.setRequestCode(DeliveryTypeEnum.ADD_TYPE.getValue().intValue());
        deliveryHttpLogEntity.setCreateTime(LocalDateTime.now());
        deliveryHttpLogEntity.setRequestMethodType(HttpMethod.POST.ordinal());
        deliveryHttpLogEntity.setSendTime(LocalDateTime.now());
        deliveryHttpLogEntity.setUpdateTime(LocalDateTime.now());
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            httpHeaders.set("user-agent", DeliveryConstants.USER_AGENT_OPERATECENTER);
            HttpEntity httpEntity = new HttpEntity(httpHeaders);
            responseData = restTemplateConfig.restTemplate().postForObject(url, httpEntity, ResponseData.class, object);
            if (ErrorCode.SUCCESS.getCode().equals(responseData.getResCode())) {
                deliveryHttpLogEntity.setReturnStatusCode(ErrorCode.SUCCESS.getCode());
                deliveryHttpLogEntity.setReturnContext(JSONObject.toJSONString(responseData.getData()));
                deliveryHttpLogEntity.setReturnStatusType("成功");
                deliveryHttpLogEntity.setReturnTime(LocalDateTime.now());
                deliveryLogUtil.addDeliveryLog(deliveryHttpLogEntity);
                return JSONObject.toJSONString(responseData);
            } else {
                deliveryHttpLogEntity.setReturnStatusCode(ErrorCode.FAILED.getCode());
                deliveryHttpLogEntity.setReturnContext(JSONObject.toJSONString(responseData.getData()));
                deliveryHttpLogEntity.setReturnStatusType("失败");
                deliveryHttpLogEntity.setReturnTime(LocalDateTime.now());
                deliveryLogUtil.addDeliveryLog(deliveryHttpLogEntity);
                log.error("请求仁云失败，各参数返回值,url:{},params:{},result:{}", url, JSON.toJSONString(object), JSON.toJSONString(responseData));
            }
        } catch (Exception e) {
            deliveryHttpLogEntity.setReturnStatusCode(ErrorCode.SYS_ERROR.getCode());
            deliveryHttpLogEntity.setReturnContext(e.getMessage());
            deliveryHttpLogEntity.setReturnStatusType("请求仁云出现问题");
            deliveryHttpLogEntity.setReturnTime(LocalDateTime.now());
            deliveryLogUtil.addDeliveryLog(deliveryHttpLogEntity);
            log.info("请求仁云失败，失败原因：case:{}", e.getMessage());
            Cat.logError("请求仁云失败，失败原因：case:" + e.getMessage(), e);
        }
        return JSONObject.toJSONString(responseData);
    }

}
