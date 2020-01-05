package com.atzuche.order.admin.service;

import com.atzuche.order.admin.vo.req.DeliveryCarRepVO;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 胡春林
 * 配送信息服务
 */
@Service
public class DeliveryCarInfoService {

    @Autowired
    DeliveryCarService deliveryCarService;
    @Autowired
    RenterOrderDeliveryService renterOrderDeliveryService;

    /**
     * 获取配送相关信息
     * @param deliveryCarDTO
     * @return
     */
    public DeliveryCarRepVO findDeliveryListByOrderNo(DeliveryCarRepVO deliveryCarDTO) {
        List<RenterOrderDeliveryEntity> renterOrderDeliveryEntityList = renterOrderDeliveryService.listRenterOrderDeliveryByRenterOrderNo(deliveryCarDTO.getOrderNo());
        return null;
    }
}
