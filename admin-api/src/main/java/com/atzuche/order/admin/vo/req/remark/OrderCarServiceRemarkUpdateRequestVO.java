package com.atzuche.order.admin.vo.req.remark;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderCarServiceRemarkUpdateRequestVO {

    @AutoDocProperty(value = "订单号")
    private String orderNo;

    @AutoDocProperty(value = "备注内容")
    private String remarkContent;


}
