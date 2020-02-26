package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.dto.OrderSupplementDetailDTO;
import com.atzuche.order.commons.vo.res.rentcosts.OrderSupplementDetailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.SupplementVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/20 11:43 上午
 **/
@FeignClient(name="order-center-api")
public interface FeignSupplementService {
    /**
     * 添加补付
     * @param orderSupplementDetailDTO
     * @return
     */
    @PostMapping("/order/supplement/add")
    public ResponseData<?> addSupplement(@Valid @RequestBody OrderSupplementDetailDTO orderSupplementDetailDTO);


    @GetMapping("/order/supplement/list")
    public ResponseData<SupplementVO> listSupplement(@RequestParam(value="orderNo",required = true) String orderNo);

    @PostMapping("/order/supplement/del")
    public ResponseData<?> delSupplement(@RequestParam(value="id",required = true) Integer id);
}
