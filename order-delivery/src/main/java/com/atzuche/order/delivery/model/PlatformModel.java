package com.atzuche.order.delivery.model;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author 胡春林
 */
@Data
@ToString
public class PlatformModel {


    /**
     * 事件编号
     */
    private String event;

    /**
     * memNo: 租客会员编号
     */
    private String memNo;

    /**
     * carNo：车编号
     */
    private String carNo;

    /**
     * orderNo: 订单编号
     */
    private String orderNo;

    /**
     * plateNum: 车牌号
     */
    private String plateNum;

    /**
     * carBrandName: 车辆品牌名称
     */
    private String carBrandName;

    /**
     * carTypeName: 车辆类型名称
     */
    private String carTypeName;

    /**
     * 交车时间，格式（yyyy年MM月dd日(周W)HH:mm）
     */
    private String dateTime;

    /**
     * 支付剩余小时数
     */
    private String leftHours;

    /**
     * 押金
     */
    private String depositPrice;

    /**
     * 优惠券金额
     */
    private String couponPrice;

    /**
     * 租用天数
     */
    private String rentDays;

    private String flag;

    /**
     * 换车前车辆品牌名称
     */
    private String oldCarBrandName;

    /**
     * 换车前车辆类型名称
     */
    private String oldCarTypeName;

    public Map<String, String> convert2Map() {
        Map<String, String> paramsMap = new HashMap<>();
        if (StringUtils.isNotEmpty(event)) {
            paramsMap.put("event", event);
            paramsMap.put("messageId", UUID.randomUUID().toString().replaceAll("-", ""));
        }
        if (StringUtils.isNotEmpty(memNo)) {
            paramsMap.put("memNo", memNo);
        }
        if (StringUtils.isNotEmpty(carNo)) {
//            paramsMap.put("carNo", carNo);
        }
        if (StringUtils.isNotEmpty(orderNo)) {
            paramsMap.put("orderNo", orderNo);
        }
        if (StringUtils.isNotEmpty(flag)) {
            paramsMap.put("flag", flag);
        }
        if (StringUtils.isNotEmpty(plateNum)) {
            paramsMap.put("plateNum", plateNum);
        }
        if (StringUtils.isNotEmpty(carBrandName)) {
            paramsMap.put("carBrandName", carBrandName);
        }
        if (StringUtils.isNotEmpty(carTypeName)) {
            paramsMap.put("carTypeName", carTypeName);
        }
        if (StringUtils.isNotEmpty(dateTime)) {
            paramsMap.put("dateTime", dateTime);
        }
        if (StringUtils.isNotEmpty(leftHours)) {
            paramsMap.put("leftHours", leftHours);
        }
        if (StringUtils.isNotEmpty(depositPrice)) {
            paramsMap.put("depositPrice", depositPrice);
        }
        if (StringUtils.isNotEmpty(couponPrice)) {
            paramsMap.put("couponPrice", couponPrice);
        }
        if (StringUtils.isNotEmpty(rentDays)) {
            paramsMap.put("rentDays", rentDays);
        }
        if (StringUtils.isNotEmpty(flag)) {
            paramsMap.put("flag", flag);
        }
        if (StringUtils.isNotEmpty(oldCarBrandName)) {
            paramsMap.put("oldCarBrandName", oldCarBrandName);
        }
        if (StringUtils.isNotEmpty(oldCarTypeName)) {
            paramsMap.put("oldCarTypeName", oldCarTypeName);
        }
        return paramsMap;
    }

}
