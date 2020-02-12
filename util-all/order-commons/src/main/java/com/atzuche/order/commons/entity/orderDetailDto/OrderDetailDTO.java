package com.atzuche.order.commons.entity.orderDetailDto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class OrderDetailDTO {
    @AutoDocProperty("订单状态便跟时间")
    public String statusUpdateTIme;
    @AutoDocProperty("订单状态")
    public String orderStatus;
    @AutoDocProperty("支付来源（线上/线下）")
    public String paySource;
    @AutoDocProperty("总租期")
    public String totalRentTime;
    @AutoDocProperty("订单来源")
    public String orderSource;
    @AutoDocProperty("租期")
    public String rentTimeStr;
    @AutoDocProperty("租期")
    public String revertTimeStr;
    @AutoDocProperty("订单编号")
    public String orderNo;

}
