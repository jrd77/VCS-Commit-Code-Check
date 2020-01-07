package com.atzuche.order.delivery.service.delivery;

import com.atzuche.order.delivery.entity.*;
import com.atzuche.order.delivery.enums.HandoverCarTypeEnum;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.handover.HandoverCarService;
import com.atzuche.order.delivery.utils.DateUtils;
import com.atzuche.order.delivery.vo.delivery.rep.*;
import com.atzuche.order.delivery.vo.delivery.req.DeliveryCarRepVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 胡春林
 * 配送信息服务
 */
@Service
public class DeliveryCarInfoService {

    //FIXME: remove -----

    @Autowired
    DeliveryCarService deliveryCarService;
    @Autowired
    RenterOrderDeliveryService renterOrderDeliveryService;
    @Autowired
    HandoverCarService handoverCarService;

    /**
     * 获取配送相关信息
     * @param deliveryCarDTO
     * @return
     */
    public DeliveryCarVO findDeliveryListByOrderNo(DeliveryCarRepVO deliveryCarDTO) {
        List<RenterHandoverCarInfoEntity> renterHandoverCarInfoEntities = handoverCarService.selectRenterByOrderNo(deliveryCarDTO.getOrderNo());
        List<OwnerHandoverCarInfoEntity>  ownerHandoverCarInfoEntities = handoverCarService.selectOwnerByOrderNo(deliveryCarDTO.getOrderNo());
        List<RenterHandoverCarRemarkEntity> renterHandoverCarRemarkEntities = handoverCarService.getRenterHandoverRemarkInfo(deliveryCarDTO.getOrderNo());
        List<OwnerHandoverCarRemarkEntity> ownerHandoverCarRemarkEntities = handoverCarService.getOwnerHandoverRemarkInfo(deliveryCarDTO.getOrderNo());
        List<RenterOrderDeliveryEntity> renterOrderDeliveryEntityList = renterOrderDeliveryService.listRenterOrderDeliveryByRenterOrderNo(deliveryCarDTO.getRenterOrderNo());
        DeliveryCarVO deliveryCarVO = createDeliveryCarVOParams(renterHandoverCarInfoEntities,ownerHandoverCarInfoEntities,renterHandoverCarRemarkEntities,ownerHandoverCarRemarkEntities,renterOrderDeliveryEntityList);
        return deliveryCarVO;
    }


    /**
     * 构造结构体
     * @param renterHandoverCarInfoEntities
     * @param ownerHandoverCarInfoEntities
     * @param renterHandoverCarRemarkEntities
     * @param ownerHandoverCarRemarkEntities
     * @return
     */
    public DeliveryCarVO createDeliveryCarVOParams(List<RenterHandoverCarInfoEntity> renterHandoverCarInfoEntities,
                                                   List<OwnerHandoverCarInfoEntity> ownerHandoverCarInfoEntities,
                                                   List<RenterHandoverCarRemarkEntity> renterHandoverCarRemarkEntities,
                                                   List<OwnerHandoverCarRemarkEntity> ownerHandoverCarRemarkEntities,
                                                   List<RenterOrderDeliveryEntity> renterOrderDeliveryEntityList) {
        DeliveryCarVO deliveryCarVO = new DeliveryCarVO();
        deliveryCarVO.setIsReturnCar("0");
        deliveryCarVO.setIsGetCar("0");
        for (RenterOrderDeliveryEntity renterOrderDeliveryEntity : renterOrderDeliveryEntityList) {
            if (null == renterOrderDeliveryEntity) {
                continue;
            }
            createGetHandoverCar(deliveryCarVO, renterHandoverCarRemarkEntities, ownerHandoverCarRemarkEntities, renterHandoverCarInfoEntities, renterOrderDeliveryEntity);
        }

        deliveryCarVO = createDeliveryCarInfo(deliveryCarVO,ownerHandoverCarInfoEntities,renterHandoverCarInfoEntities);
        return deliveryCarVO;
    }

    /**
     * 组装取送车信息
     * @param renterHandoverCarRemarkEntities
     * @param ownerHandoverCarRemarkEntities
     * @param renterOrderDeliveryEntity
     * @return
     */
    public void createGetHandoverCar(DeliveryCarVO deliveryCarVO,
                                     List<RenterHandoverCarRemarkEntity> renterHandoverCarRemarkEntities,
                                     List<OwnerHandoverCarRemarkEntity> ownerHandoverCarRemarkEntities,
                                     List<RenterHandoverCarInfoEntity> renterHandoverCarInfoEntities,
                                     RenterOrderDeliveryEntity renterOrderDeliveryEntity) {
        if (renterOrderDeliveryEntity.getType() == 1 && renterOrderDeliveryEntity.getStatus() != 0) {
            GetHandoverCarDTO getHandoverCarDTO = new GetHandoverCarDTO();
            getHandoverCarDTO.setChaoYunNengAddCrash("0");
            getHandoverCarDTO.setGetCarCrash("0");
            getHandoverCarDTO.setGetCarKM("0'");
            getHandoverCarDTO.setIsChaoYunNeng("0");
            getHandoverCarDTO.setOwnDefaultGetCarAddr(renterOrderDeliveryEntity.getOwnerGetReturnAddr());
            getHandoverCarDTO.setOwnRealReturnAddr(renterOrderDeliveryEntity.getRenterGetReturnAddr());
            getHandoverCarDTO.setOwnerGetCarCrash("0");
            String remark = ownerHandoverCarRemarkEntities.stream().filter(r -> (r.getType().intValue() != HandoverCarTypeEnum.RENYUN_TO_RENTER.getValue().intValue())).findFirst().get().getRemark();
            getHandoverCarDTO.setOwnRealGetRemark(remark);
            String renterRemark = renterHandoverCarRemarkEntities.stream().filter(r -> (r.getType().intValue() != HandoverCarTypeEnum.RENYUN_TO_RENTER.getValue().intValue())).findFirst().get().getRemark();
            getHandoverCarDTO.setRenterRealGetAddrReamrk(renterRemark);
            deliveryCarVO.setIsGetCar("1");
            LocalDateTime rentTime = renterOrderDeliveryEntity.getRentTime();
            getHandoverCarDTO.setRentTime(DateUtils.formate(rentTime, DateUtils.DATE_DEFAUTE_4) + "," + renterOrderDeliveryEntity.getAheadOrDelayTime());
            int mileageNum = renterHandoverCarInfoEntities.stream().filter(r -> (r.getType().intValue() != HandoverCarTypeEnum.RENYUN_TO_RENTER.getValue().intValue())).findFirst().get().getMileageNum();
            getHandoverCarDTO.setGetCarKM(String.valueOf(mileageNum));
            deliveryCarVO.setGetHandoverCarDTO(getHandoverCarDTO);
        } else if (renterOrderDeliveryEntity.getType() == 2 && renterOrderDeliveryEntity.getStatus() != 0) {
            ReturnHandoverCarDTO returnHandoverCarDTO = new ReturnHandoverCarDTO();
            returnHandoverCarDTO.setChaoYunNengAddCrash("0");
            returnHandoverCarDTO.setReturnCarCrash("0");
            returnHandoverCarDTO.setReturnCarKM("0'");
            returnHandoverCarDTO.setIsChaoYunNeng("0");
            returnHandoverCarDTO.setOwnDefaultReturnCarAddr(renterOrderDeliveryEntity.getOwnerGetReturnAddr());
            returnHandoverCarDTO.setOwnerRealGetAddr(renterOrderDeliveryEntity.getRenterGetReturnAddr());
            returnHandoverCarDTO.setOwnerGetCarCrash("0");
            String remark = ownerHandoverCarRemarkEntities.stream().filter(r -> (r.getType().intValue() != HandoverCarTypeEnum.RENTER_TO_RENYUN.getValue().intValue())).findFirst().get().getRemark();
            returnHandoverCarDTO.setOwnerRealGetAddrReamrk(remark);
            String renterRemark = renterHandoverCarRemarkEntities.stream().filter(r -> (r.getType().intValue() != HandoverCarTypeEnum.RENTER_TO_RENYUN.getValue().intValue())).findFirst().get().getRemark();
            returnHandoverCarDTO.setRenterRealGetRemark(renterRemark);
            deliveryCarVO.setIsReturnCar("1");
            LocalDateTime rentTime = renterOrderDeliveryEntity.getRentTime();
            returnHandoverCarDTO.setRentTime(DateUtils.formate(rentTime, DateUtils.DATE_DEFAUTE_4) + "," + renterOrderDeliveryEntity.getAheadOrDelayTime());
            int mileageNum = renterHandoverCarInfoEntities.stream().filter(r -> (r.getType().intValue() != HandoverCarTypeEnum.RENTER_TO_RENYUN.getValue().intValue())).findFirst().get().getMileageNum();
            returnHandoverCarDTO.setReturnCarKM(String.valueOf(mileageNum));
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
    public DeliveryCarVO createDeliveryCarInfo(DeliveryCarVO deliveryCarVO, List<OwnerHandoverCarInfoEntity> ownerHandoverCarInfoEntities, List<RenterHandoverCarInfoEntity> renterHandoverCarInfoEntities) {

        String oilNum = ownerHandoverCarInfoEntities.stream().filter(r -> (r.getType().intValue() != HandoverCarTypeEnum.RENYUN_TO_RENTER.getValue().intValue())).findFirst().get().getOilNum().toString();
        String mileageNum = ownerHandoverCarInfoEntities.stream().filter(r -> (r.getType().intValue() != HandoverCarTypeEnum.RENYUN_TO_RENTER.getValue().intValue())).findFirst().get().getMileageNum().toString();
        String returnMileagNum = ownerHandoverCarInfoEntities.stream().filter(r -> (r.getType().intValue() != HandoverCarTypeEnum.RENYUN_TO_RENTER.getValue().intValue())).findFirst().get().getMileageNum().toString();
        String realGetTime =  renterHandoverCarInfoEntities.stream().filter(r -> (r.getType().intValue() != HandoverCarTypeEnum.RENTER_TO_RENYUN.getValue().intValue())).findFirst().get().getRealReturnTime().toString();
        String realReturnTime =  renterHandoverCarInfoEntities.stream().filter(r -> (r.getType().intValue() != HandoverCarTypeEnum.RENYUN_TO_RENTER.getValue().intValue())).findFirst().get().getRealReturnTime().toString();
        String renterKM = renterHandoverCarInfoEntities.stream().filter(r -> (r.getType().intValue() != HandoverCarTypeEnum.RENYUN_TO_RENTER.getValue().intValue())).findFirst().get().getMileageNum().toString();


        OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO = OwnerGetAndReturnCarDTO.builder().carOilDifferenceCrash("0")
                .carOilServiceCharge("0").carOwnerOilCrash("0").dayKM("0")
                .drivingKM(String.valueOf(Integer.valueOf(returnMileagNum) - Integer.valueOf(mileageNum)))
                .getCarOil(oilNum).getKM(mileageNum).oilContainer("0")
                .oilDifference("0").oilDifferenceCrash("0").oilServiceCharge("0").carOwnerOilCrash("0").carOilServiceCharge("0").returnCarOil("0")
                .getCarOil("0").zuQi("0").returnKM(returnMileagNum).realGetTime(realGetTime).realReturnTime(realReturnTime).overKNCrash("0").platFormOilServiceCharge("0")
                .ranLiao("0").build();

        RenterGetAndReturnCarDTO renterGetAndReturnCarDTO = RenterGetAndReturnCarDTO.builder().carOwnerOilCrash("0").dayKM("0").drivingKM("0")
                .getCarOil("0").getKM(renterKM).oilContainer("0").oilDifference("0").oilDifferenceCrash("0").oilServiceCharge("0").overKNCrash("0").ranLiao("0")
                .returnCarOil("0").realGetTime(realGetTime).returnCarOil("0").realReturnTime(realReturnTime).returnKM(renterKM).carOwnerOilCrash("0").drivingKM("0").build();

        deliveryCarVO.setOwnerGetAndReturnCarDTO(ownerGetAndReturnCarDTO);
        deliveryCarVO.setRenterGetAndReturnCarDTO(renterGetAndReturnCarDTO);
        return deliveryCarVO;
    }



}
