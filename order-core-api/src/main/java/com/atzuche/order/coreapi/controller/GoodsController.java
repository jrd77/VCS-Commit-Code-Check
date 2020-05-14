package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.owner.commodity.entity.OwnerGoodsEntity;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.autoyol.commons.web.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * @Author ZhangBin
 * @Date 2020/4/28 15:29
 * @Description: 商品相关
 *
 **/
@RestController
@Slf4j
public class GoodsController {

    @Autowired
    private RenterGoodsService renterGoodsService;
    @Autowired
    private OwnerGoodsService ownerGoodsService;
    /*
     * @Author ZhangBin
     * @Date 2020/4/28 15:36
     * @Description:通过订单号查询车辆号
     *
     **/
    @RequestMapping("/goods/renter/queryCarNumByOrderNo")
    public ResponseData<String> queryCarNumByOrderNo(@RequestParam("orderNo") String orderNo){
        String carNum = renterGoodsService.queryCarNumByOrderNo(orderNo);
        return ResponseData.success(carNum);
    }
    /*
     * @Author ZhangBin
     * @Date 2020/4/28 15:53
     * @Description: 通过租客子订单号获取租客商品信息
     *
     **/
    @RequestMapping("/goods/renter/queryRenterGoodsDetail")
    public ResponseData<RenterGoodsDetailDTO> queryRenterGoodsDetail(@RequestParam("renterOrderNo")String renterOrderNo,@RequestParam("isNeedPrice") boolean isNeedPrice){
        RenterGoodsDetailDTO renterGoodsDetail = renterGoodsService.getRenterGoodsDetail(renterOrderNo, isNeedPrice);
        return ResponseData.success(renterGoodsDetail);
    }

    /*
     * @Author ZhangBin
     * @Date 2020/4/28 16:14
     * @Description: 根据订单号查询车主商品信息
     *
     **/
    @RequestMapping("/goods/owner/queryRenterGoodsDetail")
    public ResponseData<OwnerGoodsDetailDTO> queryOwnerGoodsByOrderNo(@RequestParam("orderNo")String orderNo){
        OwnerGoodsEntity ownerGoodsEntity = ownerGoodsService.getLastOwnerGoodsByOrderNo(orderNo);
        OwnerGoodsDetailDTO ownerGoodsDetailDTO = null;
        if(ownerGoodsEntity != null){
            ownerGoodsDetailDTO = new OwnerGoodsDetailDTO();
            BeanUtils.copyProperties(ownerGoodsEntity,ownerGoodsDetailDTO);
        }
        return ResponseData.success(ownerGoodsDetailDTO);
    }

    /*
     * @Author ZhangBin
     * @Date 2020/4/28 16:26
     * @Description: 使用订单号和车辆号查询商品信息
     *
     **/
    @RequestMapping("/goods/owner/queryOwnerGoodsByCarNoAndOrderNo")
    public ResponseData<OwnerGoodsDetailDTO> queryOwnerGoodsByCarNoAndOrderNo(@RequestParam("carNo") Integer carNo,@RequestParam("orderNo") String orderNo){
        OwnerGoodsEntity ownerGoodsEntity = ownerGoodsService.getOwnerGoodsByCarNoAndOrderNo(carNo, orderNo);
        OwnerGoodsDetailDTO ownerGoodsDetailDTO = null;
        if(ownerGoodsEntity != null){
            ownerGoodsDetailDTO = new OwnerGoodsDetailDTO();
            BeanUtils.copyProperties(ownerGoodsEntity,ownerGoodsDetailDTO);
        }
        return ResponseData.success(ownerGoodsDetailDTO);
    }

}
