package com.atzuche.order.settle.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class CancelOrderReqDTO {
    @AutoDocProperty(value = "主订单号")
    private String orderNo;

    @AutoDocProperty(value = "租客子订单号")
    private String renterOrderNo;

    @AutoDocProperty(value = "车主子订单号")
    private String ownerOrderNo;

    /**
     * 当为true时，需传递orderNo、ownerOrderNo
     */
    @AutoDocProperty(value = "结算车主端标志，true：需要结算车主，false：不需要结算车主")
    private boolean settleOwnerFlg;

    /**
     * 当为true时，需传递orderNo、renterOrderNo
     */
    @AutoDocProperty(value = "结算租客端标志，true：需要结算租客，false：不需要结算租客")
    private boolean settleRenterFlg;

}
