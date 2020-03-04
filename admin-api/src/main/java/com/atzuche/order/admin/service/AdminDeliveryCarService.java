package com.atzuche.order.admin.service;

import com.atzuche.order.admin.vo.resp.cost.DistributionCostVO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.req.ModifyOrderReqVO;
import com.atzuche.order.commons.vo.req.handover.req.HandoverCarInfoReqDTO;
import com.atzuche.order.commons.vo.req.handover.req.HandoverCarInfoReqVO;
import com.atzuche.order.delivery.common.DeliveryErrorCode;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.enums.CarTypeEnum;
import com.atzuche.order.delivery.enums.OilCostTypeEnum;
import com.atzuche.order.delivery.exception.DeliveryOrderException;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarInfoService;
import com.atzuche.order.delivery.service.handover.HandoverCarInfoService;
import com.atzuche.order.delivery.service.handover.RenterHandoverCarService;
import com.atzuche.order.delivery.vo.delivery.rep.*;
import com.atzuche.order.delivery.vo.delivery.req.CarConditionPhotoUploadVO;
import com.atzuche.order.delivery.vo.delivery.req.DeliveryCarRepVO;
import com.atzuche.order.delivery.vo.delivery.req.DeliveryReqDTO;
import com.atzuche.order.delivery.vo.delivery.req.DeliveryReqVO;
import com.atzuche.order.open.service.FeignOrderModifyService;
import com.atzuche.order.rentercommodity.service.RenterCommodityService;
import com.atzuche.order.rentercost.entity.RenterOrderCostDetailEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostDetailService;
import com.atzuche.order.rentermem.service.RenterMemberService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.transport.service.GetReturnCarCostProxyService;
import com.atzuche.order.transport.service.TranSportProxyService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author 胡春林
 * 配送信息服务
 */
@Service
public class AdminDeliveryCarService {

    @Autowired
    DeliveryCarInfoService deliveryCarInfoService;
    @Autowired
    HandoverCarInfoService handoverCarInfoService;
    @Autowired
    RenterHandoverCarService renterHandoverCarService;
    @Autowired
    RenterCommodityService renterCommodityService;
    @Autowired
    RenterOrderDeliveryService renterOrderDeliveryService;
    @Autowired
    TranSportProxyService tranSportProxyService;
    @Autowired
    GetReturnCarCostProxyService getReturnCarCostProxyService;

    @Autowired
    RenterOrderCostDetailService renterOrderCostDetailService;
    @Autowired
    FeignOrderModifyService feignOrderModifyService;
    @Autowired
    RenterMemberService renterMemberService;
    @Autowired
    RenterOrderService renterOrderService;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取配送相关信息
     * @param deliveryCarDTO
     * @return
     */
    public DeliveryCarVO findDeliveryListByOrderNo(DeliveryCarRepVO deliveryCarDTO) {
        logger.info("入参deliveryCarDTO：[{}]", deliveryCarDTO.toString());
        // 获取租客商品信息
        RenterOrderDeliveryEntity renterOrderDeliveryEntity = renterOrderDeliveryService.findRenterOrderByrOrderNo(deliveryCarDTO.getOrderNo(),1);
        if(null == renterOrderDeliveryEntity)
        {
            throw new DeliveryOrderException(DeliveryErrorCode.NO_DELIVERY_INFO);
        }
        RenterGoodsDetailDTO renterGoodsDetailDTO = renterCommodityService.getRenterGoodsDetail(renterOrderDeliveryEntity.getRenterOrderNo(), false);
        OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO = OwnerGetAndReturnCarDTO.builder().build();
        ownerGetAndReturnCarDTO.setRanLiao(String.valueOf(OilCostTypeEnum.getOilCostType(renterGoodsDetailDTO.getCarEngineType())));
        String daykM = renterGoodsDetailDTO.getCarDayMileage().intValue() == 0 ? "不限" :String.valueOf(renterGoodsDetailDTO.getCarDayMileage());
        ownerGetAndReturnCarDTO.setDayKM(daykM);
        ownerGetAndReturnCarDTO.setOilContainer(String.valueOf(renterGoodsDetailDTO.getCarOilVolume())+"L");
        boolean isEscrowCar = CarTypeEnum.isCarType(renterGoodsDetailDTO.getCarType());
        int carType = renterGoodsDetailDTO.getCarType();
        return deliveryCarInfoService.findDeliveryListByOrderNo(renterOrderDeliveryEntity.getRenterOrderNo(),deliveryCarDTO,ownerGetAndReturnCarDTO,isEscrowCar,renterGoodsDetailDTO.getCarEngineType(),carType,renterGoodsDetailDTO);
    }

    /**
     * 上传交接车
     * @param photoUploadReqVo
     * @return
     */
    public Boolean uploadByOrderNo(CarConditionPhotoUploadVO photoUploadReqVo) throws Exception {
        logger.info("入参photoUploadReqVo：[{}]", photoUploadReqVo.toString());
        return handoverCarInfoService.uploadByOrderNo(photoUploadReqVo);
    }

    /**
     * 更新交接车信息
     * @param deliveryCarVO
     * @throws Exception
     */
    public void updateHandoverCarInfo(DeliveryCarVO deliveryCarVO) throws Exception {
        logger.info("入参handoverCarReqVO：[{}]", deliveryCarVO.toString());
        handoverCarInfoService.updateHandoverCarInfo(createHandoverCarInfoParams(deliveryCarVO));
    }

    /**
     * 更新取还车信息 更新仁云接口
     * @param deliveryCarVO
     * @throws Exception
     */
    public void updateDeliveryCarInfo(DeliveryCarVO deliveryCarVO) throws Exception {
        logger.info("入参deliveryReqVO：[{}]", deliveryCarVO.toString());
        handoverCarInfoService.updateDeliveryCarInfo(createParams(deliveryCarVO));
//        ResponseData responseData = feignOrderModifyService.modifyOrderForConsole(createModifyOrderInfoParams(deliveryCarVO));
//        if (!responseData.getResCode().equals(ErrorCode.SUCCESS.getCode())) {
//            logger.info("修改订单失败，orderNo：[{}],cause:[{}]", deliveryCarVO.getOrderNo(), responseData.getResMsg());
//            return;
//        }
    }

    /**
     * 更新订单参数
     * @param deliveryCarVO
     * @return
     */
    public ModifyOrderReqVO createModifyOrderInfoParams(DeliveryCarVO deliveryCarVO){
        String memNo = renterMemberService.getRenterNoByOrderNo(deliveryCarVO.getOrderNo());
        ModifyOrderReqVO modifyOrderReqVO = new ModifyOrderReqVO();
        modifyOrderReqVO.setOrderNo(deliveryCarVO.getOrderNo());
        if(StringUtils.isNotBlank(memNo)) {
            modifyOrderReqVO.setMemNo(memNo);
        }
        if(Objects.nonNull(deliveryCarVO.getGetHandoverCarDTO())) {
            modifyOrderReqVO.setGetCarAddress(deliveryCarVO.getGetHandoverCarDTO().getRenterRealGetAddr());
            modifyOrderReqVO.setGetCarLat(deliveryCarVO.getGetHandoverCarDTO().getRenterRealGetLat());
            modifyOrderReqVO.setGetCarLon(deliveryCarVO.getGetHandoverCarDTO().getRenterRealGetLng());
        }
        if(Objects.nonNull(deliveryCarVO.getReturnHandoverCarDTO())) {
            modifyOrderReqVO.setRevertCarAddress(deliveryCarVO.getReturnHandoverCarDTO().getRenterRealReturnAddr());
            modifyOrderReqVO.setRevertCarLat(deliveryCarVO.getReturnHandoverCarDTO().getRenterRealReturnLat());
            modifyOrderReqVO.setRevertCarLon(deliveryCarVO.getReturnHandoverCarDTO().getOwnerRealGetLng());
        }
        modifyOrderReqVO.setSrvGetFlag(deliveryCarVO.getIsGetCar());
        modifyOrderReqVO.setSrvReturnFlag(deliveryCarVO.getIsReturnCar());
        return modifyOrderReqVO;
    }

    public DeliveryReqVO createParams(DeliveryCarVO deliveryCarVO) {
        //取车服务信息
        DeliveryReqVO deliveryReqVO = new DeliveryReqVO();
        GetHandoverCarDTO getHandoverCarDTO = deliveryCarVO.getGetHandoverCarDTO();
        ReturnHandoverCarDTO returnHandoverCarDTO = deliveryCarVO.getReturnHandoverCarDTO();
        if (Objects.nonNull(getHandoverCarDTO)) {
            if(StringUtils.isBlank(getHandoverCarDTO.getRenterRealGetAddr()) || StringUtils.isBlank(getHandoverCarDTO.getOwnRealReturnAddr()))
            {
                logger.info("取车地址相关信息不能为空");
                throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
            }
            DeliveryReqDTO deliveryReqDTO = new DeliveryReqDTO();
            deliveryReqDTO.setIsUsedGetAndReturnCar(String.valueOf(deliveryCarVO.getIsGetCar()));
            deliveryReqDTO.setOrderNo(deliveryCarVO.getOrderNo());
            deliveryReqDTO.setOwnerRealGetAddrReamrk(getHandoverCarDTO.getOwnRealGetRemark());
            deliveryReqDTO.setRenterRealGetAddrReamrk(getHandoverCarDTO.getRenterRealGetAddrReamrk());
            deliveryReqDTO.setRenterGetReturnAddr(getHandoverCarDTO.getRenterRealGetAddr());
            deliveryReqDTO.setOwnerGetReturnAddr(getHandoverCarDTO.getOwnRealReturnAddr());
            deliveryReqDTO.setOwnerGetReturnLat(getHandoverCarDTO.getOwnRealReturnLat());
            deliveryReqDTO.setOwnerGetReturnLng(getHandoverCarDTO.getOwnRealReturnLng());
            deliveryReqDTO.setRenterGetReturnLat(getHandoverCarDTO.getRenterRealGetLat());
            deliveryReqDTO.setRenterGetReturnLng(getHandoverCarDTO.getRenterRealGetLng());
            deliveryReqDTO.setIsUsedGetAndReturnCar(String.valueOf(deliveryCarVO.getIsGetCar()));
            deliveryReqVO.setGetDeliveryReqDTO(deliveryReqDTO);
        }
        if (Objects.nonNull(returnHandoverCarDTO)) {
            if(StringUtils.isBlank(returnHandoverCarDTO.getRenterRealReturnAddr()) || StringUtils.isBlank(returnHandoverCarDTO.getOwnerRealGetAddr()))
            {
                logger.info("还车地址相关信息不能为空");
                throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
            }
            DeliveryReqDTO renterDeliveryReqDTO = new DeliveryReqDTO();
            renterDeliveryReqDTO.setIsUsedGetAndReturnCar(String.valueOf(deliveryCarVO.getIsReturnCar()));
            renterDeliveryReqDTO.setOrderNo(deliveryCarVO.getOrderNo());
            renterDeliveryReqDTO.setRenterRealGetAddrReamrk(returnHandoverCarDTO.getRenterRealGetRemark());
            renterDeliveryReqDTO.setOwnerRealGetAddrReamrk(returnHandoverCarDTO.getOwnerRealGetAddrReamrk());
            renterDeliveryReqDTO.setRenterGetReturnAddr(returnHandoverCarDTO.getRenterRealReturnAddr());
            renterDeliveryReqDTO.setOwnerGetReturnAddr(returnHandoverCarDTO.getOwnerRealGetAddr());
            renterDeliveryReqDTO.setOwnerGetReturnLat(returnHandoverCarDTO.getOwnerRealGetLat());
            renterDeliveryReqDTO.setOwnerGetReturnLng(returnHandoverCarDTO.getOwnerRealGetLng());
            renterDeliveryReqDTO.setRenterGetReturnLat(returnHandoverCarDTO.getRenterRealReturnLat());
            renterDeliveryReqDTO.setRenterGetReturnLng(returnHandoverCarDTO.getRenterRealReturnLng());
            renterDeliveryReqDTO.setIsUsedGetAndReturnCar(String.valueOf(deliveryCarVO.getIsReturnCar()));
            deliveryReqVO.setRenterDeliveryReqDTO(renterDeliveryReqDTO);
        }
        return deliveryReqVO;
    }

    /**
     * 构造交接车数据
     * @param deliveryCarVO
     * @return
     */
    public HandoverCarInfoReqVO createHandoverCarInfoParams(DeliveryCarVO deliveryCarVO) {
        HandoverCarInfoReqVO handoverCarReqVO = new HandoverCarInfoReqVO();
        if (Objects.nonNull(deliveryCarVO.getRenterGetAndReturnCarDTO())) {
            RenterGetAndReturnCarDTO renterGetAndReturnCarDTO = deliveryCarVO.getRenterGetAndReturnCarDTO();
            HandoverCarInfoReqDTO handoverCarInfoReqDTO = new HandoverCarInfoReqDTO();
            handoverCarInfoReqDTO.setOrderNo(deliveryCarVO.getOrderNo());
            handoverCarInfoReqDTO.setOwnReturnKM(renterGetAndReturnCarDTO.getReturnKM());
            handoverCarInfoReqDTO.setOwnReturnOil(renterGetAndReturnCarDTO.getReturnCarOil());
            handoverCarInfoReqDTO.setRenterRetrunKM(renterGetAndReturnCarDTO.getGetKM());
            handoverCarInfoReqDTO.setRenterReturnOil(renterGetAndReturnCarDTO.getGetCarOil());
            handoverCarReqVO.setRenterHandoverCarDTO(handoverCarInfoReqDTO);

        }
        if (Objects.nonNull(deliveryCarVO.getOwnerGetAndReturnCarDTO())) {
            OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO = deliveryCarVO.getOwnerGetAndReturnCarDTO();
            HandoverCarInfoReqDTO handoverCarInfoReqDTO = new HandoverCarInfoReqDTO();
            handoverCarInfoReqDTO.setOrderNo(deliveryCarVO.getOrderNo());
            handoverCarInfoReqDTO.setOwnReturnKM(ownerGetAndReturnCarDTO.getReturnKM());
            handoverCarInfoReqDTO.setOwnReturnOil(ownerGetAndReturnCarDTO.getReturnCarOil());
            handoverCarInfoReqDTO.setRenterRetrunKM(ownerGetAndReturnCarDTO.getGetKM());
            handoverCarInfoReqDTO.setRenterReturnOil(ownerGetAndReturnCarDTO.getGetCarOil());
            handoverCarReqVO.setOwnerHandoverCarDTO(handoverCarInfoReqDTO);
        }
        return handoverCarReqVO;
    }

    /**
     * 获取配送取还车信息
     * @param deliveryCarDTO
     * @return
     */
    public DistributionCostVO findDeliveryCostByOrderNo(DeliveryCarRepVO deliveryCarDTO) {
        logger.info("入参deliveryCarDTO：[{}]", deliveryCarDTO.toString());
        DistributionCostVO distributionCostVO = DistributionCostVO.builder().build();
        distributionCostVO.setReturnCarChaoYunNeng("0");
        distributionCostVO.setGetCarChaoYunNeng("0");
        distributionCostVO.setRenturnCarAmt("0");
        distributionCostVO.setGetCarAmt("0");
        // 获取租客商品信息
//        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(deliveryCarDTO.getOrderNo());
//        if(null == renterOrderEntity)
//        {
//            return distributionCostVO;
//        }
        List<RenterOrderCostDetailEntity> list = renterOrderCostDetailService.listRenterOrderCostDetail(deliveryCarDTO.getOrderNo(), deliveryCarDTO.getRenterOrderNo());
        if (CollectionUtils.isEmpty(list)) {
            return distributionCostVO;
        }

        list.stream().filter(r -> r.getCostCode().equals(RenterCashCodeEnum.SRV_GET_COST.getCashNo())).findFirst().ifPresent(getCrashVO -> {
            distributionCostVO.setGetCarAmt(String.valueOf(getCrashVO.getUnitPrice() * getCrashVO.getCount()));
        });
        list.stream().filter(r -> r.getCostCode().equals(RenterCashCodeEnum.SRV_RETURN_COST.getCashNo())).findFirst().ifPresent(getCrashVO -> {
            distributionCostVO.setRenturnCarAmt(String.valueOf(getCrashVO.getUnitPrice() * getCrashVO.getCount()));
        });
        list.stream().filter(r -> r.getCostCode().equals(RenterCashCodeEnum.GET_BLOCKED_RAISE_AMT.getCashNo())).findFirst().ifPresent(getCrashVO -> {
            distributionCostVO.setGetCarChaoYunNeng(String.valueOf(getCrashVO.getUnitPrice() * getCrashVO.getCount()));
        });
        list.stream().filter(r -> r.getCostCode().equals(RenterCashCodeEnum.RETURN_BLOCKED_RAISE_AMT.getCashNo())).findFirst().ifPresent(getCrashVO -> {
            distributionCostVO.setReturnCarChaoYunNeng(String.valueOf(getCrashVO.getUnitPrice() * getCrashVO.getCount()));
        });
        return distributionCostVO;
    }
//    /**
//     * 获取配送取还车信息
//     * @param deliveryCarDTO
//     * @return
//     */
//    public DistributionCostVO findDeliveryCostByOrderNo(DeliveryCarRepVO deliveryCarDTO) {
//        logger.info("入参deliveryCarDTO：[{}]", deliveryCarDTO.toString());
//        DistributionCostVO distributionCostVO = DistributionCostVO.builder().build();
//        // 获取租客商品信息
//        List<RenterOrderDeliveryEntity> renterOrderDeliveryEntityList = renterOrderDeliveryService.findRenterOrderListByOrderNo(deliveryCarDTO.getOrderNo());
//        if(CollectionUtils.isEmpty(renterOrderDeliveryEntityList))
//        {
//            return distributionCostVO;
//        }
//        RenterOrderDeliveryEntity renterOrderDeliveryEntity = renterOrderDeliveryEntityList.get(0);
//        RenterGoodsDetailDTO renterGoodsDetailDTO = renterCommodityService.getRenterGoodsDetail(renterOrderDeliveryEntity.getRenterOrderNo(), false);
//        CostBaseDTO costBaseDTO = new CostBaseDTO();
//        costBaseDTO.setStartTime(renterOrderDeliveryEntity.getRentTime());
//        costBaseDTO.setEndTime(renterOrderDeliveryEntity.getRevertTime());
//        costBaseDTO.setOrderNo(renterOrderDeliveryEntity.getOrderNo());
//        costBaseDTO.setRenterOrderNo(renterOrderDeliveryEntity.getRenterOrderNo());
//        GetReturnCarOverCostReqDto getReturnCarOverCostReqDto = new GetReturnCarOverCostReqDto();
//        GetReturnCarCostReqDto getReturnCarCostReqDto = new GetReturnCarCostReqDto();
//        getReturnCarCostReqDto.setCityCode(Integer.valueOf(renterOrderDeliveryEntity.getCityCode()));
//        getReturnCarCostReqDto.setIsPackageOrder(false);
//        getReturnCarCostReqDto.setCostBaseDTO(costBaseDTO);
//        renterOrderDeliveryEntityList.stream().forEach(renterOrderDelivery -> {
//            if(renterOrderDelivery.getType().intValue() == 1) {
//                getReturnCarCostReqDto.setSrvGetLat(renterOrderDelivery.getRenterGetReturnAddrLat());
//                getReturnCarCostReqDto.setSrvGetLon(renterOrderDelivery.getRenterGetReturnAddrLon());
//                getReturnCarCostReqDto.setIsGetCarCost(true);
//                getReturnCarOverCostReqDto.setIsGetCarCost(true);
//            }else {
//                getReturnCarCostReqDto.setSrvReturnLat(renterOrderDelivery.getRenterGetReturnAddrLat());
//                getReturnCarCostReqDto.setSrvReturnLon(renterOrderDelivery.getRenterGetReturnAddrLon());
//                getReturnCarOverCostReqDto.setIsReturnCarCost(true);
//                getReturnCarCostReqDto.setIsReturnCarCost(true);
//            }
//              });
//        getReturnCarCostReqDto.setCarRealLat(renterGoodsDetailDTO.getCarRealLat());
//        getReturnCarCostReqDto.setCarRealLon(renterGoodsDetailDTO.getCarRealLon());
//        getReturnCarCostReqDto.setCarShowLat(renterGoodsDetailDTO.getCarShowLat());
//        getReturnCarCostReqDto.setCarShowLon(renterGoodsDetailDTO.getCarShowLon());
//        GetReturnCostDTO getReturnCostDTO = getReturnCarCostProxyService.getReturnCarCost(getReturnCarCostReqDto);
//        if(Objects.nonNull(getReturnCostDTO)) {
//            distributionCostVO.setGetCarAmt(String.valueOf(getReturnCostDTO.getGetReturnResponseVO().getGetFee()));
//            distributionCostVO.setRenturnCarAmt(String.valueOf(getReturnCostDTO.getGetReturnResponseVO().getReturnFee()));
//        }
//        getReturnCarOverCostReqDto.setCityCode(Integer.valueOf(renterOrderDeliveryEntity.getCityCode()));
//        getReturnCarOverCostReqDto.setOrderType(1);
//        getReturnCarOverCostReqDto.setCostBaseDTO(costBaseDTO);
//        try {
//            GetReturnOverCostDTO getReturnOverCostDTO = tranSportProxyService.getGetReturnOverCost(getReturnCarOverCostReqDto);
//                int chaoYunNengAddCrash = getReturnOverCostDTO.getGetReturnOverTransportDTO().getGetOverTransportFee();
//                distributionCostVO.setGetCarChaoYunNeng(String.valueOf(chaoYunNengAddCrash));
//                int returnChaoYunNengAddCrash = getReturnOverCostDTO.getGetReturnOverTransportDTO().getReturnOverTransportFee();
//                distributionCostVO.setReturnCarChaoYunNeng(String.valueOf(returnChaoYunNengAddCrash));
//        } catch (Exception e) {
//            logger.error("获取超运能异常，给默认值,cause:{}", e.getMessage());
//            distributionCostVO.setReturnCarChaoYunNeng("0");
//            distributionCostVO.setGetCarChaoYunNeng("0");
//        }
//        return distributionCostVO;
//    }



}
