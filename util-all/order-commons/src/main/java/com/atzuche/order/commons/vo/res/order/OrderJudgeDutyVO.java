package com.atzuche.order.commons.vo.res.order;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

/**
 * 订单取消/申诉判责信息
 *
 * @author pengcheng.fu
 * @date 2020/3/12 14:04
 */

@Data
public class OrderJudgeDutyVO {

    @AutoDocProperty(value = "主键ID")
    private Integer id;

    @AutoDocProperty(value = "主订单号")
    private String orderNo;

    @AutoDocProperty(value = "租客子订单号")
    private String renterOrderNo;

    @AutoDocProperty(value = "车主子订单号")
    private String ownerOrderNo;

    @AutoDocProperty(value = "操作名称")
    private String operateName;

    @AutoDocProperty(value = "操作方")
    private String optSource;

    @AutoDocProperty(value = "操作原因")
    private String optReason;

    @AutoDocProperty(value = "操作时间:yyyy-MM-dd HH:mm:ss")
    private String optTime;

    @AutoDocProperty(value = "责任判定")
    private String dutyource;

    @AutoDocProperty(value = "违约罚金")
    private String penaltyAmt;

    @AutoDocProperty(value = "保险罚金")
    private String insuranceAmt;

    @AutoDocProperty(value = "判责操作人")
    private String judgeDutyjOperator;

    @AutoDocProperty(value = "判责操作时间:yyyy-MM-dd HH:mm:ss")
    private String judgeDutyjOptTime;

    @AutoDocProperty(value = "是否需要手动判责:1,是 0,否")
    private String isManualCondemn;


}
