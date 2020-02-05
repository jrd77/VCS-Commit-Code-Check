package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.open.vo.RenterGoodWithoutPriceVO;
import com.atzuche.order.rentercommodity.entity.RenterGoodsEntity;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.autoyol.commons.web.ResponseData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供租客的商品情况
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/5 10:44 上午
 **/
@RestController
public class RenterGoodsController {

    @Autowired
    private RenterGoodsService renterGoodsService;

    @GetMapping("/renter/goods")
    public ResponseData<RenterGoodWithoutPriceVO> getRenterGoodsDetailWithoutPrice(@RequestParam("orderNo") String orderNo, @RequestParam("carNo") String carNo){
        RenterGoodsEntity renterGoodsEntity =  renterGoodsService.queryCarInfoByOrderNoAndCarNo(orderNo,carNo);
        if(renterGoodsEntity==null){
            throw new OrderNotFoundException(orderNo+":"+carNo);
        }
        RenterGoodWithoutPriceVO withoutPriceDTO = new RenterGoodWithoutPriceVO();
        BeanUtils.copyProperties(renterGoodsEntity,withoutPriceDTO);
        return ResponseData.success(withoutPriceDTO);
    }

}
