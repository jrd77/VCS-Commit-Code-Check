package com.atzuche.order.admin.service;

import com.atzuche.order.admin.vo.rep.delivery.DeliveryCarVO;
import com.atzuche.order.admin.vo.rep.delivery.GetHandoverCarDTO;
import com.atzuche.order.admin.vo.req.DeliveryCarRepVO;
import com.atzuche.order.delivery.entity.*;
import com.atzuche.order.delivery.enums.HandoverCarTypeEnum;
import com.atzuche.order.delivery.enums.UserTypeEnum;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.delivery.DeliveryCarService;
import com.atzuche.order.delivery.service.handover.HandoverCarService;
import com.atzuche.order.delivery.utils.DateUtils;
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
    public DeliveryCarRepVO findDeliveryListByOrderNo(DeliveryCarRepVO deliveryCarDTO) {

        //1.获取租客交接车数据
        //2.获取车主交接车数据
        //3. 组装返回数据
        //取车
        RenterHandoverCarInfoEntity renterGetHandoverCarInfo = handoverCarService.getRenterHandoverCarInfo(deliveryCarDTO.getOrderNo(), HandoverCarTypeEnum.RENYUN_TO_RENTER.getValue());
        OwnerHandoverCarInfoEntity ownerGetHandoverCarInfo = handoverCarService.getOwnerHandoverCarInfo(deliveryCarDTO.getOrderNo(), HandoverCarTypeEnum.RENYUN_TO_RENTER.getValue());
        //还车
        RenterHandoverCarInfoEntity renterReturnHandoverCarInfo = handoverCarService.getRenterHandoverCarInfo(deliveryCarDTO.getOrderNo(), HandoverCarTypeEnum.RENTER_TO_RENYUN.getValue());
        OwnerHandoverCarInfoEntity ownerReturnHandoverCarInfo = handoverCarService.getOwnerHandoverCarInfo(deliveryCarDTO.getOrderNo(), HandoverCarTypeEnum.RENTER_TO_RENYUN.getValue());
        //获取租客备注信息
        List<RenterHandoverCarRemarkEntity> renterHandoverCarRemarkEntities = handoverCarService.getRenterHandoverRemarkInfo(deliveryCarDTO.getOrderNo());
        //获取车主备注信息
        List<OwnerHandoverCarRemarkEntity> handoverCarInfoEntities = handoverCarService.getOwnerHandoverRemarkInfo(deliveryCarDTO.getOrderNo());
        createDeliveryCarVOParams(renterGetHandoverCarInfo,ownerGetHandoverCarInfo,renterReturnHandoverCarInfo,ownerReturnHandoverCarInfo,renterHandoverCarRemarkEntities,handoverCarInfoEntities);

        return null;
    }

    /**
     * 构造结构体
     * @param renterGetHandoverCarInfo
     * @param ownerGetHandoverCarInfo
     * @param renterReturnHandoverCarInfo
     * @param ownerReturnHandoverCarInfo
     * @return
     */
    public DeliveryCarVO createDeliveryCarVOParams(RenterHandoverCarInfoEntity renterGetHandoverCarInfo,OwnerHandoverCarInfoEntity ownerGetHandoverCarInfo,RenterHandoverCarInfoEntity renterReturnHandoverCarInfo,OwnerHandoverCarInfoEntity ownerReturnHandoverCarInfo,List<RenterHandoverCarRemarkEntity> renterHandoverCarRemarkEntities,List<OwnerHandoverCarRemarkEntity> handoverCarInfoEntities )
    {
        if(renterGetHandoverCarInfo != null)
        {
            GetHandoverCarDTO getHandoverCarDTO = new GetHandoverCarDTO();
            getHandoverCarDTO.setChaoYunNengAddCrash("0");
            getHandoverCarDTO.setGetCarCrash("0");
            getHandoverCarDTO.setGetCarKM("0'");
            getHandoverCarDTO.setIsChaoYunNeng("0");
            getHandoverCarDTO.setOwnDefaultGetCarAddr(renterGetHandoverCarInfo.getRealReturnAddr());
            getHandoverCarDTO.setOwnRealReturnAddr(ownerGetHandoverCarInfo.getRealReturnAddr());
            getHandoverCarDTO.setOwnerGetCarCrash("0");
            // 车主取车
            String remark = handoverCarInfoEntities.get(0).getType().intValue() == HandoverCarTypeEnum.RENYUN_TO_RENTER.getValue() ? handoverCarInfoEntities.get(0).getRemark() : handoverCarInfoEntities.get(1).getRemark();
            getHandoverCarDTO.setOwnRealGetRemark(remark);
            //租客送车
            String renterRemark = renterHandoverCarRemarkEntities.get(0).getType().intValue() == HandoverCarTypeEnum.RENTER_TO_RENYUN.getValue() ? renterHandoverCarRemarkEntities.get(0).getRemark() : renterHandoverCarRemarkEntities.get(1).getRemark();
            getHandoverCarDTO.setRenterRealGetAddrReamrk(renterRemark);
            //获取配送数据
            List<RenterOrderDeliveryEntity> renterOrderDeliveryEntityList = renterOrderDeliveryService.listRenterOrderDeliveryByRenterOrderNo(renterGetHandoverCarInfo.getRenterOrderNo());
            LocalDateTime rentTime = renterOrderDeliveryEntityList.get(0).getType().intValue() == UserTypeEnum.RENTER_TYPE.getValue() ? renterOrderDeliveryEntityList.get(0).getRentTime() : renterOrderDeliveryEntityList.get(1).getRentTime();
            getHandoverCarDTO.setRentTime(DateUtils.formate(rentTime,DateUtils.DATE_DEFAUTE_4)+","+renterGetHandoverCarInfo.getAheadTime());
            getHandoverCarDTO.setGetCarKM(String.valueOf(ownerGetHandoverCarInfo.getMileageNum()));

        }
        return null;
    }

}
