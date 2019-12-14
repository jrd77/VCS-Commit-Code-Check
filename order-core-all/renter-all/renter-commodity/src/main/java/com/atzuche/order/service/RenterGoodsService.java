package com.atzuche.order.service;

import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.coreapi.entity.dto.OrderContextDto;
import com.atzuche.order.coreapi.entity.request.SubmitOrderReq;
import com.atzuche.order.entity.RenterGoodsEntity;
import com.atzuche.order.entity.RenterGoodsPriiceDetailEntity;
import com.atzuche.order.mapper.RenterGoodsMapper;
import com.autoyol.car.api.model.vo.*;
import com.autoyol.commons.web.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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

    public void insert(OrderContextDto orderContextDto){
        CarBaseVO carBaseVO = orderContextDto.getCarDetailVO().getCarBaseVO();
        CarStewardVO carSteward = orderContextDto.getCarDetailVO().getCarSteward();
        CarDetailImageVO detailImageVO = orderContextDto.getCarDetailVO().getDetailImageVO();
        CarAddressOfTransVO carAddressOfTransVO = orderContextDto.getCarDetailVO().getCarAddressOfTransVO();
        SubmitOrderReq submitOrderReq = orderContextDto.getSubmitOrderReq();
        RenterGoodsEntity renterGoodsEntity = new RenterGoodsEntity();
        renterGoodsEntity.setCarAddrIndex(Integer.valueOf(submitOrderReq.getCarAddrIndex()));
        renterGoodsEntity.setOrderNo(orderContextDto.getOrderNo());
        renterGoodsEntity.setRenterOrderNo("");
        renterGoodsEntity.setCarNo(carBaseVO.getCarNo());
        renterGoodsEntity.setCarPlateNum(carBaseVO.getPlateNum());
        renterGoodsEntity.setCarBrand(carBaseVO.getBrand());
        renterGoodsEntity.setCarBrandTxt(carBaseVO.getBrandTxt());
        renterGoodsEntity.setCarRating(carBaseVO.getRating());
        renterGoodsEntity.setCarType(Integer.valueOf(carBaseVO.getType()));
        renterGoodsEntity.setCarTypeTxt(carBaseVO.getTypeTxt());
        renterGoodsEntity.setCarDisplacement(carBaseVO.getCc());
        renterGoodsEntity.setCarGearboxType(carBaseVO.getGbType());
        renterGoodsEntity.setCarDayMileage(carBaseVO.getDayMileage());
        renterGoodsEntity.setCarIntrod(carBaseVO.getCarDesc());
        renterGoodsEntity.setCarSurplusPrice(carBaseVO.getSurplusPrice());
        renterGoodsEntity.setCarUseSpecialPrice(Integer.valueOf(submitOrderReq.getUseSpecialPrice()));
        renterGoodsEntity.setCarGuidePrice(carBaseVO.getGuidePrice());
        renterGoodsEntity.setCarStatus(carBaseVO.getStatus());
        renterGoodsEntity.setCarImageUrl(getCoverPic(detailImageVO));
        renterGoodsEntity.setCarOwnerType(carBaseVO.getMajorType());
        renterGoodsEntity.setCarUseType(carBaseVO.getUseType());
        renterGoodsEntity.setCarOilVolume(carBaseVO.getOilVolume());
        renterGoodsEntity.setCarEngineType(carBaseVO.getEngineType());
        renterGoodsEntity.setCarDesc(carBaseVO.getCarDesc());
        renterGoodsEntity.setCarStewardPhone(carSteward.getStewardPhone()==null?"":String.valueOf(carSteward.getStewardPhone()));
        //renterGoodsEntity.setCarCheckStatus();
        renterGoodsEntity.setCarShowAddr(carAddressOfTransVO.getCarVirtualAddress());
        renterGoodsEntity.setCarShowLon(carAddressOfTransVO.getVirtualAddressLon()==null?"":String.valueOf(carAddressOfTransVO.getVirtualAddressLon()));
        renterGoodsEntity.setCarShowLat(carAddressOfTransVO.getVirtualAddressLat()==null?"":String.valueOf(carAddressOfTransVO.getVirtualAddressLat()));
        renterGoodsEntity.setCarRealAddr(carAddressOfTransVO.getCarRealAddress());
        renterGoodsEntity.setCarRealLon(carAddressOfTransVO.getRealAddressLon()==null?"":String.valueOf(carAddressOfTransVO.getRealAddressLon()));
        renterGoodsEntity.setCarRealLat(carAddressOfTransVO.getRealAddressLat()==null?"":String.valueOf(carAddressOfTransVO.getRealAddressLat()));
        renterGoodsMapper.insert(renterGoodsEntity);

    }

    //获取车辆封面图片路径
    public String getCoverPic(CarDetailImageVO detailImageVO){
        String coverPic = "";
        if(detailImageVO == null || detailImageVO.getCarImages() == null || detailImageVO.getCarImages().size()<=0){
            return coverPic;
        }
        List<ImageVO> collect = detailImageVO.getCarImages()
                .stream()
                .filter(x -> "0".equals(x.getCover()))
                .limit(1)
                .collect(Collectors.toList());
        coverPic = collect.size()<=0 ?  "" : collect.get(0).getPicPath();
        return coverPic;
    }
}
