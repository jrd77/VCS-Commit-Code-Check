package com.atzuche.order.commons.entity.dto;

import lombok.Data;

import java.util.Date;

/**
 * 违章费用操作日志
 *
 * @author pengcheng.fu
 * @date 2020/4/29 16:02
 */
@Data
public class WzCostLogDTO {

    /**
     *订单号
     */
    private String orderNo;
    /**
     *费用编码
     */
    private String costCode;
    /**
     *内容
     */
    private String content;
    /**
     *操作人
     */
    private String operator;
    /**
     *创建时间
     */
    private Date createTime;

}
