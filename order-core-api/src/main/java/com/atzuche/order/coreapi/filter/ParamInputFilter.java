package com.atzuche.order.coreapi.filter;

import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.exceptions.InputErrorException;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.autoyol.car.api.exception.ParameterException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 该Filter用于检测复杂参数的满足
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/3 10:15 上午
 **/
@Service("paramInputFilter")
public class ParamInputFilter implements OrderFilter {
    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
        OrderReqVO orderReqVo =  context.getOrderReqVO();
        if(orderReqVo==null){
            throw new IllegalArgumentException("orderReqVO cannot be null");
        }

        if(null != orderReqVo.getSrvReturnFlag() && OrderConstant.YES == orderReqVo.getSrvReturnFlag()){
            if(orderReqVo.getSrvReturnLon() == null || orderReqVo.getSrvReturnLon().trim().length()<=0
                    || orderReqVo.getSrvReturnLat() == null || orderReqVo.getSrvReturnLat().trim().length()<=0
                    || orderReqVo.getSrvReturnAddr() == null || orderReqVo.getSrvReturnAddr().trim().length()<=0){
                throw new InputErrorException("还车经纬度或还车地址不能为空");
            }
        }

        if(null != orderReqVo.getSrvGetFlag() && OrderConstant.YES == orderReqVo.getSrvGetFlag()){
            if(orderReqVo.getSrvGetLon() == null || orderReqVo.getSrvGetLon().trim().length()<=0
                    || orderReqVo.getSrvGetLat() == null || orderReqVo.getSrvGetLat().trim().length()<=0
                    || orderReqVo.getSrvGetAddr() == null || orderReqVo.getSrvGetAddr().trim().length()<=0){
                throw new InputErrorException("取车经纬度或取车地址不能为空");
            }
        }
    }
}
