package com.atzuche.order.rentercommodity.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDTO;
import com.atzuche.order.commons.entity.orderDetailDto.RenterGoodsDTO;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.rentercommodity.entity.RenterGoodsEntity;
import com.atzuche.order.rentercommodity.entity.RenterGoodsPriceDetailEntity;
import com.atzuche.order.rentercommodity.mapper.RenterGoodsMapper;
import com.atzuche.order.rentercommodity.mapper.RenterGoodsPriceDetailMapper;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 租客商品概览表
 *
 * @author ZhangBin
 * @date 2019-12-11 18:06:32
 */
@Service
public class RenterGoodsService{

    private static Logger logger = LoggerFactory.getLogger(RenterGoodsService.class);

    @Autowired
    private RenterGoodsMapper renterGoodsMapper;
    @Autowired
    private RenterGoodsPriceDetailMapper renterGoodsPriceDetailMapper;

    /**
     * 租客商品信息(不包括订单号)保存
     *
     * @param orderNo 订单号
     * @param renterOrderNo 租客订单号
     * @param renterGoodsDetailDto 租客商品信息
     */
    public void save(String orderNo, String renterOrderNo, RenterGoodsDetailDTO renterGoodsDetailDto) {
        logger.info("Save renter goods detail.param is,orderNo:[{}],renterOrderNo:[{}],renterGoodsDetailDto:[{}]",
                orderNo, renterOrderNo, JSON.toJSONString(renterGoodsDetailDto));

        if(Objects.isNull(renterGoodsDetailDto)) {
            return ;
        }
        renterGoodsDetailDto.setOrderNo(orderNo);
        renterGoodsDetailDto.setRenterOrderNo(renterOrderNo);
        save(renterGoodsDetailDto);
    }

    /**
     *  租客商品信息(包括订单号)保存
     *
     * @param renterGoodsDetailDto 租客商品信息
     */
    public void save(RenterGoodsDetailDTO renterGoodsDetailDto) {
        logger.info("Save renter goods detail.param is,renterGoodsDetailDto:[{}]", JSON.toJSONString(renterGoodsDetailDto));

        RenterGoodsEntity goodsEntity = new RenterGoodsEntity();
        BeanUtils.copyProperties(renterGoodsDetailDto, goodsEntity);
        goodsEntity.setChoiceCar(renterGoodsDetailDto.isChoiceCar() ? OrderConstant.YES : OrderConstant.NO);
        renterGoodsMapper.insert(goodsEntity);

        List<RenterGoodsPriceDetailDTO> goodsPriceDetailDtoList = renterGoodsDetailDto.getRenterGoodsPriceDetailDTOList();
        List<RenterGoodsPriceDetailEntity> goodsPriceList = new ArrayList<>();
        goodsPriceDetailDtoList.forEach(x -> {
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

    public RenterGoodsEntity queryInfoByOrderNo(String orderNo) {
        return renterGoodsMapper.queryInfoByOrderNo(orderNo);
    }


    @Data
    public static class JSONStr{
        private Integer id;
        private String username;
        private Integer age;
        private String desc;
    }
}
