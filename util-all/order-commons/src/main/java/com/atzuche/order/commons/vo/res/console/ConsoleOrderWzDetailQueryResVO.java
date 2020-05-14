package com.atzuche.order.commons.vo.res.console;

import com.atzuche.order.commons.entity.dto.WzDepositMsgDTO;
import com.atzuche.order.commons.entity.orderDetailDto.OrderStatusDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterOrderDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterOrderWzCostDetailDTO;
import lombok.Data;

import java.util.List;

/**
 * 管理后台违章明细查询需要的订单数据
 *
 * @author pengcheng.fu
 * @date 2020/4/29 11:52
 */

@Data
public class ConsoleOrderWzDetailQueryResVO {

    /**
     * 订单状态信息
     */
    private OrderStatusDTO orderStatus;

    /**
     * 违章押金信息
     */
    private WzDepositMsgDTO wzDepositMsg;

    /**
     * 违章费用信息
     */
    private List<RenterOrderWzCostDetailDTO> dtos;

    /**
     * 租客订单信息
     */
    private RenterOrderDTO renterOrderDTO;




}
