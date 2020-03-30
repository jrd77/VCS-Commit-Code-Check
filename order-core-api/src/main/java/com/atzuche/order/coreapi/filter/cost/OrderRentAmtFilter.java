package com.atzuche.order.coreapi.filter.cost;

import com.atzuche.order.commons.entity.dto.RentAmtDTO;
import com.atzuche.order.coreapi.entity.dto.cost.OrderCostContext;
import com.atzuche.order.coreapi.submitOrder.exception.OrderCostFilterException;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostCombineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 计算租金
 *
 * @author pengcheng.fu
 * @date 2020/310:59
 */
@Service
public class OrderRentAmtFilter implements OrderCostFilter {

    @Autowired
    RenterOrderCostCombineService renterOrderCostCombineService;

    @Override
    public void calculate(OrderCostContext context) throws OrderCostFilterException {
        RentAmtDTO rentAmtDTO = new RentAmtDTO();
        List<RenterOrderCostDetailEntity> renterOrderCostDetailEntities = renterOrderCostCombineService.listRentAmtEntity(rentAmtDTO);
        int rentAmt =
                renterOrderCostDetailEntities.stream().mapToInt(RenterOrderCostDetailEntity::getTotalAmount).sum();

    }
}
