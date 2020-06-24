package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.entity.wz.RenterOrderWzDetailLogEntity;
import com.atzuche.order.coreapi.mapper.RenterOrderWzDetailLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RenterOrderWzDetailLogService {
    @Autowired
    private RenterOrderWzDetailLogMapper renterOrderWzDetailLogMapper;

    /*
     * @Author ZhangBin
     * @Date 2020/6/8 14:43
     * @Description: 添加数据
     *
     **/
    public int insert(RenterOrderWzDetailLogEntity renterOrderWzDetailLogEntity){
        return renterOrderWzDetailLogMapper.insertSelective(renterOrderWzDetailLogEntity);
    }

}
