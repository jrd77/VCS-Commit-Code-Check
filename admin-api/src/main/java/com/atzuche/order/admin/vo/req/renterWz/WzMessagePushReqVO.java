package com.atzuche.order.admin.vo.req.renterWz;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 违章消息推送
 */
@Data
@EqualsAndHashCode
public class WzMessagePushReqVO {
    @AutoDocProperty("订单号")
    @NotNull(message = "订单号不能为空")
    private Long orderNo;

    @AutoDocProperty("会员号,发短信需要该字段")
    @NotNull(message = "会员号不能为空")
    private Long memNo;

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

    @AutoDocProperty("违章序号")
    private Long orderViolationId;

}
