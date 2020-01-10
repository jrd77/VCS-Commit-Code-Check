package com.atzuche.order.admin.vo.req.remark;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderCarServiceRemarkRequestVO {

    @AutoDocProperty(value = "订单号")
    private String orderNo;

    @AutoDocProperty(value = "取还车备注内容")
    private String remarkContent;





}
