package com.atzuche.order.delivery.service.handover;

import com.atzuche.order.commons.vo.req.handover.req.HandoverCarInfoReqDTO;
import com.atzuche.order.commons.vo.req.handover.req.HandoverCarInfoReqVO;
import com.atzuche.order.delivery.common.DeliveryErrorCode;
import com.atzuche.order.delivery.enums.ServiceTypeEnum;
import com.atzuche.order.delivery.exception.HandoverCarOrderException;
import com.atzuche.order.delivery.vo.handover.MileageOilOperateVo;
import com.atzuche.order.delivery.vo.handover.MileageOilVO;
import com.dianping.cat.Cat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author 胡春林
 * 处理仁云油耗数据
 */
@Service
@Slf4j
public class HandoverCarOilMileageService {

    @Autowired
    HandoverCarInfoService handoverCarInfoService;

    /**
     * 更新交接车里程油耗数据(仁云) btype 3：取车时,4：送达时 3车主 4租客
     * @param mileageOilOperateVo
     */
    public void saveMileageOilOperate(MileageOilOperateVo mileageOilOperateVo)
    {
        if(Objects.isNull(mileageOilOperateVo))
        {
            throw new HandoverCarOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(),"获取仁云油耗数据失败");
        }
        HandoverCarInfoReqVO handoverCarInfoReqVO = new HandoverCarInfoReqVO();
        HandoverCarInfoReqDTO ownerHandoverCarInfoReqDTO = new HandoverCarInfoReqDTO();
        HandoverCarInfoReqDTO renterHandoverCarInfoReqDTO = new HandoverCarInfoReqDTO();
        ownerHandoverCarInfoReqDTO.setOrderNo(mileageOilOperateVo.getOrderNo());
        renterHandoverCarInfoReqDTO.setOrderNo(mileageOilOperateVo.getOrderNo());
        if (ServiceTypeEnum.TAKE_TYPE.getValue().equals(mileageOilOperateVo.getStype())) {
            if ("3".equals(mileageOilOperateVo.getBtype())) {
                ownerHandoverCarInfoReqDTO.setRenterRetrunKM(mileageOilOperateVo.getKms());
                ownerHandoverCarInfoReqDTO.setRenterReturnOil(mileageOilOperateVo.getOil());
            } else {
                renterHandoverCarInfoReqDTO.setRenterReturnOil(mileageOilOperateVo.getOil());
                renterHandoverCarInfoReqDTO.setRenterRetrunKM(mileageOilOperateVo.getKms());
            }
        } else if (ServiceTypeEnum.BACK_TYPE.getValue().equals(mileageOilOperateVo.getStype())) {
            if ("3".equals(mileageOilOperateVo.getBtype())) {
                renterHandoverCarInfoReqDTO.setOwnReturnOil(mileageOilOperateVo.getOil());
                renterHandoverCarInfoReqDTO.setOwnReturnKM(mileageOilOperateVo.getKms());
            } else {
                ownerHandoverCarInfoReqDTO.setOwnReturnOil(mileageOilOperateVo.getOil());
                ownerHandoverCarInfoReqDTO.setOwnReturnKM(mileageOilOperateVo.getKms());
            }
        }
        else {
            log.error("更新仁云取还车油耗数据发生异常，没找到合适的取还车类型，mileageOilOperateVo：{}",mileageOilOperateVo.toString());
            return;
        }
        handoverCarInfoReqVO.setRenterHandoverCarDTO(renterHandoverCarInfoReqDTO);
        handoverCarInfoReqVO.setOwnerHandoverCarDTO(ownerHandoverCarInfoReqDTO);
        try {
            handoverCarInfoService.updateHandoverCarInfo(handoverCarInfoReqVO);
        } catch (Exception e) {
            log.info("更新仁云取还车油耗数据发生异常,具体原因:{}", e.getMessage());
            Cat.logError("更新仁云取还车油耗数据发生异常", e);
        }
    }

    /**
     * 更新交接车里程油耗数据(车主)
     * @param mileageOilVO
     */
    public void saveMileageOil(MileageOilVO mileageOilVO)
    {
        if (Objects.isNull(mileageOilVO)) {
            throw new HandoverCarOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "获取仁云油耗数据失败");
        }
        HandoverCarInfoReqVO handoverCarInfoVO = new HandoverCarInfoReqVO();
        HandoverCarInfoReqDTO ownerHandoverCarInfoDTO = new HandoverCarInfoReqDTO();
        if (ServiceTypeEnum.TAKE_TYPE.getValue().equals(mileageOilVO.getType())) {
            ownerHandoverCarInfoDTO.setRenterRetrunKM(mileageOilVO.getMileage());
            ownerHandoverCarInfoDTO.setRenterReturnOil(mileageOilVO.getOilScale());
        } else if (ServiceTypeEnum.BACK_TYPE.getValue().equals(mileageOilVO.getType())) {
            ownerHandoverCarInfoDTO.setOwnReturnOil(mileageOilVO.getMileage());
            ownerHandoverCarInfoDTO.setOwnReturnKM(mileageOilVO.getOilScale());
        } else {
            log.error("更新仁云取还车油耗数据发生异常，没找到合适的取还车类型，mileageOilVO：{}", mileageOilVO.toString());
            return;
        }
        ownerHandoverCarInfoDTO.setOrderNo(mileageOilVO.getOrderNo());
        handoverCarInfoVO.setOwnerHandoverCarDTO(ownerHandoverCarInfoDTO);
        try {
            handoverCarInfoService.updateHandoverCarInfo(handoverCarInfoVO);
        } catch (Exception e) {
            log.info("更新车主取还车油耗数据发生异常,具体原因:{}", e.getMessage());
            Cat.logError("更新车主取还车油耗数据发生异常", e);
        }
    }
}
