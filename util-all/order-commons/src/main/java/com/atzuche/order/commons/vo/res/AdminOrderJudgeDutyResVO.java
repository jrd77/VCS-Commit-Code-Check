package com.atzuche.order.commons.vo.res;

import com.atzuche.order.commons.vo.res.order.OrderJudgeDutyVO;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.util.List;

/**
 * 订单取消/申诉判责列表
 *
 * @author pengcheng.fu
 * @date 2020/3/12 14:00
 */

@Data
public class AdminOrderJudgeDutyResVO {

    @AutoDocProperty(value = "订单取消/申诉判责列表")
    private List<OrderJudgeDutyVO> orderJudgeDuties;


}
