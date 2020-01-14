package com.atzuche.order.admin.service;

import com.atzuche.order.admin.vo.resp.cost.DistributionCostVO;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.GetReturnCarOverCostReqDto;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.enums.CarTypeEnum;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarInfoService;
import com.atzuche.order.delivery.service.handover.HandoverCarInfoService;
import com.atzuche.order.delivery.vo.delivery.rep.*;
import com.atzuche.order.delivery.vo.delivery.req.CarConditionPhotoUploadVO;
import com.atzuche.order.delivery.vo.delivery.req.DeliveryCarRepVO;
import com.atzuche.order.delivery.vo.delivery.req.DeliveryReqDTO;
import com.atzuche.order.delivery.vo.delivery.req.DeliveryReqVO;
import com.atzuche.order.delivery.vo.handover.req.HandoverCarInfoReqDTO;
import com.atzuche.order.delivery.vo.handover.req.HandoverCarInfoReqVO;
import com.atzuche.order.rentercommodity.service.RenterCommodityService;
import com.atzuche.order.transport.service.TranSportProxyService;
import com.atzuche.order.transport.vo.GetReturnOverCostDTO;
import com.autoyol.platformcost.OwnerFeeCalculatorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    RenterCommodityService renterCommodityService;
    @Autowired
    RenterOrderDeliveryService renterOrderDeliveryService;
    @Autowired
    TranSportProxyService tranSportProxyService;

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
        RenterGoodsDetailDTO renterGoodsDetailDTO = renterCommodityService.getRenterGoodsDetail(renterOrderDeliveryEntity.getRenterOrderNo(), false);
        OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO = OwnerGetAndReturnCarDTO.builder().build();
        ownerGetAndReturnCarDTO.setRanLiao(String.valueOf(renterGoodsDetailDTO.getCarEngineType()));
        ownerGetAndReturnCarDTO.setDayKM(String.valueOf(renterGoodsDetailDTO.getCarDayMileage().toString()));
        ownerGetAndReturnCarDTO.setOilContainer(String.valueOf(renterGoodsDetailDTO.getCarOilVolume()));
        boolean isEscrowCar = CarTypeEnum.isCarType(renterGoodsDetailDTO.getCarType());
        int carType = renterGoodsDetailDTO.getCarType();
        return deliveryCarInfoService.findDeliveryListByOrderNo(renterOrderDeliveryEntity.getRenterOrderNo(),deliveryCarDTO,ownerGetAndReturnCarDTO,isEscrowCar,renterGoodsDetailDTO.getCarEngineType(),carType);
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
        handoverCarInfoService.updateDeliveryCarInfo( createParams(deliveryCarVO));
    }


    public DeliveryReqVO createParams(DeliveryCarVO deliveryCarVO) {
        //取车服务信息
        DeliveryReqVO deliveryReqVO = new DeliveryReqVO();
        GetHandoverCarDTO getHandoverCarDTO = deliveryCarVO.getGetHandoverCarDTO();
        ReturnHandoverCarDTO returnHandoverCarDTO = deliveryCarVO.getReturnHandoverCarDTO();
        if (Objects.nonNull(getHandoverCarDTO)) {
            DeliveryReqDTO deliveryReqDTO = new DeliveryReqDTO();
            deliveryReqDTO.setIsUsedGetAndReturnCar("1");
            deliveryReqDTO.setOrderNo(deliveryCarVO.getOrderNo());
            deliveryReqDTO.setOwnerRealGetAddrReamrk(getHandoverCarDTO.getOwnRealGetRemark());
            deliveryReqDTO.setRenterRealGetAddrReamrk(getHandoverCarDTO.getRenterRealGetAddrReamrk());
            deliveryReqDTO.setRenterGetReturnAddr(getHandoverCarDTO.getRenterRealGetAddr());
            deliveryReqDTO.setOwnerGetReturnAddr(getHandoverCarDTO.getOwnRealReturnAddr() );
            deliveryReqVO.setGetDeliveryReqDTO(deliveryReqDTO);
        }
        if (Objects.nonNull(returnHandoverCarDTO)) {
            DeliveryReqDTO renterDeliveryReqDTO = new DeliveryReqDTO();
            renterDeliveryReqDTO.setIsUsedGetAndReturnCar("1");
            renterDeliveryReqDTO.setOrderNo(deliveryCarVO.getOrderNo());
            renterDeliveryReqDTO.setRenterRealGetAddrReamrk(returnHandoverCarDTO.getRenterRealGetRemark());
            renterDeliveryReqDTO.setOwnerRealGetAddrReamrk(returnHandoverCarDTO.getOwnerRealGetAddrReamrk());
            renterDeliveryReqDTO.setRenterGetReturnAddr(returnHandoverCarDTO.getRenterRealReturnAddr());
            renterDeliveryReqDTO.setOwnerGetReturnAddr(returnHandoverCarDTO.getOwnerRealGetAddr());
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
        // 获取租客商品信息
        RenterOrderDeliveryEntity renterOrderDeliveryEntity = renterOrderDeliveryService.findRenterOrderByrOrderNo(deliveryCarDTO.getOrderNo(), 1);
        RenterGoodsDetailDTO renterGoodsDetailDTO = renterCommodityService.getRenterGoodsDetail(renterOrderDeliveryEntity.getRenterOrderNo(), false);
        //取车费用
        distributionCostVO.setGetCarAmt(String.valueOf(OwnerFeeCalculatorUtils.calOwnerSrvGetAmt(renterGoodsDetailDTO.getCarType(), 1)));
        //还车费用
        distributionCostVO.setRenturnCarAmt(String.valueOf(OwnerFeeCalculatorUtils.calOwnerSrvReturnAmt(renterGoodsDetailDTO.getCarType(), 2)));
        //获取运能数据
        //获取超运能
        GetReturnCarOverCostReqDto getReturnCarOverCostReqDto = new GetReturnCarOverCostReqDto();
        getReturnCarOverCostReqDto.setCityCode(Integer.valueOf(renterOrderDeliveryEntity.getCityCode()));
        getReturnCarOverCostReqDto.setOrderType(1);
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        costBaseDTO.setStartTime(renterOrderDeliveryEntity.getRentTime());
        costBaseDTO.setEndTime(renterOrderDeliveryEntity.getRevertTime());
        costBaseDTO.setOrderNo(renterOrderDeliveryEntity.getOrderNo());
        costBaseDTO.setRenterOrderNo(renterOrderDeliveryEntity.getRenterOrderNo());
        getReturnCarOverCostReqDto.setCostBaseDTO(costBaseDTO);
        try {
            GetReturnOverCostDTO getReturnOverCostDTO = tranSportProxyService.getGetReturnOverCost(getReturnCarOverCostReqDto);
            boolean isGetOverTransport = getReturnOverCostDTO.getGetReturnOverTransportDTO().getIsGetOverTransport();
            boolean isReturnOverTransport = getReturnOverCostDTO.getGetReturnOverTransportDTO().getIsReturnOverTransport();
            if (isGetOverTransport) {
                int chaoYunNengAddCrash = getReturnOverCostDTO.getGetReturnOverTransportDTO().getGetOverTransportFee() + getReturnOverCostDTO.getGetReturnOverTransportDTO().getNightGetOverTransportFee();
                distributionCostVO.setGetCarChaoYunNeng(String.valueOf(chaoYunNengAddCrash));
            }
            if (isReturnOverTransport) {
                int chaoYunNengAddCrash = getReturnOverCostDTO.getGetReturnOverTransportDTO().getNightReturnOverTransportFee() + getReturnOverCostDTO.getGetReturnOverTransportDTO().getNightReturnOverTransportFee();
                distributionCostVO.setReturnCarChaoYunNeng(String.valueOf(chaoYunNengAddCrash));
            }
        } catch (Exception e) {
            logger.error("获取超运能异常，给默认值,cause:{}", e.getMessage());
            distributionCostVO.setReturnCarChaoYunNeng("0");
            distributionCostVO.setGetCarChaoYunNeng("0");
        }
        return distributionCostVO;
    }

}
