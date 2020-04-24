package com.atzuche.order.coreapi.entity.dto.cost.req;

import com.atzuche.order.commons.entity.dto.RenterMemberRightDTO;
import lombok.Data;

import java.util.List;

/**
 * 计算违章押金参数
 *
 * @author pengcheng.fu
 * @date 2020/4/1 11:32
 */

@Data
public class OrderCostViolationDepositAmtReqDTO {

    /**
     * 城市code
     */
    private Integer cityCode;
    /**
     * 车牌号
     */
    private String carPlateNum;

    /**
     * 免押方式ID
     */
    private Integer freeDoubleTypeId;

    /**
     * 租客权益列表
     */
    private List<RenterMemberRightDTO> renterMemberRightDTOList;
}
