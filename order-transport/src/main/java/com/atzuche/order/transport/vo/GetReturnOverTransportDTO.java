package com.atzuche.order.transport.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 */
@Data
@ToString
public class GetReturnOverTransportDTO {

    /**
     * 取车时间是否超出运能 :true-超出运能，false-未超出
     */
    private Boolean isGetOverTransport;
    /**
     * 取车时间超出运能附加费用
     */
    private Integer getOverTransportFee;

    /**
     * 取车时间超出运能附加费用(夜间)
     */
    private Integer nightGetOverTransportFee;

    /**
     * 还车时间是否超出运能:true-超出运能，false-未超出
     */
    private Boolean isReturnOverTransport;
    /**
     * 还车时间超出运能附加费用
     */
    private Integer returnOverTransportFee;

    /**
     * 还车时间超出运能附加费用(夜间)
     */
    private Integer nightReturnOverTransportFee;

    /**
     * 是否变更订单起租时间(修改订单)
     */
    private Boolean isUpdateRentTime;

    /**
     * 是否变更订单还车时间(修改订单)
     */
    private Boolean isUpdateRevertTime;

    public GetReturnOverTransportDTO(Boolean isGetOverTransport, Integer getOverTransportFee, Boolean isReturnOverTransport, Integer returnOverTransportFee) {
        this.isGetOverTransport = isGetOverTransport;
        this.getOverTransportFee = getOverTransportFee;
        this.isReturnOverTransport = isReturnOverTransport;
        this.returnOverTransportFee = returnOverTransportFee;
    }

}
