package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.vo.req.NormalOrderCostCalculateReqVO;
import com.atzuche.order.commons.vo.res.NormalOrderCostCalculateResVO;
import org.springframework.stereotype.Service;

/**
 * 下单前费用计算
 *
 * @author pengcheng.fu
 * @date 2020/1/11 16:11
 */
@Service
public class SubmitOrderBeforeCostCalService {


    /**
     * 下单前费用计算
     *
     * @param reqVO 请求参数
     * @return NormalOrderCostCalculateResVO 返回信息
     */
    public NormalOrderCostCalculateResVO costCalculate(NormalOrderCostCalculateReqVO reqVO) {

        //TODO:租车费用处理


        //TODO:抵扣费用处理

        //TODO:车辆押金处理

        //TODO:违章押金处理

        //TODO:租车费用小计处理

        //TODO:待支付信息处理

        return new NormalOrderCostCalculateResVO();

    }


}
