package com.atzuche.order.transport.common;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.vo.res.delivery.RenterOrderDeliveryRepVO;
import com.atzuche.order.rentercommodity.service.RenterCommodityService;
import com.atzuche.order.transport.vo.delivery.DeliveryCarFeeBaseParamsVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author 胡春林
 * 基本参数
 */
@Service
@Slf4j
public class DeliveryCarFeeBaseService {

    @Autowired
    RenterCommodityService renterCommodityService;

    /**
     * 获取需要的参数数据
     * @return
     */
    public DeliveryCarFeeBaseParamsVO initNeedParams(String orderNo,List<RenterOrderDeliveryRepVO> renterOrderDeliveryRepVOList){
        log.info("入参orderNo：[{}]", orderNo);
        if(StringUtils.isBlank(orderNo)){
            log.info("订单号不能为空,orderNo:[{}]",orderNo);
            return null;
        }
        if(CollectionUtils.isEmpty(renterOrderDeliveryRepVOList))
        {
            log.info("没有找到对应的配送信息，无法计算配送相关的费用,orderNo:[{}]",orderNo);
            return null;
        }
        RenterGoodsDetailDTO renterGoodsDetailDTO = renterCommodityService.getRenterGoodsDetail(renterOrderDeliveryRepVOList.get(0).getRenterOrderNo(), false);
        CostBaseDTO costBaseDTO = new CostBaseDTO(renterGoodsDetailDTO.getOrderNo(),renterOrderDeliveryRepVOList.get(0).getRenterOrderNo(),renterGoodsDetailDTO.getOwnerMemNo(),renterOrderDeliveryRepVOList.get(0).getRentTime(),renterOrderDeliveryRepVOList.get(0).getRevertTime());
        DeliveryCarFeeBaseParamsVO deliveryCarFeeBaseParamsVO = DeliveryCarFeeBaseParamsVO.builder().costBaseDTO(costBaseDTO).renterOrderDeliveryRepVOList(renterOrderDeliveryRepVOList).renterGoodsDetailDTO(renterGoodsDetailDTO).build();
        return deliveryCarFeeBaseParamsVO;
    }
}
