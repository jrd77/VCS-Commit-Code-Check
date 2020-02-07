package com.atzuche.order.rentercommodity.service;

import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterGoodsDTO;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.rentercommodity.entity.RenterGoodsEntity;
import com.atzuche.order.rentercommodity.entity.RenterGoodsPriceDetailEntity;
import com.atzuche.order.rentercommodity.mapper.RenterGoodsMapper;
import com.atzuche.order.rentercommodity.mapper.RenterGoodsPriceDetailMapper;
import lombok.Data;
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

    public void save(RenterGoodsDetailDTO renterGoodsDetailDto){
        RenterGoodsEntity goodsEntity = new RenterGoodsEntity();
        BeanUtils.copyProperties(renterGoodsDetailDto,goodsEntity);
        renterGoodsMapper.insert(goodsEntity);

        List<RenterGoodsPriceDetailDTO> goodsPriceDetailDtoList = renterGoodsDetailDto.getRenterGoodsPriceDetailDTOList();
        List<RenterGoodsPriceDetailEntity> goodsPriceList = new ArrayList<>();
        goodsPriceDetailDtoList.forEach(x->{
            RenterGoodsPriceDetailEntity goodsPriceDetailEntity = new RenterGoodsPriceDetailEntity();
            goodsPriceDetailEntity.setOrderNo(renterGoodsDetailDto.getOrderNo());
            goodsPriceDetailEntity.setRenterOrderNo(renterGoodsDetailDto.getRenterOrderNo());
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
    public RenterGoodsDetailDTO getRenterGoodsDetail(String renterOrderNo, boolean isNeedPrice){
        RenterGoodsEntity renterGoodsEntity = renterGoodsMapper.selectByRenterOrderNo(renterOrderNo);
        RenterGoodsDetailDTO renterGoodsDetailDto  = new RenterGoodsDetailDTO();
        if(renterGoodsEntity != null){
            BeanUtils.copyProperties(renterGoodsEntity,renterGoodsDetailDto);
        }

        if(!isNeedPrice){
            return renterGoodsDetailDto;
        }
        List<RenterGoodsPriceDetailEntity> renterGoodsPriceDetailEntities = renterGoodsPriceDetailMapper.selectByRenterOrderNo(renterOrderNo);
        List<RenterGoodsPriceDetailDTO> renterGoodsPriceDetailDTOList = new ArrayList<>();
        renterGoodsPriceDetailEntities.forEach(x->{
            RenterGoodsPriceDetailDTO renterGoodsPriceDetailDto = new RenterGoodsPriceDetailDTO();
            BeanUtils.copyProperties(x,renterGoodsPriceDetailDto);
            renterGoodsPriceDetailDTOList.add(renterGoodsPriceDetailDto);
        });
        renterGoodsDetailDto.setRenterGoodsPriceDetailDTOList(renterGoodsPriceDetailDTOList);
        return renterGoodsDetailDto;
    }

    public List<String> queryOrderNosByOrderNosAndCarNo(String carNo, List<String> orderNos) {
        return renterGoodsMapper.queryOrderNosByOrderNosAndCarNo(carNo,orderNos);
    }

    public String queryCarNumByOrderNo(String orderNo) {
        return renterGoodsMapper.queryCarNumByOrderNo(orderNo);
    }

    public RenterGoodsEntity queryCarInfoByOrderNoAndCarNo(String orderNo, String carNo) {
        return renterGoodsMapper.queryCarInfoByOrderNoAndCarNo(orderNo,carNo);
    }


    @Data
    public static class JSONStr{
        private Integer id;
        private String username;
        private Integer age;
        private String desc;
    }
}
