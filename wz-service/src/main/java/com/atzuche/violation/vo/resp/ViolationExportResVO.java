package com.atzuche.violation.vo.resp;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ViolationExportResVO {
    @AutoDocProperty(value = "id")
    private String id;
    @AutoDocProperty(value = "订单类型")
    private String orderType;
    @AutoDocProperty(value = "订单号")
    private String orderNo;
    @AutoDocProperty(value = "取车时间")
    private String rentTime;
    @AutoDocProperty(value = "还车时间")
    private String revertTime;
    @AutoDocProperty(value = "车牌号")
    private String plateNum;
    @AutoDocProperty(value = "车架号")
    private String frameNo;
    @AutoDocProperty(value = "发动机号")
    private String engineNo;
    @AutoDocProperty(value = "用车城市")
    private String rentCity;
    @AutoDocProperty(value = "违章时间")
    private String illegalTime;
    @AutoDocProperty(value = "违章地点")
    private String illegalAddr;
    @AutoDocProperty(value = "违章原因")
    private String illegalReason;
    @AutoDocProperty(value = "违章罚金")
    private String illegalAmt;
    @AutoDocProperty(value = "扣分")
    private String illegalDeduct;
    @AutoDocProperty(value = "协助违章处理费")
    private String illegalFine;
    @AutoDocProperty(value = "凹凸代办服务费")
    private String illegalServiceCost;
    @AutoDocProperty(value = "不良用车处罚金")
    private String illegalDysFine;
    @AutoDocProperty(value = "途径城市")
    private String cities;
    @AutoDocProperty(value = "暂扣租车押金")
    private String illegalPauseCost;
    @AutoDocProperty(value = "车辆类型")
    private String carType;
    @AutoDocProperty(value = "违章超证费")
    private String illegalSupercerCost;
    @AutoDocProperty(value = "动力源")
    private String powerType;
}
