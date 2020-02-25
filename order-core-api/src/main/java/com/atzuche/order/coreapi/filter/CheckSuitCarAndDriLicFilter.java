package com.atzuche.order.coreapi.filter;

import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import org.springframework.stereotype.Service;

/*
 * @Author ZhangBin
 * @Date 2020/2/25 10:58
 * @Description: 准驾车型校验
 *
 **/
@Service("checkSuitCarAndDriLicFilter")
public class CheckSuitCarAndDriLicFilter implements OrderFilter {

    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
        RenterGoodsDetailDTO renterGoodsDetailDto = context.getRenterGoodsDetailDto();
        RenterMemberDTO renterMemberDto = context.getRenterMemberDto();
        Integer carGearboxType = renterGoodsDetailDto.getCarGearboxType();
        String driLicAllowCar = renterMemberDto.getDriLicAllowCar();
    }
}
