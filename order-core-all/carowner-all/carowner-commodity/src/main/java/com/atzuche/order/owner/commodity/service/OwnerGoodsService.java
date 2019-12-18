package com.atzuche.order.owner.commodity.service;

import com.atzuche.order.commons.entity.dto.OrderContextDto;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDto;
import com.atzuche.order.commons.entity.dto.OwnerGoodsPriceDetailDto;
import com.atzuche.order.owner.commodity.entity.OwnerGoodsEntity;
import com.atzuche.order.owner.commodity.entity.OwnerGoodsPriceDetailEntity;
import com.atzuche.order.owner.commodity.mapper.OwnerGoodsMapper;
import com.atzuche.order.owner.commodity.mapper.OwnerGoodsPriceDetailMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * 车主端商品概览表
 *
 * @author ZhangBin
 * @date 2019-12-17 20:30:11
 */
@Service
public class OwnerGoodsService{
    @Autowired
    private OwnerGoodsMapper ownerGoodsMapper;
    @Autowired
    private OwnerGoodsPriceDetailMapper ownerGoodsPriceDetailMapper;

    public void save(OrderContextDto orderContextDto){
        OwnerGoodsDetailDto ownerGoodsDetailDto = orderContextDto.getOwnerGoodsDetailDto();
        OwnerGoodsEntity goodsEntity = new OwnerGoodsEntity();
        BeanUtils.copyProperties(ownerGoodsDetailDto,goodsEntity);
        //goodsEntity.setCreateOp();
        //goodsEntity.setUpdateOp();
        ownerGoodsMapper.insert(goodsEntity);

        List<OwnerGoodsPriceDetailDto> goodsPriceDetailDtoList = ownerGoodsDetailDto.getOwnerGoodsPriceDetailDtoList();
        List<OwnerGoodsPriceDetailEntity> goodsPriceList = new ArrayList<>();
        goodsPriceDetailDtoList.forEach(x->{
            OwnerGoodsPriceDetailEntity goodsPriceDetailEntity = new OwnerGoodsPriceDetailEntity();
            goodsPriceDetailEntity.setOrderNo(x.getOrderNo());
            goodsPriceDetailEntity.setOwnerOrderNo(x.getOwnerOrderNo());
            goodsPriceDetailEntity.setGoodsId(goodsEntity.getId());
            goodsPriceDetailEntity.setCarDay(x.getCarDay());
            goodsPriceDetailEntity.setCarUnitPrice(x.getCarUnitPrice());
            goodsPriceDetailEntity.setCarHourCount(x.getCarHourCount());
            goodsPriceDetailEntity.setRevertTime(x.getRevertTime());
            goodsPriceList.add(goodsPriceDetailEntity);
        });
        ownerGoodsPriceDetailMapper.insertList(goodsPriceList);
    }

    /**
     *
     * @param ownerOrderNo 车主订单号
     * @param isNeedPrice 是否需要价格信息 true-需要  false-不需要
     * @return
     */
    public OwnerGoodsDetailDto getOwnerGoodsDetail(String ownerOrderNo,boolean isNeedPrice){
        OwnerGoodsEntity ownerGoodsEntity = ownerGoodsMapper.selectByOwnerOrderNo(ownerOrderNo);

        OwnerGoodsDetailDto ownerGoodsDetailDto  = new OwnerGoodsDetailDto();
        BeanUtils.copyProperties(ownerGoodsDetailDto,ownerGoodsEntity);
        if(!isNeedPrice){
            return ownerGoodsDetailDto;
        }
        List<OwnerGoodsPriceDetailEntity> ownerGoodsPriceDetailEntities = ownerGoodsPriceDetailMapper.selectByOwnerOrderNo(ownerOrderNo);
        List<OwnerGoodsPriceDetailDto> ownerGoodsPriceDetailDtoList = new ArrayList<>();
        ownerGoodsPriceDetailEntities.forEach(x->{
            OwnerGoodsPriceDetailDto ownerGoodsPriceDetailDto = new OwnerGoodsPriceDetailDto();
            BeanUtils.copyProperties(ownerGoodsPriceDetailDto,x);
            ownerGoodsPriceDetailDtoList.add(ownerGoodsPriceDetailDto);
        });
        ownerGoodsDetailDto.setOwnerGoodsPriceDetailDtoList(ownerGoodsPriceDetailDtoList);
        return ownerGoodsDetailDto;
    }
}
