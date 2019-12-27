package com.atzuche.order.ownercost.service;

import com.atzuche.order.commons.enums.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.ownercost.entity.dto.OwnerOrderCostRespDTO;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.renterorder.entity.dto.RenterOrderCostRespDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OwnerOrderCalCostService {

    private OwnerOrderPurchaseDetailService ownerOrderPurchaseDetailService;

    public OwnerOrderCostRespDTO getOrderCostAndDeailList(RenterOrderCostRespDTO renterOrderCostRespDTO){
        List<RenterOrderCostDetailEntity> renterOrderCostDetailDTOList = renterOrderCostRespDTO.getRenterOrderCostDetailDTOList();
        List<RenterOrderSubsidyDetailEntity> renterOrderSubsidyDetailEntityList = renterOrderCostRespDTO.getRenterOrderSubsidyDetailEntityList();
        //过滤租金
        List<RenterOrderCostDetailEntity> rentDetailList = renterOrderCostDetailDTOList
                .stream()
                .filter(x -> RenterCashCodeEnum.RENT_AMT.getCashNo().equals(x.getCostCode()))
                .collect(Collectors.toList());
        //过滤车主相关的补贴(来源方属于车主)
        List<RenterOrderSubsidyDetailEntity> subsidyDetailList = renterOrderSubsidyDetailEntityList
                .stream()
                .filter(x -> SubsidySourceCodeEnum.OWNER.getCode().equals(x.getSubsidySourceCode()))
                .collect(Collectors.toList());


            return null;

    }

}
