package com.atzuche.order.ownercost.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.ownercost.entity.OwnerOrderCostEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderIncrementDetailEntity;
import com.atzuche.order.ownercost.entity.dto.OwnerOrderCostReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
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

        //计算租金和补贴
        int rentAmt = ownerOrderCostReqDTO.getOwnerOrderPurchaseDetailEntity().getTotalAmount();
        int subsidyAmt = ownerOrderCostReqDTO.getOwnerOrderSubsidyDetailEntity().getSubsidyAmount();

        //计算取还车增值费用费用
        OwnerOrderIncrementDetailEntity ownerSrvGetAmtEntity = ownerOrderCostCombineService.getOwnerSrvGetAmtEntity(costBaseDTO, carOwnerType, srvGetFlag);
        OwnerOrderIncrementDetailEntity ownerSrvReturnAmtEntity = ownerOrderCostCombineService.getOwnerSrvReturnAmtEntity(costBaseDTO, carOwnerType, srvReturnFlag);
        int getTotalAmt = ownerSrvGetAmtEntity==null ? 0:ownerSrvGetAmtEntity.getTotalAmount();
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

        log.info("车主端-保存租金、租金明细、补贴明细和增值费用准备入库，ownerOrderCostReqDTO={}", JSON.toJSONString(ownerOrderCostReqDTO));
        List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetailList = Arrays.asList(ownerSrvGetAmtEntity, ownerSrvReturnAmtEntity);
        Integer integer = ownerOrderIncrementDetailService.saveOwnerOrderIncrementDetailBatch(ownerOrderIncrementDetailList);
        log.info("车主端-保存增值费用 ownerOrderIncrementDetailList={},ownerOrderNo={},result={}",JSON.toJSONString(ownerOrderIncrementDetailList),ownerOrderNo,integer);
        Integer integer1 = ownerOrderPurchaseDetailService.saveOwnerOrderPurchaseDetail(ownerOrderCostReqDTO.getOwnerOrderPurchaseDetailEntity());
        log.info("车主端-保存租金明细，OwnerOrderPurchaseDetail={},ownerOrderNo={}",JSON.toJSONString(ownerOrderCostReqDTO.getOwnerOrderPurchaseDetailEntity()),ownerOrderNo);
        Integer integer2 = ownerOrderSubsidyDetailService.saveOwnerOrderSubsidyDetail(ownerOrderCostReqDTO.getOwnerOrderSubsidyDetailEntity());

        Integer integer3 = ownerOrderCostService.saveOwnerOrderCost(ownerOrderCostEntity);
    }


}
