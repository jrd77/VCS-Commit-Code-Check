package com.atzuche.order.renterwz.service;

import com.atzuche.order.renterwz.mapper.IllegalAppealMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * IllegalAppealService
 *
 * @author shisong
 * @date 2020/1/15
 */
@Service
public class IllegalAppealService {

    @Resource
    private IllegalAppealMapper illegalAppealMapper;

    public Integer getIllegalAppealCount(String orderNo, String illegalNum) {
        return illegalAppealMapper.getIllegalAppealCount(orderNo,illegalNum);
    }
}
