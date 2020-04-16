package com.atzuche.order.renterwz.service;

import com.atzuche.order.renterwz.entity.WzCostLogEntity;
import com.atzuche.order.renterwz.mapper.WzCostLogMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * WzCostLogService
 *
 * @author shisong
 * @date 2020/1/7
 */
@Service
public class WzCostLogService {

    @Resource
    private WzCostLogMapper wzCostLogMapper;

    public void save(WzCostLogEntity wzCostLogEntity) {
        wzCostLogMapper.saveWzCostLog(wzCostLogEntity);
    }

    public List<WzCostLogEntity> queryWzCostLogsByOrderNo(String orderNo) {
        return wzCostLogMapper.queryWzCostLogsByOrderNo(orderNo);
    }
}