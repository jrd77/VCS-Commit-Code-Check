package com.atzuche.order.renterorder.service;

import com.atzuche.order.renterorder.entity.RenterDepositDetailEntity;
import com.atzuche.order.renterorder.mapper.RenterDepositDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




/**
 * 车辆押金详情
 *
 * @author ZhangBin
 * @date 2019-12-28 15:50:13
 */
@Service
public class RenterDepositDetailService{
    @Autowired
    private RenterDepositDetailMapper renterDepositDetailMapper;

    /*
     * @Author ZhangBin
     * @Date 2020/1/13 16:51
     * @Description: 获取押金减免及其详情
     *
     **/
    public RenterDepositDetailEntity queryByOrderNo(String orderNo){
        return renterDepositDetailMapper.selectByOrderNo(orderNo);
    }

}
