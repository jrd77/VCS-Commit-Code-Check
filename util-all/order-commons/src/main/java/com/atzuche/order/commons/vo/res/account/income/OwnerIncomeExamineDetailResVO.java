package com.atzuche.order.commons.vo.res.account.income;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.util.List;

/**
 * 调账成功车主收益接口
 * @author haibao.yan
 */
@Data
public class OwnerIncomeExamineDetailResVO {
    /**
     * 会员号
     */
    @AutoDocProperty("会员号")
    private String memNo;
    /**
     * 订单号
     */
    @AutoDocProperty("订单号")
    private String orderNo;

    /**
     * 类型，1收益，2调账
     */
    @AutoDocProperty("类型，1收益，2调账")
    private Integer type;
    /**
     * 订单号
     */
    @AutoDocProperty("收益明细")
    private List<OwnerIncomeExamineDetailVO> ownerIncomeExamineDetailVO;


}
