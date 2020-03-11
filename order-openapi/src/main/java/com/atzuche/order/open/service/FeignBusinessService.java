package com.atzuche.order.open.service;

import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.vo.req.OwnerUpdateSeeVO;
import com.atzuche.order.commons.vo.req.RenterAndOwnerSeeOrderVO;
import com.autoyol.commons.web.ResponseData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="order-center-api")
public interface FeignBusinessService {
    /**
     * 更新未读订单
     * @param renterAndOwnerSeeOrderVO
     * @return
     */
    @PostMapping("/orderBusiness/renterAndOwnerSeeOrder")
    public ResponseData<?> renterAndOwnerSeeOrder(@RequestBody RenterAndOwnerSeeOrderVO renterAndOwnerSeeOrderVO);


    /**
     * 会员号更新未读订单
     * @param renterAndOwnerSeeOrderVO
     * @return
     */
    @PostMapping("/orderBusiness/ownerUpdateSee")
    public ResponseData<?> ownerUpdateSee(@RequestBody OwnerUpdateSeeVO ownerUpdateSeeVO);


    @GetMapping("/orderBusiness/queryOwnerMemDetail")
    public ResponseData<OwnerMemberDTO> queryOwnerMemDetail(String orderNo);

    @GetMapping("/orderBusiness/queryRenterMemDetail")
    public ResponseData<RenterMemberDTO> queryRenterMemDetail(String orderNo);
}
