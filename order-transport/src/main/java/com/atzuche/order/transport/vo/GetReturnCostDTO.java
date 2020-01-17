package com.atzuche.order.transport.vo;

import com.atzuche.order.commons.entity.dto.RenterOrderSubsidyDetailDTO;
import com.atzuche.order.commons.vo.res.rentcosts.RenterOrderCostDetailEntity;
import lombok.Data;

import java.util.List;

/*
 * @Author ZhangBin
 * @Date 2019/12/25 20:46
 * @Description: 取还车费用明细 + 原始值
 * 
 **/

@Data
public class GetReturnCostDTO {
    /**
     * 租客取还车费用明细列表
     */
    private List<RenterOrderCostDetailEntity> renterOrderCostDetailEntityList;
    /**
     * 租客补贴明细列表
     */
    private List<RenterOrderSubsidyDetailDTO> renterOrderSubsidyDetailDTOList;
    /**
     * 取还车费用原始值
     */
    private GetReturnResponseVO getReturnResponseVO;
}
