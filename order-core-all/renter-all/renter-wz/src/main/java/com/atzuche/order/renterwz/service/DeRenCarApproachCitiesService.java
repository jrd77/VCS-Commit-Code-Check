package com.atzuche.order.renterwz.service;

import com.atzuche.order.renterwz.entity.DerenCarApproachCitysEntity;
import com.atzuche.order.renterwz.mapper.DerenCarApproachCitysMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * DeRenCarApproachCitiesService
 *
 * @author shisong
 * @date 2020/1/8
 */
@Service
public class DeRenCarApproachCitiesService {

    @Resource
    private DerenCarApproachCitysMapper derenCarApproachCitysMapper;

    public void save(DerenCarApproachCitysEntity deRenCarApproachCities) {
        derenCarApproachCitysMapper.saveDerenCarApproachCitys(deRenCarApproachCities);
    }

    public String queryCitiesByOrderNoAndCarNum(String orderNo, String plateNum) {
        return derenCarApproachCitysMapper.queryCitiesByOrderNoAndCarNum(orderNo,plateNum);
    }
}
