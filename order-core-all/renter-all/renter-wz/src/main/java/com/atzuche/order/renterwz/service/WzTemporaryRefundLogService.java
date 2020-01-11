package com.atzuche.order.renterwz.service;

import com.atzuche.order.renterwz.entity.WzTemporaryRefundLogEntity;
import com.atzuche.order.renterwz.mapper.WzTemporaryRefundLogMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * WzTemporaryRefundLogService
 *
 * @author shisong
 * @date 2020/1/7
 */
@Service
public class WzTemporaryRefundLogService {

    @Resource
    private WzTemporaryRefundLogMapper wzTemporaryRefundLogMapper;

    public List<WzTemporaryRefundLogEntity> queryTemporaryRefundLogsByOrderNo(String orderNo) {
        return wzTemporaryRefundLogMapper.queryTemporaryRefundLogsByOrderNo(orderNo);
    }

    public void save(WzTemporaryRefundLogEntity dto) {
        wzTemporaryRefundLogMapper.saveWzTemporaryRefundLog(dto);
    }
}
