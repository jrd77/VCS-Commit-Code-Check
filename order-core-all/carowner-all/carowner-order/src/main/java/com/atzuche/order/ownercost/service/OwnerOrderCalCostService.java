package com.atzuche.order.ownercost.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.ListUtil;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.ownercost.entity.OwnerOrderCostEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderIncrementDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderSubsidyDetailEntity;
import com.atzuche.order.ownercost.entity.dto.OwnerOrderCostReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    
    /**
     * 下单-车主端租车费用+明细+补贴 落库
     *
     * @author ZhangBin
     * @date 2019/12/27 16:12
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
        ownerOrderPurchaseDetailEntity.setTotalAmount(Math.abs(rentAmt));

        //计算取还车增值费用费用
        log.info("下单-车主端-获取取车费用，入参costBaseDTO=[{}],carOwnerType=[{}],srvGetFlag=[{}]",JSON.toJSONString(costBaseDTO),carOwnerType,srvGetFlag);
        OwnerOrderIncrementDetailEntity ownerSrvGetAmtEntity = ownerOrderCostCombineService.getOwnerSrvGetAmtEntity(costBaseDTO, carOwnerType, srvGetFlag);
        log.info("下单-车主端-获取取车费用ownerOrderNo=[{}],ownerSrvGetAmtEntity=[{}]",ownerOrderNo,JSON.toJSONString(ownerSrvGetAmtEntity));

        log.info("下单-车主端-获取还车费用，入参costBaseDTO=[{}],carOwnerType=[{}],srvReturnFlag=[{}]",JSON.toJSONString(costBaseDTO),carOwnerType,srvReturnFlag);
        OwnerOrderIncrementDetailEntity ownerSrvReturnAmtEntity = ownerOrderCostCombineService.getOwnerSrvReturnAmtEntity(costBaseDTO, carOwnerType, srvReturnFlag);
        log.info("下单-车主端-获取还车费用ownerOrderNo=[{}],ownerSrvReturnAmtEntity=[{}]",ownerOrderNo,ownerSrvReturnAmtEntity);
        int getTotalAmt = ownerSrvGetAmtEntity==null ? OrderConstant.ZERO : ownerSrvGetAmtEntity.getTotalAmount();
        int returnAmt = ownerSrvReturnAmtEntity ==null ? OrderConstant.ZERO : ownerSrvReturnAmtEntity.getTotalAmount();


        int realRentAmt  = rentAmt;
        int subsidyAmt = OrderConstant.ZERO;
        if(CollectionUtils.isNotEmpty(ownerOrderCostReqDTO.getOwnerOrderSubsidyDetails())) {
            for(OwnerOrderSubsidyDetailEntity s : ownerOrderCostReqDTO.getOwnerOrderSubsidyDetails()) {
                if(Objects.nonNull(s.getSubsidyAmount())) {
                    subsidyAmt = subsidyAmt + s.getSubsidyAmount();
                    if(StringUtils.equals(s.getSubsidyCostCode(), RenterCashCodeEnum.RENT_AMT.getCashNo())) {
                        realRentAmt = realRentAmt + s.getSubsidyAmount();
                    }
                }
            }
        }

        //平台服务费
        OwnerOrderIncrementDetailEntity serviceExpenseEntity = ownerOrderCostCombineService.getServiceExpenseIncrement(costBaseDTO,Math.abs(rentAmt),ownerOrderCostReqDTO.getServiceRate().intValue());
        int serviceExpense = null == serviceExpenseEntity ? OrderConstant.ZERO : serviceExpenseEntity.getTotalAmount();
        //代管车服务费
        OwnerOrderIncrementDetailEntity proxyServiceExpenseEntity = ownerOrderCostCombineService.getProxyServiceExpenseIncrement(costBaseDTO,Math.abs(rentAmt),ownerOrderCostReqDTO.getServiceProxyRate().intValue());
        int proxyServiceExpense = null == proxyServiceExpenseEntity ? OrderConstant.ZERO : proxyServiceExpenseEntity.getTotalAmount();
        //GPS服务费
        List<Integer> lsGpsSerialNumber = ListUtil.parse(ownerOrderCostReqDTO.getGpsSerialNumber(),",");
        List<OwnerOrderIncrementDetailEntity> gpsServiceAmtIncrementEntity = ownerOrderCostCombineService.getGpsServiceAmtIncrementEntity(costBaseDTO,lsGpsSerialNumber);
        log.info("GPS服务费计算结果.gpsServiceAmtIncrementEntity:[{}]",JSON.toJSONString(gpsServiceAmtIncrementEntity));
        int gpsServiceExpense = OrderConstant.ZERO;
        if(CollectionUtils.isNotEmpty(gpsServiceAmtIncrementEntity)) {
            gpsServiceExpense = gpsServiceAmtIncrementEntity.stream().mapToInt(OwnerOrderIncrementDetailEntity::getTotalAmount).sum();
        }
        int incrementAmt = getTotalAmt + returnAmt + serviceExpense + proxyServiceExpense + gpsServiceExpense;
        log.info("下单-车主端-准备保存费用，incrementAmt:[{}] = getTotalAmt:[{}] + returnAmt:[{}] + serviceExpense:[{}] + " +
                        "proxyServiceExpense:[{}] + gpsServiceExpense:[{}]",
                incrementAmt, getTotalAmt, returnAmt, serviceExpense, proxyServiceExpense, gpsServiceExpense);
        //车主端费用
        OwnerOrderCostEntity ownerOrderCostEntity = new OwnerOrderCostEntity();
        ownerOrderCostEntity.setOrderNo(orderNo);
        ownerOrderCostEntity.setOwnerOrderNo(ownerOrderNo);
        ownerOrderCostEntity.setMemNo(memNo);
        ownerOrderCostEntity.setIncrementAmount(incrementAmt);
        ownerOrderCostEntity.setSubsidyAmount(subsidyAmt);
        ownerOrderCostEntity.setPurchaseAmount(Math.abs(realRentAmt));
        ownerOrderCostEntity.setVersion(OrderConstant.ZERO);

        //车主端点增值服务费明细
        List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetailList = new ArrayList<>();
        ownerOrderIncrementDetailList.add(ownerSrvGetAmtEntity);
        ownerOrderIncrementDetailList.add(ownerSrvReturnAmtEntity);
        if(null != serviceExpenseEntity) {
            ownerOrderIncrementDetailList.add(serviceExpenseEntity);
        }

        if(null != proxyServiceExpenseEntity) {
            ownerOrderIncrementDetailList.add(proxyServiceExpenseEntity);
        }

        if(CollectionUtils.isNotEmpty(gpsServiceAmtIncrementEntity)) {
            ownerOrderIncrementDetailList.addAll(gpsServiceAmtIncrementEntity);
        }

        log.info("下单-车主端-保存增值费用，入参ownerOrderIncrementDetailList=[{}]",JSON.toJSONString(ownerOrderIncrementDetailList));
        Integer incrResult =
                ownerOrderIncrementDetailService.saveOwnerOrderIncrementDetailBatch(ownerOrderIncrementDetailList);
        log.info("下单-车主端-保存增值费用，结果IncrResult=[{}],ownerOrderIncrementDetailList=[{}],ownerOrderNo=[{}]",incrResult,JSON.toJSONString(ownerOrderIncrementDetailList),ownerOrderNo);

        log.info("下单-车主端-保存租金明细，入参param=[{}],ownerOrderNo=[{}]",JSON.toJSONString(ownerOrderPurchaseDetailEntity),ownerOrderNo);
        Integer purchaseResult = ownerOrderPurchaseDetailService.saveOwnerOrderPurchaseDetail(ownerOrderPurchaseDetailEntity);
        log.info("下单-车主端-保存租金明细，结果purchaseResult=[{}],OwnerOrderPurchaseDetail=[{}],ownerOrderNo=[{}]",purchaseResult,JSON.toJSONString(ownerOrderPurchaseDetailEntity),ownerOrderNo);

        Integer subsidyResult =
                ownerOrderSubsidyDetailService.saveOwnerOrderSubsidyDetailBatch(ownerOrderCostReqDTO.getOwnerOrderSubsidyDetails());
        log.info("下单-车主端-保存补贴费用明细结果subsidyResult=[{}]，param=[{}],ownerOrderNo=[{}]",subsidyResult,
                JSON.toJSONString(ownerOrderCostReqDTO.getOwnerOrderSubsidyDetails()),ownerOrderNo);

        Integer costResult = ownerOrderCostService.saveOwnerOrderCost(ownerOrderCostEntity);
        log.info("下单-车主端-保存费用总表，结果costResult=[{}]，param=[{}],ownerOrderNo=[{}]",costResult,JSON.toJSONString(ownerOrderCostEntity),ownerOrderNo);
    }


}
