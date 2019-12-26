package com.atzuche.order.renterorder.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.autoyol.coupon.api.CouponServiceApi;
import com.autoyol.coupon.api.MemAvailCouponRequest;
import com.autoyol.coupon.api.MemAvailCouponResponse;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 平台优惠券+取送服务券业务处理
 *
 * @author pengcheng.fu
 * @date 2019/12/25 15:41
 */
@Service
public class PlatformCouponService {

    private static final Logger logger = LoggerFactory.getLogger(PlatformCouponService.class);

    @Resource
    private CouponServiceApi couponServiceApi;


    /**
     * 获取会员有效的平台优惠券信息
     *
     * @param request 请求参数
     * @return MemAvailCouponResponse 优惠券信息
     */
    public MemAvailCouponResponse findAvailMemCoupons(MemAvailCouponRequest request) {
        logger.info("获取会员有效的平台优惠券信息.request:[{}]", JSON.toJSONString(request));
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "平台优惠券服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "CouponServiceApi.findAvailMemCoupons");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(request));
            MemAvailCouponResponse memCouponResponse = couponServiceApi.findAvailMemCoupons(request);
            logger.info("获取会员有效的平台优惠券信息.response:[{}]", JSON.toJSONString(memCouponResponse));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(memCouponResponse));
            t.setStatus(Transaction.SUCCESS);
            return memCouponResponse;
        } catch (Exception e) {
            logger.error("获取会员有效的平台优惠券信息异常.request:[{}]", request, e);
            t.setStatus(e);
            Cat.logError("获取会员有效的平台优惠券信息异常.", e);
        } finally {
            t.complete();
        }
        return null;
    }


    /**
     * 获取会员有效的去送服务优惠券信息
     *
     * @param request 请求参数
     * @return MemAvailCouponResponse 优惠券信息
     */
    public MemAvailCouponResponse findAvailMemGetAndReturnSrvCoupons(MemAvailCouponRequest request) {
        logger.info("获取会员有效的取送服务优惠券信息(平台券+取还车券).request:[{}]", JSON.toJSONString(request));
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "平台优惠券服务");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "CouponServiceApi.findAvailMemCouponsV2");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(request));
            MemAvailCouponResponse memCouponResponse = couponServiceApi.findAvailMemCouponsV2(request);
            logger.info("获取会员有效的取送服务优惠券信息(平台券+取还车券).response:[{}]", JSON.toJSONString(memCouponResponse));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(memCouponResponse));
            t.setStatus(Transaction.SUCCESS);
            return memCouponResponse;
        } catch (Exception e) {
            logger.error("获取会员有效的取送服务优惠券信息(平台券+取还车券)异常.request:[{}]", request, e);
            t.setStatus(e);
            Cat.logError("获取会员有效的取送服务优惠券信息(平台券+取还车券)异常.", e);
        } finally {
            t.complete();
        }
        return null;
    }



}
