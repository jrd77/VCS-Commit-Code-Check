package com.atzuche.order.open.service;

import com.atzuche.order.commons.vo.LianHeMaiTongOrderVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="order-center-api")
public interface FeignLianHeMaiTongService {

    @RequestMapping("/lianHeMaiTong/getOrderListByMemberNo")
    public ResponseData<List<LianHeMaiTongOrderVO>> getOrderListByMemberNo(@RequestParam("memNo") String memNo);
}
