package com.atzuche.order.commons.vo.req;


import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SearchMemberOrderDebtListReqVO extends PageBean {
    @AutoDocProperty("会员号")
    private String memNo;
    @AutoDocProperty("订单号")
    private String orderNo;
}
