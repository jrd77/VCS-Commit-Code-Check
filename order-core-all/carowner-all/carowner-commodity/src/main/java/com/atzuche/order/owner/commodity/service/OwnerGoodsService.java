package com.atzuche.order.owner.commodity.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsPriceDetailDTO;
import com.atzuche.order.owner.commodity.entity.OwnerGoodsEntity;
import com.atzuche.order.owner.commodity.entity.OwnerGoodsPriceDetailEntity;
import com.atzuche.order.owner.commodity.mapper.OwnerGoodsMapper;
import com.atzuche.order.owner.commodity.mapper.OwnerGoodsPriceDetailMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static Logger logger = LoggerFactory.getLogger(OwnerGoodsService.class);

    @Autowired
    private OwnerGoodsMapper ownerGoodsMapper;
    @Autowired
    private OwnerGoodsPriceDetailMapper ownerGoodsPriceDetailMapper;

    /**
     * 保存车主端的商品信息
     * @param ownerGoodsDetailDTO
     */
    public void save(OwnerGoodsDetailDTO ownerGoodsDetailDTO){
        logger.info("Save owner goods detail.param is,ownerGoodsDetailDTO:[{}]", JSON.toJSONString(ownerGoodsDetailDTO));

        OwnerGoodsEntity goodsEntity = new OwnerGoodsEntity();
        BeanUtils.copyProperties(ownerGoodsDetailDTO,goodsEntity);
        goodsEntity.setChoiceCar(ownerGoodsDetailDTO.isChoiceCar() ? 1 : 0);
        ownerGoodsMapper.insert(goodsEntity);

        List<OwnerGoodsPriceDetailDTO> goodsPriceDetailDtoList = ownerGoodsDetailDTO.getOwnerGoodsPriceDetailDTOList();
        List<OwnerGoodsPriceDetailEntity> goodsPriceList = new ArrayList<>();
        goodsPriceDetailDtoList.forEach(x->{
            OwnerGoodsPriceDetailEntity goodsPriceDetailEntity = new OwnerGoodsPriceDetailEntity();
            goodsPriceDetailEntity.setOrderNo(ownerGoodsDetailDTO.getOrderNo());
            goodsPriceDetailEntity.setOwnerOrderNo(ownerGoodsDetailDTO.getOwnerOrderNo());
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
    public OwnerGoodsDetailDTO getOwnerGoodsDetail(String ownerOrderNo, boolean isNeedPrice){
        OwnerGoodsEntity ownerGoodsEntity = ownerGoodsMapper.selectByOwnerOrderNo(ownerOrderNo);
        OwnerGoodsDetailDTO   ownerGoodsDetailDto  = new OwnerGoodsDetailDTO();
        if(ownerGoodsEntity != null){
            BeanUtils.copyProperties(ownerGoodsEntity,ownerGoodsDetailDto);
        }
        if(!isNeedPrice){
            return ownerGoodsDetailDto;
        }
        List<OwnerGoodsPriceDetailEntity> ownerGoodsPriceDetailEntities = ownerGoodsPriceDetailMapper.selectByOwnerOrderNo(ownerOrderNo);
        List<OwnerGoodsPriceDetailDTO> ownerGoodsPriceDetailDTOList = new ArrayList<>();
        ownerGoodsPriceDetailEntities.forEach(x->{
            OwnerGoodsPriceDetailDTO ownerGoodsPriceDetailDto = new OwnerGoodsPriceDetailDTO();
            BeanUtils.copyProperties(x,ownerGoodsPriceDetailDto);
            ownerGoodsPriceDetailDTOList.add(ownerGoodsPriceDetailDto);
        });
        ownerGoodsDetailDto.setOwnerGoodsPriceDetailDTOList(ownerGoodsPriceDetailDTOList);
        return ownerGoodsDetailDto;
    }
    
    /**
     * 获取最新的车主商品信息
     * @param orderNo
     * @return OwnerGoodsEntity
     */
    public OwnerGoodsEntity getLastOwnerGoodsByOrderNo(String orderNo) {
    	return ownerGoodsMapper.getLastOwnerGoodsByOrderNo(orderNo);
    }
    /*
     * @Author ZhangBin
     * @Date 2020/1/17 10:05
     * @Description: 
     * 
     **/
    public OwnerGoodsEntity getOwnerGoodsByCarNo(Integer carNo){
        return ownerGoodsMapper.getOwnerGoodsByCarNo(carNo);
    }
}
