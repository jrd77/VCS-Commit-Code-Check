package com.atzuche.order.coreapi.service;

import com.atzuche.order.coreapi.entity.dto.CancelOrderResDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 租客取消
 *
 * @author pengcheng.fu
 * @date 2020/1/7 16:21
 */

@Service
public class RenterCancelOrderService {

    /**
     * 取消处理
     *
     * @return CancelOrderResDTO 返回信息
     */
    @Transactional(rollbackFor = Exception.class)
    public CancelOrderResDTO cancel() {
        //校验
        //todo

        //调度判定
        //todo

        //罚金计算(罚金和收益)
        //todo

        //订单状态更新
        //todo

        //去库存
        //todo

        //落库
        //todo


        return null;
    }


}
