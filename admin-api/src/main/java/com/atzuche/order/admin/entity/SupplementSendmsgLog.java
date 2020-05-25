package com.atzuche.order.admin.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * 发送补付消息记录(SupplementSendmsgLog)实体类
 *
 * @author makejava
 * @since 2020-05-18 20:46:25
 */
public class SupplementSendmsgLog implements Serializable {
    private static final long serialVersionUID = -23491711896842737L;

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
    * 订单号，如果是补付消息类型，需要订单号
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
    * 补付ID
    */
    private Long orderSupplementDetailId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Integer getMemRole() {
        return memRole;
    }

    public void setMemRole(Integer memRole) {
        this.memRole = memRole;
    }

    public Long getMemNo() {
        return memNo;
    }

    public void setMemNo(Long memNo) {
        this.memNo = memNo;
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getPageMode() {
        return pageMode;
    }

    public void setPageMode(Integer pageMode) {
        this.pageMode = pageMode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Integer getEvent() {
        return event;
    }

    public void setEvent(Integer event) {
        this.event = event;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getCreateOp() {
        return createOp;
    }

    public void setCreateOp(String createOp) {
        this.createOp = createOp;
    }

    public Long getOrderSupplementDetailId() {
        return orderSupplementDetailId;
    }

    public void setOrderSupplementDetailId(Long orderSupplementDetailId) {
        this.orderSupplementDetailId = orderSupplementDetailId;
    }

}
