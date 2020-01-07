package com.atzuche.order.admin.vo.req.order;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by qincai.lin on 2019/12/30.
 */
@Data
@ToString
public class OrderStatusRequestVO implements Serializable {
    @AutoDocProperty(value = "订单号")
    private String orderNo;

}
