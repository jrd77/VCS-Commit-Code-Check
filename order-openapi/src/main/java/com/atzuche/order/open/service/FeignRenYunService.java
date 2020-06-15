package com.atzuche.order.open.service;

import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/*
 * @Author ZhangBin
 * @Date 2020/6/15 15:43
 * @Description: 调用仁云的接口
 *
 **/
@FeignClient(name="renyun",url = "${auto.dangerCount.url}")
public interface FeignRenYunService {
    /*
     * @Author ZhangBin
     * @Date 2020/6/15 15:45
     * @Description: 获取出险次数
     *
     **/
    @GetMapping("/AotuInterface/getclaimcount")
    public ResponseData<?> getDangerCount(String orderNo,String plateNum,String carNo);
}
