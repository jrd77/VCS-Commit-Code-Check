package com.atzuche.order.renterwz.service;

import com.atzuche.order.renterwz.mapper.RenterOrderWzFinishedTodoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * RenterOrderWzFinishedTodoService
 *
 * @author shisong
 * @date 2020/1/2
 */
@Service
public class RenterOrderWzFinishedTodoService {

    @Resource
    private RenterOrderWzFinishedTodoMapper renterOrderWzFinishedTodoMapper;

    public void updateStatus(Integer id) {
        renterOrderWzFinishedTodoMapper.updateStatus(id);
    }
}
