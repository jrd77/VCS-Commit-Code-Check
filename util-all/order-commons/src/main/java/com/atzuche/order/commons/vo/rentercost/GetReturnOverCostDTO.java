package com.atzuche.order.commons.vo.rentercost;

import lombok.Data;

import java.util.List;
/*
 * @Author ZhangBin
 * @Date 2019/12/26 11:40
 * @Description: 超运能费用
 *
 **/

@Data
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
