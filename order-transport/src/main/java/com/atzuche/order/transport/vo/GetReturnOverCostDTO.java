package com.atzuche.order.transport.vo;

import com.atzuche.order.transport.entity.RenterOrderCostDetailDTO;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author 胡春林
 * 超运能
 */
@Data
@ToString
public class GetReturnOverCostDTO {

    /**
     * 租客取还车超运能费用明细列表
     */
    private List<RenterOrderCostDetailDTO> renterOrderCostDetailEntityList;
    /**
     * 可能需要用到的值
     */
    private GetReturnOverTransportDTO getReturnOverTransportDTO;
}