package com.atzuche.order.rentercommodity.service;

import com.atzuche.order.commons.entity.dto.*;
import com.atzuche.order.rentercommodity.entity.RenterGoodsEntity;
import com.atzuche.order.rentercommodity.entity.RenterGoodsPriceDetailEntity;
import com.atzuche.order.rentercommodity.mapper.RenterGoodsMapper;
import com.atzuche.order.rentercommodity.mapper.RenterGoodsPriceDetailMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * 租客商品概览表
 *
 * @author ZhangBin
 * @date 2019-12-11 18:06:32
 */
@Service
public class RenterGoodsService{
    @Autowired
    private RenterGoodsMapper renterGoodsMapper;
    @Autowired
    private RenterGoodsPriceDetailMapper renterGoodsPriceDetailMapper;

    public void save(OrderContextDto orderContextDto){
        RenterGoodsDetailDto renterGoodsDetailDto = orderContextDto.getRenterGoodsDetailDto();
        RenterGoodsEntity goodsEntity = new RenterGoodsEntity();
        BeanUtils.copyProperties(renterGoodsDetailDto,goodsEntity);
        //goodsEntity.setCreateOp();
        //goodsEntity.setUpdateOp();
        renterGoodsMapper.insert(goodsEntity);

        List<RenterGoodsPriceDetailDto> goodsPriceDetailDtoList = renterGoodsDetailDto.getRenterGoodsPriceDetailDtoList();
        List<RenterGoodsPriceDetailEntity> goodsPriceList = new ArrayList<>();
        goodsPriceDetailDtoList.forEach(x->{
            RenterGoodsPriceDetailEntity goodsPriceDetailEntity = new RenterGoodsPriceDetailEntity();
            goodsPriceDetailEntity.setOrderNo(x.getOrderNo());
            goodsPriceDetailEntity.setRenterOrderNo(x.getRenterOrderNo());
            goodsPriceDetailEntity.setGoodsId(goodsEntity.getId());
            goodsPriceDetailEntity.setCarDay(x.getCarDay());
            goodsPriceDetailEntity.setCarUnitPrice(x.getCarUnitPrice());
            goodsPriceDetailEntity.setCarHourCount(x.getCarHourCount());
            goodsPriceDetailEntity.setRevertTime(x.getRevertTime());
            goodsPriceList.add(goodsPriceDetailEntity);
        });
        renterGoodsPriceDetailMapper.insertList(goodsPriceList);
    }

    /**
     *
     * @param renterOrderNo 租客订单号
     * @param isNeedPrice 是否需要价格信息 true-需要  false-不需要
     * @return
     */
    public RenterGoodsDetailDto getRenterGoodsDetail(String renterOrderNo, boolean isNeedPrice){
        RenterGoodsEntity renterGoodsEntity = renterGoodsMapper.selectByRenterOrderNo(renterOrderNo);
        RenterGoodsDetailDto renterGoodsDetailDto  = new RenterGoodsDetailDto();
        BeanUtils.copyProperties(renterGoodsDetailDto,renterGoodsEntity);
        if(!isNeedPrice){
            return renterGoodsDetailDto;
        }
        List<RenterGoodsPriceDetailEntity> renterGoodsPriceDetailEntities = renterGoodsPriceDetailMapper.selectByRenterOrderNo(renterOrderNo);
        List<RenterGoodsPriceDetailDto> renterGoodsPriceDetailDtoList = new ArrayList<>();
        renterGoodsPriceDetailEntities.forEach(x->{
            RenterGoodsPriceDetailDto renterGoodsPriceDetailDto = new RenterGoodsPriceDetailDto();
            BeanUtils.copyProperties(renterGoodsPriceDetailDto,x);
            renterGoodsPriceDetailDtoList.add(renterGoodsPriceDetailDto);
        });
        renterGoodsDetailDto.setRenterGoodsPriceDetailDtoList(renterGoodsPriceDetailDtoList);
        return renterGoodsDetailDto;
    }
}
