package com.atzuche.order.renterorder.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.rentercost.entity.vo.OwnerCouponLongVO;
import com.atzuche.order.renterorder.vo.owner.*;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 订单使用车主券相关操作
 *
 * @author pengcheng.fu
 * @date 2019/12/25 14:10
 */
@Service
public class OwnerDiscountCouponService {

    private static final Logger logger = LoggerFactory.getLogger(OwnerDiscountCouponService.class);

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Do any additional configuration here
        return builder.build();
    }

    @Resource
    private RestTemplate restTemplate;

    @Value("${auto.owner.coupon.url}")
    private String ownerCouponUrl;


    /**
     * 校验是否满足车主券的使用规则
     *
     * @param orderNo  订单号
     * @param rentAmt  租金
     * @param couponNo 车主券券编码
     * @param carNo    车辆注册号
     * @param mark     操作来源标示（不同地方校验规则有所不同）：1、下单 2、修改订单
     * @return BaseOutJB
     */
    public OwnerCouponGetAndValidResultVO getAndValidCoupon(String orderNo, Integer rentAmt, String couponNo,
                                                            Integer carNo,
                                                            Integer mark) {
        logger.info(
                "Verify that the owner's coupon meet the usage rules,param is:orderNo:[{}], rentAmt:[{}],couponNo:[{}], carNo:[{}], mark:[{}]",
                orderNo, rentAmt, couponNo, carNo, mark);

        StringBuilder url = new StringBuilder("ownerCoupon/trans/valid?");
        url.append("amount=").append(rentAmt);
        url.append("&carNo=").append(carNo);
        if (null != orderNo) {
            url.append("&orderNo=").append(orderNo);
        }
        url.append("&couponNo=").append(couponNo);
        url.append("&type=").append(mark);

        String urlStr = ownerCouponUrl + "/" + url.toString();
        logger.info("Invoke ownerCouponService api to verify coupon information. urlStr:[{}]", urlStr);
        try {
            String json = restTemplate.getForObject(urlStr, String.class);
            logger.info("Invoke ownerCouponService api to verify coupon information. result is:[{}]", json);
            if (StringUtils.isNotBlank(json)) {
                return new Gson().fromJson(json, OwnerCouponGetAndValidResultVO.class);
            }
        } catch (Exception e) {
            logger.info("OwnerDiscountCouponService.getAndValidCoupon error. url is:[{}]", urlStr, e);
        }
        return null;
    }


    /**
     * 车主券绑定订单操作，此时券状态为“已冻结” <BR>
     *
     * @param orderNo         订单号
     * @param rentAmt         租金
     * @param couponNo        车主券券编码
     * @param carNo           车辆注册号
     * @param renterName      租客姓名
     * @param renterFirstName 租客姓氏
     * @param renterSex       租客性別
     * @return boolean true:绑定成功 false:绑定失败
     */
    public boolean bindCoupon(String orderNo, Integer rentAmt, String couponNo, Integer carNo, String renterName,
                              String renterFirstName, String renterSex) {
        logger.info(
                "The order is tied to the owner's coupon.param is: orderNo:[{}], rentAmt:[{}], couponNo:[{}], " +
                        "carNo:[{}],renterName:[{}], renterFirstName:[{}], renterSex:[{}]",
                orderNo, rentAmt, couponNo, carNo, renterName, renterFirstName, renterSex);

        Map<String, Object> params = new HashMap<>(16);
        params.put("amount", rentAmt);
        params.put("carNo", carNo);
        params.put("orderNo", orderNo);
        params.put("couponNo", couponNo);
        params.put("renterName", renterName);
        params.put("renterFirstName", renterFirstName);
        params.put("renterSex", renterSex);

        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "车主券服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "ownerCoupon/trans/req");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(params));
            String json = restTemplate.postForObject(ownerCouponUrl + "/ownerCoupon/trans/req", params, String.class);
            logger.info("ownerCoupon/trans/req. result is:[{}]", json);
            Cat.logEvent(CatConstants.FEIGN_RESULT, json);
            t.setStatus(Transaction.SUCCESS);
            if (StringUtils.isNotBlank(json)) {
                ResponseData responseData = new Gson().fromJson(json, ResponseData.class);
                if (null != responseData) {
                    if (StringUtils.equals(ErrorCode.SUCCESS.getCode(), responseData.getResCode())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            logger.info("bindCoupon error . url:[{}],params:[{}]", ownerCouponUrl + "/ownerCoupon/trans/req", params, e);
            Cat.logError("下单绑定车主券失败. url:/ownerCoupon/trans/req,params:" + JSON.toJSONString(params), e);
            t.setStatus(e);
        } finally {
            t.complete();
        }
        return false;
    }


    /**
     * 订单已使用车主券撤销操作，此时券状态为“未使用”或“已失效” <BR>
     *
     * @param orderNo  订单号
     * @param couponNo 车主券券编码
     * @param recover  标识
     * @return boolean true:撤销成功 false:撤销失败
     */
    public boolean undoCoupon(String orderNo, String couponNo, String recover) {
        logger.info("The order undo the owner's coupon,param is: orderNo:[{}], couponNo:[{}], recover:[{}]", orderNo,
                couponNo, recover);
        Map<String, Object> params = new HashMap<>(8);
        params.put("orderNo", orderNo);
        params.put("couponNo", couponNo);
        params.put("recover", recover);

        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "车主券服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "ownerCoupon/trans/cancel");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(params));
            String json = restTemplate.postForObject(ownerCouponUrl + "/ownerCoupon/trans/cancel",
                    params, String.class);
            logger.info("ownerCoupon/trans/cancel. result is:[{}]", json);
            Cat.logEvent(CatConstants.FEIGN_RESULT, json);
            t.setStatus(Transaction.SUCCESS);
            if (StringUtils.isNotBlank(json)) {
                ResponseData responseData = new Gson().fromJson(json, ResponseData.class);
                if (null != responseData) {
                    if (StringUtils.equals(ErrorCode.SUCCESS.getCode(), responseData.getResCode())) {
                        return true;
                    }
                }
            }

        } catch (Exception e) {
            logger.info("undoCoupon error. url:[{}],params:[{}]", ownerCouponUrl + "/ownerCoupon/trans/cancel",
                    params, e);
            Cat.logError("获订单已使用车主券撤销操作. url:/ownerCoupon/trans/cancel,params:" + JSON.toJSONString(params), e);
            t.setStatus(e);
        } finally {
            t.complete();
        }
        return false;
    }


    /**
     * 获取租客名下可用车主券列表 <BR>
     *
     * @param rentAmt  租金
     * @param memNo    租客会员号
     * @param carNo    车辆注册号
     * @param couponNo 车主券券编码（选中则传否为空）
     * @return List<OwnerDiscountCoupon>
     */
    public List<OwnerDiscountCouponVO> getCouponList(Integer rentAmt, Integer memNo, Integer carNo, String couponNo) {
        logger.info(
                "Get a list of available vehicle owners' coupons under the tenant's name,param is: rentAmt:[{}], memNo:[{}] carNo:[{}], couponNo:[{}]",
                rentAmt, memNo, carNo, couponNo);

        StringBuilder url = new StringBuilder("ownerCoupon/orderOwnerCoupon?");
        url.append("rented=").append(rentAmt);
        url.append("&memNo=").append(memNo);
        url.append("&carNo=").append(carNo);
        if (StringUtils.isNotEmpty(couponNo)) {
            url.append("&couponNo=").append(couponNo);
        }
        String urlStr = ownerCouponUrl + "/" + url.toString();
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "车主券服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "ownerCoupon/orderOwnerCoupon");
            Cat.logEvent(CatConstants.FEIGN_PARAM, urlStr);
            String json = restTemplate.getForObject(urlStr, String.class);
            logger.info("ownerCoupon/orderOwnerCoupon. result is :[{}]", json);
            Cat.logEvent(CatConstants.FEIGN_RESULT, json);
            t.setStatus(Transaction.SUCCESS);
            if (null != json) {
                OwnerCouponListResult result = new Gson().fromJson(json, OwnerCouponListResult.class);
                if (null != result.getData()) {
                    return result.getData().getOwnerCouponDTOList();
                }
            }
        } catch (Exception e) {
            logger.info("getCouponList error . params is,urlStr:[{}]", urlStr, e);
            Cat.logError("获取租客名下可用车主券列表. url:/ownerCoupon/orderOwnerCoupon,params:" + urlStr, e);
            t.setStatus(e);
        } finally {
            t.complete();
        }

        return new ArrayList<>();
    }

    /**
     * 获取长租折扣信息
     *
     * @param reqVO 参数
     * @return OwnerCouponLongResVO 长租折扣信息
     */
    public OwnerCouponLongVO getLongOwnerCoupon(OwnerCouponLongReqVO reqVO) {
        logger.info("Obtain owner coupon information for long lease orders.param is,reqVO:[{}]",
                JSON.toJSONString(reqVO));

        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "车主券服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "ownerCoupon/long/calcuteOwnerLongCoupon");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(reqVO));
            String json = restTemplate.postForObject(ownerCouponUrl + "/ownerCoupon/long/calcuteOwnerLongCoupon",
                    reqVO, String.class);
            logger.info("ownerCoupon/long/calcuteOwnerLongCoupon. result is:[{}]", json);
            Cat.logEvent(CatConstants.FEIGN_RESULT, json);
            t.setStatus(Transaction.SUCCESS);
            if (StringUtils.isNotBlank(json)) {
                OwnerCouponLongResVO resVO = new Gson().fromJson(json, OwnerCouponLongResVO.class);
                if (null != resVO && StringUtils.equals(ErrorCode.SUCCESS.getCode(), resVO.getResCode())) {
                    return resVO.getData();
                }
            }
        } catch (Exception e) {
            logger.info("getLongOwnerCoupon error. url:[{}],params:[{}]", ownerCouponUrl + "/ownerCoupon/long/calcuteOwnerLongCoupon",
                    JSON.toJSONString(reqVO), e);
            Cat.logError("Obtain owner coupon information for long lease orders. url:/ownerCoupon/long/calcuteOwnerLongCoupon,params:" + JSON.toJSONString(reqVO), e);
            t.setStatus(e);
        } finally {
            t.complete();
        }
        return null;
    }

}
