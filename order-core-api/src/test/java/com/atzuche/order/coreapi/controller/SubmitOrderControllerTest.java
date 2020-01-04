package com.atzuche.order.coreapi.controller;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.http.HttpResult;
import com.atzuche.order.commons.http.HttpUtil;
import com.atzuche.order.commons.vo.req.NormalOrderReqVO;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.UUID;

public class SubmitOrderControllerTest {

    @Test
    public void SubmitOrderController() throws Exception {
//        LocalDateTime rentTime = LocalDateTime.of(2020,1,2,12,1,1);
//        LocalDateTime revertTime = LocalDateTime.of(2020,1,3,12,1,1);
        NormalOrderReqVO param = new NormalOrderReqVO();
        param.setRequestId(UUID.randomUUID().toString());
        param.setOS("PC");
        param.setOsVersion("90");
        param.setAppVersion("100");
        param.setIMEI("imei");
        param.setOAID("");
        param.setPublicLongitude("");
        param.setPublicLatitude("");
        param.setPublicCityCode("");
        param.setAppName("appname");
        param.setDeviceName("123456777xxx");
        param.setPublicToken("aba26195b8374b5ca586b1488c300a45");
        param.setAppChannelId("");
        param.setMac("");
        param.setAndroidID("xxxxxxx");
        param.setMemNo("985856422");
        param.setSchema("");

        param.setOrderCategory("1");
        param.setBusinessParentType("5");
        param.setBusinessChildType("");
        param.setPlatformParentType("1");
        param.setPlatformChildType("2");
        param.setCityCode("310100");
        param.setCityName("上海");
        param.setSceneCode("EX007");
        param.setSource("3");
        param.setSubSource("");
        param.setRentTime("2020-01-03 12:30:00");
        param.setRevertTime("2020-01-08 12:30:00");
        param.setSrvGetFlag(1);
        param.setSrvGetAddr("上海市徐家汇xxx路");
        param.setSrvGetLon("114.047754");
        param.setSrvGetLat("22.521016");
        param.setSrvReturnFlag(1);
        param.setSrvReturnAddr("上海市徐家汇xxx路");
        param.setSrvReturnLon("114.047754");
        param.setSrvReturnLat("22.521016");
        param.setCarNo("791332363");
        param.setAbatement("1");
        param.setGetCarFreeCouponId("");
        param.setDisCouponIds("");
        param.setCarOwnerCouponNo("");
        param.setUseAutoCoin(0);
        param.setUseBal(0);
        param.setFreeDoubleTypeId("3");
        param.setUseAirportService(0);
        param.setFlightNo("");
        param.setLimitRedStatus("");
        param.setLimitReductionId("");
        param.setDriverIds("");
        param.setCarAddrIndex("0");
        param.setIsLeaveCity(0);
        param.setRentCity("上海");
        param.setQueryId("queryId");
        param.setActivityId("ActivityId");
        param.setRentReason("租车原因");
        param.setOilType("1");
        param.setConPhone("12345678910");
        param.setUtmSource("广告来源");
        param.setUtmMedium("广告媒体");
        param.setUtmTerm("广告名称");
        param.setUtmCampaign("广告关键字");
        param.setModuleName("order");
        param.setFunctionName("order/req");
        param.setReqSource(1);
        param.setReqVersion("10");
        param.setReqOs("android");
        param.setSrcIp("127.0.0.1");
        param.setSrcPort(7777);
        String paramStr = JSON.toJSONString(param);
        System.out.println("paramStr---->"+paramStr);
        HttpResult httpResult = HttpUtil.doPostNotGzip("order/normal/req", paramStr);
        System.out.println(httpResult);
    }
}
