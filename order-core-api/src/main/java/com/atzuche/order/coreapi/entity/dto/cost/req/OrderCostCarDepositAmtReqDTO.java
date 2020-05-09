package com.atzuche.order.coreapi.entity.dto.cost.req;

import com.atzuche.order.commons.entity.dto.RenterMemberRightDTO;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 计算车辆押金参数
 *
 * @author pengcheng.fu
 * @date 2020/4/1 11:29
 */

@Data
public class OrderCostCarDepositAmtReqDTO {

    /**
     * 城市code
     */
    private Integer cityCode;

    /**
     * 车辆指导价格
     */
    private Integer guidPrice;

    /**
     * 车辆残值
     */
    private Integer surplusPrice;

    /**
     * 车辆品牌
     */
    private String brand;
    /**
     * 车型
     */
    private String type;
    /**
     * 行驶证注册年月
     */
    private LocalDate licenseDay;

    /**
     * 免押方式ID
     */
    private Integer freeDoubleTypeId;

    /**
     * 会员权益列表
     */
    private List<RenterMemberRightDTO> renterMemberRightDTOList;



}
