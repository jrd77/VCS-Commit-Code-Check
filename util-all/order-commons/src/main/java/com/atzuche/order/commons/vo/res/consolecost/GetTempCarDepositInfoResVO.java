package com.atzuche.order.commons.vo.res.consolecost;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.util.List;

/**
 * 订单暂扣车辆押金费用明细
 *
 * @author pengcheng.fu
 * @date 2020/4/23 17:14
 */
@Data
public class GetTempCarDepositInfoResVO {

    @AutoDocProperty(value = "订单号")
    private String orderNo;

    @AutoDocProperty(value = "租客会员号")
    private String memNo;

    @AutoDocProperty(value = "是否可编辑:0,否 1,是")
    private String isEdit;

    @AutoDocProperty(value = "订单车辆押金暂扣扣款信息列表")
    List<TempCarDepoistInfoResVO> tempCarDepoists;


}
