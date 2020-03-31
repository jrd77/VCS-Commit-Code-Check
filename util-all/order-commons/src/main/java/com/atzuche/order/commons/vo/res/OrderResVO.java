package com.atzuche.order.commons.vo.res;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

/**
 * 下单返回信息
 *
 * @author pengcheng.fu
 * @date 2020/1/2 16:26
 */

@Data
public class OrderResVO {

    @AutoDocProperty(value = "订单号")
    private String orderNo;

    @AutoDocProperty(value = "订单状态:1.待确认 4.待支付 8.待调度(需要调度的订单才有该状态) 16.待取车 32.待还车 64.待结算 128.待违章结算 256.待理赔处理 512.完成 0.结束")
    private String status;

    /**
     * 应答标识位，0未设置，1已设置
     */
    @AutoDocProperty("应答标识位，0未设置，1已设置")
    private Integer replyFlag;
}
