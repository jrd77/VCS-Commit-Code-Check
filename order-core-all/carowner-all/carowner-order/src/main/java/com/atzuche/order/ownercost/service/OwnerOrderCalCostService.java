package com.atzuche.order.ownercost.service;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.ownercost.entity.OwnerOrderCostEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderIncrementDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderSubsidyDetailEntity;
import com.atzuche.order.ownercost.entity.dto.OwnerOrderCostReqDTO;
import com.atzuche.order.ownercost.entity.dto.OwnerOrderSubsidyDetailDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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
    @Autowired
    private OwnerOrderCostCombineService ownerOrderCostCombineService;
    @Autowired
    private OwnerOrderIncrementDetailService ownerOrderIncrementDetailService;
    
    /*
     * @Author ZhangBin
     * @Date 2019/12/27 16:12
     * @Description: 车主端租车费用+明细+补贴 落库
     * 
     **/
    public void getOrderCostAndDeailList(OwnerOrderCostReqDTO ownerOrderCostReqDTO){
        CostBaseDTO costBaseDTO = ownerOrderCostReqDTO.getCostBaseDTO();
        String orderNo = costBaseDTO.getOrderNo();
        String ownerOrderNo = costBaseDTO.getOwnerOrderNo();
        String memNo = costBaseDTO.getMemNo();
        Integer carOwnerType = ownerOrderCostReqDTO.getCarOwnerType();
        Integer srvGetFlag = ownerOrderCostReqDTO.getSrvGetFlag();
        Integer srvReturnFlag = ownerOrderCostReqDTO.getSrvReturnFlag();

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
        //计算租金和补贴
        int rentAmt = ownerOrderPurchaseDetailList.stream().collect(Collectors.summingInt(OwnerOrderPurchaseDetailEntity::getTotalAmount));
        int subsidyAmt = ownerOrderSubsidyDetailDTOList.stream().collect(Collectors.summingInt(OwnerOrderSubsidyDetailDTO::getSubsidyAmount));

        //计算取还车增值费用费用
        OwnerOrderIncrementDetailEntity ownerSrvGetAmtEntity = ownerOrderCostCombineService.getOwnerSrvGetAmtEntity(costBaseDTO, carOwnerType, srvGetFlag);
        OwnerOrderIncrementDetailEntity ownerSrvReturnAmtEntity = ownerOrderCostCombineService.getOwnerSrvReturnAmtEntity(costBaseDTO, carOwnerType, srvReturnFlag);
        int getTotalAmt = ownerSrvGetAmtEntity==null?0:ownerSrvGetAmtEntity.getTotalAmount();
        int returnAmt = ownerSrvReturnAmtEntity ==null ? 0:ownerSrvReturnAmtEntity.getTotalAmount();
        int incrementAmt = getTotalAmt + returnAmt;

        OwnerOrderCostEntity ownerOrderCostEntity = new OwnerOrderCostEntity();
        ownerOrderCostEntity.setOrderNo(orderNo);
        ownerOrderCostEntity.setOwnerOrderNo(ownerOrderNo);
        ownerOrderCostEntity.setMemNo(memNo);
        ownerOrderCostEntity.setIncrementAmount(incrementAmt);
        ownerOrderCostEntity.setSubsidyAmount(subsidyAmt);
        ownerOrderCostEntity.setPurchaseAmount(rentAmt);
        ownerOrderCostEntity.setVersion(0);

        ownerOrderIncrementDetailService.saveOwnerOrderIncrementDetailBatch(Arrays.asList(ownerSrvGetAmtEntity,ownerSrvReturnAmtEntity));
        ownerOrderPurchaseDetailService.saveOwnerOrderPurchaseDetailBatch(ownerOrderPurchaseDetailList);
        ownerOrderSubsidyDetailService.saveOwnerOrderSubsidyDetailBatch(ownerSubsidyDetailList);
        ownerOrderCostService.saveOwnerOrderCost(ownerOrderCostEntity);
    }


}
