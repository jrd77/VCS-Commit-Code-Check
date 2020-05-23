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
    @AutoDocProperty("订单号,补付消息类型需要该字段")
    @NotNull(message = "订单号不能为空")
    private Long orderNo;

    @AutoDocProperty("平台，0：app，1：短信")
    @NotNull(message = "平台不能为空")
    private Integer platform;

    @AutoDocProperty("消息类型，1 订单消息、2 系统消息、3 手机短信")
    private String messageType;

    @AutoDocProperty("消息文案")
    @Length(max=200,message="消息文案长度不能超过200")
    private String content;

    @AutoDocProperty("跳转链接，如果跳转页面方式为H5需要该字段")
    private String url;

    @AutoDocProperty("274:订单详情 286：补付详情")
    private Integer event;

    @AutoDocProperty("操作人")
    private String createOp;

    @AutoDocProperty("补付ID")
    private Integer orderSupplementDetailId;

    @AutoDocProperty("补付项目")
    private String item;

    @AutoDocProperty("补付金额")
    private String amount;

    @AutoDocProperty("发送人手机号")
    private String mobile;

}
