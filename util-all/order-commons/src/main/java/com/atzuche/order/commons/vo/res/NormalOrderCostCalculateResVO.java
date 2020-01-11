package com.atzuche.order.commons.vo.res;

import com.atzuche.order.commons.vo.res.order.CostItemVO;
import com.atzuche.order.commons.vo.res.order.ReductionVO;
import com.autoyol.doc.annotation.AutoDocProperty;

import java.util.List;

/**
 *
 *
 * @author pengcheng.fu
 * @date 2020/1/11 14:17
 */


public class NormalOrderCostCalculateResVO {


    @AutoDocProperty(value = "费用信息列表")
    private List<CostItemVO> costItemList;

    @AutoDocProperty(value = "费用信息列表")
    private ReductionVO reductionVO;


}
