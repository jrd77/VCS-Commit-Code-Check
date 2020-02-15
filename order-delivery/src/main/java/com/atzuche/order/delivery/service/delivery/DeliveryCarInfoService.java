package com.atzuche.order.delivery.service.delivery;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.atzuche.order.transport.service.GetReturnCarCostService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.GetReturnCarCostReqDto;
import com.atzuche.order.commons.entity.dto.GetReturnCarOverCostReqDto;
import com.atzuche.order.commons.entity.dto.MileageAmtDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.delivery.common.delivery.TranSportService;
import com.atzuche.order.delivery.entity.OwnerHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.handover.HandoverCarService;
import com.atzuche.order.delivery.service.handover.OwnerHandoverCarService;
import com.atzuche.order.delivery.service.handover.RenterHandoverCarService;
import com.atzuche.order.delivery.utils.CommonUtil;
import com.atzuche.order.delivery.utils.DateUtils;
import com.atzuche.order.delivery.vo.delivery.rep.DeliveryCarVO;
import com.atzuche.order.delivery.vo.delivery.rep.GetHandoverCarDTO;
import com.atzuche.order.delivery.vo.delivery.rep.OwnerGetAndReturnCarDTO;
import com.atzuche.order.delivery.vo.delivery.rep.RenterGetAndReturnCarDTO;
import com.atzuche.order.delivery.vo.delivery.rep.ReturnHandoverCarDTO;
import com.atzuche.order.delivery.vo.delivery.req.DeliveryCarRepVO;
import com.atzuche.order.rentercommodity.service.RenterCommodityService;
import com.atzuche.order.transport.service.GetReturnCarCostProxyService;
import com.atzuche.order.transport.service.TranSportProxyService;
import com.atzuche.order.transport.vo.GetReturnCostDTO;
import com.atzuche.order.transport.vo.GetReturnOverCostDTO;
import com.autoyol.platformcost.OwnerFeeCalculatorUtils;
import com.autoyol.platformcost.RenterFeeCalculatorUtils;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 胡春林
 * 配送信息服务
 */
@Service
@Slf4j
public class DeliveryCarInfoService {

    @Autowired
    DeliveryCarService deliveryCarService;
    @Autowired
    RenterOrderDeliveryService renterOrderDeliveryService;
    @Autowired
    HandoverCarService handoverCarService;
    @Autowired
    DeliveryCarInfoPriceService deliveryCarInfoPriceService;
    @Autowired
    RenterHandoverCarService renterHandoverCarService;
    @Autowired
    OwnerHandoverCarService ownerHandoverCarService;
    @Autowired
    TranSportService tranSportService;
    @Autowired
    TranSportProxyService tranSportProxyService;
    @Autowired
    GetReturnCarCostProxyService getReturnCarCostProxyService;
    @Autowired
    RenterCommodityService renterCommodityService;
    @Autowired
    GetReturnCarCostService getReturnCarCostService;

    /**
     * 获取配送相关信息（待优化）
     * @param deliveryCarDTO
     * @return
     */
    public DeliveryCarVO findDeliveryListByOrderNo(String renterOrderNo,DeliveryCarRepVO deliveryCarDTO, OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO, Boolean isEscrowCar,Integer carEngineType,int carType,RenterGoodsDetailDTO renterGoodsDetailDTO) {
        List<RenterHandoverCarInfoEntity> renterHandoverCarInfoEntities = renterHandoverCarService.selectRenterByOrderNo(deliveryCarDTO.getOrderNo());
        List<OwnerHandoverCarInfoEntity> ownerHandoverCarInfoEntities = ownerHandoverCarService.selectOwnerByOrderNo(deliveryCarDTO.getOrderNo());
        List<RenterOrderDeliveryEntity> renterOrderDeliveryEntityList = renterOrderDeliveryService.selectByRenterOrderNo(renterOrderNo);
        DeliveryCarVO deliveryCarVO = createDeliveryCarVOParams(ownerGetAndReturnCarDTO, renterHandoverCarInfoEntities, ownerHandoverCarInfoEntities, renterOrderDeliveryEntityList, isEscrowCar, carEngineType, carType, renterGoodsDetailDTO);

        return deliveryCarVO;
    }


    /**
     * 构造结构体(待优化)
     * @param renterHandoverCarInfoEntities
     * @param ownerHandoverCarInfoEntities
     * @return
     */
    public DeliveryCarVO createDeliveryCarVOParams(OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO,
                                                   List<RenterHandoverCarInfoEntity> renterHandoverCarInfoEntities,
                                                   List<OwnerHandoverCarInfoEntity> ownerHandoverCarInfoEntities,
                                                   List<RenterOrderDeliveryEntity> renterOrderDeliveryEntityList,
                                                   Boolean isEscrowCar,
                                                   Integer carEngineType,
                                                   Integer carType,
                                                   RenterGoodsDetailDTO renterGoodsDetailDTO
                                                   ) {
        DeliveryCarVO deliveryCarVO = new DeliveryCarVO();

        GetReturnCarCostReqDto getReturnCarCostReqDto = new GetReturnCarCostReqDto();
        for (RenterOrderDeliveryEntity renterOrderDeliveryEntity : renterOrderDeliveryEntityList) {
            if (null == renterOrderDeliveryEntity) {
                continue;
            }
            if(renterOrderDeliveryEntity.getType().intValue() == 1) {
                getReturnCarCostReqDto.setIsGetCarCost(true);
                deliveryCarVO.setIsGetCar(renterOrderDeliveryEntity.getIsNotifyRenyun());
                ownerGetAndReturnCarDTO.setRealGetTime(DateUtils.formate(renterOrderDeliveryEntity.getRentTime().minusMinutes(renterOrderDeliveryEntity.getAheadOrDelayTime() == null ? 0 : renterOrderDeliveryEntity.getAheadOrDelayTime()),DateUtils.DATE_DEFAUTE_4));
            } else { //还车
                getReturnCarCostReqDto.setIsReturnCarCost(true);
                deliveryCarVO.setIsReturnCar(renterOrderDeliveryEntity.getIsNotifyRenyun());
                ownerGetAndReturnCarDTO.setRealReturnTime(DateUtils.formate(renterOrderDeliveryEntity.getRevertTime().plusMinutes(renterOrderDeliveryEntity.getAheadOrDelayTime() == null ? 0 : renterOrderDeliveryEntity.getAheadOrDelayTime()),DateUtils.DATE_DEFAUTE_4));
            }
            getReturnCarCostReqDto.setCityCode(Integer.valueOf(renterOrderDeliveryEntity.getCityCode()));
            getReturnCarCostReqDto.setIsPackageOrder(false);
            createGetHandoverCar(getReturnCarCostReqDto,deliveryCarVO, renterOrderDeliveryEntity,carType,renterGoodsDetailDTO);
        }
        GetReturnCostDTO getReturnCostDTO = getReturnCarCostProxyService.getReturnCarCost(getReturnCarCostReqDto);
        //GetReturnResponseVO getReturnResponseVO = deliveryCarInfoPriceService.getDeliveryCarFee(renterOrderDeliveryEntityList.get(0).getOrderNo());
        //log.info(String.valueOf(getReturnResponseVO.getGetFee()));

        if(Objects.nonNull(getReturnCostDTO)) {
            deliveryCarVO.getGetHandoverCarDTO().setGetCarCrash(String.valueOf(getReturnCostDTO.getGetReturnResponseVO().getGetFee()));
            deliveryCarVO.getReturnHandoverCarDTO().setReturnCarCrash(String.valueOf(getReturnCostDTO.getGetReturnResponseVO().getReturnFee()));
        }
        //取车时的所在城市
        RenterOrderDeliveryEntity renterOrderDelivery = renterOrderDeliveryEntityList.stream().filter(r->r.getType() == 1).findFirst().get();
        String cityCode = renterOrderDelivery.getCityCode();
        String tenancy = String.valueOf(Duration.between(renterOrderDelivery.getRentTime(),renterOrderDelivery.getRevertTime()).toDays());
        ownerGetAndReturnCarDTO.setZuQi(tenancy);
        deliveryCarVO = createDeliveryCarInfo(ownerGetAndReturnCarDTO, deliveryCarVO, ownerHandoverCarInfoEntities, renterHandoverCarInfoEntities, isEscrowCar,carEngineType,cityCode,renterGoodsDetailDTO);
        return deliveryCarVO;
    }

    /**
     * 组装取送车信息
     *
     * @param renterOrderDeliveryEntity
     * @return
     */
    public void createGetHandoverCar(GetReturnCarCostReqDto getReturnCarCostReqDto,DeliveryCarVO deliveryCarVO,
                                     RenterOrderDeliveryEntity renterOrderDeliveryEntity,
                                     Integer carType,
                                     RenterGoodsDetailDTO renterGoodsDetailDTO) {

        //获取超运能
        GetReturnCarOverCostReqDto getReturnCarOverCostReqDto = new GetReturnCarOverCostReqDto();
        getReturnCarOverCostReqDto.setCityCode(Integer.valueOf(renterOrderDeliveryEntity.getCityCode()));
        getReturnCarOverCostReqDto.setOrderType(1);
        getReturnCarOverCostReqDto.setIsGetCarCost(true);
        getReturnCarOverCostReqDto.setIsReturnCarCost(true);
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        costBaseDTO.setStartTime(renterOrderDeliveryEntity.getRentTime());
        costBaseDTO.setEndTime(renterOrderDeliveryEntity.getRevertTime());
        costBaseDTO.setOrderNo(renterOrderDeliveryEntity.getOrderNo());
        costBaseDTO.setRenterOrderNo(renterOrderDeliveryEntity.getRenterOrderNo());
        getReturnCarOverCostReqDto.setCostBaseDTO(costBaseDTO);
        getReturnCarCostReqDto.setCostBaseDTO(costBaseDTO);
        String isGetOverTransport;
        String isReturnOverTransport;
        String chaoYunNengAddCrashStr = "";
        String returnChaoYunNengAddCrashStr = "";
        try {
            GetReturnOverCostDTO getReturnOverCostDTO = tranSportProxyService.getGetReturnOverCost(getReturnCarOverCostReqDto);
            isGetOverTransport = getReturnOverCostDTO.getGetReturnOverTransportDTO().getIsGetOverTransport() == true ? "1" : "0";
            isReturnOverTransport = getReturnOverCostDTO.getGetReturnOverTransportDTO().getIsReturnOverTransport() == true ? "1" : "0";
            int chaoYunNengAddCrash = getReturnOverCostDTO.getGetReturnOverTransportDTO().getGetOverTransportFee();
            int returnChaoYunNengAddCrash =  getReturnOverCostDTO.getGetReturnOverTransportDTO().getReturnOverTransportFee();
            returnChaoYunNengAddCrashStr = String.valueOf(returnChaoYunNengAddCrash);
            chaoYunNengAddCrashStr = String.valueOf(chaoYunNengAddCrash);
        } catch (Exception e) {
            log.error("获取超运能异常，给默认值,cause:{}", e.getMessage());
            isGetOverTransport = "0";
            isReturnOverTransport = "0";
        }
        if (renterOrderDeliveryEntity.getType() == 1 && renterOrderDeliveryEntity.getStatus() != 0) {
            GetHandoverCarDTO getHandoverCarDTO = new GetHandoverCarDTO();
            getHandoverCarDTO = getHandoverCarInfo(getHandoverCarDTO, renterOrderDeliveryEntity, carType);
            try {
                getHandoverCarDTO.setRenterRealGetAddrReamrk(renterOrderDeliveryEntity.getRenterRealGetReturnRemark());
                getHandoverCarDTO.setOwnRealGetRemark(renterOrderDeliveryEntity.getOwnerRealGetReturnRemark());
            } catch (Exception e) {
                log.error("备注获取失败");
            }
            getReturnCarCostReqDto.setSrvGetLat(renterOrderDeliveryEntity.getRenterGetReturnAddrLat());
            getReturnCarCostReqDto.setSrvGetLon(renterOrderDeliveryEntity.getRenterGetReturnAddrLon());
            getHandoverCarDTO.setIsChaoYunNeng(isGetOverTransport);
            getHandoverCarDTO.setChaoYunNengAddCrash(chaoYunNengAddCrashStr);
            getHandoverCarDTO.setOwnDefaultGetCarAddr(renterGoodsDetailDTO.getCarRealAddr());
            getHandoverCarDTO.setOwnDefaultGetCarLat(renterGoodsDetailDTO.getCarRealLat());
            getHandoverCarDTO.setOwnRealReturnLng(renterGoodsDetailDTO.getCarRealLon());
            deliveryCarVO.setGetHandoverCarDTO(getHandoverCarDTO);
        } else if (renterOrderDeliveryEntity.getType() == 2 && renterOrderDeliveryEntity.getStatus() != 0) {
            ReturnHandoverCarDTO returnHandoverCarDTO = new ReturnHandoverCarDTO();
            returnHandoverCarDTO = returnHandoverCarInfo(returnHandoverCarDTO, renterOrderDeliveryEntity, carType);
            try {
                returnHandoverCarDTO.setRenterRealGetRemark(renterOrderDeliveryEntity.getRenterRealGetReturnRemark());
                returnHandoverCarDTO.setOwnerRealGetAddrReamrk(renterOrderDeliveryEntity.getOwnerRealGetReturnRemark());
            } catch (Exception e) {
                log.error("备注获取失败");
            }
            getReturnCarCostReqDto.setSrvReturnLat(renterOrderDeliveryEntity.getRenterGetReturnAddrLat());
            getReturnCarCostReqDto.setSrvReturnLon(renterOrderDeliveryEntity.getRenterGetReturnAddrLon());
            returnHandoverCarDTO.setIsChaoYunNeng(isReturnOverTransport);
            returnHandoverCarDTO.setChaoYunNengAddCrash(returnChaoYunNengAddCrashStr);
            returnHandoverCarDTO.setOwnDefaultReturnCarAddr(renterGoodsDetailDTO.getCarRealAddr());
            returnHandoverCarDTO.setOwnDefaultReturnCarLat(renterGoodsDetailDTO.getCarRealLat());
            returnHandoverCarDTO.setOwnDefaultReturnCarLng(renterGoodsDetailDTO.getCarRealLon());
            deliveryCarVO.setReturnHandoverCarDTO(returnHandoverCarDTO);
        }
    }

    /**
     * 构造最终数据
     * @param deliveryCarVO
     * @param ownerHandoverCarInfoEntities
     * @param renterHandoverCarInfoEntities
     * @return
     */
    public DeliveryCarVO createDeliveryCarInfo(OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO, DeliveryCarVO deliveryCarVO, List<OwnerHandoverCarInfoEntity> ownerHandoverCarInfoEntities, List<RenterHandoverCarInfoEntity> renterHandoverCarInfoEntities,
                                               Boolean isEscrowCar,Integer carEngineType,String cityCode,RenterGoodsDetailDTO renterGoodsDetailDTO) {
        RenterGetAndReturnCarDTO renterGetAndReturnCarDTO = RenterGetAndReturnCarDTO.builder().build();
        //车主取送信息
        ownerGetAndReturnCarDTO = deliveryCarInfoPriceService.createOwnerGetAndReturnCarDTO(ownerGetAndReturnCarDTO, ownerHandoverCarInfoEntities,carEngineType,cityCode);
        int overMileageAmt = getDeliveryCarOverMileageAmt(ownerGetAndReturnCarDTO, renterGoodsDetailDTO);
        ownerGetAndReturnCarDTO.setOverKNCrash(String.valueOf(overMileageAmt));
        OwnerGetAndReturnCarDTO ownerGetAndReturnCarDO = OwnerGetAndReturnCarDTO.builder().build();
        BeanUtils.copyProperties(ownerGetAndReturnCarDTO,ownerGetAndReturnCarDO);
        //租客取送信息
        List<OwnerHandoverCarInfoEntity> ownerHandoverCarInfoList = Lists.newArrayList();
        for(RenterHandoverCarInfoEntity renterGetAndReturnCar : renterHandoverCarInfoEntities )
        {
            OwnerHandoverCarInfoEntity ownerGetAndReturnCar = new OwnerHandoverCarInfoEntity();
            BeanUtils.copyProperties(renterGetAndReturnCar,ownerGetAndReturnCar);
            ownerHandoverCarInfoList.add(ownerGetAndReturnCar);
        }
        ownerHandoverCarInfoEntities = CommonUtil.copyList(ownerHandoverCarInfoList);
        OwnerGetAndReturnCarDTO getAndReturnCarDTO = deliveryCarInfoPriceService.createOwnerGetAndReturnCarDTO(ownerGetAndReturnCarDO,ownerHandoverCarInfoEntities,carEngineType,cityCode);
        int renterOverMileageAmt = getDeliveryCarOverMileageAmt(getAndReturnCarDTO, renterGoodsDetailDTO);
        getAndReturnCarDTO.setOverKNCrash(String.valueOf(renterOverMileageAmt));
        BeanUtils.copyProperties(getAndReturnCarDTO, renterGetAndReturnCarDTO);
        //车主平台加油服务费carOwnerOilCrash
        ownerGetAndReturnCarDTO.setPlatFormOilServiceCharge(deliveryCarInfoPriceService.getOwnerPlatFormOilServiceCharge(Integer.valueOf(ownerGetAndReturnCarDTO.getReturnCarOil()),Integer.valueOf(renterGetAndReturnCarDTO.getGetCarOil()))+"元");
        renterGetAndReturnCarDTO.setCarOwnerOilCrash(renterGetAndReturnCarDTO.getOilDifferenceCrash());
        ownerGetAndReturnCarDTO.setCarOwnerAllOilCrash(ownerGetAndReturnCarDTO.getOilDifferenceCrash()+ownerGetAndReturnCarDTO.getPlatFormOilServiceCharge());
        if (isEscrowCar) {
            ownerGetAndReturnCarDTO.setCarOilDifferenceCrash(ownerGetAndReturnCarDTO.getOilDifferenceCrash());
            ownerGetAndReturnCarDTO.setCarOwnerOilCrash(ownerGetAndReturnCarDTO.getCarOwnerAllOilCrash());
        }
        deliveryCarVO.setOwnerGetAndReturnCarDTO(ownerGetAndReturnCarDTO);
        deliveryCarVO.setRenterGetAndReturnCarDTO(renterGetAndReturnCarDTO);
        return deliveryCarVO;
    }


    /**
     * 获取取车相关数据
     * @return
     */
    public GetHandoverCarDTO getHandoverCarInfo(GetHandoverCarDTO getHandoverCarDTO, RenterOrderDeliveryEntity renterOrderDeliveryEntity, Integer carType) {
        getHandoverCarDTO.setChaoYunNengAddCrash("0");
        getHandoverCarDTO.setGetCarCrash(String.valueOf(OwnerFeeCalculatorUtils.calOwnerSrvGetAmt(carType, renterOrderDeliveryEntity.getType())));
        getHandoverCarDTO.setGetCarKM(String.valueOf(Math.round(deliveryCarInfoPriceService.getDistanceKM(renterOrderDeliveryEntity))));
        getHandoverCarDTO.setOwnerGetCarCrash(String.valueOf(OwnerFeeCalculatorUtils.calOwnerSrvGetAmt(carType, renterOrderDeliveryEntity.getType())));
        getHandoverCarDTO.setRenterRealGetAddr(renterOrderDeliveryEntity.getRenterGetReturnAddr());
        getHandoverCarDTO.setRenterRealGetLat(renterOrderDeliveryEntity.getRenterGetReturnAddrLat());
        getHandoverCarDTO.setRenterRealGetLng(renterOrderDeliveryEntity.getRenterGetReturnAddrLon());
        //车辆地址
        getHandoverCarDTO.setOwnDefaultGetCarAddr(renterOrderDeliveryEntity.getOwnerGetReturnAddr());
        getHandoverCarDTO.setOwnDefaultGetCarLat(renterOrderDeliveryEntity.getOwnerGetReturnAddrLat());
        getHandoverCarDTO.setOwnDefaultGetCarLng(renterOrderDeliveryEntity.getOwnerGetReturnAddrLon());
        getHandoverCarDTO.setOwnRealReturnAddr(renterOrderDeliveryEntity.getOwnerGetReturnAddr());
        getHandoverCarDTO.setOwnRealReturnLat(renterOrderDeliveryEntity.getOwnerGetReturnAddrLat());
        getHandoverCarDTO.setOwnRealReturnLng(renterOrderDeliveryEntity.getOwnerGetReturnAddrLon());
        LocalDateTime rentTime = renterOrderDeliveryEntity.getRentTime();
        rentTime = rentTime.minusMinutes(renterOrderDeliveryEntity.getAheadOrDelayTime());
        getHandoverCarDTO.setRentTime(DateUtils.formate(rentTime, DateUtils.DATE_DEFAUTE_4) + "," + renterOrderDeliveryEntity.getAheadOrDelayTime());
        return getHandoverCarDTO;
    }

    /**
     * 获取还车相关数据
     * @return
     */
    public ReturnHandoverCarDTO returnHandoverCarInfo(ReturnHandoverCarDTO returnHandoverCarDTO, RenterOrderDeliveryEntity renterOrderDeliveryEntity, Integer carType) {
        returnHandoverCarDTO.setChaoYunNengAddCrash("0");
        returnHandoverCarDTO.setReturnCarCrash(String.valueOf(OwnerFeeCalculatorUtils.calOwnerSrvReturnAmt(carType, renterOrderDeliveryEntity.getType())));
        returnHandoverCarDTO.setReturnCarKM(String.valueOf(Math.round(deliveryCarInfoPriceService.getDistanceKM(renterOrderDeliveryEntity))));
        returnHandoverCarDTO.setOwnerReturnCarCrash(String.valueOf(OwnerFeeCalculatorUtils.calOwnerSrvReturnAmt(carType, renterOrderDeliveryEntity.getType())));
        returnHandoverCarDTO.setRenterRealReturnAddr(renterOrderDeliveryEntity.getRenterGetReturnAddr());
        returnHandoverCarDTO.setRenterRealReturnLat(renterOrderDeliveryEntity.getRenterGetReturnAddrLat());
        returnHandoverCarDTO.setRenterRealReturnLng(renterOrderDeliveryEntity.getRenterGetReturnAddrLon());
        returnHandoverCarDTO.setOwnDefaultReturnCarAddr(renterOrderDeliveryEntity.getOwnerGetReturnAddr());
        returnHandoverCarDTO.setOwnerRealGetAddr(renterOrderDeliveryEntity.getOwnerGetReturnAddr());
        returnHandoverCarDTO.setOwnerRealGetLat(renterOrderDeliveryEntity.getOwnerGetReturnAddrLat());
        returnHandoverCarDTO.setOwnerRealGetLng(renterOrderDeliveryEntity.getOwnerGetReturnAddrLon());
        LocalDateTime revertTime = renterOrderDeliveryEntity.getRevertTime();
        revertTime = revertTime.plusMinutes(renterOrderDeliveryEntity.getAheadOrDelayTime());
        returnHandoverCarDTO.setRentTime(DateUtils.formate(revertTime, DateUtils.DATE_DEFAUTE_4) + "," + renterOrderDeliveryEntity.getAheadOrDelayTime());
        return  returnHandoverCarDTO;
    }


    /**
     * 获取超历程数据
     */
    public Integer getDeliveryCarOverMileageAmt(OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO,RenterGoodsDetailDTO renterGoodsDetailDTO) {
        try {
            MileageAmtDTO mileageAmtDTO = new MileageAmtDTO();
            mileageAmtDTO.setCarOwnerType(renterGoodsDetailDTO.getCarType());
            mileageAmtDTO.setDayMileage(renterGoodsDetailDTO.getCarDayMileage());
            mileageAmtDTO.setGetmileage(Integer.valueOf(ownerGetAndReturnCarDTO.getGetKM()));
            mileageAmtDTO.setReturnMileage(Integer.valueOf(ownerGetAndReturnCarDTO.getReturnKM()));
            mileageAmtDTO.setGuideDayPrice(renterGoodsDetailDTO.getCarGuideDayPrice());
            CostBaseDTO costBaseDTO = new CostBaseDTO();
            costBaseDTO.setStartTime(DateUtils.parseLocalDateTime(ownerGetAndReturnCarDTO.getRealGetTime(),DateUtils.DATE_DEFAUTE_4));
            costBaseDTO.setEndTime(DateUtils.parseLocalDateTime(ownerGetAndReturnCarDTO.getRealReturnTime(),DateUtils.DATE_DEFAUTE_4));
            mileageAmtDTO.setCostBaseDTO(costBaseDTO);
            return deliveryCarInfoPriceService.getMileageAmtEntity(mileageAmtDTO).getTotalFee();
        } catch (Exception e) {
            log.error("获取超历程失败原因：{}", e.getMessage());
            return 0;
        }

    }



}
