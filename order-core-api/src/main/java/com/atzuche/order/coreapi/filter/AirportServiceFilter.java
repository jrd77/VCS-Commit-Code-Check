package com.atzuche.order.coreapi.filter;

import com.atzuche.config.client.api.DefaultConfigContext;
import com.atzuche.config.client.api.ServicePointConfigSDK;
import com.atzuche.config.common.entity.ServicePointEntity;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.exceptions.AirportServiceException;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("airportServiceFilter")
public class AirportServiceFilter implements OrderFilter {
    @Autowired
    private ServicePointConfigSDK servicePointConfigSDK;
    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
        OrderReqVO orderReqVo = context.getOrderReqVO();
        if(orderReqVo.getUseAirportService() != null && 1 == orderReqVo.getUseAirportService()){
            ServicePointEntity config = servicePointConfigSDK.getConfig(new DefaultConfigContext(), orderReqVo.getSrvGetAddr(), orderReqVo.getSrvGetLat(), orderReqVo.getSrvGetLon());
            // 使用机场服务判断
            if (config == null) {
                throw new AirportServiceException();
            }
        }

    }
}
