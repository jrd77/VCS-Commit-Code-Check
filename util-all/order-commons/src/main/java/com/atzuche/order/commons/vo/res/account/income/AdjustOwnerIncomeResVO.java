package com.atzuche.order.commons.vo.res.account.income;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.util.List;

/**
 * 调账成功车主收益接口
 * @author haibao.yan
 */
@Data
public class AdjustOwnerIncomeResVO {
    @AutoDocProperty("成功审核的收益编号")
    private Integer examineId;
    @AutoDocProperty("会员号")
    private String memNo;
    @AutoDocProperty("订单号")
    private String orderNo;
}
