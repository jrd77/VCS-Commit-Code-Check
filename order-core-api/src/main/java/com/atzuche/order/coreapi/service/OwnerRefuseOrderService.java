package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.vo.req.AgreeOrderReqVO;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.commons.vo.req.RefuseOrderReqVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 车主拒绝订单
 *
 * @author pengcheng.fu
 * @date 2020/1/9 16:57
 */

@Service
public class OwnerRefuseOrderService {

    private static Logger logger = LoggerFactory.getLogger(OwnerRefuseOrderService.class);


    /**
     * 车主拒单
     *
     * @param reqVO 请求参数
     */
    public void refuse(RefuseOrderReqVO reqVO) {
        //TODO:车主拒绝前置校验





        //TODO:发送车主拒绝事件
    }


}
