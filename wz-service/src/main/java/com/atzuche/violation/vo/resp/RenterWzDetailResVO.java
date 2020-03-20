package com.atzuche.violation.vo.resp;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * RenterWzDetailResVO
 *
 * @author shisong
 * @date 2020/1/6
 */
@Data
@ToString
public class RenterWzDetailResVO {

    @AutoDocProperty("订单号")
    private String orderNo;

    @AutoDocProperty("结算状态 0未结算 1已结算")
    private String settleStatus;

    @AutoDocProperty(value="违章押金信息")
    private RenterWzInfoResVO info;

    @AutoDocProperty(value="违章押金暂扣处理")
    private RenterWzWithholdResVO withhold;

    @AutoDocProperty(value="费用详情")
    private List<RenterWzCostDetailResVO> costDetails;

    /*@AutoDocProperty("暂扣返还日志")
    private List<TemporaryRefundLogResVO> temporaryRefundLogs;*/

}
