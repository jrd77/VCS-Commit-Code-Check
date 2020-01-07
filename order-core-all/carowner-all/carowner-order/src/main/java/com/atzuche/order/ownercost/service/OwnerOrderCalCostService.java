package com.atzuche.order.ownercost.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.ownercost.entity.OwnerOrderCostEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderIncrementDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
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
     * @Description: 下单-车主端租车费用+明细+补贴 落库
     * 
     **/
    public void getOrderCostAndDeailList(OwnerOrderCostReqDTO ownerOrderCostReqDTO){
        log.info("下单-车主端-保存租金、租金明细、补贴明细和增值费用准备入库，ownerOrderCostReqDTO=[{}]", JSON.toJSONString(ownerOrderCostReqDTO));
        CostBaseDTO costBaseDTO = ownerOrderCostReqDTO.getCostBaseDTO();
        String orderNo = costBaseDTO.getOrderNo();
        String ownerOrderNo = costBaseDTO.getOwnerOrderNo();
        String memNo = costBaseDTO.getMemNo();
        Integer carOwnerType = ownerOrderCostReqDTO.getCarOwnerType();
        Integer srvGetFlag = ownerOrderCostReqDTO.getSrvGetFlag();
        Integer srvReturnFlag = ownerOrderCostReqDTO.getSrvReturnFlag();

        //计算租金和补贴
        OwnerOrderPurchaseDetailEntity ownerOrderPurchaseDetailEntity = ownerOrderCostReqDTO.getOwnerOrderPurchaseDetailEntity();
        int rentAmt = ownerOrderPurchaseDetailEntity.getTotalAmount();
        int subsidyAmt = ownerOrderCostReqDTO.getOwnerOrderSubsidyDetailEntity()==null?0:ownerOrderCostReqDTO.getOwnerOrderSubsidyDetailEntity().getSubsidyAmount();
        ownerOrderPurchaseDetailEntity.setTotalAmount(Math.abs(rentAmt));

        //计算取还车增值费用费用
        log.info("下单-车主端-获取取车费用，入参costBaseDTO=[{}],carOwnerType=[{}],srvGetFlag=[{}]",JSON.toJSONString(costBaseDTO),carOwnerType,srvGetFlag);
        OwnerOrderIncrementDetailEntity ownerSrvGetAmtEntity = ownerOrderCostCombineService.getOwnerSrvGetAmtEntity(costBaseDTO, carOwnerType, srvGetFlag);
        log.info("下单-车主端-获取取车费用ownerOrderNo=[{}],ownerSrvGetAmtEntity=[{}]",ownerOrderNo,JSON.toJSONString(ownerSrvGetAmtEntity));

        log.info("下单-车主端-获取还车费用，入参costBaseDTO=[{}],carOwnerType=[{}],srvReturnFlag=[{}]",JSON.toJSONString(costBaseDTO),carOwnerType,srvReturnFlag);
        OwnerOrderIncrementDetailEntity ownerSrvReturnAmtEntity = ownerOrderCostCombineService.getOwnerSrvReturnAmtEntity(costBaseDTO, carOwnerType, srvReturnFlag);
        log.info("下单-车主端-获取还车费用ownerOrderNo=[{}],ownerSrvReturnAmtEntity=[{}]",ownerOrderNo,ownerSrvReturnAmtEntity);
        int getTotalAmt = ownerSrvGetAmtEntity==null ? 0:ownerSrvGetAmtEntity.getTotalAmount();
        int returnAmt = ownerSrvReturnAmtEntity ==null ? 0:ownerSrvReturnAmtEntity.getTotalAmount();
        int incrementAmt = getTotalAmt + returnAmt;

        OwnerOrderCostEntity ownerOrderCostEntity = new OwnerOrderCostEntity();
        ownerOrderCostEntity.setOrderNo(orderNo);
        ownerOrderCostEntity.setOwnerOrderNo(ownerOrderNo);
        ownerOrderCostEntity.setMemNo(memNo);
        ownerOrderCostEntity.setIncrementAmount(incrementAmt);
        ownerOrderCostEntity.setSubsidyAmount(subsidyAmt);
        ownerOrderCostEntity.setPurchaseAmount(Math.abs(rentAmt));
        ownerOrderCostEntity.setVersion(0);

        log.info("下单-车主端-准备保存费用，入参ownerOrderNo=[{}]",ownerOrderNo);
        List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetailList = Arrays.asList(ownerSrvGetAmtEntity, ownerSrvReturnAmtEntity);

        log.info("下单-车主端-保存增值费用，入参ownerOrderIncrementDetailList=[{}]",JSON.toJSONString(ownerOrderIncrementDetailList));
        Integer IncrResult = ownerOrderIncrementDetailService.saveOwnerOrderIncrementDetailBatch(ownerOrderIncrementDetailList);
        log.info("下单-车主端-保存增值费用，结果IncrResult=[{}],ownerOrderIncrementDetailList=[{}],ownerOrderNo=[{}]",IncrResult,JSON.toJSONString(ownerOrderIncrementDetailList),ownerOrderNo);

        log.info("下单-车主端-保存租金明细，入参param=[{}],ownerOrderNo=[{}]",JSON.toJSONString(ownerOrderPurchaseDetailEntity),ownerOrderNo);
        Integer purchaseResult = ownerOrderPurchaseDetailService.saveOwnerOrderPurchaseDetail(ownerOrderPurchaseDetailEntity);
        log.info("下单-车主端-保存租金明细，结果purchaseResult=[{}],OwnerOrderPurchaseDetail=[{}],ownerOrderNo=[{}]",purchaseResult,JSON.toJSONString(ownerOrderPurchaseDetailEntity),ownerOrderNo);

        Integer subsidyResult = ownerOrderSubsidyDetailService.saveOwnerOrderSubsidyDetail(ownerOrderCostReqDTO.getOwnerOrderSubsidyDetailEntity());
        log.info("下单-车主端-保存补贴费用明细结果subsidyResult=[{}]，paran=[{}],ownerOrderNo=[{}]",subsidyResult,JSON.toJSONString(ownerOrderCostReqDTO.getOwnerOrderSubsidyDetailEntity()),ownerOrderNo);

        Integer costResult = ownerOrderCostService.saveOwnerOrderCost(ownerOrderCostEntity);
        log.info("下单-车主端-保存费用总表，结果costResult=[{}]，param=[{}],ownerOrderNo=[{}]",costResult,JSON.toJSONString(ownerOrderCostEntity),ownerOrderNo);
    }


}
