package com.atzuche.order.commons.vo;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class LianHeMaiTongOrderVO {
    @AutoDocProperty("订单号")
    private String orderNo;
    @AutoDocProperty("订单类型")
    private String category;
    @AutoDocProperty("订单状态")
    private String orderStatus;
    @AutoDocProperty("订单开始时间")
    private String expRentTime;
    @AutoDocProperty("订单结束时间")
    private String expRevertTime;
    @AutoDocProperty("是否取还车")
    private String isGetReturnCar;
    @AutoDocProperty("预计提前取车时间")
    private String showRentTime;
    @AutoDocProperty("预计延后还车时间")
    private String showRevertTime;
    @AutoDocProperty("交易请求时间")
    private String reqTime;
    @AutoDocProperty("一审结果")
    private String firstAuditStatus;
    @AutoDocProperty("二审结果")
    private String secondAuditStatus;
    @AutoDocProperty("GPS审核结果")
    private String gpsAuditStatus;
    @AutoDocProperty("车牌号")
    private String carPlateNum;
    @AutoDocProperty("车型")
    private String carTypeTxt;
    @AutoDocProperty("取车城市")
    private String rentCity;
    @AutoDocProperty("租客租金")
    private String rentAmt;
    @AutoDocProperty("车主姓名")
    private String ownerName;
    @AutoDocProperty("车主会员号")
    private String ownerMemNo;
    @AutoDocProperty("车主手机号")
    private String ownerPhone;
    @AutoDocProperty("租客姓名")
    private String renterName;
    @AutoDocProperty("租客会员号")
    private String renterMemNo;
    @AutoDocProperty("租客手机号")
    private String renterPhone;
}
