package com.atzuche.order.admin.service;

import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.delivery.enums.CarTypeEnum;
import com.atzuche.order.delivery.service.delivery.DeliveryCarInfoService;
import com.atzuche.order.delivery.service.handover.HandoverCarInfoService;
import com.atzuche.order.delivery.vo.delivery.rep.DeliveryCarVO;
import com.atzuche.order.delivery.vo.delivery.rep.OwnerGetAndReturnCarDTO;
import com.atzuche.order.delivery.vo.delivery.req.CarConditionPhotoUploadVO;
import com.atzuche.order.delivery.vo.delivery.req.DeliveryCarRepVO;
import com.atzuche.order.delivery.vo.delivery.req.DeliveryReqVO;
import com.atzuche.order.delivery.vo.handover.req.HandoverCarInfoReqVO;
import com.atzuche.order.rentercommodity.service.RenterCommodityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

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

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取配送相关信息
     * @param deliveryCarDTO
     * @return
     */
    public DeliveryCarVO findDeliveryListByOrderNo(DeliveryCarRepVO deliveryCarDTO) {
        logger.info("入参deliveryCarDTO：[{}]", deliveryCarDTO.toString());
        // 获取租客商品信息
        RenterGoodsDetailDTO renterGoodsDetailDTO = renterCommodityService.getRenterGoodsDetail(deliveryCarDTO.getRenterOrderNo(), false);
        OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO = OwnerGetAndReturnCarDTO.builder().build();
        String tenancy = String.valueOf(Duration.between(renterGoodsDetailDTO.getRentTime(),renterGoodsDetailDTO.getRevertTime()).toDays());
        ownerGetAndReturnCarDTO.setZuQi(tenancy);
        ownerGetAndReturnCarDTO.setRanLiao(String.valueOf(renterGoodsDetailDTO.getCarEngineType()));
        ownerGetAndReturnCarDTO.setDayKM(String.valueOf(renterGoodsDetailDTO.getCarDayMileage().toString()));
        ownerGetAndReturnCarDTO.setOilContainer(String.valueOf(renterGoodsDetailDTO.getCarOilVolume()));
        boolean isEscrowCar = CarTypeEnum.isCarType(renterGoodsDetailDTO.getCarType());
        int carType = renterGoodsDetailDTO.getCarType();
        return deliveryCarInfoService.findDeliveryListByOrderNo(deliveryCarDTO,ownerGetAndReturnCarDTO,isEscrowCar,renterGoodsDetailDTO.getCarEngineType(),carType);
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
     *
     * @param handoverCarReqVO
     * @throws Exception
     */
    public void updateHandoverCarInfo(HandoverCarInfoReqVO handoverCarReqVO) throws Exception {
        logger.info("入参handoverCarReqVO：[{}]", handoverCarReqVO.toString());
        handoverCarInfoService.updateHandoverCarInfo(handoverCarReqVO);
    }

    /**
     * 更新取还车信息 更新仁云接口
     *
     * @param deliveryReqVO
     * @throws Exception
     */
    public void updateDeliveryCarInfo(DeliveryReqVO deliveryReqVO) throws Exception {
        logger.info("入参deliveryReqVO：[{}]", deliveryReqVO.toString());
        handoverCarInfoService.updateDeliveryCarInfo(deliveryReqVO);
    }
}
