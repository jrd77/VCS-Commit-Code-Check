package com.atzuche.order.admin.vo.resp.supplement;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode
public class MessagePushRecordListResVO implements Serializable {
    private static final long serialVersionUID = 6277947143171115766L;
    @AutoDocProperty("补付ID")
    private String id;

    @AutoDocProperty("平台，0：app，1：短信")
    private String platform;

    @AutoDocProperty("消息类型，订单消息、系统消息")
    private String messageType;

    @AutoDocProperty("发送对象：1:租客, 2:车主")
    private String memRole;

    @AutoDocProperty("消息文案")
    private String content;

    @AutoDocProperty("跳转页面模式，0：native方式，1：H5方式")
    private String pageMode;

    @AutoDocProperty("跳转链接，如果跳转页面方式为H5返回该字段")
    private String url;

    @AutoDocProperty("跳转地址，如果跳转页面方式为native返回该字段")
    private String eventName;

    @AutoDocProperty("发送时间，yyyy-MM-dd HH:mm:ss")
    private String sendTime;

    @AutoDocProperty("操作人")
    private String createOp;
}
