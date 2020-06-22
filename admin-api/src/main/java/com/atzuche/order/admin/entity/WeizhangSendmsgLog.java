package com.atzuche.order.admin.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * weizhang_sendmsg_log
 * @author
 */
@Data
public class WeizhangSendmsgLog implements Serializable {
    private Long id;

    /**
     * 平台，0：app，1：短信
     */
    private Integer platform;

    /**
     * 消息类型，订单消息、系统消息
     */
    private String messageType;

    /**
     * 用户角色：1:租客, 2:车主
     */
    private Integer memRole;

    /**
     * 会员号
     */
    private Long memNo;

    /**
     * 订单号
     */
    private Long orderNo;

    /**
     * 消息文案
     */
    private String content;

    /**
     * 跳转页面模式，0：native方式，1：H5方式
     */
    private Integer pageMode;

    /**
     * 跳转链接，如果跳转页面方式为H5需要该字段
     */
    private String url;

    /**
     * 跳转地址名称
     */
    private String eventName;

    /**
     * 推送需要的参数(json格式)
     */
    private String param;

    /**
     * 如果跳转页面方式为native需要该字段
     */
    private Integer event;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 发送人（点击发送消息的人）
     */
    private String createOp;

    /**
     * 违章序号
     */
    private Long orderViolationId;

    private static final long serialVersionUID = 1L;
}