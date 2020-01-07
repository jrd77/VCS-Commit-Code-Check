package com.atzuche.order.admin.vo.req.remark;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderRemarkInformationRequestVO {

    @AutoDocProperty(value = "备注id")
    private String remarkId;


}
