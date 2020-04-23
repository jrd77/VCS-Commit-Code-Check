package com.atzuche.order.coreapi.filter;

import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.ErrorCode;
import com.atzuche.order.commons.exceptions.CarSettingCheckException;
import com.atzuche.order.commons.exceptions.LongRentTagCheckException;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/*
 * @Author ZhangBin
 * @Date 2020/4/23 15:52
 * @Description: 长租标签校验
 *
 **/
@Slf4j
@Service("tagCheckFilter")
public class TagCheckFilter implements OrderFilter {
    public static final String LONG_TAG = "";

    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
        RenterGoodsDetailDTO renterGoodsDetailDto = context.getRenterGoodsDetailDto();
        List<String> labelIds = renterGoodsDetailDto.getLabelIds();
        boolean result = Optional.ofNullable(labelIds).orElseGet(ArrayList::new).stream().anyMatch(x -> LONG_TAG.equals(x));
        if(!result){
            log.error("长租标签校验不通过");
            throw new LongRentTagCheckException();
        }
    }
}
