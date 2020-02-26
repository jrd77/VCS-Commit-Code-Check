package com.atzuche.order.admin.vo.resp.remark;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderOtherInformationResponseVO {

    @AutoDocProperty(value = "租车城市")
    private String rentCity;

    @AutoDocProperty(value = "是否风控事故,1:是;0:否")
    private String riskAccidentStatus;

    @AutoDocProperty(value = "取送车备注")
    private String remarkContent;




}
