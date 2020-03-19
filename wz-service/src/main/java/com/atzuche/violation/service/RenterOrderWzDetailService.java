package com.atzuche.violation.service;

import com.atzuche.violation.entity.RenterOrderWzDetailEntity;
import com.atzuche.violation.mapper.RenterOrderWzDetailMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * RenterOrderWzDetailService
 *
 * @author shisong
 * @date 2019/12/30
 */
@Service
public class RenterOrderWzDetailService {

    @Resource
    private RenterOrderWzDetailMapper renterOrderWzDetailMapper;


    public void updateIllegalStatus(String orderNo){
        renterOrderWzDetailMapper.updateIllegalStatus(orderNo);
    }


}
