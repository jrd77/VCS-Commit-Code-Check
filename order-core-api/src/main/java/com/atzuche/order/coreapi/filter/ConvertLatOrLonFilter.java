package com.atzuche.order.coreapi.filter;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/*
 * @Author ZhangBin
 * @Date 2020/2/21 14:48
 * @Description: 经纬度转换校验
 *
 **/
@Slf4j
@Service("convertLatOrLonFilter")
public class ConvertLatOrLonFilter implements OrderFilter {
    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {
        OrderReqVO orderReqVo = context.getOrderReqVO();
        String srvGetLat=orderReqVo.getSrvGetLat()!=null?orderReqVo.getSrvGetLat():null;
        String srvGetLon=orderReqVo.getSrvGetLon()!=null?orderReqVo.getSrvGetLon():null;
        String srvReturnLat=orderReqVo.getSrvReturnLat()!=null?orderReqVo.getSrvReturnLat():null;
        String srvReturnLon=orderReqVo.getSrvReturnLon()!=null?orderReqVo.getSrvReturnLon():null;

        if(org.apache.commons.lang.StringUtils.isNotBlank(srvGetLat)){
            orderReqVo.setSrvGetLat(convertLatOrLon(srvGetLat));
        }
        if(org.apache.commons.lang.StringUtils.isNotBlank(srvGetLon)){
            orderReqVo.setSrvGetLon(convertLatOrLon(srvGetLon));
        }
        if(org.apache.commons.lang.StringUtils.isNotBlank(srvReturnLat)){
            orderReqVo.setSrvReturnLat(convertLatOrLon(srvReturnLat));
        }
        if(org.apache.commons.lang.StringUtils.isNotBlank(srvReturnLon)){
            orderReqVo.setSrvReturnLon(convertLatOrLon(srvReturnLon));
        }
    }
    /**
     * 转换经纬度
     * 小数点最后最多为6位
     * @param latOrLon
     * @return
     */
    private String convertLatOrLon(String latOrLon){
        if(StringUtils.isNotBlank(latOrLon)) {
            String start=latOrLon.substring(0,latOrLon.indexOf(".")+1);
            String end=latOrLon.substring(latOrLon.indexOf(".")+1);
            if(end.length()>6){
                end=end.substring(0,6);
            }
            return start+end;
        }
        return latOrLon;

    }
}
