package com.atzuche.order.parentorder.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 *
 * @author pengcheng.fu
 * @date 2019/12/24 16:57
 */
@Data
public class OrderDTO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 租客会员号
     */
    private String memNoRenter;
    /**
     * 订单类型（内部分类）1：短租， 2：套餐
     */
    private Integer category;
    /**
     * 场景号
     */
    private String entryCode;
    /**
     * 来源 1：手机，2：网站，3:管理后台，4:cp b2c, 5:cp upop
     */
    private String source;
    /**
     * 预计起租时间
     */
    private LocalDateTime expRentTime;
    /**
     * 预计还车时间
     */
    private LocalDateTime expRevertTime;
    /**
     * 下单城市名称
     */
    private String cityName;
    /**
     * 下单城市code
     */
    private String cityCode;
    /**
     * 是否出市 0-否，1-是
     */
    private Integer isOutCity;
    /**
     * 是否免押 0-否，1-是
     */
    private Integer isFreeDeposit;
    /**
     * 是否使用机场服务 0-否，1-是
     */
    private Integer isUseAirPortService;
    /**
     * 请求时间
     */
    private LocalDateTime reqTime;
    /**
     * 风控审核id
     */
    private Integer riskAuditId;
    /**
     * 航班号
     */
    private String flightId;

    /**
     * 订单图片存储目录
     */
    private String basePath;

    /**
     * 限时立减金额（面额）
     */
    private Integer limitAmt;



}
