package com.atzuche.order.rentercommodity.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDTO;
import com.atzuche.order.rentercommodity.entity.RenterGoodsEntity;
import com.atzuche.order.rentercommodity.entity.RenterGoodsPriceDetailEntity;
import com.atzuche.order.rentercommodity.mapper.RenterGoodsMapper;
import com.atzuche.order.rentercommodity.mapper.RenterGoodsPriceDetailMapper;


/**
 * 商品概览价格明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 18:06:32
 */
@Service
public class RenterGoodsPriceDetailService {

    @Autowired
    private RenterGoodsPriceDetailMapper renterGoodsPriceDetailMapper;
    @Autowired
    private RenterGoodsMapper renterGoodsMapper;

    public List<RenterGoodsPriceDetailEntity> listRenterGoodsPriceByOrderNo(String orderNo) {
        return null;
    }
    
    
    /**
    *
    * @param renterOrderNo 租客订单号
    * @param isNeedPrice 是否需要价格信息 true-需要  false-不需要
    * @return
    */
   public RenterGoodsDetailDTO getRenterGoodsDetail(String renterOrderNo, boolean isNeedPrice){
	   RenterGoodsEntity renterGoodsEntity = renterGoodsMapper.selectByRenterOrderNo(renterOrderNo);
	   RenterGoodsDetailDTO   renterGoodsDetailDto  = new RenterGoodsDetailDTO();
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
   
}
