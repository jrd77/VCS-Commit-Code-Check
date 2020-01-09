package com.atzuche.order.renterorder.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.CatConstants;
import com.autoyol.coupon.api.CouponServiceApi;
import com.autoyol.coupon.api.CouponSettleRequest;
import com.autoyol.coupon.api.CouponSettleResponse;
import com.autoyol.coupon.api.MemAvailCouponRequest;
import com.autoyol.coupon.api.MemAvailCouponResponse;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

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
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "youhuiqia");
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


    /**
     * 使用平台券
     *
     * @param disCoupondId 平台优惠券ID
     * @param orderNo      主订单号
     * @param useDate      使用日期
     */
    public int usePlatformCoupon(String disCoupondId, Long orderNo, Date useDate) {
        logger.info("使用平台券.param is, disCoupondId:[{}],orderNo:[{}],useDate:[{}]", disCoupondId, orderNo, useDate);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "平台优惠券服务");
        int result = 0;
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "couponServiceApi.useCoupon");
            Cat.logEvent(CatConstants.FEIGN_PARAM, "disCoupondId=" + disCoupondId + "&orderNo=" + orderNo + "&useDate=" + useDate);
            result = couponServiceApi.useCoupon(disCoupondId, orderNo, useDate);

            logger.info("使用平台券.result is, result:[{}]", result);
            Cat.logEvent(CatConstants.FEIGN_RESULT, String.valueOf(result));
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            logger.error("使用平台券异常.result:[{}]", result, e);
            t.setStatus(e);
            Cat.logError("使用平台券异常.", e);
        } finally {
            t.complete();
        }

        return result;
    }


    /**
     * 使用送取服务券
     *
     * @param disCoupondId 送取服务惠券ID
     * @param orderNo      主订单号
     * @param useDate      使用日期
     */
    public int useGetCarFreeCoupon(String disCoupondId, Long orderNo, Date useDate) {

        logger.info("使用送取服务券.param is, disCoupondId:[{}],orderNo:[{}],useDate:[{}]", disCoupondId, orderNo, useDate);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "平台优惠券服务");
        int result = 0;
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "couponServiceApi.useGetCarFreeCoupon");
            Cat.logEvent(CatConstants.FEIGN_PARAM, "disCoupondId=" + disCoupondId + "&orderNo=" + orderNo + "&useDate=" + useDate);
            result = couponServiceApi.useGetCarFreeCoupon(disCoupondId, orderNo, useDate);

            logger.info("使用送取服务券.result is, result:[{}]", result);
            Cat.logEvent(CatConstants.FEIGN_RESULT, String.valueOf(result));
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            logger.error("使用送取服务券异常.result:[{}]", result, e);
            t.setStatus(e);
            Cat.logError("使用送取服务券异常.", e);
        } finally {
            t.complete();
        }
        return result;
    }


    /**
     * 退还优惠券(平台优惠券)
     *
     * @param orderNo 订单号
     * @return int
     */
    public int cancelPlatformCoupon(String orderNo) {
        logger.info("退还优惠券(平台优惠券).param is, orderNo:[{}]", orderNo);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "平台优惠券服务");
        int result = 0;
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "couponServiceApi.cancelCoupon");
            Cat.logEvent(CatConstants.FEIGN_PARAM, "orderNo=" + orderNo);

            result = couponServiceApi.cancelCoupon(Long.valueOf(orderNo));
            logger.info("退还优惠券(平台优惠券).result is, result:[{}]", result);
            Cat.logEvent(CatConstants.FEIGN_RESULT, String.valueOf(result));
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            logger.error("退还优惠券(平台优惠券)异常.result:[{}]", result, e);
            t.setStatus(e);
            Cat.logError("退还优惠券(平台优惠券)异常.", e);
        } finally {
            t.complete();
        }
        return result;
    }


    /**
     * 退还优惠券(送取服务券)
     *
     * @param orderNo 订单号
     * @return int
     */
    public int cancelGetCarFeeCoupon(String orderNo) {
        logger.info("退还优惠券(送取服务券).param is, orderNo:[{}]", orderNo);
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "平台优惠券服务");
        int result = 0;
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "couponServiceApi.cancelGetCarFreeCoupon");
            Cat.logEvent(CatConstants.FEIGN_PARAM, "orderNo=" + orderNo);

            result = couponServiceApi.cancelGetCarFreeCoupon(Long.valueOf(orderNo));
            logger.info("退还优惠券(送取服务券).result is, result:[{}]", result);
            Cat.logEvent(CatConstants.FEIGN_RESULT, String.valueOf(result));
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            logger.error("退还优惠券(送取服务券)异常.result:[{}]", result, e);
            t.setStatus(e);
            Cat.logError("退还优惠券(送取服务券)异常.", e);
        } finally {
            t.complete();
        }
        return result;
    }

    
    
    /**
     * 获取会员送取服务优惠券抵扣信息
     *
     * @param request 请求参数
     * @return CouponSettleResponse 优惠券信息
     */
    public CouponSettleResponse checkGetCarFreeCouponAvailable(CouponSettleRequest request) {
        logger.info("获取会员送取服务优惠券抵扣信息.request:[{}]", JSON.toJSONString(request));
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "youhuiqia");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "CouponServiceApi.checkGetCarFreeCouponAvailable");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(request));
            CouponSettleResponse memCouponResponse = couponServiceApi.checkGetCarFreeCouponAvailable(request);
            logger.info("获取会员送取服务优惠券抵扣信息.response:[{}]", JSON.toJSONString(memCouponResponse));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(memCouponResponse));
            t.setStatus(Transaction.SUCCESS);
            return memCouponResponse;
        } catch (Exception e) {
            logger.error("获取会员送取服务优惠券抵扣信息异常.request:[{}]", request, e);
            t.setStatus(e);
            Cat.logError("获取会员送取服务优惠券抵扣信息异常.", e);
        } finally {
            t.complete();
        }
        return null;
    }
    
    
    /**
     * 获取会员平台优惠券抵扣信息
     *
     * @param request 请求参数
     * @return CouponSettleResponse 优惠券信息
     */
    public CouponSettleResponse checkCouponAvailable(CouponSettleRequest request) {
        logger.info("获取会员平台优惠券抵扣信息.request:[{}]", JSON.toJSONString(request));
        Transaction t = Cat.newTransaction(CatConstants.FEIGN_CALL, "youhuiqia");
        try {
            Cat.logEvent(CatConstants.FEIGN_METHOD, "CouponServiceApi.checkCouponAvailable");
            Cat.logEvent(CatConstants.FEIGN_PARAM, JSON.toJSONString(request));
            CouponSettleResponse memCouponResponse = couponServiceApi.checkCouponAvailable(request);
            logger.info("获取会员平台优惠券抵扣信息.response:[{}]", JSON.toJSONString(memCouponResponse));
            Cat.logEvent(CatConstants.FEIGN_RESULT, JSON.toJSONString(memCouponResponse));
            t.setStatus(Transaction.SUCCESS);
            return memCouponResponse;
        } catch (Exception e) {
            logger.error("获取会员平台优惠券抵扣信息异常.request:[{}]", request, e);
            t.setStatus(e);
            Cat.logError("获取会员平台优惠券抵扣信息异常.", e);
        } finally {
            t.complete();
        }
        return null;
    }

}
