package com.atzuche.order.transport.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 超运能（暂时放这里 后期等提供出运能接口直接调）
 */
@Data
@ToString
public class GetReturnOverCostDTO {

    /**
     * 可能需要用到的值
     */
    private GetReturnOverTransportDTO getReturnOverTransportDTO;
}
