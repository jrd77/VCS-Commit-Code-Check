package com.atzuche.order.ownercost.service;

import com.atzuche.order.commons.enums.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.ownercost.entity.OwnerOrderCostEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderSubsidyDetailEntity;
import com.atzuche.order.ownercost.entity.dto.OwnerOrderCostReqDTO;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.entity.dto.RenterOrderSubsidyDetailDTO;
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
     * @Description: 车主端费用及其明细入库
     * 
     **/
    public void getOrderCostAndDeailList(OwnerOrderCostReqDTO ownerOrderCostReqDTO){
        String orderNo = ownerOrderCostReqDTO.getOrderNo();
        String ownerOrderNo = ownerOrderCostReqDTO.getOwnerOrderNo();
        String memNo = ownerOrderCostReqDTO.getMemNo();
        List<RenterOrderCostDetailEntity> renterOrderCostDetailDTOList = ownerOrderCostReqDTO.getRenterOrderCostDetailDTOList();
        List<RenterOrderSubsidyDetailDTO> renterOrderSubsidyDetailDTOList = ownerOrderCostReqDTO.getRenterOrderSubsidyDetailDTOList();

        //过滤租金
        List<OwnerOrderPurchaseDetailEntity> ownerDetailList = renterOrderCostDetailDTOList
                .stream()
                .filter(x -> RenterCashCodeEnum.RENT_AMT.getCashNo().equals(x.getCostCode()))
                .map(x -> {
                    OwnerOrderPurchaseDetailEntity ownerDetail = new OwnerOrderPurchaseDetailEntity();
                    ownerDetail.setOrderNo(orderNo);
                    ownerDetail.setOwnerOrderNo(ownerOrderNo);
                    ownerDetail.setMemNo(memNo);
                    ownerDetail.setCostCode(OwnerCashCodeEnum.RENT_AMT.getCashNo());
                    ownerDetail.setCostCodeDesc(OwnerCashCodeEnum.RENT_AMT.getTxt());
                    ownerDetail.setUnitPrice(x.getUnitPrice());
                    ownerDetail.setCount(x.getCount());
                    ownerDetail.setTotalAmount(x.getTotalAmount());
                    return ownerDetail;
                })
                .collect(Collectors.toList());

        //过滤车主相关的补贴(来源方属于车主)
        List<OwnerOrderSubsidyDetailEntity> ownerSubsidyDetailList = renterOrderSubsidyDetailDTOList
                .stream()
                .filter(x -> SubsidySourceCodeEnum.OWNER.getCode().equals(x.getSubsidySourceCode()))
                .map(x -> {
                    OwnerOrderSubsidyDetailEntity ownerSubsidyDetail = new OwnerOrderSubsidyDetailEntity();
                    BeanUtils.copyProperties(x,ownerSubsidyDetail);
                    ownerSubsidyDetail.setOrderNo(orderNo);
                    ownerSubsidyDetail.setOwnerOrderNo(ownerOrderNo);
                    ownerSubsidyDetail.setMemNo(memNo);
                    ownerSubsidyDetail.setSubsidyAmount(-x.getSubsidyAmount());
                    return ownerSubsidyDetail;
                })
                .collect(Collectors.toList());

        //费用统计
        int rentAmt = ownerDetailList.stream().collect(Collectors.summingInt(OwnerOrderPurchaseDetailEntity::getTotalAmount));
        int subsidyAmt = ownerSubsidyDetailList.stream().collect(Collectors.summingInt(OwnerOrderSubsidyDetailEntity::getSubsidyAmount));
        OwnerOrderCostEntity ownerOrderCostEntity = new OwnerOrderCostEntity();
        ownerOrderCostEntity.setOrderNo(orderNo);
        ownerOrderCostEntity.setOwnerOrderNo(ownerOrderNo);
        ownerOrderCostEntity.setMemNo(memNo);
        ownerOrderCostEntity.setIncrementAmount(0);
        ownerOrderCostEntity.setSubsidyAmount(subsidyAmt);
        ownerOrderCostEntity.setPurchaseAmount(rentAmt);
        ownerOrderCostEntity.setVersion(0);

        ownerOrderPurchaseDetailService.saveOwnerOrderPurchaseDetailBatch(ownerDetailList);
        ownerOrderSubsidyDetailService.saveOwnerOrderSubsidyDetailBatch(ownerSubsidyDetailList);
        ownerOrderCostService.saveOwnerOrderCost(ownerOrderCostEntity);
    }

}
