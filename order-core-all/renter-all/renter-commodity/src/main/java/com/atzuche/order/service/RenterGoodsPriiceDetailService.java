package com.atzuche.order.service;

import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.coreapi.entity.dto.OrderContextDto;
import com.autoyol.car.api.model.vo.CarPriceOfDayVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.mapper.RenterGoodsPriiceDetailMapper;
import com.atzuche.order.entity.RenterGoodsPriiceDetailEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * 商品概览价格明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 18:06:32
 */
@Service
public class RenterGoodsPriiceDetailService{
    @Autowired
    private RenterGoodsPriiceDetailMapper renterGoodsPriiceDetailMapper;

    public void insert(OrderContextDto orderContextDto,Integer goodsId){
        List<CarPriceOfDayVO> daysPrice = orderContextDto.getCarDetailVO().getDaysPrice();
        List<RenterGoodsPriiceDetailEntity> list = new ArrayList<>();
        daysPrice.stream().forEach(x->{
            RenterGoodsPriiceDetailEntity renterGoodsPriiceDetailEntity = new RenterGoodsPriiceDetailEntity();
            renterGoodsPriiceDetailEntity.setOrderNo(orderContextDto.getOrderNo());
            renterGoodsPriiceDetailEntity.setRenterOrderNo("");
            renterGoodsPriiceDetailEntity.setGoodsId(goodsId);
            renterGoodsPriiceDetailEntity.setCarDay(LocalDateTimeUtils.parseStringToLocalDate(x.getDateStr()));
            renterGoodsPriiceDetailEntity.setCarUnitPrice(x.getPrice());
            list.add(renterGoodsPriiceDetailEntity);
        });

    }
}
