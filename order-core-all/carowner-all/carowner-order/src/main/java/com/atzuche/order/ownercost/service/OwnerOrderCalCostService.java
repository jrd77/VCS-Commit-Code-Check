package com.atzuche.order.ownercost.service;

import com.atzuche.order.ownercost.entity.OwnerOrderCostEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderSubsidyDetailEntity;
import com.atzuche.order.ownercost.entity.dto.OwnerOrderCostReqDTO;
import com.atzuche.order.ownercost.entity.dto.OwnerOrderSubsidyDetailDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OwnerOrderCalCostService {
    @Autowired
    private OwnerOrderPurchaseDetailService ownerOrderPurchaseDetailService;
    @Autowired
    private OwnerOrderSubsidyDetailService ownerOrderSubsidyDetailService;
    @Autowired
    private OwnerOrderCostService ownerOrderCostService;
    
    /*
     * @Author ZhangBin
     * @Date 2019/12/27 16:12
     * @Description: 车主端租车费用+明细+补贴 落库
     * 
     **/
    public void getOrderCostAndDeailList(OwnerOrderCostReqDTO ownerOrderCostReqDTO){
        String orderNo = ownerOrderCostReqDTO.getOrderNo();
        String ownerOrderNo = ownerOrderCostReqDTO.getOwnerOrderNo();
        String memNo = ownerOrderCostReqDTO.getMemNo();
        List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetailList = ownerOrderCostReqDTO.getOwnerOrderPurchaseDetailList();
        List<OwnerOrderSubsidyDetailDTO> ownerOrderSubsidyDetailDTOList = ownerOrderCostReqDTO.getOwnerOrderSubsidyDetailDTOList();

        List<OwnerOrderSubsidyDetailEntity> ownerSubsidyDetailList = ownerOrderSubsidyDetailDTOList
                .stream()
                .map(x -> {
                    OwnerOrderSubsidyDetailEntity ownerSubsidyDetail = new OwnerOrderSubsidyDetailEntity();
                    BeanUtils.copyProperties(x,ownerSubsidyDetail);
                    return ownerSubsidyDetail;
                })
                .collect(Collectors.toList());

        int rentAmt = ownerOrderPurchaseDetailList.stream().collect(Collectors.summingInt(OwnerOrderPurchaseDetailEntity::getTotalAmount));
        int subsidyAmt = ownerOrderSubsidyDetailDTOList.stream().collect(Collectors.summingInt(OwnerOrderSubsidyDetailDTO::getSubsidyAmount));
        OwnerOrderCostEntity ownerOrderCostEntity = new OwnerOrderCostEntity();
        ownerOrderCostEntity.setOrderNo(orderNo);
        ownerOrderCostEntity.setOwnerOrderNo(ownerOrderNo);
        ownerOrderCostEntity.setMemNo(memNo);
        ownerOrderCostEntity.setIncrementAmount(0);
        ownerOrderCostEntity.setSubsidyAmount(subsidyAmt);
        ownerOrderCostEntity.setPurchaseAmount(rentAmt);
        ownerOrderCostEntity.setVersion(0);

        ownerOrderPurchaseDetailService.saveOwnerOrderPurchaseDetailBatch(ownerOrderPurchaseDetailList);
        ownerOrderSubsidyDetailService.saveOwnerOrderSubsidyDetailBatch(ownerSubsidyDetailList);
        ownerOrderCostService.saveOwnerOrderCost(ownerOrderCostEntity);
    }

}
