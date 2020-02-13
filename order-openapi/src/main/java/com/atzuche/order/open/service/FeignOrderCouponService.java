package com.atzuche.order.open.service;

import com.atzuche.order.commons.vo.req.AdminGetDisCouponListReqVO;
import com.atzuche.order.commons.vo.req.ModifyOrderQueryReqVO;
import com.atzuche.order.commons.vo.res.AdminGetDisCouponListResVO;
import com.atzuche.order.commons.vo.res.ModifyOrderResVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//@FeignClient(url = "http://localhost:1412" ,name="order-center-api")  //本地测试
//@FeignClient(url = "http://10.0.3.235:1412" ,name="order-center-api")
@FeignClient(name="order-center-api")
public interface FeignOrderCouponService {

    /**
     * 获取订单内租客优惠抵扣信息
     * @param req
     * @return
     */
    @PostMapping("/order/admin/queryDisCouponByOrderNo")
    public ResponseData<AdminGetDisCouponListResVO> getDisCouponListByOrderNo(@RequestBody AdminGetDisCouponListReqVO req);


}
