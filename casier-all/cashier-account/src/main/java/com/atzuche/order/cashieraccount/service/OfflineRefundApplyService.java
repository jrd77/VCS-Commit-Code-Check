package com.atzuche.order.cashieraccount.service;

import com.atzuche.order.cashieraccount.entity.OfflineRefundApplyEntity;
import com.atzuche.order.cashieraccount.mapper.OfflineRefundApplyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OfflineRefundApplyService {
    @Autowired
    private OfflineRefundApplyMapper offlineRefundApplyMapper;
    /*
     * @Author ZhangBin
     * @Date 2020/4/23 17:46
     * @Description: 根据订单号查询
     *
     **/
    public List<OfflineRefundApplyEntity> queryByOrderNo(String orderNo){
        return offlineRefundApplyMapper.getRefundApplyByOrderNo(orderNo);
    }
}
