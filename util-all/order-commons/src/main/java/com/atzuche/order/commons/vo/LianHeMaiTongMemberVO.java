package com.atzuche.order.commons.vo;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
public class LianHeMaiTongMemberVO {
    @AutoDocProperty("订单号")
    private String orderNo;
    @AutoDocProperty("订单类型")
    private String category;
    @AutoDocProperty("订单状态")
    private Integer orderStatus;
    @AutoDocProperty("订单开始时间")
    private String rentTime;
    @AutoDocProperty("订单结束时间")
    private String reverrTime;
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

}
