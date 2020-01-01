package com.atzuche.order.renterorder.service;

import com.atzuche.order.renterorder.vo.owner.OwnerCouponGetAndValidResultVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
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

    @Resource
    private RestTemplate restTemplate;

    @Value("AUTO_OWNER_COUPON_URL")
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
        try {
            String json = restTemplate.postForObject(ownerCouponUrl + "/ownerCoupon/trans/req", params, String.class);
            logger.info("ownerCoupon/trans/req. result is:[{}]", json);
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
        }
        return false;
    }


    /**
     * 订单已使用车主券撤销操作，此时券状态为“未使用”或“已失效” <BR>
     *
     * @param orderNo  订单号
     * @param couponNo 车主券券编码
     * @param recover 标识
     * @return boolean true:撤销成功 false:撤销失败
     */
    public boolean undoCoupon(String orderNo, String couponNo, String recover) {
        logger.info("The order undo the owner's coupon,param is: orderNo:[{}], couponNo:[{}], recover:[{}]", orderNo,
                couponNo, recover);
        Map<String, Object> params = new HashMap<>(8);
        params.put("orderNo", orderNo);
        params.put("couponNo", couponNo);
        params.put("recover", recover);
        try {
            String json = restTemplate.postForObject(ownerCouponUrl + "/ownerCoupon/trans/cancel",
                    params, String.class);
            logger.info("ownerCoupon/trans/cancel. result is {}", json);
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
        }
        return false;
    }

}
