package com.atzuche.order.coreapi.filter;

import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.coreapi.service.StockService;
import com.autoyol.car.api.model.dto.LocationDTO;
import com.autoyol.car.api.model.dto.OrderInfoDTO;
import com.autoyol.car.api.model.enums.OrderOperationTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 检查商品库存是否够
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/3 2:07 下午
 **/
@Service("stockFilter")
public class StockFilter implements OrderFilter {
    @Autowired
    private StockService stockService;
    private final static Logger logger = LoggerFactory.getLogger(StockFilter.class);
    

    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
        logger.info("对订单信息库存检查过滤");
        OrderReqVO orderReqVO = context.getOrderReqVO();
        OrderInfoDTO orderInfoDTO = initOrderInfoDTO(orderReqVO);
        stockService.checkCarStock(orderInfoDTO);
    }

    private OrderInfoDTO initOrderInfoDTO(OrderReqVO orderReqVO) {
        OrderInfoDTO orderInfoDTO = new OrderInfoDTO();
        orderInfoDTO.setOrderNo(null);
        orderInfoDTO.setCityCode(Integer.valueOf(orderReqVO.getCityCode()));
        orderInfoDTO.setCarNo(Integer.valueOf(orderReqVO.getCarNo()));
        orderInfoDTO.setOldCarNo(null);
        orderInfoDTO.setStartDate(LocalDateTimeUtils.localDateTimeToDate(orderReqVO.getRentTime()));
        orderInfoDTO.setEndDate(LocalDateTimeUtils.localDateTimeToDate(orderReqVO.getRevertTime()));
        orderInfoDTO.setOperationType(OrderOperationTypeEnum.ZCXD.getType());

        LocationDTO getCarAddress = new LocationDTO();
        getCarAddress.setFlag(0);
        if(orderReqVO.getSrvGetFlag() == 1){
            getCarAddress.setFlag(1);
            getCarAddress.setLat(orderReqVO.getSrvGetLat()==null?0.0:Double.valueOf(orderReqVO.getSrvGetLat()));
            getCarAddress.setLon(orderReqVO.getSrvGetLon()==null?0.0:Double.valueOf(orderReqVO.getSrvGetLon()));
            getCarAddress.setCarAddress(orderReqVO.getSrvGetAddr());
        }
        LocationDTO returnCarAddress = new LocationDTO();
        returnCarAddress.setFlag(0);
        if(orderReqVO.getSrvReturnFlag() == 1){
            returnCarAddress.setFlag(1);
            returnCarAddress.setLat(orderReqVO.getSrvReturnLat()==null?0.0:Double.valueOf(orderReqVO.getSrvReturnLat()));
            returnCarAddress.setLon(orderReqVO.getSrvReturnLon()==null?0.0:Double.valueOf(orderReqVO.getSrvReturnLon()));
            returnCarAddress.setCarAddress(orderReqVO.getSrvReturnAddr());
        }
        orderInfoDTO.setGetCarAddress(getCarAddress);
        orderInfoDTO.setReturnCarAddress(returnCarAddress);
        return orderInfoDTO;
    }
}
