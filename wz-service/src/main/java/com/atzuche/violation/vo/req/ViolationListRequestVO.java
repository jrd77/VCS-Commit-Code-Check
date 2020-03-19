package com.atzuche.violation.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * Created by qincai.lin on 2020/3/16.
 */
@Data
@ToString
public class ViolationListRequestVO {

    @AutoDocProperty(value = "订单号")
    private String orderNo; // 订单号

}
