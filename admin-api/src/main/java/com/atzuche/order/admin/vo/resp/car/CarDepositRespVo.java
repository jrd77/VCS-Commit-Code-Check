package com.atzuche.order.admin.vo.resp.car;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class CarDepositRespVo {
    @AutoDocProperty("车辆押金")
    private Integer carDepositMonty;

    @AutoDocProperty("平台任务减免")
    private Integer originalTotalAmt;

    @AutoDocProperty("应收")
    private Integer receivableMonty;

    @AutoDocProperty(value="支付方式")
    private String  payType;

    @AutoDocProperty(value="支付类型")
    private String depositType;

    @AutoDocProperty("减免金额")
    private String reliefAmtStr;
    @AutoDocProperty("支付时间")
    private String payDateStr;

    @AutoDocProperty("支付状态")
    private String payStatusStr;
}
