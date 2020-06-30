package com.atzuche.order.admin.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.exceptions.DangerCountException;
import com.atzuche.order.commons.vo.res.DangerCountRespVO;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Slf4j
@Service
public class RestTemplateService {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
    @Autowired
    private RestTemplate restTemplate;

    public DangerCountRespVO getDangerCountFromRemote(String dangerCountUrl,String orderNo, String carPlateNum, Integer carNo){
        String responseData = "";
        try {
            responseData = restTemplate.getForObject(dangerCountUrl+ "/AotuInterface/getclaimcount?orderNo="+orderNo+"&plateNum="+carPlateNum+"&carNo="+carNo, String.class);
            log.info("获取出险次数responseData={}", responseData);
            if (StringUtils.isNotBlank(responseData)) {
                ResponseData response = JSON.parseObject(responseData, ResponseData.class);
                if(response.getData() != null){
                    DangerCountRespVO data = JSON.parseObject(JSON.toJSONString(response.getData()), DangerCountRespVO.class);
                    data.setUpdateTime(LocalDateTimeUtils.formatDateTime(LocalDateTime.now(),LocalDateTimeUtils.DEFAULT_PATTERN));
                    return data;
                }
            }
            DangerCountException dangerCountException = new DangerCountException();
            log.error("远程获取出险次数失败",dangerCountException);
            throw dangerCountException;
        } catch (Exception e) {
            log.error("远程获取出险次数异常e",e);
            throw e;
        }

    }
}
