package com.atzuche.order.open.service;

import com.atzuche.order.commons.vo.req.AdminOrderReqVO;
import com.atzuche.order.commons.vo.res.OrderResVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//@FeignClient(name = "order-center-api")
@FeignClient(url = "http://10.0.3.235:1412" ,name="order-center-api")
public interface FeignOrderAdminSubmitService {
    /*
     * @Author ZhangBin
     * @Date 2020/1/8 21:06
     * @Description: 后台管理系统下单
     *
     **/
    @RequestMapping(method = RequestMethod.POST, value = "/order/admin/req")
    ResponseData<OrderResVO> getOrderDetail(@RequestBody AdminOrderReqVO adminOrderReqVO);
}
