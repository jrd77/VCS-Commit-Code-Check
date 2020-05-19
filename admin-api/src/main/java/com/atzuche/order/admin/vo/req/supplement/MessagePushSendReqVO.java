package com.atzuche.order.admin.vo.req.supplement;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author ：changshu.xie
 * @date ：Created in 2020/05/17 16:58
 */
@Data
@EqualsAndHashCode
public class MessagePushSendReqVO {

    @AutoDocProperty("会员号")
    @NotNull(message = "会员号不能为空")
    private Long memNo;

    @AutoDocProperty("订单号,补付消息类型需要该字段")
    @NotNull(message = "订单号不能为空")
    private Long orderNo;

    @AutoDocProperty("平台，0：app，1：短信")
    @NotNull(message = "平台不能为空")
    private Integer platform;

    @AutoDocProperty("消息类型，订单消息、系统消息、手机短信")
    private String messageType;

    @AutoDocProperty("用户角色：1:租客, 2:车主")
    private Integer memRole;

    @AutoDocProperty("消息文案")
    @Length(max=200,message="消息文案长度不能超过200")
    private String content;

    @AutoDocProperty("推送需要的参数(json格式)")
    private String param;

    @AutoDocProperty("跳转页面模式，0：native方式，1：H5方式")
    private Integer pageMode;

    @AutoDocProperty("跳转链接，如果跳转页面方式为H5需要该字段")
    private String url;

    @AutoDocProperty("如果跳转页面方式为native需要该字段")
    private Integer event;

    @AutoDocProperty("如果跳转页面方式为native需要跳转地址名称")
    private String eventName;

    @AutoDocProperty("消息类型，补付消息:0")
    private String type;

    @AutoDocProperty("消息发送方式，0:手动发送，1:自动发送")
    private String operateType;

    @AutoDocProperty("操作人")
    private String createOp;

    @AutoDocProperty("补付ID")
    private Integer orderSupplementDetailId;

}
