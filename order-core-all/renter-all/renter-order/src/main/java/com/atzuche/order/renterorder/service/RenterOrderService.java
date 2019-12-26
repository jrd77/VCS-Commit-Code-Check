package com.atzuche.order.renterorder.service;

import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.mapper.RenterOrderMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * 租客订单子表
 *
 * @author ZhangBin
 * @date 2019-12-14 17:24:31
 */
@Service
public class RenterOrderService {

    @Resource
    private RenterOrderMapper renterOrderMapper;


    public List<RenterOrderEntity> listAgreeRenterOrderByOrderNo(String orderNo) {
        return renterOrderMapper.listAgreeRenterOrderByOrderNo(orderNo);
    }
    
    /**
     * 获取有效的租客子单
     * @param orderNo 主订单号
     * @return RenterOrderEntity
     */
    public RenterOrderEntity getRenterOrderByOrderNoAndIsEffective(String orderNo) {
    	return renterOrderMapper.getRenterOrderByOrderNoAndIsEffective(orderNo);
    }




    public void saveRenterOrderInfo() {
        //1.租客订单处理




    }
}
