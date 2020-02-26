package com.atzuche.order.parentorder.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 订单来源统计
 * 
 * @author ZhangBin
 * @date 2019-12-24 16:19:32
 */
@Data
public class OrderSourceStatEntity implements Serializable {

    private static final long serialVersionUID = 4234437909792502665L;

    /**
	 * 
	 */
	private Integer id;
	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * app版本信息
	 */
	private String appVersion;
	/**
	 * 设备信息
	 */
	private String device;
	/**
	 * 渠道信息
	 */
	private String channelId;
	/**
	 * 其他推广信息
	 */
	private String popularize;
	/**
	 * 合作城市
	 */
	private String cooperationCity;
	/**
	 * 保险公司 1:保险公司-人保,2:保险公司-太保,3:保险公司-平安
	 */
	private String insuranceCompany;
	/**
	 * 支付来源 1-线上，2-线下
	 */
	private String paySource;
	/**
	 * 活动分类
	 */
	private String activityId;
	/**
	 * 广告来源
	 */
	private String utmSource;
	/**
	 * 广告媒体
	 */
	private String utmMedium;
	/**
	 * 广告名称
	 */
	private String utmCampaign;
	/**
	 * 广告关键字
	 */
	private String utmTerm;
	/**
	 * 其他分类
	 */
	private String otherType;
	/**
	 * 场景分类,1:收藏,2:历史订单,3:九宫格
	 */
	private String entryCode;
	/**
	 * 内部分类 1:短租,2:套餐
	 */
	private String category;
	/**
	 * 业务来源主类型,1:OTA,2代步车，3:礼品卡,4:安联,5:自有
	 */
	private String businessParentType;
	/**
	 * 业务来源子类型 1:OTA-携程,2:OTA-同城,3:OTA-飞猪,4:OTA-租租车,5:代步车-出险代步车,6:代步车-2*2代步车,7:代步车-券码下单,8:代步车-特供车
	 */
	private String businessChildType;
	/**
	 * 平台来源主类型 1:APP,2:小程序，3:微信,4:支付宝,5:PC页面,6:H5页面,7:管理后台,8:API
	 */
	private String platformParentType;
	/**
	 * 平台来源子类型 1:APP-IOS,2:APP-Android,3:小程序-支付宝,4:小程序-微信,5:小程序-百度
	 */
	private String platformChildType;
	/**
	 * 来源 1：手机，2：网站，3:管理后台，4:cp b2c, 5:cp upop
	 */
	private String source;
	/**
	 * 管理后台操作人
	 */
	private String operator;
	/**
	 * 是否使用凹凸币
	 */
	private Integer useAutoCoin;
	/**
	 * 是否后台管理操作
	 */
	private Integer specialConsole;
	/**
	 * 请求系统
	 */
	private String reqOs;
	/**
	 * 
	 */
	private String reqSource;
	/**
	 * 请求版本
	 */
	private String reqVersion;
	/**
	 * 请求id
	 */
	private String requestId;
    /**
     * 下单地址
     */
    private String reqAddr;
    /**
     * 下单地址经度
     */
    private String publicLongitude;
    /**
     * 下单地址经度
     */
    private String publicLatitude;
	/**
	 * 
	 */
	private String schema;
	/**
	 * 
	 */
	private String moduleName;
	/**
	 * 
	 */
	private String functionName;
	/**
	 * 
	 */
	private String subSource;
	/**
	 * 
	 */
	private String srcPort;
	/**
	 * 
	 */
	private String srcIp;
	/**
	 * 
	 */
	private String oaid;
	/**
	 * 租车原因
	 */
	private String rentReason;
	/**
	 * 移动设备识别码
	 */
	private String imei;
	/**
	 * 系统类型
	 */
	private String os;
	/**
	 * 设备名称
	 */
	private String deviceName;
	/**
	 * 筛选条件
	 */
	private String queryId;
	/**
	 * 请求ip
	 */
	private String reqIp;
	/**
	 * mac地址
	 */
	private String mac;
	/**
	 * app渠道id
	 */
	private String appChannelId;
	/**
	 * android id
	 */
	private String androidId;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/**
	 * 创建人
	 */
	private String createOp;
	/**
	 * 修改时间
	 */
	private LocalDateTime updateTime;
	/**
	 * 修改人
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}
