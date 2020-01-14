package com.atzuche.order.coreapi.entity.vo.req;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 提交订单风控审核需要参数
 *
 * @author pengcheng.fu
 * @date 2020/1/6 15:33
 */
@Data
public class SubmitOrderRiskCheckReqVO {

    /**
     * 下单时间
     */
    private Date reqTime;
    /**
     * 租客会员号
     */
    private String memNo;
    /**
     * 订单类型
     */
    private String orderCategory;

    /**
     * 业务来源主类型
     */
    private String businessParentType;

    /**
     * 业务来源子类型
     */
    private String businessChildType;

    /**
     * 平台来源主类型
     */
    private String platformParentType;

    /**
     * 平台来源子类型
     */
    private String platformChildType;

    /**
     * 用户客户端ip,用于同盾决策
     * reqIp
     */
    private String srcIp;

    /**
     * 车辆号
     */
    private String carNo;

    /**
     * 车辆类型
     */
    private String carOwnerType;

    /**
     * 租期开始时间
     */
    private LocalDateTime rentTime;

    /**
     * 租期截止时间
     */
    private LocalDateTime revertTime;

    /**
     * 取车服务标识
     */
    private Integer srvGetFlag;

    /**
     * 取车地址
     */
    private String srvGetAddr;

    /**
     * 取车地址-经度
     */
    private String srvGetLon;

    /**
     * 取车地址-维度
     */
    private String srvGetLat;

    /**
     * 还车服务地址
     */
    private Integer srvReturnFlag;

    /**
     * 还车地址
     */
    private String srvReturnAddr;

    /**
     * 还车地址-经度
     */
    private String srvReturnLon;

    /**
     * 还车地址-维度
     */
    private String srvReturnLat;

    /**
     * 租客定位地址-下单地址-经度
     */
    private String publicLongitude;

    /**
     * 租客定位地址-下单地址-维度
     */
    private String publicLatitude;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 城市名称
     */
    private String cityName;


    /**
     * 周末价格
     */
    private Integer weekendPrice;



}
