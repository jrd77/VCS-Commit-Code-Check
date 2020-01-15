package com.atzuche.order.transport.vo;

import com.atzuche.order.transport.entity.RenterOrderCostDetailEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author 胡春林
 * 超运能（暂时放这里 后期等提供出运能接口直接调）
 */
@Data
@ToString
public class GetReturnOverCostDTO {

    /**
     * 租客取还车超运能费用明细列表
     */
    private List<RenterOrderCostDetailEntity> renterOrderCostDetailEntityList;
    /**
     * 可能需要用到的值
     */
    private GetReturnOverTransportDTO getReturnOverTransportDTO;
}
