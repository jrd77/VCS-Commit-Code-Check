package com.atzuche.order.coreapi.service;

import com.atzuche.order.coreapi.entity.dto.ModifyOrderDTO;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单支付成功 回调子订单
 */
@Service
public class PayCallbackService {

    @Autowired ModifyOrderForRenterService modifyOrderForRenterService;
    /**
     * ModifyOrderForRenterService.supplementPayPostProcess（修改订单补付回掉）
     */
    public void callBack(String orderNo, String renterOrderNo, ModifyOrderDTO modifyOrderDTO, List<RenterOrderSubsidyDetailDTO> renterOrderSubsidyDetailDTOList){
        modifyOrderForRenterService.supplementPayPostProcess(orderNo,renterOrderNo,modifyOrderDTO,renterOrderSubsidyDetailDTOList);
    }
}
